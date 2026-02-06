package com.djt.hvac.domain.model.common.dsl.computedpoint;

import static java.util.Objects.requireNonNull;

abstract class AbstractExpression <T> implements Expression<T> {

  @Override
  public void accept(ExpressionVisitor visitor) {
    requireNonNull(visitor, "visitor cannot be null");
    visitor.visit(this);
  }
  
}
