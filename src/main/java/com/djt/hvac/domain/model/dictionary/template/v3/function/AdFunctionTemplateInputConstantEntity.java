//@formatter:off
package com.djt.hvac.domain.model.dictionary.template.v3.function;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Set;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.enums.DataType;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;

public class AdFunctionTemplateInputConstantEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  
  private final AdFunctionModuleEntity parentAdFunction;
  private final Integer sequenceNumber;
  private final String name;
  private final String description;
  private final String defaultValue;
  private final DataType dataType;
  private final PointTemplateEntity pointTemplate;
  
  public AdFunctionTemplateInputConstantEntity(
      Integer persistentIdentity,
      AdFunctionModuleEntity parentAdFunction,
      Integer sequenceNumber,
      String name,
      String description,
      String defaultValue,
      DataType dataType,
      PointTemplateEntity pointTemplate) {
    super(persistentIdentity);
    requireNonNull(parentAdFunction, "parentAdFunction cannot be null");
    requireNonNull(sequenceNumber, "sequenceNumber cannot be null");
    requireNonNull(name, "name cannot be null");
    requireNonNull(description, "description cannot be null");
    requireNonNull(defaultValue, "defaultValue cannot be null");
    requireNonNull(dataType, "dataType cannot be null");
    this.parentAdFunction = parentAdFunction;
    this.sequenceNumber = sequenceNumber;
    
    // Fix the DELAY constant for rule 3.2.20.1
    if (persistentIdentity.equals(Integer.valueOf(332)) && !name.equals("DELAY")) {
      name = "DELAY";
    }
    this.name = name;
    this.description = description;
    this.defaultValue = defaultValue;
    this.dataType = dataType;
    this.pointTemplate = pointTemplate;
  }
  
  public AdFunctionModuleEntity getParentAdFunction() {
    return this.parentAdFunction;
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

  public DataType getDataType() {
    return dataType;
  }

  public PointTemplateEntity getPointTemplate() {
    return pointTemplate;
  }
  
  @Override
  public String getNaturalIdentity() {
   
    return new StringBuilder()
        .append(parentAdFunction.getNaturalIdentity())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(name)
        .toString();
  }   
  
  public int compareTo(AbstractEntity that) {

    return this.sequenceNumber.compareTo(((AdFunctionTemplateInputConstantEntity) that).sequenceNumber);
  }  
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
  }
}
//@formatter:on