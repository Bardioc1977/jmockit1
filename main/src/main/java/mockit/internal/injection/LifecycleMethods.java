/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.internal.injection;

import static mockit.internal.injection.InjectionPoint.SERVLET_CLASS;
import static mockit.internal.injection.InjectionPoint.isServlet;
import static mockit.internal.reflection.ParameterReflection.getParameterCount;
import static mockit.internal.util.Utilities.NO_ARGS;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.ServletConfig;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mockit.internal.reflection.MethodReflection;
import mockit.internal.state.TestRun;

public final class LifecycleMethods {
    @Nonnull
    private final List<Class<?>> classesSearched;
    @Nonnull
    private final Map<Class<?>, Method> initializationMethods;
    @Nonnull
    private final Map<Class<?>, Method> terminationMethods;
    @Nonnull
    private final Map<Class<?>, Object> objectsWithTerminationMethodsToExecute;
    @Nullable
    private Object servletConfig;

    LifecycleMethods() {
        classesSearched = new ArrayList<>();
        initializationMethods = new IdentityHashMap<>();
        terminationMethods = new IdentityHashMap<>();
        objectsWithTerminationMethodsToExecute = new IdentityHashMap<>();
    }

    public void findLifecycleMethods(@Nonnull Class<?> testedClass) {
        if (testedClass.isInterface() || classesSearched.contains(testedClass)) {
            return;
        }

        boolean isServlet = isServlet(testedClass);
        Class<?> classWithLifecycleMethods = testedClass;

        do {
            findLifecycleMethodsInSingleClass(isServlet, classWithLifecycleMethods);
            classWithLifecycleMethods = classWithLifecycleMethods.getSuperclass();
        } while (classWithLifecycleMethods != Object.class);

        classesSearched.add(testedClass);
    }

    private void findLifecycleMethodsInSingleClass(boolean isServlet, @Nonnull Class<?> classWithLifecycleMethods) {
        Method initializationMethod = null;
        Method terminationMethod = null;
        int methodsFoundInSameClass = 0;

        for (Method method : classWithLifecycleMethods.getDeclaredMethods()) {
            if (method.isSynthetic()) {
                continue;
            }

            if (initializationMethod == null && isInitializationMethod(method, isServlet)) {
                initializationMethods.put(classWithLifecycleMethods, method);
                initializationMethod = method;
                methodsFoundInSameClass++;
            } else if (terminationMethod == null && isTerminationMethod(method, isServlet)) {
                terminationMethods.put(classWithLifecycleMethods, method);
                terminationMethod = method;
                methodsFoundInSameClass++;
            }

            if (methodsFoundInSameClass == 2) {
                break;
            }
        }
    }

    private static boolean isInitializationMethod(@Nonnull Method method, boolean isServlet) {
        if (hasLifecycleAnnotation(method, true)) {
            return true;
        }

        if (isServlet && "init".equals(method.getName())) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            return parameterTypes.length == 1 && parameterTypes[0] == ServletConfig.class;
        }

        return false;
    }

    private static boolean hasLifecycleAnnotation(@Nonnull Method method, boolean postConstruct) {
        try {
            Class<? extends Annotation> lifecycleAnnotation = postConstruct ? PostConstruct.class : PreDestroy.class;

            if (method.isAnnotationPresent(lifecycleAnnotation)) {
                return true;
            }
        } catch (NoClassDefFoundError ignore) {
            /* can occur on JDK 9 */ }

        return false;
    }

    private static boolean isTerminationMethod(@Nonnull Method method, boolean isServlet) {
        return hasLifecycleAnnotation(method, false)
                || isServlet && "destroy".equals(method.getName()) && getParameterCount(method) == 0;
    }

    public void executeInitializationMethodsIfAny(@Nonnull Class<?> testedClass, @Nonnull Object testedObject) {
        Class<?> superclass = testedClass.getSuperclass();

        if (superclass != Object.class) {
            executeInitializationMethodsIfAny(superclass, testedObject);
        }

        Method postConstructMethod = initializationMethods.get(testedClass);

        if (postConstructMethod != null) {
            executeInitializationMethod(testedObject, postConstructMethod);
        }

        Method preDestroyMethod = terminationMethods.get(testedClass);

        if (preDestroyMethod != null) {
            objectsWithTerminationMethodsToExecute.put(testedClass, testedObject);
        }
    }

    private void executeInitializationMethod(@Nonnull Object testedObject, @Nonnull Method initializationMethod) {
        Object[] args = NO_ARGS;

        if ("init".equals(initializationMethod.getName()) && getParameterCount(initializationMethod) == 1) {
            args = new Object[] { servletConfig };
        }

        TestRun.exitNoMockingZone();

        try {
            MethodReflection.invoke(testedObject, initializationMethod, args);
        } finally {
            TestRun.enterNoMockingZone();
        }
    }

    void executeTerminationMethodsIfAny() {
        try {
            for (Entry<Class<?>, Object> testedClassAndObject : objectsWithTerminationMethodsToExecute.entrySet()) {
                executeTerminationMethod(testedClassAndObject.getKey(), testedClassAndObject.getValue());
            }
        } finally {
            objectsWithTerminationMethodsToExecute.clear();
        }
    }

    private void executeTerminationMethod(@Nonnull Class<?> testedClass, @Nonnull Object testedObject) {
        Method terminationMethod = terminationMethods.get(testedClass);
        TestRun.exitNoMockingZone();

        try {
            MethodReflection.invoke(testedObject, terminationMethod);
        } catch (RuntimeException | AssertionError ignore) {
        } finally {
            TestRun.enterNoMockingZone();
        }
    }

    void getServletConfigForInitMethodsIfAny(@Nonnull List<? extends InjectionProvider> injectables,
            @Nonnull Object testClassInstance) {
        if (SERVLET_CLASS != null) {
            for (InjectionProvider injectable : injectables) {
                if (injectable.getDeclaredType() == ServletConfig.class) {
                    servletConfig = injectable.getValue(testClassInstance);
                    break;
                }
            }
        }
    }
}
