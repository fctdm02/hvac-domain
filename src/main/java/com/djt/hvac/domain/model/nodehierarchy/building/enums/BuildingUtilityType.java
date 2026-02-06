package com.djt.hvac.domain.model.nodehierarchy.building.enums;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public enum BuildingUtilityType {
  ELECTRIC(1, "Electric", "kWh"),
  GAS(2, "Gas", "Mcf"),
  WATER(3, "Water", "gal");
  
  private static final Map<Integer, BuildingUtilityType> UTILITY_TYPES;
  
  private final int id;
  private final String name;
  private final String consumptionUnit;
  
  static {
    Map<Integer, BuildingUtilityType> types = Maps.newHashMap();
    for (BuildingUtilityType type : BuildingUtilityType.values()) {
      types.put(type.id, type);
    }
    UTILITY_TYPES = ImmutableMap.copyOf(types);
  }
  
  public static BuildingUtilityType get(int id) {
    return UTILITY_TYPES.get(id);
  }
  
  private BuildingUtilityType(
      int id, 
      String name,
      String consumptionUnit) {
    
    this.id = id;
    this.name = name;
    this.consumptionUnit = consumptionUnit;
  }
  
  public int getId() {
    return id;
  }
  
  public String getName() {
    return name;
  }
  
  public String getConsumptionUnit() {
    return consumptionUnit;
  }
}