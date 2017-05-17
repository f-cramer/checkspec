package checkspec.report.output.html;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import checkspec.report.ClassReport;
import checkspec.report.ProblemType;
import checkspec.report.Report;
import checkspec.report.ReportProblem;
import checkspec.report.SpecReport;
import checkspec.report.output.Outputter;

public class HtmlOutputter implements Outputter {

	private static final String DIR_IS_NO_DIR = "file %s is not a directory";
	private static final TemplateEngine ENGINE;

	static {
		ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
		resolver.setTemplateMode(TemplateMode.HTML);
		resolver.setPrefix("/checkspec/report/output/template/");
		resolver.setSuffix(".html");

		ENGINE = new TemplateEngine();
		ENGINE.setTemplateResolver(resolver);
	}

	private final Path directory;

	public HtmlOutputter(Path directory) throws IOException {
		if (Files.notExists(directory)) {
			Files.createDirectories(directory);
		}

		if (!Files.isDirectory(directory)) {
			throw new IOException(String.format(DIR_IS_NO_DIR, directory));
		}

		this.directory = directory;
	}

	public HtmlOutputter(String dir) throws IOException {
		this(Paths.get(dir));
	}

	@Override
	public void output(SpecReport report) throws IOException {
		List<Row> rows = getRows(report);

		Context context = new Context();
		context.setVariable("title", report.toString());
		context.setVariable("rows", rows.subList(0, rows.size() - 1));

		Path index = directory.resolve("index.html");
		try (Writer writer = Files.newBufferedWriter(index)) {
			ENGINE.process("index", context, writer);
		}

		copyElements(directory);
	}

	private static void copyElements(Path directory) throws IOException {
		Path style = directory.resolve("style.css");
		InputStream styleStream = HtmlOutputter.class.getResourceAsStream("style.css");
		Files.copy(styleStream, style, StandardCopyOption.REPLACE_EXISTING);
	}

	private static Stream<Row> getRows(Report<?, ?> report) {
		//@formatter:off
		return Stream.concat(
			Stream.of(new Row(0, getMark(report), report.toString())),				
			report.getProblems()
		          .parallelStream()
		          .map(e -> new Row(1, getMark(e), e.toString())));
		//@formatter:on
	}

	private static Stream<Row> getRows(ClassReport report) {
		//@formatter:off
		Stream<Row> head = Stream.of(new Row(0, getMark(report), report.toString()));
		
		Stream<Row> problems = report.getProblems()
		                             .parallelStream()
		                             .map(e -> new Row(1, getMark(e), e.toString()));
		
		Stream<Row> reports = Stream.of(report.getFieldReports(), report.getConstructorReports(), report.getMethodReports())
		                            .parallel()
		                            .flatMap(List::stream)
		                            .flatMap(HtmlOutputter::getRows)
		                            .map(Row::withIncreasedIndent);

		return Stream.concat(Stream.concat(head, Stream.concat(problems, reports)), Stream.of(Row.EMPTY));
		//@formatter:on
	}

	private static List<Row> getRows(SpecReport report) {
		//@formatter:off
		return report.getClassReports()
		             .parallelStream()
		             .map(HtmlOutputter::getRows)
		             .flatMap(Function.identity())
		             .collect(Collectors.toList());
		//@formatter:on
	}

	private static Mark getMark(ReportProblem problem) {
		return getMark(problem.getType().toProblemType());
	}

	private static Mark getMark(Report<?, ?> report) {
		return getMark(report.getType());
	}

	private static Mark getMark(ProblemType type) {
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
