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

public interface IPageFilter {
  /**
   * @param page
   *          the page to check
   * @return <code>true</code> if the page needs to be keept, </code>false</code> if not.
   */
  boolean keepPage(Page page);
}
