package scoutdoc.main;

import junit.framework.Assert;
import scoutdoc.main.structure.Page;
import scoutdoc.main.structure.PageType;

/**
 * Test Utility
 */
public class TU {
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
		Assert.assertEquals("Page Type", expected.getType(), actual.getType());
		Assert.assertEquals("Page Name", expected.getName(), actual.getName());		
	}
	
}
