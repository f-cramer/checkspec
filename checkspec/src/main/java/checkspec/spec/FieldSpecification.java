package checkspec.spec;

import java.lang.reflect.Field;

import javax.annotation.Nonnull;

import checkspec.spring.ResolvableType;
import checkspec.util.FieldUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FieldSpecification implements Specification<Field> {

	@Nonnull
	private final String name;

	@Nonnull
	private final ResolvableType type;

	@Nonnull
	private final ModifiersSpecification modifiers;

	@Nonnull
	private final VisibilitySpecification visibility;

	@Nonnull
	private final Field rawElement;

	public FieldSpecification(Field field) {
		name = field.getName();
		type = FieldUtils.getType(field);
		modifiers = new ModifiersSpecification(field.getModifiers(), field.getAnnotations());
		visibility = new VisibilitySpecification(field.getModifiers(), field.getAnnotations());
		rawElement = field;
	}
}
