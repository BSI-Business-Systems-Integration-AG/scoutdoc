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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ProjectProperties {
	private static final String PROP_FOLDER_WIKI_SOURCE = "folder.wiki.source";
	private static final String PROP_FOLDER_WIKI_DIST = "folder.wiki.dist";
	private static final String PROP_WIKI_API_URL = "wiki.api.url";
	private static final String PROP_WIKI_INDEX_URL = "wiki.index.url";
	private static final String PROP_WIKI_SERVER_URL = "wiki.server.url";
	
	private static String fileSeparator = System.getProperty("file.separator");
	private static String folderWikiSource = "wiki_source";
	private static String folderWikiDist = "wiki_dist";
	private static String wikiServerUrl = "http://wiki.eclipse.org";
	private static String wikiIndexUrl = wikiServerUrl + "/index.php";
	private static String wikiApiUrl = wikiServerUrl + "/api.php";
	
	public static void initProperties(String filename) {
		Properties properties = new Properties();
	    try {
	        properties.load(new FileInputStream(filename));
	        if(properties.containsKey(PROP_FOLDER_WIKI_SOURCE)) {
	        	folderWikiSource = (String) properties.get(PROP_FOLDER_WIKI_SOURCE);	        	
	        }
	        if (properties.containsKey(PROP_WIKI_SERVER_URL)) {
	        	wikiServerUrl = (String) properties.get(PROP_WIKI_SERVER_URL);				
			}
	        if (properties.containsKey(PROP_WIKI_INDEX_URL)) {
	        	wikiIndexUrl = (String) properties.get(PROP_WIKI_INDEX_URL);
	        }
	        if (properties.containsKey(PROP_WIKI_API_URL)) {
	        	wikiApiUrl = (String) properties.get(PROP_WIKI_API_URL);
	        }
	        if (properties.containsKey(PROP_FOLDER_WIKI_DIST)) {
	        	folderWikiDist = (String) properties.get(PROP_FOLDER_WIKI_DIST);
	        }
	    } catch (IOException e) { 
	    	e.printStackTrace();
	    }
	}
	
	public static String getWikiSourceFolder() { //TODO: rename getFolderWikiSource()
		return folderWikiSource;
	}

	public static String getWikiDistFolder() {//TODO: rename getFolderWikiDist()
		return folderWikiDist;
	}
	
	public static String getFileSeparator() {
		return fileSeparator;
	}

	public static String getWikiServerUrl() {
		return wikiServerUrl;
	}
	
	public static String getWikiIndexUrl() {
		return wikiIndexUrl;
	}
	
	public static String getWikiApiUrl() {
		return wikiApiUrl;
	}
}
