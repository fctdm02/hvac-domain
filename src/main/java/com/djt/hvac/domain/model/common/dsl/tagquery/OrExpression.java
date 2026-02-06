package com.djt.hvac.domain.model.common.dsl.tagquery;

import java.util.Set;

import static java.util.Objects.requireNonNull;

class OrExpression implements Expression {
  private final Expression exp1;
  private final Expression exp2;

  OrExpression(Expression exp1, Expression exp2) {
    this.exp1 = requireNonNull(exp1, "exp1 cannot be null");
    this.exp2 = requireNonNull(exp2, "exp2 cannot be null");
  }

  @Override
  public String toSql() {
    return exp1.toSql() + " UNION " + exp2.toSql();
  }

  @Override
  public boolean match(Set<String> tags) {
    requireNonNull(tags, "tags cannot be null");
    return exp1.match(tags) || exp2.match(tags);
  }

  @Override
  public void getTags(Set<String> tags) {
    exp1.getTags(tags);
    exp2.getTags(tags);
  }
}
