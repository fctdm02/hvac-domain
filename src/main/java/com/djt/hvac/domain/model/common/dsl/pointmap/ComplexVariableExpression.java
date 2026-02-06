package com.djt.hvac.domain.model.common.dsl.pointmap;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

class ComplexVariableExpression implements Expression {

  private final NodeVariableExpression nodeVariableExpression;
  private final List<Expression> expressions;

  static Builder builder() {
    return new Builder();
  }

  private ComplexVariableExpression(Builder builder) {
    this.nodeVariableExpression = builder.nodeVariableExpression;
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
    StringBuilder buf = new StringBuilder();
    expressions
        .forEach(e -> buf.append(e.toSql(delimiter, variableSubstitutions, variableExclusions)));
    return buf.toString();
  }

  @Override
  public String toRegExp(String delimiter) {
    StringBuilder buf = new StringBuilder("(");
    expressions.forEach(e -> buf.append(e.toRegExp(delimiter)));
    buf.append(")");
    return buf.toString();
  }

  @Override
  public List<Node> convertPatternGroupsToNodes(Queue<String> patternGroups) {
    String name = patternGroups.remove();
    String displayName = patternGroups.remove();
    Node node = Node.builder()
        .withType(nodeVariableExpression.getNodeType())
        .withName(name)
        .withDisplayName(displayName)
        .build();
    return ImmutableList.of(node);
  }

  @Override
  public List<NodeVariableExpression> getNodeVariables() {
    return expressions.stream()
        .flatMap(e -> e.getNodeVariables().stream())
        .collect(Collectors.toList());
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("[");
    expressions.forEach(buf::append);
    buf.append("]");
    return buf.toString();
  }

  static class Builder implements CompositeExpressionBuilder {
    private NodeVariableExpression nodeVariableExpression;
    private List<Expression> expressions = Lists.newArrayList();

    private Builder() {}

    @Override
    public Builder add(Expression expression) {
      requireNonNull(expression, "expression cannot be null");
      boolean isNodeVarExpression = expression instanceof NodeVariableExpression;
      checkArgument(!isNodeVarExpression || this.nodeVariableExpression == null,
          "a complex expression can only have one node variable expression within it");
      this.expressions.add(expression);
      if (isNodeVarExpression) {
        this.nodeVariableExpression = (NodeVariableExpression) expression;
      }
      return this;
    }

    @Override
    public ComplexVariableExpression build() {
      requireNonNull(nodeVariableExpression, "nodeVariableExpression cannot be null");
      return new ComplexVariableExpression(this);
    }
  }

}
