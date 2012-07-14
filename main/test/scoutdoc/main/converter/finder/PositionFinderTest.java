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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import scoutdoc.main.converter.finder.PositionFinder;

public class PositionFinderTest {

	@Test
	public void testFindIn1() {
		PositionFinder finder = PositionFinder.define("|", "{{", "}}");
		Collection<Integer> expected = Arrays.<Integer>asList(
				Integer.valueOf(5),
				Integer.valueOf(13)
				);
		assertListEquals(expected, finder.indexesOf("foooo|baaaaar|baz"));
		assertListEquals(expected, finder.indexesOf("foooo|b{{a}}r|baz"));
		assertListEquals(expected, finder.indexesOf("f{{}}|baaaaar|baz"));
		assertListEquals(expected, finder.indexesOf("f{{}}|b{{a}}r|baz"));
		assertListEquals(Collections.singletonList(Integer.valueOf(3)), finder.indexesOf("foo|b{{ar|ba}}z"));
		assertListEquals(Collections.singletonList(Integer.valueOf(3)), finder.indexesOf("foo|b{{ar|ba}}z"));
	}
	
	@Test
	public void testFindInSingle() {
		singeltonFind("|foobaz");
		singeltonFind("foobaz|");
		singeltonFind("foo|baz");
		singeltonFind("{{foo|baz");
		singeltonFind("fo{{o|baz");
		singeltonFind("}}foo|baz");
		singeltonFind("fo}}o|baz");
		singeltonFind("foo|baz}}");
		singeltonFind("foo|b}}az");
		singeltonFind("foo|baz{{");
		singeltonFind("foo|b{{az");
	}

	private void singeltonFind(String input) {
		PositionFinder finder = PositionFinder.define("|", "{{", "}}");
		Collection<Integer> positions = finder.indexesOf(input);
		List<Integer> expected = Collections.singletonList(Integer.valueOf(input.indexOf("|")));
		assertListEquals(expected, positions);
	}

	private static void assertListEquals(Collection<Integer> expected, Collection<Integer> actual) {
		Assert.assertEquals("size", expected.size(), actual.size());
		assertIteratorEquals(expected.iterator(), actual.iterator());
	}
	
	private static void assertIteratorEquals(Iterator<?> iteratorExpected, Iterator<?> iteratorActual) {
		int i = 0;
		while (iteratorExpected.hasNext()) {
			Object e = iteratorExpected.next();
			if(iteratorActual.hasNext()) {
				Object a = iteratorActual.next();
				Assert.assertEquals("element (" + i + ")", e, a);
			} else {
				Assert.fail("Expected element (" + i + ") not found:"+ e);
			}
			i++;
		}
		
		if(iteratorActual.hasNext()) {
			Object a = iteratorActual.next();
			Assert.fail("Additional element (" + i + ") found:"+ a);
		}
	}
}
