package checkspec.eclipse.util.classpath;

import java.net.URL;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;

public interface ClassPathEntry {

	List<URL> resolve(IJavaProject project);
}
