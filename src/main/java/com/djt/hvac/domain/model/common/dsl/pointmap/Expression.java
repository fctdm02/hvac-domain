package com.djt.hvac.domain.model.common.dsl.pointmap;

import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.google.common.collect.ImmutableList;

interface Expression {


  default void accept(PointMapExpressionVisitor visitor) {
    visitor.visit(this);
  }

  default List<NodeVariableExpression> getNodeVariables() {
    return ImmutableList.of();
  }

  default List<Node> convertPatternGroupsToNodes(Queue<String> patternGroups) {
    return ImmutableList.of();
  }

  String toSql(String delimiter, Map<NodeType, List<String>> variableSubstitutions);

  String toSql(String delimiter, Map<NodeType, List<String>> variableSubstitutions,
      Map<NodeType, List<String>> variableExclusions);


  String toRegExp(String delimiter);


}
