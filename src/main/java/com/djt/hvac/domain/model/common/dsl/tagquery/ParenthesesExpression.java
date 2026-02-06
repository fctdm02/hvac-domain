package com.djt.hvac.domain.model.common.dsl.tagquery;

import java.util.Set;

import static java.util.Objects.requireNonNull;

class ParenthesesExpression implements Expression {
  private final Expression exp;

  ParenthesesExpression(Expression exp) {
    this.exp = requireNonNull(exp, "exp cannot be null");
  }

  @Override
  public String toSql() {
    return "(" + exp.toSql() + ")";
  }

  @Override
  public boolean match(Set<String> tags) {
    return exp.match(tags);
  }

  @Override
  public void getTags(Set<String> tags) {
    exp.getTags(tags);
  }

}
