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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	scoutdoc.main.check.RedirectionCheckerTest.class,
	scoutdoc.main.check.CheckstyleFileWriterTest.class,
	scoutdoc.main.check.dashboard.OutputElementComparatorTest.class,
	scoutdoc.main.converter.PageMapperTest.class,
	scoutdoc.main.converter.TemplateProcessorExtTest.class,
	scoutdoc.main.converter.finder.PositionFinderTest.class,
	scoutdoc.main.converter.finder.SubstringFinderTest.class,
	scoutdoc.main.mediawiki.ApiFileUtilityTest.class,
	scoutdoc.main.mediawiki.ContentFileUtilityTest.class,
	scoutdoc.main.structure.PageUtilityTest.class,
	scoutdoc.main.structure.RelatedPagesStrategyTest.class,
	scoutdoc.main.structure.TaskUtilityTest.class,
})
public class AllTests {
}
