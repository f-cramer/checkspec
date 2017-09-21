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



import static checkspec.util.MathUtils.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

public class MathUtilsTest {

	@Test
	public void multiplyWithoutOverflowTest() {
		int result = multiplyWithoutOverflow(1, 2);
		assertThat(result).isEqualTo(2);

		result = multiplyWithoutOverflow(Integer.MAX_VALUE, 2);
		assertThat(result).isEqualTo(Integer.MAX_VALUE);

		result = multiplyWithoutOverflow(Integer.MIN_VALUE, 2);
		assertThat(result).isEqualTo(Integer.MIN_VALUE);
	}
}
