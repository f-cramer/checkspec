package checkspec.cli;

/**
 * Represents an exception that is thrown if an error happens in the command
 * line interface.
 *
 * @author Florian Cramer
 *
 */
public class CommandLineException extends Exception {

	private static final long serialVersionUID = -2579147946992451321L;

	/**
	 * Creates a new {@link CommandLineException} without any message.
	 */
	public CommandLineException() {
	}

	/**
	 * Creates a new {@link CommandLineException} with the given {@code message}
	 * and {@code cause}.
	 *
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public CommandLineException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Creates a new {@link CommandLineException} with the given
	 * {@code message}.
	 *
	 * @param message
	 *            the message
	 */
	public CommandLineException(String message) {
		super(message);
	}

	/**
	 * Creates a new {@link CommandLineException} with the given {@code cause}.
	 *
	 * @param cause
	 *            the cause
	 */
	public CommandLineException(Throwable cause) {
		super(cause);
	}
}
