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

package scoutdoc.main.structure;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class TaskUtility {
	
	private static final String PROP_OUTPUT_FOLDER = "output.folder";
	private static final String PROP_OUTPUT_TITLE = "output.title";
	private static final String PROP_OUTPUT_TOC_FILE = "output.toc.file";
	private static final String PROP_OUTPUT_CHECK_FILE = "output.check.file";
	private static final String PAGES_PREFIX = "input.pages.";

	public static Task toTask(String filename) throws FileNotFoundException, IOException {
		Task t = new Task();
		Map<String, Page> pages = Maps.newTreeMap(); //Creates a mutable, empty TreeMap instance using the natural ordering of its elements.

		Properties properties = new Properties();
	    properties.load(new FileInputStream(filename));
	   
	    for (Entry<Object, Object> e : properties.entrySet()) {
			String key = (String) e.getKey();
			String value = (String) e.getValue();
			if(PROP_OUTPUT_FOLDER.equals(key)) {
				t.setOutputFolder(value);
			} else if(PROP_OUTPUT_TITLE.equals(key)) {
				t.setOutputTitle(value);
			} else if(PROP_OUTPUT_TOC_FILE.equals(key)) {
				t.setOutputTocFile(value);
			} else if(PROP_OUTPUT_CHECK_FILE.equals(key)) {
				t.setOutputCheckstyleFile(value);
			} else if(key.startsWith(PAGES_PREFIX)) {
				pages.put(key, PageUtility.toPage(value));
			} else {
				throw new IllegalStateException("Unknwon property <"+key+">");
			}
		}

	    t.setInputPages(Lists.newArrayList(pages.values()));
		return t;
	}

	public static void toFile(Task task, String filename) throws FileNotFoundException, IOException {
		Properties properties = new Properties();
		addToProperties(properties, PROP_OUTPUT_FOLDER, task.getOutputFolder());
		addToProperties(properties, PROP_OUTPUT_TITLE, task.getOutputTitle());
		addToProperties(properties, PROP_OUTPUT_TOC_FILE, task.getOutputTocFile());
		addToProperties(properties, PROP_OUTPUT_CHECK_FILE, task.getOutputCheckstyleFile());
		
		int i = 1;
		for (Page p : task.getInputPages()) {
			addToProperties(properties, PAGES_PREFIX+"page"+String.format("%02d", i), PageUtility.toFullPageNamee(p));
			i++;
		}
		properties.store(new FileOutputStream(filename), null);
	}

	private static void addToProperties(Properties properties, String key, String value) {
		if(value != null) {
			properties.put(key, value);
		}
	}
	
	
}
