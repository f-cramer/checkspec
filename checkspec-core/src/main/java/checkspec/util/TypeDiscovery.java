package checkspec.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.reflections.Reflections;

public class TypeDiscovery {

	private static Reflections REFLECTIONS = ReflectionsUtils.createDefaultReflections();
	private static Map<Class<?>, List<Class<?>>> SUB_CLASSES = new HashMap<>();
	private static Comparator<Class<?>> CLASS_COMPARATOR = Comparator.comparing(ClassUtils::getName);

	public static List<Class<?>> getSubTypesOf(Class<?> type) {
		if (SUB_CLASSES.containsKey(type)) {
			return SUB_CLASSES.get(type);
		} else {
			List<Class<?>> subTypes = new ArrayList<>(REFLECTIONS.getSubTypesOf(type));
			Collections.sort(subTypes, CLASS_COMPARATOR);
			SUB_CLASSES.put(type, subTypes);
			return subTypes;
		}
	}

	public static <T> List<T> getInstancesOf(Class<T> type) {
		return getInstancesOf(type, null);
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> getInstancesOf(Class<T> type, String errorFormat) {
		return getSubTypesOf(type).stream()
				.map(clazz -> (Class<T>) clazz)
				.flatMap(ClassUtils.instantiate(errorFormat))
				.collect(Collectors.toList());
	}
}
