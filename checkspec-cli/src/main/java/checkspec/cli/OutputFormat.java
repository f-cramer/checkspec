package checkspec.cli;

enum OutputFormat {
	TEXT, HTML, GUI;
	
	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
