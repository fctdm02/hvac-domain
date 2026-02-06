package com.djt.hvac.domain.model.dictionary.enums;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/*
 *  SCHEDULED, WEATHER_STATION, CUSTOM, COMPUTED, RULE, SYSTEM
 */
public enum NodeSubType {
  NONE(0, "None"),
  MAPPABLE_POINT(1, "MappablePoint"),
  CUSTOM_ASYNC_COMPUTED_POINT(2, "CustomAsyncComputedPoint"),
  SCHEDULED_ASYNC_COMPUTED_POINT(3, "ScheduledAsyncComputedPoint"),
  WEATHER_ASYNC_COMPUTED_POINT(4, "WeatherAsyncComputedPoint"),
  AD_FUNCTION_ASYNC_COMPUTED_POINT(5, "AdFunctionAsyncComputedPoinPoint"),
  SYSTEM_ASYNC_COMPUTED_POINT(6, "SystemAsyncComputedPointPoint"),
  MANUAL_ASYNC_COMPUTED_POINT(7, "ManualAsyncComputedPointPoint");
  
  private static final Map<Integer, NodeSubType> NODE_SUB_TYPES;
  
  private final int id;
  private final String name;
  
  static {
    Map<Integer, NodeSubType> types = Maps.newHashMap();
    for (NodeSubType type : NodeSubType.values()) {
      types.put(type.id, type);
    }
    NODE_SUB_TYPES = ImmutableMap.copyOf(types);
  }
  
  public static NodeSubType get(int id) {
    return NODE_SUB_TYPES.get(id);
  }
  
  private NodeSubType(int id, String name) {
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