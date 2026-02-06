package com.djt.hvac.domain.model.common.dsl.baseline;

import static java.util.Objects.requireNonNull;

abstract class AbstractUnaryExpression <TYPE, OPERAND_TYPE> extends AbstractExpression<TYPE> {
  protected final Expression<OPERAND_TYPE> expr;

  protected AbstractUnaryExpression(Expression<OPERAND_TYPE> expr) {
    this.expr = requireNonNull(expr, "expr cannot be null");
  }
  
  Expression<OPERAND_TYPE> getExpr() {
    return expr;
  }

  @Override
  public void accept(ExpressionVisitor visitor) {
    requireNonNull(visitor, "visitor cannot be null");
    visitor.visit(this);
    expr.accept(visitor);
  }
  
  
  
}
