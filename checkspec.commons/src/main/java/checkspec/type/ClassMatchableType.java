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



import static org.apache.commons.lang3.ClassUtils.*;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.util.MatchingState;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * A {@link MatchableType} that was created from an instance of {@link Class}.
 *
 * @author Florian Cramer
 *
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class ClassMatchableType extends AbstractMatchableType<Class<?>, ClassMatchableType> {

	private static final String TYPE_FORMAT = "%s%s";
	private static final String TYPE_VARIABLE_FORMAT = "%s %s";

	private final TypeVariableMatchableType[] typeParameters;
	private final MatchableType componentType;

	ClassMatchableType(final Class<?> rawType) {
		super(ClassMatchableType.class, rawType);
		this.typeParameters = Arrays.stream(rawType.getTypeParameters())
				.map(TypeVariableMatchableType::new)
				.toArray(TypeVariableMatchableType[]::new);
		this.componentType = rawType.isArray() ? MatchableType.forClass(rawType.getComponentType()) : null;
	}

	@Override
	protected Optional<MatchingState> matchesImpl(ClassMatchableType type, MultiValuedMap<Class<?>, Class<?>> matches) {
		Class<?> oRawType = type.getRawType();
		if (matches != null) {
			if (matches.containsMapping(rawType, oRawType)) {
				return Optional.of(MatchingState.FULL_MATCH);
			}
		}

		if (rawType.isArray() && oRawType.isArray()) {
			return Optional.of(componentType.matches(type.getComponentType(), matches));
		}

		// primitive and its wrapper
		if (isAssignable(rawType, oRawType) && isAssignable(oRawType, rawType)) {
			return Optional.of(MatchingState.PARTIAL_MATCH);
		}

		return Optional.empty();
	}

	@Override
	public Class<?> getRawClass() {
		return rawType;
	}

	@Override
	public String toString() {
		String name = rawType.getTypeName();

		if (typeParameters == null || typeParameters.length == 0) {
			return name;
		} else {
			StringJoiner joiner = new StringJoiner(", ", "<", ">");
			Arrays.stream(typeParameters).map(ClassMatchableType::typeVariableAsString).forEach(joiner::add);
			return String.format(TYPE_FORMAT, name, joiner);
		}
	}

	private static String typeVariableAsString(TypeVariableMatchableType type) {
		TypeVariable<?> variable = type.getRawType();
		String name = variable.getName();
		List<String> bounds = Arrays.stream(variable.getBounds())
				.filter(bound -> bound != Object.class)
				.map(ClassMatchableType::boundAsString)
				.collect(Collectors.toList());

		if (bounds.isEmpty()) {
			return name;
		} else {
			StringJoiner joiner = new StringJoiner(" & ", "extends ", "");
			bounds.forEach(joiner::add);
			return String.format(TYPE_VARIABLE_FORMAT, name, joiner);
		}
	}

	private static String boundAsString(Type bound) {
		MatchableType type = MatchableType.forType(bound);
		return type == null ? bound.getTypeName() : type.toString();
	}
}
