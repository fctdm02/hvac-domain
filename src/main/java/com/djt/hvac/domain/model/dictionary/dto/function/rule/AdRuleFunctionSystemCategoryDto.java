package com.djt.hvac.domain.model.dictionary.dto.function.rule;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = AdRuleFunctionSystemCategoryDto.Builder.class)
public class AdRuleFunctionSystemCategoryDto implements Comparable<AdRuleFunctionSystemCategoryDto> {
  
  private final String name;
  private final List<AdRuleFunctionEquipmentCategoryDto> adRuleFunctionEquipmentCategoryDtos;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (AdRuleFunctionSystemCategoryDto adRuleFunctionSystemCategoryDto) {
    return new Builder(adRuleFunctionSystemCategoryDto);
  }

  private AdRuleFunctionSystemCategoryDto (Builder builder) {
    this.name = builder.name;
    this.adRuleFunctionEquipmentCategoryDtos = builder.adRuleFunctionEquipmentCategoryDtos;
  }

  public String getName() {
    return name;
  }
  
  public boolean addRuleEquipmentCategory(AdRuleFunctionEquipmentCategoryDto adRuleFunctionEquipmentCategoryDto) {
    if (this.adRuleFunctionEquipmentCategoryDtos.contains(adRuleFunctionEquipmentCategoryDto)) {
      return false;
    }
    boolean b = this.adRuleFunctionEquipmentCategoryDtos.add(adRuleFunctionEquipmentCategoryDto);
    Collections.sort(this.adRuleFunctionEquipmentCategoryDtos);
    return b;
  }

  public List<AdRuleFunctionEquipmentCategoryDto> getRuleEquipmentCategories() {
    return adRuleFunctionEquipmentCategoryDtos;
  }
  
  @JsonPOJOBuilder
  public static class Builder {
    private String name;
    private List<AdRuleFunctionEquipmentCategoryDto> adRuleFunctionEquipmentCategoryDtos = new ArrayList<>();

    private Builder() {}

    private Builder(AdRuleFunctionSystemCategoryDto adRuleFunctionSystemCategoryDto) {
      requireNonNull(adRuleFunctionSystemCategoryDto, "ruleSystemCategory cannot be null");
      this.name = adRuleFunctionSystemCategoryDto.name;
      this.adRuleFunctionEquipmentCategoryDtos = adRuleFunctionSystemCategoryDto.adRuleFunctionEquipmentCategoryDtos;
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

    public Builder withRuleEquipmentCategories(List<AdRuleFunctionEquipmentCategoryDto> adRuleFunctionEquipmentCategoryDtos) {
      requireNonNull(adRuleFunctionEquipmentCategoryDtos, "ruleEquipmentCategoryDtos cannot be null");
      this.adRuleFunctionEquipmentCategoryDtos = adRuleFunctionEquipmentCategoryDtos;
      return this;
    }

    public AdRuleFunctionSystemCategoryDto build() {
      requireNonNull(name, "name cannot be null");
      requireNonNull(adRuleFunctionEquipmentCategoryDtos, "ruleEquipmentCategoryDtos cannot be null");
      return new AdRuleFunctionSystemCategoryDto(this);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result
        + ((adRuleFunctionEquipmentCategoryDtos == null) ? 0 : adRuleFunctionEquipmentCategoryDtos.hashCode());
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
    AdRuleFunctionSystemCategoryDto other = (AdRuleFunctionSystemCategoryDto) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (adRuleFunctionEquipmentCategoryDtos == null) {
      if (other.adRuleFunctionEquipmentCategoryDtos != null)
        return false;
    } else if (!adRuleFunctionEquipmentCategoryDtos.equals(other.adRuleFunctionEquipmentCategoryDtos))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "RuleSystemCategoryDto [name=" + name + ", ruleEquipmentCategoryDtos="
        + adRuleFunctionEquipmentCategoryDtos + "]";
  }

  @Override
  public int compareTo(AdRuleFunctionSystemCategoryDto that) {
    
    int thatIndex = that.name.indexOf(' ');
    Integer thatOrdinalA = Integer.valueOf(that.name.substring(0, thatIndex));
    
    int thisIndex = this.name.indexOf(' ');
    Integer thisOrdinalA = Integer.valueOf(this.name.substring(0, thisIndex));
    
    return thisOrdinalA.compareTo(thatOrdinalA);    
  }
}