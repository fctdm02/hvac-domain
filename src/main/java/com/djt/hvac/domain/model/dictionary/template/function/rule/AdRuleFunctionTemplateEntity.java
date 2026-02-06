package com.djt.hvac.domain.model.dictionary.template.function.rule;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.SimpleValidationMessage;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.common.validation.SimpleValidationMessage.MessageType;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.container.NodeTagTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.dto.function.rule.arms.InputConst;
import com.djt.hvac.domain.model.dictionary.dto.function.rule.arms.InputPoint;
import com.djt.hvac.domain.model.dictionary.dto.function.rule.arms.Rule;
import com.djt.hvac.domain.model.dictionary.energyexchange.AbstractEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AbstractAdFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateInputConstantEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateInputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;

public class AdRuleFunctionTemplateEntity extends AbstractAdFunctionTemplateEntity {
  private static final long serialVersionUID = 1L;
  
  private final String faultNumber;
  
  public AdRuleFunctionTemplateEntity(
      Integer persistentIdentity,
      AdFunctionEntity adFunction,
      String name,
      String displayName,
      String description,
      AbstractEnergyExchangeTypeEntity energyExchangeType,
      String nodeFilterExpression,
      String tupleConstraintExpression,
      Boolean isBeta,
      Integer version,
      String faultNumber) {
    super(
        persistentIdentity,
        adFunction,
        name,
        displayName,
        description,
        energyExchangeType,
        nodeFilterExpression,
        tupleConstraintExpression,
        isBeta,
        version);
    
    // To handle garbage test data.  Do same behavior as
    // old method to retrieve rule template hierarchy.
    if (faultNumber == null) {
      faultNumber = "3.1.1.1";
    }
    requireNonNull(faultNumber, "faultNumber cannot be null");
    this.faultNumber = faultNumber;
  }
  
  public String getFaultNumber() {
    return faultNumber;
  }
  
  public List<SimpleValidationMessage> reconcileWithRuleManagerSpec(Rule armsRule) {
    
    List<SimpleValidationMessage> messages = new ArrayList<>();
    
    NodeTagTemplatesContainer nodeTagTemplatesContainer = DictionaryContext.getNodeTagTemplatesContainer();
    //TagsContainer tagsContainer = DictionaryContext.getTagsContainer();
    
    //AbstractEnergyExchangeTypeEntity energyExchangeType = getEnergyExchangeType();
    //Set<PointTemplateEntity> entityPointTemplates = nodeTagTemplatesContainer.getEnergyExchangeLevelPointTemplatesForEnergyExchangeType(energyExchangeType);
    
    String armsRuleName = armsRule.getName().trim();
    if (!armsRuleName.equals(getDisplayName())) {
      messages.add(new SimpleValidationMessage(
          MessageType.INFO,
          this.getFaultNumber(),
          "display name",
          "Mismatch between rule manager app name: ["
              + armsRuleName
              + "] and AD rule function template: ["
              + getDisplayName()
              + "]"
          )); 
    }
    
    String armsRuleSummary = armsRule.getSummary();
    if (!armsRuleSummary.equals(getDescription())) {
      messages.add(new SimpleValidationMessage(
          MessageType.INFO,
          this.getFaultNumber(),
          "description",
          "Mismatch between rule manager app summary: ["
              + armsRuleSummary
              + "] and AD rule function template: ["
              + getName()
              + "]"
          )); 
    }
    
    List<Integer> armsRuleEquipmentTypeIdList = armsRule.getEquipmentTypeId();
    if (armsRuleEquipmentTypeIdList == null) {
      messages.add(new SimpleValidationMessage(
          MessageType.INFO,
          this.getFaultNumber(),
          "energy exchange type",
          "Energy Exchange type id not specified in rule manager app"
          )); 
    } else if (armsRuleEquipmentTypeIdList.size() > 1) {
      messages.add(new SimpleValidationMessage(
          MessageType.INFO,
          this.getFaultNumber(),
          "energy exchange type",
          "More than one energy exchange type id specified in rule manager app: "
              + armsRuleEquipmentTypeIdList
          )); 
    } else {
      Integer armsRuleEquipmentTypeId = armsRuleEquipmentTypeIdList.get(0);
      if (!armsRuleEquipmentTypeId.equals(getEnergyExchangeType().getPersistentIdentity())) {
        
        messages.add(new SimpleValidationMessage(
            MessageType.ERROR,
            this.getFaultNumber(),
            "energy exchange type",
            "Mismatch between rule manager app energy exchange type id: ["
                + armsRuleEquipmentTypeId
                + "] and AD rule function template energy exchange type id: ["
                + getEnergyExchangeType().getPersistentIdentity()
                + "]"
            )); 
      }
    }
    
    Set<String> inputConstantIdentities = new TreeSet<>();
    Iterator<AdFunctionTemplateInputConstantEntity> inputConstantsIterator = getInputConstants().iterator();
    while (inputConstantsIterator.hasNext()) {
      inputConstantIdentities.add(inputConstantsIterator.next().getName());
    }

    Iterator<InputConst> inputConstsIterator = armsRule.getInputConsts().iterator();
    while (inputConstsIterator.hasNext()) {
      
      InputConst armsRuleInputConst = inputConstsIterator.next();
      
      String armsRuleInputConstName = armsRuleInputConst.getName();
      AdFunctionTemplateInputConstantEntity adFunctionTemplateInputConstant = getInputConstant(armsRuleInputConstName.trim());
      if (adFunctionTemplateInputConstant == null) {
        
        messages.add(new SimpleValidationMessage(
            MessageType.ERROR,
            this.getFaultNumber(),
            "constant",
            "Rule manager app constant with name: ["
                + armsRuleInputConstName
                + "] not found in rule constants: "
                + inputConstantIdentities
            )); 
        
      } else {
        
        if (!armsRuleInputConst.getLabel().equals(adFunctionTemplateInputConstant.getDescription())) {
          
          messages.add(new SimpleValidationMessage(
              MessageType.INFO,
              this.getFaultNumber() + " - " + adFunctionTemplateInputConstant.getName(),
              "constant description",
              "Mismatch between rule manager app constant description: ["
                  + armsRuleInputConst.getLabel()
                  + "] and constant: ["
                  + adFunctionTemplateInputConstant.getDescription()
                  + "]"
              )); 
        }
        
        try {
          Double armsRuleInputConstValue = Double.parseDouble(armsRuleInputConst.getDefaultValue());
          Double adRuleInputConstValue = Double.parseDouble(adFunctionTemplateInputConstant.getDefaultValue());
          if (!armsRuleInputConstValue.equals(adRuleInputConstValue)) {
            
            messages.add(new SimpleValidationMessage(
                MessageType.ERROR,
                this.getFaultNumber() + " - " + adFunctionTemplateInputConstant.getName(),
                "constant default value",
                "Mismatch between rule manager app constant default value: ["
                    + armsRuleInputConstValue
                    + "] and constant: ["
                    + adRuleInputConstValue
                    + "]"
                )); 
          }
        } catch (NumberFormatException nfe) {
          if (!armsRuleInputConst.getDefaultValue().equals(adFunctionTemplateInputConstant.getDefaultValue())) {
            
            messages.add(new SimpleValidationMessage(
                MessageType.ERROR,
                this.getFaultNumber() + " - " + adFunctionTemplateInputConstant.getName(),
                "constant default value",
                "Mismatch between rule manager app constant default value: ["
                    + armsRuleInputConst.getDefaultValue()
                    + "] and constant: ["
                    + adFunctionTemplateInputConstant.getDefaultValue()
                    + "]"
                )); 
          }
        }
      }
    }
    
    Map<String, AdFunctionTemplateInputPointEntity> adFunctionTemplateInputPoints = new HashMap<>();
    for (AdFunctionTemplateInputPointEntity inputPoint: getInputPoints()) {
      adFunctionTemplateInputPoints.put(inputPoint.getNormalizedTagsAsSet().toString(), inputPoint);
    }
    
    Iterator<InputPoint> ruleInputPointsIterator = armsRule.getInputPoints().iterator();
    while (ruleInputPointsIterator.hasNext()) {
      
      InputPoint armsRuleInputPoint = ruleInputPointsIterator.next();
      
      Integer armsRuleInputPointTemplateId = armsRuleInputPoint.getPointTemplateId();
      PointTemplateEntity armsRuleInputPointTemplate = nodeTagTemplatesContainer.getPointTemplateNullIfNotExists(armsRuleInputPointTemplateId);
      
      // We can only do analysis when the current object expression is null.
      AdFunctionTemplateInputPointEntity adFunctionTemplateInputPoint = null;
      String armsRuleInputPointHaystackTags = armsRuleInputPointTemplate.getNormalizedTagsAsSet().toString();
      if (armsRuleInputPoint.getCurrentObjectExpression() == null) {
        
        adFunctionTemplateInputPoint = adFunctionTemplateInputPoints.get(armsRuleInputPointHaystackTags);
        
        if (adFunctionTemplateInputPoint == null) {

          messages.add(new SimpleValidationMessage(
              MessageType.ERROR,
              this.getFaultNumber(),
              "input points",
              "Input point with tags: "
                  + armsRuleInputPointHaystackTags
                  + " corresponding to point template: ["
                  + armsRuleInputPointTemplate
                  + "] not found in rule input points"
              ));
          
        } else {
          /*
          AbstractEnergyExchangeTypeEntity et = energyExchangeType;
          Set<PointTemplateEntity> pointTemplates = entityPointTemplates;
          String thisCurrentObjectExpression = adFunctionTemplateInputPoint.getCurrentObjectExpression();
          if (thisCurrentObjectExpression != null && thisCurrentObjectExpression.equals("childEquipment(tags=vav)")) {
            
            et = tagsContainer.getEnergyExchangeTypeByName("vav");
            pointTemplates = nodeTagTemplatesContainer.getEnergyExchangeLevelPointTemplatesForEnergyExchangeType(et);
            
          } else if (thisCurrentObjectExpression != null && thisCurrentObjectExpression.equals("parentEquipment()")) {

            et = tagsContainer.getEnergyExchangeTypeByName("ahu");
            pointTemplates = nodeTagTemplatesContainer.getEnergyExchangeLevelPointTemplatesForEnergyExchangeType(et);
            
          }
          
          if (!pointTemplates.contains(armsRuleInputPointTemplate)) {
            
            messages.add(new SimpleValidationMessage(
                MessageType.ERROR,
                this.getFaultNumber() + " - " + adFunctionTemplateInputPoint.getName(),
                "input point tags",
                "CRITICAL! Point template: ["
                    + armsRuleInputPointTemplate
                    + "] is invalid for energy exchange type: ["
                    + et
                    + "] with curr. obj. expr: ["
                    + thisCurrentObjectExpression
                    + "]"
                )); 
          }        
          
          String armsRuleInputPointCurrObjExpr = armsRuleInputPoint.getCurrentObjectExpression();
          if (armsRuleInputPointCurrObjExpr == null) {
            armsRuleInputPointCurrObjExpr = "";
          } else {
            armsRuleInputPointCurrObjExpr = armsRuleInputPointCurrObjExpr.trim();
          }
          String entityCurrObjExpr = adFunctionTemplateInputPoint.getCurrentObjectExpression();
          if (entityCurrObjExpr == null) {
            entityCurrObjExpr = "";
          } else {
            entityCurrObjExpr = entityCurrObjExpr.trim();
          }
          if (!armsRuleInputPointCurrObjExpr.equals(entityCurrObjExpr)) {
            
            messages.add(new SimpleValidationMessage(
                MessageType.ERROR,
                this.getFaultNumber() + " - " + adFunctionTemplateInputPoint.getName(),
                "input point curr object expr",
                "Mismatch between rule manager app input point: ["
                    + armsRuleInputPointCurrObjExpr
                    + "] and input point: ["
                    + entityCurrObjExpr
                    + "]"
                )); 
          }

          String armsRuleInputPointIsRequired = armsRuleInputPoint.getRequired().toString();
          String entityIsRequired = adFunctionTemplateInputPoint.getIsRequired().toString();
          if (!armsRuleInputPointIsRequired.equals(entityIsRequired)) {
            
            messages.add(new SimpleValidationMessage(
                MessageType.ERROR,
                this.getFaultNumber() + " - " + adFunctionTemplateInputPoint.getName(),
                "input point is required",
                "Mismatch between rule manager app input point: ["
                    + armsRuleInputPointIsRequired
                    + "] and input point: ["
                    + entityIsRequired
                    + "]"
                )); 
          }
          
          String armsRuleInputPointIsArray = armsRuleInputPoint.getArray().toString();
          String entityIsArray = adFunctionTemplateInputPoint.getIsArray().toString();
          if (!armsRuleInputPointIsArray.equals(entityIsArray)) {
            
            messages.add(new SimpleValidationMessage(
                MessageType.ERROR,
                this.getFaultNumber() + " - " + adFunctionTemplateInputPoint.getName(),
                "input point is array",
                "Mismatch between rule manager app input point: ["
                    + armsRuleInputPointIsArray
                    + "] and input point: ["
                    + entityIsArray
                    + "]"
                )); 
          }
          */
        }
      }
    }
    return messages;
  }

  @Override
  public String getNaturalIdentity() {
   
    return new StringBuilder()
        .append(faultNumber)
        .append(" ")
        .append(getName())
        .toString();
  }
  
  @Override
  public String getFaultOrReferenceNumber() {
    return faultNumber;
  }
  
  @Override
  public String getFullDisplayName() {

    return new StringBuilder()
        .append(faultNumber)
        .append(" ")
        .append(getDisplayName())
        .toString();
  }
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
    
    // NOTHING TO DO
  }
}
