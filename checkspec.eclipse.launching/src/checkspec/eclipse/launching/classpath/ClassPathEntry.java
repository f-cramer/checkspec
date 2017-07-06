package checkspec.eclipse.launching.classpath;

import org.eclipse.jdt.core.IJavaProject;

public interface ClassPathEntry {

	String resolve(IJavaProject project);
}
