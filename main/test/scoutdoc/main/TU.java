package scoutdoc.main;

import java.util.Collection;

import junit.framework.Assert;
import scoutdoc.main.structure.Page;
import scoutdoc.main.structure.PageType;
import scoutdoc.main.structure.PageUtility;

/**
 * Test Utility
 */
public class TU {
	public static final Page CAT_1 = PageUtility.toPage("Category:Test Cat1");
	public static final Page CAT_2 = PageUtility.toPage("Category:Test Cat2");
	public static final Page IMG_1 = PageUtility.toPage("File:Test Img1.png");
	public static final Page IMG_2 = PageUtility.toPage("File:Test Img2.png");
	public static final Page IMG_3 = PageUtility.toPage("File:Test Img3.png");;
	public static final Page PAGE_1 = PageUtility.toPage("Test Page1");
	public static final Page PAGE_2 = createPage(PageType.Article, "Test_Page2");
	public static final Page PAGE_3 = PageUtility.toPage("Test Page3");
	public static final Page RED_1 = createPage(PageType.Article, "Test_Red1");
	public static final Page RED_2 = createPage(PageType.Article, "Test_Red2");
	public static final Page RED_SELF = createPage(PageType.Article, "Test_RedSelf");
	public static final Page RED_CIRC_1 = PageUtility.toPage("Test RedCirc1");
	public static final Page RED_CIRC_2 = PageUtility.toPage("Test_RedCirc2");;
	public static final Page RED_CIRC_3 = PageUtility.toPage("Test_RedCirc3");;
	public static final Page TMP_1 = PageUtility.toPage("Template:Test Ipsum");
	public static final Page TMP_2 = PageUtility.toPage("Template:Test Lorem");
	public static final Page TMP_3 = PageUtility.toPage("Template:Test Dolore");;
	
	public static Page createPage(PageType type, String name) {
		Page page = new Page();
		page.setType(type);
		page.setName(name);
		return page;
	}
	
	public static void assertPageEquals(PageType expectedPageType, String expectedPageName, Page actual) {
		Page expected = createPage(expectedPageType, expectedPageName);
		assertPageEquals(expected, actual);
	}
	
	public static void assertPageEquals(Page expected, Page actual) {
		if(expected == null) {
			Assert.assertNull("expect actual Page to be null", actual);
		} else {
			Assert.assertEquals("Page Type", expected.getType(), actual.getType());
			Assert.assertEquals("Page Name", expected.getName(), actual.getName());			
		}
	}

	public static void initProperties() {
		ProjectProperties.initProperties("test_data/test.properties");		
	}

	public static void assertPageIsContained(Page expectedPage, Collection<Page> pages) {
		Assert.assertEquals("contains "+expectedPage, true, pages.contains(expectedPage));		
	}
	
}
