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

package scoutdoc.main.converter;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineParser;
import org.eclipse.mylyn.wikitext.core.parser.util.MarkupToEclipseToc;

import scoutdoc.main.ProjectProperties;
import scoutdoc.main.mediawiki.ApiFileUtility;
import scoutdoc.main.structure.Page;
import scoutdoc.main.structure.PageUtility;
import scoutdoc.main.structure.Task;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;

public class ScoutDocConverter {
  private static final Charset CHARSET = Charset.forName("UTF-8");

  public void execute(Task t) throws IOException {
    Preconditions.checkArgument(t.getInputPages().size() > 0, "InputPages needs to contain at least one page");

    MediaWikiLanguageExt markupLanguage = new MediaWikiLanguageExt();

    markupLanguage.getTemplateProviders().add(new IgnoreTemplateProvider("ScoutPage", "note")); //Could also use: markupLanguage.setTemplateExcludes("ScoutPage")
    markupLanguage.getTemplateProviders().add(new FilesTemplateProvider(new File(ProjectProperties.getFolderWikiSource() + ProjectProperties.getFileSeparator() + "Template")));
    markupLanguage.setInternalLinkPattern(ProjectProperties.getWikiServerInternalLinkPattern());

    MarkupParser markupParser = new MarkupParser();
    markupParser.setMarkupLanguage(markupLanguage);

    List<ConversionItem> items = new ArrayList<ConversionItem>();
    ConversionItem firstItem = new ConversionItem();
    firstItem.inputPage = t.getInputPages().get(0);
    firstItem.outputFileName = "index.html";
    firstItem.outputTitle = t.getOutputTitle();
    firstItem.includeToc = t.getInputPages().size() > 1;
    firstItem.firstLevel = true;
    items.add(firstItem);

    for (int i = 1; i < t.getInputPages().size(); i++) {
      Page p = t.getInputPages().get(i);
      ConversionItem item = new ConversionItem();
      item.inputPage = p;
      item.outputFileName = String.format("%02d", i) + "_" + PageUtility.toBasePageNamee(p).toLowerCase() + ".html";
      item.outputTitle = PageUtility.toBasePageName(p);
      item.includeToc = true;
      item.firstLevel = false;
      items.add(item);
    }

    markupLanguage.setPageMapping(new PageMapper(items));

    OutlineParser outlineParser = new OutlineParser();
    outlineParser.setMarkupLanguage(markupLanguage);

    OutlineItem rootItem = outlineParser.createRootItem();
    OutlineItem firstLevel = rootItem;

    for (int j = 0; j < items.size(); j++) {
      ConversionItem item = items.get(j);

      System.out.println("============");
      System.out.println("Page: " + item.inputPage.getName());
      System.out.println("outputFileName: " + item.outputFileName);

      item.inputContent = Files.toString(new File(PageUtility.toFilePath(item.inputPage)), CHARSET);

      item.outputFile = new File(ProjectProperties.getFolderWikiDist() + ProjectProperties.getFileSeparator() + t.getOutputFolder() + ProjectProperties.getFileSeparator() + item.outputFileName);
      Files.createParentDirs(item.outputFile);
      System.out.println("outputFile: " + item.outputFile);

      StringWriter out = new StringWriter();
      HtmlDocumentBuilderExt htmlDocumentBuilder = new HtmlDocumentBuilderExt(out);
      htmlDocumentBuilder.setPrependImagePrefix(ProjectProperties.getRelPathNavImagesDist());
      htmlDocumentBuilder.setDefaultAbsoluteLinkTarget("doc_external");
      htmlDocumentBuilder.setItems(items);
      htmlDocumentBuilder.setCurrentItemIndex(j);

      markupParser.setBuilder(htmlDocumentBuilder);

      markupLanguage.setPageName(item.inputPage.getName());

      markupParser.parse(item.inputContent);

      String htmlContent = out.toString();
      Files.write(htmlContent, item.outputFile, CHARSET);

      if (item.includeToc) {
        item.outlineItem = computeOutline(outlineParser, item);
        if (item.firstLevel) {
          rootItem.getChildren().add(item.outlineItem);
          firstLevel = item.outlineItem;
        }
        else {
          firstLevel.getChildren().add(item.outlineItem);
        }
      }
      else {
        item.outlineItem = new OutlineItem(rootItem, 1, "id", 1, 1, item.outputTitle);
        item.outlineItem.setResourcePath(item.outputFile.getAbsolutePath());
        if (item.firstLevel) {
          firstLevel = item.outlineItem;
        }
      }

      File apiFile = PageUtility.toApiFile(item.inputPage);
      Collection<Page> images = ApiFileUtility.parseImages(apiFile);

      File toFolder = computeImagesFolder(t);
      for (Page image : images) {
        File from = PageUtility.toFile(image);
        Files.copy(from, new File(toFolder, PageUtility.convertToInternalName(image.getName())));
      }
    }

    MarkupToEclipseToc eclipseToc = new MarkupToEclipseToc() {
      @Override
      protected String computeFile(OutlineItem item) {
        if (item != null && item.getResourcePath() != null) {
          return tail(item.getResourcePath(), ProjectProperties.getFileSeparator());
        }
        return super.computeFile(item);
      }
    };
    eclipseToc.setBookTitle(t.getOutputTitle());
    eclipseToc.setHelpPrefix(CharMatcher.anyOf(String.valueOf(ProjectProperties.getFileSeparator())).replaceFrom(t.getOutputFolder(), "/"));
    eclipseToc.setHtmlFile("index.html");
    String tocContent = eclipseToc.createToc(rootItem);

    File tocOutputFile = new File(ProjectProperties.getFolderWikiDist() + ProjectProperties.getFileSeparator() + t.getOutputTocFile());
    Files.write(tocContent, tocOutputFile, CHARSET);

    //If more than one page, copy navigation images.
    if (t.getInputPages().size() > 1) {
      File fromFolder = new File(ProjectProperties.getFolderNavImagesSource());
      File toFolder = computeImagesFolder(t);
      Files.copy(new File(fromFolder, ProjectProperties.IMAGE_HOME), new File(toFolder, ProjectProperties.IMAGE_HOME));
      Files.copy(new File(fromFolder, ProjectProperties.IMAGE_NEXT), new File(toFolder, ProjectProperties.IMAGE_NEXT));
      Files.copy(new File(fromFolder, ProjectProperties.IMAGE_PREV), new File(toFolder, ProjectProperties.IMAGE_PREV));
    }
  }

  private File computeImagesFolder(Task t) throws IOException {
    String filePath = ProjectProperties.getFolderWikiDist() + ProjectProperties.getFileSeparator() + t.getOutputFolder() + ProjectProperties.getFileSeparator() + ProjectProperties.getRelPathNavImagesDist();
    File file = new File(filePath);
    Files.createParentDirs(file);
    file.mkdir();
    return file;
  }

  private static OutlineItem computeOutline(OutlineParser outlineParser, ConversionItem conversionItem) {
    OutlineItem outlineItem = outlineParser.parse(conversionItem.inputContent);
    outlineItem.setLabel(conversionItem.outputTitle);
    outlineItem.setResourcePath(conversionItem.outputFile.getAbsolutePath());
    return outlineItem;
  }

  private static String tail(String input, String character) {
    Preconditions.checkArgument(character.length() == 1);
    char charAt = character.charAt(0);
    String output = input;
    int index = CharMatcher.is(charAt).lastIndexIn(input);
    if (index > 0) {
      output = input.substring(index + 1);
    }
    return output;
  }

//  private static String head(String input, char c) {
//    String output = input;
//    int index = CharMatcher.is(c).indexIn(input);
//    if (index > 0) {
//      output = input.substring(0, index);
//    }
//    return output;
//  }
}
