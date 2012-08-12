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

import junit.framework.Assert;

import org.junit.Test;

import scoutdoc.main.ProjectProperties;
import scoutdoc.main.TU;
import scoutdoc.main.structure.Page;
import scoutdoc.main.structure.PageUtility;

public class ApiFileUtilityTest {

	private static final Page CAT1 = PageUtility.toPage("Category:Cat1");
	private static final Page CAT2 = PageUtility.toPage("Category:Cat2");
	private static final Page IMG1 = PageUtility.toPage("File:Img1.png");
	private static final Page TMP1 = PageUtility.toPage("Template:Ipsum");
	private static final Page TMP2 = PageUtility.toPage("Template:Lorem");
	private static final Page PAGE2 = PageUtility.toPage("Page2");
	private static final Page PAGE3 = PageUtility.toPage("Page3");
	private static final Page RED1 = PageUtility.toPage("Red1");

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
	public void testParseCategories() {
		Collection<Page> categories = ApiFileUtility.parseCategories(initAndGetApiFile());
		Assert.assertEquals("size", 2, categories.size());
        
        for (Page page : categories) {
			if(page.equals(CAT1)) {
				TU.assertPageEquals(CAT1, page);
			} else if(page.equals(CAT2)) {
				TU.assertPageEquals(CAT2, page);			
			} else {
				Assert.fail("Unknown page: "+page.getName());
			}
		}
	}
	
	@Test
	public void testParseImages() {
		Collection<Page> images = ApiFileUtility.parseImages(initAndGetApiFile());
		Assert.assertEquals("size", 1, images.size());
		
        for (Page page : images) {
			if(page.equals(IMG1)) {
				TU.assertPageEquals(IMG1, page);
			} else {
				Assert.fail("Unknown page: "+page.getName());
			}
		}
	}
	
	@Test
	public void testParseLinks() {
		Collection<Page> links = ApiFileUtility.parseLinks(initAndGetApiFile());
		Assert.assertEquals("size", 3, links.size());
		
        for (Page page : links) {
			if(page.equals(PAGE2)) {
				TU.assertPageEquals(PAGE2, page);
			} else if(page.equals(PAGE3)) {
				TU.assertPageEquals(PAGE3, page);			
			} else if(page.equals(RED1)) {
				TU.assertPageEquals(RED1, page);			
			} else {
				Assert.fail("Unknown page: "+page.getName());
			}
		}
	}
	
	@Test
	public void testParseTemplates() {
		Collection<Page> templates = ApiFileUtility.parseTemplate(initAndGetApiFile());
		Assert.assertEquals("size", 2, templates.size());
        
        for (Page page : templates) {
			if(page.equals(TMP1)) {
				TU.assertPageEquals(TMP1, page);
			} else if(page.equals(TMP2)) {
				TU.assertPageEquals(TMP2, page);			
			} else {
				Assert.fail("Unknown page: "+page.getName());
			}
		}
	}

	private File initAndGetApiFile() {
		TU.initProperties();
		Page page = PageUtility.toPage("Test_Page1");
		String filePath = PageUtility.toFilePath(page, ProjectProperties.FILE_EXTENTION_META);
		File file = new File(filePath);
		return file;
	}


}
