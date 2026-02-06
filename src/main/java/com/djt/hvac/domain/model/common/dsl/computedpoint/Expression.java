package com.djt.hvac.domain.model.common.dsl.computedpoint;

interface Expression<T> {

  public T evaluate(Inputs inputs);

  public Class<T> getType();

  public void accept(ExpressionVisitor visitor);

}
