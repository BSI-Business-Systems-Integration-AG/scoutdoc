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

import java.util.List;

public class Task {
	private String outputFolder;
	private String outputTocFile;
	private String outputTitle;
	private String outputCheckFile;
	private List<Page> inputPages;
	
	public String getOutputFolder() {
		return outputFolder;
	}
	public void setOutputFolder(String outputFolder) {
		this.outputFolder = outputFolder;
	}
	public String getOutputTocFile() {
		return outputTocFile;
	}
	public void setOutputTocFile(String outputTocFile) {
		this.outputTocFile = outputTocFile;
	}
	public String getOutputTitle() {
		return outputTitle;
	}
	public void setOutputTitle(String outputTitle) {
		this.outputTitle = outputTitle;
	}
	public List<Page> getInputPages() {
		return inputPages;
	}
	public void setInputPages(List<Page> otherInputFiles) {
		this.inputPages = otherInputFiles;
	}
	public String getOutputCheckFile() {
		return outputCheckFile;
	}
	public void setOutputCheckFile(String outputCheckFile) {
		this.outputCheckFile = outputCheckFile;
	}
}
