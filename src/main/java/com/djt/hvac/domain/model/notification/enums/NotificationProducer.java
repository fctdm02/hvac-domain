package com.djt.hvac.domain.model.notification.enums;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public enum NotificationProducer {
  
  SYSTEM("SYSTEM"),
  USER("USER");
  
  private static final Map<String, NotificationProducer> VALUES;
  
  private final String name;
  
  static {
    Map<String, NotificationProducer> values = Maps.newHashMap();
    for (NotificationProducer value : NotificationProducer.values()) {
      values.put(value.name, value);
    }
    VALUES = ImmutableMap.copyOf(values);
  }
  
  public static NotificationProducer get(String name) {
    
    if (name == null) {
      throw new IllegalArgumentException("value must be specified");  
    }
        
    NotificationProducer e = VALUES.get(name.trim().toUpperCase());
    if (e != null) {
      return e;
    }
    
    throw new IllegalArgumentException("Invalid value: ["
        + name
        + "], supported values are: "
        + VALUES.keySet());
  }
  
  private NotificationProducer(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
  
  public static Set<String> getValues() {
    return VALUES.keySet();
  }
}