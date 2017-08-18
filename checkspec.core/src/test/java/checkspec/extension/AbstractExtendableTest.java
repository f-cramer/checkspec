package checkspec.extension;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

import checkspec.specification.ClassSpecification;
import checkspec.specification.ClassSpecificationExtension;
import checkspec.type.MatchableType;

public class AbstractExtendableTest {

	private String extension;
	private AbstractExtendable<ClassSpecification, MatchableType> extendable;

	@Before
	public void setUp() {
		extendable = new AbstractExtendable<ClassSpecification, MatchableType>() {
		};
		extension = "";
	}

	@Test
	public void addExtensionTest() {
		Optional<String> result = extendable.addExtension(extension);
		assertThat(result.isPresent()).isFalse();

		result = extendable.addExtension("newExtension");
		assertThat(result).isPresent().hasValueSatisfying(s -> assertThat(s).isSameAs(extension));
	}

	@Test(expected = NullPointerException.class)
	public void addExtensionNullTest() {
		extendable.addExtension(null);
	}

	@Test
	public void getExtensionTest() {
		extendable.addExtension(extension);
		Optional<String> result = extendable.getExtension(String.class);
		assertThat(result).isPresent().hasValueSatisfying(s -> assertThat(s).isSameAs(extension));
	}

	@Test(expected = NullPointerException.class)
	public void getExtensionNullTest() {
		extendable.getExtension(null);
	}

	@Test
	public void performExtensionsTest() {
		AtomicInteger counter = new AtomicInteger();
		ClassSpecificationExtension extension = (s, p) -> counter.incrementAndGet();
		ClassSpecificationExtension anotherExtension = (s, p) -> s.getClass();

		ClassSpecificationExtension[] extensions = { extension, extension, anotherExtension };
		extendable.performExtensions(extensions, null, null);

		assertThat(counter.get()).isEqualTo(2);
	}
}
