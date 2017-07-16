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

public class TypeUtils {

	public static Class<?> lowestCommonSuperclass(Iterable<Class<?>> classes) {
		List<Class<?>> lowestSupers = lowestCommonSuperclasses(classes);
		return lowestSupers.isEmpty() ? null : lowestSupers.get(0);
	}

	public static List<Class<?>> lowestCommonSuperclasses(Iterable<Class<?>> classes) {
		Collection<Class<?>> commonSupers = commonSuperclasses(classes);
		return lowestClasses(commonSupers);
	}

	private static List<Class<?>> lowestClasses(Collection<Class<?>> classes) {
		final LinkedList<Class<?>> source = new LinkedList<>(classes);
		final ArrayList<Class<?>> result = new ArrayList<>(classes.size());
		while (!source.isEmpty()) {
			Iterator<Class<?>> srcIt = source.iterator();
			Class<?> c = srcIt.next();
			srcIt.remove();
			while (srcIt.hasNext()) {
				Class<?> c2 = srcIt.next();
				if (c2.isAssignableFrom(c)) {
					srcIt.remove();
				} else if (c.isAssignableFrom(c2)) {
					c = c2;
					srcIt.remove();
				}
			}
			result.add(c);
		}
		result.trimToSize();
		return result;
	}

	private static Set<Class<?>> getSuperclasses(Class<?> clazz) {
		final Set<Class<?>> result = new LinkedHashSet<>();
		final Queue<Class<?>> queue = new ArrayDeque<>();
		queue.add(clazz);
		if (clazz.isInterface()) {
			queue.add(Object.class); // optional
		}
		while (!queue.isEmpty()) {
			Class<?> c = queue.remove();
			if (result.add(c)) {
				Class<?> sup = c.getSuperclass();
				if (sup != null)
					queue.add(sup);
				queue.addAll(Arrays.asList(c.getInterfaces()));
			}
		}
		return result;
	}

	private static Set<Class<?>> commonSuperclasses(Iterable<Class<?>> classes) {
		Iterator<Class<?>> it = classes.iterator();
		if (!it.hasNext()) {
			return Collections.emptySet();
		}
		// begin with set from first hierarchy
		Set<Class<?>> result = getSuperclasses(it.next());
		// remove non-superclasses of remaining
		while (it.hasNext()) {
			Class<?> c = it.next();
			Iterator<Class<?>> resultIt = result.iterator();
			while (resultIt.hasNext()) {
				Class<?> sup = resultIt.next();
				if (!sup.isAssignableFrom(c)) {
					resultIt.remove();
				}
			}
		}
		return result;
	}
}
