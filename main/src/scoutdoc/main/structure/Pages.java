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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import scoutdoc.main.ProjectProperties;

import com.google.common.io.Files;

/**
 * 
 */
public class Pages {
  private static HashMap<String, Page> pageMap = new HashMap<String, Page>();

  public static void initPageList() {
    pageMap.clear();
    Map<File, Page> pages;
    try {
      pages = PageUtility.loadApiFileAndPages(ProjectProperties.getFolderWikiSource());
      for (Entry<File, Page> set : pages.entrySet()) {
        Page page = set.getValue();
        File apiFile = set.getKey();

        normalizePageFiles(page, apiFile);
        add(page);
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void normalizePageFiles(Page page, File apiFile) {
    try {
      File correctApiFile = PageUtility.toApiFile(page);
      boolean moved = testAndMove(apiFile, correctApiFile);
      if (moved) {
        File contentFile = new File(apiFile.getPath().replaceAll(ProjectProperties.FILE_EXTENTION_META + "$", ProjectProperties.FILE_EXTENTION_CONTENT));
        if (contentFile.exists()) {
          File correctContentFile = PageUtility.toContentFile(page);
          testAndMove(contentFile, correctContentFile);
        }

        if (PageUtility.isImage(page)) {
          File imageFile = new File(apiFile.getPath().replaceAll("\\." + ProjectProperties.FILE_EXTENTION_META + "$", ""));
          if (imageFile.exists()) {
            File correctImageFile = PageUtility.toImageFile(page);
            testAndMove(imageFile, correctImageFile);
          }
        }

      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static boolean testAndMove(File file, File correctFile) throws IOException {
    if (!file.equals(correctFile)) {
      Files.move(file, correctFile);
      return true;
    }
    return false;
  }

  /**
   * @param pageNamee
   * @return
   */
  public static Page get(String pageName) {
    String pageNamee = PageUtility.convertToInternalName(pageName);
    if (pageMap.containsKey(pageNamee)) {
      return pageMap.get(pageNamee);
    }
    else {
      return PageUtility.toPageTitle(pageNamee);
    }
  }

  public static void add(Page page) {
    pageMap.put(PageUtility.toFullPageNamee(page), page);
  }

  /**
   * @param page
   * @return
   */
  public static Page normalize(Page page) {
    String pageNamee = PageUtility.toFullPageNamee(page);
    if (pageMap.containsKey(pageNamee)) {
      return pageMap.get(pageNamee);
    }
    throw new UnsupportedOperationException("Page is not available: " + page.toString());
  }

}
