package checkspec.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class AnnotationProxy {

	@SuppressWarnings("unchecked")
	public static <T extends Annotation> T from(Class<T> clazz, Annotation annotation) {
		return (T) Proxy.newProxyInstance(AnnotationProxy.class.getClassLoader(), new Class<?>[] { clazz }, (proxy, method, args) -> invoke(annotation, method, args));
	}

	private static Object invoke(Annotation annotation, Method method, Object[] args) throws Throwable {
		Method proxyMethod = annotation.annotationType().getMethod(method.getName(), method.getParameterTypes());
		return proxyMethod.invoke(annotation, args);
	}
}
