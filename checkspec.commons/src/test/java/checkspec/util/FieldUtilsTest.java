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

import static checkspec.util.FieldUtils.*;
import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Field;

import org.junit.Test;

import checkspec.api.Visibility;
import checkspec.type.MatchableType;

public class FieldUtilsTest {

	private static final Field FIELD;

	static {
		try {
			FIELD = FieldUtilsTest.class.getDeclaredField("FIELD");
		} catch (NoSuchFieldException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void createStringTest() {
		String result = FieldUtils.createString(FIELD);
		assertThat(result).isEqualTo("private static final java.lang.reflect.Field FIELD");
	}

	@Test(expected = NullPointerException.class)
	public void createStringNullTest() {
		FieldUtils.createString(null);
	}

	@Test
	public void getTypeTest() {
		MatchableType result = getType(FIELD);
		assertThat(result).isEqualTo(MatchableType.forFieldType(FIELD));
	}

	@Test(expected = NullPointerException.class)
	public void getTypeNullTest() {
		getType(null);
	}

	@Test
	public void getTypeNameTest() {
		String result = getTypeName(FIELD);
		assertThat(result).isEqualTo("java.lang.reflect.Field");
	}

	@Test(expected = NullPointerException.class)
	public void getTypeNameNullTest() {
		getTypeName(null);
	}

	@Test
	public void getVisibilityTest() {
		Visibility result = getVisibility(FIELD);
		assertThat(result).isSameAs(Visibility.PRIVATE);
	}

	@Test(expected = NullPointerException.class)
	public void getVisibilityNullTest() {
		getVisibility(null);
	}
}
