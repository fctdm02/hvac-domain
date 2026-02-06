package com.djt.hvac.domain.model.dictionary.enums;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public enum FunctionType {
  RULE(1, "Rule"),
  COMPUTED_POINT(2, "Computed Point");
  
  private static final Map<Integer, FunctionType> TYPES;
  
  private final int id;
  private final String name;
  
  static {
    Map<Integer, FunctionType> types = Maps.newHashMap();
    for (FunctionType type : FunctionType.values()) {
      types.put(type.id, type);
    }
    TYPES = ImmutableMap.copyOf(types);
  }
  
  public static FunctionType get(int id) {
    return TYPES.get(id);
  }

  public static FunctionType get(String name) {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("name must be specified.");
    }
    if (name.equalsIgnoreCase("Rule")) {
      return FunctionType.get(1);
    } else if (name.equalsIgnoreCase("Computed Point") || name.equalsIgnoreCase("COMPUTED_POINT")) {
      return FunctionType.get(2);
    }
    throw new IllegalArgumentException("AD Function Type with name: [" + name + "] does not exist, only 'Rule' and 'Computed Point' are supported");
  }
  
  private FunctionType(int id, String name) {
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