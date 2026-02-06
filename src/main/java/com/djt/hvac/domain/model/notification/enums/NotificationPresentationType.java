package com.djt.hvac.domain.model.notification.enums;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public enum NotificationPresentationType {
  
  EMAIL("EMAIL"),
  MODAL_POPUP("MODAL_POPUP"),
  TOAST("TOAST"),
  BADGE("BADGE");
  
  private static final Map<String, NotificationPresentationType> VALUES;
  
  private final String name;
  
  static {
    Map<String, NotificationPresentationType> values = Maps.newHashMap();
    for (NotificationPresentationType value : NotificationPresentationType.values()) {
      values.put(value.name, value);
    }
    VALUES = ImmutableMap.copyOf(values);
  }
  
  public static NotificationPresentationType get(String name) {
    
    if (name == null) {
      throw new IllegalArgumentException("value must be specified");  
    }
        
    NotificationPresentationType e = VALUES.get(name.trim().toUpperCase());
    if (e != null) {
      return e;
    }
    
    throw new IllegalArgumentException("Invalid value: ["
        + name
        + "], supported values are: "
        + VALUES.keySet());
  }
  
  private NotificationPresentationType(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
}