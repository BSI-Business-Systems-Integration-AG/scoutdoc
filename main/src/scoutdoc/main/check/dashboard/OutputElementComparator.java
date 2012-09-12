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

import java.util.Comparator;

import scoutdoc.main.check.Check;
import scoutdoc.main.check.Severity;
import scoutdoc.main.check.dashboard.DashboardWriter.Column;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

class OutputElementComparator implements Comparator<OutputElement> {
  private final Column[] columns;

  public OutputElementComparator(Column... columns) {
    this.columns = columns;
  }

  @Override
  public int compare(OutputElement e1, OutputElement e2) {
    Check c1 = e1.getCheck();
    Check c2 = e2.getCheck();
    ComparisonChain cc = ComparisonChain.start();
    for (Column column : columns) {
      switch (column) {
        case PAGE:
          cc = cc.compare(c1.getPage(), c2.getPage());
          break;
        case NAMESPACE:
          cc = cc.compare(c1.getPage().getType().name(), c2.getPage().getType().name());
          break;
        case TYPE:
          cc = cc
              .compare(c1.getSeverity(), c2.getSeverity(), Ordering.explicit(Severity.error, Severity.warning, Severity.info))
              .compare(c1.getType(), c2.getType());
          break;
        default:
          throw new IllegalStateException("Unexpected column type");
      }
    }
    return cc.result();
  }
}
