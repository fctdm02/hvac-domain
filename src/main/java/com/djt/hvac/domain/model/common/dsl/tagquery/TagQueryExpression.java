package com.djt.hvac.domain.model.common.dsl.tagquery;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import static java.util.Objects.requireNonNull;


public class TagQueryExpression {
  private final Expression exp;

  public static TagQueryExpression parse(String expression) {
    requireNonNull(expression, "expression cannot be null");
    Expression exp = Parser.parse(expression);
    return new TagQueryExpression(exp);
  }

  private TagQueryExpression(Expression exp) {
    this.exp = exp;
  }

  public String toSql() {
    return exp.toSql();
  }

  public boolean match(Set<String> tags) {
    requireNonNull(tags, "tags cannot be null");
    return exp.match(tags);
  }

  public Set<String> getTags() {
    Set<String> tags = Sets.newLinkedHashSet();
    exp.getTags(tags);
    return ImmutableSet.copyOf(tags);
  }

}
