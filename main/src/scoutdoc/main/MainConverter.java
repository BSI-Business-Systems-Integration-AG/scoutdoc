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

import java.io.FileNotFoundException;
import java.io.IOException;

import scoutdoc.main.converter.ScoutDocConverter;
import scoutdoc.main.structure.Task;
import scoutdoc.main.structure.TaskUtility;

public class MainConverter {
	
	/**
	 * @param args
	 * - First parameter: the task properties file.
	 * - Second parameter (optional): the project properties file.
	 */
	public static void main(String[] args) {
		try {
			//Load the task.
			if(args.length == 0) {
				throw new IllegalArgumentException("first argument must be the name of the task file");
			}
			Task t = TaskUtility.toTask(args[0]);

			if(args.length > 1) {
				ProjectProperties.initProperties(args[1]);
			}
			
			//Call ScoutDocFetch:
			ScoutDocConverter sdc = new ScoutDocConverter();
			sdc.execute(t);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
