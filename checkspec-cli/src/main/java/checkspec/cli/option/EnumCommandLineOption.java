package checkspec.cli.option;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class EnumCommandLineOption<E extends Enum<E>> implements CommandLineOption<E> {

	private static final String CANNOT_PARSE_ENUM = "Cannot parse value \"%s\" for option \"%s\". Available values are %s.";
	
	@NonNull
	private final Option option;

	@NonNull
	private final Class<E> clazz;

	private final E defaultValue;
	
	@Override
	public E parse(CommandLine commandLine) throws ParseException {
		String value = commandLine.getOptionValue(option.getOpt(), defaultValue == null ? null : defaultValue.toString());
		
		return Arrays.stream(clazz.getEnumConstants())
		             .filter(e -> e.toString().equalsIgnoreCase(value))
		             .findAny()
		             .orElseThrow(() -> createException(value));
	}
	

	private ParseException createException(String value) {
		return new ParseException(String.format(CANNOT_PARSE_ENUM, value, option.getOpt(), Arrays.stream(clazz.getEnumConstants()).map(e -> "\"" + e + "\"").collect(Collectors.joining(", "))));
	}

	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>> EnumCommandLineOption<E> of(Option option, @NonNull E defaultValue) {
		return new EnumCommandLineOption<E>(option, (Class<E>) defaultValue.getClass(), defaultValue);
	}
}
