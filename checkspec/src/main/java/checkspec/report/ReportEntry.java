package checkspec.report;

public interface ReportEntry {

	public int getScore();

	public default boolean isSuccess() {
		return getScore() == 0;
	}
}
