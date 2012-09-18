package scoutdoc.main.eclipsescout;

import scoutdoc.main.check.AbstractUseChecker;
import scoutdoc.main.check.Severity;
import scoutdoc.main.mediawiki.ApiFileContentType;
import scoutdoc.main.mediawiki.ContentFileUtility;
import scoutdoc.main.structure.Page;
import scoutdoc.main.structure.PageType;

public class UseScoutPageTemplateChecker extends AbstractUseChecker {

  public UseScoutPageTemplateChecker() {
    super(ApiFileContentType.Templates, ScoutPages.PAGE_TEMPLATE, false);
  }

  @Override
  public boolean shouldCheck(Page page) {
    return !ScoutPages.MAIN_PAGE.equals(page) && page.getType() == PageType.Article && ScoutPages.isScoutPage(page) && ContentFileUtility.checkRedirection(page) == null;
  }

  @Override
  protected String createType() {
    return "{{ScoutPage}} template is missing";
  }

  @Override
  protected Severity createSeverity() {
    return Severity.warning;
  }

  @Override
  protected String createMessage(Page page) {
    return "This page do not use Template:ScoutPage. Add {{ScoutPage|cat=XXX}} to the page.";
  }
}
