package checkspec.analysis;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import checkspec.extension.AbstractExtendable;
import checkspec.specification.Specification;
import checkspec.type.MatchableType;
import checkspec.util.TypeDiscovery;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class ExceptionSpecification extends AbstractExtendable<ExceptionSpecification, MatchableType[]> implements Specification<MatchableType[]> {

	private static final ExceptionSpecificationExtension[] EXTENSIONS;

	static {
		List<ExceptionSpecificationExtension> instances = TypeDiscovery.getUniqueInstancesOf(ExceptionSpecificationExtension.class);
		EXTENSIONS = instances.toArray(new ExceptionSpecificationExtension[instances.size()]);
	}

	private final String name;
	private final MatchableType[] rawElement;

	public ExceptionSpecification(@NonNull MatchableType[] throwables) {
		this.name = Arrays.stream(throwables)
				.map(MatchableType::toString)
				.collect(Collectors.joining(", "));
		this.rawElement = throwables;

		performExtensions(EXTENSIONS, this, rawElement);
	}
}
