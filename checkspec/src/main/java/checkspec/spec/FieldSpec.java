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
public class FieldSpec implements Specification<Field> {

	@NonNull
	private String name;

	@NonNull
	private ResolvableType type;

	@NonNull
	private ModifiersSpec modifiers;

	@NonNull
	private VisibilitySpec visibility;

	@NonNull
	private Field rawElement;

	public static FieldSpec from(Field field) {
		String name = field.getName();
		ResolvableType type = FieldUtils.getType(field);
		ModifiersSpec modifiers = ModifiersSpec.from(field.getModifiers());
		VisibilitySpec visibility = VisibilitySpec.from(field.getModifiers(), field.getAnnotations());

		return new FieldSpec(name, type, modifiers, visibility, field);
	}
}
