package checkspec.examples.example3;

import java.util.regex.Pattern;

import checkspec.specification.ClassSpecification;
import checkspec.specification.ClassSpecificationExtension;
import checkspec.type.ResolvableType;

public class RegexPackageClassSpecificationExtension implements ClassSpecificationExtension {

	@Override
	public void extend(ClassSpecification extensionPoint, ResolvableType payload) {
		Class<?> clazz = payload.getRawClass();
		RegexPackage regexPackage = clazz.getAnnotation(RegexPackage.class);

		if (regexPackage == null) {
			Package pkg = clazz.getPackage();
			regexPackage = pkg.getAnnotation(RegexPackage.class);
		}

		if (regexPackage != null) {
			String regex = regexPackage.value();
			int flags = regexPackage.flags();

			Pattern pattern = Pattern.compile(regex, flags);
			extensionPoint.addExtension(new RegexPackageSpecification(pattern));
		}
	}
}
