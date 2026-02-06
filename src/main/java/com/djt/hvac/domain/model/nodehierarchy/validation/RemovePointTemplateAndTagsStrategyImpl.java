package com.djt.hvac.domain.model.nodehierarchy.validation;

import java.util.Map;

import com.djt.hvac.domain.model.common.validation.RemediationStrategy;
import com.djt.hvac.domain.model.nodehierarchy.point.AbstractPointEntity;

public final class RemovePointTemplateAndTagsStrategyImpl extends AbstractPortfolioRemediationStrategy {

  private static final RemediationStrategy INSTANCE = new RemovePointTemplateAndTagsStrategyImpl();
  
  public static RemediationStrategy get() {
    return INSTANCE;
  }

  private RemovePointTemplateAndTagsStrategyImpl() {
  }
  
  @Override
  public void remediate(Map<String, Object> entities) {
    
    AbstractPointEntity point = (AbstractPointEntity)entities.get("point");
    
    point.removeHaystackTags();
    point.setPointTemplate(null);
  }
  
  @Override
  public String getRemediationDescription() {
    return "Remove point template and haystack tags";
  }  
}
