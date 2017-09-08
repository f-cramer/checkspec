package checkspec;

import static checkspec.util.SecurityUtils.*;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;

import checkspec.report.SpecReport;
import checkspec.specification.ClassSpecification;
import checkspec.util.ClassUtils;
import checkspec.util.ReflectionsUtils;

public class CheckSpecRunner {

	public final SpecReport[] generateReports(String[] specClassNames, URL[] specClasspath, URL[] implClasspath, String basePackage) {
		ClassLoader specificationClassLoader;
		if (specClasspath.length == 0) {
			specificationClassLoader = ClassUtils.getBaseClassLoader();
		} else {
			specificationClassLoader = doPrivileged(() -> new URLClassLoader(specClasspath, ClassUtils.getBaseClassLoader()));
		}

		Class<?>[] specificationClasses;
		if (specClassNames.length == 0) {
			specificationClasses = ReflectionsUtils.findClassAnnotatedWithEnabledSpec(specClasspath, specificationClassLoader);
		} else {
			specificationClasses = Arrays.stream(specClassNames)
					.flatMap(ClassUtils.classStreamSupplier(specificationClassLoader))
					.toArray(i -> new Class<?>[i]);
		}

		ClassSpecification[] specifications = Arrays.stream(specificationClasses).parallel()
				.map(ClassSpecification::new)
				.toArray(ClassSpecification[]::new);

		CheckSpec checkSpec = implClasspath.length == 0 ? CheckSpec.getDefaultInstance() : CheckSpec.getInstanceForClassPath(implClasspath);

		List<SpecReport> ret = checkSpec.checkSpec(Arrays.asList(specifications), basePackage);
		return ret.toArray(new SpecReport[ret.size()]);
	}
}
