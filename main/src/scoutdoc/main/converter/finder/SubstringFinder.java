/*******************************************************************************
 * Copyright (c) 2012 Jeremie Bresson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jeremie Bresson - initial API and implementation
 *******************************************************************************/

package scoutdoc.main.converter.finder;

public class SubstringFinder {
	private String targetOpen;

	private String targetClose;

	public static final Range EMPTY_RANGE = new Range(-1, -1);

	private SubstringFinder() {
	}

	public static SubstringFinder define(String targetOpen, String targetClose) {
		SubstringFinder rf = new SubstringFinder();
		rf.targetOpen = targetOpen;
		rf.targetClose = targetClose;
		return rf;
	}

	public Range nextRange(String text, int startAt) {
		int openIndex = text.indexOf(targetOpen, startAt);
		if (openIndex > -1) {
			Range nextRange = nextRange(text, openIndex + targetOpen.length());
			int closeIndex = text.indexOf(targetClose, openIndex + targetOpen.length());
			if (nextRange.start > 0) {
				if (closeIndex < nextRange.start) {
					Range r = new Range(openIndex, closeIndex);
					return r;
				} else if (nextRange.end > 0) {
					closeIndex = text.indexOf(targetClose, nextRange.end + targetClose.length());
					Range r;
					if (closeIndex > 0) {
						r = new Range(openIndex, closeIndex);
					} else {
						r = new Range(openIndex, nextRange.end);
					}
					return r;
				}
			} else if (closeIndex > 0) {
				Range r = new Range(openIndex, closeIndex);
				return r;
			}
		}
		return EMPTY_RANGE;
	}

	public int getOpenLength() {
		return targetOpen.length();
	}

	public int getCloseLength() {
		return targetClose.length();
	}

	public static class Range {
		private final int start;

		private final int end;

		public Range(int start, int end) {
			super();
			this.start = start;
			this.end = end;
		}

		public int getStart() {
			return start;
		}

		public int getEnd() {
			return end;
		}
	}
}
