package checkspec.report;

public interface ReportEntry {

	public int getScore();
	
	public Type getType();

	public default boolean isSuccess() {
		return getScore() == 0;
	}
}
