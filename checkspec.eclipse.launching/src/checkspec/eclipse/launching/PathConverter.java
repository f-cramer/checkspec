package checkspec.eclipse.launching;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.URIUtil;

public class PathConverter {

	public static String toString(IPath path) {
		try {
			URL url = path.toFile().getAbsoluteFile().getCanonicalFile().toURI().toURL();
			File file = URIUtil.toFile(URIUtil.toURI(FileLocator.toFileURL(url))).getAbsoluteFile();
			return file.getAbsolutePath();
		} catch (IOException | URISyntaxException e) {
			return null;
		}
	}
}
