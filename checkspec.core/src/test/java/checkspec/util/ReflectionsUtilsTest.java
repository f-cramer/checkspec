package checkspec.util;

import static checkspec.util.ReflectionsUtils.*;
import static org.assertj.core.api.Assertions.*;

import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;
import org.reflections.Configuration;
import org.reflections.Reflections;

public class ReflectionsUtilsTest {

	@Test
	public void createDefaultReflectionsTest() {
		Reflections result = createDefaultReflections();
		assertThat(result).isNotNull();

		Configuration configuration = result.getConfiguration();
		assertThat(configuration).isNotNull();

		Set<URL> urls = configuration.getUrls();
		assertThat(urls).isNotEmpty();
	}

	@Test
	public void createReflectionsTest() {
		Reflections result = createReflections(new URL[0]);
		assertThat(result).isNotNull();

		Configuration configuration = result.getConfiguration();
		assertThat(configuration).isNotNull();

		Set<URL> urls = configuration.getUrls();
		assertThat(urls).isEmpty();
	}

	// Two time the same test to test lazy calculation

	@Test
	public void getUrlsFromClasspathTest() {
		URL[] result = getUrlsFromClasspath();
		assertThat(result).isNotEmpty();
	}

	@Test
	public void getUrlsFromClasspathTest2() {
		URL[] result = getUrlsFromClasspath();
		assertThat(result).isNotEmpty();
	}

	@Test
	public void getAsUrlStreamTest() {
		List<URL> result = getAsUrlStream(".").collect(Collectors.toList());
		assertThat(result).hasSize(1);

		result = getAsUrlStream("/Path/Not/To/Be/Found").collect(Collectors.toList());
		assertThat(result).isEmpty();
	}
}
