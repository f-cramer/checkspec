package checkspec.eclipse;

import static checkspec.CheckSpecRunner.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Stream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.osgi.framework.Bundle;

import checkspec.eclipse.ui.view.ResultView;
import checkspec.eclipse.util.DisplayUtils;
import checkspec.eclipse.util.ReverseUrlClassLoader;
import checkspec.eclipse.util.classpath.Classpath;
import checkspec.report.SpecReport;
import checkspec.util.ClassUtils;
import checkspec.util.ReflectionsUtils;
import checkspec.util.StreamUtils;
import checkspec.util.TypeDiscovery;

/**
 * Launch configuration delegate for a CheckSpec specification check as a Java
 * application.
 */
public class CheckSpecLaunchConfigurationDelegate extends AbstractJavaLaunchConfigurationDelegate {

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		URL[] extensionsClasspath = findExtensionClasspath(configuration);

		ClassLoader baseLoader = getClass().getClassLoader();
		if (extensionsClasspath.length > 0) {
			baseLoader = new ReverseUrlClassLoader(baseLoader, extensionsClasspath);
		}
		ClassUtils.setBaseClassLoader(baseLoader);
		TypeDiscovery.setReflections(ReflectionsUtils.createDefaultReflections());

		String[] specificationClassNames = findSpecificationClassNames(configuration);
		URL[] specificationClasspath = findSpecificationClasspath(configuration);
		URL[] implementationClasspath = findImplementationClasspath(configuration);
		String basePackage = findBasePackage(configuration);

		SpecReport[] reports = generateReports(specificationClassNames, specificationClasspath, implementationClasspath, basePackage);
		ResultView resultView = DisplayUtils.getWithException(() -> findOpenedResultView());
		DisplayUtils.asyncExec(() -> resultView.setReports(reports));
		ClassUtils.setBaseClassLoader(null);
	}

	private String[] findSpecificationClassNames(ILaunchConfiguration configuration) throws CoreException {
		List<String> names = configuration.getAttribute(Constants.ATTR_SPECIFICATION_TYPE_NAMES, Collections.emptyList());
		return names.toArray(new String[names.size()]);
	}

	private URL[] findSpecificationClasspath(ILaunchConfiguration configuration) throws CoreException {
		return Classpath.from(configuration.getAttribute(Constants.ATTR_SPECIFICATION_CLASSPATH, Collections.emptyList())).resolve();
	}

	private URL[] findImplementationClasspath(ILaunchConfiguration configuration) throws CoreException {
		return Classpath.from(configuration.getAttribute(Constants.ATTR_IMPLEMENTATION_CLASSPATH, Collections.emptyList())).resolve();
	}

	private String findBasePackage(ILaunchConfiguration configuration) throws CoreException {
		return configuration.getAttribute(Constants.ATTR_BASE_PACKAGE, "");
	}

	private URL[] findExtensionClasspath(ILaunchConfiguration configuration) throws CoreException {
		return Classpath.from(configuration.getAttribute(Constants.ATTR_EXTENSION_CLASSPATH, Collections.emptyList())).resolve();
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
		StringJoiner arguments = new StringJoiner(" ");

		arguments.add("--spec");
		arguments.add(configuration.getAttribute(Constants.ATTR_SPECIFICATION_TYPE_NAMES, (String) null));

		String implPathString = configuration.getAttribute(Constants.ATTR_IMPLEMENTATION_CLASSPATH, (String) null);
		if (implPathString != null) {
			Classpath implPath = Classpath.from(implPathString);
			arguments.add("--implpath");
			arguments.add("\"" + implPath.resolve() + "\"");
		}

		String superArgs = super.getProgramArguments(configuration);
		arguments.add(superArgs);

		return arguments.toString();
	}

	@Override
	public String[] getClasspath(ILaunchConfiguration configuration) throws CoreException {
		String[] classpath = super.getClasspath(configuration);

		Bundle bundle = CheckSpecPlugin.getInstance().getBundle();

		String[] libraries = StreamUtils.stream(bundle.getEntryPaths("/lib"))
				.map(bundle::getEntry)
				.flatMap(url -> toFilePath(url, false))
				.toArray(String[]::new);
		if (libraries.length > 0) {
			int length = classpath.length;
			classpath = Arrays.copyOf(classpath, length + libraries.length);
			System.arraycopy(libraries, 0, classpath, length, libraries.length);
		}

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

	private Stream<String> toFilePath(URL url, boolean checkForDevelopmentMode) {
		try {
			return Stream.of(toFile(url, checkForDevelopmentMode).getCanonicalPath());
		} catch (URISyntaxException | IOException e) {
			return Stream.empty();
		}
	}
}
