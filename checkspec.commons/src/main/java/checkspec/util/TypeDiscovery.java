package checkspec.util;

/*-
 * #%L
 * CheckSpec Commons
 * %%
 * Copyright (C) 2017 Florian Cramer
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */



import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.reflections.Reflections;

import lombok.experimental.UtilityClass;

/**
 * Miscellaneous methods that can be used to perform type discovery.
 *
 * @author Florian Cramer
 *
 */
@UtilityClass
public final class TypeDiscovery {

	private static Reflections REFLECTIONS = ReflectionsUtils.createReflectionsFromClasspath();
	private static Map<Class<?>, List<Class<?>>> SUB_CLASSES = new HashMap<>();
	private static Map<Class<?>, List<?>> INSTANCES = new HashMap<>();
	private static Comparator<Class<?>> CLASS_COMPARATOR = Comparator.comparing(ClassUtils::getName);

	/**
	 * Sets the {@link Reflections} instance used by this class.
	 *
	 * @param reflections
	 *            the reflections instance
	 */
	public static void setReflections(Reflections reflections) {
		REFLECTIONS = reflections;
	}

	/**
	 * Returns a list of classes that are subtypes of the given class.
	 *
	 * @param type
	 *            the class
	 * @return list of subclasses of {@code type}
	 */
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

	/**
	 * Returns a list of new instances of given class. For any class that can be
	 * found implementing this class exactly one instance will be created.
	 *
	 * @param type
	 *            the class
	 * @param <T>
	 *            the class type
	 * @return list of instances of {@code type}
	 */
	public static <T> List<T> getNewInstancesOf(Class<T> type) {
		return getNewInstancesOf(type, null);
	}

	/**
	 * Returns a list of new instances of given class. For any class that can be
	 * found implementing this class exactly one instance will be created.
	 *
	 * @param type
	 *            the class
	 * @param errorFormat
	 *            the format that is used if an error is thrown
	 * @param <T>
	 *            the class type
	 * @return list of instances of {@code type}
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> getNewInstancesOf(Class<T> type, String errorFormat) {
		return getSubTypesOf(type).stream()
				.map(clazz -> (Class<T>) clazz)
				.flatMap(ClassUtils.instantiate(errorFormat))
				.collect(Collectors.toList());
	}

	/**
	 * Returns a list of new instances of given class. For any class that can be
	 * found implementing this class exactly one instance will be created. The
	 * output of this method is stored in a cache so that the instances can be
	 * reused.
	 *
	 * @param type
	 *            the class
	 * @param <T>
	 *            the class type
	 * @return list of instances of {@code type}
	 */
	public static <T> List<T> getUniqueInstancesOf(Class<T> type) {
		return getUniqueInstancesOf(type, null);
	}

	/**
	 * Returns a list of new instances of given class. For any class that can be
	 * found implementing this class exactly one instance will be created. The
	 * output of this method is stored in a cache so that the instances can be
	 * reused.
	 *
	 * @param type
	 *            the class
	 * @param errorFormat
	 *            the format that is used if an error is thrown
	 * @param <T>
	 *            the class type
	 * @return list of instances of {@code type}
	 */
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
