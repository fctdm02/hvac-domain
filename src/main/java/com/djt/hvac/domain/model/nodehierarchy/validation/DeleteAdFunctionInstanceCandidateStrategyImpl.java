package com.djt.hvac.domain.model.nodehierarchy.validation;

import java.util.Map;

import com.djt.hvac.domain.model.common.validation.RemediationStrategy;
import com.djt.hvac.domain.model.function.AbstractAdFunctionInstanceEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EnergyExchangeEntity;


public final class DeleteAdFunctionInstanceCandidateStrategyImpl extends AbstractPortfolioRemediationStrategy {

  private static final RemediationStrategy INSTANCE = new DeleteAdFunctionInstanceCandidateStrategyImpl();

  public static RemediationStrategy get() {
    return INSTANCE;
  }

  private DeleteAdFunctionInstanceCandidateStrategyImpl() {
    super();
  }
  
  @Override
  public void remediate(Map<String, Object> entities) {
    
    AbstractAdFunctionInstanceEntity adFunctionInstanceCandidate = (AbstractAdFunctionInstanceEntity)entities.get("function_candidate");
    
    adFunctionInstanceCandidate.setIsDeleted();
    EnergyExchangeEntity equipment = adFunctionInstanceCandidate.getEquipment();
    if (equipment != null) {
      
      equipment.addDeletedAdFunctionInstanceCandidate(adFunctionInstanceCandidate);
      equipment.setIsModified("childAdFunctionInstanceCandidate:deleted");
    }
  }
  
  @Override
  public String getRemediationDescription() {
    return "Delete AD function instance candidate";
  }
}
