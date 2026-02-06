package com.djt.hvac.domain.model.user.enums;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public enum UserType {
  CUSTOMER_USER(1, "Customer User"),
  DISTRIBUTOR_USER(2, "Distributor User");
  
  private static final Map<Integer, UserType> VALUES;
  
  private final int id;
  private final String name;
  
  static {
    Map<Integer, UserType> types = Maps.newHashMap();
    for (UserType type : UserType.values()) {
      types.put(type.id, type);
    }
    VALUES = ImmutableMap.copyOf(types);
  }
  
  public static UserType get(int id) {
    return VALUES.get(id);
  }
  
  private UserType(int id, String name) {
    this.id = id;
    this.name = name;
  }
  
  public int getId() {
    return id;
  }
  
  public String getName() {
    return name;
  }
}