package com.djt.hvac.domain.model.dictionary.dto.function.computedpoint;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = AdComputedPointFunctionTemplateHierarchyDto.Builder.class)
public class AdComputedPointFunctionTemplateHierarchyDto {
  
  private final List<AdComputedPointFunctionCategoryDto> computedPointCategories;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (AdComputedPointFunctionTemplateHierarchyDto adRuleFunctionTemplateHierarchyDto) {
    return new Builder(adRuleFunctionTemplateHierarchyDto);
  }

  private AdComputedPointFunctionTemplateHierarchyDto (Builder builder) {
    this.computedPointCategories = builder.computedPointCategories;
  }

  public List<AdComputedPointFunctionCategoryDto> getComputedPointCategories() {
    return computedPointCategories;
  }

  @JsonPOJOBuilder
  public static class Builder {
    private List<AdComputedPointFunctionCategoryDto> computedPointCategories;

    private Builder() {}

    private Builder(AdComputedPointFunctionTemplateHierarchyDto adRuleFunctionTemplateHierarchyDto) {
      requireNonNull(adRuleFunctionTemplateHierarchyDto, "adRuleFunctionHierarchyDto cannot be null");
      this.computedPointCategories = adRuleFunctionTemplateHierarchyDto.computedPointCategories;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }
    
    public Builder withComputedPointCategories(List<AdComputedPointFunctionCategoryDto> computedPointCategories) {
      requireNonNull(computedPointCategories, "computedPointCategories cannot be null");
      this.computedPointCategories = computedPointCategories;
      return this;
    }

    public AdComputedPointFunctionTemplateHierarchyDto build() {
      requireNonNull(computedPointCategories, "computedPointCategories cannot be null");
      return new AdComputedPointFunctionTemplateHierarchyDto(this);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((computedPointCategories == null) ? 0 : computedPointCategories.hashCode());
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
    AdComputedPointFunctionTemplateHierarchyDto other = (AdComputedPointFunctionTemplateHierarchyDto) obj;
    if (computedPointCategories == null) {
      if (other.computedPointCategories != null)
        return false;
    } else if (!computedPointCategories.equals(other.computedPointCategories))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "AComputedPointFunctionHierarchyDto [computedPointCategories="
        + computedPointCategories + "]";
  }
}