package checkspec.eclipse.util.classpath;

import org.eclipse.core.runtime.Path;

public final class ClassPathEntrySerializer {

	private static final String SEPARATOR = "\r";
	private static final String FORMAT = "%s";

	private static final String PROJECT_ID = "PR";
	private static final String PROJECT_FORMAT = PROJECT_ID + SEPARATOR + FORMAT;

	private static final String SOURCE_ID = "SR";
	private static final String SOURCE_FORMAT = SOURCE_ID + SEPARATOR + FORMAT;

	private ClassPathEntrySerializer() {
	}

	public static String toString(ClassPathEntry entry) {
		if (entry instanceof ProjectClassPathEntry) {
			return String.format(PROJECT_FORMAT, ((ProjectClassPathEntry) entry).getProjectPath().toPortableString());
		} else if (entry instanceof SourceClassPathEntry) {
			return String.format(SOURCE_FORMAT, ((SourceClassPathEntry) entry).getPath().toPortableString());
		} else {
			throw new IllegalArgumentException("entry: " + entry);
		}
	}

	public static ClassPathEntry from(String entry) {
		if (entry.startsWith(PROJECT_ID)) {
			String data = getResultingData(entry, PROJECT_ID);
			return new ProjectClassPathEntry(Path.fromPortableString(data));
		} else if (entry.startsWith(SOURCE_ID)) {
			String data = getResultingData(entry, SOURCE_ID);
			return new SourceClassPathEntry(Path.fromPortableString(data));
		} else {
			throw new IllegalArgumentException("entry: " + entry);
		}
	}

	private static String getResultingData(String entry, String prefix) {
		return entry.substring(prefix.length() + SEPARATOR.length());
	}
}
