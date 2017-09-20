package checkspec.eclipse.util.classpath;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;

import checkspec.eclipse.util.PathConverter;

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
	public List<URL> resolve(IJavaProject project) {
		return resolve(project.getProject().getWorkspace(), projectPath);
	}

	private static List<URL> resolve(IWorkspace workspace, IPath projectPath) {
		IProject[] projects = workspace.getRoot().getProjects();
		Optional<IProject> optProject = Arrays.stream(projects)
				.filter(p -> projectPath.equals(p.getFullPath()))
				.findAny();
		if (!optProject.isPresent()) {
			return Collections.emptyList();
		}

		IProject project = optProject.get();
		IJavaProject javaProject = project.getAdapter(IJavaProject.class);
		List<URL> urls = new ArrayList<>();
		if (javaProject != null) {
			try {
				IClasspathEntry[] classpath = javaProject.getResolvedClasspath(true);
				for (IClasspathEntry entry : classpath) {
					switch (entry.getEntryKind()) {
					case IClasspathEntry.CPE_PROJECT:
						urls.addAll(resolve(workspace, entry.getPath()));
						break;
					case IClasspathEntry.CPE_SOURCE:
						urls.add(PathConverter.toUrl(entry.getPath()));
						break;
					}
				}
			} catch (JavaModelException expected) {
			}
		}
		return urls;
	}

	@Override
	public ClassPathType getType() {
		return ClassPathType.PROJECT;
	}

	@Override
	public String getName(IWorkspace workspace) {
		IProject[] projects = workspace.getRoot().getProjects();
		Optional<IProject> optProject = Arrays.stream(projects)
				.filter(p -> projectPath.equals(p.getFullPath()))
				.findAny();
		if (!optProject.isPresent()) {
			return null;
		}

		IProject project = optProject.get();
		IJavaProject javaProject = project.getAdapter(IJavaProject.class);
		return javaProject.getElementName();
	}
}
