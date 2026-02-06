//@formatter:off
package com.djt.hvac.domain.model.dictionary.service;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.common.validation.SimpleValidationMessage;
import com.djt.hvac.domain.model.dictionary.PaymentPlanEntity;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.UnitEntity;
import com.djt.hvac.domain.model.dictionary.container.AdFunctionTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.container.NodeTagTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.container.PaymentPlansContainer;
import com.djt.hvac.domain.model.dictionary.container.ReportTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.container.ScheduledEventTypesContainer;
import com.djt.hvac.domain.model.dictionary.container.TagsContainer;
import com.djt.hvac.domain.model.dictionary.container.UnitsContainer;
import com.djt.hvac.domain.model.dictionary.container.WeatherStationsContainer;
import com.djt.hvac.domain.model.dictionary.dto.AdFunctionTemplateInputConstantPointTemplateMappingDto;
import com.djt.hvac.domain.model.dictionary.dto.IdNameDto;
import com.djt.hvac.domain.model.dictionary.dto.NodeTagTemplateDto;
import com.djt.hvac.domain.model.dictionary.dto.PointTemplateUnitMappingDto;
import com.djt.hvac.domain.model.dictionary.dto.TagDto;
import com.djt.hvac.domain.model.dictionary.dto.UnitMappingDto;
import com.djt.hvac.domain.model.dictionary.dto.function.AdFunctionTemplateDto;
import com.djt.hvac.domain.model.dictionary.dto.function.AdFunctionTemplateInputConstantDto;
import com.djt.hvac.domain.model.dictionary.dto.function.DatabaseWrapperDto;
import com.djt.hvac.domain.model.dictionary.dto.function.rule.arms.Rule;
import com.djt.hvac.domain.model.dictionary.energyexchange.EquipmentEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.LoopEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.PlantEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.enums.PaymentInterval;
import com.djt.hvac.domain.model.dictionary.event.DictionaryChangeEvent;
import com.djt.hvac.domain.model.dictionary.repository.DictionaryRepository;
import com.djt.hvac.domain.model.dictionary.repository.migrations.AdFunctionTemplateInputConstantPointTemplateMappingJsonToEntityMapper;
import com.djt.hvac.domain.model.dictionary.repository.migrations.AdFunctionTemplateInputConstantPointTemplateMappingMigrationGenerator;
import com.djt.hvac.domain.model.dictionary.repository.migrations.AdFunctionTemplateJsonToEntityMapper;
import com.djt.hvac.domain.model.dictionary.repository.migrations.AdFunctionTemplateMigrationGenerator;
import com.djt.hvac.domain.model.dictionary.repository.migrations.ArmsToAdFunctionTemplateDtoMapper;
import com.djt.hvac.domain.model.dictionary.repository.migrations.PointTemplateJsonToEntityMapper;
import com.djt.hvac.domain.model.dictionary.repository.migrations.PointTemplateMigrationGenerator;
import com.djt.hvac.domain.model.dictionary.repository.migrations.PointTemplateUnitMappingJsonToEntityMapper;
import com.djt.hvac.domain.model.dictionary.repository.migrations.PointTemplateUnitMappingMigrationGenerator;
import com.djt.hvac.domain.model.dictionary.repository.migrations.TagJsonToEntityMapper;
import com.djt.hvac.domain.model.dictionary.repository.migrations.TagMigrationGenerator;
import com.djt.hvac.domain.model.dictionary.repository.migrations.UnitJsonToEntityMapper;
import com.djt.hvac.domain.model.dictionary.repository.migrations.UnitMappingJsonToEntityMapper;
import com.djt.hvac.domain.model.dictionary.repository.migrations.UnitMappingMigrationGenerator;
import com.djt.hvac.domain.model.dictionary.repository.migrations.UnitMigrationGenerator;
import com.djt.hvac.domain.model.dictionary.service.command.MigrationRequest;
import com.djt.hvac.domain.model.dictionary.template.function.AbstractAdFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateInputConstantEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateInputConstantPointTemplateMappingEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateInputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateOutputPointEntity;
import com.djt.hvac.domain.model.dictionary.template.function.rule.AdRuleFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.AbstractNodeTagTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateUnitMappingEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.UnitMappingEntity;
import com.djt.hvac.domain.model.function.query.AdFunctionTemplateQueryDao;
import com.djt.hvac.domain.model.report.query.ReportQueryDao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

public class DictionaryServiceImpl implements DictionaryService {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryServiceImpl.class);

  private final DictionaryRepository dictionaryRepository;
  private final AdFunctionTemplateQueryDao adFunctionTemplateQueryDao;
  private final ReportQueryDao reportQueryDao;

  public DictionaryServiceImpl(
      DictionaryRepository dictionaryRepository,
      AdFunctionTemplateQueryDao adFunctionTemplateQueryDao,
      ReportQueryDao reportQueryDao) {
    
    requireNonNull(dictionaryRepository, "dictionaryRepository cannot be null");
    requireNonNull(adFunctionTemplateQueryDao, "adFunctionTemplateQueryDao cannot be null");
    requireNonNull(reportQueryDao, "reportQueryDao cannot be null");
    this.dictionaryRepository = dictionaryRepository;
    this.adFunctionTemplateQueryDao = adFunctionTemplateQueryDao;
    this.reportQueryDao = reportQueryDao;
  }
  
  @Override
  public Integer getMaxPointCap() {
    
    return getPaymentPlansContainer().getMaxPointCap();
  }
  
  @Override
  public List<PaymentPlanEntity> getQualifyingPaymentPlansAsList(int mappedPointCount) {
    
    List<PaymentPlanEntity> qualifyingPaymentPlans = new ArrayList<>();
    
    Set<PaymentPlanEntity> allPaymentPlans = getPaymentPlansContainer().getPaymentPlans();
    for (PaymentPlanEntity paymentPlan:allPaymentPlans) {
      
      if (paymentPlan.getPointCap().intValue() >= mappedPointCount) {
        
        qualifyingPaymentPlans.add(paymentPlan);
      }
    }
    
    if (qualifyingPaymentPlans.isEmpty()) {
      
      throw new IllegalStateException("No qualifying payment plans exist for mapped point count: "
          + mappedPointCount
          + "] from: "
          + allPaymentPlans);
    }
    
    return qualifyingPaymentPlans;
  }
  
  @Override 
  public Map<Integer, Map<PaymentInterval, PaymentPlanEntity>> getQualifyingPaymentPlans(int mappedPointCount) {
    
    Map<Integer, Map<PaymentInterval, PaymentPlanEntity>> qualifyingPaymentPlans = new TreeMap<>();
    
    for (PaymentPlanEntity paymentPlan: getQualifyingPaymentPlansAsList(mappedPointCount)) {
      
      Integer pointCap = paymentPlan.getPointCap();
      
      Map<PaymentInterval, PaymentPlanEntity> map = qualifyingPaymentPlans.get(pointCap);
      if (map == null) {
        
        map = new TreeMap<>();
        qualifyingPaymentPlans.put(pointCap, map);
      }
      map.put(paymentPlan.getPaymentInterval(), paymentPlan);
    }
    
    return qualifyingPaymentPlans;
  }
  
  @Override
  public PaymentPlanEntity getLowestCostPerIntervalQualifyingPaymentPlan(int mappedPointCount) {
   
    return getQualifyingPaymentPlansAsList(mappedPointCount).get(0);
  }
 
  @Override 
  public TagsContainer getTagsContainer() {
    
    ensureDictionaryDataIsLoaded();
    return dictionaryRepository.getTagsContainer();
  }

  @Override 
  public UnitsContainer getUnitsContainer() {
    
    ensureDictionaryDataIsLoaded();
    return dictionaryRepository.getUnitsContainer();
  }

  @Override 
  public NodeTagTemplatesContainer getNodeTagTemplatesContainer() {
    
    ensureDictionaryDataIsLoaded();
    return dictionaryRepository.getNodeTagTemplatesContainer();
  }

  @Override 
  public ScheduledEventTypesContainer getScheduledEventTypesContainer() {
    
    ensureDictionaryDataIsLoaded();
    return dictionaryRepository.getScheduledEventTypesContainer();
  }

  @Override 
  public AdFunctionTemplatesContainer getAdFunctionTemplatesContainer() {
    
    ensureDictionaryDataIsLoaded();
    return dictionaryRepository.getAdFunctionTemplatesContainer();
  }
  
  @Override 
  public ReportTemplatesContainer getReportTemplatesContainer() {
    
    ensureDictionaryDataIsLoaded();
    return dictionaryRepository.getReportTemplatesContainer();
  }
  
  @Override 
  public PaymentPlansContainer getPaymentPlansContainer() {
    
    ensureDictionaryDataIsLoaded();
    return dictionaryRepository.getPaymentPlansContainer();
  }
  
  @Override
  public WeatherStationsContainer getWeatherStationsContainer() {
    
    ensureDictionaryDataIsLoaded();
    return dictionaryRepository.getWeatherStationsContainer();
  }
  
  @Override 
  public void ensureDictionaryDataIsLoaded() {
    
    dictionaryRepository.ensureDictionaryDataIsLoaded();
  }
  
  @Override
  public void invalidateDictionaryData() {
    
    dictionaryRepository.invalidateDictionaryData();
  }
  
  @Override
  public void updateAdFunctionTemplateVersion(Integer adFunctionTemplateId, Integer version) {
    
    dictionaryRepository.ensureDictionaryDataIsLoaded();

    dictionaryRepository.updateAdFunctionTemplateVersion(
        adFunctionTemplateId,
        version);
  }
  
  @Override
  public void updatePointTemplate(
      Integer pointTemplateId,
      String name,
      String description,
      Boolean isPublic,
      Set<TagEntity> tags,
      Integer unitId,
      Boolean isDeprecated,
      Integer replacementPointTemplateId,
      Set<EquipmentEnergyExchangeTypeEntity> parentEquipmentTypes,
      Set<PlantEnergyExchangeTypeEntity> parentPlantTypes,
      Set<LoopEnergyExchangeTypeEntity> parentLoopTypes)
  throws 
      EntityDoesNotExistException {
    
    dictionaryRepository.ensureDictionaryDataIsLoaded();
    
    dictionaryRepository.updatePointTemplate(
        pointTemplateId, 
        name, 
        description, 
        isPublic, 
        tags, 
        unitId, 
        isDeprecated, 
        replacementPointTemplateId,
        parentEquipmentTypes,
        parentPlantTypes,
        parentLoopTypes);
  }
  
  @Override
  public void handleEvent(DictionaryChangeEvent dictionaryChangeEvent) {
    
    LOGGER.info("Invalidating/reloading dictionary data cache: dictionaryChangeEvent: {}", dictionaryChangeEvent);
    try {
      
      invalidateDictionaryData();
      ensureDictionaryDataIsLoaded();
      
    } catch (Exception e) {
      LOGGER.error("Unable to invalidate/reload dictionary data for dictionaryChangeEvent: {}, error: {}",
          dictionaryChangeEvent,
          e.getMessage(), 
          e);
    }
  }
  

  
  // METHODS USED BY TESTS TO CREATE DICTIONARY DATA
  @Override
  public UnitMappingDto createUnitMapping(
      UnitEntity ipUnit,
      UnitEntity siUnit,
      String ipToSiConversionFactor,
      String siToIpConversionFactor) 
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException {
    
    UnitMappingDto dto = dictionaryRepository.createUnitMapping(
        ipUnit, 
        siUnit, 
        ipToSiConversionFactor, 
        siToIpConversionFactor);
    
    UnitMappingEntity entity = UnitMappingEntity
        .Mapper
        .getInstance()
        .mapDtoToEntity(
            DictionaryContext.getNodeTagTemplatesContainer(),
            dto);
    
    DictionaryContext
        .getNodeTagTemplatesContainer()
        .addUnitMapping(entity);
    
    return dto;
  }
  
  @Override
  public PointTemplateUnitMappingDto createPointTemplateUnitMapping(
      PointTemplateEntity pointTemplate,
      UnitMappingEntity unitMapping,
      Integer priority) 
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException {
    
    PointTemplateUnitMappingDto dto = dictionaryRepository.createPointTemplateUnitMapping(
        pointTemplate, 
        unitMapping, 
        priority);
    
    PointTemplateUnitMappingEntity entity = PointTemplateUnitMappingEntity
        .Mapper
        .getInstance()
        .mapDtoToEntity(
            DictionaryContext.getNodeTagTemplatesContainer(),
            dto);

    DictionaryContext
        .getNodeTagTemplatesContainer()
        .addPointTemplateUnitMapping(entity);
    
    return dto;
  }

  @Override
  public AdFunctionTemplateInputConstantPointTemplateMappingDto createAdFunctionTemplateInputConstantPointTemplateMapping(
      Integer adFunctionTemplateInputConstantId,
      Integer pointTemplateId) 
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException {
    
    AdFunctionTemplateInputConstantPointTemplateMappingDto dto = dictionaryRepository.createAdFunctionTemplateInputConstantPointTemplateMapping(
        adFunctionTemplateInputConstantId,
        pointTemplateId);
    
    DictionaryContext
        .getAdFunctionTemplatesContainer()
        .addInputConstantPointTemplateMapping(
            adFunctionTemplateInputConstantId, 
            pointTemplateId);
    
    return dto;
  }
  

  
  // MIGRATION RELATED
  @Override
  public Map<String, Integer> getMaxPersistentIdentityValues() {
  
    Map<String, Integer> map = new HashMap<>();
    invalidateDictionaryData();
    ensureDictionaryDataIsLoaded();
    
    
    // TAGS
    int maxTagId = 0;
    for (TagEntity e: DictionaryContext.getTagsContainer().getTags()) {
     
      if (e.getPersistentIdentity() > maxTagId) {
        maxTagId = e.getPersistentIdentity();
      }
    }
    map.put("Tag", Integer.valueOf(maxTagId));

    
    // UNITS
    int maxUnitId = 0;
    for (UnitEntity e: DictionaryContext.getUnitsContainer().getUnits()) {
     
      if (e.getPersistentIdentity() > maxUnitId) {
        maxUnitId = e.getPersistentIdentity();
      }
    }
    map.put("Unit", Integer.valueOf(maxUnitId));

    
    // POINT TEMPLATES
    int maxPointTemplateId = 0;
    for (AbstractNodeTagTemplateEntity e: DictionaryContext.getNodeTagTemplatesContainer().getAllNodeTagTemplates()) {
     
      if (e.getPersistentIdentity() > maxPointTemplateId) {
        maxPointTemplateId = e.getPersistentIdentity();
      }
    }
    map.put("PointTemplate", Integer.valueOf(maxPointTemplateId));
    
    
    // RULES
    int maxAdFunctionTemplateId = 0;
    int maxAdFunctionModuleId = 0;
    int maxAdFunctionTemplateInputConstantId = 0;
    int maxAdFunctionTemplateInputPointId = 0;
    int maxAdFunctionTemplateOutputPointId = 0;
    for (AbstractAdFunctionTemplateEntity e: DictionaryContext.getAdFunctionTemplatesContainer().getAdFunctionTemplates()) {
      
      if (e.getPersistentIdentity() > maxAdFunctionTemplateId) {
        maxAdFunctionTemplateId = e.getPersistentIdentity();
      }
      
      if (e.getAdFunction().getPersistentIdentity() > maxAdFunctionModuleId) {
        maxAdFunctionModuleId = e.getAdFunction().getPersistentIdentity();
      }
      
      for (AdFunctionTemplateInputConstantEntity c: e.getInputConstants()) {
        if (c.getPersistentIdentity() > maxAdFunctionTemplateInputConstantId) {
          maxAdFunctionTemplateInputConstantId = c.getPersistentIdentity();  
        }
      }
      for (AdFunctionTemplateInputPointEntity i: e.getInputPoints()) {
        if (i.getPersistentIdentity() > maxAdFunctionTemplateInputPointId) {
          maxAdFunctionTemplateInputPointId = i.getPersistentIdentity();  
        }
      }
      for (AdFunctionTemplateOutputPointEntity o: e.getOutputPoints()) {
        if (o.getPersistentIdentity() > maxAdFunctionTemplateOutputPointId) {
          maxAdFunctionTemplateOutputPointId = o.getPersistentIdentity();  
        }
      }
    }
    map.put("AdFunctionTemplate", Integer.valueOf(maxAdFunctionTemplateId));
    map.put("AdFunctionModule", Integer.valueOf(maxAdFunctionModuleId));
    map.put("AdFunctionTemplateInputConstant", Integer.valueOf(maxAdFunctionTemplateInputConstantId));
    map.put("AdFunctionTemplateInputPoint", Integer.valueOf(maxAdFunctionTemplateInputPointId));
    map.put("AdFunctionTemplateOutputPoint", Integer.valueOf(maxAdFunctionTemplateOutputPointId));
    
    return map;
  }
  
  @Override
  public String generateMigrationForUnitMappingInsert(String json) throws JsonMappingException, JsonProcessingException {
   
    invalidateDictionaryData();
    ensureDictionaryDataIsLoaded();
    
    UnitMappingJsonToEntityMapper mapper = new UnitMappingJsonToEntityMapper();
    
    List<UnitMappingEntity> entities = mapper.mapFromJson(json);
    
    UnitMappingMigrationGenerator generator = new UnitMappingMigrationGenerator();
    
    return generator.generateInsertMigration(entities);
  }
  
  @Override
  public String generateMigrationForPointTemplateUnitMappingInsert(String json) throws JsonMappingException, JsonProcessingException {
   
    invalidateDictionaryData();
    ensureDictionaryDataIsLoaded();
    
    PointTemplateUnitMappingJsonToEntityMapper mapper = new PointTemplateUnitMappingJsonToEntityMapper();
    
    List<PointTemplateUnitMappingEntity> entities = mapper.mapFromJson(json);
    
    PointTemplateUnitMappingMigrationGenerator generator = new PointTemplateUnitMappingMigrationGenerator();
    
    return generator.generateInsertMigration(entities);
  }
  
  @Override
  public String generateMigrationForAdFunctionTemplateInputConstantPointTemplateMappingInsert(String json) throws JsonMappingException, JsonProcessingException {
   
    invalidateDictionaryData();
    ensureDictionaryDataIsLoaded();
    
    AdFunctionTemplateInputConstantPointTemplateMappingJsonToEntityMapper mapper = new AdFunctionTemplateInputConstantPointTemplateMappingJsonToEntityMapper();
    
    List<AdFunctionTemplateInputConstantPointTemplateMappingEntity> entities = mapper.mapFromJson(json);
    
    AdFunctionTemplateInputConstantPointTemplateMappingMigrationGenerator generator = new AdFunctionTemplateInputConstantPointTemplateMappingMigrationGenerator();
    
    return generator.generateInsertMigration(entities);
  }

  
  @Override
  public String generateMigrationForUnitInsert(String json) throws JsonMappingException, JsonProcessingException {
   
    invalidateDictionaryData();
    ensureDictionaryDataIsLoaded();
    
    List<UnitEntity> entities = new UnitJsonToEntityMapper().mapFromJson(json);
    
    Map<String, Integer> maxPersistentIdentityValues = getMaxPersistentIdentityValues();
    int maxId = maxPersistentIdentityValues.get("Unit") + 1;
    for (UnitEntity e: entities) {
      e.setPersistentIdentity(maxId++);  
    }
    
    return new UnitMigrationGenerator().generateInsertMigration(entities);
  }
  
  @Override
  public String generateMigrationForUnitUpdate(String json) throws JsonMappingException, JsonProcessingException {
   
    invalidateDictionaryData();
    ensureDictionaryDataIsLoaded();
    
    List<UnitEntity> afterEntities = new UnitJsonToEntityMapper().mapFromJson(json);
    
    Map<UnitEntity, UnitEntity> entities = new LinkedHashMap<>();
    for (UnitEntity afterEntity: afterEntities) {
      entities.put(DictionaryContext.getUnitsContainer().getUnit(afterEntity.getPersistentIdentity()), afterEntity);
    }
    
    return new UnitMigrationGenerator().generateUpdateMigration(entities);
  }

  @Override
  public String generateMigrationForUnitDelete(String json) throws JsonMappingException, JsonProcessingException {
   
    invalidateDictionaryData();
    ensureDictionaryDataIsLoaded();
    
    List<Integer> unitIds = AbstractEntity.OBJECT_MAPPER.get().readValue(json, new TypeReference<List<Integer>>() {});
    List<UnitEntity> unitsToDelete = new ArrayList<>();
    for (Integer unitId: unitIds) {
      unitsToDelete.add(DictionaryContext.getUnitsContainer().getUnit(unitId));
    }
    
    List<IdNameDto> dtoList = UnitEntity
      .Mapper
      .getInstance()
      .mapEntitiesToDtos(unitsToDelete);
    
    String dtosJson = AbstractEntity.OBJECT_WRITER.get().writeValueAsString(dtoList);
    
    List<UnitEntity> entities = new UnitJsonToEntityMapper().mapFromJson(dtosJson);
    
    return new UnitMigrationGenerator().generateDeleteMigration(entities);
  }
  
  
  @Override
  public String generateMigrationForTagInsert(String json) throws JsonMappingException, JsonProcessingException {
   
    invalidateDictionaryData();
    ensureDictionaryDataIsLoaded();
    
    List<TagEntity> entities = new TagJsonToEntityMapper().mapFromJson(json);
    
    Map<String, Integer> maxPersistentIdentityValues = getMaxPersistentIdentityValues();
    int maxId = maxPersistentIdentityValues.get("Tag") + 1;
    for (TagEntity e: entities) {
      e.setPersistentIdentity(maxId++);  
    }
    
    return new TagMigrationGenerator().generateInsertMigration(entities);
  }
  
  @Override
  public String generateMigrationForTagUpdate(String json) throws JsonMappingException, JsonProcessingException {
   
    invalidateDictionaryData();
    ensureDictionaryDataIsLoaded();
    
    List<TagEntity> afterEntities = new TagJsonToEntityMapper().mapFromJson(json);
    
    Map<TagEntity, TagEntity> entities = new LinkedHashMap<>();
    for (TagEntity afterEntity: afterEntities) {
      
      try {
        entities.put(DictionaryContext.getTagsContainer().getTag(afterEntity.getPersistentIdentity()), afterEntity);
      } catch (EntityDoesNotExistException e) {
        throw new IllegalStateException("Tag with id: ["
            + afterEntity.getPersistentIdentity()
            + "], does not exist.");
      }      
    }
    
    return new TagMigrationGenerator().generateUpdateMigration(entities);
  }

  @Override
  public String generateMigrationForTagDelete(String json) throws JsonMappingException, JsonProcessingException, EntityDoesNotExistException {
   
    invalidateDictionaryData();
    ensureDictionaryDataIsLoaded();
    
    List<Integer> tagIds = AbstractEntity.OBJECT_MAPPER.get().readValue(json, new TypeReference<List<Integer>>() {});
    List<TagEntity> tagsToDelete = new ArrayList<>();
    for (Integer tagId: tagIds) {
      tagsToDelete.add(DictionaryContext.getTagsContainer().getTag(tagId));
    }
    
    List<TagDto> dtoList = TagEntity
      .Mapper
      .getInstance()
      .mapEntitiesToDtos(tagsToDelete);
    
    String dtosJson = AbstractEntity.OBJECT_WRITER.get().writeValueAsString(dtoList);
    
    List<TagEntity> entities = new TagJsonToEntityMapper().mapFromJson(dtosJson);
    
    return new TagMigrationGenerator().generateDeleteMigration(entities);
  }
  
  
  @Override
  public String generateMigrationForPointTemplateInsert(String json) throws JsonMappingException, JsonProcessingException {
   
    invalidateDictionaryData();
    ensureDictionaryDataIsLoaded();
    
    List<PointTemplateEntity> entities = new PointTemplateJsonToEntityMapper().mapFromJson(json);
    
    Map<String, Integer> maxPersistentIdentityValues = getMaxPersistentIdentityValues();
    int maxId = maxPersistentIdentityValues.get("PointTemplate") + 1;
    for (PointTemplateEntity e: entities) {
      e.setPersistentIdentity(maxId++);  
    }
    
    return new PointTemplateMigrationGenerator().generateInsertMigration(entities);
  }
  
  @Override
  public String generateMigrationForPointTemplateUpdate(String json) throws JsonMappingException, JsonProcessingException {
   
    invalidateDictionaryData();
    ensureDictionaryDataIsLoaded();
    
    List<PointTemplateEntity> afterEntities = new PointTemplateJsonToEntityMapper().mapFromJson(json);
    
    Map<PointTemplateEntity, PointTemplateEntity> entities = new LinkedHashMap<>();
    for (PointTemplateEntity afterEntity: afterEntities) {
      
      try {
        entities.put(DictionaryContext.getNodeTagTemplatesContainer().getPointTemplate(afterEntity.getPersistentIdentity()), afterEntity);
      } catch (EntityDoesNotExistException e) {
        throw new IllegalStateException("Point template with id: ["
            + afterEntity.getPersistentIdentity()
            + "], does not exist.");
      }
    }
    
    return new PointTemplateMigrationGenerator().generateUpdateMigration(entities);
  }

  @Override
  public String generateMigrationForPointTemplateDelete(String json) throws JsonMappingException, JsonProcessingException, EntityDoesNotExistException {
   
    invalidateDictionaryData();
    ensureDictionaryDataIsLoaded();
    
    List<Integer> pointTemplateIds = AbstractEntity.OBJECT_MAPPER.get().readValue(json, new TypeReference<List<Integer>>() {});
    List<PointTemplateEntity> pointTemplatesToDelete = new ArrayList<>();
    for (Integer pointTemplateId: pointTemplateIds) {
      pointTemplatesToDelete.add(DictionaryContext.getNodeTagTemplatesContainer().getPointTemplate(pointTemplateId));
    }
    
    List<NodeTagTemplateDto> dtoList = PointTemplateEntity
      .Mapper
      .getInstance()
      .mapEntitiesToDtos(pointTemplatesToDelete);
    
    String dtosJson = AbstractEntity.OBJECT_WRITER.get().writeValueAsString(dtoList);
    
    List<PointTemplateEntity> entities = new PointTemplateJsonToEntityMapper().mapFromJson(dtosJson);
    
    return new PointTemplateMigrationGenerator().generateDeleteMigration(entities);
  }  
  
  
  @Override
  public String generateMigrationForAdFunctionTemplateInsert(String json) throws JsonMappingException, JsonProcessingException {
   
    invalidateDictionaryData();
    ensureDictionaryDataIsLoaded();
    
    List<AbstractAdFunctionTemplateEntity> entities = new AdFunctionTemplateJsonToEntityMapper().mapFromJson(json);
    
    for (AbstractAdFunctionTemplateEntity e: entities) {
      
      List<SimpleValidationMessage> issues = e.validateSimple();
      if (!issues.isEmpty()) {
        throw new IllegalStateException("Invalid: [" 
            + e.getFaultOrReferenceNumber() 
            + "], issues: "
            + issues);
      }
    }
    
    return new AdFunctionTemplateMigrationGenerator().generateInsertMigration(entities);
  }
  
  @Override
  public String generateMigrationForAdFunctionTemplateUpdate(String json) throws JsonMappingException, JsonProcessingException {
   
    invalidateDictionaryData();
    ensureDictionaryDataIsLoaded();
    
    List<AbstractAdFunctionTemplateEntity> afterEntities = new AdFunctionTemplateJsonToEntityMapper().mapFromJson(json);
    
    Map<AbstractAdFunctionTemplateEntity, AbstractAdFunctionTemplateEntity> entities = new LinkedHashMap<>();
    for (AbstractAdFunctionTemplateEntity afterEntity: afterEntities) {

      List<SimpleValidationMessage> issues = afterEntity.validateSimple();
      if (!issues.isEmpty()) {
        throw new IllegalStateException("Invalid: [" 
            + afterEntity.getFaultOrReferenceNumber() 
            + "], issues: "
            + issues);
      }
      
      entities.put(DictionaryContext.getAdFunctionTemplatesContainer().getAdFunctionTemplate(afterEntity.getPersistentIdentity()), afterEntity);
    }
    
    return new AdFunctionTemplateMigrationGenerator().generateUpdateMigration(entities);
  }

  @Override
  public String generateMigrationForAdFunctionTemplateDelete(String json) throws JsonMappingException, JsonProcessingException {
   
    invalidateDictionaryData();
    ensureDictionaryDataIsLoaded();
    
    List<AbstractAdFunctionTemplateEntity> entities = new AdFunctionTemplateJsonToEntityMapper().mapFromJson(json);
    
    return new AdFunctionTemplateMigrationGenerator().generateDeleteMigration(entities);
  }
  
  @Override
  public String generateMigrationsForAdFunctionTemplates(MigrationRequest request) throws JsonMappingException, JsonProcessingException, EntityDoesNotExistException {
    
    // INVALIDATE THE DICTIONARY CACHE
    invalidateDictionaryData();
    ensureDictionaryDataIsLoaded();
    
    
    // GET THE RULE MANAGER APP DEFINITIONS
    Map<String, Rule> armsRules = new TreeMap<>();
    boolean retrieveImplementedOnly = false;
    List<DatabaseWrapperDto> dtoList = dictionaryRepository.getRuleManagerAppRuleDefinitions(request, retrieveImplementedOnly);
    for (DatabaseWrapperDto dto: dtoList) {
      
      String functionTemplateJson = dto.getJson();
      Rule armsRule = AbstractEntity.OBJECT_MAPPER.get().readValue(functionTemplateJson, new TypeReference<Rule>(){});
      armsRules.put(armsRule.getNumber(), armsRule);
    }
    
    
    // GET THE IMPLEMENTED AD FUNCTION TEMPLATES
    Map<String, AbstractAdFunctionTemplateEntity> existingAdFunctionTemplates = new HashMap<>();
    for (AbstractAdFunctionTemplateEntity adFunctionTemplate: DictionaryContext.getAdFunctionTemplatesContainer().getAdFunctionTemplates()) {
      existingAdFunctionTemplates.put(adFunctionTemplate.getFaultOrReferenceNumber(), adFunctionTemplate);
    }
    
    
    // MAP FROM ARMS RULE TO AD FUNCTION ENTITY
    Map<String, Integer> maxPersistentIdentityValues = getMaxPersistentIdentityValues(); 
    List<AdFunctionTemplateDto> newAdFunctionTemplates = new ArrayList<>();
    List<AdFunctionTemplateDto> updatedAdFunctionTemplates = new ArrayList<>();
    List<AdFunctionTemplateDto> deletedAdFunctionTemplates = new ArrayList<>();
    ArmsToAdFunctionTemplateDtoMapper mapper = new ArmsToAdFunctionTemplateDtoMapper();
    
    // IF THE REQUEST IS EMPTY FOR NATURAL IDENTITIES, THEN ASSUME A DATABASE-WIDE RECONCILIATION OF IMPLEMENTED AD RULES ONLY.
    List<String> naturalIdentities = request.getNaturalIdentities();
    if (naturalIdentities.size() == 1 && naturalIdentities.get(0).trim().equalsIgnoreCase("ALL")) {
      
      naturalIdentities = new ArrayList<>();
      AdFunctionTemplatesContainer adFunctionTemplatesContainer = dictionaryRepository.getAdFunctionTemplatesContainer();
      for (AdRuleFunctionTemplateEntity adRuleFunctionTemplateEntity: adFunctionTemplatesContainer.getAdRuleFunctionTemplates()) {
        
        String faultNumber = adRuleFunctionTemplateEntity.getFaultNumber();
        if (!faultNumber.equals("2.5.9.1") 
            && !faultNumber.equals("2.5.9.2")) {

          LOGGER.info("RECONCILING: RULE" + faultNumber);
          naturalIdentities.add(faultNumber);
        } else {
          LOGGER.error("IGNORING RULE RECONCILATION: " + faultNumber);
        }
      }
    }
    
    for (String faultNumber: naturalIdentities) {
      
      Rule armsRule = armsRules.get(faultNumber);
      if (armsRule == null) {
        throw new IllegalStateException("Cannot find rule specification for: [" + faultNumber + "]");
      }
    
      AbstractAdFunctionTemplateEntity existingAdFunctionTemplate = existingAdFunctionTemplates.get(faultNumber);
      if (existingAdFunctionTemplate == null) {

        // INSERT
        AdFunctionTemplateDto dto = mapper.migrateArmsToAdFunctionTemplate(
            armsRule,
            null,
            maxPersistentIdentityValues);
        
        // MAKE SURE ALL NEW AD FUNCTION TEMPLATES HAVE A DELAY CONSTANT, AT LEAST ONE INPUT POINT AND ONE OUTPUT POINT.
        boolean foundDelayConstant = false;
        for (AdFunctionTemplateInputConstantDto c: dto.getInputConsts()) {
          if (c.getName().equals("DELAY")) {
            foundDelayConstant = true;
            break;
          }
        }
        if (!foundDelayConstant) {
          throw new IllegalStateException("Missing DELAY constant for :[ " + dto.getReferenceNumber() + "].");
        }
        if (dto.getInputPoints().isEmpty()) {
          throw new IllegalStateException("No input points defined for :[ " + dto.getReferenceNumber() + "].");
        }
        if (dto.getOutputPoints().isEmpty()) {
          throw new IllegalStateException("No output points defined for :[ " + dto.getReferenceNumber() + "].");
        }
        
        newAdFunctionTemplates.add(dto);
        
      } else {
        
        if (!request.getPerformDelete()) {

          // UPDATE
          updatedAdFunctionTemplates.add(mapper.migrateArmsToAdFunctionTemplate(
              armsRule,
              existingAdFunctionTemplate,
              maxPersistentIdentityValues));
          
        } else {

          // DELETE
          deletedAdFunctionTemplates.add(mapper.migrateArmsToAdFunctionTemplate(
              armsRule,
              existingAdFunctionTemplate,
              maxPersistentIdentityValues));
          
        }
      }
    }
    
    
    // CREATE THE MIGRATION FOR NEW AD FUNCTION TEMPLATES
    StringBuilder sb = new StringBuilder();
    if (!newAdFunctionTemplates.isEmpty()) {
      
      String json = AbstractEntity.OBJECT_WRITER.get().writeValueAsString(newAdFunctionTemplates);
      String migration = generateMigrationForAdFunctionTemplateInsert(json);
      sb.append("NEW AD FUNCTION TEMPLATES:\n");
      sb.append("==========================\n");
      sb.append(migration);
      sb.append("\n");
    }
    
    
    // CREATE THE MIGRATION FOR UPDATED AD FUNCTION TEMPLATES
    if (!updatedAdFunctionTemplates.isEmpty()) {

      String json = AbstractEntity.OBJECT_WRITER.get().writeValueAsString(updatedAdFunctionTemplates);
      String migration = generateMigrationForAdFunctionTemplateUpdate(json);
      sb.append("UPDATED AD FUNCTION TEMPLATES:\n");
      sb.append("==========================\n");
      sb.append(migration);
      sb.append("\n");
    }
    
    
    // CREATE THE MIGRATION FOR DELETED AD FUNCTION TEMPLATES
    if (!deletedAdFunctionTemplates.isEmpty()) {

      String json = AbstractEntity.OBJECT_WRITER.get().writeValueAsString(deletedAdFunctionTemplates);
      String migration = generateMigrationForAdFunctionTemplateDelete(json);
      sb.append("DELETED AD FUNCTION TEMPLATES:\n");
      sb.append("==========================\n");
      sb.append(migration);
      sb.append("\n");
    }
    
    return sb.toString();
  }
  
  @Override
  public String reconcileAdFunctionTemplatesWithSpecifications(MigrationRequest request) throws JsonMappingException, JsonProcessingException, EntityDoesNotExistException {
    
    // INVALIDATE THE DICTIONARY CACHE
    invalidateDictionaryData();
    ensureDictionaryDataIsLoaded();
    
    
    // GET THE IMPLEMENTED AD FUNCTION TEMPLATES
    Map<String, AdRuleFunctionTemplateEntity> existingRuleTemplates = new TreeMap<>();
    for (AbstractAdFunctionTemplateEntity adFunctionTemplate: DictionaryContext.getAdFunctionTemplatesContainer().getAdFunctionTemplates()) {
      
      if (adFunctionTemplate instanceof AdRuleFunctionTemplateEntity) {
        existingRuleTemplates.put(adFunctionTemplate.getFaultOrReferenceNumber(), (AdRuleFunctionTemplateEntity)adFunctionTemplate);  
      }
    }

    
    // GET THE RULE MANAGER APP DEFINITIONS
    Map<String, Rule> armsRules = new TreeMap<>();
    boolean retrieveImplementedOnly = false;
    List<DatabaseWrapperDto> dtoList = dictionaryRepository.getRuleManagerAppRuleDefinitions(request, retrieveImplementedOnly);
    for (DatabaseWrapperDto dto: dtoList) {
      
      String functionTemplateJson = dto.getJson();
      Rule armsRule = AbstractEntity.OBJECT_MAPPER.get().readValue(functionTemplateJson, new TypeReference<Rule>(){});
      String faultNumber = armsRule.getNumber();
      if (existingRuleTemplates.keySet().contains(faultNumber)) {
      
        armsRules.put(faultNumber, armsRule);
      }
    }
    
    
    // ITERATE THROUGH THE IMPLEMENTED AD FUNCTION TEMPLATES AND COMPARE TO THE RULE MANAGER APP DEFINITIONS
    boolean processRuleSignatureIssuesOnly = request.getProcessRuleSignatureIssuesOnly();
    Map<String, Integer> maxPersistentIdentityValues = getMaxPersistentIdentityValues(); 
    ArmsToAdFunctionTemplateDtoMapper mapper = new ArmsToAdFunctionTemplateDtoMapper();
    StringBuilder sb = new StringBuilder();
    IssueCounter issueCount = new IssueCounter();
    for (Map.Entry<String, AdRuleFunctionTemplateEntity> entry: existingRuleTemplates.entrySet()) {

      String faultNumber = entry.getKey();
      AdRuleFunctionTemplateEntity impl = entry.getValue();
      
      boolean headerPrinted = false;
      try {

        // MAP THE RULE MANAGER APP DEFINITION FROM A DTO TO AN ENTITY
        Rule armsRule = armsRules.get(faultNumber);
        if (armsRule == null) {
          throw new IllegalStateException("Cannot find rule specification for: [" + faultNumber + "]");
        }
        AdFunctionTemplateDto dto = mapper.migrateArmsToAdFunctionTemplate(
            armsRule,
            impl,
            maxPersistentIdentityValues);
        
        AbstractAdFunctionTemplateEntity spec = AbstractAdFunctionTemplateEntity
            .Mapper
            .getInstance()
            .mapDtoToEntity(
                DictionaryContext.getAdFunctionTemplatesContainer(), 
                dto);
        
        
        // COMPARE THE SPECIFICATION ENTITY TO THE IMPLEMENTATION ENTITY

        
        // NAME
        String specName = spec.getDisplayName();
        String implName = impl.getDisplayName();
        if (!processRuleSignatureIssuesOnly && !specName.equals(implName)) {
          
          if (!headerPrinted) {
            headerPrinted = true;
            header(sb, issueCount, impl.getFullDisplayName());
          }
          
          sb.append("Spec display name: [")
              .append(specName)
              .append("] doesn't match impl display name: [")
              .append(implName)
              .append("]\n");
        }
        
        
        // DESCRIPTION
        String specDescription = spec.getDescription();
        String implDescription = impl.getDescription();
        if (!processRuleSignatureIssuesOnly && !specDescription.equals(implDescription)) {
          
          if (!headerPrinted) {
            headerPrinted = true;
            header(sb, issueCount, impl.getFullDisplayName());
          }
          
          sb.append("Specification description: [")
              .append(specDescription)
              .append("] doesn't match implementation: [")
              .append(implDescription)
              .append("]\n");
        }
        
        
        // ENERGY EXCHANGE TYPE
        String specEnergyExchangeType = spec.getEnergyExchangeType().getName();
        String implEnergyExchangeType = impl.getEnergyExchangeType().getName();
        if (!specEnergyExchangeType.equals(implEnergyExchangeType)) {
          
          if (!headerPrinted) {
            headerPrinted = true;
            header(sb, issueCount, impl.getFullDisplayName());
          }
          
          sb.append("Specification energy exchange type: [")
              .append(specEnergyExchangeType)
              .append("] doesn't match implementation: [")
              .append(implEnergyExchangeType)
              .append("]\n");
        }        
        
        
        // NODE FILTER EXPRESSION
        String specNodeFilterExpression = spec.getNodeFilterExpression();
        if (specNodeFilterExpression == null) {
          specNodeFilterExpression = "";
        }
        String implNodeFilterExpression = impl.getNodeFilterExpression();
        if (implNodeFilterExpression == null) {
          implNodeFilterExpression = "";
        }
        if (!specNodeFilterExpression.equals(implNodeFilterExpression)) {
          
          if (!headerPrinted) {
            headerPrinted = true;
            header(sb, issueCount, impl.getFullDisplayName());
          }
          
          sb.append("Specification equipment metadata expression: [")
              .append(specNodeFilterExpression)
              .append("] doesn't match implementation: [")
              .append(implNodeFilterExpression)
              .append("]\n");
        }
        
        
        // INPUT POINTS
        SortedMap<String, AdFunctionTemplateInputPointEntity> specInputPoints = new TreeMap<>();
        for (AdFunctionTemplateInputPointEntity ic: spec.getInputPoints()) {
          specInputPoints.put(ic.getName(), ic);
        }
        SortedMap<String, AdFunctionTemplateInputPointEntity> implInputPoints = new TreeMap<>();
        for (AdFunctionTemplateInputPointEntity ic: impl.getInputPoints()) {
          implInputPoints.put(ic.getName(), ic);
        }
        if (specInputPoints.size() > implInputPoints.size()) {
          for (AdFunctionTemplateInputPointEntity specInputPoint: specInputPoints.values()) {
            
            AdFunctionTemplateInputPointEntity implInputPoint = implInputPoints.get(specInputPoint.getName());
            if (implInputPoint == null) {

              if (!headerPrinted) {
                headerPrinted = true;
                header(sb, issueCount, impl.getFullDisplayName());
              }
              
              sb.append("Specification input point: [")
                  .append(specInputPoint.getName())
                  .append("] doesn't exist in implementation: ")
                  .append(implInputPoints.keySet())
                  .append("\n");
              
            } else {
              
              headerPrinted = compareInputPoints(sb, issueCount, impl.getFullDisplayName(), headerPrinted, processRuleSignatureIssuesOnly, specInputPoint, implInputPoint);
              
            }
          }
          
        } else {
          
          for (AdFunctionTemplateInputPointEntity implInputPoint: implInputPoints.values()) {
            
            AdFunctionTemplateInputPointEntity specInputPoint = specInputPoints.get(implInputPoint.getName());
            if (specInputPoint == null) {
              if (!implInputPoint.getName().equals("Sensor")) {

                if (!headerPrinted) {
                  headerPrinted = true;
                  header(sb, issueCount, impl.getFullDisplayName());
                }
                
                sb.append("Implementation input point: [")
                    .append(implInputPoint.getName())
                    .append("] doesn't exist in specification: ")
                    .append(specInputPoints.keySet())
                    .append("\n");
              }
            } else {
              
              headerPrinted = compareInputPoints(sb, issueCount, impl.getFullDisplayName(), headerPrinted, processRuleSignatureIssuesOnly, specInputPoint, implInputPoint);
              
            }
          }
          
        }
        
        
        // INPUT CONSTANTS
        SortedMap<String, AdFunctionTemplateInputConstantEntity> specInputConstants = new TreeMap<>();
        for (AdFunctionTemplateInputConstantEntity ic: spec.getInputConstants()) {
          specInputConstants.put(ic.getName(), ic);
        }
        SortedMap<String, AdFunctionTemplateInputConstantEntity> implInputConstants = new TreeMap<>();
        for (AdFunctionTemplateInputConstantEntity ic: impl.getInputConstants()) {
          implInputConstants.put(ic.getName(), ic);
        }
        if (specInputConstants.size() > implInputConstants.size()) {
          for (AdFunctionTemplateInputConstantEntity specInputConstant: specInputConstants.values()) {
            
            AdFunctionTemplateInputConstantEntity implInputConstant = implInputConstants.get(specInputConstant.getName());
            if (implInputConstant == null) {

              if (!headerPrinted) {
                headerPrinted = true;
                header(sb, issueCount, impl.getFullDisplayName());
              }
              
              sb.append("Specification input constant: [")
                  .append(specInputConstant.getName())
                  .append("] doesn't exist in implementation: ")
                  .append(implInputConstants.keySet())
                  .append("\n");
              
            } else {
              
              headerPrinted = compareInputConstants(sb, issueCount, impl.getFullDisplayName(), headerPrinted, processRuleSignatureIssuesOnly, specInputConstant, implInputConstant);
              
            }
          }
          
        } else {
          
          for (AdFunctionTemplateInputConstantEntity implInputConstant: implInputConstants.values()) {
            
            AdFunctionTemplateInputConstantEntity specInputConstant = specInputConstants.get(implInputConstant.getName());
            if (specInputConstant == null) {

              if (!headerPrinted) {
                headerPrinted = true;
                header(sb, issueCount, impl.getFullDisplayName());
              }
              
              sb.append("Implementation input constant: [")
                  .append(implInputConstant.getName())
                  .append("] doesn't exist in specification: ")
                  .append(specInputConstants.keySet())
                  .append("\n");
              
            } else {
              
              headerPrinted = compareInputConstants(sb, issueCount, impl.getFullDisplayName(), headerPrinted, processRuleSignatureIssuesOnly, specInputConstant, implInputConstant);
              
            }
          }
          
        }
        
      } catch (Exception e) {

        sb.append("\n\n")
            .append(impl.getFullDisplayName())
            .append("\n")
            .append(e.getMessage())
            .append("\n");
        
      }
    }
    
    sb.append("\n\nNumber of issues: ")
        .append(issueCount.getIssueCount());
    
    return sb.toString();
  }
  
  @Override
  public SortedMap<Integer, String> getAffectedReportsForAdFunctionTemplateId(int adFunctionTemplateId) {
    
    return reportQueryDao.getAffectedReportsForAdFunctionTemplateId(adFunctionTemplateId);
  }
  
  @Override 
  public SortedMap<String, String> getAffectedRulesReportsForPointTemplateId(int pointTemplateId) {
    
    SortedMap<String, String> response = reportQueryDao.getAffectedReportsForPointTemplateId(pointTemplateId);
    
    response.putAll(adFunctionTemplateQueryDao.getAffectedRulesForPointTemplateId(pointTemplateId));
    
    return response;
  }

  @Override 
  public SortedMap<String, String> affectedRulesReportsForTagId(int tagId) {
    
    SortedMap<String, String> response = reportQueryDao.getAffectedReportsForTagId(tagId);
    
    response.putAll(adFunctionTemplateQueryDao.getAffectedRulesForTagId(tagId));
    
    return response;
  }
  
  private void header(
      StringBuilder sb,
      IssueCounter issueCount,
      String fullDisplayName) {
    
    issueCount.increment();
    
    sb.append("\n\n")
        .append(fullDisplayName)
        .append("\n");
  }
  
  private boolean compareInputPoints(
      StringBuilder sb,
      IssueCounter issueCount,
      String fullDisplayName,
      boolean headerPrinted,
      boolean processRuleSignatureIssuesOnly,
      AdFunctionTemplateInputPointEntity spec,
      AdFunctionTemplateInputPointEntity impl) {
    
    // DESCRIPTION
    String specDescription = spec.getDescription();
    String implDescription = impl.getDescription();
    if (!processRuleSignatureIssuesOnly && !specDescription.equals(implDescription)) {
      
      if (!headerPrinted) {
        headerPrinted = true;
        header(sb, issueCount, fullDisplayName);
      }
      
      sb.append("Specification input point: [")
          .append(spec.getName())
          .append("] description: [")
          .append(specDescription)
          .append("] doesn't match implementation: [")
          .append(implDescription)
          .append("]\n");
    }
    
    
    // IS REQUIRED
    String specIsRequired = spec.getIsRequired().toString();
    String implIsRequired = impl.getIsRequired().toString();
    if (!specIsRequired.equals(implIsRequired)) {

      if (!headerPrinted) {
        headerPrinted = true;
        header(sb, issueCount, fullDisplayName);
      }
      
      sb.append("Specification input point is_required: [")
          .append(specIsRequired)
          .append("] doesn't match implementation: [")
          .append(implIsRequired)
          .append("]\n");         
    }    

    
    // IS REQUIRED
    String specIsArray = spec.getIsArray().toString();
    String implIsArray = impl.getIsArray().toString();
    if (!specIsArray.equals(implIsArray)) {

      if (!headerPrinted) {
        headerPrinted = true;
        header(sb, issueCount, fullDisplayName);
      }
      
      sb.append("Specification input point is_array: [")
          .append(specIsArray)
          .append("] doesn't match implementation: [")
          .append(implIsArray)
          .append("]\n");         
    }
    
    
    // TAGS
    String specTags = spec.getTags().toString();
    String implTags = impl.getTags().toString();
    if (!specTags.equals(implTags)) {

      if (!headerPrinted) {
        headerPrinted = true;
        header(sb, issueCount, fullDisplayName);
      }
      
      sb.append("Specification input point haystack tags: ")
          .append(specTags)
          .append(" doesn't match implementation: ")
          .append(implTags)
          .append("\n");         
    }    
    
    
    // CURRENT OBJECT EXPRESSION
    String specCurrentObjectExpression = spec.getCurrentObjectExpression();
    if (specCurrentObjectExpression == null) {
      specCurrentObjectExpression = "";
    }
    String implCurrentObjectExpression = impl.getCurrentObjectExpression();
    if (implCurrentObjectExpression == null) {
      implCurrentObjectExpression = "";
    }
    if (!specCurrentObjectExpression.equals(implCurrentObjectExpression)) {

      if (!headerPrinted) {
        headerPrinted = true;
        header(sb, issueCount, fullDisplayName);
      }
      
      sb.append("Specification input point current object expression: [")
          .append(specCurrentObjectExpression)
          .append("] doesn't match implementation: [")
          .append(implCurrentObjectExpression)
          .append("]\n");         
    }
    
    return headerPrinted;
  }
  
  private boolean compareInputConstants(
      StringBuilder sb,
      IssueCounter issueCount,
      String fullDisplayName,
      boolean headerPrinted,
      boolean processRuleSignatureIssuesOnly,
      AdFunctionTemplateInputConstantEntity spec,
      AdFunctionTemplateInputConstantEntity impl) {
    
    // DESCRIPTION
    String specDescription = spec.getDescription();
    String implDescription = impl.getDescription();
    if (!processRuleSignatureIssuesOnly && !specDescription.equals(implDescription)) {
      
      if (!headerPrinted) {
        headerPrinted = true;
        header(sb, issueCount, fullDisplayName);
      }
      
      sb.append("Specification input constant: [")
          .append(spec.getName())
          .append("] description: [")
          .append(specDescription)
          .append("] doesn't match implementation: [")
          .append(implDescription)
          .append("]\n");
    }

    
    // DEFAULT VALUE
    String specDefaultValue = spec.getDefaultValue();
    String implDefaultValue = impl.getDefaultValue();
    if (specDefaultValue.equals("1") && implDefaultValue.equals("1.0")) {
      specDefaultValue = "1.0"; 
    }
    if (specDefaultValue.equals("1.0") && implDefaultValue.equals("1")) {
      implDefaultValue = "1.0"; 
    }
    if (!specDefaultValue.equals(implDefaultValue)) {

      if (!headerPrinted) {
        headerPrinted = true;
        header(sb, issueCount, fullDisplayName);
      }
      
      sb.append("Specification input constant: [")
          .append(spec.getName())
          .append("] default value: [")
          .append(specDefaultValue)
          .append("] doesn't match implementation: [")
          .append(implDefaultValue)
          .append("]\n");
    }
    return headerPrinted;
  }
  
  private static final class IssueCounter {
    
    int issueCount = 0;
    
    public void increment() {
      issueCount = issueCount + 1;
    }
    
    public int getIssueCount() {
      return issueCount;
    }
  }  
}
//@formatter:on