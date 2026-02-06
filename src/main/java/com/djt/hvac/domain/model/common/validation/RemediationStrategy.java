package com.djt.hvac.domain.model.common.validation;

import java.util.Map;

/**
 * 
 * @author tommyers
 *
 */
public interface RemediationStrategy {

  /**
   * 
   * @return A brief description of the remediation
   */
  String getRemediationDescription();

  /**
   * 
   * @param entities
   */
  void remediate(Map<String, Object> entities);
}
