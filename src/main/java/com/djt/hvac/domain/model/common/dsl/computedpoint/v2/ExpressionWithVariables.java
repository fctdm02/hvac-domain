package com.djt.hvac.domain.model.common.dsl.computedpoint.v2;

import java.util.Map;
import java.util.Set;

interface ExpressionWithVariables {

  public boolean isBooleanExpression();

  public void getVariables(Set<String> variables);

  public void getVariables(Map<String, IntervalAlignment> variables);
}
