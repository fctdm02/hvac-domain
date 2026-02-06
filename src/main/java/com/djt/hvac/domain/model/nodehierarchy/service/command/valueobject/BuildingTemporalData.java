package com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.djt.hvac.domain.model.nodehierarchy.building.enums.OperationType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = BuildingTemporalData.Builder.class)
public class BuildingTemporalData implements Serializable {
  
  private static final long serialVersionUID = 1L;
  
  private final OperationType operationType;
  private final Integer temporalId;
  private final String effectiveDate;
  private final Integer squareFeet;
  private final List<BuildingUtilityData> utilities;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (BuildingTemporalData buildingTemporalData) {
    return new Builder(buildingTemporalData);
  }

  private BuildingTemporalData (Builder builder) {
    this.operationType = builder.operationType;
    this.temporalId = builder.temporalId;
    this.effectiveDate = builder.effectiveDate;
    this.squareFeet = builder.squareFeet;
    this.utilities = builder.utilities;
  }
  
  public OperationType getOperationType() {
    return operationType;
  }

  public Integer getTemporalId() {
    return temporalId;
  }

  public String getEffectiveDate() {
    return effectiveDate;
  }

  public Integer getSquareFeet() {
    return squareFeet;
  }

  public List<BuildingUtilityData> getUtilities() {
    return utilities;
  }
  
  @JsonIgnore
  public boolean addUtility(BuildingUtilityData utility) {
    return this.utilities.add(utility);
  }

  @JsonPOJOBuilder
  public static class Builder {
    private OperationType operationType = OperationType.ADD;
    private Integer temporalId;
    private String effectiveDate;
    private Integer squareFeet;
    private List<BuildingUtilityData> utilities = new ArrayList<>();

    private Builder() {}

    private Builder(BuildingTemporalData buildingTemporalData) {
      requireNonNull(buildingTemporalData, "buildingTemporalData cannot be null");
      this.operationType = buildingTemporalData.operationType;
      this.temporalId = buildingTemporalData.temporalId;
      this.effectiveDate = buildingTemporalData.effectiveDate;
      this.squareFeet = buildingTemporalData.squareFeet;
      this.utilities = buildingTemporalData.utilities;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }
    
    public Builder withOperationType(OperationType operationType) {
      requireNonNull(operationType, "operationType cannot be null");
      this.operationType = operationType;
      return this;
    }

    public Builder withTemporalId(Integer temporalId) {
      this.temporalId = temporalId;
      return this;
    }

    public Builder withEffectiveDate(String effectiveDate) {
      this.effectiveDate = effectiveDate;
      return this;
    }

    public Builder withSquareFeet(Integer squareFeet) {
      this.squareFeet = squareFeet;
      return this;
    }

    public Builder withUtilities(List<BuildingUtilityData> utilities) {
      if (utilities != null) {
        this.utilities = utilities;
      }
      return this;
    }

    public BuildingTemporalData build() {
      return new BuildingTemporalData(this);
    }
  }
}