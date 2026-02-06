//@formatter:off
package com.djt.hvac.domain.model.dictionary.container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.common.validation.SimpleValidationMessage;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.dto.AdFunctionTemplateInputConstantPointTemplateMappingDto;
import com.djt.hvac.domain.model.dictionary.dto.function.AdFunctionTemplateDto;
import com.djt.hvac.domain.model.dictionary.dto.function.AdFunctionTemplateInputConstantDto;
import com.djt.hvac.domain.model.dictionary.dto.function.AdFunctionTemplateInputPointDto;
import com.djt.hvac.domain.model.dictionary.dto.function.AdFunctionTemplateOutputPointDto;
import com.djt.hvac.domain.model.dictionary.dto.function.DatabaseWrapperDto;
import com.djt.hvac.domain.model.dictionary.dto.function.computedpoint.AdComputedPointFunctionCategoryDto;
import com.djt.hvac.domain.model.dictionary.dto.function.computedpoint.AdComputedPointFunctionTemplateDto;
import com.djt.hvac.domain.model.dictionary.dto.function.computedpoint.AdComputedPointFunctionTemplateHierarchyDto;
import com.djt.hvac.domain.model.dictionary.dto.function.computedpoint.AdComputedPointFunctionTemplateInputConstant;
import com.djt.hvac.domain.model.dictionary.dto.function.computedpoint.AdComputedPointFunctionTemplateInputPoint;
import com.djt.hvac.domain.model.dictionary.dto.function.computedpoint.AdComputedPointFunctionTemplateOutputPoint;
import com.djt.hvac.domain.model.dictionary.dto.function.rule.AdRuleFunctionEquipmentCategoryDto;
import com.djt.hvac.domain.model.dictionary.dto.function.rule.AdRuleFunctionSystemCategoryDto;
import com.djt.hvac.domain.model.dictionary.dto.function.rule.AdRuleFunctionTemplateDto;
import com.djt.hvac.domain.model.dictionary.dto.function.rule.AdRuleFunctionTemplateHierarchyDto;
import com.djt.hvac.domain.model.dictionary.dto.function.rule.AdRuleFunctionTemplateInputConstant;
import com.djt.hvac.domain.model.dictionary.dto.function.rule.AdRuleFunctionTemplateInputPoint;
import com.djt.hvac.domain.model.dictionary.dto.function.rule.AdRuleFunctionTemplateOutputPoint;
import com.djt.hvac.domain.model.dictionary.energyexchange.AbstractEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.EquipmentEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.LoopEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.PlantEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.enums.DataType;
import com.djt.hvac.domain.model.dictionary.enums.FunctionType;
import com.djt.hvac.domain.model.dictionary.template.function.AbstractAdFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateInputConstantEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateInputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateOutputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.function.computedpoint.AdComputedPointFunctionCategoryEntity;
import com.djt.hvac.domain.model.dictionary.template.function.computedpoint.AdComputedPointFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.function.rule.AdRuleFunctionEquipmentCategoryEntity;
import com.djt.hvac.domain.model.dictionary.template.function.rule.AdRuleFunctionSystemCategoryEntity;
import com.djt.hvac.domain.model.dictionary.template.function.rule.AdRuleFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.AbstractNodeTagTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;
import com.djt.hvac.domain.model.function.AdFunctionEvaluator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

public class AdFunctionTemplatesContainer {

  private static final Logger LOGGER = LoggerFactory.getLogger(AdFunctionTemplatesContainer.class);
  
  private static final Map<String, String> CURRENT_OBJECT_EXPRESSION_DESCRIPTIONS = new HashMap<>();
  static {

    CURRENT_OBJECT_EXPRESSION_DESCRIPTIONS.put("ancestor(type=building)",
        "ancestor building");

    CURRENT_OBJECT_EXPRESSION_DESCRIPTIONS.put("ancestor(type=building).child(tags=off_prem_weather_station|*)",
        "ancestor building off prem weather station");
    
    CURRENT_OBJECT_EXPRESSION_DESCRIPTIONS.put("ancestor(type=building).descendant(tags=on_prem_weather_station|*)",
        "ancestor building on prem weather station");

    CURRENT_OBJECT_EXPRESSION_DESCRIPTIONS.put("childEquipment(tags=chiller|*)",
        "child chiller (air supply system)");

    CURRENT_OBJECT_EXPRESSION_DESCRIPTIONS.put("childEquipment(tags=coolingTower|*)",
        "child cooling tower (air supply system)");

    CURRENT_OBJECT_EXPRESSION_DESCRIPTIONS.put("childEquipment(tags=vav|*)",
        "child VAV (air supply system)");

    CURRENT_OBJECT_EXPRESSION_DESCRIPTIONS.put("child(model=airSupply,type=equipment,tags=vav|*)",
        "child VAV (air supply system)");

    CURRENT_OBJECT_EXPRESSION_DESCRIPTIONS.put("descendant(model=chilledWater,type=equipment,tags=ahu|*).elseIf(select=descendant(model=chilledWater,type=equipment,tags=chilledWaterPlant|*))",
        "descendant AHU (air supply system) or descendant chilled water plant (chilled water system)");
    
    CURRENT_OBJECT_EXPRESSION_DESCRIPTIONS.put("descendant(model=chilledWater,type=equipment,tags=ahu|*).elseIf(select=descendant(model=chilledWater,type=equipment,tags=fcu|*))",
        "descendant AHU (air supply system) or descendant FCU (chilled water system)");

    CURRENT_OBJECT_EXPRESSION_DESCRIPTIONS.put("descendant(model=chilledWater,type=equipment,tags=chiller|*).elseIf(select=descendant(model=chilledWater,type=equipment,tags=chilledWaterPlant|*))",
        "descendant AHU (air supply system) or chilled water plant (chilled water system)");

    CURRENT_OBJECT_EXPRESSION_DESCRIPTIONS.put("parentEquipment()",
        "parent equipment (air supply system)");

    CURRENT_OBJECT_EXPRESSION_DESCRIPTIONS.put("parentEquipment(tags=ahu|*)",
        "parent AHU (air supply system)");
    
    CURRENT_OBJECT_EXPRESSION_DESCRIPTIONS.put("parent(model=airSupply,type=equipment)", 
        "parent equipment (air supply system)");

    CURRENT_OBJECT_EXPRESSION_DESCRIPTIONS.put("parent(model=airSupply,type=equipment,tags=ahu|*)",
        "parent AHU (air supply system)");

    CURRENT_OBJECT_EXPRESSION_DESCRIPTIONS.put("parent(model=airSupply,type=equipment,tags=hotWaterPlant|*)",
        "parent hot water plant (air supply system)");
    
    CURRENT_OBJECT_EXPRESSION_DESCRIPTIONS.put("descendant(model=chilledWater,type=equipment,tags=chiller|*)",
        "descendant chiller (chilled water system)");
    
    CURRENT_OBJECT_EXPRESSION_DESCRIPTIONS.put("child(model=airSupply,type=equipment,tags=vav|*)",
        "child VAV (air supply system)");
    
    CURRENT_OBJECT_EXPRESSION_DESCRIPTIONS.put("descendant(model=chilledWater,type=equipment,tags=chiller|*)",
        "descendant chiller (chilled water system)");
    
    CURRENT_OBJECT_EXPRESSION_DESCRIPTIONS.put("descendant(model=chilledWater,type=equipment,tags=ahu|*).elseIf(select=descendant(model=chilledWater,type=equipment,tags=fcu|*))",
        "descendant AHU (air supply system) or descendant FCU (chilled water system)");
  }
  
  private static final Map<Integer, AdFunctionEntity> AD_FUNCTIONS = new HashMap<>();
  
  private final List<AdRuleFunctionSystemCategoryEntity> adRuleFunctionSystemCategories;
  private final List<AdComputedPointFunctionCategoryEntity> adComputedPointFunctionCategories;
  
  private final Map<Integer, AdRuleFunctionTemplateEntity> adRuleFunctionTemplates;
  private final Map<Integer, AdComputedPointFunctionTemplateEntity> adComputedPointFunctionTemplates;
  
  private final Set<String> adFunctionTemplateNames = new HashSet<>();
  
  private final Map<Integer, String> adFunctionTemplateDisplayNames = new HashMap<>();
  
  private final Map<Integer, String> pointErrorMessages;
  
  private final List<String> containerAuditMessages = new ArrayList<>();
  
  private Map<Integer, AdFunctionTemplateInputConstantEntity> allAdFunctionTemplateInputConstants;
  
  // Key is AD function template input constant id, value is point template id.
  private final Map<Integer, Integer> adFunctionTemplateInputConstantPointTemplateMappings = new HashMap<>();
  
  public AdFunctionTemplatesContainer(
      List<AbstractAdFunctionTemplateEntity> adFunctionTemplates,
      List<AdFunctionTemplateInputConstantPointTemplateMappingDto> inputConstantPointTemplateMappingsDtoList) {
    super();
    adRuleFunctionTemplates = new HashMap<>();
    adComputedPointFunctionTemplates = new HashMap<>();
    pointErrorMessages = new HashMap<>();
    
    Iterator<AbstractAdFunctionTemplateEntity> adFunctionTemplateIterator = adFunctionTemplates.iterator();
    while (adFunctionTemplateIterator.hasNext()) {
      
      AbstractAdFunctionTemplateEntity adFunctionTemplate = adFunctionTemplateIterator.next();
      
      List<SimpleValidationMessage> messages = adFunctionTemplate.validateSimple();
      
      if (LOGGER.isInfoEnabled()) {
        List<SimpleValidationMessage> infoMessages = SimpleValidationMessage.getInfoLevelMessages(messages);
        if (!infoMessages.isEmpty()) {
          
          LOGGER.info("{} has info level issues: {}",
              adFunctionTemplate,
              infoMessages);
        }
      }

      boolean valid = true;
      List<SimpleValidationMessage> errorMessages = SimpleValidationMessage.getErrorLevelMessages(messages);
      if (!errorMessages.isEmpty()) {

        valid = false;
        LOGGER.error("{} has error level issues: {}",
            adFunctionTemplate,
            errorMessages);
      }      
      
      if (valid) {
        
        // RP-7832: Duplicate AD Function Template Names
        String name = adFunctionTemplate.getName();
        if (adFunctionTemplateNames.contains(name)) {
       
          int sequenceNumber = 1;
          String uniqueName = name + "_" + Integer.toString(sequenceNumber++);
          while (adFunctionTemplateNames.contains(uniqueName)) {

            uniqueName = name + "_" + Integer.toString(sequenceNumber++);
          }
          
          LOGGER.error("AD function template: [{}] has the same name as another, changing to be unique: [{}]",
              name,
              uniqueName);
          
          name = uniqueName;
          adFunctionTemplate.setName(name);
        }
        adFunctionTemplateNames.add(name);

        Integer adFunctionTemplateId = adFunctionTemplate.getPersistentIdentity();
        String displayName = null;
        
        if (adFunctionTemplate instanceof AdRuleFunctionTemplateEntity) {
          
          adRuleFunctionTemplates.put(adFunctionTemplateId, (AdRuleFunctionTemplateEntity)adFunctionTemplate);
          addPointErrorMessages(adFunctionTemplate);
          displayName = ((AdRuleFunctionTemplateEntity)adFunctionTemplate).getFaultNumber() + " " + adFunctionTemplate.getDisplayName();
          
        } else if (adFunctionTemplate instanceof AdComputedPointFunctionTemplateEntity) {
          
          adComputedPointFunctionTemplates.put(adFunctionTemplateId, (AdComputedPointFunctionTemplateEntity)adFunctionTemplate);
          addPointErrorMessages(adFunctionTemplate);
          displayName = ((AdComputedPointFunctionTemplateEntity)adFunctionTemplate).getReferenceNumber() + " " + adFunctionTemplate.getDisplayName();
          
        } else {
          LOGGER.error("Unsupported AD Function Template: {} ({})", adFunctionTemplate, adFunctionTemplate.getClass().getSimpleName());
        }
        
        adFunctionTemplateDisplayNames.put(adFunctionTemplateId, displayName);
      }
    }
    
    try {
      adRuleFunctionSystemCategories = AdFunctionTemplatesContainer.buildAdRuleFunctionTemplateHierarchy(adRuleFunctionTemplates.values());
    } catch (EntityAlreadyExistsException e) {
      throw new IllegalStateException("Unable to build rule template hierarchy: " + e.getMessage(), e);
    }
    
    try {
      adComputedPointFunctionCategories = AdFunctionTemplatesContainer.buildAdComputedPointFunctionTemplateHierarchy(adComputedPointFunctionTemplates.values());
    } catch (EntityAlreadyExistsException e) {
      throw new IllegalStateException("Unable to build computed point template hierarchy: " + e.getMessage(), e);
    }
    
    for (AdFunctionTemplateInputConstantPointTemplateMappingDto dto: inputConstantPointTemplateMappingsDtoList) {
      
      addInputConstantPointTemplateMapping(dto.getAdFunctionTemplateInputConstId(), Integer.parseInt(dto.getPointTemplateId()));
    }
  }
  
  public void addInputConstantPointTemplateMapping(
      Integer adFunctionTemplateInputConstantId,
      Integer pointTemplateId) {
    
    adFunctionTemplateInputConstantPointTemplateMappings.put(adFunctionTemplateInputConstantId, pointTemplateId);
  }
  
  private void addPointErrorMessages(AbstractAdFunctionTemplateEntity adFunctionTemplate) {
    
    StringBuilder sb = new StringBuilder(256);
    sb.append("Point constraint: [")
        .append(adFunctionTemplate.getTupleConstraintExpression())
        .append("] was not satisfied");
    
    pointErrorMessages.put(Integer.valueOf(adFunctionTemplate.getPersistentIdentity().intValue() * -1), sb.toString());
    
    AbstractEnergyExchangeTypeEntity energyExchangeType = adFunctionTemplate.getEnergyExchangeType();
    
    for (AdFunctionTemplateInputPointEntity inputPoint: adFunctionTemplate.getInputPoints()) {
      
      sb.setLength(0);
      sb.append("Expected ");
      
      boolean isRequired = inputPoint.getIsRequired();
      boolean isArray = inputPoint.getIsArray();
      
      if (!isRequired && !isArray) { // 00
        
        sb.append("zero or one ");
        
      } else if (!isRequired && isArray) { // 01
        
        sb.append("zero or more ");
        
      } else if (isRequired && !isArray) { // 10
        
        sb.append("one ");
        
      } else if (isRequired && isArray) { // 11
        
        sb.append("one or more ");
        
      }
      
      AbstractNodeTagTemplateEntity pointTemplate = DictionaryContext
          .getNodeTagTemplatesContainer()
          .getPointTemplateByTags(inputPoint.getTags());

      sb.append(pointTemplate.getName())
          .append(" ")
          .append(pointTemplate.getTags().toString().replace("[", "(").replace("]", ")"));
      
      if (!isArray) {
        sb.append(" point");
      } else {
        sb.append(" points");
      }
      sb.append(" for ");
      
      String currentObjectExpression = inputPoint.getCurrentObjectExpression();
      if (currentObjectExpression != null) {
        String description = CURRENT_OBJECT_EXPRESSION_DESCRIPTIONS.get(currentObjectExpression);
        if (description == null) {
          description = currentObjectExpression;
        }
        sb.append(description);
      } else {
        sb.append("this ");
        if (energyExchangeType instanceof EquipmentEnergyExchangeTypeEntity) {
          sb.append("equipment");
        } else if (energyExchangeType instanceof LoopEnergyExchangeTypeEntity) {
          sb.append("loop");
        } else if (energyExchangeType instanceof PlantEnergyExchangeTypeEntity) {
          sb.append("plant");
        }
      }
      
      pointErrorMessages.put(inputPoint.getPersistentIdentity(), sb.toString());
      
      // While we are iterating through the input points, do an audit to see if the energy exchange type is compatible with the point template for the input point.
      if (inputPoint.getCurrentObjectExpression() == null) {

        Set<PointTemplateEntity> energyExchangeTypePointTemplates = DictionaryContext
            .getNodeTagTemplatesContainer()
            .getEnergyExchangeLevelPointTemplatesForEnergyExchangeType(energyExchangeType);
        
        List<Set<TagEntity>> list = new ArrayList<>();
        boolean foundMatch = false;
        for (PointTemplateEntity pt: energyExchangeTypePointTemplates) {

          list.add(pt.getTags());
          if (pt.getTags().equals(pointTemplate.getTags())) {
            
            foundMatch = true;
            break;
          }
        }
        
        if (!foundMatch) {
          
          containerAuditMessages.add(adFunctionTemplate.getFullDisplayName() 
              + " INPUT POINT: " 
              + inputPoint.getName() 
              + " WITH POINT TEMPLATE: " 
              + pointTemplate
              + " "
              + pointTemplate.getTags()
              + " ISN'T COMPATIBLE WITH ENERGY EXCHANGE TYPE: " 
              + energyExchangeType);
        }
      }
    }
  }
  
  /**
   * 
   * @return A list of AD Function Templates Container audit messages
   */
  public List<String> getContainerAuditMessages() {
    return containerAuditMessages;
  }
  
  /**
   * 
   * @param adFunctionTemplateId The given AD function template id
   * 
   * @return A display name consisting of the fault/reference number, followed by the display name
   */
  public String getDisplayName(Integer adFunctionTemplateId) {
    return adFunctionTemplateDisplayNames.get(adFunctionTemplateId);
  }

  /**
   * 
   * @param templateInputPointIds A negative value will return the function template tuple constraint error message
   * 
   * @return The error messages corresponding to the given template input point ids, or if negative values, the
   *         corresponding template tuple constraint error message
   */
  public List<String> getErrorMessages(List<Integer> templateInputPointIds) {
    
    List<String> errorMessages = new ArrayList<>();
    for (Integer templateInputPointId: templateInputPointIds) {
      errorMessages.add(getErrorMessage(templateInputPointId));
    }
    return errorMessages;
  } 
  
  /**
   * 
   * @param templateInputPointId A negative value will return the function template tuple constraint error message
   * 
   * @return The error message corresponding to the given template input point id, or if a negative value, the
   *         corresponding template tuple constraint error message
   */
  public String getErrorMessage(Integer templateInputPointId) {
    
    String errorMessage = this.pointErrorMessages.get(templateInputPointId);
    if (errorMessage == null) {
      
      throw new IllegalStateException("Template input point id: [" + templateInputPointId + "] does not exist.");
    }
    return errorMessage;
  }
  
  public List<AbstractAdFunctionTemplateEntity> getAdFunctionTemplateByInputPointTags(Set<TagEntity> tags) {
    
    if (tags == null) {
      throw new IllegalStateException("tags must be specified.");
    }
    
    List<AbstractAdFunctionTemplateEntity> list = new ArrayList<>();
    for (Map.Entry<Integer, AdRuleFunctionTemplateEntity> entry: adRuleFunctionTemplates.entrySet()) {
      
      AbstractAdFunctionTemplateEntity adFunctionTemplate = entry.getValue();
      for (AdFunctionTemplateInputPointEntity inputPoint: adFunctionTemplate.getInputPoints()) {
        
        if (inputPoint.getTags().equals(tags)) {
          list.add(adFunctionTemplate);
        }
      }
    }

    for (Map.Entry<Integer, AdComputedPointFunctionTemplateEntity> entry: adComputedPointFunctionTemplates.entrySet()) {
      
      AbstractAdFunctionTemplateEntity adFunctionTemplate = entry.getValue();
      for (AdFunctionTemplateInputPointEntity inputPoint: adFunctionTemplate.getInputPoints()) {
        
        if (inputPoint.getTags().equals(tags)) {
          list.add(adFunctionTemplate);
        }
      }
    }
    
    return list;
  }  

  public List<AbstractAdFunctionTemplateEntity> getAdFunctionTemplates() {
    
    List<AbstractAdFunctionTemplateEntity> list = new ArrayList<>();
    list.addAll(adRuleFunctionTemplates.values());
    list.addAll(adComputedPointFunctionTemplates.values());
    return list;
  }
  
  public List<AbstractAdFunctionTemplateEntity> getAdFunctionTemplates(FunctionType functionType) {
    
    List<AbstractAdFunctionTemplateEntity> list = new ArrayList<>();
    if (functionType != null) {
      if (functionType.equals(FunctionType.COMPUTED_POINT)) {
        list.addAll(adComputedPointFunctionTemplates.values());  
      } else {
        list.addAll(adRuleFunctionTemplates.values());        
      }
    } else {
      list.addAll(adRuleFunctionTemplates.values());
      list.addAll(adComputedPointFunctionTemplates.values());
    }
    return list;
  }

  public AbstractAdFunctionTemplateEntity getAdFunctionTemplate(Integer adFunctionTemplateId) {
    
    AbstractAdFunctionTemplateEntity adFunctionTemplate = getAdFunctionTemplateNullIfNotExists(adFunctionTemplateId);
    if (adFunctionTemplate != null) {
      return adFunctionTemplate;
    }

    throw new IllegalStateException("AD Function Template with id: ["
        + adFunctionTemplateId 
        + "] not found in : " 
        + getAdFunctionTemplates());
  }

  public AbstractAdFunctionTemplateEntity getAdFunctionTemplateNullIfNotExists(Integer adFunctionTemplateId) {
    
    AbstractAdFunctionTemplateEntity adFunctionTemplate = adRuleFunctionTemplates.get(adFunctionTemplateId);
    if (adFunctionTemplate != null) {
      return adFunctionTemplate;
    }

    return adComputedPointFunctionTemplates.get(adFunctionTemplateId);
  }
  
  public AdRuleFunctionTemplateEntity getRuleFunctionTemplate(Integer adFunctionTemplateId) {
    return adRuleFunctionTemplates.get(adFunctionTemplateId);
  }

  public AdRuleFunctionTemplateEntity getRuleFunctionTemplate(String faultNumber) throws EntityDoesNotExistException {
    
    AdRuleFunctionTemplateEntity adRuleFunctionTemplate = this.getRuleFunctionTemplateNullIfNotExists(faultNumber);
    if (adRuleFunctionTemplate != null) {
      
      return adRuleFunctionTemplate;
    }
    throw new EntityDoesNotExistException("AD Function Template with fault number: ["
        + faultNumber
        + "] not found.");
  }
  
  public AdRuleFunctionTemplateEntity getRuleFunctionTemplateNullIfNotExists(String faultNumber) {
    
    if (faultNumber == null || faultNumber.trim().isEmpty()) {
      throw new IllegalArgumentException("fault number must be specified.");
    }
    
    Iterator<AdRuleFunctionTemplateEntity> iterator = adRuleFunctionTemplates.values().iterator();
    while (iterator.hasNext()) {
      
      AdRuleFunctionTemplateEntity adRuleFunctionTemplate = iterator.next();
      
      if (faultNumber.equals(adRuleFunctionTemplate.getFaultNumber())) {
        return adRuleFunctionTemplate;
      }
    }
    return null;
  }
  
  public AbstractAdFunctionTemplateEntity getDifferentAdFunctionTemplateByNameNullIfNotExists(AbstractAdFunctionTemplateEntity adFunctionTemplate, String name) {

    if (adFunctionTemplate == null) {
      throw new IllegalArgumentException("adFunctionTemplate must be specified.");
    }
    
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("name must be specified.");
    }
    
    Iterator<AdRuleFunctionTemplateEntity> ruleIterator = adRuleFunctionTemplates.values().iterator();
    while (ruleIterator.hasNext()) {
      
      AdRuleFunctionTemplateEntity t = ruleIterator.next();
      
      if (name.equals(t.getName()) && !t.equals(adFunctionTemplate)) {
        return t;
      }
    }
    
    Iterator<AdComputedPointFunctionTemplateEntity> computedPointIterator = adComputedPointFunctionTemplates.values().iterator();
    while (computedPointIterator.hasNext()) {
      
      AdComputedPointFunctionTemplateEntity t = computedPointIterator.next();
      
      if (name.equals(t.getName()) && !t.equals(adFunctionTemplate)) {
        return t;
      }
    }
    
    return null;
  }
  
  public AdComputedPointFunctionTemplateEntity getAdComputedPointFunctionTemplate(Integer adFunctionTemplateId) {
    return adComputedPointFunctionTemplates.get(adFunctionTemplateId);
  }
  
  public List<AdRuleFunctionTemplateEntity> getAdRuleFunctionTemplates() {
    
    List<AdRuleFunctionTemplateEntity> list = new ArrayList<>();
    list.addAll(adRuleFunctionTemplates.values());
    return list;
  }

  public Set<Integer> getAdRuleFunctionTemplateIds() {
    
    Set<Integer> set = new HashSet<>();
    for (AdRuleFunctionTemplateEntity adFunctionTemplate: adRuleFunctionTemplates.values()) {
      set.add(adFunctionTemplate.getPersistentIdentity());
    }
    return set;
  }
  
  public List<AdComputedPointFunctionTemplateEntity> getAdComputedPointFunctionTemplates() {
    
    List<AdComputedPointFunctionTemplateEntity> list = new ArrayList<>();
    list.addAll(adComputedPointFunctionTemplates.values());
    return list;
  }
  
  public Set<Integer> getAdComputedPointFunctionTemplateIds() {
    
    Set<Integer> set = new HashSet<>();
    for (AdComputedPointFunctionTemplateEntity adFunctionTemplate: adComputedPointFunctionTemplates.values()) {
      set.add(adFunctionTemplate.getPersistentIdentity());
    }
    return set;
  }
  
  public AdFunctionTemplateInputConstantEntity getAdFunctionTemplateInputConstant(Integer adFunctionTemplateInputConstantId) {
    
    if (allAdFunctionTemplateInputConstants == null) {
      
      allAdFunctionTemplateInputConstants = new HashMap<>();
      
      for (AdRuleFunctionTemplateEntity adFunctionTemplate: adRuleFunctionTemplates.values()) {
        for (AdFunctionTemplateInputConstantEntity inputConstant: adFunctionTemplate.getInputConstants()) {

          allAdFunctionTemplateInputConstants.put(inputConstant.getPersistentIdentity(), inputConstant);
        }
      }

      for (AdComputedPointFunctionTemplateEntity adFunctionTemplate: adComputedPointFunctionTemplates.values()) {
        for (AdFunctionTemplateInputConstantEntity inputConstant: adFunctionTemplate.getInputConstants()) {

          allAdFunctionTemplateInputConstants.put(inputConstant.getPersistentIdentity(), inputConstant);
        }
      }
    }
    return allAdFunctionTemplateInputConstants.get(adFunctionTemplateInputConstantId);
  }
  
  // *** USED TO SEND DTOS FOR UI CONSUMPTION
  // AD RULE FUNCTION TEMPLATES
  public AdRuleFunctionTemplateHierarchyDto getAdRuleFunctionTemplateHierarchyDto() {
    
    return mapToAdRuleFunctionTemplateHierarchyDto(adRuleFunctionSystemCategories);
  }

  // AD COMPUTED POINT FUNCTION TEMPLATES
  public AdComputedPointFunctionTemplateHierarchyDto getAdComputedPointFunctionTemplateHierarchy() {
    
    return mapToAdComputedPointFunctionTemplateHierarchyDto(this.adComputedPointFunctionCategories);
  }
  
  public List<AdFunctionTemplateInputConstantPointTemplateMappingDto> getAdFunctionTemplateInputConstantPointTemplateMappingDtos() {
    
    List<AdFunctionTemplateInputConstantPointTemplateMappingDto> dtos = new ArrayList<>();
    for (Map.Entry<Integer, Integer> entry: adFunctionTemplateInputConstantPointTemplateMappings.entrySet()) {
      dtos.add(new AdFunctionTemplateInputConstantPointTemplateMappingDto(entry.getKey(), entry.getValue().toString()));
    }
    return dtos;
  }  
  // ***
  
  @Override
  public String toString() {
    return new StringBuilder()
        .append("AdFunctionTemplatesContainer [adRuleFunctionTemplates=")
        .append(this.adRuleFunctionTemplates)
        .append(", adComputedPointFunctionTemplates")
        .append(this.adComputedPointFunctionTemplates)
        .append("]")
        .toString();
  }
  
  private static List<AdRuleFunctionSystemCategoryEntity> buildAdRuleFunctionTemplateHierarchy(final Collection<AdRuleFunctionTemplateEntity> ruleTemplateList) throws EntityAlreadyExistsException {

    // Keys are the "ordinal" values from the rule template fault number.
    final Map<Integer, AdRuleFunctionSystemCategoryEntity> ruleSystemCategoryMap = buildAdRuleFunctionSystemCategoryMap();
    final Map<Double, AdRuleFunctionEquipmentCategoryEntity> ruleEquipmentCategoryMap = buildAdRuleFunctionEquipmentCategoryMap(ruleSystemCategoryMap);
    
    Iterator<AdRuleFunctionTemplateEntity> ruleTemplateIterator = ruleTemplateList.iterator();
    while (ruleTemplateIterator.hasNext()) {
      
      AdRuleFunctionTemplateEntity ruleTemplate = ruleTemplateIterator.next();
      
      try {
        // Given a fault number of A.B.C.D, then:
        //   A is the ordinal of the rule system category
        //   B is the ordinal of the rule equipment category
        //   C is the sub ordinal of the rule equipment category
        String faultNumber = ruleTemplate.getFaultNumber().replaceAll("\\.", ":");
        String[] faultNumberElements = faultNumber.split(":");
        if (faultNumberElements.length != 4) {
          
          LOGGER.error("Could not process rule template: [{} - {}] because faultNumber: [{}] could not be parsed in the form of: [A.B.C.D]",
              ruleTemplate.getFaultNumber(),
              ruleTemplate.getName(),
              ruleTemplate.getFaultNumber());
          
        } else {
          
          String ordinalA = faultNumberElements[0];
          String ordinalB = faultNumberElements[1];
          
          Integer ruleSystemCategoryOrdinal = Integer.valueOf(ordinalA);
          Integer ruleEquipmentCategoryOrdinal = Integer.valueOf(ordinalB);
          
          AdRuleFunctionSystemCategoryEntity ruleSystemCategory = ruleSystemCategoryMap.get(ruleSystemCategoryOrdinal);
          if (ruleSystemCategory == null) {
            
            LOGGER.error("Could not process rule template properly: [{} - {}] because ruleSystemCategory with key: [{}] was not found",
                ruleTemplate.getFaultNumber(),
                ruleTemplate.getName(),
                ruleSystemCategoryOrdinal);

            ruleSystemCategory = new AdRuleFunctionSystemCategoryEntity(
                ruleSystemCategoryOrdinal,
                ruleSystemCategoryOrdinal.toString(),
                "Unknown");
            ruleSystemCategoryMap.put(ruleSystemCategoryOrdinal, ruleSystemCategory);
          } 
          
          Double ruleEquipmentCategoryKey = Double.valueOf(buildKey(ordinalA, ordinalB));
          AdRuleFunctionEquipmentCategoryEntity ruleEquipmentCategory = ruleEquipmentCategoryMap.get(ruleEquipmentCategoryKey);
          if (ruleEquipmentCategory == null) {
            
            if (!ruleEquipmentCategoryKey.equals(Double.valueOf(5.1))) {
              LOGGER.error("Could not process rule template properly: [{} - {}] because ruleEquipmentCategory with key: [{}] was not found",
                  ruleTemplate.getFaultNumber(),
                  ruleTemplate.getName(),
                  ruleEquipmentCategoryKey);
            }
            
            Integer ruleEquipmentCategoryId = Integer.valueOf(ruleSystemCategoryOrdinal * 10 + ruleEquipmentCategoryOrdinal);  
            ruleEquipmentCategory = new AdRuleFunctionEquipmentCategoryEntity(
                ruleEquipmentCategoryId,
                ruleSystemCategory,
                ordinalA + "." + ordinalB,
                "Unknown",
                DictionaryContext.getTagsContainer().getEnergyExchangeTypeById(ruleTemplate.getEnergyExchangeType().getPersistentIdentity()));

            try {
              ruleSystemCategory.addRuleEquipmentCategory(ruleEquipmentCategory);
            } catch (EntityAlreadyExistsException e) {
              LOGGER.error("Unable to add rule template equipment category: {}, error: {}", ruleEquipmentCategory, e.getMessage(), e);
            }
            ruleEquipmentCategoryMap.put(ruleEquipmentCategoryKey, ruleEquipmentCategory);
          } 
          
          ruleEquipmentCategory.addAdRuleFunctionTemplate(ruleTemplate);
        }
      } catch (Exception e) {
        LOGGER.error("Unable to add ad rule function template to hierarchy: [{}], reason: [{}]",
            ruleTemplate,
            e.getMessage());
      }
    }
    
    List<AdRuleFunctionSystemCategoryEntity> ruleSystemCategories = new ArrayList<>();
    ruleSystemCategories.addAll(ruleSystemCategoryMap.values());
    return ruleSystemCategories;
  }
    
  private static String buildKey(String ordinal, String subOrdinal) {
    return ordinal + "." + subOrdinal;
  }
  
  private static Map<Integer, AdRuleFunctionSystemCategoryEntity> buildAdRuleFunctionSystemCategoryMap() {
    
    final Map<Integer, AdRuleFunctionSystemCategoryEntity> map = new HashMap<>();
    
    map.put(1, new AdRuleFunctionSystemCategoryEntity(
        1,
        "1",
        "Heating Systems"));
    
    map.put(2, new AdRuleFunctionSystemCategoryEntity(
        2,
        "2",
        "Chilled Water Systems"));
    
    map.put(3, new AdRuleFunctionSystemCategoryEntity(
        3,
        "3",
        "Air Distribution Systems"));
    
    map.put(4, new AdRuleFunctionSystemCategoryEntity(
        4,
        "4",
        "Meter"));
    
    map.put(5, new AdRuleFunctionSystemCategoryEntity(
        5,
        "5",
        "Building"));
    
    return map;
  }
  
  private static Map<Double, AdRuleFunctionEquipmentCategoryEntity> buildAdRuleFunctionEquipmentCategoryMap(Map<Integer, AdRuleFunctionSystemCategoryEntity> ruleSystemCategoryKeyMap) throws EntityAlreadyExistsException {
    
    Map<Double, AdRuleFunctionEquipmentCategoryEntity> map = new HashMap<>();
    AdRuleFunctionSystemCategoryEntity ruleSystemCategory = ruleSystemCategoryKeyMap.get(1);
    AdRuleFunctionEquipmentCategoryEntity ruleEquipmentCategory = new AdRuleFunctionEquipmentCategoryEntity(
        11,
        ruleSystemCategory,
        "1.1",
        "Boilers",
        DictionaryContext.getTagsContainer().getEnergyExchangeTypeByName("boiler"));
    map.put(1.1, ruleEquipmentCategory);
    ruleSystemCategory.addRuleEquipmentCategory(ruleEquipmentCategory);
    
    ruleEquipmentCategory = new AdRuleFunctionEquipmentCategoryEntity(
        12,
        ruleSystemCategory,
        "1.2",
        "Pumps",
        DictionaryContext.getTagsContainer().getEnergyExchangeTypeByName("pump"));
    map.put(1.2, ruleEquipmentCategory);
    ruleSystemCategory.addRuleEquipmentCategory(ruleEquipmentCategory);
    
    ruleEquipmentCategory = new AdRuleFunctionEquipmentCategoryEntity(
        13,
        ruleSystemCategory,
        "1.3",
        "Heat Exchangers",
        DictionaryContext.getTagsContainer().getEnergyExchangeTypeByName("heatExchanger"));    
    map.put(1.3, ruleEquipmentCategory);
    ruleSystemCategory.addRuleEquipmentCategory(ruleEquipmentCategory);

    ruleEquipmentCategory = new AdRuleFunctionEquipmentCategoryEntity(
        15,
        ruleSystemCategory,
        "1.5",
        "Heating Hot Water Systems",
        DictionaryContext.getTagsContainer().getEnergyExchangeTypeByName("hotWaterPlant"));    
    map.put(1.5, ruleEquipmentCategory);
    ruleSystemCategory.addRuleEquipmentCategory(ruleEquipmentCategory);

    ruleSystemCategory = ruleSystemCategoryKeyMap.get(2);
    ruleEquipmentCategory = new AdRuleFunctionEquipmentCategoryEntity(
        21,
        ruleSystemCategory,
        "2.1",
        "Chillers",
        DictionaryContext.getTagsContainer().getEnergyExchangeTypeByName("chiller")); 
    map.put(2.1, ruleEquipmentCategory);
    ruleSystemCategory.addRuleEquipmentCategory(ruleEquipmentCategory);

    ruleEquipmentCategory = new AdRuleFunctionEquipmentCategoryEntity(
        22,
        ruleSystemCategory,
        "2.2",
        "Pumps",
        DictionaryContext.getTagsContainer().getEnergyExchangeTypeByName("pump")); 
    map.put(2.2, ruleEquipmentCategory);
    ruleSystemCategory.addRuleEquipmentCategory(ruleEquipmentCategory);
    
    ruleEquipmentCategory = new AdRuleFunctionEquipmentCategoryEntity(
        23,
        ruleSystemCategory,
        "2.3",
        "Cooling Towers",
        DictionaryContext.getTagsContainer().getEnergyExchangeTypeByName("coolingTower"));
    map.put(2.3, ruleEquipmentCategory);
    ruleSystemCategory.addRuleEquipmentCategory(ruleEquipmentCategory);
    
    ruleEquipmentCategory = new AdRuleFunctionEquipmentCategoryEntity(
        24,
        ruleSystemCategory,
        "2.4",
        "Heat Exchangers",
        DictionaryContext.getTagsContainer().getEnergyExchangeTypeByName("heatExchanger"));
    map.put(2.4, ruleEquipmentCategory);
    ruleSystemCategory.addRuleEquipmentCategory(ruleEquipmentCategory);

    ruleEquipmentCategory = new AdRuleFunctionEquipmentCategoryEntity(
        25,
        ruleSystemCategory,
        "2.5",
        "Chilled Water Plant",
        DictionaryContext.getTagsContainer().getEnergyExchangeTypeByName("chilledWaterPlant"));
    map.put(2.5, ruleEquipmentCategory);
    ruleSystemCategory.addRuleEquipmentCategory(ruleEquipmentCategory);
    
    ruleSystemCategory = ruleSystemCategoryKeyMap.get(3);
    ruleEquipmentCategory = new AdRuleFunctionEquipmentCategoryEntity(
        31,
        ruleSystemCategory,
        "3.1",
        "AHU's (air handling units, rooftop units, packaged units)",
        DictionaryContext.getTagsContainer().getEnergyExchangeTypeByName("ahu"));
    map.put(3.1, ruleEquipmentCategory);
    ruleSystemCategory.addRuleEquipmentCategory(ruleEquipmentCategory);
    
    ruleEquipmentCategory = new AdRuleFunctionEquipmentCategoryEntity(
        32,
        ruleSystemCategory,
        "3.2",
        "VAV's (variable air volume boxes, fan powered boxes)",
        DictionaryContext.getTagsContainer().getEnergyExchangeTypeByName("vav"));
    map.put(3.2, ruleEquipmentCategory);
    ruleSystemCategory.addRuleEquipmentCategory(ruleEquipmentCategory);
    
    ruleEquipmentCategory = new AdRuleFunctionEquipmentCategoryEntity(
        33,
        ruleSystemCategory,
        "3.3",
        "FCU (fan coil units)",
        DictionaryContext.getTagsContainer().getEnergyExchangeTypeByName("fcu"));
    map.put(3.3, ruleEquipmentCategory);
    ruleSystemCategory.addRuleEquipmentCategory(ruleEquipmentCategory);

    ruleEquipmentCategory = new AdRuleFunctionEquipmentCategoryEntity(
        34,
        ruleSystemCategory,
        "3.4",
        "UV (unit ventilators)",
        DictionaryContext.getTagsContainer().getEnergyExchangeTypeByName("uv"));
    map.put(3.4, ruleEquipmentCategory);
    ruleSystemCategory.addRuleEquipmentCategory(ruleEquipmentCategory);
    
    ruleEquipmentCategory = new AdRuleFunctionEquipmentCategoryEntity(
        35,
        ruleSystemCategory,
        "3.5",
        "Heat Pumps",
        DictionaryContext.getTagsContainer().getEnergyExchangeTypeByName("heatPump"));
    map.put(3.5, ruleEquipmentCategory);
    ruleSystemCategory.addRuleEquipmentCategory(ruleEquipmentCategory);
    
    ruleSystemCategory = ruleSystemCategoryKeyMap.get(4);
    ruleEquipmentCategory = new AdRuleFunctionEquipmentCategoryEntity(
        42,
        ruleSystemCategory,
        "4.2",
        "Electric",
        DictionaryContext.getTagsContainer().getEnergyExchangeTypeByName("elecMeter"));
    map.put(4.2, ruleEquipmentCategory);
    ruleSystemCategory.addRuleEquipmentCategory(ruleEquipmentCategory);
    
    ruleEquipmentCategory = new AdRuleFunctionEquipmentCategoryEntity(
        43,
        ruleSystemCategory,
        "4.3",
        "Gas",
        DictionaryContext.getTagsContainer().getEnergyExchangeTypeByName("gasMeter"));
    map.put(4.3, ruleEquipmentCategory);
    ruleSystemCategory.addRuleEquipmentCategory(ruleEquipmentCategory);
    
    ruleEquipmentCategory = new AdRuleFunctionEquipmentCategoryEntity(
        44,
        ruleSystemCategory,
        "4.4",
        "Water",
        DictionaryContext.getTagsContainer().getEnergyExchangeTypeByName("domesticWaterMeter"));
    map.put(4.4, ruleEquipmentCategory);
    ruleSystemCategory.addRuleEquipmentCategory(ruleEquipmentCategory);

    ruleEquipmentCategory = new AdRuleFunctionEquipmentCategoryEntity(
        51,
        ruleSystemCategory,
        "5.1",
        "Outside Air Temperature",
        DictionaryContext.getTagsContainer().getEnergyExchangeTypeByName("on_prem_weather_station"));
    map.put(5.1, ruleEquipmentCategory);
    ruleSystemCategory.addRuleEquipmentCategory(ruleEquipmentCategory);

    return map;
  }
  
  public static AdFunctionTemplatesContainer mapFromDtos(
      List<DatabaseWrapperDto> dtoList,
      List<AdFunctionTemplateInputConstantPointTemplateMappingDto> inputConstantPointTemplateMappingsDtoList) {
    
    List<AbstractAdFunctionTemplateEntity> adFunctionTemplates = new ArrayList<>();
    Iterator<DatabaseWrapperDto> iterator = dtoList.iterator();
    while (iterator.hasNext()) {
      
      DatabaseWrapperDto databaseWrapperDto = iterator.next();
      
      String functionTemplateJson = databaseWrapperDto.getJson();
      
      AdFunctionTemplateDto dto = null;
      try {
        
        dto = AbstractEntity.OBJECT_MAPPER.get().readValue(functionTemplateJson, new TypeReference<AdFunctionTemplateDto>(){});
        
        AbstractAdFunctionTemplateEntity functionTemplate = mapDtoToEntity(dto);
        
        adFunctionTemplates.add(functionTemplate);
        
      } catch (Exception e) {
        LOGGER.error("Unable to process dto: {}, error: {}",
            dto,
            e.getMessage(),
            e);
      }
    }
    return new AdFunctionTemplatesContainer(adFunctionTemplates, inputConstantPointTemplateMappingsDtoList); 
  }
  
  public static AbstractAdFunctionTemplateEntity mapDtoToEntity(AdFunctionTemplateDto dto) throws EntityAlreadyExistsException, EntityDoesNotExistException {
    
    Integer functionTemplateId = dto.getId();
    
    Integer equipmentTypeId = null;
    List<Integer> equipmentTypeIdList = dto.getEquipmentTypeId();
    if (equipmentTypeIdList.isEmpty() || equipmentTypeIdList.size() > 1) {
      throw new IllegalStateException("Function template: ["
          + dto.getReferenceNumber() 
          + "] needs to have exactly one equipment type, but had: "
          + equipmentTypeIdList);
    }
    equipmentTypeId = equipmentTypeIdList.get(0);
    
    Integer adFunctionId = dto.getFunctionCodeModuleId();
    AdFunctionEntity adFunction = AD_FUNCTIONS.get(adFunctionId);
    if (adFunction == null) {
      
      adFunction = new AdFunctionEntity(
          adFunctionId,
          dto.getFunctionCodeModuleName(),
          dto.getFunctionCodeModulDescription(),
          FunctionType.get(dto.getFunctionTypeId()));
      
      AD_FUNCTIONS.put(adFunctionId, adFunction);
    }
    
    AbstractAdFunctionTemplateEntity functionTemplate = null;
    if (adFunction.getFunctionType().equals(FunctionType.RULE)) {

      functionTemplate = new AdRuleFunctionTemplateEntity(
          functionTemplateId,
          adFunction,
          dto.getName(),
          dto.getDisplayName(),
          dto.getDescription(),
          DictionaryContext.getTagsContainer().getEnergyExchangeTypeById(equipmentTypeId),
          dto.getNodeFilterExpression(),
          dto.getTupleConstraint(),
          dto.getBeta(),
          dto.getVersion(),
          dto.getReferenceNumber());   
      
    } else if (adFunction.getFunctionType().equals(FunctionType.COMPUTED_POINT)) {

      functionTemplate = new AdComputedPointFunctionTemplateEntity(
          functionTemplateId,
          adFunction,
          dto.getName(),
          dto.getDisplayName(),
          dto.getDescription(),
          DictionaryContext.getTagsContainer().getEnergyExchangeTypeById(equipmentTypeId),
          dto.getNodeFilterExpression(),
          dto.getTupleConstraint(),
          dto.getBeta(),
          dto.getVersion(),
          dto.getReferenceNumber());   
      
    } else {
      throw new IllegalStateException("Unsupported function type: ["
          + adFunction.getFunctionType() 
          + "] for " 
          + dto.getReferenceNumber());
    }
    
    if (dto.getInputConsts() != null) {
      Iterator<AdFunctionTemplateInputConstantDto> inputConstIterator = dto.getInputConsts().iterator();
      while (inputConstIterator.hasNext()) {
        
        AdFunctionTemplateInputConstantDto inputConstDto = inputConstIterator.next();
        
        if (inputConstDto != null) {

          functionTemplate.addInputConstant(
              new AdFunctionTemplateInputConstantEntity(
                  inputConstDto.getId(),
                  functionTemplate,
                  inputConstDto.getSeqNo(),
                  inputConstDto.getName(),
                  inputConstDto.getDescription(),
                  inputConstDto.getDefaultValue(),
                  inputConstDto.getIsRequired(),
                  DataType.get(inputConstDto.getDataTypeId()),
                  DictionaryContext.getUnitsContainer().getUnit(inputConstDto.getUnitId())));
        }
      }
    }
    
    if (dto.getInputPoints() != null && !dto.getInputPoints().isEmpty()) {
      Iterator<AdFunctionTemplateInputPointDto> inputPointIterator = dto.getInputPoints().iterator();
      while (inputPointIterator.hasNext()) {
        
        AdFunctionTemplateInputPointDto adFunctionTemplateInputPointDto = inputPointIterator.next();
        
        functionTemplate.addInputPoint(new AdFunctionTemplateInputPointEntity(
            adFunctionTemplateInputPointDto.getId(),
            functionTemplate,
            adFunctionTemplateInputPointDto.getName(),
            adFunctionTemplateInputPointDto.getDescription(),
            adFunctionTemplateInputPointDto.getCurrentObjectExpression(),
            adFunctionTemplateInputPointDto.getRequired(),
            adFunctionTemplateInputPointDto.getArray(),
            adFunctionTemplateInputPointDto.getSeqNo(),
            DictionaryContext.getTagsContainer().getHaystackTagsByName(adFunctionTemplateInputPointDto.getTags())            
            ));
      }
    } else {
      throw new IllegalStateException("At least one input point must be defined for " 
          + dto.getReferenceNumber());
    }
    
    if (functionTemplate instanceof AdRuleFunctionTemplateEntity 
        && (dto.getOutputPoints() == null || dto.getOutputPoints().size() > 1)) {

      throw new IllegalStateException("Only one output point can be defined for rule function template " 
          + dto.getReferenceNumber());
    }
    
    Iterator<AdFunctionTemplateOutputPointDto> outputPointIterator = dto.getOutputPoints().iterator();
    while (outputPointIterator.hasNext()) {
      
      AdFunctionTemplateOutputPointDto adFunctionTemplateOutputPointDto = outputPointIterator.next();
      
      Set<TagEntity> tags = null;
      if (adFunctionTemplateOutputPointDto.getTags() != null && !adFunctionTemplateOutputPointDto.getTags().isEmpty()) {
        tags = DictionaryContext.getTagsContainer().getHaystackTagsByName(adFunctionTemplateOutputPointDto.getTags());  
      }
      
      functionTemplate.addOutputPoint(new AdFunctionTemplateOutputPointEntity(
          adFunctionTemplateOutputPointDto.getId(),
          functionTemplate,
          adFunctionTemplateOutputPointDto.getSeqNo(),
          adFunctionTemplateOutputPointDto.getDescription(),
          DataType.get(adFunctionTemplateOutputPointDto.getDataTypeId()),
          DictionaryContext.getUnitsContainer().getUnit(adFunctionTemplateOutputPointDto.getUnitId()),
          adFunctionTemplateOutputPointDto.getRange(),
          tags));
    }
    
    return functionTemplate;
  }
  
  public static Map<String, Object> mapToDtos(AdFunctionTemplatesContainer adFunctionTemplatesContainer) {
    
    Map<String, Object> map = new HashMap<>();
    
    
    List<AdFunctionTemplateInputConstantPointTemplateMappingDto> adFunctionTemplateInputConstantPointTemplateMappingDtoList = new ArrayList<>();
    Map<Integer, Integer> adFunctionTemplateInputConstantPointTemplateMappings = adFunctionTemplatesContainer.getAdFunctionTemplateInputConstantPointTemplateMappings();
    for (Map.Entry<Integer, Integer> entry: adFunctionTemplateInputConstantPointTemplateMappings.entrySet()) {
      
      AdFunctionTemplateInputConstantPointTemplateMappingDto dto = new AdFunctionTemplateInputConstantPointTemplateMappingDto();
      dto.setAdFunctionTemplateInputConstId(entry.getKey());
      dto.setPointTemplateId(entry.getValue().toString());
      adFunctionTemplateInputConstantPointTemplateMappingDtoList.add(dto);
    }
    map.put("adFunctionTemplateInputConstantPointTemplateMappings", adFunctionTemplateInputConstantPointTemplateMappingDtoList);
    
    
    List<DatabaseWrapperDto> dtos = new ArrayList<>();
    Iterator<AbstractAdFunctionTemplateEntity> iterator = adFunctionTemplatesContainer.getAdFunctionTemplates().iterator();
    while (iterator.hasNext()) {
      
      AbstractAdFunctionTemplateEntity functionTemplate = iterator.next();
      
      try {
        
        AdFunctionTemplateDto dto = mapToDto(functionTemplate);
        String json = AbstractEntity.OBJECT_MAPPER.get().writeValueAsString(dto);
        
        DatabaseWrapperDto databaseWrapperDto = new DatabaseWrapperDto();
        databaseWrapperDto.setId(functionTemplate.getPersistentIdentity().toString());
        databaseWrapperDto.setJson(json);
        dtos.add(databaseWrapperDto);
        
      } catch (JsonProcessingException e) {
        throw new IllegalStateException("Unable to serialize function template: " + functionTemplate, e);
      }
    }
    map.put("adFunctionTemplates", dtos);
    
    
    return map;
  }
  
  public static AdRuleFunctionTemplateHierarchyDto mapToAdRuleFunctionTemplateHierarchyDto(List<AdRuleFunctionSystemCategoryEntity> entities) {
    
    List<AdRuleFunctionSystemCategoryDto> ruleSystemCategories = new ArrayList<>();
    Iterator<AdRuleFunctionSystemCategoryEntity> iterator = entities.iterator();
    while (iterator.hasNext()) {
      
      AdRuleFunctionSystemCategoryDto dto = mapToDto(iterator.next());
      if (!dto.getRuleEquipmentCategories().isEmpty()) {
        ruleSystemCategories.add(dto);  
      }
    }
    Collections.sort(ruleSystemCategories);
    return AdRuleFunctionTemplateHierarchyDto
        .builder()
        .withRuleSystemCategories(ruleSystemCategories)
        .build();
  }
  
  public static AdRuleFunctionSystemCategoryDto mapToDto(AdRuleFunctionSystemCategoryEntity ruleSystemCategory) {

    List<AdRuleFunctionEquipmentCategoryDto> ruleEquipmentCategories = new ArrayList<>();
    Iterator<AdRuleFunctionEquipmentCategoryEntity> iterator = ruleSystemCategory.getRuleEquipmentCategories().iterator();
    while (iterator.hasNext()) {
      
      AdRuleFunctionEquipmentCategoryDto dto = mapToDto(iterator.next());
      if (!dto.getRuleTemplates().isEmpty()) {
        ruleEquipmentCategories.add(dto);  
      }
    }
    Collections.sort(ruleEquipmentCategories);
    return AdRuleFunctionSystemCategoryDto
        .builder()
        .withName(
            ruleSystemCategory.getFaultPrefix()
            + " "
            + ruleSystemCategory.getName())
        .withRuleEquipmentCategories(ruleEquipmentCategories)
        .build();
  }
  
  public static AdRuleFunctionEquipmentCategoryDto mapToDto(AdRuleFunctionEquipmentCategoryEntity ruleEquipmentCategory) {

    List<AdRuleFunctionTemplateDto> ruleTemplates = new ArrayList<>();
    Iterator<AdRuleFunctionTemplateEntity> iterator = ruleEquipmentCategory.getRuleFunctionTemplates().iterator();
    while (iterator.hasNext()) {
      
      AdRuleFunctionTemplateEntity ruleTemplate = iterator.next();
      if (AdFunctionEvaluator.INCLUDE_BETA_FUNCTION_TEMPLATES 
          || !(ruleTemplate.getIsBeta() != null && ruleTemplate.getIsBeta())) {
        
        ruleTemplates.add(mapToRuleTemplateHierarchyDto(ruleTemplate));  
      }
    }
    Collections.sort(ruleTemplates);
    return AdRuleFunctionEquipmentCategoryDto
        .builder()
        .withName(
            ruleEquipmentCategory.getFaultPrefix()
            + " " 
            + ruleEquipmentCategory.getName())
        .withEquipmentTypeTags(ruleEquipmentCategory.getEnergyExchangeType().getName())
        .withRuleTemplates(ruleTemplates)
        .build();
  }
  
  public static AdFunctionTemplateDto mapToDto(AbstractAdFunctionTemplateEntity e) {
    
    AdFunctionTemplateDto d = new AdFunctionTemplateDto();
    
    d.setId(e.getPersistentIdentity());
    d.setFunctionCodeModuleId(e.getAdFunction().getPersistentIdentity());
    d.setFunctionCodeModuleName(e.getAdFunction().getName());
    d.setFunctionCodeModulDescription(e.getAdFunction().getDescription());
    d.setFunctionTypeId(e.getAdFunction().getFunctionType().getId());
    d.setBeta(e.getIsBeta());
    d.setVersion(e.getVersion());
    d.setName(e.getName());
    d.setDescription(e.getDescription());
    d.setActive(Boolean.TRUE);
    d.setDisplayName(e.getDisplayName());
    if (e instanceof AdRuleFunctionTemplateEntity) {
      d.setReferenceNumber(((AdRuleFunctionTemplateEntity)e).getFaultNumber());
    } else if (e instanceof AdComputedPointFunctionTemplateEntity) {
      d.setReferenceNumber(((AdComputedPointFunctionTemplateEntity)e).getReferenceNumber());
    }
    d.setNodeFilterExpression(e.getNodeFilterExpression());
    d.setTupleConstraint(e.getTupleConstraintExpression());
    d.setEquipmentTypeId(Arrays.asList(e.getEnergyExchangeType().getPersistentIdentity()));
    
    List<AdFunctionTemplateInputConstantDto> adFunctionTemplateInputConstantDtos = new ArrayList<>();
    Iterator<AdFunctionTemplateInputConstantEntity> inputConstantIterator = e.getInputConstants().iterator();
    while (inputConstantIterator.hasNext()) {
      
      AdFunctionTemplateInputConstantEntity ice = inputConstantIterator.next();
      
      AdFunctionTemplateInputConstantDto icd = new AdFunctionTemplateInputConstantDto();
      icd.setId(ice.getPersistentIdentity());
      icd.setName(ice.getName());
      icd.setDescription(ice.getDescription());
      icd.setSeqNo(ice.getSequenceNumber());
      icd.setUnitId(ice.getUnit().getPersistentIdentity());
      icd.setDataTypeId(ice.getDataType().getId());
      icd.setIsRequired(ice.getIsRequired());
      icd.setDefaultValue(ice.getDefaultValue());
      adFunctionTemplateInputConstantDtos.add(icd);
    }
    d.setInputConsts(adFunctionTemplateInputConstantDtos);

    List<AdFunctionTemplateInputPointDto> adFunctionTemplateInputPointDtos = new ArrayList<>();
    Iterator<AdFunctionTemplateInputPointEntity> inputPointIterator = e.getInputPoints().iterator();
    while (inputPointIterator.hasNext()) {
      
      AdFunctionTemplateInputPointEntity ipe = inputPointIterator.next();
      
      AdFunctionTemplateInputPointDto ipd = new AdFunctionTemplateInputPointDto();
      ipd.setId(ipe.getPersistentIdentity());
      ipd.setName(ipe.getName());
      ipd.setDescription(ipe.getDescription());
      ipd.setSeqNo(ipe.getSequenceNumber());
      ipd.setArray(ipe.getIsArray());
      ipd.setRequired(ipe.getIsRequired());
      ipd.setCurrentObjectExpression(ipe.getCurrentObjectExpression());
      ipd.setTags(ipe.getNormalizedTagsAsList());
      adFunctionTemplateInputPointDtos.add(ipd);
    }
    d.setInputPoints(adFunctionTemplateInputPointDtos);    

    List<AdFunctionTemplateOutputPointDto> adFunctionTemplateOutputPointDtos = new ArrayList<>();
    Iterator<AdFunctionTemplateOutputPointEntity> outputPointIterator = e.getOutputPoints().iterator();
    while (outputPointIterator.hasNext()) {
      
      AdFunctionTemplateOutputPointEntity ope = outputPointIterator.next();
      
      AdFunctionTemplateOutputPointDto opd = new AdFunctionTemplateOutputPointDto();
      opd.setId(ope.getPersistentIdentity());
      opd.setDescription(ope.getDescription());
      opd.setRange(ope.getRange());
      opd.setSeqNo(ope.getSequenceNumber());
      opd.setTags(ope.getNormalizedTagsAsList());
      opd.setUnitId(ope.getUnit().getPersistentIdentity());
      opd.setDataTypeId(ope.getDataType().getId());
      adFunctionTemplateOutputPointDtos.add(opd);
    }
    d.setOutputPoints(adFunctionTemplateOutputPointDtos);    
    
    return d;
  }
  
  public static AdRuleFunctionTemplateDto mapToRuleTemplateHierarchyDto(AbstractAdFunctionTemplateEntity e) {
    
    List<AdRuleFunctionTemplateInputConstant> inputConstants = new ArrayList<>();
    Iterator<AdFunctionTemplateInputConstantEntity> inputConstantIterator = e.getInputConstants().iterator();
    while (inputConstantIterator.hasNext()) {
      
      AdFunctionTemplateInputConstantEntity ice = inputConstantIterator.next();
      
      inputConstants.add(AdRuleFunctionTemplateInputConstant
          .builder()
          .withId(ice.getPersistentIdentity())
          .withDataTypeId(ice.getDataType().getId())
          .withUnitId(ice.getUnit().getPersistentIdentity())
          .withSequenceNumber(ice.getSequenceNumber())
          .withDescription(ice.getDescription())
          .withDefaultValue(ice.getDefaultValue())
          .withName(ice.getName())
          .withIsRequired(ice.getIsRequired())
          .build());
    }
    Collections.sort(inputConstants);

    List<AdRuleFunctionTemplateInputPoint> inputPoints = new ArrayList<>();
    Iterator<AdFunctionTemplateInputPointEntity> inputPointIterator = e.getInputPoints().iterator();
    while (inputPointIterator.hasNext()) {
      
      AdFunctionTemplateInputPointEntity ipe = inputPointIterator.next();
      
      inputPoints.add(AdRuleFunctionTemplateInputPoint
          .builder()
          .withId(ipe.getPersistentIdentity())
          .withName(ipe.getName())
          .withDescription(ipe.getDescription())
          .withCurrentObjectExpression(ipe.getCurrentObjectExpression())
          .withIsRequired(ipe.getIsRequired())
          .withIsArray(ipe.getIsArray())
          .withSequenceNumber(ipe.getSequenceNumber())
          .withTags(ipe.getNormalizedTags())
          .build());
    }
    Collections.sort(inputPoints);
        
    List<AdRuleFunctionTemplateOutputPoint> outputPoints = new ArrayList<>();
    Iterator<AdFunctionTemplateOutputPointEntity> outputPointIterator = e.getOutputPoints().iterator();
    while (outputPointIterator.hasNext()) {
      
      AdFunctionTemplateOutputPointEntity ope = outputPointIterator.next();

      outputPoints.add(AdRuleFunctionTemplateOutputPoint
          .builder()
          .withId(ope.getPersistentIdentity())
          .withDescription(ope.getDescription())
          .withDataTypeId(ope.getDataType().getId())
          .withUnitId(ope.getUnit().getPersistentIdentity())
          .withRange(ope.getRange())
          .withSequenceNumber(ope.getSequenceNumber())
          .withTags(ope.getNormalizedTags())
          .build());
    }
    Collections.sort(outputPoints);

    return AdRuleFunctionTemplateDto
        .builder()
        .withId(e.getPersistentIdentity())
        .withRuleId(e.getAdFunction().getPersistentIdentity())
        .withName(e.getName())
        .withDisplayName(e.getDisplayName())
        .withDescription(e.getDescription())
        .withFaultNumber(((AdRuleFunctionTemplateEntity)e).getFaultNumber())
        .withEquipmentTypeId(e.getEnergyExchangeType().getPersistentIdentity())
        .withEquipmentType(e.getEnergyExchangeType().getName())
        .withFunctionTypeId(e.getAdFunction().getFunctionType().getId())
        .withNodeFilterExpression(e.getNodeFilterExpression())
        .withTupleConstraintExpression(e.getTupleConstraintExpression())
        .withIsBeta(e.getIsBeta())
        .withInputConstants(inputConstants)
        .withInputPoints(inputPoints)
        .withOutputPoints(outputPoints)
        .build();
  } 
  
  private static List<AdComputedPointFunctionCategoryEntity> buildAdComputedPointFunctionTemplateHierarchy(Collection<AdComputedPointFunctionTemplateEntity> adComputedPointFunctionTemplates) throws EntityAlreadyExistsException {
    
    Map<Integer, AdComputedPointFunctionCategoryEntity> map = new HashMap<>();
    Iterator<AdComputedPointFunctionTemplateEntity> iterator = adComputedPointFunctionTemplates.iterator();
    while (iterator.hasNext()) {
      
      AdComputedPointFunctionTemplateEntity adComputedPointFunctionTemplate = iterator.next();
      
      AbstractEnergyExchangeTypeEntity energyExchangeType = adComputedPointFunctionTemplate.getEnergyExchangeType();
      Integer energyExchangeTypeId = energyExchangeType.getPersistentIdentity();
      AdComputedPointFunctionCategoryEntity category = map.get(energyExchangeTypeId);
      if (category == null) {
        
        category = new AdComputedPointFunctionCategoryEntity(
            energyExchangeTypeId,
            energyExchangeType); 
        
        map.put(energyExchangeTypeId, category);
      }
      category.addAdComputedPointFunctionTemplate(adComputedPointFunctionTemplate);
    }
    
    List<AdComputedPointFunctionCategoryEntity> list = new ArrayList<>();
    list.addAll(map.values());
    Collections.sort(list);
    return list;
  }
  
  private static AdComputedPointFunctionTemplateHierarchyDto mapToAdComputedPointFunctionTemplateHierarchyDto(List<AdComputedPointFunctionCategoryEntity> entities) {
    
    List<AdComputedPointFunctionCategoryDto> computedPointCategories = new ArrayList<>();
    Iterator<AdComputedPointFunctionCategoryEntity> iterator = entities.iterator();
    while (iterator.hasNext()) {
      
      AdComputedPointFunctionCategoryDto dto = mapToDto(iterator.next());
      if (!dto.getComputedPointTemplates().isEmpty()) {
        computedPointCategories.add(dto);  
      }
    }
    Collections.sort(computedPointCategories);
    return AdComputedPointFunctionTemplateHierarchyDto
        .builder()
        .withComputedPointCategories(computedPointCategories)
        .build();  
  }
  
  public static AdComputedPointFunctionCategoryDto mapToDto(AdComputedPointFunctionCategoryEntity category) {

    List<AdComputedPointFunctionTemplateDto> computedPointTemplates = new ArrayList<>();
    Iterator<AdComputedPointFunctionTemplateEntity> iterator = category.getComputedPointFunctionTemplates().iterator();
    while (iterator.hasNext()) {
      
      AdComputedPointFunctionTemplateEntity adComputedPointTemplate = iterator.next();
      if (AdFunctionEvaluator.INCLUDE_BETA_FUNCTION_TEMPLATES 
          || !(adComputedPointTemplate.getIsBeta() != null && adComputedPointTemplate.getIsBeta())) {
        
        computedPointTemplates.add(mapToComputedPointTemplateHierarchyDto(adComputedPointTemplate));  
      }
    }
    Collections.sort(computedPointTemplates);
    return AdComputedPointFunctionCategoryDto
        .builder()
        .withName(category.getEnergyExchangeType().getName())
        .withEquipmentTypeTag(category.getEnergyExchangeType().getName())
        .withComputedPointTemplates(computedPointTemplates)
        .build();
  }

  public static AdComputedPointFunctionTemplateDto mapToComputedPointTemplateHierarchyDto(AbstractAdFunctionTemplateEntity e) {
    
    List<AdComputedPointFunctionTemplateInputConstant> inputConstants = new ArrayList<>();
    Iterator<AdFunctionTemplateInputConstantEntity> inputConstantIterator = e.getInputConstants().iterator();
    while (inputConstantIterator.hasNext()) {
      
      AdFunctionTemplateInputConstantEntity ice = inputConstantIterator.next();
      
      inputConstants.add(AdComputedPointFunctionTemplateInputConstant
          .builder()
          .withId(ice.getPersistentIdentity())
          .withDataTypeId(ice.getDataType().getId())
          .withUnitId(ice.getUnit().getPersistentIdentity())
          .withSequenceNumber(ice.getSequenceNumber())
          .withDescription(ice.getDescription())
          .withDefaultValue(ice.getDefaultValue())
          .withName(ice.getName())
          .withIsRequired(ice.getIsRequired())
          .build());
    }
    Collections.sort(inputConstants);

    List<AdComputedPointFunctionTemplateInputPoint> inputPoints = new ArrayList<>();
    Iterator<AdFunctionTemplateInputPointEntity> inputPointIterator = e.getInputPoints().iterator();
    while (inputPointIterator.hasNext()) {
      
      AdFunctionTemplateInputPointEntity ipe = inputPointIterator.next();
      
      inputPoints.add(AdComputedPointFunctionTemplateInputPoint
          .builder()
          .withId(ipe.getPersistentIdentity())
          .withName(ipe.getName())
          .withDescription(ipe.getDescription())
          .withCurrentObjectExpression(ipe.getCurrentObjectExpression())
          .withIsRequired(ipe.getIsRequired())
          .withIsArray(ipe.getIsArray())
          .withSequenceNumber(ipe.getSequenceNumber())
          .withTags(ipe.getNormalizedTags())
          .build());
    }
    Collections.sort(inputPoints);
        
    List<AdComputedPointFunctionTemplateOutputPoint> outputPoints = new ArrayList<>();
    Iterator<AdFunctionTemplateOutputPointEntity> outputPointIterator = e.getOutputPoints().iterator();
    while (outputPointIterator.hasNext()) {
      
      AdFunctionTemplateOutputPointEntity ope = outputPointIterator.next();

      outputPoints.add(AdComputedPointFunctionTemplateOutputPoint
          .builder()
          .withId(ope.getPersistentIdentity())
          .withDescription(ope.getDescription())
          .withDataTypeId(ope.getDataType().getId())
          .withUnitId(ope.getUnit().getPersistentIdentity())
          .withRange(ope.getRange())
          .withSequenceNumber(ope.getSequenceNumber())
          .withTags(ope.getNormalizedTags())
          .build());
    }
    Collections.sort(outputPoints);

    return AdComputedPointFunctionTemplateDto
        .builder()
        .withId(e.getPersistentIdentity())
        .withFunctionId(e.getAdFunction().getPersistentIdentity())
        .withName(e.getName())
        .withDisplayName(((AdComputedPointFunctionTemplateEntity)e).getReferenceNumber() + " " + e.getDisplayName())
        .withDescription(e.getDescription())
        .withEquipmentTypeId(e.getEnergyExchangeType().getPersistentIdentity())
        .withEquipmentType(e.getEnergyExchangeType().getName())
        .withInputConstants(inputConstants)
        .withInputPoints(inputPoints)
        .withOutputPoints(outputPoints)
        .build();
  }
  
  public Map<Integer, Integer> getAdFunctionTemplateInputConstantPointTemplateMappings() {
    return this.adFunctionTemplateInputConstantPointTemplateMappings;
  }

  public Integer getAdFunctionTemplateInputConstantPointTemplateMapping(Integer adFunctionTemplateInputConstantId) {
  
    return adFunctionTemplateInputConstantPointTemplateMappings.get(adFunctionTemplateInputConstantId);
  }
}
//@formatter:on