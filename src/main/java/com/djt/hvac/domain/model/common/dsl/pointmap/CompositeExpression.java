package com.djt.hvac.domain.model.common.dsl.pointmap;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

class CompositeExpression implements Expression {

  private final List<Expression> expressions;

  static Builder builder() {
    return new Builder();
  }

  private CompositeExpression(Builder builder) {
    this.expressions = ImmutableList.copyOf(builder.expressions);
  }

  @Override
  public void accept(PointMapExpressionVisitor visitor) {
    visitor.visit(this);
    expressions.forEach(e -> e.accept(visitor));
  }

  @Override
  public String toSql(String delimiter, Map<NodeType, List<String>> variableSubstitutions) {
    return toSql(delimiter, variableSubstitutions, Maps.newHashMap());
  }

  @Override
  public String toSql(String delimiter, Map<NodeType, List<String>> variableSubstitutions,
      Map<NodeType, List<String>> variableExclusions) {
    StringBuilder buf =
        new StringBuilder(
            "SELECT rp.id, rp.metric_id "
                + "FROM raw_points rp "
                + "LEFT OUTER JOIN mappable_points mp ON rp.id = mp.raw_point_id "
                + "WHERE mp.id IS NULL AND rp.customer_id = ? AND rp.metric_id ~ '^");
    expressions
        .forEach(e -> buf.append(e.toSql(delimiter, variableSubstitutions, variableExclusions)));
    buf.append("$'");
    return buf.toString();
  }

  @Override
  public String toRegExp(String delimiter) {
    StringBuilder buf = new StringBuilder("^");
    expressions.forEach(e -> buf.append(e.toRegExp(delimiter)));
    buf.append("$");
    return buf.toString();
  }

  @Override
  public List<Node> convertPatternGroupsToNodes(Queue<String> patternGroups) {
    ImmutableList.Builder<Node> list = ImmutableList.builder();
    expressions.forEach(e -> list.addAll(e.convertPatternGroupsToNodes(patternGroups)));
    return list.build();
  }

  @Override
  public List<NodeVariableExpression> getNodeVariables() {
    return expressions.stream()
        .flatMap(e -> e.getNodeVariables().stream())
        .collect(Collectors.toList());
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    expressions.forEach(buf::append);
    return buf.toString();

  }

  static class Builder implements CompositeExpressionBuilder {
    private List<Expression> expressions = Lists.newArrayList();

    private Builder() {}

    @Override
    public Builder add(Expression expression) {
      requireNonNull(expression, "expression cannot be null");
      this.expressions.add(expression);
      return this;
    }

    @Override
    public CompositeExpression build() {
      Preconditions.checkArgument(this.expressions.size() > 0, "expected at least one expression");
      return new CompositeExpression(this);
    }
  }

}
