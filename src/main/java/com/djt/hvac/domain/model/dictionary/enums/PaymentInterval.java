package com.djt.hvac.domain.model.dictionary.enums;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public enum PaymentInterval {
  
  MONTHLY(1, "MONTHLY", "month"),
  YEARLY(2, "YEARLY", "year");
  
  private static final Map<Integer, PaymentInterval> VALUES_BY_ID;
  private static final Map<String, PaymentInterval> VALUES_BY_NAME;
  
  private final int id;
  private final String name;
  private final String displayName;
  
  static {
    Map<Integer, PaymentInterval> valuesById = Maps.newHashMap();
    Map<String, PaymentInterval> valuesByName = Maps.newHashMap();
    for (PaymentInterval value : PaymentInterval.values()) {
      
      valuesById.put(value.id, value);
      valuesByName.put(value.name, value);
    }
    VALUES_BY_ID = ImmutableMap.copyOf(valuesById);
    VALUES_BY_NAME = ImmutableMap.copyOf(valuesByName);
  }
  
  public static PaymentInterval get(int id) {
    return VALUES_BY_ID.get(id);
  }

  public static PaymentInterval get(String name) {
    return VALUES_BY_NAME.get(name);
  }
  
  private PaymentInterval(int id, String name, String displayName) {
    this.id = id;
    this.name = name;
    this.displayName = displayName;
  }
  
  public int getId() {
    return id;
  }
  
  public String getName() {
    return name;
  }
  
  public String getDisplayName() {
    return displayName;
  }  
}