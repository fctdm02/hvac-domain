package com.djt.hvac.domain.model.nodehierarchy.building.enums;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public enum BuildingPaymentType {
  
  ONLINE("ONLINE"),
  OUT_OF_BAND("OUT_OF_BAND");
  
  private static final Map<String, BuildingPaymentType> ENUMS;
  
  private final String name;
  
  static {
    Map<String, BuildingPaymentType> enums = Maps.newHashMap();
    for (BuildingPaymentType e : BuildingPaymentType.values()) {
      enums.put(e.name(), e);
    }
    ENUMS = ImmutableMap.copyOf(enums);
  }
  
  public static BuildingPaymentType get(String name) {
    
    if (name != null 
        && (name.trim().equalsIgnoreCase("ONLINE") 
        || name.trim().equals("OUT_OF_BAND"))) {
    
      return ENUMS.get(name.trim().toUpperCase());
    }
    return ONLINE;
  }
  
  private BuildingPaymentType(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
}
