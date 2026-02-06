package com.djt.hvac.domain.model.function.rule;

import java.util.List;
import java.util.Set;

import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.template.function.rule.AdRuleFunctionTemplateEntity;
import com.djt.hvac.domain.model.function.AbstractAdFunctionInstanceEntity;
import com.djt.hvac.domain.model.function.AdFunctionInstanceOutputPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EnergyExchangeEntity;

public class AdRuleFunctionInstanceEntity extends AbstractAdFunctionInstanceEntity {
  private static final long serialVersionUID = 1L;
  private AdFunctionInstanceOutputPointEntity outputPoint;

  public AdRuleFunctionInstanceEntity(
      Integer persistentIdentity,
      EnergyExchangeEntity equipment,
      AdRuleFunctionTemplateEntity adFunctionTemplate,
      boolean isCandidate,
      boolean isIgnored,
      Integer templateVersion,
      Integer instanceVersion) {
    super(
        persistentIdentity, 
        equipment, 
        adFunctionTemplate,
        isCandidate,
        isIgnored,
        templateVersion,
        instanceVersion);
  }  
  
  public boolean addOutputPoint(AdFunctionInstanceOutputPointEntity outputPoint) throws EntityAlreadyExistsException {
    
    if (!getOutputPoints().isEmpty()) {
      throw new EntityAlreadyExistsException("AD Rule Function Instance: ["
          + getNaturalIdentity()
          + "] already has an output point associated with it: ["
          + outputPoint.getNaturalIdentity()
          + "].");
    }
    this.outputPoint = outputPoint;
    return super.addOutputPoint(outputPoint);
  }
  
  public AdFunctionInstanceOutputPointEntity getOutputPoint() {
    return outputPoint;
  }
  
  public String getNaturalIdentity() {
    return getNaturalIdentity(getAdFunctionTemplate(), getEquipment());
  }
  
  @Override
  public void validate(
      Set<IssueType> issueTypes, 
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
  }
}
