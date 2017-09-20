package checkspec.cli.option;

import org.apache.commons.cli.Option;

/**
 * Represents a command line option.
 *
 * @author Florian Cramer
 *
 */
public interface CommandLineOption {

	/**
	 * Returns the {@link Option} this command line option was created from.
	 *
	 * @return the option this command line option was created from
	 */
	Option getOption();
}
