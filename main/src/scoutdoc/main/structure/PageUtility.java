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

import scoutdoc.main.ProjectProperties;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

public class PageUtility {
	private static final String TRAILING_SLASH_REPLACEMENT = "%2F";

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
		return convertToInternalName(namespace + page.getName());
	}
	
	public static String /*pageNamee*/ toPageNamee(Page page) {
		Preconditions.checkNotNull(page.getName(), "Page#Name can not be null");
		return convertToInternalName(page.getName());
	}

	public static String /*basePageNamee*/  toBasePageNamee(Page page) {
		Preconditions.checkNotNull(page.getName(), "Page#Name can not be null");
		
		String name = page.getName();
		int index = CharMatcher.is('/').lastIndexIn(name);
		if(index > 0) {
			name = name.substring(index+1);
		}
		return convertToInternalName(name);
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

		return toFilePath(page.getType(), page.getName(), fileExtension);
	}
	
	public static String toFilePath(Page page) {
		return toFilePath(page, ProjectProperties.FILE_EXTENTION_CONTENT);
	}
	
	private static String toFilePath(PageType type, String fileName, String fileExtension) {
		String filePath = ProjectProperties.getFolderWikiSource() + ProjectProperties.getFileSeparator() + type.name() + ProjectProperties.getFileSeparator() + fileName;
		filePath = Joiner.on('.').skipNulls().join(filePath, fileExtension);
		return convertToInternalName(filePath);
	}
	
	public static File toFile(Page page) {
		if(isFile(page)) {
			return new File(toFilePath(page.getType(), toPageNamee(page), null));
		} else {
			return new File(toFilePath(page));
		}
	}
	
	public static Page /*page*/ toPage(String fullPageName) {
		String fullPageNamee = convertToInternalName(fullPageName);
		
		PageType type;
		String name;
		int index = CharMatcher.is(':').indexIn(fullPageNamee);
		if(index > 0) {
			name = fullPageNamee.substring(index+1);
			String typeRaw = fullPageNamee.substring(0, index);
			try {
				type = PageType.valueOf(typeRaw);	
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				type = PageType.Article;
			}
		} else {
			type = PageType.Article;
			name = fullPageNamee;
		}
		if(name.endsWith("/")) {
			name = name.substring(0, name.length() - 1) + TRAILING_SLASH_REPLACEMENT;
		}
		Page page = new Page();
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
	
	public static boolean isFile(Page page) {
		return page.getType() == PageType.Image || page.getType() == PageType.File;
	}
}
