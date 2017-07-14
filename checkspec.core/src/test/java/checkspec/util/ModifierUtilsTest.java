package checkspec.util;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import checkspec.type.ResolvableType;

public class ModifierUtilsTest {

	@Test
	public void toStringResolvableTypeTest() {
		String result = ModifierUtils.toString(ResolvableType.forClass(ModifierUtilsTest.class));
		assertThat(result, is("public"));

		result = ModifierUtils.toString(ResolvableType.forClass(PublicStaticFinalClass.class));
		assertThat(result, is("private static final"));

		result = ModifierUtils.toString(ResolvableType.forClass(ProtectedStaticClass.class));
		assertThat(result, is("protected abstract static"));

		result = ModifierUtils.toString(ResolvableType.forClass(Interface.class));
		assertThat(result, is(""));

		result = ModifierUtils.toString(ResolvableType.forClass(Enum.class));
		assertThat(result, is(""));
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
