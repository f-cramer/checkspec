package checkspec.cli;

/**
 * Represents an output format for the command line.
 *
 * @author Florian Cramer
 *
 */
enum OutputFormat {

	/**
	 * Output via text on {@link System#out} or into a file.
	 */
	TEXT,
	/**
	 * Output via html into a file.
	 */
	HTML,
	/**
	 * Output via gui.
	 */
	GUI;

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
