//@formatter:off
package com.djt.hvac.domain.model.dictionary.template.v3.function.instance.inputpoint;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Set;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.template.v3.function.inputpoint.AbstractAdFunctionTemplateInputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.v3.function.instance.AdEngineAdFunctionInstanceEntity;

public abstract class AbstractAdEngineAdFunctionInstanceInputPointEntity extends AbstractEntity {
  private static final long serialVersionUID = 1L;
  
  private final AdEngineAdFunctionInstanceEntity parentAdFunctionInstance; 
  private final AbstractAdFunctionTemplateInputPointEntity adFunctionTemplateInputPoint;
  private final String metricId;
  private final Integer subscript;
  
  public AbstractAdEngineAdFunctionInstanceInputPointEntity(
      AdEngineAdFunctionInstanceEntity parentAdFunctionInstance, 
      AbstractAdFunctionTemplateInputPointEntity adFunctionTemplateInputPoint,
      String metricId,
      Integer subscript) {
    requireNonNull(parentAdFunctionInstance, "parentAdFunctionInstance cannot be null");
    requireNonNull(adFunctionTemplateInputPoint, "adFunctionTemplateInputPoint cannot be null");
    requireNonNull(metricId, "metricId cannot be null");
    requireNonNull(subscript, "subscript cannot be null");
    this.parentAdFunctionInstance = parentAdFunctionInstance;
    this.adFunctionTemplateInputPoint = adFunctionTemplateInputPoint;
    this.metricId = metricId;
    this.subscript = subscript;
  }
  
  public AdEngineAdFunctionInstanceEntity getParentAdFunctionInstance() {
    return parentAdFunctionInstance;
  }

  public AbstractAdFunctionTemplateInputPointEntity getAdFunctionTemplateInputPoint() {
    return adFunctionTemplateInputPoint;
  }

  public String getMetricId() {
    return metricId;
  }
  
  public Integer getSubscript() {
    return subscript;
  }
  
  @Override
  public String getNaturalIdentity() {
    
    return new StringBuilder()
        .append(parentAdFunctionInstance.getNaturalIdentity())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(adFunctionTemplateInputPoint.getName())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(metricId)
        .toString();
  }
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
  }
}
//@formatter:on