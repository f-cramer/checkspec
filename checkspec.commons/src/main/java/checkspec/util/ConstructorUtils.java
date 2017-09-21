package checkspec.util;

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
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import checkspec.api.Visibility;
import checkspec.type.MatchableType;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Miscellaneous methods that are working on instances of {@link Constructor}.
 * Mainly for internal use within the framework itself.
 *
 * @author Florian Cramer
 * @see Constructor
 */
@UtilityClass
public final class ConstructorUtils {

	/**
	 * Returns a string representation of the given {@link Constructor}. This
	 * follows the following pattern:
	 * <p>
	 * {@code [visibilty-modifier] <init>([parameter-list])}
	 * <p>
	 * E.g. "public &lt;init&gt;();
	 *
	 * @param constructor
	 *            the non-null constructor
	 * @return the string representation
	 * @throws NullPointerException
	 *             if {@code constructor} is {@code null}
	 */
	public static String createString(@NonNull Constructor<?> constructor) {
		Visibility visibility = getVisibility(constructor);
		String parameterList = getParametersAsString(constructor);
		return String.format("%s <init>(%s)", visibility, parameterList);
	}

	/**
	 * Returns the {@link Visibility} of the given type.
	 *
	 * @param constructor
	 *            the constructor, null not permitted
	 * @return the visibility
	 * @throws NullPointerException
	 *             if {@code constructor} is {@code null}
	 */
	public static Visibility getVisibility(@NonNull Constructor<?> constructor) {
		return MemberUtils.getVisibility(constructor.getModifiers());
	}

	/**
	 * Returns the parameters of the given constructor wrapped as instances of
	 * {@link MatchableType}.
	 *
	 * @param constructor
	 *            the non-null constructor
	 * @return the parameters
	 * @throws NullPointerException
	 *             if {@code constructor} is {@code null}
	 */
	public static MatchableType[] getParametersAsResolvableType(@NonNull Constructor<?> constructor) {
		return getParameterList(constructor)
				.toArray(MatchableType[]::new);
	}

	/**
	 * Returns a string representation of the parameters of the given
	 * constructor. This uses the method
	 * {@link ClassUtils#getName(MatchableType)} to create a string
	 * representation for each parameters and joins them with a comma.
	 *
	 * @param constructor
	 *            the non-null constructor
	 * @return a string representation of the parameters of the given
	 *         constructor
	 * @throws NullPointerException
	 *             if {@code constructor} is {@code null}
	 */
	public static String getParametersAsString(@NonNull Constructor<?> constructor) {
		return getParameterList(constructor)
				.map(ClassUtils::getName)
				.collect(Collectors.joining(", "));
	}

	private static Stream<MatchableType> getParameterList(Constructor<?> constructor) {
		return IntStream.range(0, constructor.getParameterCount()).parallel()
				.mapToObj(i -> MatchableType.forConstructorParameter(constructor, i));
	}
}
