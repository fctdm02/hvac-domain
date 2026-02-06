package com.djt.hvac.domain.model.common.dsl.computedpoint.v2;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

class AndExpression extends AbstractBooleanExpression {

  private final AbstractExpressionWithVariables left;
  private final AbstractExpressionWithVariables right;

  AndExpression(AbstractExpressionWithVariables left, AbstractExpressionWithVariables right) {
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
    BooleanResult leftResult = left.evalBoolean(now, variableValues, functionState);
    BooleanResult rightResult = right.evalBoolean(now, variableValues, leftResult.getState());
    Optional<Boolean> resultValue;
    if (leftResult.getValue().isPresent() && rightResult.getValue().isPresent()) {
      boolean value = leftResult.getValue().get() && rightResult.getValue().get();
      resultValue = Optional.of(value);
    } else {
      resultValue = Optional.empty();
    }
    return BooleanResult.builder()
        .withState(mergeFunctionState(leftResult.getState(), rightResult.getState()))
        .withValue(resultValue)
        .build();
  }


}
