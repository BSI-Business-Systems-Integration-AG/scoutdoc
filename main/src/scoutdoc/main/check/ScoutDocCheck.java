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
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import scoutdoc.main.ProjectProperties;
import scoutdoc.main.mediawiki.ApiFileUtility;
import scoutdoc.main.mediawiki.ContentFileUtility;
import scoutdoc.main.structure.Page;
import scoutdoc.main.structure.PageUtility;
import scoutdoc.main.structure.Task;

/**
 * Main Class for the check tool.
 */
public class ScoutDocCheck {

	public void execute(Task t) {
		HashSet<Page> pages = new HashSet<Page>();
		pages.addAll(pages);
		for (Page page : t.getInputPages()) {
			Collection<Page> links = ApiFileUtility.parseLinks(new File(PageUtility.toFilePath(page, ProjectProperties.FILE_EXTENTION_META)));
			pages.addAll(links);
			for (Page p : links) {
				Page redirection = ContentFileUtility.checkRedirection(p);
				while(redirection != null) {
					pages.add(redirection);
					redirection = ContentFileUtility.checkRedirection(redirection);
				}
			}
		}
		execute(pages);
	}

	public void execute(Collection<Page> pages) {
		List<Check> list = new ArrayList<Check>();
		
		for (Page page : pages) {
			System.out.println(page.toString());
			list.addAll(RedirectionChecker.check(page));
		}

		PrintWriter pw = null;
		try {
			pw = new PrintWriter("Checkstyle.xml");
			Writer.write(list, pw);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(pw !=null) {
				pw.close();				
			}
		}
	}

}
