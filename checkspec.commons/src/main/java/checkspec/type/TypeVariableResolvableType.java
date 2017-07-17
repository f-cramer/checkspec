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
import lombok.NonNull;

@Getter
@EqualsAndHashCode(callSuper = true)
class TypeVariableResolvableType extends AbstractResolvableType<TypeVariable<?>> {

	private final ResolvableType genericDeclarationType;
	private final ResolvableType[] bounds;
	private final int index;

	public TypeVariableResolvableType(@NonNull final TypeVariable<?> rawType) {
		super(rawType);
		this.genericDeclarationType = ResolvableType.forClass(getClass(rawType.getGenericDeclaration()));
		this.bounds = Arrays.stream(rawType.getBounds())
				.map(ResolvableType::forType)
				.toArray(ResolvableType[]::new);
		this.index = getIndex(rawType);
	}

	@Override
	public MatchingState matches(ResolvableType type, MultiValuedMap<Class<?>, Class<?>> matches) {
		if (equals(type)) {
			return MatchingState.FULL_MATCH;
		}

		MatchingState state = MatchingState.FULL_MATCH;
		if (type instanceof TypeVariableResolvableType) {
			ResolvableType oGenericDeclarationType = ((TypeVariableResolvableType) type).getGenericDeclarationType();
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
				.map(ResolvableType::getRawClass)
				.collect(Collectors.toList());
		return TypeUtils.lowestCommonSuperclass(rawClasses);
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
