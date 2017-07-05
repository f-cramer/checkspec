package checkspec.cli.option;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import checkspec.cli.CommandLineException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SwitchCommandLineOption implements CommandLineOption {

	@NonNull
	private final Option option;

	public boolean isSet(CommandLine commandLine) throws CommandLineException {
		return commandLine.hasOption(option.getOpt());
	}

	public static SwitchCommandLineOption of(String opt) {
		Option option = Option.builder(opt).build();
		return of(option);
	}

	public static SwitchCommandLineOption of(Option option) {
		return new SwitchCommandLineOption(option);
	}
}
