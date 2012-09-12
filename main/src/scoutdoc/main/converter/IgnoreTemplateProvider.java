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

import java.util.Arrays;
import java.util.List;

import org.eclipse.mylyn.wikitext.mediawiki.core.Template;
import org.eclipse.mylyn.wikitext.mediawiki.core.TemplateResolver;

public class IgnoreTemplateProvider extends TemplateResolver {

  private List<String> namesList;

  public IgnoreTemplateProvider() {
    this(new String[]{});
  }

  public IgnoreTemplateProvider(String... names) {
    namesList = Arrays.asList(names);
  }

  @Override
  public Template resolveTemplate(String templateName) {
    if (namesList.contains(templateName)) {
      return new Template(templateName, "");
    }
    return null;
  }

}
