package checkspec.type;

/*-
 * #%L
 * CheckSpec Commons
 * %%
 * Copyright (C) 2017 Florian Cramer
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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

/**
 * Represents a type that can be matched to another one.
 *
 * @author Florian Cramer
 *
 */
public interface MatchableType {

	/**
	 * Returns the raw {@link Type} this {@link MatchableType} was created from.
	 *
	 * @return the raw type
	 */
	Type getRawType();

	/**
	 * Returns the raw {@link Class} of this {@link MatchableType}.
	 *
	 * @return the raw class
	 */
	Class<?> getRawClass();

	/**
	 * Returns the super type of this {@link MatchableType}.
	 *
	 * @return the super type
	 */
	default MatchableType getSuperType() {
		Type superType = getRawClass().getGenericSuperclass();
		if (superType == null) {
			return MatchableType.OBJECT;
		}
		return MatchableType.forType(superType);
	}

	/**
	 * Returns the interfaces directly implemented by this
	 * {@link MatchableType}.
	 *
	 * @return the interfaces
	 */
	default MatchableType[] getInterfaces() {
		return Arrays.stream(getRawClass().getGenericInterfaces())
				.map(MatchableType::forType)
				.toArray(MatchableType[]::new);
	}

	/**
	 * Returns whether or not this {@link MatchableType} matches the given one.
	 *
	 * <p>
	 * One type fully matches another, if the are the same or if the raw classes
	 * of both types are contained in {@code matches}.
	 * </p>
	 * <p>
	 * They match each other partially if they do not match fully but the given
	 * type or one of its matches are assignable to this one.
	 * </p>
	 * <p>
	 * They do not match each other if non of the above conditions matches.
	 * </p>
	 *
	 * @param type
	 *            the given type
	 * @param matches
	 *            a list of matches
	 * @return in which way the given type matches the current one
	 */
	MatchingState matches(MatchableType type, MultiValuedMap<Class<?>, Class<?>> matches);

	MatchableType OBJECT = new ClassMatchableType(Object.class) {
		@Override
		public MatchableType getSuperType() {
			return null;
		}
	};

	/**
	 * Creates a new {@link MatchableType} from the given class.
	 *
	 * @param clazz
	 *            the class
	 * @return the matchable type
	 */
	static MatchableType forClass(@NonNull Class<?> clazz) {
		return forType(clazz);
	}

	/**
	 * Creates a new {@link MatchableType} from the given type.
	 *
	 * @param type
	 *            the given type
	 * @return the matchable type
	 */
	static MatchableType forType(@NonNull Type type) {
		synchronized (OBJECT) {
			return MatchableTypeCache.get(type)
					.orElseGet(() -> {
						if (type instanceof Class) {
							return new ClassMatchableType((Class<?>) type);
						} else if (type instanceof ParameterizedType) {
							return new ParameterizedTypeMatchableType((ParameterizedType) type);
						} else if (type instanceof WildcardType) {
							return new WildcardTypeMatchableType((WildcardType) type);
						} else if (type instanceof TypeVariable) {
							return new TypeVariableMatchableType((TypeVariable<?>) type);
						} else if (type instanceof GenericArrayType) {
							return new GenericArrayTypeMatchableType((GenericArrayType) type);
						}
						throw new IllegalArgumentException(type.getClass().getName());
					});
		}
	}

	/**
	 * Creates a new {@link MatchableType} from the type of the given field.
	 *
	 * @param field
	 *            the field
	 * @return the matchable type
	 */
	static MatchableType forFieldType(Field field) {
		return forType(field.getGenericType());
	}

	/**
	 * Creates a new {@link MatchableType} from the return type of the given
	 * method.
	 *
	 * @param method
	 *            the method
	 * @return the matchable type
	 */
	static MatchableType forMethodReturnType(Method method) {
		return forType(method.getGenericReturnType());
	}

	/**
	 * Creates a new {@link MatchableType} from a parameter at the given index
	 * of the given method.
	 *
	 * @param method
	 *            the method
	 * @param index
	 *            the index of the parameter
	 * @return the matchable type
	 */
	static MatchableType forMethodParameter(Method method, int index) {
		return forType(method.getGenericParameterTypes()[index]);
	}

	/**
	 * Creates a new {@link MatchableType} from a parameter at the given index
	 * of the given constructor.
	 *
	 * @param constructor
	 *            the constructor
	 * @param index
	 *            the index of the parameter
	 * @return the matchable type
	 */
	static MatchableType forConstructorParameter(Constructor<?> constructor, int index) {
		final Type[] genericParameterTypes = constructor.getGenericParameterTypes();
		if (genericParameterTypes.length > index) {
			return forType(genericParameterTypes[index]);
		} else {
			return forType(constructor.getParameterTypes()[index]);
		}
	}
}
