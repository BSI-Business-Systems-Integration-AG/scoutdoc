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

package scoutdoc.main;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import scoutdoc.main.check.Check;
import scoutdoc.main.check.ScoutDocCheck;
import scoutdoc.main.converter.ScoutDocConverter;
import scoutdoc.main.fetch.ScoutDocFetch;
import scoutdoc.main.filter.AcceptAllPageFilter;
import scoutdoc.main.filter.IPageFilter;
import scoutdoc.main.structure.Page;
import scoutdoc.main.structure.PageUtility;
import scoutdoc.main.structure.Pages;
import scoutdoc.main.structure.RelatedPagesStrategy;
import scoutdoc.main.structure.Task;
import scoutdoc.main.structure.TaskUtility;

import com.google.common.base.Joiner;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

public class Main {

  private static final String DEFAULT_CHECKSTYLE_NAME = "Checkstyle.xml";
  private static final String DEFAULT_DASHBOARD_NAME = "dashboard";
  private static final String HELP_ID = "h";
  private static final String PROP_ID = "c";
  private static final String SOURCE_TASKS_ID = "t";
  private static final String SOURCE_ALL_PAGES_ID = "p";
  private static final String SOURCE_LIST_ID = "l";
  private static final String SOURCE_RSS_ID = "r";
  private static final String SOURCE_RECENT_CHANGES_ID = "g";
  private static final String SOURCE_FILTER_ID = "f";
  private static final String OPERATION_ID = "o";
  private static final String OUTPUT_CHECKSTYLE_ID = "x";
  private static final String OUTPUT_DASHBOARD_ID = "d";

  /**
   * @param args
   */
  public static void main(String[] args) {
    Option optHelp = new Option(HELP_ID, "help", false, "print this message");

    Option optProp = new Option(PROP_ID, "config", true, "configuration file");
    optProp.setArgName("file");

    //source
    Option optTask = new Option(SOURCE_TASKS_ID, "task", true, "(source) one or many task files");
    optTask.setArgName("files");
    optTask.setArgs(Option.UNLIMITED_VALUES);

    Option optAllPages = new Option(SOURCE_ALL_PAGES_ID, "pages", false, "(source) use the pages contained in the source folder");

    Option optListPages = new Option(SOURCE_LIST_ID, "list", true, "(source) list of pages contained in the file");
    optListPages.setArgName("file");

    Option optRecentChange = new Option(SOURCE_RECENT_CHANGES_ID, "recent-changes", false, "(source) use the pages from the wiki recent changes");

    Option optRss = new Option(SOURCE_RSS_ID, "rss", false, "(source) use the pages from the rss feed of the wiki");

    OptionGroup sourceGroup = new OptionGroup();
    sourceGroup.setRequired(true);
    sourceGroup.addOption(optTask);
    sourceGroup.addOption(optAllPages);
    sourceGroup.addOption(optListPages);
    sourceGroup.addOption(optRecentChange);
    sourceGroup.addOption(optRss);

    Option optfilter = new Option(SOURCE_FILTER_ID, "filter", true, "Filter for list of pages used as source");
    optfilter.setArgName("class");

    List<String> values = Lists.newArrayList();
    for (Operation o : Operation.values()) {
      values.add(o.name());
    }
    Option optOperation = new Option(OPERATION_ID, "operation", true, "operation: " + Joiner.on(", ").join(values));
    optOperation.setArgName("operations");
    optOperation.setArgs(Option.UNLIMITED_VALUES);
    optOperation.setRequired(true);

    Option optOutputCheckstyle = new Option(OUTPUT_CHECKSTYLE_ID, "output-checkstyle", true, "(CHECK output) create a xml checkstyle file (<filename> is optional. Default: " + DEFAULT_CHECKSTYLE_NAME + ")");
    optOutputCheckstyle.setArgName("filename");
    optOutputCheckstyle.setOptionalArg(true);

    Option optOutputDashboard = new Option(OUTPUT_DASHBOARD_ID, "output-dashboard", true, "(CHECK output) create an html dashboard (<folder> is optional. Default: " + DEFAULT_DASHBOARD_NAME + ")");
    optOutputDashboard.setArgName("folder");
    optOutputDashboard.setOptionalArg(true);

    Options options = new Options();
    options.addOption(optHelp);
    options.addOption(optProp);
    options.addOptionGroup(sourceGroup);
    options.addOption(optfilter);
    options.addOption(optOperation);
    options.addOption(optOutputCheckstyle);
    options.addOption(optOutputDashboard);

    try {
      CommandLineParser parser = new GnuParser();
      CommandLine cmd = parser.parse(options, args);

      if (cmd.hasOption(HELP_ID)) {
        printHelpAndExit(options);
      }

      if (cmd.hasOption(PROP_ID)) {
        ProjectProperties.initProperties(cmd.getOptionValue(PROP_ID));
      }
      Pages.initPageList();

      List<Operation> operations = readOptionEnum(cmd, optOperation, Operation.class);
      List<Task> tasks = readTasks(cmd, optTask);

      Collection<Page> pageList;
      if (cmd.hasOption(SOURCE_ALL_PAGES_ID)) {
        pageList = PageUtility.loadPages(ProjectProperties.getFolderWikiSource());
      }
      else if (cmd.hasOption(SOURCE_LIST_ID)) {
        String name = cmd.getOptionValue(SOURCE_LIST_ID);
        try {
          pageList = PageUtility.readList(name);
        }
        catch (IOException e) {
          throw new MissingArgumentException("IOException for file <" + name + "> for <" + optListPages.getLongOpt() + "> : " + e.getMessage());
        }
      }
      else {
        pageList = Collections.emptyList();
      }

      IPageFilter filter;
      if (cmd.hasOption(SOURCE_FILTER_ID)) {
        if (tasks.size() > 0) {
          throw new MissingArgumentException("Filter <" + optfilter.getLongOpt() + "> is not allowed for source <" + optTask.getLongOpt() + ">.");
        }
        filter = newInstance(cmd.getOptionValue(SOURCE_FILTER_ID), IPageFilter.class, new AcceptAllPageFilter());
      }
      else {
        filter = new AcceptAllPageFilter();
      }
      List<Page> pages = Lists.newArrayList();
      for (Page page : pageList) {
        if (filter.keepPage(page)) {
          pages.add(page);
        }
      }

      if (operations.contains(Operation.FETCH)) {
        if (pages.size() > 0) {
          ScoutDocFetch sdf = new ScoutDocFetch();
          RelatedPagesStrategy strategy;
          if (cmd.hasOption(SOURCE_ALL_PAGES_ID)) {
            strategy = RelatedPagesStrategy.NO_RELATED_PAGES;
          }
          else if (cmd.hasOption(SOURCE_LIST_ID)) {
            strategy = RelatedPagesStrategy.IMAGES_TEMPLATES_AND_LINKS;
          }
          else {
            throw new IllegalStateException("Page list comes from an unexpected option");
          }
          sdf.execute(pages, strategy);
        }
        else if (cmd.hasOption(SOURCE_RECENT_CHANGES_ID)) {
          ScoutDocFetch sdf = new ScoutDocFetch();
          sdf.executeRecentChanges(filter);
        }
        else if (cmd.hasOption(SOURCE_RSS_ID)) {
          ScoutDocFetch sdf = new ScoutDocFetch();
          sdf.executeRss(filter);
        }
        else if (tasks.size() > 0) {
          for (Task task : tasks) {
            ScoutDocFetch sdf = new ScoutDocFetch();
            sdf.execute(task);
          }
        }
        else {
          throw new MissingArgumentException("Missing a source");
        }
      }

      if (operations.contains(Operation.CHECK)) {
        ScoutDocCheck sdc = new ScoutDocCheck();
        List<Check> checks = Lists.newArrayList();
        ensureNotSet(cmd, optRecentChange, Operation.CHECK);
        ensureNotSet(cmd, optRss, Operation.CHECK);
        if (pages.size() > 0) {
          checks = sdc.analysePages(pages);
        }
        else if (tasks.size() > 0) {
          for (Task task : tasks) {
            ScoutDocCheck sdcForTask = new ScoutDocCheck();
            checks.addAll(sdcForTask.execute(task));
          }
        }
        else {
          throw new MissingArgumentException("Missing a source");
        }
        //output:
        if (cmd.hasOption(OUTPUT_CHECKSTYLE_ID)) {
          String fileName = cmd.getOptionValue(OUTPUT_CHECKSTYLE_ID, DEFAULT_CHECKSTYLE_NAME);
          sdc.writeCheckstyleFile(checks, fileName);
        }
        if (cmd.hasOption(OUTPUT_DASHBOARD_ID)) {
          String folderName = cmd.getOptionValue(OUTPUT_DASHBOARD_ID, DEFAULT_DASHBOARD_NAME);
          sdc.writeDashboardFiles(checks, folderName);
        }
      }

      if (operations.contains(Operation.CONVERT)) {
        ensureNotSet(cmd, optAllPages, Operation.CONVERT);
        ensureNotSet(cmd, optListPages, Operation.CONVERT);
        ensureNotSet(cmd, optRecentChange, Operation.CONVERT);
        ensureNotSet(cmd, optRss, Operation.CONVERT);
        if (tasks.size() > 0) {
          for (Task task : tasks) {
            ScoutDocConverter sdc = new ScoutDocConverter();
            sdc.execute(task);
          }
        }
        else {
          throw new MissingArgumentException("Missing a source");
        }
      }

    }
    catch (MissingOptionException e) {
      // Check if it is an error or if optHelp was selected.
      boolean help = false;
      try {
        Options helpOptions = new Options();
        helpOptions.addOption(optHelp);
        CommandLineParser parser = new PosixParser();
        CommandLine line = parser.parse(helpOptions, args);
        if (line.hasOption(HELP_ID)) {
          help = true;
        }
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
      if (!help) {
        System.err.println(e.getMessage());
        System.err.flush();
      }
      printHelpAndExit(options);
    }
    catch (MissingArgumentException e) {
      System.err.println(e.getMessage());
      printHelpAndExit(options);
    }
    catch (ParseException e) {
      System.err.println("Error while parsing the command line: " + e.getMessage());
      System.exit(1);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void ensureNotSet(CommandLine cmd, Option option, Operation operation) throws MissingArgumentException {
    if (cmd.hasOption(option.getOpt())) {
      throw new MissingArgumentException("Source <" + option.getLongOpt() + "> is not allowed for the <" + operation.name() + "> operation.");
    }
  }

  private static <T extends Enum<T>> List<T> readOptionEnum(CommandLine cmd, Option option, Class<T> c) throws MissingArgumentException {
    List<T> operations = Lists.newArrayList();
    for (String name : cmd.getOptionValues(option.getOpt())) {
      try {
        T operation = Enum.valueOf(c, name);
        operations.add(operation);
      }
      catch (IllegalArgumentException e) {
        throw new MissingArgumentException("Unknown value '" + name + "' for '--" + option.getLongOpt() + "'");
      }
    }
    return operations;
  }

  private static List<Task> readTasks(CommandLine cmd, Option option) throws MissingArgumentException {
    List<Task> result = Lists.newArrayList();
    if (cmd.hasOption(option.getOpt())) {
      for (String name : cmd.getOptionValues(option.getOpt())) {
        try {
          Task task = TaskUtility.toTask(name);
          result.add(task);
        }
        catch (IOException e) {
          throw new MissingArgumentException("IOException for file '" + name + "' for '--" + option.getLongOpt() + "' : " + e.getMessage());
        }
      }
    }
    return result;
  }

  private static void printHelpAndExit(Options options) {
    HelpFormatter helpFormatter = new HelpFormatter();
    helpFormatter.setWidth(200);
    helpFormatter.setOptionComparator(new Comparator<Option>() {

      @Override
      public int compare(Option opt1, Option opt2) {
        return ComparisonChain.start()
            .compare(opt1.getOpt(), opt2.getOpt(), Ordering.explicit(
                HELP_ID,
                PROP_ID,
                SOURCE_TASKS_ID,
                SOURCE_ALL_PAGES_ID,
                SOURCE_LIST_ID,
                SOURCE_RECENT_CHANGES_ID,
                SOURCE_RSS_ID,
                SOURCE_FILTER_ID,
                OPERATION_ID,
                OUTPUT_CHECKSTYLE_ID,
                OUTPUT_DASHBOARD_ID
                )).result();
      }
    });
    helpFormatter.printHelp("scoutdoc.main.Main", options);
    System.exit(1);
  }

  static <T> T newInstance(String className, Class<T> classType, T defaultClass) {
    Class<?> cls;
    try {
      cls = Class.forName(className);
      if (classType.isAssignableFrom(cls)) {
        return classType.cast(cls.newInstance());
      }
    }
    catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    catch (InstantiationException e) {
      e.printStackTrace();
    }
    catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return defaultClass;
  }

  public static enum Operation {
    FETCH,
    CHECK,
    CONVERT
  }
}
