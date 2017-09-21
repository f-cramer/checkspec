package checkspec.type.internal;

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



import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.type.ClassMatchableType;
import checkspec.type.MatchableType;
import checkspec.type.MatchableTypePair;
import checkspec.type.Matcher;
import checkspec.type.WildcardTypeMatchableType;
import checkspec.util.MatchingState;

/**
 * A matcher that matches {@link ClassMatchableType} to
 * {@link WildcardTypeMatchableType}. Matches for example {@code String} to
 * {@code ? extends String}.
 *
 * @author Florian Cramer
 *
 */
public class ClassWildcardMatcher implements Matcher {

	@Override
	public MatchableTypePair[] getMatchables() {
		return new MatchableTypePair[] {
				new MatchableTypePair(ClassMatchableType.class, WildcardTypeMatchableType.class),
				new MatchableTypePair(WildcardTypeMatchableType.class, ClassMatchableType.class),
		};
	}

	@Override
	public MatchingState matches(MatchableType left, MatchableType right, MultiValuedMap<Class<?>, Class<?>> matches) {
		ClassMatchableType clazz;
		WildcardTypeMatchableType wildcard;

		if (left instanceof ClassMatchableType) {
			clazz = (ClassMatchableType) left;
			wildcard = (WildcardTypeMatchableType) right;
		} else {
			clazz = (ClassMatchableType) right;
			wildcard = (WildcardTypeMatchableType) left;
		}

		MatchingState state = MatchingState.PARTIAL_MATCH;
		state.merge(matches(clazz, wildcard.getUpperBounds(), matches));
		state.merge(matches(clazz, wildcard.getLowerBounds(), matches));
		return state;
	}

	private Optional<MatchingState> matches(MatchableType type, MatchableType[] bounds, MultiValuedMap<Class<?>, Class<?>> matches) {
		return Arrays.stream(bounds)
				.map(bound -> type.matches(bound, matches))
				.max(Comparator.naturalOrder());
	}
}
