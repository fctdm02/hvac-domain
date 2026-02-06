//@formatter:off
package com.djt.hvac.domain.model.dictionary.template.v3.function.instance.inputpoint;

import java.util.Map;
import java.util.TreeMap;

import com.djt.hvac.domain.model.dictionary.template.v3.function.inputpoint.DoubleAdFunctionTemplateInputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.v3.function.instance.AdEngineAdFunctionInstanceEntity;

public class DoubleAdEngineAdFunctionInstanceInputPointEntity extends AbstractAdEngineAdFunctionInstanceInputPointEntity {
  private static final long serialVersionUID = 1L;
  
  private Map<Long, Double> values;
  
  public DoubleAdEngineAdFunctionInstanceInputPointEntity(
      AdEngineAdFunctionInstanceEntity parentAdFunctionInstance, 
      DoubleAdFunctionTemplateInputPointEntity adFunctionTemplateInputPoint,
      String metricId,
      Integer subscript) {
    super(
        parentAdFunctionInstance,
        adFunctionTemplateInputPoint,
        metricId,
        subscript);
  }
  
  public void addValue(Long timestamp, Double value) {

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
  
  public Map<Long, Double> getValues() {
    
    return values;
  }  
}