package checkspec.report.output.html;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.text.StringEscapeUtils;

import lombok.NonNull;
import lombok.Value;

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
