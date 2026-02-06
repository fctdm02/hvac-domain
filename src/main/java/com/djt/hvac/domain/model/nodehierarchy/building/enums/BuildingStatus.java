package com.djt.hvac.domain.model.nodehierarchy.building.enums;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public enum BuildingStatus {

  CREATED(1, "CREATED"), 
  PENDING_ACTIVATION(2, "PENDING_ACTIVATION"), // ONLINE ONLY
  ACTIVE(3, "ACTIVE"); // ONLINE ONLY

  private static final Map<Integer, BuildingStatus> VALUES;

  private final int id;
  private final String name;

  static {
    Map<Integer, BuildingStatus> values = Maps.newHashMap();
    for (BuildingStatus value : BuildingStatus.values()) {
      values.put(value.id, value);
    }
    VALUES = ImmutableMap.copyOf(values);
  }

  public static BuildingStatus get(int id) {
    return VALUES.get(id);
  }

  public static BuildingStatus get(String status) {
    for (BuildingStatus bs: VALUES.values()) {
      if (bs.getName().equalsIgnoreCase(status)) {
        return bs;
      }
    }
    return null;
  }

  private BuildingStatus(int id, String name) {
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
