package com.djt.hvac.domain.model.common.dsl.computedpoint.v2;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

class EqualsExpression extends AbstractBooleanExpression {

  private final AbstractExpressionWithVariables left;
  private final AbstractExpressionWithVariables right;

  EqualsExpression(AbstractExpressionWithVariables left, AbstractExpressionWithVariables right) {
    this.left = requireNonNull(left, "left cannot be null");
    this.right = requireNonNull(right, "right cannot be null");
  }

  @Override
  public void getVariables(Set<String> variables) {
    left.getVariables(variables);
    right.getVariables(variables);
  }

  @Override
  public void getVariables(Map<String, IntervalAlignment> variables) {
    left.getVariables(variables);
    right.getVariables(variables);
  }

  @Override
  public BooleanResult eval(long now, Map<String, Double> variableValues,
      Map<String, String> functionState) {
    Map<String, String> resultState;
    Optional<Boolean> resultValue;
    if (left.isBooleanExpression() || right.isBooleanExpression()) {
      BooleanResult leftResult = left.evalBoolean(now, variableValues, functionState);
      BooleanResult rightResult = right.evalBoolean(now, variableValues, leftResult.getState());
      if (leftResult.getValue().isPresent() && rightResult.getValue().isPresent()) {
        boolean value = leftResult.getValue().get().equals(rightResult.getValue().get());
        resultValue = Optional.of(value);
      } else {
        resultValue = Optional.empty();
      }
      resultState = mergeFunctionState(leftResult.getState(), rightResult.getState());
    } else {
      Result leftResult = left.evalNumeric(now, variableValues, functionState);
      Result rightResult = right.evalNumeric(now, variableValues, leftResult.getState());
      if (leftResult.getValue().isPresent() && rightResult.getValue().isPresent()) {
        boolean value = leftResult.getValue().get().equals(rightResult.getValue().get());
        resultValue = Optional.of(value);
      } else {
        resultValue = Optional.empty();
      }
      resultState = mergeFunctionState(leftResult.getState(), rightResult.getState());
    }
    return BooleanResult.builder()
        .withState(resultState)
        .withValue(resultValue)
        .build();
  }


}
