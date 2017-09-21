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

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.cli.Option;

import checkspec.cli.CommandLineException;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Represents a command line option with one or more arguments that can be
 * parsed to an enum.
 *
 * @author Florian Cramer
 *
 * @param <E>
 *            the argument type
 */
@Getter
public final class EnumCommandLineOption<E extends Enum<E>> extends TextCommandLineOption<E> {

	private static final String CANNOT_PARSE_ENUM = "Cannot parse value \"%s\" for option \"%s\". Available values are %s.";

	/**
	 * Creates a new instance of this class using the given {@link Option},
	 * {@link Class} and default value.
	 *
	 * @param option
	 *            the option
	 * @param clazz
	 *            the class of the enum that is the argument
	 * @param defaultValue
	 *            the default value if the option is not given
	 */
	private EnumCommandLineOption(Option option, Class<E> clazz, E defaultValue) {
		super(option, new EnumParser<>(option, clazz), clazz, defaultValue);
	}

	/**
	 * Represents a parser that parses an enum.
	 *
	 * @author Florian Cramer
	 *
	 * @param <E>
	 *            the enum
	 */
	@RequiredArgsConstructor
	private static class EnumParser<E> implements Parser<E> {

		@NonNull
		private final Option option;

		@NonNull
		private final Class<E> clazz;

		@Override
		public E parse(String value) throws CommandLineException {
			return Arrays.stream(clazz.getEnumConstants()).filter(e -> e.toString().equalsIgnoreCase(value)).findAny().orElseThrow(() -> createException(value));
		}

		/**
		 * Creates an exception from the given value.
		 *
		 * @param value
		 *            the value from which the exception will be created
		 * @return a new exception
		 */
		private CommandLineException createException(String value) {
			String collect = Arrays.stream(clazz.getEnumConstants()).parallel()
					.map(e -> "\"" + e + "\"")
					.collect(Collectors.joining(", "));
			return new CommandLineException(String.format(CANNOT_PARSE_ENUM, value, option.getOpt(), collect));
		}
	}

	@Override
	public EnumCommandLineOption<E> withDefaultValue(E defaultValue) {
		return new EnumCommandLineOption<E>(getOption(), getArgumentClass(), defaultValue);
	}

	/**
	 * Creates a new {@link EnumCommandLineOption} from the given values.
	 *
	 * @param option
	 *            the {@link Option}
	 * @param defaultValue
	 *            the default value if this option is not given
	 * @param <E>
	 *            the argument type
	 * @return a new {@link EnumCommandLineOption}
	 */
	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>> EnumCommandLineOption<E> of(@NonNull Option option, @NonNull E defaultValue) {
		return new EnumCommandLineOption<>(option, (Class<E>) defaultValue.getClass(), defaultValue);
	}

	/**
	 * Creates a new {@link EnumCommandLineOption} from the given values without
	 * a default value.
	 *
	 * @param option
	 *            the {@link Option}
	 * @param argumentClass
	 *            the class of the enum
	 * @param <E>
	 *            the argument type
	 * @return a new {@link EnumCommandLineOption}
	 */
	public static <E extends Enum<E>> EnumCommandLineOption<E> of(@NonNull Option option, @NonNull Class<E> argumentClass) {
		return new EnumCommandLineOption<E>(option, argumentClass, null);
	}

	/**
	 * Creates a new {@link EnumCommandLineOption} from the given values.
	 *
	 * @param opt
	 *            the option name
	 * @param defaultValue
	 *            the default value if this option is not given
	 * @param <E>
	 *            the argument type
	 * @return a new {@link EnumCommandLineOption}
	 */
	public static <E extends Enum<E>> EnumCommandLineOption<E> of(@NonNull String opt, @NonNull E defaultValue) {
		Option option = Option.builder(opt).hasArg().build();
		return of(option, defaultValue);
	}

	/**
	 * Creates a new {@link EnumCommandLineOption} from the given values without
	 * a default value.
	 *
	 * @param opt
	 *            the option name
	 * @param argumentClass
	 *            the class of the enum
	 * @param <E>
	 *            the argument type
	 * @return a new {@link EnumCommandLineOption}
	 */
	public static <E extends Enum<E>> EnumCommandLineOption<E> of(@NonNull String opt, @NonNull Class<E> argumentClass) {
		Option option = Option.builder(opt).hasArg().build();
		return of(option, argumentClass);
	}
}
