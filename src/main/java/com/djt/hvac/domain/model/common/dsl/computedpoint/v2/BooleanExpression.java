package com.djt.hvac.domain.model.common.dsl.computedpoint.v2;

import java.util.Map;

interface BooleanExpression extends ExpressionWithVariables {

  public BooleanResult eval(long now, Map<String, Double> variableValues,
      Map<String, String> functionState);

}
