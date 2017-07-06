package checkspec.eclipse.launching.classpath;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;

import checkspec.eclipse.launching.Constants;
import checkspec.eclipse.launching.PathConverter;

public class ProjectClassPathEntry implements ClassPathEntry {

	private final IPath projectPath;

	public ProjectClassPathEntry(IPath projectPath) {
		Objects.requireNonNull(projectPath);
		this.projectPath = projectPath;
	}

	public IPath getProjectPath() {
		return projectPath;
	}

	@Override
	public String resolve(IJavaProject project) {
		return resolve(project.getProject().getWorkspace(), projectPath);
	}

	private static String resolve(IWorkspace workspace, IPath projectPath) {
		IProject[] projects = workspace.getRoot().getProjects();
		Optional<IProject> optProject = Arrays.stream(projects)
				.filter(p -> projectPath.equals(p.getFullPath()))
				.findAny();
		if (!optProject.isPresent()) {
			return "";
		}

		IProject project = optProject.get();
		IJavaProject javaProject = project.getAdapter(IJavaProject.class);
		StringJoiner joiner = new StringJoiner(Constants.CLASSPATH_SEPARATOR);
		if (javaProject != null) {
			try {
				IClasspathEntry[] classpath = javaProject.getResolvedClasspath(true);
				for (IClasspathEntry entry : classpath) {
					switch (entry.getEntryKind()) {
					case IClasspathEntry.CPE_PROJECT:
						joiner.add(resolve(workspace, entry.getPath()));
						break;
					case IClasspathEntry.CPE_SOURCE:
						joiner.add(PathConverter.toString(entry.getPath()));
						break;
					}
				}
			} catch (JavaModelException e) {
			}
		}
		return joiner.toString();
	}
}
