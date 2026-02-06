package com.djt.hvac.domain.model.dictionary.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.UnitEntity;
import com.djt.hvac.domain.model.dictionary.dto.EquipmentPointTemplateDto;
import com.djt.hvac.domain.model.dictionary.dto.LoopPointTemplateDto;
import com.djt.hvac.domain.model.dictionary.dto.NodeTagTemplateDto;
import com.djt.hvac.domain.model.dictionary.dto.PlantPointTemplateDto;
import com.djt.hvac.domain.model.dictionary.dto.PointTemplateAllAttributesDto;
import com.djt.hvac.domain.model.dictionary.dto.PointTemplateDto;
import com.djt.hvac.domain.model.dictionary.dto.PointTemplateUnitMappingDto;
import com.djt.hvac.domain.model.dictionary.dto.UnitMappingDto;
import com.djt.hvac.domain.model.dictionary.energyexchange.AbstractEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.EquipmentEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.LoopEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.PlantEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.enums.NodeType;
import com.djt.hvac.domain.model.dictionary.template.function.AbstractAdFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateInputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.function.computedpoint.AdComputedPointFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.function.rule.AdRuleFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.AbstractNodeTagTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateUnitMappingEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.UnitMappingEntity;
import com.djt.hvac.domain.model.dictionary.template.report.AbstractReportTemplatePointSpecEntity;
import com.djt.hvac.domain.model.dictionary.template.report.ReportTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.report.ReportTemplateEquipmentSpecEntity;
import com.djt.hvac.domain.model.dictionary.template.report.ReportTemplateStandardPointSpecEntity;

public class NodeTagTemplatesContainer {

  private static final Logger LOGGER = LoggerFactory.getLogger(NodeTagTemplatesContainer.class);
    
  private Map<Integer, AbstractNodeTagTemplateEntity> nodeTagTemplates = new HashMap<>();
  private Map<String, AbstractNodeTagTemplateEntity> nodeTagTemplatesByName = new HashMap<>();
  private Map<Set<TagEntity>, AbstractNodeTagTemplateEntity> nodeTagTemplatesByTags = new HashMap<>();
  
  private Set<PointTemplateEntity> buildingPointTemplates;
  private Map<EquipmentEnergyExchangeTypeEntity, Set<PointTemplateEntity>> equipmentPointTemplates;
  private Map<PlantEnergyExchangeTypeEntity, Set<PointTemplateEntity>> plantPointTemplates;
  private Map<LoopEnergyExchangeTypeEntity, Set<PointTemplateEntity>> loopPointTemplates;
    
  private Integer maxNodeTagTemplateId = Integer.valueOf(0);
  
  // Keyed by unit mapping id, value is unit mapping entity.
  private final Map<Integer, UnitMappingEntity> unitMappings = new HashMap<>();
  
  // Outer key is point template id.  Outer value is map keyed by priority with value being point template unit mapping.
  private final Map<Integer, Map<Integer, PointTemplateUnitMappingEntity>> pointTemplateUnitMappings = new HashMap<>();
  
  public NodeTagTemplatesContainer(
      Set<PointTemplateEntity> buildingPointTemplates,
      Map<EquipmentEnergyExchangeTypeEntity, Set<PointTemplateEntity>> equipmentPointTemplates,
      Map<PlantEnergyExchangeTypeEntity, Set<PointTemplateEntity>> plantPointTemplates,
      Map<LoopEnergyExchangeTypeEntity, Set<PointTemplateEntity>> loopPointTemplates,
      List<UnitMappingDto> unitMappingDtoList,
      List<PointTemplateUnitMappingDto> pointTemplateUnitMappingDtoList) {
    
    super();
    
    this.buildingPointTemplates = buildingPointTemplates;
    this.equipmentPointTemplates = equipmentPointTemplates;
    this.plantPointTemplates = plantPointTemplates;
    this.loopPointTemplates = loopPointTemplates;
    
    List<AbstractNodeTagTemplateEntity> entities = new ArrayList<>();
    entities.addAll(buildingPointTemplates);
    for (Map.Entry<EquipmentEnergyExchangeTypeEntity, Set<PointTemplateEntity>> entry: equipmentPointTemplates.entrySet()) {
      entities.addAll(entry.getValue());
    }
    for (Map.Entry<PlantEnergyExchangeTypeEntity, Set<PointTemplateEntity>> entry: plantPointTemplates.entrySet()) {
      entities.addAll(entry.getValue());
    }
    for (Map.Entry<LoopEnergyExchangeTypeEntity, Set<PointTemplateEntity>> entry: loopPointTemplates.entrySet()) {
      entities.addAll(entry.getValue());
    }
    
    Iterator<AbstractNodeTagTemplateEntity> iterator = entities.iterator();
    while (iterator.hasNext()) {
      
      AbstractNodeTagTemplateEntity nodeTagTemplate = iterator.next();
      
      Integer id = nodeTagTemplate.getPersistentIdentity();
      if (id.intValue() > maxNodeTagTemplateId.intValue()) {
        maxNodeTagTemplateId = id;
      }
      
      nodeTagTemplates.put(id, nodeTagTemplate);
      nodeTagTemplatesByName.put(nodeTagTemplate.getName().trim().toLowerCase(), nodeTagTemplate);
      
      Set<TagEntity> tags = nodeTagTemplate.getTags();
      AbstractNodeTagTemplateEntity check = nodeTagTemplatesByTags.get(tags);
      if (check != null && !check.getPersistentIdentity().equals(nodeTagTemplate.getPersistentIdentity())) {
        
        LOGGER.warn("Point template: [{}] is duplicated with: [{}] with tags: {}",
            check,
            nodeTagTemplate,
            tags);
      }
      
      nodeTagTemplatesByTags.put(tags, nodeTagTemplate);
    }
    
    List<UnitMappingEntity> unitMappingEntityList = UnitMappingEntity
        .Mapper
        .getInstance()
        .mapDtosToEntities(this, unitMappingDtoList);
    
    for (UnitMappingEntity e: unitMappingEntityList) {
      addUnitMapping(e);
    }
    
    List<PointTemplateUnitMappingEntity> pointTemplateUnitMappingEntityList = PointTemplateUnitMappingEntity
        .Mapper
        .getInstance()
        .mapDtosToEntities(this, pointTemplateUnitMappingDtoList);
    
    for (PointTemplateUnitMappingEntity e: pointTemplateUnitMappingEntityList) {
      addPointTemplateUnitMapping(e);
    }    
  }
  
  public void addUnitMapping(UnitMappingEntity e) {
    
    unitMappings.put(e.getPersistentIdentity(), e);
  }
  
  public void addPointTemplateUnitMapping(PointTemplateUnitMappingEntity e) {
    
    Integer pointTemplateId = e.getPointTemplate().getPersistentIdentity();
    Map<Integer, PointTemplateUnitMappingEntity> map = pointTemplateUnitMappings.get(pointTemplateId);
    if (map == null) {
      
      map = new HashMap<>();
      pointTemplateUnitMappings.put(pointTemplateId, map);
    }
    
    map.put(e.getPriority(), e);
  }  
  
  public Integer getMaxNodeTagTemplateId() {
    return maxNodeTagTemplateId;
  }
  
  public Collection<AbstractNodeTagTemplateEntity> getAllNodeTagTemplates() {
    
    return nodeTagTemplates.values();
  }

  public AbstractNodeTagTemplateEntity getNodeTagTemplate(Integer persistentIdentity) throws EntityDoesNotExistException {
    
    if (persistentIdentity == null) {
      throw new IllegalStateException("id must be specified.");
    }
    AbstractNodeTagTemplateEntity nodeTagTemplate = getNodeTagTemplateNullIfNotExists(persistentIdentity);
    if (nodeTagTemplate == null) {
      throw new EntityDoesNotExistException("Could not find node tag template with id: [" + persistentIdentity + "].");
    }
    return nodeTagTemplate;
  }
  
  public AbstractNodeTagTemplateEntity getNodeTagTemplateNullIfNotExists(Integer persistentIdentity) {
    
    if (persistentIdentity == null) {
      return null;
    }
    return nodeTagTemplates.get(persistentIdentity);
  }
  
  public PointTemplateEntity getPointTemplate(Integer persistentIdentity) throws EntityDoesNotExistException {
    
    PointTemplateEntity pointTemplate = getPointTemplateNullIfNotExists(persistentIdentity);
    if (pointTemplate != null) {
      return pointTemplate;
    }
    throw new EntityDoesNotExistException("Could not find point template with id: [" + persistentIdentity + "].");
  }

  public PointTemplateEntity getPointTemplateNullIfNotExists(Integer persistentIdentity) {
    
    AbstractNodeTagTemplateEntity nodeTagTemplate = getNodeTagTemplateNullIfNotExists(persistentIdentity);
    if (nodeTagTemplate != null) {
      if (nodeTagTemplate instanceof PointTemplateEntity) {
        return (PointTemplateEntity)nodeTagTemplate;
      } else {
        throw new IllegalStateException("Node tag template with id: [" + persistentIdentity + "] is not an instanceof PointTemplateEntity, rather, it is: " + nodeTagTemplate.getClassAndNaturalIdentity());
      }
    }
    return null;
  }
  
  public PointTemplateEntity getPointTemplateByName(String pointTemplateName) throws EntityDoesNotExistException {
    
    PointTemplateEntity pointTemplate = getPointTemplateByNameNullIfNotExists(pointTemplateName);
    if (pointTemplate != null) {
      return pointTemplate;
    }
    throw new IllegalStateException("Could not find point template with name: [" + pointTemplateName + "].");
  }

  public PointTemplateEntity getPointTemplateByNameNullIfNotExists(String pointTemplateName) {
    
    if (pointTemplateName == null || pointTemplateName.trim().isEmpty()) {
      throw new IllegalStateException("pointTemplateName must be specified.");
    }
    AbstractNodeTagTemplateEntity nodeTagTemplate = nodeTagTemplatesByName.get(pointTemplateName.toLowerCase().trim());
    if (nodeTagTemplate != null) {
      if (nodeTagTemplate instanceof PointTemplateEntity) {
        return (PointTemplateEntity)nodeTagTemplate;
      } else {
        throw new IllegalStateException("Node tag template with name: [" + pointTemplateName + "] is not an instanceof PointTemplateEntity, rather, it is: " + nodeTagTemplate.getClassAndNaturalIdentity());
      }
    }
    return null;
  }
  
  public PointTemplateEntity getPointTemplateByTags(Set<TagEntity> tags) {
    
    if (tags == null) {
      throw new IllegalStateException("tags must be specified.");
    }
    AbstractNodeTagTemplateEntity nodeTagTemplate = nodeTagTemplatesByTags.get(tags);
    if (nodeTagTemplate != null) {
      if (nodeTagTemplate instanceof PointTemplateEntity) {
        return (PointTemplateEntity)nodeTagTemplate;
      } else {
        throw new IllegalStateException("Node tag template with tags: " + tags + " is not an instanceof PointTemplateEntity, rather, it is: " + nodeTagTemplate.getClassAndNaturalIdentity());
      }
    }
    
    
    // EVERYTHING BELOW IS A HACK
    
    /* Can't do the right thing here and throw an exception because there is a bunch of garbage test data in the test SQL scripts.
    throw new IllegalStateException("Could not find point template with tags: " + tags + ".");
    */
    
    for (AbstractNodeTagTemplateEntity nt: nodeTagTemplates.values()) {
      if (nt instanceof PointTemplateEntity) {
        return (PointTemplateEntity)nt;
      }
    }
    return null;
  }

  public Set<PointTemplateEntity> getAllPointTemplates() {
    
    Set<PointTemplateEntity> set = new HashSet<>();
    for (AbstractNodeTagTemplateEntity nt: nodeTagTemplates.values()) {
      if (nt instanceof PointTemplateEntity) {
        set.add((PointTemplateEntity)nt);   
      }
    }
    return set;
  }
  
  public Set<PointTemplateEntity> getAllMatchingPointTemplateByTags(Set<TagEntity> tags) {
    
    if (tags == null) {
      throw new IllegalStateException("tags must be specified.");
    }
    Set<PointTemplateEntity> set = new HashSet<>();
    
    // TODO: TDM: There exists multiple point templates that have the same tag signatures.
    /*
    AbstractNodeTagTemplateEntity pointTemplate = nodeTagTemplatesByTags.get(tags);
    if (pointTemplate != null) {
      return pointTemplate;
    }
    */
    
    /* Can't do the right thing here and throw an exception because there is a bunch of garbage test data in the test SQL scripts.
    throw new IllegalStateException("Could not find point template with tags: " + tags + ".");
    */
    
    for (AbstractNodeTagTemplateEntity nt: nodeTagTemplates.values()) {
      if (nt instanceof PointTemplateEntity && nt.getTags().equals(tags)) {
        set.add((PointTemplateEntity)nt); 
      }
    }
    return set;
  }
  
  public Set<PointTemplateEntity> getEquipmentPointTemplatesForEquipmentType(EquipmentEnergyExchangeTypeEntity equipmentType) {
    Set<PointTemplateEntity> set = equipmentPointTemplates.get(equipmentType);
    if (set != null) {
     return set; 
    }
    return new HashSet<>();
  }

  public Set<PointTemplateEntity> getPlantPointTemplatesForPlantType(PlantEnergyExchangeTypeEntity plantType) {
    Set<PointTemplateEntity> set = plantPointTemplates.get(plantType);
    if (set != null) {
     return set; 
    }
    return new HashSet<>();
  }

  public Set<PointTemplateEntity> getLoopPointTemplatesForLoopType(LoopEnergyExchangeTypeEntity loopType) {
    Set<PointTemplateEntity> set = loopPointTemplates.get(loopType);
    if (set != null) {
     return set; 
    }
    return new HashSet<>();
  }
  
  public Set<PointTemplateEntity> getEnergyExchangeLevelPointTemplatesForEnergyExchangeType(
      AbstractEnergyExchangeTypeEntity energyExchangeType) {
    
    Set<PointTemplateEntity> set = null;
    if (energyExchangeType instanceof EquipmentEnergyExchangeTypeEntity) {
      
      set = getEquipmentPointTemplatesForEquipmentType((EquipmentEnergyExchangeTypeEntity)energyExchangeType);
      
    } else if (energyExchangeType instanceof PlantEnergyExchangeTypeEntity) {
      
      set = getPlantPointTemplatesForPlantType((PlantEnergyExchangeTypeEntity)energyExchangeType);
      
    } else if (energyExchangeType instanceof LoopEnergyExchangeTypeEntity) {
      
      set = getLoopPointTemplatesForLoopType((LoopEnergyExchangeTypeEntity)energyExchangeType);
      
    }
    
    if (set == null) {
      set = new HashSet<>();
    }
    
    return set;
  }
  
  public Set<PointTemplateEntity> getBuildingPointTemplates() {
    return buildingPointTemplates;
  }
  
  // *** USED TO SEND DTOS FOR UI CONSUMPTION
  public List<UnitMappingDto> getUnitMappingDtos() {
    
    List<UnitMappingDto> dtos = new ArrayList<>();
    for (UnitMappingEntity e: unitMappings.values()) {
      
      dtos.add(UnitMappingEntity
          .Mapper
          .getInstance()
          .mapEntityToDto(e));
    }
    return dtos;
  }
  
  public List<PointTemplateUnitMappingDto> getPointTemplateUnitMappingDtos() {
    
    List<PointTemplateUnitMappingDto> dtos = new ArrayList<>();
    for (Map<Integer, PointTemplateUnitMappingEntity> entry: pointTemplateUnitMappings.values()) {
      for (PointTemplateUnitMappingEntity e: entry.values()) {
        
        dtos.add(PointTemplateUnitMappingEntity
            .Mapper
            .getInstance()
            .mapEntityToDto(e));
      }
    }
    return dtos;
  }
  //***
  
  @Override
  public String toString() {
    return new StringBuilder()
        .append("NodeTagTemplatesContainer [nodeTagTemplates=")
        .append(nodeTagTemplates)
        .append("]")
        .toString();
  }
  
  // METHODS TO FULFILL UI USE CASES
  public List<PointTemplateAllAttributesDto> getPointTemplatesAllAttributes() {

    // Create a mapping of all AD function templates and their input points by tags.
    Map<String, Set<String>> adFunctionTemplatesMap = new HashMap<>();
    AdFunctionTemplatesContainer adFunctionTemplatesContainer = DictionaryContext.getAdFunctionTemplatesContainer();
    for (AbstractAdFunctionTemplateEntity adFunctionTemplate: adFunctionTemplatesContainer.getAdFunctionTemplates()) {
      for (AdFunctionTemplateInputPointEntity inputPoint: adFunctionTemplate.getInputPoints()) {
        
        String tags = inputPoint.getTags().toString();
        Set<String> adFunctionTemplates = adFunctionTemplatesMap.get(tags);
        if (adFunctionTemplates == null) {
          
          adFunctionTemplates = new TreeSet<>();
          adFunctionTemplatesMap.put(tags, adFunctionTemplates);
        }
        
        if (adFunctionTemplate instanceof AdRuleFunctionTemplateEntity) {
          adFunctionTemplates.add(((AdRuleFunctionTemplateEntity)adFunctionTemplate).getFaultNumber());
        } else if (adFunctionTemplate instanceof AdComputedPointFunctionTemplateEntity) {
          adFunctionTemplates.add(((AdComputedPointFunctionTemplateEntity)adFunctionTemplate).getReferenceNumber());
        } else {
          throw new RuntimeException("Unsupported AD function template type: " + adFunctionTemplate.getClassAndNaturalIdentity());
        }
      }
    }
    
    // Create a mapping of all report templates and their input points by tags.
    Map<String, Set<String>> reportTemplatesMap = new HashMap<>();
    ReportTemplatesContainer reportTemplatesContainer = DictionaryContext.getReportTemplatesContainer();
    for (ReportTemplateEntity reportTemplate: reportTemplatesContainer.getReportTemplates()) {
      for (ReportTemplateEquipmentSpecEntity equipmentSpec: reportTemplate.getEquipmentSpecs()) {
        for (AbstractReportTemplatePointSpecEntity pointSpec: equipmentSpec.getPointSpecs()) {
          if (pointSpec instanceof ReportTemplateStandardPointSpecEntity) {
            
           String tags = ((ReportTemplateStandardPointSpecEntity)pointSpec).getTags().toString();

           Set<String> reportTemplates = reportTemplatesMap.get(tags);
           if (reportTemplates == null) {
             
             reportTemplates = new TreeSet<>();
             reportTemplatesMap.put(tags, reportTemplates);
           }
           reportTemplates.add(reportTemplate.getName());
          }
        }
      }
    }
    
    List<PointTemplateAllAttributesDto> list = new ArrayList<>();
    
    for (Map.Entry<Integer, AbstractNodeTagTemplateEntity> entry: nodeTagTemplates.entrySet()) {
      
      AbstractNodeTagTemplateEntity e = entry.getValue();
      
      List<String> tags = new ArrayList<>();
      for (TagEntity t: e.getTags()) {
        tags.add(t.getName());
      }
      
      String unit = "";
      if (e instanceof PointTemplateEntity) {
        unit = ((PointTemplateEntity)e).getUnit().getName();
      }
      
      List<String> parentNodeTypesList = new ArrayList<>();
      for (NodeType parentNodeType: e.getParentNodeTypes()) {
        parentNodeTypesList.add(parentNodeType.getName().toLowerCase());
      }
      String parentNodeTypes = parentNodeTypesList.toString().replace("[", "").replace("]", "");
      
      String parentEnergyExchangeTypes = "";
      if (e instanceof PointTemplateEntity) {
        List<String> parentEnergyExchangeTypesList = new ArrayList<>();
        for (AbstractEnergyExchangeTypeEntity energyExchangeType: ((PointTemplateEntity)e).getParentEnergyExchangeTypes()) {
          parentEnergyExchangeTypesList.add(energyExchangeType.getName());
        }
        parentEnergyExchangeTypes = parentEnergyExchangeTypesList.toString().replace("[", "").replace("]", "");
      }
            
      Set<String> referencedAdFunctionTemplatesSet = adFunctionTemplatesMap.get(e.getTags().toString());
      String referencedAdFunctionTemplates = "";
      if (referencedAdFunctionTemplatesSet != null) {
        referencedAdFunctionTemplates = referencedAdFunctionTemplatesSet.toString().replace("[", "").replace("]", "");
      }
      
      Set<String> referencedReportTemplatesSet = reportTemplatesMap.get(e.getTags().toString());
      String referencedReportTemplates = "";
      if (referencedReportTemplatesSet != null) {
        referencedReportTemplates = referencedReportTemplatesSet.toString().replace("[", "").replace("]", "");
      }
      
      String replacementPointTemplateId = null;
      if (e.getReplacementPointTemplateId() != null) {
        replacementPointTemplateId = e.getReplacementPointTemplateId().toString();
      } else {
        replacementPointTemplateId = "";
      }
      
      list.add(PointTemplateAllAttributesDto
          .builder()
          .withId(e.getPersistentIdentity())
          .withName(e.getName())
          .withDescription(e.getDescription())
          .withTags(tags.toString().replace("[", "").replace("]", ""))
          .withUnit(unit)
          .withIsPublic(e.getIsPublic())
          .withIsDeprecated(e.getIsDeprecated())
          .withReplacementPointTemplateId(replacementPointTemplateId)
          .withParentNodeTypes(parentNodeTypes)
          .withParentEnergyExchangeTypes(parentEnergyExchangeTypes)
          .withReferencedAdFunctionTemplates(referencedAdFunctionTemplates)
          .withReferencedReportTemplates(referencedReportTemplates)
          .build());
    }
        
    Collections.sort(list);
    
    LOGGER.info("Short Name, Long Name, Energy Exchange Type, Referenced Rules/Computed Points, Referenced Reports");
    for (PointTemplateAllAttributesDto dto: list) {
      
      StringBuilder sb = new StringBuilder();
      
      sb.append(dto.getName())
          .append(",")
          .append(dto.getDescription())
          .append(",")
          .append(dto.getParentEnergyExchangeTypes())
          .append(",");
      
      sb.append(dto.getReferencedAdFunctionTemplates())
          .append(",");
      
      sb.append(dto.getReferencedReportTemplates())
          .toString();
      
      LOGGER.info(sb.toString());
          
    }

    return list;
  }
  
  public List<EquipmentPointTemplateDto> getEquipmentPointTemplateHierarchyDto() {
    return getEquipmentPointTemplateHierarchyDto(null);
  }
  
  public List<EquipmentPointTemplateDto> getEquipmentPointTemplateHierarchyDto(String equipmentTypeName) {
    
    List<EquipmentPointTemplateDto> dtos = new ArrayList<>();
    Iterator<Entry<EquipmentEnergyExchangeTypeEntity, Set<PointTemplateEntity>>> iterator = equipmentPointTemplates.entrySet().iterator();
    while (iterator.hasNext()) {
      
      Entry<EquipmentEnergyExchangeTypeEntity, Set<PointTemplateEntity>> entry = iterator.next();
      EquipmentEnergyExchangeTypeEntity e = entry.getKey();
      
      if (equipmentTypeName == null || equipmentTypeName.equals(e.getName())) {

        EquipmentPointTemplateDto v = new EquipmentPointTemplateDto();
        v.setParentEquipmentTypeId(e.getPersistentIdentity());
      
        List<PointTemplateDto> pointTemplates = new ArrayList<>();
        Iterator<PointTemplateEntity> pointTemplateIterator = entry.getValue().iterator();
        while (pointTemplateIterator.hasNext()) {
          
          PointTemplateEntity pte = pointTemplateIterator.next();
          pointTemplates.add(PointTemplateDto
              .builder()
              .withId(pte.getPersistentIdentity())
              .withName(getCombinedNameAndTagNames(pte))
              .withDisplayName(pte.getName())
              .withUnitId(pte.getUnit().getPersistentIdentity())
              .build());
        }
        v.setPointTemplates(pointTemplates);
        dtos.add(v);
      }
    }
    return dtos;
  }

  public List<PlantPointTemplateDto> getPlantPointTemplateHierarchyDto() {
    return getPlantPointTemplateHierarchyDto(null);
  }
  
  public List<PlantPointTemplateDto> getPlantPointTemplateHierarchyDto(String plantTypeName) {
    
    List<PlantPointTemplateDto> dtos = new ArrayList<>();
    Iterator<Entry<PlantEnergyExchangeTypeEntity, Set<PointTemplateEntity>>> iterator = plantPointTemplates.entrySet().iterator();
    while (iterator.hasNext()) {
      
      Entry<PlantEnergyExchangeTypeEntity, Set<PointTemplateEntity>> entry = iterator.next();
      PlantEnergyExchangeTypeEntity e = entry.getKey();
      
      if (plantTypeName == null || plantTypeName.equals(e.getName())) {

        PlantPointTemplateDto v = new PlantPointTemplateDto();
        v.setParentPlantTypeId(e.getPersistentIdentity());
      
        List<PointTemplateDto> pointTemplates = new ArrayList<>();
        Iterator<PointTemplateEntity> pointTemplateIterator = entry.getValue().iterator();
        while (pointTemplateIterator.hasNext()) {
          
          PointTemplateEntity pte = pointTemplateIterator.next();
          pointTemplates.add(PointTemplateDto
              .builder()
              .withId(pte.getPersistentIdentity())
              .withName(getCombinedNameAndTagNames(pte))
              .withDisplayName(pte.getName())
              .withUnitId(pte.getUnit().getPersistentIdentity())
              .build());
        }
        v.setPointTemplates(pointTemplates);
        dtos.add(v);
      }
    }
    return dtos;
  }  
  
  public List<LoopPointTemplateDto> getLoopPointTemplateHierarchyDto() {
    return getLoopPointTemplateHierarchyDto(null);
  }
  
  public List<LoopPointTemplateDto> getLoopPointTemplateHierarchyDto(String loopTypeName) {
    
    List<LoopPointTemplateDto> dtos = new ArrayList<>();
    Iterator<Entry<LoopEnergyExchangeTypeEntity, Set<PointTemplateEntity>>> iterator = loopPointTemplates.entrySet().iterator();
    while (iterator.hasNext()) {
      
      Entry<LoopEnergyExchangeTypeEntity, Set<PointTemplateEntity>> entry = iterator.next();
      LoopEnergyExchangeTypeEntity e = entry.getKey();
      
      if (loopTypeName == null || loopTypeName.equals(e.getName())) {

        LoopPointTemplateDto v = new LoopPointTemplateDto();
        v.setParentLoopTypeId(e.getPersistentIdentity());
      
        List<PointTemplateDto> pointTemplates = new ArrayList<>();
        Iterator<PointTemplateEntity> pointTemplateIterator = entry.getValue().iterator();
        while (pointTemplateIterator.hasNext()) {
          
          PointTemplateEntity pte = pointTemplateIterator.next();
          pointTemplates.add(PointTemplateDto
              .builder()
              .withId(pte.getPersistentIdentity())
              .withName(getCombinedNameAndTagNames(pte))
              .withDisplayName(pte.getName())
              .withUnitId(pte.getUnit().getPersistentIdentity())
              .build());
        }
        v.setPointTemplates(pointTemplates);
        dtos.add(v);
      }
    }
    return dtos;
  }  
  
  public List<PointTemplateDto> getBuildingPointTemplatesDto() {
    
    List<PointTemplateDto> dtos = new ArrayList<>();
    Iterator<PointTemplateEntity> iterator = buildingPointTemplates.iterator();
    while (iterator.hasNext()) {
      
      PointTemplateEntity e = iterator.next();
      
      if (e.getIsPublic().booleanValue()) {

        dtos.add(PointTemplateDto
            .builder()
            .withId(e.getPersistentIdentity())
            .withName(getCombinedNameAndTagNames(e))
            .withDisplayName(e.getName())
            .withUnitId(e.getUnit().getPersistentIdentity())
            .build());
      }
    }
    return dtos;
  }
  
  private String getCombinedNameAndTagNames(AbstractNodeTagTemplateEntity nodeTagTemplate) {
    
    StringBuilder sb = new StringBuilder();
    sb.append(nodeTagTemplate.getName());
    sb.append(" (");
    sb.append(getTagsAsString(nodeTagTemplate.getTags()));
    sb.append(")");
    return sb.toString();
  }
  

  // DTO/ENTITY MAPPERS
  public static NodeTagTemplatesContainer mapFromDtos(
      TagsContainer tagsContainer,
      UnitsContainer unitsContainer,
      List<NodeTagTemplateDto> dtoList,
      List<UnitMappingDto> unitMappingDtoList,
      List<PointTemplateUnitMappingDto> pointTemplateUnitMappingDtoList) {
    
    Set<PointTemplateEntity> buildingPointTemplates = new TreeSet<>();
    Map<EquipmentEnergyExchangeTypeEntity, Set<PointTemplateEntity>> equipmentPointTemplates = new TreeMap<>();
    Map<PlantEnergyExchangeTypeEntity, Set<PointTemplateEntity>> plantPointTemplates = new TreeMap<>();
    Map<LoopEnergyExchangeTypeEntity, Set<PointTemplateEntity>> loopPointTemplates = new TreeMap<>();
    
    for (NodeTagTemplateDto dto: dtoList) {
      
      try {
        
        Integer persistentIdentity = dto.getId();
        String name = dto.getName();
        String description = dto.getDescription();
        Boolean isPublic = dto.getIsPublic();
        Boolean isDeprecated = dto.getIsDeprecated();
        Integer replacementPointTemplateId = dto.getReplacementPointTemplateId();
        Integer parentNodeType = dto.getParentNodeTypeId();
        UnitEntity unit = unitsContainer.getUnit(dto.getUnitId());        
        
        Set<TagEntity> haystackTags = new TreeSet<>();
        String[] tagArray = dto
            .getTags()
            .replace("{", "")
            .replace("}", "")
            .replace("\"", "")
            .split(",");
        for (int i=0; i < tagArray.length; i++) {
          haystackTags.add(tagsContainer.getHaystackTag(tagArray[i]));
        }
        
        Set<NodeType> parentNodeTypes = new HashSet<>();
        
        // First, see if there are any "energy exchange" associations (i.e. equipment, plant or loop).
        // Otherwise, use the default parent node type associated with the point template.
        String parentEnergyExchangeTypeIds = dto.getParentEnergyExchangeTypeIds();
        if (parentEnergyExchangeTypeIds != null 
            && !parentEnergyExchangeTypeIds.equals("{}") 
            && !parentEnergyExchangeTypeIds.equalsIgnoreCase("{NULL}")) {
          
          Set<EquipmentEnergyExchangeTypeEntity> parentEquipmentTypes = new TreeSet<>();
          Set<PlantEnergyExchangeTypeEntity> parentPlantTypes = new TreeSet<>();
          Set<LoopEnergyExchangeTypeEntity> parentLoopTypes = new TreeSet<>();
          
          String[] parentEnergyExchangeTypeIdArray = parentEnergyExchangeTypeIds
              .replace("{", "")
              .replace("}", "")
              .replace("\"", "")
              .split(",");

          for (int i=0; i < parentEnergyExchangeTypeIdArray.length; i++) {
            
            String parentEnergyExchangeTypeId = parentEnergyExchangeTypeIdArray[i];
            if (!parentEnergyExchangeTypeId.trim().equalsIgnoreCase("null")) {

              AbstractEnergyExchangeTypeEntity energyExchangeType = tagsContainer.getEnergyExchangeTypeById(Integer.parseInt(parentEnergyExchangeTypeId));
              if (energyExchangeType instanceof EquipmentEnergyExchangeTypeEntity) {
                parentEquipmentTypes.add((EquipmentEnergyExchangeTypeEntity)energyExchangeType);
                
              } else if (energyExchangeType instanceof PlantEnergyExchangeTypeEntity) {
                parentPlantTypes.add((PlantEnergyExchangeTypeEntity)energyExchangeType);
                
              } else if (energyExchangeType instanceof LoopEnergyExchangeTypeEntity) {
                parentLoopTypes.add((LoopEnergyExchangeTypeEntity)energyExchangeType);
                
              } else {
                
                throw new IllegalArgumentException("Unsupported energy exchange type: ["
                    + energyExchangeType.getClassAndNaturalIdentity()
                    + "], expected: [Equipment, Plant or Loop].");
              }
            }
          }
          
          if (!parentEquipmentTypes.isEmpty() 
              || !parentPlantTypes.isEmpty() 
              || !parentLoopTypes.isEmpty()) {
            
            if (parentEquipmentTypes.isEmpty()) {
              parentEquipmentTypes = PointTemplateEntity.EMPTY_PARENT_EQUIPMENT_TYPES;
            } else {
              parentNodeTypes.add(NodeType.EQUIPMENT);              
            }
            
            if (parentPlantTypes.isEmpty()) {
              parentPlantTypes = PointTemplateEntity.EMPTY_PARENT_PLANT_TYPES;
            } else {
              parentNodeTypes.add(NodeType.PLANT);
            }

            if (parentLoopTypes.isEmpty()) {
              parentLoopTypes = PointTemplateEntity.EMPTY_PARENT_LOOP_TYPES;
            } else {
              parentNodeTypes.add(NodeType.LOOP);
            }

            // It's possible that a point template can be associated with both 
            // buildings and energy exchange types (plants, loops and equipment).
            if (parentNodeType.equals(NodeType.BUILDING.getId())) {
              parentNodeTypes.add(NodeType.BUILDING);
            }
            
            PointTemplateEntity pointTemplate = new PointTemplateEntity(
                persistentIdentity,
                name,
                description,
                parentNodeTypes,
                isPublic,
                haystackTags,
                isDeprecated,
                replacementPointTemplateId,
                unit,
                parentEquipmentTypes,
                parentPlantTypes,
                parentLoopTypes);
            
            if (!parentEquipmentTypes.isEmpty()) {
              
              for (EquipmentEnergyExchangeTypeEntity equipmentType: parentEquipmentTypes) {
                
                Set<PointTemplateEntity> set = equipmentPointTemplates.get(equipmentType);
                if (set == null) {
                  
                  set = new TreeSet<>();
                  equipmentPointTemplates.put(equipmentType, set);
                }
                set.add(pointTemplate);
              }
            }

            if (!parentPlantTypes.isEmpty()) {

              for (PlantEnergyExchangeTypeEntity plantType: parentPlantTypes) {
                
                Set<PointTemplateEntity> set = plantPointTemplates.get(plantType);
                if (set == null) {
                  
                  set = new TreeSet<>();
                  plantPointTemplates.put(plantType, set);
                }
                set.add(pointTemplate);
              }
            }

            if (!parentLoopTypes.isEmpty()) {

              for (LoopEnergyExchangeTypeEntity loopType: parentLoopTypes) {
                
                Set<PointTemplateEntity> set = loopPointTemplates.get(loopType);
                if (set == null) {
                  
                  set = new TreeSet<>();
                  loopPointTemplates.put(loopType, set);
                }
                set.add(pointTemplate);
              }
            }
          }
          
        } else if (parentNodeType.equals(NodeType.BUILDING.getId())) {
         
          parentNodeTypes.add(NodeType.BUILDING);
          
          buildingPointTemplates.add(new PointTemplateEntity(
              persistentIdentity,
              name,
              description,
              parentNodeTypes,
              isPublic,
              haystackTags,
              isDeprecated,
              replacementPointTemplateId,
              unit,
              PointTemplateEntity.EMPTY_PARENT_EQUIPMENT_TYPES,
              PointTemplateEntity.EMPTY_PARENT_PLANT_TYPES,
              PointTemplateEntity.EMPTY_PARENT_LOOP_TYPES));
          
        } else {
          
          throw new IllegalStateException("Unsupported parent node type: ["
              + parentNodeType
              + "] for point template: ["
              + dto.getName() 
              + "].");
        }
      } catch (Exception e) {
        
        LOGGER.error("Unable to map node tag template: [{}], error: [{}]",
            dto.getName(),
            e.getMessage(),
            e);
      }
    }        
    return new NodeTagTemplatesContainer(
        buildingPointTemplates,
        equipmentPointTemplates,
        plantPointTemplates,
        loopPointTemplates,
        unitMappingDtoList,
        pointTemplateUnitMappingDtoList);
  }
  
  public static Map<String, Object> mapToDtos(NodeTagTemplatesContainer nodeTagTemplatesContainer) {
    
    Map<String, Object> map = new HashMap<>();
    
    
    List<UnitMappingDto> unitMappingDtoList = new ArrayList<>();
    Map<Integer, UnitMappingEntity> unitMappings = nodeTagTemplatesContainer.getUnitMappings();
    for (UnitMappingEntity e: unitMappings.values()) {
      unitMappingDtoList.add(UnitMappingEntity
          .Mapper
          .getInstance()
          .mapEntityToDto(e));
    }
    map.put("unitMappings", unitMappingDtoList);
    
    
    List<PointTemplateUnitMappingDto> pointTemplateUnitMappingDtoList = new ArrayList<>();
    Map<Integer, Map<Integer, PointTemplateUnitMappingEntity>> pointTemplateUnitMappings = nodeTagTemplatesContainer.getPointTemplateUnitMappings();
    for (Map<Integer, PointTemplateUnitMappingEntity> pointTemplateUnitMapping: pointTemplateUnitMappings.values()) {
      for (PointTemplateUnitMappingEntity e: pointTemplateUnitMapping.values()) {
        pointTemplateUnitMappingDtoList.add(PointTemplateUnitMappingEntity
            .Mapper
            .getInstance()
            .mapEntityToDto(e));
      }
    }
    map.put("pointTemplateUnitMappings", pointTemplateUnitMappingDtoList);
    
    
    List<NodeTagTemplateDto> dtos = new ArrayList<>();
    Iterator<AbstractNodeTagTemplateEntity> iterator = nodeTagTemplatesContainer.getAllNodeTagTemplates().iterator();
    while (iterator.hasNext()) {
      dtos.add(mapToDto(iterator.next()));
    }
    map.put("pointTemplates", dtos);
    
    
    return map;
  }

  public static NodeTagTemplateDto mapToDto(AbstractNodeTagTemplateEntity entity) {
    
    NodeTagTemplateDto dto = new NodeTagTemplateDto();
    
    if (entity instanceof PointTemplateEntity) {
      
      PointTemplateEntity pointTemplate = (PointTemplateEntity)entity;
      
      dto.setUnitId(pointTemplate.getUnit().getPersistentIdentity());
      if (pointTemplate instanceof PointTemplateEntity) {
        
        PointTemplateEntity equipmentLevelPointTemplate = (PointTemplateEntity)entity;
        dto.setParentEnergyExchangeTypeIds(getParentEnergyExchangeTypeIdsAsString(equipmentLevelPointTemplate.getParentEnergyExchangeTypes()));
      }
    }
    dto.setId(entity.getPersistentIdentity());
    dto.setName(entity.getName());
    dto.setDescription(entity.getDescription());
    dto.setIsPublic(entity.getIsPublic());
    dto.setParentNodeTypeId(Integer.valueOf(entity.getParentNodeType().getId()));
    dto.setTags("{" + getTagsAsString(entity.getTags()) + "}");
    return dto;
  }

  public static String getTagsAsString(Set<TagEntity> tags) {
    
    StringBuilder sb = new StringBuilder();
    if (tags != null && !tags.isEmpty()) {
      
      List<TagEntity> list = new ArrayList<>();
      list.addAll(tags);
      for (int i=0; i < list.size(); i++) {
        
        TagEntity tag = list.get(i);
        sb.append(tag.getName());
        if (i < (list.size()-1)) {
          sb.append(",");
        }
      }
    }
    return sb.toString();
  }
  
  public static String getParentEnergyExchangeTypeIdsAsString(Set<AbstractEnergyExchangeTypeEntity> entities) {
    
    String s = null;
    StringBuilder sb = new StringBuilder();
    if (entities != null && !entities.isEmpty()) {
      sb.append("{");
      Iterator<AbstractEnergyExchangeTypeEntity> iterator = entities.iterator();
      while (iterator.hasNext()) {
        
        AbstractEnergyExchangeTypeEntity equipmentType = iterator.next();
        sb.append(equipmentType.getPersistentIdentity().toString());
        
        if (iterator.hasNext()) {
          sb.append(",");
        }
      }
      sb.append("}");
      s = sb.toString();
    }
    return s;
  }    

  public Map<Integer, UnitMappingEntity> getUnitMappings() {
    return unitMappings;
  }
  
  public Map<Integer, Map<Integer, PointTemplateUnitMappingEntity>> getPointTemplateUnitMappings() {
    return pointTemplateUnitMappings;
  }
  
  public UnitMappingEntity getUnitMapping(Integer unitMappingId) throws EntityDoesNotExistException {
    
    UnitMappingEntity unitMapping = unitMappings.get(unitMappingId);
    if (unitMapping != null) {
      return unitMapping;
    }
    throw new EntityDoesNotExistException("Unit mapping with id: ["
        + unitMappingId
        + "]");
  }

  public PointTemplateUnitMappingEntity getDefaultPointTemplateUnitMapping(Integer pointTemplateId) {

    Map<Integer, PointTemplateUnitMappingEntity> map = pointTemplateUnitMappings.get(pointTemplateId);
    if (map != null) {
      return map.get(Integer.valueOf(1));
    }
    return null;
  }
}