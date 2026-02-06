package com.djt.hvac.domain.model.function;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.TreeMap;

import com.djt.hvac.domain.model.common.AbstractAssociativeEntity;
import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateInputPointEntity;

public class AdFunctionInstanceInputPointEntity extends AbstractAssociativeEntity {
  private static final long serialVersionUID = 1L;
  private final AbstractAdFunctionInstanceEntity parentAdFunctionInstance; 
  private final AdFunctionTemplateInputPointEntity adFunctionTemplateInputPoint;
  private final AdFunctionInstanceEligiblePoint point;
  private Integer subscript;
  private Map<Long, String> values;
  
  public AdFunctionInstanceInputPointEntity(
      AbstractAdFunctionInstanceEntity parentAdFunctionInstance, 
      AdFunctionTemplateInputPointEntity adFunctionTemplateInputPoint,
      AdFunctionInstanceEligiblePoint point,
      Integer subscript) {
    requireNonNull(parentAdFunctionInstance, "parentAdFunctionInstance cannot be null");
    requireNonNull(adFunctionTemplateInputPoint, "adFunctionTemplateInputPoint cannot be null");
    requireNonNull(point, "point cannot be null");
    requireNonNull(subscript, "subscript cannot be null");
    this.parentAdFunctionInstance = parentAdFunctionInstance;
    this.adFunctionTemplateInputPoint = adFunctionTemplateInputPoint;
    this.point = point;
    this.subscript = subscript;
  }
  
  public Map<String, Integer> getParentIdentities() {
    
    Map<String, Integer> parentIdentities = new TreeMap<>();
    parentIdentities.put("adFunctionInstanceId", parentAdFunctionInstance.getPersistentIdentity());
    parentIdentities.put("adFunctionTemplateInputPointId", adFunctionTemplateInputPoint.getPersistentIdentity());
    parentIdentities.put("pointId", point.getPersistentIdentity());
    return parentIdentities;
  }
  
  public AbstractAdFunctionInstanceEntity getParentAdFunctionInstance() {
    return parentAdFunctionInstance;
  }

  public AdFunctionTemplateInputPointEntity getAdFunctionTemplateInputPoint() {
    return adFunctionTemplateInputPoint;
  }

  public AdFunctionInstanceEligiblePoint getPoint() {
    return point;
  }

  void incrementSubscript() {
    subscript = Integer.valueOf(subscript.intValue() + 1);
  }
  
  public Integer getSubscript() {
    return subscript;
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
        .append(adFunctionTemplateInputPoint.getName())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(point.getNaturalIdentity())
        .toString();
  }
}
