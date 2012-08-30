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

package scoutdoc.main;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import scoutdoc.main.fetch.RssUtility;
import scoutdoc.main.fetch.ScoutDocFetch;
import scoutdoc.main.fetch.UrlUtility;
import scoutdoc.main.structure.Page;
import scoutdoc.main.structure.RelatedPagesStrategy;

/**
 * Main class to fetch all pages of an MediaWiki RSS Feed.
 */
public class MainFetchRss {
	
	/**
	 * Main function
	 * @param args
	 * - First parameter (optional): the project properties file.
	 */
	public static void main(String[] args) {
		//Load the property file
		if(args.length > 0) {
			System.out.println("properties file: " + args[0]);
			ProjectProperties.initProperties(args[0]);
		}

		Map<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("title", "Special:RecentChanges");
		parameters.put("feed", "rss");

		String rssUrl = UrlUtility.createFullUrl(ProjectProperties.getWikiIndexUrl(), parameters);

		//Find Pages:
		List<Page> pages = RssUtility.parseRss(rssUrl);
		System.out.println("found " + pages.size() + " pages with changes");

		//Download this pages, and the related items:
		ScoutDocFetch sdf = new ScoutDocFetch();
		sdf.execute(pages, RelatedPagesStrategy.IMAGES_TEMPLATES_AND_LINKS);
		
		System.out.println("end");
	}
}
