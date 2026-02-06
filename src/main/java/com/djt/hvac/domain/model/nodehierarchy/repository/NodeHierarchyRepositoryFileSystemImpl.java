//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.repository;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.common.exception.StaleDataException;
import com.djt.hvac.domain.model.common.utils.ObjectMappers;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.customer.repository.CustomerRepository;
import com.djt.hvac.domain.model.dictionary.PaymentPlanEntity;
import com.djt.hvac.domain.model.dictionary.container.AdFunctionTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.energyexchange.AbstractEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.enums.FunctionType;
import com.djt.hvac.domain.model.dictionary.enums.NodeType;
import com.djt.hvac.domain.model.dictionary.repository.DictionaryRepository;
import com.djt.hvac.domain.model.dictionary.template.report.ReportTemplateEntity;
import com.djt.hvac.domain.model.distributor.paymentmethod.AbstractPaymentMethodEntity;
import com.djt.hvac.domain.model.function.AbstractAdFunctionInstanceEntity;
import com.djt.hvac.domain.model.function.dto.AdFunctionErrorMessagesDto;
import com.djt.hvac.domain.model.function.dto.AdFunctionInstanceDto;
import com.djt.hvac.domain.model.function.status.AdFunctionErrorMessageSearchCriteria;
import com.djt.hvac.domain.model.function.status.AdFunctionErrorMessagesValueObject;
import com.djt.hvac.domain.model.nodehierarchy.AbstractNodeEntity;
import com.djt.hvac.domain.model.nodehierarchy.FloorEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.SubBuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BillableBuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingSubscriptionEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingTemporalConfigEntity;
import com.djt.hvac.domain.model.nodehierarchy.dto.AddNodeDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.AsyncComputedPointNodeDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.CustomAsyncComputedPointNodeDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.EnergyExchangeSystemEdgeDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.MappablePointNodeDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.NodeTagDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.NonPointNodeDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.ScheduledAsyncComputedPointNodeDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.TagInfo;
import com.djt.hvac.domain.model.nodehierarchy.dto.custompoint.AsyncPoint;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EnergyExchangeEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EquipmentEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.LoopEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.PlantEntity;
import com.djt.hvac.domain.model.nodehierarchy.event.NodeHierarchyChangeEvent;
import com.djt.hvac.domain.model.nodehierarchy.event.dto.NodeHierarchyChangeEventDto;
import com.djt.hvac.domain.model.nodehierarchy.mapper.PortfolioDtoMapper;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.AsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.ScheduledAsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.CustomAsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.TemporalAsyncComputedPointConfigEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.mapped.MappablePointEntity;
import com.djt.hvac.domain.model.nodehierarchy.service.command.NodeHierarchyCommandRequest;
import com.djt.hvac.domain.model.nodehierarchy.utils.LoadPortfolioOptions;
import com.djt.hvac.domain.model.rawpoint.dto.RawPointDto;
import com.djt.hvac.domain.model.rawpoint.repository.RawPointRepository;
import com.djt.hvac.domain.model.report.ReportInstanceEntity;
import com.djt.hvac.domain.model.report.ReportInstanceEquipmentEntity;
import com.djt.hvac.domain.model.report.ReportInstanceEquipmentErrorMessagesEntity;
import com.djt.hvac.domain.model.report.dto.ReportInstanceDto;
import com.djt.hvac.domain.model.report.dto.ReportInstanceStatusDto;
import com.djt.hvac.domain.model.report.status.BuildingReportStatusValueObject;
import com.djt.hvac.domain.model.report.status.PortfolioReportSummaryValueObject;
import com.djt.hvac.domain.model.report.status.ReportEquipmentErrorMessageSearchCriteria;
import com.djt.hvac.domain.model.report.status.ReportEquipmentErrorMessageValueObject;
import com.djt.hvac.domain.model.report.status.ReportStatusValueObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectWriter;

public class NodeHierarchyRepositoryFileSystemImpl extends AbstractNodeHierarchyRepository {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(NodeHierarchyRepositoryFileSystemImpl.class);

  private static Integer MAX_PERSISTENT_IDENTITY_VALUE = Integer.valueOf(0);
  synchronized private static Integer getNextPersistentIdentityValue() {
    
    Integer nextValue = Integer.valueOf((MAX_PERSISTENT_IDENTITY_VALUE.intValue()-1));
    MAX_PERSISTENT_IDENTITY_VALUE = nextValue;
    return nextValue;
  }

  private static Integer AD_INSTANCE_MAX_PERSISTENT_IDENTITY_VALUE = Integer.valueOf(0);
  synchronized private static Integer getNextAdInstancePersistentIdentityValue() {
    
    Integer nextValue = Integer.valueOf((AD_INSTANCE_MAX_PERSISTENT_IDENTITY_VALUE.intValue()-1));
    AD_INSTANCE_MAX_PERSISTENT_IDENTITY_VALUE = nextValue;
    return nextValue;
  }

  private static Integer AD_CANDIDATE_MAX_PERSISTENT_IDENTITY_VALUE = Integer.valueOf(0);
  synchronized private static Integer getNextAdCandidatePersistentIdentityValue() {
    
    Integer nextValue = Integer.valueOf((AD_CANDIDATE_MAX_PERSISTENT_IDENTITY_VALUE.intValue()-1));
    AD_CANDIDATE_MAX_PERSISTENT_IDENTITY_VALUE = nextValue;
    return nextValue;
  }
  
  private static Integer AD_REPORT_INSTANCE_MAX_PERSISTENT_IDENTITY_VALUE = Integer.valueOf(0);
  synchronized private static Integer getNextReportInstancePersistentIdentityValue() {
    
    Integer nextValue = Integer.valueOf((AD_REPORT_INSTANCE_MAX_PERSISTENT_IDENTITY_VALUE.intValue()-1));
    AD_REPORT_INSTANCE_MAX_PERSISTENT_IDENTITY_VALUE = nextValue;
    return nextValue;
  }

  private static Integer BUILDING_TEMPORAL_MAX_PERSISTENT_IDENTITY_VALUE = Integer.valueOf(0);
  synchronized private static Integer getBuildingTemporalPersistentIdentityValue() {
    
    Integer nextValue = Integer.valueOf((BUILDING_TEMPORAL_MAX_PERSISTENT_IDENTITY_VALUE.intValue()-1));
    BUILDING_TEMPORAL_MAX_PERSISTENT_IDENTITY_VALUE = nextValue;
    return nextValue;
  }
  
  private static Integer CUSTOM_ASYNC_POINT_TEMPORAL_MAX_PERSISTENT_IDENTITY_VALUE = Integer.valueOf(0);
  synchronized private static Integer getCustomAsyncPointTemporalPersistentIdentityValue() {
    
    Integer nextValue = Integer.valueOf((CUSTOM_ASYNC_POINT_TEMPORAL_MAX_PERSISTENT_IDENTITY_VALUE.intValue()-1));
    CUSTOM_ASYNC_POINT_TEMPORAL_MAX_PERSISTENT_IDENTITY_VALUE = nextValue;
    return nextValue;
  }
  
  private static boolean USE_PRETTY_PRINT = false;
  public static boolean getPrettyPrint() {
    return USE_PRETTY_PRINT;
  }
  public static void setPrettyPrint(boolean prettyPrint) {
    USE_PRETTY_PRINT = prettyPrint;
  }
  
  private static final ThreadLocal<ObjectWriter> OBJECT_WRITER = new ThreadLocal<ObjectWriter>() {
    
    @Override
    protected ObjectWriter initialValue() {
      return ObjectMappers.create().writer();
    }
  };

  private static final ThreadLocal<ObjectWriter> OBJECT_WRITER_WITH_PRETTY_PRINTER = new ThreadLocal<ObjectWriter>() {
    
    @Override
    protected ObjectWriter initialValue() {
      return ObjectMappers.create().writerWithDefaultPrettyPrinter();
    }
  };
  
  private static final ObjectWriter getObjectWriter() {
    if  (USE_PRETTY_PRINT) {
      return OBJECT_WRITER_WITH_PRETTY_PRINTER.get(); 
    }
    return OBJECT_WRITER.get();
  }
  
  private String basePath;

  public NodeHierarchyRepositoryFileSystemImpl(
      RawPointRepository rawPointRepository,
      CustomerRepository customerRepository,
      DictionaryRepository dictionaryRepository) {
    this(
        null,
        rawPointRepository,
        customerRepository,
        dictionaryRepository);
  }

  public NodeHierarchyRepositoryFileSystemImpl(
      String basePath,
      RawPointRepository rawPointRepository,
      CustomerRepository customerRepository,
      DictionaryRepository dictionaryRepository) {
    super(
	rawPointRepository,
        customerRepository,
        dictionaryRepository);
    
    if (basePath != null) {
      this.basePath = basePath;
    } else {
      this.basePath = System.getProperty("user.home") + "/";      
    }
    
  }
  
  public String basePath() {
    return basePath;
  }
  
  public void setBasePath(String basePath) {
    this.basePath = basePath;
  }
  
  @Override
  public PortfolioEntity loadPortfolio(
      LoadPortfolioOptions loadPortfolioOptions) 
  throws 
      EntityDoesNotExistException {
  
    int customerId = loadPortfolioOptions.getCustomerId();
    
    boolean loadDistributorPaymentMethods = loadPortfolioOptions.getLoadDistributorPaymentMethods();
    boolean loadDistributorUsers = loadPortfolioOptions.getLoadDistributorUsers();
    
    NodeType filterNodeType = loadPortfolioOptions.getFilterNodeType();
    List<Integer> filterNodePersistentIdentities = loadPortfolioOptions.getFilterNodePersistentIdentities();
    NodeType depthNodeType = loadPortfolioOptions.getDepthNodeType();
    boolean loadAdFunctionInstances = loadPortfolioOptions.getLoadAdFunctionInstances();
    boolean loadReportInstances = loadPortfolioOptions.getLoadReportInstances();
    boolean loadPointLastValues = loadPortfolioOptions.getLoadPointLastValues();
    boolean loadBuildingTemporalData = loadPortfolioOptions.getLoadBuildingTemporalData();
    boolean loadCustomPointTemporalData = true;
    
    if (depthNodeType != null) {
      
      if (loadAdFunctionInstances || loadPointLastValues) {
        
        throw new IllegalArgumentException("When specifying depthNodeType: ["
            + depthNodeType.getName()
            + "], cannot specify loadAdFunctionInstances or loadPointLastValues as true");
      }
      
      if (depthNodeType.equals(NodeType.PORTFOLIO) && filterNodeType != null) {
        
        throw new IllegalArgumentException("Cannot specify filterNodeType: ["
            + filterNodeType
            + "] when a depthNodeType of PORTFOLIO is specified.");
      }
      
      if (filterNodeType != null && filterNodeType.getId() < depthNodeType.getId()) {
        
        if (filterNodeType.equals(NodeType.PORTFOLIO)) {
          throw new IllegalArgumentException("Specified filterNodeType: ["
              + filterNodeType.getName()
              + "] is invalid.  Only BUILDING, SUB_BUILDING, FLOOR, EQUIPMENT and POINT are supported.");
        }
        
        if (filterNodeType.getId() > depthNodeType.getId()) {
          
          throw new IllegalArgumentException("When specifying filterNodeType: ["
              + filterNodeType.getName()
              + "], cannot specify a depth node type of: ["
              + depthNodeType.getName()
              + "], as it is at a greater node depth.");
        }
      }
    }
    
    AbstractCustomerEntity parentCustomer = customerRepository.loadCustomer(
        customerId, 
        loadDistributorPaymentMethods, 
        loadDistributorUsers);
    
    parentCustomer.filterNodeType = filterNodeType;
    parentCustomer.filterNodePersistentIdentities = filterNodePersistentIdentities;
    parentCustomer.loadAdFunctionInstances = loadAdFunctionInstances;
    parentCustomer.loadReportInstances = loadReportInstances;
    parentCustomer.loadPointLastValues = loadPointLastValues;
    parentCustomer.loadBuildingTemporalData = loadBuildingTemporalData;
    parentCustomer.loadCustomPointTemporalData = loadCustomPointTemporalData;
    parentCustomer.depthNodeType = depthNodeType;
    
    List<RawPointDto> rawPointDtoList = loadRawPointDtoList(customerId);
    List<NonPointNodeDto> nonPointNodeDtoList = loadNonPointNodeDtoList(customerId);
    List<MappablePointNodeDto> mappablePointNodeDtoList = loadMappablePointNodeDtoList(customerId);
    List<CustomAsyncComputedPointNodeDto> customAsyncComputedPointNodeDtoList = loadCustomAsyncComputedPointNodeDtoList(customerId);
    List<ScheduledAsyncComputedPointNodeDto> scheduledAsyncComputedPointNodeDtoList = loadScheduledAsyncComputedPointNodeDtoList(customerId);
    List<AsyncComputedPointNodeDto> asyncComputedPointNodeDtoList = loadAsyncComputedPointNodeDtoList(customerId);
    List<NodeTagDto> nodeTagDtoList = loadNodeTagDtoList(customerId);
    List<EnergyExchangeSystemEdgeDto> energyExchangeSystemEdgeDtoList = loadEnergyExchangeSystemEdgeDtoList(customerId);
    
    List<ReportInstanceDto> reportInstanceDtoList = null;
    List<ReportInstanceStatusDto> reportInstanceStatusDtoList = null;
    if (loadReportInstances) {
      reportInstanceDtoList = loadReportInstanceDtoList(customerId);
      reportInstanceStatusDtoList = loadReportInstanceStatusDtoList(customerId);
    } else {
      reportInstanceDtoList = new ArrayList<>();
      reportInstanceStatusDtoList = new ArrayList<>();
    }
    
    List<AdFunctionInstanceDto> adFunctionInstanceCandidateDtoList = null;
    List<AdFunctionInstanceDto> adFunctionInstanceDtoList = null;
    List<AdFunctionErrorMessagesDto> adFunctionErrorMessages = null;
    if (loadAdFunctionInstances) {
      adFunctionInstanceCandidateDtoList = loadAdFunctionInstanceCandidateDtoList(customerId);
      adFunctionInstanceDtoList = loadAdFunctionInstanceDtoList(customerId);
      adFunctionErrorMessages = loadAdFunctionErrorMessagesDtoList(customerId);
    } else {
      adFunctionInstanceCandidateDtoList = new ArrayList<>();
      adFunctionInstanceDtoList = new ArrayList<>();
      adFunctionErrorMessages = new ArrayList<>();
    }
    
    PortfolioEntity portfolio = null;
    try {
      portfolio = PortfolioEntity.mapFromDtos(
          parentCustomer,
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
    } catch (Exception e) {
      throw new IllegalStateException("Unable to load portfolio for customer with id: "
          + customerId 
          + "] due to mapping error: " 
          + e.getMessage(), e);
    }
    
    portfolio.resetAllIsModified();
    
    parentCustomer.filterNodeType = filterNodeType;
    parentCustomer.filterNodePersistentIdentities = filterNodePersistentIdentities;
    parentCustomer.loadAdFunctionInstances = loadAdFunctionInstances;
    parentCustomer.loadReportInstances = loadReportInstances;
    parentCustomer.loadPointLastValues = loadPointLastValues;
    parentCustomer.loadBuildingTemporalData = loadBuildingTemporalData;
    parentCustomer.depthNodeType = depthNodeType;
    
    return portfolio;

  }
  
  @Override
  public Timestamp getPortfolioNodeUpdatedAt(int customerId) {
    
    try {
      
      PortfolioEntity portfolio = loadPortfolio(
          LoadPortfolioOptions
          .builder()
          .withCustomerId(customerId)
          .withDepthNodeType(NodeType.BUILDING)
          .build());
      
      return portfolio.getUpdatedAt();
      
    } catch (EntityDoesNotExistException e) {
      throw new RuntimeException("Could not load portfolio, as it could not be found for customerId: " + customerId);
    }
  }
  
  @Override
  public PortfolioReportSummaryValueObject getReportConfigurationStatus(
      int customerId,
      boolean noInternalReports,
      String rubyTimezoneLabel)
  throws
      EntityDoesNotExistException {

    long start = System.currentTimeMillis();
    
    // Load all buildings.
    // The report instances are loaded, but only with green/red equipment counts, 
    // (i.e. child entities are not instantiated), as that is all we need for this use case.
    LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
        .builder()
        .withCustomerId(customerId)
        .withDepthNodeType(NodeType.BUILDING)
        .build();
    
    PortfolioEntity portfolio = loadPortfolio(loadPortfolioOptions);
    
    // For each building in the given portfolio.
    List<BuildingReportStatusValueObject> buildingReportStatuses = new ArrayList<>();
    for (BuildingEntity building: portfolio.getChildBuildings()) {

      String buildingName = building.getDisplayName();
      Integer buildingId = building.getPersistentIdentity();
      String timezone = building.getTimezone();
      
      // For each report in the given building.
      List<ReportStatusValueObject> reports = new ArrayList<>();
      int numGreenReports = 0;
      int numYellowReports = 0;
      int numRedReports = 0;
      for (ReportInstanceEntity reportInstance: building.getReportInstances()) {

        ReportTemplateEntity reportTemplate = reportInstance.getReportTemplate();
        
        // If the 'no internal reports' flag is set, then do not include internal reports.
        if (!noInternalReports || (noInternalReports && !reportTemplate.getIsInternal())) {

          Integer reportTemplateId = reportTemplate.getPersistentIdentity();
          String reportTemplateName = reportTemplate.getName();
          String reportTemplateDescription = reportTemplate.getDescription();
          String lastUpdated = AbstractEntity.toDisplayFormattedZonedTime(reportInstance.getLastEvaluationTime(), timezone);
          Integer numGreenEquipment = reportInstance.getNumEquipmentInGreenStatus();
          Integer numEquipmentTotal = reportInstance.getNumEquipmentTotal();
          String status = reportInstance.getStatus();
          if (status.equals(ReportInstanceEntity.STATUS_GREEN)) {
            numGreenReports++;
          } else if (status.equals(ReportInstanceEntity.STATUS_YELLOW)) {
            numYellowReports++;
          } else {
            numRedReports++;
          }
          boolean isEnabled = reportInstance.isEnabled();
          boolean isValid = reportInstance.isValid();
          boolean isIgnored = reportInstance.isIgnored();
          String priority = reportInstance.getPriority().toString();
          
          ReportStatusValueObject report = ReportStatusValueObject
              .builder()
              .withReportTemplateId(reportTemplateId)
              .withReportTemplateName(reportTemplateName)
              .withReportTemplateDescription(reportTemplateDescription)
              .withNumGreenEquipment(numGreenEquipment)
              .withNumEquipmentTotal(numEquipmentTotal)
              .withStatus(status)
              .withIsEnabled(isEnabled)
              .withIsValid(isValid)
              .withIsIgnored(isIgnored)
              .withLastUpdated(lastUpdated)
              .withPriority(priority)
              .build();          
         
          reports.add(report);
        }
      }
      
      // For each building, we aggregate the number of reports in GREEN, YELLOW or RED status.
      BuildingReportStatusValueObject buildingReportStatus = BuildingReportStatusValueObject
          .builder()
          .withBuildingId(buildingId)
          .withBuildingName(buildingName)
          .withNumGreen(numGreenReports)
          .withNumYellow(numYellowReports)
          .withNumRed(numRedReports)
          .withReports(reports)
          .build();
      buildingReportStatuses.add(buildingReportStatus);
    }
    
    Collections.sort(buildingReportStatuses);
    PortfolioReportSummaryValueObject response = PortfolioReportSummaryValueObject
        .builder()
        .withBuildingReportStatuses(buildingReportStatuses)
        .build();

    LOGGER.info("getReportConfigurationStatus(): elapsed(ms): {}",
        (System.currentTimeMillis()-start));
    
    return response;    
  }
  
  @Override
  public int getReportEquipmentErrorMessagesCount(
      int customerId,
      ReportEquipmentErrorMessageSearchCriteria searchCriteria) {
    
    throw new IllegalStateException("This operation is not supported for this implementation.");
  }
  
  @Override
  public List<ReportEquipmentErrorMessageValueObject> getReportEquipmentErrorMessages(
      int customerId,
      ReportEquipmentErrorMessageSearchCriteria searchCriteria) {
    
    throw new IllegalStateException("This operation is not supported for this implementation.");
  }
  
  @Override
  public PortfolioEntity createPortfolio(
      AbstractCustomerEntity parentCustomer,
      String name,
      String displayName)
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException {
    
    PortfolioEntity childPortfolio = new PortfolioEntity(
        parentCustomer,
        name,
        displayName);
    
    childPortfolio.setPersistentIdentity(getNextPersistentIdentityValue());
    
    parentCustomer.setChildPortfolio(childPortfolio);

    boolean resetAllIsModified = true;
    boolean resetCreatedLists = true;
    savePortfolioToFileSystem(
        parentCustomer.getPersistentIdentity(),
        resetAllIsModified,
        resetCreatedLists,
        childPortfolio);
    
    return childPortfolio;
  }
  
  @Override
  public Map<String, List<AbstractNodeEntity>> storePortfolio(
      PortfolioEntity portfolio,
      NodeHierarchyCommandRequest commandRequest,
      boolean reportsWereEvaluated) 
  throws 
      StaleDataException {
    
    Map<String, List<AbstractNodeEntity>> nodes = new LinkedHashMap<>();
    
    nodes.put(NodeHierarchyRepository.CREATED, storeCreatedNodes(portfolio));
    nodes.put(NodeHierarchyRepository.UPDATED, storeUpdatedNodes(portfolio));
    nodes.put(NodeHierarchyRepository.DELETED, removeDeletedNodes(portfolio));
    
    return nodes;
  } 
  
  private List<AbstractNodeEntity> storeCreatedNodes(
      PortfolioEntity portfolio)
  throws
      StaleDataException {
    
    List<AbstractNodeEntity> createdNodes = portfolio.getAllCreatedNodes();
    List<AbstractNodeEntity> createdNodesResponse = new ArrayList<>(); 
    if (!createdNodes.isEmpty()) {

      createdNodesResponse.addAll(createdNodes);
      AbstractCustomerEntity customer = portfolio.getParentCustomer();
      /*
      try {
        
        // STALE DATA CHECK ON ROOT PORTFOLIO NODE
        Timestamp entityUpdatedAtTimestamp = portfolio.getUpdatedAt();
        LocalDateTime entityUpdatedAt = null;
        if (entityUpdatedAtTimestamp != null) {
          entityUpdatedAt = entityUpdatedAtTimestamp.toLocalDateTime().truncatedTo(ChronoUnit.SECONDS);
        }
        
        PortfolioEntity p = loadPortfolio(customer.getPersistentIdentity());
        Timestamp databaseUpdatedAtTimestamp = p.getUpdatedAt();
        LocalDateTime databaseUpdatedAt = null;
        if (databaseUpdatedAtTimestamp != null) {
          databaseUpdatedAt = databaseUpdatedAtTimestamp.toLocalDateTime().truncatedTo(ChronoUnit.SECONDS);
        }
        if (entityUpdatedAt != null && databaseUpdatedAt != null && !databaseUpdatedAt.equals(entityUpdatedAt)) {
          
          throw new IllegalStateException("STALE DATA: Portfolio: ["
              + portfolio
              + "] to update is stale, as its updated at time: ["
              + entityUpdatedAt
              + "] does not match what is stored in the database: ["
              + databaseUpdatedAt
              + "]. Please reload and try again.");
        }
        portfolio.setUpdatedAt();
        
      } catch (EntityDoesNotExistException ednee) {
        LOGGER.error("Unable to load portfolio for customer: [{}] for stale data checking",
            customer,
            ednee);
      }
      */
      
      // Assign PK values to newly instantiated nodes.
      for (AbstractNodeEntity node: createdNodesResponse) {
        node.setPersistentIdentity(getNextPersistentIdentityValue());
      }
      
      // Assign PK values to newly instantiated AD function candidates.
      for (AbstractAdFunctionInstanceEntity entity: portfolio.getAllAdFunctionInstanceCandidates()) {
        if (entity.getPersistentIdentity() == null) {
          entity.setPersistentIdentity(getNextAdCandidatePersistentIdentityValue());  
        }
      }

      // Assign PK values to newly instantiated AD function instances.
      for (AbstractAdFunctionInstanceEntity entity: portfolio.getAllAdFunctionInstances()) {
        if (entity.getPersistentIdentity() == null) {
          entity.setPersistentIdentity(getNextAdInstancePersistentIdentityValue());
        }
      }

      // Assign PK values to newly instantiated building temporal configs.
      for (BuildingEntity entity: portfolio.getChildBuildings()) {
        for (BuildingTemporalConfigEntity childTemporalConfig: entity.getChildTemporalConfigs()) {
          childTemporalConfig.setPersistentIdentity(getBuildingTemporalPersistentIdentityValue());
        }
      }

      // Assign PK values to newly instantiated custom async point temporal configs.
      for (CustomAsyncComputedPointEntity entity: portfolio.getAllCustomAsyncComputedPoints()) {
        for (TemporalAsyncComputedPointConfigEntity childTemporalConfig: entity.getChildTemporalConfigs()) {
          childTemporalConfig.setPersistentIdentity(getCustomAsyncPointTemporalPersistentIdentityValue());
        }
      }
      
      boolean resetAllIsModified = false;
      boolean resetCreatedLists = true;
      savePortfolioToFileSystem(
          customer.getPersistentIdentity(),
          resetAllIsModified,
          resetCreatedLists,
          portfolio);
      
      portfolio.addCreatedNodesToNodeIndex(createdNodesResponse);
      
      portfolio.resetCreatedLists();
    }
    
    return createdNodesResponse;
  }
  
  private List<AbstractNodeEntity> storeUpdatedNodes(
      PortfolioEntity portfolio)
  throws 
      StaleDataException {
    
    List<AbstractNodeEntity> updatedNodes = portfolio.getAllModifiedNodes();
    List<AbstractNodeEntity> updatedNodesResponse = new ArrayList<>();
    if (!updatedNodes.isEmpty()) {

      updatedNodesResponse.addAll(updatedNodes);
      
      Map<String, Object> dtos = PortfolioEntity.mapToDtos(portfolio);
      
      List<NonPointNodeDto> nonPointNodeDtoList = PortfolioDtoMapper.getNonPointNodeDtoList(dtos);
      List<MappablePointNodeDto> mappablePointNodeDtoList = PortfolioDtoMapper.getMappablePointNodeDtoList(dtos);
      List<ScheduledAsyncComputedPointNodeDto> scheduledAsyncComputedPointNodeDtoList = PortfolioDtoMapper.getScheduledAsyncComputedPointNodeDtoList(dtos);
      List<AsyncComputedPointNodeDto> asyncComputedPointNodeDtoList = PortfolioDtoMapper.getAsyncComputedPointNodeDtoList(dtos);
      List<AdFunctionInstanceDto> adFunctionInstanceCandidateDtoList = PortfolioDtoMapper.getAdFunctionInstanceCandidateDtoList(dtos);
      List<AdFunctionInstanceDto> adFunctionInstanceDtoList = PortfolioDtoMapper.getAdFunctionInstanceDtoList(dtos);
      List<NodeTagDto> nodeTagDtoList = PortfolioDtoMapper.getNodeTagDtoList(dtos);
      Map<Integer, NodeTagDto> nodeTagDtoMap = new HashMap<>();
      for (NodeTagDto dto: nodeTagDtoList) {
        nodeTagDtoMap.put(dto.getId(), dto);
      }
      
      // ONLY DEAL WITH UPDATED ENTITIES
      Set<Integer> deletedNodeIds = new HashSet<>();
      Set<Integer> updatedNonPointNodeIds = new HashSet<>();
      Set<Integer> updatedMappablePointNodeIds = new HashSet<>();
      Set<Integer> updatedScheduledAsyncComputedPointNodeIds = new HashSet<>();
      Set<Integer> updatedAsyncComputedPointNodeIds = new HashSet<>();
      Set<Integer> deletedNodeTagNodeIds = new HashSet<>();
      Set<Integer> deletedAdFunctionInstanceCandidateIds = new HashSet<>();
      Set<Integer> updatedAdFunctionInstanceIds = new HashSet<>();
      Set<Integer> deletedAdFunctionInstanceIds = new HashSet<>();
      for (AbstractNodeEntity node: updatedNodes) {
        
        if (node instanceof BuildingEntity) {

          updatedNonPointNodeIds.add(node.getPersistentIdentity());
          deletedNodeIds.addAll(node.getDeletedChildNodes());

        } else if (node instanceof SubBuildingEntity) {

          updatedNonPointNodeIds.add(node.getPersistentIdentity());
          deletedNodeIds.addAll(node.getDeletedChildNodes());
          
        } else if (node instanceof FloorEntity) {

          updatedNonPointNodeIds.add(node.getPersistentIdentity());
          deletedNodeIds.addAll(node.getDeletedChildNodes());
          
        } else if (node instanceof EquipmentEntity) {
          
          EquipmentEntity equipment = (EquipmentEntity)node;
          
          Integer equipmentId = equipment.getPersistentIdentity();
          
          updatedNonPointNodeIds.add(equipmentId);
          deletedNodeIds.addAll(node.getDeletedChildNodes());
          
          deletedAdFunctionInstanceCandidateIds.addAll(equipment.getDeletedAdFunctionInstanceCandidateIds());

          deletedAdFunctionInstanceIds.addAll(equipment.getDeletedAdFunctionInstanceIds());
          portfolio.addNewlyDisabledAdFunctionInstanceIds(equipment.getDeletedAdFunctionInstanceIds());
          
          //addedEnergyExchangeSystemEdgeDtoList.addAll(equipment.getAddedEnergyExchangeSystemEdges());
          
          //removedEnergyExchangeSystemEdgeDtoList.addAll(equipment.getRemovedEnergyExchangeSystemEdges());
          
          //equipment.getAddedAdFunctionErrorMessages(addedAdFunctionErrorMessages);
          
          //equipment.getRemovedAdFunctionErrorMessages(removedAdFunctionErrorMessages);
          
        } else if (node instanceof PlantEntity) {
          
          PlantEntity plant = (PlantEntity)node;
          
          Integer plantId = plant.getPersistentIdentity();
          
          updatedNonPointNodeIds.add(plantId);
          deletedNodeIds.addAll(node.getDeletedChildNodes());
          
          deletedAdFunctionInstanceCandidateIds.addAll(plant.getDeletedAdFunctionInstanceCandidateIds());

          deletedAdFunctionInstanceIds.addAll(plant.getDeletedAdFunctionInstanceIds());
          portfolio.addNewlyDisabledAdFunctionInstanceIds(plant.getDeletedAdFunctionInstanceIds());

          //addedEnergyExchangeSystemEdgeDtoList.addAll(plant.getAddedEnergyExchangeSystemEdges());
          
          //removedEnergyExchangeSystemEdgeDtoList.addAll(plant.getRemovedEnergyExchangeSystemEdges());
          
          //plant.getAddedAdFunctionErrorMessages(addedAdFunctionErrorMessages);
          
          //plant.getRemovedAdFunctionErrorMessages(removedAdFunctionErrorMessages);
          
        } else if (node instanceof LoopEntity) {
          
          LoopEntity loop = (LoopEntity)node;
          
          Integer loopId = loop.getPersistentIdentity();
          
          updatedNonPointNodeIds.add(loopId);
          deletedNodeIds.addAll(node.getDeletedChildNodes());
          
          deletedAdFunctionInstanceCandidateIds.addAll(loop.getDeletedAdFunctionInstanceCandidateIds());

          deletedAdFunctionInstanceIds.addAll(loop.getDeletedAdFunctionInstanceIds());
          portfolio.addNewlyDisabledAdFunctionInstanceIds(loop.getDeletedAdFunctionInstanceIds());

          //addedEnergyExchangeSystemEdgeDtoList.addAll(loop.getAddedEnergyExchangeSystemEdges());
          
          //removedEnergyExchangeSystemEdgeDtoList.addAll(loop.getRemovedEnergyExchangeSystemEdges());
          
          //loop.getAddedAdFunctionErrorMessages(addedAdFunctionErrorMessages);
          
          //loop.getRemovedAdFunctionErrorMessages(removedAdFunctionErrorMessages);
          
        } else if (node instanceof MappablePointEntity) {
          
          updatedMappablePointNodeIds.add(node.getPersistentIdentity());
          
        } else if (node instanceof ScheduledAsyncComputedPointEntity) {

          updatedScheduledAsyncComputedPointNodeIds.add(node.getPersistentIdentity());
          
        } else if (node instanceof AsyncComputedPointEntity) {

          updatedAsyncComputedPointNodeIds.add(node.getPersistentIdentity());
        }
      }
      
      // SEGREGATE THE NON POINT NODES BY TYPE
      List<NonPointNodeDto> updatedBuildingDtoList = new ArrayList<>();
      List<NonPointNodeDto> updatedSubBuildingDtoList = new ArrayList<>();
      List<NonPointNodeDto> updatedFloorDtoList = new ArrayList<>();
      List<NonPointNodeDto> updatedEquipmentDtoList = new ArrayList<>();
      List<NonPointNodeDto> updatedPlantDtoList = new ArrayList<>();
      List<NonPointNodeDto> updatedLoopDtoList = new ArrayList<>();
      List<MappablePointNodeDto> updatedMappablePointNodeDtoList = new ArrayList<>();
      List<ScheduledAsyncComputedPointNodeDto> updatedScheduledAsyncComputedPointNodeDtoList = new ArrayList<>();
      List<AsyncComputedPointNodeDto> updatedAsyncComputedPointNodeDtoList = new ArrayList<>();
      Set<NodeTagDto> updatedNodeTagDtoList = new HashSet<>();
      List<AdFunctionInstanceDto> createdAdFunctionInstanceCandidateDtoList = new ArrayList<>();
      List<AdFunctionInstanceDto> createdAdFunctionInstanceDtoList = new ArrayList<>();
      List<AdFunctionInstanceDto> updatedAdFunctionInstanceDtoList = new ArrayList<>();
      List<AdFunctionInstanceDto> deletedAdFunctionInstanceDtoList = new ArrayList<>();
      
      for (Integer id: deletedNodeIds) {
        deletedNodeTagNodeIds.add(id);
      }
      
      for (NonPointNodeDto dto: nonPointNodeDtoList) {
        
        Integer nodeTypeId = dto.getNodeTypeId();
        Integer id = dto.getNodeId();
        if (nodeTypeId == NodeType.BUILDING.getId() && updatedNonPointNodeIds.contains(id)) {
          
          updatedBuildingDtoList.add(dto);
          NodeTagDto nodeTagDto = nodeTagDtoMap.get(id);
          if (nodeTagDto != null) {
            updatedNodeTagDtoList.add(nodeTagDto);
          } else {
            deletedNodeTagNodeIds.add(id);
          }
          
        } else if (nodeTypeId == NodeType.SUB_BUILDING.getId() && updatedNonPointNodeIds.contains(id)) {
          
          updatedSubBuildingDtoList.add(dto);
          NodeTagDto nodeTagDto = nodeTagDtoMap.get(id);
          if (nodeTagDto != null) {
            updatedNodeTagDtoList.add(nodeTagDto);
          } else {
            deletedNodeTagNodeIds.add(id);
          }

        } else if (nodeTypeId == NodeType.FLOOR.getId() && updatedNonPointNodeIds.contains(id)) {
          
          updatedFloorDtoList.add(dto);
          NodeTagDto nodeTagDto = nodeTagDtoMap.get(id);
          if (nodeTagDto != null) {
            updatedNodeTagDtoList.add(nodeTagDto);
          } else {
            deletedNodeTagNodeIds.add(id);
          }

        } else if (nodeTypeId == NodeType.EQUIPMENT.getId() && updatedNonPointNodeIds.contains(id)) {
          
          updatedEquipmentDtoList.add(dto);
          NodeTagDto nodeTagDto = nodeTagDtoMap.get(id);
          if (nodeTagDto != null) {
            updatedNodeTagDtoList.add(nodeTagDto);
          } else {
            deletedNodeTagNodeIds.add(id);
          }

        } else if (nodeTypeId == NodeType.PLANT.getId() && updatedNonPointNodeIds.contains(id)) {
          
          updatedPlantDtoList.add(dto);
          NodeTagDto nodeTagDto = nodeTagDtoMap.get(id);
          if (nodeTagDto != null) {
            updatedNodeTagDtoList.add(nodeTagDto);
          } else {
            deletedNodeTagNodeIds.add(id);
          }

        } else if (nodeTypeId == NodeType.LOOP.getId() && updatedNonPointNodeIds.contains(id)) {
          
          updatedLoopDtoList.add(dto);
          NodeTagDto nodeTagDto = nodeTagDtoMap.get(id);
          if (nodeTagDto != null) {
            updatedNodeTagDtoList.add(nodeTagDto);
          } else {
            deletedNodeTagNodeIds.add(id);
          }
          
        }
      }

      for (MappablePointNodeDto dto: mappablePointNodeDtoList) {
        
        Integer id = dto.getNodeId();
        if (updatedMappablePointNodeIds.contains(id)) {
          
          updatedMappablePointNodeDtoList.add(dto);
          NodeTagDto nodeTagDto = nodeTagDtoMap.get(id);
          if (nodeTagDto != null) {
            updatedNodeTagDtoList.add(nodeTagDto);
          } else {
            deletedNodeTagNodeIds.add(id);
          }
          
        }
      }

      for (ScheduledAsyncComputedPointNodeDto dto: scheduledAsyncComputedPointNodeDtoList) {
        
        Integer id = dto.getNodeId();
        if (updatedScheduledAsyncComputedPointNodeIds.contains(id)) {
          
          updatedScheduledAsyncComputedPointNodeDtoList.add(dto);
          NodeTagDto nodeTagDto = nodeTagDtoMap.get(id);
          if (nodeTagDto != null) {
            updatedNodeTagDtoList.add(nodeTagDto);
          } else {
            deletedNodeTagNodeIds.add(id);
          }
          
        }
      }

      for (AsyncComputedPointNodeDto dto: asyncComputedPointNodeDtoList) {
        
        Integer id = dto.getNodeId();
        if (updatedAsyncComputedPointNodeIds.contains(id)) {
          
          updatedAsyncComputedPointNodeDtoList.add(dto);
          NodeTagDto nodeTagDto = nodeTagDtoMap.get(id);
          if (nodeTagDto != null) {
            updatedNodeTagDtoList.add(nodeTagDto);
          } else {
            deletedNodeTagNodeIds.add(id);
          }
          
        }
      }
      
      for (AdFunctionInstanceDto dto: adFunctionInstanceCandidateDtoList) {
        
        Integer id = dto.getId();
        if (id == null) {
          createdAdFunctionInstanceCandidateDtoList.add(dto);
        } else if (deletedAdFunctionInstanceIds.contains(id)) {
          deletedAdFunctionInstanceDtoList.add(dto);
        } else if (updatedAdFunctionInstanceIds.contains(id)) {
          updatedAdFunctionInstanceDtoList.add(dto);  
        }
      }      
      
      for (AdFunctionInstanceDto dto: adFunctionInstanceDtoList) {
        
        Integer id = dto.getId();
        if (id == null) {
          createdAdFunctionInstanceDtoList.add(dto);
        } else if (deletedAdFunctionInstanceIds.contains(id)) {
          deletedAdFunctionInstanceDtoList.add(dto);
        } else if (updatedAdFunctionInstanceIds.contains(id)) {
          updatedAdFunctionInstanceDtoList.add(dto);  
        }
      } 

      
      
      AbstractCustomerEntity parentCustomer = portfolio.getParentCustomer();
      //int customerId = parentCustomer.getPersistentIdentity().intValue();

      // STALE DATA CHECK ON ROOT PORTFOLIO NODE
      /*
      Timestamp entityUpdatedAtTimestamp = portfolio.getUpdatedAt();
      LocalDateTime entityUpdatedAt = null;
      if (entityUpdatedAtTimestamp != null) {
        entityUpdatedAt = entityUpdatedAtTimestamp.toLocalDateTime().truncatedTo(ChronoUnit.SECONDS);
      }
      
      PortfolioEntity p = null;
      try {
        p = loadPortfolio(customerId);
      } catch (EntityDoesNotExistException e) {
        throw new IllegalStateException("Unable to load portfolio for stale data checking: "
            + e.getMessage(), e);
      }
      Timestamp databaseUpdatedAtTimestamp = p.getUpdatedAt();
      LocalDateTime databaseUpdatedAt = null;
      if (databaseUpdatedAtTimestamp != null) {
        databaseUpdatedAt = databaseUpdatedAtTimestamp.toLocalDateTime().truncatedTo(ChronoUnit.SECONDS);
      }
      if (entityUpdatedAt != null && databaseUpdatedAt != null && !databaseUpdatedAt.equals(entityUpdatedAt)) {
        
        throw new IllegalStateException("STALE DATA: Portfolio: ["
            + portfolio
            + "] to update is stale, as its updated at time: ["
            + entityUpdatedAt
            + "] does not match what is stored in the database: ["
            + databaseUpdatedAt
            + "]. Please reload and try again.");
      }
      */
      
      // ONLY UPDATE REPORTS IF THEY WERE LOADED WITH THE PORTFOLIO.
      if (parentCustomer.loadReportInstances) {
        
        List<ReportInstanceEquipmentEntity> reportInstanceEquipment = new ArrayList<>();
        List<ReportInstanceEquipmentErrorMessagesEntity> reportInstanceEquipmentErrorMessages = new ArrayList<>();

        // Report instances have their information split between:
        // "Enabled" reports with green equipment (instance tables)
        // "Disabled" reports with green equipment in candidate JSON (status tables)
        // Both enabled/disabled red equipment error messages (children of status tables)
        List<ReportInstanceEntity> needsEnablingReportInstances = new ArrayList<>();
        List<ReportInstanceEntity> updatedReportInstances = new ArrayList<>();
        List<ReportInstanceEntity> needsDisablingReportInstances = new ArrayList<>();
        
        List<ReportInstanceEntity> allReportInstances = portfolio.getAllReportInstances();
        for (ReportInstanceEntity reportInstance: allReportInstances) {
          
          if (reportInstance.getNeedsEnabling()) {

            needsEnablingReportInstances.add(reportInstance);
            reportInstanceEquipment.addAll(reportInstance.getReportInstanceEquipment());
            reportInstanceEquipmentErrorMessages.addAll(reportInstance.getReportInstanceEquipmentErrorMessages());

          } else if (reportInstance.getNeedsDisabling()) {
            
            needsDisablingReportInstances.add(reportInstance);
            reportInstanceEquipmentErrorMessages.addAll(reportInstance.getReportInstanceEquipmentErrorMessages());
            
          } else if (reportInstance.getIsModified()) {
            
            updatedReportInstances.add(reportInstance);
            if (reportInstance.isEnabled()) {
              reportInstanceEquipment.addAll(reportInstance.getReportInstanceEquipment());  
            }
            reportInstanceEquipmentErrorMessages.addAll(reportInstance.getReportInstanceEquipmentErrorMessages());
            
          }
        }
        
        
        // ENABLED REPORTS: BASE ROW
        for (ReportInstanceEntity entity: needsEnablingReportInstances) {
          
          ReportInstanceDto instanceDto = ReportInstanceEntity
              .Mapper
              .getInstance()
              .mapEntityToDto(entity);
          LOGGER.info("Enabling Report Instance: " + instanceDto);
          
          int reportInstanceId = getNextReportInstancePersistentIdentityValue();
          entity.setPersistentIdentity(Integer.valueOf(reportInstanceId));
          portfolio.addNewlyCreatedReportInstance(entity);
          entity.resetNeedsEnabling();
        }
        
        
        // UPDATED REPORTS
        for (ReportInstanceEntity entity: updatedReportInstances) {
          
          if (entity.isEnabled()) {
            LOGGER.info("Updating Report Instance: " + entity);
          }
        }

        
        // DISABLED REPORTS
        for (ReportInstanceEntity entity: needsDisablingReportInstances) {
          LOGGER.info("Disabling Report Instance: " + entity);
          //portfolio.addNewlyDisabledReportInstance(entity);
        }
        
        
        // ENABLED REPORTS: GREEN EQUIPMENT
        if (!reportInstanceEquipment.isEmpty()) {
          LOGGER.info("Deleting before upsert for Green Equipment for Enabled Reports: " + reportInstanceEquipment);
          LOGGER.info("Upserting Green Equipment for Enabled Reports: " + reportInstanceEquipment);
        }

        
        // ENABLED/DISABLED REPORTS: BASE STATUS ROW (INCLUDES CANDIDATE JSON FOR DISABLED REPORTS)
        for (ReportInstanceEntity entity: allReportInstances) {
          LOGGER.info("Upserting Base Status for Enabled/Disabled Reports: " + entity);
        }

        
        // ENABLED/DISABLED REPORTS: RED EQUIPMENT ERROR MESSAGES
        for (ReportInstanceEquipmentErrorMessagesEntity entity: reportInstanceEquipmentErrorMessages) {
          
          LOGGER.info("Re-insert, deleting Stale Red Equipment for Enabled/Disabled Reports:" + entity);
          LOGGER.info("Re-Inserting Red Equipment for Enabled/Disabled Reports: " + entity);
        }
        
        for (ReportInstanceEntity reportInstance: allReportInstances) {
          
          reportInstance.resetNeedsEnabling();
          reportInstance.resetNeedsDisabling();
          reportInstance.setNotModified();
        }
      }
      
      // ONLY DEAL WITH AD FUNCTION INSTANCE AND AD INSTANCE CANDIDATES IF THEY WERE LOADED WITH THE PORTFOLIO.
      if (parentCustomer.loadAdFunctionInstances) {

        // CANDIDATES
        if (!deletedAdFunctionInstanceCandidateIds.isEmpty()) {

          LOGGER.info("Deleting AD Function Instance Candidates: " + deletedAdFunctionInstanceCandidateIds);
        }

        if (!createdAdFunctionInstanceCandidateDtoList.isEmpty()) {
          
          LOGGER.info("Creating AD Function Instance Candidates: " + createdAdFunctionInstanceCandidateDtoList);
        }
        
        
        // INSTANCES
        // We don't hard delete AD function instances, rather, we set the "active" flag to false and "effective_end_date" to the current timestamp.
        if (!updatedAdFunctionInstanceDtoList.isEmpty()) {

          LOGGER.info("deactivateAdFunctionInstances: " + updatedAdFunctionInstanceDtoList);
          LOGGER.info("renameDeactivatedAdFunctionInstanceOutputPoints: " + updatedAdFunctionInstanceDtoList);
          
          LOGGER.info("storeAdFunctionInstancesNoIds: " + updatedAdFunctionInstanceDtoList);
        }
        
        if (!deletedAdFunctionInstanceDtoList.isEmpty()) {

          LOGGER.info("deactivateAdFunctionInstances: " + updatedAdFunctionInstanceDtoList);
          LOGGER.info("renameDeactivatedAdFunctionInstanceOutputPoints: " + updatedAdFunctionInstanceDtoList);
        }
        
        if (!createdAdFunctionInstanceDtoList.isEmpty()) {
          
          LOGGER.info("storeAdFunctionInstancesNoIds: " + createdAdFunctionInstanceDtoList);
        }
      }
      
      // SET THE STATE OF THE ENTITIES (JUST IN CASE THE CALLER CARES)
      portfolio.setUpdatedAt();
      for (AbstractNodeEntity node: updatedNodes) {
        
        node.setUpdatedAt();
        node.setNotModified();
      }

      portfolio.resetAllIsModified();
      
      // Assign PK values to newly instantiated AD function candidates.
      for (AbstractAdFunctionInstanceEntity entity: portfolio.getAllAdFunctionInstanceCandidates()) {
        if (entity.getPersistentIdentity() == null) {
          entity.setPersistentIdentity(getNextAdCandidatePersistentIdentityValue());  
        }
      }

      // Assign PK values to newly instantiated AD function instances.
      for (AbstractAdFunctionInstanceEntity entity: portfolio.getAllAdFunctionInstances()) {
        if (entity.getPersistentIdentity() == null) {
          entity.setPersistentIdentity(getNextAdInstancePersistentIdentityValue());
        }
      }

      // Assign PK values to newly instantiated building temporal configs.
      for (BuildingEntity entity: portfolio.getChildBuildings()) {
        for (BuildingTemporalConfigEntity childTemporalConfig: entity.getChildTemporalConfigs()) {
          childTemporalConfig.setPersistentIdentity(getBuildingTemporalPersistentIdentityValue());
        }
      }

      // Assign PK values to newly instantiated custom async point temporal configs.
      for (CustomAsyncComputedPointEntity entity: portfolio.getAllCustomAsyncComputedPoints()) {
        for (TemporalAsyncComputedPointConfigEntity childTemporalConfig: entity.getChildTemporalConfigs()) {
          if (childTemporalConfig.getPersistentIdentity() == null) {
            childTemporalConfig.setPersistentIdentity(getCustomAsyncPointTemporalPersistentIdentityValue());
          }
        }
      }
      
      boolean resetAllIsModified = true;
      boolean resetCreatedLists = false;
      savePortfolioToFileSystem(
          portfolio.getParentCustomer().getPersistentIdentity(),
          resetAllIsModified,
          resetCreatedLists,
          portfolio);      
    }
    
    return updatedNodesResponse;
  }
  
  private List<AbstractNodeEntity> removeDeletedNodes(
      PortfolioEntity portfolio) 
  throws 
      StaleDataException {
  
    List<AbstractNodeEntity> deletedNodes = portfolio.getAllDeletedNodes();
    List<AbstractNodeEntity> deletedNodesResponse = new ArrayList<>();
    if (!deletedNodes.isEmpty()) {
      
      deletedNodesResponse.addAll(deletedNodes);
      AbstractCustomerEntity customer = portfolio.getParentCustomer();
      
      /*
      try {
        
        // STALE DATA CHECK ON ROOT PORTFOLIO NODE
        Timestamp entityUpdatedAtTimestamp = portfolio.getUpdatedAt();
        LocalDateTime entityUpdatedAt = null;
        if (entityUpdatedAtTimestamp != null) {
          entityUpdatedAt = entityUpdatedAtTimestamp.toLocalDateTime().truncatedTo(ChronoUnit.SECONDS);
        }
        
        PortfolioEntity p = loadPortfolio(customer.getPersistentIdentity());
        Timestamp databaseUpdatedAtTimestamp = p.getUpdatedAt();
        LocalDateTime databaseUpdatedAt = null;
        if (databaseUpdatedAtTimestamp != null) {
          databaseUpdatedAt = databaseUpdatedAtTimestamp.toLocalDateTime().truncatedTo(ChronoUnit.SECONDS);
        }
        if (entityUpdatedAt != null && databaseUpdatedAt != null && !databaseUpdatedAt.equals(entityUpdatedAt)) {
          
          throw new IllegalStateException("STALE DATA: Portfolio: ["
              + portfolio
              + "] to update is stale, as its updated at time: ["
              + entityUpdatedAt
              + "] does not match what is stored in the database: ["
              + databaseUpdatedAt
              + "]. Please reload and try again.");
        }
        portfolio.setUpdatedAt();
       
      } catch (EntityDoesNotExistException ednee) {
        LOGGER.error("Unable to load portfolio for customer: ["
            + customer
            + "] for stale data checking", ednee);
      }
      */
      
      List<AbstractNodeEntity> updatedNodes = portfolio.getAllModifiedNodes();
      for (AbstractNodeEntity node: updatedNodes) {
        
        node.setUpdatedAt();
        node.setNotModified();
      }
      
      boolean resetAllIsModified = false;
      boolean resetCreatedLists = false;
      savePortfolioToFileSystem(
          customer.getPersistentIdentity(),
          resetAllIsModified,
          resetCreatedLists,
          portfolio);
    }
    
    return deletedNodesResponse;
  }
  
  @Override
  public BuildingSubscriptionEntity createBuildingSubscription(
      BillableBuildingEntity parentBuilding,
      PaymentPlanEntity parentPaymentPlan,
      AbstractPaymentMethodEntity parentPaymentMethod,
      String stripeSubscriptionId) 
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException {
    
    BuildingSubscriptionEntity buildingSubscription = new BuildingSubscriptionEntity(
        parentBuilding.getPersistentIdentity(),
        parentBuilding,
        parentPaymentPlan,
        parentPaymentMethod,
        stripeSubscriptionId,
        AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
        AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
        null,
        null);
    
    parentBuilding.setChildBuildingSubscription(buildingSubscription);
    
    PortfolioEntity portfolio = parentBuilding.getRootPortfolioNode();
    
    List<AbstractNodeEntity> updatedNodes = portfolio.getAllModifiedNodes();
    for (AbstractNodeEntity node: updatedNodes) {
      
      node.setUpdatedAt();
      node.setNotModified();
    }
    
    AbstractCustomerEntity customer = portfolio.getParentCustomer();
    
    boolean resetAllIsModified = true;
    boolean resetCreatedLists = true;
    savePortfolioToFileSystem(
        customer.getPersistentIdentity(),
        resetAllIsModified,
        resetCreatedLists,
        portfolio);
    
    return buildingSubscription;  
  }
  
  @Override
  public Integer getBuildingIdForDescendantId(
      Integer customerId, 
      Integer descendantId)
  throws 
      EntityDoesNotExistException {
    
    PortfolioEntity portfolio = loadPortfolio(customerId);
    for (AbstractNodeEntity node: portfolio.getAllNodes()) {
      
      if (node.getPersistentIdentity().equals(descendantId)) {
        
        return node.getAncestorBuilding().getPersistentIdentity();
      }
    }
    throw new EntityDoesNotExistException("Descendant node with id: ["
        + descendantId
        + "] does have an ancestor building for customer id: ["
        + customerId
        + "].");
  }
  
  @Override
  public Set<Integer> getBuildingIdsForDescendantIds(
      Integer customerId, 
      Collection<Integer> descendantIds)
  throws 
      EntityDoesNotExistException {
    
    PortfolioEntity portfolio = loadPortfolio(customerId);
    
    Set<Integer> buildingIds = new HashSet<>();
    for (AbstractNodeEntity node: portfolio.getAllNodes()) {
      
      if (descendantIds.contains(node.getPersistentIdentity())) {
        
        buildingIds.add(node.getAncestorBuilding().getPersistentIdentity());
      }
    }
    return buildingIds;
  }
  
  @Override
  public Set<Integer> getBuildingIdsForAdFunctionInstanceIds(
      Integer customerId,
      Collection<Integer> instanceIds)
  throws 
      EntityDoesNotExistException {
    
    PortfolioEntity portfolio = loadPortfolio(customerId);
    
    Set<Integer> buildingIds = new HashSet<>();
    for (AbstractAdFunctionInstanceEntity adFunctionInstance: portfolio.getAllAdFunctionInstances()) {
      
      if (instanceIds.contains(adFunctionInstance.getPersistentIdentity())) {
        
        buildingIds.add(adFunctionInstance.getEquipment().getAncestorBuilding().getPersistentIdentity());  
      }
    }
    return buildingIds;
  }

  @Override
  public Set<Integer> getBuildingIdsForRawPointIds(
      Integer customerId,
      Collection<Integer> rawPointIds)
  throws 
      EntityDoesNotExistException {
    
    PortfolioEntity portfolio = loadPortfolio(customerId);
    
    Set<Integer> buildingIds = new HashSet<>();
    for (MappablePointEntity mappablePoint: portfolio.getAllMappablePoints()) {
      
      if (rawPointIds.contains(mappablePoint.getRawPoint().getPersistentIdentity())) {
        
        buildingIds.add(mappablePoint.getAncestorBuilding().getPersistentIdentity());  
      }
    }
    return buildingIds;
  }
  
  @Override
  public List<Integer> getBuildingIds(Integer customerId){
    
    try {
      PortfolioEntity portfolio = loadPortfolio(customerId);
      
      List<Integer> buildingIds = new ArrayList<>();
      for (BuildingEntity building: portfolio.getChildBuildings()) {
        
        buildingIds.add(building.getPersistentIdentity());
      }
      return buildingIds;
      
    } catch (EntityDoesNotExistException e) {
      throw new IllegalStateException("Customer does not exist: " + customerId);
    }
  }
  
  @Override
  public Double getAdFunctionConfigurationStatusPercent(
      Integer customerId,
      FunctionType functionType)
  throws
      EntityDoesNotExistException {

    PortfolioEntity portfolio = loadPortfolio(customerId);
    
    int numGreenCountCandidateState = 0;
    for (AbstractAdFunctionInstanceEntity adFunctionInstanceCandidate: portfolio.getAllAdFunctionInstanceCandidates()) {
      
      if (adFunctionInstanceCandidate.getAdFunctionTemplate().getAdFunction().getFunctionType().equals(functionType)) {
        numGreenCountCandidateState++;
      }
    }

    int numGreenCountInstanceState = 0;
    for (AbstractAdFunctionInstanceEntity adFunctionInstanceCandidate: portfolio.getAllAdFunctionInstances()) {
      
      if (adFunctionInstanceCandidate.getAdFunctionTemplate().getAdFunction().getFunctionType().equals(functionType)) {
        numGreenCountInstanceState++;
      }
    }
    
    int numRedCount = getAdFunctionErrorMessagesCount(customerId, AdFunctionErrorMessageSearchCriteria
        .builder()
        .withFunctionTypeId(functionType.getId())
        .build());
    
    int totalCount = numGreenCountCandidateState + numGreenCountInstanceState + numRedCount;
    
    double percent = 0.0d;
    if (totalCount > 0) {
      
      percent = ((double)(numGreenCountCandidateState + numGreenCountInstanceState) / (double)totalCount) * 100.0d;
    }

    return Double.valueOf(percent);
  }
  
  @Override
  public Double getEnabledAdFunctionInstancesPercent(
      Integer customerId,
      FunctionType functionType)
  throws
      EntityDoesNotExistException {
   
    PortfolioEntity portfolio = loadPortfolio(customerId);
    
    int numGreenCountCandidateState = 0;
    for (AbstractAdFunctionInstanceEntity adFunctionInstanceCandidate: portfolio.getAllAdFunctionInstanceCandidates()) {
      
      if (adFunctionInstanceCandidate.getAdFunctionTemplate().getAdFunction().getFunctionType().equals(functionType)) {
        numGreenCountCandidateState++;
      }
    }

    int numGreenCountInstanceState = 0;
    for (AbstractAdFunctionInstanceEntity adFunctionInstanceCandidate: portfolio.getAllAdFunctionInstances()) {
      
      if (adFunctionInstanceCandidate.getAdFunctionTemplate().getAdFunction().getFunctionType().equals(functionType)) {
        numGreenCountInstanceState++;
      }
    }
    
    int numRedCount = getAdFunctionErrorMessagesCount(customerId, AdFunctionErrorMessageSearchCriteria
        .builder()
        .withFunctionTypeId(functionType.getId())
        .build());
    
    int totalCount = numGreenCountCandidateState + numGreenCountInstanceState + numRedCount;
    
    double percent = 0.0d;
    if (totalCount > 0) {
      
      percent = ((double)(numGreenCountCandidateState + numGreenCountInstanceState) / (double)totalCount) * 100.0d;
    }

    return Double.valueOf(percent);    
  }
  
  @Override
  public int getAdFunctionErrorMessagesCount(
      int customerId,
      AdFunctionErrorMessageSearchCriteria searchCriteria) {
    
    AdFunctionTemplatesContainer adFunctionTemplatesContainer = DictionaryContext.getAdFunctionTemplatesContainer();
    Set<Integer> adFunctionTemplates = null;
    if (searchCriteria.getFunctionTypeId().equals(AdFunctionErrorMessageSearchCriteria.FUNCTION_TYPE_RULE)) {
      adFunctionTemplates = adFunctionTemplatesContainer.getAdRuleFunctionTemplateIds();  
    } else {
      adFunctionTemplates = adFunctionTemplatesContainer.getAdComputedPointFunctionTemplateIds();
    }
    
    Integer criteriaAdFunctionTemplateId = searchCriteria.getAdFunctionTemplateId();
    Integer criteriaEnergyExchangeId = searchCriteria.getEnergyExchangeId();
    List<AdFunctionErrorMessagesDto> dtoList = loadAdFunctionErrorMessagesDtoList(customerId);
    List<AdFunctionErrorMessagesDto> filteredDtoList = new ArrayList<>();
    for (AdFunctionErrorMessagesDto dto: dtoList) {
      
      if (adFunctionTemplates.contains(dto.getAdFunctionTemplateId())) {

        boolean matches = true;
        if (criteriaAdFunctionTemplateId != null && !criteriaAdFunctionTemplateId.equals(dto.getAdFunctionTemplateId())) {
          matches = false;
        }
        if (criteriaEnergyExchangeId != null && !criteriaEnergyExchangeId.equals(dto.getEnergyExchangeId())) {
          matches = false;
        }
        if (matches) {
          filteredDtoList.add(dto);
        }
      }
    }
    return filteredDtoList.size();
  }
  
  @Override
  public List<AdFunctionErrorMessagesValueObject> getAdFunctionErrorMessagesData(
      int customerId,
      AdFunctionErrorMessageSearchCriteria searchCriteria) {
    
    try {
      
      PortfolioEntity portfolio = loadPortfolio(customerId);
      AdFunctionTemplatesContainer adFunctionTemplatesContainer = DictionaryContext.getAdFunctionTemplatesContainer();
      Set<Integer> adFunctionTemplates = null;
      if (searchCriteria.getFunctionTypeId().equals(AdFunctionErrorMessageSearchCriteria.FUNCTION_TYPE_RULE)) {
        adFunctionTemplates = adFunctionTemplatesContainer.getAdRuleFunctionTemplateIds();  
      } else {
        adFunctionTemplates = adFunctionTemplatesContainer.getAdComputedPointFunctionTemplateIds();
      }

      Integer criteriaAdFunctionTemplateId = searchCriteria.getAdFunctionTemplateId();
      Integer criteriaEnergyExchangeId = searchCriteria.getEnergyExchangeId();
      List<AdFunctionErrorMessagesDto> dtoList = loadAdFunctionErrorMessagesDtoList(customerId);
      List<AdFunctionErrorMessagesValueObject> filteredDtoList = new ArrayList<>();
      for (AdFunctionErrorMessagesDto dto: dtoList) {
        
        if (adFunctionTemplates.contains(dto.getAdFunctionTemplateId())) {

          boolean matches = true;
          if (criteriaAdFunctionTemplateId != null && !criteriaAdFunctionTemplateId.equals(dto.getAdFunctionTemplateId())) {
            matches = false;
          }
          if (criteriaEnergyExchangeId != null && !criteriaEnergyExchangeId.equals(dto.getEnergyExchangeId())) {
            matches = false;
          }
          if (matches) {
            
            EnergyExchangeEntity energyExchangeNode = portfolio.getEnergyExchangeSystemNode(dto.getEnergyExchangeId());
            
            String energyExchangeTypeName = null;
            AbstractEnergyExchangeTypeEntity energyExchangeType = energyExchangeNode.getEnergyExchangeTypeNullIfNotExists();
            if (energyExchangeType != null) {
              energyExchangeTypeName = energyExchangeType.getName();
            }
            
            filteredDtoList.add(AdFunctionErrorMessagesValueObject
                .builder()
                .withAdFunctionTemplateId(dto.getAdFunctionTemplateId())
                .withAdFunctionTemplateName(adFunctionTemplatesContainer.getDisplayName(dto.getAdFunctionTemplateId()))
                .withEnergyExchangeTypeName(energyExchangeTypeName)
                .withEnergyExchangeId(dto.getEnergyExchangeId())
                .withEnergyExchangeName(energyExchangeNode.getDisplayName())
                .withNodePath(energyExchangeNode.getNodePath())
                .withErrorMessages(adFunctionTemplatesContainer.getErrorMessages(dto.getErrorMessages()))
                .build());
          }
        }
      }
      Collections.sort(filteredDtoList);
      return filteredDtoList;      
    } catch (Exception e) {
      throw new IllegalStateException("getAdFunctionErrorMessagesData() encountered a problem with customer: " + customerId, e);
    }
  }
  
  @Override
  public void storeNodeHierarchyChangeEvent(NodeHierarchyChangeEvent nodeHierarchyChangeEvent) {
    
    List<NodeHierarchyChangeEvent> entities = loadNodeHierarchyChangeEvents();
    
    entities.add(nodeHierarchyChangeEvent);
    
    storeNodeHierarchyChangeEventDtos( NodeHierarchyChangeEvent
        .Mapper
        .getInstance()
        .mapEntitiesToDtos(entities));
  }
  
  private List<NodeHierarchyChangeEvent> loadNodeHierarchyChangeEvents() {
    
    List<NodeHierarchyChangeEvent> entities = new ArrayList<>();
    List<NodeHierarchyChangeEventDto> dtos = loadNodeHierarchyChangeEventDtos();
    for (NodeHierarchyChangeEventDto dto: dtos) {
      
      NodeHierarchyChangeEvent entity = NodeHierarchyChangeEvent
          .Mapper
          .getInstance()
          .mapDtoToEntity(
              dto.getCustomerId(), 
              dto);
      
      entities.add(entity);
    }
    return entities;
  }  
  
  private synchronized List<NodeHierarchyChangeEventDto> loadNodeHierarchyChangeEventDtos() {
    
    File file = new File(basePath + "/Node_Hierarchy_Change_Events.json");
    if (file.exists() && file.length() > 0) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));
          
          Scanner s = new Scanner(in).useDelimiter("\\A")) {
          String fullJson = s.hasNext() ? s.next() : "";
  
          JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
          String compactJson = jsonNode.toString();
          
          return AbstractEntity.OBJECT_MAPPER.get().readValue(compactJson, new TypeReference<List<NodeHierarchyChangeEventDto>>() {});
  
      } catch (IOException e) {
        LOGGER.error("Unable to load file: " + file.getAbsolutePath(), e);
      }
    }
    return new ArrayList<>();
  }
  
  private synchronized void storeNodeHierarchyChangeEventDtos(List<NodeHierarchyChangeEventDto> nodeHierarchyChangeEvents) {
    
    File file = new File(basePath + "/Node_Hierarchy_Change_Events.json");
    OutputStream out = null;
    try {
      
      out = new BufferedOutputStream(new FileOutputStream(file));
      AbstractEntity.OBJECT_WRITER.get().writeValue(out, nodeHierarchyChangeEvents);

    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (out != null) {
        try {
          out.flush();
          out.close();
        } catch (IOException ioe) {
        }
      }
    }
  }
  
  // FAST LANE WRITER OPERATIONS (I.E. DOES NOT LOAD PORTFOLIO, DOES STRAIGHT INSERT/UPDATE)
  @Override
  public TagInfo getTagInfo(int tagId) {
    
    throw new RuntimeException("Not implemented yet!");
  }
  
  @Override
  public int insertNodeTag(int customerId, int nodeId, int tagId) {

    List<NodeTagDto> nodeTagDtoList = loadNodeTagDtoList(customerId);

    NodeTagDto dto = new NodeTagDto();
    dto.setId(nodeId);
    dto.setTagIds(Arrays.asList(tagId));
    nodeTagDtoList.add(dto);
    
    storeNodeTagDtoList(customerId, nodeTagDtoList);
    
    return 1;
  }

  @Override
  public int deleteNodeTag(int customerId, int nodeId, int tagId) {

    List<NodeTagDto> nodeTagDtoList = loadNodeTagDtoList(customerId);

    NodeTagDto victim = null;
    for (NodeTagDto dto: nodeTagDtoList) {
      if (dto.getId() == nodeId && dto.getTagIds().contains(tagId)) {
	victim = dto;
	break;
      }
    }
    
    if (victim != null) {
      
      nodeTagDtoList.remove(victim);
      
      storeNodeTagDtoList(customerId, nodeTagDtoList);
      
      return 1;
    }
    return 0;
  }
  
  // FAST LANE WRITER OPERATIONS (I.E. DOES NOT LOAD PORTFOLIO, DOES STRAIGHT INSERT/UPDATE)
  @Override
  public List<AddNodeDto> insertNodes(int customerId, List<AddNodeDto> dtoList) {
    
    throw new RuntimeException("Not implemented yet!");
  }

  @Override
  public AddNodeDto insertNode(int customerId, AddNodeDto dto) {
    
    throw new RuntimeException("Not implemented yet!");
  }
  
  @Override
  public AsyncPoint insertCustomAsyncComputedPoint(AsyncPoint dto) {
    
    throw new RuntimeException("Not implemented yet!");
  }

  @Override
  public AsyncPoint updateCustomAsyncComputedPoint(AsyncPoint dto) {
    
    throw new RuntimeException("Not implemented yet!");
  }
  
  @Override
  public void updateNodeDisplayName(int customerId, int nodeId, int nodeTypeId, int pointTypeId, String nodeDisplayName) {
    
    throw new RuntimeException("Not implemented yet!");
  }

  // EXPOSED TO ALLOW FOR PORTFOLIO EXPORTS  
  public void savePortfolioToFileSystem(
      int customerId,
      boolean resetAllIsModified,
      boolean resetCreatedLists,
      PortfolioEntity portfolio) {
    
    Map<String, Object> dtos = PortfolioEntity.mapToDtos(portfolio);
    
    List<RawPointDto> rawPointDtoList = PortfolioDtoMapper.getRawPointDtoList(dtos);
    storeRawPointDtoList(customerId, rawPointDtoList);
    
    List<NonPointNodeDto> nonPointNodeDtoList = PortfolioDtoMapper.getNonPointNodeDtoList(dtos);
    storeNonPointNodeDtoList(customerId, nonPointNodeDtoList);
    
    List<MappablePointNodeDto> mappablePointNodeDtoList = PortfolioDtoMapper.getMappablePointNodeDtoList(dtos);
    storeMappablePointNodeDtoList(customerId, mappablePointNodeDtoList);

    List<CustomAsyncComputedPointNodeDto> customAsyncComputedPointNodeDtoList = PortfolioDtoMapper.getCustomAsyncComputedPointNodeDtoList(dtos);
    storeCustomAsyncComputedPointNodeDtoList(customerId, customAsyncComputedPointNodeDtoList);
    
    List<ScheduledAsyncComputedPointNodeDto> scheduledAsyncComputedPointNodeDtoList = PortfolioDtoMapper.getScheduledAsyncComputedPointNodeDtoList(dtos);
    storeScheduledAsyncComputedPointNodeDtoList(customerId, scheduledAsyncComputedPointNodeDtoList);

    List<AsyncComputedPointNodeDto> asyncComputedPointNodeDtoList = PortfolioDtoMapper.getAsyncComputedPointNodeDtoList(dtos);
    storeAsyncComputedPointNodeDtoList(customerId, asyncComputedPointNodeDtoList);
    
    List<NodeTagDto> nodeTagDtoList = PortfolioDtoMapper.getNodeTagDtoList(dtos);
    storeNodeTagDtoList(customerId, nodeTagDtoList);
    
    List<EnergyExchangeSystemEdgeDto> energyExchangeSystemEdgeDtoList = PortfolioDtoMapper.getEnergyExchangeSystemEdgeDtoList(dtos);
    storeEnergyExchangeSystemEdgeDtoList(customerId, energyExchangeSystemEdgeDtoList);

    List<ReportInstanceDto> filteredReportInstanceDtoList = new ArrayList<>();
    List<ReportInstanceDto> reportInstanceDtoList = PortfolioDtoMapper.getReportInstanceDtoList(dtos);
    for (ReportInstanceDto dto: reportInstanceDtoList) {
      if (!dto.getNeedsDisabling()) {
        filteredReportInstanceDtoList.add(dto);
      }
    }
    storeReportInstanceDtoList(customerId, filteredReportInstanceDtoList);

    List<ReportInstanceStatusDto> reportInstanceStatusDtoList = PortfolioDtoMapper.getReportInstanceStatusDtoList(dtos);
    storeReportInstanceStatusDtoList(customerId, reportInstanceStatusDtoList);
 
    List<AdFunctionInstanceDto> adFunctionInstanceCandidateDtoList = PortfolioDtoMapper.getAdFunctionInstanceCandidateDtoList(dtos);
    storeAdFunctionInstanceCandidateDtoList(customerId, adFunctionInstanceCandidateDtoList);
    
    List<AdFunctionInstanceDto> adFunctionInstanceDtoList = PortfolioDtoMapper.getAdFunctionInstanceDtoList(dtos);
    storeAdFunctionInstanceDtoList(customerId, adFunctionInstanceDtoList);

    List<AdFunctionErrorMessagesDto> adFunctionErrorMessagesDtoList = PortfolioDtoMapper.getAdFunctionErrorMessagesDtoList(dtos);
    storeAdFunctionErrorMessagesDtoList(customerId, adFunctionErrorMessagesDtoList);
    
    if (resetAllIsModified) {
    
      portfolio.resetAllIsModified();
    }
    
    if (resetCreatedLists) {
    
      portfolio.resetCreatedLists();
    }
  }
  
  protected List<RawPointDto> loadRawPointDtoList(int customerId) {
    
    File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_RawPoints.json");
    if (file.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));
          
          Scanner s = new Scanner(in).useDelimiter("\\A")) {
          String fullJson = s.hasNext() ? s.next() : "";
  
          JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
          String compactJson = jsonNode.toString();
          
          List<RawPointDto> rawPointDtoList = AbstractEntity.OBJECT_MAPPER.get().readValue(compactJson, new TypeReference<List<RawPointDto>>() {});
          return rawPointDtoList;
  
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }
    }
    return new ArrayList<>();
  }
  
  protected void storeRawPointDtoList(int customerId, List<RawPointDto> dtoList) {
    
    if (!dtoList.isEmpty()) {
      File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_RawPoints.json");
      OutputStream out = null;
      try {
        
        out = new BufferedOutputStream(new FileOutputStream(file));
        getObjectWriter().writeValue(out, dtoList);

      } catch (IOException e) {
        throw new IllegalStateException(e);
      } finally {
        if (out != null) {
          try {
            out.flush();
            out.close();
          } catch (IOException ioe) {
          }
        }
      }
    }
  }  
  
  protected List<NonPointNodeDto> loadNonPointNodeDtoList(int customerId) {
    
    File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_NonPointNodes.json");
    if (file.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));
          
          Scanner s = new Scanner(in).useDelimiter("\\A")) {
          String fullJson = s.hasNext() ? s.next() : "";
  
          JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
          String compactJson = jsonNode.toString();
          
          List<NonPointNodeDto> nonPointNodeDtoList = AbstractEntity.OBJECT_MAPPER.get().readValue(compactJson, new TypeReference<List<NonPointNodeDto>>() {});
          return nonPointNodeDtoList;
  
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }
    }
    return new ArrayList<>();
  }

  protected void storeNonPointNodeDtoList(int customerId, List<NonPointNodeDto> dtoList) {
    
    File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_NonPointNodes.json");
    OutputStream out = null;
    try {
      
      out = new BufferedOutputStream(new FileOutputStream(file));
      getObjectWriter().writeValue(out, dtoList);

    } catch (IOException e) {
      throw new IllegalStateException(e);
    } finally {
      if (out != null) {
        try {
          out.flush();
          out.close();
        } catch (IOException ioe) {
        }
      }
    }
  }  
  
  protected List<MappablePointNodeDto> loadMappablePointNodeDtoList(int customerId) {
    
    File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_MappablePointNodes.json");
    if (file.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));
      
          Scanner s = new Scanner(in).useDelimiter("\\A")) {
          String fullJson = s.hasNext() ? s.next() : "";
  
          JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
          String compactJson = jsonNode.toString();
          
          List<MappablePointNodeDto> mappablePointNodeDtoList = AbstractEntity.OBJECT_MAPPER.get().readValue(compactJson, new TypeReference<List<MappablePointNodeDto>>() {});
          return mappablePointNodeDtoList;
  
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }
    }
    return new ArrayList<>();
  }
  
  protected void storeMappablePointNodeDtoList(int customerId, List<MappablePointNodeDto> dtoList) {
    
    File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_MappablePointNodes.json");
    OutputStream out = null;
    try {
      
      out = new BufferedOutputStream(new FileOutputStream(file));
      getObjectWriter().writeValue(out, dtoList);

    } catch (IOException e) {
      throw new IllegalStateException(e);
    } finally {
      if (out != null) {
        try {
          out.flush();
          out.close();
        } catch (IOException ioe) {
        }
      }
    }
  }  

  protected List<CustomAsyncComputedPointNodeDto> loadCustomAsyncComputedPointNodeDtoList(int customerId) {
    
    File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_CustomAsyncComputedPointNodes.json");
    if (file.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));
      
          Scanner s = new Scanner(in).useDelimiter("\\A")) {
          String fullJson = s.hasNext() ? s.next() : "";
  
          JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
          String compactJson = jsonNode.toString();
          
          List<CustomAsyncComputedPointNodeDto> customAsyncComputedPointNodeDtoList = AbstractEntity.OBJECT_MAPPER.get().readValue(compactJson, new TypeReference<List<CustomAsyncComputedPointNodeDto>>() {});
          return customAsyncComputedPointNodeDtoList;
  
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }
    }
    return new ArrayList<>();
  }
  
  protected void storeCustomAsyncComputedPointNodeDtoList(int customerId, List<CustomAsyncComputedPointNodeDto> dtoList) {
    
    File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_CustomAsyncComputedPointNodes.json");
    OutputStream out = null;
    try {
      
      out = new BufferedOutputStream(new FileOutputStream(file));
      getObjectWriter().writeValue(out, dtoList);

    } catch (IOException e) {
      throw new IllegalStateException(e);
    } finally {
      if (out != null) {
        try {
          out.flush();
          out.close();
        } catch (IOException ioe) {
        }
      }
    }
  }
  
  protected List<ScheduledAsyncComputedPointNodeDto> loadScheduledAsyncComputedPointNodeDtoList(int customerId) {
    
    File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_ScheduledAsyncComputedPointNodes.json");
    if (file.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));
      
          Scanner s = new Scanner(in).useDelimiter("\\A")) {
          String fullJson = s.hasNext() ? s.next() : "";
  
          JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
          String compactJson = jsonNode.toString();
          
          List<ScheduledAsyncComputedPointNodeDto> scheduledAsyncComputedPointNodeDtoList = AbstractEntity.OBJECT_MAPPER.get().readValue(compactJson, new TypeReference<List<ScheduledAsyncComputedPointNodeDto>>() {});
          return scheduledAsyncComputedPointNodeDtoList;
  
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }
    }
    return new ArrayList<>();
  }
    
  protected void storeScheduledAsyncComputedPointNodeDtoList(int customerId, List<ScheduledAsyncComputedPointNodeDto> dtoList) {
    
    File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_ScheduledAsyncComputedPointNodes.json");
    OutputStream out = null;
    try {
      
      out = new BufferedOutputStream(new FileOutputStream(file));
      getObjectWriter().writeValue(out, dtoList);

    } catch (IOException e) {
      throw new IllegalStateException(e);
    } finally {
      if (out != null) {
        try {
          out.flush();
          out.close();
        } catch (IOException ioe) {
        }
      }
    }
  }  
  
  protected List<AsyncComputedPointNodeDto> loadAsyncComputedPointNodeDtoList(int customerId) {
    
    File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_AsyncComputedPointNodes.json");
    if (file.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));
      
          Scanner s = new Scanner(in).useDelimiter("\\A")) {
          String fullJson = s.hasNext() ? s.next() : "";
  
          JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
          String compactJson = jsonNode.toString();
          
          List<AsyncComputedPointNodeDto> asyncComputedPointNodeDtoList = AbstractEntity.OBJECT_MAPPER.get().readValue(compactJson, new TypeReference<List<AsyncComputedPointNodeDto>>() {});
          return asyncComputedPointNodeDtoList;
  
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }
    }
    return new ArrayList<>();
  }
  
  protected void storeAsyncComputedPointNodeDtoList(int customerId, List<AsyncComputedPointNodeDto> dtoList) {
    
    File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_AsyncComputedPointNodes.json");
    OutputStream out = null;
    try {
      
      out = new BufferedOutputStream(new FileOutputStream(file));
      getObjectWriter().writeValue(out, dtoList);

    } catch (IOException e) {
      throw new IllegalStateException(e);
    } finally {
      if (out != null) {
        try {
          out.flush();
          out.close();
        } catch (IOException ioe) {
        }
      }
    }
  }   

  protected List<NodeTagDto> loadNodeTagDtoList(int customerId) {

    File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_NodeTags.json");
    if (file.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));
          
          Scanner s = new Scanner(in).useDelimiter("\\A")) {
          String fullJson = s.hasNext() ? s.next() : "";
  
          JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
          String compactJson = jsonNode.toString();
          
          List<NodeTagDto> nodeTagDtoList = AbstractEntity.OBJECT_MAPPER.get().readValue(compactJson, new TypeReference<List<NodeTagDto>>() {});
          return nodeTagDtoList;
          
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }
    }
    return new ArrayList<>();
  }
  
  protected void storeNodeTagDtoList(int customerId, List<NodeTagDto> dtoList) {
    
    File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_NodeTags.json");
    OutputStream out = null;
    try {
      
      out = new BufferedOutputStream(new FileOutputStream(file));
      getObjectWriter().writeValue(out, dtoList);

    } catch (IOException e) {
      throw new IllegalStateException(e);
    } finally {
      if (out != null) {
        try {
          out.flush();
          out.close();
        } catch (IOException ioe) {
        }
      }
    }
  }  
  
  protected List<EnergyExchangeSystemEdgeDto> loadEnergyExchangeSystemEdgeDtoList(int customerId) {

    File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_EnergyExchangeSystemEdges.json");
    if (file.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));
          
          Scanner s = new Scanner(in).useDelimiter("\\A")) {
          String fullJson = s.hasNext() ? s.next() : "";
  
          JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
          String compactJson = jsonNode.toString();
          
          return AbstractEntity.OBJECT_MAPPER.get().readValue(compactJson, new TypeReference<List<EnergyExchangeSystemEdgeDto>>() {});
          
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }
    }
    return new ArrayList<>();
  }
  
  protected void storeEnergyExchangeSystemEdgeDtoList(int customerId, List<EnergyExchangeSystemEdgeDto> dtoList) {
    
    File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_EnergyExchangeSystemEdges.json");
    OutputStream out = null;
    try {
      
      out = new BufferedOutputStream(new FileOutputStream(file));
      getObjectWriter().writeValue(out, dtoList);

    } catch (IOException e) {
      throw new IllegalStateException(e);
    } finally {
      if (out != null) {
        try {
          out.flush();
          out.close();
        } catch (IOException ioe) {
        }
      }
    }
  }  
  
  protected List<ReportInstanceDto> loadReportInstanceDtoList(int customerId) {

    File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_ReportInstances.json");
    if (file.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));
          
          Scanner s = new Scanner(in).useDelimiter("\\A")) {
          String fullJson = s.hasNext() ? s.next() : "";
  
          JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
          String compactJson = jsonNode.toString();
  
          List<ReportInstanceDto> instanceDtoList = AbstractEntity.OBJECT_MAPPER.get().readValue(compactJson, new TypeReference<List<ReportInstanceDto>>() {});
          return instanceDtoList;
          
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }
    }
    return new ArrayList<>();
  }
  
  protected void storeReportInstanceDtoList(int customerId, List<ReportInstanceDto> dtoList) {
    
    File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_ReportInstances.json");
    OutputStream out = null;
    try {
      
      out = new BufferedOutputStream(new FileOutputStream(file));
      getObjectWriter().writeValue(out, dtoList);

    } catch (IOException e) {
      throw new IllegalStateException(e);
    } finally {
      if (out != null) {
        try {
          out.flush();
          out.close();
        } catch (IOException ioe) {
        }
      }
    }
  }
  
  protected List<ReportInstanceStatusDto> loadReportInstanceStatusDtoList(int customerId) {

    File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_ReportInstanceStatuses.json");
    if (file.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));
          
          Scanner s = new Scanner(in).useDelimiter("\\A")) {
          String fullJson = s.hasNext() ? s.next() : "";
  
          JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
          String compactJson = jsonNode.toString();
  
          List<ReportInstanceStatusDto> instanceDtoList = AbstractEntity.OBJECT_MAPPER.get().readValue(compactJson, new TypeReference<List<ReportInstanceStatusDto>>() {});
          return instanceDtoList;
          
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }
    }
    return new ArrayList<>();
  }
  
  protected void storeReportInstanceStatusDtoList(int customerId, List<ReportInstanceStatusDto> dtoList) {
    
    File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_ReportInstanceStatuses.json");
    OutputStream out = null;
    try {
      
      out = new BufferedOutputStream(new FileOutputStream(file));
      getObjectWriter().writeValue(out, dtoList);

    } catch (IOException e) {
      throw new IllegalStateException(e);
    } finally {
      if (out != null) {
        try {
          out.flush();
          out.close();
        } catch (IOException ioe) {
        }
      }
    }
  }  
  
  protected List<AdFunctionInstanceDto> loadAdFunctionInstanceCandidateDtoList(int customerId) {

    File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_FunctionInstanceCandidates.json");
    if (file.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));
          
          Scanner s = new Scanner(in).useDelimiter("\\A")) {
          String fullJson = s.hasNext() ? s.next() : "";
  
          JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
          String compactJson = jsonNode.toString();
  
          List<AdFunctionInstanceDto> dtoList = AbstractEntity.OBJECT_MAPPER.get().readValue(compactJson, new TypeReference<List<AdFunctionInstanceDto>>() {});
          return dtoList;
          
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }
    }
    return new ArrayList<>();
  }
  
  protected void storeAdFunctionInstanceCandidateDtoList(int customerId, List<AdFunctionInstanceDto> dtoList) {
    
    File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_FunctionInstanceCandidates.json");
    OutputStream out = null;
    try {
      
      out = new BufferedOutputStream(new FileOutputStream(file));
      getObjectWriter().writeValue(out, dtoList);

    } catch (IOException e) {
      throw new IllegalStateException(e);
    } finally {
      if (out != null) {
        try {
          out.flush();
          out.close();
        } catch (IOException ioe) {
        }
      }
    }
  }

  protected List<AdFunctionInstanceDto> loadAdFunctionInstanceDtoList(int customerId) {

    File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_FunctionInstances.json");
    if (file.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));
          
          Scanner s = new Scanner(in).useDelimiter("\\A")) {
          String fullJson = s.hasNext() ? s.next() : "";
  
          JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
          String compactJson = jsonNode.toString();
  
          List<AdFunctionInstanceDto> dtoList = AbstractEntity.OBJECT_MAPPER.get().readValue(compactJson, new TypeReference<List<AdFunctionInstanceDto>>() {});
          return dtoList;
          
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }
    }
    return new ArrayList<>();
  }
  
  protected void storeAdFunctionInstanceDtoList(int customerId, List<AdFunctionInstanceDto> dtoList) {
    
    File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_FunctionInstances.json");
    OutputStream out = null;
    try {
      
      out = new BufferedOutputStream(new FileOutputStream(file));
      getObjectWriter().writeValue(out, dtoList);

    } catch (IOException e) {
      throw new IllegalStateException(e);
    } finally {
      if (out != null) {
        try {
          out.flush();
          out.close();
        } catch (IOException ioe) {
        }
      }
    }
  }
  
  protected List<AdFunctionErrorMessagesDto> loadAdFunctionErrorMessagesDtoList(int customerId) {

    File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_AdFunctionErrorMessages.json");
    if (file.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));
          
          Scanner s = new Scanner(in).useDelimiter("\\A")) {
          String fullJson = s.hasNext() ? s.next() : "";
  
          JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
          String compactJson = jsonNode.toString();
          
          return AbstractEntity.OBJECT_MAPPER.get().readValue(compactJson, new TypeReference<List<AdFunctionErrorMessagesDto>>() {});
          
      } catch (IOException e) {
        throw new IllegalStateException(e);
      }
    }
    return new ArrayList<>();
  }
  
  protected void storeAdFunctionErrorMessagesDtoList(int customerId, List<AdFunctionErrorMessagesDto> dtoList) {
    
    File file = new File(basePath + "/Customer_" + Integer.toString(customerId) + "_AdFunctionErrorMessages.json");
    OutputStream out = null;
    try {
      
      out = new BufferedOutputStream(new FileOutputStream(file));
      getObjectWriter().writeValue(out, dtoList);

    } catch (IOException e) {
      throw new IllegalStateException(e);
    } finally {
      if (out != null) {
        try {
          out.flush();
          out.close();
        } catch (IOException ioe) {
        }
      }
    }
  }   
}
//@formatter:on