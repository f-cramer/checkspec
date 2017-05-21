package checkspec.test;

import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.UIManager;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import checkspec.CheckSpec;
import checkspec.MethodInvocationHandler;
import checkspec.report.ClassReport;
import checkspec.report.MethodReport;
import checkspec.report.SpecReport;
import checkspec.report.output.Outputter;
import checkspec.report.output.TextOutputter;
import checkspec.report.output.gui.GuiOutputter;
import checkspec.report.output.html.HtmlOutputter;
import checkspec.spec.ClassSpecification;
import checkspec.util.ClassUtils;
import checkspec.util.MethodUtils;
import javassist.util.proxy.ProxyFactory;

public class Main {

	private static Objenesis OBJENESIS = new ObjenesisStd();

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		CheckSpec checkSpec = CheckSpec.getInstanceForClassPathWithoutJars();
		Class<?> clazz = Calculator.class;

		SpecReport report = checkSpec.checkSpec(new ClassSpecification(clazz), Main.class);
		// SpecReport report = checkSpec.checkSpec(ClassSpec.from(clazz));

		Outputter outputter = new TextOutputter(new OutputStreamWriter(System.out));
		outputter.output(report);

		if (args.length > 0) {
			Path path = Paths.get(args[0]);
			if (Files.notExists(path) || Files.isDirectory(path)) {
				outputter = new HtmlOutputter(path);
				outputter.output(report);
			}
		}

		outputter = new GuiOutputter();
		outputter.output(report);
	}

	@SuppressWarnings("unchecked")
	public static <T> T createProxy(Class<?> clazz, MethodInvocationHandler handler) {
		if (clazz.isInterface()) {
			return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[] { clazz }, handler);
		} else {
			ProxyFactory factory = new ProxyFactory();
			factory.setSuperclass(clazz);
			factory.setFilter(e -> !MethodUtils.isAbstract(e));
			Class<?> proxyClass = factory.createClass();
			T proxy = (T) OBJENESIS.newInstance(proxyClass);
			((javassist.util.proxy.Proxy) proxy).setHandler(handler);
			return proxy;
		}
	}

	@SuppressWarnings("unused")
	private static MethodInvocationHandler createInvocationHandler(Class<?> spec, SpecReport report) {
		final List<ClassReport> classReports = report.getClassReports();
		if (classReports.isEmpty()) {
			String format = "no implementation of \"%s\" could be found";
			String classString = ClassUtils.toString(spec);
			throw new UnsupportedOperationException(String.format(format, classString));
		}

		String specName = ClassUtils.getName(spec);

		try {
			ClassReport classReport = classReports.get(0);
			Class<?> implementingClass = classReport.getImplementation().getRawClass();
			String implementationName = ClassUtils.getName(implementingClass);
			Object implementation = implementingClass.newInstance();

			// @formatter:off
			Map<Method, MethodReport> methodReports = classReport.getMethodReports()
			                                                     .parallelStream()
			                                                     .collect(Collectors.toMap(e -> e.getSpec().getRawElement(), Function.identity()));
			// @formatter:on

			return (proxy, method, args) -> {
				MethodReport actualMethod = methodReports.get(method);

				if (actualMethod == null || actualMethod.getImplementation() == null) {
					String methodName = method.getName();
					String parameterList = MethodUtils.getParameterList(method);
					String format = "no implementation of %s#%s(%s) could be found in %s";
					throw new UnsupportedOperationException(String.format(format, specName, methodName, parameterList, implementationName));
				} else {
					return actualMethod.getImplementation().invoke(implementation, args);
				}
			};
		} catch (InstantiationException | IllegalAccessException e1) {
			throw new IllegalArgumentException();
		}

	}
}
