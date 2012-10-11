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

public class Page implements Comparable<Page> {
  private String name;
  private PageType type;
  private Integer id;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public PageType getType() {
    return type;
  }

  public void setType(PageType type) {
    this.type = type;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Page other = (Page) obj;
    if (id == null) {
      if (other.id != null) return false;
    }
    else if (!id.equals(other.id)) return false;
    if (name == null) {
      if (other.name != null) return false;
    }
    else if (!name.equals(other.name)) return false;
    if (type != other.type) return false;
    return true;
  }

  @Override
  public String toString() {
    return "Page [type=" + type + ", name=" + name + ", id=" + id + "]";
  }

  @Override
  public int compareTo(Page other) {
    int i = this.type.compareTo(other.type);
    if (i == 0) {
      return this.name.compareTo(other.name);
    }
    else {
      return i;
    }
  }
}
