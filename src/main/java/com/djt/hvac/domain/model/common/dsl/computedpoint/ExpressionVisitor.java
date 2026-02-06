package com.djt.hvac.domain.model.common.dsl.computedpoint;

public interface ExpressionVisitor {
  
  public void visit (Expression<?> expr);

}
