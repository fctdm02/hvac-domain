package com.djt.hvac.domain.model.common.dsl.computedpoint.v2;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

class IfElseExpression extends AbstractExpression {

  private final AbstractExpressionWithVariables condition;
  private final AbstractExpressionWithVariables ifCase;
  private final AbstractExpressionWithVariables elseCase;

  IfElseExpression(AbstractExpressionWithVariables condition,
      AbstractExpressionWithVariables ifCase, AbstractExpressionWithVariables elseCase) {
    this.condition = requireNonNull(condition, "condition cannot be null");
    this.ifCase = requireNonNull(ifCase, "ifCase cannot be null");
    this.elseCase = requireNonNull(elseCase, "elseCase cannot be null");
  }

  @Override
  public void getVariables(Set<String> variables) {
    condition.getVariables(variables);
    ifCase.getVariables(variables);
    elseCase.getVariables(variables);
  }

  @Override
  public void getVariables(Map<String, IntervalAlignment> variables) {
    condition.getVariables(variables);
    ifCase.getVariables(variables);
    elseCase.getVariables(variables);
  }

  @Override
  public Result eval(long now, Map<String, Double> variableValues,
      Map<String, String> functionState) {
    BooleanResult conditionResult = condition.evalBoolean(now, variableValues, functionState);
    Result ifResult = ifCase.evalNumeric(now, variableValues, conditionResult.getState());
    Result elseResult = elseCase.evalNumeric(now, variableValues, ifResult.getState());
    Map<String, String> resultState =
        mergeFunctionState(conditionResult.getState(), ifResult.getState(), elseResult.getState());
    Result result;
    if (conditionResult.getValue().isPresent()) {
      if (conditionResult.getValue().get()) {
        result = Result.builder(ifResult)
            .withState(resultState)
            .build();
      } else {
        result = Result.builder(elseResult)
            .withState(resultState)
            .build();
      }
    } else {
      result = Result.builder()
          .withState(resultState)
          .withValue(Optional.empty())
          .build();
    }
    return result;
  }


}
