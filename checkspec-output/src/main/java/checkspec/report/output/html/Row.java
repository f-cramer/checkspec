package checkspec.report.output.html;

import com.google.common.base.Strings;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public final class Row {
	
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
	
	public static final Row EMPTY = new Row(0, null, NO_BREAK_SPACE);
}
