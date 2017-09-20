package checkspec.cli.option;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import checkspec.cli.CommandLineException;
import checkspec.util.Wrapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Represents a command line option that has one or more arguments that is
 * parsed using an instance of {@link Parser}.
 *
 * @author Florian Cramer
 *
 * @param <E>
 *            the argument type
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class TextCommandLineOption<E> implements ArgumentCommandLineOption<E> {

	@NonNull
	private final Option option;

	@NonNull
	private final Parser<E> parser;

	@NonNull
	private final Class<E> argumentClass;

	private final E defaultValue;

	@Override
	public E parse(CommandLine commandLine) throws CommandLineException {
		String value = commandLine.getOptionValue(option.getOpt());
		return value == null ? defaultValue : parser.parse(value);
	}

	/**
	 * Parses a single value using the given parser. Returns a wrapper that can
	 * either hold the parsed value or an exception.
	 *
	 * @param value
	 *            the value
	 * @return the parsed value or an exception if one was thrown
	 */
	private Wrapper<E, CommandLineException> parse(String value) {
		try {
			return Wrapper.<E, CommandLineException>ofValue(parser.parse(value));
		} catch (CommandLineException e) {
			return Wrapper.<E, CommandLineException>ofThrowable(e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public E[] parseMultiple(CommandLine commandLine) throws CommandLineException {
		String[] values = commandLine.getOptionValues(option.getOpt());

		if (values == null) {
			return (E[]) Array.newInstance(argumentClass, 0);
		}

		List<Wrapper<E, CommandLineException>> wrapped = Arrays.stream(values).parallel()
				.map(this::parse)
				.collect(Collectors.toList());
		Optional<CommandLineException> exception = wrapped.parallelStream()
				.filter(Wrapper::hasThrowable)
				.map(Wrapper::getThrowable)
				.findFirst();

		if (exception.isPresent()) {
			throw exception.get();
		}

		return wrapped.parallelStream()
				.flatMap(Wrapper::getValueAsStream)
				.toArray(i -> (E[]) Array.newInstance(argumentClass, i));
	}

	@Override
	public TextCommandLineOption<E> withDefaultValue(E defaultValue) {
		return new TextCommandLineOption<E>(option, parser, argumentClass, defaultValue);
	}

	/**
	 * Creates a new {@link TextCommandLineOption} from the given values that
	 * has a single argument and no default value.
	 *
	 * @param opt
	 *            the option name
	 * @param argumentClass
	 *            the argument class
	 * @param parser
	 *            the parser that parses the option
	 * @param <E>
	 *            the argument type
	 * @return a new {@link TextCommandLineOption} from the given arguments
	 */
	public static <E> TextCommandLineOption<E> single(@NonNull String opt, @NonNull Class<E> argumentClass, @NonNull Parser<E> parser) {
		Option option = Option.builder(opt).hasArg().build();
		return of(option, argumentClass, parser);
	}

	/**
	 * Creates a new {@link TextCommandLineOption} from the given values that
	 * has a single argument and the given default value.
	 *
	 * @param opt
	 *            the option name
	 * @param defaultValue
	 *            the default value
	 * @param parser
	 *            the parser that parses the option
	 * @param <E>
	 *            the argument type
	 * @return a new {@link TextCommandLineOption} from the given arguments
	 */
	public static <E> TextCommandLineOption<E> single(@NonNull String opt, @NonNull E defaultValue, @NonNull Parser<E> parser) {
		Option option = Option.builder(opt).hasArg().build();
		return of(option, defaultValue, parser);
	}

	/**
	 * Creates a new {@link TextCommandLineOption} from the given values that
	 * has a more than one argument and no default value.
	 *
	 * @param opt
	 *            the option name
	 * @param argumentClass
	 *            the argument class
	 * @param parser
	 *            the parser that parses the option
	 * @param <E>
	 *            the argument type
	 * @return a new {@link TextCommandLineOption} from the given arguments
	 */
	public static <E> TextCommandLineOption<E> multiple(@NonNull String opt, @NonNull Class<E> argumentClass, @NonNull Parser<E> parser) {
		Option option = Option.builder(opt).hasArgs().build();
		return of(option, argumentClass, parser);
	}

	/**
	 * Creates a new {@link TextCommandLineOption} from the given values that
	 * has more than one argument and the given default value.
	 *
	 * @param opt
	 *            the option name
	 * @param defaultValue
	 *            the default value
	 * @param parser
	 *            the parser that parses the option
	 * @param <E>
	 *            the argument type
	 * @return a new {@link TextCommandLineOption} from the given arguments
	 */
	public static <E> TextCommandLineOption<E> multiple(@NonNull String opt, @NonNull E defaultValue, @NonNull Parser<E> parser) {
		Option option = Option.builder(opt).hasArgs().build();
		return of(option, defaultValue, parser);
	}

	/**
	 * Creates a new {@link TextCommandLineOption} from the given values that
	 * has no default value.
	 *
	 * @param option
	 *            the option
	 * @param argumentClass
	 *            the argument class
	 * @param parser
	 *            the parser that parses the option
	 * @param <E>
	 *            the argument type
	 * @return a new {@link TextCommandLineOption} from the given arguments
	 */
	public static <E> TextCommandLineOption<E> of(@NonNull Option option, @NonNull Class<E> argumentClass, @NonNull Parser<E> parser) {
		return new TextCommandLineOption<E>(option, parser, argumentClass, null);
	}

	/**
	 * Creates a new {@link TextCommandLineOption} from the given values that
	 * has a default value.
	 *
	 * @param option
	 *            the option
	 * @param defaultValue
	 *            the default value
	 * @param parser
	 *            the parser that parses the option
	 * @param <E>
	 *            the argument type
	 * @return a new {@link TextCommandLineOption} from the given arguments
	 */
	@SuppressWarnings("unchecked")
	public static <E> TextCommandLineOption<E> of(@NonNull Option option, @NonNull E defaultValue, @NonNull Parser<E> parser) {
		return new TextCommandLineOption<E>(option, parser, (Class<E>) defaultValue.getClass(), defaultValue);
	}

	/**
	 * Represents a parser that creates an instance of {@code E} from a given
	 * string.
	 *
	 * @author Florian Cramer
	 *
	 * @param <E>
	 *            the argument type
	 */
	public static interface Parser<E> {

		/**
		 * Parses the given value to an instance of {@code E}.
		 *
		 * @param value
		 *            the value
		 * @return the parsed value
		 * @throws CommandLineException
		 *             if an exception is thrown while parsing
		 */
		E parse(String value) throws CommandLineException;

		/**
		 * the default parser that returns the value itself.
		 */
		static Parser<String> IDENTITY = e -> e;

		/**
		 * Creates a new parser from the given function.
		 *
		 * @param parser
		 *            the parsing function
		 * @param <E>
		 *            the argument type
		 * @return the parser
		 */
		static <E> Parser<E> of(@NonNull Function<String, E> parser) {
			return parser::apply;
		}
	}
}
