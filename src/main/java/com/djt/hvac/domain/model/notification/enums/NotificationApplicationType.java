package com.djt.hvac.domain.model.notification.enums;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public enum NotificationApplicationType {
  
  FUSION("FUSION"),
  SYNERGY("SYNERGY");
  
  private static final Map<String, NotificationApplicationType> VALUES;
  
  private final String name;
  
  static {
    Map<String, NotificationApplicationType> values = Maps.newHashMap();
    for (NotificationApplicationType value : NotificationApplicationType.values()) {
      values.put(value.name, value);
    }
    VALUES = ImmutableMap.copyOf(values);
  }
  
  public static NotificationApplicationType get(String name) {
    
    if (name == null) {
      throw new IllegalArgumentException("value must be specified");  
    }
        
    NotificationApplicationType e = VALUES.get(name.trim().toUpperCase());
    if (e != null) {
      return e;
    }
    
    throw new IllegalArgumentException("Invalid value: ["
        + name
        + "], supported values are: "
        + VALUES.keySet());
  }
  
  private NotificationApplicationType(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
}