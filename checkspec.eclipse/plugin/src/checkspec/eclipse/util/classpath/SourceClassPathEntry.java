package checkspec.eclipse.util.classpath;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;

import checkspec.eclipse.util.PathConverter;

public class SourceClassPathEntry implements ClassPathEntry {

	private final IPath path;

	public SourceClassPathEntry(IPath path) {
		Objects.requireNonNull(path, "path");
		this.path = path;
	}

	public IPath getPath() {
		return path;
	}

	@Override
	public List<URL> resolve(IJavaProject project) {
		IPath workspacePath = project.getProject().getWorkspace().getRoot().getLocation();
		return Collections.singletonList(PathConverter.toUrl(workspacePath.append(path)));
	}
}
