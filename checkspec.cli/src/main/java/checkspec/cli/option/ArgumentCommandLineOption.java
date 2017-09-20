package checkspec.cli.option;

import org.apache.commons.cli.CommandLine;

import checkspec.cli.CommandLineException;

/**
 * Represents an option of the command line interface that has either a single
 * or multiple arguments.
 *
 * @author Florian Cramer
 *
 * @param <E>
 *            return type of the option
 */
public interface ArgumentCommandLineOption<E> extends CommandLineOption {

	/**
	 * Parses the argument for this option from the given command line string.
	 *
	 * @param commandLine
	 *            the command line string
	 * @return the argument
	 * @throws CommandLineException
	 *             if an exception occurs while parsing
	 */
	E parse(CommandLine commandLine) throws CommandLineException;

	/**
	 * Parses the arguments for this option from the given command line string.
	 *
	 * @param commandLine
	 *            the command line string
	 * @return the arguments
	 * @throws CommandLineException
	 *             if an exception occurs while parsing
	 */
	E[] parseMultiple(CommandLine commandLine) throws CommandLineException;

	/**
	 * Returns a new {@link ArgumentCommandLineOption} with the same name and
	 * argument count but the given default value.
	 *
	 * @param defaultValue
	 *            the default value if this option is not given
	 * @return a new instance with this given default value
	 */
	ArgumentCommandLineOption<E> withDefaultValue(E defaultValue);
}
