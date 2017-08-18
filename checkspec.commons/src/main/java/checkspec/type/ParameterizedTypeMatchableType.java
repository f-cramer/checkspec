package checkspec.type;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.util.MatchingState;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
class ParameterizedTypeMatchableType extends AbstractMatchableType<ParameterizedType> {

	private final MatchableType typeWithoutGenerics;
	private final MatchableType[] actualTypeArguments;

	public ParameterizedTypeMatchableType(final ParameterizedType rawType) {
		super(rawType);
		this.typeWithoutGenerics = MatchableType.forType(rawType.getRawType());
		this.actualTypeArguments = Arrays.stream(rawType.getActualTypeArguments())
				.map(MatchableType::forType)
				.toArray(MatchableType[]::new);
	}

	@Override
	public MatchingState matches(MatchableType type, MultiValuedMap<Class<?>, Class<?>> matches) {
		if (equals(type)) {
			return MatchingState.FULL_MATCH;
		}

		MatchingState state = MatchingState.FULL_MATCH;
		if (type instanceof ParameterizedTypeMatchableType) {
			MatchableType oTypeWithoutGenerics = ((ParameterizedTypeMatchableType) type).getTypeWithoutGenerics();
			state = state.merge(typeWithoutGenerics.matches(oTypeWithoutGenerics, matches));
			if (state == MatchingState.NO_MATCH) {
				return MatchingState.NO_MATCH;
			}
			MatchableType[] oActualTypeArguments = ((ParameterizedTypeMatchableType) type).getActualTypeArguments();
			state = state.merge(IntStream.range(0, Math.min(actualTypeArguments.length, oActualTypeArguments.length))
					.mapToObj(i -> actualTypeArguments[i].matches(oActualTypeArguments[i], matches))
					.max(Comparator.naturalOrder()).orElse(MatchingState.FULL_MATCH));
			if (state == MatchingState.NO_MATCH) {
				return MatchingState.NO_MATCH;
			}

			return state;
		}

		return MatchingState.NO_MATCH;
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
