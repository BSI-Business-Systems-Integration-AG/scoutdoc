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

package scoutdoc.main.structure;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.io.Files;

import scoutdoc.main.ProjectProperties;
import scoutdoc.main.TU;

public class PageUtilityTest {

	@Test
	public void testToFullPageNamee() {
		Assert.assertEquals("MyPage", PageUtility.toFullPageNamee(TU.createPage(PageType.Article, "MyPage")));
		Assert.assertEquals("Root/MyPage", PageUtility.toFullPageNamee(TU.createPage(PageType.Article, "Root/MyPage")));
		Assert.assertEquals("MyPage_Foo", PageUtility.toFullPageNamee(TU.createPage(PageType.Article, "MyPage_Foo")));
		Assert.assertEquals("Root/MyPage_Foo", PageUtility.toFullPageNamee(TU.createPage(PageType.Article, "Root/MyPage_Foo")));
		Assert.assertEquals("Template:MyPage", PageUtility.toFullPageNamee(TU.createPage(PageType.Template, "MyPage")));
		Assert.assertEquals("Template:Root/MyPage", PageUtility.toFullPageNamee(TU.createPage(PageType.Template, "Root/MyPage")));
		Assert.assertEquals("Template:MyPage_Foo", PageUtility.toFullPageNamee(TU.createPage(PageType.Template, "MyPage_Foo")));
		Assert.assertEquals("Template:Root/MyPage_Foo", PageUtility.toFullPageNamee(TU.createPage(PageType.Template, "Root/MyPage_Foo")));
		Assert.assertEquals("Template:Root/MyPage_Foo/", PageUtility.toFullPageNamee(TU.createPage(PageType.Template, "Root/MyPage_Foo/")));
		Assert.assertEquals("Root/MyPage_Foo/", PageUtility.toFullPageNamee(TU.createPage(PageType.Article, "Root/MyPage_Foo%2F")));
		Assert.assertEquals("Template:Root/MyPage_Foo/", PageUtility.toFullPageNamee(PageUtility.toPage("Template:Root/MyPage_Foo/")));
	}

	@Test
	public void testToBasePageNamee() {
		Assert.assertEquals("MyPage", PageUtility.toBasePageNamee(TU.createPage(PageType.Article, "MyPage")));
		Assert.assertEquals("MyPage", PageUtility.toBasePageNamee(TU.createPage(PageType.Article, "Root/MyPage")));
		Assert.assertEquals("MyPage_Foo", PageUtility.toBasePageNamee(TU.createPage(PageType.Article, "MyPage_Foo")));
		Assert.assertEquals("MyPage_Foo", PageUtility.toBasePageNamee(TU.createPage(PageType.Article, "Root/MyPage_Foo")));
		Assert.assertEquals("MyPage", PageUtility.toBasePageNamee(TU.createPage(PageType.Template, "MyPage")));
		Assert.assertEquals("MyPage", PageUtility.toBasePageNamee(TU.createPage(PageType.Template, "Root/MyPage")));
		Assert.assertEquals("MyPage_Foo", PageUtility.toBasePageNamee(TU.createPage(PageType.Template, "MyPage_Foo")));
		Assert.assertEquals("MyPage_Foo", PageUtility.toBasePageNamee(TU.createPage(PageType.Template, "Root/MyPage_Foo")));
	}
	
	@Test
	public void testToBasePageName() {
		Assert.assertEquals("MyPage", PageUtility.toBasePageName(TU.createPage(PageType.Article, "MyPage")));
		Assert.assertEquals("MyPage", PageUtility.toBasePageName(TU.createPage(PageType.Article, "Root/MyPage")));
		Assert.assertEquals("MyPage Foo", PageUtility.toBasePageName(TU.createPage(PageType.Article, "MyPage_Foo")));
		Assert.assertEquals("MyPage Foo", PageUtility.toBasePageName(TU.createPage(PageType.Article, "Root/MyPage_Foo")));
		Assert.assertEquals("MyPage", PageUtility.toBasePageName(TU.createPage(PageType.Template, "MyPage")));
		Assert.assertEquals("MyPage", PageUtility.toBasePageName(TU.createPage(PageType.Template, "Root/MyPage")));
		Assert.assertEquals("MyPage Foo", PageUtility.toBasePageName(TU.createPage(PageType.Template, "MyPage_Foo")));
		Assert.assertEquals("MyPage Foo", PageUtility.toBasePageName(TU.createPage(PageType.Template, "Root/MyPage_Foo")));
	}

	@Test
	public void testToFilePath() {
		runToFilePath("Article/Root/MyPage",TU.createPage(PageType.Article, "Root/MyPage"));
		runToFilePath("Image/Img1.png",TU.createPage(PageType.Image, "Img1.png"));
		runToFilePath("Article/My/Page%2F",TU.createPage(PageType.Article, "My/Page%2F"));
		runToFilePath("Article/My/Page%2F",TU.createPage(PageType.Article, "My/Page/"));
	}

	private void runToFilePath(String expectedPageName, Page page) {
		String root = ProjectProperties.getFolderWikiSource() + ProjectProperties.getFileSeparator() + expectedPageName;
		Assert.assertEquals(root + ".mediawiki", PageUtility.toFilePath(page));
		Assert.assertEquals(root + ".txt", PageUtility.toFilePath(page,"txt"));
	}

	@Test
	public void testToPageString() {
		TU.assertPageEquals(PageType.Template, "Root/MyPage_Foo", PageUtility.toPage("Template:Root/MyPage_Foo"));
		TU.assertPageEquals(PageType.File, "This/MyPage_Foo", PageUtility.toPage("File:This/MyPage_Foo"));
		TU.assertPageEquals(PageType.Article, "My/Page", PageUtility.toPage("My/Page"));
		TU.assertPageEquals(PageType.Article, "My/Page%2F", PageUtility.toPage("My/Page/"));
	}

	@Test
	public void testToFile() {
		runToFile("Root/MyPage.mediawiki", TU.createPage(PageType.Article, "Root/MyPage"));
		runToFile("TheTemplate.mediawiki", TU.createPage(PageType.Template, "TheTemplate"));
		runToFile("Img1.png", TU.createPage(PageType.Image, "Img1.png"));
		runToFile("Img1.png", PageUtility.toPage("Image:Img1.png"));
		runToFile("My/Page%2F.mediawiki", TU.createPage(PageType.Article, "My/Page%2F"));
		runToFile("My/Page%2F.mediawiki", PageUtility.toPage("My/Page/"));
		runToFile("Img1.png", TU.createPage(PageType.File, "Img1.png"));
		runToFile("Img1.png", PageUtility.toPage("File:Img1.png"));
	}

	private void runToFile(String expectedFileName, Page page) {
		String root = ProjectProperties.getFolderWikiSource() + ProjectProperties.getFileSeparator() + page.getType() + ProjectProperties.getFileSeparator();
		File expected = new File(root + expectedFileName);
		File actual = PageUtility.toFile(page);
		Assert.assertEquals("AbsolutePath", expected.getAbsolutePath(), actual.getAbsolutePath());
	}
	
	@Test
	public void testToPageFile() throws Exception {
		TU.initProperties();
		runToPageFile(TU.PAGE_1);
		runToPageFile(TU.createPage(PageType.Article, "Root/MyPage"));
		runToPageFile(TU.createPage(PageType.File, "My/File%2F"));
		runToPageFile(TU.CAT_1);
		runToPageFile(TU.IMG_1);
		runToPageFile(TU.TMP_1);
	}

	private void runToPageFile(Page page) {
		String contentFilePath = PageUtility.toFilePath(page, ProjectProperties.FILE_EXTENTION_CONTENT);
		File contentFile = new File(contentFilePath);
		Assert.assertEquals(page, PageUtility.toPage(contentFile));
		
		String apiFilePath = PageUtility.toFilePath(page, ProjectProperties.FILE_EXTENTION_META);
		File apiFile = new File(apiFilePath);
		Assert.assertEquals(page, PageUtility.toPage(apiFile));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testLoadPagesIllegalBecauseNotAFolder() throws Exception {
		TU.initProperties();
		PageUtility.loadPages(File.createTempFile("Test", "mediawiki"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testLoadPagesIllegalBecauseNotInSource() throws Exception {
		TU.initProperties();
		PageUtility.loadPages(Files.createTempDir());
	}
	
	@Test
	public void testLoadPages() throws Exception {
		TU.initProperties();
		List<Page> pages1 = PageUtility.loadPages(new File(ProjectProperties.getFolderWikiSource()));
		Assert.assertEquals("size", 18, pages1.size());
		TU.assertPageIsContained(TU.CAT_1, pages1);	
		TU.assertPageIsContained(TU.CAT_2, pages1);	
		TU.assertPageIsContained(TU.IMG_1, pages1);	
		TU.assertPageIsContained(TU.IMG_2, pages1);	
		TU.assertPageIsContained(TU.IMG_3, pages1);	
		TU.assertPageIsContained(TU.PAGE_1, pages1);
		TU.assertPageIsContained(TU.PAGE_2, pages1);			
		TU.assertPageIsContained(TU.PAGE_3, pages1);	
		TU.assertPageIsContained(TU.RED_1, pages1);	
		TU.assertPageIsContained(TU.RED_2, pages1);	
		TU.assertPageIsContained(TU.RED_3, pages1);	
		TU.assertPageIsContained(TU.RED_CIRC_1, pages1);	
		TU.assertPageIsContained(TU.RED_CIRC_2, pages1);	
		TU.assertPageIsContained(TU.RED_CIRC_3, pages1);	
		TU.assertPageIsContained(TU.RED_SELF, pages1);	
		TU.assertPageIsContained(TU.TMP_1, pages1);	
		TU.assertPageIsContained(TU.TMP_2, pages1);
		TU.assertPageIsContained(TU.TMP_3, pages1);	
		
		List<Page> pages2 = PageUtility.loadPages(new File(ProjectProperties.getFolderWikiSource(), "Category"));
		Assert.assertEquals("size", 2, pages2.size());
		TU.assertPageIsContained(TU.CAT_1, pages2);	
		TU.assertPageIsContained(TU.CAT_2, pages2);	
	}
}
