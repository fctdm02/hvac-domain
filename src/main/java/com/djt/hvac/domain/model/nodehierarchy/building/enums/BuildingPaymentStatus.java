package com.djt.hvac.domain.model.nodehierarchy.building.enums;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public enum BuildingPaymentStatus {
  
  UP_TO_DATE(1, "UP_TO_DATE"),
  DELINQUENT(2, "DELINQUENT");
  
  private static final Map<Integer, BuildingPaymentStatus> VALUES;
  
  private final int id;
  private final String name;
  
  static {
    Map<Integer, BuildingPaymentStatus> values = Maps.newHashMap();
    for (BuildingPaymentStatus value : BuildingPaymentStatus.values()) {
      values.put(value.id, value);
    }
    VALUES = ImmutableMap.copyOf(values);
  }
  
  public static BuildingPaymentStatus get(int id) {
    return VALUES.get(id);
  }
  
  private BuildingPaymentStatus(int id, String name) {
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