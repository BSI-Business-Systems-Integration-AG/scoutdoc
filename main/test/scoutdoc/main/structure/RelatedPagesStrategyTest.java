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

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import scoutdoc.main.TU;

public class RelatedPagesStrategyTest {

  @Test
  public void testFindRelatedPagesNone() {
    Collection<Page> pages;
    TU.initProperties();

    pages = RelatedPagesStrategy.findRelatedPages(TU.PAGE_1, RelatedPagesStrategy.NO_RELATED_PAGES);
    Assert.assertEquals("size", 0, pages.size());

    pages = RelatedPagesStrategy.findRelatedPages(TU.RED_1, RelatedPagesStrategy.NO_RELATED_PAGES);
    Assert.assertEquals("size", 0, pages.size());

    pages = RelatedPagesStrategy.findRelatedPages(TU.RED_SELF, RelatedPagesStrategy.NO_RELATED_PAGES);
    Assert.assertEquals("size", 0, pages.size());

    pages = RelatedPagesStrategy.findRelatedPages(TU.RED_CIRC_1, RelatedPagesStrategy.NO_RELATED_PAGES);
    Assert.assertEquals("size", 0, pages.size());
  }

  @Test
  public void testFindRelatedPagesImgagesAndTemplate() {
    Collection<Page> pages;
    TU.initProperties();

    pages = RelatedPagesStrategy.findRelatedPages(TU.PAGE_1, RelatedPagesStrategy.IMAGES_AND_TEMPLATES);
    Assert.assertEquals("size", 6, pages.size());
    TU.assertPageIsContained(TU.IMG_1, pages);
    TU.assertPageIsContained(TU.IMG_2, pages);
    TU.assertPageIsContained(TU.IMG_3, pages);
    TU.assertPageIsContained(TU.TMP_1, pages);
    TU.assertPageIsContained(TU.TMP_2, pages);
    TU.assertPageIsContained(TU.TMP_3, pages);

    pages = RelatedPagesStrategy.findRelatedPages(TU.RED_1, RelatedPagesStrategy.IMAGES_AND_TEMPLATES);
    Assert.assertEquals("size", 0, pages.size());

    pages = RelatedPagesStrategy.findRelatedPages(TU.RED_SELF, RelatedPagesStrategy.IMAGES_AND_TEMPLATES);
    Assert.assertEquals("size", 0, pages.size());

    pages = RelatedPagesStrategy.findRelatedPages(TU.RED_CIRC_1, RelatedPagesStrategy.IMAGES_AND_TEMPLATES);
    Assert.assertEquals("size", 0, pages.size());
  }

  @Test
  public void testFindRelatedPagesImgagesTemplateAndImage() {
    Collection<Page> pages;
    TU.initProperties();

    pages = RelatedPagesStrategy.findRelatedPages(TU.PAGE_1, RelatedPagesStrategy.IMAGES_TEMPLATES_AND_LINKS);
    Assert.assertEquals("size", 10, pages.size());
    TU.assertPageIsContained(TU.IMG_1, pages);
    TU.assertPageIsContained(TU.IMG_2, pages);
    TU.assertPageIsContained(TU.IMG_3, pages);
    TU.assertPageIsContained(TU.PAGE_2, pages);
    TU.assertPageIsContained(TU.PAGE_3, pages);
    TU.assertPageIsContained(TU.RED_1, pages);
    TU.assertPageIsContained(TU.RED_2, pages);
    TU.assertPageIsContained(TU.TMP_1, pages);
    TU.assertPageIsContained(TU.TMP_2, pages);
    TU.assertPageIsContained(TU.TMP_3, pages);

    pages = RelatedPagesStrategy.findRelatedPages(TU.RED_1, RelatedPagesStrategy.IMAGES_TEMPLATES_AND_LINKS);
    Assert.assertEquals("size", 6, pages.size());
    TU.assertPageIsContained(TU.IMG_1, pages);
    TU.assertPageIsContained(TU.IMG_2, pages);
    TU.assertPageIsContained(TU.IMG_3, pages);
    TU.assertPageIsContained(TU.RED_2, pages);
    TU.assertPageIsContained(TU.PAGE_2, pages);
    TU.assertPageIsContained(TU.TMP_3, pages);

    pages = RelatedPagesStrategy.findRelatedPages(TU.RED_SELF, RelatedPagesStrategy.IMAGES_TEMPLATES_AND_LINKS);
    Assert.assertEquals("size", 0, pages.size());

    pages = RelatedPagesStrategy.findRelatedPages(TU.RED_CIRC_1, RelatedPagesStrategy.IMAGES_TEMPLATES_AND_LINKS);
    Assert.assertEquals("size", 1, pages.size());
    TU.assertPageIsContained(TU.RED_CIRC_2, pages);

  }

  @Test
  public void testFindRelatedPagesAll() {
    Collection<Page> pages;
    TU.initProperties();

    pages = RelatedPagesStrategy.findRelatedPages(TU.PAGE_1, RelatedPagesStrategy.ALL);
    Assert.assertEquals("size", 12, pages.size());
    TU.assertPageIsContained(TU.CAT_1, pages);
    TU.assertPageIsContained(TU.CAT_2, pages);
    TU.assertPageIsContained(TU.IMG_1, pages);
    TU.assertPageIsContained(TU.IMG_2, pages);
    TU.assertPageIsContained(TU.IMG_3, pages);
    TU.assertPageIsContained(TU.RED_1, pages);
    TU.assertPageIsContained(TU.RED_2, pages);
    TU.assertPageIsContained(TU.PAGE_2, pages);
    TU.assertPageIsContained(TU.PAGE_3, pages);
    TU.assertPageIsContained(TU.TMP_1, pages);
    TU.assertPageIsContained(TU.TMP_2, pages);
    TU.assertPageIsContained(TU.TMP_3, pages);

    pages = RelatedPagesStrategy.findRelatedPages(TU.RED_1, RelatedPagesStrategy.ALL);
    Assert.assertEquals("size", 7, pages.size());
    TU.assertPageIsContained(TU.CAT_1, pages);
    TU.assertPageIsContained(TU.IMG_1, pages);
    TU.assertPageIsContained(TU.IMG_2, pages);
    TU.assertPageIsContained(TU.IMG_3, pages);
    TU.assertPageIsContained(TU.PAGE_2, pages);
    TU.assertPageIsContained(TU.RED_2, pages);
    TU.assertPageIsContained(TU.TMP_3, pages);

    pages = RelatedPagesStrategy.findRelatedPages(TU.RED_SELF, RelatedPagesStrategy.ALL);
    Assert.assertEquals("size", 0, pages.size());

    pages = RelatedPagesStrategy.findRelatedPages(TU.RED_CIRC_1, RelatedPagesStrategy.ALL);
    Assert.assertEquals("size", 1, pages.size());
    TU.assertPageIsContained(TU.RED_CIRC_2, pages);
  }
}
