package com.djt.hvac.domain.model.nodehierarchy.building.enums;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public enum EveryMonth {
  JANUARY(1, "January"),
  FEBRUARY(2, "February"),
  MARCH(3, "March"),
  APRIL(4, "April"),
  MAY(5, "May"),
  JUNE(6, "June"),
  JULY(7, "July"),
  AUGUST(8, "August"),
  SEPTEMBER(9, "September"),
  OCTOBER(10, "October"),
  NOVEMBER(11, "November"),
  DECEMBER(12, "December");
  
  private static final Map<Integer, EveryMonth> MAP;
  
  private final int id;
  private final String name;
  
  static {
    Map<Integer, EveryMonth> types = Maps.newHashMap();
    for (EveryMonth type : EveryMonth.values()) {
      types.put(type.id, type);
    }
    MAP = ImmutableMap.copyOf(types);
  }
  
  public static EveryMonth get(int id) {
    return MAP.get(id);
  }
  
  private EveryMonth(int id, String name) {
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