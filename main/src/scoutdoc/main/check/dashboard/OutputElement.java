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

package scoutdoc.main.check.dashboard;

import scoutdoc.main.check.Check;

class OutputElement {
	private Check check;
	private int id;
	private String pageOutput;
	
	public OutputElement(Check check, int id) {
		this.check = check;
		this.id = id;
	}
	
	public Check getCheck() {
		return check;
	}
	
	public void setCheck(Check check) {
		this.check = check;
	}
	
	public int getId() {
		return id;
	}

	public String getPageOutput() {
		return pageOutput;
	}
	
	public void setPageOutput(String pageOutput) {
		this.pageOutput = pageOutput;
	}
	
}
