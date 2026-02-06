package com.djt.hvac.domain.model.dictionary.template.function.rule;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;

public class AdRuleFunctionSystemCategoryEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  
  private final String faultPrefix;   
  private final String name;
  private final Set<AdRuleFunctionEquipmentCategoryEntity> ruleEquipmentCategories = new TreeSet<>();
  
  public AdRuleFunctionSystemCategoryEntity(
      Integer persistentIdentity,
      String faultPrefix,   
      String name) {
    super(persistentIdentity);
    requireNonNull(faultPrefix, "faultPrefix cannot be null");
    requireNonNull(name, "faultPrefix name be null");
    this.faultPrefix = faultPrefix;
    this.name = name;
  }
  
  public String getFaultPrefix() {
    return faultPrefix;
  }
  
  public String getName() {
    return name;
  }
  
  public boolean addRuleEquipmentCategory(AdRuleFunctionEquipmentCategoryEntity ruleEquipmentCategory) throws EntityAlreadyExistsException {
    return addChild(ruleEquipmentCategories, ruleEquipmentCategory, this);
  }
  
  public Set<AdRuleFunctionEquipmentCategoryEntity> getRuleEquipmentCategories() {
    return ruleEquipmentCategories;
  }
  
  @Override
  public String getNaturalIdentity() {
    
    return new StringBuilder()
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
  }
}
