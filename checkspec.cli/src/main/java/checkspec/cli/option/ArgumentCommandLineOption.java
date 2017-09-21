package checkspec.cli.option;

/*-
 * #%L
 * CheckSpec CLI
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
