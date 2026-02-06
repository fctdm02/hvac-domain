package com.djt.hvac.domain.model.common.dsl.computedpoint.v2;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public enum IntervalAlignment {

  Floor("af"), Nearest("nf"), Ceiling("cf");

  private String token;

  private IntervalAlignment(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }

  private static final Map<String, IntervalAlignment> TYPES;

  static {
    Map<String, IntervalAlignment> types = Maps.newHashMap();
    for (IntervalAlignment type : IntervalAlignment.values()) {
      types.put(type.token, type);
    }

    TYPES = ImmutableMap.copyOf(types);
  }

  public static IntervalAlignment of(String token) {
    return TYPES.get(token);
  }
}
