package com.djt.hvac.domain.model.dictionary.dto.function.computedpoint;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = AdComputedPointFunctionCategoryDto.Builder.class)
public class AdComputedPointFunctionCategoryDto implements Comparable<AdComputedPointFunctionCategoryDto> {
  
  private final String name;
  private final String equipmentTypeTag;
  private final List<AdComputedPointFunctionTemplateDto> computedPointTemplates;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (AdComputedPointFunctionCategoryDto adComputedPointFunctionEquipmentCategoryDto) {
    return new Builder(adComputedPointFunctionEquipmentCategoryDto);
  }

  private AdComputedPointFunctionCategoryDto (Builder builder) {
    this.name = builder.name;
    this.equipmentTypeTag = builder.equipmentTypeTag;
    this.computedPointTemplates = builder.computedPointTemplates;
  }

  public String getName() {
    return name;
  }
  
  public String getEquipmentTypeTag() {
    return equipmentTypeTag;
  }
  
  public boolean addComputedPointTemplate(AdComputedPointFunctionTemplateDto adFunctionTemplateDto) {
    if (this.computedPointTemplates.contains(adFunctionTemplateDto)) {
      throw new IllegalStateException("computedPoint equipment category: [" + name + "] already contains computedPoint template: [" + adFunctionTemplateDto.getDisplayName() + "].");
    }    
    boolean b = this.computedPointTemplates.add(adFunctionTemplateDto);
    return b;
  }

  public List<AdComputedPointFunctionTemplateDto> getComputedPointTemplates() {
    return computedPointTemplates;
  }
  
  public AdComputedPointFunctionTemplateDto getComputedPointTemplate(Integer computedPointTemplateId) {
    
    Iterator<AdComputedPointFunctionTemplateDto> iterator = this.computedPointTemplates.iterator();
    while (iterator.hasNext()) {
      
      AdComputedPointFunctionTemplateDto adFunctionTemplateDto = iterator.next();
      if (adFunctionTemplateDto.getId().equals(computedPointTemplateId)) {
        return adFunctionTemplateDto;
      }      
    }
    return null;
  }
  
  public void addAllComputedPointTemplates(List<AdComputedPointFunctionTemplateDto> adFunctionTemplateDtos) {
    
    adFunctionTemplateDtos.addAll(this.computedPointTemplates);
  }

  @JsonPOJOBuilder
  public static class Builder {
    private String name;
    private String equipmentTypeTag;
    private List<AdComputedPointFunctionTemplateDto> computedPointTemplates = new ArrayList<>();

    private Builder() {}

    private Builder(AdComputedPointFunctionCategoryDto adComputedPointFunctionEquipmentCategoryDto) {
      requireNonNull(adComputedPointFunctionEquipmentCategoryDto, "computedPointEquipmentCategory cannot be null");
      this.name = adComputedPointFunctionEquipmentCategoryDto.name;
      this.computedPointTemplates = adComputedPointFunctionEquipmentCategoryDto.computedPointTemplates;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }
    
    public Builder withName(String name) {
      requireNonNull(name, "name cannot be null");
      this.name = name;
      return this;
    }
    
    public Builder withEquipmentTypeTag(String equipmentTypeTag) {
      requireNonNull(equipmentTypeTag, "equipmentTypeTag cannot be null");
      this.equipmentTypeTag = equipmentTypeTag;
      return this;
    }    
    
    public Builder withComputedPointTemplates(List<AdComputedPointFunctionTemplateDto> computedPointTemplates) {
      requireNonNull(computedPointTemplates, "computedPointTemplates cannot be null");
      this.computedPointTemplates = computedPointTemplates;
      return this;
    }

    public AdComputedPointFunctionCategoryDto build() {
      requireNonNull(name, "name cannot be null");
      requireNonNull(equipmentTypeTag, "equipmentTypeTag cannot be null");
      requireNonNull(computedPointTemplates, "computedPointTemplates cannot be null");
      return new AdComputedPointFunctionCategoryDto(this);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((computedPointTemplates == null) ? 0 : computedPointTemplates.hashCode());
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
    AdComputedPointFunctionCategoryDto other = (AdComputedPointFunctionCategoryDto) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (computedPointTemplates == null) {
      if (other.computedPointTemplates != null)
        return false;
    } else if (!computedPointTemplates.equals(other.computedPointTemplates))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ComputedPointEquipmentCategoryDto [name=" + name + ", computedPointTemplates=" + computedPointTemplates + "]";
  }

  @Override
  public int compareTo(AdComputedPointFunctionCategoryDto that) {
    
    return this.name.compareTo(that.name);
  }
}