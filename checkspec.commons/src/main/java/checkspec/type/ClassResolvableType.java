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
class ClassResolvableType extends AbstractResolvableType<Class<?>> {

	private static final String TYPE_FORMAT = "%s%s";
	private static final String TYPE_VARIABLE_FORMAT = "%s %s";

	private final TypeVariableResolvableType[] typeParameters;

	public ClassResolvableType(final Class<?> rawType) {
		super(rawType);
		this.typeParameters = Arrays.stream(rawType.getTypeParameters())
				.map(TypeVariableResolvableType::new)
				.toArray(TypeVariableResolvableType[]::new);
	}

	@Override
	public MatchingState matches(ResolvableType type, MultiValuedMap<Class<?>, Class<?>> matches) {
		if (equals(type)) {
			return MatchingState.FULL_MATCH;
		}

		if (type instanceof ClassResolvableType) {
			// match class to class, i.e. "String" to "String"

			Class<?> oRawType = ((ClassResolvableType) type).getRawType();
			if (matches != null) {
				// direct match was found
				if (matches.containsMapping(rawType, oRawType)) {
					return MatchingState.FULL_MATCH;
				}
			}

			if (rawType.isArray() && oRawType.isArray()) {
				Class<?> compType = rawType.getComponentType();
				Class<?> oCompType = oRawType.getComponentType();
				return ResolvableType.forClass(compType).matches(ResolvableType.forClass(oCompType), matches);
			}

			// primitive and its wrapper
			if (ClassUtils.isAssignable(rawType, oRawType) && ClassUtils.isAssignable(oRawType, rawType)) {
				return MatchingState.PARTIAL_MATCH;
			}
		} else if (type instanceof WildcardTypeResolvableType) {
			// match class to wildcard, i.e. "String" to "? extends String"
			WildcardTypeResolvableType oType = (WildcardTypeResolvableType) type;
			MatchingState state = MatchingState.PARTIAL_MATCH;
			state.merge(matches(oType.getUpperBounds(), matches));
			state.merge(matches(oType.getLowerBounds(), matches));
			return state;
		}

		return MatchingState.NO_MATCH;
	}

	private Optional<MatchingState> matches(ResolvableType[] bounds, MultiValuedMap<Class<?>, Class<?>> matches) {
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
			Arrays.stream(typeParameters).map(ClassResolvableType::typeVariableAsString).forEach(joiner::add);
			return String.format(TYPE_FORMAT, name, joiner);
		}
	}

	private static String typeVariableAsString(TypeVariableResolvableType type) {
		TypeVariable<?> variable = type.getRawType();
		String name = variable.getName();
		List<String> bounds = Arrays.stream(variable.getBounds())
				.filter(bound -> bound != Object.class)
				.map(ClassResolvableType::boundAsString)
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
		ResolvableType type = ResolvableType.forType(bound);
		return type == null ? bound.getTypeName() : type.toString();
	}
}