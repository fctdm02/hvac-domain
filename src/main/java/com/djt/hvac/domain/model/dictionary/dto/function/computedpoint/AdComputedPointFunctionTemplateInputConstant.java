package com.djt.hvac.domain.model.dictionary.dto.function.computedpoint;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = AdComputedPointFunctionTemplateInputConstant.Builder.class)
public class AdComputedPointFunctionTemplateInputConstant implements Comparable<AdComputedPointFunctionTemplateInputConstant> {
  
  private final Integer id;
  private final Integer dataTypeId;
  private final Integer unitId;
  private final Integer sequenceNumber;
  private final String description;
  private final String defaultValue;
  private final String name;
  private final Boolean isRequired;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (AdComputedPointFunctionTemplateInputConstant ruleTemplateInputConstant) {
    return new Builder(ruleTemplateInputConstant);
  }

  private AdComputedPointFunctionTemplateInputConstant (Builder builder) {
    this.id = builder.id;
    this.dataTypeId = builder.dataTypeId;
    this.unitId = builder.unitId;
    this.sequenceNumber = builder.sequenceNumber;
    this.description = builder.description;
    this.defaultValue = builder.defaultValue;
    this.name = builder.name;
    this.isRequired = builder.isRequired;
  }

  public Integer getId() {
    return id;
  }

  public Integer getDataTypeId() {
    return dataTypeId;
  }

  public Integer getUnitId() {
    return unitId;
  }

  public Integer getSequenceNumber() {
    return sequenceNumber;
  }

  public String getDescription() {
    return description;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public String getName() {
    return name;
  }

  public Boolean getIsRequired() {
    return isRequired;
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Integer id;
    private Integer dataTypeId;
    private Integer unitId;
    private Integer sequenceNumber;
    private String description;
    private String defaultValue;
    private String name;
    private Boolean isRequired;

    private Builder() {}

    private Builder(AdComputedPointFunctionTemplateInputConstant ruleTemplateInputConstant) {
      requireNonNull(ruleTemplateInputConstant, "ruleTemplateInputConstant cannot be null");
      this.id = ruleTemplateInputConstant.id;
      this.dataTypeId = ruleTemplateInputConstant.dataTypeId;
      this.unitId = ruleTemplateInputConstant.unitId;
      this.sequenceNumber = ruleTemplateInputConstant.sequenceNumber;
      this.description = ruleTemplateInputConstant.description;
      this.defaultValue = ruleTemplateInputConstant.defaultValue;
      this.name = ruleTemplateInputConstant.name;
      this.isRequired = ruleTemplateInputConstant.isRequired;
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

    public Builder withDataTypeId(Integer dataTypeId) {
      requireNonNull(dataTypeId, "dataTypeId cannot be null");
      this.dataTypeId = dataTypeId;
      return this;
    }

    public Builder withUnitId(Integer unitId) {
      requireNonNull(unitId, "unitId cannot be null");
      this.unitId = unitId;
      return this;
    }

    public Builder withSequenceNumber(Integer sequenceNumber) {
      requireNonNull(sequenceNumber, "sequenceNumber cannot be null");
      this.sequenceNumber = sequenceNumber;
      return this;
    }

    public Builder withDescription(String description) {
      requireNonNull(description, "description cannot be null");
      this.description = description;
      return this;
    }

    public Builder withDefaultValue(String defaultValue) {
      requireNonNull(defaultValue, "defaultValue cannot be null");
      this.defaultValue = defaultValue;
      return this;
    }

    public Builder withName(String name) {
      requireNonNull(name, "name cannot be null");
      this.name = name;
      return this;
    }

    public Builder withIsRequired(Boolean isRequired) {
      requireNonNull(isRequired, "isRequired cannot be null");
      this.isRequired = isRequired;
      return this;
    }

    public AdComputedPointFunctionTemplateInputConstant build() {
      requireNonNull(id, "id cannot be null");
      requireNonNull(dataTypeId, "dataTypeId cannot be null");
      requireNonNull(unitId, "unitId cannot be null");
      requireNonNull(sequenceNumber, "sequenceNumber cannot be null");
      requireNonNull(description, "description cannot be null");
      requireNonNull(defaultValue, "defaultValue cannot be null");
      requireNonNull(name, "name cannot be null");
      requireNonNull(isRequired, "isRequired cannot be null");
      return new AdComputedPointFunctionTemplateInputConstant(this);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((dataTypeId == null) ? 0 : dataTypeId.hashCode());
    result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((isRequired == null) ? 0 : isRequired.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((sequenceNumber == null) ? 0 : sequenceNumber.hashCode());
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
    AdComputedPointFunctionTemplateInputConstant other = (AdComputedPointFunctionTemplateInputConstant) obj;
    if (dataTypeId == null) {
      if (other.dataTypeId != null)
        return false;
    } else if (!dataTypeId.equals(other.dataTypeId))
      return false;
    if (defaultValue == null) {
      if (other.defaultValue != null)
        return false;
    } else if (!defaultValue.equals(other.defaultValue))
      return false;
    if (description == null) {
      if (other.description != null)
        return false;
    } else if (!description.equals(other.description))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (isRequired == null) {
      if (other.isRequired != null)
        return false;
    } else if (!isRequired.equals(other.isRequired))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (sequenceNumber == null) {
      if (other.sequenceNumber != null)
        return false;
    } else if (!sequenceNumber.equals(other.sequenceNumber))
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
    return "RuleTemplateInputConstantDto [id=" + id + ", dataTypeId=" + dataTypeId + ", unitId="
        + unitId + ", sequenceNumber=" + sequenceNumber + ", description=" + description
        + ", defaultValue=" + defaultValue + ", name=" + name + ", isRequired=" + isRequired + "]";
  }
  
  public int compareTo(AdComputedPointFunctionTemplateInputConstant that) {

    return this.sequenceNumber.compareTo(((AdComputedPointFunctionTemplateInputConstant) that).sequenceNumber);
  }   
}