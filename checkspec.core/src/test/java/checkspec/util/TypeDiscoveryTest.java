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



import static checkspec.util.TypeDiscovery.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.Test;

import checkspec.analysis.ClassAnalysis;
import checkspec.report.Report;

public class TypeDiscoveryTest {

	// Same test two times to cover saved results

	@Test
	public void getSubTypesOfTest() {
		List<Class<?>> result = getSubTypesOf(Report.class);
		assertThat(result).isNotEmpty();
	}

	@Test
	public void getSubTypesOfTest2() {
		List<Class<?>> result = getSubTypesOf(Report.class);
		assertThat(result).isNotEmpty();
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void getNewInstancesOfTest() {
		List<ClassAnalysis> result = getNewInstancesOf(ClassAnalysis.class);
		assertThat(result).isNotEmpty();
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void getNewInstancesOfWithErrorFormatTest() {
		List<ClassAnalysis> result = getNewInstancesOf(ClassAnalysis.class, "");
		assertThat(result).isNotEmpty();
	}

	// Same test two times to cover saved results

	@Test
	@SuppressWarnings("rawtypes")
	public void getUniqueInstancesOfTest() {
		List<ClassAnalysis> result = getUniqueInstancesOf(ClassAnalysis.class);
		assertThat(result).isNotEmpty();
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void getUniqueInstancesOfTest2() {
		List<ClassAnalysis> result = getUniqueInstancesOf(ClassAnalysis.class);
		assertThat(result).isNotEmpty();
	}

	// Same test two times to cover saved results

	@Test
	@SuppressWarnings("rawtypes")
	public void getUniqueInstancesOfWithErrorFormatTest() {
		List<ClassAnalysis> result = getUniqueInstancesOf(ClassAnalysis.class, "");
		assertThat(result).isNotEmpty();
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void getUniqueInstancesOfWithErrorFormatTest2() {
		List<ClassAnalysis> result = getUniqueInstancesOf(ClassAnalysis.class, "");
		assertThat(result).isNotEmpty();
	}
}
