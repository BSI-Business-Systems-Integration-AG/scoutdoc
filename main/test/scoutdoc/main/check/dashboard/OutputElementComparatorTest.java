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

package scoutdoc.main.check.dashboard;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

import scoutdoc.main.TU;
import scoutdoc.main.check.Check;
import scoutdoc.main.check.Severity;
import scoutdoc.main.check.dashboard.DashboardWriter.Column;
import scoutdoc.main.structure.Page;

import com.google.common.collect.Lists;

public class OutputElementComparatorTest {
  private final OutputElement OE_1 = createRedirectionOE(TU.PAGE_1, 1);
  private final OutputElement OE_2 = createRedirectionOE(TU.PAGE_2, 2);
  private final OutputElement OE_3 = createRedirectionOE(TU.CAT_1, 3);
  private final OutputElement OE_4 = createRedirectionOE(TU.CAT_2, 4);
  private final OutputElement OE_5 = createOtherOE(TU.PAGE_1, 5);
  private final OutputElement OE_6 = createOtherOE(TU.CAT_1, 6);

  @Test
  public void test1() {
    ArrayList<OutputElement> list = Lists.newArrayList(
        OE_1,
        OE_2,
        OE_3,
        OE_4,
        OE_5,
        OE_6
        );
    Collections.sort(list, new OutputElementComparator(Column.PAGE, Column.TYPE));

    Assert.assertEquals(OE_1.getId(), list.get(0).getId());
    Assert.assertEquals(OE_5.getId(), list.get(1).getId());
    Assert.assertEquals(OE_2.getId(), list.get(2).getId());
    Assert.assertEquals(OE_3.getId(), list.get(3).getId());
    Assert.assertEquals(OE_6.getId(), list.get(4).getId());
    Assert.assertEquals(OE_4.getId(), list.get(5).getId());
  }

  @Test
  public void test2() {
    ArrayList<OutputElement> list = Lists.newArrayList(
        OE_3,
        OE_1,
        OE_5,
        OE_4,
        OE_6,
        OE_2
        );
    Collections.sort(list, new OutputElementComparator(Column.TYPE, Column.PAGE));

    Assert.assertEquals(OE_1.getId(), list.get(0).getId());
    Assert.assertEquals(OE_2.getId(), list.get(1).getId());
    Assert.assertEquals(OE_3.getId(), list.get(2).getId());
    Assert.assertEquals(OE_4.getId(), list.get(3).getId());
    Assert.assertEquals(OE_5.getId(), list.get(4).getId());
    Assert.assertEquals(OE_6.getId(), list.get(5).getId());
  }

  private static OutputElement createRedirectionOE(Page page, int id) {
    Check c = new Check();
    c.setLine(1);
    c.setColumn(1);
    c.setPage(page);
    c.setType("DOUBLE REDIRECTION");
    c.setMessage("Ri izo randa giuma finyuri, dite amadaci berojen ili zu. Supa mandi amadaci mi iro, ipe ta ceika urene anice.");
    c.setSeverity(Severity.warning);
    c.setSource("source1");
    return new OutputElement(c, id);
  }

  private static OutputElement createOtherOE(Page page, int id) {
    Check c = new Check();
    c.setLine(1);
    c.setColumn(1);
    c.setPage(page);
    c.setType("OTHER");
    c.setMessage("Du noci suhum imi. Lango umidi te ipa, hinne abuni ubo pe. Ika teka imagi gonyo on.");
    c.setSeverity(Severity.info);
    c.setSource("source2");
    return new OutputElement(c, id);
  }
}
