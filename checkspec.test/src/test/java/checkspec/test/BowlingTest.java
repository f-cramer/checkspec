package checkspec.test;

/*-
 * #%L
 * CheckSpec Test
 * %%
 * Copyright (C) 2017 Florian Cramer
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.Test;

import checkspec.cli.CommandLineException;
import checkspec.report.ClassReport;
import checkspec.report.SpecReport;

public class BowlingTest extends AbstractIntegrationTest {

	private static final String BOWLING = "bowling.Bowling";
	private static final String GAME = "bowling.Game";
	private static final String PLAYER = "bowling.Player";
	private static final String TANNENBAUM_KEGELN = "bowling.TannenbaumKegeln";

	@Test
	public void bowlingTest() throws CommandLineException {
		String[] specClassNames = { BOWLING, GAME, PLAYER, TANNENBAUM_KEGELN };
		SpecReport[] reports = generateReports("bowling", specClassNames);

		assertThat(reports).hasSize(4);

		List<ClassReport> bowlingReports = findClassReportsForSpecificationClass(reports, BOWLING);
		assertThat(bowlingReports).hasSize(5);
		assertThat(getNameOfBestImplementation(bowlingReports)).isEqualTo(BOWLING);

		List<ClassReport> gameReports = findClassReportsForSpecificationClass(reports, GAME);
		assertThat(gameReports).hasSize(4);
		assertThat(getNameOfBestImplementation(gameReports)).isEqualTo(GAME);

		List<ClassReport> playerReports = findClassReportsForSpecificationClass(reports, PLAYER);
		assertThat(playerReports).hasSize(3);
		assertThat(getNameOfBestImplementation(playerReports)).isEqualTo(PLAYER);

		List<ClassReport> tannenbaumKegelnReports = findClassReportsForSpecificationClass(reports, TANNENBAUM_KEGELN);
		assertThat(tannenbaumKegelnReports).hasSize(5);
		assertThat(getNameOfBestImplementation(tannenbaumKegelnReports)).isEqualTo(TANNENBAUM_KEGELN);
	}
}
