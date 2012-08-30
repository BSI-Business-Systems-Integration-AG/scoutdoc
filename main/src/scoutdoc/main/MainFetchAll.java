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

import java.util.Collection;

import scoutdoc.main.fetch.ScoutDocFetch;
import scoutdoc.main.structure.Page;
import scoutdoc.main.structure.PageUtility;
import scoutdoc.main.structure.RelatedPagesStrategy;

/**
 * Main class to fetch all pages of a list or of the source folder (refresh)
 */
public class MainFetchAll {
	
	/**
	 * Main function
	 * @param args
	 * - First parameter (optional): the project properties file.
	 * - Second parameter (optional): the list of pages. if not set the function will refresh the source folder 
	 */
	public static void main(String[] args) {
		//Load the property file
		if(args.length > 0) {
			System.out.println("properties file: " + args[0]);
			ProjectProperties.initProperties(args[0]);
		}
		
		//Load list:
		Collection<Page> pages;
		if(args.length > 1) {
			System.out.println("list file: " + args[1]);
			pages = PageUtility.readList(args[1]);
		} else {			
			System.out.println("Get the page list from the source folder");
			pages = PageUtility.loadPages(ProjectProperties.getFolderWikiSource());
		}

		System.out.println("found " + pages.size() + " pages to download");

		//Download this pages, and the related items:
		ScoutDocFetch sdf = new ScoutDocFetch();
		sdf.execute(pages, RelatedPagesStrategy.IMAGES_TEMPLATES_AND_LINKS);
		
		System.out.println("end");
	}
}
