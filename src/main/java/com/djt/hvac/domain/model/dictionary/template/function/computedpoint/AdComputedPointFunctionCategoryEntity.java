package com.djt.hvac.domain.model.dictionary.template.function.computedpoint;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.energyexchange.AbstractEnergyExchangeTypeEntity;

public class AdComputedPointFunctionCategoryEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  
  private final AbstractEnergyExchangeTypeEntity energyExchangeType;
  private final Set<AdComputedPointFunctionTemplateEntity> adComputedPointFunctionTemplates = new TreeSet<>();
  
  public AdComputedPointFunctionCategoryEntity(
      Integer persistentIdentity,
      AbstractEnergyExchangeTypeEntity energyExchangeType) {
    super(persistentIdentity);
    requireNonNull(energyExchangeType, "energyExchangeType cannot be null");
    this.energyExchangeType = energyExchangeType;
  }
  
  public AbstractEnergyExchangeTypeEntity getEnergyExchangeType() {
    return energyExchangeType;
  }
  
  public boolean addAdComputedPointFunctionTemplate(AdComputedPointFunctionTemplateEntity adComputedPointFunctionTemplate) throws EntityAlreadyExistsException {
    return addChild(adComputedPointFunctionTemplates, adComputedPointFunctionTemplate, this);
  }
  
  public Set<AdComputedPointFunctionTemplateEntity> getComputedPointFunctionTemplates() {
    return adComputedPointFunctionTemplates;
  }
  
  public String getNaturalIdentity() {
    return energyExchangeType.getNaturalIdentity();
  }   
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
    
    // NOTHING TO DO
  }
}
