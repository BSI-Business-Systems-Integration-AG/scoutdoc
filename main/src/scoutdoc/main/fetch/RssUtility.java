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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import scoutdoc.main.ProjectProperties;
import scoutdoc.main.eclipsescout.ScoutPages;
import scoutdoc.main.mediawiki.ApiFileUtility;
import scoutdoc.main.structure.Page;
import scoutdoc.main.structure.PageUtility;

import com.google.common.base.Splitter;

public class RssUtility {
  private static final String ARG_TITLE = "title=";
  private static final String ARG_DIFF = "diff=";
  private static final String ARG_OLDID = "oldid=";

//	public static List<Page> parseRss(InputSource inputSource) {
  public static List<Page> parseRss(String url) {
    List<Page> result = new ArrayList<Page>();

    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    docFactory.setNamespaceAware(true);
    try {
      DocumentBuilder builder = docFactory.newDocumentBuilder();
      Document doc = builder.parse(url);
      XPathFactory factory = XPathFactory.newInstance();
      XPath xpath = factory.newXPath();
      XPathExpression expr = xpath.compile("//item/link");
      NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
      for (int i = 0; i < nodes.getLength(); i++) {
        Page page = convertToPage(nodes.item(i).getTextContent());
        if (page != null) {
          result.add(page);
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return Collections.unmodifiableList(result);
  }

  public static Page convertToPage(String linkText) {
    String s = linkText;
    String title = null;
    String revIdText = null;
    if (s.startsWith(ProjectProperties.getWikiIndexUrl() + "?")) {
      s = s.substring(ProjectProperties.getWikiIndexUrl().length() + 1);
      Iterable<String> arguments = Splitter.on("&").split(s);
      for (String a : arguments) {
        if (a.startsWith(ARG_TITLE)) {
          title = a.substring(ARG_TITLE.length());
        }
        else if (a.startsWith(ARG_DIFF)) {
          revIdText = a.substring(ARG_DIFF.length());
        }
        else if (a.startsWith(ARG_OLDID)) {
          if (!"prev".equals(a.substring(ARG_OLDID.length()))) {
            System.err.println("Unexpected argument (oldid):" + a);
          }
        }
        else {
          System.err.println("Unexpected argument : " + a);
        }
      }
    }

    if (title != null) {
      Page page = PageUtility.toPage(title);
      if (page != null) {
        long revId = Long.MAX_VALUE;
        if (revIdText != null) {
          revId = Long.parseLong(revIdText);
        }
        File apiFile = new File(PageUtility.toFilePath(page, ProjectProperties.FILE_EXTENTION_META));
        if (ApiFileUtility.readRevisionId(apiFile) < revId) {
          return filterPage(page);
        }
      }
    }
    return null;
  }

  public static Page filterPage(Page page) {
    if (PageUtility.exists(page)) {
      return page;
    }
    else if (ScoutPages.isScoutPage(page)) {
      return page;
    }
    return null;
  }

}
