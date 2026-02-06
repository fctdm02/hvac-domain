package com.djt.hvac.domain.model.common.dsl.computedpoint.v2;

import java.util.Map;

interface Expression extends ExpressionWithVariables {

  public Result eval(long now, Map<String, Double> variableValues,
      Map<String, String> functionState);

}
