package scoutdoc.main.mediawiki;

import java.io.File;
import java.io.IOException;

import scoutdoc.main.converter.finder.SubstringFinder;
import scoutdoc.main.converter.finder.SubstringFinder.Range;
import scoutdoc.main.structure.Page;
import scoutdoc.main.structure.PageUtility;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class ContentFileUtility {
	
	public static final String REDIRECT_MARKER = "#REDIRECT";

	public static Page checkRedirection(File contentFile) throws IOException {
		if(contentFile.exists()) {
			String contentText = Files.toString(contentFile, Charsets.UTF_8);			
			return checkRedirection(contentText);
		}
		return null;
	}

	public static Page checkRedirection(Page page) {
		return checkRedirection(page, false);
	}
	
	public static Page checkRedirection(Page page, boolean checkSelfRedirection) {
		try {
			Page result = checkRedirection(PageUtility.toFile(page));
			if(!checkSelfRedirection && page.equals(result)) {
				return null; //Redirection on the same page is like no redirection at all.
			} else {
				return result;				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Page checkRedirection(String contentText) {
		if(contentText.startsWith(REDIRECT_MARKER)) {
			SubstringFinder finder = SubstringFinder.define("[[", "]]");
			Range range = finder.nextRange(contentText, REDIRECT_MARKER.length());
			if(!SubstringFinder.EMPTY_RANGE.equals(range)) {
				String pageName = contentText.substring(range.getContentStart(), range.getContentEnd());
				if(pageName.startsWith(":")) {
					pageName = pageName.substring(1);
				}
				return PageUtility.toPage(pageName);
			}
		}
		return null;
	}
}
