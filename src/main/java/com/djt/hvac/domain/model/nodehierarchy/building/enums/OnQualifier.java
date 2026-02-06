package com.djt.hvac.domain.model.nodehierarchy.building.enums;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public enum OnQualifier {
  DAY(1, "Day"),
  FIRST(2, "First"),
  SECOND(3, "Second"),
  THIRD(4, "Third"),
  FOURTH(5, "Fourth"),
  LAST(6, "Last");
  
  private static final Map<Integer, OnQualifier> MAP;
  
  private final int id;
  private final String name;
  
  static {
    Map<Integer, OnQualifier> types = Maps.newHashMap();
    for (OnQualifier type : OnQualifier.values()) {
      types.put(type.id, type);
    }
    MAP = ImmutableMap.copyOf(types);
  }
  
  public static OnQualifier get(int id) {
    return MAP.get(id);
  }
  
  private OnQualifier(int id, String name) {
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