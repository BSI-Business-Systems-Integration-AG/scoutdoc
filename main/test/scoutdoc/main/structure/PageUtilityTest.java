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

import org.junit.Assert;
import org.junit.Test;

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

//	@Test
//	public void testToFilePathPageString() {
//		fail("Not yet implemented");
//	}

//	@Test
//	public void testToFilePathPage() {
//		fail("Not yet implemented");
//	}

//	@Test
//	public void testToPage() {
//		fail("Not yet implemented");
//	}

	private Page createPage(PageType type, String name) {
		Page page = new Page();
		page.setType(type);
		page.setName(name);
		return page;
	}

}
