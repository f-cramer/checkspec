package checkspec.report.output.html;

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



import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.text.StringEscapeUtils;

import lombok.NonNull;
import lombok.Value;

/**
 * Represents an HTML file.
 *
 * @author Florian Cramer
 *
 */
@Value
class HtmlFile {

	private static final String CONTENT = "<!DOCTYPE html>%n"
			+ "<html>%n"
			+ "<head><meta charset=\"UTF-8\"/><link rel=\"stylesheet\" href=\"style.css\"></head>%n"
			+ "<body>%n%s%n%s%n</body>%n"
			+ "</html>";
	private static final String H1 = "<h1>%s</h1>";
	private static final String TABLE = "<table>%s</table>";
	private static final String TR = "<tr>%s</tr>";

	@NonNull
	private String title;
	@NonNull
	private List<Row> rows;

	@Override
	public String toString() {
		String h1 = String.format(H1, StringEscapeUtils.escapeHtml4(title));
		String entries = rows.stream()
				.map(Row::toString)
				.map(e -> String.format(TR, e))
				.collect(Collectors.joining());

		String table = String.format(TABLE, entries);
		return String.format(CONTENT, h1, table);
	}
}
