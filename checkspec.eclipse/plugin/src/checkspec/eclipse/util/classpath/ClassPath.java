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

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.core.IJavaProject;

public final class ClassPath {

	private static final String ENTRY_SEPARATOR = "\n";

	private final List<ClassPathEntry> entries;

	private ClassPath(List<ClassPathEntry> entries) {
		this.entries = new ArrayList<>(entries);
	}

	public void add(ClassPathEntry entry) {
		Objects.requireNonNull(entry);
		entries.add(entry);
	}

	public void remove(ClassPathEntry entry) {
		Objects.requireNonNull(entry);
		entries.remove(entry);
	}

	public URL[] resolve(IJavaProject project) {
		Objects.requireNonNull(project);
		return entries.stream()
				.map(e -> e.resolve(project))
				.flatMap(List::stream)
				.distinct()
				.toArray(URL[]::new);
	}

	@Override
	public String toString() {
		return getEntriesStringStream()
				.collect(Collectors.joining(ENTRY_SEPARATOR));
	}

	public List<String> toStringList() {
		return getEntriesStringStream()
				.collect(Collectors.toList());
	}

	public List<ClassPathEntry> getEntries() {
		return Collections.unmodifiableList(entries);
	}

	private Stream<String> getEntriesStringStream() {
		return entries.stream()
				.map(ClassPathEntrySerializer::toString);
	}

	public static ClassPath empty() {
		return new ClassPath(Collections.emptyList());
	}

	public static ClassPath from(String classpath) {
		Objects.requireNonNull(classpath);
		String[] split = classpath.split(ENTRY_SEPARATOR);
		return from(Arrays.asList(split));
	}

	public static ClassPath from(List<String> entryStrings) {
		Objects.requireNonNull(entryStrings);
		List<ClassPathEntry> entries = entryStrings.stream()
				.map(ClassPathEntrySerializer::from)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
		return new ClassPath(entries);
	}
}
