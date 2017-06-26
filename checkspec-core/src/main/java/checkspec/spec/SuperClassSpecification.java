package checkspec.spec;

import java.util.List;

import checkspec.extension.SuperClassSpecificationExtension;
import checkspec.spring.ResolvableType;
import checkspec.util.ClassUtils;
import checkspec.util.TypeDiscovery;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
public class SuperClassSpecification extends AbstractExtendable {

	private static final SuperClassSpecificationExtension[] EXTENSIONS;

	static {
		List<SuperClassSpecificationExtension> instances = TypeDiscovery.getInstancesOf(SuperClassSpecificationExtension.class);
		EXTENSIONS = instances.toArray(new SuperClassSpecificationExtension[instances.size()]);
	}

	@NonNull
	private final String name;

	@NonNull
	private final ResolvableType rawElement;

	public SuperClassSpecification(Class<?> superClass) {
		this.name = ClassUtils.getName(superClass);
		rawElement = ResolvableType.forClass(superClass);

		performExtensions(EXTENSIONS, superClass, this);
	}
}
