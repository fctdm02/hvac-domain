package com.djt.hvac.domain.model.dictionary.template.function;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Set;

import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.enums.FunctionType;

public class AdFunctionEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  
  private final String name;
  private final String description;
  private final FunctionType functionType;
  
  public AdFunctionEntity(
      Integer persistentIdentity,
      String name,
      String description,
      FunctionType functionType) {
    super(persistentIdentity);
    requireNonNull(name, "name cannot be null");
    requireNonNull(description, "description cannot be null");
    requireNonNull(functionType, "functionType cannot be null");
    this.name = name;
    this.description = description;
    this.functionType = functionType;
  }
  
  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public FunctionType getFunctionType() {
    return functionType;
  }
  
  public String getNaturalIdentity() {
    return name;
  }  
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
  }
  
  public String getSignature() {
    
    return new StringBuilder()
        .append("functionName=")
        .append(name)
        .append(", functionDescription=")
        .append(description)
        .toString();
  }
}
