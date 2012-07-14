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

import scoutdoc.main.ProjectProperties;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

public class PageUtility {
	/**
	 * Convert a {@link Page} to the FULLPAGENAME (which is {{NAMESPACE}}:{{PAGENAME}}).
	 * Can be used a title parameter in a query to MediaWiki.
	 * @param page
	 * @return fullPageName
	 */
	public static String /*fullPageNamee*/ toFullPageNamee(Page page) {
		Preconditions.checkNotNull(page.getType(), "Page#Type can not be null");
		Preconditions.checkNotNull(page.getName(), "Page#Name can not be null");

		String namespace;
		if(page.getType() == PageType.Article) {
			namespace = "";
		} else {
			namespace = page.getType()+":";
		}
		return namespace + page.getName();
	}
	
	public static String /*basePageNamee*/  toBasePageNamee(Page page) {
		Preconditions.checkNotNull(page.getName(), "Page#Name can not be null");
		
		String name = page.getName();
		int index = CharMatcher.is('/').lastIndexIn(name);
		if(index > 0) {
			name = name.substring(index+1);
		}
		return name;
	}
	
	public static String /*basePageName*/ toBasePageName(Page page) {
		return convertToDisplayName(toBasePageNamee(page));
	}
	
	/**
	 * Convert a {@link Page} to the file.
	 * @param page
	 * @param fileExtension without a point
	 * @return path of the file
	 */
	public static String /*file path*/ toFilePath(Page page, String fileExtension) {
		Preconditions.checkNotNull(page.getType(), "Page#Type can not be null");
		Preconditions.checkNotNull(page.getName(), "Page#Name can not be null");

		String filePath =  ProjectProperties.getWikiSourceFolder() + ProjectProperties.getFileSeparator() + page.getType().name() + ProjectProperties.getFileSeparator() + page.getName();
		return Joiner.on('.').join(filePath, fileExtension);
	}
	
	public static String toFilePath(Page page) {
		return toFilePath(page, "mediawiki");
	}
	
	public static Page /*page*/ toPage(String fullPageName) {
		Page page = new Page();
		
		PageType type = PageType.Article;
		String name = fullPageName;
		int index = CharMatcher.is(':').indexIn(fullPageName);
		if(index > 0) {
			name = fullPageName.substring(index+1);
			String typeRaw = fullPageName.substring(0, index);
			try {
				type = PageType.valueOf(typeRaw);	
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		page.setName(name);	
		page.setType(type);
		return page;
	}

	public static String convertToInternalName(String pageName) {
		return CharMatcher.anyOf(" ").replaceFrom(pageName, "_");
	}
	
	public static String convertToDisplayName(String pageId) {
		return CharMatcher.anyOf("_").replaceFrom(pageId, " ");
	}
}
