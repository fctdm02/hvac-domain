package com.djt.hvac.domain.model.dictionary.energyexchange.enums;

import java.util.HashMap;
import java.util.Map;

public enum EnergyExchangeSystemType {
  CHILLED_WATER(1, "Chilled Water"),
  HOT_WATER(2, "Hot Water"),
  STEAM(3, "Steam"),
  AIR_SUPPLY(4, "Air Supply");
  
  private static final Map<Integer, EnergyExchangeSystemType> TYPES;
  
  private final int id;
  private final String name;
  
  static {
    TYPES = new HashMap<>();
    for (EnergyExchangeSystemType type : EnergyExchangeSystemType.values()) {
      TYPES.put(type.id, type);
    }
  }
  
  public static EnergyExchangeSystemType get(int id) {
    return TYPES.get(id);
  }
  
  private EnergyExchangeSystemType(int id, String name) {
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

