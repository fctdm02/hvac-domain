package com.djt.hvac.domain.model.dictionary.enums;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public enum NodeType {
  PORTFOLIO(1, "Portfolio"),
  SITE(2, "Site"),
  BUILDING(3, "Building"),
  SUB_BUILDING(4, "Sub-building"),
  FLOOR(5, "Floor"),
  ZONE(6, "Zone"),
  METER(7, "Meter"),
  EQUIPMENT(8, "Equipment"),
  POINT(9, "Point"),
  AREA_OF_INTEREST(10, "Area of Interest"),
  PLANT(11, "Plant"),
  LOOP(12, "Loop");
  
  private static final Map<Integer, NodeType> NODE_TYPES;
  
  private final int id;
  private final String name;
  
  static {
    Map<Integer, NodeType> types = Maps.newHashMap();
    for (NodeType type : NodeType.values()) {
      types.put(type.id, type);
    }
    NODE_TYPES = ImmutableMap.copyOf(types);
  }
  
  public static NodeType get(int id) {
    return NODE_TYPES.get(id);
  }
  
  private NodeType(int id, String name) {
    this.id = id;
    this.name = name;
  }
  
  public int getId() {
    return id;
  }
  
  public String getName() {
    return name;
  }
}