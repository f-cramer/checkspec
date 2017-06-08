package checkspec.cli.option;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.cli.Option;

import checkspec.cli.CommandLineException;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
public final class EnumCommandLineOption<E extends Enum<E>> extends TextCommandLineOption<E> {

	private static final String CANNOT_PARSE_ENUM = "Cannot parse value \"%s\" for option \"%s\". Available values are %s.";

	private EnumCommandLineOption(Option option, Class<E> clazz, E defaultValue) {
		super(option, new EnumParser<>(option, clazz), clazz, defaultValue);
	}

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

		private CommandLineException createException(String value) {
			String collect = Arrays.stream(clazz.getEnumConstants()).parallel()
					.map(e -> "\"" + e + "\"")
					.collect(Collectors.joining(", "));
			return new CommandLineException(String.format(CANNOT_PARSE_ENUM, value, option.getOpt(), collect));
		}
	}

	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>> EnumCommandLineOption<E> of(@NonNull String opt, @NonNull E defaultValue) {
		Option option = Option.builder(opt).hasArg().build();
		return new EnumCommandLineOption<>(option, (Class<E>) defaultValue.getClass(), defaultValue);
	}

	public static <E extends Enum<E>> EnumCommandLineOption<E> of(@NonNull String opt, @NonNull Class<E> clazz) {
		Option option = Option.builder(opt).hasArg().build();
		return new EnumCommandLineOption<E>(option, clazz, null);
	}
}
