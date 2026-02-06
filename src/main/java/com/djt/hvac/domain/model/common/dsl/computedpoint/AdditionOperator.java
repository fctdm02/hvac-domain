package com.djt.hvac.domain.model.common.dsl.computedpoint;

import static java.util.Objects.requireNonNull;

class AdditionOperator extends AbstractBinaryExpression<Double, Double> implements ArithmeticOperator {
  
  static AdditionOperator create(Expression<Double> expr1, Expression<Double> expr2) {
    return new AdditionOperator(expr1, expr2);
  }
  
  private AdditionOperator(Expression<Double> expr1, Expression<Double> expr2) {
    super(expr1, expr2);
  }

  @Override
  public Double evaluate(Inputs inputs) {
    requireNonNull(inputs, "inputs cannot be null");
    return expr1.evaluate(inputs) + expr2.evaluate(inputs);
  }

  @Override
  public Class<Double> getType() {
    return Double.class;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((expr1 == null) ? 0 : expr1.hashCode());
    result = prime * result + ((expr2 == null) ? 0 : expr2.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AdditionOperator other = (AdditionOperator) obj;
    if (expr1 == null) {
      if (other.expr1 != null)
        return false;
    } else if (!expr1.equals(other.expr1))
      return false;
    if (expr2 == null) {
      if (other.expr2 != null)
        return false;
    } else if (!expr2.equals(other.expr2))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "("+ expr1 + ") + (" + expr2 + ")";
  }

  
}
