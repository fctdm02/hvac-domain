//@formatter:off
package com.djt.hvac.domain.model.dictionary.repository;

import java.util.List;
import java.util.Set;

import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
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
import com.djt.hvac.domain.model.dictionary.dto.function.DatabaseWrapperDto;
import com.djt.hvac.domain.model.dictionary.energyexchange.EquipmentEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.LoopEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.PlantEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.service.command.MigrationRequest;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.UnitMappingEntity;

public interface DictionaryRepository {
  
  void ensureDictionaryDataIsLoaded();
  
  void invalidateDictionaryData();

  TagsContainer getTagsContainer();

  UnitsContainer getUnitsContainer();

  NodeTagTemplatesContainer getNodeTagTemplatesContainer();

  ScheduledEventTypesContainer getScheduledEventTypesContainer();

  AdFunctionTemplatesContainer getAdFunctionTemplatesContainer();
  
  ReportTemplatesContainer getReportTemplatesContainer();
  
  PaymentPlansContainer getPaymentPlansContainer();
  
  WeatherStationsContainer getWeatherStationsContainer();

  /**
   * 
   * @param weatherStationId The weather station id
   * @param customerId The customer id
   * @param buildingUnitSystem The building unit system 
   * @param submittedBy The submitting user
   * 
   * @return The number of weather station global computed points that were created
   * 
   * @throws EntityDoesNotExistException If the weather station does not exist
   */
  int storeNewlyCreatedWeatherStationGlobalPoints(Integer weatherStationId, Integer customerId, String buildingUnitSystem, String submittedBy) throws EntityDoesNotExistException;
  
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
   * @param replacementPointTemplateId The replacement point template id (if marked as deprecated
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

  /**
   *
   * REQUIRES THE FOLLOWING TUNNEL:
   * ssh -L 5436:postgres-arms.dc.res0.local:5432 mgmt.dc.res0.local
   * 
   * @param request A request containing both connection info and info on what entities are of interest
   * @param retrieveImplementedOnly Only retrieve implemented entities
   * 
   * @return A list of JSON wrapper objects for specs/definitions of entities
   */
  List<DatabaseWrapperDto> getRuleManagerAppRuleDefinitions(MigrationRequest request, boolean retrieveImplementedOnly);
}
//@formatter:on