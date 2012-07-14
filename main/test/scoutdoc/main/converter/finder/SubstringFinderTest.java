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
		assertEquals(-1, -1, finder.nextRange("xxxxxx", 0));
		assertEquals(3, 8, finder.nextRange("xxx{{zzz}}xxx", 0));
		assertEquals(-1, -1, finder.nextRange("xxx{{zzz}}xxx", 4));
		assertEquals(-1, -1, finder.nextRange("xxx{{zzz}}xxx", 6));
		assertEquals(3, 15, finder.nextRange("xxx{{zz{{ww}}zz}}xxx", 0));
		assertEquals(3, 15, finder.nextRange("xxx{{zz{{ww}}zz}}xxx", 3));
		assertEquals(3, 07, finder.nextRange("xxx{{zz}}zz}}xxx", 0));
		assertEquals(7, 11, finder.nextRange("xxx{{zz{{ww}}zz}}xxx", 4));
		
		assertEquals(-1, -1, finder.nextRange("xxx}}zzz{{xxx", 0));
	}

	private static void assertEquals(int expectedStart, int expectedEnd, Range actual) {
		Assert.assertEquals("start", expectedStart, actual.getStart());
		Assert.assertEquals("end", expectedEnd, actual.getEnd());
	}
}
