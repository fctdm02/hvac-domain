package com.djt.hvac.domain.model.dictionary.enums;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public enum UnitSystem {
  
  IP("IP"),
  SI("SI");
  
  private static final Map<String, UnitSystem> VALUES;
  
  private final String name;
  
  static {
    Map<String, UnitSystem> values = Maps.newHashMap();
    for (UnitSystem value : UnitSystem.values()) {
      values.put(value.name, value);
    }
    VALUES = ImmutableMap.copyOf(values);
  }
  
  public static UnitSystem get(String name) {
    
    if (name == null || name.trim().isEmpty()) {
      // TODO: TDM: RP-13090: Change back to throw an exception once the exported customer data DTOs have unit_system filled.
      // throw new IllegalArgumentException("name cannot be null/empty");
      return IP;
    }
    UnitSystem unitSystem = VALUES.get(name.trim().toUpperCase());
    if (unitSystem != null) {
      return unitSystem;
    }
    throw new IllegalArgumentException("Invalid UnitSystem value: ["
        + name
        + "], supported values are: [IP, SI].");
  }
  
  private UnitSystem(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
}