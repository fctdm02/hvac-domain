package com.djt.hvac.domain.model.dictionary.dto.function.rule;

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
@JsonDeserialize(builder = AdRuleFunctionTemplateDto.Builder.class)
public class AdRuleFunctionTemplateDto implements Comparable<AdRuleFunctionTemplateDto> {

  private final Integer id;
  private final Integer ruleId;
  private final String name;
  private final String displayName;
  private final String description;
  private final String faultNumber;
  private final Integer equipmentTypeId;
  private final String equipmentType;
  private final Integer functionTypeId;
  private final String nodeFilterExpression;
  private final String tupleConstraintExpression;
  private final Boolean isBeta;
  private final List<AdRuleFunctionTemplateInputConstant> inputConstants;
  private final List<AdRuleFunctionTemplateInputPoint> inputPoints;
  private final List<AdRuleFunctionTemplateOutputPoint> outputPoints;

  @JsonCreator
  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(AdRuleFunctionTemplateDto ruleTemplate) {
    return new Builder(ruleTemplate);
  }

  private AdRuleFunctionTemplateDto(Builder builder) {
    this.id = builder.id;
    this.ruleId = builder.ruleId;
    this.name = builder.name;
    this.displayName = builder.displayName;
    this.description = builder.description;
    this.faultNumber = builder.faultNumber;
    this.equipmentTypeId = builder.equipmentTypeId;
    this.equipmentType = builder.equipmentType;
    this.nodeFilterExpression = builder.nodeFilterExpression;
    this.functionTypeId = builder.functionTypeId;
    this.tupleConstraintExpression = builder.tupleConstraintExpression;
    this.isBeta = builder.isBeta;
    this.inputConstants = builder.inputConstants;
    this.inputPoints = builder.inputPoints;
    this.outputPoints = builder.outputPoints;
  }

  public Integer getId() {
    return id;
  }

  public Integer getRuleId() {
    return ruleId;
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

  public String getFaultNumber() {
    return faultNumber;
  }

  public Integer getEquipmentTypeId() {
    return equipmentTypeId;
  }

  public String getEquipmentType() {
    return equipmentType;
  }

  public String getNodeFilterExpression() {
    return nodeFilterExpression;
  }

  public Integer getFunctionTypeId() {
    return functionTypeId;
  }

  public String getTupleConstraintExpression() {
    return tupleConstraintExpression;
  }

  public Boolean getIsBeta() {
    return isBeta;
  }
  
  public void addInputConstants(List<AdRuleFunctionTemplateInputConstant> inputConstants) {
    this.inputConstants.clear();
    this.inputConstants.addAll(inputConstants);
  }

  public List<AdRuleFunctionTemplateInputConstant> getInputConstants() {
    return inputConstants;
  }

  public void addInputPoints(List<AdRuleFunctionTemplateInputPoint> inputPoints) {
    this.inputPoints.clear();
    this.inputPoints.addAll(inputPoints);
  }

  public List<AdRuleFunctionTemplateInputPoint> getInputPoints() {
    return inputPoints;
  }

  public void addOutputPoints(List<AdRuleFunctionTemplateOutputPoint> outputPoints) {
    this.outputPoints.clear();
    this.outputPoints.addAll(outputPoints);
  }

  public List<AdRuleFunctionTemplateOutputPoint> getOutputPoints() {
    return outputPoints;
  }

  @JsonIgnore
  public AdRuleFunctionTemplateOutputPoint getOutputPoint() {
    return outputPoints.get(0);
  }

  @JsonIgnore
  public AdRuleFunctionTemplateInputPoint getInputPoint(Integer inputPointId) {
    Iterator<AdRuleFunctionTemplateInputPoint> iterator = inputPoints.iterator();
    while (iterator.hasNext()) {
      AdRuleFunctionTemplateInputPoint inputPoint = iterator.next();
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
    private Integer ruleId;
    private String name;
    private String displayName;
    private String description;
    private String faultNumber;
    private Integer equipmentTypeId;
    private String equipmentType;
    private String nodeFilterExpression;
    private Integer functionTypeId;
    private String tupleConstraintExpression;
    private Boolean isBeta;
    private List<AdRuleFunctionTemplateInputConstant> inputConstants = new ArrayList<>();
    private List<AdRuleFunctionTemplateInputPoint> inputPoints = new ArrayList<>();
    private List<AdRuleFunctionTemplateOutputPoint> outputPoints = new ArrayList<>();

    private Builder() {}

    private Builder(AdRuleFunctionTemplateDto ruleTemplate) {
      requireNonNull(ruleTemplate, "ruleTemplate cannot be null");
      this.id = ruleTemplate.id;
      this.ruleId = ruleTemplate.ruleId;
      this.name = ruleTemplate.name;
      this.displayName = ruleTemplate.displayName;
      this.description = ruleTemplate.description;
      this.faultNumber = ruleTemplate.faultNumber;
      this.equipmentTypeId = ruleTemplate.equipmentTypeId;
      this.equipmentType = ruleTemplate.equipmentType;
      this.nodeFilterExpression = ruleTemplate.nodeFilterExpression;
      this.functionTypeId = ruleTemplate.functionTypeId;
      this.tupleConstraintExpression = ruleTemplate.tupleConstraintExpression;
      this.isBeta = ruleTemplate.isBeta;
      this.inputConstants = ruleTemplate.inputConstants;
      this.inputPoints = ruleTemplate.inputPoints;
      this.outputPoints = ruleTemplate.outputPoints;
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

    public Builder withRuleId(Integer ruleId) {
      this.ruleId = ruleId;
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

    public Builder withFaultNumber(String faultNumber) {
      requireNonNull(faultNumber, "faultNumber cannot be null");
      this.faultNumber = faultNumber;
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

    public Builder withNodeFilterExpression(String nodeFilterExpression) {
      this.nodeFilterExpression = nodeFilterExpression;
      return this;
    }

    public Builder withFunctionTypeId(Integer functionTypeId) {
      requireNonNull(functionTypeId, "functionTypeId cannot be null");
      this.functionTypeId = functionTypeId;
      return this;
    }

    public Builder withTupleConstraintExpression(String tupleConstraintExpression) {
      this.tupleConstraintExpression = tupleConstraintExpression;
      return this;
    }    

    public Builder withIsBeta(Boolean isBeta) {
      this.isBeta = isBeta;
      return this;
    }    
    
    public Builder withInputConstants(List<AdRuleFunctionTemplateInputConstant> inputConstants) {
      requireNonNull(inputConstants, "inputConstants cannot be null");
      this.inputConstants = inputConstants;
      return this;
    }

    public Builder withInputPoints(List<AdRuleFunctionTemplateInputPoint> inputPoints) {
      requireNonNull(inputPoints, "inputPoints cannot be null");
      this.inputPoints = inputPoints;
      return this;
    }

    public Builder withOutputPoints(List<AdRuleFunctionTemplateOutputPoint> outputPoints) {
      requireNonNull(outputPoints, "outputPoints cannot be null");
      this.outputPoints = outputPoints;
      return this;
    }

    public AdRuleFunctionTemplateDto build() {
      requireNonNull(id, "id cannot be null");
      requireNonNull(name, "name cannot be null");
      requireNonNull(displayName, "displayName cannot be null");
      requireNonNull(description, "description cannot be null");
      requireNonNull(faultNumber, "faultNumber cannot be null");
      requireNonNull(equipmentTypeId, "equipmentTypeId cannot be null");
      requireNonNull(equipmentType, "equipmentType cannot be null");
      requireNonNull(functionTypeId, "functionTypeId cannot be null");
      requireNonNull(inputConstants, "inputConstants cannot be null");
      requireNonNull(inputPoints, "inputPoints cannot be null");
      return new AdRuleFunctionTemplateDto(this);
    }
  }

  @Override
  public int compareTo(AdRuleFunctionTemplateDto that) {

    // Given a fault number of A.B.C.D, then:
    // A is the ordinal of the rule system category
    // A is the ordinal of the rule equipment category
    // B is the subOrdinal of the rule equipment category
    // C is the ordinal of the rule template instance
    // D is the subOrdinal of the rule template instance
    String thatFaultNumber = that.faultNumber.replaceAll("\\.", ":");
    String[] thatFaultNumberElements = thatFaultNumber.split(":");
    if (thatFaultNumberElements.length != 4) {
      return -1;
    }
    String thatOrdinalA = thatFaultNumberElements[2];
    String thatOrdinalB = thatFaultNumberElements[3];


    String thisFaultNumber = this.faultNumber.replaceAll("\\.", ":");
    String[] thisFaultNumberElements = thisFaultNumber.split(":");
    if (thisFaultNumberElements.length != 4) {
      return -1;
    }
    String thisOrdinalA = thisFaultNumberElements[2];
    String thisOrdinalB = thisFaultNumberElements[3];

    int compareTo = Integer.valueOf(thisOrdinalA).compareTo(Integer.valueOf(thatOrdinalA));
    if (compareTo == 0) {
      compareTo = Integer.valueOf(thisOrdinalB).compareTo(Integer.valueOf(thatOrdinalB));
    }
    return compareTo;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((equipmentType == null) ? 0 : equipmentType.hashCode());
    result = prime * result + ((equipmentTypeId == null) ? 0 : equipmentTypeId.hashCode());
    result = prime * result + ((faultNumber == null) ? 0 : faultNumber.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((inputConstants == null) ? 0 : inputConstants.hashCode());
    result = prime * result + ((inputPoints == null) ? 0 : inputPoints.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
    result = prime * result + ((functionTypeId == null) ? 0 : functionTypeId.hashCode());
    result =
        prime * result + ((nodeFilterExpression == null) ? 0 : nodeFilterExpression.hashCode());
    result = prime * result + ((outputPoints == null) ? 0 : outputPoints.hashCode());
    result = prime * result + ((ruleId == null) ? 0 : ruleId.hashCode());
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
    AdRuleFunctionTemplateDto other = (AdRuleFunctionTemplateDto) obj;
    if (description == null) {
      if (other.description != null)
        return false;
    } else if (!description.equals(other.description))
      return false;
    if (equipmentType == null) {
      if (other.equipmentType != null)
        return false;
    } else if (!equipmentType.equals(other.equipmentType))
      return false;
    if (equipmentTypeId == null) {
      if (other.equipmentTypeId != null)
        return false;
    } else if (!equipmentTypeId.equals(other.equipmentTypeId))
      return false;
    if (faultNumber == null) {
      if (other.faultNumber != null)
        return false;
    } else if (!faultNumber.equals(other.faultNumber))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (inputConstants == null) {
      if (other.inputConstants != null)
        return false;
    } else if (!inputConstants.equals(other.inputConstants))
      return false;
    if (inputPoints == null) {
      if (other.inputPoints != null)
        return false;
    } else if (!inputPoints.equals(other.inputPoints))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (displayName == null) {
      if (other.displayName != null)
        return false;
    } else if (!displayName.equals(other.displayName))
      return false;
    if (nodeFilterExpression == null) {
      if (other.nodeFilterExpression != null)
        return false;
    } else if (!nodeFilterExpression.equals(other.nodeFilterExpression))
      return false;
    if (functionTypeId == null) {
      if (other.functionTypeId != null)
        return false;
    } else if (!functionTypeId.equals(other.functionTypeId))
      return false;
    if (outputPoints == null) {
      if (other.outputPoints != null)
        return false;
    } else if (!outputPoints.equals(other.outputPoints))
      return false;
    if (ruleId == null) {
      if (other.ruleId != null)
        return false;
    } else if (!ruleId.equals(other.ruleId))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "RuleTemplateDto [id=" + id + ", ruleId=" + ruleId + ", name=" + name + ", displayName="
        + displayName +
        ", description=" + description + ", faultNumber=" + faultNumber + ", equipmentTypeId="
        + equipmentTypeId
        + ", equipmentType=" + equipmentType + ", nodeFilterExpression=" + nodeFilterExpression
        + ", functionTypeId=" + functionTypeId
        + ", inputConstants=" + inputConstants + ", inputPoints=" + inputPoints + ", outputPoints="
        + outputPoints + "]";
  }
}
