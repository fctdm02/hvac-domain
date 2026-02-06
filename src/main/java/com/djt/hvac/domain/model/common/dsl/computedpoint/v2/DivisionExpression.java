package com.djt.hvac.domain.model.common.dsl.computedpoint.v2;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DivisionExpression extends AbstractExpression {
  Logger log = LoggerFactory.getLogger(DivisionExpression.class);

  private final AbstractExpressionWithVariables left;
  private final AbstractExpressionWithVariables right;

  DivisionExpression(AbstractExpressionWithVariables left, AbstractExpressionWithVariables right) {
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
  public Result eval(long now, Map<String, Double> variableValues,
      Map<String, String> functionState) {
    Result leftResult = left.evalNumeric(now, variableValues, functionState);
    Result rightResult = right.evalNumeric(now, variableValues, leftResult.getState());
    Optional<Double> resultValue;
    if (leftResult.getValue().isPresent() && rightResult.getValue().isPresent()
        && rightResult.getValue().get() != 0.0) {
      double value = leftResult.getValue().get() / rightResult.getValue().get();
      resultValue = Optional.of(value);
    } else {
//      if (rightResult.getValue().get() != 0.0) {
//        log.warn("Illegal division by zero; returning an empty result");
//      }
      resultValue = Optional.empty();
    }
    return Result.builder()
        .withState(mergeFunctionState(leftResult.getState(), rightResult.getState()))
        .withValue(resultValue)
        .build();
  }


}
