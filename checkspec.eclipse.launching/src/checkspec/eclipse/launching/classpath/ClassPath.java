package checkspec.eclipse.launching.classpath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.IJavaProject;

import checkspec.eclipse.launching.Constants;

public class ClassPath {
	
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

	public String resolve(IJavaProject project) {
		Objects.requireNonNull(project);
		return entries.stream()
				.map(e -> e.resolve(project))
				.collect(Collectors.joining(Constants.CLASSPATH_SEPARATOR));
	}

	@Override
	public String toString() {
		return entries.stream()
				.map(ClassPathEntrySerializer::toString)
				.collect(Collectors.joining(ENTRY_SEPARATOR));
	}

	public static ClassPath empty() {
		return new ClassPath(Collections.emptyList());
	}

	public static ClassPath from(String classpath) {
		Objects.requireNonNull(classpath);
		String[] split = classpath.split(ENTRY_SEPARATOR);
		List<ClassPathEntry> entries = Arrays.stream(split)
				.map(ClassPathEntrySerializer::from)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
		return new ClassPath(entries);
	}
}
