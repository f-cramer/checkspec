package checkspec.report.output.html;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Mark {

	private final String className;
	private final String text;

	public static Mark SUCCESS = new Mark("mark-success", "\u2713");
	public static Mark ERROR = new Mark("mark-error", "\u2717");
	// public static Mark WARNING = new Mark("mark-warning", "\uff01");
	public static Mark WARNING = new Mark("mark-warning", "\u00a0!\u00a0");
}
