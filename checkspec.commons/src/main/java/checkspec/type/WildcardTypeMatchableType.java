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

import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Comparator;
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
 * {@link WildcardType}.
 *
 * @author Florian Cramer
 *
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class WildcardTypeMatchableType extends AbstractMatchableType<WildcardType, WildcardTypeMatchableType> {

	private final MatchableType[] upperBounds;
	private final MatchableType[] lowerBounds;

	WildcardTypeMatchableType(final WildcardType rawType) {
		super(WildcardTypeMatchableType.class, rawType);
		this.upperBounds = Arrays.stream(rawType.getUpperBounds())
				.map(MatchableType::forType)
				.toArray(MatchableType[]::new);
		this.lowerBounds = Arrays.stream(rawType.getLowerBounds())
				.map(MatchableType::forType)
				.toArray(MatchableType[]::new);
	}

	@Override
	protected Optional<MatchingState> matchesImpl(WildcardTypeMatchableType type, MultiValuedMap<Class<?>, Class<?>> matches) {
		// match wildcard to wildcard, i.e. "?" to "?"
		MatchableType[] oUpperBounds = type.getUpperBounds();
		MatchableType[] oLowerBounds = type.getLowerBounds();
		if (upperBounds.length != oUpperBounds.length || lowerBounds.length != oLowerBounds.length) {
			return Optional.of(MatchingState.NO_MATCH);
		}

		MatchingState state = matchBounds(upperBounds, oUpperBounds, matches).orElse(MatchingState.FULL_MATCH);
		if (state == MatchingState.NO_MATCH) {
			return Optional.of(MatchingState.NO_MATCH);
		}

		state = state.merge(matchBounds(lowerBounds, oLowerBounds, matches));
		return Optional.of(state);
	}

	private static Optional<MatchingState> matchBounds(MatchableType[] bounds, MatchableType[] oBounds, MultiValuedMap<Class<?>, Class<?>> matches) {
		return IntStream.range(0, bounds.length)
				.mapToObj(i -> bounds[i].matches(oBounds[i], matches))
				.max(Comparator.naturalOrder());
	}

	@Override
	public Class<?> getRawClass() {
		List<Class<?>> rawClasses = Arrays.stream(upperBounds)
				.map(MatchableType::getRawClass)
				.collect(Collectors.toList());
		return TypeUtils.getMostSpecificCommonSuperType(rawClasses);
	}

	@Override
	public String toString() {
		return rawType.getTypeName();
	}
}
