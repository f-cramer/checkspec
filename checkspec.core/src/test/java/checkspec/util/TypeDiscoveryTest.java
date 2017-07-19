package checkspec.util;

import static checkspec.util.TypeDiscovery.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.Test;

import checkspec.analysis.ClassAnalysis;
import checkspec.report.Report;

public class TypeDiscoveryTest {

	// Same test two times to cover saved results

	@Test
	public void getSubTypesOfTest() {
		List<Class<?>> result = getSubTypesOf(Report.class);
		assertThat(result).isNotEmpty();
	}

	@Test
	public void getSubTypesOfTest2() {
		List<Class<?>> result = getSubTypesOf(Report.class);
		assertThat(result).isNotEmpty();
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void getNewInstancesOfTest() {
		List<ClassAnalysis> result = getNewInstancesOf(ClassAnalysis.class);
		assertThat(result).isNotEmpty();
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void getNewInstancesOfWithErrorFormatTest() {
		List<ClassAnalysis> result = getNewInstancesOf(ClassAnalysis.class, "");
		assertThat(result).isNotEmpty();
	}

	// Same test two times to cover saved results

	@Test
	@SuppressWarnings("rawtypes")
	public void getUniqueInstancesOfTest() {
		List<ClassAnalysis> result = getUniqueInstancesOf(ClassAnalysis.class);
		assertThat(result).isNotEmpty();
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void getUniqueInstancesOfTest2() {
		List<ClassAnalysis> result = getUniqueInstancesOf(ClassAnalysis.class);
		assertThat(result).isNotEmpty();
	}

	// Same test two times to cover saved results

	@Test
	@SuppressWarnings("rawtypes")
	public void getUniqueInstancesOfWithErrorFormatTest() {
		List<ClassAnalysis> result = getUniqueInstancesOf(ClassAnalysis.class, "");
		assertThat(result).isNotEmpty();
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void getUniqueInstancesOfWithErrorFormatTest2() {
		List<ClassAnalysis> result = getUniqueInstancesOf(ClassAnalysis.class, "");
		assertThat(result).isNotEmpty();
	}
}
