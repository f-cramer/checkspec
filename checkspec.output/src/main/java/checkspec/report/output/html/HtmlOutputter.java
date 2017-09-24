package checkspec.report.output.html;

/*-
 * #%L
 * CheckSpec Output
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

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import checkspec.report.ClassReport;
import checkspec.report.Report;
import checkspec.report.ReportProblem;
import checkspec.report.ReportType;
import checkspec.report.SpecReport;
import checkspec.report.output.OutputException;
import checkspec.report.output.Outputter;
import checkspec.specification.ClassSpecification;
import checkspec.type.MatchableType;
import checkspec.util.ClassUtils;

/**
 * Represents an {@link Outputter} that outputs a {@link SpecReport} as HTML to
 * a given directory.
 *
 * @author Florian Cramer
 *
 */
public class HtmlOutputter implements Outputter {

	private static final String REPORT_FORMAT = "%s%n   %s";
	private static final String DIR_IS_NO_DIR = "file %s is not a directory";

	private final Path directory;
	private final List<SpecReport> reports = new LinkedList<>();

	/**
	 * Creates a new {@link HtmlOutputter} that outputs to the given directory.
	 *
	 * @param directory
	 *            the directory
	 * @throws IOException
	 *             if the directory is not a directory or an {@link IOException}
	 *             happens while creating it
	 */
	public HtmlOutputter(Path directory) throws IOException {
		if (!Files.exists(directory)) {
			Files.createDirectories(directory);
		}

		if (!Files.isDirectory(directory)) {
			throw new IOException(String.format(DIR_IS_NO_DIR, directory));
		}

		this.directory = directory;
	}

	@Override
	public void output(SpecReport report) throws OutputException {
		reports.add(report);
		List<Row> rawRows = getRows(report);

		List<Row> rows = rawRows.size() == 0 ? rawRows : rawRows.subList(0, rawRows.size() - 1);

		Path file = directory.resolve(report.getSpecification().getName() + ".html");
		try {
			Files.deleteIfExists(file);
			try (Writer writer = Files.newBufferedWriter(file, StandardOpenOption.CREATE)) {
				createHtmlFile(report.toString(), getLocation(report), rows, writer);
			}
		} catch (IOException e) {
			throw new OutputException(e);
		}
	}

	@Override
	public void finished() throws OutputException {
		Path fileindex = directory.resolve("index.html");
		List<Row> rows = reports.stream()
				.map(SpecReport::getSpecification)
				.map(ClassSpecification::getRawElement)
				.map(MatchableType::getRawClass)
				.map(Class::getName)
				.map(name -> new LinkRow(name, name + ".html"))
				.collect(Collectors.toList());

		try {
			Files.deleteIfExists(fileindex);
			try (Writer writer = Files.newBufferedWriter(fileindex, StandardOpenOption.CREATE)) {
				createHtmlFile("Reports", null, rows, writer);
				copyElements(directory);
			}
		} catch (IOException e) {
			throw new OutputException(e);
		}
	}

	private static void createHtmlFile(String title, String subtitle, List<Row> rows, Writer writer) throws IOException {
		HtmlFile file = new HtmlFile(title, subtitle, rows);
		writer.write(file.toString());
	}

	private static void copyElements(Path directory) throws IOException {
		Path style = directory.resolve("style.css");
		InputStream styleStream = HtmlOutputter.class.getResourceAsStream("style.css");
		Files.copy(styleStream, style, StandardCopyOption.REPLACE_EXISTING);
	}

	private static Stream<Row> getRows(Report<?, ?> report) {
		Stream<Row> header = Stream.of(new Row(0, getMark(report), getTitle(report)));
		Stream<Row> problems = report.getProblems().parallelStream()
				.map(e -> new Row(1, getMark(e), e.toString()));
		Stream<Row> reports = report.getSubReports().parallelStream()
				.flatMap(HtmlOutputter::getRows)
				.map(row -> row.withIncreasedIndent());
		return Stream.concat(header, Stream.concat(problems, reports));
	}

	private static List<Row> getRows(SpecReport report) {
		return report.getClassReports().parallelStream()
				.map(HtmlOutputter::getRows)
				.flatMap(Function.identity())
				.collect(Collectors.toList());
	}

	private static Mark getMark(ReportProblem problem) {
		return getMark(problem.getType().toReportType());
	}

	private static Mark getMark(Report<?, ?> report) {
		return getMark(report.getType());
	}

	private static String getLocation(SpecReport report) {
		Class<?> rawElement = report.getSpecification().getRawElement().getRawClass();
		return ClassUtils.getLocation(rawElement).toString();
	}

	private static String getTitle(Report<?, ?> report) {
		if (report instanceof ClassReport) {
			Class<?> rawElement = ((ClassReport) report).getImplementation().getRawClass();
			return String.format(REPORT_FORMAT, report, ClassUtils.getLocation(rawElement));
		} else {
			return report.toString();
		}
	}

	private static Mark getMark(ReportType type) {
		switch (type) {
		case SUCCESS:
			return Mark.SUCCESS;
		case ERROR:
			return Mark.ERROR;
		case WARNING:
			return Mark.WARNING;
		}

		throw new IllegalArgumentException();
	}
}
