package com.djt.hvac.domain.model.common.dsl.computedpoint.v2;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

class NotExpression extends AbstractBooleanExpression {

  private final AbstractExpressionWithVariables expr;

  NotExpression(AbstractExpressionWithVariables expr) {
    this.expr = requireNonNull(expr, "expr cannot be null");
  }

  @Override
  public void getVariables(Set<String> variables) {
    expr.getVariables(variables);
  }

  @Override
  public void getVariables(Map<String, IntervalAlignment> variables) {
    expr.getVariables(variables);
  }

  @Override
  public BooleanResult eval(long now, Map<String, Double> variableValues,
      Map<String, String> functionState) {
    BooleanResult exprResult = expr.evalBoolean(now, variableValues, functionState);
    Optional<Boolean> resultValue;
    if (exprResult.getValue().isPresent()) {
      boolean value = !exprResult.getValue().get();
      resultValue = Optional.of(value);
    } else {
      resultValue = Optional.empty();
    }
    return BooleanResult.builder()
        .withState(exprResult.getState())
        .withValue(resultValue)
        .build();
  }


}
