package com.djt.hvac.domain.model.dictionary.enums;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public enum TagType {
  MARKER(1, "Marker"),
  KEY_VALUE(2, "Key-Value");
  
  private static final Map<Integer, TagType> DATA_TYPES;
  
  private final int id;
  private final String name;
  
  static {
    Map<Integer, TagType> types = Maps.newHashMap();
    for (TagType type : TagType.values()) {
      types.put(type.id, type);
    }
    DATA_TYPES = ImmutableMap.copyOf(types);
  }
  
  public static TagType get(int id) {
    return DATA_TYPES.get(id);
  }
  
  private TagType(int id, String name) {
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
