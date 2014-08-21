/*******************************************************************************
 * Copyright (c) 2009, 2014 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Alex Nistico - initial API and implementation
 *
 *******************************************************************************/
package org.jacoco.maven;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.jacoco.report.FileMultiReportOutput;
import org.jacoco.report.IReportVisitor;
import org.jacoco.report.MultiReportVisitor;
import org.jacoco.report.html.SummaryHTMLFormatter;

/**
 * Creates a summary code coverage report for tests of a project and its modules
 * in HTML.
 * 
 * @goal summary-report
 * @requiresProject true
 * @aggregator
 * @threadSafe
 * @since 0.7.2
 */
public class SummaryMojo extends AbstractReportMojo {

	/**
	 * Output directory for the reports. Note that this parameter is only
	 * relevant if the goal is run from the command line or from the default
	 * build lifecycle. If the goal is run indirectly as part of a site
	 * generation, the output directory configured in the Maven Site Plugin is
	 * used instead.
	 * 
	 * @parameter default-value="${project.reporting.outputDirectory}/jacoco"
	 */
	private File outputDirectory;

	/**
	 * File with execution data.
	 * 
	 * @parameter default-value="${project.build.directory}/jacoco.exec"
	 */
	private File dataFile;

	@Override
	public boolean canGenerateReport() {
		if ("pom".equals(project.getPackaging())
				&& project.getModules().isEmpty()) {
			// if it is a pom project and it has no modules there would be
			// nothing to report about (at least for coverage)
			getLog().info(
					"Summary report can only be run from a root pom with modules");
			return false;
		}
		return super.canGenerateReport();
	}

	@Override
	public String getOutputName() {
		return "jacoco/index";
	}

	@Override
	public String getName(final Locale locale) {
		return "JaCoCo Test";
	}

	@Override
	public void setReportOutputDirectory(final File reportOutputDirectory) {
		this.outputDirectory = reportOutputDirectory;
	}

	@Override
	protected String getOutputDirectory() {
		return outputDirectory.getAbsolutePath();
	}

	@Override
	File getOutputDirectoryFile() {
		return outputDirectory;
	}

	@Override
	File getDataFile() {
		return dataFile;
	}

	@Override
	File getClassesRoot() {
		return project.getBasedir();
	}

	@Override
	List<String> getExcludes() {
		final ArrayList<String> actualExcludes = new ArrayList<String>(
				super.getExcludes());
		if (!actualExcludes.contains("**/*.jar")) {
			actualExcludes.add("**/*.jar");
		}
		if (!actualExcludes.contains("**/*.war")) {
			actualExcludes.add("**/*.war");
		}
		if (!actualExcludes.contains("**/*.zip")) {
			actualExcludes.add("**/*.zip");
		}
		return actualExcludes;
	}

	@Override
	IReportVisitor createVisitor(final Locale locale) throws IOException {
		final File output = getOutputDirectoryFile();
		output.mkdirs();
		final SummaryHTMLFormatter formatter = new SummaryHTMLFormatter();
		formatter.setOutputEncoding(outputEncoding);
		formatter.setLocale(locale);
		final IReportVisitor visitor = formatter
				.createVisitor(new FileMultiReportOutput(output));

		return new MultiReportVisitor(
				Collections.<IReportVisitor> singletonList(visitor));
	}

	@Override
	List<File> getCompileSourceRoots() {
		return new ArrayList<File>(0);
	}
}
