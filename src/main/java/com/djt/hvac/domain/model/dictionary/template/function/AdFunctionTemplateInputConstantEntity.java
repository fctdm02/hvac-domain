package com.djt.hvac.domain.model.dictionary.template.function;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Set;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.UnitEntity;
import com.djt.hvac.domain.model.dictionary.enums.DataType;

public class AdFunctionTemplateInputConstantEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  
  private final AbstractAdFunctionTemplateEntity parentRuleTemplate;
  private final Integer sequenceNumber;
  private final String name;
  private final String description;
  private final String defaultValue;
  private final Boolean isRequired;
  private final DataType dataType;
  private final UnitEntity unit;
  
  public AdFunctionTemplateInputConstantEntity(
      Integer persistentIdentity,
      AbstractAdFunctionTemplateEntity parentRuleTemplate,
      Integer sequenceNumber,
      String name,
      String description,
      String defaultValue,
      Boolean isRequired,
      DataType dataType,
      UnitEntity unit) {
    super(persistentIdentity);
    requireNonNull(parentRuleTemplate, "parentRuleTemplate cannot be null");
    requireNonNull(sequenceNumber, "sequenceNumber cannot be null");
    requireNonNull(name, "name cannot be null");
    requireNonNull(description, "description cannot be null");
    requireNonNull(defaultValue, "defaultValue cannot be null");
    requireNonNull(isRequired, "isRequired cannot be null");
    requireNonNull(dataType, "dataType cannot be null");
    requireNonNull(unit, "unit cannot be null");
    this.parentRuleTemplate = parentRuleTemplate;
    this.sequenceNumber = sequenceNumber;
    
    // Fix the DELAY constant for rule 3.2.20.1
    if (persistentIdentity.equals(Integer.valueOf(332)) && !name.equals("DELAY")) {
      name = "DELAY";
    }
    this.name = name;
    this.description = description;
    this.defaultValue = defaultValue;
    this.isRequired = isRequired;
    this.dataType = dataType;
    this.unit = unit;
  }
  
  public AbstractAdFunctionTemplateEntity getParentRuleTemplate() {
    return this.parentRuleTemplate;
  }
  
  public Integer getSequenceNumber() {
    return sequenceNumber;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public Boolean getIsRequired() {
    return isRequired;
  }

  public DataType getDataType() {
    return dataType;
  }

  public UnitEntity getUnit() {
    return unit;
  }
  
  @Override
  public String getNaturalIdentity() {
   
    return new StringBuilder()
        .append(parentRuleTemplate.getNaturalIdentity())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(name)
        .toString();
  }   
  
  public int compareTo(AbstractEntity that) {

    if (that instanceof AdFunctionTemplateInputConstantEntity) {
      return this.sequenceNumber.compareTo(((AdFunctionTemplateInputConstantEntity)that).sequenceNumber);  
    }
    throw new IllegalStateException("Cannot compare to non AdFunctionTemplateInputConstantEntity entity: " + that.getClass().getSimpleName());
  }  
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
  }
  
  public String getSignature() {

    return new StringBuilder()
        .append("sequenceNumber=")
        .append(sequenceNumber)
        .append(", name=")
        .append(name)
        .append(", description=")
        .append(description)
        .append(", defaultValue=")
        .append(defaultValue)
        .append(", isRequired=")
        .append(isRequired)
        .append(", dataType=")
        .append(dataType)
        .append(", unit=")
        .append(unit)
        .toString();
  }  
  
}
