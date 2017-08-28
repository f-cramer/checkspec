package checkspec.type;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.lang3.ClassUtils;

import checkspec.util.MatchingState;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
class ClassMatchableType extends AbstractMatchableType<Class<?>> {

	private static final String TYPE_FORMAT = "%s%s";
	private static final String TYPE_VARIABLE_FORMAT = "%s %s";

	private final TypeVariableMatchableType[] typeParameters;
	private final MatchableType componentType;

	public ClassMatchableType(final Class<?> rawType) {
		super(rawType);
		this.typeParameters = Arrays.stream(rawType.getTypeParameters())
				.map(TypeVariableMatchableType::new)
				.toArray(TypeVariableMatchableType[]::new);
		this.componentType = rawType.isArray() ? MatchableType.forClass(rawType.getComponentType()) : null;
	}

	@Override
	public MatchingState matches(MatchableType type, MultiValuedMap<Class<?>, Class<?>> matches) {
		if (equals(type)) {
			return MatchingState.FULL_MATCH;
		}

		if (type instanceof ClassMatchableType) {
			// match class to class, i.e. "String" to "String"

			Class<?> oRawType = ((ClassMatchableType) type).getRawType();
			if (matches != null) {
				// direct match was found
				if (matches.containsMapping(rawType, oRawType)) {
					return MatchingState.FULL_MATCH;
				}
			}

			if (rawType.isArray() && oRawType.isArray()) {
				return componentType.matches(((ClassMatchableType) type).getComponentType(), matches);
			}

			// primitive and its wrapper
			if (ClassUtils.isAssignable(rawType, oRawType) && ClassUtils.isAssignable(oRawType, rawType)) {
				return MatchingState.PARTIAL_MATCH;
			}
		} else if (type instanceof WildcardTypeMatchableType) {
			// match class to wildcard, i.e. "String" to "? extends String"
			WildcardTypeMatchableType oType = (WildcardTypeMatchableType) type;
			MatchingState state = MatchingState.PARTIAL_MATCH;
			state.merge(matches(oType.getUpperBounds(), matches));
			state.merge(matches(oType.getLowerBounds(), matches));
			return state;
		}

		return MatchingState.NO_MATCH;
	}

	private Optional<MatchingState> matches(MatchableType[] bounds, MultiValuedMap<Class<?>, Class<?>> matches) {
		return Arrays.stream(bounds)
				.map(bound -> this.matches(bound, matches))
				.max(Comparator.naturalOrder());
	}

	@Override
	public Class<?> getRawClass() {
		return rawType;
	}

	@Override
	public String toString() {
		String name = rawType.getTypeName();

		if (typeParameters == null || typeParameters.length == 0) {
			return name;
		} else {
			StringJoiner joiner = new StringJoiner(", ", "<", ">");
			Arrays.stream(typeParameters).map(ClassMatchableType::typeVariableAsString).forEach(joiner::add);
			return String.format(TYPE_FORMAT, name, joiner);
		}
	}

	private static String typeVariableAsString(TypeVariableMatchableType type) {
		TypeVariable<?> variable = type.getRawType();
		String name = variable.getName();
		List<String> bounds = Arrays.stream(variable.getBounds())
				.filter(bound -> bound != Object.class)
				.map(ClassMatchableType::boundAsString)
				.collect(Collectors.toList());

		if (bounds.isEmpty()) {
			return name;
		} else {
			StringJoiner joiner = new StringJoiner(" & ", "extends ", "");
			bounds.forEach(joiner::add);
			return String.format(TYPE_VARIABLE_FORMAT, name, joiner);
		}
	}

	private static String boundAsString(Type bound) {
		MatchableType type = MatchableType.forType(bound);
		return type == null ? bound.getTypeName() : type.toString();
	}
}