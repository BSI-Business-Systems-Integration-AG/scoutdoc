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

package scoutdoc.main.mediawiki;

import java.io.ByteArrayInputStream;
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

import scoutdoc.main.structure.Page;
import scoutdoc.main.structure.PageType;
import scoutdoc.main.structure.PageUtility;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;

public class ApiFileUtility {

  public static final Collection<ApiFileContentType> ALL = Arrays.asList(ApiFileContentType.values());
  public static final Collection<ApiFileContentType> IMAGES_AND_TEMPLATES = Arrays.asList(ApiFileContentType.Images, ApiFileContentType.Templates);
  public static final Collection<ApiFileContentType> IMAGES_TEMPLATES_AND_LINKS = Arrays.asList(ApiFileContentType.Images, ApiFileContentType.Links, ApiFileContentType.Templates);
  public static final Collection<ApiFileContentType> CATEGORIES_IMAGES_AND_TEMPLATES = Arrays.asList(ApiFileContentType.Categories, ApiFileContentType.Images, ApiFileContentType.Templates);

  public static Collection<Page> parseContent(File apiFile, Collection<ApiFileContentType> types) {
    Set<Page> pages = new HashSet<Page>();
    for (ApiFileContentType t : types) {
      switch (t) {
        case Categories:
          pages.addAll(parseCategories(apiFile));
          break;
        case Images:
          pages.addAll(parseImages(apiFile));
          break;
        case Links:
          pages.addAll(parseLinks(apiFile));
          break;
        case Templates:
          pages.addAll(parseTemplates(apiFile));
          break;
        default:
          throw new IllegalStateException("Unexpected type " + t);
      }
    }
    return Collections.unmodifiableCollection(pages);
  }

  /**
   * @param apiFile
   *          the API File
   * @return categories
   */
  public static Collection<Page> parseCategories(File apiFile) {
    return parseApiFile(apiFile, "//cl/@title");
  }

  /**
   * @param apiFile
   *          the API File
   * @return categories
   */
  public static Collection<Page> parseImages(File apiFile) {
    return parseApiFile(apiFile, "//im/@title");

  }

  /**
   * @param apiFile
   *          the API File
   * @return links
   */
  public static Collection<Page> parseLinks(File apiFile) {
    return parseApiFile(apiFile, "//pl/@title");
  }

  /**
   * @param apiFile
   *          the API File
   * @return templates
   */
  public static Collection<Page> parseTemplates(File apiFile) {
    return parseApiFile(apiFile, "//tl/@title");
  }

  public static boolean isParentCategory(Page category, Page targetCategory) {
    Preconditions.checkArgument(category.getType() == PageType.Category, "category should have the type PageType.Category");
    Preconditions.checkArgument(targetCategory.getType() == PageType.Category, "targetCategory should have the type PageType.Category");
    return containsTarget(category, targetCategory, ApiFileContentType.Categories, true);
  }

  public static boolean containsTarget(Page page, Page targetPage, ApiFileContentType targetType, boolean followContent) {
    return containsTarget(page, targetPage, targetType, followContent, new HashSet<Page>());
  }

  private static boolean containsTarget(Page page, Page targetPage, ApiFileContentType targetType, boolean followContent, Set<Page> checkedPages) {
    File apiFile = PageUtility.toApiFile(page);
    Collection<Page> content = ApiFileUtility.parseContent(apiFile, Collections.singletonList(targetType));
    if (content.contains(targetPage)) {
      return true;
    }
    else if (followContent) {
      checkedPages.add(page);
      for (Page p : content) {
        if (!checkedPages.contains(p) && containsTarget(p, targetPage, targetType, followContent, checkedPages)) {
          return true;
        }
      }
    }
    return false;
  }

  public static long readRevisionId(File file) {
    if (!file.exists()) {
      return 0L;
    }
    return readRevisionId(createInputSource(file));
  }

  public static long readRevisionId(String content) {
    return readRevisionId(createInputSource(content));
  }

  private static long readRevisionId(InputSource inputSource) {
    String value = readValue(inputSource, "//revisions/rev/@revid");
    if (value == null) {
      return 0L;
    }
    else {
      return Long.parseLong(value);
    }
  }

  public static String readTimestamp(File apiFile) {
    return readValue(createInputSource(apiFile), "//revisions/rev/@timestamp");
  }

  public static String readValue(File file, String xpathQuery) {
    return readValue(createInputSource(file), xpathQuery);
  }

  public static String readValue(String content, String xpathQuery) {
    return readValue(createInputSource(content), xpathQuery);
  }

  public static List<String> readValues(File file, String xpathQuery) {
    return readValues(createInputSource(file), xpathQuery);
  }

  public static List<String> readValues(String content, String xpathQuery) {
    return readValues(createInputSource(content), xpathQuery);
  }

  private static String readValue(InputSource inputSource, String xpathQuery) {
    List<String> values = readValues(inputSource, xpathQuery);
    if (values.size() == 0) {
      return null;
    }
    else if (values.size() == 1) {
      return values.get(0);
    }
    else {
      throw new IllegalStateException("[Xpath] Unexpected node length: " + values.size() + ". Expected 1, at " + xpathQuery);
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
    if (inputSource == null) {
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
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return Collections.unmodifiableList(result);
  }

  private static InputSource createInputSource(String content) {
    return new InputSource(new ByteArrayInputStream(content.getBytes(Charsets.UTF_8)));
  }

  private static InputSource createInputSource(File file) {
    InputSource inputSource = null;
    try {
      inputSource = new InputSource(new FileInputStream(file));
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return inputSource;
  }
}
