package com.djt.hvac.domain.model.common.validation;

import java.util.Map;

public final class NoOpResolutionStrategyImpl extends AbstractRemediationStrategy {
  
  private static final RemediationStrategy INSTANCE = new NoOpResolutionStrategyImpl();
  
  public static RemediationStrategy get() {
    return INSTANCE;
  }

  private NoOpResolutionStrategyImpl() {
  }

  @Override
  public void remediate(Map<String, Object> entities) {
  }
  
  @Override
  public String getRemediationDescription() {
    return "";
  }  
}
