package checkspec;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javassist.util.proxy.MethodHandler;

public interface MethodInvocationHandler extends InvocationHandler, MethodHandler {

	Object invokeImpl(Object proxy, Method method, Object[] args) throws Throwable;

	default Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return invokeImpl(proxy, method, args);
	}

	default Object invoke(Object proxy, Method method, Method proceed, Object[] args) throws Throwable {
		return invokeImpl(proxy, method, args);
	}
}
