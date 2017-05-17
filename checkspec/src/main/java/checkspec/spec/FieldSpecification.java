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
	private String name;

	@NonNull
	private ResolvableType type;

	@NonNull
	private ModifiersSpecification modifiers;

	@NonNull
	private VisibilitySpecification visibility;

	@NonNull
	private Field rawElement;

	public static FieldSpecification from(Field field) {
		String name = field.getName();
		ResolvableType type = FieldUtils.getType(field);
		ModifiersSpecification modifiers = ModifiersSpecification.from(field.getModifiers(), field.getAnnotations());
		VisibilitySpecification visibility = VisibilitySpecification.from(field.getModifiers(), field.getAnnotations());

		return new FieldSpecification(name, type, modifiers, visibility, field);
	}
}
