package checkspec.eclipse;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.StringJoiner;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.osgi.framework.Bundle;

import checkspec.eclipse.util.classpath.ClassPath;

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
public class CheckSpecLaunchConfigurationDelegate2 extends JavaLaunchDelegate {

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

		Bundle bundle = CheckSpecPlugin.getInstance().getBundle(CheckSpecPlugin.getPluginId());

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