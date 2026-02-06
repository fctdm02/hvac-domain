package com.djt.hvac.domain.model.common.dsl.currentobject;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public enum NodeType {
  // @formatter:off
  PORTFOLIO(TokenType.PORTFOLIO, 1),
  SITE(TokenType.SITE, 2),
  BUILDING(TokenType.BUILDING, 3),
  SUB_BUILDING(TokenType.SUB_BUILDING, 4),
  FLOOR(TokenType.FLOOR, 5),
  ZONE(TokenType.ZONE, 6),
  METER(TokenType.METER, 7),
  EQUIPMENT(TokenType.EQUIPMENT, 8),
  PLANT(TokenType.PLANT, 11),
  LOOP(TokenType.LOOP, 12);
  // @formatter:on

  private static Map<TokenType, NodeType> nodeTypesByTokenType = Arrays.stream(NodeType.values())
      .collect(toMap(NodeType::getTokenType, Function.identity()));

  private static Map<String, NodeType> nodeTypesByName = Arrays.stream(NodeType.values())
      .collect(toMap(NodeType::getName, Function.identity()));

  private static Map<Integer, NodeType> nodeTypesById = Arrays.stream(NodeType.values())
      .collect(toMap(NodeType::getId, Function.identity()));

  private TokenType tokenType;
  private int id;
  private String name;

  static NodeType get(TokenType tokenType) {
    return nodeTypesByTokenType.get(tokenType);
  }

  public static NodeType get(String name) {
    return nodeTypesByName.get(name);
  }

  public static NodeType get(int id) {
    return nodeTypesById.get(id);
  }

  private NodeType(TokenType tokenType, int id) {
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
