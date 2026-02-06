package com.djt.hvac.domain.model.dictionary.dto.function.rule;

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
@JsonDeserialize(builder = AdRuleFunctionEquipmentCategoryDto.Builder.class)
public class AdRuleFunctionEquipmentCategoryDto implements Comparable<AdRuleFunctionEquipmentCategoryDto> {
  
  private final String name;
  private final String equipmentTypeTags;
  private final List<AdRuleFunctionTemplateDto> adFunctionTemplateDtos;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (AdRuleFunctionEquipmentCategoryDto adRuleFunctionEquipmentCategoryDto) {
    return new Builder(adRuleFunctionEquipmentCategoryDto);
  }

  private AdRuleFunctionEquipmentCategoryDto (Builder builder) {
    this.name = builder.name;
    this.equipmentTypeTags = builder.equipmentTypeTags;
    this.adFunctionTemplateDtos = builder.adFunctionTemplateDtos;
  }

  public String getName() {
    return name;
  }
  
  public String getEquipmentTypeTags() {
    return equipmentTypeTags;
  }
  
  public boolean addRuleTemplate(AdRuleFunctionTemplateDto adFunctionTemplateDto) {
    if (this.adFunctionTemplateDtos.contains(adFunctionTemplateDto)) {
      throw new IllegalStateException("rule equipment category: [" + name + "] already contains rule template: [" + adFunctionTemplateDto.getFaultNumber() + "].");
    }    
    boolean b = this.adFunctionTemplateDtos.add(adFunctionTemplateDto);
    return b;
  }

  public List<AdRuleFunctionTemplateDto> getRuleTemplates() {
    return adFunctionTemplateDtos;
  }
  
  public AdRuleFunctionTemplateDto getRuleTemplate(Integer ruleTemplateId) {
    
    Iterator<AdRuleFunctionTemplateDto> iterator = this.adFunctionTemplateDtos.iterator();
    while (iterator.hasNext()) {
      
      AdRuleFunctionTemplateDto adFunctionTemplateDto = iterator.next();
      if (adFunctionTemplateDto.getId().equals(ruleTemplateId)) {
        return adFunctionTemplateDto;
      }      
    }
    return null;
  }
  
  public void addAllRuleTemplates(List<AdRuleFunctionTemplateDto> adFunctionTemplateDtos) {
    
    adFunctionTemplateDtos.addAll(this.adFunctionTemplateDtos);
  }

  @JsonPOJOBuilder
  public static class Builder {
    private String name;
    private String equipmentTypeTags;
    private List<AdRuleFunctionTemplateDto> adFunctionTemplateDtos = new ArrayList<>();

    private Builder() {}

    private Builder(AdRuleFunctionEquipmentCategoryDto adRuleFunctionEquipmentCategoryDto) {
      requireNonNull(adRuleFunctionEquipmentCategoryDto, "ruleEquipmentCategory cannot be null");
      this.name = adRuleFunctionEquipmentCategoryDto.name;
      this.adFunctionTemplateDtos = adRuleFunctionEquipmentCategoryDto.adFunctionTemplateDtos;
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
    
    public Builder withEquipmentTypeTags(String equipmentTypeTags) {
      requireNonNull(equipmentTypeTags, "equipmentTypeTags cannot be null");
      this.equipmentTypeTags = equipmentTypeTags;
      return this;
    }    
    
    public Builder withRuleTemplates(List<AdRuleFunctionTemplateDto> adFunctionTemplateDtos) {
      requireNonNull(adFunctionTemplateDtos, "ruleTemplateDtos cannot be null");
      this.adFunctionTemplateDtos = adFunctionTemplateDtos;
      return this;
    }

    public AdRuleFunctionEquipmentCategoryDto build() {
      requireNonNull(name, "name cannot be null");
      requireNonNull(name, "equipmentTypeTags cannot be null");
      requireNonNull(adFunctionTemplateDtos, "ruleTemplateDtos cannot be null");
      return new AdRuleFunctionEquipmentCategoryDto(this);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((adFunctionTemplateDtos == null) ? 0 : adFunctionTemplateDtos.hashCode());
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
    AdRuleFunctionEquipmentCategoryDto other = (AdRuleFunctionEquipmentCategoryDto) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (adFunctionTemplateDtos == null) {
      if (other.adFunctionTemplateDtos != null)
        return false;
    } else if (!adFunctionTemplateDtos.equals(other.adFunctionTemplateDtos))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "RuleEquipmentCategoryDto [name=" + name + ", ruleTemplateDtos=" + adFunctionTemplateDtos + "]";
  }

  @Override
  public int compareTo(AdRuleFunctionEquipmentCategoryDto that) {
    
    Double thatOrdinalA = null;
    int thatIndex = that.name.indexOf(' ');
    if (thatIndex != -1) {
      thatOrdinalA = Double.valueOf(that.name.substring(0, thatIndex));  
    } else {
      thatOrdinalA = Double.valueOf(that.name);
    }
    
    Double thisOrdinalA = null;
    int thisIndex = this.name.indexOf(' ');
    if (thisIndex != -1) {
      thisOrdinalA = Double.valueOf(this.name.substring(0, thisIndex));
    } else {
      thisOrdinalA = Double.valueOf(this.name);
    }
    
    return thisOrdinalA.compareTo(thatOrdinalA);
  }
}