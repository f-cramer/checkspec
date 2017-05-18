package checkspec.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import checkspec.cli.option.CommandLineOption;
import checkspec.cli.option.EnumCommandLineOption;

public class CommandLineInterface {

	private static final Option FORMAT_OPTION = Option.builder("f").hasArg().build();
	private static final CommandLineOption<OutputFormat> FORMAT = EnumCommandLineOption.of(FORMAT_OPTION, OutputFormat.TEXT);
	private static final Option OUTPUT_PATH = Option.builder("o").hasArg().build();

	private static final Options OPTIONS = new Options().addOption(FORMAT_OPTION).addOption(OUTPUT_PATH);

	private static final CommandLineParser PARSER = new DefaultParser();

	public static void main(String[] args) {
		try {
			CommandLine commands = PARSER.parse(OPTIONS, args);
			OutputFormat format = FORMAT.parse(commands);

			System.out.println(format);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
