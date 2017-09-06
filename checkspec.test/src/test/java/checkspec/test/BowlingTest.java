package checkspec.test;

import static org.assertj.core.api.Assertions.*;

import org.junit.Before;
import org.junit.Test;

import checkspec.cli.CommandLineException;
import checkspec.cli.CommandLineInterface;
import checkspec.report.SpecReport;
import checkspec.report.output.Outputter;

public class BowlingTest extends AbstractIntegrationTest {

	private CommandLineInterface cli;

	@Before
	public void setUp() {
		cli = new CommandLineInterface();
	}

	@Test
	public void bowlingTest() throws CommandLineException {
		String[] specClassNames = { "bowling.Bowling", "bowling.Game", "bowling.Player", "bowling.TannenbaumKegeln" };
		SpecReport[] reports = cli.run(specClassNames, getSpecClasspath(), getImplementationClasspath(), "bowling", Outputter.NULL_OUTPUTTER);

		assertThat(reports).hasSize(4);
	}
}
