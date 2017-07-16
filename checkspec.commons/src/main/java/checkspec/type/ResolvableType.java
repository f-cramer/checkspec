package checkspec.type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;

import org.apache.commons.collections4.MultiValuedMap;

import checkspec.util.MatchingState;
import lombok.NonNull;

public interface ResolvableType {

	Type getRawType();

	Class<?> getRawClass();

	default ResolvableType getSuperType() {
		Type superType = getRawClass().getGenericSuperclass();
		if (superType == null) {
			return ResolvableType.OBJECT;
		}
		return ResolvableType.forType(superType);
	}

	default ResolvableType[] getInterfaces() {
		return Arrays.stream(getRawClass().getGenericInterfaces())
				.map(ResolvableType::forType)
				.toArray(ResolvableType[]::new);
	}

	MatchingState matches(ResolvableType type, MultiValuedMap<Class<?>, Class<?>> matches);

	ResolvableType OBJECT = new ClassResolvableType(Object.class) {
		@Override
		public ResolvableType getSuperType() {
			return null;
		}
	};

	static ResolvableType forClass(@NonNull Class<?> clazz) {
		return ResolvableTypeUtils.get(clazz)
				.orElseGet(() -> new ClassResolvableType(clazz));
	}

	static ResolvableType forType(@NonNull Type type) {
		return ResolvableTypeUtils.get(type)
				.orElseGet(() -> {
					if (type instanceof Class) {
						return forClass((Class<?>) type);
					} else if (type instanceof ParameterizedType) {
						return new ParameterizedTypeResolvableType((ParameterizedType) type);
					} else if (type instanceof WildcardType) {
						return new WildcardTypeResolvableType((WildcardType) type);
					} else if (type instanceof TypeVariable) {
						return new TypeVariableResolvableType((TypeVariable<?>) type);
					} else if (type instanceof GenericArrayType) {
						return new GenericArrayTypeResolvabelType((GenericArrayType) type);
					}
					throw new IllegalArgumentException(type.getClass().getName());
				});

	}

	static ResolvableType forField(Field field) {
		return forFieldType(field);
	}

	static ResolvableType forFieldType(Field field) {
		return forType(field.getGenericType());
	}

	static ResolvableType forMethodReturnType(Method method) {
		return forType(method.getGenericReturnType());
	}

	static ResolvableType forMethodParameter(Method method, int index) {
		return forType(method.getGenericParameterTypes()[index]);
	}

	static ResolvableType forConstructorParameter(Constructor<?> constructor, int index) {
		return forType(constructor.getGenericParameterTypes()[index]);
	}
}
