package checkspec.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.api.Visibility;
import checkspec.type.MatchableType;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class MethodUtils {

	private static final String TO_STRING_FORMAT = "%s %s %s(%s)";
	private static final int MAX_COST_PER_STEP = 20;

	public static String toString(@NonNull Method method) {
		String modifiers = Modifier.toString(method.getModifiers());
		String returnTypeName = getReturnTypeName(method);
		String parameterList = getParameterList(method);
		return String.format(TO_STRING_FORMAT, modifiers, returnTypeName, method.getName(), parameterList);
	}

	public static Visibility getVisibility(@NonNull Method method) {
		return MemberUtils.getVisibility(method.getModifiers());
	}

	public static String getReturnTypeName(@NonNull Method method) {
		return ClassUtils.getName(MatchableType.forMethodReturnType(method));
	}

	public static String getParameterList(@NonNull Method method) {
		return IntStream.range(0, method.getParameterCount()).parallel()
				.mapToObj(i -> MatchableType.forMethodParameter(method, i))
				.map(ClassUtils::getName)
				.collect(Collectors.joining(", "));
	}

	public static boolean isAbstract(@NonNull Method method) {
		return Modifier.isAbstract(method.getModifiers());
	}

	public static int calculateParameterDistance(@NonNull MatchableType[] left, @NonNull MatchableType[] right, MultiValuedMap<Class<?>, Class<?>> matches) {
		/*
		 * This implementation use two variable to record the previous cost counts, So
		 * this implementation use less memory than previous impl.
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
