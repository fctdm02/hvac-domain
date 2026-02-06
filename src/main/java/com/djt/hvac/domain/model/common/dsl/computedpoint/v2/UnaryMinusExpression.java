package com.djt.hvac.domain.model.common.dsl.computedpoint.v2;

import java.util.Map;
import java.util.Optional;

class UnaryMinusExpression extends AbstractExpression {

  private final NumberExpression expr;

  UnaryMinusExpression(NumberExpression expr) {
    this.expr = expr;
  }

  @Override
  public Result eval(long now, Map<String, Double> variableValues,
      Map<String, String> functionState) {
    Result result = expr.evalNumeric(now, variableValues, functionState);
    Optional<Double> resultValue = result.getValue().map(v -> -1 * v);
    return Result.builder()
        .withValue(resultValue)
        .withState(functionState)
        .build();
  }


}
