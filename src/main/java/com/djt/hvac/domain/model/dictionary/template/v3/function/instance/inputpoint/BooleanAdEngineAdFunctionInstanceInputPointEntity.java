//@formatter:off
package com.djt.hvac.domain.model.dictionary.template.v3.function.instance.inputpoint;

import java.util.Map;
import java.util.TreeMap;

import com.djt.hvac.domain.model.dictionary.template.v3.function.inputpoint.BooleanAdFunctionTemplateInputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.v3.function.instance.AdEngineAdFunctionInstanceEntity;

public class BooleanAdEngineAdFunctionInstanceInputPointEntity extends AbstractAdEngineAdFunctionInstanceInputPointEntity {
  private static final long serialVersionUID = 1L;
  
  private Map<Long, Boolean> values;
  
  public BooleanAdEngineAdFunctionInstanceInputPointEntity(
      AdEngineAdFunctionInstanceEntity parentAdFunctionInstance, 
      BooleanAdFunctionTemplateInputPointEntity adFunctionTemplateInputPoint,
      String metricId,
      Integer subscript) {
    super(
        parentAdFunctionInstance,
        adFunctionTemplateInputPoint,
        metricId,
        subscript);
  }
  
  public void addValue(Long timestamp, Boolean value) {

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
    
    values.put(timestamp, value);
  }
  
  public Map<Long, Boolean> getValues() {
    
    return values;
  }  
}