package checkspec.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import checkspec.api.Visibility;
import checkspec.spring.ResolvableType;
import lombok.NonNull;

public class MethodUtils {

	public static String createString(@NonNull Method method) {
		return toString(method);
	}

	public static String toString(@NonNull Method method) {
		Visibility visibility = getVisibility(method);
		String returnTypeName = getReturnTypeName(method);
		String parameterList = getParameterList(method);
		return String.format("%s %s %s(%s)", visibility, returnTypeName, method.getName(), parameterList);
	}

	public static Visibility getVisibility(@NonNull Method method) {
		return MemberUtils.getVisibility(method.getModifiers());
	}

	public static String getReturnTypeName(@NonNull Method method) {
		return ClassUtils.getName(ResolvableType.forMethodReturnType(method));
	}

	public static String getParameterList(@NonNull Method method) {
		return IntStream.range(0, method.getParameterCount()).parallel()
				.mapToObj(i -> ResolvableType.forMethodParameter(method, i))
				.map(ClassUtils::getName)
				.collect(Collectors.joining(", "));
	}

	public static boolean isAbstract(@NonNull Method method) {
		return Modifier.isAbstract(method.getModifiers());
	}

	public static int calculateParameterDistance(ResolvableType[] left, ResolvableType[] right) {
		if (left == null || right == null) {
			throw new IllegalArgumentException("Strings must not be null");
		}

		/*
		 * This implementation use two variable to record the previous cost
		 * counts, So this implementation use less memory than previous impl.
		 */

		int leftLength = left.length; // length of left
		int rightLength = right.length; // length of right

		if (leftLength == 0) {
			return rightLength;
		} else if (rightLength == 0) {
			return leftLength;
		}

		int[] costs = new int[leftLength + 1];

		// indexes into strings left and right
		int leftIterator; // iterates through left
		int rightIterator; // iterates through right
		int upperLeft;
		int upper;

		ResolvableType rightJ; // jth parameter of right
		int cost; // cost

		for (leftIterator = 0; leftIterator <= leftLength; leftIterator++) {
			costs[leftIterator] = leftIterator;
		}

		for (rightIterator = 1; rightIterator <= rightLength; rightIterator++) {
			upperLeft = costs[0];
			rightJ = right[rightIterator - 1];
			costs[0] = rightIterator;

			for (leftIterator = 1; leftIterator <= leftLength; leftIterator++) {
				upper = costs[leftIterator];
				ResolvableType leftJ = left[leftIterator - 1];
				cost = leftJ.equals(rightJ) ? 0 : leftJ.getRawClass() == rightJ.getRawClass() ? 2 : ClassUtils.isAssignable(leftJ, rightJ) ? 5 : 10;
				// minimum of cell to the left+1, to the top+1, diagonally left
				// and up +cost
				costs[leftIterator] = Math.min(Math.min(costs[leftIterator - 1] + 1, costs[leftIterator] + 1), upperLeft + cost);
				upperLeft = upper;
			}
		}

		return costs[leftLength];
	}
}
