package checkspec.spec;

import java.lang.reflect.Field;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FieldSpec implements Spec<Field> {

	@NonNull
	private String name;
	
	@NonNull
	private Class<?> type;
	
	@NonNull
	private ModifiersSpec modifiers;
	
	@NonNull
	private VisibilitySpec visibility;
	
	@NonNull
	private Field rawElement;
	
	public static FieldSpec from(Field field) {
		String name = field.getName();
		Class<?> type = field.getType();
		ModifiersSpec modifiers = ModifiersSpec.from(field.getModifiers());
		VisibilitySpec visibility = VisibilitySpec.from(field.getModifiers(), field.getAnnotations());

		return new FieldSpec(name, type, modifiers, visibility, field);
	}
}
