/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.internal.expectations;

import static java.lang.reflect.Modifier.*;

import javax.annotation.*;

import mockit.internal.state.*;

public enum ExecutionMode {
    Regular {
        @Override
        boolean isNativeMethodToBeIgnored(int access) {
            return false;
        }

        @Override
        boolean isToExecuteRealImplementation(@Nullable Object instance) {
            return instance != null && !TestRun.mockFixture().isInstanceOfMockedClass(instance);
        }
    },

    Partial {
        @Override
        boolean isToExecuteRealImplementation(@Nullable Object instance) {
            return instance != null && !TestRun.mockFixture().isInstanceOfMockedClass(instance);
        }

        @Override
        boolean isWithRealImplementation(@Nullable Object instance) {
            return instance == null || !TestRun.getExecutingTest().isInjectableMock(instance);
        }

        @Override
        boolean isToExecuteRealObjectOverride(@Nonnull Object instance) {
            return true;
        }
    },

    PerInstance {
        @Override
        boolean isStaticMethodToBeIgnored(int access) {
            return isStatic(access);
        }

        @Override
        boolean isToExecuteRealImplementation(@Nullable Object instance) {
            return instance == null || TestRun.getExecutingTest().isUnmockedInstance(instance);
        }

        @Override
        boolean isToExecuteRealObjectOverride(@Nonnull Object instance) {
            return TestRun.getExecutingTest().isUnmockedInstance(instance);
        }
    };

    public final boolean isMethodToBeIgnored(int access) {
        return isStaticMethodToBeIgnored(access) || isNativeMethodToBeIgnored(access);
    }

    boolean isStaticMethodToBeIgnored(int access) {
        return false;
    }

    boolean isNativeMethodToBeIgnored(int access) {
        return isNative(access);
    }

    boolean isToExecuteRealImplementation(@Nullable Object instance) {
        return false;
    }

    boolean isWithRealImplementation(@Nullable Object instance) {
        return false;
    }

    boolean isToExecuteRealObjectOverride(@Nonnull Object instance) {
        return false;
    }
}
