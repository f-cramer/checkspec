package checkspec.test;

import checkspec.analysis.LevenshteinDetailedDistance;

public class Main {

	public static void main(String[] args) throws Exception {
		new LevenshteinDetailedDistance().apply("left", "2ldgt1");

		// IntStream.iterate(0, i -> i +
		// 1).parallel().filter(Character::isMirrored).findFirst().ifPresent(System.out::println);
		//
		// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		//
		// CheckSpec checkSpec = CheckSpec.getInstanceForClassPathWithoutJars();
		// Class<?> clazz = Calculator.class;
		//
		// SpecReport report = checkSpec.checkSpec(new
		// ClassSpecification(clazz), Main.class);
		// // SpecReport report = checkSpec.checkSpec(ClassSpec.from(clazz));
		//
		// Outputter outputter = new TextOutputter(new
		// OutputStreamWriter(System.out));
		// outputter.output(report);
		//
		// if (args.length > 0) {
		// Path path = Paths.get(args[0]);
		// if (Files.notExists(path) || Files.isDirectory(path)) {
		// if (Files.isWritable(path)) {
		// outputter = new HtmlOutputter(path);
		// outputter.output(report);
		// }
		// }
		// }
		//
		// outputter = new GuiOutputter();
		// outputter.output(report);
		//
		// Calculator proxy = CheckSpec.createProxy(report);
		// System.out.println(proxy.add(1, 2));
	}
}
