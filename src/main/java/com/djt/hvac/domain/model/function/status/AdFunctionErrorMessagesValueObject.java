package com.djt.hvac.domain.model.function.status;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = AdFunctionErrorMessagesValueObject.Builder.class)
public class AdFunctionErrorMessagesValueObject implements Comparable<AdFunctionErrorMessagesValueObject> {
  
  private final String nodePath;
  private final Integer adFunctionTemplateId;
  private final String adFunctionTemplateName;
  private final String adFunctionTemplateDescription;
  private final String energyExchangeTypeName;
  private final Integer energyExchangeId;
  private final String energyExchangeName;
  private final List<String> errorMessages;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (AdFunctionErrorMessagesValueObject adFunctionEquipmentErrorMessagesDto) {
    return new Builder(adFunctionEquipmentErrorMessagesDto);
  }

  private AdFunctionErrorMessagesValueObject (Builder builder) {
    this.nodePath = builder.nodePath;
    this.adFunctionTemplateId = builder.adFunctionTemplateId;
    this.adFunctionTemplateName = builder.adFunctionTemplateName;
    this.adFunctionTemplateDescription = builder.adFunctionTemplateDescription;
    this.energyExchangeTypeName = builder.energyExchangeTypeName;
    this.energyExchangeId = builder.energyExchangeId;
    this.energyExchangeName = builder.energyExchangeName;
    this.errorMessages = builder.errorMessages;
  }
  
  public String getNodePath() {
    return nodePath;
  }

  public Integer getAdFunctionTemplateId() {
    return adFunctionTemplateId;
  }
  
  public String getAdFunctionTemplateName() {
    return adFunctionTemplateName;
  }
  
  public String getAdFunctionTemplateDescription() {
    return adFunctionTemplateDescription;
  }
  
  public String getEnergyExchangeTypeName() {
    return energyExchangeTypeName;
  }

  public Integer getEnergyExchangeId() {
    return energyExchangeId;
  }
  
  public String getEnergyExchangeName() {
    return energyExchangeName;
  }

  public List<String> getErrorMessages() {
    return errorMessages;
  }

  @Override
  public int hashCode() {
    
    final int prime = 31;
    int result = 1;
    result = prime * result + ((energyExchangeId == null) ? 0 : energyExchangeId.hashCode());
    result = prime * result + ((adFunctionTemplateId == null) ? 0 : adFunctionTemplateId.hashCode());
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
    AdFunctionErrorMessagesValueObject other = (AdFunctionErrorMessagesValueObject) obj;
    if (energyExchangeId == null) {
      if (other.energyExchangeId != null)
        return false;
    } else if (!energyExchangeId.equals(other.energyExchangeId))
      return false;
    if (adFunctionTemplateId == null) {
      if (other.adFunctionTemplateId != null)
        return false;
    } else if (!adFunctionTemplateId.equals(other.adFunctionTemplateId))
      return false;
    return true;
  }

  @Override
  public int compareTo(AdFunctionErrorMessagesValueObject that) {

    int compareTo = this.nodePath.compareTo(that.nodePath);
    if (compareTo == 0) {
      compareTo = this.adFunctionTemplateName.compareTo(that.adFunctionTemplateName);
    }
    return compareTo;
  }
  
  @Override
  public String toString() {
    
    return new StringBuilder()
        .append("AdFunctionErrorMessagesValueObject [adFunctionTemplateId=")
        .append(adFunctionTemplateId)
        .append(", adFunctionTemplateName=")
        .append(adFunctionTemplateName)
        .append(", adFunctionTemplateDescription=")
        .append(adFunctionTemplateDescription)
        .append(", energyExchangeTypeName=")
        .append(energyExchangeTypeName)
        .append(", energyExchangeId=")
        .append(energyExchangeId)
        .append(", energyExchangeName=")
        .append(energyExchangeName)
        .append(", nodePath=")
        .append(nodePath)
        .append(", errorMessages=")
        .append(errorMessages)
        .append("]")
        .toString();
  }
  
  @JsonPOJOBuilder
  public static class Builder {
    private String nodePath;
    private Integer adFunctionTemplateId;
    private String adFunctionTemplateName;
    private String adFunctionTemplateDescription;
    private String energyExchangeTypeName;
    private Integer energyExchangeId;
    private String energyExchangeName;
    private List<String> errorMessages;

    private Builder() {}

    private Builder(AdFunctionErrorMessagesValueObject vo) {
      requireNonNull(vo, "vo cannot be null");
      this.nodePath = vo.getNodePath();
      this.adFunctionTemplateId = vo.adFunctionTemplateId;
      this.adFunctionTemplateName = vo.adFunctionTemplateName;
      this.adFunctionTemplateDescription = vo.adFunctionTemplateDescription;
      this.energyExchangeTypeName = vo.energyExchangeTypeName;
      this.energyExchangeId = vo.energyExchangeId;
      this.energyExchangeName = vo.energyExchangeName;
      this.errorMessages = vo.errorMessages;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withNodePath(String nodePath) {
      this.nodePath = nodePath;
      return this;
    }
    
    public Builder withAdFunctionTemplateId(Integer adFunctionTemplateId) {
      requireNonNull(adFunctionTemplateId, "adFunctionTemplateId cannot be null");
      this.adFunctionTemplateId = adFunctionTemplateId;
      return this;
    }

    public Builder withAdFunctionTemplateName(String adFunctionTemplateName) {
      requireNonNull(adFunctionTemplateName, "adFunctionTemplateName cannot be null");
      this.adFunctionTemplateName = adFunctionTemplateName;
      return this;
    }
    
    public Builder withAdFunctionTemplateDescription(String adFunctionTemplateDescription) {
      requireNonNull(adFunctionTemplateDescription, "adFunctionTemplateDescription cannot be null");
      this.adFunctionTemplateDescription = adFunctionTemplateDescription;
      return this;
    }

    public Builder withEnergyExchangeTypeName(String energyExchangeTypeName) {
      requireNonNull(energyExchangeTypeName, "energyExchangeTypeName cannot be null");
      this.energyExchangeTypeName = energyExchangeTypeName;
      return this;
    }
    
    public Builder withEnergyExchangeId(Integer energyExchangeId) {
      requireNonNull(energyExchangeId, "energyExchangeId cannot be null");
      this.energyExchangeId = energyExchangeId;
      return this;
    }

    public Builder withEnergyExchangeName(String energyExchangeName) {
      requireNonNull(energyExchangeName, "energyExchangeName cannot be null");
      this.energyExchangeName = energyExchangeName;
      return this;
    }
    
    public Builder withErrorMessages(List<String> errorMessages) {
      requireNonNull(errorMessages, "errorMessages cannot be null");
      this.errorMessages = errorMessages;
      return this;
    }

    public AdFunctionErrorMessagesValueObject build() {
      requireNonNull(adFunctionTemplateId, "adFunctionTemplateId cannot be null");
      requireNonNull(energyExchangeId, "energyExchangeId cannot be null");
      requireNonNull(errorMessages, "errorMessages cannot be null");
      return new AdFunctionErrorMessagesValueObject(this);
    }
  }
}