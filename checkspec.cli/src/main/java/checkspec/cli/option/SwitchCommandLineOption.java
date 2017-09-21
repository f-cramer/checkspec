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
import org.apache.commons.cli.Option;

import checkspec.cli.CommandLineException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Represents a command line option that can either be set or not.
 *
 * @author Florian Cramer
 *
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SwitchCommandLineOption implements CommandLineOption {

	@NonNull
	private final Option option;

	/**
	 * Returns true if the given option was set in the given command line or
	 * false otherwise.
	 *
	 * @param commandLine
	 *            the command line
	 * @return true if this option was set on the given command line, false
	 *         otherwise
	 * @throws CommandLineException
	 *             if an exception happens while parsing
	 */
	public boolean isSet(CommandLine commandLine) throws CommandLineException {
		return commandLine.hasOption(option.getOpt());
	}

	/**
	 * Creates a new {@link SwitchCommandLineOption} from the given option name.
	 *
	 * @param opt
	 *            the option name
	 * @return a new {@link SwitchCommandLineOption} from the given option name
	 */
	public static SwitchCommandLineOption of(String opt) {
		Option option = Option.builder(opt).build();
		return of(option);
	}

	/**
	 * Creates a new {@link SwitchCommandLineOption} from the given option.
	 *
	 * @param option
	 *            the option
	 * @return a new {@link SwitchCommandLineOption} from the given option
	 */
	public static SwitchCommandLineOption of(Option option) {
		return new SwitchCommandLineOption(option);
	}
}
