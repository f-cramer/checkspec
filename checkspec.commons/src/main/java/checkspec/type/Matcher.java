package checkspec.type;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.util.MatchingState;

public interface Matcher {

	MatchableTypePair[] getMatchables();

	MatchingState matches(MatchableType left, MatchableType right, MultiValuedMap<Class<?>, Class<?>> matches);
}
