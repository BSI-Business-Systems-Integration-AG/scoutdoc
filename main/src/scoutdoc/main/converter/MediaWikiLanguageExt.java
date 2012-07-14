/*******************************************************************************
 * Copyright (c) 2007, 2012 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     BSI Business Systems Integration AG - Ext version
 *******************************************************************************/

package scoutdoc.main.converter;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;

/**
 * Extension of the WikiText MediaWikiLanguage to add some features.
 */
public class MediaWikiLanguageExt extends MediaWikiLanguage {
	
	private String pageName;

	@Override
	public void processContent(MarkupParser parser, String markupContent, boolean asDocument) {
		if (isEnableMacros()) {
			markupContent = preprocessContent(markupContent, pageName);
		}
		super.processContent(parser, markupContent, asDocument);
	}

	/**
	 * preprocess content, which involves template substitution.
	 */
	private String preprocessContent(String markupContent, String markupPageName) {
		return new TemplateProcessorExt(this).processTemplates(markupContent, markupPageName);
	}

	public String getPageName() {
		return pageName;
	}
	
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
}
