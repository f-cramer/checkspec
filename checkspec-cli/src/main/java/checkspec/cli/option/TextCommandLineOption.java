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

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class TextCommandLineOption<E> implements CommandLineOption<E> {

	@NonNull
	private final Option option;

	@NonNull
	private final Parser<E> parser;

	@NonNull
	private final Class<E> clazz;

	private final E defaultValue;

	@Override
	public E parse(CommandLine commandLine) throws CommandLineException {
		String value = commandLine.getOptionValue(option.getOpt());
		return value == null ? defaultValue : parser.parse(value);
	}

	private Wrapper<E, CommandLineException> parse(String value) {
		try {
			return Wrapper.<E, CommandLineException> ofValue(parser.parse(value));
		} catch (CommandLineException e) {
			return Wrapper.<E, CommandLineException> ofException(e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public E[] parseMultiple(CommandLine commandLine) throws CommandLineException {
		String[] values = commandLine.getOptionValues(option.getOpt());

		if (values == null) {
			return (E[]) Array.newInstance(clazz, 0);
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
				.map(Wrapper::getWrapped)
				.toArray(i -> (E[]) Array.newInstance(clazz, i));
	}

	@Override
	public CommandLineOption<E> withDefaultValue(E defaultValue) {
		return new TextCommandLineOption<E>(option, parser, clazz, defaultValue);
	}

	public static <E> TextCommandLineOption<E> single(@NonNull String opt, @NonNull Class<E> clazz, @NonNull Parser<E> parser) {
		Option option = Option.builder(opt).hasArg().build();
		return new TextCommandLineOption<E>(option, parser, clazz, null);
	}

	@SuppressWarnings("unchecked")
	public static <E> TextCommandLineOption<E> single(@NonNull String opt, @NonNull E defaultValue, @NonNull Parser<E> parser) {
		Option option = Option.builder(opt).hasArg().build();
		return new TextCommandLineOption<E>(option, parser, (Class<E>) defaultValue.getClass(), defaultValue);
	}

	public static <E> TextCommandLineOption<E> multiple(@NonNull String opt, @NonNull Class<E> clazz, @NonNull Parser<E> parser) {
		Option option = Option.builder(opt).hasArgs().build();
		return new TextCommandLineOption<E>(option, parser, clazz, null);
	}

	@SuppressWarnings("unchecked")
	public static <E> TextCommandLineOption<E> multiple(@NonNull String opt, @NonNull E defaultValue, @NonNull Parser<E> parser) {
		Option option = Option.builder(opt).hasArgs().build();
		return new TextCommandLineOption<E>(option, parser, (Class<E>) defaultValue.getClass(), defaultValue);
	}

	public static interface Parser<E> {
		E parse(String value) throws CommandLineException;

		static Parser<String> IDENTITY = e -> e;

		static <E> Parser<E> of(@NonNull Function<String, E> parser) {
			return e -> parser.apply(e);
		}
	}
}
