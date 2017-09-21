package checkspec.report.output.gui;

/*-
 * #%L
 * CheckSpec Output
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



import checkspec.report.SpecReport;
import checkspec.report.output.Outputter;
import lombok.RequiredArgsConstructor;

/**
 * Represents an {@link Outputter} that outputs a given {@link SpecReport} via a
 * GUI.
 *
 * @author Florian Cramer
 *
 */
@RequiredArgsConstructor
public class GuiOutputter implements Outputter {

	private final CheckSpecFrame frame = new CheckSpecFrame();

	@Override
	public void output(SpecReport report) {
		frame.addReport(report);
	}

	@Override
	public void finished() {
		frame.finishedAddingReports();
		frame.setVisible(true);
	}
}
