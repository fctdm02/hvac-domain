package com.djt.hvac.domain.model.function;

import static java.util.Objects.requireNonNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import com.djt.hvac.domain.model.common.AbstractAssociativeEntity;
import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateOutputPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.AsyncComputedPointEntity;

public class AdFunctionInstanceOutputPointEntity extends AbstractAssociativeEntity {
  private static final long serialVersionUID = 1L;
  private final AbstractAdFunctionInstanceEntity parentAdFunctionInstance; 
  private final AdFunctionTemplateOutputPointEntity adFunctionTemplateOutputPoint;
  private final AsyncComputedPointEntity point;
  private Map<Long, String> values;
  
  public AdFunctionInstanceOutputPointEntity(
      AbstractAdFunctionInstanceEntity parentAdFunctionInstance, 
      AdFunctionTemplateOutputPointEntity adFunctionTemplateOutputPoint,
      AsyncComputedPointEntity point) {
    requireNonNull(parentAdFunctionInstance, "parentAdFunctionInstance cannot be null");
    requireNonNull(adFunctionTemplateOutputPoint, "adFunctionTemplateOutputPoint cannot be null");
    requireNonNull(point, "point cannot be null");
    this.parentAdFunctionInstance = parentAdFunctionInstance;
    this.adFunctionTemplateOutputPoint = adFunctionTemplateOutputPoint;
    this.point = point;
  }
  
  public Map<String, Integer> getParentIdentities() {
    
    Map<String, Integer> parentIdentities = new LinkedHashMap<>();
    parentIdentities.put("adFunctionInstanceId", parentAdFunctionInstance.getPersistentIdentity());
    parentIdentities.put("adFunctionTemplateOutputPointId", adFunctionTemplateOutputPoint.getPersistentIdentity());
    parentIdentities.put("pointId", point.getPersistentIdentity());
    return parentIdentities;
  }
  
  public AbstractAdFunctionInstanceEntity getParentAdFunctionInstance() {
    return parentAdFunctionInstance;
  }

  public AdFunctionTemplateOutputPointEntity getAdFunctionTemplateOutputPoint() {
    return adFunctionTemplateOutputPoint;
  }

  public AsyncComputedPointEntity getPoint() {
    return point;
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
  public String toString() {
    
    return new StringBuilder()
        .append(parentAdFunctionInstance.getNaturalIdentity())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(adFunctionTemplateOutputPoint.getSequenceNumber())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(point.getDisplayName())
        .toString();
  }
}
