package checkspec.type;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.util.MatchingState;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
class GenericArrayTypeMatchableType extends AbstractMatchableType<GenericArrayType> {

	private final MatchableType componentType;

	public GenericArrayTypeMatchableType(final GenericArrayType rawType) {
		super(rawType);
		this.componentType = MatchableType.forType(rawType.getGenericComponentType());
	}

	@Override
	public MatchingState matches(MatchableType type, MultiValuedMap<Class<?>, Class<?>> matches) {
		if (equals(type)) {
			return MatchingState.FULL_MATCH;
		}

		if (type instanceof GenericArrayTypeMatchableType) {
			MatchableType oComponentType = ((GenericArrayTypeMatchableType) type).getComponentType();
			return componentType.matches(oComponentType, matches);
		}

		return MatchingState.NO_MATCH;
	}

	@Override
	public Class<?> getRawClass() {
		Class<?> componentTypeRawClass = componentType.getRawClass();
		return Array.newInstance(componentTypeRawClass, 0).getClass();
	}
}
