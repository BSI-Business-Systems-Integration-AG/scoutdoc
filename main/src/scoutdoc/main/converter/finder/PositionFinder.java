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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import scoutdoc.main.converter.finder.SubstringFinder.Range;

public class PositionFinder {
  private String target;

  private SubstringFinder excludeSubstringFinder;

  public static PositionFinder define(String target, String excludeOpen, String excludeClose) {
    PositionFinder fd = new PositionFinder();
    fd.target = target;
    fd.excludeSubstringFinder = SubstringFinder.define(excludeOpen, excludeClose);
    return fd;
  }

  public Collection<Integer> indexesOf(String text) {
    return indexesOf(text, 0);
  }

  public Collection<Integer> indexesOf(String text, int startAt) {
    List<Integer> results = new ArrayList<Integer>();
    addResult(results, text, startAt);
    return Collections.unmodifiableCollection(results);
  }

  private void addResult(List<Integer> results, String text, int startAt) {
    Range exclude = excludeSubstringFinder.nextRange(text, startAt);
    if (exclude.getContentStart() > startAt) {
      addResult(results, text, startAt, exclude.getContentStart());
      addResult(results, text, exclude.getRangeEnd());
    }
    else {
      addResult(results, text, startAt, text.length());
    }
  }

  private void addResult(List<Integer> results, String text, int startAt, int stopBefore) {
    int index = text.indexOf(target, startAt);
    while (index > -1 && index < stopBefore) {
      results.add(Integer.valueOf(index));
      index = text.indexOf(target, index + target.length());
    }
  }
}
