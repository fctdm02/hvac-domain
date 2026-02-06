package com.djt.hvac.domain.model.notification.enums;

import java.util.Comparator;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public enum NotificationEventAppType implements Comparator<NotificationEventAppType> {

  SYNERGY("SYNERGY"),
  FUSION("FUSION"),
  BOTH("BOTH");
  
  private static final Map<String, NotificationEventAppType> VALUES;
  
  private final String name;
  
  static {
    Map<String, NotificationEventAppType> values = Maps.newHashMap();
    for (NotificationEventAppType value : NotificationEventAppType.values()) {
      values.put(value.name, value);
    }
    VALUES = ImmutableMap.copyOf(values);
  }
  
  public static NotificationEventAppType get(String name) {
    
    if (name == null || name.trim().isEmpty()) {
      return NotificationEventAppType.SYNERGY;
    }
    NotificationEventAppType notificationEventAppType = VALUES.get(name.trim().toUpperCase());
    if (notificationEventAppType != null) {
      return notificationEventAppType;
    }
    throw new IllegalArgumentException("Invalid NotificationEventAppType value: ["
        + name
        + "], supported values are: ['SYNERGY', 'FUSION' or 'HIGH'].");
  }
  
  private NotificationEventAppType(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }

  @Override
  public int compare(NotificationEventAppType o1, NotificationEventAppType o2) {
    
    String name1 = o1.getName();
    String name2 = o2.getName();
    
    int compareTo = 0;
    if (name1.equals(SYNERGY.toString()) && (name2.equals(FUSION.toString()) || name2.equals(BOTH.toString()))) {
      compareTo = -1;
    } else if (name1.equals(FUSION.toString()) && name2.equals(BOTH.toString())) {
      compareTo = -1;
    } else if (name1.equals(FUSION.toString()) && name2.equals(SYNERGY.toString())) {
      compareTo = 1;
    } else if (name1.equals(BOTH.toString()) && (name2.equals(SYNERGY.toString()) || name2.equals(FUSION.toString()))) {
      compareTo = 1;
    }
    return compareTo;
  }
}