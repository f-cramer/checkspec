package checkspec.util;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import checkspec.type.ResolvableType;

public class ModifierUtilsTest {

	@Test
	public void toStringResolvableTypeTest() {
		String result = ModifierUtils.toString(ResolvableType.forClass(ModifierUtilsTest.class));
		assertThat(result).isEqualTo("public");

		result = ModifierUtils.toString(ResolvableType.forClass(PublicStaticFinalClass.class));
		assertThat(result).isEqualTo("private static final");

		result = ModifierUtils.toString(ResolvableType.forClass(ProtectedStaticClass.class));
		assertThat(result).isEqualTo("protected abstract static");

		result = ModifierUtils.toString(ResolvableType.forClass(Interface.class));
		assertThat(result).isEmpty();

		result = ModifierUtils.toString(ResolvableType.forClass(Enum.class));
		assertThat(result).isEmpty();
	}

	@Test(expected = NullPointerException.class)
	public void toStringResolvableTypeNullTest() {
		ModifierUtils.toString(null);
	}

	private static final class PublicStaticFinalClass {
	}

	protected abstract static class ProtectedStaticClass {
	}

	interface Interface {
	}

	enum Enum {
	}
}
