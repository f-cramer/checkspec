package checkspec.test;

import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;

import javax.swing.UIManager;

import checkspec.CheckSpec;
import checkspec.examples.example1.Calculator;
import checkspec.report.SpecReport;
import checkspec.report.output.Outputter;
import checkspec.report.output.gui.GuiOutputter;
import checkspec.report.output.html.HtmlOutputter;
import checkspec.report.output.text.TextOutputter;
import checkspec.specification.ClassSpecification;

public class Main {

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		CheckSpec checkSpec = CheckSpec.getInstanceForClassPathWithoutJars();
		Class<?> clazz = Calculator.class;

		Set<ClassSpecification> specifications = Collections.singleton(new ClassSpecification(clazz));
		SpecReport report = checkSpec.checkSpec(specifications, clazz).get(0);

		Outputter outputter = new TextOutputter(new OutputStreamWriter(System.out));
		outputter.output(report);

		if (args.length > 0) {
			Path path = Paths.get(args[0]);
			if (Files.notExists(path) || Files.isDirectory(path)) {
				if (Files.isWritable(path)) {
					outputter = new HtmlOutputter(path);
					outputter.output(report);
				}
			}
		}

		outputter = new GuiOutputter();
		outputter.output(report);

		Calculator proxy = CheckSpec.createProxy(report);
		System.out.println(proxy.add(1, 2));
	}
}
