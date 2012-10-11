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

package scoutdoc.main.converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.mylyn.internal.wikitext.mediawiki.core.PageMapping;

import scoutdoc.main.mediawiki.ContentFileUtility;
import scoutdoc.main.structure.Page;
import scoutdoc.main.structure.PageUtility;
import scoutdoc.main.structure.Pages;

public class PageMapper implements PageMapping {
  private Map<String, String> pageToHrefMap;

  public PageMapper(List<ConversionItem> items) {
    pageToHrefMap = new HashMap<String, String>();

    for (ConversionItem item : items) {
      pageToHrefMap.put(PageUtility.toFullPageNamee(item.inputPage), item.outputFileName);
    }
  }

  @Override
  public String mapPageNameToHref(String pageName) {
    String pageNamee = PageUtility.convertToInternalName(pageName);
    if (!pageToHrefMap.containsKey(pageNamee)) {
      //New pageName, check if it is a redirection:
      Page page = Pages.get(pageNamee);
      while (page != null) {
        String newPageNamee = PageUtility.toFullPageNamee(page);
        if (pageToHrefMap.containsKey(newPageNamee)) {
          String href = pageToHrefMap.get(newPageNamee);
          pageToHrefMap.put(pageNamee, href);
          return href;
        }
        page = ContentFileUtility.checkRedirection(page);
      }
      //Is not a redirection or the page is not local
      return null;
    }
    //pageName already exists => return Href.
    return pageToHrefMap.get(pageNamee);
  }

}
