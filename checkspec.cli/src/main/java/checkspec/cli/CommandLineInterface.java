package checkspec.cli;

import static checkspec.util.SecurityUtils.*;

import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TimeZone;
import java.util.function.Consumer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;

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
import checkspec.specification.ClassSpecification;
import checkspec.util.ClassUtils;
import checkspec.util.ReflectionsUtils;
import checkspec.util.Wrapper;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CommandLineInterface {

	private static final String JAVA_CLASS_PATH = System.getProperty("java.class.path");
	private static final String PATH_SEPARATOR = System.getProperty("path.separator");

	private static final Option FORMAT_OPTION = Option.builder("f").longOpt("format").hasArg().argName("format")
			.desc("Sets the output format. Available options are \"text\", \"html\" and \"gui\". Default is \"text\".").build();
	private static final ArgumentCommandLineOption<OutputFormat> FORMAT = EnumCommandLineOption.of(FORMAT_OPTION, OutputFormat.TEXT);
	private static final Option OUTPUT_PATH_OPTION = Option.builder("o").longOpt("output").hasArg().argName("path")
			.desc("Sets the output path for formats \"text\" and \"html\". For format \"text\" the default is to output to the standard output stream, for format \"html\""
					+ " it is to output to the directory \"./output/\" which is created if it does not already exist.")
			.build();
	private static final Option COLORED_OPTION = Option.builder("c").longOpt("colored").hasArg(false)
			.desc("If set, text output will be colored using ansi coloring").build();
	private static final SwitchCommandLineOption COLORED = SwitchCommandLineOption.of(COLORED_OPTION);
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

	private static final HelpFormatter HELP_FORMATTER = new HelpFormatter();
	private static final String SYNTAX = "java -jar checkspec-1.0.0-standalone.jar";
	private static final Options OPTIONS = createOptions(FORMAT, COLORED, SPECS, OUTPUT_PATH, SPEC_PATH, IMPLEMENTATION_PATH, BASE_PACKAGE, HELP);

	private static final String VERSION;
	private static final ZonedDateTime TIMESTAMP;
	private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
	private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG);
	private static final String WELCOME = "You are using CheckSpec CLI version %s from %s%n";

	static {
		try {
			Class<CommandLineInterface> clazz = CommandLineInterface.class;
			VERSION = IOUtils.toString(clazz.getResourceAsStream("/checkspec/version"), StandardCharsets.UTF_8);

			String timestamp = IOUtils.toString(clazz.getResourceAsStream("/checkspec/timestamp"), StandardCharsets.UTF_8);
			LocalDateTime localDateTime = LocalDateTime.parse(timestamp, INPUT_FORMATTER);
			TIMESTAMP = ZonedDateTime.of(localDateTime, ZoneId.of("UTC"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {
		try {
			printWelcomeMessage();
			new CommandLineInterface().parse(args);
		} catch (CommandLineException e) {
			System.err.println(e.getMessage());
			if (!(e.getCause() instanceof HeadlessException)) {
				HELP_FORMATTER.printHelp(SYNTAX, OPTIONS);
			}
		}
	}

	private static void printWelcomeMessage() {
		System.out.printf(WELCOME, VERSION, OUTPUT_FORMATTER.format(TIMESTAMP));
	}

	protected final SpecReport[] parse(String[] args) throws CommandLineException {
		CommandLine commandLine;
		try {
			commandLine = new DefaultParser().parse(OPTIONS, args);
		} catch (ParseException e) {
			throw wrapException(e);
		}

		if (HELP.isSet(commandLine)) {
			HELP_FORMATTER.printHelp(SYNTAX, OPTIONS);
			return null;
		}

		URL[] specificationClasspath = parseSpecificationClasspath(commandLine);
		String[] specifications = parseSpecificationClassNames(commandLine);
		Outputter outputter = parseOutputter(commandLine);
		URL[] implementationUrls = parseImplemenationUrls(commandLine);
		String basePackage = parseBasePackage(commandLine);

		return run(specifications, specificationClasspath, implementationUrls, basePackage, outputter);
	}

	public final SpecReport[] run(String[] specificationClassNames, URL[] specificationClasspath, URL[] implementationClasspath, String basePackage, Outputter outputter)
			throws CommandLineException {
		ClassLoader specificationClassLoader;
		if (specificationClasspath.length == 0) {
			specificationClassLoader = ClassUtils.getBaseClassLoader();
		} else {
			specificationClassLoader = doPrivileged(() -> new URLClassLoader(specificationClasspath, ClassUtils.getBaseClassLoader()));
		}

		Class<?>[] specificationClasses;
		if (specificationClassNames.length == 0) {
			specificationClasses = ReflectionsUtils.findClassAnnotatedWithEnabledSpec(specificationClasspath, specificationClassLoader);
		} else {
			specificationClasses = Arrays.stream(specificationClassNames)
					.flatMap(ClassUtils.classStreamSupplier(specificationClassLoader))
					.toArray(i -> new Class<?>[i]);
		}

		ClassSpecification[] specifications = Arrays.stream(specificationClasses).parallel()
				.map(ClassSpecification::new)
				.toArray(ClassSpecification[]::new);

		return run(specifications, implementationClasspath, basePackage, outputter);
	}

	protected final SpecReport[] run(ClassSpecification[] specifications, URL[] implementationClasspath, String basePackage, Outputter outputter) throws CommandLineException {
		Consumer<SpecReport> wrappedOutputter = wrapOutputter(outputter);
		CheckSpec checkSpec = implementationClasspath.length == 0 ? CheckSpec.getDefaultInstance() : CheckSpec.getInstanceForClassPath(implementationClasspath);

		return checkSpec.checkSpec(Arrays.asList(specifications), basePackage).stream()
				.peek(wrappedOutputter::accept)
				.toArray(SpecReport[]::new);

	}

	private Consumer<SpecReport> wrapOutputter(Outputter outputter) {
		return report -> {
			try {
				outputter.output(report);
			} catch (OutputException expected) {
			}
		};
	}

	protected Outputter parseOutputter(CommandLine commandLine) throws CommandLineException {
		OutputFormat format = FORMAT.parse(commandLine);
		Outputter outputter = null;
		if (format == OutputFormat.GUI && GraphicsEnvironment.isHeadless()) {
			throw new CommandLineException("jvm is headless, gui cannot be shown", new HeadlessException());
		}

		switch (format) {
		case TEXT:
			boolean colored = COLORED.isSet(commandLine);
			outputter = createTextOutputter(commandLine, colored);
			break;
		case HTML:
			outputter = createHtmlOutputter(commandLine);
			break;
		case GUI:
			outputter = createGuiOutputter(commandLine);
			break;
		}

		return outputter;
	}

	private Outputter createTextOutputter(CommandLine commandLine, boolean colored) throws CommandLineException {
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

		return new TextOutputter(writer, colored);
	}

	private Outputter createHtmlOutputter(CommandLine commandLine) throws CommandLineException {
		String path = OUTPUT_PATH.withDefaultValue("report").parse(commandLine);
		try {
			return new HtmlOutputter(Paths.get(path));
		} catch (IOException e) {
			throw wrapException(e);
		}
	}

	private Outputter createGuiOutputter(CommandLine commandLine) {
		return new GuiOutputter();
	}

	private URL[] parseSpecificationClasspath(CommandLine commandLine) throws CommandLineException {
		URL[] specPaths = SPEC_PATH.parseMultiple(commandLine);

		if (specPaths.length == 0) {
			return Arrays.stream(JAVA_CLASS_PATH.split(PATH_SEPARATOR)).parallel().map(CommandLineInterface::parseWrappedUrl).flatMap(Wrapper::getValueAsStream).toArray(URL[]::new);
		} else {
			return specPaths;
		}
	}

	private URL[] parseImplemenationUrls(CommandLine commandLine) throws CommandLineException {
		return IMPLEMENTATION_PATH.parseMultiple(commandLine);
	}

	private String[] parseSpecificationClassNames(CommandLine commandLine) throws CommandLineException {
		return SPECS.parseMultiple(commandLine);
	}

	private String parseBasePackage(CommandLine commandLine) throws CommandLineException {
		return BASE_PACKAGE.parse(commandLine).replaceAll("\\s", "");
	}

	private static Wrapper<URL, CommandLineException> parseWrappedUrl(String uri) {
		try {
			return Wrapper.ofValue(parseUrl(uri));
		} catch (CommandLineException e) {
			return Wrapper.ofThrowable(e);
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
