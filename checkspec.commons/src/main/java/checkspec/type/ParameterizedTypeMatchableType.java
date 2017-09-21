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

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.util.MatchingState;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * A {@link MatchableType} that was created from an instance of
 * {@link ParameterizedType}.
 *
 * @author Florian Cramer
 *
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class ParameterizedTypeMatchableType extends AbstractMatchableType<ParameterizedType, ParameterizedTypeMatchableType> {

	private final MatchableType typeWithoutGenerics;
	private final MatchableType[] actualTypeArguments;

	ParameterizedTypeMatchableType(final ParameterizedType rawType) {
		super(ParameterizedTypeMatchableType.class, rawType);
		this.typeWithoutGenerics = MatchableType.forType(rawType.getRawType());
		this.actualTypeArguments = Arrays.stream(rawType.getActualTypeArguments())
				.map(MatchableType::forType)
				.toArray(MatchableType[]::new);
	}

	@Override
	public Optional<MatchingState> matchesImpl(ParameterizedTypeMatchableType type, MultiValuedMap<Class<?>, Class<?>> matches) {
		MatchingState state = MatchingState.FULL_MATCH;
		MatchableType oTypeWithoutGenerics = type.getTypeWithoutGenerics();
		state = state.merge(typeWithoutGenerics.matches(oTypeWithoutGenerics, matches));
		if (state == MatchingState.NO_MATCH) {
			return Optional.of(MatchingState.NO_MATCH);
		}
		MatchableType[] oActualTypeArguments = type.getActualTypeArguments();
		state = state.merge(matches(actualTypeArguments, oActualTypeArguments, matches));

		return Optional.of(state);
	}

	private static Optional<MatchingState> matches(MatchableType[] arguments, MatchableType[] oArguments, MultiValuedMap<Class<?>, Class<?>> matches) {
		return IntStream.range(0, Math.min(arguments.length, oArguments.length))
				.mapToObj(i -> arguments[i].matches(oArguments[i], matches))
				.max(Comparator.naturalOrder());
	}

	@Override
	public Class<?> getRawClass() {
		return typeWithoutGenerics.getRawClass();
	}

	@Override
	public String toString() {
		String name = rawType.getRawType().getTypeName();
		String arguments = Arrays.stream(rawType.getActualTypeArguments())
				.map(MatchableType::forType)
				.filter(Objects::nonNull)
				.map(Object::toString)
				.collect(Collectors.joining(", "));

		if (arguments.isEmpty()) {
			return name;
		} else {
			return String.format("%s<%s>", name, arguments);
		}
	}
}
