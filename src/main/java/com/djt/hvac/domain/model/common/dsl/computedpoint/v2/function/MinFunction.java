package com.djt.hvac.domain.model.common.dsl.computedpoint.v2.function;

import java.util.Optional;

@ComputedPointFunction(name = "min", stateful = false, minParams = 2, maxParams = Integer.MAX_VALUE)
public class MinFunction extends AbstractStatelessFunction {

  @Override
  protected Optional<Double> eval(long timestamp, Arguments args) {
    for (Optional<Double> arg : args) {
      if (!arg.isPresent()) {
        return Optional.empty();
      }
    }
    double resultValue = Double.MAX_VALUE;
    for (Optional<Double> arg : args) {
      double v = arg.get();
      if (v < resultValue) {
        resultValue = v;
      }
    }
    return Optional.of(resultValue);
  }

}
