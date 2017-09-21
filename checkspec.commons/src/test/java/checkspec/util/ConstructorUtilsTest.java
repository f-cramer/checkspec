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

import static checkspec.util.ConstructorUtils.*;
import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Constructor;

import org.junit.Test;

import checkspec.api.Visibility;
import checkspec.type.MatchableType;

public class ConstructorUtilsTest {

	private static final Constructor<?> CONSTRUCTOR;
	private static final Constructor<?> STRING_CONSTRUCTOR;

	static {
		try {
			CONSTRUCTOR = ConstructorUtilsTest.class.getDeclaredConstructor();
			STRING_CONSTRUCTOR = String.class.getConstructor(String.class);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void createStringTest() {
		String result = createString(CONSTRUCTOR);
		assertThat(result).isEqualTo("public <init>()");
	}

	@Test(expected = NullPointerException.class)
	public void createStringNullTest() {
		createString(null);
	}

	@Test
	public void getVisibilityTest() {
		Visibility result = getVisibility(CONSTRUCTOR);
		assertThat(result).isSameAs(Visibility.PUBLIC);
	}

	@Test(expected = NullPointerException.class)
	public void getVisibilityNullTest() {
		getVisibility(null);
	}

	@Test
	public void getParametersAsResolvableTypeTest() {
		MatchableType[] result = getParametersAsResolvableType(CONSTRUCTOR);
		assertThat(result).isEmpty();
	}

	@Test(expected = NullPointerException.class)
	public void getParametersAsResolvableTypeNullTest() {
		getParametersAsResolvableType(null);
	}

	@Test
	public void getParametersAsStringTest() {
		String result = getParametersAsString(STRING_CONSTRUCTOR);
		assertThat(result).isEqualTo("java.lang.String");
	}

	@Test(expected = NullPointerException.class)
	public void getParametersAsStringNullTest() {
		getParametersAsString(null);
	}
}
