package com.djt.hvac.domain.model.common.dsl.computedpoint;

import static java.util.Objects.requireNonNull;

public class Variable<T> implements Expression<T> {

  private final Class<T> type;
  private final String id;

  static <T> Variable<T> create(Class<T> type, String id) {
    return new Variable<T>(type, id);
  }

  private Variable(Class<T> type, String id) {
    this.type = requireNonNull(type, "type cannot be null");
    this.id = id;
  }

  @Override
  public T evaluate(Inputs inputs) {
    return inputs.getValue(this);
  }

  @Override
  public Class<T> getType() {
    return type;
  }

  @Override
  public void accept(ExpressionVisitor visitor) {
    visitor.visit(this);
  }

  public String getId() {
    return id;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
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
    Variable<?> other = (Variable<?>) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (type == null) {
      if (other.type != null)
        return false;
    } else if (!type.equals(other.type))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Variable [type=" + type + ", id=" + id + "]";
  }

}
