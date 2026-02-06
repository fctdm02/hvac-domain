package com.djt.hvac.domain.model.dictionary.dto.function.computedpoint;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = AdComputedPointFunctionTemplateDto.Builder.class)
public class AdComputedPointFunctionTemplateDto implements Comparable<AdComputedPointFunctionTemplateDto> {

  private final Integer id;
  private final Integer functionId;
  private final String name;
  private final String displayName;
  private final String description;
  private final Integer equipmentTypeId;
  private final String equipmentType;
  private final List<AdComputedPointFunctionTemplateInputConstant> inputConstants;
  private final List<AdComputedPointFunctionTemplateInputPoint> inputPoints;
  private final List<AdComputedPointFunctionTemplateOutputPoint> outputPoints;

  @JsonCreator
  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(AdComputedPointFunctionTemplateDto ruleTemplate) {
    return new Builder(ruleTemplate);
  }

  private AdComputedPointFunctionTemplateDto(Builder builder) {
    this.id = builder.id;
    this.functionId = builder.functionId;
    this.name = builder.name;
    this.displayName = builder.displayName;
    this.description = builder.description;
    this.equipmentTypeId = builder.equipmentTypeId;
    this.equipmentType = builder.equipmentType;
    this.inputConstants = builder.inputConstants;
    this.inputPoints = builder.inputPoints;
    this.outputPoints = builder.outputPoints;
  }

  public Integer getId() {
    return id;
  }

  public Integer getFunctionId() {
    return functionId;
  }

  public String getName() {
    return name;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getDescription() {
    return description;
  }

  public Integer getEquipmentTypeId() {
    return equipmentTypeId;
  }

  public String getEquipmentType() {
    return equipmentType;
  }
  
  public void addInputConstants(List<AdComputedPointFunctionTemplateInputConstant> inputConstants) {
    this.inputConstants.clear();
    this.inputConstants.addAll(inputConstants);
  }

  public List<AdComputedPointFunctionTemplateInputConstant> getInputConstants() {
    return inputConstants;
  }

  public void addInputPoints(List<AdComputedPointFunctionTemplateInputPoint> inputPoints) {
    this.inputPoints.clear();
    this.inputPoints.addAll(inputPoints);
  }

  public List<AdComputedPointFunctionTemplateInputPoint> getInputPoints() {
    return inputPoints;
  }

  public void addOutputPoints(List<AdComputedPointFunctionTemplateOutputPoint> outputPoints) {
    this.outputPoints.clear();
    this.outputPoints.addAll(outputPoints);
  }

  public List<AdComputedPointFunctionTemplateOutputPoint> getOutputPoints() {
    return outputPoints;
  }

  @JsonIgnore
  public AdComputedPointFunctionTemplateOutputPoint getOutputPoint() {
    return outputPoints.get(0);
  }

  @JsonIgnore
  public AdComputedPointFunctionTemplateInputPoint getInputPoint(Integer inputPointId) {
    Iterator<AdComputedPointFunctionTemplateInputPoint> iterator = inputPoints.iterator();
    while (iterator.hasNext()) {
      AdComputedPointFunctionTemplateInputPoint inputPoint = iterator.next();
      if (inputPoint.getId().equals(inputPointId)) {
        return inputPoint;
      }
    }
    throw new RuntimeException("Rule template input point with id: "
        + inputPointId
        + " not found.");
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Integer id;
    private Integer functionId;
    private String name;
    private String displayName;
    private String description;
    private Integer equipmentTypeId;
    private String equipmentType;
    private List<AdComputedPointFunctionTemplateInputConstant> inputConstants = new ArrayList<>();
    private List<AdComputedPointFunctionTemplateInputPoint> inputPoints = new ArrayList<>();
    private List<AdComputedPointFunctionTemplateOutputPoint> outputPoints = new ArrayList<>();

    private Builder() {}

    private Builder(AdComputedPointFunctionTemplateDto template) {
      requireNonNull(template, "template cannot be null");
      this.id = template.id;
      this.functionId = template.functionId;
      this.name = template.name;
      this.displayName = template.displayName;
      this.description = template.description;
      this.equipmentTypeId = template.equipmentTypeId;
      this.equipmentType = template.equipmentType;
      this.inputConstants = template.inputConstants;
      this.inputPoints = template.inputPoints;
      this.outputPoints = template.outputPoints;
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

    public Builder withFunctionId(Integer functionId) {
      this.functionId = functionId;
      return this;
    }

    public Builder withName(String name) {
      requireNonNull(name, "name cannot be null");
      this.name = name;
      return this;
    }

    public Builder withDisplayName(String displayName) {
      requireNonNull(displayName, "displayName cannot be null");
      this.displayName = displayName;
      return this;
    }

    public Builder withDescription(String description) {
      requireNonNull(description, "description cannot be null");
      this.description = description;
      return this;
    }

    public Builder withEquipmentTypeId(Integer equipmentTypeId) {
      requireNonNull(equipmentTypeId, "equipmentTypeId cannot be null");
      this.equipmentTypeId = equipmentTypeId;
      return this;
    }

    public Builder withEquipmentType(String equipmentType) {
      requireNonNull(equipmentType, "equipmentType cannot be null");
      this.equipmentType = equipmentType;
      return this;
    }
    
    public Builder withInputConstants(List<AdComputedPointFunctionTemplateInputConstant> inputConstants) {
      requireNonNull(inputConstants, "inputConstants cannot be null");
      this.inputConstants = inputConstants;
      return this;
    }

    public Builder withInputPoints(List<AdComputedPointFunctionTemplateInputPoint> inputPoints) {
      requireNonNull(inputPoints, "inputPoints cannot be null");
      this.inputPoints = inputPoints;
      return this;
    }

    public Builder withOutputPoints(List<AdComputedPointFunctionTemplateOutputPoint> outputPoints) {
      requireNonNull(outputPoints, "outputPoints cannot be null");
      this.outputPoints = outputPoints;
      return this;
    }

    public AdComputedPointFunctionTemplateDto build() {
      requireNonNull(id, "id cannot be null");
      requireNonNull(name, "name cannot be null");
      requireNonNull(displayName, "displayName cannot be null");
      requireNonNull(description, "description cannot be null");
      requireNonNull(equipmentTypeId, "equipmentTypeId cannot be null");
      requireNonNull(equipmentType, "equipmentType cannot be null");
      requireNonNull(inputConstants, "inputConstants cannot be null");
      requireNonNull(inputPoints, "inputPoints cannot be null");
      return new AdComputedPointFunctionTemplateDto(this);
    }
  }

  @Override
  public int compareTo(AdComputedPointFunctionTemplateDto that) {

    return this.displayName.compareTo(that.displayName);
  }
}
