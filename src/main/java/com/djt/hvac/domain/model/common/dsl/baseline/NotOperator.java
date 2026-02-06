package com.djt.hvac.domain.model.common.dsl.baseline;

import static java.util.Objects.requireNonNull;

class NotOperator extends AbstractUnaryExpression<Boolean, Boolean> implements LogicalOperator {
  
  static NotOperator create(Expression<Boolean> expr) {
    return new NotOperator(expr);
  }
  
  private NotOperator(Expression<Boolean> expr) {
    super(expr);
  }

  @Override
  public Boolean evaluate(Inputs inputs) {
    requireNonNull(inputs, "inputs cannot be null");
    return !expr.evaluate(inputs);
  }

  @Override
  public Class<Boolean> getType() {
    return Boolean.class;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((expr == null) ? 0 : expr.hashCode());
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
    NotOperator other = (NotOperator) obj;
    if (expr == null) {
      if (other.expr != null)
        return false;
    } else if (!expr.equals(other.expr))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "!("+ expr + ")";
  }

}
