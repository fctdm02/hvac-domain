package com.djt.hvac.domain.model.common.dsl.computedpoint;

import static java.util.Objects.requireNonNull;

abstract class AbstractBinaryExpression <TYPE, OPERAND_TYPE> extends AbstractExpression<TYPE> {
  protected final Expression<OPERAND_TYPE> expr1;
  protected final Expression<OPERAND_TYPE> expr2;

  protected AbstractBinaryExpression(Expression<OPERAND_TYPE> expr1, Expression<OPERAND_TYPE> expr2) {
    this.expr1 = requireNonNull(expr1, "expr1 cannot be null");
    this.expr2 = requireNonNull(expr2, "expr2 cannot be null");
  }
  
  Expression<OPERAND_TYPE> getExpr1() {
    return expr1;
  }

  Expression<OPERAND_TYPE> getExpr2() {
    return expr2;
  }

  @Override
  public void accept(ExpressionVisitor visitor) {
    requireNonNull(visitor, "visitor cannot be null");
    visitor.visit(this);
    expr1.accept(visitor);
    expr2.accept(visitor);
  }
  
  
  
}
