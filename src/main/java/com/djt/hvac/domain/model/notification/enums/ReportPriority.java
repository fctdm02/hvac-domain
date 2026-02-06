package com.djt.hvac.domain.model.notification.enums;

import java.util.Comparator;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public enum ReportPriority implements Comparator<ReportPriority> {
  
  LOW("LOW"),
  MEDIUM("MEDIUM"),
  HIGH("HIGH");
  
  private static final Map<String, ReportPriority> VALUES;
  
  private final String name;
  
  static {
    Map<String, ReportPriority> values = Maps.newHashMap();
    for (ReportPriority value : ReportPriority.values()) {
      values.put(value.name, value);
    }
    VALUES = ImmutableMap.copyOf(values);
  }
  
  public static ReportPriority get(String name) {
    
    if (name == null || name.trim().isEmpty()) {
      return ReportPriority.LOW;
    }
    ReportPriority reportPriority = VALUES.get(name.trim().toUpperCase());
    if (reportPriority != null) {
      return reportPriority;
    }
    throw new IllegalArgumentException("Invalid ReportPriority value: ["
        + name
        + "], supported values are: ['LOW', 'MEDIUM' or 'HIGH'].");
  }
  
  private ReportPriority(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }

  @Override
  public int compare(ReportPriority o1, ReportPriority o2) {
    
    String name1 = o1.getName();
    String name2 = o2.getName();
    
    int compareTo = 0;
    if (name1.equals(LOW.toString()) && (name2.equals(MEDIUM.toString()) || name2.equals(HIGH.toString()))) {
      compareTo = -1;
    } else if (name1.equals(MEDIUM.toString()) && name2.equals(HIGH.toString())) {
      compareTo = -1;
    } else if (name1.equals(MEDIUM.toString()) && name2.equals(LOW.toString())) {
      compareTo = 1;
    } else if (name1.equals(HIGH.toString()) && (name2.equals(LOW.toString()) || name2.equals(MEDIUM.toString()))) {
      compareTo = 1;
    }
    return compareTo;
  }
}
