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
import com.djt.hvac.domain.model.dictionary.template.v3.function.AdFunctionTemplateOutputPointEntity;

public class AdEngineAdFunctionInstanceOutputPointEntity extends AbstractEntity {
  private static final long serialVersionUID = 1L;
  
  private final AdEngineAdFunctionInstanceEntity parentAdFunctionInstance; 
  private final AdFunctionTemplateOutputPointEntity adFunctionTemplateOutputPoint;
  private final String metricId;
  private Map<Long, String> values;
  
  public AdEngineAdFunctionInstanceOutputPointEntity(
      AdEngineAdFunctionInstanceEntity parentAdFunctionInstance, 
      AdFunctionTemplateOutputPointEntity adFunctionTemplateOutputPoint,
      String metricId) {
    requireNonNull(parentAdFunctionInstance, "parentAdFunctionInstance cannot be null");
    requireNonNull(adFunctionTemplateOutputPoint, "adFunctionTemplateOutputPoint cannot be null");
    requireNonNull(metricId, "point cannot be null");
    this.parentAdFunctionInstance = parentAdFunctionInstance;
    this.adFunctionTemplateOutputPoint = adFunctionTemplateOutputPoint;
    this.metricId = metricId;
  }
  
  public AdEngineAdFunctionInstanceEntity getParentAdFunctionInstance() {
    return parentAdFunctionInstance;
  }

  public AdFunctionTemplateOutputPointEntity getAdFunctionTemplateOutputPoint() {
    return adFunctionTemplateOutputPoint;
  }

  public String getMetricId() {
    return metricId;
  }
  
  public void addValue(Long timestamp, String value) {
    
    if (values == null) {
      values = new TreeMap<>();
    }
    values.put(timestamp, value);
  }
  
  public Map<Long, String> getValues() {
    
    return values;
  }
  
  @Override
  public String getNaturalIdentity() {
    
    return new StringBuilder()
        .append(parentAdFunctionInstance.getNaturalIdentity())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(adFunctionTemplateOutputPoint.getSequenceNumber())
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