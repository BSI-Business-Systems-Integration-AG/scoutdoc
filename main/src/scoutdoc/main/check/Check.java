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

package scoutdoc.main.check;

import scoutdoc.main.structure.Page;

/**
 * POJO to represent a check (as in the XML output File)
 */
public class Check {
  private int column;
  private int line;
  private Page page;
  private String type;
  private String message;
  private Severity severity;
  private String source;

  public int getColumn() {
    return column;
  }

  public void setColumn(int column) {
    this.column = column;
  }

  public int getLine() {
    return line;
  }

  public void setLine(int line) {
    this.line = line;
  }

  public Page getPage() {
    return page;
  }

  public void setPage(Page page) {
    this.page = page;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Severity getSeverity() {
    return severity;
  }

  public void setSeverity(Severity severity) {
    this.severity = severity;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + column;
    result = prime * result + line;
    result = prime * result + ((message == null) ? 0 : message.hashCode());
    result = prime * result + ((page == null) ? 0 : page.hashCode());
    result = prime * result
        + ((severity == null) ? 0 : severity.hashCode());
    result = prime * result + ((source == null) ? 0 : source.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Check other = (Check) obj;
    if (column != other.column) return false;
    if (line != other.line) return false;
    if (message == null) {
      if (other.message != null) return false;
    }
    else if (!message.equals(other.message)) return false;
    if (page == null) {
      if (other.page != null) return false;
    }
    else if (!page.equals(other.page)) return false;
    if (severity != other.severity) return false;
    if (source == null) {
      if (other.source != null) return false;
    }
    else if (!source.equals(other.source)) return false;
    if (type == null) {
      if (other.type != null) return false;
    }
    else if (!type.equals(other.type)) return false;
    return true;
  }
}
