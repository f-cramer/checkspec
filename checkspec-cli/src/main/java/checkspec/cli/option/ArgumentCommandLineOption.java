package checkspec.cli.option;

import org.apache.commons.cli.CommandLine;

import checkspec.cli.CommandLineException;

public interface ArgumentCommandLineOption<E> extends CommandLineOption {

	E parse(CommandLine commandLine) throws CommandLineException;

	E[] parseMultiple(CommandLine commandLine) throws CommandLineException;

	ArgumentCommandLineOption<E> withDefaultValue(E defaultValue);
}
