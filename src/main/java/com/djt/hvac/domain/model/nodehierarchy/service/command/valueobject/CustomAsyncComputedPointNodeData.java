//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = CustomAsyncComputedPointNodeData.Builder.class)
@JsonPropertyOrder({
  "id",
  "nodePath",
  "displayName",
  "pointTemplateId",  
  "unitId",
  "additionalProperties"
})
public class CustomAsyncComputedPointNodeData {
  
  private final Integer id;
  private final String nodePath;
  private final String displayName;
  private final Integer pointTemplateId;
  private final Integer unitId;
  private final Map<String, Object> additionalProperties;

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(CustomAsyncComputedPointNodeData data) {
    return new Builder(data);
  }

  private CustomAsyncComputedPointNodeData(Builder builder) {
    this.id = builder.id;
    this.nodePath = builder.nodePath;
    this.displayName = builder.displayName;
    this.pointTemplateId = builder.pointTemplateId;
    this.unitId = builder.unitId;
    this.additionalProperties = builder.additionalProperties;
  }
  
  public Integer getId() {
    return id;
  }
  
  public String getNodePath() {
    return nodePath;
  }
  
  public String getDisplayName() {
    return displayName;
  }
  
  public Integer getPointTemplateId() {
    return pointTemplateId;
  }

  public Integer getUnitId() {
    return unitId;
  }
  
  public Map<String, Object> getAdditionalProperties() {
    return additionalProperties;
  }
  
  @JsonIgnore
  private void checkRequiredField(Object object, String validationMessage) {
    if (object == null) {
      throw new IllegalArgumentException(validationMessage);
    }
  }
  
  @JsonIgnore
  public void validate(boolean useGrouping) {
    
    checkRequiredField(id, "id cannot be null.");
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result =
        prime * result + ((additionalProperties == null) ? 0 : additionalProperties.hashCode());
    result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((nodePath == null) ? 0 : nodePath.hashCode());
    result = prime * result + ((pointTemplateId == null) ? 0 : pointTemplateId.hashCode());
    result = prime * result + ((unitId == null) ? 0 : unitId.hashCode());
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
    CustomAsyncComputedPointNodeData other = (CustomAsyncComputedPointNodeData) obj;
    if (additionalProperties == null) {
      if (other.additionalProperties != null)
        return false;
    } else if (!additionalProperties.equals(other.additionalProperties))
      return false;
    if (displayName == null) {
      if (other.displayName != null)
        return false;
    } else if (!displayName.equals(other.displayName))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (nodePath == null) {
      if (other.nodePath != null)
        return false;
    } else if (!nodePath.equals(other.nodePath))
      return false;
    if (pointTemplateId == null) {
      if (other.pointTemplateId != null)
        return false;
    } else if (!pointTemplateId.equals(other.pointTemplateId))
      return false;
    if (unitId == null) {
      if (other.unitId != null)
        return false;
    } else if (!unitId.equals(other.unitId))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder2 = new StringBuilder();
    builder2.append("CustomAsyncComputedPointNodeData [id=").append(id).append(", nodePath=")
        .append(nodePath).append(", displayName=").append(displayName).append(", pointTemplateId=")
        .append(pointTemplateId).append(", unitId=").append(unitId)
        .append(", additionalProperties=").append(additionalProperties).append("]");
    return builder2.toString();
  }

  public static class Builder {
    private Integer id;
    private String nodePath;
    private String displayName;
    private Integer pointTemplateId;
    private Integer unitId;
    private Map<String, Object> additionalProperties;

    private Builder() {}

    private Builder(CustomAsyncComputedPointNodeData updateMappablePointNodeRequestDto) {
      requireNonNull(updateMappablePointNodeRequestDto, "response cannot be null");
      this.id = updateMappablePointNodeRequestDto.id;
      this.nodePath = updateMappablePointNodeRequestDto.nodePath;
      this.displayName = updateMappablePointNodeRequestDto.displayName;
      this.pointTemplateId = updateMappablePointNodeRequestDto.pointTemplateId;
      this.unitId = updateMappablePointNodeRequestDto.unitId;
      this.additionalProperties = updateMappablePointNodeRequestDto.additionalProperties;
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
    
    public Builder withNodePath(String nodePath) {
      this.nodePath = nodePath;
      return this;
    }
    
    public Builder withDisplayName(String displayName) {
      requireNonNull(displayName, "displayName cannot be null");
      this.displayName = displayName;
      return this;
    }
    
    public Builder withPointTemplateId(Integer pointTemplateId) {
      requireNonNull(pointTemplateId, "pointTemplateId cannot be null");
      this.pointTemplateId = pointTemplateId;
      return this;
    }

    public Builder withUnitId(Integer unitId) {
      requireNonNull(unitId, "unitId cannot be null");
      this.unitId = unitId;
      return this;
    }
    
    public Builder withAdditionalProperties(Map<String, Object> additionalProperties) {
      this.additionalProperties = additionalProperties;
      return this;
    }
    
    public CustomAsyncComputedPointNodeData build() {
      requireNonNull(id, "id cannot be null");
      return new CustomAsyncComputedPointNodeData(this);
    }
  }
}
//@formatter:on