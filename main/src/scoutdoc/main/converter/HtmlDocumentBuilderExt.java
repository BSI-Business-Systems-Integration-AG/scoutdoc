/*******************************************************************************
 * Copyright (c) 2007, 2011, 2012 David Green and others.
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

import java.io.StringWriter;
import java.util.List;

import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;

/**
 * Extension of the HtmlDocumentBuilder of WikiText to add navigation table
 */
public class HtmlDocumentBuilderExt extends HtmlDocumentBuilder {
	private static final ConversionItem NULL_ITEM = new ConversionItem();
	private final AbstractCreator NULL_CREATOR = new NullCreator();
;

	private List<ConversionItem> items;
	private int currentItemIndex;
	
	public HtmlDocumentBuilderExt(StringWriter out) {
		super(out);
	}

	protected void beginBody() {
		super.beginBody();
		appendNavigationTable(true, getItems(), getCurrentItemIndex());
	}

	protected void endBody() {
		appendNavigationTable(false, getItems(), getCurrentItemIndex());
		super.endBody();
	}
	
	private void appendNavigationTable(boolean topTable, List<ConversionItem> items, int currentIndex) {
		if(items.size() > 1) {
			ConversionItem prevItem, homeItem, nextItem;
			if(currentIndex > 0) {
				prevItem = items.get(currentIndex-1);
			} else {
				prevItem = NULL_ITEM;
			}
			
			if(!topTable && currentIndex > 0) {
				homeItem = items.get(0);
			} else {
				homeItem = NULL_ITEM;
			}
			
			if(currentIndex < items.size() - 1) {
				nextItem = items.get(currentIndex + 1);
			} else {
				nextItem = NULL_ITEM;
			}
			
			writer.writeStartElement(getHtmlNsUri(), "table"); //$NON-NLS-1$
			writer.writeAttribute(getHtmlNsUri(), "border", "0");
			writer.writeAttribute(getHtmlNsUri(), "class", "navigation");
			writer.writeAttribute(getHtmlNsUri(), "style", "width: 100%;");
			writer.writeAttribute(getHtmlNsUri(), "summary", "navigation");
			
			if(topTable) {
				writer.writeStartElement(getHtmlNsUri(), "tr"); //$NON-NLS-1$
				writer.writeStartElement(getHtmlNsUri(), "th"); //$NON-NLS-1$
				writer.writeAttribute(getHtmlNsUri(), "align", "center");
				writer.writeAttribute(getHtmlNsUri(), "colspan", "3");
				writer.writeAttribute(getHtmlNsUri(), "rowspan", "1");
				writer.writeAttribute(getHtmlNsUri(), "style", "width: 100%;");
				writer.writeCharacters(items.get(currentItemIndex).outputTitle);
				writer.writeEndElement(); // th
				writer.writeEndElement(); // tr
				
			}
			AbstractCreator prevItemLinkCreator = new LinkCreator(prevItem, "Previous", "../../Images/prev.gif");
			AbstractCreator homeItemLinkCreator = new LinkCreator(homeItem, homeItem.outputTitle, "../../Images/home.gif");
			AbstractCreator nextItemLinkCreator = new LinkCreator(nextItem, "Next", "../../Images/next.gif");
			appendNavigationTableTR(prevItemLinkCreator, homeItemLinkCreator, nextItemLinkCreator);
			appendNavigationTableTR(new TextCreator(prevItem.outputTitle), NULL_CREATOR, new TextCreator(nextItem.outputTitle) );
			writer.writeEndElement(); // table
		}
	}


	private void appendNavigationTableTR(AbstractCreator left, AbstractCreator mid, AbstractCreator rigth) {
		writer.writeStartElement(getHtmlNsUri(), "tr"); //$NON-NLS-1$
		appendNavigationTableTD("left", "width: 20%", left);
		appendNavigationTableTD("center", "width: 60%", mid);
		appendNavigationTableTD("right", "width: 20%", rigth);
		writer.writeEndElement(); // tr
	}

	private void appendNavigationTableTD(String align, String style, AbstractCreator contentCreator) {
		writer.writeStartElement(getHtmlNsUri(), "td"); //$NON-NLS-1$
		writer.writeAttribute(getHtmlNsUri(), "align", align);
		writer.writeAttribute(getHtmlNsUri(), "colspan", "1");
		writer.writeAttribute(getHtmlNsUri(), "rowspan", "1");
		writer.writeAttribute(getHtmlNsUri(), "style", style);
		contentCreator.create();
		writer.writeEndElement(); // td
	}
	
	
	public List<ConversionItem> getItems() {
		return items;
	}
	
	public void setItems(List<ConversionItem> items) {
		this.items = items;
	}
	
	public int getCurrentItemIndex() {
		return currentItemIndex;
	}
	
	public void setCurrentItemIndex(int currentItemIndex) {
		this.currentItemIndex = currentItemIndex;
	}
	
	private abstract class AbstractCreator {
		abstract void create();
	}
	private class LinkCreator extends AbstractCreator {

		private ConversionItem conversionItem;
		private String imgAlt;
		private String imgSrc;

		public LinkCreator(ConversionItem conversionItem, String imgAlt, String imgSrc) {
			this.conversionItem = conversionItem;
			this.imgAlt = imgAlt;
			this.imgSrc = imgSrc;
		}

		@Override
		void create() {
			if(conversionItem != NULL_ITEM) {
				writer.writeStartElement(getHtmlNsUri(), "a"); //$NON-NLS-1$
				writer.writeAttribute(getHtmlNsUri(), "href", conversionItem.outputFileName);
				writer.writeAttribute(getHtmlNsUri(), "shape", "rect");
				writer.writeAttribute(getHtmlNsUri(), "title", conversionItem.outputTitle);
				writer.writeStartElement(getHtmlNsUri(), "img"); //$NON-NLS-1$
				writer.writeAttribute(getHtmlNsUri(), "alt", imgAlt);
				writer.writeAttribute(getHtmlNsUri(), "border", "0");
				writer.writeAttribute(getHtmlNsUri(), "src", imgSrc);
				writer.writeEndElement(); // img
				writer.writeEndElement(); // a
			}
			
		}
		
	}
	private class NullCreator extends AbstractCreator {
		@Override
		void create() {
		}
	}
	private class TextCreator extends AbstractCreator {
		private String text;

		public TextCreator(String text) {
			this.text = text;
		}

		@Override
		void create() {
			writer.writeCharacters(text);	
		}
	}
}
