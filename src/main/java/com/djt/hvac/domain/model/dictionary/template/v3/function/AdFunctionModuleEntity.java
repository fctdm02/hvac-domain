//@formatter:off
package com.djt.hvac.domain.model.dictionary.template.v3.function;

import static java.util.Objects.requireNonNull;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.enums.FunctionType;
import com.djt.hvac.domain.model.dictionary.template.v3.function.inputpoint.AdFunctionTemplateInputPointGroupEntity;

public class AdFunctionModuleEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  
  private final String name;
  private final String displayName;
  private final String description;
  private final FunctionType functionType;
  private final Set<AdFunctionTemplateInputPointGroupEntity> inputPointGroups = new TreeSet<>();
  private final Set<AdFunctionTemplateInputConstantEntity> inputConstants = new TreeSet<>();
  private final Set<AdFunctionTemplateOutputPointEntity> outputPoints = new TreeSet<>();
  
  /**
   * 
   * From Derek:
   * <pre>
   * so for the interface, I want to make the actual code less verbose and have less boilerplate.  I want to externalize "delay" (RE engine code will handle it), 
   * get rid of string parameter keys (hashmap) for iparams and constants (1), change the way generics are (mis)utilized, externalize "coercion" (2) and allow 
   * the function module some control over the 15 minutes downsample on input data (3)
   * 
   * ok following up on above topic on the java interface around adfunctions...
The current interface would remain supported as "Class A" functions
new classes would be added.  some of them may be more restricted, simple, and optimized e.g. the use case with only one input parameter, or a stateless function module , certain use cases that can be optimized around.
for the footnotes above
(1) replace string keys and HashMaps with Enums and EnumMaps for iparams.  each function module can provide a nested Enum for iparams and another one for constants.  enum values can have properties for optional, array, etc. we can then add an integration test somewhere that goes through the function module Enum classes and compares with the rule templates in the databases to ensure there is an exact match, warn if an enum is never used, etc.
(2) the function module is able to tell the rule engine what coersion rules it should use for each input parameter, and then when the function is called, it is guaranteed to get that type and does not need to coerce
(3) the function module is able to provide a rule for roll up on input data, with "last seen value 15 minute" being the standard.  this allows for further optimization in the RE, and support for a type of rule that requires greater than 15 minute sensitivity
actually 3 could be added later
   * </pre>
   * @param persistentIdentity
   * @param name
   * @param description
   * @param functionType
   */
  public AdFunctionModuleEntity(
      Integer persistentIdentity,
      String name,
      String description,
      FunctionType functionType) {
    super(persistentIdentity);
    requireNonNull(name, "name cannot be null");
    requireNonNull(description, "description cannot be null");
    requireNonNull(functionType, "functionType cannot be null");
    name = name.replace(" ", "_");
    this.name = name;
    this.displayName = name.replace("_", " ");
    this.description = description;
    this.functionType = functionType;
  }
  
  public String getName() {
    return name;
  }
  
  public String getDisplayName() {
    return displayName;
  }

  public String getDescription() {
    return description;
  }

  public FunctionType getFunctionType() {
    return functionType;
  }
  
  public Set<AdFunctionTemplateInputPointGroupEntity> getInputPointGroups() {
    return inputPointGroups;
  }
  
  public boolean addInputPointGroup(AdFunctionTemplateInputPointGroupEntity inputPointGroup) throws EntityAlreadyExistsException {
    return addChild(inputPointGroups, inputPointGroup, this);
  }
  
  public AdFunctionTemplateInputPointGroupEntity getInputPointGroup(Integer inputPointGroupId) {
    
    Iterator<AdFunctionTemplateInputPointGroupEntity> iterator = inputPointGroups.iterator();
    while (iterator.hasNext()) {
      AdFunctionTemplateInputPointGroupEntity inputPointGroup = iterator.next();
      if (inputPointGroup.getPersistentIdentity().equals(inputPointGroupId)) {
        return inputPointGroup;
      }
    }
    throw new IllegalStateException("AD function input point group with id: "
        + inputPointGroupId
        + " not found.");
  }  

  public AdFunctionTemplateInputPointGroupEntity getInputPointGroup(String inputPointGroupName) {
    
    Iterator<AdFunctionTemplateInputPointGroupEntity> iterator = inputPointGroups.iterator();
    while (iterator.hasNext()) {
      AdFunctionTemplateInputPointGroupEntity inputPointGroup = iterator.next();
      if (inputPointGroup.getName().equals(inputPointGroupName)) {
        return inputPointGroup;
      }
    }
    throw new IllegalStateException("AD function input point group with name: ["
        + inputPointGroupName
        + "] not found in AD function template: ["
        + this.getNaturalIdentity()
        + "]");
  }  
  
  public boolean addInputConstant(AdFunctionTemplateInputConstantEntity inputConstant) throws EntityAlreadyExistsException {
    return addChild(inputConstants, inputConstant, this);
  }
  
  public Set<AdFunctionTemplateInputConstantEntity> getInputConstants() {
    return inputConstants;
  }

  public AdFunctionTemplateInputConstantEntity getInputConstant(Integer inputConstantId) {
    
    Iterator<AdFunctionTemplateInputConstantEntity> iterator = inputConstants.iterator();
    while (iterator.hasNext()) {
      AdFunctionTemplateInputConstantEntity inputConstant = iterator.next();
      if (inputConstant.getPersistentIdentity().equals(inputConstantId)) {
        return inputConstant;
      }
    }
    throw new IllegalStateException("AD function template input constant with id: "
        + inputConstantId
        + " not found.");
  }  

  public AdFunctionTemplateInputConstantEntity getInputConstant(String inputConstantName) {
    
    Iterator<AdFunctionTemplateInputConstantEntity> iterator = inputConstants.iterator();
    while (iterator.hasNext()) {
      AdFunctionTemplateInputConstantEntity inputConstant = iterator.next();
      if (inputConstant.getName().equals(inputConstantName)) {
        return inputConstant;
      }
    }
    return null;
  }
  
  public AdFunctionTemplateOutputPointEntity getOutputPoint(Integer outputPointId) {
    
    Iterator<AdFunctionTemplateOutputPointEntity> iterator = outputPoints.iterator();
    while (iterator.hasNext()) {
      AdFunctionTemplateOutputPointEntity outputPoint = iterator.next();
      if (outputPoint.getPersistentIdentity().equals(outputPointId)) {
        return outputPoint;
      }
    }
    throw new IllegalStateException("AD function template output point with id: "
        + outputPointId
        + " not found.");
  }
  
  public boolean addOutputPoint(AdFunctionTemplateOutputPointEntity outputPoint) throws EntityAlreadyExistsException {
    return addChild(outputPoints, outputPoint, this);
  }
  
  public Set<AdFunctionTemplateOutputPointEntity> getOutputPoints() {
    return outputPoints;
  }
  
  @Override
  public String getNaturalIdentity() {
    return name;
  }  
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
  }
  
  public void normalizeFaultExpressionTemplates() {
    
    for (AdFunctionTemplateInputPointGroupEntity inputPointGroup: inputPointGroups) {
      
      inputPointGroup.normalizeFaultExpressionTemplate();
    }
  }
}
//@formatter:on