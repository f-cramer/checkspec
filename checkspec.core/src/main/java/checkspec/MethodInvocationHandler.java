package checkspec;

/*-
 * #%L
 * CheckSpec Core
 * %%
 * Copyright (C) 2017 Florian Cramer
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javassist.util.proxy.MethodHandler;

/**
 * Encapsulates {@link InvocationHandler} and {@linkplain MethodHandler}.
 * 
 * @author Florian Cramer
 */
interface MethodInvocationHandler extends InvocationHandler, MethodHandler {

	/**
	 * The actual invocation that is called as an implementation of
	 * {@link InvocationHandler#invoke(Object, Method, Object[])} and
	 * {@link MethodHandler#invoke(Object, Method, Method, Object[])}
	 * 
	 * @param proxy
	 *            the proxy instance that the method was invoked on
	 * @param method
	 *            the Method instance corresponding to the interface method invoked
	 *            on the proxy instance. The declaring class of the Method object
	 *            will be the interface that the method was declared in, which may
	 *            be a superinterface of the proxy interface that the proxy class
	 *            inherits the method through.
	 * @param args
	 *            an array of objects containing the values of the arguments passed
	 *            in the method invocation on the proxy instance, or null if
	 *            interface method takes no arguments. Arguments of primitive types
	 *            are wrapped in instances of the appropriate primitive wrapper
	 *            class, such as java.lang.Integer or java.lang.Boolean.
	 * @return the value to return from the method invocation on the proxy instance.
	 *         If the declared return type of the interface method is a primitive
	 *         type, then the value returned by this method must be an instance of
	 *         the corresponding primitive wrapper class; otherwise, it must be a
	 *         type assignable to the declared return type. If the value returned by
	 *         this method is null and the interface method's return type is
	 *         primitive, then a NullPointerException will be thrown by the method
	 *         invocation on the proxy instance. If the value returned by this
	 *         method is otherwise not compatible with the interface method's
	 *         declared return type as described above, a ClassCastException will be
	 *         thrown by the method invocation on the proxy instance.
	 * @throws Throwable
	 *             the exception to throw from the method invocation on the proxy
	 *             instance. The exception's type must be assignable either to any
	 *             of the exception types declared in the throws clause of the
	 *             interface method or to the unchecked exception types
	 *             java.lang.RuntimeException or java.lang.Error. If a checked
	 *             exception is thrown by this method that is not assignable to any
	 *             of the exception types declared in the throws clause of the
	 *             interface method, then an UndeclaredThrowableException containing
	 *             the exception that was thrown by this method will be thrown by
	 *             the method invocation on the proxy instance.
	 */
	Object invokeImpl(Object proxy, Method method, Object[] args) throws Throwable;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
	 * java.lang.reflect.Method, java.lang.Object[])
	 */
	default Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return invokeImpl(proxy, method, args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javassist.util.proxy.MethodHandler#invoke(java.lang.Object,
	 * java.lang.reflect.Method, java.lang.reflect.Method, java.lang.Object[])
	 */
	default Object invoke(Object proxy, Method method, Method proceed, Object[] args) throws Throwable {
		return invokeImpl(proxy, method, args);
	}
}
