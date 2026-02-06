package com.djt.hvac.domain.model.common.dsl.currentobject;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public enum Model {
  // @formatter:off
  STANDARD(TokenType.STANDARD, 0),
  CHILLED_WATER(TokenType.CHILLED_WATER, 1),
  HOT_WATER(TokenType.HOT_WATER, 2),
  STEAM(TokenType.STEAM, 3),
  AIR_SUPPLY(TokenType.AIR_SUPPLY, 4);
  // @formatter:on

  private static Map<TokenType, Model> modelsByTokenType = Arrays.stream(Model.values())
      .collect(toMap(Model::getTokenType, Function.identity()));

  private static Map<String, Model> modelsByName = Arrays.stream(Model.values())
      .collect(toMap(Model::getName, Function.identity()));

  private static Map<Integer, Model> modelsById = Arrays.stream(Model.values())
      .collect(toMap(Model::getId, Function.identity()));

  private TokenType tokenType;
  private int id;
  private String name;

  static Model get(TokenType tokenType) {
    return modelsByTokenType.get(tokenType);
  }

  public static Model get(String name) {
    return modelsByName.get(name);
  }

  public static Model get(int id) {
    return modelsById.get(id);
  }

  private Model(TokenType tokenType, int id) {
    this.tokenType = tokenType;
    this.name = tokenType.getValue();
    this.id = id;
  }

  TokenType getTokenType() {
    return tokenType;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }
}
