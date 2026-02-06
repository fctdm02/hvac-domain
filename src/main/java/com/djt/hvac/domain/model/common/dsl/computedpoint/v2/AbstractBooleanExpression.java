package com.djt.hvac.domain.model.common.dsl.computedpoint.v2;

abstract class AbstractBooleanExpression extends AbstractExpressionWithVariables
    implements BooleanExpression {

  @Override
  public final boolean isBooleanExpression() {
    return true;
  }

}
