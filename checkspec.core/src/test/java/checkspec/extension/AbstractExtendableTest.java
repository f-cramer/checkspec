package checkspec.extension;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

import checkspec.specification.ClassSpecification;
import checkspec.specification.ClassSpecificationExtension;
import checkspec.spring.ResolvableType;

public class AbstractExtendableTest {

	private String extension;
	private AbstractExtendable<ClassSpecification, ResolvableType> extendable;

	@Before
	public void setUp() {
		extendable = new AbstractExtendable<ClassSpecification, ResolvableType>() {
		};
		extension = "";
	}

	@Test
	public void addExtensionTest() {
		Optional<String> result = extendable.addExtension(extension);
		assertThat(result.isPresent(), is(false));

		result = extendable.addExtension("newExtension");
		assertThat(result.isPresent(), is(true));
		assertThat(result.get(), is(sameInstance(extension)));
	}

	@Test(expected = NullPointerException.class)
	public void addExtensionNullTest() {
		extendable.addExtension(null);
	}

	@Test
	public void getExtensionTest() {
		extendable.addExtension(extension);
		Optional<String> result = extendable.getExtension(String.class);
		assertThat(result.isPresent(), is(true));
		assertThat(result.get(), is(sameInstance(extension)));
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

		assertThat(counter.get(), is(2));
	}
}
