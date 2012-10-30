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

package scoutdoc.main.check;

import java.util.Collections;
import java.util.List;

import scoutdoc.main.mediawiki.ApiFileContentType;
import scoutdoc.main.mediawiki.ApiFileUtility;
import scoutdoc.main.structure.Page;

/**
 * Base class to check if a page is using on other resource (based on api file).
 */
public abstract class AbstractUseChecker implements IChecker {

  protected final ApiFileContentType type;
  protected final Page targetPage;
  private boolean followContent;

  public AbstractUseChecker(ApiFileContentType type, Page targetPage, boolean recursive) {
    this.type = type;
    this.targetPage = targetPage;
    this.followContent = recursive;
  }

  @Override
  public List<Check> check(Page page) {
    if (ApiFileUtility.containsTarget(page, targetPage, type, followContent)) {
      return Collections.emptyList();
    }
    else {
      Check check = new Check();
      check.setType(createType());
      check.setPage(page);
      check.setLine(1);
      check.setColumn(1);
      check.setSeverity(createSeverity());
      check.setMessage(createMessage(page));
      return Collections.singletonList(check);
    }
  }

  protected abstract String createType();

  protected abstract Severity createSeverity();

  protected abstract String createMessage(Page page);
}
