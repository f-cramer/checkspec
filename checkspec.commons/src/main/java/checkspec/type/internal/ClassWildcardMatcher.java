package checkspec.type.internal;

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
