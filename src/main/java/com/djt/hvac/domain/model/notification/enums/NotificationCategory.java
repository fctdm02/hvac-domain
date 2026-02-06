package com.djt.hvac.domain.model.notification.enums;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public enum NotificationCategory {
  
  SYSTEM("SYSTEM"),
  APPLICATION_ALERT("APPLICATION_ALERT"),
  APPLICATION_STATUS("APPLICATION_STATUS");
  
  private static final Map<String, NotificationCategory> VALUES;
  
  private final String name;
  
  static {
    Map<String, NotificationCategory> values = Maps.newHashMap();
    for (NotificationCategory value : NotificationCategory.values()) {
      values.put(value.name, value);
    }
    VALUES = ImmutableMap.copyOf(values);
  }
  
  public static NotificationCategory get(String name) {
    
    if (name == null) {
      throw new IllegalArgumentException("value must be specified");  
    }
        
    NotificationCategory e = VALUES.get(name.trim().toUpperCase());
    if (e != null) {
      return e;
    }
    
    throw new IllegalArgumentException("Invalid value: ["
        + name
        + "], supported values are: "
        + VALUES.keySet());
  }
  
  private NotificationCategory(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
}