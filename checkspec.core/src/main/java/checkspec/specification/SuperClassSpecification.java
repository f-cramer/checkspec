package checkspec.specification;

import java.util.List;

import checkspec.extension.AbstractExtendable;
import checkspec.type.ResolvableType;
import checkspec.util.ClassUtils;
import checkspec.util.TypeDiscovery;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
public class SuperClassSpecification extends AbstractExtendable<SuperClassSpecification, ResolvableType> {

	private static final SuperClassSpecificationExtension[] EXTENSIONS;

	static {
		List<SuperClassSpecificationExtension> instances = TypeDiscovery.getUniqueInstancesOf(SuperClassSpecificationExtension.class);
		EXTENSIONS = instances.toArray(new SuperClassSpecificationExtension[instances.size()]);
	}

	@NonNull
	private final String name;

	@NonNull
	private final ResolvableType rawElement;

	public SuperClassSpecification(Class<?> superClass) {
		this.name = superClass == null ? ClassUtils.getName(Object.class) : ClassUtils.getName(superClass);
		rawElement = ResolvableType.forClass(superClass);

		performExtensions(EXTENSIONS, this, rawElement);
	}
}
