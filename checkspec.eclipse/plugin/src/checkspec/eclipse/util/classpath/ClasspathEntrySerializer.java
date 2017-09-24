package checkspec.eclipse.util.classpath;

/*-
 * #%L
 * checkspec.eclipse.plugin
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

import org.eclipse.core.runtime.Path;

public final class ClasspathEntrySerializer {

	private static final String SEPARATOR = "\r";
	private static final String FORMAT = "%s";

	private static final String PROJECT_ID = "PR";
	private static final String PROJECT_FORMAT = PROJECT_ID + SEPARATOR + FORMAT;

	private ClasspathEntrySerializer() {
	}

	public static String toString(ClasspathEntry entry) {
		if (entry instanceof ProjectClasspathEntry) {
			return String.format(PROJECT_FORMAT, ((ProjectClasspathEntry) entry).getProjectPath().toPortableString());
		} else {
			throw new IllegalArgumentException("entry: " + entry);
		}
	}

	public static ClasspathEntry from(String entry) {
		if (entry.startsWith(PROJECT_ID)) {
			String data = getResultingData(entry, PROJECT_ID);
			return new ProjectClasspathEntry(Path.fromPortableString(data));
		} else {
			throw new IllegalArgumentException("entry: " + entry);
		}
	}

	private static String getResultingData(String entry, String prefix) {
		return entry.substring(prefix.length() + SEPARATOR.length());
	}
}
