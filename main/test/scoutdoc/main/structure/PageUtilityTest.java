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

import org.junit.Assert;
import org.junit.Test;

import scoutdoc.main.ProjectProperties;

public class PageUtilityTest {

	@Test
	public void testToFullPageNamee() {
		Assert.assertEquals("MyPage", PageUtility.toFullPageNamee(createPage(PageType.Article, "MyPage")));
		Assert.assertEquals("Root/MyPage", PageUtility.toFullPageNamee(createPage(PageType.Article, "Root/MyPage")));
		Assert.assertEquals("MyPage_Foo", PageUtility.toFullPageNamee(createPage(PageType.Article, "MyPage_Foo")));
		Assert.assertEquals("Root/MyPage_Foo", PageUtility.toFullPageNamee(createPage(PageType.Article, "Root/MyPage_Foo")));
		Assert.assertEquals("Template:MyPage", PageUtility.toFullPageNamee(createPage(PageType.Template, "MyPage")));
		Assert.assertEquals("Template:Root/MyPage", PageUtility.toFullPageNamee(createPage(PageType.Template, "Root/MyPage")));
		Assert.assertEquals("Template:MyPage_Foo", PageUtility.toFullPageNamee(createPage(PageType.Template, "MyPage_Foo")));
		Assert.assertEquals("Template:Root/MyPage_Foo", PageUtility.toFullPageNamee(createPage(PageType.Template, "Root/MyPage_Foo")));
	}

	@Test
	public void testToBasePageNamee() {
		Assert.assertEquals("MyPage", PageUtility.toBasePageNamee(createPage(PageType.Article, "MyPage")));
		Assert.assertEquals("MyPage", PageUtility.toBasePageNamee(createPage(PageType.Article, "Root/MyPage")));
		Assert.assertEquals("MyPage_Foo", PageUtility.toBasePageNamee(createPage(PageType.Article, "MyPage_Foo")));
		Assert.assertEquals("MyPage_Foo", PageUtility.toBasePageNamee(createPage(PageType.Article, "Root/MyPage_Foo")));
		Assert.assertEquals("MyPage", PageUtility.toBasePageNamee(createPage(PageType.Template, "MyPage")));
		Assert.assertEquals("MyPage", PageUtility.toBasePageNamee(createPage(PageType.Template, "Root/MyPage")));
		Assert.assertEquals("MyPage_Foo", PageUtility.toBasePageNamee(createPage(PageType.Template, "MyPage_Foo")));
		Assert.assertEquals("MyPage_Foo", PageUtility.toBasePageNamee(createPage(PageType.Template, "Root/MyPage_Foo")));
	}
	
	@Test
	public void testToBasePageName() {
		Assert.assertEquals("MyPage", PageUtility.toBasePageName(createPage(PageType.Article, "MyPage")));
		Assert.assertEquals("MyPage", PageUtility.toBasePageName(createPage(PageType.Article, "Root/MyPage")));
		Assert.assertEquals("MyPage Foo", PageUtility.toBasePageName(createPage(PageType.Article, "MyPage_Foo")));
		Assert.assertEquals("MyPage Foo", PageUtility.toBasePageName(createPage(PageType.Article, "Root/MyPage_Foo")));
		Assert.assertEquals("MyPage", PageUtility.toBasePageName(createPage(PageType.Template, "MyPage")));
		Assert.assertEquals("MyPage", PageUtility.toBasePageName(createPage(PageType.Template, "Root/MyPage")));
		Assert.assertEquals("MyPage Foo", PageUtility.toBasePageName(createPage(PageType.Template, "MyPage_Foo")));
		Assert.assertEquals("MyPage Foo", PageUtility.toBasePageName(createPage(PageType.Template, "Root/MyPage_Foo")));
	}

	@Test
	public void testToFilePath() {
		runToFilePath(createPage(PageType.Article, "Root/MyPage"));
		runToFilePath(createPage(PageType.Image, "Img1.png"));
		runToFilePath(createPage(PageType.Article, "My/Page%2F"));
	}

	private void runToFilePath(Page page) {
		String root = ProjectProperties.getFolderWikiSource() + ProjectProperties.getFileSeparator() + page.getType() + ProjectProperties.getFileSeparator() + page.getName();
		Assert.assertEquals(root + ".mediawiki", PageUtility.toFilePath(page));
		Assert.assertEquals(root + ".txt", PageUtility.toFilePath(page,"txt"));
	}

	@Test
	public void testToPage() {
		runToPage(PageType.Template, "Root/MyPage_Foo", "Template:Root/MyPage_Foo");
		runToPage(PageType.File, "This/MyPage_Foo", "File:This/MyPage_Foo");
		runToPage(PageType.Article, "My/Page", "My/Page");
		runToPage(PageType.Article, "My/Page%2F", "My/Page/");
	}

	private void runToPage(PageType expectedPageType, String expectedPageName, String fullPageName) {
		Page page = PageUtility.toPage(fullPageName);
		Assert.assertEquals("Page Type", expectedPageType, page.getType());
		Assert.assertEquals("Page Name", expectedPageName, page.getName());
	}
	
	@Test
	public void testToFile() {
		runToFile("Root/MyPage.mediawiki", createPage(PageType.Article, "Root/MyPage"));
		runToFile("TheTemplate.mediawiki", createPage(PageType.Template, "TheTemplate"));
		runToFile("Img1.png", createPage(PageType.Image, "Img1.png"));
		runToFile("Img1.png", PageUtility.toPage("Image:Img1.png"));
		runToFile("My/Page%2F.mediawiki", createPage(PageType.Article, "My/Page%2F"));
		runToFile("My/Page%2F.mediawiki", PageUtility.toPage("My/Page/"));
		runToFile("Img1.png", createPage(PageType.File, "Img1.png"));
		runToFile("Img1.png", PageUtility.toPage("File:Img1.png"));
	}

	private void runToFile(String expectedFileName, Page page) {
		String root = ProjectProperties.getFolderWikiSource() + ProjectProperties.getFileSeparator() + page.getType() + ProjectProperties.getFileSeparator();
		File expected = new File(root + expectedFileName);
		File actual = PageUtility.toFile(page);
		Assert.assertEquals("AbsolutePath", expected.getAbsolutePath(), actual.getAbsolutePath());
	}
	

	private Page createPage(PageType type, String name) {
		Page page = new Page();
		page.setType(type);
		page.setName(name);
		return page;
	}

}
