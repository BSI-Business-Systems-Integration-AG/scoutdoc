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

/**
 * test for {@link RedirectionChecker}
 */
public class RedirectionCheckerTest {

  @Test
  public void testCheckNotMatch() {
    TU.init();

    runNotMatch(TU.PAGE_1);
    runNotMatch(TU.RED_2);
    runNotMatch(TU.RED_3);
  }

  private void runNotMatch(Page page) {
    List<Check> actual = new RedirectionChecker().check(page);
    Assert.assertEquals("size", 0, actual.size());
  }

  @Test
  public void testCheck() {
    TU.init();

    Page page = TU.RED_1;

    List<Check> actual = new RedirectionChecker().check(page);
    Assert.assertEquals("size", 1, actual.size());

    Assert.assertEquals("check type", "Multiple redirection", actual.get(0).getType());
    Assert.assertEquals("check file name", PageUtility.toFilePath(page), actual.get(0).getFileName());
    Assert.assertEquals("check page", page, actual.get(0).getPage());
    Assert.assertEquals("check line", 1, actual.get(0).getLine());
    Assert.assertEquals("check column", 1, actual.get(0).getColumn());
    Assert.assertEquals("check message", "MULTIPLE REDIRECTION: 'Test Red1' => 'Test Red2' => 'Test Page2'", actual.get(0).getMessage());
    Assert.assertEquals("check severity", Severity.warning, actual.get(0).getSeverity());
  }

  @Test
  public void testCheckSelfRedirection() {
    TU.init();
    runCircularRedirection("'Test RedSelf' => 'Test RedSelf'", TU.RED_SELF);
  }

  @Test
  public void testCircularRedirection() {
    TU.init();

    runCircularRedirection("'Test RedCirc1' => 'Test RedCirc2' => 'Test RedCirc1'", TU.RED_CIRC_1);
    runCircularRedirection("'Test RedCirc2' => 'Test RedCirc1' => 'Test RedCirc2'", TU.RED_CIRC_2);
    runCircularRedirection("'Test RedCirc3' => 'Test RedCirc2' => 'Test RedCirc1' => 'Test RedCirc2'", TU.RED_CIRC_3);
  }

  private void runCircularRedirection(String expectedMessagePath, Page page) {
    List<Check> actual = new RedirectionChecker().check(page);
    Assert.assertEquals("size", 1, actual.size());

    Assert.assertEquals("check type", "Circular redirection", actual.get(0).getType());
    Assert.assertEquals("check file name", PageUtility.toFilePath(page), actual.get(0).getFileName());
    Assert.assertEquals("check page", page, actual.get(0).getPage());
    Assert.assertEquals("check line", 1, actual.get(0).getLine());
    Assert.assertEquals("check column", 1, actual.get(0).getColumn());
    Assert.assertEquals("check column", "CIRCULAR REDIRECTION: " + expectedMessagePath, actual.get(0).getMessage());
    Assert.assertEquals("check column", Severity.error, actual.get(0).getSeverity());
  }
}
