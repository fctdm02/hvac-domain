package com.djt.hvac.domain.model.notification.enums;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public enum EmailType {
  
  AT_TIME_OF_EVENT("AT_TIME_OF_EVENT"),
  BI_MONTHLY_NEWS_LETTER("BI_MONTHLY_NEWS_LETTER"),
  MORNING_EVENING_ALERT_EMAIL("MORNING_EVENING_ALERT_EMAIL");
  
  private static final Map<String, EmailType> VALUES;
  
  private final String name;
  
  static {
    Map<String, EmailType> values = Maps.newHashMap();
    for (EmailType value : EmailType.values()) {
      values.put(value.name, value);
    }
    VALUES = ImmutableMap.copyOf(values);
  }
  
  public static EmailType get(String name) {
    
    if (name == null) {
      throw new IllegalArgumentException("value must be specified");  
    }
        
    EmailType e = VALUES.get(name.trim().toUpperCase());
    if (e != null) {
      return e;
    }
    
    throw new IllegalArgumentException("Invalid value: ["
        + name
        + "], supported values are: "
        + VALUES.keySet());
  }
  
  private EmailType(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
}