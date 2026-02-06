package com.djt.hvac.domain.model.nodehierarchy.validation;

import java.util.Map;

import com.djt.hvac.domain.model.common.validation.RemediationStrategy;
import com.djt.hvac.domain.model.nodehierarchy.point.AbstractPointEntity;

public final class HardDeleteNodeStrategyImpl extends AbstractPortfolioRemediationStrategy {
  
  private static final RemediationStrategy INSTANCE = new HardDeleteNodeStrategyImpl();
  
  public static RemediationStrategy get() {
    return INSTANCE;
  }

  private HardDeleteNodeStrategyImpl() {
    super();
  }
  
  @Override
  public void remediate(Map<String, Object> entities) {
    
    AbstractPointEntity point = (AbstractPointEntity)entities.get("point");  
    
    point.getParentNode().setIsModified("childPoint:deleted");
    point.setIsDeleted();
  }
  
  @Override
  public String getRemediationDescription() {
    return "Hard delete point";
  }  
}
