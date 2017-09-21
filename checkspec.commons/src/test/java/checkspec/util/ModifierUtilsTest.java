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

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import checkspec.type.MatchableType;

public class ModifierUtilsTest {

	@Test
	public void toStringResolvableTypeTest() {
		String result = ModifierUtils.createString(MatchableType.forClass(ModifierUtilsTest.class));
		assertThat(result).isEqualTo("public");

		result = ModifierUtils.createString(MatchableType.forClass(PublicStaticFinalClass.class));
		assertThat(result).isEqualTo("private static final");

		result = ModifierUtils.createString(MatchableType.forClass(ProtectedStaticClass.class));
		assertThat(result).isEqualTo("protected abstract static");

		result = ModifierUtils.createString(MatchableType.forClass(Interface.class));
		assertThat(result).isEmpty();

		result = ModifierUtils.createString(MatchableType.forClass(Enum.class));
		assertThat(result).isEmpty();
	}

	@Test(expected = NullPointerException.class)
	public void toStringResolvableTypeNullTest() {
		ModifierUtils.createString(null);
	}

	private static final class PublicStaticFinalClass {
	}

	protected abstract static class ProtectedStaticClass {
	}

	interface Interface {
	}

	enum Enum {
	}
}
