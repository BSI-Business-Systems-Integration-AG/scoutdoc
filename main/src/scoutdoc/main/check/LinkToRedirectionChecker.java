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

package scoutdoc.main.check;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import scoutdoc.main.ProjectProperties;
import scoutdoc.main.mediawiki.ApiFileUtility;
import scoutdoc.main.mediawiki.ContentFileUtility;
import scoutdoc.main.structure.Page;
import scoutdoc.main.structure.PageUtility;

/**
 * Check if the links of a page (that is not a redirection) redirect to an other page.
 */
public class LinkToRedirectionChecker implements IChecker {

	@Override
	public List<Check> check(Page page) {
		if(ContentFileUtility.checkRedirection(page, true) != null) {
			//This page is a redirection, the link to an other redirection will create a Multiple Redirection
			//see {@link RedictionChecker}.
			return Collections.emptyList();
		}

		List<Check> result = new ArrayList<Check>();
		
		File apiFile = new File(PageUtility.toFilePath(page, ProjectProperties.FILE_EXTENTION_META));
		Collection<Page> links = ApiFileUtility.parseLinks(apiFile);

		for (Page link : links) {
			Page redirection = ContentFileUtility.checkRedirection(link, true);
			if(redirection != null) {
				Check check = new Check();
				check.setType("Link to redirection");
				check.setFileName(PageUtility.toFilePath(page));
				check.setPage(page);
				check.setLine(1);
				check.setColumn(1);
				check.setSeverity(Severity.warning);
				check.setMessage("LINK to '"+PageUtility.toFullPageName(link)+"' redirects to '"+PageUtility.toFullPageName(redirection)+"'");
				result.add(check);
			}
		}
		return Collections.unmodifiableList(result);
	}
}
