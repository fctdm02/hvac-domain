package com.djt.hvac.domain.model.dictionary.template.function.computedpoint;

import java.util.List;
import java.util.Set;

import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.energyexchange.AbstractEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AbstractAdFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionEntity;

public class AdComputedPointFunctionTemplateEntity extends AbstractAdFunctionTemplateEntity {
  private static final long serialVersionUID = 1L;
  
  private final String referenceNumber;
  
  public AdComputedPointFunctionTemplateEntity(
      Integer persistentIdentity,
      AdFunctionEntity adFunction,
      String name,
      String displayName,
      String description,
      AbstractEnergyExchangeTypeEntity energyExchangeType,
      String nodeFilterExpression,
      String tupleConstraintExpression,
      Boolean isBeta,
      Integer version,
      String referenceNumber) {
    super(
        persistentIdentity,
        adFunction,
        name,
        displayName,
        description,
        energyExchangeType,
        nodeFilterExpression,
        tupleConstraintExpression,
        isBeta,
        version);
    
    this.referenceNumber = referenceNumber;
  }
  
  @Override
  public String getNaturalIdentity() {
   
    return new StringBuilder()
        .append(referenceNumber)
        .append(" ")
        .append(getName())
        .toString();
  }
  
  @Override
  public String getFaultOrReferenceNumber() {
    return referenceNumber;
  }
  
  @Override
  public String getFullDisplayName() {

    return new StringBuilder()
        .append(referenceNumber)
        .append(" ")
        .append(getDisplayName())
        .toString();
  }
  
  public String getReferenceNumber() {
    return this.referenceNumber;
  }
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
    
    // NOTHING TO DO
  }
}
