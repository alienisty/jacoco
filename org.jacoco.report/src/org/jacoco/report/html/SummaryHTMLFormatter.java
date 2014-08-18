/*******************************************************************************
 * Copyright (c) 2009, 2014 Mountainminds GmbH & Co. KG and Contributors
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

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.analysis.IPackageCoverage;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.SessionInfo;
import org.jacoco.report.IMultiReportOutput;
import org.jacoco.report.IReportGroupVisitor;
import org.jacoco.report.IReportVisitor;
import org.jacoco.report.ISourceFileLocator;
import org.jacoco.report.internal.ReportOutputFolder;
import org.jacoco.report.internal.html.ILinkable;
import org.jacoco.report.internal.html.index.ElementIndex;
import org.jacoco.report.internal.html.page.TablePage;
import org.jacoco.report.internal.html.resources.Resources;
import org.jacoco.report.internal.html.table.ITableItem;

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
