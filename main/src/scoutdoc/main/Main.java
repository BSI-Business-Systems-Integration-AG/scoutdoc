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

import scoutdoc.main.check.ScoutDocCheck;
import scoutdoc.main.converter.ScoutDocConverter;
import scoutdoc.main.fetch.ScoutDocFetch;
import scoutdoc.main.structure.Page;
import scoutdoc.main.structure.PageUtility;
import scoutdoc.main.structure.RelatedPagesStrategy;
import scoutdoc.main.structure.Task;
import scoutdoc.main.structure.TaskUtility;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class Main {

	private static final String DEFAULT_CHECKSTYLE_NAME = "Checkstyle.xml";
	private static final String HELP_ID = "h";
	private static final String PROP_ID = "c";
	private static final String SOURCE_TASKS_ID = "t";
	private static final String SOURCE_ALL_PAGES_ID = "p";
	private static final String SOURCE_LIST_ID = "l";
	private static final String SOURCE_RSS_ID = "r";
	private static final String OPERATION_ID = "o";
	private static final String OUTPUT_CHECKSTYLE_ID = "x";

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
		
		Option optRss = new Option(SOURCE_RSS_ID, "rss", false, "(source) use the pages from the rss file");
		
		OptionGroup sourceGroup = new OptionGroup();
		sourceGroup.setRequired(true);
		sourceGroup.addOption(optTask);
		sourceGroup.addOption(optAllPages);
		sourceGroup.addOption(optListPages);
		sourceGroup.addOption(optRss);
		
		List<String> values = Lists.newArrayList();
		for (Operation o : Operation.values()) {
			values.add(o.name());
		}
		Option optOperation = new Option(OPERATION_ID, "operation", true, "operation: "+ Joiner.on(", ").join(values));
		optOperation.setArgName("operations");
		optOperation.setArgs(Option.UNLIMITED_VALUES);
		optOperation.setRequired(true);
		
		Option optOutputCheckstyle = new Option(OUTPUT_CHECKSTYLE_ID, "output-checkstyle", true, "(CHECK output) create a xml checkstyle file (<filename> is optional. Default: "+ DEFAULT_CHECKSTYLE_NAME + ")");
		optOutputCheckstyle.setArgName("filename");
		
		Options options = new Options();
		options.addOption(optHelp);
		options.addOption(optProp);
		options.addOptionGroup(sourceGroup);
		options.addOption(optOperation);
		options.addOption(optOutputCheckstyle);

		try {
			CommandLineParser parser = new GnuParser();
			CommandLine cmd = parser.parse(options, args);
			
			if(cmd.hasOption(HELP_ID)) {
				printHelpAndExit(options);
			}
			
			if(cmd.hasOption(PROP_ID)) {
				ProjectProperties.initProperties(cmd.getOptionValue(PROP_ID));
			}
			
			List<Operation> operations = readOptionEnum(cmd, optOperation, Operation.class);
			List<Task> tasks = readTasks(cmd, optTask);
			
			List<Page> pages;
			if(cmd.hasOption(SOURCE_ALL_PAGES_ID)) {
				pages = PageUtility.loadPages(ProjectProperties.getFolderWikiSource());
			} else if(cmd.hasOption(SOURCE_LIST_ID)) {
				String name = cmd.getOptionValue(SOURCE_LIST_ID);
				try {
					pages = PageUtility.readList(name);
				} catch (IOException e) {
					throw new MissingArgumentException("IOException for file <" + name + "> for <" + optListPages.getLongOpt() + "> : " + e.getMessage());
				}
			} else {
				pages = Collections.emptyList();
			}
			
			if(operations.contains(Operation.FETCH)) {
				if(pages.size() > 0) {
					ScoutDocFetch sdf = new ScoutDocFetch();
					RelatedPagesStrategy strategy;
					if(cmd.hasOption(SOURCE_ALL_PAGES_ID)) {
						strategy = RelatedPagesStrategy.NO_RELATED_PAGES;
					} else if(cmd.hasOption(SOURCE_LIST_ID)) {
						strategy = RelatedPagesStrategy.IMAGES_TEMPLATES_AND_LINKS;
					} else {
						throw new IllegalStateException("Page list comes from an unexpected option");
					}
					sdf.execute(pages, strategy);
				} else if(cmd.hasOption(SOURCE_RSS_ID)) {
					ScoutDocFetch sdf = new ScoutDocFetch();
					sdf.executeRss();
				} else if(tasks.size() > 0) {
					for (Task task : tasks) {
						ScoutDocFetch sdf = new ScoutDocFetch();
						sdf.execute(task);
					}
				} else {
					throw new MissingArgumentException("Missing a source");
				}
			}
			
			if(operations.contains(Operation.CHECK)) {
				if(cmd.hasOption(SOURCE_RSS_ID)) {
					throw new MissingArgumentException("Source <"+optRss.getLongOpt()+"> is not allowed for the <CHECK> operation.");
				} else if(pages.size() > 0) {
					ScoutDocCheck sdc = new ScoutDocCheck();
					String fileName = cmd.getOptionValue(OUTPUT_CHECKSTYLE_ID, DEFAULT_CHECKSTYLE_NAME);
					sdc.execute(pages, fileName);
				} else if(tasks.size() > 0) {
					for (Task task : tasks) {
						ScoutDocCheck sdc = new ScoutDocCheck();
						sdc.execute(task);
					}
				} else {
					throw new MissingArgumentException("Missing a source");
				}
			}
			
			if(operations.contains(Operation.CONVERT)) {
				if(cmd.hasOption(SOURCE_ALL_PAGES_ID)) {
					throw new MissingArgumentException("Source <"+optAllPages.getLongOpt()+"> is not allowed for the <CONVERT> operation.");
				} else if(cmd.hasOption(SOURCE_LIST_ID)) {
					throw new MissingArgumentException("Source <"+optListPages.getLongOpt()+"> is not allowed for the <CONVERT> operation.");
				} else if(cmd.hasOption(SOURCE_RSS_ID)) {
					throw new MissingArgumentException("Source <"+optRss.getLongOpt()+"> is not allowed for the <CONVERT> operation.");
				} else if(tasks.size() > 0) {
					for (Task task : tasks) {
						ScoutDocConverter sdc = new ScoutDocConverter();
						sdc.execute(task);
					}
				} else {
					throw new MissingArgumentException("Missing a source");
				}
			}
			
		} catch (MissingOptionException e) {
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
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (!help) {
				System.err.println(e.getMessage());
				System.err.flush();
			}
			printHelpAndExit(options);
		} catch (MissingArgumentException e) {
			System.err.println(e.getMessage());
			printHelpAndExit(options);
		} catch (ParseException e) {
			System.err.println("Error while parsing the command line: " + e.getMessage());
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static <T extends Enum<T>> List<T> readOptionEnum(CommandLine cmd, Option option, Class<T> c) throws MissingArgumentException {
		List<T> operations = Lists.newArrayList();
		for (String name : cmd.getOptionValues(option.getOpt())) {
			try {
				T operation = Enum.valueOf(c, name);
				operations.add(operation);
			} catch (IllegalArgumentException e) {
				throw new MissingArgumentException("Unknown value '"+ name+"' for '--"+option.getLongOpt()+"'");
			}
		}
		return operations;
	}
	
	private static List<Task> readTasks(CommandLine cmd, Option option) throws MissingArgumentException {
		List<Task> result = Lists.newArrayList();
		if(cmd.hasOption(option.getOpt())) {
			for (String name : cmd.getOptionValues(option.getOpt())) {
				try {
					Task task = TaskUtility.toTask(name);
					result.add(task);
				} catch (IOException e) {
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
				String key1 = opt1.getOpt();
				String key2 = opt2.getOpt();
				int r = 0;
				r = compare(r, HELP_ID, key1, key2);
				r = compare(r, PROP_ID, key1, key2);
				r = compare(r, SOURCE_TASKS_ID, key1, key2);
				r = compare(r, SOURCE_ALL_PAGES_ID, key1, key2);
				r = compare(r, SOURCE_LIST_ID, key1, key2);
				r = compare(r, SOURCE_RSS_ID, key1, key2);
				r = compare(r, OPERATION_ID, key1, key2);
				r = compare(r, OUTPUT_CHECKSTYLE_ID, key1, key2);
				return r;
			}

			private int compare(int r, String id, String key1, String key2) {
				if(r != 0) {
					return r;
				} else if(key1.equals(key2)) {
					return 0;
				} else if(id.equals(key1)) {
					return -1;
				} else if(id.equals(key2)) {
					return 1;
				} else {
					return 0;
				}
			}
			
		});
		helpFormatter.printHelp("scoutdoc.main.Main", options);
		System.exit(1);
	}

	public static enum Operation {
		FETCH,
		CHECK,
		CONVERT
	}
}
