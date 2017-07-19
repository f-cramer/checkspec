package checkspec.util;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import lombok.NonNull;

public class SecurityUtils {

	public static <T> T doPrivileged(@NonNull PrivilegedAction<T> action) {
		return AccessController.doPrivileged(action);
	}

	public static <T> T doPrivilegedWithException(@NonNull PrivilegedExceptionAction<T> action) throws Exception {
		try {
			return AccessController.doPrivileged(action);
		} catch (PrivilegedActionException e) {
			throw e.getException();
		}
	}
}
