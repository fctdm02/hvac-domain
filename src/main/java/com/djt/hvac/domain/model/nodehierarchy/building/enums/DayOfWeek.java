package com.djt.hvac.domain.model.nodehierarchy.building.enums;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public enum DayOfWeek {
  MONDAY(1, "Monday"),
  TUESDAY(2, "Tuesday"),
  WEDNESDAY(3, "Wednesday"),
  THURSDAY(4, "Thursday"),
  FRIDAY(5, "Friday"),
  SATURDAY(6, "Saturday"),
  SUNDAY(7, "Sunday");
  
  private static final Map<Integer, DayOfWeek> MAP;
  
  private final int id;
  private final String name;
  
  static {
    Map<Integer, DayOfWeek> types = Maps.newHashMap();
    for (DayOfWeek type : DayOfWeek.values()) {
      types.put(type.id, type);
    }
    MAP = ImmutableMap.copyOf(types);
  }
  
  public static DayOfWeek get(int id) {
    return MAP.get(id);
  }
  
  private DayOfWeek(int id, String name) {
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