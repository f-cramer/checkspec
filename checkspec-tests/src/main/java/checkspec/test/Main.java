package checkspec.test;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import checkspec.CheckSpec;
import checkspec.MethodInvocationHandler;
import checkspec.gui.CheckSpecFrame;
import checkspec.report.ClassReport;
import checkspec.report.MethodReport;
import checkspec.report.SpecReport;
import checkspec.report.output.ConsoleOutputter;
import checkspec.report.output.Outputter;
import checkspec.test.generics.GenericTestImpl;
import checkspec.util.ClassUtils;
import checkspec.util.MethodUtils;
import javassist.util.proxy.ProxyFactory;

public class Main {

	private static Objenesis OBJENESIS = new ObjenesisStd();

	public static void main(String[] args) {
		Class<?> class1 = GenericTestImpl.class;
		Class<?>[] interfaces = class1.getClass().getInterfaces();
		System.out.println(Arrays.toString(interfaces));

		CheckSpec checkSpec = new CheckSpec();
		Class<Calc> clazz = Calc.class;

		SpecReport report = checkSpec.checkSpec(clazz);

		try {
			Calc proxy = createProxy(clazz, createInvocationHandler(clazz, report));
			System.out.println(proxy.add(1, 2));
		} catch (Throwable t) {
			t.printStackTrace();
		}

		Outputter outputter = new ConsoleOutputter();
		outputter.output(report);

		new CheckSpecFrame(report).setVisible(true);
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
			Class<?> implementingClass = classReport.getImplementingObject();
			String implementationName = ClassUtils.getName(implementingClass);
			Object implementation = implementingClass.newInstance();

			// @formatter:off
			Map<Method, MethodReport> methodReports = classReport.getMethodReports()
			                                                     .parallelStream()
			                                                     .map(e -> e)
			                                                     .collect(Collectors.toMap(e -> e.getSpecObject(), Function.identity()));
			// @formatter:on

			return (proxy, method, args) -> {
				MethodReport actualMethod = methodReports.get(method);

				if (actualMethod == null || actualMethod.getImplementingObject() == null) {
					String methodName = method.getName();
					String parameterList = MethodUtils.getParameterList(method);
					String format = "no implementation of %s#%s(%s) could be found in %s";
					throw new UnsupportedOperationException(String.format(format, specName, methodName, parameterList, implementationName));
				} else {
					return actualMethod.getImplementingObject().invoke(implementation, args);
				}
			};
		} catch (InstantiationException | IllegalAccessException e1) {
			throw new IllegalArgumentException();
		}

	}
}
