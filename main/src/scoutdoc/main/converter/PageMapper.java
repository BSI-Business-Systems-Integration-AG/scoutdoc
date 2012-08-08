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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.mylyn.internal.wikitext.mediawiki.core.PageMapping;

import scoutdoc.main.structure.PageUtility;


public class PageMapper implements PageMapping {
	private Map<String, String> pageToHrefMap;

	public PageMapper(List<ConversionItem> items) {
		pageToHrefMap = new HashMap<String, String>();
		
		for (ConversionItem item : items) {
			pageToHrefMap.put(PageUtility.toFullPageNamee(item.inputPage), item.outputFileName);
		}
	}

	@Override
	public String mapPageNameToHref(String pageName) {
		//TODO: Handle redirections 
		return pageToHrefMap.get(PageUtility.convertToInternalName(pageName));
	}

}
