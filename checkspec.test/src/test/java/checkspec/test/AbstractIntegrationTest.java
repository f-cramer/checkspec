package checkspec.test;

/*-
 * #%L
 * CheckSpec Test
 * %%
 * Copyright (C) 2017 Florian Cramer
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import checkspec.CheckSpecRunner;
import checkspec.report.ClassReport;
import checkspec.report.SpecReport;
import checkspec.report.output.OutputException;
import checkspec.report.output.Outputter;

public abstract class AbstractIntegrationTest {

	private final Outputter outputter;

	{
		try {
			// Path path = getFile(getCurrentDirectory().getParentFile(),
			// "target", "html").toPath();
			// outputter = new HtmlOutputter(path);
			outputter = report -> {
			};
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected final SpecReport[] generateReports(String basePackage, String... specificationNames) {
		URL[] specificationClasspath = getSpecificationClasspath();
		URL[] implementationClasspath = getImplementationClasspath();
		SpecReport[] reports = CheckSpecRunner.generateReports(specificationNames, specificationClasspath, implementationClasspath, basePackage);
		Arrays.asList(reports).forEach(this::output);
		try {
			outputter.finished();
		} catch (OutputException expected) {
		}

		return reports;
	}

	protected final SpecReport findReportForSpecificationClass(SpecReport[] reports, String specificationClassName) {
		return Arrays.stream(reports)
				.filter(report -> report.getSpecification().getRawElement().getRawClass().getName().equals(specificationClassName))
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException(specificationClassName));
	}

	protected final List<ClassReport> findClassReportsForSpecificationClass(SpecReport[] reports, String specificationClassName) {
		return findReportForSpecificationClass(reports, specificationClassName).getClassReports();
	}

	protected final String getNameOfBestImplementation(List<ClassReport> classReports) {
		if (classReports.isEmpty()) {
			throw new IllegalArgumentException("given spec report does not contain any implementations");
		}
		return classReports.get(0).getImplementation().getRawClass().getName();
	}

	private final URL[] getSpecificationClasspath() {
		return getClasspath("checkspec.test.specification", "target", "classes");
	}

	private final URL[] getImplementationClasspath() {
		return getClasspath("checkspec.test.implementation", "target", "classes");
	}

	private final URL[] getClasspath(String... children) {
		try {
			return new URL[] {
					getFile(getCurrentDirectory().getParentFile(), children).toURI().toURL()
			};
		} catch (MalformedURLException e) {
			throw new AssertionError();
		}
	}

	private final void output(SpecReport report) {
		try {
			outputter.output(report);
		} catch (OutputException expected) {
		}
	}

	protected static final File getFile(File parent, String... children) {
		File file = parent;
		for (String child : children) {
			file = new File(file, child);
		}
		return file;
	}

	protected static final File getCurrentDirectory() {
		try {
			return new File(".").getCanonicalFile();
		} catch (IOException e) {
			throw new AssertionError();
		}
	}

}
