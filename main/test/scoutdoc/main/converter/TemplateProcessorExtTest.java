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

import static org.junit.Assert.assertEquals;

import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;
import org.eclipse.mylyn.wikitext.mediawiki.core.Template;
import org.junit.Before;
import org.junit.Test;

public class TemplateProcessorExtTest {

  private MediaWikiLanguage markupLanguage;

  @Before
  public void setUp() throws Exception {
    markupLanguage = new MediaWikiLanguage();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testUnkownTemplate() throws Exception {
    TemplateProcessorExt templateProcessor = new TemplateProcessorExt(markupLanguage);

    String markup;
    markup = templateProcessor.processTemplates("one {{MyUnknownTemplate}} two", "MyPage");
    assertEquals("one MyPage two", markup);
  }

  @Test
  public void testScoutLink() throws Exception {
    Template template = new Template();
    template.setName("ScoutLink");
    template.setTemplateMarkup("_expanded_");
    markupLanguage.getTemplates().add(template);

    TemplateProcessorExt templateProcessor = new TemplateProcessorExt(markupLanguage);

    String markup = templateProcessor.processTemplates("one {{ScoutLink|SDK|Page|Link}} two", "MyPage");
    assertEquals("one [[Scout/SDK/Page|Link]] two", markup);
  }

  @Test
  public void testScoutLinkName() throws Exception {
    TemplateProcessorExt templateProcessor = new TemplateProcessorExt(markupLanguage);

    String markup = templateProcessor.processTemplates("one {{ScoutLink|SDK|name=Scout SDK}} two", "MyPage");
    assertEquals("one [[Scout/SDK|Scout SDK]] two", markup);
  }

  @Test
  public void testPageNameTemplate() {
    TemplateProcessorExt templateProcessor = new TemplateProcessorExt(markupLanguage);

    String markup;
    markup = templateProcessor.processTemplates("one {{BASEPAGENAME}} two", "MyPage");
    assertEquals("one MyPage two", markup);
    markup = templateProcessor.processTemplates("one {{BASEPAGENAME}} two", "Root/MyPage");
    assertEquals("one Root two", markup);
    markup = templateProcessor.processTemplates("one {{BASEPAGENAME}} two", "The/Root/MyPage");
    assertEquals("one The/Root two", markup);
  }

  @Test
  public void testIncludeTemplate() {
    Template templateFoo = new Template();
    templateFoo.setName("foo");
    templateFoo.setTemplateMarkup("'''{{{1}}}'''");
    markupLanguage.getTemplates().add(templateFoo);

    Template templateBar = new Template();
    templateBar.setName("bar");
    templateBar.setTemplateMarkup("1:{{{1}}}, 2:{{{2}}}");
    markupLanguage.getTemplates().add(templateBar);

    TemplateProcessorExt templateProcessor = new TemplateProcessorExt(markupLanguage);

    String markup;
    markup = templateProcessor.processTemplates("xx {{bar|Scout Tutorial|This page.}} xx", "MyPage");
    assertEquals("xx 1:Scout Tutorial, 2:This page. xx", markup);

    markup = templateProcessor.processTemplates("xx {{bar|foo [[page|link]] foo|foo.}} xx", "MyPage");
    assertEquals("xx 1:foo [[page|link]] foo, 2:foo. xx", markup);

    markup = templateProcessor.processTemplates("xx {{bar|Scout Tutorial|This page [[{{BASEPAGENAME}}_Step-by-Step|Minicrm Step-by-Step Tutorial]].}} xx", "MyPage");
    assertEquals("xx 1:Scout Tutorial, 2:This page [[MyPage_Step-by-Step|Minicrm Step-by-Step Tutorial]]. xx", markup);

    markup = templateProcessor.processTemplates("xx {{bar|foo {{foo|fff}} foo|bar {{foo|bbb}} bar}} xx", "MyPage");
    assertEquals("xx 1:foo '''fff''' foo, 2:bar '''bbb''' bar xx", markup);
  }

  @Test
  public void testTrim() {
    Template templateFoo = new Template();
    templateFoo.setName("foo");
    templateFoo.setTemplateMarkup("");
    markupLanguage.getTemplates().add(templateFoo);

    Template templateBar = new Template();
    templateBar.setName("bar");
    templateBar.setTemplateMarkup("--bar--");
    markupLanguage.getTemplates().add(templateBar);

    TemplateProcessorExt templateProcessor = new TemplateProcessorExt(markupLanguage);

    String markup;
    markup = templateProcessor.processTemplates("xx {{foo}} xx", "MyPage");
    assertEquals("xx xx", markup);

    markup = templateProcessor.processTemplates("xx {{foo}} {{bar}} xx", "MyPage");
    assertEquals("xx --bar-- xx", markup);

    markup = templateProcessor.processTemplates(" {{bar}} xx", "MyPage");
    assertEquals(" --bar-- xx", markup);

    markup = templateProcessor.processTemplates("{{foo}} {{bar}} xx", "MyPage");
    assertEquals("--bar-- xx", markup);
  }
}
