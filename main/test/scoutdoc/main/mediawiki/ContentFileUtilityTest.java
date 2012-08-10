package scoutdoc.main.mediawiki;

import junit.framework.Assert;

import org.junit.Test;

import scoutdoc.main.TU;
import scoutdoc.main.structure.Page;
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
		
		Page page1 = TU.createPage(PageType.Article, "Page1");
		TU.assertPageEquals(page1, ContentFileUtility.checkRedirection("#REDIRECT [[Page1]]"));
		TU.assertPageEquals(page1, ContentFileUtility.checkRedirection("#REDIRECT [[Page1]]\n"));

		Page myPage = TU.createPage(PageType.Article, "My_Page");
		TU.assertPageEquals(myPage, ContentFileUtility.checkRedirection("#REDIRECT [[My Page]]"));
		TU.assertPageEquals(myPage, ContentFileUtility.checkRedirection("#REDIRECT [[My_Page]]"));

		Page rootPage = TU.createPage(PageType.Article, "Root/Page");
		TU.assertPageEquals(rootPage, ContentFileUtility.checkRedirection("#REDIRECT [[Root/Page]]"));
		
		Page rootPageSlash = TU.createPage(PageType.Article, "Root/Page%2F");
		TU.assertPageEquals(rootPageSlash, ContentFileUtility.checkRedirection("#REDIRECT [[Root/Page/]]"));
		
		Page category = TU.createPage(PageType.Category, "MyCategory");
		TU.assertPageEquals(category, ContentFileUtility.checkRedirection("#REDIRECT [[:Category:MyCategory]]"));
		TU.assertPageEquals(category, ContentFileUtility.checkRedirection("#REDIRECT [[:Category:MyCategory]]\n"));
	}
	
	@Test
	public void testCheckRedirectionPage() {
		TU.initProperties();
		
		TU.assertPageEquals(TU.RED_2, ContentFileUtility.checkRedirection(TU.RED_1));
		TU.assertPageEquals(TU.PAGE_2, ContentFileUtility.checkRedirection(TU.RED_2));
		TU.assertPageEquals(null, ContentFileUtility.checkRedirection(TU.PAGE_2));
		TU.assertPageEquals(null, ContentFileUtility.checkRedirection(TU.RED_SELF));		
	}


}
