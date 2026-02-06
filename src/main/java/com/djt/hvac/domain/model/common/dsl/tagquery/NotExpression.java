package com.djt.hvac.domain.model.common.dsl.tagquery;

import java.util.Set;

import static java.util.Objects.requireNonNull;

class NotExpression implements Expression {
  private final Expression exp;

  NotExpression(Expression exp) {
    this.exp = requireNonNull(exp, "exp cannot be null");
  }

  @Override
  public String toSql() {
    return "SELECT id AS node_id FROM nodes WHERE id NOT IN (" + exp.toSql() + ")";
  }

  @Override
  public boolean match(Set<String> tags) {
    requireNonNull(tags, "tags cannot be null");
    return !exp.match(tags);
  }

  @Override
  public void getTags(Set<String> tags) {
    exp.getTags(tags);
  }

}
