/*******************************************************************************
 * Copyright (c) 2010 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package scoutdoc.main.filter;

import scoutdoc.main.structure.Page;

/**
 * Default implementation for {@link IPageFilter}.
 */
public class AcceptAllPageFilter implements IPageFilter {

  @Override
  public boolean keepPage(Page page) {
    return true;
  }

}
