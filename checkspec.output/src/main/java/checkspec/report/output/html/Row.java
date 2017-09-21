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



import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.text.StringEscapeUtils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * Represents a row inside of an HTML file.
 *
 * @author Florian Cramer
 *
 */
@Value
@RequiredArgsConstructor
final class Row {

	private static final String TD = "<td>%s</td>";
	private static final String SPAN = "<span>%s</span>";
	private static final String SPAN_CLASS = "<span class=\"%s\">%s</span>";

	private static final String NO_BREAK_SPACE = "\u00a0";
	private static final String INDENTATION = repeat(NO_BREAK_SPACE, 4);

	@Getter(AccessLevel.NONE)
	private final int indent;
	private final Mark mark;
	private final String text;

	public final String getIndentation() {
		return repeat(INDENTATION, indent);
	}

	public Row withIncreasedIndent() {
		return new Row(indent + 1, mark, text);
	}

	@Override
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

	private static String repeat(String s, int times) {
		return Stream.generate(() -> s)
				.limit(times)
				.collect(Collectors.joining());
	}
}
