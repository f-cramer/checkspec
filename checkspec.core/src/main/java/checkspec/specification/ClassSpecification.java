package checkspec.specification;

/*-
 * #%L
 * CheckSpec Core
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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import checkspec.api.Spec;
import checkspec.extension.AbstractExtendable;
import checkspec.type.MatchableType;
import checkspec.util.TypeDiscovery;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;

/**
 * A specification for a class.
 *
 * @author Florian Cramer
 *
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class ClassSpecification extends AbstractExtendable<ClassSpecification, MatchableType> implements Specification<MatchableType> {

	private static final ClassSpecificationExtension[] EXTENSIONS;

	static {
		List<ClassSpecificationExtension> instances = TypeDiscovery.getUniqueInstancesOf(ClassSpecificationExtension.class);
		EXTENSIONS = instances.toArray(new ClassSpecificationExtension[instances.size()]);
	}

	@NonNull
	private final String name;

	@NonNull
	private final ModifiersSpecification modifiers;

	@NonNull
	private final VisibilitySpecification visibility;

	@NonNull
	@Getter(AccessLevel.NONE)
	private final PackageSpecification pkg;

	@NonNull
	private final SuperclassSpecification superclass;

	@NonNull
	private final InterfaceSpecification[] interfaces;

	@NonNull
	private final FieldSpecification[] fields;

	@NonNull
	private final MethodSpecification[] methods;

	@NonNull
	private final ConstructorSpecification[] constructors;

	@NonNull
	private final MatchableType rawElement;

	/**
	 * Creates a new {@link ClassSpecification} for the given class.
	 *
	 * @param clazz
	 *            the class
	 */
	public ClassSpecification(Class<?> clazz) {
		rawElement = MatchableType.forClass(clazz);

		name = clazz.getName();
		pkg = new PackageSpecification(clazz.getPackage(), clazz.getAnnotations());
		modifiers = new ModifiersSpecification(clazz.getModifiers(), clazz.getAnnotations());
		visibility = new VisibilitySpecification(clazz.getModifiers(), clazz.getAnnotations());
		superclass = new SuperclassSpecification(rawElement.getSuperType());

		interfaces = Arrays.stream(rawElement.getInterfaces()).parallel()
				.map(InterfaceSpecification::new)
				.toArray(InterfaceSpecification[]::new);

		fields = Arrays.stream(clazz.getDeclaredFields()).parallel()
				.filter(ClassSpecification::isIncluded)
				.map(FieldSpecification::new)
				.toArray(FieldSpecification[]::new);

		constructors = Arrays.stream(clazz.getDeclaredConstructors()).parallel()
				.filter(ClassSpecification::isIncluded)
				.map(ConstructorSpecification::new)
				.toArray(ConstructorSpecification[]::new);

		methods = Arrays.stream(clazz.getDeclaredMethods()).parallel()
				.filter(ClassSpecification::isIncluded)
				.map(MethodSpecification::new)
				.toArray(MethodSpecification[]::new);

		performExtensions(EXTENSIONS, this, rawElement);
	}

	public PackageSpecification getPackage() {
		return pkg;
	}

	/**
	 * Whether or not a report should be generated for the given class.
	 *
	 * @param clazz
	 *            the class
	 * @return whether or not a report should be generated for the class
	 */
	public static boolean shouldBeGenerated(Class<?> clazz) {
		return isIncluded(clazz.getAnnotation(Spec.class));
	}

	private static boolean isIncluded(Field field) {
		return isIncluded(field.getAnnotation(Spec.class));
	}

	private static boolean isIncluded(Method method) {
		return isIncluded(method.getAnnotation(Spec.class));
	}

	private static boolean isIncluded(Constructor<?> constructor) {
		return isIncluded(constructor.getAnnotation(Spec.class));
	}

	private static boolean isIncluded(Spec spec) {
		return spec == null || spec.value();
	}
}
