package checkspec.report.output.html;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Mark {

	private final String className;
	private final String text;

	public static Mark SUCCESS = new Mark("mark-success", "✓");
	public static Mark ERROR = new Mark("mark-error", "✗");
	// public static Mark WARNING = new Mark("mark-warning", "！");
	public static Mark WARNING = new Mark("mark-warning", " ! ");
}
