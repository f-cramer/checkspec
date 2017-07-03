package checkspec.util;

import static checkspec.util.ReflectionsUtils.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

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
		assertThat(result, is(notNullValue()));

		Configuration configuration = result.getConfiguration();
		assertThat(configuration, is(notNullValue()));

		Set<URL> urls = configuration.getUrls();
		assertThat(urls, hasSize(is(greaterThan(0))));
	}

	@Test
	public void createReflectionsTest() {
		Reflections result = createReflections(new URL[0]);
		assertThat(result, is(notNullValue()));

		Configuration configuration = result.getConfiguration();
		assertThat(configuration, is(notNullValue()));

		Set<URL> urls = configuration.getUrls();
		assertThat(urls, is(empty()));
	}

	@Test
	public void getUrlsFromClasspathTest() {
		URL[] result = getUrlsFromClasspath();
		assertThat(result, is(arrayWithSize(greaterThan(0))));
	}

	@Test
	public void getAsUrlStreamTest() {
		List<URL> result = getAsUrlStream(".").collect(Collectors.toList());
		assertThat(result, hasSize(1));

		result = getAsUrlStream("/Path/Not/To/Be/Found").collect(Collectors.toList());
		assertThat(result, is(empty()));
	}
}
