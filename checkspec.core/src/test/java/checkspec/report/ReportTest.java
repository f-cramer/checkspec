package checkspec.report;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import checkspec.specification.ClassSpecification;
import checkspec.spring.ResolvableType;
import checkspec.util.ClassUtils;

public class ReportTest {

	private ResolvableType type;
	private ClassSpecification specification;
	private Report<ResolvableType, ClassSpecification> report;
	private Report<ResolvableType, ClassSpecification> reportWithoutImplementation;
	private Report<ResolvableType, ClassSpecification> reportWithoutProblems;
	private ReportProblem warning = new ReportProblem(1, "Warning", ReportProblemType.WARNING);
	private ReportProblem error = new ReportProblem(10, "Error", ReportProblemType.ERROR);

	@Before
	public void setUp() {
		type = ResolvableType.forClass(ReportTest.class);
		specification = new ClassSpecification(ReportTest.class);

		report = new TestReport(specification, type, "Report");
		reportWithoutImplementation = new TestReport(specification);
		reportWithoutProblems = new TestReport(specification, type);

		report.addProblem(warning);
		report.addProblem(error);
	}

	@Test(expected = NullPointerException.class)
	public void constructorSpecificationTypeNullTest() {
		new TestReport(null);
	}

	@Test(expected = NullPointerException.class)
	public void constructorSpecificationTypeRawTypeNullTest() {
		new TestReport(null, type);
	}

	@Test(expected = NullPointerException.class)
	public void constructorSpecificationTypeRawTypeStringNullTest() {
		new TestReport(null, type, "");
	}

	@Test
	public void getSpecTest() {
		ClassSpecification result = report.getSpec();
		assertThat(result, is(specification));
	}

	@Test
	public void getImplementationTest() {
		ResolvableType result = report.getImplementation();
		assertThat(result, is(type));
	}

	@Test
	public void getProblemsTest() {
		List<ReportProblem> result = report.getProblems();
		assertThat(result, hasSize(2));
		assertThat(result, hasItems(warning, error));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void getProblemsUnmodifiableTest() {
		List<ReportProblem> result = report.getProblems();
		result.add(null);
	}

	@Test
	public void getSubReportsTest() {
		List<Report<?, ?>> result = report.getSubReports();
		assertThat(result, is(empty()));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void getSubReportsUnmodifiableTest() {
		List<Report<?, ?>> result = report.getSubReports();
		result.add(null);
	}

	@Test(expected = NullPointerException.class)
	public void addProblemNullTest() {
		report.addProblem(null);
	}

	@Test
	public void addProblemsTest() {
		report.addProblems(Arrays.asList(warning, error));
		assertThat(report.getProblems(), hasSize(4));

		report.addProblems(Arrays.asList(warning, null));
		assertThat(report.getProblems(), hasSize(5));
	}

	@Test(expected = NullPointerException.class)
	public void addProblemsNullTest() {
		report.addProblems(null);
	}

	@Test
	public void getTypeTest() {
		ReportType result = report.getType();
		assertThat(result, is(ReportType.ERROR));

		result = reportWithoutProblems.getType();
		assertThat(result, is(ReportType.SUCCESS));

		reportWithoutProblems.addProblem(warning);
		result = reportWithoutProblems.getType();
		assertThat(result, is(ReportType.WARNING));

		result = reportWithoutImplementation.getType();
		assertThat(result, is(ReportType.ERROR));
	}

	@Test
	public void isNameFittingTest() {
		boolean result = report.isNameFitting();
		assertThat(result, is(true));

		result = reportWithoutImplementation.isNameFitting();
		assertThat(result, is(false));
	}

	@Test
	public void getScoreTest() {
		int result = report.getScore();
		assertThat(result, is(11));

		result = reportWithoutImplementation.getScore();
		assertThat(result, is(100));

		result = reportWithoutProblems.getScore();
		assertThat(result, is(0));
	}

	@Test
	public void toStringTest() {
		String result = report.toString();
		assertThat(result, is("Report [11]"));

		result = reportWithoutImplementation.toString();
		assertThat(result, is("null [100]"));

		result = reportWithoutProblems.toString();
		assertThat(result, is("null"));
	}

	@Test
	public void compareToTest() {
		int result = report.compareTo(null);
		assertThat(result, is(1));

		result = report.compareTo(reportWithoutProblems);
		assertThat(result, is(1));

		result = report.compareTo(report);
		assertThat(result, is(0));
	}

	private static class TestReport extends Report<ResolvableType, ClassSpecification> {

		public TestReport(ClassSpecification spec, ResolvableType implementation, String title) {
			super(spec, implementation, title);
		}

		public TestReport(ClassSpecification spec, ResolvableType implementation) {
			super(spec, implementation);
		}

		public TestReport(ClassSpecification spec) {
			super(spec);
		}

		@Override
		protected String getRawTypeName(ResolvableType rawType) {
			return ClassUtils.getName(rawType);
		}
	}
}
