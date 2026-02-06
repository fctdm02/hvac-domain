// @formatter:off
package com.djt.hvac.domain.model.dictionary.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.dictionary.PaymentPlanEntity;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.UnitEntity;
import com.djt.hvac.domain.model.dictionary.container.AdFunctionTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.container.NodeTagTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.container.PaymentPlansContainer;
import com.djt.hvac.domain.model.dictionary.container.ReportTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.container.ScheduledEventTypesContainer;
import com.djt.hvac.domain.model.dictionary.container.TagsContainer;
import com.djt.hvac.domain.model.dictionary.container.UnitsContainer;
import com.djt.hvac.domain.model.dictionary.container.WeatherStationsContainer;
import com.djt.hvac.domain.model.dictionary.dto.AdFunctionTemplateInputConstantPointTemplateMappingDto;
import com.djt.hvac.domain.model.dictionary.dto.PointTemplateUnitMappingDto;
import com.djt.hvac.domain.model.dictionary.dto.UnitMappingDto;
import com.djt.hvac.domain.model.dictionary.energyexchange.EquipmentEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.LoopEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.PlantEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.enums.PaymentInterval;
import com.djt.hvac.domain.model.dictionary.event.DictionaryChangeEventSubscriber;
import com.djt.hvac.domain.model.dictionary.service.command.MigrationRequest;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.UnitMappingEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * 
 * @author tommyers
 * 
 */
public interface DictionaryService extends DictionaryChangeEventSubscriber {
  
  /**
   * 
   * @return The greatest point cap that is available.
   */
  Integer getMaxPointCap();
  
  /**
   * 
   * @param mappedPointCount The mapped point count
   * 
   * @return A list of qualifying payment plans (where the point cap is greater than the given 
   * <code>mappedPointCount</code>), where they are sorted by cost per interval, lowest to highest.  
   */
  List<PaymentPlanEntity> getQualifyingPaymentPlansAsList(int mappedPointCount);

  /**
   * 
   * @param mappedPointCount The mapped point count
   * 
   * @return A map whose key is the point cap (that is greater than the given <code>mappedPointCount</code>)
   * and value is another map that is keyed by the two payment interval types (MONTHLY and YEARLY), and 
   * finally, whose value is the payment plan itself (with the point cap and interval matching the keys in the
   * inner and outer maps). This map is ordered such that a depth first traversal results in the same ordering
   * as <code>getQualifyingPaymentPlansAsList</code>, where they are sorted by cost per interval, lowest to highest.  
   */
  Map<Integer, Map<PaymentInterval, PaymentPlanEntity>> getQualifyingPaymentPlans(int mappedPointCount);

  /**
   * 
   * @param mappedPointCount The mapped point count
   * 
   * @return The lowest cost per interval payment plan for the given mapped point count
   */
  PaymentPlanEntity getLowestCostPerIntervalQualifyingPaymentPlan(int mappedPointCount);
  
  /**
   * 
   * @return TagsContainer 
   */
  TagsContainer getTagsContainer();

  /**
   * 
   * @return UnitsContainer
   */
  UnitsContainer getUnitsContainer();

  /**
   * 
   * @return NodeTagTemplatesContainer
   */
  NodeTagTemplatesContainer getNodeTagTemplatesContainer();

  /**
   * 
   * @return ScheduledEventTypesContainer
   */
  ScheduledEventTypesContainer getScheduledEventTypesContainer();

  /**
   * 
   * @return AdFunctionTemplatesContainer
   */
  AdFunctionTemplatesContainer getAdFunctionTemplatesContainer();
  
  /**
   * 
   * @return ReportTemplatesContainer
   */
  ReportTemplatesContainer getReportTemplatesContainer();
  
  /**
   * 
   * @return PaymentPlansContainer 
   */
  PaymentPlansContainer getPaymentPlansContainer();
  
  /**
   * 
   * @return WeatherStationsContainer
   */
  WeatherStationsContainer getWeatherStationsContainer();
  
  /**
   * 
   */
  void ensureDictionaryDataIsLoaded();
  
  /**
   * 
   */
  void invalidateDictionaryData();
  
  /**
   * 
   * @param adFunctionTemplateId The AD function template to update
   * @param version The new version to set
   */
  void updateAdFunctionTemplateVersion(Integer adFunctionTemplateId, Integer version);
  
  /**
   * 
   * @param pointTemplateId The point template id
   * @param name The point template name (NOTE: Point template names must be unique)
   * @param description The point template description
   * @param isPublic Whether the point template is public or not
   * @param tags The set of point haystack tags for the point template
   * @param unitId The unit id to set
   * @param isDeprecated Whether the point template is deprecated or not
   * @param replacementPointTemplateId The replacement point template id (if marked as deprecated)
   * @param parentEquipmentTypes The set of parent equipment types
   * @param parentPlantTypes The set of parent plant types
   * @param parentLoopTypes The set of parent loop types
   * 
   * @throws EntityDoesNotExistException If the point template does not exist
   */
  void updatePointTemplate(
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
      EntityDoesNotExistException;
  

  
  // METHODS USED BY TESTS TO CREATE DICTIONARY DATA
  /**
   * 
   * @param ipUnit The IP unit
   * @param siUnit The SI unit
   * @param ipToSiConversionFactor The IP to SI conversion factor/formula
   * @param siToIpConversionFactor The SI to IP conversion factor/formula
   * 
   * @return Newly created unit mapping
   * 
   * @throws EntityAlreadyExistsException If the unit mapping already exists
   * @throws EntityDoesNotExistException If any units do not exist
   */
  UnitMappingDto createUnitMapping(
      UnitEntity ipUnit,
      UnitEntity siUnit,
      String ipToSiConversionFactor,
      String siToIpConversionFactor) 
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException;  
  
  /**
   * 
   * @param pointTemplate The point template
   * @param unitMapping The unit mapping
   * @param priority The priority
   * 
   * @return Newly created point template unit mapping
   * 
   * @throws EntityAlreadyExistsException If the point template unit mapping already exists
   * @throws EntityDoesNotExistException If point template or unit mapping does not exist
   */
  PointTemplateUnitMappingDto createPointTemplateUnitMapping(
      PointTemplateEntity pointTemplate,
      UnitMappingEntity unitMapping,
      Integer priority) 
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException;  

  /**
   * 
   * @param adFunctionTemplateInputConstantId The AD function template input constant id
   * @param pointTemplateId The point template id
   * 
   * @return The newly created AD function template input constant to point template mapping
   * 
   * @throws EntityAlreadyExistsException If the AD function template input constant to point template mapping already exists
   * @throws EntityDoesNotExistException If the AD function template input constant or point template does not exist
   */
  AdFunctionTemplateInputConstantPointTemplateMappingDto createAdFunctionTemplateInputConstantPointTemplateMapping(
      Integer adFunctionTemplateInputConstantId,
      Integer pointTemplateId) 
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException;
  
  
  
  // MIGRATION RELATED
  /**
   * 
   * @return A map keyed by entity class name and the max persistent identity value for that entity.
   */
  Map<String, Integer> getMaxPersistentIdentityValues();
  
  String generateMigrationForUnitMappingInsert(String json) throws JsonMappingException, JsonProcessingException;
  String generateMigrationForPointTemplateUnitMappingInsert(String json) throws JsonMappingException, JsonProcessingException;
  String generateMigrationForAdFunctionTemplateInputConstantPointTemplateMappingInsert(String json) throws JsonMappingException, JsonProcessingException;
  
  String generateMigrationForUnitInsert(String json) throws JsonMappingException, JsonProcessingException;
  String generateMigrationForUnitUpdate(String json) throws JsonMappingException, JsonProcessingException;
  String generateMigrationForUnitDelete(String json) throws JsonMappingException, JsonProcessingException;
  
  String generateMigrationForTagInsert(String json) throws JsonMappingException, JsonProcessingException;
  String generateMigrationForTagUpdate(String json) throws JsonMappingException, JsonProcessingException;
  String generateMigrationForTagDelete(String json) throws JsonMappingException, JsonProcessingException, EntityDoesNotExistException;
  
  String generateMigrationForPointTemplateInsert(String json) throws JsonMappingException, JsonProcessingException;
  String generateMigrationForPointTemplateUpdate(String json) throws JsonMappingException, JsonProcessingException;
  String generateMigrationForPointTemplateDelete(String json) throws JsonMappingException, JsonProcessingException, EntityDoesNotExistException;

  String generateMigrationForAdFunctionTemplateInsert(String json) throws JsonMappingException, JsonProcessingException;
  String generateMigrationForAdFunctionTemplateUpdate(String json) throws JsonMappingException, JsonProcessingException;
  String generateMigrationForAdFunctionTemplateDelete(String json) throws JsonMappingException, JsonProcessingException;
  
  String generateMigrationsForAdFunctionTemplates(MigrationRequest request) throws JsonMappingException, JsonProcessingException, EntityDoesNotExistException;
  
  String reconcileAdFunctionTemplatesWithSpecifications(MigrationRequest request) throws JsonMappingException, JsonProcessingException, EntityDoesNotExistException;
  
  
  // CEREBRO RELATED
  /**
   * 
   * @param adFunctionTemplateId The AD function template id to get affected reports for
   * @return The set of affected reports (key is report id, value is report name)
   */
  SortedMap<Integer, String> getAffectedReportsForAdFunctionTemplateId(int adFunctionTemplateId);
  
  /**
   * 
   * @param pointTemplateId The point template id to get affected rules/reports for
   * @return The set of affected rules/reports (key is rule/report id, value is rule/report name)
   */
  SortedMap<String, String> getAffectedRulesReportsForPointTemplateId(int pointTemplateId);

  /**
   * 
   * @param tagId The tag id to get affected rules/reports for
   * @return The set of affected rules/reports (key is rule/report id, value is rule/report name)
   */
  SortedMap<String, String> affectedRulesReportsForTagId(int tagId);
  
  
  
  
}
// @formatter:on