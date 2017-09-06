package checkspec.type;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.util.Optional;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.util.MatchingState;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class GenericArrayTypeMatchableType extends AbstractMatchableType<GenericArrayType, GenericArrayTypeMatchableType> {

	private final MatchableType componentType;

	GenericArrayTypeMatchableType(final GenericArrayType rawType) {
		super(GenericArrayTypeMatchableType.class, rawType);
		this.componentType = MatchableType.forType(rawType.getGenericComponentType());
	}

	@Override
	protected Optional<MatchingState> matchesImpl(GenericArrayTypeMatchableType type, MultiValuedMap<Class<?>, Class<?>> matches) {
		MatchableType oComponentType = type.getComponentType();
		return Optional.of(componentType.matches(oComponentType, matches));
	}

	@Override
	public Class<?> getRawClass() {
		Class<?> componentTypeRawClass = componentType.getRawClass();
		return Array.newInstance(componentTypeRawClass, 0).getClass();
	}
}
