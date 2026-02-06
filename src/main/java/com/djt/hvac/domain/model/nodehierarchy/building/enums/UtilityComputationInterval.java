package com.djt.hvac.domain.model.nodehierarchy.building.enums;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public enum UtilityComputationInterval {
  
  DAILY(1, "Daily", com.djt.hvac.domain.model.common.dsl.baseline.ComputationInterval.DAILY),
  MONTHLY(2, "Monthly", com.djt.hvac.domain.model.common.dsl.baseline.ComputationInterval.MONTHLY),
  MONTHLY_CALC_DAILY(3, "Monthly (calculated daily)", com.djt.hvac.domain.model.common.dsl.baseline.ComputationInterval.MONTHLY_CALC_DAILY),
  HISTORICAL(4, "Historical", com.djt.hvac.domain.model.common.dsl.baseline.ComputationInterval.HISTORICAL);
  
  private static final Map<Integer, UtilityComputationInterval> INTERVALS;
  
  private final int id;
  private final String name;
  private final com.djt.hvac.domain.model.common.dsl.baseline.ComputationInterval baselineExpressionComputationInterval;
  
  static {
    Map<Integer, UtilityComputationInterval> intervals = Maps.newHashMap();
    for (UtilityComputationInterval interval : values()) {
      intervals.put(interval.id, interval);
    }
    INTERVALS = ImmutableMap.copyOf(intervals);
  }

  public static UtilityComputationInterval get (int id) {
    return INTERVALS.get(id);
  }
  
  public static UtilityComputationInterval getDefault () {
    return UtilityComputationInterval.DAILY;
  }
  private UtilityComputationInterval(int id, String name, com.djt.hvac.domain.model.common.dsl.baseline.ComputationInterval baselineExpressionComputationInterval) {
    this.id = id;
    this.name = name;
    this.baselineExpressionComputationInterval = baselineExpressionComputationInterval;
  }

  public com.djt.hvac.domain.model.common.dsl.baseline.ComputationInterval toBaselineExpressionComputationInterval () {
    return baselineExpressionComputationInterval;
  }
  
  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }
}
