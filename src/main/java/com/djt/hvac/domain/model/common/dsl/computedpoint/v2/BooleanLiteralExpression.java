package com.djt.hvac.domain.model.common.dsl.computedpoint.v2;

import java.util.Map;
import java.util.Optional;

class BooleanLiteralExpression extends AbstractBooleanExpression {

  private final boolean value;

  BooleanLiteralExpression(boolean value) {
    this.value = value;
  }

  @Override
  public BooleanResult eval(long now, Map<String, Double> variableValues,
      Map<String, String> functionState) {
    return BooleanResult.builder()
        .withValue(Optional.of(value))
        .withState(functionState)
        .build();
  }


}
