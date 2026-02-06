package com.djt.hvac.domain.model.dictionary.template.report.enums;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public enum ReportState {
  
  ENABLED("ENABLED"),
  DISABLED("DISABLED"),
  IGNORED("IGNORED");
  
  private static final Map<String, ReportState> VALUES;
  
  private final String name;
  
  static {
    Map<String, ReportState> values = Maps.newHashMap();
    for (ReportState value : ReportState.values()) {
      values.put(value.name, value);
    }
    VALUES = ImmutableMap.copyOf(values);
  }
  
  public static ReportState get(String name) {
    
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("ReportState 'name' must be specified and one of: ['ENABLED', 'DISABLED' or 'IGNORED'].");
    }
    ReportState reportState = VALUES.get(name.trim().toUpperCase());
    if (reportState != null) {
      return reportState;
    }
    throw new IllegalArgumentException("Invalid ReportState value: ["
        + name
        + "], supported values are: ['ENABLED', 'DISABLED' or 'IGNORED'].");
  }
  
  private ReportState(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
}