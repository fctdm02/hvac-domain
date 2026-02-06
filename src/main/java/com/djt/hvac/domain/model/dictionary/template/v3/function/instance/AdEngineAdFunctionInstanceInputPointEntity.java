//@formatter:off
package com.djt.hvac.domain.model.dictionary.template.v3.function.instance;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.template.v3.function.inputpoint.AbstractAdFunctionTemplateInputPointEntity;

public class AdEngineAdFunctionInstanceInputPointEntity extends AbstractEntity {
  private static final long serialVersionUID = 1L;
  
  private final AdEngineAdFunctionInstanceEntity parentAdFunctionInstance; 
  private final AbstractAdFunctionTemplateInputPointEntity adFunctionTemplateInputPoint;
  private final String metricId;
  private final Integer subscript;
  private Map<Long, String> values;
  
  public AdEngineAdFunctionInstanceInputPointEntity(
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

  public void addValue(Long timestamp, Object value) {

    if (timestamp == null) {
      throw new IllegalArgumentException("timestamp cannot be null");
    }
    
    if (value == null) {
      throw new IllegalArgumentException("value cannot be null");
    }
    
    if (values == null) {
      values = new TreeMap<>();
    }
    
    String s = value.toString();
    if (s.isEmpty()) {
      throw new IllegalArgumentException("value cannot be empty");
    }
    
    values.put(timestamp, s);
  }
  
  public Map<Long, String> getValues() {
    
    return values;
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