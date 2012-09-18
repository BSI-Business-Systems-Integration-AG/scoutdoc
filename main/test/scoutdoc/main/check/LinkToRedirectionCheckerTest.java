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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import scoutdoc.main.TU;
import scoutdoc.main.structure.Page;
import scoutdoc.main.structure.PageUtility;

public class LinkToRedirectionCheckerTest {

  @Test
  public void testCheckNotMatch() {
    TU.initProperties();

    runNotMatch(TU.PAGE_2);
    runNotMatch(TU.RED_1);
    runNotMatch(TU.RED_2);
    runNotMatch(TU.RED_3);
  }

  private void runNotMatch(Page page) {
    List<Check> actual = new LinkToRedirectionChecker().check(page);
    Assert.assertEquals("size", 0, actual.size());
  }

  @Test
  public void testCheck() {
    List<Check> actual = new LinkToRedirectionChecker().check(TU.PAGE_1);
    Assert.assertEquals("size", 1, actual.size());

    Assert.assertEquals("check type", "Link to redirection", actual.get(0).getType());
    Assert.assertEquals("check file name", PageUtility.toFilePath(TU.PAGE_1), actual.get(0).getFileName());
    Assert.assertEquals("check page", TU.PAGE_1, actual.get(0).getPage());
    Assert.assertEquals("check line", 1, actual.get(0).getLine());
    Assert.assertEquals("check column", 1, actual.get(0).getColumn());
    Assert.assertEquals("check message", "LINK to 'Test Red1' redirects to 'Test Red2'", actual.get(0).getMessage());
    Assert.assertEquals("check severity", Severity.info, actual.get(0).getSeverity());
  }

}
