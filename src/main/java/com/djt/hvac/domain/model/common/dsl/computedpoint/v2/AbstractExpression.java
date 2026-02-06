package com.djt.hvac.domain.model.common.dsl.computedpoint.v2;

abstract class AbstractExpression extends AbstractExpressionWithVariables implements Expression {

  @Override
  public final boolean isBooleanExpression() {
    return false;
  }

}
