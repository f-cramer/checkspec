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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import checkspec.extension.AbstractExtendable;
import checkspec.type.MatchableType;
import checkspec.util.TypeDiscovery;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

/**
 * A specification of a method.
 *
 * @author Florian Cramer
 *
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class MethodSpecification extends AbstractExtendable<MethodSpecification, Method> implements ExecutableSpecification<Method>, Comparable<MethodSpecification> {

	private static final MethodSpecificationExtension[] EXTENSIONS;

	static {
		List<MethodSpecificationExtension> instances = TypeDiscovery.getUniqueInstancesOf(MethodSpecificationExtension.class);
		EXTENSIONS = instances.toArray(new MethodSpecificationExtension[instances.size()]);
	}

	@NonNull
	private final String name;

	@NonNull
	private final MatchableType returnType;

	@NonNull
	private final ParametersSpecification parameters;

	@NonNull
	private final ModifiersSpecification modifiers;

	@NonNull
	private final VisibilitySpecification visibility;

	@NonNull
	private final ExceptionSpecification[] exceptions;

	@NonNull
	private final Method rawElement;

	/**
	 * Creates a new {@link MethodSpecification} from the given method.
	 *
	 * @param method
	 *            the method
	 */
	public MethodSpecification(Method method) {
		name = method.getName();
		returnType = MatchableType.forMethodReturnType(method);

		parameters = new ParametersSpecification(method.getParameters(), index -> MatchableType.forMethodParameter(method, index));
		modifiers = new ModifiersSpecification(method.getModifiers(), method.getAnnotations());
		visibility = new VisibilitySpecification(method.getModifiers(), method.getAnnotations());

		exceptions = Arrays.stream(method.getGenericExceptionTypes())
				.map(MatchableType::forType)
				.map(ExceptionSpecification::new)
				.toArray(ExceptionSpecification[]::new);
		rawElement = method;

		performExtensions(EXTENSIONS, this, method);
	}

	@Override
	public int compareTo(MethodSpecification other) {
		int nameComp = Objects.compare(name, other.name, Comparator.naturalOrder());
		if (nameComp != 0) {
			return nameComp;
		}

		List<ParameterSpecification> parameterSpecifications = parameters.getParameterSpecifications();
		List<ParameterSpecification> otherParameterSpecifications = other.parameters.getParameterSpecifications();
		int length = Math.min(parameterSpecifications.size(), otherParameterSpecifications.size());
		for (int i = 0; i < length; i++) {
			Class<?> thisClass = parameterSpecifications.get(i).getType().getRawClass();
			Class<?> otherClass = otherParameterSpecifications.get(i).getType().getRawClass();

			if (thisClass != otherClass) {
				return thisClass.getName().compareTo(otherClass.getName());
			}
		}

		return Integer.compare(parameterSpecifications.size(), otherParameterSpecifications.size());
	}
}
