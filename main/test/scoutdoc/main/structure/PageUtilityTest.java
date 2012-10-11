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

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Test;

import scoutdoc.main.ProjectProperties;
import scoutdoc.main.TU;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class PageUtilityTest {

  @Test
  public void testToFullPageNamee() {
    Assert.assertEquals("MyPage", PageUtility.toFullPageNamee(TU.createPage(PageType.Article, "MyPage", 100)));
    Assert.assertEquals("Root/MyPage", PageUtility.toFullPageNamee(TU.createPage(PageType.Article, "Root/MyPage", 101)));
    Assert.assertEquals("MyPage_Foo", PageUtility.toFullPageNamee(TU.createPage(PageType.Article, "MyPage_Foo", 102)));
    Assert.assertEquals("Root/MyPage_Foo", PageUtility.toFullPageNamee(TU.createPage(PageType.Article, "Root/MyPage_Foo", 103)));
    Assert.assertEquals("Template:MyPage", PageUtility.toFullPageNamee(TU.createPage(PageType.Template, "MyPage", 104)));
    Assert.assertEquals("Template:Root/MyPage", PageUtility.toFullPageNamee(TU.createPage(PageType.Template, "Root/MyPage", 105)));
    Assert.assertEquals("Template:MyPage_Foo", PageUtility.toFullPageNamee(TU.createPage(PageType.Template, "MyPage_Foo", 106)));
    Assert.assertEquals("Template:Root/MyPage_Foo", PageUtility.toFullPageNamee(TU.createPage(PageType.Template, "Root/MyPage_Foo", 107)));
    Assert.assertEquals("Template:Root/MyPage_Foo/", PageUtility.toFullPageNamee(TU.createPage(PageType.Template, "Root/MyPage_Foo/", 108)));
    Assert.assertEquals("Root/MyPage_Foo/", PageUtility.toFullPageNamee(TU.createPage(PageType.Article, "Root/MyPage_Foo/", 109)));
    Assert.assertEquals("Template:Root/MyPage_Foo/", PageUtility.toFullPageNamee(TU.createPage(PageType.Template, "Root/MyPage_Foo/", 110)));
  }

  @Test
  public void testToBasePageNamee() {
    Assert.assertEquals("MyPage", PageUtility.toBasePageNamee(TU.createPage(PageType.Article, "MyPage", 100)));
    Assert.assertEquals("MyPage", PageUtility.toBasePageNamee(TU.createPage(PageType.Article, "Root/MyPage", 101)));
    Assert.assertEquals("MyPage_Foo", PageUtility.toBasePageNamee(TU.createPage(PageType.Article, "MyPage_Foo", 102)));
    Assert.assertEquals("MyPage_Foo", PageUtility.toBasePageNamee(TU.createPage(PageType.Article, "Root/MyPage_Foo", 103)));
    Assert.assertEquals("MyPage", PageUtility.toBasePageNamee(TU.createPage(PageType.Template, "MyPage", 104)));
    Assert.assertEquals("MyPage", PageUtility.toBasePageNamee(TU.createPage(PageType.Template, "Root/MyPage", 105)));
    Assert.assertEquals("MyPage_Foo", PageUtility.toBasePageNamee(TU.createPage(PageType.Template, "MyPage_Foo", 106)));
    Assert.assertEquals("MyPage_Foo", PageUtility.toBasePageNamee(TU.createPage(PageType.Template, "Root/MyPage_Foo", 107)));
  }

  @Test
  public void testToBasePageName() {
    Assert.assertEquals("MyPage", PageUtility.toBasePageName(TU.createPage(PageType.Article, "MyPage", 100)));
    Assert.assertEquals("MyPage", PageUtility.toBasePageName(TU.createPage(PageType.Article, "Root/MyPage", 101)));
    Assert.assertEquals("MyPage Foo", PageUtility.toBasePageName(TU.createPage(PageType.Article, "MyPage_Foo", 102)));
    Assert.assertEquals("MyPage Foo", PageUtility.toBasePageName(TU.createPage(PageType.Article, "Root/MyPage_Foo", 103)));
    Assert.assertEquals("MyPage", PageUtility.toBasePageName(TU.createPage(PageType.Template, "MyPage", 104)));
    Assert.assertEquals("MyPage", PageUtility.toBasePageName(TU.createPage(PageType.Template, "Root/MyPage", 105)));
    Assert.assertEquals("MyPage Foo", PageUtility.toBasePageName(TU.createPage(PageType.Template, "MyPage_Foo", 106)));
    Assert.assertEquals("MyPage Foo", PageUtility.toBasePageName(TU.createPage(PageType.Template, "Root/MyPage_Foo", 107)));
  }

  @Test
  public void testToFilePath() {
    String fs = ProjectProperties.getFileSeparator();
    runToFilePath("Article" + fs + "Root%2FMyPage-100", TU.createPage(PageType.Article, "Root/MyPage", 100));
    runToFilePath("Article" + fs + "My%2FPage%2F-102", TU.createPage(PageType.Article, "My/Page%2F", 102));
    runToFilePath("Article" + fs + "My%2FPage%2F-103", TU.createPage(PageType.Article, "My/Page/", 103));
    runToFilePath("Image" + fs + "Img1.png-101", TU.createPage(PageType.Image, "Img1.png", 101));
  }

  private void runToFilePath(String expectedPageName, Page page) {
    String root = ProjectProperties.getFolderWikiSource() + ProjectProperties.getFileSeparator() + expectedPageName;
    Assert.assertEquals(root + ".mediawiki", PageUtility.toFilePath(page));
    Assert.assertEquals(root + ".txt", PageUtility.toFilePath(page, "txt"));
  }

  @Test
  public void testToPageString() {
    TU.assertPageEquals(PageType.Template, "Root/MyPage_Foo", PageUtility.toPage("Template:Root/MyPage_Foo", 11));
    TU.assertPageEquals(PageType.File, "This/MyPage_Foo", PageUtility.toPage("File:This/MyPage_Foo", 12));
    TU.assertPageEquals(PageType.Article, "My/Page", PageUtility.toPage("My/Page", 13));
    TU.assertPageEquals(PageType.Article, "My/Page/", PageUtility.toPage("My/Page/", 14));
    TU.assertPageEquals(PageType.Talk, "My/Page", PageUtility.toPage("Talk:My/Page", 15));
    TU.assertPageEquals(PageType.Template_talk, "ThisPage", PageUtility.toPage("Template talk:ThisPage", 16));

    TU.assertPageEquals(PageType.Article, "The:Movie", PageUtility.toPage("The:Movie", 100));

    TU.assertPageEquals(PageType.Image, "DerbyDB.zip", PageUtility.toPage("Media:DerbyDB.zip", 24251));
  }

  @Test
  public void testToFile() {
    runToFile("Root%2FMyPage-100.mediawiki", TU.createPage(PageType.Article, "Root/MyPage", 100));
    runToFile("The%3AMovie-101.mediawiki", TU.createPage(PageType.Article, "The:Movie", 101));
    runToFile("My%2FPage%2F-102.mediawiki", TU.createPage(PageType.Article, "My/Page/", 102));
    runToFile("My%2FPage%2F-102.mediawiki", PageUtility.toPage("My/Page/", 102));

    runToFile("TheTemplate-110.mediawiki", TU.createPage(PageType.Template, "TheTemplate", 110));
    runToFile("TheTemplate-110.mediawiki", PageUtility.toPage("Template:TheTemplate", 110));
    runToFile("The%3ATemplate-111.mediawiki", TU.createPage(PageType.Template, "The:Template", 111));
    runToFile("The%3ATemplate-111.mediawiki", PageUtility.toPage("Template:The:Template", 111));
    runToFile("The%2FTemplate-112.mediawiki", TU.createPage(PageType.Template, "The/Template", 112));
    runToFile("The%2FTemplate-112.mediawiki", PageUtility.toPage("Template:The/Template", 112));

    runToFile("Img1-120.png", TU.createPage(PageType.Image, "Img1.png", 120));
    runToFile("Img1-121.png", PageUtility.toPage("Image:Img1.png", 121));
    runToFile("Img1-122.bmp", TU.createPage(PageType.Image, "Img1.bmp", 122));
    runToFile("Img1-123.bmp", PageUtility.toPage("Image:Img1.bmp", 123));
    runToFile("Img1-124.jpeg", TU.createPage(PageType.Image, "Img1.jpeg", 124));
    runToFile("Img1-125.jpeg", PageUtility.toPage("Image:Img1.jpeg", 125));

    runToFile("Img1-130.png", TU.createPage(PageType.File, "Img1.png", 130));
    runToFile("Img1-131.png", PageUtility.toPage("File:Img1.png", 131));
    runToFile("Img1-132.bmp", TU.createPage(PageType.File, "Img1.bmp", 132));
    runToFile("Img1-133.bmp", PageUtility.toPage("File:Img1.bmp", 133));
    runToFile("Img1-134.jpeg", TU.createPage(PageType.File, "Img1.jpeg", 134));
    runToFile("Img1-135.jpeg", PageUtility.toPage("File:Img1.jpeg", 135));

  }

  private void runToFile(String expectedFileName, Page page) {
    String root = ProjectProperties.getFolderWikiSource() + ProjectProperties.getFileSeparator() + page.getType() + ProjectProperties.getFileSeparator();
    File expected = new File(root + expectedFileName);
    File actual = PageUtility.toFile(page);
    Assert.assertEquals("AbsolutePath", expected.getAbsolutePath(), actual.getAbsolutePath());
  }

  @Test
  public void testToPageFile() throws Exception {
    TU.init();
    runToPageFile(TU.PAGE_1);
    runToPageFile(TU.CAT_1);
    runToPageFile(TU.IMG_1);
    runToPageFile(TU.TMP_1);
    runToPageFile(TU.createPage(PageType.Article, "Root/MyPage", 100));
    runToPageFile(TU.createPage(PageType.File, "My/File%2F", 101));
  }

  private void runToPageFile(Page page) {
    String contentFilePath = PageUtility.toFilePath(page, ProjectProperties.FILE_EXTENTION_CONTENT);
    File apiFile = PageUtility.toApiFile(page);
    if (apiFile.exists()) {
      Assert.assertEquals(page, PageUtility.toPage(apiFile));
      File contentFile = new File(contentFilePath);
      if (contentFile.exists()) {
        Assert.assertEquals(page, PageUtility.toPage(contentFile));
      }
    }
    else {
      Assert.assertNull(PageUtility.toPage(apiFile));
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testLoadPagesIllegalBecauseNotAFolder() throws Exception {
    TU.init();
    PageUtility.loadPages(File.createTempFile("Test", "mediawiki"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testLoadPagesIllegalBecauseNotInSource() throws Exception {
    TU.init();
    PageUtility.loadPages(Files.createTempDir());
  }

  @Test
  public void testLoadPages() throws Exception {
    TU.init();
    Collection<Page> pages = PageUtility.loadPages(new File(ProjectProperties.getFolderWikiSource()));
    Assert.assertEquals("size", 19, pages.size());
    TU.assertPageIsContained(TU.CAT_1, pages);
    TU.assertPageIsContained(TU.CAT_2, pages);
    TU.assertPageIsContained(TU.CAT_ROOT, pages);
    TU.assertPageIsContained(TU.IMG_1, pages);
    TU.assertPageIsContained(TU.IMG_2, pages);
    TU.assertPageIsContained(TU.IMG_3, pages);
    TU.assertPageIsContained(TU.PAGE_1, pages);
    TU.assertPageIsContained(TU.PAGE_2, pages);
    TU.assertPageIsContained(TU.PAGE_3, pages);
    TU.assertPageIsContained(TU.RED_1, pages);
    TU.assertPageIsContained(TU.RED_2, pages);
    TU.assertPageIsContained(TU.RED_3, pages);
    TU.assertPageIsContained(TU.RED_CIRC_1, pages);
    TU.assertPageIsContained(TU.RED_CIRC_2, pages);
    TU.assertPageIsContained(TU.RED_CIRC_3, pages);
    TU.assertPageIsContained(TU.RED_SELF, pages);
    TU.assertPageIsContained(TU.TMP_1, pages);
    TU.assertPageIsContained(TU.TMP_2, pages);
    TU.assertPageIsContained(TU.TMP_3, pages);

    Collection<Page> pagesCat = PageUtility.loadPages(new File(ProjectProperties.getFolderWikiSource(), "Category"));
    Assert.assertEquals("size", 3, pagesCat.size());
    TU.assertPageIsContained(TU.CAT_1, pagesCat);
    TU.assertPageIsContained(TU.CAT_2, pagesCat);
    TU.assertPageIsContained(TU.CAT_ROOT, pagesCat);
  }

  @Test
  public void testLoadApiFileAndPages() throws Exception {
    TU.init();
    Map<File, Page> pages = PageUtility.loadApiFileAndPages(ProjectProperties.getFolderWikiSource());
    for (Entry<File, Page> e : pages.entrySet()) {
      Page page = e.getValue();
      Assert.assertNotNull(page.getId());

      File apiFile = e.getKey();
      File correctApiFile = PageUtility.toApiFile(page);
      Assert.assertEquals(correctApiFile, apiFile);

      File contentFile = PageUtility.toContentFileFromApiFile(apiFile);
      File correctContentFile = PageUtility.toContentFile(page);
      Assert.assertEquals(correctContentFile, contentFile);
    }
  }

  @Test
  public void testExists() throws Exception {
    TU.init();

    Assert.assertEquals(true, PageUtility.exists(TU.CAT_1));
    Assert.assertEquals(true, PageUtility.exists(TU.CAT_2));
    Assert.assertEquals(true, PageUtility.exists(TU.CAT_ROOT));
    Assert.assertEquals(true, PageUtility.exists(TU.IMG_1));
    Assert.assertEquals(true, PageUtility.exists(TU.IMG_2));
    Assert.assertEquals(true, PageUtility.exists(TU.IMG_3));
    Assert.assertEquals(true, PageUtility.exists(TU.PAGE_1));
    Assert.assertEquals(true, PageUtility.exists(TU.PAGE_2));
    Assert.assertEquals(true, PageUtility.exists(TU.PAGE_3));
    Assert.assertEquals(true, PageUtility.exists(TU.RED_1));
    Assert.assertEquals(true, PageUtility.exists(TU.RED_2));
    Assert.assertEquals(true, PageUtility.exists(TU.RED_3));
    Assert.assertEquals(true, PageUtility.exists(TU.RED_CIRC_1));
    Assert.assertEquals(true, PageUtility.exists(TU.RED_CIRC_2));
    Assert.assertEquals(true, PageUtility.exists(TU.RED_CIRC_3));
    Assert.assertEquals(true, PageUtility.exists(TU.RED_SELF));
    Assert.assertEquals(true, PageUtility.exists(TU.TMP_1));
    Assert.assertEquals(true, PageUtility.exists(TU.TMP_2));
    Assert.assertEquals(true, PageUtility.exists(TU.TMP_3));

    Assert.assertEquals(false, PageUtility.exists(TU.createPage(PageType.Article, "My/Page", 100)));
    Assert.assertEquals(false, PageUtility.exists(TU.createPage(PageType.Article, "Test Cat1", 101)));

    Assert.assertEquals(false, PageUtility.exists(TU.createIncompletePage(PageType.Article, "Test Cat1")));
  }

  @Test
  public void testWriteList() throws Exception {
    File f = File.createTempFile("list", "txt");
    Collection<Page> pages = Arrays.asList(
        TU.TMP_2,
        TU.PAGE_3,
        TU.PAGE_1,
        TU.PAGE_2,
        TU.CAT_1,
        TU.TMP_3
        );
    PageUtility.writeList(pages, f.getAbsolutePath());

    List<String> actual = Files.readLines(f, Charsets.UTF_8);
    Assert.assertEquals("lines count", 6, actual.size());
    Assert.assertEquals("line 1", "Test_Page1", actual.get(0));
    Assert.assertEquals("line 2", "Test_Page2", actual.get(1));
    Assert.assertEquals("line 3", "Test_Page3", actual.get(2));
    Assert.assertEquals("line 4", "Category:Test_Cat1", actual.get(3));
    Assert.assertEquals("line 5", "Template:Test_Dolore", actual.get(4));
    Assert.assertEquals("line 6", "Template:Test_Lorem", actual.get(5));
  }

  @Test
  public void testReadList() throws Exception {
    File f = File.createTempFile("list", "txt");
    Collection<Page> pages = Arrays.asList(
        TU.TMP_2,
        TU.TMP_1,
        TU.CAT_2,
        TU.PAGE_1,
        TU.PAGE_2,
        TU.CAT_1,
        TU.TMP_3
        );
    String listFilePath = f.getAbsolutePath();
    PageUtility.writeList(pages, listFilePath);
    List<Page> actual = PageUtility.readList(listFilePath);
    Assert.assertEquals("pages list count", 7, actual.size());
    TU.assertPageEquals(TU.PAGE_1, actual.get(0));
    TU.assertPageEquals(TU.PAGE_2, actual.get(1));
    TU.assertPageEquals(TU.CAT_1, actual.get(2));
    TU.assertPageEquals(TU.CAT_2, actual.get(3));
    TU.assertPageEquals(TU.TMP_3, actual.get(4));
    TU.assertPageEquals(TU.TMP_1, actual.get(5));
    TU.assertPageEquals(TU.TMP_2, actual.get(6));
  }
}
