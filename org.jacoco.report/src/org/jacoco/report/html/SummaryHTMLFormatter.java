/*******************************************************************************
 * Copyright (c) 2009, 2015 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Alex Nistico - initial implementation
 *    
 *******************************************************************************/
package org.jacoco.report.html;

import static org.jacoco.core.analysis.ICoverageNode.CounterEntity.INSTRUCTION;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.analysis.ICoverageNode.CounterEntity;
import org.jacoco.core.analysis.IPackageCoverage;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.SessionInfo;
import org.jacoco.report.IMultiReportOutput;
import org.jacoco.report.IReportGroupVisitor;
import org.jacoco.report.IReportVisitor;
import org.jacoco.report.ISourceFileLocator;
import org.jacoco.report.internal.ReportOutputFolder;
import org.jacoco.report.internal.html.HTMLElement;
import org.jacoco.report.internal.html.ILinkable;
import org.jacoco.report.internal.html.index.ElementIndex;
import org.jacoco.report.internal.html.page.TablePage;
import org.jacoco.report.internal.html.resources.Resources;
import org.jacoco.report.internal.html.resources.Styles;
import org.jacoco.report.internal.html.table.BarColumn;
import org.jacoco.report.internal.html.table.ITableItem;
import org.jacoco.report.internal.html.table.LabelColumn;
import org.jacoco.report.internal.html.table.PercentageColumn;
import org.jacoco.report.internal.html.table.Table;

/**
 * Formatter for summary coverage reports in a single HTML page.
 */
public class SummaryHTMLFormatter extends HTMLReportContext<ILinkable> {
	private static final ILinkable NO_SESSIONS_PAGE = new ILinkable() {

		public String getLinkStyle() {
			return null;
		}

		public String getLinkLabel() {
			return "";
		}

		public String getLink(final ReportOutputFolder base) {
			return null;
		}
	};

	@Override
	Table createTable() {
		final Table t = new Table() {

			@Override
			protected void footer(final HTMLElement table,
					final ICoverageNode total,
					final Resources resources, final ReportOutputFolder base)
					throws IOException {
				final HTMLElement tr = table.thead().tr();
				for (final Column c : columns()) {
					c.footer(tr, total, resources, base);
				}
			}
		};
		t.add("Package", null, new LabelColumn() {
			String label;

			@Override
			public boolean init(final List<? extends ITableItem> items,
					final ICoverageNode total) {
				label = "Total " + items.size();
				return super.init(items, total);
			}

			@Override
			public void footer(final HTMLElement td, final ICoverageNode total,
					final Resources resources, final ReportOutputFolder base)
					throws IOException {
				td.text(label);
			}
		}, false);
		t.add("Missed Instructions", Styles.BAR, new BarColumn(INSTRUCTION,
				getLocale()), false);
		t.add("Cov.", Styles.CTR2,
				new PercentageColumn(INSTRUCTION, getLocale()), true);
		t.add("Missed Branches", Styles.BAR, new BarColumn(
				CounterEntity.BRANCH, getLocale()), false);
		t.add("Cov.", Styles.CTR2, new PercentageColumn(CounterEntity.BRANCH,
				getLocale()), false);
		addMissedTotalColumns(t, "CyCo", CounterEntity.COMPLEXITY);
		addMissedTotalColumns(t, "Lines", CounterEntity.LINE);
		addMissedTotalColumns(t, "Methods", CounterEntity.METHOD);
		addMissedTotalColumns(t, "Classes", CounterEntity.CLASS);
		return t;
	}

	/**
	 * Creates a new visitor to write a report to the given output.
	 * 
	 * @param output
	 *            output to write the report to
	 * @return visitor to emit the report data to
	 * @throws IOException
	 *             in case of problems with the output stream
	 */
	public IReportVisitor createVisitor(final IMultiReportOutput output)
			throws IOException {
		final ReportOutputFolder root = new ReportOutputFolder(output);
		setIndexUpdate(new ElementIndex(root));
		setResources(new Resources(root));
		getResources().copyResources();
		setSessionsPage(NO_SESSIONS_PAGE);
		return new IReportVisitor() {

			public void visitBundle(final IBundleCoverage bundle,
					final ISourceFileLocator locator) throws IOException {
				new BundleSummary(bundle, root).render();
			}

			public IReportGroupVisitor visitGroup(final String name)
					throws IOException {
				return null;
			}

			public void visitInfo(final List<SessionInfo> sessionInfos,
					final Collection<ExecutionData> executionData)
					throws IOException {
			}

			public void visitEnd() throws IOException {
				output.close();
			}
		};
	}

	private class BundleSummary extends TablePage<ICoverageNode> {

		private BundleSummary(final IBundleCoverage bundle,
				final ReportOutputFolder folder) {
			super(bundle.getPlainCopy(), null, folder,
					SummaryHTMLFormatter.this);
			for (final IPackageCoverage coverage : bundle.getPackages()) {
				addItem(new PackageSummary(coverage));
			}
		}

		@Override
		protected String getFileName() {
			return "index.html";
		}
	}

	private class PackageSummary implements ITableItem {

		private final IPackageCoverage coverage;

		private PackageSummary(final IPackageCoverage coverage) {
			this.coverage = coverage;
		}

		public String getLink(final ReportOutputFolder base) {
			return null;
		}

		public String getLinkStyle() {
			return null;
		}

		public String getLinkLabel() {
			return getLanguageNames().getPackageName(getNode().getName());
		}

		public ICoverageNode getNode() {
			return coverage;
		}
	}
}
