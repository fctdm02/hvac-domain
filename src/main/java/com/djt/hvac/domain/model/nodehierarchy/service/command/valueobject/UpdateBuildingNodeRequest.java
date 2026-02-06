package com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableList;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = UpdateBuildingNodeRequest.Builder.class)
public class UpdateBuildingNodeRequest implements Serializable {
  
  private static final long serialVersionUID = 1L;
  
  public static final String ONLINE_BUILDING_PAYMENT_METHOD = "ONLINE";
  public static final String OUT_OF_BAND_BUILDING_PAYMENT_METHOD = "OUT_OF_BAND";
  
  private final Integer id;
  private final String displayName;
  private final String buildingPaymentType; // Only applies to online distributors whose "allowOutOfBandBuildings" is true. Can only be changed during trial period.
  private final BuildingAddressData addressData;
  private final List<BuildingTemporalData> temporalData;
  private final String unitSystem;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (UpdateBuildingNodeRequest updateBuildingNodeRequest) {
    return new Builder(updateBuildingNodeRequest);
  }

  private UpdateBuildingNodeRequest (Builder builder) {
    this.id = builder.id;
    this.displayName = builder.displayName;
    this.buildingPaymentType = builder.buildingPaymentType;
    this.addressData = builder.addressData;
    this.temporalData = builder.temporalData;
    this.unitSystem = builder.unitSystem;
  }
  
  public Integer getId() {
    return id;
  }

  public String getDisplayName() {
    return displayName;
  }
  
  public String getBuildingPaymentType() {
    return buildingPaymentType;
  }

  public BuildingAddressData getAddressData() {
    return addressData;
  }

  public List<BuildingTemporalData> getTemporalData() {
    return temporalData;
  }
  
  public String getUnitSystem() {
    return unitSystem;
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Integer id;
    private String displayName;
    private String buildingPaymentType;
    private BuildingAddressData addressData;
    private List<BuildingTemporalData> temporalData;
    private String unitSystem;

    private Builder() {}

    private Builder(UpdateBuildingNodeRequest updateBuildingNodeRequest) {
      requireNonNull(updateBuildingNodeRequest, "updateBuildingNodeRequest cannot be null");
      this.id = updateBuildingNodeRequest.id;
      this.displayName = updateBuildingNodeRequest.displayName;
      this.buildingPaymentType = updateBuildingNodeRequest.buildingPaymentType;
      this.addressData = updateBuildingNodeRequest.addressData;
      this.temporalData = updateBuildingNodeRequest.temporalData;
      this.unitSystem = updateBuildingNodeRequest.unitSystem;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withId(Integer id) {
      requireNonNull(id, "id cannot be null");
      this.id = id;
      return this;
    }
    
    public Builder withDisplayName(String displayName) {
      requireNonNull(displayName, "displayName cannot be null");
      this.displayName = displayName;
      return this;
    }

    public Builder withBuildingPaymentType(String buildingPaymentType) {
      this.buildingPaymentType = buildingPaymentType;
      return this;
    }

    public Builder withAddressData(BuildingAddressData addressData) {
      this.addressData = addressData;
      return this;
    }

    public Builder withTemporalData(List<BuildingTemporalData> temporalData) {
      requireNonNull(temporalData, "temporalData cannot be null");
      this.temporalData = ImmutableList.copyOf(temporalData);
      return this;
    }
    
    public Builder withUnitSystem(String unitSystem) {
      requireNonNull(unitSystem, "unitSystem cannot be null");
      this.unitSystem = unitSystem;
      return this;
    }

    public UpdateBuildingNodeRequest build() {
      return new UpdateBuildingNodeRequest(this);
    }
  }
}