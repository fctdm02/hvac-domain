//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.mapper;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.common.mapper.DtoMapper;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.customer.OnlineCustomerEntity;
import com.djt.hvac.domain.model.dictionary.PaymentPlanEntity;
import com.djt.hvac.domain.model.dictionary.ScheduledEventTypeEntity;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.UnitEntity;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.container.PaymentPlansContainer;
import com.djt.hvac.domain.model.dictionary.container.WeatherStationsContainer;
import com.djt.hvac.domain.model.dictionary.energyexchange.enums.EnergyExchangeSystemType;
import com.djt.hvac.domain.model.dictionary.enums.DataType;
import com.djt.hvac.domain.model.dictionary.enums.NodeType;
import com.djt.hvac.domain.model.dictionary.enums.UnitSystem;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;
import com.djt.hvac.domain.model.dictionary.weather.WeatherStationEntity;
import com.djt.hvac.domain.model.distributor.AbstractDistributorEntity;
import com.djt.hvac.domain.model.distributor.OnlineDistributorEntity;
import com.djt.hvac.domain.model.function.AbstractAdFunctionInstanceEntity;
import com.djt.hvac.domain.model.function.AdFunctionErrorMessagesEntity;
import com.djt.hvac.domain.model.function.dto.AdFunctionErrorMessagesDto;
import com.djt.hvac.domain.model.function.dto.AdFunctionInstanceDto;
import com.djt.hvac.domain.model.nodehierarchy.AbstractNodeEntity;
import com.djt.hvac.domain.model.nodehierarchy.FloorEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.SubBuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BillableBuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingSubscriptionEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingTemporalConfigEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingTemporalUtilityEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingPaymentStatus;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingPaymentType;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingStatus;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingUtilityType;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.UtilityComputationInterval;
import com.djt.hvac.domain.model.nodehierarchy.dto.AsyncComputedPointNodeDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.CustomAsyncComputedPointNodeDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.EnergyExchangeSystemEdgeDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.MappablePointNodeDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.NodeTagDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.NonPointNodeDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.ScheduledAsyncComputedPointNodeDto;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.AbstractEnergyExchangeEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EnergyExchangeEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EquipmentEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.LoopEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.PlantEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.AdFunctionAsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.AsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.ManualAsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.ScheduledAsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.SystemAsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.WeatherAsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.CustomAsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.FormulaVariableEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.TemporalAsyncComputedPointConfigEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.enums.ComputationInterval;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.enums.FillPolicy;
import com.djt.hvac.domain.model.nodehierarchy.point.mapped.MappablePointEntity;
import com.djt.hvac.domain.model.rawpoint.RawPointEntity;
import com.djt.hvac.domain.model.rawpoint.dto.RawPointDto;
import com.djt.hvac.domain.model.report.ReportInstanceEntity;
import com.djt.hvac.domain.model.report.dto.ReportInstanceDto;
import com.djt.hvac.domain.model.report.dto.ReportInstanceStatusDto;
import com.djt.hvac.domain.model.report.dto.ReportInstanceStatusErrorMessageDto;
import com.google.common.collect.Maps;

public final class PortfolioDtoMapper implements DtoMapper<PortfolioEntity, PortfolioEntity, Map<String, Object>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(PortfolioDtoMapper.class);

  public static final String RAW_POINT_DTO_LIST = "RAW_POINT_DTO_LIST";
  public static final String NON_POINT_NODE_DTO_LIST = "NON_POINT_NODE_DTO_LIST";
  public static final String MAPPABLE_POINT_NODE_DTO_LIST = "MAPPABLE_POINT_NODE_DTO_LIST";
  public static final String CUSTOM_ASYNC_COMPUTED_POINT_NODE_DTO_LIST = "CUSTOM_ASYNC_COMPUTED_POINT_NODE_DTO_LIST";
  public static final String SCHEDULED_ASYNC_COMPUTED_POINT_NODE_DTO_LIST = "SCHEDULED_ASYNC_COMPUTED_POINT_NODE_DTO_LIST";
  public static final String ASYNC_COMPUTED_POINT_NODE_DTO_LIST = "ASYNC_COMPUTED_POINT_NODE_DTO_LIST";
  public static final String NODE_TAG_DTO_LIST = "POINT_TAG_DTO_LIST";
  public static final String ENERGY_EXCHANGE_SYSTEM_EDGE_DTO_LIST = "ENERGY_EXCHANGE_SYSTEM_EDGE_DTO_LIST";
  public static final String REPORT_INSTANCE_DTO_LIST = "REPORT_INSTANCE_DTO_LIST";
  public static final String REPORT_INSTANCE_STATUS_DTO_LIST = "REPORT_INSTANCE_STATUS_DTO_LIST";
  public static final String AD_FUNCTION_INSTANCE_CANDIDATE_DTO_LIST = "AD_FUNCTION_INSTANCE_CANDIDATE_DTO_LIST";
  public static final String AD_FUNCTION_INSTANCE_DTO_LIST = "AD_FUNCTION_INSTANCE_DTO_LIST";
  public static final String AD_FUNCTION_ERROR_MESSAGES_DTO_LIST = "AD_FUNCTION_ERROR_MESSAGES_DTO_LIST";
  
  private final AbstractCustomerEntity parentCustomer;
  private Map<Integer, RawPointEntity> rawPointMap;

  public PortfolioDtoMapper(AbstractCustomerEntity parentCustomer) {
    this.parentCustomer = parentCustomer;
  }

  @Override
  @SuppressWarnings("unchecked")
  public PortfolioEntity mapDtoToEntity(PortfolioEntity aggregateRoot, Map<String, Object> dtos) {
    
    if (DictionaryContext.getTagsContainer() == null) {
      throw new IllegalStateException("Dictionary data must be loaded before mapping DTOs to entities");
    }

    List<RawPointDto> rawPointDtoList = (List<RawPointDto>) dtos.get(RAW_POINT_DTO_LIST);
    List<NonPointNodeDto> nonPointNodeDtoList = (List<NonPointNodeDto>) dtos.get(NON_POINT_NODE_DTO_LIST);
    List<MappablePointNodeDto> mappablePointNodeDtoList = (List<MappablePointNodeDto>) dtos.get(MAPPABLE_POINT_NODE_DTO_LIST);
    List<CustomAsyncComputedPointNodeDto> customAsyncComputedPointNodeDtoList = (List<CustomAsyncComputedPointNodeDto>) dtos.get(CUSTOM_ASYNC_COMPUTED_POINT_NODE_DTO_LIST);
    List<ScheduledAsyncComputedPointNodeDto> scheduledAsyncComputedPointNodeDtoList = (List<ScheduledAsyncComputedPointNodeDto>) dtos.get(SCHEDULED_ASYNC_COMPUTED_POINT_NODE_DTO_LIST);
    List<AsyncComputedPointNodeDto> asyncComputedPointNodeDtoList = (List<AsyncComputedPointNodeDto>) dtos.get(ASYNC_COMPUTED_POINT_NODE_DTO_LIST);
    List<NodeTagDto> nodeTagDtoList = (List<NodeTagDto>) dtos.get(NODE_TAG_DTO_LIST);
    List<EnergyExchangeSystemEdgeDto> energyExchangeSystemEdgeDtoList = (List<EnergyExchangeSystemEdgeDto>) dtos.get(ENERGY_EXCHANGE_SYSTEM_EDGE_DTO_LIST);  
    List<ReportInstanceDto> reportInstanceDtoList = (List<ReportInstanceDto>) dtos.get(REPORT_INSTANCE_DTO_LIST);
    List<ReportInstanceStatusDto> reportInstanceStatusDtoList = (List<ReportInstanceStatusDto>) dtos.get(REPORT_INSTANCE_STATUS_DTO_LIST);
    List<AdFunctionInstanceDto> adFunctionInstanceCandidateDtoList = (List<AdFunctionInstanceDto>) dtos.get(AD_FUNCTION_INSTANCE_CANDIDATE_DTO_LIST);
    List<AdFunctionInstanceDto> adFunctionInstanceDtoList = (List<AdFunctionInstanceDto>) dtos.get(AD_FUNCTION_INSTANCE_DTO_LIST);
    List<AdFunctionErrorMessagesDto> adFunctionErrorMessagesDtoList = (List<AdFunctionErrorMessagesDto>) dtos.get(AD_FUNCTION_ERROR_MESSAGES_DTO_LIST);
    
    NonPointNodeDto portfolioNodeDto = nonPointNodeDtoList.get(0);

    Integer portfolioId = portfolioNodeDto.getNodeId();
    String portfolioName = portfolioNodeDto.getNodeName();
    String portfolioDisplayName = portfolioNodeDto.getNodeDisplayName();
    String uuid = portfolioNodeDto.getUuid();
    String portfolioCreatedAt = portfolioNodeDto.getNodeCreatedAt();
    if (portfolioCreatedAt == null || portfolioCreatedAt.isEmpty()) {
      portfolioCreatedAt = AbstractEntity.formatTimestamp(AbstractEntity.getTimeKeeper().getCurrentTimestamp());  
    }
    String portfolioUpdatedAt = portfolioNodeDto.getNodeUpdatedAt();
    
    int nodeHierarchySize = nonPointNodeDtoList.size()
        + mappablePointNodeDtoList.size()
        + customAsyncComputedPointNodeDtoList.size()
        + scheduledAsyncComputedPointNodeDtoList.size();

    PortfolioEntity portfolio = new PortfolioEntity(
        portfolioId,
        parentCustomer,
        portfolioName,
        portfolioDisplayName,
        uuid,
        portfolioCreatedAt,
        portfolioUpdatedAt,
        nodeHierarchySize);
    
    // This will disable a lot of modification tracking that is just reset when the portfolio is done being mapped.
    portfolio.isBeingMapped = true;

    // First, instantiate hash maps that are sized according to the number of value objects to be
    // processed.
    Map<Integer, NodeTagDto> nodeTagsMap = Maps.newHashMapWithExpectedSize(nodeTagDtoList.size());
    Iterator<NodeTagDto> pointTagIterator = nodeTagDtoList.iterator();
    while (pointTagIterator.hasNext()) {

      NodeTagDto nodeTagDto = pointTagIterator.next();
      nodeTagsMap.put(nodeTagDto.getId(), nodeTagDto);
    }
    
    // Custom async computed points can have many child temporal configs.  The DTO list has them in a linear list,
    // so we use a map to collapse them down so that each point has its collection of temporal configs.
    Map<Integer, CustomAsyncComputedPointEntity> customPointsMap = Maps.newHashMapWithExpectedSize(customAsyncComputedPointNodeDtoList.size());    

    // Buildings can have many child temporal configs.  The DTO list has them in a linear list,
    // so we use a map to collapse them down so that each building has its collection of temporal configs.
    Map<Integer, BuildingEntity> buildingsMap = Maps.newHashMap();
    
    if (!rawPointDtoList.isEmpty()) {
      rawPointMap = Maps.newHashMapWithExpectedSize(rawPointDtoList.size());
      for (RawPointDto rawPointDto: rawPointDtoList) {
        
        RawPointEntity rawPoint = new RawPointEntity(
            rawPointDto.getId(),
            parentCustomer.getPersistentIdentity(),
            rawPointDto.getComponentId(),
            rawPointDto.getMetricId(),
            rawPointDto.getPointType(),
            rawPointDto.getRange(),
            rawPointDto.getUnitType(),
            rawPointDto.getIgnore(),
            rawPointDto.getDeleted());
        
        rawPointMap.put(rawPointDto.getId(), rawPoint);
      }
      parentCustomer.addRawPoints(rawPointMap.values());
    }
    
    PaymentPlansContainer paymentPlansContainer = DictionaryContext.getPaymentPlansContainer();
    WeatherStationsContainer weatherStationsContainer = DictionaryContext.getWeatherStationsContainer();

    for (NonPointNodeDto nonPointNodeDto: nonPointNodeDtoList) {
      mapNonPointNode(portfolio, nonPointNodeDto, nodeTagsMap, nonPointNodeDtoList, paymentPlansContainer, weatherStationsContainer, buildingsMap);
    }

    for (MappablePointNodeDto mappablePointNodeDto: mappablePointNodeDtoList) {
      mapMappablePointNode(portfolio, mappablePointNodeDto, nodeTagsMap);
    }

    for (AsyncComputedPointNodeDto asyncComputedPointNodeDto: asyncComputedPointNodeDtoList) {
      mapAsyncComputedPointNode(portfolio, asyncComputedPointNodeDto, nodeTagsMap);
    }
    
    for (CustomAsyncComputedPointNodeDto customAsyncComputedPointNodeDto: customAsyncComputedPointNodeDtoList) {
      mapCustomAsyncComputedPointNode(portfolio, customAsyncComputedPointNodeDto, nodeTagsMap, customPointsMap);
    }
    
    for (ScheduledAsyncComputedPointNodeDto scheduledAsyncComputedPointNodeDto: scheduledAsyncComputedPointNodeDtoList) {
      mapScheduledAsyncComputedPointNode(portfolio, scheduledAsyncComputedPointNodeDto, nodeTagsMap);
    }
    
    for (EnergyExchangeSystemEdgeDto energyExchangeSystemEdgeDto: energyExchangeSystemEdgeDtoList) {
      mapEnergyExchangeSystemEdgeDtoToEntity(portfolio, energyExchangeSystemEdgeDto);
    }

    
    // The trick here is to ensure that there exists one report instance for each combination of
    // building/report template. This is because the database persistence only has "enabled"
    // report instances occupying the report instance tables. If a combination is "disabled", we
    // return a report instance that is both "disabled" and has an empty list of "bound"
    // equipment/points (i.e. GREEN equipment status). We need this entity for every
    // building/report template combination for *evaluation* purposes, as well as to hold the map
    // of qualifying equipment/points so that when "enabled", the report instance tables can easily
    // be created. In addition, this base report instance will hold the *results* of periodic
    // evaluation, which means we have a list of equipment that didn't match, along with a list of
    // error messages explaining why an equipment is RED.
    if (!portfolio.getChildBuildings().isEmpty()) {

      Map<String, ReportInstanceDto> reportInstanceDtoMap = new HashMap<>();
      for (ReportInstanceDto dto: reportInstanceDtoList) {

        reportInstanceDtoMap.put(dto.getBuildingId() + "_" + dto.getReportTemplateId(), dto);
      }
      
      Map<String, ReportInstanceStatusDto> reportInstanceStatusDtoMap = new HashMap<>();
      for (ReportInstanceStatusDto dto: reportInstanceStatusDtoList) {

        reportInstanceStatusDtoMap.put(dto.getBuildingId() + "_" + dto.getReportTemplateId(), dto);
      }

      for (BuildingEntity building: portfolio.getAllBuildings()) {
        
        // We call this method in order to initialize/instantiate the report instances, in which there
        // will be one report instance per report template.  We then combine information from two different
        // table hierarchies into one entity ("instance" DTOs for enabled reports and their green equipment
        // and "status" DTOs, which holds everything else: red equipment error messages and candidate JSON,
        // which is the green equipment stuffed into a JSON string.
        Set<ReportInstanceEntity> reportInstances = building.getReportInstances();
        int size = reportInstances.size();
        for (int i=0; i < size; i++) {
          portfolio.incrementNumReportInstancesProcessed(); 
        }
      }
      
      for (AdFunctionInstanceDto adFunctionInstanceCandidateDto: adFunctionInstanceCandidateDtoList) {
        mapAdFunctionInstanceCandidate(portfolio, adFunctionInstanceCandidateDto);
      }

      for (AdFunctionInstanceDto adFunctionInstanceDto: adFunctionInstanceDtoList) {
        mapAdFunctionInstance(portfolio, adFunctionInstanceDto);
      }
      
      for (AdFunctionErrorMessagesDto adFunctionErrorMessagesDto: adFunctionErrorMessagesDtoList) {
        mapAdFunctionErrorMessages(portfolio, adFunctionErrorMessagesDto);
      }
      
      // Infer what node type depth the portfolio is being loaded at.  If building level, then we have
      // non-empty lists of report/report status DTO objects, but because there aren't any equipment/points,
      // we can instantiate the report instance equipment/error message entities (i.e. green/red equipment),
      // so, we just serialize to JSON for these two hierarchies.
      NodeType depthNodeType = parentCustomer.depthNodeType;
      if (depthNodeType != null && depthNodeType.equals(NodeType.BUILDING)) {

        try {
          for (ReportInstanceDto dto: reportInstanceDtoList) {
            
            Integer buildingId = dto.getBuildingId();
            Integer reportTemplateId = dto.getReportTemplateId();
            int numGreenEquipment = dto.getReportInstanceEquipment().size();
            
            BuildingEntity building = portfolio.getChildBuilding(buildingId);
            ReportInstanceEntity reportInstance = building.getReportInstanceByReportTemplateId(reportTemplateId);
            reportInstance.setNumGreenEquipment(numGreenEquipment);
          }

          for (ReportInstanceStatusDto dto: reportInstanceStatusDtoList) {
            
            Integer buildingId = dto.getBuildingId();
            Integer reportTemplateId = dto.getReportTemplateId();
            
            Set<Integer> redEquipmentIds = new HashSet<>();
            List<ReportInstanceStatusErrorMessageDto> errorMessageDtos = dto.getErrorMessages();
            if (errorMessageDtos != null && !errorMessageDtos.isEmpty()) {
              for (ReportInstanceStatusErrorMessageDto errorMessageDto: dto.getErrorMessages()) {
                
                redEquipmentIds.add(errorMessageDto.getEquipmentId());
              }
            }
            BuildingEntity building = portfolio.getChildBuilding(buildingId);
            ReportInstanceEntity reportInstance = building.getReportInstanceByReportTemplateId(reportTemplateId);
            reportInstance.setNumRedEquipment(redEquipmentIds.size());
          }
        } catch (Exception e) {
          String errorMessage = "Unable to parse report instance dto list and report instance status dto list for green/red equipment counts";
          throw new IllegalStateException(errorMessage, e);
        }
        
      } else {
        
        // Instance DTOs will exist only if the user "enabled" the report
        // We map these first, as they contain the report instance id.
        // If report instance is non-null, the report is considered to be "enabled",
        // so any candidate_json is NOT mapped to green report instance equipment,
        // as that is done with these "instance" DTOs.
        for (ReportInstanceDto reportInstanceDto: reportInstanceDtoList) {
          mapReportInstance(portfolio, reportInstanceDto);
        }
        
        // Status DTOs will exist no matter what
        for (ReportInstanceStatusDto reportInstanceStatusDto: reportInstanceStatusDtoList) {
          mapReportInstanceStatus(portfolio, reportInstanceStatusDto);
        }
      }
    }
    
    parentCustomer.setChildPortfolio(portfolio);
    
    // This will enable modification tracking.
    portfolio.isBeingMapped = false;
    
    return portfolio;
  }

  private void mapNonPointNode(
      PortfolioEntity portfolio,
      NonPointNodeDto nonPointNodeDto,
      Map<Integer, NodeTagDto> nodeTagsMap,
      List<NonPointNodeDto> nodeDtoList,
      PaymentPlansContainer paymentPlansContainer,
      WeatherStationsContainer weatherStationsContainer,
      Map<Integer, BuildingEntity> buildingsMap) {

    Integer nodeId = nonPointNodeDto.getNodeId();
    Integer nodeTypeId = nonPointNodeDto.getNodeTypeId();
    if (nodeTypeId > 1) {

      NodeType nodeType = NodeType.get(nodeTypeId);
      Integer nodeParentId = nonPointNodeDto.getNodeParentId();
      
      AbstractNodeEntity parentNode = null;

      String nodeName = nonPointNodeDto.getNodeName();
      String nodeDisplayName = nonPointNodeDto.getNodeDisplayName();
      String uuid = nonPointNodeDto.getUuid();
      String nodeCreatedAt = nonPointNodeDto.getNodeCreatedAt();
      String nodeUpdatedAt = nonPointNodeDto.getNodeUpdatedAt();

      try {
        
        parentNode = portfolio.getChildNodeNullIfNotExists(nodeParentId);
        if (parentNode == null) {
          if (nodeType.equals(NodeType.BUILDING)) {
            parentNode = portfolio;
          } else {
            mapNonPointNode(portfolio, getNonPointNodeDto(nodeDtoList, nodeParentId), nodeTagsMap, nodeDtoList, paymentPlansContainer, weatherStationsContainer, buildingsMap);
            parentNode = portfolio.getChildNodeNullIfNotExists(nodeParentId);
          }
        }

        Set<TagEntity> nodeTags = null;
        NodeTagDto nodeTagDto = nodeTagsMap.get(nodeId);
        if (nodeTagDto != null) {
          if (nodeTagDto.getTagIds() != null) {
            nodeTags = DictionaryContext.getTagsContainer().getTags(nodeTagDto.getTagIds());
          } else {
            nodeTags = DictionaryContext.getTagsContainer().getTags(nodeTagDto.getNodeTags());  
          }
        }

        if (nodeType.equals(NodeType.BUILDING)) {
          
          BuildingEntity building = buildingsMap.get(nodeId);
          if (building == null) {

            BuildingStatus buildingStatus = null;
            if (nonPointNodeDto.getBuildingStatus() != null) {
              buildingStatus =  BuildingStatus.valueOf(nonPointNodeDto.getBuildingStatus());
            } else {
              buildingStatus = BuildingStatus.CREATED;
            }

            BuildingPaymentStatus buildingPaymentStatus = null;
            if (nonPointNodeDto.getBuildingStatus() != null) {
              buildingPaymentStatus =  BuildingPaymentStatus.valueOf(nonPointNodeDto.getBuildingPaymentStatus());
            } else {
              buildingPaymentStatus = BuildingPaymentStatus.UP_TO_DATE;
            }
            
            WeatherStationEntity weatherStation = null;
            Integer weatherStationId = nonPointNodeDto.getBuildingWeatherStationId();
            if (weatherStationId != null) {
              weatherStation = weatherStationsContainer.getWeatherStationById(weatherStationId);
            }
            
            boolean isOnlineBuilding = false;
            if (portfolio.getParentCustomer() instanceof OnlineCustomerEntity) {
              
              isOnlineBuilding = true;
              
              AbstractDistributorEntity distributor = portfolio.getParentCustomer().getParentDistributor();
              if (distributor instanceof OnlineDistributorEntity
                  && ((OnlineDistributorEntity)distributor).getAllowOutOfBandBuildings()
                  && BuildingPaymentType.get(nonPointNodeDto.getBuildingPaymentType()).equals(BuildingPaymentType.OUT_OF_BAND)) {
                
                isOnlineBuilding = false;
              }
            }
            
            if (isOnlineBuilding) {

              building = new BillableBuildingEntity(
                  nodeId,
                  portfolio,
                  nodeName,
                  nodeDisplayName,
                  uuid,
                  nodeCreatedAt,
                  nodeUpdatedAt,
                  nodeTags,
                  nonPointNodeDto.getBuildingTimezone(),
                  nonPointNodeDto.getBuildingAddress(),
                  nonPointNodeDto.getBuildingCity(),
                  nonPointNodeDto.getBuildingStateOrProvince(),
                  nonPointNodeDto.getBuildingPostalCode(),
                  nonPointNodeDto.getBuildingCountryCode(),
                  UnitSystem.get(nonPointNodeDto.getBuildingUnitSystem()),
                  nonPointNodeDto.getBuildingLatitude(),
                  nonPointNodeDto.getBuildingLongitude(),
                  weatherStation,
                  buildingStatus,
                  AbstractEntity.parseTimestamp(nonPointNodeDto.getBuildingStatusUpdatedAt()),
                  buildingPaymentStatus,
                  AbstractEntity.parseTimestamp(nonPointNodeDto.getBuildingPaymentStatusUpdatedAt()),
                  nonPointNodeDto.getBuildingBillingGracePeriod(),
                  nonPointNodeDto.getBuildingGracePeriodWarningNotificationId(),
                  nonPointNodeDto.getBuildingPendingDeletion(),
                  AbstractEntity.parseTimestamp(nonPointNodeDto.getBuildingPendingDeletionUpdatedAt()));
              
              if (nonPointNodeDto.getBuildingPaymentPlanId() != null) {
                
                OnlineDistributorEntity distributor = (OnlineDistributorEntity)portfolio.getParentCustomer().getParentDistributor();
                
                // Only instantiate the building subscription if we have loaded the distributor's payment methods.
                // Need to have all payment processing related service methods throw an exception if they have not been loaded.
                if (distributor.loadDistributorPaymentMethods) {

                  Timestamp startedAt = null;
                  if (nonPointNodeDto.getBuildingSubscriptionStartedAt() != null) {
                    startedAt = AbstractEntity.parseTimestamp(nonPointNodeDto.getBuildingSubscriptionStartedAt());
                  }

                  Timestamp currentIntervalStartedAt = null;
                  if (nonPointNodeDto.getBuildingSubscriptionStartedAt() != null) {
                    currentIntervalStartedAt = AbstractEntity.parseTimestamp(nonPointNodeDto.getBuildingSubscriptionCurrentIntervalStartedAt());
                  }
                  
                  PaymentPlanEntity pendingPaymentPlan = null;
                  if (nonPointNodeDto.getBuildingPendingPaymentPlanId() != null) {
                    pendingPaymentPlan = paymentPlansContainer.getPaymentPlan(nonPointNodeDto.getBuildingPendingPaymentPlanId());
                  }
                  
                  Timestamp pendingPaymentPlanUpdatedAt = null;
                  if (nonPointNodeDto.getBuildingPendingPaymentPlanUpdatedAt() != null) {
                    pendingPaymentPlanUpdatedAt = AbstractEntity.parseTimestamp(nonPointNodeDto.getBuildingPendingPaymentPlanUpdatedAt());
                  }

                  BuildingSubscriptionEntity buildingSubscription = new BuildingSubscriptionEntity(
                      nodeId,
                      (BillableBuildingEntity)building,
                      paymentPlansContainer.getPaymentPlan(nonPointNodeDto.getBuildingPaymentPlanId()),
                      distributor.getChildPaymentMethod(nonPointNodeDto.getBuildingPaymentMethodId()),
                      nonPointNodeDto.getBuildingStripeSubscriptionId(),
                      startedAt,
                      currentIntervalStartedAt,
                      pendingPaymentPlan,
                      pendingPaymentPlanUpdatedAt);
                  
                  ((BillableBuildingEntity)building).setChildBuildingSubscription(buildingSubscription);
                }
              }
              
            } else {

              building = new BuildingEntity(
                  nodeId,
                  portfolio,
                  nodeName,
                  nodeDisplayName,
                  uuid,
                  nodeCreatedAt,
                  nodeUpdatedAt,
                  nodeTags,
                  nonPointNodeDto.getBuildingTimezone(),
                  nonPointNodeDto.getBuildingAddress(),
                  nonPointNodeDto.getBuildingCity(),
                  nonPointNodeDto.getBuildingStateOrProvince(),
                  nonPointNodeDto.getBuildingPostalCode(),
                  nonPointNodeDto.getBuildingCountryCode(),
                  UnitSystem.get(nonPointNodeDto.getBuildingUnitSystem()),
                  nonPointNodeDto.getBuildingLatitude(),
                  nonPointNodeDto.getBuildingLongitude(),
                  weatherStation,
                  buildingStatus,
                  AbstractEntity.parseTimestamp(nonPointNodeDto.getBuildingStatusUpdatedAt()),
                  buildingPaymentStatus,
                  AbstractEntity.parseTimestamp(nonPointNodeDto.getBuildingPaymentStatusUpdatedAt()),
                  nonPointNodeDto.getBuildingBillingGracePeriod(),
                  nonPointNodeDto.getBuildingGracePeriodWarningNotificationId(),
                  BuildingPaymentType.OUT_OF_BAND);
              
            }
            
            portfolio.addNodeToParentAndIndex(building);
            buildingsMap.put(building.getPersistentIdentity(), building);
          }
          
          if (parentCustomer.loadBuildingTemporalData && nonPointNodeDto.getBuildingTemporalId() != null) {
            
            BuildingTemporalConfigEntity childTemporalConfig = new BuildingTemporalConfigEntity(
                Integer.parseInt(nonPointNodeDto.getBuildingTemporalId()),
                building,
                LocalDate.parse(nonPointNodeDto.getBuildingTemporalEffectiveDate()),
                Integer.parseInt(nonPointNodeDto.getBuildingTemporalSqft()));
            
            building.addChildTemporalConfig(childTemporalConfig);
            
            String[] utilityIdArr = nonPointNodeDto.getBuildingUtilityId()
                .replace("{", "")
                .replace("}", "")
                .replace("\"", "")
                .split(",");
            
            String[] utilityComputationIntervalIdArr = nonPointNodeDto.getBuildingUtilityComputationIntervalId()
                .replace("{", "")
                .replace("}", "")
                .replace("\"", "")
                .split(",");
                
            String[] utilityFormulaArr = nonPointNodeDto.getBuildingUtilityFormula()
                .replace("{", "")
                .replace("}", "")
                .replace("\"", "")
                .split(",");
            
            String[] utilityRateArr = nonPointNodeDto.getBuildingUtilityRate()
                .replace("{", "")
                .replace("}", "")
                .replace("\"", "")
                .split(",");
                
            String[] utilityBaselineDescriptionArr = nonPointNodeDto.getBuildingUtilityBaselineDescription()
                .replace("{", "")
                .replace("}", "")
                .replace("\"", "")
                .split(",");
            
            String[] utilityUserNotesArr = nonPointNodeDto.getBuildingUtilityUserNotes()
                .replace("{", "")
                .replace("}", "")
                .replace("\"", "")
                .split(",");
            
            // TODO: TDM: Need to encode actual commas as URL safe as we use commas to split here.
            for (int i=0; i < utilityIdArr.length; i++) {
              
              if (!utilityIdArr[i].trim().isEmpty()) {
                
                String strUtilityId = utilityIdArr[i];
                if (!strUtilityId.trim().equalsIgnoreCase("null")) {
                  
                  Integer utilityId = Integer.parseInt(strUtilityId);
                  Integer computationIntervalId = Integer.parseInt(utilityComputationIntervalIdArr[i]);
                  String formula = utilityFormulaArr[i];
                  
                  Double rate = null;
                  if (i < utilityRateArr.length) {
                    String strRate = utilityRateArr[i];
                    if (!strRate.trim().equalsIgnoreCase("null")) {
                      rate = Double.valueOf(utilityRateArr[i]);  
                    }
                  }
                  
                  String baselineDescription = null;
                  if (i < utilityBaselineDescriptionArr.length) {
                    baselineDescription = utilityBaselineDescriptionArr[i];
                  }

                  String userNotes = null;
                  if (i < utilityUserNotesArr.length) {
                    userNotes = utilityUserNotesArr[i];
                  }
                  
                  childTemporalConfig.addChildUtility(new BuildingTemporalUtilityEntity(
                      childTemporalConfig,
                      BuildingUtilityType.get(utilityId),
                      UtilityComputationInterval.get(computationIntervalId),
                      formula,
                      rate,
                      baselineDescription,
                      userNotes));
                }                
              }
            }
          }

        } else if (nodeType.equals(NodeType.SUB_BUILDING)) {

          SubBuildingEntity subBuilding = new SubBuildingEntity(
              nodeId,
              (BuildingEntity)parentNode,
              nodeName,
              nodeDisplayName,
              uuid,
              nodeCreatedAt,
              nodeUpdatedAt,
              nodeTags);
          
          portfolio.addNodeToParentAndIndex(parentNode, subBuilding);

        } else if (nodeType.equals(NodeType.FLOOR)) {

          FloorEntity floor = new FloorEntity(
              nodeId,
              parentNode,
              nodeName,
              nodeDisplayName,
              uuid,
              nodeCreatedAt,
              nodeUpdatedAt,
              nodeTags,
              nonPointNodeDto.getFloorOrdinal());
          
          portfolio.addNodeToParentAndIndex(parentNode, floor);
          
        } else if (nodeType.equals(NodeType.PLANT)) {

          PlantEntity plant = new PlantEntity(
              nodeId,
              parentNode,
              nodeName,
              nodeDisplayName,
              uuid,
              nodeCreatedAt,
              nodeUpdatedAt,
              nodeTags);

          portfolio.addNodeToParentAndIndex(parentNode, plant);

        } else if (nodeType.equals(NodeType.LOOP)) {

          LoopEntity loop = new LoopEntity(
              nodeId,
              parentNode,
              nodeName,
              nodeDisplayName,
              uuid,
              nodeCreatedAt,
              nodeUpdatedAt,
              nodeTags);

          portfolio.addNodeToParentAndIndex(parentNode, loop);
          
        } else if (nodeType.equals(NodeType.EQUIPMENT)) {

          EquipmentEntity childEquipment = (EquipmentEntity) portfolio.getChildNodeNullIfNotExists(nodeId);
          if (childEquipment == null) {

            childEquipment = new EquipmentEntity(
                nodeId,
                parentNode,
                nodeName,
                nodeDisplayName,
                uuid,
                nodeCreatedAt,
                nodeUpdatedAt,
                nodeTags);

            portfolio.addNodeToParentAndIndex(parentNode, childEquipment);
          }
        }
      } catch (Exception e) {
        throw new IllegalStateException("Unable to map node with nodeId: ["
            + nodeId
            + "] nodeType: ["
            + nodeType
            + "] nodeParentId: ["
            + nodeParentId
            + "] parentNode: ["
            + parentNode
            + "] \n"
            + nonPointNodeDto
            + "\n error: "
            + e.getMessage(), e);
      }
    }
  }

  private NonPointNodeDto getNonPointNodeDto(List<NonPointNodeDto> nodeDtoList, Integer id) {

    Optional<NonPointNodeDto> nodeDto = nodeDtoList
        .stream()
        .filter(n -> n.getNodeId().equals(id))
        .findFirst();

    if (nodeDto.isPresent()) {
      return nodeDto.get();
    }

    throw new IllegalStateException(
        "Could not find node with id: ["
            + id
            + "]");

  }

  private void mapMappablePointNode(
      PortfolioEntity portfolio,
      MappablePointNodeDto mappablePointNodeDto,
      Map<Integer, NodeTagDto> nodeTagsMap) {

    Integer nodeId = mappablePointNodeDto.getNodeId();
    Integer nodeParentId = mappablePointNodeDto.getNodeParentId();
    
    AbstractNodeEntity parentNode = null;

    String nodeName = mappablePointNodeDto.getNodeName();
    String nodeDisplayName = mappablePointNodeDto.getNodeDisplayName();
    String nodeCreatedAt = "";
    String nodeUpdatedAt = "";

    try {

      MappablePointEntity mappablePoint = null;

      parentNode = portfolio.getChildNode(nodeParentId);

      PointTemplateEntity pointTemplate = null;
      Integer pointTemplateId = mappablePointNodeDto.getPointPointTemplateId();
      if (pointTemplateId != null && pointTemplateId.intValue() > 0) {
        pointTemplate = DictionaryContext.getNodeTagTemplatesContainer().getPointTemplate(pointTemplateId);
      }

      Set<TagEntity> nodeTags = null;
      NodeTagDto nodeTagDto = nodeTagsMap.get(nodeId);
      if (nodeTagDto != null) {
        if (nodeTagDto.getTagIds() != null) {
          nodeTags = DictionaryContext.getTagsContainer().getTags(nodeTagDto.getTagIds());
        } else {
          nodeTags = DictionaryContext.getTagsContainer().getTags(nodeTagDto.getNodeTags());  
        }
      }
      
      UnitEntity unit = null;
      Integer unitId = mappablePointNodeDto.getPointUnitId();
      if (unitId != null && unitId > 0) {
        
        unit = DictionaryContext.getUnitsContainer().getUnit(unitId);
      }

      RawPointEntity rawPoint = null;
      RawPointDto rawPointDto = mappablePointNodeDto.getRawPointDto();
      if (rawPointDto != null) {
        
        rawPoint = new RawPointEntity(
            rawPointDto.getId(),
            parentCustomer.getPersistentIdentity(),
            rawPointDto.getComponentId(),
            rawPointDto.getMetricId(),
            rawPointDto.getPointType(),
            rawPointDto.getRange(),
            rawPointDto.getUnitType(),
            rawPointDto.getIgnore(),
            rawPointDto.getDeleted());
        
        parentCustomer.addRawPoint(rawPoint);
        
      } else {
      
        Integer rawPointId = mappablePointNodeDto.getPointRawPointId();
        rawPoint = rawPointMap.get(rawPointId);
        
      }
            
      mappablePoint = new MappablePointEntity(
          nodeId,
          parentNode,
          nodeName,
          nodeDisplayName,
          nodeCreatedAt,
          nodeUpdatedAt,
          nodeTags,
          DataType.get(mappablePointNodeDto.getPointDataTypeId()),
          unit,
          mappablePointNodeDto.getPointRange(),
          pointTemplate,
          mappablePointNodeDto.getValue(),
          mappablePointNodeDto.getValueTimestamp(),
          rawPoint,
          mappablePointNodeDto.getCov());
      
      portfolio.addNodeToParentAndIndex(parentNode, mappablePoint);

    } catch (Exception e) {
      throw new IllegalStateException("Unable to map mappable point with nodeId: ["
          + nodeId
          + "] nodeParentId: ["
          + nodeParentId
          + "] parentNode: ["
          + parentNode
          + "] \n"
          + mappablePointNodeDto
          + "\n error: "
          + e.getMessage(), e);
    }
  }

  private void mapCustomAsyncComputedPointNode(
      PortfolioEntity portfolio,
      CustomAsyncComputedPointNodeDto dto,
      Map<Integer, NodeTagDto> nodeTagsMap,
      Map<Integer, CustomAsyncComputedPointEntity> customPointsMap) {

    Integer id = dto.getId();
    Integer parentId = dto.getParentId();
    
    AbstractNodeEntity parentNode = portfolio.getChildNodeNullIfNotExists(parentId);

    String name = dto.getName();
    String displayName = dto.getDisplayName();
    String createdAt = dto.getCreatedAt();
    String updatedAt = dto.getUpdatedAt();

    try {
      
      CustomAsyncComputedPointEntity entity = customPointsMap.get(id);
      if (entity == null) {

        Set<TagEntity> nodeTags = null;
        NodeTagDto nodeTagDto = nodeTagsMap.get(id);
        if (nodeTagDto != null) {
          if (nodeTagDto.getTagIds() != null) {
            nodeTags = DictionaryContext.getTagsContainer().getTags(nodeTagDto.getTagIds());
          } else {
            nodeTags = DictionaryContext.getTagsContainer().getTags(nodeTagDto.getNodeTags());  
          }
        }

        UnitEntity unit = null;
        Integer unitId = dto.getUnitId();
        if (unitId != null) {
          unit = DictionaryContext.getUnitsContainer()
              .getUnit(unitId);
        }

        PointTemplateEntity pointTemplate = null;
        Integer pointTemplateId = dto.getPointTemplateId();
        if (pointTemplateId != null && pointTemplateId.intValue() > 0) {
          pointTemplate = DictionaryContext.getNodeTagTemplatesContainer().getPointTemplate(pointTemplateId);
        }
        
        ComputationInterval computationInterval = null;
        if (portfolio.getParentCustomer().loadCustomPointTemporalData) {
        
          String ci = dto.getComputationInterval();
          computationInterval = ComputationInterval.fromAggregator(ci);
          if (computationInterval == null) {
            computationInterval = ComputationInterval.valueOf(ci);
            if (computationInterval == null) {
              computationInterval = ComputationInterval.fromName(ci);
            }
          }
        }
                
        entity = new CustomAsyncComputedPointEntity(
            id,
            parentNode,
            name,
            displayName,
            createdAt,
            updatedAt,
            nodeTags,
            DataType.NUMERIC,
            unit,
            pointTemplate,
            dto.getValue(),
            dto.getValueTimestamp(),      
            dto.getMetricId(),
            dto.getConfigurable(),
            dto.getTimezoneBasedRollups(),
            computationInterval);
        
        portfolio.addNodeToParentAndIndex(parentNode, entity);
        
        customPointsMap.put(entity.getPersistentIdentity(), entity);
      }
      
      if (portfolio.getParentCustomer().loadCustomPointTemporalData) {
        
        LocalDate effectiveDate = null;
        if (dto.getEffectiveDate() != null) {
          effectiveDate = LocalDate.parse(dto.getEffectiveDate());
        }
        
        TemporalAsyncComputedPointConfigEntity childTemporalConfig = new TemporalAsyncComputedPointConfigEntity(
            dto.getTemporalConfigId(),
            entity,
            effectiveDate,
            dto.getFormula(),
            dto.getDescription());
        
        entity.addChildTemporalConfig(childTemporalConfig);
        
        String[] variablePointIdArr = dto.getVariablePointId()
            .replace("{", "")
            .replace("}", "")
            .replace("\"", "")
            .split(",");
        
        String[] variableFillPolicyIdArr = dto.getVariableFillPolicyId()
            .replace("{", "")
            .replace("}", "")
            .replace("\"", "")
            .split(",");      

        String[] variableNameArr = dto.getVariableName()
            .replace("{", "")
            .replace("}", "")
            .replace("\"", "")
            .split(",");
        
        for (int i=0; i < variablePointIdArr.length; i++) {
          
          if (!variablePointIdArr[i].trim().isEmpty()) {
          
            Integer variablePointId = Integer.parseInt(variablePointIdArr[i]);
            Integer variableFillPolicyId = Integer.parseInt(variableFillPolicyIdArr[i]);
            String variableName = variableNameArr[i];
            
            childTemporalConfig.addChildVariable(new FormulaVariableEntity(
                childTemporalConfig,
                portfolio.getCustomPointFormulaVariableEligiblePointNullIfNotExists(variablePointId),
                variableName,
                FillPolicy.fromId(variableFillPolicyId)));
          }
        }        
      }
    } catch (Exception e) {
      throw new IllegalStateException("Unable to map custom async computed point with id: ["
          + id
          + "] parentId: ["
          + parentId
          + "] parentNode: ["
          + parentNode
          + "] \n"
          + dto
          + "\n error: "
          + e.getMessage(), e);
    }
  }
  
  private void mapScheduledAsyncComputedPointNode(
      PortfolioEntity portfolio,
      ScheduledAsyncComputedPointNodeDto scheduledAsyncComputedPointNodeDto,
      Map<Integer, NodeTagDto> nodeTagsMap) {

    Integer nodeId = scheduledAsyncComputedPointNodeDto.getNodeId();
    Integer nodeParentId = scheduledAsyncComputedPointNodeDto.getNodeParentId();

    String nodeName = scheduledAsyncComputedPointNodeDto.getNodeName();
    String nodeDisplayName = scheduledAsyncComputedPointNodeDto.getNodeDisplayName();
    String nodeCreatedAt = "";
    String nodeUpdatedAt = "";
    
    BuildingEntity parentBuilding = null;

    try {

      parentBuilding = (BuildingEntity) portfolio.getChildNode(nodeParentId);

      ScheduledEventTypeEntity scheduledEventType =
          DictionaryContext.getScheduledEventTypesContainer()
              .getScheduledEventType(scheduledAsyncComputedPointNodeDto.getScheduledEventTypeId());

      Set<TagEntity> nodeTags = null;
      NodeTagDto nodeTagDto = nodeTagsMap.get(nodeId);
      if (nodeTagDto != null) {
        if (nodeTagDto.getTagIds() != null) {
          nodeTags = DictionaryContext.getTagsContainer().getTags(nodeTagDto.getTagIds());
        } else {
          nodeTags = DictionaryContext.getTagsContainer().getTags(nodeTagDto.getNodeTags());  
        }
      }

      UnitEntity unit = null;
      if (scheduledAsyncComputedPointNodeDto.getPointUnitId() != null) {
        unit = DictionaryContext.getUnitsContainer()
            .getUnit(scheduledAsyncComputedPointNodeDto.getPointUnitId());
      }

      PointTemplateEntity pointTemplate = null;
      Integer pointTemplateId = scheduledAsyncComputedPointNodeDto.getPointPointTemplateId();
      if (pointTemplateId != null && pointTemplateId.intValue() > 0) {
        pointTemplate = DictionaryContext.getNodeTagTemplatesContainer().getPointTemplate(pointTemplateId);
      }

      ScheduledAsyncComputedPointEntity scheduledAsyncComputedPoint =
          new ScheduledAsyncComputedPointEntity(
              nodeId,
              parentBuilding,
              nodeName,
              nodeDisplayName,
              nodeCreatedAt,
              nodeUpdatedAt,
              nodeTags,
              DataType.get(scheduledAsyncComputedPointNodeDto.getPointDataTypeId()),
              unit,
              scheduledAsyncComputedPointNodeDto.getPointRange(),
              pointTemplate,
              scheduledAsyncComputedPointNodeDto.getValue(),
              scheduledAsyncComputedPointNodeDto.getValueTimestamp(),
              scheduledAsyncComputedPointNodeDto.getPointMetricId(),
              Boolean.FALSE,
              Boolean.FALSE,
              null,
              scheduledEventType);
      
      portfolio.addNodeToParentAndIndex(parentBuilding, scheduledAsyncComputedPoint);

    } catch (Exception e) {
      throw new IllegalStateException("Unable to map scheduled async computed point with nodeId: ["
          + nodeId
          + "] nodeParentId: ["
          + nodeParentId
          + "] parentBuilding: ["
          + parentBuilding
          + "] \n"
          + scheduledAsyncComputedPointNodeDto
          + "\n error: "
          + e.getMessage(), e);
    }
  }

  private void mapAsyncComputedPointNode(
      PortfolioEntity portfolio,
      AsyncComputedPointNodeDto asyncComputedPointNodeDto,
      Map<Integer, NodeTagDto> nodeTagsMap) {

    Integer nodeId = asyncComputedPointNodeDto.getNodeId();
    String nodeName = asyncComputedPointNodeDto.getNodeName();
    String nodeDisplayName = asyncComputedPointNodeDto.getNodeDisplayName();
    Integer nodeParentId = asyncComputedPointNodeDto.getNodeParentId();
    String nodeMetricId = asyncComputedPointNodeDto.getMetricId();
    Integer pointTemplateId = asyncComputedPointNodeDto.getPointPointTemplateId();
    Integer unitId = asyncComputedPointNodeDto.getPointUnitId();
    Integer dataTypeId = asyncComputedPointNodeDto.getPointDataTypeId();
    
    try {

      AbstractNodeEntity parentNode = portfolio.getChildNodeNullIfNotExists(nodeParentId);
      if (parentNode != null) {

        Set<TagEntity> nodeTags = null;
        NodeTagDto nodeTagDto = nodeTagsMap.get(nodeId);
        if (nodeTagDto != null) {
          if (nodeTagDto.getTagIds() != null) {
            nodeTags = DictionaryContext.getTagsContainer().getTags(nodeTagDto.getTagIds());
          } else {
            nodeTags = DictionaryContext.getTagsContainer().getTags(nodeTagDto.getNodeTags());  
          }
        }

        PointTemplateEntity pointTemplate = null;
        if (pointTemplateId != null && pointTemplateId.intValue() > 0) {
          
          pointTemplate = DictionaryContext.getNodeTagTemplatesContainer().getPointTemplate(pointTemplateId);
        }
        
        DataType dataType = null;
        if (dataTypeId != null) {
          dataType = DataType.get(dataTypeId);
        } else {
          dataType = DataType.BOOLEAN;
        }
        
        if (nodeDisplayName == null) {
          nodeDisplayName = nodeName;
        }

        UnitEntity unit = null;
        if (unitId != null && unitId > 0) {
          unit = DictionaryContext.getUnitsContainer().getUnit(unitId);
        }
        
        AsyncComputedPointEntity asyncComputedPoint = null;
        String subtype = asyncComputedPointNodeDto.getSubtype();
        if (subtype == null || subtype.isEmpty()) {

          if (nodeMetricId.contains("Async/Rule/")) {
            
            subtype = AsyncComputedPointNodeDto.RULE;
            
          } else if (nodeMetricId.contains("Async/Computed_Point/")) {
            
            subtype = AsyncComputedPointNodeDto.COMPUTED;
            
          } else {
            
            subtype = AsyncComputedPointNodeDto.CUSTOM;
            
          }
        }
        
        String createdAt = asyncComputedPointNodeDto.getCreatedAt();
        String updatedAt = asyncComputedPointNodeDto.getUpdatedAt();
        String range = asyncComputedPointNodeDto.getPointRange();
        Boolean configurable = asyncComputedPointNodeDto.getConfigurable();
        Boolean timezoneBasedRollups = asyncComputedPointNodeDto.getTimezoneBasedRollups();
        Integer globalComputedPointId = asyncComputedPointNodeDto.getGlobalComputedPointId();
        String lastValue = asyncComputedPointNodeDto.getValue();
        Long lastValueTimestamp = asyncComputedPointNodeDto.getValueTimestamp();
        
        if (subtype.equals("COMPUTED") || subtype.equals("RULE")) {

          asyncComputedPoint = new AdFunctionAsyncComputedPointEntity(
              nodeId,
              parentNode,
              nodeName,
              nodeDisplayName,
              createdAt,
              updatedAt,
              nodeTags,
              dataType,
              unit,
              range,
              pointTemplate,
              lastValue,
              lastValueTimestamp,
              nodeMetricId,
              configurable,
              timezoneBasedRollups,
              globalComputedPointId);
          
        } else if (subtype.equals("WEATHER_STATION")) {

          asyncComputedPoint = new WeatherAsyncComputedPointEntity(
              nodeId,
              parentNode,
              nodeName,
              nodeDisplayName,
              createdAt,
              updatedAt,
              nodeTags,
              dataType,
              unit,
              range,
              pointTemplate,
              lastValue,
              lastValueTimestamp,
              nodeMetricId,
              configurable,
              timezoneBasedRollups,
              globalComputedPointId);
          
        } else if (subtype.equals("SYSTEM")) {

          asyncComputedPoint = new SystemAsyncComputedPointEntity(
              nodeId,
              parentNode,
              nodeName,
              nodeDisplayName,
              createdAt,
              updatedAt,
              nodeTags,
              dataType,
              unit,
              range,
              pointTemplate,
              lastValue,
              lastValueTimestamp,
              nodeMetricId,
              configurable,
              timezoneBasedRollups,
              globalComputedPointId);
          
        } else if (subtype.equals("MANUAL")) {

          asyncComputedPoint = new ManualAsyncComputedPointEntity(
              nodeId,
              parentNode,
              nodeName,
              nodeDisplayName,
              createdAt,
              updatedAt,
              nodeTags,
              dataType,
              unit,
              range,
              pointTemplate,
              lastValue,
              lastValueTimestamp,
              nodeMetricId,
              configurable,
              timezoneBasedRollups,
              globalComputedPointId);
          
        } else {
          
          throw new IllegalStateException("Unsupported subtype occurrence, subtype: [" 
              + subtype
              + "], dto: ["
              + asyncComputedPointNodeDto
              + "]");
        }

        portfolio.addNodeToParentAndIndex(parentNode, asyncComputedPoint);        
      }
    } catch (Exception e) {
      throw new IllegalStateException("Unable to map async computed point with nodeId: ["
          + nodeId
          + "] nodeParentId: ["
          + nodeParentId
          + "] \n"
          + asyncComputedPointNodeDto
          + "\n error: "
          + e.getMessage(), e);
    }
  }
  
  private void mapEnergyExchangeSystemEdgeDtoToEntity(
      PortfolioEntity portfolio, 
      EnergyExchangeSystemEdgeDto dto) {
    
    try {
      
      AbstractEnergyExchangeEntity
          .Mapper
          .getInstance()
          .mapDtoToEntity(portfolio, dto);
      
    } catch (EntityDoesNotExistException | EntityAlreadyExistsException e) {
      
      // RP-12690: There exists equipment set as parent to other equipment that are in different buildings. (node hierarchy relationship)
      // RP-15650: There exists equipment set as parent to other equipment that are in different buildings. (energy exchange relationships, e.g. airside)
      LOGGER.error("Invalid energy exchange system edge, cannot assign parent/child relationships when they exist in different buildings.: [{}]", dto);
    }
  }

  @Override
  public Map<String, Object> mapEntityToDto(PortfolioEntity portfolio) {

    List<RawPointDto> rawPointDtoList = new ArrayList<>();
    List<NonPointNodeDto> nonPointNodeDtoList = new ArrayList<>();
    List<MappablePointNodeDto> mappablePointNodeDtoList = new ArrayList<>();
    List<CustomAsyncComputedPointNodeDto> customAsyncComputedPointNodeDtoList = new ArrayList<>();
    List<ScheduledAsyncComputedPointNodeDto> scheduledAsyncComputedPointNodeDtoList = new ArrayList<>();
    List<AsyncComputedPointNodeDto> asyncComputedPointNodeDtoList = new ArrayList<>();
    List<NodeTagDto> nodeTagDtoList = new ArrayList<>();
    List<EnergyExchangeSystemEdgeDto> energyExchangeSystemEdgeDtoList = new ArrayList<>();
    List<ReportInstanceDto> reportInstanceDtoList = new ArrayList<>();
    List<ReportInstanceStatusDto> reportInstanceStatusDtoList = new ArrayList<>();
    List<AdFunctionInstanceDto> adFunctionInstanceCandidateDtoList = new ArrayList<>();
    List<AdFunctionInstanceDto> adFunctionInstanceDtoList = new ArrayList<>();
    List<AdFunctionErrorMessagesDto> adFunctionErrorMessages = new ArrayList<>();

    Map<String, Object> dtos = buildDtosMap(
        rawPointDtoList,
        nonPointNodeDtoList,
        mappablePointNodeDtoList,
        customAsyncComputedPointNodeDtoList,
        scheduledAsyncComputedPointNodeDtoList,
        asyncComputedPointNodeDtoList,
        nodeTagDtoList,
        energyExchangeSystemEdgeDtoList,
        reportInstanceDtoList,
        reportInstanceStatusDtoList,
        adFunctionInstanceCandidateDtoList,
        adFunctionInstanceDtoList,
        adFunctionErrorMessages);

    portfolio.mapToDtos(dtos);

    return dtos;
  }

  public static Map<String, Object> buildDtosMap(
      List<RawPointDto> rawPointDtoList,
      List<NonPointNodeDto> nonPointNodeDtoList,
      List<MappablePointNodeDto> mappablePointNodeDtoList,
      List<CustomAsyncComputedPointNodeDto> customAsyncComputedPointNodeDtoList,
      List<ScheduledAsyncComputedPointNodeDto> scheduledAsyncComputedPointNodeDtoList,
      List<AsyncComputedPointNodeDto> asyncComputedPointNodeDtoList,
      List<NodeTagDto> nodeTagDtoList,
      List<EnergyExchangeSystemEdgeDto> energyExchangeSystemEdgeDtoList,
      List<ReportInstanceDto> reportInstanceDtoList,
      List<ReportInstanceStatusDto> reportInstanceStatusDtoList,
      List<AdFunctionInstanceDto> adFunctionInstanceCandidateDtoList,
      List<AdFunctionInstanceDto> adFunctionInstanceDtoList,
      List<AdFunctionErrorMessagesDto> adFunctionErrorMessages) {

    Map<String, Object> dtos = new HashMap<>();

    dtos.put(RAW_POINT_DTO_LIST, rawPointDtoList);
    dtos.put(NON_POINT_NODE_DTO_LIST, nonPointNodeDtoList);
    dtos.put(MAPPABLE_POINT_NODE_DTO_LIST, mappablePointNodeDtoList);
    dtos.put(CUSTOM_ASYNC_COMPUTED_POINT_NODE_DTO_LIST, customAsyncComputedPointNodeDtoList);
    dtos.put(SCHEDULED_ASYNC_COMPUTED_POINT_NODE_DTO_LIST, scheduledAsyncComputedPointNodeDtoList);
    dtos.put(ASYNC_COMPUTED_POINT_NODE_DTO_LIST, asyncComputedPointNodeDtoList);
    dtos.put(NODE_TAG_DTO_LIST, nodeTagDtoList);
    dtos.put(ENERGY_EXCHANGE_SYSTEM_EDGE_DTO_LIST, energyExchangeSystemEdgeDtoList);
    dtos.put(REPORT_INSTANCE_DTO_LIST, reportInstanceDtoList);
    dtos.put(REPORT_INSTANCE_STATUS_DTO_LIST, reportInstanceStatusDtoList);
    dtos.put(AD_FUNCTION_INSTANCE_CANDIDATE_DTO_LIST, adFunctionInstanceCandidateDtoList);
    dtos.put(AD_FUNCTION_INSTANCE_DTO_LIST, adFunctionInstanceDtoList);
    dtos.put(AD_FUNCTION_ERROR_MESSAGES_DTO_LIST, adFunctionErrorMessages);

    return dtos;
  }

  public static Map<String, Object> buildDtosMap() {

    Map<String, Object> dtos = new HashMap<>();

    dtos.put(RAW_POINT_DTO_LIST, new ArrayList<>());
    dtos.put(NON_POINT_NODE_DTO_LIST, new ArrayList<>());
    dtos.put(MAPPABLE_POINT_NODE_DTO_LIST, new ArrayList<>());
    dtos.put(CUSTOM_ASYNC_COMPUTED_POINT_NODE_DTO_LIST, new ArrayList<>());
    dtos.put(SCHEDULED_ASYNC_COMPUTED_POINT_NODE_DTO_LIST, new ArrayList<>());
    dtos.put(ASYNC_COMPUTED_POINT_NODE_DTO_LIST, new ArrayList<>());
    dtos.put(NODE_TAG_DTO_LIST, new ArrayList<>());
    dtos.put(ENERGY_EXCHANGE_SYSTEM_EDGE_DTO_LIST, new ArrayList<>());
    dtos.put(REPORT_INSTANCE_DTO_LIST, new ArrayList<>());
    dtos.put(REPORT_INSTANCE_STATUS_DTO_LIST, new ArrayList<>());
    dtos.put(AD_FUNCTION_INSTANCE_CANDIDATE_DTO_LIST, new ArrayList<>());
    dtos.put(AD_FUNCTION_INSTANCE_DTO_LIST, new ArrayList<>());
    dtos.put(AD_FUNCTION_ERROR_MESSAGES_DTO_LIST, new ArrayList<>());

    return dtos;
  }
  
  @SuppressWarnings("unchecked")
  public static List<RawPointDto> getRawPointDtoList(Map<String, Object> dtos) {

    return (List<RawPointDto>) dtos.get(RAW_POINT_DTO_LIST);
  }

  @SuppressWarnings("unchecked")
  public static List<NonPointNodeDto> getNonPointNodeDtoList(Map<String, Object> dtos) {

    return (List<NonPointNodeDto>) dtos.get(NON_POINT_NODE_DTO_LIST);
  }

  @SuppressWarnings("unchecked")
  public static List<MappablePointNodeDto> getMappablePointNodeDtoList(Map<String, Object> dtos) {

    return (List<MappablePointNodeDto>) dtos.get(MAPPABLE_POINT_NODE_DTO_LIST);
  }

  @SuppressWarnings("unchecked")
  public static List<CustomAsyncComputedPointNodeDto> getCustomAsyncComputedPointNodeDtoList(
      Map<String, Object> dtos) {

    return (List<CustomAsyncComputedPointNodeDto>) dtos
        .get(CUSTOM_ASYNC_COMPUTED_POINT_NODE_DTO_LIST);
  }
  
  @SuppressWarnings("unchecked")
  public static List<ScheduledAsyncComputedPointNodeDto> getScheduledAsyncComputedPointNodeDtoList(
      Map<String, Object> dtos) {

    return (List<ScheduledAsyncComputedPointNodeDto>) dtos
        .get(SCHEDULED_ASYNC_COMPUTED_POINT_NODE_DTO_LIST);
  }

  @SuppressWarnings("unchecked")
  public static List<AsyncComputedPointNodeDto> getAsyncComputedPointNodeDtoList(
      Map<String, Object> dtos) {

    return (List<AsyncComputedPointNodeDto>) dtos.get(ASYNC_COMPUTED_POINT_NODE_DTO_LIST);
  }

  @SuppressWarnings("unchecked")
  public static List<NodeTagDto> getNodeTagDtoList(Map<String, Object> dtos) {

    return (List<NodeTagDto>) dtos.get(NODE_TAG_DTO_LIST);
  }

  @SuppressWarnings("unchecked")
  public static List<EnergyExchangeSystemEdgeDto> getEnergyExchangeSystemEdgeDtoList(Map<String, Object> dtos) {

    return (List<EnergyExchangeSystemEdgeDto>) dtos.get(ENERGY_EXCHANGE_SYSTEM_EDGE_DTO_LIST);
  }

  @SuppressWarnings("unchecked")
  public static List<ReportInstanceDto> getReportInstanceDtoList(Map<String, Object> dtos) {

    return (List<ReportInstanceDto>) dtos.get(REPORT_INSTANCE_DTO_LIST);
  }

  @SuppressWarnings("unchecked")
  public static List<ReportInstanceStatusDto> getReportInstanceStatusDtoList(Map<String, Object> dtos) {

    return (List<ReportInstanceStatusDto>) dtos.get(REPORT_INSTANCE_STATUS_DTO_LIST);
  }
  
  @SuppressWarnings("unchecked")
  public static List<AdFunctionInstanceDto> getAdFunctionInstanceCandidateDtoList(Map<String, Object> dtos) {

    return (List<AdFunctionInstanceDto>) dtos.get(AD_FUNCTION_INSTANCE_CANDIDATE_DTO_LIST);
  }
  
  @SuppressWarnings("unchecked")
  public static List<AdFunctionInstanceDto> getAdFunctionInstanceDtoList(Map<String, Object> dtos) {

    return (List<AdFunctionInstanceDto>) dtos.get(AD_FUNCTION_INSTANCE_DTO_LIST);
  }

  @SuppressWarnings("unchecked")
  public static List<AdFunctionErrorMessagesDto> getAdFunctionErrorMessagesDtoList(Map<String, Object> dtos) {

    return (List<AdFunctionErrorMessagesDto>) dtos.get(AD_FUNCTION_ERROR_MESSAGES_DTO_LIST);
  }
  
  public static RawPointDto mapRawPointDto(RawPointEntity rawPoint, Map<String, Object> dtos) {

    if (rawPoint.getIsDeleted()) {
      return null;
    }
    
    List<RawPointDto> rawPointDtoList = getRawPointDtoList(dtos);
    RawPointDto rawPointDto = new RawPointDto();

    rawPointDto.setId(rawPoint.getPersistentIdentity());
    // rawPointDto.setComponentId(rawPoint.getComponentId());
    rawPointDto.setMetricId(rawPoint.getMetricId());
    rawPointDto.setPointType(rawPoint.getPointType());
    rawPointDto.setRange(rawPoint.getRange());
    // rawPointDto.setUnitType(rawPoint.getUnitType());
    rawPointDto.setIgnore(rawPoint.getIgnored());
    rawPointDto.setDeleted(rawPoint.getDeleted());
    // rawPointDto.setCreatedAt(rawPoint.getCreatedAtAsString());

    rawPointDtoList.add(rawPointDto);
    return rawPointDto;
  }
  
  public static NonPointNodeDto mapNonPointNodeDto(AbstractNodeEntity node, Map<String, Object> dtos) {

    if (node.getIsDeleted()) {
      return null;
    }
    
    List<NonPointNodeDto> nonPointNodeDtoList = getNonPointNodeDtoList(dtos);
    NonPointNodeDto nonPointNodeDto = new NonPointNodeDto();

    nonPointNodeDto.setNodeId(node.getPersistentIdentity());
    nonPointNodeDto.setNodeTypeId(node.getNodeType().getId());
    nonPointNodeDto.setNodeName(node.getName());
    nonPointNodeDto.setNodeDisplayName(node.getDisplayName());
    nonPointNodeDto.setNodeCreatedAt(AbstractEntity.formatTimestamp(node.getCreatedAt()));
    nonPointNodeDto.setNodeUpdatedAt(AbstractEntity.formatTimestamp(node.getUpdatedAt()));

    if (!(node instanceof PortfolioEntity)) {
      
      nonPointNodeDto.setNodeParentId(node.getParentNode().getPersistentIdentity());
      nonPointNodeDto.setNodeParentNodeTypeId(node.getParentNode().getNodeType().getId());
    }

    if (node instanceof EquipmentEntity) {
      
      EquipmentEntity equipment = (EquipmentEntity) node;
      
      List<EnergyExchangeSystemEdgeDto> energyExchangeSystemEdgeDtoList = PortfolioDtoMapper.getEnergyExchangeSystemEdgeDtoList(dtos);
      
      for (Map.Entry<EnergyExchangeSystemType, Set<EnergyExchangeEntity>> entry: equipment.getAllParentEnergyExchangeSystemNodes().entrySet()) {
        
        EnergyExchangeSystemType energyExchangeSystemType = entry.getKey();
        
        for (EnergyExchangeEntity parentEnergyExchangeSystemEntity: entry.getValue()) {
          
          energyExchangeSystemEdgeDtoList.add(AbstractEnergyExchangeEntity
              .Mapper
              .getInstance()
              .mapEntityToDto(
                  energyExchangeSystemType,
                  parentEnergyExchangeSystemEntity,
                  equipment));
        }
      }
      
    } else if (node instanceof PlantEntity) {

      PlantEntity plant = (PlantEntity) node;
      
      List<EnergyExchangeSystemEdgeDto> energyExchangeSystemEdgeDtoList = PortfolioDtoMapper.getEnergyExchangeSystemEdgeDtoList(dtos);
      
      for (Map.Entry<EnergyExchangeSystemType, Set<EnergyExchangeEntity>> entry: plant.getAllParentEnergyExchangeSystemNodes().entrySet()) {
        
        EnergyExchangeSystemType energyExchangeSystemType = entry.getKey();
        
        for (EnergyExchangeEntity parentEnergyExchangeSystemEntity: entry.getValue()) {
          
          energyExchangeSystemEdgeDtoList.add(AbstractEnergyExchangeEntity
              .Mapper
              .getInstance()
              .mapEntityToDto(
                  energyExchangeSystemType,
                  parentEnergyExchangeSystemEntity,
                  plant));
        }
      }
      
    } else if (node instanceof LoopEntity) {

      LoopEntity loop = (LoopEntity) node;
      
      List<EnergyExchangeSystemEdgeDto> energyExchangeSystemEdgeDtoList = PortfolioDtoMapper.getEnergyExchangeSystemEdgeDtoList(dtos);
      
      for (Map.Entry<EnergyExchangeSystemType, Set<EnergyExchangeEntity>> entry: loop.getAllParentEnergyExchangeSystemNodes().entrySet()) {
        
        EnergyExchangeSystemType energyExchangeSystemType = entry.getKey();
        
        for (EnergyExchangeEntity parentEnergyExchangeSystemEntity: entry.getValue()) {
          
          energyExchangeSystemEdgeDtoList.add(AbstractEnergyExchangeEntity
              .Mapper
              .getInstance()
              .mapEntityToDto(
                  energyExchangeSystemType,
                  parentEnergyExchangeSystemEntity,
                  loop));
        }
      }
      
    } else if (node instanceof FloorEntity) {
      
      nonPointNodeDto.setFloorOrdinal(((FloorEntity)node).getFloorOrdinal());
      
    }

    Set<TagEntity> nodeTags = node.getNodeTags();
    if (!nodeTags.isEmpty()) {

      List<NodeTagDto> nodeTagDtoList = getNodeTagDtoList(dtos);
      NodeTagDto nodeTagDto = new NodeTagDto();
      nodeTagDto.setId(node.getPersistentIdentity());
      nodeTagDto.setTagIds(node.getNodeTagIdsAsList());
      nodeTagDtoList.add(nodeTagDto);
    }

    nonPointNodeDtoList.add(nonPointNodeDto);
    
    return nonPointNodeDto;
  }
  
  public static List<NonPointNodeDto> mapNonPointNodeDto(BuildingEntity b, Map<String, Object> dtos) {

    List<NonPointNodeDto> list = new ArrayList<>();
    if (b.getIsDeleted()) {
      return list;
    }
    List<NonPointNodeDto> nonPointNodeDtoList = getNonPointNodeDtoList(dtos);
    
    if (b.getChildTemporalConfigs().isEmpty()) {

      NonPointNodeDto nonPointNodeDto = new NonPointNodeDto();
      
      mapBuildingAttributes(b, nonPointNodeDto, dtos);
      
      nonPointNodeDtoList.add(nonPointNodeDto);
      
      list.add(nonPointNodeDto);
      
    } else {

      for (BuildingTemporalConfigEntity t: b.getChildTemporalConfigs()) {
        
        if (!t.getIsDeleted()) {

          NonPointNodeDto nonPointNodeDto = new NonPointNodeDto();
          
          mapBuildingAttributes(b, nonPointNodeDto, dtos);
          
          Integer temporalId = t.getPersistentIdentity();
          nonPointNodeDto.setBuildingTemporalId(temporalId != null?temporalId.toString():null);
          nonPointNodeDto.setBuildingTemporalEffectiveDate(t.getEffectiveDate().toString());
          nonPointNodeDto.setBuildingTemporalSqft(t.getSquareFeet().toString());

          StringBuilder utilityIdSb = new StringBuilder("{");
          StringBuilder computationIntervalSb = new StringBuilder("{");
          StringBuilder formulaSb = new StringBuilder("{");
          StringBuilder rateSb = new StringBuilder("{");
          StringBuilder baselineDescriptionSb = new StringBuilder("{");
          StringBuilder userNotesSb = new StringBuilder("{");
          
          List<BuildingTemporalUtilityEntity> childUtilities = new ArrayList<>();
          childUtilities.addAll(t.getChildUtilities());
          for (int i=0; i < childUtilities.size(); i++) {
            
            BuildingTemporalUtilityEntity u = childUtilities.get(i);
          
            utilityIdSb.append(u.getBuildingUtilityType().getId());
            computationIntervalSb.append(u.getComputationInterval().getId());
            formulaSb.append(u.getFormula());
            rateSb.append(u.getRate());
            baselineDescriptionSb.append(u.getBaselineDescription() != null?u.getBaselineDescription():"");
            userNotesSb.append(u.getUserNotes() != null?u.getUserNotes():"");
            
            if (i < childUtilities.size()-1) {
              
              utilityIdSb.append(",");
              computationIntervalSb.append(",");
              formulaSb.append(",");
              rateSb.append(",");
              baselineDescriptionSb.append(",");
              userNotesSb.append(",");
            }          
          }
          utilityIdSb.append("}");
          computationIntervalSb.append("}");
          formulaSb.append("}");
          rateSb.append("}");
          baselineDescriptionSb.append("}");
          userNotesSb.append("}");        
          
          nonPointNodeDto.setBuildingUtilityId(utilityIdSb.toString());
          nonPointNodeDto.setBuildingUtilityComputationIntervalId(computationIntervalSb.toString());
          nonPointNodeDto.setBuildingUtilityFormula(formulaSb.toString());
          nonPointNodeDto.setBuildingUtilityRate(rateSb.toString());
          nonPointNodeDto.setBuildingUtilityBaselineDescription(baselineDescriptionSb.toString());
          nonPointNodeDto.setBuildingUtilityUserNotes(userNotesSb.toString());        
          
          nonPointNodeDtoList.add(nonPointNodeDto);
          
          list.add(nonPointNodeDto);
        }
      }
      
      // If temporal configs were marked as deleted, then we need to map the building (as if there hadn't been any temporal configs)
      if (list.isEmpty()) {
        
        NonPointNodeDto nonPointNodeDto = new NonPointNodeDto();
        
        mapBuildingAttributes(b, nonPointNodeDto, dtos);
        
        nonPointNodeDtoList.add(nonPointNodeDto);
        
        list.add(nonPointNodeDto);
      }
    }
    return list;
  }
  
  public static void mapBuildingAttributes(
      BuildingEntity b, 
      NonPointNodeDto nonPointNodeDto,
      Map<String, Object> dtos) {
   
    nonPointNodeDto.setNodeId(b.getPersistentIdentity());
    nonPointNodeDto.setNodeTypeId(b.getNodeType().getId());
    nonPointNodeDto.setNodeName(b.getName());
    nonPointNodeDto.setNodeDisplayName(b.getDisplayName());
    nonPointNodeDto.setNodeCreatedAt(AbstractEntity.formatTimestamp(b.getCreatedAt()));
    nonPointNodeDto.setNodeUpdatedAt(AbstractEntity.formatTimestamp(b.getUpdatedAt()));
    
    nonPointNodeDto.setNodeParentId(b.getParentNode().getPersistentIdentity());
    nonPointNodeDto.setNodeParentNodeTypeId(b.getParentNode().getNodeType().getId());
    
    nonPointNodeDto.setBuildingTimezone(b.getTimezone());
    nonPointNodeDto.setBuildingAddress(b.getAddress());
    nonPointNodeDto.setBuildingCity(b.getCity());
    nonPointNodeDto.setBuildingStateOrProvince(b.getStateOrProvince());
    nonPointNodeDto.setBuildingPostalCode(b.getPostalCode());
    nonPointNodeDto.setBuildingCountryCode(b.getCountryCode());
    nonPointNodeDto.setBuildingUnitSystem(b.getUnitSystem().toString());
    Float latitude = b.getLatitude();
    if (latitude != null) {
      nonPointNodeDto.setBuildingLatitude(latitude.toString());  
    }
    Float longitude = b.getLongitude();
    if (longitude != null) {
      nonPointNodeDto.setBuildingLongitude(longitude.toString());  
    }
    WeatherStationEntity weatherStation = b.getWeatherStation();
    if (weatherStation != null) {
      nonPointNodeDto.setBuildingWeatherStationId(weatherStation.getPersistentIdentity());
    }
    
    nonPointNodeDto.setBuildingStatus(b.getBuildingStatus().getName());
    nonPointNodeDto.setBuildingStatusUpdatedAt(AbstractEntity.formatTimestamp(b.getBuildingStatusUpdatedAt()));
    nonPointNodeDto.setBuildingPaymentStatus(b.getBuildingPaymentStatus().getName());
    nonPointNodeDto.setBuildingPaymentStatusUpdatedAt(AbstractEntity.formatTimestamp(b.getBuildingPaymentStatusUpdatedAt()));
    nonPointNodeDto.setBuildingBillingGracePeriod(b.getBillingGracePeriod());
    nonPointNodeDto.setBuildingGracePeriodWarningNotificationId(b.getBuildingGracePeriodWarningNotificationId());
    nonPointNodeDto.setBuildingPaymentType(b.getBuildingPaymentType().getName());
    
    nonPointNodeDto.setBuildingPendingDeletion(Boolean.FALSE);
    
    if (b instanceof BillableBuildingEntity) {
      
      BillableBuildingEntity bb = (BillableBuildingEntity)b;

      BuildingSubscriptionEntity s = bb.getChildBuildingSubscriptionNullIfNotExists();
      if (s != null) {
        
        nonPointNodeDto.setBuildingPaymentPlanId(s.getParentPaymentPlan().getPersistentIdentity());
        nonPointNodeDto.setBuildingPaymentMethodId(s.getParentPaymentMethod().getPersistentIdentity());
        nonPointNodeDto.setBuildingStripeSubscriptionId(s.getStripeSubscriptionId());
        nonPointNodeDto.setBuildingSubscriptionStartedAt(AbstractEntity.formatTimestamp(s.getStartedAt()));
        nonPointNodeDto.setBuildingSubscriptionCurrentIntervalStartedAt(AbstractEntity.formatTimestamp(s.getCurrentIntervalStartedAt()));
        
        if (s.getPendingPaymentPlan() != null) {
          nonPointNodeDto.setBuildingPendingPaymentPlanId(s.getPendingPaymentPlan().getPersistentIdentity());
          nonPointNodeDto.setBuildingPendingPaymentPlanUpdatedAt(AbstractEntity.formatTimestamp(s.getPendingPaymentPlanUpdatedAt()));
        }
      }
            
      nonPointNodeDto.setBuildingPendingDeletion(bb.getPendingDeletion());
      if (bb.getPendingDeletionUpdatedAt() != null) {
        
        nonPointNodeDto.setBuildingPendingDeletionUpdatedAt(AbstractEntity.formatTimestamp(bb.getPendingDeletionUpdatedAt()));
        
      }
    }
    
    Set<TagEntity> nodeTags = b.getNodeTags();
    if (!nodeTags.isEmpty()) {

      List<NodeTagDto> nodeTagDtoList = getNodeTagDtoList(dtos);
      NodeTagDto nodeTagDto = new NodeTagDto();
      nodeTagDto.setId(b.getPersistentIdentity());
      nodeTagDto.setTagIds(b.getNodeTagIdsAsList());
      nodeTagDtoList.add(nodeTagDto);
    }
  }

  public static MappablePointNodeDto mapMappablePointNodeDto(
      MappablePointEntity mappablePoint,
      Map<String, Object> dtos) {

    if (mappablePoint.getIsDeleted()) {
      return null;
    }
    
    List<MappablePointNodeDto> mappablePointNodeDtoList = getMappablePointNodeDtoList(dtos);
    MappablePointNodeDto mappablePointNodeDto = new MappablePointNodeDto();

    RawPointEntity rawPoint = mappablePoint.getRawPoint();

    mappablePointNodeDto.setNodeId(mappablePoint.getPersistentIdentity());
    mappablePointNodeDto.setNodeName(mappablePoint.getName());
    mappablePointNodeDto.setNodeDisplayName(mappablePoint.getDisplayName());
    mappablePointNodeDto.setNodeParentId(mappablePoint.getParentNode().getPersistentIdentity());
    mappablePointNodeDto.setNodeParentNodeTypeId(mappablePoint.getParentNode().getNodeType().getId());
    mappablePointNodeDto.setPointDataTypeId(mappablePoint.getDataType().getId());
    
    UnitEntity unit = mappablePoint.getUnitNullIfNotExists();
    if (unit != null) {
      
      mappablePointNodeDto.setPointUnitId(unit.getPersistentIdentity());
      mappablePointNodeDto.setPointUnitType(unit.getName());
    }
    
    String r = mappablePoint.getRangeNullIfEmpty();
    if (r != null) {
      mappablePointNodeDto.setPointRange(r);  
    }
    
    mappablePointNodeDto.setPointRawPointId(rawPoint.getPersistentIdentity());
    mappablePointNodeDto.setPointMetricId(rawPoint.getMetricId());
    
    PointTemplateEntity pt = mappablePoint.getPointTemplateNullIfEmpty();
    if (pt != null) {
      mappablePointNodeDto.setPointPointTemplateId(pt.getPersistentIdentity());
    }

    Set<TagEntity> nodeTags = mappablePoint.getNodeTags();
    if (!nodeTags.isEmpty()) {

      List<NodeTagDto> nodeTagDtoList = getNodeTagDtoList(dtos);
      NodeTagDto nodeTagDto = new NodeTagDto();
      nodeTagDto.setId(mappablePoint.getPersistentIdentity());
      nodeTagDto.setTagIds(mappablePoint.getNodeTagIdsAsList());
      nodeTagDtoList.add(nodeTagDto);
    }

    mapRawPointDto(rawPoint, dtos);

    mappablePointNodeDtoList.add(mappablePointNodeDto);
    return mappablePointNodeDto;
  }

  public static void mapCustomAsyncComputedPointNodeDto(
      CustomAsyncComputedPointEntity entity, 
      Map<String, Object> dtos) {
    
    if (entity.getIsDeleted()) {
      return;
    }
    
    List<CustomAsyncComputedPointNodeDto> customAsyncComputedPointNodeDtoList = getCustomAsyncComputedPointNodeDtoList(dtos);
    
    if (entity.getComputationInterval() != null) {
      for (TemporalAsyncComputedPointConfigEntity childTemporalConfig: entity.getChildTemporalConfigs()) {
        
        CustomAsyncComputedPointNodeDto dto = new CustomAsyncComputedPointNodeDto();
        
        dto.setId(entity.getPersistentIdentity());
        dto.setName(entity.getName());
        dto.setDisplayName(entity.getDisplayName());
        dto.setParentId(entity.getParentNode().getPersistentIdentity());
        dto.setParentNodeTypeId(entity.getParentNode().getNodeType().getId());
        dto.setCreatedAt(entity.getCreatedAtAsString());
        dto.setUpdatedAt(entity.getUpdatedAtAsString());
        
        UnitEntity u = entity.getUnitNullIfNotExists();
        if (u != null) {
          dto.setUnitId(u.getPersistentIdentity());
        }
        
        dto.setValue(entity.getLastValue());
        dto.setValueTimestamp(entity.getLastValueTimestamp());
        dto.setConfigurable(entity.getConfigurable());
        dto.setTimezoneBasedRollups(entity.getTimezoneBasedRollups());
        dto.setMetricId(entity.getMetricId());
        
        PointTemplateEntity pt = entity.getPointTemplateNullIfEmpty();
        if (pt != null) {
          dto.setPointTemplateId(pt.getPersistentIdentity());  
        }
        
        Set<TagEntity> nodeTags = entity.getNodeTags();
        if (!nodeTags.isEmpty()) {

          List<NodeTagDto> nodeTagDtoList = getNodeTagDtoList(dtos);
          NodeTagDto nodeTagDto = new NodeTagDto();
          nodeTagDto.setId(entity.getPersistentIdentity());
          nodeTagDto.setTagIds(entity.getNodeTagIdsAsList());
          nodeTagDtoList.add(nodeTagDto);
        }
        
        ComputationInterval computationInterval = entity.getComputationInterval();
        if (computationInterval != null) {
          dto.setComputationInterval(entity.getComputationInterval().toString());  
        }
        
        dto.setTemporalConfigId(childTemporalConfig.getPersistentIdentity());
        
        LocalDate effectiveDate = childTemporalConfig.getEffectiveDate();
        if (effectiveDate != null) {
          dto.setEffectiveDate(childTemporalConfig.getEffectiveDate().toString());  
        }
        
        dto.setFormula(childTemporalConfig.getFormula());
        dto.setDescription(childTemporalConfig.getDescription());

        StringBuilder variablePointIdSb = new StringBuilder("{");
        StringBuilder variableFillPolicyIdSb = new StringBuilder("{");
        StringBuilder variableNameSb = new StringBuilder("{");
        List<FormulaVariableEntity> childVariables = new ArrayList<>();
        childVariables.addAll(childTemporalConfig.getChildVariables());
        for (int i=0; i < childVariables.size(); i++) {
          
          FormulaVariableEntity childVariable = childVariables.get(i);
          
          variablePointIdSb.append(childVariable.getParentPoint().getPersistentIdentity().toString());
          variableFillPolicyIdSb.append(Integer.toString(childVariable.getFillPolicy().getId()));
          variableNameSb.append(childVariable.getName());
          
          if (i < childVariables.size()-1) {
            
            variablePointIdSb.append(",");
            variableFillPolicyIdSb.append(",");
            variableNameSb.append(",");
          }
        }
        variablePointIdSb.append("}");
        variableFillPolicyIdSb.append("}");
        variableNameSb.append("}");
        
        dto.setVariablePointId(variablePointIdSb.toString());
        dto.setVariableFillPolicyId(variableFillPolicyIdSb.toString());
        dto.setVariableName(variableNameSb.toString());

        customAsyncComputedPointNodeDtoList.add(dto);
      }
    } else {
      
      CustomAsyncComputedPointNodeDto dto = new CustomAsyncComputedPointNodeDto();

      dto.setId(entity.getPersistentIdentity());
      dto.setName(entity.getName());
      dto.setDisplayName(entity.getDisplayName());
      dto.setParentId(entity.getParentNode().getPersistentIdentity());
      dto.setParentNodeTypeId(entity.getParentNode().getNodeType().getId());
      dto.setCreatedAt(entity.getCreatedAtAsString());
      dto.setUpdatedAt(entity.getUpdatedAtAsString());
      
      UnitEntity u = entity.getUnitNullIfNotExists();
      if (u != null) {
        dto.setUnitId(u.getPersistentIdentity());
      }
      
      dto.setValue(entity.getLastValue());
      dto.setValueTimestamp(entity.getLastValueTimestamp());
      dto.setConfigurable(entity.getConfigurable());
      dto.setTimezoneBasedRollups(entity.getTimezoneBasedRollups());
      dto.setMetricId(entity.getMetricId());
      
      PointTemplateEntity pt = entity.getPointTemplateNullIfEmpty();
      if (pt != null) {
        dto.setPointTemplateId(pt.getPersistentIdentity());  
      }
      
      Set<TagEntity> nodeTags = entity.getNodeTags();
      if (!nodeTags.isEmpty()) {

        List<NodeTagDto> nodeTagDtoList = getNodeTagDtoList(dtos);
        NodeTagDto nodeTagDto = new NodeTagDto();
        nodeTagDto.setId(entity.getPersistentIdentity());
        nodeTagDto.setTagIds(entity.getNodeTagIdsAsList());
        nodeTagDtoList.add(nodeTagDto);
      }
    }
  }
  
  public static ScheduledAsyncComputedPointNodeDto mapScheduledAsyncComputedPointNodeDto(
      ScheduledAsyncComputedPointEntity scheduledAsyncComputedPoint, Map<String, Object> dtos) {
    
    if (scheduledAsyncComputedPoint.getIsDeleted()) {
      return null;
    }    

    List<ScheduledAsyncComputedPointNodeDto> scheduledAsyncComputedPointNodeDtoList =
        getScheduledAsyncComputedPointNodeDtoList(dtos);
    ScheduledAsyncComputedPointNodeDto scheduledAsyncComputedPointNodeDto =
        new ScheduledAsyncComputedPointNodeDto();

    scheduledAsyncComputedPointNodeDto.setNodeId(scheduledAsyncComputedPoint.getPersistentIdentity());
    scheduledAsyncComputedPointNodeDto.setNodeName(scheduledAsyncComputedPoint.getName());
    scheduledAsyncComputedPointNodeDto.setNodeDisplayName(scheduledAsyncComputedPoint.getDisplayName());
    scheduledAsyncComputedPointNodeDto.setNodeParentId(scheduledAsyncComputedPoint.getParentNode().getPersistentIdentity());
    scheduledAsyncComputedPointNodeDto.setNodeParentNodeTypeId(scheduledAsyncComputedPoint.getParentNode().getNodeType().getId());
    scheduledAsyncComputedPointNodeDto.setNodeCreatedAt(scheduledAsyncComputedPoint.getCreatedAtAsString());
    
    UnitEntity u = scheduledAsyncComputedPoint.getUnitNullIfNotExists();
    if (u != null) {
      scheduledAsyncComputedPointNodeDto.setPointUnitId(u.getPersistentIdentity());
    }
    
    scheduledAsyncComputedPointNodeDto.setPointDataTypeId(scheduledAsyncComputedPoint.getDataType().getId());
    
    String r = scheduledAsyncComputedPoint.getRangeNullIfEmpty();
    if (r != null) {
      scheduledAsyncComputedPointNodeDto.setPointRange(r);  
    }
    
    scheduledAsyncComputedPointNodeDto.setPointMetricId(scheduledAsyncComputedPoint.getMetricId());
    
    scheduledAsyncComputedPointNodeDto.setValue(scheduledAsyncComputedPoint.getLastValue());
    scheduledAsyncComputedPointNodeDto.setValueTimestamp(scheduledAsyncComputedPoint.getLastValueTimestamp());
    scheduledAsyncComputedPointNodeDto.setConfigurable(scheduledAsyncComputedPoint.getConfigurable());
    scheduledAsyncComputedPointNodeDto.setTimezoneBasedRollups(scheduledAsyncComputedPoint.getTimezoneBasedRollups());
    scheduledAsyncComputedPointNodeDto.setGlobalComputedPointId(scheduledAsyncComputedPoint.getGlobalComputedPointId());
    scheduledAsyncComputedPointNodeDto.setScheduledEventTypeId(scheduledAsyncComputedPoint.getScheduledEventType().getPersistentIdentity());

    PointTemplateEntity pt = scheduledAsyncComputedPoint.getPointTemplateNullIfEmpty();
    if (pt != null) {
      scheduledAsyncComputedPointNodeDto.setPointPointTemplateId(pt.getPersistentIdentity());  
    }    
    
    Set<TagEntity> nodeTags = scheduledAsyncComputedPoint.getNodeTags();
    if (!nodeTags.isEmpty()) {

      List<NodeTagDto> nodeTagDtoList = getNodeTagDtoList(dtos);
      NodeTagDto nodeTagDto = new NodeTagDto();
      nodeTagDto.setId(scheduledAsyncComputedPoint.getPersistentIdentity());
      nodeTagDto.setTagIds(scheduledAsyncComputedPoint.getNodeTagIdsAsList());
      nodeTagDtoList.add(nodeTagDto);
    }

    scheduledAsyncComputedPointNodeDtoList.add(scheduledAsyncComputedPointNodeDto);
    return scheduledAsyncComputedPointNodeDto;
  }

  public static AsyncComputedPointNodeDto mapAsyncComputedPointNodeDto(
      AsyncComputedPointEntity asyncComputedPoint,
      Map<String, Object> dtos) {

    /*
    if (asyncComputedPoint.getIsDeleted()) {
      return null;
    } 
    */   
    
    List<AsyncComputedPointNodeDto> asyncComputedPointNodeDtoList = getAsyncComputedPointNodeDtoList(dtos);
    AsyncComputedPointNodeDto asyncComputedPointNodeDto = new AsyncComputedPointNodeDto();
    
    String subtype = null;
    if (asyncComputedPoint instanceof WeatherAsyncComputedPointEntity) {
     
      subtype = AsyncComputedPointNodeDto.WEATHER_STATION;

    } else if (asyncComputedPoint instanceof ManualAsyncComputedPointEntity) {
      
      subtype = AsyncComputedPointNodeDto.MANUAL;
      
    } else if (asyncComputedPoint instanceof SystemAsyncComputedPointEntity) {
      
      subtype = AsyncComputedPointNodeDto.SYSTEM;

    } else if (asyncComputedPoint instanceof ScheduledAsyncComputedPointEntity) {
      
      // TODO: ASK CARLOS IF HE CAN REFACTOR THE SCHEDULED ASYNC COMPUTED POINT TABLE 
      // TO BE A PROPER SUBCLASS OF ASYNC COMPUTED POINT.
      throw new IllegalStateException("Scheduled async computed points are dealt with separately currently. Unexpected occurrence: " + asyncComputedPoint);
      
    } else if (asyncComputedPoint instanceof AdFunctionAsyncComputedPointEntity) {

      // TODO: TDM: Because there is no relationship between the async computed point and
      // the AD function instance output point, we have to infer the type based on the metricId.
      String metricId = asyncComputedPoint.getMetricId();
      if (metricId.contains("/Rule")) {
        
        subtype = AsyncComputedPointNodeDto.RULE;
        
      } else if (metricId.contains("/Computed")) {
        
        subtype = AsyncComputedPointNodeDto.COMPUTED;
        
      } else {
       
        throw new IllegalStateException("Could not determine AD Function Instance Type from metricId: ["
            + metricId
            + "] for point: ["
            + asyncComputedPoint
            + "].");
      }
      
    } else if (asyncComputedPoint instanceof CustomAsyncComputedPointEntity) {

      subtype = AsyncComputedPointNodeDto.CUSTOM;
      
      
    } else {

      throw new IllegalStateException("Unsupported asyncComputedPoint: [" 
          + asyncComputedPoint.getClassAndNaturalIdentity()
          + "] with id: ["
          + asyncComputedPoint.getPersistentIdentity()
          + "]");
      
    }
    asyncComputedPointNodeDto.setSubtype(subtype);

    asyncComputedPointNodeDto.setNodeId(asyncComputedPoint.getPersistentIdentity());
    asyncComputedPointNodeDto.setNodeName(asyncComputedPoint.getName());
    asyncComputedPointNodeDto.setNodeDisplayName(asyncComputedPoint.getDisplayName());
    asyncComputedPointNodeDto.setNodeParentId(asyncComputedPoint.getParentNode().getPersistentIdentity());
    asyncComputedPointNodeDto.setMetricId(asyncComputedPoint.getMetricId());
    
    PointTemplateEntity pt = asyncComputedPoint.getPointTemplateNullIfEmpty();
    if (pt != null) {
      asyncComputedPointNodeDto.setPointPointTemplateId(pt.getPersistentIdentity());  
    }
    
    asyncComputedPointNodeDto.setValue(asyncComputedPoint.getLastValue());
    asyncComputedPointNodeDto.setValueTimestamp(asyncComputedPoint.getLastValueTimestamp());
    
    UnitEntity u = asyncComputedPoint.getUnitNullIfNotExists();
    if (u != null) {
      asyncComputedPointNodeDto.setPointUnitId(u.getPersistentIdentity());
    }
    
    asyncComputedPointNodeDto.setPointDataTypeId(asyncComputedPoint.getDataType().getId());
    
    String r = asyncComputedPoint.getRangeNullIfEmpty();
    if (r != null) {
      asyncComputedPointNodeDto.setPointRange(r);  
    }
    
    Set<TagEntity> nodeTags = asyncComputedPoint.getNodeTags();
    if (!nodeTags.isEmpty()) {

      List<NodeTagDto> nodeTagDtoList = getNodeTagDtoList(dtos);
      NodeTagDto nodeTagDto = new NodeTagDto();
      nodeTagDto.setId(asyncComputedPoint.getPersistentIdentity());
      nodeTagDto.setTagIds(asyncComputedPoint.getNodeTagIdsAsList());
      nodeTagDtoList.add(nodeTagDto);
    }

    asyncComputedPointNodeDto.setConfigurable(asyncComputedPoint.getConfigurable());
    asyncComputedPointNodeDto.setTimezoneBasedRollups(asyncComputedPoint.getTimezoneBasedRollups());            
    asyncComputedPointNodeDto.setGlobalComputedPointId(asyncComputedPoint.getGlobalComputedPointId());
    asyncComputedPointNodeDto.setCreatedAt(asyncComputedPoint.getCreatedAtAsString());
    asyncComputedPointNodeDto.setUpdatedAt(asyncComputedPoint.getUpdatedAtAsString());

    asyncComputedPointNodeDtoList.add(asyncComputedPointNodeDto);
    return asyncComputedPointNodeDto;
  }

  private void mapReportInstance(
      PortfolioEntity portfolio,
      ReportInstanceDto reportInstanceDto) {

    try {
      
      ReportInstanceEntity
          .Mapper
          .getInstance()
          .mapDtoToEntity(portfolio, reportInstanceDto);
      
    } catch (Exception e) {
      LOGGER.error("Unable to map: {},\n error: {}",
          reportInstanceDto,
          e.getMessage(),
          e);
    }
  }

  public static void mapReportInstanceToDtos(
      ReportInstanceEntity reportInstance,
      Map<String, Object> dtos) {

    List<ReportInstanceDto> reportInstanceDtoList = getReportInstanceDtoList(dtos);
    reportInstanceDtoList.add(ReportInstanceEntity.Mapper.getInstance().mapEntityToDto(reportInstance));
    
    List<ReportInstanceStatusDto> reportInstanceStatusDtoList = getReportInstanceStatusDtoList(dtos);
    reportInstanceStatusDtoList.add(ReportInstanceEntity.Mapper.getInstance().mapEntityToStatusDto(reportInstance));
  }
  
  private void mapReportInstanceStatus(
      PortfolioEntity portfolio,
      ReportInstanceStatusDto reportInstanceStatusDto) {
    
    try {
      
      ReportInstanceEntity
          .Mapper
          .getInstance()
          .mapStatusDtoToEntity(portfolio, reportInstanceStatusDto);
      
    } catch (Exception e) {
      LOGGER.error("Unable to map: {},\n error: {}",
          reportInstanceStatusDto,
          e.getMessage(), 
          e);
    }
  }

  public static void mapReportInstanceStatusDto(
      ReportInstanceEntity reportInstance,
      Map<String, Object> dtos) {

    List<ReportInstanceStatusDto> reportInstanceStatusDtoList = getReportInstanceStatusDtoList(dtos);

    reportInstanceStatusDtoList.add(ReportInstanceEntity
        .Mapper
        .getInstance()
        .mapEntityToStatusDto(reportInstance));
  }
  
  private void mapAdFunctionInstanceCandidate(
      PortfolioEntity portfolio,
      AdFunctionInstanceDto dto) {

    try {
      if (dto.getCandidateJson() != null) {
        AbstractAdFunctionInstanceEntity entity = AbstractAdFunctionInstanceEntity.Mapper.getInstance().mapDtoToEntity(portfolio, dto);
        if (entity != null && !entity.getIsDeleted()) {

          Object object = portfolio.getChildNodeNullIfNotExists(dto.getEquipmentId());
          if (object instanceof EnergyExchangeEntity) {

            EnergyExchangeEntity equipment = (EnergyExchangeEntity)object;
            
            Integer equipmentId = dto.getEquipmentId();
            Integer adFunctionTemplateId = dto.getTemplateId();
            
            AbstractAdFunctionInstanceEntity candidate = equipment.getAdFunctionInstanceCandidateByTemplateIdNullIfNotExists(
                adFunctionTemplateId);
            
            if (candidate != null) {
              LOGGER.error("Ignoring duplicate candidate: Equipment with id: {} already has an AD Function Instance Candidate with id: {}, dto: {}",
                  equipmentId,
                  adFunctionTemplateId,
                  dto);
            } else {
              equipment.addAdFunctionInstanceCandidate(entity);
              portfolio.incrementNumAdFunctionInstanceCandidatesProcessed();
            }
          }
        } else {
          portfolio.addInvalidAdFunctionInstanceCandidate(entity);
        }
      } else {
        LOGGER.error("DTO corresponds to a function instance: {}", dto);
      }
    } catch (Exception e) {
      LOGGER.error("Unable to map: [{}] \n error: {}",
          dto,
          e.getMessage(),
          e);
    }
  }
  
  private void mapAdFunctionInstance(
      PortfolioEntity portfolio,
      AdFunctionInstanceDto dto) {

    try {
      if (dto.getCandidateJson() == null) {
        AbstractAdFunctionInstanceEntity entity = AbstractAdFunctionInstanceEntity.Mapper.getInstance().mapDtoToEntity(portfolio, dto);
        if (entity != null) {

          EnergyExchangeEntity equipment = (EnergyExchangeEntity) portfolio.getChildNode(dto.getEquipmentId());
          equipment.addAdFunctionInstance(entity);
          portfolio.incrementNumAdFunctionInstancesProcessed();
        }
      } else {
        LOGGER.error("DTO corresponds to a function instance candidate: " + dto);
      }
    } catch (Exception e) {
      LOGGER.error("Unable to map: ["
          + dto
          + "\n error: "
          + e.getMessage(), e);
    }
  }
  
  public static void mapAdFunctionInstanceCandidateDto(AbstractAdFunctionInstanceEntity adFunctionInstance, Map<String, Object> dtos) {

    List<AdFunctionInstanceDto> dtoList = getAdFunctionInstanceCandidateDtoList(dtos);

    dtoList.add(AbstractAdFunctionInstanceEntity.Mapper.getInstance().mapEntityToDto(adFunctionInstance));
  }  
  
  public static void mapAdFunctionInstanceDto(AbstractAdFunctionInstanceEntity adFunctionInstance, Map<String, Object> dtos) {

    List<AdFunctionInstanceDto> dtoList = getAdFunctionInstanceDtoList(dtos);

    dtoList.add(AbstractAdFunctionInstanceEntity.Mapper.getInstance().mapEntityToDto(adFunctionInstance));
  }
  
  private void mapAdFunctionErrorMessages(
      PortfolioEntity portfolio,
      AdFunctionErrorMessagesDto dto) {

    try {
      
      EnergyExchangeEntity energyExchangeNode = portfolio.getEnergyExchangeSystemNodeNullIfNotExists(dto.getEnergyExchangeId());
      if (energyExchangeNode != null) {

        AdFunctionErrorMessagesEntity entity = AdFunctionErrorMessagesEntity.Mapper.getInstance().mapDtoToEntity(
            energyExchangeNode, 
            dto);
        
        if (entity != null) {
          energyExchangeNode.addAdFunctionErrorMessages(entity);
          portfolio.incrementNumAdFunctionErrorMessagesProcessed();
        }
      }
    } catch (Exception e) {
      LOGGER.error("Unable to map: ["
          + dto
          + "\n error: "
          + e.getMessage(), e);
    }
  }  
}
//@formatter:on