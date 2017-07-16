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
public class SuperclassSpecification extends AbstractExtendable<SuperclassSpecification, ResolvableType> {

	private static final SuperclassSpecificationExtension[] EXTENSIONS;

	static {
		List<SuperclassSpecificationExtension> instances = TypeDiscovery.getUniqueInstancesOf(SuperclassSpecificationExtension.class);
		EXTENSIONS = instances.toArray(new SuperclassSpecificationExtension[instances.size()]);
	}

	@NonNull
	private final String name;

	@NonNull
	private final ResolvableType rawElement;

	public SuperclassSpecification(Class<?> superClass) {
		this(ResolvableType.forClass(superClass));
	}

	public SuperclassSpecification(ResolvableType superType) {
		rawElement = superType;
		this.name = rawElement == null ? ClassUtils.getName(Object.class) : ClassUtils.getName(rawElement);

		performExtensions(EXTENSIONS, this, rawElement);
	}
}
