package checkspec.cli;

import static checkspec.util.ClassUtils.classStreamSupplier;

import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
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
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import checkspec.CheckSpec;
import checkspec.cli.option.ArgumentCommandLineOption;
import checkspec.cli.option.CommandLineOption;
import checkspec.cli.option.EnumCommandLineOption;
import checkspec.cli.option.SwitchCommandLineOption;
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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommandLineInterface {

	private static final String JAVA_CLASS_PATH = System.getProperty("java.class.path");
	private static final String PATH_SEPARATOR = System.getProperty("path.separator");

	private static final Option FORMAT_OPTION = Option.builder("f").longOpt("format").hasArg().argName("format")
			.desc("Sets the output format. Available options are \"text\", \"html\" and \"gui\". Default is \"text\".").build();
	private static final ArgumentCommandLineOption<OutputFormat> FORMAT = EnumCommandLineOption.of(FORMAT_OPTION, OutputFormat.TEXT);
	private static final Option OUTPUT_PATH_OPTION = Option.builder("o").longOpt("output").hasArg().argName("path")
			.desc("Sets the output path for formats \"text\" and \"html\". For format \"text\" the default is to output to the standard output stream, for format \"html\""
					+ " it is to output to the directory \"./output/\" which is created if it does not already exist.")
			.build();
	private static final ArgumentCommandLineOption<String> OUTPUT_PATH = TextCommandLineOption.of(OUTPUT_PATH_OPTION, String.class, Parser.IDENTITY);
	private static final Option SPECS_OPTION = Option.builder("s").longOpt("spec").hasArgs().argName("spec-classes")
			.desc("Sets the classes specifications are build from. If not set a specification will be build for any class that is annotated @Spec.").build();
	private static final ArgumentCommandLineOption<String> SPECS = TextCommandLineOption.of(SPECS_OPTION, String.class, Parser.IDENTITY);
	private static final Option SPEC_PATH_OPTION = Option.builder("p").longOpt("specpath").hasArgs().argName("paths")
			.desc("Sets the classpath to load the specifications from. If not set the default classpath is used.").build();
	private static final ArgumentCommandLineOption<URL> SPEC_PATH = TextCommandLineOption.<URL>of(SPEC_PATH_OPTION, URL.class, CommandLineInterface::parseUrl);
	private static final Option IMPLEMENTATION_PATH_OPTION = Option.builder("i").longOpt("implpath").hasArg().argName("paths")
			.desc("Sets the classpath to load the implementations from. If not set the default classpath is used.").build();
	private static final ArgumentCommandLineOption<URL> IMPLEMENTATION_PATH = TextCommandLineOption.<URL>of(IMPLEMENTATION_PATH_OPTION, URL.class, CommandLineInterface::parseUrl);
	private static final Option BASE_PACKAGE_OPTION = Option.builder("b").longOpt("basepackage").hasArg().argName("package")
			.desc("Sets the base package that is used to load implementations. Each found implemenation will be inside of this package or in one of its child packages")
			.build();
	private static final ArgumentCommandLineOption<String> BASE_PACKAGE = TextCommandLineOption.of(BASE_PACKAGE_OPTION, "", Parser.IDENTITY);
	private static final Option HELP_OPTION = Option.builder("h").longOpt("help").desc("Displays this help message.").build();
	private static final SwitchCommandLineOption HELP = SwitchCommandLineOption.of(HELP_OPTION);

	private static final Options OPTIONS = createOptions(FORMAT, SPECS, OUTPUT_PATH, SPEC_PATH, IMPLEMENTATION_PATH, BASE_PACKAGE, HELP);

	private static final CommandLineParser PARSER = new DefaultParser();

	public static void main(String[] args) {
		try {
			parse(args);
		} catch (CommandLineException e) {
			System.err.println(e.getMessage());
		}
	}

	private static void parse(String[] args) throws CommandLineException {
		CommandLine commandLine;
		try {
			commandLine = PARSER.parse(OPTIONS, args);
		} catch (ParseException e) {
			throw wrapException(e);
		}

		if (HELP.isSet(commandLine)) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -jar checkspec-1.0.0-standalone.jar", OPTIONS);
			return;
		}

		OutputFormat format = FORMAT.parse(commandLine);

		Outputter outputter = null;
		if (format == OutputFormat.GUI && GraphicsEnvironment.isHeadless()) {
			throw new CommandLineException("jvm is headless, gui cannot be shown");
		}

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

		URL[] specUrls = parseSpecUrls(commandLine);
		URLClassLoader specClassLoader = new URLClassLoader(specUrls, ClassLoader.getSystemClassLoader());

		Consumer<SpecReport> wrappedOutputter = wrapOutputter(outputter);
		ClassSpecification[] specifications = parseSpecs(commandLine, specUrls, specClassLoader);

		URL[] implementationUrls = parseImplemenationUrls(commandLine);
		CheckSpec checkSpec = CheckSpec.getInstanceForClassPath(implementationUrls);

		String basePackage = parseBasePackage(commandLine);

		Function<ClassSpecification, SpecReport> loader = e -> checkSpec.checkSpec(e, basePackage);

		Arrays.stream(specifications)
				.map(loader)
				.forEach(wrappedOutputter::accept);
	}

	private static Consumer<SpecReport> wrapOutputter(Outputter outputter) {
		return report -> {
			try {
				outputter.output(report);
			} catch (OutputException expected) {
			}
		};
	}

	private static Outputter createTextOutputter(CommandLine commandLine) throws CommandLineException {
		String path = OUTPUT_PATH.parse(commandLine);
		Writer writer;
		if (path == null) {
			writer = new OutputStreamWriter(System.out, StandardCharsets.UTF_8);
		} else {
			try {
				writer = Files.newBufferedWriter(Paths.get(path), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
			} catch (IOException e) {
				throw wrapException(e);
			}
		}

		return new TextOutputter(writer);
	}

	private static Outputter createHtmlOutputter(CommandLine commandLine) throws CommandLineException {
		String path = OUTPUT_PATH.withDefaultValue("report").parse(commandLine);
		try {
			return new HtmlOutputter(Paths.get(path));
		} catch (IOException e) {
			throw wrapException(e);
		}
	}

	private static Outputter createGuiOutputter(CommandLine commandLine) {
		return new GuiOutputter();
	}

	private static URL[] parseSpecUrls(CommandLine commandLine) throws CommandLineException {
		URL[] specPaths = SPEC_PATH.parseMultiple(commandLine);

		if (specPaths.length == 0) {
			return Arrays.stream(JAVA_CLASS_PATH.split(PATH_SEPARATOR)).parallel().map(CommandLineInterface::parseWrappedUrl).flatMap(Wrapper::getWrappedAsStream).toArray(URL[]::new);
		} else {
			return specPaths;
		}
	}

	private static URL[] parseImplemenationUrls(CommandLine commandLine) throws CommandLineException {
		URL[] implementationPaths = IMPLEMENTATION_PATH.parseMultiple(commandLine);

		if (implementationPaths.length == 0) {
			return Arrays.stream(JAVA_CLASS_PATH.split(PATH_SEPARATOR)).parallel()
					.map(CommandLineInterface::parseWrappedUrl)
					.flatMap(Wrapper::getWrappedAsStream)
					.toArray(URL[]::new);
		} else {
			return implementationPaths;
		}
	}

	private static ClassSpecification[] parseSpecs(CommandLine commandLine, URL[] urls, ClassLoader classLoader) throws CommandLineException {
		String[] rawSpecs = SPECS.parseMultiple(commandLine);
		if (rawSpecs.length == 0) {
			return CheckSpec.findSpecifications(urls);
		}

		Function<String, Stream<Class<?>>> loader = classStreamSupplier(classLoader);

		ClassSpecification[] specs = Arrays.stream(rawSpecs).parallel()
				.flatMap(loader)
				.filter(Objects::nonNull)
				.map(ClassSpecification::new)
				.toArray(ClassSpecification[]::new);

		if (specs.length == 0) {
			throw new CommandLineException("No spec was given or none of the given spec classes could be found");
		} else {
			return specs;
		}
	}

	private static String parseBasePackage(CommandLine commandLine) throws CommandLineException {
		return BASE_PACKAGE.parse(commandLine).replaceAll("\\s", "");
	}

	private static Wrapper<URL, CommandLineException> parseWrappedUrl(String uri) {
		try {
			return Wrapper.ofValue(parseUrl(uri));
		} catch (CommandLineException e) {
			return Wrapper.ofException(e);
		}
	}

	private static URL parseUrl(String uriString) throws CommandLineException {
		try {
			try {
				return Paths.get(uriString).toAbsolutePath().normalize().toUri().toURL();
			} catch (InvalidPathException e) {
				return new URI(uriString).toURL();
			}
		} catch (MalformedURLException | URISyntaxException e) {
			throw wrapException(e);
		}
	}

	private static CommandLineException wrapException(Exception ex) {
		return new CommandLineException(ex.getMessage(), ex);
	}

	private static Options createOptions(CommandLineOption... singleOptions) {
		Options options = new Options();

		Arrays.stream(singleOptions).parallel()
				.distinct()
				.map(CommandLineOption::getOption)
				.sorted(Comparator.comparing(Option::getOpt))
				.forEachOrdered(options::addOption);

		return options;
	}
}
