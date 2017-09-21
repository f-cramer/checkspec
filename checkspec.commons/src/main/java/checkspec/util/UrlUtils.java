package checkspec.util;

import java.net.URL;

import lombok.NonNull;

/**
 * Miscellaneous methods that are working on instances of {@link URL}. Mainly
 * for internal use within the framework itself.
 *
 * @author Florian Cramer
 * @see URL
 */
public class UrlUtils {

	/**
	 * Returns whether or not the path of {@code child} starts with the path of
	 * {@code parent}, i.e. if {@code parent} is a parent to {@code child}.
	 *
	 * @param child
	 *            the child
	 * @param parent
	 *            the parent
	 * @return whether or not {@code parent} is parent to {@code child}
	 */
	public static boolean isParent(@NonNull URL child, @NonNull URL parent) {
		return child.getPath().startsWith(parent.getPath());
	}
}
