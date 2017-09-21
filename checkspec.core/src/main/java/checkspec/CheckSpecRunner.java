package checkspec;

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



import static checkspec.util.ClassUtils.*;
import static checkspec.util.ReflectionsUtils.*;
import static checkspec.util.SecurityUtils.*;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;

import checkspec.report.SpecReport;
import checkspec.specification.ClassSpecification;
import lombok.experimental.UtilityClass;

/**
 * Helper class that provides methods to easier make calls on {@link CheckSpec}.
 *
 * @author Florian Cramer
 *
 */
@UtilityClass
public class CheckSpecRunner {

	/**
	 * Generates reports for the given parameters.
	 *
	 * @param specClassNames
	 *            the class names for which specification should be created
	 * @param specClasspath
	 *            the classpath from which specifications are loaded
	 * @param implClasspath
	 *            the classpath from which implementations are loaded
	 * @param basePackage
	 *            the base package for all implementations
	 * @return the generated reports
	 */
	public static SpecReport[] generateReports(String[] specClassNames, URL[] specClasspath, URL[] implClasspath, String basePackage) {
		ClassLoader specificationClassLoader;
		if (specClasspath == null || specClasspath.length == 0) {
			specificationClassLoader = getBaseClassLoader();
		} else {
			specificationClassLoader = doPrivileged(() -> new URLClassLoader(specClasspath, getBaseClassLoader()));
		}

		Class<?>[] specificationClasses;
		if (specClassNames == null || specClassNames.length == 0) {
			specificationClasses = findClassAnnotatedWithEnabledSpec(specClasspath, specificationClassLoader);
		} else {
			specificationClasses = Arrays.stream(specClassNames)
					.flatMap(classStreamSupplier(specificationClassLoader))
					.toArray(i -> new Class<?>[i]);
		}

		ClassSpecification[] specifications = Arrays.stream(specificationClasses).parallel()
				.map(ClassSpecification::new)
				.toArray(ClassSpecification[]::new);

		CheckSpec checkSpec;
		if (implClasspath == null || implClasspath.length == 0) {
			checkSpec = CheckSpec.getDefaultInstance();
		} else {
			checkSpec = CheckSpec.getInstanceForClassPath(implClasspath);
		}

		List<SpecReport> ret = checkSpec.checkSpec(Arrays.asList(specifications), basePackage);
		return ret.toArray(new SpecReport[ret.size()]);
	}
}
