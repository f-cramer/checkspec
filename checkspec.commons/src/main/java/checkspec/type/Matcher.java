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



import org.apache.commons.collections4.MultiValuedMap;

import checkspec.util.MatchingState;

/**
 * Represents a matching adapter that integrates into the matching system built
 * by {@link MatchableType}. A matcher is called whenever the implementations of
 * the different {@link MatchableType}s cannot find a matching result.
 *
 * @author Florian Cramer
 *
 */
public interface Matcher {

	/**
	 * The pairs of {@link MatchableType} that can be matched by this matcher.
	 *
	 * @return pairs of {@link MatchableType} that can be matched by
	 *         {@code this}.
	 */
	MatchableTypePair[] getMatchables();

	/**
	 * Returns whether or not given types match each other.
	 *
	 * <p>
	 * One type fully matches another, if the are the same or if the raw classes
	 * of both types are contained in {@code matches}.
	 * </p>
	 * <p>
	 * They match each other partially if they do not match fully but the given
	 * type or one of its matches are assignable to this one.
	 * </p>
	 * <p>
	 * They do not match each other if non of the above conditions matches.
	 * </p>
	 *
	 * @param left
	 *            the first type
	 * @param right
	 *            the second type
	 * @param matches
	 *            a list of matches
	 * @return in which way the given type matches the current one
	 */
	MatchingState matches(MatchableType left, MatchableType right, MultiValuedMap<Class<?>, Class<?>> matches);
}
