package checkspec.cli.option;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import checkspec.cli.CommandLineException;

public interface CommandLineOption<E> {

	Option getOption();

	E parse(CommandLine commandLine) throws CommandLineException;

	E[] parseMultiple(CommandLine commandLine) throws CommandLineException;

	CommandLineOption<E> withDefaultValue(E defaultValue);
}
