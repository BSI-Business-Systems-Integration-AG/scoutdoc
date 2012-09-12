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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import scoutdoc.main.ProjectProperties;
import scoutdoc.main.mediawiki.ApiFileUtility;
import scoutdoc.main.mediawiki.ContentFileUtility;

public enum RelatedPagesStrategy {
  NO_RELATED_PAGES {
    @Override
    public Collection<Page> findNextRelatedPages(Page page, boolean mainPage) {
      return Collections.emptyList();
    }
  },
  IMAGES_AND_TEMPLATES {
    @Override
    public Collection<Page> findNextRelatedPages(Page page, boolean mainPage) {
      File apiFile = new File(PageUtility.toFilePath(page, ProjectProperties.FILE_EXTENTION_META));
      return ApiFileUtility.parseContent(apiFile, ApiFileUtility.IMAGES_AND_TEMPLATES);
    }
  },
  IMAGES_TEMPLATES_AND_LINKS {
    @Override
    public Collection<Page> findNextRelatedPages(Page page, boolean mainPage) {
      File apiFile = new File(PageUtility.toFilePath(page, ProjectProperties.FILE_EXTENTION_META));
      if (mainPage || ContentFileUtility.checkRedirection(page) != null) {
        return ApiFileUtility.parseContent(apiFile, ApiFileUtility.IMAGES_TEMPLATES_AND_LINKS);
      }
      else {
        return ApiFileUtility.parseContent(apiFile, ApiFileUtility.IMAGES_AND_TEMPLATES);
      }
    }
  },
  ALL {
    @Override
    public Collection<Page> findNextRelatedPages(Page page, boolean mainPage) {
      File apiFile = new File(PageUtility.toFilePath(page, ProjectProperties.FILE_EXTENTION_META));
      return ApiFileUtility.parseContent(apiFile, ApiFileUtility.ALL);
    }
  };

  public abstract Collection<Page> findNextRelatedPages(Page page, boolean mainPage);

  public static Collection<Page> findRelatedPages(Page page, RelatedPagesStrategy strategy) {
    HashSet<Page> unknownPages = new HashSet<Page>();
    HashSet<Page> pages = new HashSet<Page>();
    unknownPages.add(page);
    while (unknownPages.size() > 0) {
      HashSet<Page> relatedPages = new HashSet<Page>();
      for (Page p : unknownPages) {
        relatedPages.addAll(strategy.findNextRelatedPages(p, p.equals(page)));
      }
      unknownPages = new HashSet<Page>();
      for (Page p : relatedPages) {
        if (!pages.contains(p) && !p.equals(page)) {
          unknownPages.add(p);
          pages.add(p);
        }
      }
    }
    return Collections.unmodifiableSet(pages);
  }
}
