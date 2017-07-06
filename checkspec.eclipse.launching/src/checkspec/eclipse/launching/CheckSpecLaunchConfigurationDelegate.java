package checkspec.eclipse.launching;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.StringJoiner;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.osgi.framework.Bundle;

import checkspec.eclipse.launching.classpath.ClassPath;

/**
 * Launch configuration delegate for a CheckSpec specification check as a Java application.
 *
 * <p>
 * Clients can instantiate and extend this class.
 * </p>
 * @since 3.3
 */
public class CheckSpecLaunchConfigurationDelegate extends JavaLaunchDelegate {

	@Override
	public String verifyMainTypeName(ILaunchConfiguration configuration) throws CoreException {
		return "checkspec.eclipse.launching.RemoteCheckSpec";
	}

	@Override
	public String getProgramArguments(ILaunchConfiguration configuration) throws CoreException {
		String superArgs = super.getProgramArguments(configuration);

		IJavaProject project = getJavaProject(configuration);

		StringJoiner arguments = new StringJoiner(" ");

		arguments.add("--spec");
		arguments.add(configuration.getAttribute(Constants.ATTR_SPEC_TYPE_NAME, (String) null));

		String implPathString = configuration.getAttribute(Constants.ATTR_IMPL_PATH, (String) null);
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
		if (bundle != null) {
			URL url = bundle.getEntry("/lib/checkspec.bundle.jar");
			File file;
			try {
				file = toFile(url, false);
				classpath = Arrays.copyOf(classpath, classpath.length + 1);
				classpath[classpath.length - 1] = file.getAbsolutePath();
			} catch (URISyntaxException | IOException e) {
			}
			
			url = bundle.getEntry("/");
			try {
				file = toFile(url, true);
				classpath = Arrays.copyOf(classpath, classpath.length + 1);
				classpath[classpath.length - 1] = file.getAbsolutePath();
			} catch (URISyntaxException | IOException expected) {
			}
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
}