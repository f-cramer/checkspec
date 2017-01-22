package checkspec;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import checkspec.report.ErrorReport;
import test.Calc;

public class Main {

	public static void main(String[] args) {
		CheckSpec checkSpec = new CheckSpec();
		List<Pair<Class<?>, ErrorReport>> pairs = checkSpec.checkSpec(Calc.class);

		ErrorReport report = ErrorReport.success("Reports");
		pairs.parallelStream().map(Pair::getRight).forEachOrdered(report::add);

		new CheckSpecWindow(report).setVisible(true);
	}
}
