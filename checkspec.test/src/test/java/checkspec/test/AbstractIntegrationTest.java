package checkspec.test;

/*-
 * #%L
 * CheckSpec Test
 * %%
 * Copyright (C) 2017 Florian Cramer
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class AbstractIntegrationTest {

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
