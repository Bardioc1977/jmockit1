/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.internal.faking;

import static mockit.internal.util.Utilities.*;

import java.lang.reflect.*;

import javax.annotation.*;

import mockit.internal.*;
import mockit.internal.state.*;

/**
 * An invocation to a {@linkplain mockit.Mock fake} method.
 */
public final class FakeInvocation extends BaseInvocation {
    @Nonnull
    private final FakeState fakeState;
    @Nonnull
    private final String fakedClassDesc;
    @Nonnull
    private final String fakedMethodName;
    @Nonnull
    private final String fakedMethodDesc;
    boolean proceeding;

    @Nonnull // called by generated bytecode
    public static FakeInvocation create(@Nullable Object invokedInstance, @Nullable Object[] invokedArguments,
            @Nonnull String fakeClassDesc, @Nonnegative int fakeStateIndex, @Nonnull String fakedClassDesc,
            @Nonnull String fakedMethodName, @Nonnull String fakedMethodDesc) {
        Object fake = TestRun.getFake(fakeClassDesc);
        FakeState fakeState = TestRun.getFakeStates().getFakeState(fake, fakeStateIndex);
        Object[] args = invokedArguments == null ? NO_ARGS : invokedArguments;
        return new FakeInvocation(invokedInstance, args, fakeState, fakedClassDesc, fakedMethodName, fakedMethodDesc);
    }

    FakeInvocation(@Nullable Object invokedInstance, @Nonnull Object[] invokedArguments, @Nonnull FakeState fakeState,
            @Nonnull String fakedClassDesc, @Nonnull String fakedMethodName, @Nonnull String fakedMethodDesc) {
        super(invokedInstance, invokedArguments, fakeState.getTimesInvoked());
        this.fakeState = fakeState;
        this.fakedClassDesc = fakedClassDesc;
        this.fakedMethodName = fakedMethodName;
        this.fakedMethodDesc = fakedMethodDesc;
    }

    @Nonnull
    @Override
    protected Member findRealMember() {
        Object invokedInstance = getInvokedInstance();

        if (invokedInstance != null) {
            Class<?> mockedClass = invokedInstance.getClass();
            return fakeState.getRealMethodOrConstructor(mockedClass, fakedMethodName, fakedMethodDesc);
        }

        return fakeState.getRealMethodOrConstructor(fakedClassDesc, fakedMethodName, fakedMethodDesc);
    }

    @SuppressWarnings("WeakerAccess") // called from generated bytecode
    public boolean shouldProceedIntoConstructor() {
        if (proceeding && getInvokedMember() instanceof Constructor) {
            fakeState.clearProceedIndicator();
            return true;
        }

        return false;
    }

    @Override
    public void prepareToProceed() {
        fakeState.prepareToProceed(this);
        proceeding = true;
    }

    public void prepareToProceedFromNonRecursiveMock() {
        fakeState.prepareToProceedFromNonRecursiveFake(this);
        proceeding = true;
    }

    @Override
    public void cleanUpAfterProceed() {
        fakeState.clearProceedIndicator();
    }
}
