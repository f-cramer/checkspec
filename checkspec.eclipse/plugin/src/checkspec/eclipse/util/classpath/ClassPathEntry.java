package checkspec.eclipse.util.classpath;

import java.net.URL;
import java.util.List;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.jdt.core.IJavaProject;

public interface ClassPathEntry {

	List<URL> resolve(IJavaProject project);

	ClassPathType getType();

	String getName(IWorkspace workspace);
}
