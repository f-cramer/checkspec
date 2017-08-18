package checkspec.specification;

import java.util.List;

import checkspec.extension.AbstractExtendable;
import checkspec.type.MatchableType;
import checkspec.util.ClassUtils;
import checkspec.util.TypeDiscovery;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
public class SuperclassSpecification extends AbstractExtendable<SuperclassSpecification, MatchableType> implements Specification<MatchableType> {

	private static final SuperclassSpecificationExtension[] EXTENSIONS;

	static {
		List<SuperclassSpecificationExtension> instances = TypeDiscovery.getUniqueInstancesOf(SuperclassSpecificationExtension.class);
		EXTENSIONS = instances.toArray(new SuperclassSpecificationExtension[instances.size()]);
	}

	@NonNull
	private final String name;

	@NonNull
	private final MatchableType rawElement;

	public SuperclassSpecification(Class<?> superClass) {
		this(MatchableType.forClass(superClass));
	}

	public SuperclassSpecification(MatchableType superType) {
		rawElement = superType;
		this.name = rawElement == null ? ClassUtils.getName(Object.class) : ClassUtils.getName(rawElement);

		performExtensions(EXTENSIONS, this, rawElement);
	}
}
