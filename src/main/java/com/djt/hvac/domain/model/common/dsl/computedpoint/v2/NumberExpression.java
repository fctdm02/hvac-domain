package com.djt.hvac.domain.model.common.dsl.computedpoint.v2;

import java.util.Map;
import java.util.Optional;

class NumberExpression extends AbstractExpression {

  private final double value;

  NumberExpression(double value) {
    this.value = value;
  }

  @Override
  public Result eval(long now, Map<String, Double> variableValues,
      Map<String, String> functionState) {
    return Result.builder()
        .withValue(Optional.of(value))
        .withState(functionState)
        .build();
  }


}
