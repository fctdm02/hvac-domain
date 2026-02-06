package com.djt.hvac.domain.model.common.dsl.computedpoint.v2;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.djt.hvac.domain.model.common.dsl.computedpoint.v2.function.Arguments;
import com.djt.hvac.domain.model.common.dsl.computedpoint.v2.function.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

class FunctionCallExpression extends AbstractExpression {

  private final Function func;
  private final String functionCallId;
  private final List<Expression> argExpressions;

  public FunctionCallExpression(Function func, String functionCallId,
      List<Expression> argExpressions) {
    this.func = requireNonNull(func, "func cannot be null");
    this.functionCallId = requireNonNull(functionCallId, "functionCallId cannot be null");
    this.argExpressions =
        ImmutableList.copyOf(requireNonNull(argExpressions, "argExpressions cannot be null"));
  }

  @Override
  public void getVariables(Set<String> variables) {
    for (Expression expr : argExpressions) {
      expr.getVariables(variables);
    }
  }

  @Override
  public void getVariables(Map<String, IntervalAlignment> variables) {

    Map<String, IntervalAlignment> funcVariables = Maps.newHashMap();

    for (Expression expr : argExpressions) {
      expr.getVariables(funcVariables);
    }

    if (func.getIntervalAlignment() != IntervalAlignment.Floor) {
      for (String name : funcVariables.keySet()) {
        variables.put(name, func.getIntervalAlignment());
      }
    } else {
      variables.putAll(funcVariables);
    }
  }

  @Override
  public Result eval(long now, Map<String, Double> variableValues,
      Map<String, String> functionState) {
    Arguments.Builder argsBuilder = Arguments.builder();
    Map<String, String> resultState = functionState;
    for (Expression expr : argExpressions) {
      Result argResult = expr.eval(now, variableValues, resultState);
      argsBuilder.withArgument(argResult.getValue());
      resultState = argResult.getState();
    }
    Arguments args = argsBuilder.build();
    Result result = func.eval(now, args, resultState, functionCallId);
    return result;
  }

}
