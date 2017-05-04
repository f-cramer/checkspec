package checkspec.type;

import java.lang.reflect.Field;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FieldSpec implements MemberSpec<Field> {

	@NonNull
	private String name;
	
	@NonNull
	private Class<?> type;
	
	@NonNull
	private ModifiersSpec modifiers;
	
	@NonNull
	private Field rawElement;
	
	public static FieldSpec from(Field field) {
		String name = field.getName();
		Class<?> type = field.getType();
		ModifiersSpec modifiers = ModifiersSpec.from(field.getModifiers());

		return new FieldSpec(name, type, modifiers, field);
	}
}
