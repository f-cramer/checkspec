package checkspec.report.output.html;

import java.util.StringJoiner;

import org.apache.commons.text.StringEscapeUtils;

import com.google.common.base.Strings;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
final class Row {

	private static final String TD = "<td>%s</td>";
	private static final String SPAN = "<span>%s</span>";
	private static final String SPAN_CLASS = "<span class=\"%s\">%s</span>";

	private static final String NO_BREAK_SPACE = "\u00a0";
	private static final String INDENTATION = Strings.repeat(NO_BREAK_SPACE, 4);

	@Getter(AccessLevel.NONE)
	private final int indent;
	private final Mark mark;
	private final String text;

	public final String getIndentation() {
		return Strings.repeat(INDENTATION, indent);
	}

	public Row withIncreasedIndent() {
		return new Row(indent + 1, mark, text);
	}

	public String toString() {
		StringJoiner spans = new StringJoiner("");
		
		String indentation = getIndentation();
		if (mark != null && !indentation.isEmpty()) {
			spans.add(String.format(SPAN, indentation));
		}

		if (mark != null) {
			spans.add(String.format(SPAN_CLASS, mark.getClassName(), mark.getText()));
		}

		spans.add(String.format(SPAN, StringEscapeUtils.escapeHtml4(text)));
		return String.format(TD, spans.toString());
	}

	public static final Row EMPTY = new Row(0, null, NO_BREAK_SPACE);
}
