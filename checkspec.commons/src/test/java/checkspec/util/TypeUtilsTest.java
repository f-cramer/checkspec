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

import static checkspec.util.TypeUtils.*;
import static org.assertj.core.api.Assertions.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class TypeUtilsTest {

	@Test
	public void getLowestCommonSuperTypeTest() {
		List<Class<?>> classes = Arrays.asList(String.class, Integer.class);
		Class<?> result = getMostSpecificCommonSuperType(classes);
		assertThat(result).isSameAs(Serializable.class);

		classes = Arrays.asList(Serializable.class, String.class);
		result = getMostSpecificCommonSuperType(classes);
		assertThat(result).isSameAs(Serializable.class);

		classes = Arrays.asList(String.class, String.class);
		result = getMostSpecificCommonSuperType(classes);
		assertThat(result).isSameAs(String.class);

		classes = Arrays.asList(Integer.class, Long.class);
		result = getMostSpecificCommonSuperType(classes);
		assertThat(result).isSameAs(Number.class);

		classes = Arrays.asList(int.class, Integer.class);
		result = getMostSpecificCommonSuperType(classes);
		assertThat(result).isNull();

		classes = Collections.emptyList();
		result = getMostSpecificCommonSuperType(classes);
		assertThat(result).isNull();
	}

	@Test(expected = NullPointerException.class)
	public void getLowestCommonSuperTypeNullTest() {
		getMostSpecificCommonSuperType(null);
	}
}
