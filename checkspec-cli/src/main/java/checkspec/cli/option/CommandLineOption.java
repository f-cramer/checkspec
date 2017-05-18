package checkspec.cli.option;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

public interface CommandLineOption<E> {

	Option getOption();
	
	E parse(CommandLine commandLine) throws ParseException;
}
