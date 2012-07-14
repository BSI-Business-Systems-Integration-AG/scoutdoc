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

package scoutdoc.main.converter;

import org.eclipse.mylyn.wikitext.mediawiki.core.Template;

public class PlaceHolderTemplate extends Template {
	public static PlaceHolderTemplate ScoutLink = new PlaceHolderTemplate("ScoutLink", "");
	public static PlaceHolderTemplate NAMESPACE = new PlaceHolderTemplate("NAMESPACE", "");
	public static PlaceHolderTemplate PAGENAME = new PlaceHolderTemplate("PAGENAME", "");
	public static PlaceHolderTemplate BASEPAGENAME = new PlaceHolderTemplate("BASEPAGENAME", "");
	public static PlaceHolderTemplate BASEPAGENAMEE = new PlaceHolderTemplate("BASEPAGENAMEE", "");
	public static PlaceHolderTemplate SUBPAGENAME = new PlaceHolderTemplate("SUBPAGENAME", "");
	public static PlaceHolderTemplate SUBPAGENAMEE = new PlaceHolderTemplate("SUBPAGENAMEE", "");
	public static PlaceHolderTemplate FULLPAGENAME = new PlaceHolderTemplate("FULLPAGENAME", "");
	public static PlaceHolderTemplate note = new PlaceHolderTemplate("note", "");
	public static PlaceHolderTemplate tip = new PlaceHolderTemplate("tip", "");
	public static PlaceHolderTemplate warning = new PlaceHolderTemplate("warning", "");
	
	private PlaceHolderTemplate(String name, String markup) {
		super(name, markup);
	}
}
