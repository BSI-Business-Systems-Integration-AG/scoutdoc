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

import junit.framework.Assert;

import org.junit.Test;

import scoutdoc.main.ProjectProperties;
import scoutdoc.main.TU;
import scoutdoc.main.structure.PageType;

public class ContentFileUtilityTest {

  @Test
  public void testCheckRedirectionString() {
    Assert.assertEquals(null, ContentFileUtility.checkRedirection("lorem Ipsum"));
    Assert.assertEquals(null, ContentFileUtility.checkRedirection("lorem [[Page1]] Ipsum"));
    Assert.assertEquals(null, ContentFileUtility.checkRedirection("lorem [[Page1]]"));
    Assert.assertEquals(null, ContentFileUtility.checkRedirection("#REDIRECT [[Page1]"));
    Assert.assertEquals(null, ContentFileUtility.checkRedirection("#REDIRECT [Page1]"));
    Assert.assertEquals(null, ContentFileUtility.checkRedirection("#REDIRECT [Page1]]"));
    Assert.assertEquals(null, ContentFileUtility.checkRedirection("REDIRECT [[Page1]]"));

    TU.assertPageEquals(PageType.Article, "Page1", ContentFileUtility.checkRedirection("#REDIRECT [[Page1]]"));
    TU.assertPageEquals(PageType.Article, "Page1", ContentFileUtility.checkRedirection("#REDIRECT [[Page1]]\n"));
    TU.assertPageEquals(PageType.Article, "Page1", ContentFileUtility.checkRedirection("#REDIRECT[[Page1]]"));
    TU.assertPageEquals(PageType.Article, "Page1", ContentFileUtility.checkRedirection("#REDIRECT[[Page1]]\n"));

    TU.assertPageEquals(PageType.Article, "My_Page", ContentFileUtility.checkRedirection("#REDIRECT [[My Page]]"));
    TU.assertPageEquals(PageType.Article, "My_Page", ContentFileUtility.checkRedirection("#REDIRECT [[My_Page]]"));

    TU.assertPageEquals(PageType.Article, "Root/Page", ContentFileUtility.checkRedirection("#REDIRECT [[Root/Page]]"));
    TU.assertPageEquals(PageType.Article, "Root/Page/", ContentFileUtility.checkRedirection("#REDIRECT [[Root/Page/]]"));

    TU.assertPageEquals(PageType.Category, "MyCategory", ContentFileUtility.checkRedirection("#REDIRECT [[:Category:MyCategory]]"));
    TU.assertPageEquals(PageType.Category, "MyCategory", ContentFileUtility.checkRedirection("#REDIRECT [[:Category:MyCategory]]\n"));
  }

  @Test
  public void testCheckRedirectionStringWithCustomPrefix() {
    TU.init();

    //Precondition:
    Assert.assertTrue(ProjectProperties.getMediaWikiConfiguration().getRedirectionPrefixes().contains("#CUSTOM_REDIRECTION"));

    //Test:
    TU.assertPageEquals(PageType.Article, "ThisPage", ContentFileUtility.checkRedirection("#CUSTOM_REDIRECTION [[ThisPage]]"));
    TU.assertPageEquals(PageType.Article, "ThisPage", ContentFileUtility.checkRedirection("#CUSTOM_REDIRECTION [[ThisPage]]\n"));
    TU.assertPageEquals(PageType.Article, "ThisPage", ContentFileUtility.checkRedirection("#CUSTOM_REDIRECTION[[ThisPage]]"));
    TU.assertPageEquals(PageType.Article, "ThisPage", ContentFileUtility.checkRedirection("#CUSTOM_REDIRECTION[[ThisPage]]\n"));
  }

  @Test
  public void testCheckRedirectionPage() {
    TU.init();

    TU.assertPageEquals(TU.RED_2, ContentFileUtility.checkRedirection(TU.RED_1));
    TU.assertPageEquals(TU.PAGE_2, ContentFileUtility.checkRedirection(TU.RED_2));
    TU.assertPageEquals(null, ContentFileUtility.checkRedirection(TU.PAGE_2));
    TU.assertPageEquals(null, ContentFileUtility.checkRedirection(TU.RED_SELF));
  }

  @Test
  public void testCheckRedirectionPageNotExist() {
    TU.init();

    //Page do no exists:
    TU.assertPageEquals(null, ContentFileUtility.checkRedirection(TU.createPage(PageType.Article, "MyPage", 3)));

    //Page is unknown:
    TU.assertPageEquals(null, ContentFileUtility.checkRedirection(TU.createIncompletePage(PageType.Article, "MyPage2")));
  }

}
