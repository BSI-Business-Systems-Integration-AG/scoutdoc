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

import org.eclipse.mylyn.wikitext.mediawiki.core.Template;
import org.eclipse.mylyn.wikitext.mediawiki.core.TemplateResolver;

import scoutdoc.main.structure.Page;
import scoutdoc.main.structure.PageUtility;
import scoutdoc.main.structure.Pages;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Files;

public class PagesTemplateProvider extends TemplateResolver {

  public PagesTemplateProvider() {
    super();
  }

  @Override
  public Template resolveTemplate(String templateName) {
    if (Strings.isNullOrEmpty(templateName)) {
      return null;
    }
    String basicName = templateName.toLowerCase().startsWith("template:") ? templateName.substring(templateName.lastIndexOf(':') + 1) : templateName;

    String templatePageName = "Template:" + basicName.substring(0, 1).toUpperCase() + basicName.substring(1);
    Page templatePage = Pages.get(templatePageName);
    File file = PageUtility.toContentFile(templatePage);

    if (file.exists()) {
      try {
        String content = Files.toString(file, Charsets.UTF_8);

        Template template = new Template();
        template.setName(basicName);
        template.setTemplateMarkup(content);
        return template;

      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
    return null;
  }
}
