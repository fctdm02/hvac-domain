package com.djt.hvac.domain.model.common.dsl.computedpoint.v2.function;

import java.util.Optional;

@ComputedPointFunction(name = "max", stateful = false, minParams = 2, maxParams = Integer.MAX_VALUE)
public class AvgFunction extends AbstractStatelessFunction {

  @Override
  protected Optional<Double> eval(long timestamp, Arguments args) {
    for (Optional<Double> arg : args) {
      if (!arg.isPresent()) {
        return Optional.empty();
      }
    }
    double total = 0.0;
    int count = 0;
    for (Optional<Double> arg : args) {
      double v = arg.get();
      count += 1;
      total += v;
    }
    double resultValue = total / count;
    return Optional.of(resultValue);
  }

}
