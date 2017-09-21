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

import static checkspec.util.MethodUtils.*;
import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Method;

import org.junit.Test;

import checkspec.api.Visibility;
import checkspec.type.MatchableType;

public class MethodUtilsTest {

	private static final Method METHOD;

	static {
		try {
			METHOD = MethodUtils.class.getDeclaredMethod("createString", Method.class);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void toStringTest() {
		String result = MethodUtils.createString(METHOD);
		assertThat(result).isEqualTo("public static java.lang.String createString(java.lang.reflect.Method)");

	}

	@Test(expected = NullPointerException.class)
	public void toStringNullTest() {
		MethodUtils.createString(null);
	}

	@Test
	public void getVisibilityTest() {
		Visibility result = getVisibility(METHOD);
		assertThat(result).isSameAs(Visibility.PUBLIC);

	}

	@Test(expected = NullPointerException.class)
	public void getVisibilityNullTest() {
		getVisibility(null);
	}

	@Test
	public void getReturnTypeNameTest() {
		String result = getReturnTypeName(METHOD);
		assertThat(result).isEqualTo("java.lang.String");
	}

	@Test(expected = NullPointerException.class)
	public void getReturnTypeNameNullTest() {
		getReturnTypeName(null);
	}

	@Test
	public void getParameterListTest() {
		String result = getParameterList(METHOD);
		assertThat(result).isEqualTo("java.lang.reflect.Method");
	}

	@Test(expected = NullPointerException.class)
	public void getParameterListNullTest() {
		getParameterList(null);
	}

	@Test
	public void isAbstractTest() {
		boolean result = isAbstract(METHOD);
		assertThat(result).isFalse();
	}

	@Test(expected = NullPointerException.class)
	public void isAbstractNullTest() {
		isAbstract(null);
	}

	@Test
	public void calculateParameterDistanceTest() {
		MatchableType[] emptyParameterList = new MatchableType[0];
		MatchableType[] oneParameterList = { MatchableType.forClass(Integer.TYPE) };
		MatchableType[] anotherOneParameterList = { MatchableType.forClass(Integer.class) };
		MatchableType[] yetAnotherOneParameterList = { MatchableType.forClass(String.class) };
		MatchableType[] twoParameterList = { MatchableType.forClass(Integer.class), MatchableType.forClass(String.class) };

		int result = calculateParameterDistance(emptyParameterList, oneParameterList, null);
		assertThat(result).isEqualTo(20);

		result = calculateParameterDistance(oneParameterList, emptyParameterList, null);
		assertThat(result).isEqualTo(20);

		result = calculateParameterDistance(oneParameterList, oneParameterList, null);
		assertThat(result).isEqualTo(0);

		result = calculateParameterDistance(oneParameterList, anotherOneParameterList, null);
		assertThat(result).isEqualTo(5);

		result = calculateParameterDistance(oneParameterList, yetAnotherOneParameterList, null);
		assertThat(result).isEqualTo(10);

		result = calculateParameterDistance(oneParameterList, twoParameterList, null);
		assertThat(result).isEqualTo(25);
	}

	@Test(expected = NullPointerException.class)
	public void calculateParameterDistanceNullTest() {
		calculateParameterDistance(null, new MatchableType[0], null);
	}

	@Test(expected = NullPointerException.class)
	public void calculateParameterDistanceNullTest2() {
		calculateParameterDistance(new MatchableType[0], null, null);
	}

	@Test(expected = NullPointerException.class)
	public void calculateParameterDistanceNullTest3() {
		calculateParameterDistance(null, null, null);
	}
}
