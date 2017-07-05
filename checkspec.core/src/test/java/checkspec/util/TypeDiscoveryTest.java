package checkspec.util;

import static checkspec.util.TypeDiscovery.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import checkspec.analysis.AnalysisForClass;
import checkspec.report.Report;

public class TypeDiscoveryTest {

	// Same test two times to cover saved results

	@Test
	public void getSubTypesOfTest() {
		List<Class<?>> result = getSubTypesOf(Report.class);
		assertThat(result, is(not(empty())));
	}

	@Test
	public void getSubTypesOfTest2() {
		List<Class<?>> result = getSubTypesOf(Report.class);
		assertThat(result, is(not(empty())));
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void getNewInstancesOfTest() {
		List<AnalysisForClass> result = getNewInstancesOf(AnalysisForClass.class);
		assertThat(result, is(not(empty())));
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void getNewInstancesOfWithErrorFormatTest() {
		List<AnalysisForClass> result = getNewInstancesOf(AnalysisForClass.class, "");
		assertThat(result, is(not(empty())));
	}

	// Same test two times to cover saved results

	@Test
	@SuppressWarnings("rawtypes")
	public void getUniqueInstancesOfTest() {
		List<AnalysisForClass> result = getUniqueInstancesOf(AnalysisForClass.class);
		assertThat(result, is(not(empty())));
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void getUniqueInstancesOfTest2() {
		List<AnalysisForClass> result = getUniqueInstancesOf(AnalysisForClass.class);
		assertThat(result, is(not(empty())));
	}

	// Same test two times to cover saved results

	@Test
	@SuppressWarnings("rawtypes")
	public void getUniqueInstancesOfWithErrorFormatTest() {
		List<AnalysisForClass> result = getUniqueInstancesOf(AnalysisForClass.class, "");
		assertThat(result, is(not(empty())));
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void getUniqueInstancesOfWithErrorFormatTest2() {
		List<AnalysisForClass> result = getUniqueInstancesOf(AnalysisForClass.class, "");
		assertThat(result, is(not(empty())));
	}
}
