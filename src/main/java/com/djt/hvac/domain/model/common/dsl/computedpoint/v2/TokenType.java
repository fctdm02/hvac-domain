package com.djt.hvac.domain.model.common.dsl.computedpoint.v2;

public enum TokenType {

  // @formatter:off

  // general
  LEFT_PAREN("("),
  RIGHT_PAREN(")"),
  EOS("end of string"),
  
  // arithmetic operators
  PLUS("+"),
  MINUS("-"),
  MULTIPLY("*"),
  DIVIDE("/"),
  COMMA(","),
  
  // conditional operators
  IF("if"),
  ELSE("else"),
  
  // logical operators
  AND("&&"),
  OR("||"),
  EQUALS("=="),
  NOT_EQUALS("!="),
  GREATER_THAN(">"),
  LESS_THAN("<"),
  GREATER_THAN_OR_EQUALS(">"),
  LESS_THAN_OR_EQUALS("<"),
  NOT("!"),
  
  // boolean literals
  TRUE("true"),
  FALSE("false"),

  // variable lexemes
  NUMBER("a number"),
  FUNCTION("a function"),
  VARIABLE("a variable");

  // @formatter:on

  private final String value;

  private TokenType(String value) {
    this.value = value;
  }

  String getValue() {
    return value;
  }
}
