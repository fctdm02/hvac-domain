package com.djt.hvac.domain.model.common.dsl.pointmap;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

enum TokenType {
  // @formatter:off
  START_SIMPLE_VARIABLE("'{'"),
  END_SIMPLE_VARIABLE("'}'"),
  START_COMPLEX_VARIABLE("'['"),
  END_COMPLEX_VARIABLE("']'"),
  WILDCARD_LITERAL("'*'"),
  STRING_LITERAL("a string literal"),
  EOE("end of expression");
  // @formatter:off
  
  static final Set<Character> SPECIAL_CHARS = ImmutableSet.<Character>builder()
      .add('{')
      .add('}')
      .add('[')
      .add(']')
      .add('*')
      .add((char) -1)
      .build();
  
  private final String value;
  
  private TokenType(String value) {
    this.value = value;
  }
  
  String getValue() {
    return value;
  }
  
  public String toString() {
    return value;
  }
  
}
