package checkspec.util;

import java.net.URL;

import lombok.NonNull;

public class UrlUtils {

	public static boolean isParent(@NonNull URL child, @NonNull URL parent) {
		return child.getPath().startsWith(parent.getPath());
	}
}
