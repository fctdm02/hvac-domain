package com.djt.hvac.domain.model.common.dsl.baseline;

import static com.djt.hvac.domain.model.common.dsl.baseline.ComputationInterval.DAILY;
import static com.djt.hvac.domain.model.common.dsl.baseline.ComputationInterval.HISTORICAL;
import static com.djt.hvac.domain.model.common.dsl.baseline.ComputationInterval.MONTHLY;
import static com.djt.hvac.domain.model.common.dsl.baseline.ComputationInterval.MONTHLY_CALC_DAILY;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class VariableId<T> implements Comparable<VariableId<T>> {

  public static final VariableId<Double> AVG_DAILY_TEMP =
      new VariableId<>("avg_daily_temp", Double.class, Sets.immutableEnumSet(DAILY));

  public static final VariableId<Boolean> WEEK_DAY =
      new VariableId<>("week_day", Boolean.class, Sets.immutableEnumSet(DAILY));

  public static final VariableId<Double> AVG_MONTHLY_TEMP =
      new VariableId<>("avg_monthly_temp", Double.class,
          Sets.immutableEnumSet(MONTHLY, MONTHLY_CALC_DAILY));

  public static final VariableId<Double> ELAPSED_DAYS_IN_MONTH = new VariableId<>(
      "elapsed_days_in_month", Double.class, Sets.immutableEnumSet(MONTHLY_CALC_DAILY));

  public static final VariableId<Double> SAME_MONTH_LAST_YEAR_TOTAL = new VariableId<>(
      "same_month_last_year_total", Double.class, Sets.immutableEnumSet(HISTORICAL));

  public static final VariableId<Double> SAME_MONTH_YEAR = new VariableId<>(
      "same_month_year", Double.class, Sets.immutableEnumSet(HISTORICAL),
      ImmutableList.of(NumericLiteral.class));
  
  public static final VariableId<Double> UTILITY_BILL_SAME_MONTH_LAST_YEAR = new VariableId<> (
		  "utility_bill_same_month_last_year", Double.class, Sets.immutableEnumSet(HISTORICAL));

  public static final VariableId<Double> UTILITY_BILL_SAME_MONTH_YEAR = new VariableId<> (
		  "utility_bill_same_month_year", Double.class, Sets.immutableEnumSet(HISTORICAL),
		  ImmutableList.of(NumericLiteral.class));
  
  private static final VariableId<?>[] VALUES = {AVG_DAILY_TEMP, WEEK_DAY, AVG_MONTHLY_TEMP,
      ELAPSED_DAYS_IN_MONTH, SAME_MONTH_LAST_YEAR_TOTAL, SAME_MONTH_YEAR, 
      UTILITY_BILL_SAME_MONTH_LAST_YEAR, UTILITY_BILL_SAME_MONTH_YEAR};

  private static final Map<String, VariableId<?>> VARIABLE_IDS;

  private final String name;
  private final Class<T> type;
  private final Set<ComputationInterval> computationIntervals;
  private final List<Class<? extends Literal<?>>> parameterTypes;

  static {
    Map<String, VariableId<?>> ids = Maps.newHashMap();
    for (VariableId<?> id : VariableId.VALUES) {
      ids.put(id.name, id);
    }
    VARIABLE_IDS = ImmutableMap.copyOf(ids);
  }

  public static VariableId<?>[] values() {
    return Arrays.copyOf(VALUES, VALUES.length);
  }

  @SuppressWarnings("unchecked")
  public static <T> VariableId<T> get(String name) {
    if (name == null) {
      return null;
    }
    return (VariableId<T>) VARIABLE_IDS.get(name.toLowerCase());
  }

  private VariableId(String name, Class<T> type, Set<ComputationInterval> computationIntervals) {
    this(name, type, computationIntervals, ImmutableList.of());
  }

  private VariableId(String name, Class<T> type, Set<ComputationInterval> computationIntervals,
      List<Class<? extends Literal<?>>> parameterTypes) {
    this.name = name;
    this.type = type;
    this.computationIntervals = computationIntervals;
    this.parameterTypes = parameterTypes;
  }

  public String getName() {
    return name;
  }

  public Class<T> getType() {
    return type;
  }

  public Set<ComputationInterval> getComputationIntervals() {
    return computationIntervals;
  }

  public List<Class<? extends Literal<?>>> getParameterTypes() {
    return parameterTypes;
  }

  @Override
  public String toString() {
    return this.name;
  }

  @Override
  public int compareTo(VariableId<T> o) {
    return this.name.compareTo(o.name);
  }

}
