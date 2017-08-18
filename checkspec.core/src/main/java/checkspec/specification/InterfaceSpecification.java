package checkspec.specification;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import checkspec.extension.AbstractExtendable;
import checkspec.type.MatchableType;
import checkspec.util.ClassUtils;
import checkspec.util.TypeDiscovery;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class InterfaceSpecification extends AbstractExtendable<InterfaceSpecification, MatchableType> implements Specification<MatchableType>, Comparable<InterfaceSpecification> {

	private static final InterfaceSpecificationExtension[] EXTENSIONS;

	static {
		List<InterfaceSpecificationExtension> instances = TypeDiscovery.getUniqueInstancesOf(InterfaceSpecificationExtension.class);
		EXTENSIONS = instances.toArray(new InterfaceSpecificationExtension[instances.size()]);
	}

	@NonNull
	private final String name;

	@NonNull
	private final MatchableType rawElement;

	public InterfaceSpecification(MatchableType interf) {
		this.name = ClassUtils.getName(interf);
		rawElement = interf;

		performExtensions(EXTENSIONS, this, rawElement);
	}

	@Override
	public int compareTo(InterfaceSpecification o) {
		return Objects.compare(name, o.name, Comparator.naturalOrder());
	}
}
