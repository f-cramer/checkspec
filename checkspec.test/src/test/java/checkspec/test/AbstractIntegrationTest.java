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
import java.nio.file.Path;
import java.util.Arrays;

import checkspec.CheckSpecRunner;
import checkspec.report.SpecReport;
import checkspec.report.output.OutputException;
import checkspec.report.output.Outputter;
import checkspec.report.output.html.HtmlOutputter;

public abstract class AbstractIntegrationTest {

	private final Outputter outputter;

	{
		try {
			Path path = getFile(getCurrentDirectory().getParentFile(), "target", "html").toPath();
			System.out.println(path);
			outputter = new HtmlOutputter(path);
		} catch (IOException e) {
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
