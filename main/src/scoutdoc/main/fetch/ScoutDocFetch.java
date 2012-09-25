/*******************************************************************************
 * Copyright (c) 2012 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/

package scoutdoc.main.fetch;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import scoutdoc.main.ProjectProperties;
import scoutdoc.main.filter.IPageFilter;
import scoutdoc.main.mediawiki.ApiFileUtility;
import scoutdoc.main.structure.Page;
import scoutdoc.main.structure.PageUtility;
import scoutdoc.main.structure.RelatedPagesStrategy;
import scoutdoc.main.structure.Task;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import com.google.common.io.Resources;

public class ScoutDocFetch {
  public void execute(Task t) {
    execute(t.getInputPages(), RelatedPagesStrategy.CATEGORIES_IMAGES_TEMPLATES_AND_LINKS);
  }

  public void executeRss(IPageFilter pageFilter) {
    Map<String, String> parameters = new LinkedHashMap<String, String>();
    parameters.put("title", "Special:RecentChanges");
    parameters.put("feed", "rss");

    String rssUrl = UrlUtility.createFullUrl(ProjectProperties.getWikiIndexUrl(), parameters);

    //Find Pages:
    List<Page> pages = RssUtility.parseRss(rssUrl, pageFilter);
    System.out.println("found " + pages.size() + " pages with changes");

    //Download this pages, and the related items:
    execute(pages, RelatedPagesStrategy.CATEGORIES_IMAGES_TEMPLATES_AND_LINKS);
  }

  public void executeRecentChanges(IPageFilter pageFilter) {
    String lastTimestamp = null;
    File f = new File(ProjectProperties.getFolderWikiSource(), "info.txt");
    if (f.exists() && f.canRead()) {
      try {
        String line = Files.readFirstLine(f, Charsets.UTF_8);
        lastTimestamp = line;
      }
      catch (IOException e) {
        e.printStackTrace();
      }
      catch (NumberFormatException e) {
        e.printStackTrace();
      }
    }
    if (lastTimestamp == null) {
      lastTimestamp = "0000-00-00T00:00:00Z";
      List<Page> pages = PageUtility.loadPages(ProjectProperties.getFolderWikiSource());
      for (Page page : pages) {
        String timestamp = ApiFileUtility.readTimestamp(PageUtility.toApiFile(page));
        if (timestamp.compareTo(lastTimestamp) > 0) {
          lastTimestamp = timestamp;
        }
      }
    }

    List<Page> pages = new ArrayList<Page>();

    String queryContinue = lastTimestamp;
    while (queryContinue != null) {
      Map<String, String> parameters = new LinkedHashMap<String, String>();
      parameters.put("action", "query");
      parameters.put("list", "recentchanges");
      parameters.put("rcprop", Joiner.on("|").join("title", "timestamp", "ids"));
      parameters.put("rcdir", "newer");
      parameters.put("rclimit", "50");
      parameters.put("rcstart", queryContinue);
      parameters.put("format", "xml");

      try {
        String queryContent = downlaod(UrlUtility.createFullUrl(ProjectProperties.getWikiApiUrl(), parameters));
        List<String> pageNames = ApiFileUtility.readValues(queryContent, "//recentchanges/rc/@title");
        for (String pageName : pageNames) {
          Page page = PageUtility.toPage(pageName);
          if (pageFilter.keepPage(page)) {
            pages.add(page);
          }
        }

        List<String> pageTimestamps = ApiFileUtility.readValues(queryContent, "//recentchanges/rc/@timestamp");
        lastTimestamp = Collections.max(pageTimestamps);

        queryContinue = ApiFileUtility.readValue(queryContent, "//query-continue/recentchanges/@rcstart");
      }
      catch (IOException e) {
        e.printStackTrace();
        queryContinue = null;
      }
      catch (TransformerException e) {
        e.printStackTrace();
        queryContinue = null;
      }
    }

    execute(pages, RelatedPagesStrategy.CATEGORIES_IMAGES_TEMPLATES_AND_LINKS);

    try {
      Files.write(lastTimestamp, f, Charsets.UTF_8);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * @param inputPages
   * @param relatedTypes
   *          also download the
   */
  public void execute(Collection<Page> inputPages, RelatedPagesStrategy strategy) {
    Set<Page> pages = new HashSet<Page>();
    Set<Page> pagesToDownload = new HashSet<Page>();
    pages.addAll(inputPages);
    pagesToDownload.addAll(inputPages);

    while (pagesToDownload.size() > 0) {
      Set<Page> pagesAdditional = new HashSet<Page>();

      for (Page page : pagesToDownload) {
        downloadPage(page);
        //Read the API Page to related content to the set. 
        pagesAdditional.addAll(strategy.findNextRelatedPages(page, inputPages.contains(page)));
      }

      pagesToDownload = new HashSet<Page>();
      for (Page page : pagesAdditional) {
        if (!pages.contains(page)) {
          pages.add(page);
          pagesToDownload.add(page);
        }
      }
    }
  }

  private static void downloadPage(Page page) {
    try {
      long lastRevisionId = ApiFileUtility.readRevisionId(PageUtility.toApiFile(page));
      File apiFile = downloadApiPage(page, lastRevisionId);
      if (apiFile != null) {
        downloadMediaWikiPage(page);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void downloadMediaWikiPage(Page page) throws IOException, TransformerException {
    String url = ProjectProperties.getWikiIndexUrl();

    Map<String, String> parameters = new LinkedHashMap<String, String>();
    parameters.put("action", "raw");
    parameters.put("title", URLEncoder.encode(PageUtility.toFullPageNamee(page), "UTF-8"));
//    parameters.put("templates", "expand");

    String fullUrl = UrlUtility.createFullUrl(url, parameters);
    String content = downlaod(fullUrl);

    File file = new File(PageUtility.toFilePath(page, ProjectProperties.FILE_EXTENTION_CONTENT));
    writeContentToFile(file, content);
  }

  private static File downloadApiPage(Page page, long lastRevisionId) throws IOException, TransformerException {
    Preconditions.checkNotNull(page.getType(), "Page#Type can not be null");

    String url = ProjectProperties.getWikiApiUrl();

    Map<String, String> parameters = new LinkedHashMap<String, String>();
    parameters.put("format", "xml");
    parameters.put("action", "query");
    if (PageUtility.isImage(page)) {
      parameters.put("prop", Joiner.on("|").join("categories", "images", "links", "templates", "revisions", "imageinfo"));
      parameters.put("iiprop", Joiner.on("|").join("timestamp", "user", "url", "metadata"));
    }
    else {
      parameters.put("prop", Joiner.on("|").join("categories", "images", "links", "templates", "revisions"));
    }
    parameters.put("titles", URLEncoder.encode(PageUtility.toFullPageNamee(page), "UTF-8"));

    String fullUrl = UrlUtility.createFullUrl(url, parameters);
    String content = downlaod(fullUrl);

    List<String> invalidTags = ApiFileUtility.readValues(content, "//page[@invalid]");
    if (invalidTags.size() > 0) {
      throw new IllegalStateException("Got an invalid api file for url: " + fullUrl);
    }

    long revisionId = ApiFileUtility.readRevisionId(content);
    if (revisionId <= lastRevisionId) {
      return null;
    }
    content = prettyFormat(content);

    File file = PageUtility.toApiFile(page);
    writeContentToFile(file, content);

    if (PageUtility.isImage(page)) {
      String value = ApiFileUtility.readValue(file, "//imageinfo/ii/@url");
      if (value != null) {
        downloadImage(page, value);
      }
    }
    return file;
  }

  private static File downloadImage(Page imagePage, String imageServerPath) throws IOException, TransformerException {
    Preconditions.checkNotNull(imageServerPath, "imageServerPath can not be null");
    Preconditions.checkNotNull(imagePage, "imagePage can not be null");
    Preconditions.checkArgument(PageUtility.isImage(imagePage), "imagePage should have a PageUtility.isFile(..) type (Image/File)");

    File file = PageUtility.toFile(imagePage);
    String fullUrl;
    if (imageServerPath.startsWith(ProjectProperties.getWikiServerUrl())) {
      fullUrl = imageServerPath;
    }
    else {
      fullUrl = ProjectProperties.getWikiServerUrl() + imageServerPath;
    }

    InputSupplier<InputStream> inputSupplier = createInputSupplier(fullUrl);
    Files.createParentDirs(file);
    Files.copy(inputSupplier, file);

    return file;
  }

  private static String downlaod(String fullUrl) throws IOException, TransformerException {
    InputSupplier<InputStream> inputSupplier = createInputSupplier(fullUrl);
    InputSupplier<InputStreamReader> readerSupplier = CharStreams.newReaderSupplier(inputSupplier, Charsets.UTF_8);
    return CharStreams.toString(readerSupplier);
  }

  private static InputSupplier<InputStream> createInputSupplier(String fullUrl) throws MalformedURLException {
    System.out.println(fullUrl);

    InputSupplier<InputStream> inputSupplier = Resources.newInputStreamSupplier(new URL(fullUrl));
    return inputSupplier;
  }

  private static void writeContentToFile(File file, String content) throws IOException {
    Files.createParentDirs(file);
    Files.write(content, file, Charsets.UTF_8);
  }

  public static String prettyFormat(String input) throws TransformerException {
    Source xmlInput = new StreamSource(new StringReader(input));
    StringWriter stringWriter = new StringWriter();
    StreamResult xmlOutput = new StreamResult(stringWriter);
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    transformerFactory.setAttribute("indent-number", 2);
    Transformer transformer = transformerFactory.newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.transform(xmlInput, xmlOutput);
    return xmlOutput.getWriter().toString();
  }
}
