package com.djt.hvac.domain.model.common.dsl.computedpoint.v2;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ComputedPointExpression {

  private final Expression expr;
  // private Set<String> variables;
  private Map<String, IntervalAlignment> variablesWithAlignment;

  public static ComputedPointExpression parse(String s) {
    FunctionRegistry functionRegistry = FunctionRegistry.getInstance();
    Parser parser = new Parser(functionRegistry);
    return parser.parse(s);
  }

  ComputedPointExpression(Expression expr) {
    this.expr = requireNonNull(expr, "expr cannot be null");
  }

  public Set<String> getVariables() {
    // Set<String> vars = this.variables;
    // if (vars == null) {
    // synchronized (this) {
    // vars = this.variables;
    // if (vars == null) {
    // vars = Sets.newHashSet();
    // expr.getVariables(vars);
    // this.variables = vars = ImmutableSet.copyOf(vars);
    // }
    // }
    // }
    // return vars;

    return getVariablesWithAlignment().keySet();
  }

  public Map<String, IntervalAlignment> getVariablesWithAlignment() {
    Map<String, IntervalAlignment> vars = this.variablesWithAlignment;
    if (vars == null) {
      synchronized (this) {
        vars = Maps.newHashMap();
        expr.getVariables(vars);
        this.variablesWithAlignment = vars = ImmutableMap.copyOf(vars);
      }
    }

    return vars;
  }

  public Result eval(long now, Map<String, Double> variableValues,
      Map<String, String> functionState) {
    requireNonNull(variableValues, "variableValues cannot be null");
    requireNonNull(functionState, "functionState cannot be null");
    validateVariableValues(variableValues);
    return expr.eval(now, variableValues, functionState);
  }

  private void validateVariableValues(Map<String, Double> variableValues) {
    Set<String> missingValues = Sets.newHashSet();
    for (String var : getVariables()) {
      if (!variableValues.containsKey(var) || variableValues.get(var) == null) {
        missingValues.add(var);
      }
    }
    if (!missingValues.isEmpty()) {
      throw new IllegalArgumentException(
          "variableValues is missing entries for all of the following variables: " + missingValues);
    }
  }

}
