package checkspec.type;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.util.MatchingState;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ParameterizedTypeMatchableType extends AbstractMatchableType<ParameterizedType, ParameterizedTypeMatchableType> {

	private final MatchableType typeWithoutGenerics;
	private final MatchableType[] actualTypeArguments;

	ParameterizedTypeMatchableType(final ParameterizedType rawType) {
		super(ParameterizedTypeMatchableType.class, rawType);
		this.typeWithoutGenerics = MatchableType.forType(rawType.getRawType());
		this.actualTypeArguments = Arrays.stream(rawType.getActualTypeArguments())
				.map(MatchableType::forType)
				.toArray(MatchableType[]::new);
	}

	@Override
	public Optional<MatchingState> matchesImpl(ParameterizedTypeMatchableType type, MultiValuedMap<Class<?>, Class<?>> matches) {
		MatchingState state = MatchingState.FULL_MATCH;
		MatchableType oTypeWithoutGenerics = type.getTypeWithoutGenerics();
		state = state.merge(typeWithoutGenerics.matches(oTypeWithoutGenerics, matches));
		if (state == MatchingState.NO_MATCH) {
			return Optional.of(MatchingState.NO_MATCH);
		}
		MatchableType[] oActualTypeArguments = type.getActualTypeArguments();
		state = state.merge(matches(actualTypeArguments, oActualTypeArguments, matches));

		return Optional.of(state);
	}

	private static Optional<MatchingState> matches(MatchableType[] arguments, MatchableType[] oArguments, MultiValuedMap<Class<?>, Class<?>> matches) {
		return IntStream.range(0, Math.min(arguments.length, oArguments.length))
				.mapToObj(i -> arguments[i].matches(oArguments[i], matches))
				.max(Comparator.naturalOrder());
	}

	@Override
	public Class<?> getRawClass() {
		return typeWithoutGenerics.getRawClass();
	}

	@Override
	public String toString() {
		String name = rawType.getRawType().getTypeName();
		String arguments = Arrays.stream(rawType.getActualTypeArguments())
				.map(MatchableType::forType)
				.filter(Objects::nonNull)
				.map(Object::toString)
				.collect(Collectors.joining(", "));

		if (arguments.isEmpty()) {
			return name;
		} else {
			return String.format("%s<%s>", name, arguments);
		}
	}
}
