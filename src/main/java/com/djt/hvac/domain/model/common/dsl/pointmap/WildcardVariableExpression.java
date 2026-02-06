package com.djt.hvac.domain.model.common.dsl.pointmap;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

class WildcardVariableExpression implements Expression {

  @Override
  public String toString() {
    return "{*}";
  }

  @Override
  public String toSql(String delimiter, Map<NodeType, List<String>> variableSubstitutions) {
    return toSql(delimiter, variableSubstitutions, Maps.newHashMap());
  }

  @Override
  public String toSql(String delimiter, Map<NodeType, List<String>> variableSubstitutions,
      Map<NodeType, List<String>> variableExclusions) {
    return "[^" + delimiter.replace(".", "\\.") + "]+";
  }

  @Override
  public String toRegExp(String delimiter) {
    return "[^\\" + delimiter + "]+";
  }

}
