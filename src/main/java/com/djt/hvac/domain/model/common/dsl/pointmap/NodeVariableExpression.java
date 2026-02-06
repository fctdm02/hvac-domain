package com.djt.hvac.domain.model.common.dsl.pointmap;

import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

class NodeVariableExpression implements Expression {
  private final NodeType nodeType;

  NodeVariableExpression(NodeType nodeType) {
    this.nodeType = nodeType;
  }

  @Override
  public String toString() {
    return "{" + nodeType.getValue() + "}";
  }

  @Override
  public String toSql(String delimiter, Map<NodeType, List<String>> variableSubstitutions) {
    return toSql(delimiter, variableSubstitutions, Maps.newHashMap());
  }


  @Override
  public String toSql(String delimiter, Map<NodeType, List<String>> variableSubstitutions,
      Map<NodeType, List<String>> variableExclusions) {
    if (variableSubstitutions.containsKey(nodeType)) {
      return "(" + String.join("|", variableSubstitutions.get(nodeType)) + ")";
    } else if (variableExclusions.containsKey(nodeType)) {
      return "(?!(" + String.join("|", variableExclusions.get(nodeType)) + "))[^"
          + delimiter.replace(".", "\\.") + "]+";

    }

    return "[^" + delimiter.replace(".", "\\.") + "]+";
  }

  @Override
  public String toRegExp(String delimiter) {
    return "([^\\" + delimiter + "]+)";
  }

  @Override
  public List<Node> convertPatternGroupsToNodes(Queue<String> patternGroups) {
    String name = patternGroups.remove();
    Node node = Node.builder()
        .withType(nodeType)
        .withName(name)
        .withDisplayName(name)
        .build();
    return ImmutableList.of(node);
  }


  @Override
  public List<NodeVariableExpression> getNodeVariables() {
    return ImmutableList.of(this);
  }

  NodeType getNodeType() {
    return nodeType;
  }
}
