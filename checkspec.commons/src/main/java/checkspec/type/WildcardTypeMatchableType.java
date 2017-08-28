package checkspec.type;

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

@Getter
@EqualsAndHashCode(callSuper = true)
class WildcardTypeMatchableType extends AbstractMatchableType<WildcardType> {

	private final MatchableType[] upperBounds;
	private final MatchableType[] lowerBounds;

	public WildcardTypeMatchableType(final WildcardType rawType) {
		super(rawType);
		this.upperBounds = Arrays.stream(rawType.getUpperBounds())
				.map(MatchableType::forType)
				.toArray(MatchableType[]::new);
		this.lowerBounds = Arrays.stream(rawType.getLowerBounds())
				.map(MatchableType::forType)
				.toArray(MatchableType[]::new);
	}

	@Override
	public MatchingState matches(MatchableType type, MultiValuedMap<Class<?>, Class<?>> matches) {
		if (equals(type)) {
			return MatchingState.FULL_MATCH;
		}

		MatchingState state = MatchingState.FULL_MATCH;
		if (type instanceof WildcardTypeMatchableType) {
			// match wildcard to wildcard, i.e. "?" to "?"
			MatchableType[] oUpperBounds = ((WildcardTypeMatchableType) type).getUpperBounds();
			MatchableType[] oLowerBounds = ((WildcardTypeMatchableType) type).getLowerBounds();
			if (upperBounds.length != oUpperBounds.length || lowerBounds.length != oLowerBounds.length) {
				return MatchingState.NO_MATCH;
			}

			state = state.merge(IntStream.range(0, upperBounds.length)
					.mapToObj(i -> upperBounds[i].matches(oUpperBounds[i], matches))
					.max(Comparator.naturalOrder()).orElse(MatchingState.FULL_MATCH));
			if (state == MatchingState.NO_MATCH) {
				return MatchingState.NO_MATCH;
			}

			state = state.merge(IntStream.range(0, lowerBounds.length)
					.mapToObj(i -> upperBounds[i].matches(oUpperBounds[i], matches))
					.max(Comparator.naturalOrder()).orElse(MatchingState.FULL_MATCH));
			return state;
		} else if (type instanceof ClassMatchableType) {
			// to match wildcard to class, i.e. "? extends String" to "String"
			state = state.merge(MatchingState.PARTIAL_MATCH);
			state = state.merge(matchesUpperBounds(type, matches));
			state = state.merge(matches(lowerBounds, type, matches));
			return state;
		}

		return MatchingState.NO_MATCH;
	}

	private static Optional<MatchingState> matches(MatchableType[] bounds, MatchableType type, MultiValuedMap<Class<?>, Class<?>> matches) {
		return Arrays.stream(bounds)
				.map(bound -> bound.matches(type, matches))
				.max(Comparator.naturalOrder());
	}

	private Optional<MatchingState> matchesUpperBounds(MatchableType type, MultiValuedMap<Class<?>, Class<?>> matches) {
		if (upperBounds.length == 1) {
			Class<?> rawClass = upperBounds[0].getRawClass();
			if (rawClass == Object.class) {
				return Optional.of(MatchingState.FULL_MATCH);
			}
		}
		return matches(upperBounds, type, matches);
	}

	@Override
	public Class<?> getRawClass() {
		List<Class<?>> rawClasses = Arrays.stream(upperBounds)
				.map(MatchableType::getRawClass)
				.collect(Collectors.toList());
		return TypeUtils.getLowestCommonSuperType(rawClasses);
	}

	@Override
	public String toString() {
		return rawType.getTypeName();
	}
}
