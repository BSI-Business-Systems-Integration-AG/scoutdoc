package scoutdoc.main.mediawiki;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import scoutdoc.main.structure.ContentType;
import scoutdoc.main.structure.Page;
import scoutdoc.main.structure.PageUtility;

public class ApiFileUtility {
	
	public static final Collection<ContentType> ALL = Arrays.asList(ContentType.values());

	public static Collection<Page> parseContent(File apiFile, Collection<ContentType> types) {
		Set<Page> pages = new HashSet<Page>();
		for (ContentType t : types) {
			switch (t) {
			case Category:
				pages.addAll(parseCategories(apiFile));				
				break;
			case Images:
				pages.addAll(parseImages(apiFile));				
				break;
			case Links:
				pages.addAll(parseLinks(apiFile));
				break;
			case Template:
				pages.addAll(parseTemplate(apiFile));
				break;
			default:
				throw new IllegalStateException("Unexpected type "+t);
			}
		}
		return Collections.unmodifiableCollection(pages);
	}

	/**
	 * 
	 * @param apiFile the API File
	 * @return categories
	 */
	public static Collection<Page> parseCategories(File apiFile) {
		return parseApiFile(apiFile, "//cl/@title");
	}

	/**
	 * 
	 * @param apiFile the API File
	 * @return categories
	 */
	public static Collection<Page> parseImages(File apiFile) {
		return parseApiFile(apiFile, "//im/@title");

	}
	
	/**
	 * 
	 * @param apiFile the API File
	 * @return links
	 */
	private static Collection<Page> parseLinks(File apiFile) {
		return parseApiFile(apiFile, "//pl/@title");
	}
	
	/**
	 * 
	 * @param apiFile the API File
	 * @return templates
	 */
	public static Collection<Page> parseTemplate(File apiFile) {
		return parseApiFile(apiFile, "//tl/@title");
	}
		
	public static String readValue(File file, String xpathQuery) {
		return readValue(createInputSource(file), xpathQuery);
	}
	
	private static String readValue(InputSource inputSource, String xpathQuery) {
		List<String> values = readValues(inputSource, xpathQuery);
		if(values.size() == 0) {
			return null;
		} else if(values.size() == 1) {
			return values.get(0);
		} else {
			throw new IllegalStateException("[Xpath] Unexpected node length: "+values.size()+". Expected 1, at "+xpathQuery);
		}
	}
	
	private static Collection<Page> parseApiFile(File apiFile, String xpathQuery) {
		Set<Page> pagesSet = new HashSet<Page>();
		List<String> values = readValues(createInputSource(apiFile), xpathQuery);
		for (String v : values) {
			pagesSet.add(PageUtility.toPage(v));							
		}
		return Collections.unmodifiableCollection(pagesSet);
	}

	private static List<String> readValues(InputSource inputSource, String xpathQuery) {
		List<String> result = new ArrayList<String>();
		if(inputSource == null) {
			return result;
		}
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		docFactory.setNamespaceAware(true);
		try {
			DocumentBuilder builder = docFactory.newDocumentBuilder();
			Document doc = builder.parse(inputSource);
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath.compile(xpathQuery);
			NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength(); i++) {
				result.add(nodes.item(i).getNodeValue());				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
		
	private static InputSource createInputSource(File file) {
		InputSource inputSource = null;
		try {
			inputSource = new InputSource(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return inputSource;
	}
}
