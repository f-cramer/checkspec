package checkspec.type;

import java.lang.reflect.Executable;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.util.MatchingState;
import checkspec.util.TypeUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
class TypeVariableMatchableType extends AbstractMatchableType<TypeVariable<?>> {

	private final MatchableType genericDeclarationType;
	private final MatchableType[] bounds;
	private final int index;

	public TypeVariableMatchableType(final TypeVariable<?> rawType) {
		super(rawType);
		this.genericDeclarationType = MatchableType.forClass(getClass(rawType.getGenericDeclaration()));
		this.bounds = Arrays.stream(rawType.getBounds())
				.map(MatchableType::forType)
				.toArray(MatchableType[]::new);
		this.index = getIndex(rawType);
	}

	@Override
	public MatchingState matches(MatchableType type, MultiValuedMap<Class<?>, Class<?>> matches) {
		if (equals(type)) {
			return MatchingState.FULL_MATCH;
		}

		MatchingState state = MatchingState.FULL_MATCH;
		if (type instanceof TypeVariableMatchableType) {
			MatchableType oGenericDeclarationType = ((TypeVariableMatchableType) type).getGenericDeclarationType();
			state = state.merge(genericDeclarationType.matches(oGenericDeclarationType, matches));
			if (state == MatchingState.NO_MATCH) {
				return MatchingState.NO_MATCH;
			}

			return state;
		}

		return MatchingState.NO_MATCH;
	}

	@Override
	public Class<?> getRawClass() {
		List<Class<?>> rawClasses = Arrays.stream(bounds)
				.map(MatchableType::getRawClass)
				.collect(Collectors.toList());
		return TypeUtils.getLowestCommonSuperType(rawClasses);
	}

	@Override
	public String toString() {
		return rawType.getName();
	}

	private static Class<?> getClass(GenericDeclaration declaration) {
		if (declaration instanceof Class<?>) {
			return (Class<?>) declaration;
		} else if (declaration instanceof Method) {
			return ((Method) declaration).getDeclaringClass();
		} else if (declaration instanceof Executable) {
			return ((Executable) declaration).getDeclaringClass();
		}
		String typeName = declaration == null ? "null" : declaration.getClass().getName();
		throw new IllegalArgumentException("declaration is of unknown type " + typeName);
	}

	private static int getIndex(TypeVariable<?> variable) {
		TypeVariable<?>[] parameters = variable.getGenericDeclaration().getTypeParameters();
		return IntStream.range(0, parameters.length)
				.filter(index -> parameters[index] == variable)
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException(variable.toString()));
	}
}
