package com.djt.hvac.domain.model.distributor.enums;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public enum PaymentMethodType {
  
  CREDIT_CARD(1, "CREDIT_CARD", "Credit Card"),
  ACH(2, "ACH", "ACH");
  
  private static final Map<Integer, PaymentMethodType> VALUES;
  
  private final int id;
  private final String name;
  private final String displayName;
  
  static {
    Map<Integer, PaymentMethodType> values = Maps.newHashMap();
    for (PaymentMethodType value : PaymentMethodType.values()) {
      values.put(value.id, value);
    }
    VALUES = ImmutableMap.copyOf(values);
  }
  
  public static PaymentMethodType get(int id) {
    return VALUES.get(id);
  }
  
  private PaymentMethodType(int id, String name, String displayName) {
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