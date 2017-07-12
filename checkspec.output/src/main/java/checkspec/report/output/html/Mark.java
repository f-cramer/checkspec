package checkspec.report.output.html;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
enum Mark {

	SUCCESS("mark-success", "✓"), ERROR("mark-error", "✗"), WARNING("mark-warning", " ! ");

	private final String className;
	private final String text;
}
