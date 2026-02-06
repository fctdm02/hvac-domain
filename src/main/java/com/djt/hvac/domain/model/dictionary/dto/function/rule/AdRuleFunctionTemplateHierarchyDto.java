package com.djt.hvac.domain.model.dictionary.dto.function.rule;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = AdRuleFunctionTemplateHierarchyDto.Builder.class)
public class AdRuleFunctionTemplateHierarchyDto {
  
  private final List<AdRuleFunctionSystemCategoryDto> ruleSystemCategories;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (AdRuleFunctionTemplateHierarchyDto adRuleFunctionTemplateHierarchyDto) {
    return new Builder(adRuleFunctionTemplateHierarchyDto);
  }

  private AdRuleFunctionTemplateHierarchyDto (Builder builder) {
    this.ruleSystemCategories = builder.ruleSystemCategories;
  }

  public List<AdRuleFunctionSystemCategoryDto> getRuleSystemCategories() {
    return ruleSystemCategories;
  }

  @JsonPOJOBuilder
  public static class Builder {
    private List<AdRuleFunctionSystemCategoryDto> ruleSystemCategories;

    private Builder() {}

    private Builder(AdRuleFunctionTemplateHierarchyDto adRuleFunctionTemplateHierarchyDto) {
      requireNonNull(adRuleFunctionTemplateHierarchyDto, "adRuleFunctionHierarchyDto cannot be null");
      this.ruleSystemCategories = adRuleFunctionTemplateHierarchyDto.ruleSystemCategories;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }
    
    public Builder withRuleSystemCategories(List<AdRuleFunctionSystemCategoryDto> ruleSystemCategories) {
      requireNonNull(ruleSystemCategories, "ruleSystemCategories cannot be null");
      this.ruleSystemCategories = ruleSystemCategories;
      return this;
    }

    public AdRuleFunctionTemplateHierarchyDto build() {
      requireNonNull(ruleSystemCategories, "ruleSystemCategories cannot be null");
      return new AdRuleFunctionTemplateHierarchyDto(this);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((ruleSystemCategories == null) ? 0 : ruleSystemCategories.hashCode());
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
    AdRuleFunctionTemplateHierarchyDto other = (AdRuleFunctionTemplateHierarchyDto) obj;
    if (ruleSystemCategories == null) {
      if (other.ruleSystemCategories != null)
        return false;
    } else if (!ruleSystemCategories.equals(other.ruleSystemCategories))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "AdRuleFunctionHierarchyDto [ruleSystemCategories="
        + ruleSystemCategories + "]";
  }
}