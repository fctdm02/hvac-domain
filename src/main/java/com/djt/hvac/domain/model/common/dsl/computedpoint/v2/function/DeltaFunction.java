package com.djt.hvac.domain.model.common.dsl.computedpoint.v2.function;

import java.util.Optional;

import com.djt.hvac.domain.model.common.dsl.computedpoint.v2.IntervalAlignment;

@ComputedPointFunction(name = "delta", stateful = true, minParams = 1, maxParams = 1,
    intervalAlignment = IntervalAlignment.Nearest)
public class DeltaFunction extends AbstractStatefulFunction<DeltaFunctionState> {

  @Override
  protected FunctionCallResult eval(long timestamp, Arguments args,
      Optional<DeltaFunctionState> state) {
    Optional<Double> resultValue = Optional.empty();
    Optional<DeltaFunctionState> resultState = state;
    Optional<Double> arg = args.get(0);
    if (arg.isPresent()) {
      resultState = Optional.of(DeltaFunctionState.builder()
          .withValue(arg.get())
          .withTimestamp(timestamp)
          .build());
      if (state.isPresent()) {
        double v = arg.get() - state.get().getValue();
        v = Math.round(v * 100000.0) / 100000.0;
        resultValue = Optional.of(v);
      }
    }
    return createResult(resultValue, resultState);
  }


}
