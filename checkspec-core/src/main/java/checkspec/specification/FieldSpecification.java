package checkspec.specification;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import checkspec.extension.AbstractExtendable;
import checkspec.spring.ResolvableType;
import checkspec.util.FieldUtils;
import checkspec.util.TypeDiscovery;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class FieldSpecification extends AbstractExtendable<FieldSpecification, Field> implements MemberSpecification<Field>, Comparable<FieldSpecification> {

	private static final FieldSpecificationExtension[] EXTENSIONS;

	static {
		List<FieldSpecificationExtension> instances = TypeDiscovery.getUniqueInstancesOf(FieldSpecificationExtension.class);
		EXTENSIONS = instances.toArray(new FieldSpecificationExtension[instances.size()]);
	}

	@NonNull
	private final String name;

	@NonNull
	private final ResolvableType type;

	@NonNull
	private final ModifiersSpecification modifiers;

	@NonNull
	private final VisibilitySpecification visibility;

	@NonNull
	private final Field rawElement;

	public FieldSpecification(Field field) {
		name = field.getName();
		type = FieldUtils.getType(field);
		modifiers = new ModifiersSpecification(field.getModifiers(), field.getAnnotations());
		visibility = new VisibilitySpecification(field.getModifiers(), field.getAnnotations());
		rawElement = field;

		performExtensions(EXTENSIONS, this, field);
	}

	@Override
	public int compareTo(FieldSpecification o) {
		return Objects.compare(name, o.name, Comparator.naturalOrder());
	}
}
