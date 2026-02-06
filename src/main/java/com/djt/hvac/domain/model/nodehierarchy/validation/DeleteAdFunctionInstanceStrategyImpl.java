package com.djt.hvac.domain.model.nodehierarchy.validation;

import java.util.Map;

import com.djt.hvac.domain.model.common.validation.RemediationStrategy;
import com.djt.hvac.domain.model.function.AbstractAdFunctionInstanceEntity;

public final class DeleteAdFunctionInstanceStrategyImpl extends AbstractPortfolioRemediationStrategy {
  
  private static final RemediationStrategy INSTANCE = new DeleteAdFunctionInstanceStrategyImpl();
  
  public static RemediationStrategy get() {
    return INSTANCE;
  }

  private DeleteAdFunctionInstanceStrategyImpl() {
  }
  
  @Override
  public void remediate(Map<String, Object> entities) {
    
    AbstractAdFunctionInstanceEntity adFunctionInstance = (AbstractAdFunctionInstanceEntity)entities.get("function_instance"); 
    
    adFunctionInstance.getEquipment().addDeletedAdFunctionInstance(adFunctionInstance);
  }
  
  @Override
  public String getRemediationDescription() {
    return "Delete AD function function instance";
  }
}
