package com.djt.hvac.domain.model.nodehierarchy.validation;

import java.util.Map;

import com.djt.hvac.domain.model.common.validation.RemediationStrategy;
import com.djt.hvac.domain.model.nodehierarchy.point.AbstractPointEntity;

public final class DeleteTagsStrategyImpl extends AbstractPortfolioRemediationStrategy {
  
  private static final RemediationStrategy INSTANCE = new DeleteTagsStrategyImpl();
  
  public static RemediationStrategy get() {
    return INSTANCE;
  }

  private DeleteTagsStrategyImpl() {
  }
  
  @Override
  public void remediate(Map<String, Object> entities) {
    
    AbstractPointEntity point = (AbstractPointEntity)entities.get("point");  
    
    point.removeHaystackTags();
  }

  @Override
  public String getRemediationDescription() {
    return "Delete haystack tags for point";
  }  
}
