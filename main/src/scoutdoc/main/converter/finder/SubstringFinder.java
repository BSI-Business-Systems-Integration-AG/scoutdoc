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

	public static final Range EMPTY_RANGE = new Range(-1, -1, -1, -1);

	private SubstringFinder() {
	}

	public static SubstringFinder define(String targetOpen, String targetClose) {
		SubstringFinder rf = new SubstringFinder();
		rf.targetOpen = targetOpen;
		rf.targetClose = targetClose;
		return rf;
	}

	public Range nextRange(String text, int startAt) {
		int rangeStartIndex = text.indexOf(targetOpen, startAt);
		if (rangeStartIndex > -1) {
			int contentStartIndex = rangeStartIndex + targetOpen.length();
			return computeCloseIndexAndCreateRange(text, rangeStartIndex, contentStartIndex, contentStartIndex);
		}
		return EMPTY_RANGE;
	}

	private Range computeCloseIndexAndCreateRange(String text, int rangeStartIndex, int contentStartIndex, int startAt) {
		Range nextRange = nextRange(text, startAt);
		
		int closeIndex = text.indexOf(targetClose, startAt);
		if (nextRange != EMPTY_RANGE) {
			if (closeIndex < nextRange.contentStart) {
				Range r = new Range(rangeStartIndex, contentStartIndex, closeIndex, closeIndex + targetClose.length());
				return r;
			} else {
				return computeCloseIndexAndCreateRange(text, rangeStartIndex, contentStartIndex, nextRange.rangeEnd);
			}
		} else if (closeIndex > 0) {
			Range r = new Range(rangeStartIndex, contentStartIndex, closeIndex, closeIndex + targetClose.length());
			return r;
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
		private final int rangeStart;
		private final int contentStart;
		private final int contentEnd;
		private final int rangeEnd;
		
		public Range(int rangeStart, int contentStart, int contentEnd, int rangeEnd) {
			super();
			this.rangeStart = rangeStart;
			this.contentStart = contentStart;
			this.contentEnd = contentEnd;
			this.rangeEnd = rangeEnd;
		}
		
		public int getRangeStart() {
			return rangeStart;
		}

		public int getContentStart() {
			return contentStart;
		}

		public int getContentEnd() {
			return contentEnd;
		}
		
		public int getRangeEnd() {
			return rangeEnd;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + contentEnd;
			result = prime * result + contentStart;
			result = prime * result + rangeEnd;
			result = prime * result + rangeStart;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Range other = (Range) obj;
			if (contentEnd != other.contentEnd)
				return false;
			if (contentStart != other.contentStart)
				return false;
			if (rangeEnd != other.rangeEnd)
				return false;
			if (rangeStart != other.rangeStart)
				return false;
			return true;
		}

	}
}
