package checkspec.specification;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import checkspec.extension.AbstractExtendable;
import checkspec.spring.ResolvableType;
import checkspec.util.ClassUtils;
import checkspec.util.TypeDiscovery;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
public class InterfaceSpecification extends AbstractExtendable<InterfaceSpecification, ResolvableType> implements Specification<ResolvableType>, Comparable<InterfaceSpecification> {

	private static final InterfaceSpecificationExtension[] EXTENSIONS;

	static {
		List<InterfaceSpecificationExtension> instances = TypeDiscovery.getUniqueInstancesOf(InterfaceSpecificationExtension.class);
		EXTENSIONS = instances.toArray(new InterfaceSpecificationExtension[instances.size()]);
	}

	@NonNull
	private final String name;

	@NonNull
	private final ResolvableType rawElement;

	public InterfaceSpecification(Class<?> interf) {
		this.name = ClassUtils.getName(interf);
		rawElement = ResolvableType.forClass(interf);

		performExtensions(EXTENSIONS, this, rawElement);
	}

	@Override
	public int compareTo(InterfaceSpecification o) {
		return Objects.compare(name, o.name, Comparator.naturalOrder());
	}
}
