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
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import scoutdoc.main.ProjectProperties;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;

public class PageUtility {
	private static final String SLASH = "/";
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
		String pageName = namespace + page.getName();
		pageName = decodeTrailingSlash(pageName);
		return convertToInternalName(pageName);
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
	
	public static String /*fullPageName*/ toFullPageName(Page page) {
		return convertToDisplayName(toFullPageNamee(page));
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
		filePath = codeTrailingSlash(filePath);
		filePath = Joiner.on('.').skipNulls().join(filePath, fileExtension);
		return convertToInternalName(filePath);
	}
	
	public static File toFile(Page page) {
		if(isImage(page)) {
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
		name = codeTrailingSlash(name);
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
	
	public static boolean isImage(Page page) {
		return page.getType() == PageType.Image || page.getType() == PageType.File || page.getType() == PageType.Media;
	}
	
	public static List<Page> loadPages(String sourceFolder) {
		File f = new File(sourceFolder);
		return loadPages(f);
	}

	public static List<Page> loadPages(File folder) {
		Preconditions.checkArgument(folder.isDirectory(), "should be a directory: "+folder.getPath());
		Preconditions.checkArgument(isInSourceFolder(folder), "the directory should be included in ProjectProperties.getFolderWikiSource().");

		List<Page> result = new ArrayList<Page>();
		
		File[] childContentFiles = folder.listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.getName().endsWith("."+ProjectProperties.FILE_EXTENTION_CONTENT);
			}
		});
		for (File contentFile : childContentFiles) {
			//To page:
			Page page = toPage(contentFile); //TODO: devrait plut™t tre getAbsolute - get Absolute par raport au base folder (PP)
			
			//To api File
			File apiFile = new File(toFilePath(page, ProjectProperties.FILE_EXTENTION_META));
			if(apiFile.exists()) {
				result.add(page);
			}
		}
		
		File[] childFolder = folder.listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.isDirectory();
			}
		});
		
		for (File f : childFolder) {
			result.addAll(loadPages(f));
		}
			
		return Collections.unmodifiableList(result);
	}
	
	public static Page toPage(File file) {
			try {
				String path = file.getCanonicalPath();
				boolean isContentFile = path.endsWith("."+ProjectProperties.FILE_EXTENTION_CONTENT);
				boolean isApiFile = path.endsWith("."+ProjectProperties.FILE_EXTENTION_META);
				if(!isContentFile && !isApiFile) {
					return null;
				}
				
				if(isContentFile) {
					path = path.substring(0, path.length() - ProjectProperties.FILE_EXTENTION_CONTENT.length() - 1);
				}				
				if(isApiFile) {
					path = path.substring(0, path.length() - ProjectProperties.FILE_EXTENTION_META.length() - 1);
				}
				
				String sourcePath = new File(ProjectProperties.getFolderWikiSource()).getCanonicalPath();
				if(path.startsWith(sourcePath)) {
					path = path.substring(sourcePath.length() + ProjectProperties.getFileSeparator().length());
					int i = path.indexOf(ProjectProperties.getFileSeparator());
					if(i > 0) {
						path = path.substring(0, i) + ":" + path.substring(i + ProjectProperties.getFileSeparator().length());
						return toPage(path);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		return null;
	}

	private static boolean isInSourceFolder(File folder) {
		File wikiSource = new File(ProjectProperties.getFolderWikiSource());
		File parent = folder;
		while (parent != null) {
			if(parent.equals(wikiSource)) {
				return true;
			}
			parent = parent.getParentFile();
		}
		return false;
	}

	private static String codeTrailingSlash(String filePath) {
		if(filePath.endsWith(SLASH)) {
			filePath = filePath.substring(0, filePath.length() - SLASH.length()) + TRAILING_SLASH_REPLACEMENT;
		}
		return filePath;
	}
	
	private static String decodeTrailingSlash(String filePath) {
		if(filePath.endsWith(TRAILING_SLASH_REPLACEMENT)) {
			filePath = filePath.substring(0, filePath.length() - TRAILING_SLASH_REPLACEMENT.length()) + SLASH;
		}
		return filePath;
	}
}
