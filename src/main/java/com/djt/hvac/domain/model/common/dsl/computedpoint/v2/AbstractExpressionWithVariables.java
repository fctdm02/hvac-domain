package com.djt.hvac.domain.model.common.dsl.computedpoint.v2;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Maps;

abstract class AbstractExpressionWithVariables implements ExpressionWithVariables {

  @SafeVarargs
  protected final Map<String, String> mergeFunctionState(Map<String, String>... functionStates) {
    Map<String, String> result = Maps.newTreeMap();
    for (Map<String, String> state : functionStates) {
      result.putAll(state);
    }
    return result;
  }

  protected static BooleanResult coerceToBoolean(Result result) {
    Optional<Boolean> value = Optional.empty();
    if (result.getValue().isPresent()) {
      double doubleValue = result.getValue().get();
      if (doubleValue == 0.0) {
        value = Optional.of(false);
      } else {
        value = Optional.of(true);
      }
    }
    return BooleanResult.builder()
        .withValue(value)
        .withState(result.getState())
        .build();
  }

  protected static Result coerceToNumeric(BooleanResult result) {
    Optional<Double> value = Optional.empty();
    if (result.getValue().isPresent()) {
      boolean booleanValue = result.getValue().get();
      if (booleanValue == true) {
        value = Optional.of(1.0);
      } else {
        value = Optional.of(0.0);
      }
    }
    return Result.builder()
        .withValue(value)
        .withState(result.getState())
        .build();
  }

  @Override
  public void getVariables(Set<String> variables) {
    // Do-nothing default implementation
  }

  @Override
  public void getVariables(Map<String, IntervalAlignment> variables) {
    // Do-nothing default implemenation
  }

  protected final BooleanResult evalBoolean(long now, Map<String, Double> variableValues,
      Map<String, String> functionState) {
    BooleanResult result;
    if (isBooleanExpression()) {
      result = BooleanExpression.class.cast(this).eval(now, variableValues, functionState);
    } else {
      Result numericResult = Expression.class.cast(this).eval(now, variableValues, functionState);
      result = coerceToBoolean(numericResult);
    }
    return result;
  }

  protected final Result evalNumeric(long now, Map<String, Double> variableValues,
      Map<String, String> functionState) {
    Result result;
    if (isBooleanExpression()) {
      BooleanResult booleanResult =
          BooleanExpression.class.cast(this).eval(now, variableValues, functionState);
      result = coerceToNumeric(booleanResult);
    } else {
      result = Expression.class.cast(this).eval(now, variableValues, functionState);
    }
    return result;
  }
}
