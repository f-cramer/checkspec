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
	public Optional<MatchingState> matchesImpl(WildcardTypeMatchableType type, MultiValuedMap<Class<?>, Class<?>> matches) {
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
