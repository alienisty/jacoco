/*******************************************************************************
 * Copyright (c) 2009, 2014 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Alex Nistico
 *    
 *******************************************************************************/
package org.jacoco.report.html;

import java.util.Locale;

import org.jacoco.core.analysis.ICoverageNode.CounterEntity;
import org.jacoco.report.ILanguageNames;
import org.jacoco.report.JavaNames;
import org.jacoco.report.internal.html.IHTMLReportContext;
import org.jacoco.report.internal.html.ILinkable;
import org.jacoco.report.internal.html.index.ElementIndex;
import org.jacoco.report.internal.html.index.IIndexUpdate;
import org.jacoco.report.internal.html.resources.Resources;
import org.jacoco.report.internal.html.resources.Styles;
import org.jacoco.report.internal.html.table.BarColumn;
import org.jacoco.report.internal.html.table.CounterColumn;
import org.jacoco.report.internal.html.table.LabelColumn;
import org.jacoco.report.internal.html.table.PercentageColumn;
import org.jacoco.report.internal.html.table.Table;

/**
 * Context for HTML rendering
 * 
 * @param <S>
 *            the type of the sessions page.
 */
abstract class HTMLReportContext<S extends ILinkable> implements
		IHTMLReportContext {

	private ILanguageNames languageNames = new JavaNames();

	private Locale locale = Locale.getDefault();

	private String footerText = "";

	private String outputEncoding = "UTF-8";

	private Resources resources;

	private ElementIndex indexUpdate;

	S sessionsPage;

	private Table table;

	/**
	 * New instance with default settings.
	 */
	public HTMLReportContext() {
	}

	/**
	 * Sets the implementation for language name display. Java language names
	 * are defined by default.
	 * 
	 * @param languageNames
	 *            converter for language specific names
	 */
	public void setLanguageNames(final ILanguageNames languageNames) {
		this.languageNames = languageNames;
	}

	/**
	 * Sets the static resources to be used in this report.
	 * 
	 * @param resources
	 */
	public void setResources(final Resources resources) {
		this.resources = resources;
	}

	/**
	 * Sets the service for index updates.
	 * 
	 * @param index
	 */
	public void setIndexUpdate(final ElementIndex index) {
		this.indexUpdate = index;
	}

	/**
	 * Sets the locale used for report rendering. The current default locale is
	 * used by default.
	 * 
	 * @param locale
	 *            locale used for report rendering
	 */
	public void setLocale(final Locale locale) {
		this.locale = locale;
	}

	/**
	 * Sets the optional text that should be included in every footer page.
	 * 
	 * @param footerText
	 *            footer text
	 */
	public void setFooterText(final String footerText) {
		this.footerText = footerText;
	}

	/**
	 * Sets the link to the sessions page.
	 * 
	 * @param sessionsPage
	 *            sessions page link
	 */
	public void setSessionsPage(final S sessionsPage) {
		this.sessionsPage = sessionsPage;
	}

	/**
	 * Sets the encoding used for generated HTML pages. Default is UTF-8.
	 * 
	 * @param outputEncoding
	 *            HTML output encoding
	 */
	public void setOutputEncoding(final String outputEncoding) {
		this.outputEncoding = outputEncoding;
	}

	// === IHTMLReportContext ===

	public ILanguageNames getLanguageNames() {
		return languageNames;
	}

	public Resources getResources() {
		return resources;
	}

	public Table getTable() {
		if (table == null) {
			table = createTable();
		}
		return table;
	}

	private Table createTable() {
		final Table t = new Table();
		t.add("Element", null, new LabelColumn(), false);
		t.add("Missed Instructions", Styles.BAR, new BarColumn(
				CounterEntity.INSTRUCTION,
				locale), true);
		t.add("Cov.", Styles.CTR2,
				new PercentageColumn(CounterEntity.INSTRUCTION, locale), false);
		t.add("Missed Branches", Styles.BAR, new BarColumn(
				CounterEntity.BRANCH, locale),
				false);
		t.add("Cov.", Styles.CTR2, new PercentageColumn(CounterEntity.BRANCH,
				locale),
				false);
		addMissedTotalColumns(t, "Cxty", CounterEntity.COMPLEXITY);
		addMissedTotalColumns(t, "Lines", CounterEntity.LINE);
		addMissedTotalColumns(t, "Methods", CounterEntity.METHOD);
		addMissedTotalColumns(t, "Classes", CounterEntity.CLASS);
		return t;
	}

	private void addMissedTotalColumns(final Table table, final String label,
			final CounterEntity entity) {
		table.add("Missed", Styles.CTR1,
				CounterColumn.newMissed(entity, locale), false);
		table.add(label, Styles.CTR2, CounterColumn.newTotal(entity, locale),
				false);
	}

	public String getFooterText() {
		return footerText;
	}

	public ILinkable getSessionsPage() {
		return sessionsPage;
	}

	public String getOutputEncoding() {
		return outputEncoding;
	}

	public IIndexUpdate getIndexUpdate() {
		return indexUpdate;
	}

	public Locale getLocale() {
		return locale;
	}
}
