package checkspec.type;

import java.lang.reflect.Executable;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
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
public class TypeVariableMatchableType extends AbstractMatchableType<TypeVariable<?>, TypeVariableMatchableType> {

	private final MatchableType genericDeclarationType;
	private final MatchableType[] bounds;
	private final int index;

	TypeVariableMatchableType(final TypeVariable<?> rawType) {
		super(TypeVariableMatchableType.class, rawType);
		this.genericDeclarationType = MatchableType.forClass(getClass(rawType.getGenericDeclaration()));
		this.bounds = Arrays.stream(rawType.getBounds())
				.map(MatchableType::forType)
				.toArray(MatchableType[]::new);
		this.index = getIndex(rawType);
	}

	@Override
	public Optional<MatchingState> matchesImpl(TypeVariableMatchableType type, MultiValuedMap<Class<?>, Class<?>> matches) {
		MatchableType oGenericDeclarationType = type.getGenericDeclarationType();
		return Optional.of(genericDeclarationType.matches(oGenericDeclarationType, matches));
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
