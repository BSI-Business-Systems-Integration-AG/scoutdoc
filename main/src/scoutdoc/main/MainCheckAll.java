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

import scoutdoc.main.check.ScoutDocCheck;
import scoutdoc.main.structure.Page;
import scoutdoc.main.structure.PageUtility;

/**
 * Main class to perform all checks on all pages of the {@link ProjectProperties#PROP_FOLDER_WIKI_SOURCE} directory.
 */
public class MainCheckAll {
	
	/**
	 * Main function
	 * @param args
	 * - First parameter (optional): the project properties file.
	 * - Second parameter (optional): name of the Checkstyle file.
	 */
	public static void main(String[] args) {
		//Load the property file
		if(args.length > 0) {
			System.out.println("properties file: " + args[0]);
			ProjectProperties.initProperties(args[0]);
		}
		
		//Get file name
		String fileName = null;
		if(args.length > 1) {
			System.out.println("output file name: " + args[1]);
			fileName = args[1];
		}
		
		//Call ScoutDocCheck:
		ScoutDocCheck sdf = new ScoutDocCheck();
		Collection<Page> pages = PageUtility.loadPages(ProjectProperties.getFolderWikiSource());
		System.out.println("found " + pages.size() + " pages to check");

		sdf.execute(pages , fileName);
		
		System.out.println("end");
	}
}
