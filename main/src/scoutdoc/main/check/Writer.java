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

import java.io.PrintWriter;
import java.util.List;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * Write list of Check into a PrintWriter
 */
public class Writer {
	private static final String XML_VERSION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	private static final String CHECKSTYLE_OPEN = "<checkstyle version=\"5.5\">";
	private static final String CHECKSTYLE_CLOSE = "</checkstyle>";
	private static final String FILE_CLOSE = "</file>";
	
	public static void write(List<Check> list, PrintWriter w) {
		w.println(XML_VERSION);
		w.println(CHECKSTYLE_OPEN);
		
		Multimap<String, Check> multimap = ArrayListMultimap.create();
		
		for (Check check : list) {
			multimap.put(check.getFileName(), check);
		}
		
		for (String file : multimap.keySet()) {
			w.println("<file name=\""+file+"\">");
			for (Check check : multimap.get(file)) {
				w.print("<error"); 
				w.print(" line=\"" + check.getLine() + "\""); 
				w.print(" column=\"" + check.getColumn() + "\""); 
				w.print(" severity=\"" + Objects.firstNonNull(check.getSeverity(), "") + "\""); 
				w.print(" message=\"" + Strings.nullToEmpty(check.getMessage()) + "\""); 
				w.print(" source=\"" + Strings.nullToEmpty(check.getSource()) + "\""); 
				w.println("/>");
			}
			w.println(FILE_CLOSE);
		}
		w.println(CHECKSTYLE_CLOSE);
	}

	
	
}
