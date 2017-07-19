package checkspec.eclipse.util.classpath;

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
