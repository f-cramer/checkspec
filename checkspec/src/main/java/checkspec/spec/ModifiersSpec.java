package checkspec.spec;

import java.lang.reflect.Modifier;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ModifiersSpec {

	private final int modifiers;

	public boolean isStatic() {
		return Modifier.isStatic(modifiers);
	}

	public boolean isFinal() {
		return Modifier.isFinal(modifiers);
	}

	public boolean isSynchronized() {
		return Modifier.isSynchronized(modifiers);
	}

	public boolean isVolatile() {
		return Modifier.isVolatile(modifiers);
	}

	public boolean isTransient() {
		return Modifier.isTransient(modifiers);
	}

	public boolean isNative() {
		return Modifier.isNative(modifiers);
	}

	public boolean isInterface() {
		return Modifier.isInterface(modifiers);
	}

	public boolean isAbstract() {
		return Modifier.isAbstract(modifiers);
	}

	public boolean isStrict() {
		return Modifier.isStrict(modifiers);
	}

	public static ModifiersSpec from(int modifiers) {
		return new ModifiersSpec(modifiers);
	}
}
