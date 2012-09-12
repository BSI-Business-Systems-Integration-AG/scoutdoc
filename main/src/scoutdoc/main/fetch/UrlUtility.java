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

package scoutdoc.main.fetch;

import java.util.Map;

import com.google.common.base.Joiner;

public class UrlUtility {

  public static String createFullUrl(String url, Map<String, String> parameters) {
    String fullUrl = Joiner.on("?").join(url, Joiner.on("&").withKeyValueSeparator("=").join(parameters));
    fullUrl = fullUrl.replaceAll(" ", "%20");
    return fullUrl;
  }

}
