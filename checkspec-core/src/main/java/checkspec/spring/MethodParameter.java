/*
 * Copyright 2002-2017 the original author or authors.
 *
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
 */

package checkspec.spring;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

/**
 * Helper class that encapsulates the specification of a method parameter, i.e.
 * a {@link Method} or {@link Constructor} plus a parameter index and a nested
 * type index for a declared generic type. Useful as a specification object to
 * pass along.
 *
 * <p>
 * As of 4.2, there is a
 * {@link org.springframework.core.annotation.SynthesizingMethodParameter}
 * subclass available which synthesizes annotations with attribute aliases. That
 * subclass is used for web and message endpoint processing, in particular.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Andy Clement
 * @author Sam Brannen
 * @since 2.0
 * @see org.springframework.core.annotation.SynthesizingMethodParameter
 */
class MethodParameter {

	private final Method method;

	private final Constructor<?> constructor;

	private final int parameterIndex;

	private int nestingLevel = 1;

	/** Map from Integer level to Integer type index */
	Map<Integer, Integer> typeIndexesPerLevel;

	private volatile Class<?> containingClass;

	private volatile Class<?> parameterType;

	private volatile Type genericParameterType;

	private volatile Annotation[] parameterAnnotations;

	private volatile String parameterName;

	/**
	 * Create a new {@code MethodParameter} for the given method, with nesting level
	 * 1.
	 * 
	 * @param method
	 *            the Method to specify a parameter for
	 * @param parameterIndex
	 *            the index of the parameter: -1 for the method return type; 0 for
	 *            the first method parameter; 1 for the second method parameter,
	 *            etc.
	 */
	public MethodParameter(Method method, int parameterIndex) {
		this(method, parameterIndex, 1);
	}

	/**
	 * Create a new {@code MethodParameter} for the given method.
	 * 
	 * @param method
	 *            the Method to specify a parameter for
	 * @param parameterIndex
	 *            the index of the parameter: -1 for the method return type; 0 for
	 *            the first method parameter; 1 for the second method parameter,
	 *            etc.
	 * @param nestingLevel
	 *            the nesting level of the target type (typically 1; e.g. in case of
	 *            a List of Lists, 1 would indicate the nested List, whereas 2 would
	 *            indicate the element of the nested List)
	 */
	public MethodParameter(Method method, int parameterIndex, int nestingLevel) {
		Objects.requireNonNull(method, "Method must not be null");
		this.method = method;
		this.parameterIndex = parameterIndex;
		this.nestingLevel = nestingLevel;
		this.constructor = null;
	}

	/**
	 * Create a new MethodParameter for the given constructor, with nesting level 1.
	 * 
	 * @param constructor
	 *            the Constructor to specify a parameter for
	 * @param parameterIndex
	 *            the index of the parameter
	 */
	public MethodParameter(Constructor<?> constructor, int parameterIndex) {
		this(constructor, parameterIndex, 1);
	}

	/**
	 * Create a new MethodParameter for the given constructor.
	 * 
	 * @param constructor
	 *            the Constructor to specify a parameter for
	 * @param parameterIndex
	 *            the index of the parameter
	 * @param nestingLevel
	 *            the nesting level of the target type (typically 1; e.g. in case of
	 *            a List of Lists, 1 would indicate the nested List, whereas 2 would
	 *            indicate the element of the nested List)
	 */
	public MethodParameter(Constructor<?> constructor, int parameterIndex, int nestingLevel) {
		Objects.requireNonNull(constructor, "Constructor must not be null");
		this.constructor = constructor;
		this.parameterIndex = parameterIndex;
		this.nestingLevel = nestingLevel;
		this.method = null;
	}

	/**
	 * Copy constructor, resulting in an independent MethodParameter object based on
	 * the same metadata and cache state that the original object was in.
	 * 
	 * @param original
	 *            the original MethodParameter object to copy from
	 */
	public MethodParameter(MethodParameter original) {
		Objects.requireNonNull(original, "Original must not be null");
		this.method = original.method;
		this.constructor = original.constructor;
		this.parameterIndex = original.parameterIndex;
		this.nestingLevel = original.nestingLevel;
		this.typeIndexesPerLevel = original.typeIndexesPerLevel;
		this.containingClass = original.containingClass;
		this.parameterType = original.parameterType;
		this.genericParameterType = original.genericParameterType;
		this.parameterAnnotations = original.parameterAnnotations;
		this.parameterName = original.parameterName;
	}

	/**
	 * Return the wrapped Method, if any.
	 * <p>
	 * Note: Either Method or Constructor is available.
	 * 
	 * @return the Method, or {@code null} if none
	 */
	public Method getMethod() {
		return this.method;
	}

	/**
	 * Return the wrapped Constructor, if any.
	 * <p>
	 * Note: Either Method or Constructor is available.
	 * 
	 * @return the Constructor, or {@code null} if none
	 */
	public Constructor<?> getConstructor() {
		return this.constructor;
	}

	/**
	 * Return the class that declares the underlying Method or Constructor.
	 */
	public Class<?> getDeclaringClass() {
		return getMember().getDeclaringClass();
	}

	/**
	 * Return the wrapped member.
	 * 
	 * @return the Method or Constructor as Member
	 */
	public Member getMember() {
		// NOTE: no ternary expression to retain JDK <8 compatibility even when
		// using
		// the JDK 8 compiler (potentially selecting
		// java.lang.reflect.Executable
		// as common type, with that new base class not available on older JDKs)
		if (this.method != null) {
			return this.method;
		} else {
			return this.constructor;
		}
	}

	/**
	 * Return the index of the method/constructor parameter.
	 * 
	 * @return the parameter index (-1 in case of the return type)
	 */
	public int getParameterIndex() {
		return this.parameterIndex;
	}

	/**
	 * Return the nesting level of the target type (typically 1; e.g. in case of a
	 * List of Lists, 1 would indicate the nested List, whereas 2 would indicate the
	 * element of the nested List).
	 */
	public int getNestingLevel() {
		return this.nestingLevel;
	}

	/**
	 * Set a containing class to resolve the parameter type against.
	 */
	void setContainingClass(Class<?> containingClass) {
		this.containingClass = containingClass;
	}

	public Class<?> getContainingClass() {
		return (this.containingClass != null ? this.containingClass : getDeclaringClass());
	}

	/**
	 * Set a resolved (generic) parameter type.
	 */
	void setParameterType(Class<?> parameterType) {
		this.parameterType = parameterType;
	}

	/**
	 * Return the generic type of the method/constructor parameter.
	 * 
	 * @return the parameter type (never {@code null})
	 * @since 3.0
	 */
	public Type getGenericParameterType() {
		if (this.genericParameterType == null) {
			if (this.parameterIndex < 0) {
				this.genericParameterType = (this.method != null ? this.method.getGenericReturnType() : null);
			} else {
				this.genericParameterType = (this.method != null ? this.method.getGenericParameterTypes()[this.parameterIndex] : this.constructor.getGenericParameterTypes()[this.parameterIndex]);
			}
		}
		return this.genericParameterType;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof MethodParameter)) {
			return false;
		}
		MethodParameter otherParam = (MethodParameter) other;
		return (this.parameterIndex == otherParam.parameterIndex && getMember().equals(otherParam.getMember()));
	}

	@Override
	public int hashCode() {
		return (getMember().hashCode() * 31 + this.parameterIndex);
	}

	@Override
	public String toString() {
		return (this.method != null ? "method '" + this.method.getName() + "'" : "constructor") + " parameter " + this.parameterIndex;
	}

	@Override
	public MethodParameter clone() {
		return new MethodParameter(this);
	}
}
