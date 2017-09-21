package checkspec.util;

/*-
 * #%L
 * CheckSpec Core
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

import static checkspec.util.ReflectionsUtils.*;
import static org.assertj.core.api.Assertions.*;

import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;
import org.reflections.Configuration;
import org.reflections.Reflections;

public class ReflectionsUtilsTest {

	@Test
	public void createDefaultReflectionsTest() {
		Reflections result = createDefaultReflections();
		assertThat(result).isNotNull();

		Configuration configuration = result.getConfiguration();
		assertThat(configuration).isNotNull();

		Set<URL> urls = configuration.getUrls();
		assertThat(urls).isNotEmpty();
	}

	@Test
	public void createReflectionsTest() {
		Reflections result = createReflections(new URL[0]);
		assertThat(result).isNotNull();

		Configuration configuration = result.getConfiguration();
		assertThat(configuration).isNotNull();

		Set<URL> urls = configuration.getUrls();
		assertThat(urls).isEmpty();
	}

	// Two time the same test to test lazy calculation

	@Test
	public void getUrlsFromClasspathTest() {
		URL[] result = getUrlsFromClasspath();
		assertThat(result).isNotEmpty();
	}

	@Test
	public void getUrlsFromClasspathTest2() {
		URL[] result = getUrlsFromClasspath();
		assertThat(result).isNotEmpty();
	}

	@Test
	public void getAsUrlStreamTest() {
		List<URL> result = getAsUrlStream(".").collect(Collectors.toList());
		assertThat(result).hasSize(1);

		result = getAsUrlStream("/Path/Not/To/Be/Found").collect(Collectors.toList());
		assertThat(result).isEmpty();
	}
}
