package checkspec.spec;

import java.lang.reflect.Field;

import checkspec.spring.ResolvableType;
import checkspec.util.FieldUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FieldSpecification implements Specification<Field> {

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
	}
}
