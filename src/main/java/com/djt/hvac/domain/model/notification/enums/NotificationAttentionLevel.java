package com.djt.hvac.domain.model.notification.enums;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public enum NotificationAttentionLevel {
  
  LOW("LOW"),
  MEDIUM("MEDIUM"),
  HIGH("HIGH");
  
  private static final Map<String, NotificationAttentionLevel> VALUES;
  
  private final String name;
  
  static {
    Map<String, NotificationAttentionLevel> values = Maps.newHashMap();
    for (NotificationAttentionLevel value : NotificationAttentionLevel.values()) {
      values.put(value.name, value);
    }
    VALUES = ImmutableMap.copyOf(values);
  }
  
  public static NotificationAttentionLevel get(String name) {
    
    if (name == null) {
      throw new IllegalArgumentException("value must be specified");  
    }
        
    NotificationAttentionLevel e = VALUES.get(name.trim().toUpperCase());
    if (e != null) {
      return e;
    }
    
    throw new IllegalArgumentException("Invalid value: ["
        + name
        + "], supported values are: "
        + VALUES.keySet());
  }
  
  private NotificationAttentionLevel(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
}