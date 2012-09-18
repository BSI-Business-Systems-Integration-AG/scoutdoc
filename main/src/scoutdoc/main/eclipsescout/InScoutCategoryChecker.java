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

package scoutdoc.main.eclipsescout;

import scoutdoc.main.check.AbstractUseChecker;
import scoutdoc.main.check.Severity;
import scoutdoc.main.mediawiki.ApiFileContentType;
import scoutdoc.main.mediawiki.ContentFileUtility;
import scoutdoc.main.structure.Page;
import scoutdoc.main.structure.PageType;

/**
 * Check if the page is in the scout category or in one of the sub category.
 */
public class InScoutCategoryChecker extends AbstractUseChecker {

  public InScoutCategoryChecker() {
    super(ApiFileContentType.Categories, ScoutPages.MAIN_CATEGORY, true);
  }

  @Override
  public boolean shouldCheck(Page page) {
    return ScoutPages.isScoutPage(page) && page.getType() == PageType.Article && ContentFileUtility.checkRedirection(page) == null;
  }

  @Override
  protected String createType() {
    return "Page is not in any Scout categories";
  }

  @Override
  protected Severity createSeverity() {
    return Severity.info;
  }

  @Override
  protected String createMessage(Page page) {
    return "This is not in any Scout Categories. Add {{ScoutPage|cat=XXX}} to the page.";
  }
}
