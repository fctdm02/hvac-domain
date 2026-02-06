package com.djt.hvac.domain.model.dictionary.enums;

import java.util.HashMap;
import java.util.Map;

public enum AggregatorType {
  AVG(1, "avg", "Average"),
  COUNT(2, "count", "Count"),
  DEV(3, "dev", "Stamdard Deviation"),
  MIN(4, "min", "Minimum"),
  MAX(5, "max", "Maximum"),
  SUM(6, "sum", "Sum");
  
  private static final Map<Integer, AggregatorType> AGGREGATOR_TYPES;
  
  private final int id;
  private final String name;
  private final String displayName;
  
  static {
    AGGREGATOR_TYPES = new HashMap<>();
    for (AggregatorType type : AggregatorType.values()) {
      AGGREGATOR_TYPES.put(type.id, type);
    }
  }
  
  public static AggregatorType get(int id) {
    return AGGREGATOR_TYPES.get(id);
  }
  
  private AggregatorType(int id, String name, String displayName) {
    this.id = id;
    this.name = name;
    this.displayName = displayName;
  }
  
  public int getId() {
    return id;
  }
  
  public String getName() {
    return name;
  }

  public String getDisplayName() {
    return displayName;
  }  
}
