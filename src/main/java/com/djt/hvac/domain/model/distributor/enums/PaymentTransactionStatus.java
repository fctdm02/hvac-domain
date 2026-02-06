package com.djt.hvac.domain.model.distributor.enums;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public enum PaymentTransactionStatus {
  
  PAID(1, "PAID", "Paid"),
  FAILED(2, "FAILED", "Failed");
  
  private static final Map<Integer, PaymentTransactionStatus> VALUES;
  
  private final int id;
  private final String name;
  private final String displayName;
  
  static {
    Map<Integer, PaymentTransactionStatus> values = Maps.newHashMap();
    for (PaymentTransactionStatus value : PaymentTransactionStatus.values()) {
      values.put(value.id, value);
    }
    VALUES = ImmutableMap.copyOf(values);
  }
  
  public static PaymentTransactionStatus get(int id) {
    return VALUES.get(id);
  }
  
  private PaymentTransactionStatus(int id, String name, String displayName) {
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