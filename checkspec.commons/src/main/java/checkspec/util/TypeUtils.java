package checkspec.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Miscellaneous methods that are working on instances of
 * {@link java.lang.reflect.Type Type}. Mainly for internal use within the
 * framework itself.
 *
 * @author Florian Cramer
 * @see java.lang.reflect.Type Type
 */
@UtilityClass
public class TypeUtils {

	/**
	 * Returns the most specific common super type for the given types.
	 *
	 * @param classes
	 *            the classes
	 * @return the most specific common super type for the given types
	 */
	public static Class<?> getMostSpecificCommonSuperType(@NonNull Iterable<Class<?>> classes) {
		List<Class<?>> lowestSupers = getMostSpecificCommonSuperTypes(classes);
		return lowestSupers.isEmpty() ? null : lowestSupers.get(0);
	}

	private static List<Class<?>> getMostSpecificCommonSuperTypes(@NonNull Iterable<Class<?>> classes) {
		Collection<Class<?>> commonSupers = getCommonSuperTypes(classes);
		return getLowestTypes(commonSupers);
	}

	private static Set<Class<?>> getCommonSuperTypes(Iterable<Class<?>> classes) {
		Iterator<Class<?>> iterator = classes.iterator();
		if (!iterator.hasNext()) {
			return Collections.emptySet();
		}
		// begin with set from first hierarchy
		Set<Class<?>> result = getSuperTypes(iterator.next());
		// remove non-superclasses of remaining
		while (iterator.hasNext()) {
			Class<?> current = iterator.next();
			Iterator<Class<?>> resultIt = result.iterator();
			while (resultIt.hasNext()) {
				Class<?> sup = resultIt.next();
				if (!sup.isAssignableFrom(current)) {
					resultIt.remove();
				}
			}
		}
		return result;
	}

	private static Set<Class<?>> getSuperTypes(Class<?> clazz) {
		final Set<Class<?>> result = new LinkedHashSet<>();
		final Queue<Class<?>> queue = new ArrayDeque<>();
		queue.add(clazz);
		if (clazz.isInterface()) {
			queue.add(Object.class); // optional
		}
		while (!queue.isEmpty()) {
			Class<?> current = queue.remove();
			if (result.add(current)) {
				Class<?> superclass = current.getSuperclass();
				if (superclass != null) {
					queue.add(superclass);
				}
				queue.addAll(Arrays.asList(current.getInterfaces()));
			}
		}
		return result;
	}

	private static List<Class<?>> getLowestTypes(Collection<Class<?>> classes) {
		final LinkedList<Class<?>> source = new LinkedList<>(classes);
		final ArrayList<Class<?>> result = new ArrayList<>(classes.size());
		while (!source.isEmpty()) {
			Iterator<Class<?>> srcIt = source.iterator();
			Class<?> current = srcIt.next();
			srcIt.remove();
			while (srcIt.hasNext()) {
				Class<?> next = srcIt.next();
				if (next.isAssignableFrom(current)) {
					srcIt.remove();
				} else if (current.isAssignableFrom(next)) {
					current = next;
					srcIt.remove();
				}
			}
			result.add(current);
		}
		result.trimToSize();
		return result;
	}
}
