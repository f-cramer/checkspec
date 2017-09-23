package checkspec.type;

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

import java.lang.reflect.Executable;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.util.MatchingState;
import checkspec.util.TypeUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * A {@link MatchableType} that was created from an instance of
 * {@link TypeVariable}.
 *
 * @author Florian Cramer
 *
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class TypeVariableMatchableType extends AbstractMatchableType<TypeVariable<?>, TypeVariableMatchableType> {

	private static final String NOT_FOUND = "Type variable \"%s\" could not be found for generic declaration \"%s\"";

	private final MatchableType genericDeclarationType;
	private final MatchableType[] bounds;
	private final int index;

	TypeVariableMatchableType(final TypeVariable<?> rawType) {
		super(TypeVariableMatchableType.class, rawType);
		this.genericDeclarationType = MatchableType.forClass(getClass(rawType.getGenericDeclaration()));
		this.bounds = Arrays.stream(rawType.getBounds())
				.map(MatchableType::forType)
				.toArray(MatchableType[]::new);
		this.index = getIndex(rawType);
	}

	@Override
	public Optional<MatchingState> matchesImpl(TypeVariableMatchableType type, MultiValuedMap<Class<?>, Class<?>> matches) {
		MatchableType oGenericDeclarationType = type.getGenericDeclarationType();
		return Optional.of(genericDeclarationType.matches(oGenericDeclarationType, matches));
	}

	@Override
	public Class<?> getRawClass() {
		List<Class<?>> rawClasses = Arrays.stream(bounds)
				.map(MatchableType::getRawClass)
				.collect(Collectors.toList());
		return TypeUtils.getMostSpecificCommonSuperType(rawClasses);
	}

	@Override
	public String toString() {
		return rawType.getName();
	}

	private static Class<?> getClass(GenericDeclaration declaration) {
		if (declaration instanceof Class<?>) {
			return (Class<?>) declaration;
		} else if (declaration instanceof Method) {
			return ((Method) declaration).getDeclaringClass();
		} else if (declaration instanceof Executable) {
			return ((Executable) declaration).getDeclaringClass();
		}
		String typeName = declaration == null ? "null" : declaration.getClass().getName();
		throw new IllegalArgumentException("declaration is of unknown type " + typeName);
	}

	private static int getIndex(TypeVariable<?> variable) {
		GenericDeclaration genericDeclaration = variable.getGenericDeclaration();
		TypeVariable<?>[] parameters = genericDeclaration.getTypeParameters();
		return IntStream.range(0, parameters.length)
				.filter(index -> parameters[index] == variable)
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException(String.format(NOT_FOUND, variable, genericDeclaration)));
	}
}
