package com.djt.hvac.domain.model.common.dsl.tagquery;

enum TokenType {

  // @formatter:off
  LEFT_PAREN("("),
  RIGHT_PAREN(")"),
  AND("&&"),
  OR("||"),
  NOT("!"),
  TAG("a tag"),
  EOE("end of expression");
  // @formatter:on

  private final String value;

  private TokenType(String value) {
    this.value = value;
  }

  String getValue() {
    return value;
  }

}
