package scoutdoc.main;

import junit.framework.Assert;
import scoutdoc.main.structure.Page;
import scoutdoc.main.structure.PageType;

/**
 * Test Utility
 */
public class TU {
	public static Page RED_1 = createPage(PageType.Article, "Test_Red1");
	public static Page RED_2 = createPage(PageType.Article, "Test_Red2");
	public static Page PAGE_2 = createPage(PageType.Article, "Test_Page2");
	public static Page RED_SELF = createPage(PageType.Article, "Test_RedSelf");
	
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
	
}
