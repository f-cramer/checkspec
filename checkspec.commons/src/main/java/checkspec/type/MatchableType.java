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

public interface MatchableType {

	Type getRawType();

	Class<?> getRawClass();

	default MatchableType getSuperType() {
		Type superType = getRawClass().getGenericSuperclass();
		if (superType == null) {
			return MatchableType.OBJECT;
		}
		return MatchableType.forType(superType);
	}

	default MatchableType[] getInterfaces() {
		return Arrays.stream(getRawClass().getGenericInterfaces())
				.map(MatchableType::forType)
				.toArray(MatchableType[]::new);
	}

	MatchingState matches(MatchableType type, MultiValuedMap<Class<?>, Class<?>> matches);

	MatchableType OBJECT = new ClassMatchableType(Object.class) {
		@Override
		public MatchableType getSuperType() {
			return null;
		}
	};

	static MatchableType forClass(@NonNull Class<?> clazz) {
		return forType(clazz);
	}

	static MatchableType forType(@NonNull Type type) {
		synchronized (OBJECT) {
			return MatchableTypeCache.get(type)
					.orElseGet(() -> {
						MatchableType matchableType = null;
						if (type instanceof Class) {
							matchableType = forClass((Class<?>) type);
						} else if (type instanceof ParameterizedType) {
							matchableType = new ParameterizedTypeMatchableType((ParameterizedType) type);
						} else if (type instanceof WildcardType) {
							matchableType = new WildcardTypeMatchableType((WildcardType) type);
						} else if (type instanceof TypeVariable) {
							matchableType = new TypeVariableMatchableType((TypeVariable<?>) type);
						} else if (type instanceof GenericArrayType) {
							matchableType = new GenericArrayTypeMatchableType((GenericArrayType) type);
						}
						if (matchableType != null) {
							MatchableTypeCache.put(type, matchableType);
							return matchableType;
						}
						throw new IllegalArgumentException(type.getClass().getName());
					});
		}
	}

	static MatchableType forFieldType(Field field) {
		return forType(field.getGenericType());
	}

	static MatchableType forMethodReturnType(Method method) {
		return forType(method.getGenericReturnType());
	}

	static MatchableType forMethodParameter(Method method, int index) {
		return forType(method.getGenericParameterTypes()[index]);
	}

	static MatchableType forConstructorParameter(Constructor<?> constructor, int index) {
		return forType(constructor.getGenericParameterTypes()[index]);
	}
}
