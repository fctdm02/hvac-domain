package com.djt.hvac.domain.model.common.dsl.computedpoint.v2.function;

import java.util.Map;
import java.util.Optional;

import com.djt.hvac.domain.model.common.dsl.computedpoint.v2.Result;

import static java.util.Objects.requireNonNull;

import static com.google.common.base.Preconditions.checkArgument;

public abstract class AbstractStatelessFunction extends AbstractFunction {

  @Override
  public Result eval(long timestamp, Arguments args, Map<String, String> functionState,
      String functionCallId) {
    validateArgs(args);
    requireNonNull(functionState, "functionState cannot be null");
    requireNonNull(functionCallId, "functionCallId cannot be null");

    Optional<Double> resultValue = eval(timestamp, args);

    Result result = Result.builder()
        .withValue(resultValue)
        .withState(functionState)
        .build();
    return result;
  }

  @Override
  protected final void validateStateful(boolean stateful) {
    checkArgument(stateful == false, "expected the value of the stateful property to be false");
  }

  protected abstract Optional<Double> eval(long timestamp, Arguments args);

}
