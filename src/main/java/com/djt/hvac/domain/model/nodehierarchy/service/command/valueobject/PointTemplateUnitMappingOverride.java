package com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = PointTemplateUnitMappingOverride.Builder.class)
public class PointTemplateUnitMappingOverride {
  private final Integer id;
  private final Integer distributorId;
  private final Integer customerId;
  private final Integer buildingId;
  private final Integer pointTemplateId;
  private final Boolean keepIpUnitSystem;
  private final Integer unitMappingId;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (PointTemplateUnitMappingOverride pointTemplateUnitMappingOverride) {
    return new Builder(pointTemplateUnitMappingOverride);
  }

  private PointTemplateUnitMappingOverride (Builder builder) {
    this.id = builder.id;
    this.distributorId = builder.distributorId;
    this.customerId = builder.customerId;
    this.buildingId = builder.buildingId;
    this.pointTemplateId = builder.pointTemplateId;
    this.keepIpUnitSystem = builder.keepIpUnitSystem;
    this.unitMappingId = builder.unitMappingId;
  }
  
  public Integer getId() {
    return id;
  }

  public Integer getDistributorId() {
    return distributorId;
  }

  public Integer getCustomerId() {
    return customerId;
  }

  public Integer getBuildingId() {
    return buildingId;
  }

  public Integer getPointTemplateId() {
    return pointTemplateId;
  }

  public Boolean getKeepIpUnitSystem() {
    return keepIpUnitSystem;
  }

  public Integer getUnitMappingId() {
    return unitMappingId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((buildingId == null) ? 0 : buildingId.hashCode());
    result = prime * result + ((customerId == null) ? 0 : customerId.hashCode());
    result = prime * result + ((distributorId == null) ? 0 : distributorId.hashCode());
    result = prime * result + ((keepIpUnitSystem == null) ? 0 : keepIpUnitSystem.hashCode());
    result = prime * result + ((pointTemplateId == null) ? 0 : pointTemplateId.hashCode());
    result = prime * result + ((unitMappingId == null) ? 0 : unitMappingId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PointTemplateUnitMappingOverride other = (PointTemplateUnitMappingOverride) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (buildingId == null) {
      if (other.buildingId != null)
        return false;
    } else if (!buildingId.equals(other.buildingId))
      return false;
    if (customerId == null) {
      if (other.customerId != null)
        return false;
    } else if (!customerId.equals(other.customerId))
      return false;
    if (distributorId == null) {
      if (other.distributorId != null)
        return false;
    } else if (!distributorId.equals(other.distributorId))
      return false;
    if (keepIpUnitSystem == null) {
      if (other.keepIpUnitSystem != null)
        return false;
    } else if (!keepIpUnitSystem.equals(other.keepIpUnitSystem))
      return false;
    if (pointTemplateId == null) {
      if (other.pointTemplateId != null)
        return false;
    } else if (!pointTemplateId.equals(other.pointTemplateId))
      return false;
    if (unitMappingId == null) {
      if (other.unitMappingId != null)
        return false;
    } else if (!unitMappingId.equals(other.unitMappingId))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder2 = new StringBuilder();
    builder2.append("PointTemplateUnitMappingOverride [id=").append(id)
        .append(", distributorId=").append(distributorId)
        .append(", customerId=").append(customerId).append(", buildingId=").append(buildingId)
        .append(", pointTemplateId=").append(pointTemplateId).append(", keepIpUnitSystem=")
        .append(keepIpUnitSystem).append(", unitMappingId=").append(unitMappingId).append("]");
    return builder2.toString();
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Integer id;
    private Integer distributorId;
    private Integer customerId;
    private Integer buildingId;
    private Integer pointTemplateId;
    private Boolean keepIpUnitSystem = Boolean.FALSE;
    private Integer unitMappingId;

    private Builder() {}

    private Builder(PointTemplateUnitMappingOverride pointTemplateUnitMappingOverride) {
      requireNonNull(pointTemplateUnitMappingOverride, "pointTemplateUnitMappingOverride cannot be null");
      this.id = pointTemplateUnitMappingOverride.id;
      this.distributorId = pointTemplateUnitMappingOverride.distributorId;
      this.customerId = pointTemplateUnitMappingOverride.customerId;
      this.buildingId = pointTemplateUnitMappingOverride.buildingId;
      this.pointTemplateId = pointTemplateUnitMappingOverride.pointTemplateId;
      this.keepIpUnitSystem = pointTemplateUnitMappingOverride.keepIpUnitSystem;
      this.unitMappingId = pointTemplateUnitMappingOverride.unitMappingId;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withId(Integer id) {
      this.id = id;
      return this;
    }
    
    public Builder withDistributorId(Integer distributorId) {
      this.distributorId = distributorId;
      return this;
    }

    public Builder withCustomerId(Integer customerId) {
      this.customerId = customerId;
      return this;
    }

    public Builder withBuildingId(Integer buildingId) {
      this.buildingId = buildingId;
      return this;
    }

    public Builder withPointTemplateId(Integer pointTemplateId) {
      this.pointTemplateId = pointTemplateId;
      return this;
    }

    public Builder withKeepIpUnitSystem(Boolean keepIpUnitSystem) {
      this.keepIpUnitSystem = keepIpUnitSystem;
      return this;
    }

    public Builder withUnitMappingId(Integer unitMappingId) {
      this.unitMappingId = unitMappingId;
      return this;
    }

    public PointTemplateUnitMappingOverride build() {
      return new PointTemplateUnitMappingOverride(this);
    }
  }
}