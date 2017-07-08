package checkspec.eclipse.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.URIUtil;

public class PathConverter {

	public static String toString(IPath path) {
		try {
			return URIUtil.toFile(URIUtil.toURI(toUrl(path))).getCanonicalFile().getAbsolutePath();
		} catch (URISyntaxException | IOException e) {
			return null;
		}
	}

	public static URL toUrl(IPath path) {
		try {
			return FileLocator.toFileURL(path.toFile().getAbsoluteFile().getCanonicalFile().toURI().toURL());
		} catch (IOException e) {
			return null;
		}
	}
}
