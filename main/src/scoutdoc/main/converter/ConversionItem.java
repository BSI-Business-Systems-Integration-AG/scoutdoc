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

import java.io.File;

import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem;

import scoutdoc.main.structure.Page;


public class ConversionItem {
	public OutlineItem outlineItem;
	public String outputTitle;
	public Page inputPage;
	public String inputContent;
	public String outputFileName;
	public File outputFile;
	public boolean includeToc;
	public boolean firstLevel;
}