package checkspec.analysis;

import java.util.List;

import checkspec.extension.AbstractExtendable;
import checkspec.specification.Specification;
import checkspec.type.MatchableType;
import checkspec.util.TypeDiscovery;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class ExceptionSpecification extends AbstractExtendable<ExceptionSpecification, MatchableType> implements Specification<MatchableType> {

	private static final ExceptionSpecificationExtension[] EXTENSIONS;

	static {
		List<ExceptionSpecificationExtension> instances = TypeDiscovery.getUniqueInstancesOf(ExceptionSpecificationExtension.class);
		EXTENSIONS = instances.toArray(new ExceptionSpecificationExtension[instances.size()]);
	}

	private final String name;
	private final MatchableType rawElement;

	public ExceptionSpecification(@NonNull MatchableType throwable) {
		this.name = throwable.toString();
		this.rawElement = throwable;

		performExtensions(EXTENSIONS, this, rawElement);
	}
}
