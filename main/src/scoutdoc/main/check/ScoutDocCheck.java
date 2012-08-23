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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import scoutdoc.main.structure.Page;
import scoutdoc.main.structure.RelatedPagesStrategy;
import scoutdoc.main.structure.Task;

/**
 * Main Class for the check tool.
 */
public class ScoutDocCheck {

	public void execute(Task t) {
		HashSet<Page> pages = new HashSet<Page>();
		for (Page page : t.getInputPages()) {
			pages.addAll(RelatedPagesStrategy.findRelatedPages(page, RelatedPagesStrategy.IMAGES_TEMPLATES_AND_LINKS));
		}
		execute(pages, t.getOutputCheckFile());
	}

	public void execute(Collection<Page> pages, String fileName) {
		List<Check> list = new ArrayList<Check>();
		
		List<IChecker> checkers = Arrays.asList(
				new LinkToRedirectionChecker(),
				new RedirectionChecker()
				);
		
		for (IChecker c : checkers) {
			for (Page page : pages) {
				list.addAll(c.check(page));
			}
		}

		PrintWriter pw = null;
		try {
			String f;
			if(fileName == null || fileName.isEmpty()) {
				f = "Checkstyle.xml";
			} else {
				f = fileName;
			}
			pw = new PrintWriter(f);
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
