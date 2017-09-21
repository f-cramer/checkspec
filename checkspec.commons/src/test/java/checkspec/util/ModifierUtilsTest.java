package checkspec.util;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import checkspec.type.MatchableType;

public class ModifierUtilsTest {

	@Test
	public void toStringResolvableTypeTest() {
		String result = ModifierUtils.createString(MatchableType.forClass(ModifierUtilsTest.class));
		assertThat(result).isEqualTo("public");

		result = ModifierUtils.createString(MatchableType.forClass(PublicStaticFinalClass.class));
		assertThat(result).isEqualTo("private static final");

		result = ModifierUtils.createString(MatchableType.forClass(ProtectedStaticClass.class));
		assertThat(result).isEqualTo("protected abstract static");

		result = ModifierUtils.createString(MatchableType.forClass(Interface.class));
		assertThat(result).isEmpty();

		result = ModifierUtils.createString(MatchableType.forClass(Enum.class));
		assertThat(result).isEmpty();
	}

	@Test(expected = NullPointerException.class)
	public void toStringResolvableTypeNullTest() {
		ModifierUtils.createString(null);
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
