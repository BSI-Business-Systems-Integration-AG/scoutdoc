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

import java.io.File;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import scoutdoc.main.TU;
import scoutdoc.main.structure.Page;
import scoutdoc.main.structure.PageUtility;

public class ApiFileUtilityTest {

  @Test
  public void testReadRecisionId() {
    runReadRecisionId(245, "<rev revid=\"245\" parentid=\"243\" user=\"Admin\" timestamp=\"2012-08-10T16:59:40Z\" comment=\"\"/>");
    runReadRecisionId(5, "<rev revid=\"5\" />");
    runReadRecisionId(0, "");
  }

  private void runReadRecisionId(int expected, String content) {
    StringBuilder sb = new StringBuilder();
    sb.append("<api><query><pages><page><revisions>");
    sb.append(content);
    sb.append("</revisions></page></pages></query></api>");

    long actual = ApiFileUtility.readRevisionId(sb.toString());
    Assert.assertEquals(expected, actual);
  }

  @Test
  public void testReadRecisionIdApiFile() {
    long actual = ApiFileUtility.readRevisionId(initAndGetApiFile());
    Assert.assertEquals(245, actual);
  }

  @Test
  public void testReadTimestampApiFile() throws Exception {
    String timestamp = ApiFileUtility.readTimestamp(initAndGetApiFile());
    Assert.assertEquals("2012-08-10T16:59:40Z", timestamp);
  }

  @Test
  public void testParseCategories() {
    Collection<Page> categories = ApiFileUtility.parseCategories(initAndGetApiFile());
    Assert.assertEquals("size", 2, categories.size());
    TU.assertPageIsContained(TU.CAT_1, categories);
    TU.assertPageIsContained(TU.CAT_2, categories);
  }

  @Test
  public void testParseImages() {
    Collection<Page> images = ApiFileUtility.parseImages(initAndGetApiFile());
    Assert.assertEquals("size", 1, images.size());
    TU.assertPageIsContained(TU.IMG_1, images);
  }

  @Test
  public void testParseLinks() {
    Collection<Page> links = ApiFileUtility.parseLinks(initAndGetApiFile());
    Assert.assertEquals("size", 3, links.size());
    TU.assertPageIsContained(TU.PAGE_2, links);
    TU.assertPageIsContained(TU.PAGE_3, links);
    TU.assertPageIsContained(TU.RED_1, links);
  }

  @Test
  public void testParseTemplates() {
    Collection<Page> templates = ApiFileUtility.parseTemplates(initAndGetApiFile());
    Assert.assertEquals("size", 2, templates.size());
    TU.assertPageIsContained(TU.TMP_1, templates);
    TU.assertPageIsContained(TU.TMP_2, templates);
  }

  @Test
  public void testParseInvalidValue() {
    String content = "<?xml version=\"1.0\"?><api><query><pages><page title=\"Page1%2F\" invalid=\"\" /></pages></query></api>";
    List<String> values1 = ApiFileUtility.readValues(content, "//page[@invalid]");
    Assert.assertEquals("values size", 1, values1.size());

    File file = initAndGetApiFile();
    List<String> values2 = ApiFileUtility.readValues(file, "//page[@invalid]");
    Assert.assertEquals("values size", 0, values2.size());
  }

  @Test
  public void testIsParentCategory() throws Exception {
    Assert.assertEquals("CAT1 is parent from CAT2", true, ApiFileUtility.isParentCategory(TU.CAT_2, TU.CAT_1));
    Assert.assertEquals("CAT2 is parent from CAT1", false, ApiFileUtility.isParentCategory(TU.CAT_1, TU.CAT_2));
    Assert.assertEquals("ROOT is parent from CAT2", true, ApiFileUtility.isParentCategory(TU.CAT_2, TU.CAT_ROOT));
    Assert.assertEquals("CAT2 is parent from ROOT", false, ApiFileUtility.isParentCategory(TU.CAT_ROOT, TU.CAT_2));
    Assert.assertEquals("CAT1 is parent from CAT1", false, ApiFileUtility.isParentCategory(TU.CAT_1, TU.CAT_1));
  }

  private File initAndGetApiFile() {
    TU.initProperties();
    return PageUtility.toApiFile(TU.PAGE_1);
  }

}
