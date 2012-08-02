package scoutdoc.main.fetch;

import java.io.File;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import scoutdoc.main.structure.Page;
import scoutdoc.main.structure.PageUtility;

public class ApiFileUtility {
	
	public static void parseImages(File apiFile, Set<Page> imagesSet) {
		parseApiFile(apiFile, "//im/@title", imagesSet);
	}
	
	public static void parseTemplate(File apiFile, Set<Page> templatesSet) {
		parseApiFile(apiFile, "//tl/@title", templatesSet);
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
}
