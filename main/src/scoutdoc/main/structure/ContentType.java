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

/**
 * Types of object available in the API File 
 *
 */
public enum ContentType {
	/**
	 * Pages referenced between the <code>categories</code> tag.
	 */
	Category,
	/**
	 * Pages referenced between the <code>images</code> tag.
	 */
	Images,
	/**
	 * Pages referenced between the <code>links</code> tag.
	 */
	Links,
	/**
	 * Pages referenced between the <code>templates</code> tag.
	 */
	Template
}
