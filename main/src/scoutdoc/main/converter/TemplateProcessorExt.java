/*******************************************************************************
 * Copyright (c) 2010, 2012 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     BSI Business Systems Integration AG - Ext version
 *******************************************************************************/

package scoutdoc.main.converter;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.internal.wikitext.mediawiki.core.AbstractMediaWikiLanguage;
import org.eclipse.mylyn.wikitext.mediawiki.core.Template;
import org.eclipse.mylyn.wikitext.mediawiki.core.TemplateResolver;

import scoutdoc.main.converter.finder.PositionFinder;
import scoutdoc.main.converter.finder.SubstringFinder;
import scoutdoc.main.converter.finder.SubstringFinder.Range;

import com.google.common.base.CharMatcher;

/**
 * Copy of the WikiText TemplateProcessor to add some features that should be merged back in the official library.
 */
public class TemplateProcessorExt {

//	private static final Pattern templatePattern = Pattern.compile("(?:^|(?<!\\{))(\\{\\{(#?[a-zA-Z0-9_ :]+)\\s*(\\|[^\\}]*)?\\}\\})"); //$NON-NLS-1$
	
	private static final SubstringFinder templateFinder = SubstringFinder.define("{{", "}}"); //$NON-NLS-1$ //$NON-NLS-2$

	private static final String templateSeparator = "|"; //$NON-NLS-1$
	
	private static final Pattern templateParameterPattern = Pattern.compile("\\{\\{\\{([a-zA-Z0-9]+)\\s*(\\|[^\\}]*)?\\}\\}\\}"); //$NON-NLS-1$

	private static final PositionFinder parameterSpec = PositionFinder.define("|", "[", "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	private static final Pattern includeOnlyPattern = Pattern.compile(".*?<includeonly>(.*?)</includeonly>.*", //$NON-NLS-1$
			Pattern.DOTALL);

	private static final Pattern noIncludePattern = Pattern.compile("<noinclude>(.*?)</noinclude>", Pattern.DOTALL); //$NON-NLS-1$

	private final AbstractMediaWikiLanguage mediaWikiLanguage;

	private final Map<String, Template> templateByName = new HashMap<String, Template>();

	private final List<Pattern> excludePatterns = new ArrayList<Pattern>();

	public TemplateProcessorExt(AbstractMediaWikiLanguage abstractMediaWikiLanguage) {
		this.mediaWikiLanguage = abstractMediaWikiLanguage;

		for (Template template : mediaWikiLanguage.getTemplates()) {
			templateByName.put(template.getName(), normalize(template));
		}
		String templateExcludes = abstractMediaWikiLanguage.getTemplateExcludes();
		if (templateExcludes != null) {
			String[] split = templateExcludes.split("\\s*,\\s*"); //$NON-NLS-1$
			for (String exclude : split) {
				String pattern = exclude.replaceAll("([^a-zA-Z:\\*])", "\\\\$1").replaceAll("\\*", ".*?"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				excludePatterns.add(Pattern.compile(pattern, Pattern.CASE_INSENSITIVE));
			}
		}
		putPlaceHolderTemplate(PlaceHolderTemplate.ScoutLink);
		putPlaceHolderTemplate(PlaceHolderTemplate.note);
		putPlaceHolderTemplate(PlaceHolderTemplate.tip);
		putPlaceHolderTemplate(PlaceHolderTemplate.warning);
		putPlaceHolderTemplate(PlaceHolderTemplate.BASEPAGENAME);
		putPlaceHolderTemplate(new Template("#expr:35+10", "45"));
		putPlaceHolderTemplate(new Template("#expr:35+5", "40"));
	}


	private void putPlaceHolderTemplate(Template template) {
		templateByName.put(template.getName(), template);
	}
	

 	public String processTemplates(String markupContent, String markupPageName) {
 		return processTemplates(markupContent, markupPageName, Collections.<String> emptySet());
 	}

	private String processTemplates(String markupContent, String markupPageName, Set<String> usedTemplates) {
		StringBuilder processedMarkup = new StringBuilder();

		int index = 0;
		Range templateRange = templateFinder.nextRange(markupContent, index);
		while (!SubstringFinder.EMPTY_RANGE.equals(templateRange)) {
			processedMarkup.append(markupContent.substring(index, templateRange.getRangeStart()));

			int contentStart = templateRange.getContentStart();
			int contentEnd = templateRange.getContentEnd();

			String templateName, parametersText;
			int pipeIndex = markupContent.indexOf(templateSeparator, contentStart);
			if (pipeIndex == -1 || pipeIndex > contentEnd) {
				templateName = markupContent.substring(contentStart, contentEnd);
				parametersText = ""; //$NON-NLS-1$
			} else {
				templateName = markupContent.substring(contentStart, pipeIndex);
				parametersText = markupContent.substring(pipeIndex, contentEnd);
			}

			templateName = templateName.trim();
			Template template = resolveTemplate(templateName);
			if (template != null) {
				String replacementText;
				if (usedTemplates.contains(templateName)) {
					StringBuilder sb = new StringBuilder();
					sb.append("<span class=\"error\">"); //$NON-NLS-1$
					sb.append(MessageFormat.format("Template loop detected:{0}", template.getName())); //$NON-NLS-1$
					sb.append("</span>"); //$NON-NLS-1$
					replacementText = sb.toString();
				} else {
					//The replacementText might contain other templates. Add the current template to the set of used template and call recursively this function again:
					Set<String> templates = new HashSet<String>(usedTemplates);
					templates.add(templateName);
					parametersText = processTemplates(parametersText, markupPageName);
					List<Parameter> parameters = processParameters(parametersText); 
					replacementText = processTemplate(template, markupPageName, parameters);
					replacementText = processTemplates(replacementText, markupPageName, templates);
				}
				processedMarkup.append(replacementText);
			}

			index = contentEnd + templateFinder.getCloseLength();
			templateRange = templateFinder.nextRange(markupContent, index);
		}
		if (index < markupContent.length()) {
			processedMarkup.append(markupContent.substring(index));
		}

		return processedMarkup.toString();
	}

	private String processTemplate(Template template, String pageName, List<Parameter> parameters) {
		if (template.getTemplateMarkup() == null) {
			return ""; //$NON-NLS-1$
		}
		
		if (template == PlaceHolderTemplate.ScoutLink) {
			return processTemplateScoutLinkTemplate(parameters);
		} else if(template == PlaceHolderTemplate.BASEPAGENAME) {
			return processBASEPAGENAMETemplate(pageName);
		} else if(template == PlaceHolderTemplate.note) {
			return processNoteTemplate(parameters);
		} else if(template == PlaceHolderTemplate.tip) {
			return processTipTemplate(parameters);
		} else if(template == PlaceHolderTemplate.warning) {
			return processWarningTemplate(parameters);
		}
		
		String macro = template.getTemplateContent();
		
		StringBuilder processedMarkup = new StringBuilder();
		int lastIndex = 0;
		Matcher matcher = templateParameterPattern.matcher(macro);
		while (matcher.find()) {
			int start = matcher.start();
			if (lastIndex < start) {
				processedMarkup.append(macro.substring(lastIndex, start));
			}
			String parameterName = matcher.group(1);
			//Default value:
			String parameterValue = matcher.group(2);
			if (parameterValue != null && parameterValue.startsWith("|")) { //$NON-NLS-1$
				parameterValue = parameterValue.substring(1);
			}
			try {
				int parameterIndex = Integer.parseInt(parameterName);
				if (parameterIndex <= parameters.size() && parameterIndex > 0) {
					parameterValue = parameters.get(parameterIndex - 1).value;
				}
			} catch (NumberFormatException e) {
				for (Parameter param : parameters) {
					if (parameterName.equalsIgnoreCase(param.name)) {
						parameterValue = param.value;
						break;
					}
				}
			}
			if (parameterValue != null) {
				processedMarkup.append(parameterValue);
			}

			lastIndex = matcher.end();
		}
		if (lastIndex == 0) {
			return macro;
		}
		if (lastIndex < macro.length()) {
			processedMarkup.append(macro.substring(lastIndex));
		}
		return processedMarkup.toString();
	}

	private String processTemplateScoutLinkTemplate(List<Parameter> parameters) {
		String categoryName, pageName, linkName;
		if(parameters.size()==3) {
			categoryName = parameters.get(0).value;
			pageName = parameters.get(1).value;
			linkName = parameters.get(2).value;
		} else if(parameters.size()==2 && "name".equals(parameters.get(1).name)) {
			categoryName = parameters.get(0).value;
			pageName = null;
			linkName = parameters.get(1).value;
		} else {
			throw new UnsupportedOperationException("unexpected ScoutLinkError");
//			return "*****ScoutLinkError*****";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("[[Scout/");
		sb.append(categoryName);
		if(pageName !=null) {
			sb.append("/");
			sb.append(pageName);			
		}
		if(linkName !=null) {
			sb.append("|");
			sb.append(linkName);			
		}
		sb.append("]]");
		return sb.toString();
	}
	
	private String processBASEPAGENAMETemplate(String pageName) {
		String output = pageName;
		int index = CharMatcher.is('/').lastIndexIn(pageName);
		if(index > 0) {
			output = pageName.substring(0, index);
		}
		return output;
	}
	
	private String processNoteTemplate(List<Parameter> parameters) {
		if(parameters.size()==2) {
			return "{{message|1='''"+ parameters.get(0).value + "'''<br/>" +
					parameters.get(1).value +
					"|image=Note.png|bgcolor=#def3fe|bdcolor=#c5d7e0}}";
		} else {
			throw new UnsupportedOperationException("unexpected Note Template Configuration");
	//		return "*****ScoutLinkError*****";
		}
	}
	
	private String processTipTemplate(List<Parameter> parameters) {
		if(parameters.size()==2) {
			return "{{message|1='''"+ parameters.get(0).value + "'''<br/>" +
					parameters.get(1).value +
					"|image=Idea.png|bgcolor=#def3fe|bdcolor=#c5d7e0}}";
		} else {
			throw new UnsupportedOperationException("unexpected Tip Template Configuration");
			//		return "*****ScoutLinkError*****";
		}
	}
	
	private String processWarningTemplate(List<Parameter> parameters) {
		if(parameters.size()==2) {
			return "{{message|1='''"+ parameters.get(0).value + "'''<br/>" +
					parameters.get(1).value +
					"|image=Warning2.png}}";
		} else {
			throw new UnsupportedOperationException("unexpected Warning Template Configuration");
			//		return "*****ScoutLinkError*****";
		}
	}
	
	private List<Parameter> processParameters(String parametersText) {
		List<Parameter> parameters = new ArrayList<TemplateProcessorExt.Parameter>();
		if (parametersText != null && parametersText.length() > 0) {
			int lastIndex = 1;
			for (Integer index : parameterSpec.indexesOf(parametersText, lastIndex)) {
				String parameterText = parametersText.substring(lastIndex, index);
				parameters.add(createParameter(parameterText));

				lastIndex = index + 1;
			}
			String parameterText = parametersText.substring(lastIndex);
			parameters.add(createParameter(parameterText));
		}
		return parameters;
	}

	private Parameter createParameter(String parameterText) {
		Parameter parameter = new Parameter();
		int i = parameterText.indexOf('=');
		if (i == -1) {
			parameter.value = parameterText;
		} else {
			String possibleName = parameterText.substring(0, i);
			if(possibleName.matches("[a-zA-Z0-9_ :]+")) {
				parameter.name = possibleName;
				parameter.value = parameterText.substring(i + 1);
			} else {
				parameter.value = parameterText;
			}
		}
		return parameter;
	}
	
	private Template resolveTemplate(String templateName) {
		if (!excludePatterns.isEmpty()) {
			for (Pattern p : excludePatterns) {
				if (p.matcher(templateName).matches()) {
					return null;
				}
			}
		}
		Template template = templateByName.get(templateName);
		if (template == null) {
			for (TemplateResolver resolver : mediaWikiLanguage.getTemplateProviders()) {
				template = resolver.resolveTemplate(templateName);
				if (template != null) {
					template = normalize(template);
					break;
				}
			}
			if (template == null) {
				throw new UnsupportedOperationException("Unknown Template Name:" + templateName);
//				template = new Template();
//				template.setName(templateName);
//				template.setTemplateMarkup(""); //$NON-NLS-1$
			}
			templateByName.put(template.getName(), template);
		}
		return template;
	}

	private Template normalize(Template template) {
		Template normalizedTemplate = new Template();
		normalizedTemplate.setName(template.getName());
		normalizedTemplate.setTemplateMarkup(normalizeTemplateMarkup(template.getTemplateContent()));

		return normalizedTemplate;
	}

	private String normalizeTemplateMarkup(String templateMarkup) {
		Matcher matcher = includeOnlyPattern.matcher(templateMarkup);
		if (matcher.matches()) {
			return matcher.group(1);
		}
		matcher = noIncludePattern.matcher(templateMarkup);
		return matcher.replaceAll(""); //$NON-NLS-1$
	}

	private static class Parameter {
		String name;
		String value;
	}
}
