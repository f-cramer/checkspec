package checkspec.specification;

import java.lang.annotation.Annotation;
import java.util.List;

import checkspec.extension.AbstractExtendable;
import checkspec.util.TypeDiscovery;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class PackageSpecification extends AbstractExtendable<PackageSpecification, Package> implements Specification<Package> {

	private static final PackageSpecificationExtension[] EXTENSIONS;

	static {
		List<PackageSpecificationExtension> instances = TypeDiscovery.getUniqueInstancesOf(PackageSpecificationExtension.class);
		EXTENSIONS = instances.toArray(new PackageSpecificationExtension[instances.size()]);
	}

	@NonNull
	private final String name;

	@NonNull
	private final Package rawElement;

	public PackageSpecification(Package pkg, Annotation[] annotations) {
		rawElement = pkg;
		name = pkg.getName();

		performExtensions(EXTENSIONS, this, rawElement);
	}
}
