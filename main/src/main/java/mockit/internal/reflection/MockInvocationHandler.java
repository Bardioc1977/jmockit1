/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.internal.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mockit.internal.util.DefaultValues;
import mockit.internal.util.ObjectMethods;

/**
 * Handles invocations to all kinds of mock implementations created for interfaces and annotation types through any of
 * the mocking APIs.
 * <p>
 * The <code>java.lang.Object</code> methods <code>equals</code>, <code>hashCode</code>, and <code>toString</code> are
 * handled in a meaningful way, returning a value that makes sense for the proxy instance. The special
 * {@linkplain Annotation} contracts for these three methods is <em>not</em> observed, though, since it would require
 * making dynamic calls to the mocked annotation attributes.
 * <p>
 * Any other method invocation is handled by simply returning the default value according to the method's return type
 * (as defined in {@linkplain DefaultValues}).
 */
public final class MockInvocationHandler implements InvocationHandler {
    public static final InvocationHandler INSTANCE = new MockInvocationHandler();

    @Nullable
    @Override
    public Object invoke(@Nonnull Object proxy, @Nonnull Method method, @Nullable Object[] args) {
        Class<?> declaringClass = method.getDeclaringClass();
        String methodName = method.getName();

        if (declaringClass == Object.class) {
            if ("equals".equals(methodName)) {
                assert args != null;
                return proxy == args[0];
            } else if ("hashCode".equals(methodName)) {
                return System.identityHashCode(proxy);
            } else { // "toString"
                return ObjectMethods.objectIdentity(proxy);
            }
        }

        if (declaringClass == Annotation.class) {
            return proxy.getClass().getInterfaces()[0];
        }

        Class<?> retType = method.getReturnType();
        return DefaultValues.computeForType(retType);
    }
}
