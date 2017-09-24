package checkspec.report.output.html;

import org.apache.commons.text.StringEscapeUtils;

/**
 * Represents a hyperlink row inside of an html file
 *
 * @author Florian Cramer
 *
 */
class LinkRow extends Row {

	private static final String FORMAT = "<a href=\"%s\">%s</a>";

	private final String link;

	public LinkRow(String text, String link) {
		super(0, null, text);
		this.link = link;
	}

	@Override
	protected String createTextElement() {
		if (link != null && !link.trim().equals("")) {
			return String.format(FORMAT, StringEscapeUtils.escapeHtml4(link), StringEscapeUtils.escapeHtml4(getText()));
		} else {
			return super.createTextElement();
		}
	}
}
