package checkspec.cli;

import static checkspec.util.ClassUtils.classStreamSupplier;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import checkspec.CheckSpec;
import checkspec.cli.option.CommandLineOption;
import checkspec.cli.option.EnumCommandLineOption;
import checkspec.cli.option.TextCommandLineOption;
import checkspec.cli.option.TextCommandLineOption.Parser;
import checkspec.report.SpecReport;
import checkspec.report.output.OutputException;
import checkspec.report.output.Outputter;
import checkspec.report.output.gui.GuiOutputter;
import checkspec.report.output.html.HtmlOutputter;
import checkspec.report.output.text.TextOutputter;
import checkspec.spec.ClassSpecification;
import checkspec.util.Wrapper;

public final class CommandLineInterface {

	private static final String JAVA_CLASS_PATH = System.getProperty("java.class.path");
	private static final String PATH_SEPARATOR = System.getProperty("path.separator");

	private static final Option FORMAT_OPTION = Option.builder("f").hasArg().build();
	private static final CommandLineOption<OutputFormat> FORMAT = EnumCommandLineOption.of(FORMAT_OPTION, OutputFormat.TEXT);
	private static final Option OUTPUT_PATH_OPTION = Option.builder("o").hasArg().build();
	private static final CommandLineOption<String> OUTPUT_PATH = TextCommandLineOption.of(OUTPUT_PATH_OPTION, String.class, Parser.IDENTITY);
	private static final Option SPECS_OPTION = Option.builder("s").hasArgs().build();
	private static final CommandLineOption<String> SPECS = TextCommandLineOption.of(SPECS_OPTION, String.class, Parser.IDENTITY);
	private static final Option SPEC_PATH_OPTION = Option.builder("p").hasArgs().build();
	private static final CommandLineOption<URL> SPEC_PATH = TextCommandLineOption.<URL> of(SPEC_PATH_OPTION, URL.class, CommandLineInterface::parseURL);
	private static final Option IMPLEMENTATION_PATH_OPTION = Option.builder("i").hasArgs().build();
	private static final CommandLineOption<URL> IMPLEMENTATION_PATH = TextCommandLineOption.<URL> of(IMPLEMENTATION_PATH_OPTION, URL.class, CommandLineInterface::parseURL);
	private static final Option BASE_PACKAGE_OPTION = Option.builder("b").hasArg().build();
	private static final CommandLineOption<String> BASE_PACKAGE = TextCommandLineOption.of(BASE_PACKAGE_OPTION, "", Parser.IDENTITY);

	private static final Options OPTIONS = createOptions(FORMAT_OPTION, SPECS_OPTION, OUTPUT_PATH_OPTION, SPEC_PATH_OPTION, IMPLEMENTATION_PATH_OPTION, BASE_PACKAGE_OPTION);

	private static final CommandLineParser PARSER = new DefaultParser();

	public static void main(String[] args) {
		new CommandLineInterface().parse(args);
	}

	private CommandLineInterface() {
	}

	public void parse(String[] args) {
		try {
			parseThrowingException(args);
		} catch (IOException | CommandLineException | ParseException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	private void parseThrowingException(String[] args) throws IOException, CommandLineException, ParseException {
		CommandLine commandLine = PARSER.parse(OPTIONS, args);
		OutputFormat format = FORMAT.parse(commandLine);

		Outputter outputter = null;
		switch (format) {
		case TEXT:
			outputter = createTextOutputter(commandLine);
			break;
		case HTML:
			outputter = createHtmlOutputter(commandLine);
			break;
		case GUI:
			outputter = createGuiOutputter(commandLine);
			break;
		}
		
		ClassLoader specClassLoader = parseSpecClassLoader(commandLine);

		Consumer<SpecReport> wrappedOutputter = wrap(outputter);
		ClassSpecification[] specifications = parseSpecs(commandLine, specClassLoader);

		URL[] implementationUrls = parseImplemenationURLs(commandLine);
		ClassLoader implementationLoader = new URLClassLoader(implementationUrls, ClassLoader.getSystemClassLoader());
		CheckSpec checkSpec = CheckSpec.getInstanceForClassPath(implementationUrls);
		
		String basePackage = parseBasePackage(commandLine);
		
		Function<ClassSpecification, SpecReport> loader = e -> checkSpec.checkSpec(e, basePackage, implementationLoader);
		
		//@formatter:off
		Arrays.stream(specifications)
		      .parallel()
		      .map(loader)
		      .peek(wrappedOutputter::accept)
		      .toArray(SpecReport[]::new);
		//@formatter:on
	}

	private Consumer<SpecReport> wrap(Outputter outputter) {
		return report -> {
			try {
				outputter.output(report);
			} catch (OutputException e) {
			}
		};
	}

	private Outputter createTextOutputter(CommandLine commandLine) throws IOException, CommandLineException {
		String path = OUTPUT_PATH.parse(commandLine);
		Writer writer;
		if (path == null || "".equals(path.trim())) {
			writer = new OutputStreamWriter(System.out, StandardCharsets.UTF_8);
		} else {
			writer = Files.newBufferedWriter(Paths.get(path), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
		}

		return new TextOutputter(writer);
	}

	private Outputter createHtmlOutputter(CommandLine commandLine) throws IOException, CommandLineException {
		String path = OUTPUT_PATH.withDefaultValue("report").parse(commandLine);
		return new HtmlOutputter(Paths.get(path));
	}

	private Outputter createGuiOutputter(CommandLine commandLine) {
		return new GuiOutputter();
	}

	private ClassLoader parseSpecClassLoader(CommandLine commandLine) throws CommandLineException {
		URL[] urls = SPEC_PATH.parseMultiple(commandLine);
		
		if (urls.length == 0) {
			return ClassLoader.getSystemClassLoader();
		} else {
			return new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
		}
	}

	private URL[] parseImplemenationURLs(CommandLine commandLine) throws CommandLineException {
		URL[] implementationPaths = IMPLEMENTATION_PATH.parseMultiple(commandLine);

		if (implementationPaths.length == 0) {
			//@formatter:off
			return Arrays.stream(JAVA_CLASS_PATH.split(PATH_SEPARATOR)).parallel()
			             .map(CommandLineInterface::parseWrappedURL)
			             .flatMap(Wrapper::getWrappedAsStream)
			             .toArray(URL[]::new);
			//@formatter:on
		} else {
			return implementationPaths;
		}
	}
	
	private ClassSpecification[] parseSpecs(CommandLine commandLine, ClassLoader classLoader) throws CommandLineException {
		Function<String, Stream<Class<?>>> loader = classStreamSupplier(classLoader);
		//@formatter:off
		ClassSpecification[] specs = Arrays.stream(SPECS.parseMultiple(commandLine)).parallel()
		                                   .flatMap(loader::apply)
		                                   .filter(Objects::nonNull)
		                                   .map(ClassSpecification::new)
		                                   .toArray(ClassSpecification[]::new);
		//@formatter:on

		if (specs.length == 0) {
			throw new CommandLineException("No spec was given or none of the given spec classes could be found");
		} else {
			return specs;
		}
	}
	
	private String parseBasePackage(CommandLine commandLine) throws CommandLineException {
		return BASE_PACKAGE.parse(commandLine);
	}
	
	private static Wrapper<URL, CommandLineException> parseWrappedURL(String uri) {
		try {
			return Wrapper.ofValue(parseURL(uri));
		} catch (CommandLineException e) {
			return Wrapper.ofException(e);
		}
	}

	private static URL parseURL(String uriString) throws CommandLineException {
		try {
			return Paths.get(uriString).toAbsolutePath().normalize().toUri().toURL();
		} catch (MalformedURLException e) {
			throw new CommandLineException(e.getMessage(), e);
		}
	}
	
	private static Options createOptions(Option... singleOptions) {
		Options options = new Options();
		Arrays.stream(singleOptions).distinct().sorted(Comparator.comparing(Option::getOpt)).forEachOrdered(options::addOption);
		return options;
	}
}
