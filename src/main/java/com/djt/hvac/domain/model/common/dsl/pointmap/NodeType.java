package com.djt.hvac.domain.model.common.dsl.pointmap;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;

public enum NodeType {
  // @formatter:off
  BUILDING("building"), 
  SUB_BUILDING("subBuilding"),
  PLANT("plant"),
  FLOOR("floor"), 
  EQUIPMENT("equipment"),
  POINT("point");
  // @formatter:on

  private static final Map<String, NodeType> NODE_TYPES = Arrays.stream(NodeType.values())
      .collect(collectingAndThen(
          toMap(NodeType::getValue, Function.identity()),
          ImmutableMap::copyOf));

  private final String value;

  static boolean isValid(String value) {
    return NODE_TYPES.containsKey(value);
  }

  static Optional<NodeType> get(String value) {
    return Optional.ofNullable(NODE_TYPES.get(value));
  }

  private NodeType(String value) {
    this.value = value;
  }

  String getValue() {
    return value;
  }

  String getVariable() {
    return "{" + value + "}";
  }

}
