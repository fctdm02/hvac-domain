package com.djt.hvac.domain.model.dictionary.template.function.rule;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.energyexchange.AbstractEnergyExchangeTypeEntity;

public class AdRuleFunctionEquipmentCategoryEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  
  private final AdRuleFunctionSystemCategoryEntity parentRuleSystemCategory;
  private final String faultPrefix;
  private final String name;
  private final AbstractEnergyExchangeTypeEntity energyExchangeType;
  private final Set<AdRuleFunctionTemplateEntity> adRuleFunctionTemplates = new TreeSet<>();
  
  public AdRuleFunctionEquipmentCategoryEntity(
      Integer persistentIdentity,
      AdRuleFunctionSystemCategoryEntity parentRuleSystemCategory,
      String faultPrefix,
      String name,
      AbstractEnergyExchangeTypeEntity energyExchangeType) {
    super(persistentIdentity);
    requireNonNull(parentRuleSystemCategory, "parentRuleSystemCategory cannot be null");
    requireNonNull(faultPrefix, "faultPrefix cannot be null");
    requireNonNull(name, "name cannot be null");
    requireNonNull(energyExchangeType, "energyExchangeType cannot be null");
    this.parentRuleSystemCategory = parentRuleSystemCategory;
    this.faultPrefix = faultPrefix;
    this.name = name;
    this.energyExchangeType = energyExchangeType;
  }

  public AdRuleFunctionSystemCategoryEntity getParentRuleSystemCategory() {
    return parentRuleSystemCategory;
  }
  
  public String getFaultPrefix() {
    return faultPrefix;
  }
  
  public String getName() {
    return name;
  }

  public AbstractEnergyExchangeTypeEntity getEnergyExchangeType() {
    return energyExchangeType;
  }
  
  public boolean addAdRuleFunctionTemplate(AdRuleFunctionTemplateEntity adRuleFunctionTemplate) throws EntityAlreadyExistsException {
    return addChild(adRuleFunctionTemplates, adRuleFunctionTemplate, this);
  }
  
  public Set<AdRuleFunctionTemplateEntity> getRuleFunctionTemplates() {
    return adRuleFunctionTemplates;
  }
  
  @Override
  public String getNaturalIdentity() {
    
    return new StringBuilder()
        .append(parentRuleSystemCategory.getNaturalIdentity())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(faultPrefix)
        .append(" - ")
        .append(name)
        .toString();
  }   
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
    
    // NOTHING TO DO 
  }
}
