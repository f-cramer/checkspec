package checkspec.report.output;

public class OutputException extends Exception {

	private static final long serialVersionUID = 3346155355030336320L;

	public OutputException() {
	}

	public OutputException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public OutputException(String message, Throwable cause) {
		super(message, cause);
	}

	public OutputException(String message) {
		super(message);
	}

	public OutputException(Throwable cause) {
		super(cause);
	}
}
