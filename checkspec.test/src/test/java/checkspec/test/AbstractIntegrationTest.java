package checkspec.test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import checkspec.CheckSpecRunner;

public abstract class AbstractIntegrationTest {

	protected final CheckSpecRunner runner = new CheckSpecRunner();

	protected final URL[] getSpecClasspath() {
		return getClasspath("checkspec.test.files.results", "target", "classes");
	}

	protected final URL[] getImplementationClasspath() {
		return getClasspath("checkspec.test.files", "target", "classes");
	}

	private final URL[] getClasspath(String... children) {
		try {
			return new URL[] {
					getFile(getCurrentDirectory().getParentFile(), children).toURI().toURL()
			};
		} catch (MalformedURLException e) {
			throw new AssertionError();
		}
	}

	protected final File getFile(File parent, String... children) {
		File file = parent;
		for (String child : children) {
			file = new File(file, child);
		}
		return file;
	}

	protected final File getCurrentDirectory() {
		try {
			return new File(".").getCanonicalFile();
		} catch (IOException e) {
			throw new AssertionError();
		}
	}

}