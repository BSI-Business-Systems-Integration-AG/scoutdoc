package scoutdoc.main;

import java.util.Collection;

import junit.framework.Assert;
import scoutdoc.main.structure.Page;
import scoutdoc.main.structure.PageType;
import scoutdoc.main.structure.Pages;

/**
 * Test Utility
 */
public class TU {
  public static final Page CAT_1 = createPage(PageType.Category, "Test_Cat1", 135);
  public static final Page CAT_2 = createPage(PageType.Category, "Test_Cat2", 136);
  public static final Page CAT_ROOT = createPage(PageType.Category, "Test_CRoot", 143);
  public static final Page IMG_1 = createPage(PageType.File, "Test_Img1.png", 128);
  public static final Page IMG_2 = createPage(PageType.File, "Test_Img2.png", 129);
  public static final Page IMG_3 = createPage(PageType.File, "Test_Img3.png", 130);
  public static final Page PAGE_1 = createPage(PageType.Article, "Test_Page1", 125);
  public static final Page PAGE_2 = createPage(PageType.Article, "Test_Page2", 132);
  public static final Page PAGE_3 = createPage(PageType.Article, "Test_Page3", 134);
  public static final Page RED_1 = createPage(PageType.Article, "Test_Red1", 142);
  public static final Page RED_2 = createPage(PageType.Article, "Test_Red2", 141);
  public static final Page RED_3 = createPage(PageType.Article, "Test_Red3", 139);
  public static final Page RED_SELF = createPage(PageType.Article, "Test_RedSelf", 14);
  public static final Page RED_CIRC_1 = createPage(PageType.Article, "Test_RedCirc1", 137);
  public static final Page RED_CIRC_2 = createPage(PageType.Article, "Test_RedCirc2", 144);
  public static final Page RED_CIRC_3 = createPage(PageType.Article, "Test_RedCirc3", 133);
  public static final Page TMP_1 = createPage(PageType.Template, "Test_Ipsum", 127);
  public static final Page TMP_2 = createPage(PageType.Template, "Test_Lorem", 126);
  public static final Page TMP_3 = createPage(PageType.Template, "Test_Dolore", 131);

  public static Page createPage(PageType type, String name, int id) {
    Page page = createIncompletePage(type, name);
    page.setId(Integer.valueOf(id));
    return page;
  }

  public static Page createIncompletePage(PageType type, String name) {
    Page page = new Page();
    page.setType(type);
    page.setName(name);
    return page;
  }

  public static void assertPageEquals(PageType expectedPageType, String expectedPageName, Page actual) {
    Assert.assertEquals("Page Type", expectedPageType, actual.getType());
    Assert.assertEquals("Page Name", expectedPageName, actual.getName());
  }

  public static void assertPageEquals(Page expected, Page actual) {
    if (expected == null) {
      Assert.assertNull("expect actual Page to be null", actual);
    }
    else {
      Assert.assertEquals("Page Type", expected.getType(), actual.getType());
      Assert.assertEquals("Page Name", expected.getName(), actual.getName());
      Assert.assertEquals("Page Id", expected.getId(), actual.getId());
    }
  }

  public static void init() {
    ProjectProperties.initProperties("test_data/test.properties");
    Pages.initPageList();
  }

  public static void assertPageIsContained(Page expectedPage, Collection<Page> pages) {
    Assert.assertEquals("contains " + expectedPage, true, pages.contains(expectedPage));
  }

}
