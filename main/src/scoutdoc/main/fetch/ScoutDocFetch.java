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
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
import scoutdoc.main.mediawiki.ApiFileUtility;
import scoutdoc.main.structure.ContentType;
import scoutdoc.main.structure.Page;
import scoutdoc.main.structure.PageUtility;
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
		execute(t.getInputPages(), Arrays.asList(ContentType.Images, ContentType.Template));
	}
	
	/**
	 * @param inputPages
	 * @param relatedTypes also download the 
	 */
	public void execute(Collection<Page> inputPages, Collection<ContentType> relatedTypes) {
		Set<Page> pages = new HashSet<Page>();
		Set<Page> pagesToDownload = new HashSet<Page>();
		pages.addAll(inputPages);
		pagesToDownload.addAll(inputPages);

		while (pagesToDownload.size() > 0) {
			Set<Page> pagesAdditional = new HashSet<Page>(); 
			
			downloadPages(pagesToDownload, pagesAdditional, relatedTypes);
			
			pagesToDownload = new HashSet<Page>();
			for (Page page : pagesAdditional) {
				if(!pages.contains(page)) {
					pages.add(page);
					pagesToDownload.add(page);
				}
			}
		}
	}

	private static void downloadPages(Collection<Page> pages, Set<Page> relatedPages, Collection<ContentType> relatedTypes) {
		for (Page page : pages) {
			try {
				downloadMediaWikiPage(page);
				File apiFile = downloadApiPage(page);
				
				//Read the API Page to add the images and template to the sets. 
				relatedPages.addAll(ApiFileUtility.parseContent(apiFile, relatedTypes));
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void downloadMediaWikiPage(Page page) throws IOException, TransformerException {
		String url = ProjectProperties.getWikiIndexUrl();
		
		Map<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("action", "raw");
		parameters.put("title", PageUtility.toFullPageNamee(page));
//		parameters.put("templates", "expand");

		downloadPage(page, url, parameters, ProjectProperties.FILE_EXTENTION_CONTENT, false);
	}
	
	private static File downloadApiPage(Page page) throws IOException, TransformerException {
		Preconditions.checkNotNull(page.getType(), "Page#Type can not be null");
		
		String url = ProjectProperties.getWikiApiUrl();
		
		Map<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("format", "xml");
		parameters.put("action", "query");
		if(PageUtility.isImage(page)) {
			parameters.put("prop", Joiner.on("|").join("categories", "images", "links", "templates", "revisions", "imageinfo"));
			parameters.put("iiprop", Joiner.on("|").join("timestamp", "user", "url", "metadata"));			
		} else {
			parameters.put("prop", Joiner.on("|").join("categories", "images", "links", "templates", "revisions"));
		}
		parameters.put("titles", URLEncoder.encode(PageUtility.toFullPageNamee(page), "UTF-8"));
		
		File file = downloadPage(page, url, parameters, ProjectProperties.FILE_EXTENTION_META, true);
				
		if(PageUtility.isImage(page)) {
			String value = ApiFileUtility.readValue(file, "//imageinfo/ii/@url");
			if(value == null) {
				downloadImage(page, value);
			}
		}
		return file;
	}

	private static File downloadPage(Page page, String url, Map<String, String> parameters, String fileExtension, boolean isXml) throws IOException, TransformerException {
		Preconditions.checkNotNull(page.getType(), "Page#Type can not be null");

		File file = new File(PageUtility.toFilePath(page, fileExtension));
		
		String fullUrl = Joiner.on("?").join(url, Joiner.on("&").withKeyValueSeparator("=").join(parameters));
		fullUrl = fullUrl.replaceAll(" ", "%20");
		
		downlaod(file, fullUrl, isXml);
		
		return file;
	}
	
	private static File downloadImage(Page imagePage, String imageServerPath) throws IOException, TransformerException {
		Preconditions.checkNotNull(imageServerPath, "imageServerPath can not be null");
		Preconditions.checkNotNull(imagePage, "imagePage can not be null");
		Preconditions.checkArgument(PageUtility.isImage(imagePage), "imagePage should have a PageUtility.isFile(..) type (Image/File)");
		
		File file = PageUtility.toFile(imagePage);
		String fullUrl;
		if(imageServerPath.startsWith(ProjectProperties.getWikiServerUrl())) {
			fullUrl = imageServerPath;			
		} else {
			fullUrl = ProjectProperties.getWikiServerUrl() + imageServerPath;						
		}
		
		downlaod(file, fullUrl, true);
		return file;
	}

	private static void downlaod(File file, String fullUrl, boolean isXml) throws IOException, TransformerException {
		System.out.println(fullUrl);

		InputSupplier<InputStream> inputSupplier = Resources.newInputStreamSupplier(new URL(fullUrl));
		
		InputSupplier<InputStreamReader> readerSupplier = CharStreams.newReaderSupplier(inputSupplier, Charsets.UTF_8);
		String content = CharStreams.toString(readerSupplier);
		if(isXml) {
			content = prettyFormat(content);
		} 
		
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
