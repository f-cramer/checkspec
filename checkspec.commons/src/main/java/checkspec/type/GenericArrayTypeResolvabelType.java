package checkspec.type;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.util.MatchingState;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
class GenericArrayTypeResolvabelType extends AbstractResolvableType<GenericArrayType> {

	private final ResolvableType componentType;

	public GenericArrayTypeResolvabelType(final GenericArrayType rawType) {
		super(rawType);
		this.componentType = ResolvableType.forType(rawType.getGenericComponentType());
	}

	@Override
	public MatchingState matches(ResolvableType type, MultiValuedMap<Class<?>, Class<?>> matches) {
		if (equals(type)) {
			return MatchingState.FULL_MATCH;
		}

		if (type instanceof GenericArrayTypeResolvabelType) {
			ResolvableType oComponentType = ((GenericArrayTypeResolvabelType) type).getComponentType();
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
