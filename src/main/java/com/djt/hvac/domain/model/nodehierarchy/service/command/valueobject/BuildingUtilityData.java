package com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Map;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.Maps;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = BuildingUtilityData.Builder.class)
public class BuildingUtilityData implements Serializable {
  
  private static final long serialVersionUID = 1L;
  
  /*
    **utilityId**
    
    ```
    utilityId   Type        Unit Consumption    Unit Demand     KBTU Conversion Factor
    ---------   -------     ----------------    -----------     ----------------------
    1           Electric    kWh                 kW              3.412
    2           Gas         Mcf                                 1,026
    3           Water       gal                                 0
    ```
    
    
    
    **computationIntervalId**
    
    ```
    computationIntervalId       Name
    ---------------------       ----
    1                           Daily
    2                           Monthly
    3                           Monthly (calculated daily)
    4                           Historical
   */
  public static final int ELECTRIC_UTILITY_ID = 1;
  public static final int GAS_UTILITY_ID = 2;
  public static final int WATER_UTILITY_ID = 3;
  
  public static final int DAILY_COMPUTATION_INTERVAL_ID = 1;
  public static final int MONTHLY_COMPUTATION_INTERVAL_ID = 2;
  public static final int MONTHLY_CALCULATED_DAILY_COMPUTATION_INTERVAL_ID = 3;
  public static final int HISTORICAL_COMPUTATION_INTERVAL_ID = 4;
  
  private static final Map<Integer, String> UTILITY_ID_MAPPINGS = Maps.newHashMap();
  private static final Map<Integer, String> COMPUTATION_INTERVAL_ID_MAPPINGS = Maps.newHashMap();
  static {
    UTILITY_ID_MAPPINGS.put(Integer.valueOf(ELECTRIC_UTILITY_ID), "Electric");
    UTILITY_ID_MAPPINGS.put(Integer.valueOf(GAS_UTILITY_ID), "Gas");
    UTILITY_ID_MAPPINGS.put(Integer.valueOf(WATER_UTILITY_ID), "Water");
    
    COMPUTATION_INTERVAL_ID_MAPPINGS.put(Integer.valueOf(DAILY_COMPUTATION_INTERVAL_ID), "Daily");
    COMPUTATION_INTERVAL_ID_MAPPINGS.put(Integer.valueOf(MONTHLY_COMPUTATION_INTERVAL_ID), "Monthly");
    COMPUTATION_INTERVAL_ID_MAPPINGS.put(Integer.valueOf(MONTHLY_CALCULATED_DAILY_COMPUTATION_INTERVAL_ID), "Monthly (calculated daily)");
    COMPUTATION_INTERVAL_ID_MAPPINGS.put(Integer.valueOf(HISTORICAL_COMPUTATION_INTERVAL_ID), "Historical");
  }
  
  public static String getUtilityNameForId(int id) {
    return UTILITY_ID_MAPPINGS.get(Integer.valueOf(id));
  }

  public static String getComputationIntervalNameForId(int id) {
    return COMPUTATION_INTERVAL_ID_MAPPINGS.get(Integer.valueOf(id));
  }

  private final Integer utilityId;
  private final Integer computationIntervalId;
  private final String formula;
  private final String baselineDescription;
  private final Double utilityRate;
  private final String userNotes;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (BuildingUtilityData buildingUtilityData) {
    return new Builder(buildingUtilityData);
  }

  private BuildingUtilityData (Builder builder) {
    this.utilityId = builder.utilityId;
    this.computationIntervalId = builder.computationIntervalId;
    this.formula = builder.formula;
    this.baselineDescription = builder.baselineDescription;
    this.utilityRate = builder.utilityRate;
    this.userNotes = builder.userNotes;
  }

  public Integer getUtilityId() {
    return utilityId;
  }

  public Integer getComputationIntervalId() {
    return computationIntervalId;
  }

  public String getFormula() {
    return formula;
  }

  public String getBaselineDescription() {
    return baselineDescription;
  }

  public Double getUtilityRate() {
    return utilityRate;
  }

  public String getUserNotes() {
    return userNotes;
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Integer utilityId;
    private Integer computationIntervalId;
    private String formula;
    private String baselineDescription;
    private Double utilityRate = Double.valueOf(0.0);
    private String userNotes;

    private Builder() {}

    private Builder(BuildingUtilityData buildingUtilityData) {
      requireNonNull(buildingUtilityData, "buildingUtility cannot be null");
      this.utilityId = buildingUtilityData.utilityId;
      this.computationIntervalId = buildingUtilityData.computationIntervalId;
      this.formula = buildingUtilityData.formula;
      this.baselineDescription = buildingUtilityData.baselineDescription;
      this.utilityRate = buildingUtilityData.utilityRate;
      this.userNotes = buildingUtilityData.userNotes;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withUtilityId(Integer utilityId) {
      requireNonNull(utilityId, "utilityId cannot be null");
      this.utilityId = utilityId;
      return this;
    }

    public Builder withComputationIntervalId(Integer computationIntervalId) {
      requireNonNull(computationIntervalId, "computationIntervalId cannot be null");
      this.computationIntervalId = computationIntervalId;
      return this;
    }

    public Builder withFormula(String formula) {
      requireNonNull(formula, "formula cannot be null");
      this.formula = formula;
      return this;
    }

    public Builder withBaselineDescription(String baselineDescription) {
      this.baselineDescription = baselineDescription;
      return this;
    }

    public Builder withUtilityRate(Double utilityRate) {
      if (utilityRate != null) {
        this.utilityRate = utilityRate;
      }
      return this;
    }

    public Builder withUserNotes(String userNotes) {
      this.userNotes = userNotes;
      return this;
    }

    public BuildingUtilityData build() {
      requireNonNull(utilityId, "utilityId cannot be null");
      requireNonNull(computationIntervalId, "computationIntervalId cannot be null");
      requireNonNull(formula, "formula cannot be null");
      requireNonNull(utilityRate, "utilityRate cannot be null");
      return new BuildingUtilityData(this);
    }
  }
}