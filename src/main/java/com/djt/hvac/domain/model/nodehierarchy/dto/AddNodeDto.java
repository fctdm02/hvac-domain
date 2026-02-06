package com.djt.hvac.domain.model.nodehierarchy.dto;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = AddNodeDto.Builder.class)
public class AddNodeDto {
  private final Integer nodeId;
  private final Integer parentId;
  private final Integer typeId;
  private final String name;
  private final String displayName;
  private final Integer energyExchangeSystemTypeId;
  private final String metricId;
  private final Integer unitId;
  private final String result;
  private final String reason;

  @JsonCreator
  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(AddNodeDto addNodeDto) {
    return new Builder(addNodeDto);
  }

  private AddNodeDto(Builder builder) {
    this.nodeId = builder.nodeId;
    this.parentId = builder.parentId;
    this.typeId = builder.typeId;
    this.name = builder.name;
    this.displayName = builder.displayName;
    this.energyExchangeSystemTypeId = builder.energyExchangeSystemTypeId;
    this.metricId = builder.metricId;
    this.unitId = builder.unitId;
    this.result = builder.result;
    this.reason = builder.reason;
  }

  public Integer getNodeId() {
    return nodeId;
  }

  public Integer getParentId() {
    return parentId;
  }

  public Integer getTypeId() {
    return typeId;
  }

  public String getName() {
    return name;
  }

  public String getDisplayName() {
    return displayName;
  }
  
  public Integer getEnergyExchangeSystemTypeId() {
    return energyExchangeSystemTypeId;
  }
  
  public String getMetricId() {
    return metricId;
  }
  
  public Integer getUnitId() {
    return unitId;
  }

  public String getResult() {
    return result;
  }

  public String getReason() {
    return reason;
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Integer nodeId;
    private Integer parentId;
    private Integer typeId;
    private String name;
    private String displayName;
    private Integer energyExchangeSystemTypeId;
    private String metricId;
    private Integer unitId;
    private String result;
    private String reason;

    private Builder() {}

    private Builder(AddNodeDto nodeEntity) {
      requireNonNull(nodeEntity, "nodeEntity cannot be null");
      this.nodeId = nodeEntity.nodeId;
      this.parentId = nodeEntity.parentId;
      this.typeId = nodeEntity.typeId;
      this.name = nodeEntity.name;
      this.displayName = nodeEntity.displayName;
      this.energyExchangeSystemTypeId = nodeEntity.energyExchangeSystemTypeId;
      this.metricId = nodeEntity.metricId;
      this.unitId = nodeEntity.unitId;
      this.result = nodeEntity.result;
      this.reason = nodeEntity.reason;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withNodeId(Integer nodeId) {
      requireNonNull(nodeId, "nodeId cannot be null");
      this.nodeId = nodeId;
      return this;
    }

    public Builder withParentId(Integer parentId) {
      requireNonNull(parentId, "parentId cannot be null");
      this.parentId = parentId;
      return this;
    }

    public Builder withTypeId(Integer typeId) {
      requireNonNull(typeId, "typeId cannot be null");
      this.typeId = typeId;
      return this;
    }

    public Builder withName(String name) {
      requireNonNull(name, "name cannot be null");
      this.name = name;
      return this;
    }

    public Builder withDisplayName(String displayName) {
      requireNonNull(displayName, "displayName cannot be null");
      this.displayName = displayName;
      return this;
    }
    
    public Builder withEnergyExchangeSystemTypeId(Integer energyExchangeSystemTypeId) {
      this.energyExchangeSystemTypeId = energyExchangeSystemTypeId;
      return this;
    }

    public Builder withMetricId(String metricId) {
      this.metricId = metricId;
      return this;
    }
    
    public Builder withUnitId(Integer unitId) {
      this.unitId = unitId;
      return this;
    }
    
    public Builder withResult(String result) {
      this.result = result;
      return this;
    }

    public Builder withReason(String reason) {
      this.reason = reason;
      return this;
    }

    public AddNodeDto build() {
      requireNonNull(parentId, "parentId cannot be null");
      requireNonNull(typeId, "typeId cannot be null");
      return new AddNodeDto(this);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((nodeId == null) ? 0 : nodeId.hashCode());
    result = prime * result + ((parentId == null) ? 0 : parentId.hashCode());
    result = prime * result + ((reason == null) ? 0 : reason.hashCode());
    result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
    result = prime * result + ((typeId == null) ? 0 : typeId.hashCode());
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
    AddNodeDto other = (AddNodeDto) obj;
    if (displayName == null) {
      if (other.displayName != null)
        return false;
    } else if (!displayName.equals(other.displayName))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (nodeId == null) {
      if (other.nodeId != null)
        return false;
    } else if (!nodeId.equals(other.nodeId))
      return false;
    if (parentId == null) {
      if (other.parentId != null)
        return false;
    } else if (!parentId.equals(other.parentId))
      return false;
    if (reason == null) {
      if (other.reason != null)
        return false;
    } else if (!reason.equals(other.reason))
      return false;
    if (result == null) {
      if (other.result != null)
        return false;
    } else if (!result.equals(other.result))
      return false;
    if (typeId == null) {
      if (other.typeId != null)
        return false;
    } else if (!typeId.equals(other.typeId))
      return false;
    return true;
  }

  @Override
  public String toString() {

    if (result != null) {
      if (result.equals("Success")) {
        return "nodeId="
            + nodeId
            + ", parentId="
            + parentId
            + ", typeId="
            + typeId + ", name="
            + name + ", displayName="
            + displayName + ", result="
            + result;
      } else {
        return "parentId="
            + parentId
            + ", typeId="
            + typeId + ", name="
            + name + ", displayName="
            + displayName + ", result="
            + result + ", reason="
            + reason;
      }
    }
    return "parentId="
        + parentId
        + ", typeId="
        + typeId + ", name="
        + name
        + ", displayName="
        + displayName;
  }
}
