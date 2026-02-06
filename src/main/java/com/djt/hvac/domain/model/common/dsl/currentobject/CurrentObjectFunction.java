package com.djt.hvac.domain.model.common.dsl.currentobject;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.ImmutableSet;

public enum CurrentObjectFunction {
  // @formatter:off
  // Functions
  ELSEIF(TokenType.ELSEIF),
  PARENT(TokenType.PARENT), 
  ANCESTOR(TokenType.ANCESTOR), 
  CHILD(TokenType.CHILD), 
  DESCENDANT(TokenType.DESCENDANT),
  
  // Deprecated Functions
  PARENT_EQUIPMENT(TokenType.PARENT_EQUIPMENT), 
  ANCESTOR_EQUIPMENT(TokenType.ANCESTOR_EQUIPMENT), 
  CHILD_EQUIPMENT(TokenType.CHILD_EQUIPMENT), 
  DESCENDANT_EQUIPMENT(TokenType.DESCENDANT_EQUIPMENT); 
  // @formatter:on

  private static Map<TokenType, CurrentObjectFunction> modelsByTokenType =
      Arrays.stream(CurrentObjectFunction.values())
          .collect(toMap(CurrentObjectFunction::getTokenType, Function.identity()));

  private static Map<String, CurrentObjectFunction> modelsByName =
      Arrays.stream(CurrentObjectFunction.values())
          .collect(toMap(CurrentObjectFunction::getName, Function.identity()));

  private static Set<CurrentObjectFunction> deprecated =
      ImmutableSet.<CurrentObjectFunction>builder()
          .add(PARENT_EQUIPMENT, ANCESTOR_EQUIPMENT, CHILD_EQUIPMENT, DESCENDANT_EQUIPMENT)
          .build();

  private TokenType tokenType;
  private String name;

  static CurrentObjectFunction get(TokenType tokenType) {
    return modelsByTokenType.get(tokenType);
  }

  public static CurrentObjectFunction get(String name) {
    return modelsByName.get(name);
  }

  private CurrentObjectFunction(TokenType tokenType) {
    this.tokenType = tokenType;
    this.name = tokenType.getValue();
  }

  TokenType getTokenType() {
    return tokenType;
  }

  public String getName() {
    return name;
  }

  public boolean isDeprecated() {
    return deprecated.contains(this);
  }
}
