package com.djt.hvac.domain.model.function.computedpoint;

import java.util.List;
import java.util.Set;

import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.template.function.computedpoint.AdComputedPointFunctionTemplateEntity;
import com.djt.hvac.domain.model.function.AbstractAdFunctionInstanceEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EnergyExchangeEntity;

public class AdComputedPointFunctionInstanceEntity extends AbstractAdFunctionInstanceEntity {
  private static final long serialVersionUID = 1L;
  public AdComputedPointFunctionInstanceEntity(
      Integer persistentIdentity,
      EnergyExchangeEntity equipment,
      AdComputedPointFunctionTemplateEntity adFunctionTemplate,
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
  
  public String getNaturalIdentity() {
    return getNaturalIdentity(getAdFunctionTemplate(), getEquipment());
  }
  
  @Override
  public void validate(
      Set<IssueType> issueTypes, 
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
    
    // NOTHING TO DO
  }
}
