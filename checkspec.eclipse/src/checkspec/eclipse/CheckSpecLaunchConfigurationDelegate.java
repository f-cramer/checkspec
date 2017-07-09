package checkspec.eclipse;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.StringJoiner;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.osgi.framework.Bundle;

import checkspec.cli.CommandLineException;
import checkspec.cli.CommandLineInterface;
import checkspec.eclipse.ui.view.ResultView;
import checkspec.eclipse.util.DisplayUtils;
import checkspec.eclipse.util.ReverseURLClassLoader;
import checkspec.eclipse.util.classpath.ClassPath;
import checkspec.report.SpecReport;
import checkspec.report.output.Outputter;
import checkspec.util.ClassUtils;

/**
 * Launch configuration delegate for a CheckSpec specification check as a Java
 * application.
 *
 * <p>
 * Clients can instantiate and extend this class.
 * </p>
 *
 * @since 3.3
 */
public class CheckSpecLaunchConfigurationDelegate extends AbstractJavaLaunchConfigurationDelegate {

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		try {
			CommandLineInterface cli = new CommandLineInterface();
			String[] specificationClassNames = findSpecificationClassNames(configuration);
			URL[] specificationClasspath = findSpecificationClasspath(configuration);
			URL[] implementationClasspath = findImplementationClasspath(configuration);
			String basePackage = findBasePackage(configuration);
			URL[] extensionsClasspath = findExtensionClasspath(configuration);

			ClassLoader baseLoader = getClass().getClassLoader();
			if (extensionsClasspath.length > 0) {
				baseLoader = new ReverseURLClassLoader(baseLoader, extensionsClasspath);
			}
			ClassUtils.setBaseClassLoader(baseLoader);
			SpecReport[] reports = cli.run(specificationClassNames, specificationClasspath, implementationClasspath, basePackage, Outputter.NULL_OUTPUTTER);
			ResultView resultView = DisplayUtils.getWithException(() -> findOpenedResultView());
			DisplayUtils.asyncExec(() -> resultView.setReports(reports));
			ClassUtils.setBaseClassLoader(null);
		} catch (CommandLineException e) {
			throw new CoreException(new Status(Status.ERROR, Constants.PLUGIN_ID, e.getMessage()));
		}
	}

	private String[] findSpecificationClassNames(ILaunchConfiguration configuration) throws CoreException {
		List<String> names = configuration.getAttribute(Constants.ATTR_SPECIFICATION_TYPE_NAMES, Collections.emptyList());
		return names.toArray(new String[names.size()]);
	}

	private URL[] findSpecificationClasspath(ILaunchConfiguration configuration) throws CoreException {
		return ClassPath.from(configuration.getAttribute(Constants.ATTR_SPECIFICATION_CLASSPATH, Collections.emptyList())).resolve(getJavaProject(configuration));
	}

	private URL[] findImplementationClasspath(ILaunchConfiguration configuration) throws CoreException {
		return ClassPath.from(configuration.getAttribute(Constants.ATTR_IMPLEMENTATION_CLASSPATH, Collections.emptyList())).resolve(getJavaProject(configuration));
	}

	private String findBasePackage(ILaunchConfiguration configuration) throws CoreException {
		return configuration.getAttribute(Constants.ATTR_BASE_PACKAGE, "");
	}

	private URL[] findExtensionClasspath(ILaunchConfiguration configuration) throws CoreException {
		return ClassPath.from(configuration.getAttribute(Constants.ATTR_EXTENSION_CLASSPATH, Collections.emptyList())).resolve(getJavaProject(configuration));
	}

	private ResultView findOpenedResultView() throws PartInitException {
		IWorkbenchWindow activeWorkbenchWindow = CheckSpecPlugin.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
		return (ResultView) activePage.showView(ResultView.VIEW_ID);
	}

	@Override
	public String verifyMainTypeName(ILaunchConfiguration configuration) throws CoreException {
		return "checkspec.eclipse.RemoteCheckSpec";
	}

	@Override
	public String getProgramArguments(ILaunchConfiguration configuration) throws CoreException {
		String superArgs = super.getProgramArguments(configuration);

		IJavaProject project = getJavaProject(configuration);

		StringJoiner arguments = new StringJoiner(" ");

		arguments.add("--spec");
		arguments.add(configuration.getAttribute(Constants.ATTR_SPECIFICATION_TYPE_NAMES, (String) null));

		String implPathString = configuration.getAttribute(Constants.ATTR_IMPLEMENTATION_CLASSPATH, (String) null);
		if (implPathString != null) {
			ClassPath implPath = ClassPath.from(implPathString);
			arguments.add("--implpath");
			arguments.add("\"" + implPath.resolve(project) + "\"");
		}

		arguments.add(superArgs);

		return arguments.toString();
	}

	@Override
	public String[] getClasspath(ILaunchConfiguration configuration) throws CoreException {
		String[] classpath = super.getClasspath(configuration);

		Bundle bundle = CheckSpecPlugin.getInstance().getBundle();

		String[] libraries = stream(bundle.getEntryPaths("/lib"))
				.map(bundle::getEntry)
				.map(url -> checkedToFile(url, false))
				.map(file -> {
					try {
						return file.getAbsoluteFile().getCanonicalPath();
					} catch (IOException e) {
						return null;
					}
				})
				.filter(Objects::nonNull)
				.toArray(String[]::new);
		if (libraries.length > 0) {
			int length = classpath.length;
			classpath = Arrays.copyOf(classpath, length + libraries.length);
			System.arraycopy(libraries, 0, classpath, length, libraries.length);
		}

		// URL url = bundle.getEntry("/lib/checkspec.bundle.jar");
		// File file;
		// try {
		// file = toFile(url, false);
		// classpath = Arrays.copyOf(classpath, classpath.length + 1);
		// classpath[classpath.length - 1] = file.getAbsolutePath();
		// } catch (URISyntaxException | IOException e) {
		// }

		URL url = bundle.getEntry("/");
		try {
			File file = toFile(url, true);
			classpath = Arrays.copyOf(classpath, classpath.length + 1);
			classpath[classpath.length - 1] = file.getAbsolutePath();
		} catch (URISyntaxException | IOException expected) {
		}

		return classpath;
	}

	private File toFile(URL url, boolean checkForDevelopmentMode) throws URISyntaxException, IOException {
		File file = URIUtil.toFile(URIUtil.toURI(FileLocator.toFileURL(url))).getAbsoluteFile();
		if (checkForDevelopmentMode && Platform.inDevelopmentMode()) {
			return new File(file, "bin");
		}
		return file;
	}

	private File checkedToFile(URL url, boolean checkForDevelopmentMode) {
		try {
			return toFile(url, checkForDevelopmentMode);
		} catch (URISyntaxException | IOException e) {
			return null;
		}
	}

	private <T> Stream<T> stream(Enumeration<T> e) {
		return StreamSupport.stream(
				Spliterators.spliteratorUnknownSize(
						new Iterator<T>() {
							@Override
							public T next() {
								return e.nextElement();
							}

							@Override
							public boolean hasNext() {
								return e.hasMoreElements();
							}
						},
						Spliterator.ORDERED),
				false);
	}
}