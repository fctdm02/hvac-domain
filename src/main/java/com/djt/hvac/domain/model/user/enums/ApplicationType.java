package com.djt.hvac.domain.model.user.enums;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public enum ApplicationType {
  FUSION_3_0(1, "Resolute Cloud Application 3.0"),
  SYNERGY_3_0(2, " Resolute Configuration Application 3.0"),
  SYNERGY_2_0(3, " Resolute Cloud Application 2.0");
  
  private static final Map<Integer, ApplicationType> VALUES;
  
  private final int id;
  private final String name;
  
  static {
    Map<Integer, ApplicationType> types = Maps.newHashMap();
    for (ApplicationType type : ApplicationType.values()) {
      types.put(type.id, type);
    }
    VALUES = ImmutableMap.copyOf(types);
  }
  
  public static ApplicationType get(int id) {
    return VALUES.get(id);
  }
  
  private ApplicationType(int id, String name) {
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