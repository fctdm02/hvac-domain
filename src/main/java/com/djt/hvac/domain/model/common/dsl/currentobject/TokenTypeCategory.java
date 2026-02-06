package com.djt.hvac.domain.model.common.dsl.currentobject;

public enum TokenTypeCategory {
  // @formatter:off
  SYMBOL("a symbol"),
  FUNCTION("a function"),
  ARGUMENT("an argument"),
  NODE_TYPE("a node type"),
  MODEL_TYPE("a model"),
  WORD("a word"),
  SPECIAL("a special token");
  // @formatter:on

  private final String value;

  private TokenTypeCategory(String value) {
    this.value = value;
  }

  String getValue() {
    return value;
  }
}
