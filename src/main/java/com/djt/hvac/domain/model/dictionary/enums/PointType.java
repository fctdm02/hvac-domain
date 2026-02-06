package com.djt.hvac.domain.model.dictionary.enums;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public enum PointType {
  MAPPABLE(1, "Mappable"),
  COMPUTED_SYNCHRONOUSLY(2, "Computed Synchronously"),
  COMPUTED_ASYNCHRONOUSLY(3, "Computed Asynchronously");
  
  private static final Map<Integer, PointType> POINT_TYPES;
  
  private final int id;
  private final String name;
  
  static {
    Map<Integer, PointType> types = Maps.newHashMap();
    for (PointType type : PointType.values()) {
      types.put(type.id, type);
    }
    POINT_TYPES = ImmutableMap.copyOf(types);
  }
  
  public static PointType get(int id) {
    return POINT_TYPES.get(id);
  }
  
  private PointType(int id, String name) {
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