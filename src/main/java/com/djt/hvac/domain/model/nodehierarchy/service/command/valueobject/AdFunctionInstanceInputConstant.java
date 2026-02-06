package com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = AdFunctionInstanceInputConstant.Builder.class)
public class AdFunctionInstanceInputConstant {
  
  private final Integer adFunctionTemplateInputConstantId;
  private final String adFunctionTemplateInputConstantDescription;
  private final Integer adFunctionTemplateInputConstantDataType;
  private final String value;
  private final String defaultValue;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (AdFunctionInstanceInputConstant adFunctionInstanceInputConstant) {
    return new Builder(adFunctionInstanceInputConstant);
  }

  private AdFunctionInstanceInputConstant (Builder builder) {
    this.adFunctionTemplateInputConstantId = builder.adFunctionTemplateInputConstantId;
    this.adFunctionTemplateInputConstantDescription = builder.adFunctionTemplateInputConstantDescription;
    this.adFunctionTemplateInputConstantDataType = builder.adFunctionTemplateInputConstantDataType;
    this.value = builder.value;
    this.defaultValue = builder.defaultValue;
  }

  public Integer getAdFunctionTemplateInputConstantId() {
    return adFunctionTemplateInputConstantId;
  }

  public String getAdFunctionTemplateInputConstantDescription() {
    return adFunctionTemplateInputConstantDescription;
  }

  public Integer getAdFunctionTemplateInputConstantDataType() {
    return adFunctionTemplateInputConstantDataType;
  }
  
  public String getValue() {
    return value;
  }
  
  public String getDefaultValue() {
    return defaultValue;
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Integer adFunctionTemplateInputConstantId;
    private String adFunctionTemplateInputConstantDescription;
    private Integer adFunctionTemplateInputConstantDataType;
    private String value;
    private String defaultValue;

    private Builder() {}

    private Builder(AdFunctionInstanceInputConstant adFunctionInstanceInputConstant) {
      requireNonNull(adFunctionInstanceInputConstant, "adFunctionInstanceInputConstant cannot be null");
      this.adFunctionTemplateInputConstantId = adFunctionInstanceInputConstant.adFunctionTemplateInputConstantId;
      this.adFunctionTemplateInputConstantDescription = adFunctionInstanceInputConstant.adFunctionTemplateInputConstantDescription;
      this.adFunctionTemplateInputConstantDataType = adFunctionInstanceInputConstant.adFunctionTemplateInputConstantDataType;
      this.value = adFunctionInstanceInputConstant.value;
      this.defaultValue = adFunctionInstanceInputConstant.value;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withAdFunctionTemplateInputConstantId(Integer adFunctionTemplateInputConstantId) {
      requireNonNull(adFunctionTemplateInputConstantId, "adFunctionTemplateInputConstantId cannot be null");
      this.adFunctionTemplateInputConstantId = adFunctionTemplateInputConstantId;
      return this;
    }

    public Builder withAdFunctionTemplateInputConstantDescription(String adFunctionTemplateInputConstantDescription) {
      requireNonNull(adFunctionTemplateInputConstantDescription, "adFunctionTemplateInputConstantDescription cannot be null");
      this.adFunctionTemplateInputConstantDescription = adFunctionTemplateInputConstantDescription;
      return this;
    }

    public Builder withAdFunctionTemplateInputConstantDataType(Integer adFunctionTemplateInputConstantDataType) {
      requireNonNull(adFunctionTemplateInputConstantDataType, "adFunctionTemplateInputConstantDataType cannot be null");
      this.adFunctionTemplateInputConstantDataType = adFunctionTemplateInputConstantDataType;
      return this;
    }
    
    public Builder withValue(String value) {
      requireNonNull(value, "value cannot be null");
      this.value = value;
      return this;
    }

    public Builder withDefaultValue(String defaultValue) {
      if (defaultValue == null) {
        defaultValue = ""; // This is because the tests have garbage data in app-service-common.
      }
      requireNonNull(defaultValue, "defaultValue cannot be null");
      this.defaultValue = defaultValue;
      return this;
    }
    
    public AdFunctionInstanceInputConstant build() {
      requireNonNull(adFunctionTemplateInputConstantId, "adFunctionTemplateInputConstantId cannot be null");
      requireNonNull(value, "value cannot be null");
      return new AdFunctionInstanceInputConstant(this);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((adFunctionTemplateInputConstantId == null) ? 0 : adFunctionTemplateInputConstantId.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
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
    AdFunctionInstanceInputConstant other = (AdFunctionInstanceInputConstant) obj;
    if (adFunctionTemplateInputConstantId == null) {
      if (other.adFunctionTemplateInputConstantId != null)
        return false;
    } else if (!adFunctionTemplateInputConstantId.equals(other.adFunctionTemplateInputConstantId))
      return false;
    if (value == null) {
      if (other.value != null)
        return false;
    } else if (!value.equals(other.value))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "AdFunctionInstanceInputConstant [adFunctionTemplateInputConstantId=" + adFunctionTemplateInputConstantId
        + ", value=" + value + "]";
  }
}