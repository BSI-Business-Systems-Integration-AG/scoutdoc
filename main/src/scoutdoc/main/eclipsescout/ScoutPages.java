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

import scoutdoc.main.structure.Page;
import scoutdoc.main.structure.PageUtility;

public class ScoutPages {
  public static final Page MAIN_CATEGORY = PageUtility.toPage("Category:Scout", 24069);
  public static final Page PAGE_TEMPLATE = PageUtility.toPage("Template:ScoutPage", 26226);
  public static final Page MAIN_PAGE = PageUtility.toPage("Scout", 23968);

  public static boolean isScoutPage(Page page) {
    return page.getName().contains("Scout") || page.getName().contains("scout");
  }
}
