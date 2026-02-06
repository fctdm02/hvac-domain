package com.djt.hvac.domain.model.function;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.TreeMap;

import com.djt.hvac.domain.model.common.AbstractAssociativeEntity;
import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.dictionary.enums.DataType;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateInputConstantEntity;

public class AdFunctionInstanceInputConstantEntity extends AbstractAssociativeEntity {
  private static final long serialVersionUID = 1L;
  private final AbstractAdFunctionInstanceEntity parentAdFunctionInstance; 
  private final AdFunctionTemplateInputConstantEntity adFunctionTemplateInputConstant;
  private String value;
  
  public AdFunctionInstanceInputConstantEntity(
      AbstractAdFunctionInstanceEntity parentAdFunctionInstance, 
      AdFunctionTemplateInputConstantEntity adFunctionTemplateInputConstant,
      String value) {
    requireNonNull(parentAdFunctionInstance, "parentAdFunctionInstance cannot be null");
    requireNonNull(adFunctionTemplateInputConstant, "adFunctionTemplateInputConstant cannot be null");
    requireNonNull(value, "value cannot be null");
    this.parentAdFunctionInstance = parentAdFunctionInstance;
    this.adFunctionTemplateInputConstant = adFunctionTemplateInputConstant;
    this.value = value;
  }
  
  public Map<String, Integer> getParentIdentities() {
    
    Map<String, Integer> parentIdentities = new TreeMap<>();
    parentIdentities.put("adFunctionInstanceId", parentAdFunctionInstance.getPersistentIdentity());
    parentIdentities.put("adFunctionTemplateInputConstantId", adFunctionTemplateInputConstant.getPersistentIdentity());
    return parentIdentities;
  }
  
  public AbstractAdFunctionInstanceEntity getParentAdFunctionInstance() {
    return parentAdFunctionInstance;
  }

  public AdFunctionTemplateInputConstantEntity getAdFunctionTemplateInputConstant() {
    return adFunctionTemplateInputConstant;
  }

  public String getValue() {
    return value;
  }
  
  public void setValue(String value) {
    
    if (value == null || value.isEmpty()) {
      throw new IllegalArgumentException("Cannot set a null/empty value for input constant: ["
          + getNaturalIdentity()
          + "]");
    }
    
    // RP-12875: Ensure that the value is valid for the data type associated with the template constant.
    DataType dataType = adFunctionTemplateInputConstant.getDataType();
    if (dataType.equals(DataType.NUMERIC)) {
      
      try {
        Double.parseDouble(value);
      } catch (NumberFormatException nfe) {
        throw new IllegalArgumentException("Cannot set a non-numeric value: ["
            + value 
            + "] for input constant: ["
            + getNaturalIdentity()
            + "]");
      }
      
    } else if (dataType.equals(DataType.BOOLEAN)) {
      
      if (!value.equalsIgnoreCase("true") 
          && !value.equalsIgnoreCase("false")
          && !value.equalsIgnoreCase("1.0")
          && !value.equalsIgnoreCase("0.0")
          && !value.equalsIgnoreCase("1")
          && !value.equalsIgnoreCase("0")) {

        throw new IllegalArgumentException("Cannot set a non-boolean value: ["
            + value 
            + "] for input constant: ["
            + getNaturalIdentity()
            + "]");
      }
      
    }
    
    if (!this.value.equals(value)) {

      this.value = value;
      setIsModified("value");
      parentAdFunctionInstance.setIsModified("inputConstant:value:changed");
    }    
  }
  
  @Override
  public String toString() {
    
    return new StringBuilder()
        .append(parentAdFunctionInstance.getNaturalIdentity())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(adFunctionTemplateInputConstant.getName())
        .toString();
  }
}
