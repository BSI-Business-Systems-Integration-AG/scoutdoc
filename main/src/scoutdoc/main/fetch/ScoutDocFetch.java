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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.rmi.UnexpectedException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import scoutdoc.main.ProjectProperties;
import scoutdoc.main.structure.Page;
import scoutdoc.main.structure.PageType;
import scoutdoc.main.structure.PageUtility;
import scoutdoc.main.structure.Task;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import com.google.common.io.Resources;

public class ScoutDocFetch {

	public void execute(Task t) {
		//Download pages:
		Set<Page> templates = new HashSet<Page>(); 
		Set<Page> images = new HashSet<Page>(); 
		downloadPages(t.getInputPages(), templates, images);

		//Download images:
		Set<Page> emptyImageSet = new HashSet<Page>(); 
		downloadPages(images, templates, emptyImageSet);
		if(!emptyImageSet.isEmpty()) {
			System.err.println("Got images containing other images: "+emptyImageSet.size());
		}
		
		//Download templates:
		Set<Page> emptyTemplateSet = new HashSet<Page>(); 
		downloadPages(templates, emptyTemplateSet, emptyImageSet);
		if(!emptyImageSet.isEmpty()) {
			System.err.println("Got templates containing other images: "+emptyImageSet.size());
		}
		if(!emptyTemplateSet.isEmpty()) {
			System.err.println("Got templates containing other templates: "+emptyTemplateSet.size());
		}
		
		
	}

	private static void downloadPages(Collection<Page> pages, Set<Page> templates, Set<Page> images) {
		for (Page page : pages) {
			try {
				downloadMediaWikiPage(page);
				File apiFile = downloadApiPage(page);
				
				//Read the API Page to add the images and template to the sets. 
				parseApiFile(apiFile, "//tl/@title", templates);
				parseApiFile(apiFile, "//im/@title", images);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void parseApiFile(File apiFile, String xpathQuery, Set<Page> pagesSet) {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setNamespaceAware(true);
		try {
			DocumentBuilder builder = docFactory.newDocumentBuilder();
			Document doc = builder.parse(apiFile);
			
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath.compile(xpathQuery);
			NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength(); i++) {
				pagesSet.add(PageUtility.toPage(nodes.item(i).getNodeValue()));				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void downloadMediaWikiPage(Page page) throws IOException {
		String fileExtension = "mediawiki";
		String url = ProjectProperties.getWikiIndexUrl();
		
		Map<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("action", "raw");
		parameters.put("title", PageUtility.toFullPageNamee(page));
//		parameters.put("templates", "expand");

		downloadPage(page, url, parameters, fileExtension);
	}
	
	private static File downloadApiPage(Page page) throws IOException {
		Preconditions.checkNotNull(page.getType(), "Page#Type can not be null");
		
		String fileExtension = "meta.xml";
		String url = ProjectProperties.getWikiApiUrl();
		
		Map<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("format", "xml");
		parameters.put("action", "query");
		switch (page.getType()) {
		case Image:
			parameters.put("prop", Joiner.on("|").join("categories", "images", "templates", "imageinfo"));
			parameters.put("iiprop", Joiner.on("|").join("timestamp", "user", "url", "metadata"));
			break;
		default:
			parameters.put("prop", Joiner.on("|").join("categories", "images", "templates"));
			break;
		}
		parameters.put("titles", URLEncoder.encode(PageUtility.toFullPageNamee(page), "UTF-8"));
		
		File file = downloadPage(page, url, parameters, fileExtension);
		
//		String content = Files.toString(file, Charsets.UTF_8);
//		System.out.println(content);
//		System.out.println();
		
		if(page.getType() == PageType.Image) {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			docFactory.setNamespaceAware(true);
			try {
				DocumentBuilder builder = docFactory.newDocumentBuilder();
				Document doc = builder.parse(file);
				
				XPathFactory factory = XPathFactory.newInstance();
				XPath xpath = factory.newXPath();
				XPathExpression expr = xpath.compile("//imageinfo/ii/@url");
				NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
				if(nodes.getLength() == 1) {
					downloadImage(nodes.item(0).getNodeValue());
				} else {
					throw new UnexpectedException("[Get Image URL] Unexpected node length: "+nodes.getLength());
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	private static File downloadPage(Page page, String url, Map<String, String> parameters, String fileExtension) throws IOException, MalformedURLException {
		Preconditions.checkNotNull(page.getType(), "Page#Type can not be null");

		File file = new File(PageUtility.toFilePath(page, fileExtension));
		
		String fullUrl = Joiner.on("?").join(url, Joiner.on("&").withKeyValueSeparator("=").join(parameters));
		fullUrl = fullUrl.replaceAll(" ", "%20");
		
		downlaod(file, fullUrl);
		
		return file;
	}
	
	private static File downloadImage(String imageServerPath) throws IOException, MalformedURLException {
		Preconditions.checkNotNull(imageServerPath, "imageServerPath can not be null");
		
		String imageName = imageServerPath;
		int lastIndex = CharMatcher.is('/').lastIndexIn(imageName);
		if(lastIndex > 0) {
			imageName = imageName.substring(lastIndex+1);
		}
		File file = new File(ProjectProperties.getWikiSourceFolder() + ProjectProperties.getFileSeparator()  + PageType.Image.name() + ProjectProperties.getFileSeparator() + imageName);
		
		String fullUrl = ProjectProperties.getWikiServerUrl() + imageServerPath;
		
		downlaod(file, fullUrl);
		
		return file;
	}

	private static void downlaod(File file, String fullUrl) throws MalformedURLException, IOException {
		System.out.println(fullUrl);

		InputSupplier<InputStream> inputSupplier = Resources.newInputStreamSupplier(new URL(fullUrl));
		Files.createParentDirs(file);
		Files.copy(inputSupplier, file);
	}
}
