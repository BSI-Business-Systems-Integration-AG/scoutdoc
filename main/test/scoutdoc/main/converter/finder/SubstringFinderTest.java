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

import org.junit.Assert;
import org.junit.Test;

import scoutdoc.main.converter.finder.SubstringFinder;
import scoutdoc.main.converter.finder.SubstringFinder.Range;


public class SubstringFinderTest {

	@Test
	public void testNextRange() {
		SubstringFinder finder = SubstringFinder.define("{{", "}}");
		assertEquals(-1, -1, finder, finder.nextRange("xxxxxx", 0));
		assertEquals(5, 8, finder, finder.nextRange("xxx{{zzz}}xxx", 0));
		assertEquals(-1, -1, finder, finder.nextRange("xxx{{zzz}}xxx", 4));
		assertEquals(-1, -1, finder, finder.nextRange("xxx{{zzz}}xxx", 6));
		assertEquals(5, 15, finder, finder.nextRange("xxx{{zz{{ww}}zz}}xxx", 0));
		assertEquals(5, 15, finder, finder.nextRange("xxx{{zz{{ww}}zz}}xxx", 3));
		assertEquals(5, 07, finder, finder.nextRange("xxx{{zz}}zz}}xxx", 0));
		assertEquals(9, 11, finder, finder.nextRange("xxx{{zz{{ww}}zz}}xxx", 4));
		
		assertEquals(-1, -1, finder, finder.nextRange("xxx}}zzz{{xxx", 0));
	}
	
	@Test
	public void testNextRange2() {
		SubstringFinder finder = SubstringFinder.define("[[[", ")");
		assertEquals(-1, -1, finder, finder.nextRange("xxxxxx", 0));
		assertEquals(5, 8, finder, finder.nextRange("xx[[[zzz)xxxx", 0));
		assertEquals(3, 8, finder, finder.nextRange("[[[zzzzz)xxx", 0));
		assertEquals(4, 8, finder, finder.nextRange("z[[[zzzz)xxx", 0));
		assertEquals(8, 8, finder, finder.nextRange("zzzzz[[[)", 0));
		assertEquals(8, 8, finder, finder.nextRange("zzzzz[[[)xxx", 0));
		assertEquals(-1, -1, finder, finder.nextRange("xx[[[zzz)xxxx", 4));
		assertEquals(-1, -1, finder, finder.nextRange("xx[[[zzz)xxxx", 6));
	}

	private static void assertEquals(int expectedContentStart, int expectedContentEnd, SubstringFinder finder, Range actual) {
		int expectedRangeStart = -1;
		if(expectedContentStart > -1) {
			expectedRangeStart = expectedContentStart - finder.getOpenLength();
		}
		int expectedRangeEnd = -1;
		if(expectedContentEnd > -1) {
			expectedRangeEnd = expectedContentEnd + finder.getCloseLength();
		}
		Assert.assertEquals("range start", expectedRangeStart, actual.getRangeStart());
		Assert.assertEquals("content start", expectedContentStart, actual.getContentStart());
		Assert.assertEquals("content end", expectedContentEnd, actual.getContentEnd());
		Assert.assertEquals("range end", expectedRangeEnd, actual.getRangeEnd());
	}
}
