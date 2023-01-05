/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.internal.expectations;

import java.util.*;
import javax.annotation.*;

import mockit.internal.expectations.invocation.*;
import static mockit.internal.expectations.state.ExecutingTest.*;

public final class RecordPhase extends TestOnlyPhase
{
   RecordPhase(@Nonnull PhasedExecutionState executionState) { super(executionState); }

   void addResult(@Nullable Object result) {
      if (currentExpectation != null) {
         currentExpectation.addResult(result);
      }
   }

   public void addSequenceOfReturnValues(@Nonnull Object[] values) {
      if (currentExpectation != null) {
         currentExpectation.addSequenceOfReturnValues(values);
      }
   }

   @Nullable @Override
   Object handleInvocation(
      @Nullable Object mock, int mockAccess, @Nonnull String mockClassDesc, @Nonnull String mockNameAndDesc,
      @Nullable String genericSignature, boolean withRealImpl, @Nonnull Object[] args
   ) {
      mock = configureMatchingOnMockInstanceIfSpecified(mock);

      ExpectedInvocation invocation = new ExpectedInvocation(
         mock, mockAccess, mockClassDesc, mockNameAndDesc, matchInstance, genericSignature, args);

      boolean nonStrictInvocation = false;

      if (!matchInstance && invocation.isConstructor()) {
         Map<Object, Object> replacementMap = getReplacementMap();
         replacementMap.put(mock, mock);
      }
      else {
         nonStrictInvocation = isInstanceMethodWithStandardBehavior(mock, mockNameAndDesc);
      }

      currentExpectation = new Expectation(this, invocation, nonStrictInvocation);

      if (argMatchers != null) {
         invocation.arguments.setMatchers(argMatchers);
         argMatchers = null;
      }

      executionState.addExpectation(currentExpectation);

      return invocation.getDefaultValueForReturnType();
   }

   @Nullable
   private Object configureMatchingOnMockInstanceIfSpecified(@Nullable Object mock) {
      matchInstance = false;

      if (mock == null) {
         return null;
      }

      Map<Object, Object> replacementMap = getReplacementMap();
      Object replacementInstance = replacementMap.get(mock);
      matchInstance = mock == replacementInstance || isEnumElement(mock);
      return mock;
   }

   @Override
   void handleInvocationCountConstraint(int minInvocations, int maxInvocations) {
      if (currentExpectation != null) {
         currentExpectation.constraints.setLimits(minInvocations, maxInvocations);
      }
   }
}