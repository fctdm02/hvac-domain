package com.djt.hvac.domain.model.common.dsl.computedpoint.v2;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

class VariableExpression extends AbstractExpression {
  private final String name;

  VariableExpression(String name) {
    this.name = requireNonNull(name, "name cannot be null");
  }

  @Override
  public void getVariables(Set<String> variables) {
    variables.add(name);
  }

  @Override
  public void getVariables(Map<String, IntervalAlignment> variables) {

    if (!variables.containsKey(name)) {
      variables.put(name, IntervalAlignment.Floor);
    }
  }

  @Override
  public Result eval(long now, Map<String, Double> variableValues,
      Map<String, String> functionState) {
    Double value = variableValues.get(name);
    if (value == null) {
      throw new AssertionError(
          "Expected variableValues to contain an entry for every variable, but it is missing an entry for variable "
              + name);
    }
    return Result.builder()
        .withValue(Optional.of(value))
        .withState(functionState)
        .build();
  }


}
