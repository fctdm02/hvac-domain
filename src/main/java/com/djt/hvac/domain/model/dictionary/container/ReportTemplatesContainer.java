//@formatter:off
// This class houses all the report template entities
package com.djt.hvac.domain.model.dictionary.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.common.validation.SimpleValidationMessage;
import com.djt.hvac.domain.model.dictionary.dto.report.ReportTemplateDto;
import com.djt.hvac.domain.model.dictionary.dto.report.ReportTemplateEquipmentSpecDto;
import com.djt.hvac.domain.model.dictionary.dto.report.ReportTemplatePointSpecDto;
import com.djt.hvac.domain.model.dictionary.energyexchange.AbstractEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.EquipmentEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.LoopEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.PlantEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.template.function.rule.AdRuleFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.AbstractNodeTagTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.report.AbstractReportTemplatePointSpecEntity;
import com.djt.hvac.domain.model.dictionary.template.report.ReportTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.report.ReportTemplateEquipmentSpecEntity;
import com.djt.hvac.domain.model.dictionary.template.report.ReportTemplateRulePointSpecEntity;
import com.djt.hvac.domain.model.dictionary.template.report.ReportTemplateStandardPointSpecEntity;

public class ReportTemplatesContainer {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReportTemplatesContainer.class);

  private static final Map<String, String> CURRENT_OBJECT_EXPRESSION_DESCRIPTIONS = new HashMap<>();
  static {
    
    CURRENT_OBJECT_EXPRESSION_DESCRIPTIONS.put("ancestor(type=building)", 
        "ancestor building");

    CURRENT_OBJECT_EXPRESSION_DESCRIPTIONS.put("ancestor(type=building).child(tags=off_prem_weather_station|*)", 
        "ancestor building off prem weather station");

    CURRENT_OBJECT_EXPRESSION_DESCRIPTIONS.put("ancestor(type=building).child(tags=on_prem_weather_station|*)", 
        "ancestor building on prem weather station");
    
    CURRENT_OBJECT_EXPRESSION_DESCRIPTIONS.put("parent(model=airSupply,type=equipment,tags=ahu|*)", 
        "parent AHU (air supply system)");
    
    CURRENT_OBJECT_EXPRESSION_DESCRIPTIONS.put("parentEquipment(tags=ahu|*)", 
        "parent AHU (air supply system)");
    
    CURRENT_OBJECT_EXPRESSION_DESCRIPTIONS.put("parent(model=airSupply,type=equipment)", 
        "parent equipment (air supply system)");
  }
  
  private final Map<Integer, ReportTemplateEntity> reportTemplates;
  private final Map<Integer, String> pointSpecErrorMessages;
  private final List<String> containerAuditMessages = new ArrayList<>();
  
  public ReportTemplatesContainer(Map<Integer, ReportTemplateEntity> reportTemplates) {
    super();
    this.reportTemplates = new HashMap<>();
    this.pointSpecErrorMessages = new HashMap<>();
    Iterator<ReportTemplateEntity> iterator = reportTemplates.values().iterator();
    while (iterator.hasNext()) {

      addReportTemplate(iterator.next());
    }
  }

  public void addReportTemplate(ReportTemplateEntity reportTemplate) {

    List<SimpleValidationMessage> messages = reportTemplate.validateSimple();

    if (LOGGER.isInfoEnabled()) {
      List<SimpleValidationMessage> infoMessages = SimpleValidationMessage.getInfoLevelMessages(messages);
      if (!infoMessages.isEmpty()) {
        LOGGER.info("Report template: [{}] has info level issues: [{}]",
            reportTemplate,
            infoMessages);
      }
    }

    List<SimpleValidationMessage> errorMessages = SimpleValidationMessage.getErrorLevelMessages(messages);
    if (errorMessages.isEmpty()) {

      reportTemplates.put(reportTemplate.getPersistentIdentity(), reportTemplate);
      
      addPointErrorMessages(reportTemplate);

    } else {

      LOGGER.error("Report template: [{}] has error level issues: [{}]",
          reportTemplate,
          errorMessages);
    }
  }
  
  private void addPointErrorMessages(ReportTemplateEntity reportTemplate) {
    
    for (ReportTemplateEquipmentSpecEntity equipmentSpec: reportTemplate.getEquipmentSpecs()) {
      
      StringBuilder sb = new StringBuilder(256);
      sb.append("Point constraint: [")
          .append(equipmentSpec.getTupleConstraintExpression())
          .append("] was not satisfied");
      
      pointSpecErrorMessages.put(Integer.valueOf(equipmentSpec.getPersistentIdentity().intValue() * -1), sb.toString());
      
      AbstractEnergyExchangeTypeEntity energyExchangeType = equipmentSpec.getEnergyExchangeType();
      
      for (AbstractReportTemplatePointSpecEntity pointSpec: equipmentSpec.getPointSpecs()) {
        
        if (pointSpec instanceof ReportTemplateStandardPointSpecEntity) {
          
          ReportTemplateStandardPointSpecEntity standardPointSpec = (ReportTemplateStandardPointSpecEntity)pointSpec;
          
          sb.setLength(0);
          sb.append("Expected ");
          
          boolean isRequired = pointSpec.isRequired();
          boolean isArray = pointSpec.isArray();
          
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
              .getPointTemplateByTags(standardPointSpec.getTags());

          sb.append(pointTemplate.getName())
              .append(" ")
              .append(pointTemplate.getTags().toString().replace("[", "(").replace("]", ")"));
          
          if (!isArray) {
            sb.append(" point");
          } else {
            sb.append(" points");
          }
          sb.append(" for ");
          
          String currentObjectExpression = standardPointSpec.getCurrentObjectExpression();
          if (currentObjectExpression != null) {
            sb.append(CURRENT_OBJECT_EXPRESSION_DESCRIPTIONS.get(currentObjectExpression));
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
          
          pointSpecErrorMessages.put(standardPointSpec.getPersistentIdentity(), sb.toString());
          
          // While we are iterating through the input points, do an audit to see if the energy exchange type is compatible with the point template for the input point.
          if (standardPointSpec.getCurrentObjectExpression() == null) {

            Set<PointTemplateEntity> energyExchangeTypePointTemplates = DictionaryContext
                .getNodeTagTemplatesContainer()
                .getEnergyExchangeLevelPointTemplatesForEnergyExchangeType(energyExchangeType);
            
            if (!energyExchangeTypePointTemplates.contains(pointTemplate)) {
              containerAuditMessages.add(reportTemplate.getName() + " INPUT POINT: " + standardPointSpec.getName() + " IS ASSOCIATED WITH POINT TEMPLATE: " + pointTemplate + " THAT ISN'T COMPATIBLE WITH ENERGY EXCHANGE TYPE: " + energyExchangeType);
            }
          }
        } else if (pointSpec instanceof ReportTemplateRulePointSpecEntity) {
          
          ReportTemplateRulePointSpecEntity rulePointSpec = (ReportTemplateRulePointSpecEntity)pointSpec;
          
          sb.setLength(0);
          sb.append("Expected ");
          
          boolean isRequired = pointSpec.isRequired();
          boolean isArray = pointSpec.isArray();
          
          if (!isRequired && !isArray) { // 00
            
            sb.append("zero or one ");
            
          } else if (!isRequired && isArray) { // 01
            
            sb.append("zero or more ");
            
          } else if (isRequired && !isArray) { // 10
            
            sb.append("one ");
            
          } else if (isRequired && isArray) { // 11
            
            sb.append("one or more ");
            
          }
          
          if (isArray) {
            sb.append("instances of rule ");  
          } else {
            sb.append("instance of rule ");
          }

          AdRuleFunctionTemplateEntity ruleTemplate = rulePointSpec.getRuleTemplate();
          sb.append(ruleTemplate.getFullDisplayName());
          
          sb.append(" for ");
          
          String currentObjectExpression = rulePointSpec.getCurrentObjectExpression();
          if (currentObjectExpression != null) {
            sb.append(CURRENT_OBJECT_EXPRESSION_DESCRIPTIONS.get(currentObjectExpression));
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
          
          pointSpecErrorMessages.put(rulePointSpec.getPersistentIdentity(), sb.toString());
        }
      }
    }
  }  
  
  /**
   * 
   * @return A list of Report Templates Container audit messages
   */
  public List<String> getContainerAuditMessages() {
    return containerAuditMessages;
  }
  
  /**
   * 
   * @param pointSpecId A negative value will return the equipment spec tuple constraint error message
   * 
   * @return The error message corresponding to the given point spec id, or if a negative value, the
   *         corresponding equipment spec tuple constraint error message
   */
  public String getPointSpecErrorMessage(Integer pointSpecId) {
    
    String errorMessage = this.pointSpecErrorMessages.get(pointSpecId);
    if (errorMessage == null) {
      
      throw new IllegalStateException("Point spec with id: [" + pointSpecId + "] does not exist.");
    }
    return errorMessage;
  }
  
  private transient Set<Integer> _ruleTemplateIds = null;
  
  /**
   * 
   * @return The set of rule template ids associated with reports
   */
  public Set<Integer> getReportAssociatedRuleTemplateIds() {
    
    if (_ruleTemplateIds == null) {

      _ruleTemplateIds = new HashSet<>();
      for (ReportTemplateEntity reportTemplate: getReportTemplates()) {
        
        for (ReportTemplateEquipmentSpecEntity equipmentSpec: reportTemplate.getEquipmentSpecs()) {
          
          for (AbstractReportTemplatePointSpecEntity pointSpec: equipmentSpec.getPointSpecs()) {
            
            if (pointSpec instanceof ReportTemplateRulePointSpecEntity) {
              
              ReportTemplateRulePointSpecEntity rulePointSpec = (ReportTemplateRulePointSpecEntity)pointSpec;
              
              AdRuleFunctionTemplateEntity ruleTemplate = rulePointSpec.getRuleTemplate();
              
              _ruleTemplateIds.add(ruleTemplate.getPersistentIdentity());
            }
          }
        }
      }
    }
    return _ruleTemplateIds;
  }

  public Set<ReportTemplateEntity> getReportTemplates() {

    Set<ReportTemplateEntity> set = new TreeSet<>();
    set.addAll(reportTemplates.values());
    return set;
  }

  public List<Integer> getReportTemplateIds() {

    List<Integer> list = new ArrayList<>();
    for (ReportTemplateEntity reportTemplate: reportTemplates.values()) {
      list.add(reportTemplate.getPersistentIdentity());
    }
    return list;
  }
  
  public ReportTemplateEntity getReportTemplate(Integer reportTemplateId) {

    return this.reportTemplates.get(reportTemplateId);
  }

  public static ReportTemplatesContainer mapFromDtos(List<ReportTemplateDto> dtoList) {

    Map<Integer, ReportTemplateEntity> map = new HashMap<>();
    Iterator<ReportTemplateDto> iterator = dtoList.iterator();
    while (iterator.hasNext()) {

      ReportTemplateDto dto = iterator.next();

      Integer id = dto.getId();
      ReportTemplateEntity reportTemplate = new ReportTemplateEntity(
          id,
          dto.getName(),
          dto.getDescription(),
          dto.getIsInternal(),
          dto.getIsBeta());

      try {
        mapFromReportTemplateDtos(reportTemplate, dto.getEquipmentSpecs());
      } catch (EntityAlreadyExistsException | EntityDoesNotExistException e) {
        throw new IllegalStateException("Unable to map from DTOs: " + dtoList, e);
      }

      map.put(id, reportTemplate);
    }
    return new ReportTemplatesContainer(map);
  }

  private static void mapFromReportTemplateDtos(ReportTemplateEntity reportTemplate, List<ReportTemplateEquipmentSpecDto> dtoList) throws EntityAlreadyExistsException, EntityDoesNotExistException {

    Iterator<ReportTemplateEquipmentSpecDto> iterator = dtoList.iterator();
    while (iterator.hasNext()) {

      ReportTemplateEquipmentSpecDto dto = iterator.next();

      ReportTemplateEquipmentSpecEntity equipmentSpec = new ReportTemplateEquipmentSpecEntity(
          dto.getId(),
          reportTemplate,
          DictionaryContext.getTagsContainer().getEnergyExchangeTypeById(dto.getEquipmentTypeId()),
          dto.getNodeFilterExpression(),
          dto.getNodeFilterErrorMessage(),
          dto.getTupleConstraintExpression(),
          dto.getTupleConstraintErrorMessage());

      mapFromEquipmentSpecDtos(equipmentSpec, dto.getPointSpecs());

      reportTemplate.addEquipmentSpec(equipmentSpec);
    }
  }

  private static void mapFromEquipmentSpecDtos(ReportTemplateEquipmentSpecEntity equipmentSpec, List<ReportTemplatePointSpecDto> dtoList) throws EntityAlreadyExistsException, EntityDoesNotExistException {

    TagsContainer tagsContainer = DictionaryContext.getTagsContainer();
    AdFunctionTemplatesContainer adFunctionTemplatesContainer = DictionaryContext.getAdFunctionTemplatesContainer();

    Iterator<ReportTemplatePointSpecDto> iterator = dtoList.iterator();
    while (iterator.hasNext()) {

      ReportTemplatePointSpecDto dto = iterator.next();

      AbstractReportTemplatePointSpecEntity pointSpec = null;
      if (dto.getType().equals(ReportTemplatePointSpecDto.TYPE_STANDARD)) {

        pointSpec = new ReportTemplateStandardPointSpecEntity(
            dto.getId(),
            equipmentSpec,
            dto.getName(),
            dto.getIsRequired(),
            dto.getIsArray(),
            dto.getCurrentObjectExpression(),
            dto.getErrorMessage(),
            dto.getRequiredDataTypeId(),
            tagsContainer.getHaystackTagsByName(dto.getTags()));
      } else {

        pointSpec = new ReportTemplateRulePointSpecEntity(
            dto.getId(),
            equipmentSpec,
            dto.getName(),
            dto.getIsRequired(),
            dto.getCurrentObjectExpression(),
            dto.getErrorMessage(),
            dto.getRequiredDataTypeId(),
            adFunctionTemplatesContainer.getRuleFunctionTemplate(dto.getRuleTemplateId()));
      }
      equipmentSpec.addPointSpec(pointSpec);
    }
  }

  public static List<ReportTemplateDto> mapToDtos(ReportTemplatesContainer reportTemplatesContainer) {

    List<ReportTemplateDto> dtoList = new ArrayList<>();
    Iterator<ReportTemplateEntity> iterator = reportTemplatesContainer.getReportTemplates().iterator();
    while (iterator.hasNext()) {

      ReportTemplateEntity reportTemplate = iterator.next();

      ReportTemplateDto reportTemplateDto = ReportTemplateDto
          .builder()
          .withId(reportTemplate.getPersistentIdentity())
          .withName(reportTemplate.getName())
          .withDescription(reportTemplate.getDescription())
          .withIsInternal(reportTemplate.getIsInternal())
          .withIsBeta(reportTemplate.getIsBeta())
          .withEquipmentSpecs(mapToEquipmentSpecDtos(reportTemplate.getEquipmentSpecs()))
          .build();

      dtoList.add(reportTemplateDto);
    }
    return dtoList;
  }

  public static List<ReportTemplateEquipmentSpecDto> mapToEquipmentSpecDtos(Set<ReportTemplateEquipmentSpecEntity> equipmentSpecs) {

    List<ReportTemplateEquipmentSpecDto> dtoList = new ArrayList<>();
    Iterator<ReportTemplateEquipmentSpecEntity> iterator = equipmentSpecs.iterator();
    while (iterator.hasNext()) {

      ReportTemplateEquipmentSpecEntity equipmentSpec = iterator.next();

      dtoList.add(ReportTemplateEquipmentSpecDto
          .builder()
          .withId(equipmentSpec.getPersistentIdentity())
          .withEquipmentTypeId(equipmentSpec.getEnergyExchangeType().getPersistentIdentity())
          .withEquipmentTypeName(equipmentSpec.getEnergyExchangeType().getName())
          .withNodeFilterExpression(equipmentSpec.getNodeFilterExpression())
          .withNodeFilterErrorMessage(equipmentSpec.getNodeFilterErrorMessage())
          .withTupleConstraintExpression(equipmentSpec.getTupleConstraintExpression())
          .withTupleConstraintErrorMessage(equipmentSpec.getTupleConstraintErrorMessage())
          .withPointSpecs(mapToPointSpecDtos(equipmentSpec.getPointSpecs()))
          .build());
    }
    return dtoList;
  }

  public static List<ReportTemplatePointSpecDto> mapToPointSpecDtos(Set<AbstractReportTemplatePointSpecEntity> pointSpecs) {

    List<ReportTemplatePointSpecDto> dtoList = new ArrayList<>();
    Iterator<AbstractReportTemplatePointSpecEntity> iterator = pointSpecs.iterator();
    while (iterator.hasNext()) {

      AbstractReportTemplatePointSpecEntity pointSpec = iterator.next();

      if (pointSpec instanceof ReportTemplateStandardPointSpecEntity) {

        dtoList.add(ReportTemplatePointSpecDto
            .builder()
            .withId(pointSpec.getPersistentIdentity())
            .withName(pointSpec.getName())
            .withType(ReportTemplatePointSpecDto.TYPE_STANDARD)
            .withIsRequired(pointSpec.isRequired())
            .withIsArray(pointSpec.isArray())
            .withCurrentObjectExpression(pointSpec.getCurrentObjectExpression())
            .withErrorMessage(pointSpec.getErrorMessage())
            .withTags(((ReportTemplateStandardPointSpecEntity)pointSpec).getTagNames())
            .withRequiredDataTypeId(pointSpec.getRequiredDataTypeId())
            .build());

      } else if (pointSpec instanceof ReportTemplateRulePointSpecEntity) {

        dtoList.add(ReportTemplatePointSpecDto
            .builder()
            .withId(pointSpec.getPersistentIdentity())
            .withName(pointSpec.getName())
            .withType(ReportTemplatePointSpecDto.TYPE_RULE)
            .withIsRequired(pointSpec.isRequired())
            .withCurrentObjectExpression(pointSpec.getCurrentObjectExpression())
            .withErrorMessage(pointSpec.getErrorMessage())
            .withRuleTemplateId(((ReportTemplateRulePointSpecEntity) pointSpec).getRuleTemplate().getPersistentIdentity())
            .withRequiredDataTypeId(pointSpec.getRequiredDataTypeId())
            .build());

      } else {
        throw new IllegalStateException("Expected an insance of ReportTemplateStandardPointSpecEntity or ReportTemplateRulePointSpecEntity, but encountered: ["
            + pointSpec.getClassAndNaturalIdentity()
            + "]");
      }
    }
    return dtoList;
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("ReportsContainer [reportTemplates=")
        .append(reportTemplates)
        .append("]")
        .toString();
  }
}
//@formatter:on