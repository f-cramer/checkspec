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



import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.api.Visibility;
import checkspec.type.MatchableType;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Miscellaneous methods that are working on instances of {@link Method}. Mainly
 * for internal use within the framework itself.
 *
 * @author Florian Cramer
 * @see Method
 */
@UtilityClass
public final class MethodUtils {

	private static final String TO_STRING_FORMAT = "%s %s %s(%s)";
	private static final int MAX_COST_PER_STEP = 20;

	/**
	 * Returns a string representation of the given {@link Method}. This looks
	 * exactly like the string you would write to define the given method. E.g.
	 * "public static String createString(Method)" for the this method.
	 * <p>
	 * The modifiers of the given type are sorted using the canonical order
	 * found in {@link java.lang.reflect.Modifier#toString(int)}.
	 *
	 * @param method
	 *            the non-null method
	 * @return the string representation
	 * @throws NullPointerException
	 *             if {@code method} is {@code null}
	 */
	public static String createString(@NonNull Method method) {
		String modifiers = Modifier.toString(method.getModifiers());
		String returnTypeName = getReturnTypeName(method);
		String parameterList = getParameterList(method);
		return String.format(TO_STRING_FORMAT, modifiers, returnTypeName, method.getName(), parameterList);
	}

	/**
	 * Returns the visibility of the given method.
	 *
	 * @param method
	 *            the non-null method
	 * @return the visibility
	 * @see MemberUtils#getVisibility(int)
	 */
	public static Visibility getVisibility(@NonNull Method method) {
		return MemberUtils.getVisibility(method.getModifiers());
	}

	/**
	 * Returns a string representation for the return type of the given method.
	 *
	 * @param method
	 *            the non-null method
	 * @return the string representation
	 * @throws NullPointerException
	 *             if {@code method} is {@code null}
	 * @see ClassUtils#getName(MatchableType)
	 */
	public static String getReturnTypeName(@NonNull Method method) {
		return ClassUtils.getName(MatchableType.forMethodReturnType(method));
	}

	/**
	 * Returns a string representation for the parameters of the given method.
	 *
	 * @param method
	 *            the non-null method
	 * @return the string representation
	 * @throws NullPointerException
	 *             if {@code method} is {@code null}
	 * @see ClassUtils#getName(MatchableType)
	 */
	public static String getParameterList(@NonNull Method method) {
		return IntStream.range(0, method.getParameterCount()).parallel()
				.mapToObj(i -> MatchableType.forMethodParameter(method, i))
				.map(ClassUtils::getName)
				.collect(Collectors.joining(", "));
	}

	/**
	 * Returns whether or not the given method is {@code abstract}.
	 *
	 * @param method
	 *            the non-null method
	 * @return whether or not the given method is {@code abstract}
	 * @throws NullPointerException
	 *             if {@code method} is {@code null}
	 * @see Method#getModifiers()
	 * @see Modifier#isAbstract(int)
	 */
	public static boolean isAbstract(@NonNull Method method) {
		return Modifier.isAbstract(method.getModifiers());
	}

	/**
	 * Calculates the distances between two parameter lists using an adapted
	 * version of the Levenshtein algorithm implemented in the <a href=
	 * "https://commons.apache.org/sandbox/commons-text/apidocs/index.html?org/apache/commons/text/similarity/LevenshteinDistance.html"
	 * > Apache Commons Text library</a>.
	 *
	 * @param left
	 *            the left parameter list
	 * @param right
	 *            the right parameter list
	 * @param matches
	 *            a map of classes that shall be seen as equal
	 * @return the parameter distance
	 */
	public static int calculateParameterDistance(@NonNull MatchableType[] left, @NonNull MatchableType[] right, MultiValuedMap<Class<?>, Class<?>> matches) {
		/*
		 * This implementation use two variable to record the previous cost
		 * counts, So this implementation use less memory than previous impl.
		 */

		int leftLength = left.length; // length of left
		int rightLength = right.length; // length of right

		if (leftLength == 0) {
			return rightLength * MAX_COST_PER_STEP;
		} else if (rightLength == 0) {
			return leftLength * MAX_COST_PER_STEP;
		}

		int[] costs = new int[leftLength + 1];

		// indexes into strings left and right
		int leftIterator; // iterates through left
		int rightIterator; // iterates through right
		int upperLeft;
		int upper;

		MatchableType rightJ; // jth parameter of right
		int cost; // cost

		for (leftIterator = 0; leftIterator <= leftLength; leftIterator++) {
			costs[leftIterator] = leftIterator * MAX_COST_PER_STEP;
		}

		for (rightIterator = 1; rightIterator <= rightLength; rightIterator++) {
			upperLeft = costs[0];
			rightJ = right[rightIterator - 1];
			costs[0] = rightIterator * MAX_COST_PER_STEP;

			for (leftIterator = 1; leftIterator <= leftLength; leftIterator++) {
				upper = costs[leftIterator];
				MatchableType leftJ = left[leftIterator - 1];
				cost = leftJ.matches(rightJ, matches).evaluate(0, 5, 10);
				// minimum of cell to the left+1, to the top+1, diagonally left
				// and up +cost
				costs[leftIterator] = Math.min(Math.min(costs[leftIterator - 1] + 20, costs[leftIterator] + 20), upperLeft + cost);
				upperLeft = upper;
			}
		}

		return costs[leftLength];
	}
}
