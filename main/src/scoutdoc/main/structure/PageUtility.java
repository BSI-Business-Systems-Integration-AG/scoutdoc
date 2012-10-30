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

package scoutdoc.main.structure;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scoutdoc.main.ProjectProperties;
import scoutdoc.main.mediawiki.ApiFileUtility;

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

public class PageUtility {
  private static final char SLASH = '/';
  private static final String SLASH_REPLACEMENT = "%2F";
  private static final char DOUBLE_POINT = ':';
  private static final String DOUBLE_POINT_REPLACEMENT = "%3A";
  private static final int MAX_FILE_NAME_LENGTH = 100;

  /**
   * Convert a {@link Page} to the FULLPAGENAME (which is {{NAMESPACE}}:{{PAGENAME}}).
   * Can be used a title parameter in a query to MediaWiki.
   * 
   * @param page
   * @return fullPageName
   */
  public static String /*fullPageNamee*/toFullPageNamee(Page page) {
    Preconditions.checkNotNull(page.getType(), "Page#Type can not be null");
    Preconditions.checkNotNull(page.getName(), "Page#Name can not be null");

    String namespace;
    if (page.getType() == PageType.Article) {
      namespace = "";
    }
    else {
      namespace = page.getType() + ":";
    }
    String pageName = namespace + page.getName();
    return convertToInternalName(pageName);
  }

  public static String /*pageNamee*/toPageNamee(Page page) {
    Preconditions.checkNotNull(page.getName(), "Page#Name can not be null");
    return convertToInternalName(page.getName());
  }

  public static String /*basePageNamee*/toBasePageNamee(Page page) {
    Preconditions.checkNotNull(page.getName(), "Page#Name can not be null");

    String name = page.getName();
    int index = CharMatcher.is('/').lastIndexIn(name);
    if (index > 0) {
      name = name.substring(index + 1);
    }
    return convertToInternalName(name);
  }

  public static String /*fullPageName*/toFullPageName(Page page) {
    return convertToDisplayName(toFullPageNamee(page));
  }

  public static String /*basePageName*/toBasePageName(Page page) {
    return convertToDisplayName(toBasePageNamee(page));
  }

  /**
   * Convert a {@link Page} to the file.
   * 
   * @param page
   * @param fileExtension
   *          without a point
   * @return path of the file
   */
  public static String /*file path*/toFilePath(Page page, String fileExtension) {
    Preconditions.checkNotNull(page.getType(), "Page#Type can not be null. Page: " + page.toString());
    Preconditions.checkNotNull(page.getName(), "Page#Name can not be null. Page: " + page.toString());
    Preconditions.checkNotNull(page.getId(), "Page#Id can not be null. Page: " + page.toString());

    return toFilePath(page.getType(), page.getId(), page.getName(), fileExtension);
  }

  public static String toFilePath(Page page) {
    return toFilePath(page, ProjectProperties.FILE_EXTENTION_CONTENT);
  }

  private static String toFilePath(PageType type, Integer pageId, String fileName, String fileExtension) {
    String filePath = fileName;
    filePath = CharMatcher.is(SLASH).replaceFrom(filePath, SLASH_REPLACEMENT);
    filePath = CharMatcher.is(DOUBLE_POINT).replaceFrom(filePath, DOUBLE_POINT_REPLACEMENT);
    if (filePath.length() > MAX_FILE_NAME_LENGTH) {
      filePath = filePath.substring(0, MAX_FILE_NAME_LENGTH);
    }
    filePath = Joiner.on('-').join(filePath, pageId.toString());
    filePath = ProjectProperties.getFolderWikiSource() + ProjectProperties.getFileSeparator() + type.name() + ProjectProperties.getFileSeparator() + filePath;
    filePath = Joiner.on('.').skipNulls().join(filePath, fileExtension);
    return convertToInternalName(filePath);
  }

  public static File toFile(Page page) {
    Preconditions.checkNotNull(page.getId(), "PageId can not be null. Page: " + page.toString());
    if (isImage(page)) {
      return toImageFile(page);
    }
    else {
      return toContentFile(page);
    }
  }

  public static File toContentFile(Page page) {
    return new File(toFilePath(page));
  }

  public static File toImageFile(Page imagePage) {
    Preconditions.checkArgument(isImage(imagePage), "imagePage should be an image see PageUtility.isImage(..)");
    Preconditions.checkNotNull(imagePage.getId(), "PageId can not be null. Page: " + imagePage.toString());

    String name = toPageNamee(imagePage);

    String ext, fileName;
    int pos = CharMatcher.is('.').lastIndexIn(name);
    if (pos > 0) {
      fileName = name.substring(0, pos);
      ext = name.substring(pos + 1);
    }
    else {
      fileName = name;
      ext = null;
    }
    return new File(toFilePath(imagePage.getType(), imagePage.getId(), fileName, ext));
  }

  public static File toContentFileFromApiFile(File apiFile) {
    return new File(apiFile.getPath().replaceAll(ProjectProperties.FILE_EXTENTION_META + "$", ProjectProperties.FILE_EXTENTION_CONTENT));
  }

  public static File toApiFile(Page page) {
    return new File(toFilePath(page, ProjectProperties.FILE_EXTENTION_META));
  }

  public static File toApiFileFromContentFile(File apiFile) {
    return new File(apiFile.getPath().replaceAll(ProjectProperties.FILE_EXTENTION_META + "$", ProjectProperties.FILE_EXTENTION_CONTENT));
  }

//  public static Page /*page*/toPage(String fullPageName) {
//    return toPage(fullPageName, null);
//  }

  /**
   * @param pageNamee
   */
  public static Page toPageTitle(String fullPageName) {
    return toPageInternal(fullPageName, null);
  }

  public static Page /*page*/toPage(String fullPageName, int pageId) {
    return toPageInternal(fullPageName, Integer.valueOf(pageId));
  }

  private static Page /*page*/toPageInternal(String fullPageName, Integer pageId) {
    Page page = new Page();
    String fullPageNamee = convertToInternalName(fullPageName);

    PageType type;
    String name;
    int index = CharMatcher.is(':').indexIn(fullPageNamee);
    if (index > 0) {
      name = fullPageNamee.substring(index + 1);
      String typeRaw = fullPageNamee.substring(0, index);
      try {
        type = PageType.valueOf(typeRaw);
        if (type == PageType.Media) {
          type = PageType.Image;
        }
      }
      catch (IllegalArgumentException e) {
        System.err.println("Namespace not found: " + typeRaw);
        type = PageType.Article;
        name = fullPageNamee;
      }
    }
    else {
      type = PageType.Article;
      name = fullPageNamee;
    }
    page.setName(name);
    page.setType(type);
    page.setId(pageId);
    return page;
  }

  public static String convertToInternalName(String pageName) {
    return CharMatcher.anyOf(" ").replaceFrom(pageName, "_");
  }

  public static String convertToDisplayName(String pageId) {
    return CharMatcher.anyOf("_").replaceFrom(pageId, " ");
  }

  public static boolean isImage(Page page) {
    return page.getType() == PageType.Image || page.getType() == PageType.File || page.getType() == PageType.Media;
  }

  public static Collection<Page> loadPages(String sourceFolder) throws IOException {
    return loadApiFileAndPages(sourceFolder).values();
  }

  public static Collection<Page> loadPages(File folder) {
    return loadApiFileAndPages(folder).values();
  }

  public static Map<File, Page> loadApiFileAndPages(String sourceFolder) throws IOException {
    File f = new File(sourceFolder);
    if (!f.exists()) {
      Files.createParentDirs(f);
      f.mkdir();
    }
    return loadApiFileAndPages(f);
  }

  public static Map<File, Page> loadApiFileAndPages(File folder) {
    Preconditions.checkArgument(folder.isDirectory(), "should be a directory: " + folder.getPath());
    Preconditions.checkArgument(isInSourceFolder(folder), "the directory should be included in ProjectProperties.getFolderWikiSource().");

    HashMap<File, Page> result = new HashMap<File, Page>();

    File[] childApiFiles = folder.listFiles(new FileFilter() {
      @Override
      public boolean accept(File f) {
        return f.getName().endsWith("." + ProjectProperties.FILE_EXTENTION_META);
      }
    });
    for (File apiFile : childApiFiles) {
      //To page:
      Page page = toPage(apiFile);

      //To api File
      File contentFile = toContentFileFromApiFile(apiFile);
      if (contentFile.exists()) {
        result.put(apiFile, page);
      }
    }

    File[] childFolder = folder.listFiles(new FileFilter() {
      @Override
      public boolean accept(File f) {
        return f.isDirectory();
      }
    });

    for (File f : childFolder) {
      result.putAll(loadApiFileAndPages(f));
    }

    return result;
  }

  public static Page toPage(File file) {
    try {
      String path = file.getCanonicalPath();
      boolean isContentFile = path.endsWith("." + ProjectProperties.FILE_EXTENTION_CONTENT);
      boolean isApiFile = path.endsWith("." + ProjectProperties.FILE_EXTENTION_META);
      File apiFile;
      if (isContentFile) {
        String apiFilePath = path.replaceAll("\\." + ProjectProperties.FILE_EXTENTION_CONTENT + "$", "." + ProjectProperties.FILE_EXTENTION_META);
        apiFile = new File(apiFilePath);
        if (!apiFile.exists()) {
          return null;
        }
      }
      else if (isApiFile) {
        apiFile = file;
      }
      else {
        return null;
      }

      return ApiFileUtility.createPage(apiFile);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static boolean exists(Page page) {
    if (page.getId() == null) {
      return false;
    }
    File file = toFile(page);
    return file.exists();
  }

  public static void writeList(Collection<Page> pages, String listFilePath) {
    ArrayList<Page> p = Lists.newArrayList(pages);

    Collections.sort(p);
    StringBuffer sb = new StringBuffer();

    boolean isFirst = true;
    for (Page e : p) {
      if (!isFirst) {
        sb.append("\n");
      }
      else {
        isFirst = false;
      }
      sb.append(toFullPageNamee(e));
    }

    File file = new File(listFilePath);
    try {
      Files.write(sb.toString(), file, Charsets.UTF_8);
    }
    catch (IOException e1) {
      e1.printStackTrace();
    }
  }

  public static List<Page> readList(String listFilePath) throws IOException {
    if (listFilePath == null || listFilePath.isEmpty()) {
      throw new FileNotFoundException("file path is null or empty");
    }
    ArrayList<Page> pages = Lists.newArrayList();

    File file = new File(listFilePath);
    List<String> lines = Files.readLines(file, Charsets.UTF_8);
    for (String l : lines) {
      pages.add(Pages.get(l));
    }
    return Collections.unmodifiableList(pages);
  }

  private static boolean isInSourceFolder(File folder) {
    File wikiSource = new File(ProjectProperties.getFolderWikiSource());
    File parent = folder;
    while (parent != null) {
      if (parent.equals(wikiSource)) {
        return true;
      }
      parent = parent.getParentFile();
    }
    return false;
  }
}
