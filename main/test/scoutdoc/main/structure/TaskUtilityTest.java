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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;

import scoutdoc.main.TU;

public class TaskUtilityTest {

  @Test
  public void test() throws Exception {
    TU.init();

    File f = File.createTempFile("test", "properties");

    Task t = new Task();
    t.setOutputFolder("html/folder/test");
    t.setOutputTocFile("test_toc.xml");
    t.setOutputTitle("Test Title");
    t.setOutputCheckstyleFile("My-checkstyle-file.xml");
    t.setInputPages(Arrays.<Page> asList(
        TU.PAGE_1,
        TU.PAGE_2,
        TU.PAGE_3
        ));

    System.out.println(f.getAbsolutePath());

    TaskUtility.toFile(t, f.getAbsolutePath());
    Task a = TaskUtility.toTask(f.getAbsolutePath());

    assertTaskEquals(t, a);
  }

  private static void assertTaskEquals(Task expected, Task actual) {
    assertEquals("OutputFolder", expected.getOutputFolder(), actual.getOutputFolder());
    assertEquals("OutputTitle", expected.getOutputTitle(), actual.getOutputTitle());
    assertEquals("OutputTocFile", expected.getOutputTocFile(), actual.getOutputTocFile());
    assertEquals("OutputCheckFile", expected.getOutputCheckstyleFile(), actual.getOutputCheckstyleFile());

    assertEquals("InputPages().size()", expected.getInputPages().size(), expected.getInputPages().size());

    for (int i = 0; i < expected.getInputPages().size(); i++) {
      Page expectedPage = expected.getInputPages().get(i);
      Page acutualPage = actual.getInputPages().get(i);
      assertEquals("Page " + (i + 1), expectedPage, acutualPage);
    }
  }

}
