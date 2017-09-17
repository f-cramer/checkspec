package checkspec.test;

import static checkspec.CheckSpecRunner.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import checkspec.cli.CommandLineException;
import checkspec.report.SpecReport;

public class BowlingTest extends AbstractIntegrationTest {

	@Test
	public void bowlingTest() throws CommandLineException {
		String[] specClassNames = { "bowling.Bowling", "bowling.Game", "bowling.Player", "bowling.TannenbaumKegeln" };
		SpecReport[] reports = generateReports(specClassNames, getSpecClasspath(), getImplementationClasspath(), "bowling");

		assertThat(reports).hasSize(4);
	}
}
