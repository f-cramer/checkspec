package checkspec.util;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Miscellaneous methods to work with access control.
 *
 * @author Florian Cramer
 * @see AccessController
 */
@UtilityClass
public class SecurityUtils {

	/**
	 * Wrapper method for
	 * {@link AccessController#doPrivileged(PrivilegedAction)}.
	 *
	 * @param action
	 *            the action
	 * @param <T>
	 *            the result type of {@code action}
	 * @return the result of the action
	 */
	public static <T> T doPrivileged(@NonNull PrivilegedAction<T> action) {
		return AccessController.doPrivileged(action);
	}

	/**
	 * Wrapper method for
	 * {@link AccessController#doPrivileged(PrivilegedExceptionAction)}.
	 *
	 * @param action
	 *            the action
	 * @param <T>
	 *            the result type of {@code action}
	 * @param <E>
	 *            the exception type that is thrown by {@code action}
	 * @return the result of action
	 * @throws E
	 *             if an exception was thrown while executing the {@code action}
	 */
	@SuppressWarnings("unchecked")
	public static <T, E extends Exception> T doPrivilegedWithException(@NonNull PrivilegedActionWithException<T, E> action) throws E {
		try {
			return AccessController.doPrivileged((PrivilegedExceptionAction<T>) (action::doPrivileged));
		} catch (PrivilegedActionException e) {
			throw (E) e.getException();
		}
	}

	/**
	 * Wrapper for {@link PrivilegedExceptionAction}.
	 *
	 * @author Florian Cramer
	 *
	 * @param <T>
	 *            the result type
	 * @param <E>
	 *            the exception type
	 */
	@FunctionalInterface
	public static interface PrivilegedActionWithException<T, E extends Exception> {

		T doPrivileged() throws E;
	}
}
