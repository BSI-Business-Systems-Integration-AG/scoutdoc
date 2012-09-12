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

package scoutdoc.main.check.dashboard;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scoutdoc.main.ProjectProperties;
import scoutdoc.main.check.Check;
import scoutdoc.main.check.Severity;
import scoutdoc.main.fetch.UrlUtility;
import scoutdoc.main.mediawiki.ContentFileUtility;
import scoutdoc.main.structure.Page;
import scoutdoc.main.structure.PageUtility;

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.io.Files;

/**
 * Write list of Check into a PrintWriter
 */
public class DashboardWriter {
  private static final String FILE_NAME_FORMAT = "f%02d.html";
  private static final String HTML_FILE_TOP = "<html>\n<head>";
  private static final String HTML_FILE_MIDLE = "</head>\n<body>\n";
  private static final String HTML_FILE_END = "</body>\n</html>";
  private static final String TITLE_OPEN = "<title>";
  private static final String TITLE_CLOSE = "</title>";
  private static final String H1_OPEN = "<h1>";
  private static final String H1_CLOSE = "</h1>";
  private static final String TABLE_OPEN = "<table>";
  private static final String TABLE_CLOSE = "</table>";
  private static final String TR_OPEN = "<tr>";
  private static final String TR_CLOSE = "</tr>";
  private static final String TH_OPEN = "<th>";
  private static final String TH_CLOSE = "</th>";
  private static final String TD_OPEN = "<td>";
  private static final String TD_CLOSE = "</td>";
  private static final Context EMPTY = new Context();

  final private HashMap<Check, OutputElement> elementMap = new HashMap<Check, OutputElement>();

  public void write(List<Check> list, String folderName) throws IOException {
    File folder = new File(folderName);
    Files.createParentDirs(folder);
    folder.mkdir();
    for (File f : folder.listFiles()) {
      if (f.isFile() && !f.isHidden()) {
        f.delete();
      }
    }

    List<OutputElement> elements = Lists.newArrayList();
    for (Check check : list) {
      OutputElement element = createElement(check);
      elements.add(element);
      String name = String.format(FILE_NAME_FORMAT, element.getId());
      writeDetailFile(Collections.singletonList(element), folder, name, "Details > Check #" + element.getId());
    }

    writeTypeFile(elements, folder, "index.html", "Types", true);
    writeNameSpaceFile(elements, folder, "namespaces.html", "Namespaces", true);
    writePageFile(elements, folder, "pages.html", "Pages", true);
    writeTableFile(elements, folder, "table.html");
    writeDetailFile(elements, folder, "details.html", "Details");

    File fromFolder = new File(ProjectProperties.getFolderDashboardContent());
    Files.copy(new File(fromFolder, "edit.gif"), new File(folder, "edit.gif"));
    Files.copy(new File(fromFolder, "error.png"), new File(folder, "error.png"));
    Files.copy(new File(fromFolder, "history.gif"), new File(folder, "history.gif"));
    Files.copy(new File(fromFolder, "info.png"), new File(folder, "info.png"));
    Files.copy(new File(fromFolder, "style.css"), new File(folder, "style.css"));
    Files.copy(new File(fromFolder, "view.gif"), new File(folder, "view.gif"));
    Files.copy(new File(fromFolder, "warning.png"), new File(folder, "warning.png"));
  }

  public void writeTypeFile(Collection<OutputElement> list, File folder, String pageName, String pageTitle, boolean generateSubPages) throws FileNotFoundException {
    PrintWriter w = new PrintWriter(new File(folder, pageName));
    Multimap<OutputElement, OutputElement> multimap = ArrayListMultimap.create();

    for (OutputElement e : list) {
      Check group = new Check();
      group.setType(e.getCheck().getType());
      group.setSeverity(e.getCheck().getSeverity());
      multimap.put(createElement(group), e);
    }

    if (generateSubPages) {
      for (OutputElement element : multimap.keySet()) {
        String name = String.format(FILE_NAME_FORMAT, element.getId());
        writePageFile(multimap.get(element), folder, name, "Types > " + element.getCheck().getType(), false);
      }
    }

    writeFile(w, pageTitle, multimap, new OutputElementComparator(Column.TYPE), Arrays.asList(Column.TYPE, Column.TOTAL, Column.STATS));
    w.close();
  }

  public void writeNameSpaceFile(Collection<OutputElement> list, File folder, String pageName, String pageTitle, boolean generateSubPages) throws FileNotFoundException {
    PrintWriter w = new PrintWriter(new File(folder, pageName));
    Multimap<OutputElement, OutputElement> multimap = ArrayListMultimap.create();

    for (OutputElement e : list) {
      Check group = new Check();
      Page page = new Page();
      page.setType(e.getCheck().getPage().getType());
      group.setPage(page);
      multimap.put(createElement(group), e);
    }

    if (generateSubPages) {
      for (OutputElement element : multimap.keySet()) {
        String name = String.format(FILE_NAME_FORMAT, element.getId());
        writePageFile(multimap.get(element), folder, name, "Namespaces > " + element.getCheck().getPage().getType(), false);
      }
    }

    writeFile(w, pageTitle, multimap, new OutputElementComparator(Column.NAMESPACE), Arrays.asList(Column.NAMESPACE, Column.TOTAL, Column.STATS));
    w.close();
  }

  public void writePageFile(Collection<OutputElement> list, File folder, String pageName, String pageTitle, boolean generateSubPages) throws FileNotFoundException {
    PrintWriter w = new PrintWriter(new File(folder, pageName));
    Multimap<OutputElement, OutputElement> multimap = ArrayListMultimap.create();

    for (OutputElement e : list) {
      Check group = new Check();
      group.setPage(e.getCheck().getPage());
      multimap.put(createElement(group), e);
    }

    if (generateSubPages) {
      if (generateSubPages) {
        for (OutputElement element : multimap.keySet()) {
          String name = String.format(FILE_NAME_FORMAT, element.getId());
          pageTitle = "Pages > " + PageUtility.toFullPageName(element.getCheck().getPage());
          writeDetailFile(multimap.get(element), folder, name, pageTitle);
        }
      }
    }

    writeFile(w, pageTitle, multimap, new OutputElementComparator(Column.PAGE), Arrays.asList(Column.PAGE, Column.TOTAL, Column.STATS));
    w.close();
  }

  public void writeTableFile(Collection<OutputElement> list, File folder, String pageName) throws FileNotFoundException {
    PrintWriter w = new PrintWriter(new File(folder, pageName));
    Multimap<OutputElement, OutputElement> multimap = ArrayListMultimap.create();

    for (OutputElement e : list) {
      multimap.put(e, e);
    }
    writeFile(w, "Table", multimap, new OutputElementComparator(Column.PAGE, Column.TYPE), Arrays.asList(Column.PAGE, Column.TYPE, Column.DETAIL));
    w.close();
  }

  private OutputElement createElement(Check check) {
    OutputElement outputElement = elementMap.get(check);
    if (outputElement == null) {
      outputElement = new OutputElement(check, elementMap.size() + 1);
      elementMap.put(check, outputElement);

      Page page = check.getPage();
      if (page != null && page.getName() != null) {
        StringBuilder sb = new StringBuilder();
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("title", PageUtility.toFullPageNamee(page));
        sb.append("<a href=\"");
        sb.append(String.format(FILE_NAME_FORMAT, outputElement.getId()));
        sb.append("\">");
        sb.append(PageUtility.toFullPageName(page));
        sb.append("</a>");
        sb.append(" (");
        sb.append("<a class=\"view\" href=\"");
        HashMap<String, String> pView = Maps.newHashMap(parameters);
        if (ContentFileUtility.checkRedirection(page) != null) {
          pView.put("redirect", "no");
        }
        sb.append(UrlUtility.createFullUrl(ProjectProperties.getWikiIndexUrl(), pView));
        sb.append("\">view</a> - ");
        sb.append("<a class=\"history\" href=\"");
        HashMap<String, String> pHist = Maps.newHashMap(parameters);
        pHist.put("action", "history");
        sb.append(UrlUtility.createFullUrl(ProjectProperties.getWikiIndexUrl(), pHist));
        sb.append("\">history</a> - ");
        sb.append("<a class=\"edit\" href=\"");
        HashMap<String, String> pEdit = Maps.newHashMap(parameters);
        pEdit.put("action", "edit");
        sb.append(UrlUtility.createFullUrl(ProjectProperties.getWikiIndexUrl(), pEdit));
        sb.append("\">edit</a>)");
        outputElement.setPageOutput(sb.toString());
      }
      else {
        outputElement.setPageOutput("");
      }
    }
    return outputElement;
  }

  private void writeDetailFile(Collection<OutputElement> elements, File folder, String pageName, String pageTitle) throws FileNotFoundException {
    PrintWriter w = new PrintWriter(new File(folder, pageName));
    writeFileBegin(w, pageTitle);

    List<OutputElement> list = Lists.newArrayList(elements);
    Collections.sort(list, new OutputElementComparator(Column.PAGE, Column.TYPE));
    for (OutputElement element : list) {
      w.println(TABLE_OPEN);
      w.println(TR_OPEN);
      w.println(TH_OPEN);
      Column.PAGE.writeLabel(w);
      w.println(": ");
      Column.PAGE.writeCellContent(w, element, EMPTY);
      w.println(", ");
      Column.TYPE.writeLabel(w);
      w.println(": ");
      Column.TYPE.writeCellContent(w, element, EMPTY);

      w.println(TH_CLOSE);
      w.println(TR_CLOSE);
      w.println(TR_OPEN);
      w.println(TD_OPEN);
      Column.DETAIL.writeCellContent(w, element, EMPTY);
      w.println(TD_CLOSE);
      w.println(TR_CLOSE);
      w.println(TABLE_CLOSE);
    }
    writeFileEnd(w);
    w.close();
  }

  public void writeFile(PrintWriter w, String title, Multimap<OutputElement, OutputElement> content, Comparator<OutputElement> comparator, List<Column> columns) {
    List<OutputElement> groups = Lists.newArrayList(content.keySet());
    Collections.sort(groups, comparator);

    Context context = new Context();
    context.content = content;
    int max = 0;
    for (OutputElement groupKey : groups) {
      Collection<OutputElement> group = content.get(groupKey);
      int size = group.size();
      if (max < size) {
        max = size;
      }
      if (size == 1) {
        //Replace the check in the groupKey: with the check of the singleton element (constituting the group).
        OutputElement element = group.iterator().next();
        groupKey.setCheck(element.getCheck());
      }
    }
    context.distributionMax = max;

    List<Column> columnsInternal;
    if (max == 1 && !columns.contains(Column.DETAIL)) {
      columnsInternal = new ArrayList<DashboardWriter.Column>(columns);
      columnsInternal.add(Column.DETAIL);
    }
    else {
      columnsInternal = columns;
    }

    writeFileBegin(w, title);

    w.println(TABLE_OPEN);
    w.println(TR_OPEN);
    for (Column c : columnsInternal) {
      c.writeHeaderCell(w);
    }
    w.println(TR_CLOSE);

    for (OutputElement group : groups) {
      w.println(TR_OPEN);
      for (Column c : columnsInternal) {
        c.writeCell(w, group, context);
      }
      w.println(TR_CLOSE);

    }
    w.println(TABLE_CLOSE);
    writeFileEnd(w);
  }

  private void writeFileBegin(PrintWriter w, String title) {
    w.println(HTML_FILE_TOP);
    w.println("<link rel=\"stylesheet\" href=\"style.css\" type=\"text/css\">");
    w.println(TITLE_OPEN);
    w.println(title);
    w.println(TITLE_CLOSE);
    w.println(HTML_FILE_MIDLE);
    w.println(H1_OPEN);
    w.println(title);
    w.println(H1_CLOSE);

    //Tabs:
    w.println("<p>");
    w.println("[<a href=\"index.html\">Types</a>] ");
    w.println("[<a href=\"namespaces.html\">Namespaces</a>] ");
    w.println("[<a href=\"pages.html\">Pages</a>] ");
    w.println("[<a href=\"table.html\">Table</a>] ");
    w.println("[<a href=\"details.html\">Details</a>] ");
    w.println("</p>");
  }

  private void writeFileEnd(PrintWriter w) {
    w.println(HTML_FILE_END);
    w.flush();
  }

  public enum Column {
    PAGE {
      @Override
      public void writeLabel(PrintWriter w) {
        w.print("Page");
      }

      @Override
      public void writeCellContent(PrintWriter w, OutputElement element, Context context) {
        w.print(element.getPageOutput());
      }
    },
    NAMESPACE {
      @Override
      public void writeLabel(PrintWriter w) {
        w.print("Namespace");
      }

      @Override
      public void writeCellContent(PrintWriter w, OutputElement element, Context context) {
        Page page = element.getCheck().getPage();
        if (page != null) {
          w.print("<a href=\"");
          w.print(String.format(FILE_NAME_FORMAT, element.getId()));
          w.print("\">");
          w.print(page.getType().name());
          w.print("</a>");
        }
      }
    },
    TYPE {
      @Override
      public void writeLabel(PrintWriter w) {
        w.print("Type");
      }

      @Override
      public void writeCellContent(PrintWriter w, OutputElement element, Context context) {
        w.print("<a href=\"");
        w.print(String.format(FILE_NAME_FORMAT, element.getId()));
        w.print("\">");
        w.print(Strings.nullToEmpty(element.getCheck().getType()));
        w.print("</a>");
      }
    },
    TOTAL {
      @Override
      public void writeLabel(PrintWriter w) {
        w.print("Total");
      }

      @Override
      public void writeCellContent(PrintWriter w, OutputElement element, Context context) {
        w.print(context.content.get(element).size());
      }
    },
    DETAIL {
      @Override
      public void writeLabel(PrintWriter w) {
        w.print("Detail");
      }

      @Override
      public void writeCellContent(PrintWriter w,
          OutputElement element,
          Context context) {
        w.print(Strings.nullToEmpty(element.getCheck().getMessage()));
      }
    },
    STATS {

      @Override
      public void writeLabel(PrintWriter w) {
        w.print("Distribution");
      }

      @Override
      public void writeCellContent(PrintWriter w, OutputElement element, Context context) {
        Multimap<Severity, OutputElement> list = ArrayListMultimap.create();
        for (OutputElement e : context.content.get(element)) {
          list.put(e.getCheck().getSeverity(), e);
        }
        writeImage(w, list, Severity.error, context.distributionMax);
        writeImage(w, list, Severity.warning, context.distributionMax);
        writeImage(w, list, Severity.info, context.distributionMax);
      }

      private void writeImage(PrintWriter w, Multimap<Severity, OutputElement> list, Severity severity, int max) {
        String imgName = severity.name() + ".png";
        int size = list.get(severity).size();
        if (size > 0) {
          int width = (int) (300.0 * size / max);
          w.print("<img src=\"" + imgName + "\" alt =\"" + severity.name() + ": " + size + "\" height=\"7px\" width =\"" + width + "px\"/>");
        }
      }

    };

    public abstract void writeLabel(PrintWriter w);

    public abstract void writeCellContent(PrintWriter w, OutputElement element, Context context);

    public void writeHeaderCell(PrintWriter w) {
      w.println(TH_OPEN);
      writeLabel(w);
      w.println(TH_CLOSE);
    }

    public void writeCell(PrintWriter w, OutputElement group, Context context) {
      w.println(TD_OPEN);
      writeCellContent(w, group, context);
      w.println(TD_CLOSE);
    }
  }

}
