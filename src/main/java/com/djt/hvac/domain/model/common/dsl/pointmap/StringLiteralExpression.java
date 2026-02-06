package com.djt.hvac.domain.model.common.dsl.pointmap;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

class StringLiteralExpression implements Expression {
  private final String lexeme;

  StringLiteralExpression(String lexeme) {
    this.lexeme = requireNonNull(lexeme, "lexeme cannot be null");
  }

  @Override
  public String toString() {
    return lexeme;
  }

  @Override
  public String toSql(String delimiter, Map<NodeType, List<String>> variableSubstitutions) {
    return toSql(delimiter, variableSubstitutions, Maps.newHashMap());
  }


  @Override
  public String toSql(String delimiter, Map<NodeType, List<String>> variableSubstitutions,
      Map<NodeType, List<String>> variableExclusions) {
    return lexeme.replace(".", "\\.");
  }

  @Override
  public String toRegExp(String delimiter) {
    return "\\Q" + lexeme + "\\E";
  }


}
