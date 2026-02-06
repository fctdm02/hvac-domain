//@formatter:off
package com.djt.hvac.domain.model.dictionary.template.v3.function.instance;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Set;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.template.v3.function.AdFunctionTemplateInputConstantEntity;

public class AdEngineAdFunctionInstanceInputConstantEntity extends AbstractEntity {
  private static final long serialVersionUID = 1L;
  
  private final AdEngineAdFunctionInstanceEntity parentAdFunctionInstance; 
  private final AdFunctionTemplateInputConstantEntity adFunctionTemplateInputConstant;
  private String value;
  
  public AdEngineAdFunctionInstanceInputConstantEntity(
      AdEngineAdFunctionInstanceEntity parentAdFunctionInstance, 
      AdFunctionTemplateInputConstantEntity adFunctionTemplateInputConstant,
      String value) {
    requireNonNull(parentAdFunctionInstance, "parentAdFunctionInstance cannot be null");
    requireNonNull(adFunctionTemplateInputConstant, "adFunctionTemplateInputConstant cannot be null");
    requireNonNull(value, "value cannot be null");
    this.parentAdFunctionInstance = parentAdFunctionInstance;
    this.adFunctionTemplateInputConstant = adFunctionTemplateInputConstant;
    this.value = value;
  }
  
  public AdEngineAdFunctionInstanceEntity getParentAdFunctionInstance() {
    return parentAdFunctionInstance;
  }

  public AdFunctionTemplateInputConstantEntity getAdFunctionTemplateInputConstant() {
    return adFunctionTemplateInputConstant;
  }

  public String getValue() {
    return value;
  }
  
  public void setValue(String value) {
    
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException("Cannot set a null/empty value for input constant: ["
          + getNaturalIdentity()
          + "]");
    }
    
    if (!this.value.equals(value)) {

      this.value = value;
      setIsModified("value");
      parentAdFunctionInstance.setIsModified("inputConstant:value:changed");
    }    
  }
  
  @Override
  public String getNaturalIdentity() {
    
    return new StringBuilder()
        .append(parentAdFunctionInstance.getNaturalIdentity())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(adFunctionTemplateInputConstant.getName())
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