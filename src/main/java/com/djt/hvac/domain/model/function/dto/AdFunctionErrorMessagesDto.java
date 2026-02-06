package com.djt.hvac.domain.model.function.dto;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = AdFunctionErrorMessagesDto.Builder.class)
public class AdFunctionErrorMessagesDto {
  private final String nodePath;
  private final Integer adFunctionTemplateId;
  private final Integer energyExchangeId;
  private final List<Integer> errorMessages;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (AdFunctionErrorMessagesDto adFunctionEquipmentErrorMessagesDto) {
    return new Builder(adFunctionEquipmentErrorMessagesDto);
  }

  private AdFunctionErrorMessagesDto (Builder builder) {
    this.nodePath = builder.nodePath;
    this.adFunctionTemplateId = builder.adFunctionTemplateId;
    this.energyExchangeId = builder.energyExchangeId;
    this.errorMessages = builder.errorMessages;
  }
  
  public String getNodePath() {
    return nodePath;
  }

  public Integer getAdFunctionTemplateId() {
    return adFunctionTemplateId;
  }

  public Integer getEnergyExchangeId() {
    return energyExchangeId;
  }

  public List<Integer> getErrorMessages() {
    return errorMessages;
  }

  @JsonPOJOBuilder
  public static class Builder {
    private String nodePath;
    private Integer adFunctionTemplateId;
    private Integer energyExchangeId;
    private List<Integer> errorMessages;

    private Builder() {}

    private Builder(AdFunctionErrorMessagesDto dto) {
      requireNonNull(dto, "dto cannot be null");
      this.nodePath = dto.getNodePath();
      this.adFunctionTemplateId = dto.adFunctionTemplateId;
      this.energyExchangeId = dto.energyExchangeId;
      this.errorMessages = dto.errorMessages;
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

    public Builder withEnergyExchangeId(Integer energyExchangeId) {
      requireNonNull(energyExchangeId, "energyExchangeId cannot be null");
      this.energyExchangeId = energyExchangeId;
      return this;
    }

    public Builder withErrorMessages(List<Integer> errorMessages) {
      requireNonNull(errorMessages, "errorMessages cannot be null");
      this.errorMessages = errorMessages;
      return this;
    }

    public AdFunctionErrorMessagesDto build() {
      requireNonNull(adFunctionTemplateId, "adFunctionTemplateId cannot be null");
      requireNonNull(energyExchangeId, "energyExchangeId cannot be null");
      requireNonNull(errorMessages, "errorMessages cannot be null");
      return new AdFunctionErrorMessagesDto(this);
    }
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
    AdFunctionErrorMessagesDto other = (AdFunctionErrorMessagesDto) obj;
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
  public String toString() {
    
    return new StringBuilder()
        .append("AdFunctionEquipmentErrorMessagesDto [adFunctionTemplateId=")
        .append(adFunctionTemplateId)
        .append(", energyExchangeId=")
        .append(energyExchangeId)
        .append(", errorMessages=")
        .append(errorMessages)
        .append("]")
        .toString();
  }
}