package checkspec.spec;

import java.util.List;

import checkspec.extension.InterfaceSpecificationExtension;
import checkspec.spring.ResolvableType;
import checkspec.util.ClassUtils;
import checkspec.util.TypeDiscovery;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
public class InterfaceSpecification extends AbstractExtendable {

	private static final InterfaceSpecificationExtension[] EXTENSIONS;

	static {
		List<InterfaceSpecificationExtension> instances = TypeDiscovery.getInstancesOf(InterfaceSpecificationExtension.class);
		EXTENSIONS = instances.toArray(new InterfaceSpecificationExtension[instances.size()]);
	}

	@NonNull
	private final String name;
	
	@NonNull
	private final ResolvableType rawElement;
	
	public InterfaceSpecification(Class<?> interf) {
		this.name = ClassUtils.getName(interf);
		rawElement = ResolvableType.forClass(interf);

		performExtensions(EXTENSIONS, interf, this);
	}
}
