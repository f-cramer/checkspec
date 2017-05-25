package checkspec.cli;

public class CommandLineException extends Exception {

	private static final long serialVersionUID = -2579147946992451321L;

	public CommandLineException() {
	}

	public CommandLineException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CommandLineException(String message, Throwable cause) {
		super(message, cause);
	}

	public CommandLineException(String message) {
		super(message);
	}

	public CommandLineException(Throwable cause) {
		super(cause);
	}
}
