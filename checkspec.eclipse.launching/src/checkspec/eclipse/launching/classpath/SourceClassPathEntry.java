package checkspec.eclipse.launching.classpath;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;

public class SourceClassPathEntry implements ClassPathEntry {

	private final IPath path;

	public SourceClassPathEntry(IPath path) {
		this.path = path;
	}

	public IPath getPath() {
		return path;
	}

	@Override
	public String resolve(IJavaProject project) {
		IPath workspacePath = project.getProject().getWorkspace().getRoot().getLocation();
		return workspacePath.append(path).toOSString();
	}
}
