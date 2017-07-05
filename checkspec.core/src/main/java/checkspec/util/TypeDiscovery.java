package checkspec.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.reflections.Reflections;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class TypeDiscovery {

	private static Reflections REFLECTIONS = ReflectionsUtils.createDefaultReflections();
	private static Map<Class<?>, List<Class<?>>> SUB_CLASSES = new HashMap<>();
	private static Map<Class<?>, List<?>> INSTANCES = new HashMap<>();
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

	public static <T> List<T> getNewInstancesOf(Class<T> type) {
		return getNewInstancesOf(type, null);
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> getNewInstancesOf(Class<T> type, String errorFormat) {
		return getSubTypesOf(type).stream()
				.map(clazz -> (Class<T>) clazz)
				.flatMap(ClassUtils.instantiate(errorFormat))
				.collect(Collectors.toList());
	}

	public static <T> List<T> getUniqueInstancesOf(Class<T> type) {
		return getUniqueInstancesOf(type, null);
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> getUniqueInstancesOf(Class<T> type, String errorFormat) {
		if (INSTANCES.containsKey(type)) {
			return (List<T>) INSTANCES.get(type);
		} else {
			synchronized (INSTANCES) {
				if (INSTANCES.containsKey(type)) {
					return (List<T>) INSTANCES.get(type);
				} else {
					List<T> instances = getNewInstancesOf(type, errorFormat);
					INSTANCES.put(type, instances);
					return instances;
				}
			}
		}
	}
}
