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

import static checkspec.util.MemberUtils.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import checkspec.api.Visibility;

public class MemberUtilsTest {

	private static final int PUBLIC = 1;
	private static final int PRIVATE = 2;
	private static final int PROTECTED = 4;
	private static final int PACKAGE = 0;

	@Test
	public void getVisibilityTest() {
		Visibility result = getVisibility(PUBLIC);
		assertThat(result).isSameAs(Visibility.PUBLIC);

		result = getVisibility(PRIVATE);
		assertThat(result).isSameAs(Visibility.PRIVATE);

		result = getVisibility(PROTECTED);
		assertThat(result).isSameAs(Visibility.PROTECTED);

		result = getVisibility(PACKAGE);
		assertThat(result).isSameAs(Visibility.PACKAGE);
	}
}
