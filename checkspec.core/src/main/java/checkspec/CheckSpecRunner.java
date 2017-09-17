package checkspec;

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

@UtilityClass
public class CheckSpecRunner {

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
