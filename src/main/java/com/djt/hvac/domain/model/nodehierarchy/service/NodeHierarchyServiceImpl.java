//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service;

import static java.util.Objects.requireNonNull;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.cache.client.CacheClient;
import com.djt.hvac.domain.model.cache.kryo.KryoSerialize;
import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.dsl.computedpoint.v2.Result;
import com.djt.hvac.domain.model.common.dsl.pointmap.Node;
import com.djt.hvac.domain.model.common.event.AbstractEvent;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.common.exception.StaleDataException;
import com.djt.hvac.domain.model.common.exception.ValidationException;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.customer.CustomerLevelPointTemplateUnitMappingOverrideEntity;
import com.djt.hvac.domain.model.customer.OnlineCustomerEntity;
import com.djt.hvac.domain.model.customer.enums.CustomerStatus;
import com.djt.hvac.domain.model.customer.repository.CustomerRepository;
import com.djt.hvac.domain.model.customer.repository.CustomerRepositoryFileSystemImpl;
import com.djt.hvac.domain.model.dictionary.PaymentPlanEntity;
import com.djt.hvac.domain.model.dictionary.ScheduledEventTypeEntity;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.UnitEntity;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.container.NodeTagTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.container.TagsContainer;
import com.djt.hvac.domain.model.dictionary.container.UnitsContainer;
import com.djt.hvac.domain.model.dictionary.container.WeatherStationsContainer;
import com.djt.hvac.domain.model.dictionary.energyexchange.AbstractEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.EquipmentEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.LoopEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.PlantEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.enums.EnergyExchangeSystemType;
import com.djt.hvac.domain.model.dictionary.enums.DataType;
import com.djt.hvac.domain.model.dictionary.enums.FunctionType;
import com.djt.hvac.domain.model.dictionary.enums.NodeSubType;
import com.djt.hvac.domain.model.dictionary.enums.NodeType;
import com.djt.hvac.domain.model.dictionary.enums.PaymentInterval;
import com.djt.hvac.domain.model.dictionary.enums.TagGroupType;
import com.djt.hvac.domain.model.dictionary.enums.UnitSystem;
import com.djt.hvac.domain.model.dictionary.event.DictionaryChangeEvent;
import com.djt.hvac.domain.model.dictionary.repository.DictionaryRepository;
import com.djt.hvac.domain.model.dictionary.repository.DictionaryRepositoryFileSystemImpl;
import com.djt.hvac.domain.model.dictionary.template.function.AbstractAdFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.function.computedpoint.AdComputedPointFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.function.rule.AdRuleFunctionTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.AbstractNodeTagTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateUnitMappingEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.UnitMappingEntity;
import com.djt.hvac.domain.model.dictionary.template.report.ReportTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.report.enums.ReportPriority;
import com.djt.hvac.domain.model.dictionary.template.report.enums.ReportState;
import com.djt.hvac.domain.model.dictionary.weather.GlobalComputedPointEntity;
import com.djt.hvac.domain.model.dictionary.weather.WeatherStationEntity;
import com.djt.hvac.domain.model.distributor.AbstractDistributorEntity;
import com.djt.hvac.domain.model.distributor.DistributorLevelPointTemplateUnitMappingOverrideEntity;
import com.djt.hvac.domain.model.distributor.OnlineDistributorEntity;
import com.djt.hvac.domain.model.distributor.enums.DistributorStatus;
import com.djt.hvac.domain.model.distributor.paymentmethod.AbstractPaymentMethodEntity;
import com.djt.hvac.domain.model.distributor.repository.DistributorRepository;
import com.djt.hvac.domain.model.distributor.repository.DistributorRepositoryFileSystemImpl;
import com.djt.hvac.domain.model.distributor.service.DistributorHierarchyStateEvaluator;
import com.djt.hvac.domain.model.function.AbstractAdFunctionInstanceEntity;
import com.djt.hvac.domain.model.function.AdFunctionInstanceInputConstantEntity;
import com.djt.hvac.domain.model.function.computedpoint.AdComputedPointFunctionInstanceEntity;
import com.djt.hvac.domain.model.function.dto.AdFunctionInstanceDto;
import com.djt.hvac.domain.model.function.rule.AdRuleFunctionInstanceEntity;
import com.djt.hvac.domain.model.function.status.AdFunctionErrorMessageSearchCriteria;
import com.djt.hvac.domain.model.function.status.AdFunctionErrorMessageSearchResponse;
import com.djt.hvac.domain.model.function.status.AdFunctionErrorMessagesValueObject;
import com.djt.hvac.domain.model.geocoding.client.GeocodingClient;
import com.djt.hvac.domain.model.geocoding.dto.GeocodingAddress;
import com.djt.hvac.domain.model.geocoding.exception.GeocodingClientLookupFailureException;
import com.djt.hvac.domain.model.nodehierarchy.AbstractNodeEntity;
import com.djt.hvac.domain.model.nodehierarchy.FloorEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.SubBuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BillableBuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingLevelPointTemplateUnitMappingOverrideEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingSubscriptionEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingTemporalConfigEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingTemporalUtilityEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingPaymentStatus;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingPaymentType;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingStatus;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingUtilityType;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.OperationType;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.UtilityComputationInterval;
import com.djt.hvac.domain.model.nodehierarchy.dto.AddNodeDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.custompoint.AsyncPoint;
import com.djt.hvac.domain.model.nodehierarchy.dto.custompoint.BuildingCustomAsyncComputedPointState;
import com.djt.hvac.domain.model.nodehierarchy.dto.custompoint.CustomAsyncComputedPointState;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.AbstractEnergyExchangeEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EnergyExchangeEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EquipmentEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.LoopEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.PlantEntity;
import com.djt.hvac.domain.model.nodehierarchy.event.ModelChangeEventPublisher;
import com.djt.hvac.domain.model.nodehierarchy.event.NodeHierarchyChangeEvent;
import com.djt.hvac.domain.model.nodehierarchy.point.AbstractPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.ScheduledAsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.CustomAsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.CustomPointFormulaVariableEligiblePoint;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.FormulaVariableEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.TemporalAsyncComputedPointConfigEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.enums.ComputationInterval;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.enums.FillPolicy;
import com.djt.hvac.domain.model.nodehierarchy.point.mapped.MappablePointEntity;
import com.djt.hvac.domain.model.nodehierarchy.repository.NodeHierarchyRepository;
import com.djt.hvac.domain.model.nodehierarchy.repository.NodeHierarchyRepositoryFileSystemCachingImpl;
import com.djt.hvac.domain.model.nodehierarchy.repository.NodeHierarchyRepositoryFileSystemImpl;
import com.djt.hvac.domain.model.nodehierarchy.service.command.AddPointTemplateOverrideRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.BuildingSubscriptionRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.CreateAdFunctionInstancesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.CreateNodeRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.DeleteAdFunctionInstancesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.DeleteChildNodesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.EvaluateReportsRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.FindAdFunctionInstanceCandidatesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.IgnoreRawPointsRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.MapRawPointsRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.MoveChildNodesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.NodeHierarchyCommandRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.NodeHierarchyCommandResponse;
import com.djt.hvac.domain.model.nodehierarchy.service.command.PerformPortfolioMaintenanceRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.RemediatePortfolioRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.RemovePointTemplateOverrideRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UnignoreRawPointsRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UnmapRawPointsRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UpdateAdFunctionInstancesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UpdateBuildingNodesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UpdateCustomAsyncComputedPointNodesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UpdateEnergyExchangeSystemNodesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UpdateMappablePointNodesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UpdateReportInstancesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.ValidatePortfolioRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.AdFunctionInstanceData;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.BuildingAddressData;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.BuildingTemporalData;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.BuildingUtilityData;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.CustomAsyncComputedPointNodeData;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.EnergyExchangeSystemNodeData;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.MappablePointNodeData;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.PointTemplateUnitMappingOverride;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.RawPointData;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.ReportInstanceData;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.UpdateBuildingNodeRequest;
import com.djt.hvac.domain.model.nodehierarchy.utils.BillableBuildingPointLimiter;
import com.djt.hvac.domain.model.nodehierarchy.utils.ExceptionUtils;
import com.djt.hvac.domain.model.nodehierarchy.utils.LoadPortfolioOptions;
import com.djt.hvac.domain.model.nodehierarchy.utils.OpenTsdbStringUtils;
import com.djt.hvac.domain.model.nodehierarchy.utils.RawPointMappingNodeNameFilter;
import com.djt.hvac.domain.model.nodehierarchy.visitor.PortfolioVisitor;
import com.djt.hvac.domain.model.notification.dto.CreateNotificationEventOptions;
import com.djt.hvac.domain.model.notification.enums.NotificationEventType;
import com.djt.hvac.domain.model.notification.service.NotificationService;
import com.djt.hvac.domain.model.rawpoint.RawPointEntity;
import com.djt.hvac.domain.model.rawpoint.repository.RawPointRepository;
import com.djt.hvac.domain.model.report.ReportEvaluator;
import com.djt.hvac.domain.model.report.ReportInstanceEntity;
import com.djt.hvac.domain.model.report.dto.ReportInstanceStatusDto;
import com.djt.hvac.domain.model.report.status.BuildingReportStatusValueObject;
import com.djt.hvac.domain.model.report.status.PortfolioReportSummaryValueObject;
import com.djt.hvac.domain.model.report.status.ReportEquipmentErrorMessageListResponse;
import com.djt.hvac.domain.model.report.status.ReportEquipmentErrorMessageSearchCriteria;
import com.djt.hvac.domain.model.report.status.ReportEquipmentErrorMessageValueObject;
import com.djt.hvac.domain.model.report.status.ReportStatusValueObject;
import com.djt.hvac.domain.model.stripe.client.StripeClient;
import com.djt.hvac.domain.model.stripe.dto.StripeClientResponse;
import com.djt.hvac.domain.model.stripe.dto.StripeInvoice;
import com.djt.hvac.domain.model.stripe.dto.StripeSubscription;
import com.djt.hvac.domain.model.stripe.exception.StripeClientException;
import com.djt.hvac.domain.model.timeseries.client.MockTimeSeriesServiceClient;
import com.djt.hvac.domain.model.timeseries.client.TimeSeriesServiceClient;
import com.djt.hvac.domain.model.timeseries.exception.TimeSeriesClientException;
import com.djt.hvac.domain.model.user.repository.UserRepositoryFileSystemImpl;
import com.google.common.collect.Lists;

public class NodeHierarchyServiceImpl implements NodeHierarchyService {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(NodeHierarchyServiceImpl.class);
  
  private static final String CUSTOM_COMPUTED_POINT_INTERDAY_KAFKA_JOB_ID = "InterdayACP";
  private static final String CUSTOM_COMPUTED_POINT_INTRADAY_KAFKA_JOB_ID = "IntradayACP";
  
  private final DistributorRepository distributorRepository;
  private final RawPointRepository rawPointRepository;
  private final CustomerRepository customerRepository;
  private final NodeHierarchyRepository nodeHierarchyRepository;
  private final DictionaryRepository dictionaryRepository;
  private final StripeClient stripeClient;
  private final GeocodingClient geocodingClient;
  private final CacheClient cacheClient;
  private final TimeSeriesServiceClient timeSeriesServiceClient;
  private final NotificationService notificationService;
  private final ModelChangeEventPublisher eventPublisherDelegate;
  private final boolean performAutomaticConfigurationForPortfolioMaintenance;

  public NodeHierarchyServiceImpl(
      DistributorRepository distributorRepository,
      RawPointRepository rawPointRepository,
      CustomerRepository customerRepository,
      NodeHierarchyRepository nodeHierarchyRepository,
      DictionaryRepository dictionaryRepository,
      StripeClient stripeClient,
      GeocodingClient geocodingClient,
      CacheClient cacheClient,
      TimeSeriesServiceClient timeSeriesServiceClient,
      NotificationService notificationService,
      ModelChangeEventPublisher eventPublisherDelegate,
      boolean performAutomaticConfigurationForPortfolioMaintenance) {
    requireNonNull(distributorRepository, "distributorRepository cannot be null");
    requireNonNull(rawPointRepository, "rawPointRepository cannot be null");
    requireNonNull(customerRepository, "customerRepository cannot be null");
    requireNonNull(nodeHierarchyRepository, "nodeHierarchyRepository cannot be null");
    requireNonNull(dictionaryRepository, "dictionaryRepository cannot be null");
    requireNonNull(stripeClient, "stripeClient cannot be null");
    requireNonNull(geocodingClient, "geocodingClient cannot be null");
    requireNonNull(cacheClient, "cacheClient cannot be null");
    requireNonNull(timeSeriesServiceClient, "timeSeriesServiceClient cannot be null");
    requireNonNull(notificationService, "notificationService cannot be null");
    requireNonNull(eventPublisherDelegate, "eventPublisherDelegate cannot be null");
    this.distributorRepository = distributorRepository;
    this.rawPointRepository = rawPointRepository;
    this.customerRepository = customerRepository;
    this.nodeHierarchyRepository = nodeHierarchyRepository;
    this.dictionaryRepository = dictionaryRepository;
    this.stripeClient = stripeClient;
    this.geocodingClient = geocodingClient;
    this.cacheClient = cacheClient;
    this.timeSeriesServiceClient = timeSeriesServiceClient;
    this.notificationService = notificationService;
    this.eventPublisherDelegate = eventPublisherDelegate;
    this.performAutomaticConfigurationForPortfolioMaintenance = performAutomaticConfigurationForPortfolioMaintenance;
  }
  
  @Override
  public PortfolioEntity loadAggregateRoot(int persistentIdentity) throws EntityDoesNotExistException {
    
    return loadPortfolio(persistentIdentity);
  }

  @Override
  public void ensureDictionaryDataIsLoaded() {
    
    dictionaryRepository.ensureDictionaryDataIsLoaded();
  }
  
  @Override
  public PortfolioEntity loadPortfolio(Integer customerId) throws EntityDoesNotExistException {
    
    ensureDictionaryDataIsLoaded();
   
    return nodeHierarchyRepository.loadPortfolio(customerId);
  }

  @Override
  public PortfolioEntity loadPortfolio(
      LoadPortfolioOptions loadPortfolioOptions) 
  throws 
      EntityDoesNotExistException {
    
    return nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);
  }
  
  @Override
  public List<ValidationMessage> validatePortfolio(
      ValidatePortfolioRequest request) 
  throws 
      EntityDoesNotExistException {
    
    try {
      
      LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
          .builder()
          .withCustomerId(request.getCustomerId())
          .withLoadAdFunctionInstances(Boolean.TRUE)
          .withLoadReportInstances(Boolean.TRUE)
          .build();
      
      List<Integer> buildingIds = request.getBuildingIds();
      if (buildingIds != null && !buildingIds.isEmpty()) {

        loadPortfolioOptions = LoadPortfolioOptions
            .builder(loadPortfolioOptions)
            .withFilterNodeType(NodeType.BUILDING)
            .withFilterNodePersistentIdentities(buildingIds)
            .build();
      }    
      
      PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);
      
      return portfolio.validate(request.getIssueTypes());
      
    } catch (EntityDoesNotExistException e) {
      throw new RuntimeException("Unable to validate portfolio for customer with id: ["
          + request.getCustomerId() 
          + "], error: " 
          + e.getMessage(), e);
    }
  }

  @Override
  public List<ValidationMessage> remediatePortfolio(
      RemediatePortfolioRequest request) 
  throws 
      EntityDoesNotExistException,
      StaleDataException {
    
    LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
        .builder()
        .withCustomerId(request.getCustomerId())
        .withLoadAdFunctionInstances(Boolean.TRUE)
        .withLoadReportInstances(Boolean.TRUE)
        .build();
    
    List<Integer> buildingIds = request.getBuildingIds();
    if (buildingIds != null && !buildingIds.isEmpty()) {

      loadPortfolioOptions = LoadPortfolioOptions
          .builder(loadPortfolioOptions)
          .withFilterNodeType(NodeType.BUILDING)
          .withFilterNodePersistentIdentities(buildingIds)
          .build();
    }    
    
    PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);
    
    List<ValidationMessage> allValidationMessages = remediatePortfolio(
        portfolio, 
        request.getIssueTypes());

    // Intelligent store to the repository.
    updatePortfolio(
        portfolio,
        request);
    
    return allValidationMessages;
  }
  
  private List<ValidationMessage> remediatePortfolio(
      PortfolioEntity portfolio,
      Set<IssueType> issueTypes) 
  throws 
      EntityDoesNotExistException {
    
    try {

      // Phase One: Points (Point Template Associations and Haystack Tags)
      LOGGER.debug("REMEDIATE PHASE ONE: {}", portfolio);
      Set<IssueType> phaseOneIssueTypes = ValidationMessage.extractPhaseOneIssueTypes(issueTypes);
      List<ValidationMessage> phaseOneValidationMessages = portfolio.remediate(phaseOneIssueTypes);

      
      // Phase Two: Rule Candidates and Rule Instances
      LOGGER.debug("REMEDIATE PHASE TWO: {}", portfolio);
      Set<IssueType> phaseTwoIssueTypes = ValidationMessage.extractPhaseTwoIssueTypes(issueTypes);
      List<ValidationMessage> phaseTwoValidationMessages = portfolio.remediate(phaseTwoIssueTypes);
      
      
      // Return the number of issues resolved.
      List<ValidationMessage> allValidationMessages = new ArrayList<>();
      allValidationMessages.addAll(phaseOneValidationMessages);
      allValidationMessages.addAll(phaseTwoValidationMessages);
      int numIssuesResolved = phaseOneValidationMessages.size() + phaseTwoValidationMessages.size();
      LOGGER.debug("NUM ISSUES RESOLVED FOR {}: {}", portfolio, numIssuesResolved);
      return allValidationMessages;
      
    } catch (Exception e) {
      String message = ExceptionUtils.extractReason("Unable to remediate portfolio: ", e);
      throw new RuntimeException(message, e);
    }
  }  
  
  @Override
  public List<ReportInstanceStatusDto> evaluateReports(
      EvaluateReportsRequest request)
  throws 
      EntityDoesNotExistException,
      StaleDataException {
    
    long start = AbstractEntity.getTimeKeeper().getCurrentTimeInMillis();
    LOGGER.info("evaluateReports(): BEGIN");

    LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
        .builder()
        .withCustomerId(request.getCustomerId())
        .withLoadAdFunctionInstances(Boolean.TRUE)
        .withLoadReportInstances(Boolean.TRUE)
        .build();
    
    List<Integer> buildingIds = request.getBuildingIds();
    if (buildingIds != null && !buildingIds.isEmpty()) {

      loadPortfolioOptions = LoadPortfolioOptions
          .builder(loadPortfolioOptions)
          .withFilterNodeType(NodeType.BUILDING)
          .withFilterNodePersistentIdentities(buildingIds)
          .build();
    }    
    
    PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);
    
    Boolean performAutomaticRemediation = request.getPerformAutomaticRemediation();
    
    Boolean performAutomaticConfiguration = request.getPerformAutomaticConfiguration();
    
    ReportTemplateEntity reportTemplate = null;
    Integer reportTemplateId = request.getReportTemplateId();
    if (reportTemplateId != null) {
      
      reportTemplate = dictionaryRepository
          .getReportTemplatesContainer()
          .getReportTemplate(reportTemplateId);
    }
    
    if (performAutomaticRemediation.booleanValue()) {

      Set<IssueType> issueTypes = null;
      List<ValidationMessage> validationMessages = remediatePortfolio(
          portfolio, 
          issueTypes);
      
      if (LOGGER.isDebugEnabled() && !validationMessages.isEmpty()) {
        LOGGER.debug("Resolved {} issue(s)", validationMessages.size());
      }    
    }
    
    List<ReportInstanceEntity> entityList = PortfolioVisitor.evaluateReports(
        portfolio,
        reportTemplate);
    
    boolean isModified = portfolio.getIsModified();
    if (isModified 
        && performAutomaticConfiguration.booleanValue() 
        && portfolio.getParentCustomer().allowAutomaticConfiguration()) {
      
      updateReportInstancesNoStore(
          portfolio,
          new ArrayList<>());      
    }
    
    // Intelligent store to the repository.
    boolean reportsWereEvaluated = true;
    updatePortfolio(
        portfolio,
        request,
        reportsWereEvaluated);
    
    List<ReportInstanceStatusDto> modifiedReports = ReportInstanceEntity
        .Mapper
        .getInstance()
        .mapEntitiesToStatusDtos(entityList);

    LOGGER.info("evaluateReports(): updated report count: {} elapsed(ms): {}",
        modifiedReports.size(),
        (System.currentTimeMillis()-start));
    
    return modifiedReports;
  }
  
  @Override
  public String getFormattedReportTupleConstraintErrorMessage(
      Integer customerId,
      Integer buildingId, 
      Integer reportTemplateId, 
      Integer equipmentId)
  throws
      EntityDoesNotExistException {
    
    LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
        .builder()
        .withCustomerId(customerId)
        .withFilterNodeType(NodeType.BUILDING)
        .withFilterNodePersistentIdentity(buildingId)
        .withLoadAdFunctionInstances(Boolean.TRUE)
        .withLoadReportInstances(Boolean.TRUE)
        .build();
    
    PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);
    
    BuildingEntity building = portfolio.getChildBuilding(buildingId);
    
    ReportInstanceEntity reportInstance = building.getReportInstanceByReportTemplateId(reportTemplateId);
    
    EquipmentEntity equipment = portfolio.getEquipment(equipmentId);
    
    Map<Integer, String> equipmentMap = new HashMap<>();
    equipmentMap.put(equipmentId, "");
    
    Map<AbstractEnergyExchangeTypeEntity, Set<EnergyExchangeEntity>> typeMap = new HashMap<>();
    Set<EnergyExchangeEntity> set = new HashSet<>();
    set.add(equipment);
    typeMap.put(equipment.getEnergyExchangeTypeNullIfNotExists(), set);
    
    ReportEvaluator.evaluate(
        typeMap, 
        reportInstance,
        equipmentMap);
    
    return equipmentMap.get(equipmentId);
  }
  
  @Override
  public PortfolioReportSummaryValueObject getReportConfigurationStatus(
      Integer customerId,
      boolean noInternalReports,
      String rubyTimezoneLabel)
  throws
      EntityDoesNotExistException {
    
    return nodeHierarchyRepository.getReportConfigurationStatus(
        customerId, 
        noInternalReports,
        rubyTimezoneLabel);
  }

  @Override
  public Double getReportConfigurationStatusPercent(
      Integer customerId)
  throws
      EntityDoesNotExistException {

    boolean noInternalReports = true;
    String rubyTimezoneLabel = "Eastern Time (US & Canada)";
    PortfolioReportSummaryValueObject customerReportSummary = getReportConfigurationStatus(
            customerId,
            noInternalReports,
            rubyTimezoneLabel);
    
    double numGreenEquipment = 0;
    double numEquipmentTotal = 0;
    for (BuildingReportStatusValueObject buildingReportSummary: customerReportSummary.getBuildingReportStatuses()) {

      buildingReportSummary.getNumGreen();
      for (ReportStatusValueObject reportSummary: buildingReportSummary.getReports()) {

        numGreenEquipment = numGreenEquipment + reportSummary.getNumGreenEquipment();
        numEquipmentTotal = numEquipmentTotal + reportSummary.getNumEquipmentTotal();
      }
    }
    
    Double reportConfigPercent = Double.valueOf(0.0);
    if (numEquipmentTotal > 0) {
      reportConfigPercent = (numGreenEquipment / numEquipmentTotal) * 100;
    }
    
    return reportConfigPercent;
  }
  
  @Override
  public Double getEnabledReportsPercent(
      Integer customerId)
  throws
      EntityDoesNotExistException {

    boolean noInternalReports = true;
    String rubyTimezoneLabel = "Eastern Time (US & Canada)";
    PortfolioReportSummaryValueObject customerReportSummary = getReportConfigurationStatus(
            customerId,
            noInternalReports,
            rubyTimezoneLabel);
    
    double numDisabledReports = 0.0d;
    double numEnabledReports = 0.0d;
    for (BuildingReportStatusValueObject buildingReportSummary: customerReportSummary.getBuildingReportStatuses()) {

      for (ReportStatusValueObject reportSummary: buildingReportSummary.getReports()) {
        
        if (reportSummary.getIsEnabled()) {
          numEnabledReports++;
        } else {
          numDisabledReports++;
        }
      }
    }
    
    Double enabledReportsPercent = Double.valueOf(0.0);
    double numReportsTotal = numDisabledReports + numEnabledReports;
    if (numReportsTotal > 0) {
      enabledReportsPercent = (numEnabledReports / numReportsTotal) * 100;
    } else {
      enabledReportsPercent = Double.valueOf(100.0);
    }
    
    return enabledReportsPercent;
  }
  
  @Override
  public ReportEquipmentErrorMessageListResponse getReportEquipmentErrorMessages(
      Integer customerId,
      int buildingId, 
      int reportTemplateId,
      String nodePath,
      String sortDirection,
      int limit,
      int offset) {
    
    ReportEquipmentErrorMessageSearchCriteria searchCriteria = ReportEquipmentErrorMessageSearchCriteria
        .builder()
        .withBuildingId(buildingId)
        .withReportTemplateId(reportTemplateId)
        .withNodePath(nodePath)
        .withSortDirection(sortDirection)
        .withLimit(limit)
        .withOffset(offset)
        .build();
    
    searchCriteria.validate();

    int count = nodeHierarchyRepository.getReportEquipmentErrorMessagesCount(
        customerId,
        searchCriteria);

    List<ReportEquipmentErrorMessageValueObject> data = null;
    if (count > 0) {
      data = nodeHierarchyRepository.getReportEquipmentErrorMessages(
          customerId,
          searchCriteria);
    } else {
      data = new ArrayList<>();
    }

    return ReportEquipmentErrorMessageListResponse
        .builder()
        .withSearchCriteria(searchCriteria)
        .withCount(count)
        .withData(data)
        .build();
  }
  
  @Override
  public List<ReportInstanceEntity> updateReportInstances(UpdateReportInstancesRequest updateReportInstancesRequest) 
  throws 
      EntityDoesNotExistException,
      StaleDataException {
    
    List<ReportInstanceData> reportInstanceData = updateReportInstancesRequest.getData();
    
    // Load only the building in the request.
    List<Integer> buildingIds = new ArrayList<>();
    for (ReportInstanceData request: reportInstanceData) {
      buildingIds.add(request.getBuildingId());
    }
    
    LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
        .builder()
        .withCustomerId(updateReportInstancesRequest.getCustomerId())
        .withFilterNodeType(NodeType.BUILDING)
        .withFilterNodePersistentIdentities(buildingIds)
        .withLoadAdFunctionInstances(Boolean.TRUE)
        .withLoadReportInstances(Boolean.TRUE)
        .build();
    
    PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);

    List<ReportInstanceEntity> affectedReportInstances = updateReportInstancesNoStore(
        portfolio, 
        updateReportInstancesRequest.getData());
    
    // Intelligent store to the repository.
    updatePortfolio(
        portfolio,
        updateReportInstancesRequest);
    
    return affectedReportInstances;
  }
  
  private List<ReportInstanceEntity> updateReportInstancesNoStore(
      PortfolioEntity portfolio, 
      List<ReportInstanceData> pReportInstanceDataList) 
  throws 
      EntityDoesNotExistException {
    
    /*
     * REPORT STATE:
     * =============
     *              VALID/ENABLED ===== (state #2)   At least 1 GREEN Equipment - "enabled" state
     *                /\    ||       ||
     *                ||    \/       ||
     *   BEGIN-1:  VALID/DISABLED    || (state #1)   At least 1 GREEN Equipment - "disabled" state
     *                ||    /\       ||
     *                \/    ||       ||
     *   BEGIN-2: INVALID/DISABLED <=== (state #3)   Zero Equipment OR no GREEN Equipment - "invalid" state
     *   
     *   NOTE: Reports can be "ignored" when in the invalid/disabled or valid/disabled state
     *   
     *   
     * REPORT PRIORITY:
     * ================
     * Low, Medium or High: The report priority does not affect functionality (it only affects sort order for the getters)
     *
     *
     * REPORT STATUS: (derived/evaluated on-demand)
     * ============================================
     *   GREEN: 1 or more pieces of equipment where GREEN status is between 70-100% (inclusive)
     *  YELLOW: 1 or more pieces of equipment where GREEN status is between 0-70% (exclusive)
     *     RED: 0 or more pieces of equipment where either 0 pieces of equipment or NONE with GREEN status
     *
     *
     * EQUIPMENT STATUS: (derived/evaluated on-demand)
     * ===============================================
     *  GREEN: All required point specs have been matched
     *    RED: At least 1 required point spec has not been matched
     */
    
    // Report instance data to update: priority, enabled, disabled and ignored attributes.
    //
    // If the request is empty, then that means automatically enable any
    // report instances that are both valid and disabled.
    List<ReportInstanceData> reportInstanceDataList = initReportInstanceDataList(portfolio, pReportInstanceDataList);
    
    List<ReportInstanceEntity> affectedReportInstances = new ArrayList<>();
    for (ReportInstanceData reportInstanceData: reportInstanceDataList) {
      
      ReportInstanceEntity affectedReportInstance = updateReportInstance(portfolio, reportInstanceData);
      if (affectedReportInstance != null) {
        
        affectedReportInstances.add(affectedReportInstance);
      }
    }
    
    return affectedReportInstances;
  }

  /*
   * If the request is empty, then that means automatically enable any
   * report instances that are both valid and disabled.
   */
  private List<ReportInstanceData> initReportInstanceDataList(
      PortfolioEntity portfolio,
      List<ReportInstanceData> pReportInstanceDataList) {
    
    List<ReportInstanceData> reportInstanceDataList = null;
    if (pReportInstanceDataList != null && pReportInstanceDataList.isEmpty()) {
      
      reportInstanceDataList = new ArrayList<>();
      for (BuildingEntity building: portfolio.getAllBuildings()) {
        
        for (Integer reportTemplateId: DictionaryContext.getReportTemplatesContainer().getReportTemplateIds()) {

          reportInstanceDataList.add(ReportInstanceData
              .builder()
              .withBuildingId(building.getPersistentIdentity())
              .withReportTemplateId(reportTemplateId)
              .withState(ReportState.ENABLED.toString())
              .build());
        }
      }
    } else {
      reportInstanceDataList = pReportInstanceDataList;
    }
    return reportInstanceDataList;
  }
  
  /*
   * 
   */
  private ReportInstanceEntity updateReportInstance(
      PortfolioEntity portfolio,
      ReportInstanceData reportInstanceData) throws EntityDoesNotExistException {
    
    Integer buildingId = reportInstanceData.getBuildingId();
    Integer reportTemplateId = reportInstanceData.getReportTemplateId();
    
    BuildingEntity building = portfolio.getChildBuilding(buildingId);
    
    ReportInstanceEntity affectedReportInstance = null;
    ReportInstanceEntity reportInstance = building.getReportInstanceByReportTemplateId(reportTemplateId);
    
    
    // REGARDLESS OF STATE, SET THE PRIORITY (IF SPECIFIED)
    if (reportInstanceData.getPriority() != null) {
      
      reportInstance.setPriority(ReportPriority.get(reportInstanceData.getPriority()));
      affectedReportInstance = reportInstance;
    }

    // Update the report state: enabled, disabled or ignored.
    ReportState reportState = null;
    if (reportInstanceData.getState() != null) {
      
      reportState = ReportState.get(reportInstanceData.getState());
      affectedReportInstance = updateReportInstanceState(reportState, reportInstanceData, reportInstance);
    }
    
    return affectedReportInstance;
  }
  
  private ReportInstanceEntity updateReportInstanceState(
      ReportState reportState,
      ReportInstanceData reportInstanceData,
      ReportInstanceEntity reportInstance) {
    
    ReportInstanceEntity affectedReportInstance = null;
    
    // CURRENT STATE
    boolean isValid = reportInstance.isValid();
    boolean isEnabled = reportInstance.isEnabled();
    boolean isIgnored = reportInstance.isIgnored();
    
    // Ignore a report only if it is disabled.
    if (reportState.equals(ReportState.IGNORED)) {
      
      if (isEnabled) {
        
        throw new IllegalStateException("Cannot ignore report: ["
            + reportInstance
            + "] because it needs to be disabled first.");
        
      } else if (!isIgnored) {
        
        reportInstance.setDisabled();
        reportInstance.setIsIgnored();
        reportInstance.setRedEquipment(new HashSet<>());
        reportInstance.setGreenEquipment(new HashSet<>());
        affectedReportInstance = reportInstance;
        
      }
      
    } else if (reportState.equals(ReportState.ENABLED) && !isEnabled && isValid) {
      
      // Enable a report only if it is valid.
      reportInstance.setEnabled();
      affectedReportInstance = reportInstance;
      
    } else if (reportState.equals(ReportState.DISABLED) && isEnabled) {
     
      reportInstance.setDisabled();
      affectedReportInstance = reportInstance;
      
    }
    
    // We automatically disable a report if it is currently enabled but isn't valid anymore.
    if (isEnabled && !isValid) {

      LOGGER.warn("Detected enabled report instance: ["
          + reportInstance
          + "], that is in an invalid state, automatically disabling");
      
      reportInstance.setDisabled();
      affectedReportInstance = reportInstance;
      
    }
    return affectedReportInstance;
  }
  
  @Override
  public List<AdFunctionInstanceDto> findAdFunctionInstanceCandidates(
      FindAdFunctionInstanceCandidatesRequest request) 
  throws
      EntityDoesNotExistException,
      StaleDataException {
    
    Set<Integer> buildingIdsToProcess = new HashSet<>();
    
    LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
        .builder()
        .withCustomerId(request.getCustomerId())
        .withLoadAdFunctionInstances(Boolean.TRUE)
        .build();
    
    List<Integer> buildingIds = request.getBuildingIds();
    if (buildingIds == null) {
      buildingIds = new ArrayList<>();
    }
    
    Integer buildingId = request.getBuildingId();
    if (buildingId != null) {
      buildingIds.add(buildingId); 
    }
    
    if (buildingIds != null && !buildingIds.isEmpty()) {
      buildingIdsToProcess.addAll(buildingIds);
    }
    
    if (buildingIds.isEmpty()) {
      buildingIdsToProcess = null;
    }
    
    loadPortfolioOptions = LoadPortfolioOptions
        .builder(loadPortfolioOptions)
        .withFilterNodeType(NodeType.BUILDING)
        .withFilterNodePersistentIdentities(buildingIds)
        .build();    

    PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);       
    
    List<AdFunctionInstanceDto> dtoList = PortfolioVisitor.findAdFunctionInstanceCandidates(
        portfolio,
        request.getFunctionType(),
        buildingIdsToProcess);
    
    // Intelligent store to the repository.
    updatePortfolio(
        portfolio,
        request);
    
    return dtoList;
  }

  @Override
  public List<AbstractAdFunctionInstanceEntity> createAdFunctionInstancesFromCandidates(
      CreateAdFunctionInstancesRequest request)
  throws
      EntityDoesNotExistException,
      StaleDataException {

    LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
        .builder()
        .withCustomerId(request.getCustomerId())
        .build();
    
    List<Integer> buildingIds = request.getBuildingIds();
    if (buildingIds != null && !buildingIds.isEmpty()) {

      loadPortfolioOptions = LoadPortfolioOptions
          .builder(loadPortfolioOptions)
          .withFilterNodeType(NodeType.BUILDING)
          .withFilterNodePersistentIdentities(buildingIds)
          .build();
    }    
    
    Boolean performAutomaticRemediation = request.getPerformAutomaticRemediation();
    
    Boolean performAutomaticEvaluateReports = request.getPerformAutomaticEvaluateReports();
    
    Boolean performAutomaticConfiguration = request.getPerformAutomaticConfiguration();
    
    Integer equipmentId = request.getEquipmentId();
    if (equipmentId != null) {
      
      loadPortfolioOptions = LoadPortfolioOptions
          .builder(loadPortfolioOptions)
          .withFilterNodeType(NodeType.EQUIPMENT)
          .withFilterNodePersistentIdentity(equipmentId)
          .build();
      
      // Don't do any report evaluation when this is an equipment level operation.
      performAutomaticEvaluateReports = Boolean.FALSE;
    }
    
    loadPortfolioOptions = LoadPortfolioOptions
        .builder(loadPortfolioOptions)
        .withLoadAdFunctionInstances(Boolean.TRUE)
        .withLoadReportInstances(performAutomaticEvaluateReports)
        .build();
    
    PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);
    
    Map<Integer, Set<Integer>> candidateTemplateEquipmentIds = request.getCandidateTemplateEquipmentIds();
    List<Integer> candidateIds = request.getCandidateIds();

    List<AbstractAdFunctionInstanceEntity> allCandidates = portfolio.getAllAdFunctionInstanceCandidates();
    List<AbstractAdFunctionInstanceEntity> selectedCandidates = new ArrayList<>();
    if (candidateTemplateEquipmentIds != null && !candidateTemplateEquipmentIds.isEmpty()) {

      for (AbstractAdFunctionInstanceEntity candidate: allCandidates) {
        
        Integer templateId = candidate.getAdFunctionTemplate().getPersistentIdentity();
        Set<Integer> equipmentIds = candidateTemplateEquipmentIds.get(templateId);
        if (equipmentIds != null 
            && equipmentIds.contains(candidate.getEquipment().getPersistentIdentity())) {
          
          selectedCandidates.add(candidate);
        }
      }
    } else if (candidateIds != null && !candidateIds.isEmpty()) {
      
      for (AbstractAdFunctionInstanceEntity candidate: allCandidates) {
        
        if (candidateIds.contains(candidate.getPersistentIdentity())) {
          
          selectedCandidates.add(candidate);
        }
      }
    } else {
      
      // RP-9411: When nothing is supplied in the request, then create instances from all available candidates.
      FunctionType functionType = FunctionType.get(request.getFunctionType());
      for (AbstractAdFunctionInstanceEntity candidate: allCandidates) {
        
        if (candidate.getAdFunctionTemplate().getAdFunction().getFunctionType().equals(functionType)
            && isEligibleForEnableAll(candidate, request)) {
          
          selectedCandidates.add(candidate);
        }
      }
    }
    
    return createAdFunctionInstances(
        portfolio,
        request,
        selectedCandidates,
        performAutomaticRemediation,
        performAutomaticEvaluateReports,
        performAutomaticConfiguration);    
  }
  
  // RP-10341: Add filtering for "enable all" of node path, equipment type id and/or AD function template id
  private boolean isEligibleForEnableAll(
      AbstractAdFunctionInstanceEntity candidate,
      CreateAdFunctionInstancesRequest request) {
    
    boolean isEligibleForEnableAll = false;
    EnergyExchangeEntity equipment = candidate.getEquipment();
    String nodePath = request.getNodePath();
    Integer equipmentTypeId = request.getEquipmentTypeId();
    Integer adFunctionTemplateId = request.getAdFunctionTemplateId();
    
    if ((nodePath.equals(CreateAdFunctionInstancesRequest.NODE_PATH_ALL) 
            || nodePath.equals(equipment.getNodePath()))
        
        && (equipmentTypeId.equals(CreateAdFunctionInstancesRequest.EQUIPMENT_TYPE_ID_ALL) 
            || equipmentTypeId.equals(equipment.getEnergyExchangeTypeNullIfNotExists().getPersistentIdentity()))
        
        && (adFunctionTemplateId.equals(CreateAdFunctionInstancesRequest.AD_FUNCTION_TEMPLATE_ID_ALL) 
            || adFunctionTemplateId.equals(candidate.getAdFunctionTemplate().getPersistentIdentity()))) {
      
      isEligibleForEnableAll = true;
    }
    return isEligibleForEnableAll;
  }
  
  @Override
  public List<AbstractAdFunctionInstanceEntity> updateAdFunctionInstances(
      UpdateAdFunctionInstancesRequest request)
  throws 
      EntityDoesNotExistException,
      StaleDataException {
    
    if (request.getData() == null || request.getData().isEmpty()) {
      return new ArrayList<>();
    }
    
    LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
        .builder()
        .withCustomerId(request.getCustomerId())
        .build();
    
    List<Integer> buildingIds = request.getBuildingIds();
    if (buildingIds != null && !buildingIds.isEmpty()) {

      loadPortfolioOptions = LoadPortfolioOptions
          .builder(loadPortfolioOptions)
          .withFilterNodeType(NodeType.BUILDING)
          .withFilterNodePersistentIdentities(buildingIds)
          .build();
    }    
    
    Boolean performAutomaticRemediation = request.getPerformAutomaticRemediation();
    
    Boolean performAutomaticEvaluateReports = request.getPerformAutomaticEvaluateReports();
    
    Boolean performAutomaticConfiguration = request.getPerformAutomaticConfiguration();
    
    Integer equipmentId = request.getEquipmentId();
    if (equipmentId != null) {
      
      loadPortfolioOptions = LoadPortfolioOptions
          .builder(loadPortfolioOptions)
          .withFilterNodePersistentIdentity(equipmentId)
          .withFilterNodeType(NodeType.EQUIPMENT)
          .build();
      
      performAutomaticRemediation = Boolean.FALSE;
      performAutomaticEvaluateReports = Boolean.FALSE;
      performAutomaticConfiguration = Boolean.FALSE;
    }

    loadPortfolioOptions = LoadPortfolioOptions
        .builder(loadPortfolioOptions)
        .withLoadAdFunctionInstances(Boolean.TRUE)
        .withLoadReportInstances(performAutomaticEvaluateReports)
        .build();
    
    PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);
    
    Map<String, AdFunctionInstanceData> requestItemMap = new HashMap<>();
    Set<String> ignoredAdFunctionCombinations = new HashSet<>();
    for (AdFunctionInstanceData requestItem: request.getData()) {
      
      String key = requestItem.getKey();
      requestItemMap.put(key, requestItem);
      
      if (requestItem.getIgnore()) {
        
        ignoredAdFunctionCombinations.add(key);
      }
    }

    Map<String, AbstractAdFunctionInstanceEntity> instancesToUpdate = new HashMap<>();
    for (AbstractAdFunctionInstanceEntity instance: portfolio.getAllAdFunctionInstances()) {
      
      String key = AdFunctionInstanceData.buildKey(
          instance.getEquipment().getPersistentIdentity(), 
          instance.getAdFunctionTemplate().getPersistentIdentity());
      
      AdFunctionInstanceData requestItem = requestItemMap.get(key);
      if (requestItem != null) {
        
        // Add to the list of instances to "copy on write".
        instancesToUpdate.put(key, instance);

        // Mark the old instance as deleted, which means that its 'active' flag is set to false and
        // its 'effective end date' is set to the current timestamp (i.e. we do a "copy on write").
        instance.setIsDeleted();
        
        // AD function instances can only be ignored if invalid/disabled or valid/disabled.
        if (requestItem.getIgnore()) {
          
          throw new IllegalStateException("Cannot ignore AD function instance: ["
              + instance
              + "] because it needs to be disabled first.");
          
        } else {
          
          // Update the constants, by id, as given in the request.
          for (Map.Entry<Integer, String> entry: requestItem.getDataByConstantIds().entrySet()) {
            
            Integer constantId = entry.getKey();
            String constantValue = entry.getValue();
            
            AdFunctionInstanceInputConstantEntity inputConstant = instance.getInputConstantByTemplateConstantId(constantId);
            
            inputConstant.setValue(constantValue);
          }
        }
      }
    }
    
    // Make sure that every combination to ignore is taken care of, as the combination may not exist as a candidate/instance
    // (i.e. there are error messages associated with the combination)  We create a special case of AD function instance candidate
    // that cannot be enabled, as it has the "ignored" attribute set.
    for (String key: ignoredAdFunctionCombinations) {
      
      AbstractAdFunctionInstanceEntity candidate = instancesToUpdate.get(key);
      if (candidate == null) {

        int idx = key.indexOf("_");
        Integer energyExchangeId = Integer.parseInt(key.substring(0, idx));
        Integer adFunctionTemplateId = Integer.parseInt(key.substring(idx+1));

        EnergyExchangeEntity energyExchangeEntity = portfolio.getEnergyExchangeSystemNode(energyExchangeId);
        AbstractAdFunctionTemplateEntity adFunctionTemplate = DictionaryContext.getAdFunctionTemplatesContainer().getAdFunctionTemplate(adFunctionTemplateId);
        
        if (adFunctionTemplate instanceof AdRuleFunctionTemplateEntity) {
          
          candidate = new AdRuleFunctionInstanceEntity(
              null,
              energyExchangeEntity,
              (AdRuleFunctionTemplateEntity)adFunctionTemplate,
              true, // isCandidate
              true, // isIgnored
              adFunctionTemplate.getVersion(),
              Integer.valueOf(1)); // instanceVersion          
          
        } else if (adFunctionTemplate instanceof AdComputedPointFunctionTemplateEntity) {
          
          candidate = new AdComputedPointFunctionInstanceEntity(
              null,
              energyExchangeEntity,
              (AdComputedPointFunctionTemplateEntity)adFunctionTemplate,
              true, // isCandidate
              true, // isIgnored
              adFunctionTemplate.getVersion(),
              Integer.valueOf(1)); // instanceVersion
          
        }
        
        if (candidate != null) {

          try {
            energyExchangeEntity.addAdFunctionInstanceCandidate(candidate);
            energyExchangeEntity.removeAdFunctionErrorMessages(candidate.getAdFunctionTemplate());
          } catch (EntityAlreadyExistsException eaee) {
            throw new IllegalStateException("Unable to ignore: " + candidate + " because the same candidate already exists", eaee);
          }
        }
      }
    }
    
    // Verify that we have the same number of instances to update as were in the request.
    if (request.getData().size() != (instancesToUpdate.size() + ignoredAdFunctionCombinations.size())) {
      
      throw new IllegalStateException("Mismatch between number of instances to update from request: ["
          + request.getData().size()
          + "] and the number of instances to update from the portfolio: ["
          + instancesToUpdate.size()
          + "]");
    }
    
    // Create a new instance using a copy of the old instance, but where the 
    // constants are updated, as specified in the request.
    return createAdFunctionInstances(
        portfolio,
        request,
        instancesToUpdate.values(),
        performAutomaticRemediation,
        performAutomaticEvaluateReports,
        performAutomaticConfiguration);    
  }
  
  private List<AbstractAdFunctionInstanceEntity> createAdFunctionInstances(
      PortfolioEntity portfolio,
      NodeHierarchyCommandRequest request,
      Collection<AbstractAdFunctionInstanceEntity> instances,
      Boolean performAutomaticRemediation,
      Boolean performAutomaticEvaluateReports,
      Boolean performAutomaticConfiguration)
  throws 
      EntityDoesNotExistException,
      StaleDataException {
    
    List<AbstractAdFunctionInstanceEntity> createdInstances = createAdFunctionInstancesNoStore(
        portfolio, 
        instances, 
        Boolean.FALSE);
    
    boolean isModified = portfolio.getIsModified();

    // If specified, then automatically perform validation/remediation.
    if (isModified && performAutomaticRemediation) {

      Set<IssueType> issueTypes = null;
      List<ValidationMessage> validationMessages = remediatePortfolio(
          portfolio, 
          issueTypes);
      
      PortfolioVisitor.findAdFunctionInstanceCandidates(portfolio);
      
      if (LOGGER.isDebugEnabled() && !validationMessages.isEmpty()) {
        LOGGER.debug("Resolved {} issue(s)", validationMessages.size());
      }    
    }
    
    // If specified, then automatically enable/instantiate any available 
    // AD rule/computed point function candidates.
    if (isModified && performAutomaticConfiguration && portfolio.getParentCustomer().allowAutomaticConfiguration()) {

      List<AbstractAdFunctionInstanceEntity> autoCreatedInstances = createAdFunctionInstancesNoStore(
          portfolio,
          portfolio.getAllAdFunctionInstanceCandidates(),
          Boolean.FALSE);
      
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Created {} AD function instance(s)", autoCreatedInstances.size());
      }
    }
    
    // If specified, then automatically evaluate the state of the portfolio for reports.
    if (isModified && performAutomaticEvaluateReports) {
      
      List<ReportInstanceEntity> modifiedReportInstances = PortfolioVisitor.evaluateReports(portfolio);
      
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Modified {} report instance(s)", modifiedReportInstances.size());
      }
    }
    
    // If specified, then automatically enable any valid/disabled report instances.
    if (isModified && performAutomaticConfiguration && portfolio.getParentCustomer().allowAutomaticConfiguration()) {
      updateReportInstancesNoStore(
          portfolio,
          new ArrayList<>());      
    }     
    
    // Intelligent store to the repository.
    updatePortfolio(
        portfolio,
        request,
        performAutomaticEvaluateReports);
    
    return createdInstances;
  }
  
  private List<AbstractAdFunctionInstanceEntity> createAdFunctionInstancesNoStore(
      PortfolioEntity portfolio,
      Collection<AbstractAdFunctionInstanceEntity> instances,
      Boolean performAutomaticEvaluateReports) {
    
    NodeTagTemplatesContainer nodeTagTemplatesContainer = dictionaryRepository.getNodeTagTemplatesContainer();
    
    List<AbstractAdFunctionInstanceEntity> createdInstances = new ArrayList<>();
    for (AbstractAdFunctionInstanceEntity instance: instances) {
      
      createdInstances.add(AbstractAdFunctionInstanceEntity.createAdFunctionInstance(
          nodeTagTemplatesContainer,
          portfolio,
          instance));
    }
    
    // If specified, then automatically evaluate the state of the portfolio for reports.
    if (portfolio.getIsModified() && performAutomaticEvaluateReports) {
      
      List<ReportInstanceEntity> modifiedReportInstances = PortfolioVisitor.evaluateReports(portfolio);
      
      if (LOGGER.isDebugEnabled()) {
        
        LOGGER.debug("Modified {} report instance(s)", modifiedReportInstances.size());
      }
    }
    
    return createdInstances;
  }  
  
  @Override
  public List<AbstractAdFunctionInstanceEntity> deleteAdFunctionInstances(
      DeleteAdFunctionInstancesRequest request) 
  throws 
      EntityDoesNotExistException,
      StaleDataException {
    
    if (request.getData() == null || request.getData().isEmpty()) {
      return new ArrayList<>();
    }
    
    // Load the portfolio (down to the point level).
    long start = System.currentTimeMillis();
    
    LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
        .builder()
        .withCustomerId(request.getCustomerId())
        .build();
    
    List<Integer> buildingIds = request.getBuildingIds();
    if (buildingIds != null && !buildingIds.isEmpty()) {

      loadPortfolioOptions = LoadPortfolioOptions
          .builder(loadPortfolioOptions)
          .withFilterNodeType(NodeType.BUILDING)
          .withFilterNodePersistentIdentities(buildingIds)
          .build();
    }    
    
    Boolean performAutomaticRemediation = request.getPerformAutomaticRemediation();
    
    Boolean performAutomaticEvaluateReports = request.getPerformAutomaticEvaluateReports();
    
    Boolean performAutomaticConfiguration = request.getPerformAutomaticConfiguration();
    
    loadPortfolioOptions = LoadPortfolioOptions
        .builder(loadPortfolioOptions)
        .withLoadAdFunctionInstances(Boolean.TRUE)
        .withLoadReportInstances(performAutomaticEvaluateReports)
        .build();
    
    PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);
    
    Set<Integer> instanceIds = new HashSet<>();
    instanceIds.addAll(request.getData());
    
    Set<FunctionType> functionTypes = new HashSet<>(); 
    List<AbstractAdFunctionInstanceEntity> deletedInstances = new ArrayList<>();
    Set<Integer> deletedInstanceIds = new HashSet<>();
    for (AbstractAdFunctionInstanceEntity instance: portfolio.getAllAdFunctionInstances()) {
      
      Integer id = instance.getPersistentIdentity();
      if (instanceIds.contains(id)) {
        
        instance.setIsDeleted();
        instance.getEquipment().addDeletedAdFunctionInstance(instance);
        deletedInstances.add(instance);
        deletedInstanceIds.add(id);
        
        functionTypes.add(instance
            .getAdFunctionTemplate()
            .getAdFunction()
            .getFunctionType());
      }
    }
    
    if (!instanceIds.equals(deletedInstanceIds)) {
      
      throw new RuntimeException("List of actually deleted instance ids: "
          + deletedInstanceIds
          + " does not match list of instance ids to delete from the request: "
          + instanceIds);
    }
    
    // RP-9032: Automatically find AD function candidates after deactivating AD function instances.
    List<AdFunctionInstanceDto> candidates = PortfolioVisitor.findAdFunctionInstanceCandidates(portfolio);

    LOGGER.info("Found [{}] AD function candidates for customer: [{}]",
        candidates.size(),
        portfolio.getCustomerId());
    
    // Perform inline remediation if modified.
    boolean isModified = portfolio.getIsModified();
    if (isModified && performAutomaticRemediation) {

      Set<IssueType> issueTypes = null;
      List<ValidationMessage> validationMessages = remediatePortfolio(
          portfolio, 
          issueTypes);
      
      if (LOGGER.isDebugEnabled() && !validationMessages.isEmpty()) {
        LOGGER.debug("Resolved {} issue(s)", validationMessages.size());
      }    
    }

    // If specified, then automatically evaluate the state of the portfolio for reports.
    if (isModified && performAutomaticEvaluateReports) {
      
      List<ReportInstanceEntity> modifiedReportInstances = PortfolioVisitor.evaluateReports(portfolio);
      
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Modified {} report instance(s)", modifiedReportInstances.size());
      }
    }
    
    // If specified, then automatically enable any valid/disabled report instances.
    if (isModified && performAutomaticConfiguration && portfolio.getParentCustomer().allowAutomaticConfiguration()) {
      updateReportInstancesNoStore(
          portfolio,
          new ArrayList<>());      
    }     
    
    // Intelligent store to the repository.
    updatePortfolio(
        portfolio,
        request,
        performAutomaticEvaluateReports);
    
    LOGGER.info("deleteAdFunctionInstances(): {}: elapsed(ms): {}",
        AbstractEntity.getTimeKeeper().getCurrentLocalDate(),
        (System.currentTimeMillis()-start));
    
    return deletedInstances;
  }
  
  @Override
  public PortfolioEntity createPortfolio(
      AbstractCustomerEntity parentCustomer,
      String name,
      String displayName)
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException {
    
    return nodeHierarchyRepository.createPortfolio(
        parentCustomer, 
        name, 
        displayName);
  }

  @Override
  public NodeHierarchyChangeEvent updatePortfolio(
      PortfolioEntity portfolio,
      NodeHierarchyCommandRequest commandRequest)
  throws
      StaleDataException {
    
    Map<String, List<AbstractNodeEntity>> storePortfolioResultsMap = nodeHierarchyRepository.storePortfolio(
        portfolio, 
        commandRequest,
        false);  
    
    // Create, and publish, a node hierarchy change event that contains nodes that were inserted, 
    // updated or deleted (as well as any enabled/disabled AD function instances and report instances).
    NodeHierarchyChangeEvent event = publishRepositoryEvent(
        portfolio, 
        commandRequest, 
        storePortfolioResultsMap);
    
    // Store the event to the repository
    nodeHierarchyRepository.storeNodeHierarchyChangeEvent(event);
    
    return event;
  }

  private List<RawPointEntity> getRawPointEntitiesFromRequest(
      AbstractCustomerEntity customer,
      MapRawPointsRequest request) {
    
    // Retrieve the raw point entities corresponding to the raw point ids in the request.
    // If empty, retrieve all unmapped points
    List<RawPointEntity> rawPoints = new ArrayList<>();
    if (request.getRawPoints().isEmpty()) {
      
      rawPoints = rawPointRepository.loadRawPoints(
          customer, 
          true, //loadUnmappedOnly 
          false, //loadIgnored 
          false); //loadDeleted
      
    } else {
      
      List<Integer> rawPointIds = new ArrayList<>();
      for (RawPointData rpd: request.getRawPoints()) {
        rawPointIds.add(rpd.getRawPointId());
      }
      rawPoints = rawPointRepository.loadRawPoints(
          request.getCustomerId(), 
          rawPointIds);
      
    }
    return rawPoints;
  }
  
  @Override
  public Collection<RawPointEntity> getEligibleRawPointsForMapping(
      MapRawPointsRequest request)
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException {
    
    LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
        .builder()
        .withCustomerId(request.getCustomerId())
        .withDepthNodeType(NodeType.POINT)
        .build();
    
    List<Integer> buildingIds = request.getBuildingIds();
    if (buildingIds != null && !buildingIds.isEmpty()) {

      loadPortfolioOptions = LoadPortfolioOptions
          .builder(loadPortfolioOptions)
          .withFilterNodeType(NodeType.BUILDING)
          .withFilterNodePersistentIdentities(buildingIds)
          .build();
    }    
    
    PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);      
    
    // Retrieve the raw point entities corresponding to the raw point ids in the request.
    List<RawPointEntity> rawPointsToProcess = getRawPointEntitiesFromRequest(
        portfolio.getParentCustomer(),
        request);
    
    // Perform node name filtering based upon what is in the request.  This is done regardless of customer type.
    Map<RawPointEntity, List<Node>> eligibleRawPointsMap = RawPointMappingNodeNameFilter.getEligibleRawPoints(
        portfolio, 
        rawPointsToProcess, 
        request);
    
    // If an online customer, then we must perform point cap limiting on the eligible points, so that they are under the point cap.
    return BillableBuildingPointLimiter.processForBuildingPointCaps(
        portfolio, 
        eligibleRawPointsMap,
        request)
        .keySet();
  }

  @Override
  public List<MappablePointEntity> mapRawPoints(
      MapRawPointsRequest request)
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException, 
      StaleDataException {
    
    long start = System.currentTimeMillis();
    
    LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
        .builder()
        .withCustomerId(request.getCustomerId())
        .withDepthNodeType(NodeType.POINT)
        .withLoadDistributorPaymentMethods(Boolean.TRUE)
        .withLoadDistributorUsers(Boolean.TRUE)
        .build();
    
    List<Integer> buildingIds = request.getBuildingIds();
    if (buildingIds != null && !buildingIds.isEmpty()) {

      loadPortfolioOptions = LoadPortfolioOptions
          .builder(loadPortfolioOptions)
          .withFilterNodeType(NodeType.BUILDING)
          .withFilterNodePersistentIdentities(buildingIds)
          .build();
    }    
    
    PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);   
    
    // Retrieve the raw point entities corresponding to the raw point ids in the request.
    List<RawPointEntity> rawPointsToProcess = getRawPointEntitiesFromRequest(
        portfolio.getParentCustomer(),
        request);
    
    // Perform node name filtering based upon what is in the request.  This is done regardless of customer type.
    Map<RawPointEntity, List<Node>> eligibleRawPointsMap = RawPointMappingNodeNameFilter.getEligibleRawPoints(
        portfolio, 
        rawPointsToProcess, 
        request);
    
    // If an online customer, then we must perform point cap limiting on the eligible points, so that they are under the point cap.
    if (portfolio.getParentCustomer() instanceof OnlineCustomerEntity) {
      
      eligibleRawPointsMap = BillableBuildingPointLimiter.processForBuildingPointCaps(
          portfolio, 
          eligibleRawPointsMap,
          request);    
    }

    /*
     POSSIBLE PARENT/CHILD COMBINATIONS:
     ===================================
     building/subBuilding
     building/plant
     building/floor
     building/equipment
     building/point
     
     subBuilding/plant
     subBuilding/floor
     subBuilding/equipment
     subBuilding/point
     
     plant/floor
     plant/equipment
     plant/point

     floor/equipment
     floor/point

     equipment/equipment
     equipment/point
     

     MAPPER MUST HANDLE THE SCENARIO WHERE A POINT WITH THE GIVEN 
     NAME ALREADY EXISTS FOR THE GIVEN PARENT NODE 
     */
    List<MappablePointEntity> createdMappablePoints = new ArrayList<>();
    for (Map.Entry<RawPointEntity, List<Node>> entry: eligibleRawPointsMap.entrySet()) {
      
      RawPointEntity rawPoint = entry.getKey();
      List<Node> nodes = entry.getValue();

      AbstractNodeEntity parentNode = portfolio;
      
      int size = nodes.size();
      for (int i = 0; i < size; i++) {
        
        Node node = nodes.get(i);
        
        com.djt.hvac.domain.model.common.dsl.pointmap.NodeType nodeType = node.getType();
        String name = node.getName();
        String displayName = node.getDisplayName();
        
        // If the current parent node is a plant node and the current child node
        // to be created is not a point, then make the parent be the immediate parent
        // of the plant, as only points can exist under plants in the node hierarchy.
        if (parentNode instanceof PlantEntity && 
            (nodeType.equals(com.djt.hvac.domain.model.common.dsl.pointmap.NodeType.FLOOR) 
                || nodeType.equals(com.djt.hvac.domain.model.common.dsl.pointmap.NodeType.EQUIPMENT))) {
          
          parentNode = parentNode.getParentNode();
        }
        
        if (parentNode instanceof PortfolioEntity && nodeType.equals(com.djt.hvac.domain.model.common.dsl.pointmap.NodeType.BUILDING)) {
          
          BuildingEntity building = portfolio.getChildBuildingByNameNullIfNotExists(name);
          if (building == null) {
            building = portfolio.addChildBuilding(
                name, 
                displayName);
          }
          parentNode = building;
          
        } else if (parentNode instanceof BuildingEntity && nodeType.equals(com.djt.hvac.domain.model.common.dsl.pointmap.NodeType.SUB_BUILDING)) {
          
          BuildingEntity parentBuilding = ((BuildingEntity)parentNode);
          SubBuildingEntity subBuilding = parentBuilding.getChildSubBuildingByNameNullIfNotExists(name);
          if (subBuilding == null) {
            
            subBuilding = new SubBuildingEntity(
                parentBuilding,
                name,
                displayName);
            portfolio.addNodeToParentAndIndex(parentBuilding, subBuilding);
          }
          parentNode = subBuilding;
          
        } else if (parentNode instanceof BuildingEntity && nodeType.equals(com.djt.hvac.domain.model.common.dsl.pointmap.NodeType.PLANT)) {
          
          BuildingEntity parentBuilding = ((BuildingEntity)parentNode);
          PlantEntity plant = parentBuilding.getChildPlantByNameNullIfNotExists(name);
          if (plant == null) {
            
            plant = new PlantEntity(
                parentBuilding,
                name,
                displayName);
            portfolio.addNodeToParentAndIndex(parentBuilding, plant);
          }
          parentNode = plant;
          
        } else if (parentNode instanceof BuildingEntity && nodeType.equals(com.djt.hvac.domain.model.common.dsl.pointmap.NodeType.FLOOR)) {
          
          AbstractNodeEntity childNode = parentNode.getChildNodeByNameNullIfNotExists(name);
          if (childNode == null) {
          
            BuildingEntity parentBuilding = ((BuildingEntity)parentNode);
            FloorEntity floor = parentBuilding.getChildFloorByNameNullIfNotExists(name);
            if (floor == null) {
              
              floor = new FloorEntity(
                  parentBuilding,
                  name,
                  displayName);
              portfolio.addNodeToParentAndIndex(parentBuilding, floor);
            }
            parentNode = floor;
          } else {
            parentNode = childNode; 
          }
          
        } else if (parentNode instanceof BuildingEntity && nodeType.equals(com.djt.hvac.domain.model.common.dsl.pointmap.NodeType.EQUIPMENT)) {
          
          AbstractNodeEntity childNode = parentNode.getChildNodeByNameNullIfNotExists(name);
          if (childNode == null) {
          
            BuildingEntity parentBuilding = ((BuildingEntity)parentNode);
            EquipmentEntity equipment = parentBuilding.getChildEquipmentByNameNullIfNotExists(name);
            if (equipment == null) {
              
              equipment = new EquipmentEntity(
                  parentBuilding,
                  name,
                  displayName);
              portfolio.addNodeToParentAndIndex(parentBuilding, equipment);
            }
            parentNode = equipment;
          } else {
            parentNode = childNode; 
          }
          
        } else if (parentNode instanceof SubBuildingEntity && nodeType.equals(com.djt.hvac.domain.model.common.dsl.pointmap.NodeType.PLANT)) {
          
          AbstractNodeEntity childNode = parentNode.getChildNodeByNameNullIfNotExists(name);
          if (childNode == null) {
            
            SubBuildingEntity parentSubBuilding = ((SubBuildingEntity)parentNode);
            PlantEntity plant = parentSubBuilding.getChildPlantByNameNullIfNotExists(name);
            if (plant == null) {
              
              plant = new PlantEntity(
                  parentSubBuilding,
                  name,
                  displayName);
              portfolio.addNodeToParentAndIndex(parentSubBuilding, plant);
            }
            parentNode = plant;
          } else {
            parentNode = childNode; 
          }              
          
        } else if (parentNode instanceof SubBuildingEntity && nodeType.equals(com.djt.hvac.domain.model.common.dsl.pointmap.NodeType.FLOOR)) {
          
          AbstractNodeEntity childNode = parentNode.getChildNodeByNameNullIfNotExists(name);
          if (childNode == null) {
            
            SubBuildingEntity parentSubBuilding = ((SubBuildingEntity)parentNode);
            FloorEntity floor = parentSubBuilding.getChildFloorByNameNullIfNotExists(name);
            if (floor == null) {
              
              floor = new FloorEntity(
                  parentSubBuilding,
                  name,
                  displayName);
              portfolio.addNodeToParentAndIndex(parentSubBuilding, floor);
            }
            parentNode = floor;
          } else {
            parentNode = childNode; 
          }
          
        } else if (parentNode instanceof SubBuildingEntity && nodeType.equals(com.djt.hvac.domain.model.common.dsl.pointmap.NodeType.EQUIPMENT)) {
          
          AbstractNodeEntity childNode = parentNode.getChildNodeByNameNullIfNotExists(name);
          if (childNode == null) {
            
            SubBuildingEntity parentSubBuilding = ((SubBuildingEntity)parentNode);
            EquipmentEntity equipment = parentSubBuilding.getChildEquipmentByNameNullIfNotExists(name);
            if (equipment == null) {
              
              equipment = new EquipmentEntity(
                  parentSubBuilding,
                  name,
                  displayName);
              portfolio.addNodeToParentAndIndex(parentSubBuilding, equipment);
            }
            parentNode = equipment;
          } else {
            parentNode = childNode;
          }

        } else if (parentNode instanceof FloorEntity && nodeType.equals(com.djt.hvac.domain.model.common.dsl.pointmap.NodeType.EQUIPMENT)) {
          
          AbstractNodeEntity childNode = parentNode.getChildNodeByNameNullIfNotExists(name);
          if (childNode == null) {
            
            FloorEntity parentFloor = ((FloorEntity)parentNode);
            EquipmentEntity equipment = parentFloor.getChildEquipmentByNameNullIfNotExists(name);
            if (equipment == null) {
              
              equipment = new EquipmentEntity(
                  parentFloor,
                  name,
                  displayName);
              portfolio.addNodeToParentAndIndex(parentFloor, equipment);
            }
            parentNode = equipment;
          } else {
            parentNode = childNode;
          }
          
        } else if (parentNode instanceof EquipmentEntity && nodeType.equals(com.djt.hvac.domain.model.common.dsl.pointmap.NodeType.EQUIPMENT)) {
          
          AbstractNodeEntity childNode = parentNode.getChildNodeByNameNullIfNotExists(name);
          if (childNode == null) {
            
            EquipmentEntity parentEquipment = ((EquipmentEntity)parentNode);
            EquipmentEntity equipment = parentEquipment.getNodeChildHierarchyEquipmentByNameNullIfNotExists(name);
            if (equipment == null) {
              
              equipment = new EquipmentEntity(
                  parentEquipment,
                  name,
                  displayName);
              portfolio.addNodeToParentAndIndex(parentEquipment, equipment);
            }
            parentNode = equipment;
          } else {
            parentNode = childNode;
          }              
          
        } else if (nodeType.equals(com.djt.hvac.domain.model.common.dsl.pointmap.NodeType.POINT)) {
          
          // If there already exists a child mappable point with the same name,
          // then its name will be suffixed with "_X", where X is some positive integer.
          // The display name will remain the same (as uniqueness is only by name).
          MappablePointEntity mappablePoint = portfolio.addChildMappablePoint(
              parentNode,
              name, 
              displayName, 
              rawPoint);
          createdMappablePoints.add(mappablePoint);
          
          BuildingEntity ancestorBuilding = mappablePoint.getAncestorBuilding();
          if (ancestorBuilding instanceof BillableBuildingEntity) {
            
            BillableBuildingEntity billableBuilding = (BillableBuildingEntity)ancestorBuilding;
            int mappedPointCount = billableBuilding.getTotalMappedPointCount();
            BuildingStatus currentbuildingStatus = billableBuilding.getBuildingStatus();
            if (mappedPointCount > 0 && currentbuildingStatus.equals(BuildingStatus.CREATED)) {

              BuildingStatus newBuildingStatus = BuildingStatus.PENDING_ACTIVATION;
              LOGGER.info("{}: {}: config status: {} --> {}: num mapped points: {}",
                  AbstractEntity.getTimeKeeper().getCurrentLocalDate(),
                  billableBuilding.getPersistentIdentity(),
                  currentbuildingStatus,
                  newBuildingStatus,
                  mappedPointCount);
              billableBuilding.setBuildingStatus(newBuildingStatus);
            }
          }
        } else {
          throw new RuntimeException("Unsupported combination for node creation: parentNode: "
              + parentNode.getClass().getSimpleName() 
              + " and childNode: " 
              + nodeType);
        }
      }
    }
    
    // Intelligent store to the repository.
    updatePortfolio(
        portfolio,
        request);

    // NOTIFICATION: If any building(s) are at the point cap, then publish a POINT_CAP_EXCEEDED notification event.
    AbstractCustomerEntity customer = portfolio.getParentCustomer();
    if (customer instanceof OnlineCustomerEntity) {
      
      for (BuildingEntity building: portfolio.getAllBuildings()) {
        if (building instanceof BillableBuildingEntity) {
          
          BillableBuildingEntity billableBuilding = (BillableBuildingEntity)building;
          BuildingSubscriptionEntity subscription = billableBuilding.getChildBuildingSubscriptionNullIfNotExists();
          int pointCap = 0;
          if (subscription != null) {
            pointCap = subscription.getParentPaymentPlan().getPointCap();
          } else {
            pointCap = dictionaryRepository.getPaymentPlansContainer().getMaxPointCap();
          }
          
          int pointCount = building.getTotalMappedPointCount(); 
          if (pointCount >= pointCap) {
            
            SortedMap<String, String> substitutionTokenValues = new TreeMap<>();
            substitutionTokenValues.put("POINT_CAP_VALUE", Integer.toString(pointCap));
            substitutionTokenValues.put("BUILDING_NAME", building.getNodePath());
            substitutionTokenValues.put("CUSTOMER_NAME", customer.getNaturalIdentity());

            notificationService.createNotificationEvent(CreateNotificationEventOptions
                .builder()
                .withEventType(NotificationEventType.POINT_CAP_EXCEEDED.toString())
                .withCustomerId(customer.getPersistentIdentity())
                .withSubstitutionTokenValues(substitutionTokenValues)
                .withPublishImmediately(Boolean.TRUE)
                .withPublishedBy("SYSTEM")
                .build());
          }
        }
      }
    }
    
    LOGGER.info("mapRawPoints(): {}: created node count: {}, elapsed(ms): {}",
        AbstractEntity.getTimeKeeper().getCurrentLocalDate(),
        createdMappablePoints.size(),
        (System.currentTimeMillis()-start));
    
    return createdMappablePoints;
  }
  
  @Override
  public AbstractNodeEntity createNode(CreateNodeRequest request)
  throws 
      EntityDoesNotExistException,
      EntityAlreadyExistsException,
      StaleDataException {
    
    dictionaryRepository.ensureDictionaryDataIsLoaded();
    
    NodeType nodeType = request.getNodeType();
    Integer parentId = request.getParentId();
    String name = request.getName();
    String displayName = request.getDisplayName();
    if (displayName == null) {
      displayName = name;
    }
    Map<String, Object> additionalProperties = request.getAdditionalProperties();
    
    NodeSubType nodeSubType = null;
    if (nodeType.equals(NodeType.PORTFOLIO)) {
      
      throw new IllegalArgumentException("Cannot specify nodeType of [PORTFOLIO].");
      
    } else if (nodeType.equals(NodeType.POINT)) {
      
      Object nodeSubTypeObj = extractParameter(additionalProperties, "pointType");
      if (nodeSubTypeObj instanceof NodeSubType) {
        nodeSubType = (NodeSubType)extractParameter(additionalProperties, "pointType");  
      } else {
        nodeSubType = NodeSubType.valueOf(nodeSubTypeObj.toString());
      }
    }
    
    if (nodeType.equals(NodeType.POINT) && (nodeSubType == null 
        || (!nodeSubType.equals(NodeSubType.CUSTOM_ASYNC_COMPUTED_POINT)) 
        && !nodeSubType.equals(NodeSubType.SCHEDULED_ASYNC_COMPUTED_POINT))) {
      
      throw new IllegalArgumentException("When specifying a nodeType of [POINT], "
          + "a nodeSubType must be specified in additionalProperties, and must be either "
          + "CUSTOM_ASYNC_COMPUTED_POINT or SCHEDULED_ASYNC_COMPUTED_POINT");
    }
    
    // We load down to the depth of the type of node that we are creating, as its
    // parent will need to know about its siblings (for dealing with name collisions)
    LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
        .builder()
        .withCustomerId(request.getCustomerId())
        .withDepthNodeType(nodeType)
        .withLoadCustomPointTemporalData(Boolean.TRUE)
        .build();
    
    PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);
    
    AbstractNodeEntity parentNode = null;
    if (nodeType.equals(NodeType.BUILDING)) {
      parentNode = portfolio;
    } else {
      parentNode = portfolio.getChildNode(parentId);
    }
    
    AbstractNodeEntity childNode = null;
    
    if (nodeType.equals(NodeType.POINT)) {
      
      if (nodeSubType.equals(NodeSubType.SCHEDULED_ASYNC_COMPUTED_POINT)) {
        
        if (parentNode instanceof BuildingEntity) {
          
          BuildingEntity parentBuilding = (BuildingEntity)parentNode;
          
          Integer scheduledEventTypeId = (Integer)extractParameter(additionalProperties, "scheduledEventTypeId");
          ScheduledEventTypeEntity scheduledEventType = DictionaryContext
              .getScheduledEventTypesContainer()
              .getScheduledEventType(scheduledEventTypeId);
          
          String metricId = OpenTsdbStringUtils.toValidMetricId(
              parentBuilding.getNodePath() 
              + "/Scheduled/"
              + scheduledEventType.getMetricId());
          
          String range = scheduledEventType.getRange();
          
          TagsContainer tagsContainer = dictionaryRepository.getTagsContainer();
          Set<TagEntity> nodeTags = tagsContainer.getHaystackTagsByName(scheduledEventType.getHaystackTags());

          UnitEntity unit = dictionaryRepository.getUnitsContainer().getUnit(Integer.valueOf(1));

          PointTemplateEntity pointTemplate = null;
          Integer pointTemplateId = (Integer)extractParameter(additionalProperties, "pointTemplateId");
          if (pointTemplateId != null && pointTemplateId.intValue() > 0) {
            pointTemplate = DictionaryContext.getNodeTagTemplatesContainer().getPointTemplate(pointTemplateId);
          }
          
          if (displayName == null) {
            displayName = name;
          }

          // TODO: TDM: Extract out the scheduled event (calendar) stuff, but all that needs to be implemented first.
          childNode = new ScheduledAsyncComputedPointEntity(
              parentBuilding,
              name,
              displayName,
              nodeTags,
              unit,
              range,
              pointTemplate,
              metricId,
              scheduledEventType);
          portfolio.addNodeToParentAndIndex(parentBuilding, (ScheduledAsyncComputedPointEntity)childNode);
          
        } else {
          throw new IllegalArgumentException("Scheduled async computed points can only have buildings as parents, yet: ["
              + parentNode.getNodePath()
              + "] was specified, which is a : ["
              + parentNode.getNodeType()
              + "] and it has an id of: ["
              + parentId
              + "].");
        }
        
      } else if (nodeSubType.equals(NodeSubType.CUSTOM_ASYNC_COMPUTED_POINT)) {
        
        String metricId = (String)extractParameter(additionalProperties, "metricId");
        
        Integer pointTemplateId = (Integer)extractParameter(additionalProperties, "pointTemplateId");
        
        Integer unitId = (Integer)extractParameter(additionalProperties, "unitId");
        
        ComputationInterval computationInterval = ComputationInterval.fromName((String)extractParameter(additionalProperties, "computationInterval"));
        
        PointTemplateEntity pointTemplate = null;
        Set<TagEntity> nodeTags = null;
        if (pointTemplateId != null) {
          
          pointTemplate = dictionaryRepository.getNodeTagTemplatesContainer().getPointTemplate(pointTemplateId);
          nodeTags = pointTemplate.getTags();
          
          if (unitId == null && pointTemplate instanceof PointTemplateEntity) {
            
            unitId = ((PointTemplateEntity)pointTemplate).getUnit().getPersistentIdentity();
            
          }
        }
        
        UnitEntity unit = null;
        if (unitId != null) {
          
          unit = dictionaryRepository.getUnitsContainer().getUnit(unitId);
        }
        
        childNode = new CustomAsyncComputedPointEntity(
            parentNode,
            name,
            displayName,
            nodeTags,
            unit,
            pointTemplate,
            metricId,
            computationInterval);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> childTemporalConfigProps = (List<Map<String, Object>>)extractParameter(additionalProperties, "childTemporalConfigs");
        for (Map<String, Object> childTemporalConfigProp: childTemporalConfigProps) {

          LocalDate effectiveDate = LocalDate.parse((String)childTemporalConfigProp.get("effectiveDate"));
          String formula = (String)childTemporalConfigProp.get("formula");
          String description = (String)childTemporalConfigProp.get("description");
          
          TemporalAsyncComputedPointConfigEntity childTemporalConfig = new TemporalAsyncComputedPointConfigEntity(
              ((CustomAsyncComputedPointEntity)childNode),
              effectiveDate,
              formula,
              description);

          ((CustomAsyncComputedPointEntity)childNode).addChildTemporalConfig(childTemporalConfig);
          
          @SuppressWarnings("unchecked")
          List<Map<String, Object>> childVariableProps = (List<Map<String, Object>>)childTemporalConfigProp.get("childVariables");
          for (Map<String, Object> childVariableProp: childVariableProps) {
            
            Integer variablePointId = (Integer)childVariableProp.get("pointId");
            Integer variableFillPolicyId = (Integer)childVariableProp.get("fillPolicyId");
            String variableName = (String)childVariableProp.get("name");
            
            childTemporalConfig.addChildVariable(new FormulaVariableEntity(
                childTemporalConfig,
                portfolio.getCustomPointFormulaVariableEligiblePointNullIfNotExists(variablePointId),
                variableName,
                FillPolicy.fromId(variableFillPolicyId)));
          }          
        }
        
        portfolio.addNodeToParentAndIndex(parentNode, (CustomAsyncComputedPointEntity)childNode);
        
      }
      
    } else if (nodeType.equals(NodeType.BUILDING)) {
      
      BuildingEntity check = portfolio.getChildBuildingByNameNullIfNotExists(name);
      if (check != null) {

        throw new EntityAlreadyExistsException("Portfolio: ["
            + portfolio
            + "] already has a child building with name: ["
            + name
            + "] and it has an id of: ["
            + check.getPersistentIdentity()
            + "].");
      }
      
      childNode = portfolio.addChildBuilding(
          name, 
          displayName);
      
    } else if (nodeType.equals(NodeType.SUB_BUILDING)) {
      
      if (parentNode instanceof BuildingEntity) {
        
        BuildingEntity parentBuilding = (BuildingEntity)parentNode;
        SubBuildingEntity check = parentBuilding.getChildSubBuildingByNameNullIfNotExists(name);
        if (check != null) {

          throw new EntityAlreadyExistsException("Parent building: ["
              + parentBuilding
              + "] already has a child sub building with name: ["
              + name
              + "] and it has an id of: ["
              + check.getPersistentIdentity()
              + "].");
        }
        
        childNode = new SubBuildingEntity(
            parentBuilding,
            name,
            displayName);
        portfolio.addNodeToParentAndIndex(parentBuilding, (SubBuildingEntity)childNode);
        
      } else {

        throw new IllegalStateException("Node with id: ["
            + parentNode.getPersistentIdentity()
            + "] is not a building, rather, it is: ["
            + parentNode.getClassAndNaturalIdentity()
            + "].");
      }
      
    } else if (nodeType.equals(NodeType.FLOOR)) {

      if (parentNode instanceof BuildingEntity) {
        
        BuildingEntity parentBuilding = (BuildingEntity)parentNode;
        FloorEntity check = parentBuilding.getChildFloorByNameNullIfNotExists(name);
        if (check != null) {

          throw new EntityAlreadyExistsException("Parent building: ["
              + parentBuilding
              + "] already has a child floor with name: ["
              + name
              + "] and it has an id of: ["
              + check.getPersistentIdentity()
              + "].");
        }
        
        childNode = new FloorEntity(
            parentBuilding,
            name,
            displayName);
        portfolio.addNodeToParentAndIndex(parentBuilding, (FloorEntity)childNode);
        
      } else if (parentNode instanceof SubBuildingEntity) {
        
        SubBuildingEntity parentSubBuilding = (SubBuildingEntity)parentNode;
        FloorEntity check = parentSubBuilding.getChildFloorByNameNullIfNotExists(name);
        if (check != null) {

          throw new EntityAlreadyExistsException("Parent sub building: ["
              + parentSubBuilding
              + "] already has a child floor with name: ["
              + name
              + "] and it has an id of: ["
              + check.getPersistentIdentity()
              + "].");
        }
        
        childNode = new FloorEntity(
            parentSubBuilding,
            name,
            displayName);
        portfolio.addNodeToParentAndIndex(parentSubBuilding, (FloorEntity)childNode);
        
      } else {

        throw new IllegalStateException("Node with id: ["
            + parentNode.getPersistentIdentity()
            + "] is not a building or sub building, rather, it is: ["
            + parentNode.getClassAndNaturalIdentity()
            + "].");
      }
      
      Object object = additionalProperties.get("floorOrdinal");
      if (object instanceof Integer) {
        ((FloorEntity)childNode).setFloorOrdinal((Integer)object);
      }
      ((FloorEntity)childNode).remediateFloorOrdinalConflicts();
      
    } else if (nodeType.equals(NodeType.EQUIPMENT)) {
      
      if (parentNode instanceof BuildingEntity) {
        
        BuildingEntity parentBuilding = (BuildingEntity)parentNode;
        EquipmentEntity check = parentBuilding.getChildEquipmentByNameNullIfNotExists(name);
        if (check != null) {

          throw new EntityAlreadyExistsException("Parent building: ["
              + parentBuilding
              + "] already has child equipment with name: ["
              + name
              + "] and it has an id of: ["
              + check.getPersistentIdentity()
              + "].");
        }
        
        childNode = new EquipmentEntity(
            parentBuilding,
            name,
            displayName);
        portfolio.addNodeToParentAndIndex(parentBuilding, (EquipmentEntity)childNode);
        
      } else if (parentNode instanceof SubBuildingEntity) {
        
        SubBuildingEntity parentSubBuilding = (SubBuildingEntity)parentNode;
        EquipmentEntity check = parentSubBuilding.getChildEquipmentByNameNullIfNotExists(name);
        if (check != null) {

          throw new EntityAlreadyExistsException("Parent sub building: ["
              + parentSubBuilding
              + "] already has child equipment with name: ["
              + name
              + "] and it has an id of: ["
              + check.getPersistentIdentity()
              + "].");
        }
        
        childNode = new EquipmentEntity(
            parentSubBuilding,
            name,
            displayName);
        portfolio.addNodeToParentAndIndex(parentSubBuilding, (EquipmentEntity)childNode);
        
      } else if (parentNode instanceof FloorEntity) {
        
        FloorEntity parentFloor = (FloorEntity)parentNode;
        EquipmentEntity check = parentFloor.getChildEquipmentByNameNullIfNotExists(name);
        if (check != null) {

          throw new EntityAlreadyExistsException("Parent floor: ["
              + parentFloor
              + "] already has child equipment with name: ["
              + name
              + "] and it has an id of: ["
              + check.getPersistentIdentity()
              + "].");
        }
        
        childNode = new EquipmentEntity(
            parentFloor,
            name,
            displayName);
        portfolio.addNodeToParentAndIndex(parentFloor, (EquipmentEntity)childNode);        
        
      } else {

        throw new IllegalStateException("Node with id: ["
            + parentNode.getPersistentIdentity()
            + "] is not a building, sub building or floor, rather, it is: ["
            + parentNode.getClassAndNaturalIdentity()
            + "].");
      }
      
      Object equipmentTypeId = additionalProperties.get("equipmentTypeId");
      if (equipmentTypeId instanceof Integer) {
        
        EquipmentEnergyExchangeTypeEntity equipmentType = DictionaryContext
            .getTagsContainer()
            .getEquipmentTypeById((Integer)equipmentTypeId);
        
        ((EquipmentEntity)childNode).setEquipmentType(equipmentType);
      }
      
      Object energyExchangeSystemTypeId = additionalProperties.get("energyExchangeSystemTypeId");
      Object parentEnergyExchangeSystemNodeId = additionalProperties.get("parentEnergyExchangeSystemNodeId");
      if (energyExchangeSystemTypeId instanceof Integer && parentEnergyExchangeSystemNodeId instanceof Integer) {
        
        EnergyExchangeSystemType energyExchangeSystemType = EnergyExchangeSystemType.get((Integer)energyExchangeSystemTypeId);

        EnergyExchangeEntity parentEnergyExchangeSystemNode = null;
        AbstractNodeEntity parentEnergyExchangeNode = portfolio.getChildNode((Integer)parentEnergyExchangeSystemNodeId);
        if (parentEnergyExchangeNode instanceof EnergyExchangeEntity) {
          parentEnergyExchangeSystemNode = (EnergyExchangeEntity)parentEnergyExchangeNode;
        } else {
          throw new IllegalArgumentException("The given parent energy exchange node is not an equipment, plant or loop, but instead was: ["
              + parentEnergyExchangeNode.getClassAndNaturalIdentity()
              + "].");
        }
        
        EnergyExchangeEntity childEnergyExchangeSystemNode = null;
        if (childNode instanceof EnergyExchangeEntity) {
          childEnergyExchangeSystemNode = (EnergyExchangeEntity)childNode;
        } else {
          throw new IllegalArgumentException("The given child node is not an equipment, plant or loop, but instead was: ["
              + childNode.getClassAndNaturalIdentity()
              + "].");
        }
        
        childEnergyExchangeSystemNode.setParentEnergyExchangeSystemNodes(
            energyExchangeSystemType, 
            Arrays.asList(parentEnergyExchangeSystemNode));
      }      
      
    } else if (nodeType.equals(NodeType.PLANT)) {
      
      if (parentNode instanceof BuildingEntity) {
        
        BuildingEntity parentBuilding = (BuildingEntity)parentNode;
        PlantEntity check = parentBuilding.getChildPlantByNameNullIfNotExists(name);
        if (check != null) {

          throw new EntityAlreadyExistsException("Parent building: ["
              + parentBuilding
              + "] already has a child plant with name: ["
              + name
              + "] and it has an id of: ["
              + check.getPersistentIdentity()
              + "].");
        }
        
        childNode = new PlantEntity(
            parentBuilding,
            name,
            displayName);
        portfolio.addNodeToParentAndIndex(parentBuilding, (PlantEntity)childNode);
        
      } else if (parentNode instanceof SubBuildingEntity) {
        
        SubBuildingEntity parentSubBuilding = (SubBuildingEntity)parentNode;
        PlantEntity check = parentSubBuilding.getChildPlantByNameNullIfNotExists(name);
        if (check != null) {

          throw new EntityAlreadyExistsException("Parent sub building: ["
              + parentSubBuilding
              + "] already has a child plant with name: ["
              + name
              + "] and it has an id of: ["
              + check.getPersistentIdentity()
              + "].");
        }
        
        childNode = new PlantEntity(
            parentSubBuilding,
            name,
            displayName);
        portfolio.addNodeToParentAndIndex(parentSubBuilding, (PlantEntity)childNode);
        
      } else {

        throw new IllegalStateException("Node with id: ["
            + parentNode.getPersistentIdentity()
            + "] is not a building or sub building, rather, it is: ["
            + parentNode.getClassAndNaturalIdentity()
            + "].");
      }
      
      Object plantTypeId = additionalProperties.get("plantTypeId");
      if (plantTypeId instanceof Integer) {
        
        PlantEnergyExchangeTypeEntity plantType = DictionaryContext
            .getTagsContainer()
            .getPlantTypeById((Integer)plantTypeId);
        
        ((PlantEntity)childNode).setPlantType(plantType);
      }
      
      Object energyExchangeSystemTypeId = additionalProperties.get("energyExchangeSystemTypeId");
      Object parentEnergyExchangeSystemNodeId = additionalProperties.get("parentEnergyExchangeSystemNodeId");
      if (energyExchangeSystemTypeId instanceof Integer && parentEnergyExchangeSystemNodeId instanceof Integer) {
        
        EnergyExchangeSystemType energyExchangeSystemType = EnergyExchangeSystemType.get((Integer)energyExchangeSystemTypeId);

        EnergyExchangeEntity parentEnergyExchangeSystemNode = null;
        AbstractNodeEntity parentEnergyExchangeNode = portfolio.getChildNode((Integer)parentEnergyExchangeSystemNodeId);
        if (parentEnergyExchangeNode instanceof EnergyExchangeEntity) {
          parentEnergyExchangeSystemNode = (EnergyExchangeEntity)parentEnergyExchangeNode;
        } else {
          throw new IllegalArgumentException("The given parent energy exchange node is not an equipment, plant or loop, but instead was: ["
              + parentEnergyExchangeNode.getClassAndNaturalIdentity()
              + "].");
        }
        
        EnergyExchangeEntity childEnergyExchangeSystemNode = null;
        if (childNode instanceof EnergyExchangeEntity) {
          childEnergyExchangeSystemNode = (EnergyExchangeEntity)childNode;
        } else {
          throw new IllegalArgumentException("The given child node is not an equipment, plant or loop, but instead was: ["
              + childNode.getClassAndNaturalIdentity()
              + "].");
        }
        
        childEnergyExchangeSystemNode.setParentEnergyExchangeSystemNodes(
            energyExchangeSystemType, 
            Arrays.asList(parentEnergyExchangeSystemNode));
      } 
      
    } else if (nodeType.equals(NodeType.LOOP)) {
      
      if (parentNode instanceof PlantEntity) {
        
        PlantEntity parentPlant = (PlantEntity)parentNode;
        LoopEntity check = parentPlant.getChildLoopByNameNullIfNotExists(name);
        if (check != null) {

          throw new EntityAlreadyExistsException("Parent plant: ["
              + parentPlant
              + "] already has a child loop with name: ["
              + name
              + "] and it has an id of: ["
              + check.getPersistentIdentity()
              + "].");
        }
        
        childNode = new LoopEntity(
            parentPlant,
            name,
            displayName);
        portfolio.addNodeToParentAndIndex(parentPlant, (LoopEntity)childNode);

        // Set the parent energy exchange system type at the same time to the parent plant node.
        Object energyExchangeSystemTypeIdObj = extractParameter(additionalProperties, "energyExchangeSystemTypeId");
        Integer energyExchangeSystemTypeId = Integer.parseInt(energyExchangeSystemTypeIdObj.toString());
        EnergyExchangeSystemType energyExchangeSystemType = EnergyExchangeSystemType.get((Integer)energyExchangeSystemTypeId);
        ((AbstractEnergyExchangeEntity)childNode).setParentEnergyExchangeSystemNodes(
            energyExchangeSystemType, 
            Arrays.asList(parentPlant));
        
      } else {

        throw new IllegalStateException("Node with id: ["
            + parentNode.getPersistentIdentity()
            + "] is not a plant, rather, it is: ["
            + parentNode.getClassAndNaturalIdentity()
            + "].");
      }
      
      Object loopTypeId = additionalProperties.get("loopTypeId");
      if (loopTypeId instanceof Integer) {
        
        LoopEnergyExchangeTypeEntity plantType = DictionaryContext
            .getTagsContainer()
            .getLoopTypeById((Integer)loopTypeId);
        
        ((LoopEntity)childNode).setLoopType(plantType);
      }
      
      Object energyExchangeSystemTypeId = additionalProperties.get("energyExchangeSystemTypeId");
      Object parentEnergyExchangeSystemNodeId = additionalProperties.get("parentEnergyExchangeSystemNodeId");
      if (energyExchangeSystemTypeId instanceof Integer && parentEnergyExchangeSystemNodeId instanceof Integer) {
        
        EnergyExchangeSystemType energyExchangeSystemType = EnergyExchangeSystemType.get((Integer)energyExchangeSystemTypeId);

        EnergyExchangeEntity parentEnergyExchangeSystemNode = null;
        AbstractNodeEntity parentEnergyExchangeNode = portfolio.getChildNode((Integer)parentEnergyExchangeSystemNodeId);
        if (parentEnergyExchangeNode instanceof EnergyExchangeEntity) {
          parentEnergyExchangeSystemNode = (EnergyExchangeEntity)parentEnergyExchangeNode;
        } else {
          throw new IllegalArgumentException("The given parent energy exchange node is not an equipment, plant or loop, but instead was: ["
              + parentEnergyExchangeNode.getClassAndNaturalIdentity()
              + "].");
        }
        
        EnergyExchangeEntity childEnergyExchangeSystemNode = null;
        if (childNode instanceof EnergyExchangeEntity) {
          childEnergyExchangeSystemNode = (EnergyExchangeEntity)childNode;
        } else {
          throw new IllegalArgumentException("The given child node is not an equipment, plant or loop, but instead was: ["
              + childNode.getClassAndNaturalIdentity()
              + "].");
        }
        
        childEnergyExchangeSystemNode.setParentEnergyExchangeSystemNodes(
            energyExchangeSystemType, 
            Arrays.asList(parentEnergyExchangeSystemNode));
      } 
      
    }
    
    // Intelligent store to the repository.
    updatePortfolio(
        portfolio,
        request);
    
    return childNode;
  }
  
  private Object extractParameter(Map<String, Object> additionalProperties, String parameterName) {

    if (additionalProperties == null || additionalProperties.isEmpty()) {
      throw new IllegalArgumentException("'additionalProperties' cannot be null/empty");
    }
    
    if (parameterName == null || parameterName.isEmpty()) {
      throw new IllegalArgumentException("'parameterName' cannot be null/empty");
    }
    
    Object parameter = null;
    if (additionalProperties != null) {
      
      parameter = additionalProperties.get(parameterName);
    }
    
    return parameter;
  }

  @Override
  public List<BuildingEntity> updateBuildingNodes(
      UpdateBuildingNodesRequest request)
  throws 
      GeocodingClientLookupFailureException,
      EntityDoesNotExistException,
      EntityAlreadyExistsException,
      StaleDataException {
    
    // Used for inline remediation.
    Set<IssueType> phaseOneIssueTypes = null;
    Set<IssueType> phaseTwoIssueTypes = null;
    List<ValidationMessage> validationMessages = null;

    LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
        .builder()
        .withCustomerId(request.getCustomerId())
        .build();
    
    List<Integer> buildingIds = request.getBuildingIds();
    if (buildingIds != null && !buildingIds.isEmpty()) {

      loadPortfolioOptions = LoadPortfolioOptions
          .builder(loadPortfolioOptions)
          .withFilterNodeType(NodeType.BUILDING)
          .withFilterNodePersistentIdentities(buildingIds)
          .build();
    }    
    
    Boolean performAutomaticRemediation = request.getPerformAutomaticRemediation();
    
    Boolean performAutomaticEvaluateReports = request.getPerformAutomaticEvaluateReports();
    
    Boolean performAutomaticConfiguration = request.getPerformAutomaticConfiguration();
    
    boolean loadBuildingTemporalData = false;
    for (UpdateBuildingNodeRequest dto: request.getData()) {
      if (dto.getTemporalData() != null) {
       
        loadBuildingTemporalData = true;
        break;
      }
    }
    
    loadPortfolioOptions = LoadPortfolioOptions
        .builder(loadPortfolioOptions)
        .withLoadAdFunctionInstances(Boolean.TRUE)
        .withLoadReportInstances(performAutomaticEvaluateReports)
        .withLoadBuildingTemporalData(loadBuildingTemporalData)
        .build();
    
    PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);
    
    if (performAutomaticRemediation.booleanValue()) {
      
      phaseOneIssueTypes = ValidationMessage.extractPhaseOneIssueTypes();
      phaseTwoIssueTypes = ValidationMessage.extractPhaseTwoIssueTypes();
      validationMessages = new ArrayList<>();
    }
    
    // For each building in the request, make the requested changes.
    List<BuildingEntity> responseList = new ArrayList<>();
    for (UpdateBuildingNodeRequest dto: request.getData()) {

      BuildingEntity building = portfolio.getChildBuilding(dto.getId());
      
      building.setDisplayName(dto.getDisplayName());
      
      // RP-13109: Handle unit system change.
      String strUnitSystem = dto.getUnitSystem();
      if (strUnitSystem != null) {
        
        setBuildingUnitSystem(
            request.getCustomerId(),
            portfolio, 
            building, 
            strUnitSystem, 
            request.getSubmittedBy());
      }
      
      // RP-10818: If an online distributor and whose "allowOutOfBandBuildings" is true, then if a building is still
      // in the "trial" period, then it has a special attribute, an enum called "buildingPaymentType" whose values
      // are ONLINE(default for online customer) and OUT_OF_BAND, can be changed as follows:
      //
      // 1. If ONLINE, can be changed to OUT_OF_BAND
      // 2. If OUT_OF_BAND, can be changed back to ONLINE if, and only if, the building point cap is under the max point cap
      //    of the payment band with the highest cap.
      String buildingPaymentType= dto.getBuildingPaymentType();
      if (buildingPaymentType != null) {
        
        building.setBuildingPaymentType(BuildingPaymentType.get(dto.getBuildingPaymentType()));
      }
      
      BuildingAddressData addressData = dto.getAddressData();
      if (addressData != null) {
        
        building.setTimezoneByRubyLabel(addressData.getRubyTimeZoneLabel());
        
        building.setAddress(addressData.getAddress());
        building.setCity(addressData.getCity());
        building.setStateOrProvince(addressData.getStateOrProvince());
        building.setPostalCode(addressData.getPostalCode());
        building.setCountryCode(addressData.getCountryCode());

        // RP-12548: If the lat/long are both 0 and the address fields are present, then perform geocoding.
        if ((addressData.getLatitude() == null || addressData.getLatitude().trim().equals("0") || addressData.getLongitude() == null || addressData.getLongitude().trim().equals("0"))
            && (building.getModifiedAttributes().contains("address")
            || building.getModifiedAttributes().contains("city")
            || building.getModifiedAttributes().contains("stateOrProvince")
            || building.getModifiedAttributes().contains("postalCode")
            || building.getModifiedAttributes().contains("countryCode"))) {
          
          GeocodingAddress geocodingResponse = geocodingClient.geocode(GeocodingAddress
              .builder()
              .withAddress(building.getAddress())
              .withCity(building.getCity())
              .withStateOrProvince(building.getStateOrProvince())
              .withPostalCode(building.getPostalCode())
              .withCountryCode(building.getCountryCode())
              .build());
          
          building.setAddress(geocodingResponse.getAddress());
          building.setCity(geocodingResponse.getCity());
          building.setStateOrProvince(geocodingResponse.getStateOrProvince());
          building.setPostalCode(geocodingResponse.getPostalCode());
          building.setCountryCode(geocodingResponse.getCountryCode());
          building.setLatitude(Double.toString(geocodingResponse.getLatitude()));
          building.setLongitude(Double.toString(geocodingResponse.getLongitude()));
          
        } else {
          
          building.setLatitude(addressData.getLatitude());
          building.setLongitude(addressData.getLongitude());
          
        }
        
        Integer weatherStationId = addressData.getWeatherStationId();
        if (weatherStationId != null) {
          
          if (strUnitSystem == null) {
            strUnitSystem = UnitSystem.IP.toString();
          }
          
          setBuildingWeatherStation(
              building,
              strUnitSystem,
              weatherStationId,
              request.getCustomerId(),
              request.getSubmittedBy());
        }
      }
      
      if (dto.getTemporalData() != null) {

        for (BuildingTemporalData t: dto.getTemporalData()) {
          
          OperationType operationType = t.getOperationType();
          
          if (operationType.equals(OperationType.ADD)) {
      
            // CREATE
            BuildingTemporalConfigEntity childTemporalConfig = new BuildingTemporalConfigEntity(
                building,
                LocalDate.parse(t.getEffectiveDate()),
                t.getSquareFeet());
            
            building.addChildTemporalConfig(childTemporalConfig);
            
            for (BuildingUtilityData u: t.getUtilities()) {
              
              childTemporalConfig.addChildUtility(new BuildingTemporalUtilityEntity(
                  childTemporalConfig,
                  BuildingUtilityType.get(u.getUtilityId().intValue()),
                  UtilityComputationInterval.get(u.getComputationIntervalId()),
                  u.getFormula(),
                  u.getUtilityRate(),
                  u.getBaselineDescription(),
                  u.getUserNotes()));
            }
            building.setIsModified("added_temporal_data");
            
            // RP-10402: Perform baseline formula validation for building temporal utilities.
            List<String> baselineFormulaErrors = childTemporalConfig.validateFormula();
            if (!baselineFormulaErrors.isEmpty()) {
              throw new ValidationException("Building: ["
                  + building.getNodePath()
                  + "] has validation errors for temporal data",
                  baselineFormulaErrors);
            }
            
          } else if (operationType.equals(OperationType.UPDATE)) {
            
            // UPDATE
            BuildingTemporalConfigEntity childTemporalConfig = building.getChildTemporalConfig(t.getTemporalId());
            
            String strEffectiveDate = t.getEffectiveDate();
            if (strEffectiveDate != null && !strEffectiveDate.trim().isEmpty()) {
              childTemporalConfig.setEffectiveDate(LocalDate.parse(strEffectiveDate));  
            }
            
            Integer squareFeet = t.getSquareFeet();
            if (squareFeet != null && squareFeet.intValue() > 0) {
              childTemporalConfig.setSquareFeet(t.getSquareFeet());  
            }
            
            childTemporalConfig.removeAllChildUtilities();
            
            for (BuildingUtilityData u: t.getUtilities()) {
              
              childTemporalConfig.addChildUtility(new BuildingTemporalUtilityEntity(
                  childTemporalConfig,
                  BuildingUtilityType.get(u.getUtilityId().intValue()),
                  UtilityComputationInterval.get(u.getComputationIntervalId()),
                  u.getFormula(),
                  u.getUtilityRate(),
                  u.getBaselineDescription(),
                  u.getUserNotes()));
            }
            childTemporalConfig.setIsModified("updated");
            building.setIsModified("updated_temporal_data");
            
            // RP-10402: Perform baseline formula validation for building temporal utilities.
            List<String> baselineFormulaErrors = childTemporalConfig.validateFormula();
            if (!baselineFormulaErrors.isEmpty()) {
              throw new ValidationException("Building: ["
                  + building.getNodePath()
                  + "] has validation errors for temporal data",
                  baselineFormulaErrors);
            }
            
          } else {
            
            // DELETE
            building.removeChildTemporalConfig(t.getTemporalId());
            building.setIsModified("removed_temporal_data");
            
          }
        }
      }
      
      // Perform inline remediation if modified.
      if (building.getIsModified()) {

        responseList.add(building);
        
        if (performAutomaticRemediation) {

          int sizeBefore = validationMessages.size();
          building.validate(
              phaseOneIssueTypes, 
              validationMessages, 
              performAutomaticRemediation);
          
          int sizeAfter = validationMessages.size();
          if (sizeAfter != sizeBefore) {
            
            building.validate(
                phaseTwoIssueTypes, 
                validationMessages, 
                performAutomaticRemediation);
          }
        }
      }
    }
    
    boolean isModified = portfolio.getIsModified();

    // If specified, then automatically perform validation/remediation.
    if (performAutomaticRemediation 
        && LOGGER.isDebugEnabled() 
        && !validationMessages.isEmpty()) {
      LOGGER.debug("Resolved {} issue(s)", validationMessages.size());
    }
    
    if (isModified && performAutomaticRemediation) {
      
      PortfolioVisitor.findAdFunctionInstanceCandidates(portfolio);
    }

    // If specified, then automatically enable/instantiate any available 
    // AD rule/computed point function candidates.
    if (isModified && performAutomaticConfiguration) {

      List<AbstractAdFunctionInstanceEntity> createdInstances = createAdFunctionInstancesNoStore(
          portfolio,
          portfolio.getAllAdFunctionInstanceCandidates(),
          Boolean.FALSE);
      
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Created {} AD function instance(s)", createdInstances.size());
      }
    }
    
    // If specified, then automatically evaluate the state of the portfolio for reports.
    if (isModified && performAutomaticEvaluateReports) {
      
      List<ReportInstanceEntity> modifiedReportInstances = PortfolioVisitor.evaluateReports(portfolio);
      
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Modified {} report instance(s)", modifiedReportInstances.size());
      }
    }
    
    // If specified, then automatically enable any valid/disabled report instances.
    if (isModified && performAutomaticConfiguration && portfolio.getParentCustomer().allowAutomaticConfiguration()) {
      updateReportInstancesNoStore(
          portfolio,
          new ArrayList<>());      
    }    
    
    // Intelligent store to the repository.
    updatePortfolio(
        portfolio,
        request,
        performAutomaticEvaluateReports);
    
    // Return the list of equipment nodes that have been modified.
    return responseList;
  }
  
  private void setBuildingUnitSystem(
      Integer customerId,
      PortfolioEntity portfolio,
      BuildingEntity building, 
      String strUnitSystem,
      String submittedBy) 
  throws 
      EntityDoesNotExistException {
    
    // If the unit system changed, then migrate point template associations and AD function instance constants.
    UnitSystem unitSystem = UnitSystem.get(strUnitSystem);
    if (!unitSystem.equals(building.getUnitSystem())) {
      
      for (AbstractNodeEntity node: portfolio.getAllNodes()) {
        if (node.getAncestorBuilding().equals(building)) {
          if (node instanceof AbstractPointEntity) {
            
            ((AbstractPointEntity)node).setUnitSystem(unitSystem);
            
          } else if (node instanceof EnergyExchangeEntity) {
            
            ((EnergyExchangeEntity)node).setUnitSystem(unitSystem, null);
            
          } else if (node instanceof BuildingEntity) {
            
            // RP-13171: When switching unit systems, switch weather station point for the temperature 
            // on the OffPremWeatherStation to be the variant that matches the given unit system.
            BuildingEntity buildingNode = (BuildingEntity)node;
            WeatherStationEntity buildingNodeWeatherStation = buildingNode.getWeatherStation();
            if (buildingNodeWeatherStation != null) {

              setBuildingWeatherStation(
                  buildingNode,
                  strUnitSystem,
                  buildingNodeWeatherStation.getPersistentIdentity(),
                  customerId,
                  submittedBy);
              
            }
          }
        }
      }
    }
    building.setUnitSystem(unitSystem);
  }
  
  private void setBuildingWeatherStation(
      BuildingEntity building,
      String strUnitSystem,
      Integer weatherStationId,
      Integer customerId,
      String submittedBy) 
  throws 
      EntityDoesNotExistException {
    
    if (strUnitSystem == null) {
      throw new IllegalArgumentException("strUnitSystem  must be one of: [IP, SI].");
    }
    
    WeatherStationsContainer weatherStationsContainer = dictionaryRepository.getWeatherStationsContainer();
    WeatherStationEntity weatherStation = weatherStationsContainer.getWeatherStationById(weatherStationId);
    
    boolean addedGlobalComputedPoints = false;
    String stationCode = weatherStation.getCode();
    String weatherStationCity = weatherStation.getCity();
    
    if (strUnitSystem.equals(UnitSystem.SI.toString())) {
      
      GlobalComputedPointEntity oatCurrentSiGlobalPoint = weatherStation.getOatCurrentSiGlobalPoint();
      if (oatCurrentSiGlobalPoint == null) {
        
        addedGlobalComputedPoints = true;
        oatCurrentSiGlobalPoint = new GlobalComputedPointEntity(
            stationCode + " OaTemp SI",
            stationCode + " outside air temperature (SI)",
            weatherStationCity + " outside air temperature (SI)",
            DictionaryContext.getUnitsContainer().getUnit(Integer.valueOf(7)),
            "weather/Station/" + stationCode + "/OatCurrent/SI");
        
        weatherStation.setOatCurrentSiGlobalPoint(oatCurrentSiGlobalPoint);
      }
      
    } else {

      GlobalComputedPointEntity oatCurrentGlobalPoint = weatherStation.getOatCurrentGlobalPoint();
      if (oatCurrentGlobalPoint == null) {
        
        addedGlobalComputedPoints = true;
        oatCurrentGlobalPoint = new GlobalComputedPointEntity(
            stationCode + " OaTemp",
            stationCode + " outside air temperature",
            weatherStationCity + "outside air temperature",
            DictionaryContext.getUnitsContainer().getUnit(Integer.valueOf(13)),
            "weather/Station/" + stationCode + "/OatCurrent");
        
        weatherStation.setOatCurrentGlobalPoint(oatCurrentGlobalPoint);
      }
      
    }

    GlobalComputedPointEntity rhCurrentGlobalPoint = weatherStation.getRhCurrentGlobalPoint();
    if (rhCurrentGlobalPoint == null) {
      
      addedGlobalComputedPoints = true;
      rhCurrentGlobalPoint = new GlobalComputedPointEntity(
          stationCode + " OaHumidity",
          stationCode + " outside air humidity",
          weatherStationCity + " relative humidity",
          DictionaryContext.getUnitsContainer().getUnit(Integer.valueOf(44)),
          "weather/Station/" + stationCode + "/RH");
      
      weatherStation.setRhCurrentGlobalPoint(rhCurrentGlobalPoint);
    }

    if (addedGlobalComputedPoints) {
      
      dictionaryRepository.storeNewlyCreatedWeatherStationGlobalPoints(
          weatherStationId,
          customerId,
          strUnitSystem,
          submittedBy);
      DictionaryContext.setWeatherStationsContainer(null);
      dictionaryRepository.ensureDictionaryDataIsLoaded();
      
      publishDictionaryChangeEvent(DictionaryChangeEvent.WEATHER_STATIONS);
      
      weatherStationsContainer = dictionaryRepository.getWeatherStationsContainer();
      weatherStation = weatherStationsContainer.getWeatherStationById(weatherStationId);
    }
    
    building.setWeatherStation(weatherStation, strUnitSystem);
  }

  private EnergyExchangeEntity convertEnergyExchangeSystemNode(
      PortfolioEntity portfolio,
      EnergyExchangeEntity energyExchangeSystemNode,
      String convertToNodeType) throws EntityAlreadyExistsException {
    
    // Mark the old node as deleted.
    energyExchangeSystemNode.setIsDeleted();
    
    // Create the new node of the right type with the same information.
    EnergyExchangeEntity convertedEnergyExchangeSystemNode = null;
    if (convertToNodeType.equals(EnergyExchangeSystemNodeData.NODE_TYPE_PLANT)) {
      
      convertedEnergyExchangeSystemNode = new PlantEntity(
          null,
          energyExchangeSystemNode.getParentNode(),
          energyExchangeSystemNode.getName(),
          energyExchangeSystemNode.getDisplayName(),
          UUID.randomUUID().toString(),
          AbstractEntity.formatTimestamp(energyExchangeSystemNode.getCreatedAt()),
          AbstractEntity.formatTimestamp(energyExchangeSystemNode.getUpdatedAt()),
          new HashSet<>());
      
      // Add the newly created node to via the root portfolio, so that it gets added to the list of nodes
      // that need to be created, as well as establishing both ends of the relationship.
      if (energyExchangeSystemNode.getParentNode() instanceof BuildingEntity) {
        portfolio.addNodeToParentAndIndex((BuildingEntity)energyExchangeSystemNode.getParentNode(), (PlantEntity)convertedEnergyExchangeSystemNode);  
      } else if (energyExchangeSystemNode.getParentNode() instanceof SubBuildingEntity) {
        portfolio.addNodeToParentAndIndex((SubBuildingEntity)energyExchangeSystemNode.getParentNode(), (PlantEntity)convertedEnergyExchangeSystemNode);  
      } else {
        throw new IllegalStateException("Cannot add plant to node of type: " + energyExchangeSystemNode.getParentNode().getClassAndNaturalIdentity());
      }
      
    } else if (convertToNodeType.equals(EnergyExchangeSystemNodeData.NODE_TYPE_LOOP)) {
      
      throw new IllegalStateException("Cannot convertnode of type: "
          + energyExchangeSystemNode.getClassAndNaturalIdentity()
          + "to a loop node because only type conversion between equipment and plants are allowed.");
      
    } else if (convertToNodeType.equals(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)) {
      
      convertedEnergyExchangeSystemNode = new EquipmentEntity(
          null,
          energyExchangeSystemNode.getParentNode(),
          energyExchangeSystemNode.getName(),
          energyExchangeSystemNode.getDisplayName(),
          UUID.randomUUID().toString(),
          AbstractEntity.formatTimestamp(energyExchangeSystemNode.getCreatedAt()),
          AbstractEntity.formatTimestamp(energyExchangeSystemNode.getUpdatedAt()),
          new HashSet<>());
      
      // Add the newly created node to via the root portfolio, so that it gets added to the list of nodes
      // that need to be created, as well as establishing both ends of the relationship.
      if (energyExchangeSystemNode.getParentNode() instanceof BuildingEntity) {
        portfolio.addNodeToParentAndIndex((BuildingEntity)energyExchangeSystemNode.getParentNode(), (EquipmentEntity)convertedEnergyExchangeSystemNode);  
      } else if (energyExchangeSystemNode.getParentNode() instanceof SubBuildingEntity) {
        portfolio.addNodeToParentAndIndex((SubBuildingEntity)energyExchangeSystemNode.getParentNode(), (EquipmentEntity)convertedEnergyExchangeSystemNode);
      } else if (energyExchangeSystemNode.getParentNode() instanceof FloorEntity) {
        portfolio.addNodeToParentAndIndex((FloorEntity)energyExchangeSystemNode.getParentNode(), (EquipmentEntity)convertedEnergyExchangeSystemNode);  
      } else if (energyExchangeSystemNode.getParentNode() instanceof EquipmentEntity) {
        portfolio.addNodeToParentAndIndex((EquipmentEntity)energyExchangeSystemNode.getParentNode(), (EquipmentEntity)convertedEnergyExchangeSystemNode);  
      } else {
        throw new IllegalStateException("Cannot add equipment to node of type: " + energyExchangeSystemNode.getParentNode().getClassAndNaturalIdentity());
      }
    }
        
    // Move all child points from the old node to the new node.
    if (convertedEnergyExchangeSystemNode != null) {
      for (AbstractNodeEntity childNode: energyExchangeSystemNode.getAllChildNodes()) {
        
        if (childNode instanceof AbstractPointEntity) {
          childNode.setNewParentNode((AbstractNodeEntity)convertedEnergyExchangeSystemNode);
        } else {
          throw new IllegalStateException("Cannot convert: ["
              + energyExchangeSystemNode
              + "] to: ["
              + convertToNodeType.toLowerCase()
              + "] because there exists a non-point child: ["
              + childNode
              + "] that is not eligible to be a child.");
        }
      }
    }
    
    return convertedEnergyExchangeSystemNode;
  }  
  
  @Override
  public List<EnergyExchangeEntity> updateEnergyExchangeSystemNodes(
      UpdateEnergyExchangeSystemNodesRequest request)
  throws 
      EntityDoesNotExistException,
      EntityAlreadyExistsException,
      StaleDataException {
    
    
    // If there is nothing to do, then return right away.
    if (request.getData() == null || request.getData().isEmpty()) {
      return new ArrayList<>();
    }
    
    
    // ************************************************************************
    // STEP ONE: ONLY LOAD THE BUILDINGS THAT ARE INVOLVED IN THE REQUEST DATA
    // ************************************************************************
    LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
        .builder()
        .withCustomerId(request.getCustomerId())
        .build();
    
    List<Integer> buildingIds = request.getBuildingIds();
    if (buildingIds != null && !buildingIds.isEmpty()) {

      loadPortfolioOptions = LoadPortfolioOptions
          .builder(loadPortfolioOptions)
          .withFilterNodeType(NodeType.BUILDING)
          .withFilterNodePersistentIdentities(buildingIds)
          .build();
    }    
    
    boolean performAutomaticRemediation = request.getPerformAutomaticRemediation();
    boolean performAutomaticEvaluateReports = request.getPerformAutomaticEvaluateReports();
    boolean performAutomaticConfiguration = request.getPerformAutomaticConfiguration();
    
    Integer energyExchangeSystemNodeId = request.getEnergyExchangeSystemNodeId();
    if (energyExchangeSystemNodeId != null) {
      
      loadPortfolioOptions = LoadPortfolioOptions
          .builder(loadPortfolioOptions)
          .withFilterNodeType(NodeType.EQUIPMENT)
          .withFilterNodePersistentIdentity(energyExchangeSystemNodeId)
          .build();    
      
      performAutomaticRemediation = false;
      performAutomaticEvaluateReports = false;
      performAutomaticConfiguration = false;
    }
    
    loadPortfolioOptions = LoadPortfolioOptions
        .builder(loadPortfolioOptions)
        .withLoadAdFunctionInstances(Boolean.TRUE)
        .withLoadReportInstances(performAutomaticEvaluateReports)
        .build();    
    
    // Load the portfolio, given the options for this operation/use case.
    PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);
    
    // See if the parent customer is configured to allow automatic configuration.
    boolean parentAllowsAutomaticConfiguration = portfolio.getParentCustomer().allowAutomaticConfiguration();
    
    
    // ************************************************************************
    // STEP 2: For each equipment/plant/loop in the request, make the requested changes.
    // ************************************************************************
    // Used for inline remediation.
    Set<IssueType> phaseOneIssueTypes = null;
    Set<IssueType> phaseTwoIssueTypes = null;
    List<ValidationMessage> validationMessages = null;
    if (performAutomaticRemediation) {
      
      phaseOneIssueTypes = ValidationMessage.extractPhaseOneIssueTypes();
      phaseTwoIssueTypes = ValidationMessage.extractPhaseTwoIssueTypes();
      validationMessages = new ArrayList<>();
    }  
    
    // Get the cached tags container, which holds equipment/plant/loop types, as 
    // well as equipment metadata tags.
    TagsContainer tagsContainer = DictionaryContext.getTagsContainer();

    // Loop through all the request data, making the requested changes.
    List<EnergyExchangeEntity> responseList = new ArrayList<>();
    for (EnergyExchangeSystemNodeData dto: request.getData()) {
      
      // Get the energy exchange system node to modify.
      EnergyExchangeEntity energyExchangeSystemNode = updateEnergyExchangeSystemNode(
          tagsContainer,
          portfolio,
          dto);
      
      // If specified, then automatically perform validation/remediation. Note that
      // could very well be that nothing was actually changed from what it was before.
      if (energyExchangeSystemNode.getIsModified() && performAutomaticRemediation) {

        int sizeBefore = validationMessages.size();
        energyExchangeSystemNode.validate(
            phaseOneIssueTypes, 
            validationMessages, 
            performAutomaticRemediation);
        
        int sizeAfter = validationMessages.size();
        if (sizeAfter != sizeBefore) {
          
          energyExchangeSystemNode.validate(
              phaseTwoIssueTypes, 
              validationMessages, 
              performAutomaticRemediation);
        }
      }
      
      responseList.add(energyExchangeSystemNode);
    }

    
    // ************************************************************************
    // STEP 3: AUTOMATICALLY PERFORM ANY REMEDIATION/REPORT EVALUATION/CONFIGURATION
    // ************************************************************************

    // See if anything HAS actually changed in the portfolio.
    boolean isModified = portfolio.getIsModified();

    
    // A business requirement is to automatically generate any 
    // AD function instance candidates on any operation that may affect them.
    if (isModified && performAutomaticRemediation) {
      
      PortfolioVisitor.findAdFunctionInstanceCandidates(portfolio);
    }

    
    // If specified, then automatically enable/instantiate any valid, but disabled, 
    // AD rule/computed point function candidates.
    if (isModified && performAutomaticConfiguration && parentAllowsAutomaticConfiguration) {

      List<AbstractAdFunctionInstanceEntity> createdInstances = createAdFunctionInstancesNoStore(
          portfolio,
          portfolio.getAllAdFunctionInstanceCandidates(),
          Boolean.FALSE);
      
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Created {} AD function instance(s)", createdInstances.size());
      }
    }
    
    
    // If specified, then automatically evaluate the state of the portfolio for reports.
    if (isModified && performAutomaticEvaluateReports) {
      
      List<ReportInstanceEntity> modifiedReportInstances = PortfolioVisitor.evaluateReports(portfolio);
      
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Modified {} report instance(s)", modifiedReportInstances.size());
      }
    }
    
    
    // If specified, then automatically enable any valid, but disabled, report instances.
    if (isModified && performAutomaticConfiguration && parentAllowsAutomaticConfiguration) {
      
      updateReportInstancesNoStore(
          portfolio,
          new ArrayList<>());      
    }
    
    
    // ************************************************************************
    // STEP 4: Intelligent store to the repository (the call here is independent of this operation).
    // ************************************************************************
    updatePortfolio(
        portfolio,
        request,
        performAutomaticEvaluateReports);
    
    
    // Return the list of energy exchange nodes that have been modified.
    return responseList;
  }
  
  private EnergyExchangeEntity updateEnergyExchangeSystemNode(
      TagsContainer tagsContainer,
      PortfolioEntity portfolio,
      EnergyExchangeSystemNodeData dto) 
  throws 
      EntityDoesNotExistException, 
      EntityAlreadyExistsException {
    
    String nodeType = dto.getNodeType();
    Integer id = dto.getId();
    EnergyExchangeEntity energyExchangeSystemNode = portfolio.getEnergyExchangeSystemNode(id);
    
    // See if we are to "convert" from one node type to another.
    String convertToNodeType = dto.getConvertToNodeType();
    if (convertToNodeType != null && !convertToNodeType.equals(nodeType)) {

      nodeType = convertToNodeType;
      energyExchangeSystemNode = convertEnergyExchangeSystemNode(
          portfolio, 
          energyExchangeSystemNode, 
          convertToNodeType);
    }
    
    if (energyExchangeSystemNode != null) {

      String displayName = dto.getDisplayName();
      if (displayName != null) {
        energyExchangeSystemNode.setDisplayName(displayName);  
      }
      
      boolean plantTypeChanged = false;
      Integer energyExchangeTypeId = dto.getTypeId();
      if (energyExchangeTypeId != null && energyExchangeTypeId.intValue() > 0) {
        
        if (nodeType.equals(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)) {
          ((EquipmentEntity)energyExchangeSystemNode).setEquipmentType(tagsContainer.getEquipmentTypeById(energyExchangeTypeId));
          
        } else if (nodeType.equals(EnergyExchangeSystemNodeData.NODE_TYPE_PLANT)) {
          plantTypeChanged = ((PlantEntity)energyExchangeSystemNode).setPlantType(tagsContainer.getPlantTypeById(energyExchangeTypeId));
          
        } else if (nodeType.equals(EnergyExchangeSystemNodeData.NODE_TYPE_LOOP)) {
          ((LoopEntity)energyExchangeSystemNode).setLoopType(tagsContainer.getLoopTypeById(energyExchangeTypeId));
          
        } else {
          throw new IllegalArgumentException("Unsupported node type: ["
              + nodeType
              + "], expected: [EQUIPMENT, PLANT or LOOP].");
        }
      } else if (energyExchangeTypeId == null || energyExchangeTypeId.intValue() == -1) { // NULL or -1 means set to NULL

        if (nodeType.equals(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)) {
          ((EquipmentEntity)energyExchangeSystemNode).setEquipmentType(null);
          
        } else if (nodeType.equals(EnergyExchangeSystemNodeData.NODE_TYPE_PLANT)) {
          plantTypeChanged = ((PlantEntity)energyExchangeSystemNode).setPlantType(null);
          
        } else if (nodeType.equals(EnergyExchangeSystemNodeData.NODE_TYPE_LOOP)) {
          ((LoopEntity)energyExchangeSystemNode).setLoopType(null);
          
        } else {
          throw new IllegalArgumentException("Unsupported node type: ["
              + nodeType
              + "], expected: [EQUIPMENT, PLANT or LOOP].");
        }
      } else {
        // An energy exchange type id of -2 means ignore/do nothing.
      }
      
      // RP-12902: If the plant type is changed/removed, then remove any energy exchange
      // system relationships that may exist. Additionally, delete any child loops that
      // may exist under the plant (using the "deleteLoop()" as part of RP-12872.
      if (plantTypeChanged && energyExchangeSystemNode instanceof PlantEntity) {
        
        PlantEntity plant = (PlantEntity)energyExchangeSystemNode;
        
        // Remove all existing descendant loops.
        List<LoopEntity> childLoops = new ArrayList<>();
        childLoops.addAll(plant.getChildLoops());
        for (int i=0; i < childLoops.size(); i++) {
          deleteLoop(portfolio, childLoops.get(i));
        }
        
        // Remove all existing energy exchange relationships (except for the one in the current request DTO).
        EnergyExchangeSystemType energyExchangeSystemType = EnergyExchangeSystemType.get(dto.getSystemTypeId());
        if (!energyExchangeSystemType.equals(EnergyExchangeSystemType.CHILLED_WATER)) {
          plant.setChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.CHILLED_WATER, new ArrayList<>());  
        }
        if (!energyExchangeSystemType.equals(EnergyExchangeSystemType.HOT_WATER)) {
          plant.setChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.HOT_WATER, new ArrayList<>());  
        }
        if (!energyExchangeSystemType.equals(EnergyExchangeSystemType.STEAM)) {
          plant.setChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.STEAM, new ArrayList<>());
        }
        if (!energyExchangeSystemType.equals(EnergyExchangeSystemType.AIR_SUPPLY)) {
          plant.setChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY, new ArrayList<>());  
        }
      }
      
      List<Integer> childIds = dto.getChildIds();
      if (childIds != null) {
        
        EnergyExchangeSystemType energyExchangeSystemType = EnergyExchangeSystemType.get(dto.getSystemTypeId());
        
        List<EnergyExchangeEntity> childEnergyExchangeSystemNodes = new ArrayList<>();
        for (Integer childId: childIds) {
          
          AbstractNodeEntity childNode = portfolio.getChildNodeNullIfNotExists(childId);
          if (childNode != null) {
            if (childNode instanceof EnergyExchangeEntity) {
              childEnergyExchangeSystemNodes.add((EnergyExchangeEntity)childNode);
            } else {
              LOGGER.error("Non energy exchange node encountered: {}", childNode.getNaturalIdentity());
            }
          } else {
            throw new EntityDoesNotExistException("EnergyExchangeEntity with persistentIdentity: ["
                + childId
                + "] does not exist in portfolio: [" 
                + portfolio.getNaturalIdentity()
                + "]");
          }
        }
        energyExchangeSystemNode.setChildEnergyExchangeSystemNodes(
            energyExchangeSystemType, 
            childEnergyExchangeSystemNodes);
      }
      
      List<Integer> parentIds = dto.getParentIds();
      if (parentIds != null) {
        
        EnergyExchangeSystemType energyExchangeSystemType = EnergyExchangeSystemType.get(dto.getSystemTypeId());
        
        List<EnergyExchangeEntity> parentEnergyExchangeSystemNodes = new ArrayList<>();
        for (Integer parentId: parentIds) {
          
          AbstractNodeEntity parentNode = portfolio.getChildNodeNullIfNotExists(parentId);
          if (parentNode != null) {
            if (parentNode instanceof EnergyExchangeEntity) {
              parentEnergyExchangeSystemNodes.add((EnergyExchangeEntity)parentNode);
            } else {
              LOGGER.error("Non energy exchange node encountered: {}", parentNode.getNaturalIdentity());
            }
          } else {
            throw new EntityDoesNotExistException("EnergyExchangeEntity with persistentIdentity: ["
                + parentId
                + "] does not exist in portfolio: [" 
                + portfolio.getNaturalIdentity()
                + "]");
          }
        }
        energyExchangeSystemNode.setParentEnergyExchangeSystemNodes(
            energyExchangeSystemType, 
            parentEnergyExchangeSystemNodes);
      }
      
      // We only deal with metadata tags when the list is non-null.
      // If the list is empty, that means we remove all existing.
      List<String> metadataTagNames = dto.getMetadataTags();
      if (nodeType.equals(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT) && metadataTagNames != null) {
       
        Set<TagEntity> metadataTags = new HashSet<>();
        for (String metadataTagName: metadataTagNames) {
          
          if (metadataTagName.trim().length() > 0) {
          
            metadataTags.add(tagsContainer.getTagByName(metadataTagName, TagGroupType.EQUIPMENT_METADATA));
          }
        }
        ((EquipmentEntity)energyExchangeSystemNode).setMetadataTags(metadataTags);
      }
      
      List<Integer> metadataTagIds = dto.getMetadataTagIds();
      if (nodeType.equals(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT) && metadataTagIds != null) {
       
        Set<TagEntity> metadataTags = new HashSet<>();
        for (Integer metadataTagId: metadataTagIds) {
          
          metadataTags.add(tagsContainer.getTag(metadataTagId));
        }
        ((EquipmentEntity)energyExchangeSystemNode).setMetadataTags(metadataTags);
      }
      
    } else {
      throw new EntityDoesNotExistException("EnergyExchangeEntity with persistentIdentity: ["
          + id
          + "] does not exist in portfolio: [" 
          + portfolio.getNaturalIdentity()
          + "]");      
    }
    
    return energyExchangeSystemNode;
  }

  @Override
  public List<MappablePointEntity> updateMappablePointNodes(
      UpdateMappablePointNodesRequest request)
  throws 
      EntityDoesNotExistException,
      StaleDataException {

    if (request.getData() == null || request.getData().isEmpty()) {
      return new ArrayList<>();
    }
    
    LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
        .builder()
        .withCustomerId(request.getCustomerId())
        .build();
    
    List<Integer> buildingIds = request.getBuildingIds();
    if (buildingIds != null && !buildingIds.isEmpty()) {

      loadPortfolioOptions = LoadPortfolioOptions
          .builder(loadPortfolioOptions)
          .withFilterNodeType(NodeType.BUILDING)
          .withFilterNodePersistentIdentities(buildingIds)
          .build();
    }    
    
    Boolean performAutomaticRemediation = request.getPerformAutomaticRemediation();
    
    Boolean performAutomaticEvaluateReports = request.getPerformAutomaticEvaluateReports();
    
    Boolean performAutomaticConfiguration = request.getPerformAutomaticConfiguration();
    
    // If we are updating the metadata tags for a point, then we have to 
    // load the whole building in order to resolve scoped tag conflicts.
    if (!request.getUseGrouping().booleanValue() 
        && request.getData().size() == 1
        && request.getData().get(0).getMetadataTags() == null) {

      MappablePointNodeData dto = request.getData().get(0);
      Integer pointId = dto.getId();
      if (pointId != null) {
        
        loadPortfolioOptions = LoadPortfolioOptions
            .builder(loadPortfolioOptions)
            .withFilterNodeType(NodeType.POINT)
            .withFilterNodePersistentIdentity(pointId)
            .build();    
        
        performAutomaticRemediation = Boolean.FALSE;
        performAutomaticEvaluateReports = Boolean.FALSE;
        performAutomaticConfiguration = Boolean.FALSE;
      }
    }
    
    boolean loadAdFunctionInstances = false;
    if (performAutomaticEvaluateReports.booleanValue() 
        || performAutomaticRemediation.booleanValue()
        || performAutomaticConfiguration.booleanValue()) {
      
      loadAdFunctionInstances = true;
    }    
    
    if (performAutomaticRemediation 
        || performAutomaticEvaluateReports
        || performAutomaticConfiguration) {

      loadPortfolioOptions = LoadPortfolioOptions
          .builder(loadPortfolioOptions)
          .withLoadAdFunctionInstances(Boolean.TRUE)
          .withLoadReportInstances(performAutomaticEvaluateReports)
          .build();    
      
    } else {

      loadPortfolioOptions = LoadPortfolioOptions
          .builder(loadPortfolioOptions)
          .withDepthNodeType(NodeType.POINT)
          .withLoadAdFunctionInstances(loadAdFunctionInstances)
          .build();    

    }
    
    PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);    
    
    // Used for inline remediation.
    Set<IssueType> phaseOneIssueTypes = null;
    Set<IssueType> phaseTwoIssueTypes = null;
    List<ValidationMessage> validationMessages = null;
    if (performAutomaticRemediation.booleanValue()) {
      
      phaseOneIssueTypes = ValidationMessage.extractPhaseOneIssueTypes();
      phaseTwoIssueTypes = ValidationMessage.extractPhaseTwoIssueTypes();
      validationMessages = new ArrayList<>();
    }  
    
    // Get the cached dictionary data.
    TagsContainer tagsContainer = dictionaryRepository.getTagsContainer();
    NodeTagTemplatesContainer nodeTagTemplatesContainer = dictionaryRepository.getNodeTagTemplatesContainer();
    UnitsContainer unitsContainer = dictionaryRepository.getUnitsContainer();
    
    // See if we are to deal with point groups or not.
    Boolean useGrouping = request.getUseGrouping();
    List<MappablePointNodeData> dtoList = request.getData();
    
    // For each point/point group in the request, make the requested changes.
    List<MappablePointEntity> responseList = new ArrayList<>();
    for (MappablePointNodeData dto: dtoList) {
      
      String name = dto.getName();
      
      AbstractEnergyExchangeTypeEntity parentEnergyExchangeType = null;
      Integer parentEquipmentTypeId = dto.getParentEquipmentTypeId();
      if (parentEquipmentTypeId != null && parentEquipmentTypeId.intValue() > 0) {
        
        parentEnergyExchangeType = tagsContainer.getEnergyExchangeTypeById(parentEquipmentTypeId);
      }
      
      String oldDisplayName = dto.getOldDisplayName();
      String newDisplayName = dto.getDisplayName();

      // Get the old point template.
      Integer oldPointTemplateId = dto.getOldPointTemplateId();
      if (oldPointTemplateId == null) {
        oldPointTemplateId = ANY;
      } else if (oldPointTemplateId.intValue() > 0) {
        oldPointTemplateId = nodeTagTemplatesContainer.getPointTemplate(oldPointTemplateId).getPersistentIdentity();
      } else if (oldPointTemplateId.intValue() == -1) {
        oldPointTemplateId = NULL;
      } else if (oldPointTemplateId.intValue() == -2) {
        oldPointTemplateId = ANY;
      }      

      // Get the new point template.
      Integer newPointTemplateId = dto.getPointTemplateId();
      PointTemplateEntity newPointTemplate = null;
      if (newPointTemplateId != null && newPointTemplateId.intValue() > 0) {
        AbstractNodeTagTemplateEntity nodeTagTemplate = nodeTagTemplatesContainer.getPointTemplate(newPointTemplateId);
        if (nodeTagTemplate instanceof PointTemplateEntity) {
          newPointTemplate = (PointTemplateEntity)nodeTagTemplate;
        } else {
          throw new IllegalStateException("New point template with id: ["
              + newPointTemplateId
              + "] is not an instance of PointTemplateEntity, rather it is: ["
              + nodeTagTemplate.getClassAndNaturalIdentity()
              + "].");
        }
      }
      
      // Get the old unit.
      Integer oldUnitId = dto.getOldUnitId();
      if (oldUnitId == null) {
        oldUnitId = ANY;
      } else if (oldUnitId.intValue() > 0) {
        oldUnitId = unitsContainer.getUnit(oldUnitId).getPersistentIdentity();  
      } else if (oldUnitId.intValue() == -1) {
        oldUnitId = NULL;
      } else if (oldUnitId.intValue() == -2) {
        oldUnitId = ANY;
      }

      // Get the new unit.
      Integer newUnitId = dto.getUnitId();
      UnitEntity newUnit = null;
      if (newUnitId != null && newUnitId.intValue() > 0) {
        newUnit = unitsContainer.getUnit(newUnitId);
      }
      
      // If grouped, each DTO can represent many points, all with same 
      // characteristics for display name, point template and unit.
      Set<MappablePointEntity> mappablePoints = null;
      if (useGrouping.booleanValue()) {
        
        mappablePoints = portfolio.getMappablePointGroup(
            name,
            parentEnergyExchangeType, 
            DataType.get(dto.getPointDataTypeId()), 
            oldDisplayName,
            oldPointTemplateId, 
            oldUnitId);
        
        int matchedPointGroupSize = mappablePoints.size();
        int requestPointGroupSize = dto.getQuantity().intValue();
        if (matchedPointGroupSize != requestPointGroupSize) {
          
          throw new IllegalStateException("Expected to have: ["
              + dto.getQuantity()
              + "] mappable points to update in the group, yet only found: ["
              + mappablePoints.size()
              + "] for criteria with name: ["
              + name
              + "], parentEnergyExchangeType: ["
              + parentEnergyExchangeType
              + "], dataType: ["
              + DataType.get(dto.getPointDataTypeId())
              + "], oldDisplayName: ["
              + oldDisplayName
              + "], oldPointTemplateId: ["
              + oldPointTemplateId
              + "], oldUnitId: ["
              + oldUnitId
              + "] ");
        }
      } else {
        
        mappablePoints = new HashSet<>();
        mappablePoints.add(portfolio.getMappablePoint(dto.getId()));
      }
      
      // Update the point(s)
      for (MappablePointEntity mappablePoint: mappablePoints) {
        
        if (newDisplayName != null) {
          mappablePoint.setDisplayName(newDisplayName);  
        }
        mappablePoint.setPointTemplate(newPointTemplate);
        
        // TODO: TDM: UNIT_SYSTEM: Figure out a better way to do this.
        UnitSystem unitSystem = mappablePoint.getAncestorBuilding().getUnitSystem();
        boolean converted = false;
        if (unitSystem.equals(UnitSystem.SI)) {
          converted = mappablePoint.setUnitSystem(unitSystem);  
        }
        if (!converted) {
          mappablePoint.setUnit(newUnit);
        }
        
        if (!useGrouping.booleanValue()) {
          
          if (dto.getRange() != null) {
            mappablePoint.setRange(dto.getRange());
          }
          
          // We only deal with metadata tags when the list is non-null.
          // If the list is empty, that means we remove all existing.
          List<String> metadataTagNames = dto.getMetadataTags();
          if (metadataTagNames != null) {
           
            Set<TagEntity> metadataTags = new HashSet<>();
            for (String metadataTagName: metadataTagNames) {
              
              if (metadataTagName.trim().length() > 0) {
              
                metadataTags.add(tagsContainer.getTagByName(metadataTagName, TagGroupType.POINT_TAG));
              }
            }
            mappablePoint.setMetadataTags(metadataTags);
          }
        }
        
        responseList.add(mappablePoint);
        
        // Perform inline remediation if modified.
        if (mappablePoint.getIsModified() && performAutomaticRemediation.booleanValue()) {
          
          AbstractNodeEntity parentNode = mappablePoint.getParentNode();
          if (parentNode instanceof EquipmentEntity) {

            // if the parent is equipment, then perform the remediation on it.
            EquipmentEntity parentEquipment = (EquipmentEntity)parentNode;
            int sizeBefore = validationMessages.size();
            parentEquipment.validate(
                phaseOneIssueTypes, 
                validationMessages, 
                performAutomaticRemediation);
            
            int sizeAfter = validationMessages.size();
            if (sizeAfter != sizeBefore) {
              
              parentEquipment.validate(
                  phaseTwoIssueTypes, 
                  validationMessages, 
                  performAutomaticRemediation);
            }
          } else {
            
            // Otherwise, just remediate the point. 
            int sizeBefore = validationMessages.size();
            mappablePoint.validate(
                phaseOneIssueTypes, 
                validationMessages, 
                performAutomaticRemediation);
            
            int sizeAfter = validationMessages.size();
            if (sizeAfter != sizeBefore) {
              
              mappablePoint.validate(
                  phaseTwoIssueTypes, 
                  validationMessages, 
                  performAutomaticRemediation);
            }
          }
        }
      }
    }
    
    boolean isModified = portfolio.getIsModified();

    // If specified, then automatically perform validation/remediation.
    if (performAutomaticRemediation.booleanValue() 
        && LOGGER.isDebugEnabled() 
        && !validationMessages.isEmpty()) {
      LOGGER.debug("Resolved {} issue(s)", validationMessages.size());
    }

    // If specified, then automatically enable/instantiate any available 
    // AD rule/computed point function candidates.
    if (isModified && performAutomaticConfiguration.booleanValue() && portfolio.getParentCustomer().allowAutomaticConfiguration()) {
      
      PortfolioVisitor.findAdFunctionInstanceCandidates(portfolio);

      List<AbstractAdFunctionInstanceEntity> createdInstances = createAdFunctionInstancesNoStore(
          portfolio,
          portfolio.getAllAdFunctionInstanceCandidates(),
          Boolean.FALSE);
      
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Created {} AD function instance(s)", createdInstances.size());
      }
    }
    
    // If specified, then automatically evaluate the state of the portfolio for reports.
    if (isModified && performAutomaticEvaluateReports.booleanValue()) {
      
      List<ReportInstanceEntity> modifiedReportInstances = PortfolioVisitor.evaluateReports(portfolio);
      
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Modified {} report instance(s)", modifiedReportInstances.size());
      }
    }
    
    // If specified, then automatically enable any valid/disabled report instances.
    if (isModified && performAutomaticConfiguration.booleanValue() && portfolio.getParentCustomer().allowAutomaticConfiguration()) {
      updateReportInstancesNoStore(
          portfolio,
          new ArrayList<>());      
    }
    
    // Intelligent store to the repository.
    updatePortfolio(
        portfolio,
        request,
        performAutomaticEvaluateReports);
    
    // Return the list of mappable point nodes that have been modified.
    return responseList;
  }

  @Override
  public List<CustomAsyncComputedPointEntity> updateCustomAsyncComputedPointNodes(
      UpdateCustomAsyncComputedPointNodesRequest request)
  throws 
      EntityDoesNotExistException,
      EntityAlreadyExistsException,
      StaleDataException {
    
    // Load the portfolio (down to the point level).
    LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
        .builder()
        .withCustomerId(request.getCustomerId())
        .withDepthNodeType(NodeType.POINT)
        .withLoadCustomPointTemporalData(Boolean.TRUE)
        .build();
    
    List<Integer> buildingIds = request.getBuildingIds();
    if (buildingIds != null && !buildingIds.isEmpty()) {

      loadPortfolioOptions = LoadPortfolioOptions
          .builder(loadPortfolioOptions)
          .withFilterNodeType(NodeType.BUILDING)
          .withFilterNodePersistentIdentities(buildingIds)
          .build();
    }    
    
    PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);       
    
    // Get the cached dictionary data.
    NodeTagTemplatesContainer nodeTagTemplatesContainer = DictionaryContext.getNodeTagTemplatesContainer();
    UnitsContainer unitsContainer = DictionaryContext.getUnitsContainer();
    
    // For each point in the request, make the requested changes.
    List<CustomAsyncComputedPointEntity> responseList = new ArrayList<>();
    for (CustomAsyncComputedPointNodeData dto: request.getDtoList()) {

      Integer pointId = dto.getId();
      
      // Update the point.
      AbstractPointEntity point = portfolio.getChildPoint(pointId);
      if (point instanceof CustomAsyncComputedPointEntity) {
        
        CustomAsyncComputedPointEntity customAsyncComputedPoint = (CustomAsyncComputedPointEntity)point;

        // Get the new display name.
        String newDisplayName = dto.getDisplayName();
        if (newDisplayName != null) {
          customAsyncComputedPoint.setDisplayName(newDisplayName);  
        }
        
        // Get the new point template.
        Integer newPointTemplateId = dto.getPointTemplateId();
        PointTemplateEntity newPointTemplate = null;
        if (newPointTemplateId != null && newPointTemplateId.intValue() > 0) {
          AbstractNodeTagTemplateEntity nodeTagTemplate = nodeTagTemplatesContainer.getPointTemplate(newPointTemplateId);
          if (nodeTagTemplate instanceof PointTemplateEntity) {
            newPointTemplate = (PointTemplateEntity)nodeTagTemplate;
          } else {
            LOGGER.error("New point template with id: ["
                + newPointTemplateId
                + "] is not an instance of PointTemplateEntity, rather it is: ["
                + nodeTagTemplate.getClassAndNaturalIdentity()
                + "].");
          }
        }
        customAsyncComputedPoint.setPointTemplate(newPointTemplate);
        
        // Get the new unit.
        Integer newUnitId = dto.getUnitId();
        UnitEntity newUnit = null;
        if (newUnitId != null) {
          newUnit = unitsContainer.getUnit(newUnitId);
        }
        customAsyncComputedPoint.setUnit(newUnit);

        responseList.add(customAsyncComputedPoint);
        
        Map<String, Object> additionalProperties = dto.getAdditionalProperties();
        if (additionalProperties != null && !additionalProperties.isEmpty()) {

          String metricId = (String)extractParameter(additionalProperties, "metricId");
          customAsyncComputedPoint.setMetricId(metricId);
          
          ComputationInterval computationInterval = ComputationInterval.fromName((String)extractParameter(additionalProperties, "computationInterval"));
          customAsyncComputedPoint.setComputationInterval(computationInterval);

          Integer unitId = (Integer)extractParameter(additionalProperties, "unitId");
          
          Integer pointTemplateId = (Integer)extractParameter(additionalProperties, "pointTemplateId");
          PointTemplateEntity pointTemplate = null;
          Set<TagEntity> nodeTags = null;
          if (pointTemplateId != null) {
            
            pointTemplate = dictionaryRepository.getNodeTagTemplatesContainer().getPointTemplate(pointTemplateId);
            
            customAsyncComputedPoint.setPointTemplate(pointTemplate);
            
            customAsyncComputedPoint.removeAllNodeTags();
            nodeTags = pointTemplate.getTags();
            customAsyncComputedPoint.addNodeTags(nodeTags);
            
            if (unitId == null && pointTemplate instanceof PointTemplateEntity) {
              
              unitId = ((PointTemplateEntity)pointTemplate).getUnit().getPersistentIdentity();
            }
          }

          UnitEntity unit = null;
          if (unitId != null) {
            
            unit = dictionaryRepository.getUnitsContainer().getUnit(unitId);
            customAsyncComputedPoint.setUnit(unit);
          }
          
          @SuppressWarnings("unchecked")
          List<Map<String, Object>> childTemporalConfigProps = (List<Map<String, Object>>)extractParameter(additionalProperties, "childTemporalConfigs");
          for (Map<String, Object> childTemporalConfigProp: childTemporalConfigProps) {

            Integer id = (Integer)childTemporalConfigProp.get("id");
            TemporalAsyncComputedPointConfigEntity childTemporalConfig = customAsyncComputedPoint.getChildTemporalConfig(id);
            
            LocalDate effectiveDate = LocalDate.parse((String)childTemporalConfigProp.get("effectiveDate"));
            childTemporalConfig.setEffectiveDate(effectiveDate);
            
            String formula = (String)childTemporalConfigProp.get("formula");
            childTemporalConfig.setFormula(formula);
            
            String description = (String)childTemporalConfigProp.get("description");
            childTemporalConfig.setDescription(description);
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> childVariableProps = (List<Map<String, Object>>)childTemporalConfigProp.get("childVariables");
            if (childVariableProps != null) {
              for (Map<String, Object> childVariableProp: childVariableProps) {
                
                Integer variablePointId = (Integer)childVariableProp.get("pointId");
                Integer variableFillPolicyId = (Integer)childVariableProp.get("fillPolicyId");
                String variableName = (String)childVariableProp.get("name");
                
                FormulaVariableEntity childVariable = childTemporalConfig.getChildVariableByMappablePointIdNullIfNotExists(variablePointId);
                if (childVariable != null) {
                  
                  childVariable.setName(variableName);
                  childVariable.setFillPolicy(FillPolicy.fromId(variableFillPolicyId));
                  
                } else {
                  childTemporalConfig.addChildVariable(new FormulaVariableEntity(
                      childTemporalConfig,
                      portfolio.getCustomPointFormulaVariableEligiblePointNullIfNotExists(variablePointId),
                      variableName,
                      FillPolicy.fromId(variableFillPolicyId)));
                }
              }          
            }
          }
        }
        
      } else {
        throw new IllegalArgumentException("Point: ["
            + point.getClassAndNaturalIdentity()
            + "] is not a custom asynchronous computed point.");
      }
    }
   
    // Intelligent store to the repository.
    updatePortfolio(
        portfolio,
        request);
    
    // Return the list of mappable point nodes that have been modified.
    return responseList;
  }
  
  @Override
  public List<AbstractNodeEntity> getEligibleChildNodesforMoveToNewParentNode(
      MoveChildNodesRequest moveChildNodesRequest)
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException {

    LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
        .builder()
        .withCustomerId(moveChildNodesRequest.getCustomerId())
        .withLoadDistributorPaymentMethods(Boolean.TRUE)
        .withLoadDistributorUsers(Boolean.TRUE)
        .build();
    
    Integer buildingId = moveChildNodesRequest.getBuildingId();
    if (buildingId != null) {

      loadPortfolioOptions = LoadPortfolioOptions
          .builder(loadPortfolioOptions)
          .withFilterNodeType(NodeType.BUILDING)
          .withFilterNodePersistentIdentity(buildingId)
          .withDepthNodeType(NodeType.POINT)
          .build();
      
    } else {

      loadPortfolioOptions = LoadPortfolioOptions
          .builder(loadPortfolioOptions)
          .withDepthNodeType(NodeType.POINT)
          .build();
      
    }
    
    PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);       
    
    AbstractNodeEntity newParentNode = portfolio.getChildNode(moveChildNodesRequest.getNewParentId());
    
    List<AbstractNodeEntity> childNodes = new ArrayList<>();
    for (Integer childId: moveChildNodesRequest.getChildIds()) {
      childNodes.add(portfolio.getChildNode(childId));
    }
    
    if (portfolio.getParentCustomer() instanceof OnlineCustomerEntity) {

      return BillableBuildingPointLimiter.processForBuildingPointCaps(
          portfolio, 
          newParentNode, 
          childNodes);
    }
    
    return childNodes;
  }

  @Override
  public List<AbstractNodeEntity> moveChildNodesToNewParentNode(
      MoveChildNodesRequest request)
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException,
      StaleDataException {
    
    // Load the portfolio (down to the point level).
    long start = System.currentTimeMillis();
    
    LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
        .builder()
        .withCustomerId(request.getCustomerId())
        .withLoadDistributorPaymentMethods(Boolean.TRUE)
        .withLoadDistributorUsers(Boolean.TRUE)
        .build();
    
    List<Integer> buildingIds = request.getBuildingIds();
    if (buildingIds != null && !buildingIds.isEmpty()) {

      loadPortfolioOptions = LoadPortfolioOptions
          .builder(loadPortfolioOptions)
          .withFilterNodeType(NodeType.BUILDING)
          .withFilterNodePersistentIdentities(buildingIds)
          .build();
    }    
    
    Boolean performAutomaticRemediation = request.getPerformAutomaticRemediation();
    
    Boolean performAutomaticEvaluateReports = request.getPerformAutomaticEvaluateReports();
    
    Boolean performAutomaticConfiguration = request.getPerformAutomaticConfiguration();
    
    if (performAutomaticRemediation 
        || performAutomaticEvaluateReports
        || performAutomaticConfiguration) {

      loadPortfolioOptions = LoadPortfolioOptions
          .builder(loadPortfolioOptions)
          .withLoadAdFunctionInstances(Boolean.TRUE)
          .withLoadReportInstances(performAutomaticEvaluateReports)
          .build();    
      
    } else {

      loadPortfolioOptions = LoadPortfolioOptions
          .builder(loadPortfolioOptions)
          .withDepthNodeType(NodeType.POINT)
          .build();    

    }
    
    PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);   
    
    AbstractNodeEntity newParentNode = portfolio.getChildNode(request.getNewParentId());
    
    List<AbstractNodeEntity> childNodes = new ArrayList<>();
    for (Integer childId: request.getChildIds()) {
      
      AbstractNodeEntity childNode = portfolio.getChildNode(childId); 
      childNodes.add(childNode);
    }
    
    // If the portfolio is owned by an online customer, then we need to perform point limiting.
    List<AbstractNodeEntity> eligibleChildNodes = null;
    
    Timestamp oldestSourceBuildingStatusUpdatedAt = null;
    AbstractCustomerEntity parentCustomer = portfolio.getParentCustomer();
    if (parentCustomer instanceof OnlineCustomerEntity) {

      eligibleChildNodes = BillableBuildingPointLimiter.processForBuildingPointCaps(
          portfolio, 
          newParentNode, 
          childNodes);
      
      for (AbstractNodeEntity childNode: eligibleChildNodes) {
        
        BuildingEntity sourceBuilding = childNode.getAncestorBuilding();
        Timestamp sourceBuildingStatusUpdatedAt = sourceBuilding.getBuildingStatusUpdatedAt();
        if (oldestSourceBuildingStatusUpdatedAt == null 
            || sourceBuildingStatusUpdatedAt.before(oldestSourceBuildingStatusUpdatedAt)) {
          
          oldestSourceBuildingStatusUpdatedAt = sourceBuildingStatusUpdatedAt;
        }
      }
    } else {
      eligibleChildNodes = childNodes;
    }
    
    // Perform the move(s).
    for (AbstractNodeEntity childNode: eligibleChildNodes) {
    
      moveChildNodesToNewParentNode(newParentNode, childNode);
    }
    
    if (parentCustomer instanceof OnlineCustomerEntity) {
      
      BuildingEntity targetBuilding = newParentNode.getAncestorBuilding();
      if (targetBuilding instanceof BillableBuildingEntity) {

        BillableBuildingEntity targetBillableBuilding = (BillableBuildingEntity)targetBuilding;
        
        // Act based on how many mapped points exist for the target building.
        if (targetBillableBuilding.getTotalMappedPointCount() > 0) {

          // If the building is PENDING_ACTIVATION, then use the status updated at from
          // the source building, preventing the user from tricking the system.
          if (targetBillableBuilding.getBuildingStatus().equals(BuildingStatus.PENDING_ACTIVATION)) {

            LOGGER.info("Changing building {} status updated at from {} to {}",
                targetBillableBuilding,
                targetBillableBuilding.getBuildingStatusUpdatedAt(),
                oldestSourceBuildingStatusUpdatedAt);

            targetBillableBuilding.setBuildingStatusUpdatedAt(oldestSourceBuildingStatusUpdatedAt);
            
          } else if (targetBillableBuilding.getBuildingStatus().equals(BuildingStatus.CREATED)) {
            
            // Otherwise, if the building was CREATED, move it into PENDING_ACTIVATION
            targetBillableBuilding.setBuildingStatus(BuildingStatus.PENDING_ACTIVATION);
            
          }
          
        } else if (targetBillableBuilding.getBuildingStatus().equals(BuildingStatus.PENDING_ACTIVATION)) {
          
          // The target building doesn't have any mapped points, so move it back to CREATED
          targetBillableBuilding.setBuildingStatus(BuildingStatus.CREATED);
          
        }
      }
    }
    
    boolean isModified = portfolio.getIsModified();

    // If specified, then automatically perform validation/remediation.
    if (isModified && performAutomaticRemediation) {

      Set<IssueType> issueTypes = null;
      List<ValidationMessage> validationMessages = remediatePortfolio(
          portfolio, 
          issueTypes);
      
      PortfolioVisitor.findAdFunctionInstanceCandidates(portfolio);
      
      if (LOGGER.isDebugEnabled() && !validationMessages.isEmpty()) {
        LOGGER.debug("Resolved {} issue(s)", validationMessages.size());
      }    
    }
    
    // If specified, then automatically enable/instantiate any available 
    // AD rule/computed point function candidates.
    if (isModified && performAutomaticConfiguration && portfolio.getParentCustomer().allowAutomaticConfiguration()) {

      List<AbstractAdFunctionInstanceEntity> createdInstances = createAdFunctionInstancesNoStore(
          portfolio,
          portfolio.getAllAdFunctionInstanceCandidates(),
          Boolean.FALSE);
      
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Created {} AD function instance(s)", createdInstances.size());
      }
    }
    
    // If specified, then automatically evaluate the state of the portfolio for reports.
    if (isModified && performAutomaticEvaluateReports) {
      
      List<ReportInstanceEntity> modifiedReportInstances = PortfolioVisitor.evaluateReports(portfolio);
      
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Modified {} report instance(s)", modifiedReportInstances.size());
      }
    }
    
    // If specified, then automatically enable any valid/disabled report instances.
    if (isModified && performAutomaticConfiguration && portfolio.getParentCustomer().allowAutomaticConfiguration()) {
      updateReportInstancesNoStore(
          portfolio,
          new ArrayList<>());      
    }     
    
    // Intelligent store to the repository.
    Map<String, List<AbstractNodeEntity>> affectedNodes = updatePortfolio(
        portfolio,
        request,
        performAutomaticEvaluateReports);
    
    List<AbstractNodeEntity> updatedNodes = affectedNodes.get(NodeHierarchyRepository.UPDATED);
    
    LOGGER.info("moveChildNodesToNewParentNode(): updated node count: {} elapsed(ms): {}",
        updatedNodes.size(),
        (System.currentTimeMillis()-start));
    
    return eligibleChildNodes;
  }
  
  private void moveChildNodesToNewParentNode(AbstractNodeEntity newParentNode, AbstractNodeEntity childNode) {
    
    if (childNode instanceof LoopEntity && !childNode.getParentNode().equals(newParentNode)) {
      
      throw new IllegalArgumentException("Loop: ["
          + childNode.getNodePath()
          + "] is not allowed to be moved from parent plant: ["
          + childNode.getParentNode().getNodePath()
          + "] to: ["
          + newParentNode.getNodePath()
          + "].");
    }
    
    childNode.setNewParentNode(newParentNode);
  }
  
  @Override
  public List<AbstractNodeEntity> deleteChildNodes(
      DeleteChildNodesRequest request)
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException,
      StaleDataException {
    
    // Load the portfolio (down to the point level).
    long start = System.currentTimeMillis();
    
    LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
        .builder()
        .withCustomerId(request.getCustomerId())
        .build();
    
    List<Integer> buildingIds = request.getBuildingIds();
    if (buildingIds != null && !buildingIds.isEmpty()) {

      loadPortfolioOptions = LoadPortfolioOptions
          .builder(loadPortfolioOptions)
          .withFilterNodeType(NodeType.BUILDING)
          .withFilterNodePersistentIdentities(buildingIds)
          .build();
    }    
    
    Boolean performAutomaticRemediation = request.getPerformAutomaticRemediation();
    
    Boolean performAutomaticEvaluateReports = request.getPerformAutomaticEvaluateReports();
    
    Boolean performAutomaticConfiguration = request.getPerformAutomaticConfiguration();
    
    if (performAutomaticRemediation 
        || performAutomaticEvaluateReports
        || performAutomaticConfiguration) {

      loadPortfolioOptions = LoadPortfolioOptions
          .builder(loadPortfolioOptions)
          .withLoadAdFunctionInstances(Boolean.TRUE)
          .withLoadReportInstances(performAutomaticEvaluateReports)
          .build();    
      
    } else {

      loadPortfolioOptions = LoadPortfolioOptions
          .builder(loadPortfolioOptions)
          .withDepthNodeType(NodeType.POINT)
          .build();    

    }
    
    PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);       
    
    // RP-9895: When deleting mappable points, optionally ignore the corresponding raw points.
    Boolean ignoreMappablePointRawPoint = request.getIgnoreMappablePointRawPoint();
    
    for (Integer childId: request.getChildIds()) {
      
      deleteChildNode(
          portfolio, 
          childId, 
          ignoreMappablePointRawPoint);
    }
    
    boolean isModified = portfolio.getIsModified();

    // If specified, then automatically perform validation/remediation.
    if (isModified && performAutomaticRemediation) {

      Set<IssueType> issueTypes = null;
      List<ValidationMessage> validationMessages = remediatePortfolio(
          portfolio, 
          issueTypes);
      
      PortfolioVisitor.findAdFunctionInstanceCandidates(portfolio);
      
      if (LOGGER.isDebugEnabled() && !validationMessages.isEmpty()) {
        LOGGER.debug("Resolved {} issue(s)", validationMessages.size());
      }    
    }
    
    // If specified, then automatically enable/instantiate any available 
    // AD rule/computed point function candidates.
    if (isModified && performAutomaticConfiguration && portfolio.getParentCustomer().allowAutomaticConfiguration()) {

      List<AbstractAdFunctionInstanceEntity> createdInstances = createAdFunctionInstancesNoStore(
          portfolio,
          portfolio.getAllAdFunctionInstanceCandidates(),
          Boolean.FALSE);
      
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Created {} AD function instance(s)", createdInstances.size());
      }
    }
    
    // If specified, then automatically evaluate the state of the portfolio for reports.
    if (isModified && performAutomaticEvaluateReports) {
      
      List<ReportInstanceEntity> modifiedReportInstances = PortfolioVisitor.evaluateReports(portfolio);
      
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Modified {} report instance(s)", modifiedReportInstances.size());
      }
    }
    
    // If specified, then automatically enable any valid/disabled report instances.
    if (isModified && performAutomaticConfiguration && portfolio.getParentCustomer().allowAutomaticConfiguration()) {
      updateReportInstancesNoStore(
          portfolio,
          new ArrayList<>());      
    }     
    
    // Intelligent store to the repository.
    Map<String, List<AbstractNodeEntity>> affectedNodes = updatePortfolio(
        portfolio,
        request,
        performAutomaticEvaluateReports);
        
    List<AbstractNodeEntity> deletedNodes = affectedNodes.get(NodeHierarchyRepository.DELETED);
    
    LOGGER.info("deleteChildNodes(): deleted node count: {} elapsed(ms): {}",
        deletedNodes.size(),
        (System.currentTimeMillis()-start));
    
    return deletedNodes;
  }
  
  private void deleteChildNode(PortfolioEntity portfolio, Integer childId, boolean ignoreMappablePointRawPoint) throws EntityDoesNotExistException {
    
    AbstractNodeEntity childNode = portfolio.getChildNode(childId);
    childNode.setIsDeleted();
    
    if (ignoreMappablePointRawPoint && childNode instanceof MappablePointEntity) {
      
      ((MappablePointEntity)childNode).getRawPoint().setIgnored(ignoreMappablePointRawPoint);
      
    } else if (childNode instanceof LoopEntity) {

      // RP-12872: When a loop is removed, any nested points should move to the parent plant that the loop was created under and loop relationships 
      // should be deleted. When a parent loop is removed, any nested loops will be removed as well with associated points being moved to the parent plant.
      deleteLoop(portfolio, (LoopEntity)childNode);
      
    }
  }
  
  private void deleteLoop(PortfolioEntity portfolio, LoopEntity loop) throws EntityDoesNotExistException {
    
    // Mark the loop as deleted for the node hierarchy relationship.
    loop.setIsDeleted();

    // First, move any child points of the loop to the parent plant.
    PlantEntity parentPlant = (PlantEntity)loop.getParentNode();
    List<AbstractPointEntity> childPoints = new ArrayList<>();
    childPoints.addAll(loop.getChildPoints());
    for (int i=0; i < childPoints.size(); i++) {
      
      AbstractPointEntity childPoint = childPoints.get(i);
      childPoint.setNewParentNode(parentPlant);
    }
    
    // When a loop is created, it is always under a plant as the node hierarchy parent.  One energy exchange relationship is added at the same time.
    // So, we figure out what it was, so that we can traverse the energy exchange system graph and deal with any child loops.  We move all points from
    // loops to the parent plant and we delete any child loops (which would also be node hierarchy children of the plant, so the user essentially wants
    // to start from scratch.
    EnergyExchangeSystemType energyExchangeSystemType = EnergyExchangeSystemType.CHILLED_WATER;
    Set<EnergyExchangeEntity> childEnergyExchangeSystemNodes = null;
    Set<EnergyExchangeEntity> parentEnergyExchangeSystemNodes = loop.getParentEnergyExchangeSystemNodes(energyExchangeSystemType);
    if (parentEnergyExchangeSystemNodes.contains(parentPlant)) {
      
      childEnergyExchangeSystemNodes = loop.getChildEnergyExchangeSystemNodes(energyExchangeSystemType);
      
    } else {
      
      energyExchangeSystemType = EnergyExchangeSystemType.HOT_WATER;
      parentEnergyExchangeSystemNodes = loop.getParentEnergyExchangeSystemNodes(energyExchangeSystemType);
      if (parentEnergyExchangeSystemNodes.contains(parentPlant)) {
        
        childEnergyExchangeSystemNodes = loop.getChildEnergyExchangeSystemNodes(energyExchangeSystemType);
        
      } else {
        
        energyExchangeSystemType = EnergyExchangeSystemType.STEAM;
        parentEnergyExchangeSystemNodes = loop.getParentEnergyExchangeSystemNodes(energyExchangeSystemType);
        if (parentEnergyExchangeSystemNodes.contains(parentPlant)) {
          
          childEnergyExchangeSystemNodes = loop.getChildEnergyExchangeSystemNodes(energyExchangeSystemType);
          
        } else {
          
          energyExchangeSystemType = EnergyExchangeSystemType.AIR_SUPPLY;
          parentEnergyExchangeSystemNodes = loop.getParentEnergyExchangeSystemNodes(energyExchangeSystemType);
          if (parentEnergyExchangeSystemNodes.contains(parentPlant)) {
            
            childEnergyExchangeSystemNodes = loop.getChildEnergyExchangeSystemNodes(energyExchangeSystemType);
            
          }
        }
      }
    }
    
    if (childEnergyExchangeSystemNodes != null) {
      
      List<EnergyExchangeEntity> childEnergyExchangeSystemNodesList = new ArrayList<>();
      childEnergyExchangeSystemNodesList.addAll(childEnergyExchangeSystemNodes);
      for (int i=0; i < childEnergyExchangeSystemNodes.size(); i++) {
        
        EnergyExchangeEntity childEnergyExchangeSystemNode = childEnergyExchangeSystemNodesList.get(i); 
        
        if (childEnergyExchangeSystemNode instanceof LoopEntity) {
          
          deleteLoop(portfolio, (LoopEntity)childEnergyExchangeSystemNode);
        }
        
        childEnergyExchangeSystemNode.removeParentEnergyExchangeSystemNode(
            energyExchangeSystemType, 
            loop, 
            true);
      }
      
    } else {
      LOGGER.error("Loop: [{}] does not have a parent energy exchange system plant, unable to process delete properly.",
          loop.getNodePath());
    }
  }
  
  @Override
  public Map<Integer, MappablePointEntity> unmapRawPoints(
      UnmapRawPointsRequest request)
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException,
      StaleDataException {
    
    // Load the portfolio (down to the point level).
    long start = System.currentTimeMillis();
    
    LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
        .builder()
        .withCustomerId(request.getCustomerId())
        .build();
    
    List<Integer> buildingIds = request.getBuildingIds();
    if (buildingIds != null && !buildingIds.isEmpty()) {

      loadPortfolioOptions = LoadPortfolioOptions
          .builder(loadPortfolioOptions)
          .withFilterNodeType(NodeType.BUILDING)
          .withFilterNodePersistentIdentities(buildingIds)
          .build();
    }    
    
    Boolean performAutomaticRemediation = request.getPerformAutomaticRemediation();
    
    Boolean performAutomaticEvaluateReports = request.getPerformAutomaticEvaluateReports();
    
    Boolean performAutomaticConfiguration = request.getPerformAutomaticConfiguration();
    
    if (performAutomaticRemediation 
        || performAutomaticEvaluateReports
        || performAutomaticConfiguration) {

      loadPortfolioOptions = LoadPortfolioOptions
          .builder(loadPortfolioOptions)
          .withLoadAdFunctionInstances(Boolean.TRUE)
          .withLoadReportInstances(performAutomaticEvaluateReports)
          .build();    
      
    } else {

      loadPortfolioOptions = LoadPortfolioOptions
          .builder(loadPortfolioOptions)
          .withDepthNodeType(NodeType.POINT)
          .build();    

    }
    
    PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);      
    
    Set<Integer> set = new HashSet<>();
    set.addAll(request.getRawPoints());
    for (MappablePointEntity mappablePoint: portfolio.getAllMappablePoints()) {
      if (set.contains(mappablePoint.getRawPoint().getPersistentIdentity())) {
        mappablePoint.setIsDeleted();
      }
    }
    
    boolean isModified = portfolio.getIsModified();

    // If specified, then automatically perform validation/remediation.
    if (isModified && performAutomaticRemediation) {

      Set<IssueType> issueTypes = null;
      List<ValidationMessage> validationMessages = remediatePortfolio(
          portfolio, 
          issueTypes);
      
      PortfolioVisitor.findAdFunctionInstanceCandidates(portfolio);
      
      if (LOGGER.isDebugEnabled() && !validationMessages.isEmpty()) {
        LOGGER.debug("Resolved {} issue(s)", validationMessages.size());
      }    
    }
    
    // If specified, then automatically enable/instantiate any available 
    // AD rule/computed point function candidates.
    if (isModified && performAutomaticConfiguration && portfolio.getParentCustomer().allowAutomaticConfiguration()) {

      List<AbstractAdFunctionInstanceEntity> createdInstances = createAdFunctionInstancesNoStore(
          portfolio,
          portfolio.getAllAdFunctionInstanceCandidates(),
          Boolean.FALSE);
      
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Created {} AD function instance(s)", createdInstances.size());
      }
    }
    
    // If specified, then automatically evaluate the state of the portfolio for reports.
    if (isModified && performAutomaticEvaluateReports) {
      
      List<ReportInstanceEntity> modifiedReportInstances = PortfolioVisitor.evaluateReports(portfolio);
      
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Modified {} report instance(s)", modifiedReportInstances.size());
      }
    }
    
    // If specified, then automatically enable any valid/disabled report instances.
    if (isModified && performAutomaticConfiguration && portfolio.getParentCustomer().allowAutomaticConfiguration()) {
      updateReportInstancesNoStore(
          portfolio,
          new ArrayList<>());      
    }     
    
    // Intelligent store to the repository.
    Map<String, List<AbstractNodeEntity>> affectedNodes = updatePortfolio(
        portfolio,
        request,
        performAutomaticEvaluateReports);
    
    List<AbstractNodeEntity> deletedNodes = affectedNodes.get(NodeHierarchyRepository.DELETED);
    
    Map<Integer, MappablePointEntity> map = new HashMap<>();
    for (AbstractNodeEntity deletedNode: deletedNodes) {
      if (deletedNode instanceof MappablePointEntity) {
        MappablePointEntity point = (MappablePointEntity)deletedNode;
        map.put(point.getRawPoint().getPersistentIdentity(), point);
      }
    }
    
    LOGGER.info("unmapRawPoints(): deleted point count: {} elapsed(ms): {}",
        map.keySet().size(),
        (System.currentTimeMillis()-start));
    
    return map;
  }
  
  @Override
  public Boolean ignoreRawPoints(IgnoreRawPointsRequest request) 
  throws 
      EntityDoesNotExistException,
      StaleDataException {

    long start = System.currentTimeMillis();
    
    AbstractCustomerEntity customer = customerRepository.loadCustomer(
        request.getCustomerId(),
        false,  // loadDistributorPaymentMethods
        false); // loadDistributorUsers
    rawPointRepository.ignoreRawPoints(customer, request.getRawPoints());
    
    LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
        .builder()
        .withCustomerId(request.getCustomerId())
        .build();
    
    List<Integer> buildingIds = request.getBuildingIds();
    if (buildingIds != null && !buildingIds.isEmpty()) {

      loadPortfolioOptions = LoadPortfolioOptions
          .builder(loadPortfolioOptions)
          .withFilterNodeType(NodeType.BUILDING)
          .withFilterNodePersistentIdentities(buildingIds)
          .build();
    }    
    
    Boolean performAutomaticRemediation = request.getPerformAutomaticRemediation();
    
    Boolean performAutomaticEvaluateReports = request.getPerformAutomaticEvaluateReports();
    
    Boolean performAutomaticConfiguration = request.getPerformAutomaticConfiguration();
    
    if (performAutomaticRemediation 
        || performAutomaticEvaluateReports
        || performAutomaticConfiguration) {

      loadPortfolioOptions = LoadPortfolioOptions
          .builder(loadPortfolioOptions)
          .withLoadAdFunctionInstances(Boolean.TRUE)
          .withLoadReportInstances(performAutomaticEvaluateReports)
          .build();    
      
    } else {

      loadPortfolioOptions = LoadPortfolioOptions
          .builder(loadPortfolioOptions)
          .withDepthNodeType(NodeType.POINT)
          .build();    

    }
    
    Set<Integer> set = new HashSet<>();
    set.addAll(request.getRawPoints());

    PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);
    for (MappablePointEntity mappablePoint: portfolio.getAllMappablePoints()) {
      if (set.contains(mappablePoint.getRawPoint().getPersistentIdentity())) {
        mappablePoint.setIsDeleted();
      }
    }
    
    boolean isModified = portfolio.getIsModified();

    // If specified, then automatically perform validation/remediation.
    if (isModified && performAutomaticRemediation) {

      Set<IssueType> issueTypes = null;
      List<ValidationMessage> validationMessages = remediatePortfolio(
          portfolio, 
          issueTypes);
      
      PortfolioVisitor.findAdFunctionInstanceCandidates(portfolio);
      
      if (LOGGER.isDebugEnabled() && !validationMessages.isEmpty()) {
        LOGGER.debug("Resolved {} issue(s)", validationMessages.size());
      }    
    }
    
    // If specified, then automatically enable/instantiate any available 
    // AD rule/computed point function candidates.
    if (isModified && performAutomaticConfiguration && portfolio.getParentCustomer().allowAutomaticConfiguration()) {

      List<AbstractAdFunctionInstanceEntity> createdInstances = createAdFunctionInstancesNoStore(
          portfolio,
          portfolio.getAllAdFunctionInstanceCandidates(),
          Boolean.FALSE);
      
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Created {} AD function instance(s)", createdInstances.size());
      }
    }
    
    // If specified, then automatically evaluate the state of the portfolio for reports.
    if (isModified && performAutomaticEvaluateReports) {
      
      List<ReportInstanceEntity> modifiedReportInstances = PortfolioVisitor.evaluateReports(portfolio);
      
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Modified {} report instance(s)", modifiedReportInstances.size());
      }
    }
    
    // If specified, then automatically enable any valid/disabled report instances.
    if (isModified && performAutomaticConfiguration && portfolio.getParentCustomer().allowAutomaticConfiguration()) {
      updateReportInstancesNoStore(
          portfolio,
          new ArrayList<>());      
    }     
    
    // Intelligent store to the repository.
    Map<String, List<AbstractNodeEntity>> affectedNodes = updatePortfolio(
        portfolio,
        request,
        performAutomaticEvaluateReports);
    
    List<AbstractNodeEntity> deletedNodes = affectedNodes.get(NodeHierarchyRepository.DELETED);
    
    Map<Integer, MappablePointEntity> map = new HashMap<>();
    for (AbstractNodeEntity deletedNode: deletedNodes) {
      if (deletedNode instanceof MappablePointEntity) {
        MappablePointEntity point = (MappablePointEntity)deletedNode;
        map.put(point.getRawPoint().getPersistentIdentity(), point);
      }
    }
    
    LOGGER.info("ignoreRawPoints(): deleted point count: {} elapsed(ms): {}",
        map.keySet().size(),
        (System.currentTimeMillis()-start));
    
    return Boolean.TRUE;
  }
  
  @Override
  public Boolean unignoreRawPoints(UnignoreRawPointsRequest request) 
  throws 
      EntityDoesNotExistException {

    long start = System.currentTimeMillis();
    
    AbstractCustomerEntity customer = customerRepository.loadCustomer(
        request.getCustomerId(),
        false,  // loadDistributorPaymentMethods
        false); // loadDistributorUsers
    rawPointRepository.unignoreRawPoints(customer, request.getRawPoints());
    
    LOGGER.info("unignoreRawPoints(): customer: {} elapsed(ms): {}",
        customer,
        (System.currentTimeMillis()-start));
    
    return Boolean.TRUE;
  }
  
  @Override
  public BuildingSubscriptionEntity createBuildingSubscription(
      Integer customerId,
      Integer billableBuildingId,
      Integer paymentPlanId,
      Integer paymentMethodId) 
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException,
      StaleDataException,
      StripeClientException {
    
    // Load the portfolio (down to the building level). 
    LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
        .builder()
        .withCustomerId(customerId)
        .withFilterNodeType(NodeType.BUILDING)
        .withFilterNodePersistentIdentity(billableBuildingId)
        .withDepthNodeType(NodeType.BUILDING)
        .withLoadDistributorPaymentMethods(Boolean.TRUE)
        .withLoadDistributorUsers(Boolean.TRUE)
        .build();
    
    PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);         
    
    AbstractCustomerEntity parentCustomer = portfolio.getParentCustomer();
    if (parentCustomer instanceof OnlineCustomerEntity) {
      
      AbstractDistributorEntity parentDistributor = parentCustomer.getParentDistributor();
      if (parentDistributor instanceof OnlineDistributorEntity) {
        
        OnlineDistributorEntity onlineDistributor = (OnlineDistributorEntity)parentDistributor;
        
        BuildingEntity building = portfolio.getChildBuilding(billableBuildingId);
        
        if (building instanceof BillableBuildingEntity) {
          
          BillableBuildingEntity billableBuilding = (BillableBuildingEntity)building;

          PaymentPlanEntity paymentPlan = dictionaryRepository
              .getPaymentPlansContainer()
              .getPaymentPlan(paymentPlanId);
          
          AbstractPaymentMethodEntity paymentMethod = onlineDistributor
              .getChildPaymentMethod(paymentMethodId);
          
          return createBuildingSubscription(
              customerId,
              billableBuilding, 
              paymentPlan, 
              paymentMethod);
          
        } else {
          throw new IllegalStateException("Building: ["
              + building
              + "] is not an instance of BillableBuildingEntity");
        }
      } else {
        throw new IllegalStateException("Distributor: ["
            + parentDistributor
            + "] is not an instance of OnlineDistributorEntity");
      }
    } else {
      throw new IllegalStateException("Customer: ["
          + parentCustomer
          + "] is not an instance of OnlineCustomerEntity");
    }
  }
  
  private BuildingSubscriptionEntity createBuildingSubscription(
      Integer customerId,
      BillableBuildingEntity billableBuilding,
      PaymentPlanEntity paymentPlan,
      AbstractPaymentMethodEntity paymentMethod) 
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException,
      StaleDataException,
      StripeClientException {
    
    String errorMessage = "Unable to create building subscription for building: ["
        + billableBuilding
        + "] using payment plan: ["
        + paymentPlan
        + "] and payment method: ["
        + paymentMethod
        + "]";
    
    AbstractCustomerEntity customer = billableBuilding.getRootPortfolioNode().getParentCustomer();
    OnlineDistributorEntity distributor = (OnlineDistributorEntity)customer.getParentDistributor();
    
    String stripeCustomerId = distributor.getStripeCustomerId();
    if (stripeCustomerId == null || stripeCustomerId.trim().isEmpty()) {
      
      throw new IllegalStateException("Cannot create a Stripe subscription because distributor: ["
          + distributor
          + "] does not have its stripeCustomerId set.");
    }
    
    String stripePlanId = null;
    if (stripeClient.isLiveMode()) {
      stripePlanId = paymentPlan.getStripePlanId();      
    } else {
      stripePlanId = paymentPlan.getStripeTestPlanId();
    }
    
    String stripeSourceId = paymentMethod.getStripeSourceId();
    StripeClientResponse subscriptionResponse = stripeClient.createStripeSubscription(
        stripeCustomerId, 
        stripeSourceId, 
        stripePlanId,
        customer.getName(),
        billableBuilding.getDisplayName(),
        billableBuilding.getUuid());
    
    if (!subscriptionResponse.getResult().equals(StripeClientResponse.RESULT_SUCCESS)) {
    
      throw new StripeClientException(
          errorMessage
          + " Reason: "
          + subscriptionResponse.getReason());      
    }
    
    StripeSubscription stripeSubscription = (StripeSubscription)subscriptionResponse
        .getResponseObjects()
        .get(StripeClient.SUBSCRIPTION);
    
    String stripeSubscriptionId = stripeSubscription.getStripeSubscriptionId();
    
    BuildingSubscriptionEntity buildingSubscription = nodeHierarchyRepository.createBuildingSubscription(
        billableBuilding, 
        paymentPlan, 
        paymentMethod, 
        stripeSubscriptionId);
    
    PortfolioEntity portfolio = billableBuilding.getRootPortfolioNode();
    updatePortfolio(
        portfolio, 
        BuildingSubscriptionRequest
            .builder()
            .withBuildingId(billableBuilding.getPersistentIdentity())
            .withCustomerId(customerId)
            .withOperationSubType(BuildingSubscriptionRequest.CREATE_BUILDING_SUBSCRIPTION)
            .build(), 
        false);
    
    /*
      CUSTOMER/DISTRIBUTOR AUTOMATIC STATE TRANSITIONS FOR THE JDBC REPOSITORY IMPL:
      =================================================================================================
      status:           CREATED -> BILLABLE         (at least one billable building)    Yes (automatic)
      status:           BILLABLE -> CREATED         (no billable buildings)             Yes (automatic)
      
      payment_status:   UP_TO_DATE -> DELINQUENT    (at least one delinquent building)  Yes (automatic)
      payment_status:   DELINQUENT -> UP_TO_DATE    (no delinquent buildings)           Yes (automatic)
      
      DISTRIBUTOR ONLY:
      payment_status:   PAST_DUE -> UP_TO_DATE      (no delinquent buildings)           Yes (automatic)
     */
    
    OnlineCustomerEntity onlineCustomer = (OnlineCustomerEntity)billableBuilding.getRootPortfolioNode().getParentCustomer();
    CustomerStatus currentCustomerStatus = onlineCustomer.getCustomerStatus();
    if (!currentCustomerStatus.equals(CustomerStatus.BILLABLE)) {

      CustomerStatus newCustomerStatus = CustomerStatus.BILLABLE;
      
      int activeBuildingCount = onlineCustomer.getActiveBuildingCount();
      LOGGER.info(
          AbstractEntity.getTimeKeeper().getCurrentLocalDate()
          + ": "
          + onlineCustomer 
          + ": config status: "
          + currentCustomerStatus
          + " --> "
          + newCustomerStatus
          + ": activeBuildingCount: "
          + activeBuildingCount);
      onlineCustomer.setCustomerStatus(newCustomerStatus);

      // AUTOMATIC IN DB
      if (customerRepository instanceof CustomerRepositoryFileSystemImpl) {
        customerRepository.storeCustomer(onlineCustomer);  
      }
    }
    
    OnlineDistributorEntity onlineDistributor = (OnlineDistributorEntity)onlineCustomer.getParentDistributor();
    DistributorStatus currentDistributorStatus = onlineDistributor.getDistributorStatus();
    if (!currentDistributorStatus.equals(DistributorStatus.BILLABLE)) {

      DistributorStatus newDistributorStatus = DistributorStatus.BILLABLE;
      
      int billableCustomerCount = onlineDistributor.getBillableCustomersCount();
      LOGGER.info(
          AbstractEntity.getTimeKeeper().getCurrentLocalDate()
          + ": "
          + onlineDistributor 
          + ": config status: "
          + currentDistributorStatus
          + " --> "
          + newDistributorStatus
          + ": billableCustomerCount: "
          + billableCustomerCount);
      onlineDistributor.setDistributorStatus(newDistributorStatus);
      
      // AUTOMATIC IN DB
      if (distributorRepository instanceof DistributorRepositoryFileSystemImpl) {
        distributorRepository.storeDistributor(onlineDistributor);  
      }
      
      AbstractDistributorEntity ancestorDistributor = onlineDistributor.getParentDistributor();
      while (ancestorDistributor instanceof OnlineDistributorEntity) {

        OnlineDistributorEntity od = (OnlineDistributorEntity)ancestorDistributor;
        DistributorStatus currentAncestorDistributorStatus = od.getDistributorStatus();
        if (!currentAncestorDistributorStatus.equals(DistributorStatus.BILLABLE)) {

          billableCustomerCount = onlineDistributor.getBillableCustomersCount();
          LOGGER.info(
              AbstractEntity.getTimeKeeper().getCurrentLocalDate()
              + ": "
              + od 
              + ": config status: "
              + currentAncestorDistributorStatus
              + " --> "
              + newDistributorStatus
              + ": billableCustomerCount: "
              + billableCustomerCount);
          od.setDistributorStatus(newDistributorStatus);
          
          // AUTOMATIC IN DB
          if (distributorRepository instanceof DistributorRepositoryFileSystemImpl) {
            distributorRepository.storeDistributor(od);  
          }
          
          ancestorDistributor = od.getParentDistributor();
        }
      }
    }
    
    return buildingSubscription;
  }
  
  @Override
  public void cancelBuildingSubscription(
      Integer customerId,
      Integer billableBuildingId) 
  throws 
      EntityDoesNotExistException,
      StaleDataException,
      StripeClientException {
    
    // Load the portfolio (down to the building level). 
    LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
        .builder()
        .withCustomerId(customerId)
        .withFilterNodeType(NodeType.BUILDING)
        .withFilterNodePersistentIdentity(billableBuildingId)
        .withDepthNodeType(NodeType.BUILDING)
        .withLoadDistributorPaymentMethods(Boolean.TRUE)
        .withLoadDistributorUsers(Boolean.TRUE)
        .build();
    
    PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);    
    
    AbstractDistributorEntity distributor = portfolio.getParentCustomer().getParentDistributor();
    if (distributor instanceof OnlineDistributorEntity) {
      
      BuildingEntity building = portfolio.getChildBuilding(billableBuildingId);
      if (building instanceof BillableBuildingEntity) {
        
        BillableBuildingEntity billableBuilding = (BillableBuildingEntity)building;
        
        BuildingSubscriptionEntity buildingSubscription = billableBuilding.getChildBuildingSubscriptionNullIfNotExists();
        if (buildingSubscription != null) {
          
          cancelBuildingSubscription(customerId, billableBuilding);
          
        } else {
          throw new IllegalStateException("Building: ["
              + building.getClassAndNaturalIdentity() 
              + "] does not have a subscription associated with it");
        }
      } else {
        throw new IllegalStateException("Building: ["
            + building.getClassAndNaturalIdentity() 
            + "] is not a billable building");
      }      
    } else {
      throw new IllegalStateException("Distributor: ["
          + distributor.getClassAndNaturalIdentity() 
          + "] is not an online distributor");
    }
  }
  
  private void cancelBuildingSubscription(
      Integer customerId,
      BillableBuildingEntity billableBuilding) 
  throws 
      EntityDoesNotExistException,
      StaleDataException,
      StripeClientException {
    
    BuildingSubscriptionEntity buildingSubscription = billableBuilding.getChildBuildingSubscriptionNullIfNotExists();
    if (buildingSubscription == null) {
      
      throw new IllegalStateException(
          billableBuilding.getNodePath() 
          + " does not have a subscription, so there is nothing to cancel.");
    }
    
    if (billableBuilding.getPendingDeletion()) {
     
      throw new IllegalStateException(
          billableBuilding.getNodePath() 
          + " has already been canceled, as of: "
          + billableBuilding.getPendingDeletionUpdatedAt());      
    }
    
    String stripeSubscriptionId = buildingSubscription.getStripeSubscriptionId();
    
    String errorMessage = "Unable to cancel building subscription for building: ["
        + billableBuilding
        + "] with building subscription: ["
        + buildingSubscription
        + "]";
    
    StripeClientResponse stripeClientResponse = stripeClient.deleteStripeSubscription(stripeSubscriptionId);
    if (stripeClientResponse.getResult().equals(StripeClientResponse.RESULT_SUCCESS)) {
    
      // When we cancel the building subscription, we set the pending deletion flag set to true. The
      // pending deletion updated at is set at the same time.  Once the current (i.e. last) payment
      // interval ends, then the building can be hard deleted.  So, in other words, canceling the
      // subscription, is essentially "soft deleting" the building.
      billableBuilding.cancelSubscription();

      LOGGER.info(
          AbstractEntity.getTimeKeeper().getCurrentLocalDate()
          + ": "
          + billableBuilding.getPersistentIdentity() 
          + ": building subscription: "
          + buildingSubscription
          + " has been canceled, so pending deletion FALSE --> TRUE, as of: "
          + billableBuilding.getPendingDeletionUpdatedAt());
      
      PortfolioEntity portfolio = billableBuilding.getRootPortfolioNode();
      updatePortfolio(
          portfolio, 
          BuildingSubscriptionRequest
              .builder()
              .withBuildingId(billableBuilding.getPersistentIdentity())
              .withCustomerId(customerId)
              .withOperationSubType(BuildingSubscriptionRequest.CANCEL_BUILDING_SUBSCRIPTION)
              .build(), 
          false);
    } else {
      
      throw new StripeClientException(
          errorMessage
          + " Reason: "
          + stripeClientResponse.getReason());
    }
  }
  
  @Override
  public BuildingSubscriptionEntity updateBuildingSubscriptionForNewPaymentMethod(
      Integer customerId,
      Integer billableBuildingId,
      Integer paymentMethodId) 
  throws 
      EntityDoesNotExistException,
      StaleDataException,
      StripeClientException {
    
    // Load the portfolio (down to the building level).
    LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
        .builder()
        .withCustomerId(customerId)
        .withFilterNodeType(NodeType.BUILDING)
        .withFilterNodePersistentIdentity(billableBuildingId)
        .withDepthNodeType(NodeType.BUILDING)
        .withLoadDistributorPaymentMethods(Boolean.TRUE)
        .withLoadDistributorUsers(Boolean.TRUE)
        .build();
    
    PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);    
    
    AbstractDistributorEntity distributor = portfolio.getParentCustomer().getParentDistributor();
    if (distributor instanceof OnlineDistributorEntity) {
      
      OnlineDistributorEntity onlineDistributor = (OnlineDistributorEntity)distributor;
      AbstractPaymentMethodEntity paymentMethod = onlineDistributor.getChildPaymentMethod(paymentMethodId);
      
      BuildingEntity building = portfolio.getChildBuilding(billableBuildingId);
      if (building instanceof BillableBuildingEntity) {
        
        BillableBuildingEntity billableBuilding = (BillableBuildingEntity)building;
        
        BuildingSubscriptionEntity buildingSubscription = billableBuilding.getChildBuildingSubscriptionNullIfNotExists();
        if (buildingSubscription != null) {
          
          return updateBuildingSubscriptionForNewPaymentMethod(
              customerId,
              billableBuilding, 
              paymentMethod);
          
        } else {
          throw new IllegalStateException("Building: ["
              + building.getClassAndNaturalIdentity() 
              + "] does not have a subscription associated with it");
        }
      } else {
        throw new IllegalStateException("Building: ["
            + building.getClassAndNaturalIdentity() 
            + "] is not a billable building");
      }      
    } else {
      throw new IllegalStateException("Distributor: ["
          + distributor.getClassAndNaturalIdentity() 
          + "] is not an online distributor");
    }  
  }
  
  private BuildingSubscriptionEntity updateBuildingSubscriptionForNewPaymentMethod(
      Integer customerId,
      BillableBuildingEntity billableBuilding,
      AbstractPaymentMethodEntity paymentMethod) 
  throws 
      EntityDoesNotExistException,
      StaleDataException,
      StripeClientException {

    BuildingSubscriptionEntity buildingSubscription = billableBuilding.getChildBuildingSubscriptionNullIfNotExists();
    if (buildingSubscription == null) {
     
      throw new EntityDoesNotExistException("Building: ["
          + billableBuilding.getNodePath()
          + "] does not have a subscription to update");
    }
    
    if (buildingSubscription.getIsDeleted()) {
      
      throw new IllegalStateException("Building: ["
          + billableBuilding.getNodePath()
          + "] subscription cannot be modified after being canceled");
    }
    
    String stripeSubscriptionId = buildingSubscription.getStripeSubscriptionId();
    String stripeSourceId = paymentMethod.getStripeSourceId();
    
    String errorMessage = "Unable to update building subscription for building: ["
        + billableBuilding
        + "] for new payment method: ["
        + paymentMethod
        + "]";
    
    StripeClientResponse stripeClientResponse = stripeClient.updateStripeSubscriptionForNewPaymentMethod(stripeSubscriptionId, stripeSourceId);
    if (stripeClientResponse.getResult().equals(StripeClientResponse.RESULT_SUCCESS)) {
    
      LOGGER.info(
          AbstractEntity.getTimeKeeper().getCurrentLocalDate()
          + ": "
          + billableBuilding.getPersistentIdentity() 
          + ": building subscription: "
          + buildingSubscription
          + " has been updated for new payment method: "
          + paymentMethod);
      
      buildingSubscription.setParentPaymentMethod(paymentMethod);
      
      PortfolioEntity portfolio = billableBuilding.getRootPortfolioNode();
      updatePortfolio(
          portfolio, 
          BuildingSubscriptionRequest
              .builder()
              .withBuildingId(billableBuilding.getPersistentIdentity())
              .withCustomerId(customerId)
              .withOperationSubType(BuildingSubscriptionRequest.UPDATE_BUILDING_SUBSCRIPTION_FOR_NEW_PAYMENT_METHOD)
              .build(), 
          false);
      
      buildingSubscription.setNotModified();
      
      LOGGER.info(
          AbstractEntity.getTimeKeeper().getCurrentLocalDate()
          + ": "
          + billableBuilding.getPersistentIdentity() 
          + ": changed building subscription to have a new payment method: ["
          + paymentMethod
          + "]");
      
      // If the subscription is delinquent, see if Stripe has handled the payment successfully already.
      if (billableBuilding.getBuildingPaymentStatus().equals(BuildingPaymentStatus.DELINQUENT)) {
        try {

          LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
              .builder()
              .withCustomerId(customerId)
              .withDepthNodeType(NodeType.BUILDING)
              .build();
          
          portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);    
          
          performStripeDelinquentPaymentProcessing(portfolio);
        } catch (Exception e) {
          LOGGER.error("Unable to get latest invoice status after updating payment method, error: "
              + e.getMessage(), e);
        }
      }
    } else {
      
      throw new StripeClientException(
          errorMessage
          + " Reason: "
          + stripeClientResponse.getReason());
    }    
    
    return buildingSubscription;
  }

  @Override
  public BuildingSubscriptionEntity updateBuildingSubscriptionForNewPaymentPlanSameInterval(
      Integer customerId,
      Integer billableBuildingId,
      Integer paymentPlanId) 
  throws 
      EntityDoesNotExistException,
      StaleDataException,
      StripeClientException {

    // Load the portfolio (down to the building level).
    LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
        .builder()
        .withCustomerId(customerId)
        .withFilterNodeType(NodeType.BUILDING)
        .withFilterNodePersistentIdentity(billableBuildingId)
        .withDepthNodeType(NodeType.BUILDING)
        .withLoadDistributorPaymentMethods(Boolean.TRUE)
        .withLoadDistributorUsers(Boolean.TRUE)
        .build();
    
    PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);    
    
    AbstractDistributorEntity distributor = portfolio.getParentCustomer().getParentDistributor();
    if (distributor instanceof OnlineDistributorEntity) {
      
      PaymentPlanEntity paymentPlan = dictionaryRepository.getPaymentPlansContainer().getPaymentPlan(paymentPlanId);
      
      BuildingEntity building = portfolio.getChildBuilding(billableBuildingId);
      if (building instanceof BillableBuildingEntity) {
        
        BillableBuildingEntity billableBuilding = (BillableBuildingEntity)building;
        
        BuildingSubscriptionEntity buildingSubscription = billableBuilding.getChildBuildingSubscriptionNullIfNotExists();
        if (buildingSubscription != null) {
          
          return updateBuildingSubscriptionForNewPaymentPlanSameInterval(
              customerId,
              billableBuilding, 
              paymentPlan);
          
        } else {
          throw new IllegalStateException("Building: ["
              + building.getClassAndNaturalIdentity() 
              + "] does not have a subscription associated with it");
        }
      } else {
        throw new IllegalStateException("Building: ["
            + building.getClassAndNaturalIdentity() 
            + "] is not a billable building");
      }      
    } else {
      throw new IllegalStateException("Distributor: ["
          + distributor.getClassAndNaturalIdentity() 
          + "] is not an online distributor");
    }  
  }
  
  private BuildingSubscriptionEntity updateBuildingSubscriptionForNewPaymentPlanSameInterval(
      Integer customerId,
      BillableBuildingEntity billableBuilding,
      PaymentPlanEntity paymentPlan) 
  throws 
      EntityDoesNotExistException,
      StaleDataException,
      StripeClientException {
    
    BuildingSubscriptionEntity buildingSubscription = billableBuilding.getChildBuildingSubscriptionNullIfNotExists();
    if (buildingSubscription == null) {
     
      throw new EntityDoesNotExistException("Building: ["
          + billableBuilding.getNodePath()
          + "] does not have a subscription to update");
    }
    
    if (buildingSubscription.getIsDeleted()) {
      
      throw new IllegalStateException("Building: ["
          + billableBuilding.getNodePath()
          + "] subscription cannot be modified after being canceled");
    }
    
    PaymentPlanEntity currentPaymentPlan = buildingSubscription.getParentPaymentPlan();
    
    PaymentInterval currentPaymentInterval = currentPaymentPlan.getPaymentInterval();
    PaymentInterval paymentInterval = paymentPlan.getPaymentInterval();
    
    if (!currentPaymentInterval.equals(paymentInterval)) {
      
      throw new IllegalStateException("Cannot set new payment plan with same interval: ["
          + paymentPlan.getDisplayName()
          + "] for building: ["
          + billableBuilding.getNodePath()
          + "] subscription because it does not match the current payment interval: ["
          + currentPaymentInterval
          + "]");
    }
    
    String stripeSubscriptionId = buildingSubscription.getStripeSubscriptionId();
    String stripePlanId = null;
    if (stripeClient.isLiveMode()) {
      stripePlanId = paymentPlan.getStripePlanId();
    } else {
      stripePlanId = paymentPlan.getStripeTestPlanId();
    }
    
    String errorMessage = "Unable to update building subscription for building: ["
        + billableBuilding
        + "] with the same payment interval. BEFORE: ["
        + currentPaymentInterval
        + "] AFTER: ["
        + paymentInterval
        + "] currentIntervalStartedAt: ["
        + buildingSubscription.getCurrentIntervalStartedAt()
        + "] currentIntervalEndsAtLocalDate: ["
        + buildingSubscription.getCurrentIntervalEndsAt()
        + "] for subscription: ["
        + buildingSubscription
        + "].";
    
    StripeClientResponse stripeClientResponse = stripeClient.updateStripeSubscriptionForNewProductSameInterval(
        stripeSubscriptionId,
        stripePlanId);
    if (stripeClientResponse.getResult().equals(StripeClientResponse.RESULT_SUCCESS)) {
    
      buildingSubscription.setParentPaymentPlan(paymentPlan);
      
      PortfolioEntity portfolio = billableBuilding.getRootPortfolioNode();
      updatePortfolio(
          portfolio, 
          BuildingSubscriptionRequest
              .builder()
              .withBuildingId(billableBuilding.getPersistentIdentity())
              .withCustomerId(customerId)
              .withOperationSubType(BuildingSubscriptionRequest.UPDATE_BUILDING_SUBSCRIPTION_FOR_NEW_PAYMENT_PLAN_SAME_INTERVAL)
              .build(), 
          false);
      
      buildingSubscription.setNotModified();
      
      LOGGER.info(
          AbstractEntity.getTimeKeeper().getCurrentLocalDate()
          + ": "
          + billableBuilding.getPersistentIdentity() 
          + ": changed building subscription to have a pending payment plan: ["
          + paymentPlan
          + "] with the same payment interval. BEFORE: ["
          + currentPaymentInterval
          + "] AFTER: ["
          + paymentInterval
          + "] currentIntervalStartedAt: ["
          + buildingSubscription.getCurrentIntervalStartedAt()
          + "] currentIntervalEndsAtLocalDate: ["
          + buildingSubscription.getCurrentIntervalEndsAt()
          + "] for subscription: ["
          + buildingSubscription
          + "].");
      
    } else {
      
      throw new StripeClientException(
          errorMessage
          + " Reason: "
          + stripeClientResponse.getReason());
    }    
    
    return buildingSubscription;
  }
  
  @Override
  public BuildingSubscriptionEntity updateBuildingSubscriptionForNewPaymentPlanDifferentInterval(
      Integer customerId,
      Integer billableBuildingId,
      Integer paymentPlanId) 
  throws 
      EntityDoesNotExistException,
      StaleDataException,
      StripeClientException {

    // Load the portfolio (down to the building level). 
    LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
        .builder()
        .withCustomerId(customerId)
        .withFilterNodeType(NodeType.BUILDING)
        .withFilterNodePersistentIdentity(billableBuildingId)
        .withDepthNodeType(NodeType.BUILDING)
        .withLoadDistributorPaymentMethods(Boolean.TRUE)
        .withLoadDistributorUsers(Boolean.TRUE)
        .build();
    
    PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);    
    
    AbstractDistributorEntity distributor = portfolio.getParentCustomer().getParentDistributor();
    if (distributor instanceof OnlineDistributorEntity) {
      
      PaymentPlanEntity paymentPlan = dictionaryRepository.getPaymentPlansContainer().getPaymentPlan(paymentPlanId);
      
      BuildingEntity building = portfolio.getChildBuilding(billableBuildingId);
      if (building instanceof BillableBuildingEntity) {
        
        BillableBuildingEntity billableBuilding = (BillableBuildingEntity)building;
        
        BuildingSubscriptionEntity buildingSubscription = billableBuilding.getChildBuildingSubscriptionNullIfNotExists();
        if (buildingSubscription != null) {
          
          return updateBuildingSubscriptionForNewPaymentPlanDifferentInterval(
              customerId,
              billableBuilding, 
              paymentPlan);
          
        } else {
          throw new IllegalStateException("Building: ["
              + building.getClassAndNaturalIdentity() 
              + "] does not have a subscription associated with it");
        }
      } else {
        throw new IllegalStateException("Building: ["
            + building.getClassAndNaturalIdentity() 
            + "] is not a billable building");
      }      
    } else {
      throw new IllegalStateException("Distributor: ["
          + distributor.getClassAndNaturalIdentity() 
          + "] is not an online distributor");
    }  
  }
  
  private BuildingSubscriptionEntity updateBuildingSubscriptionForNewPaymentPlanDifferentInterval(
      Integer customerId,
      BillableBuildingEntity billableBuilding,
      PaymentPlanEntity paymentPlan) 
  throws 
      EntityDoesNotExistException,
      StaleDataException,
      StripeClientException {

    BuildingSubscriptionEntity buildingSubscription = billableBuilding.getChildBuildingSubscriptionNullIfNotExists();
    if (buildingSubscription == null) {
     
      throw new EntityDoesNotExistException("Building: ["
          + billableBuilding.getNodePath()
          + "] does not have a subscription to update");
    }
    
    if (buildingSubscription.getIsDeleted()) {
      
      throw new IllegalStateException("Building: ["
          + billableBuilding.getNodePath()
          + "] subscription cannot be modified after being canceled");
    }
    
    PaymentPlanEntity currentPaymentPlan = buildingSubscription.getParentPaymentPlan();
    
    PaymentInterval currentPaymentInterval = currentPaymentPlan.getPaymentInterval();
    PaymentInterval paymentInterval = paymentPlan.getPaymentInterval();
    
    if (currentPaymentInterval.equals(paymentInterval)) {
      
      throw new IllegalStateException("Cannot set new payment plan with different interval: ["
          + paymentPlan.getDisplayName()
          + "] for building: ["
          + billableBuilding.getNodePath()
          + "] subscription because it already matches the current payment interval: ["
          + currentPaymentInterval
          + "]");
    }
    
    String stripeSubscriptionId = buildingSubscription.getStripeSubscriptionId();
    String stripePlanId = null;
    if (stripeClient.isLiveMode()) {
      stripePlanId = paymentPlan.getStripePlanId();
    } else {
      stripePlanId = paymentPlan.getStripeTestPlanId();
    }
    
    String errorMessage = "Unable to update building subscription for building: ["
        + billableBuilding
        + "] with a different payment interval. BEFORE: ["
        + currentPaymentInterval
        + "] AFTER: ["
        + paymentInterval
        + "] currentIntervalStartedAt: ["
        + buildingSubscription.getCurrentIntervalStartedAt()
        + "] currentIntervalEndsAtLocalDate: ["
        + buildingSubscription.getCurrentIntervalEndsAt()
        + "] for subscription: ["
        + buildingSubscription
        + "].";
    
    StripeClientResponse stripeClientResponse = stripeClient.updateStripeSubscriptionForNewProductDifferentInterval(
        stripeSubscriptionId, 
        stripePlanId);
    if (stripeClientResponse.getResult().equals(StripeClientResponse.RESULT_SUCCESS)) {
    
      buildingSubscription.setPendingPaymentPlan(paymentPlan);
      
      PortfolioEntity portfolio = billableBuilding.getRootPortfolioNode();
      updatePortfolio(
          portfolio, 
          BuildingSubscriptionRequest
              .builder()
              .withBuildingId(billableBuilding.getPersistentIdentity())
              .withCustomerId(customerId)
              .withOperationSubType(BuildingSubscriptionRequest.UPDATE_BUILDING_SUBSCRIPTION_FOR_NEW_PAYMENT_PLAN_DIFFERENT_INTERVAL)
              .build(), 
          false);
      
      buildingSubscription.setNotModified();
      
      LOGGER.info(
          AbstractEntity.getTimeKeeper().getCurrentLocalDate()
          + ": "
          + billableBuilding.getPersistentIdentity() 
          + ": changed building subscription to have a pending payment plan: ["
          + paymentPlan
          + "] with a different payment interval. BEFORE: ["
          + currentPaymentInterval
          + "] AFTER: ["
          + paymentInterval
          + "] currentIntervalStartedAt: ["
          + buildingSubscription.getCurrentIntervalStartedAt()
          + "] currentIntervalEndsAtLocalDate: ["
          + buildingSubscription.getCurrentIntervalEndsAt()
          + "] for subscription: ["
          + buildingSubscription
          + "].");
      
    } else {
      
      throw new StripeClientException(
          errorMessage
          + " Reason: "
          + stripeClientResponse.getReason());
    }    
    
    return buildingSubscription;
  }  
  
  @Override
  public void performStripeDelinquentPaymentProcessing(PortfolioEntity portfolio) {
    
    List<BuildingSubscriptionEntity> buildingSubscriptions = portfolio.getAllDescendantBuildingSubscriptions(); 
    for (BuildingSubscriptionEntity buildingSubscription: buildingSubscriptions) {
      
      String stripeSubscriptionId = buildingSubscription.getStripeSubscriptionId();
      StripeClientResponse stripeClientResponse = null;
      
      BuildingPaymentStatus buildingPaymentStatus = buildingSubscription.getParentBuilding().getBuildingPaymentStatus();
      
      // When the payment status is delinquent, we continually poll Stripe (at whatever frequency the system 
      // scheduled job is set at that invokes the distributor state evaluator (which invokes this method) 
      // to see if the payment issue(s) have been fixed for the subscription, as the user adds credit cards 
      // and pays for failed invoices with Stripe directly.
      if (buildingPaymentStatus.equals(BuildingPaymentStatus.DELINQUENT) && !buildingSubscription.isCanceled()) {
        
        try {
          
          stripeClientResponse = stripeClient.getLatestStripeInvoice(stripeSubscriptionId);
          if (stripeClientResponse.getResult().equals(StripeClientResponse.RESULT_SUCCESS)) {
            
            buildingSubscription.getParentBuilding().setBuildingPaymentStatus(BuildingPaymentStatus.UP_TO_DATE);
            
            LOGGER.info("DELINQUENT STRIPE SUBSCRIPTION IS NOW UP-TO-DATE: building subscription: ["
                + buildingSubscription
                + "], current time: ["
                + AbstractEntity.getTimeKeeper().getCurrentTimestamp()
                + "]");
          } else {
            LOGGER.debug("DELINQUENT STRIPE SUBSCRIPTION IS STILL DELINQUENT: building subscription: ["
                + buildingSubscription
                + "], reason: ["
                + stripeClientResponse.getReason()
                + "], current time: ["
                + AbstractEntity.getTimeKeeper().getCurrentTimestamp()
                + "]");
          }
        } catch (StripeClientException stripeClientException) {
          LOGGER.error("COULD NOT GET DELINQUENT STRIPE SUBSCRIPTION LATEST INVOICE: building subscription: ["
              + buildingSubscription
              + "], stripeClientResponse: ["
              + stripeClientResponse
              + "], current time: ["
              + AbstractEntity.getTimeKeeper().getCurrentTimestamp()
              + "]");
        }        
      }
    }
  }
  
  @Override
  public void performStripePaymentProcessing(PortfolioEntity portfolio) {
    
    List<BuildingSubscriptionEntity> buildingSubscriptions = portfolio.getAllDescendantBuildingSubscriptions(); 
    for (BuildingSubscriptionEntity buildingSubscription: buildingSubscriptions) {
      
      String stripeSubscriptionId = buildingSubscription.getStripeSubscriptionId();
      StripeClientResponse stripeClientResponse = null;
      
      boolean hasCurrentPaymentIntervalExpired = buildingSubscription.hasCurrentPaymentIntervalExpired();

      // If the payment interval has ended, then we perform an interval transition and see if the subscription payment plan
      // is to change to one with a different interval (only if not cancelled)
      if (hasCurrentPaymentIntervalExpired && !buildingSubscription.isCanceled()) {
        
        // When we change the resolute subscription to have a pending payment plan (i.e. diff interval),
        // then we need to change the Stripe subscription to NOT AUTOMATICALLY RENEW.  Instead, at the end of
        // the payment interval, which is now, we update the stripe subscription to the new payment plan and turn
        // on subscription renewal (or otherwise change things so that stripe charges for the first payment of the
        // new payment plan. (unlike create subscription, if the first payment was unsuccessful, the subscription
        // undergoes the payment plan state transition, except that the payment status is transitioned to DELINQUENT.
        
        // See if there is a new payment plan with different payment interval to transition to and check payment status of
        PaymentPlanEntity newPaymentPlan = buildingSubscription.transitionToNewPaymentInterval();
        if (newPaymentPlan != null) {
          try {

            String stripePlanId = null;
            if (stripeClient.isLiveMode()) {
              stripePlanId = newPaymentPlan.getStripePlanId();
            } else {
              stripePlanId = newPaymentPlan.getStripeTestPlanId();
            }
            stripeClient.updateStripeSubscriptionForNewProductDifferentInterval(stripeSubscriptionId, stripePlanId);
            
          } catch (StripeClientException stripeClientException) {
            LOGGER.error("COULD NOT CHANGE STRIPE SUBSCRIPTION PAYMENT PLAN WITH DIFFERENT INTERVAL: building subscription: ["
                + buildingSubscription
                + "], stripeClientResponse: ["
                + stripeClientResponse
                + "], current time: ["
                + AbstractEntity.getTimeKeeper().getCurrentTimestamp()
                + "]");
          }
        }
        
        // Regardless of new payment plan with different interval or current payment plan, 
        // we check to see the status of the payment.
        try {

          stripeClientResponse = stripeClient.getLatestStripeInvoice(stripeSubscriptionId);
          if (!stripeClientResponse.getResult().equals(StripeClientResponse.RESULT_SUCCESS)) {
            
            throw new StripeClientException("Unable to get latest stripe invoice for subscription for building: ["
                + buildingSubscription.getParentBuilding().getNodePath()
                + "],  Reason: "
                + stripeClientResponse.getReason());      
          }
          
          StripeInvoice latestStripeInvoice = (StripeInvoice)stripeClientResponse.getResponseObjects().get(StripeClient.INVOICE);
          Boolean isPaid = latestStripeInvoice.getIsPaid();
          if (!isPaid) {
            
            LOGGER.error("STRIPE PAYMENT FAILED: building subscription: ["
                + buildingSubscription.getParentBuilding().getNodePath()
                + "], latestStripeInvoice: ["
                + latestStripeInvoice
                + "], current time: ["
                + AbstractEntity.getTimeKeeper().getCurrentTimestamp()
                + "]");

            buildingSubscription.getParentBuilding().setBuildingPaymentStatus(BuildingPaymentStatus.DELINQUENT);
          }
          
        } catch (StripeClientException stripeClientException) {
          LOGGER.error("COULD NOT GET STRIPE SUBSCRIPTION RENEWAL PAYMENT INFORMATION: building subscription: ["
              + buildingSubscription
              + "], stripeClientResponse: ["
              + stripeClientResponse
              + "], current time: ["
              + AbstractEntity.getTimeKeeper().getCurrentTimestamp()
              + "]");
        }          
      }
    }
  }
  
  @Override 
  public boolean hasDatabaseRepository() {

    if (this.nodeHierarchyRepository instanceof NodeHierarchyRepositoryFileSystemImpl
        || this.nodeHierarchyRepository instanceof NodeHierarchyRepositoryFileSystemCachingImpl) {
      return false;  
    }
    return true;
  }

  @Override
  public void exportPortfolio(Integer customerId) throws EntityDoesNotExistException {
    exportPortfolio(customerId, true, null);
  }

  @Override
  public void exportPortfolio(Integer customerId, boolean exportDictionaryData) throws EntityDoesNotExistException {
    exportPortfolio(customerId, exportDictionaryData, null);
  }

  @Override
  public void exportPortfolio(Integer customerId, boolean exportDictionaryData, String exportPath) throws EntityDoesNotExistException {
    exportPortfolio(customerId, null, exportDictionaryData, null);
  }
  
  @Override
  public void exportPortfolio(Integer customerId, Integer buildingId, boolean exportDictionaryData, String exportPath) throws EntityDoesNotExistException {
    exportPortfolio(customerId, buildingId, exportDictionaryData, null, null, null, exportPath);
  }
  
  @Override
  public void exportPortfolio(
      Integer customerId,
      Integer buildingId,
      boolean exportDictionaryData,
      Integer exportCustomAsyncComputedPointId,
      Timestamp exportTimeSeriesDataStartTimestamp,
      Timestamp exportTimeSeriesDataEndTimestamp, 
      String exportPath) throws EntityDoesNotExistException {
    
    LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
        .builder()
        .withCustomerId(customerId)
        .withLoadBuildingTemporalData(Boolean.TRUE)
        .withLoadCustomPointTemporalData(Boolean.TRUE)
        .withLoadDistributorPaymentMethods(Boolean.TRUE)
        .withLoadDistributorUsers(Boolean.TRUE)
        .withLoadAdFunctionInstances(Boolean.TRUE)
        .withLoadReportInstances(Boolean.TRUE)
        .build();
    
    if (buildingId != null && buildingId.intValue() > 0) {

      loadPortfolioOptions = LoadPortfolioOptions
          .builder(loadPortfolioOptions)
          .withFilterNodePersistentIdentity(buildingId)
          .withFilterNodeType(NodeType.BUILDING)
          .build();
    }
    
    PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);
    
    AbstractCustomerEntity customer = portfolio.getParentCustomer();
    
    DictionaryRepositoryFileSystemImpl ddfs = new DictionaryRepositoryFileSystemImpl(exportPath);
    NodeHierarchyRepositoryFileSystemImpl nhfs = new NodeHierarchyRepositoryFileSystemImpl(
        exportPath,
        rawPointRepository,
        customerRepository,
        ddfs);
    
    boolean resetAllIsModified = true;
    boolean resetCreatedLists = true;
    nhfs.savePortfolioToFileSystem(
        customerId,
        resetAllIsModified,
        resetCreatedLists,
        portfolio);
    
    if (exportDictionaryData) {
      ddfs.saveDictionaryDataToFilesystem();
    }

    try {
      UserRepositoryFileSystemImpl.setPrettyPrint(true);
      UserRepositoryFileSystemImpl ufs = new UserRepositoryFileSystemImpl(
          exportPath, 
          customerRepository);
      
      DistributorRepositoryFileSystemImpl dfs = new DistributorRepositoryFileSystemImpl(
          exportPath,
          ufs);

      boolean loadDistributorPaymentMethods = true;
      boolean loadDistributorUsers = true;
      Collection<AbstractDistributorEntity> distributors = distributorRepository.loadAllDistributors(
          loadDistributorPaymentMethods,
          loadDistributorUsers).values();
      
      Set<AbstractDistributorEntity> ancestorDistributors = customer.getAncestorDistributors();
      for (AbstractDistributorEntity distributor: distributors) {
        if (ancestorDistributors.contains(distributor)) {
          ufs.storeDistributorUsers(distributor);  
        }
      }
            
      dfs.storeDistributors(ancestorDistributors);
      
      CustomerRepositoryFileSystemImpl cfs = new CustomerRepositoryFileSystemImpl(
          exportPath,
          rawPointRepository,
          distributorRepository);
      
      ufs.storeCustomerUsers(customer);

      Set<AbstractCustomerEntity> customers = new HashSet<>();
      customers.add(customer);
      cfs.storeCustomers(customers);
      
      // Export custom async computed point (and variables) time-series data (if specified)
      if (exportCustomAsyncComputedPointId != null && exportTimeSeriesDataStartTimestamp != null && exportTimeSeriesDataEndTimestamp != null) {
        
        exportCustomAsyncPoint(
            portfolio, 
            portfolio.getCustomAsyncComputedPoint(exportCustomAsyncComputedPointId), 
            exportTimeSeriesDataStartTimestamp, 
            exportTimeSeriesDataEndTimestamp);
      }
    } catch (Exception e) {
      throw new RuntimeException("Unable to export customer/distributor/user data: " + e.getMessage(), e);
    }
  }  
  
  private void exportCustomAsyncPoint(
      PortfolioEntity portfolio,
      CustomAsyncComputedPointEntity customAsyncComputedPoint,
      Timestamp startTimestamp,
      Timestamp endTimestamp)
  throws 
      EntityDoesNotExistException,
      TimeSeriesClientException {

    // USED FOR SAVING TIME SERIES DATA TO THE FILE SYSTEM.
    MockTimeSeriesServiceClient mockTimeSeriesServiceClient = MockTimeSeriesServiceClient.getInstance();
    Map<String, String> tags = new HashMap<>();
    tags.put(TimeSeriesServiceClient.TAG, TimeSeriesServiceClient.DUMMY);

    
    // ADJUST THE CURRENT TIME AND SETUP TIME-SERIES CLIENT VARS. ACCORDING TO THE COMPUTATION INTERVAL.
    ComputationInterval computationInterval = customAsyncComputedPoint.getComputationInterval();
    String metricQueryPrefix = null;    
    if (computationInterval.equals(ComputationInterval.QUARTER_HOUR)) {
      metricQueryPrefix = TimeSeriesServiceClient.METRIC_QUERY_AGGREGATOR_SUM + TimeSeriesServiceClient.METRIC_QUERY_DOWNSAMPLER_FIFTEEN_MIN_INTERVAL;
    } else if (computationInterval.equals(ComputationInterval.DAILY)) {
      metricQueryPrefix = TimeSeriesServiceClient.METRIC_QUERY_AGGREGATOR_SUM + TimeSeriesServiceClient.METRIC_QUERY_DOWNSAMPLER_ONE_DAY_INTERVAL;
    } else {
      metricQueryPrefix = TimeSeriesServiceClient.METRIC_QUERY_AGGREGATOR_SUM + TimeSeriesServiceClient.METRIC_QUERY_DOWNSAMPLER_ONE_MONTH_INTERVAL;
    }
    
    // ITERATION IS DONE VIA EPOCH MILLIS.
    long startMillis = startTimestamp.getTime();
    long endMillis = endTimestamp.getTime();
    
    
    // LOOP THROUGH EACH TEMPORAL CONFIG.
    List<TemporalAsyncComputedPointConfigEntity> childTemporalConfigs = new ArrayList<>();
    childTemporalConfigs.addAll(customAsyncComputedPoint.getChildTemporalConfigs());
    for (int i=0; i < childTemporalConfigs.size(); i++) {
      
      TemporalAsyncComputedPointConfigEntity childTemporalConfig = childTemporalConfigs.get(i);

      
      // DETERMINE IF THE START/END MILLIS NEED TO BE CLIPPED BASED ON EFFECTIVE DATE.
      LocalDateTime childTemporalConfigEffectiveLocalDateTime = childTemporalConfig.getEffectiveDate().atStartOfDay();
      ZoneOffset zo = AbstractEntity.UTC_ZONE_ID.getRules().getOffset(childTemporalConfigEffectiveLocalDateTime);        
      long childTemporalConfigEffectiveEpochMillis = childTemporalConfigEffectiveLocalDateTime.toInstant(zo).toEpochMilli();
      
      
      // START
      if (childTemporalConfigEffectiveEpochMillis >= startMillis) {
        startMillis = childTemporalConfigEffectiveEpochMillis;
      }
      
      
      // END
      if (i == childTemporalConfigs.size()-1) {
        
        endMillis = endTimestamp.getTime();

      } else if (childTemporalConfigEffectiveEpochMillis >= startTimestamp.getTime()) {
        
        TemporalAsyncComputedPointConfigEntity nextChildTemporalConfig = childTemporalConfigs.get(i + 1); 
        LocalDateTime nextChildTemporalConfigEffectiveLocalDateTime = nextChildTemporalConfig.getEffectiveDate().atStartOfDay();
        long nextChildTemporalConfigEffectiveEpochMillis = nextChildTemporalConfigEffectiveLocalDateTime.toInstant(zo).toEpochMilli();
        endMillis = nextChildTemporalConfigEffectiveEpochMillis;
        
      }
      
      
      // GATHER THE FORMULA POINT VARIABLES FOR THE GIVEN TEMPORAL CONFIG.
      Set<String> metricIds = new TreeSet<>();
      metricIds.add(metricQueryPrefix + customAsyncComputedPoint.getMetricIdForTsdb());
      for (FormulaVariableEntity formulaVariable: childTemporalConfig.getChildVariables()) {
        metricIds.add(metricQueryPrefix + formulaVariable.getParentPoint().getMetricIdForTsdb());
      }
      
      LocalDateTime startLocalDateTime = Instant
          .ofEpochMilli(startMillis)
          .atZone(TimeZone
              .getTimeZone(CustomAsyncComputedPointEntity.ETC_UTC_TIMEZONE)
              .toZoneId())
          .toLocalDateTime();            

      LocalDateTime endLocalDateTime = Instant
          .ofEpochMilli(endMillis)
          .atZone(TimeZone
              .getTimeZone(CustomAsyncComputedPointEntity.ETC_UTC_TIMEZONE)
              .toZoneId())
          .toLocalDateTime();

      
      // PROCESS TIME SERIES ACCORDING TO THE COMPUTATION INTERVAL.
      LocalDateTime fromLocalDateTime = startLocalDateTime;
      Long fromMillis = fromLocalDateTime.toEpochSecond(ZoneOffset.UTC) * 1000;
      
      LocalDateTime toLocalDateTime = null;
      if (computationInterval.equals(ComputationInterval.QUARTER_HOUR)) {

        toLocalDateTime = fromLocalDateTime.plusDays(TimeSeriesServiceClient.FIFTEEN_MINUTE_INTERVAL_NUM_DAYS_TO_PROCESS_AT_A_TIME);
        
      } else if (computationInterval.equals(ComputationInterval.DAILY)) {
        
        toLocalDateTime = fromLocalDateTime.plusDays(TimeSeriesServiceClient.DAILY_INTERVAL_NUM_DAYS_TO_PROCESS_AT_A_TIME);
        
      } else {
        
        toLocalDateTime = fromLocalDateTime.plusMonths(TimeSeriesServiceClient.MONTHLY_INTERVAL_NUM_MONTHS_TO_PROCESS_AT_A_TIME);
        
      }
      if (toLocalDateTime.isAfter(endLocalDateTime)) {
        toLocalDateTime = endLocalDateTime;
      }
      Long toMillis = toLocalDateTime.toEpochSecond(ZoneOffset.UTC) * 1000;

      
      // PROCESS THE GIVEN INTERVAL.
      while (fromLocalDateTime.isBefore(toLocalDateTime)) {
        
        // RETRIEVE ALL RELEVANT TIME SERIES DATA FOR THE GIVEN INTERVAL.
        Map<String, Map<Long, Double>> allMetricValues = timeSeriesServiceClient.retrieveMetricValuesFromTsdb(
            portfolio.getParentCustomer(),
            metricIds,
            Long.valueOf(fromMillis),
            Long.valueOf(toMillis));

        
        // EXPORT TO THE FILESYSTEM
        mockTimeSeriesServiceClient.submitMetricValuesBatchToTsdb(
            portfolio.getParentCustomer().getPersistentIdentity(), 
            tags, 
            allMetricValues);
        

        // INCREMENT THE TIME INTERVAL TO PROCESS (IF THE END OF THE LAST INTERVAL IS BEFORE THE END TIMESTAMP).
        fromLocalDateTime = toLocalDateTime;
        fromMillis = toMillis;
        if (toLocalDateTime.isBefore(endLocalDateTime)) {
          if (computationInterval.equals(ComputationInterval.QUARTER_HOUR)) {
            toLocalDateTime = fromLocalDateTime.plusDays(TimeSeriesServiceClient.FIFTEEN_MINUTE_INTERVAL_NUM_DAYS_TO_PROCESS_AT_A_TIME);
          } else if (computationInterval.equals(ComputationInterval.DAILY)) {
            toLocalDateTime = fromLocalDateTime.plusDays(TimeSeriesServiceClient.DAILY_INTERVAL_NUM_DAYS_TO_PROCESS_AT_A_TIME);
          } else {
            toLocalDateTime = fromLocalDateTime.plusMonths(TimeSeriesServiceClient.MONTHLY_INTERVAL_NUM_MONTHS_TO_PROCESS_AT_A_TIME);
          }
          if (toLocalDateTime.isAfter(endLocalDateTime)) {
            toLocalDateTime = endLocalDateTime;
          }
          toMillis = toLocalDateTime.toEpochSecond(ZoneOffset.UTC) * 1000;
        }
      }
    }
  }
  
  @Override
  public List<String> performPortfolioMaintenance(
      List<Integer> customerIds,
      DistributorHierarchyStateEvaluator distributorHierarchyStateEvaluator,
      boolean performStripePaymentProcessing) {

    List<String> errors = new ArrayList<>();
    long portfolioMaintenanceStart = System.currentTimeMillis();
    LOGGER.info("BEGIN: PORTFOLIO MAINTENANCE");
    
    List<AbstractDistributorEntity> distributors = new ArrayList<>();
    for (Integer customerId: customerIds) {
      
      if (customerId != null && customerId.intValue() != -999) {

        try {

          List<Integer> buildingIds = nodeHierarchyRepository.getBuildingIds(customerId);
          List<List<Integer>> buildingIdSubLists = Lists.partition(buildingIds, 15);
          for (List<Integer> buildingIdSubList: buildingIdSubLists) {
 
            // STEP 1: Load the portfolio.
            long loadStart = System.currentTimeMillis();
            
            LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
                .builder()
                .withCustomerId(customerId)
                .withFilterNodeType(NodeType.BUILDING)
                .withFilterNodePersistentIdentities(buildingIdSubList)
                .withLoadAdFunctionInstances(Boolean.TRUE)
                .withLoadReportInstances(Boolean.TRUE)
                .withLoadBuildingTemporalData(Boolean.TRUE)
                .withLoadCustomPointTemporalData(Boolean.TRUE)
                .withLoadDistributorPaymentMethods(Boolean.TRUE)
                .withLoadDistributorUsers(Boolean.TRUE)
                .build();
            
            PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);      
            distributors.add(portfolio.getParentCustomer().getParentDistributor());
            
            LOGGER.debug("LOAD PORTFOLIO: {}: ELAPSED(ms): {}",
                portfolio.getName(),
                (System.currentTimeMillis()-loadStart));
            
            // STEP 2: See if there are any issues to resolve.
            try {
              
              long remediateStart = System.currentTimeMillis();
              LOGGER.debug("BEGIN: REMEDIATE PORTFOLIO: {}", portfolio.getName());

              Set<IssueType> allIssueTypes = new HashSet<>();
              allIssueTypes.addAll(ValidationMessage.extractPhaseOneIssueTypes());
              allIssueTypes.addAll(ValidationMessage.extractPhaseTwoIssueTypes());

              // Phase One: Points (Point Template Associations and Haystack Tags)
              LOGGER.debug("REMEDIATE PHASE ONE: {}", portfolio);
              Set<IssueType> phaseOneIssueTypes = ValidationMessage.extractPhaseOneIssueTypes(allIssueTypes);
              List<ValidationMessage> phaseOneValidationMessages = portfolio.remediate(phaseOneIssueTypes);
              
              // Phase Two: Rule Candidates and Rule Instances
              LOGGER.debug("REMEDIATE PHASE TWO: {}", portfolio);
              Set<IssueType> phaseTwoIssueTypes = ValidationMessage.extractPhaseTwoIssueTypes(allIssueTypes);
              List<ValidationMessage> phaseTwoValidationMessages = portfolio.remediate(phaseTwoIssueTypes);
              
              // We may need to perform a second pass
              int numIssuesResolved = phaseOneValidationMessages.size() + phaseTwoValidationMessages.size();
              if (!phaseTwoValidationMessages.isEmpty()) {
                
                phaseOneValidationMessages = portfolio.remediate(phaseOneIssueTypes);
                phaseTwoValidationMessages = portfolio.remediate(phaseTwoIssueTypes);
                numIssuesResolved = numIssuesResolved + phaseOneValidationMessages.size() + phaseTwoValidationMessages.size();
              }
              LOGGER.debug("NUM ISSUES RESOLVED FOR {}: {}", portfolio, numIssuesResolved);
              
              LOGGER.debug("END: REMEDIATE PORTFOLIO: {}: ISSUE COUNT: {}, ELAPSED(ms): {}",
                  portfolio.getName(),
                  numIssuesResolved, 
                  (System.currentTimeMillis()-remediateStart));
              
            } catch (Exception e) {
              String errorMessagePrefix = "Unable to remediate portfolio for customer: "
                  + customerId
                  + ", error: "
                  + e.getMessage();
              String error = ExceptionUtils.extractReason(errorMessagePrefix, e);
              LOGGER.error(error, e);
              errors.add(error);
            }


            // STEP 3: See if there are any computed point function candidates to create.
            try {
              
              long findAdComputedPointFunctionCandidatesStart = System.currentTimeMillis();
              LOGGER.debug("BEGIN: FIND COMPUTED POINT FUNCTION CANDIDATES: {}", portfolio.getName());
              
              List<AdFunctionInstanceDto> dtoList = PortfolioVisitor.findAdFunctionInstanceCandidates(
                  portfolio,
                  FunctionType.COMPUTED_POINT);
              
              LOGGER.debug("END: FIND COMPUTED POINT FUNCTION CANDIDATES: {}, NEW COUNT: {}, ELAPSED(ms): {}",
                  portfolio.getName(),
                  dtoList.size(), 
                  (System.currentTimeMillis()-findAdComputedPointFunctionCandidatesStart));          
              
            } catch (Exception e) {
              String errorMessagePrefix = "Unable to find ad computed point function candidates for customer: "
                  + customerId
                  + ", error: "
                  + e.getMessage();
              String error = ExceptionUtils.extractReason(errorMessagePrefix, e);
              LOGGER.error(error, e);
              errors.add(error);
            }        


            // STEP 4: See if there are any rule function candidates to create.
            try {
              
              long findAdRuleFunctionCandidatesStart = System.currentTimeMillis();
              LOGGER.debug("BEGIN: FIND RULE FUNCTION CANDIDATES: {}", portfolio.getName());
              
              List<AdFunctionInstanceDto> dtoList = PortfolioVisitor.findAdFunctionInstanceCandidates(
                  portfolio,
                  FunctionType.RULE);
              
              LOGGER.debug("END: FIND RULE FUNCTION CANDIDATES: {}, NEW COUNT: {}, ELAPSED(ms): {}",
                  portfolio.getName(),
                  dtoList.size(), 
                  (System.currentTimeMillis()-findAdRuleFunctionCandidatesStart));
              
            } catch (Exception e) {
              String errorMessagePrefix = "Unable to find ad rule function candidates for customer: "
                  + customerId
                  + ", error: "
                  + e.getMessage();
              String error = ExceptionUtils.extractReason(errorMessagePrefix, e);
              LOGGER.error(error, e);
              errors.add(error);
            }


            // STEP 5: RP-10256: Automatically enable any AD rule/computed point function and report instances. 
            try {

              long autoEnableStart = System.currentTimeMillis();
              
              if (performAutomaticConfigurationForPortfolioMaintenance) {
                LOGGER.debug("BEGIN: AUTO ENABLE: {}", portfolio.getName());  
              } else {
                LOGGER.debug("BEGIN: EVALUATE REPORTS: {}", portfolio.getName());
              }
              
              // Enable any AD rule/computed function instances that are available to be enabled.
              int numEnabledAdFunctionInstances = 0;
              if (performAutomaticConfigurationForPortfolioMaintenance && portfolio.getParentCustomer().allowAutomaticConfiguration()) {

                List<AbstractAdFunctionInstanceEntity> candidates = portfolio.getAllAdFunctionInstanceCandidates();
                if (!candidates.isEmpty()) {

                  createAdFunctionInstancesNoStore(
                      portfolio,
                      portfolio.getAllAdFunctionInstanceCandidates(),
                      Boolean.FALSE);
                  
                  numEnabledAdFunctionInstances = candidates.size();
                }
              }

              
              // Regardless of any AD rule/computed point function instances that were enabled, we evaluate the reports.
              int numEnabledReportInstances = 0;
              List<ReportInstanceEntity> modifiedReportInstances = PortfolioVisitor.evaluateReports(portfolio);
              
              
              // Enable any report instances that are available to be enabled.
              if (performAutomaticConfigurationForPortfolioMaintenance && !modifiedReportInstances.isEmpty() && portfolio.getParentCustomer().allowAutomaticConfiguration()) {

                updateReportInstancesNoStore(
                    portfolio,
                    new ArrayList<>());
                
                for (ReportInstanceEntity reportInstance: modifiedReportInstances) {
                  
                  if (reportInstance.isEnabled()) {
                   
                    numEnabledReportInstances++;
                  }
                }
              }
              
              if (performAutomaticConfigurationForPortfolioMaintenance) {
                LOGGER.debug("END: AUTO ENABLE: {}, AD FUNCTION CANDIDATES: {}, REPORTS: {}, ELAPSED(ms): {}",
                    portfolio.getName(),
                    numEnabledAdFunctionInstances,
                    numEnabledReportInstances,
                    (System.currentTimeMillis()-autoEnableStart));
              } else {
                LOGGER.debug("END: EVALUATE REPORTS: {}, REPORTS: {}, ELAPSED(ms): {}",
                    portfolio.getName(),
                    modifiedReportInstances.size(),
                    (System.currentTimeMillis()-autoEnableStart));
              }

            } catch (Exception e) {
              String errorMessagePrefix = "Unable to auto enable ad rule/computed points/reports or evaluate reports only for customer: "
                  + customerId
                  + ", performAutomaticConfiguration: "
                  + performAutomaticConfigurationForPortfolioMaintenance
                  + ", error: "
                  + e.getMessage();
              String error = ExceptionUtils.extractReason(errorMessagePrefix, e);
              LOGGER.error(error, e);
              errors.add(error);
            }        


            // STEP 6: Now that all the operations have been performed, we store any changes that were made.
            try {
              
              boolean reportsWereEvaluated = true;
              updatePortfolio(
                  portfolio,
                  PerformPortfolioMaintenanceRequest
                  .builder()
                  .withCustomerId(portfolio.getCustomerId())
                  .withSubmittedBy(NodeHierarchyCommandRequest.SYSTEM)
                  .build(),
                  reportsWereEvaluated);
              
            } catch (Exception e) {
              String errorMessagePrefix = "Unable to store portfolio changes for customer: "
                  + customerId
                  + ", error: "
                  + e.getMessage();
              String error = ExceptionUtils.extractReason(errorMessagePrefix, e);
              LOGGER.error(error, e);
              errors.add(error);
            }
          }
          
          // STEP 7: We invoke the distributor state evaluator with every invocation, as there may be delinquent
          // subscriptions that we want to check to see if payment has been made.  Regardless, the actual stripe
          // processing for active subscriptions only occurs ONCE per day.
          // See if we need to run nightly payment processing and distributor/customer/portfolio state evaluation.
          boolean performedPaymentProcessing = false;
          try {

            long paymentProcessingStart = System.currentTimeMillis();
            
            // Reload the portfolio, but only down to the building level, as that is all we need for payment processing.
            LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
                .builder()
                .withCustomerId(customerId)
                .withDepthNodeType(NodeType.BUILDING)
                .withLoadDistributorPaymentMethods(Boolean.TRUE)
                .withLoadDistributorUsers(Boolean.TRUE)
                .build();
            
            PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);

            LOGGER.debug("BEGIN: PERFORM DISTRIBUTOR STATE EVALUATION FOR PORTFOLIO: {}", portfolio.getName());

            performedPaymentProcessing = distributorHierarchyStateEvaluator.evaluatePortfolioState(portfolio);

            LOGGER.debug("END: PERFORM NIGHTLY PAYMENT PROCESSING: {}, PROCESSED PAYMENTS: {}, ELAPSED(ms): {}",
                portfolio.getName(),
                performedPaymentProcessing, 
                (System.currentTimeMillis()-paymentProcessingStart));       
            
          } catch (Exception e) {
            String errorMessagePrefix = "Unable to perform portfolio config/pending deletion/nightly payment processing: for customer: "
                + customerId
                + ", performedPaymentProcessing: "
                + performedPaymentProcessing
                + ", error: "
                + e.getMessage();
            String error = ExceptionUtils.extractReason(errorMessagePrefix, e);
            LOGGER.error(error, e);
            errors.add(error);
          }

        } catch (Exception e) {
          String errorMessagePrefix = "Unable to perform portfolio maintenance for customer: "
              + customerId
              + ", error: "
              + e.getMessage();
          String error = ExceptionUtils.extractReason(errorMessagePrefix, e);
          LOGGER.error(error, e);
          errors.add(error);
        }       
      }
    }
    
    
    // PERFORM STRIPE PAYMENT PROCESSING (IF ENABLED)
    try {

      // NOTE: If in test mode, then we do not do anything, as it is handled by manual endpoints in config service.
      if (performStripePaymentProcessing) {
        
        // Evaluate the root Resolute distributor, which will allow us to deal with any child
        // distributors that do not have descendant portfolios yet.
        distributorHierarchyStateEvaluator.evaluateRootDistributorState(distributors);
      }
      
      LOGGER.info("END: PORTFOLIO MAINTENANCE: ELAPSED(ms): {}", 
          (System.currentTimeMillis()-portfolioMaintenanceStart));
      
    } catch (Exception e) {
      String errorMessagePrefix = "Unable to perform stripe payment processing for root distributor, error: "
          + e.getMessage();
      String error = ExceptionUtils.extractReason(errorMessagePrefix, e);
      LOGGER.error(error, e);
      errors.add(error);
    }
    
    
    // LASTLY, PERFORM NOTIFICATION EVENT EVALUATION AND USER NOTIFICATION CREATION FOR ANY UNPUBLISHED EVENTS
    // THAT WERE SYSTEM GENERATED (AS A RESULT OF SOME EVENT, SUCH AS POINT CAP EXCEEDED OR TRIAL EXPIRED)
    try {
      notificationService.evaluateNotificationEvents();
    } catch (Exception e) {
      String errorMessagePrefix = "Unable to perform user notification processing, error: " + e.getMessage();
      String error = ExceptionUtils.extractReason(errorMessagePrefix, e);
      LOGGER.error(error, e);
      errors.add(error);
    }
    
    return errors;
  } 
  
  @Override
  public Integer getBuildingIdForDescendantId(
      Integer customerId, 
      Integer descendantId)
  throws 
      EntityDoesNotExistException {
    
    return nodeHierarchyRepository.getBuildingIdForDescendantId(
        customerId, 
        descendantId);
  }
  
  @Override
  public List<Integer> getBuildingIds(Integer customerId) {
    
    return nodeHierarchyRepository.getBuildingIds(customerId);
  }
  
  @Override
  public Set<Integer> getBuildingIdsForDescendantIds(
      Integer customerId, 
      Collection<Integer> descendantIds)
  throws 
      EntityDoesNotExistException {
    
    if (descendantIds == null || descendantIds.isEmpty()) {
      throw new IllegalArgumentException("List of descendant node ids to get ancestor building ids for cannot be empty.");
    }
    
    return nodeHierarchyRepository.getBuildingIdsForDescendantIds(
        customerId, 
        descendantIds);
  }
  
  @Override
  public Set<Integer> getBuildingIdsForAdFunctionInstanceIds(
      Integer customerId,
      Collection<Integer> instanceIds)
  throws 
      EntityDoesNotExistException {
    
    return nodeHierarchyRepository.getBuildingIdsForAdFunctionInstanceIds(
        customerId, 
        instanceIds); 
  }

  @Override
  public Set<Integer> getBuildingIdsForRawPointIds(
      Integer customerId,
      Collection<Integer> rawPointIds)
  throws 
      EntityDoesNotExistException {
    
    return nodeHierarchyRepository.getBuildingIdsForRawPointIds(
        customerId, 
        rawPointIds); 
  }
  
  @Override
  public Map<Integer, Integer> getPaymentMethodRefCounts(
      int distributorId)
  throws 
      EntityDoesNotExistException {
    
    if (hasDatabaseRepository()) {
      
      return distributorRepository.getPaymentMethodRefCounts(distributorId);
    }
    
    Map<Integer, Integer> paymentMethodRefCounts = new HashMap<>();
    
    AbstractDistributorEntity distributor = distributorRepository.loadDistributor(
        distributorId,
        true, // loadDistributorPaymentMethods
        false); //loadDistributorUsers
        
    if (distributor instanceof OnlineDistributorEntity) {
      
      OnlineDistributorEntity od = (OnlineDistributorEntity)distributor;
      for (AbstractPaymentMethodEntity pm: od.getChildPaymentMethods()) {
        
        paymentMethodRefCounts.put(pm.getPersistentIdentity(), Integer.valueOf(0));
      }
      
      List<AbstractCustomerEntity> childCustomers = customerRepository.loadChildCustomers(od);
      for (AbstractCustomerEntity customer: childCustomers) {
        
        if (customer instanceof OnlineCustomerEntity) {
          
          LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
              .builder()
              .withCustomerId(customer.getPersistentIdentity())
              .withDepthNodeType(NodeType.BUILDING)
              .build();
          
          PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);      
          
          for (BuildingEntity building: portfolio.getChildBuildings()) {
            
            if (building instanceof BillableBuildingEntity) {
              
              BillableBuildingEntity bb = (BillableBuildingEntity)building;
              BuildingSubscriptionEntity bs = bb.getChildBuildingSubscriptionNullIfNotExists();
              if (bs != null) {
                
                AbstractPaymentMethodEntity pm = bs.getParentPaymentMethod();
                Integer refCount = paymentMethodRefCounts.get(pm.getPersistentIdentity());
                refCount = Integer.valueOf(refCount.intValue() + 1);
                paymentMethodRefCounts.put(pm.getPersistentIdentity(), refCount);
              }
            }
          }
        }
      }
    }
    
    return paymentMethodRefCounts;
  }

  @Override
  public Double getAdFunctionConfigurationStatusPercent(
      Integer customerId,
      String functionType)
  throws
      EntityDoesNotExistException {
    
    return nodeHierarchyRepository.getAdFunctionConfigurationStatusPercent(
        customerId, 
        FunctionType.get(functionType));
  }
  
  @Override
  public Double getEnabledAdFunctionInstancesPercent(
      Integer customerId,
      String functionType)
  throws
      EntityDoesNotExistException {
    
    return nodeHierarchyRepository.getEnabledAdFunctionInstancesPercent(
        customerId, 
        FunctionType.get(functionType));
  }
  
  @Override
  public AdFunctionErrorMessageSearchResponse getAdFunctionErrorMessages(
      Integer customerId, 
      AdFunctionErrorMessageSearchCriteria searchCriteria) {
    
    int count = nodeHierarchyRepository.getAdFunctionErrorMessagesCount(customerId, searchCriteria);
    List<AdFunctionErrorMessagesValueObject> data = null;
    if (count > 0) {
      data = nodeHierarchyRepository.getAdFunctionErrorMessagesData(customerId, searchCriteria);
    } else {
      data = new ArrayList<>();
    }

    return AdFunctionErrorMessageSearchResponse
        .builder()
        .withCriteria(searchCriteria)
        .withCount(count)
        .withData(data)
        .build();
  }

  @Override
  public void duplicatePortfolio(
      Integer customerId,
      Integer startingIndex,
      Integer duplicationFactor)
  throws
      EntityDoesNotExistException,
      StaleDataException {
    
    PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(LoadPortfolioOptions
        .builder()
        .withCustomerId(customerId)
        .withDepthNodeType(NodeType.BUILDING)
        .build());
    
    List<Integer> buildingIdList = new ArrayList<>();
    for (BuildingEntity building: portfolio.getChildBuildings()) {
      buildingIdList.add(building.getPersistentIdentity());
    }
    
    for (Integer buildingId: buildingIdList) {
      try {
        duplicateBuilding(customerId, buildingId, startingIndex, duplicationFactor);  
      } catch (Exception e) {
        LOGGER.error("Unable to duplicate portfolio: "
            + portfolio 
            + ", building: " 
            + buildingId, e);
      }
    }
  }
  
  @Override
  public void duplicateBuilding(
      Integer customerId,
      Integer sourceBuildingId,
      Integer startingIndex,
      Integer duplicationFactor)
  throws
      EntityDoesNotExistException,
      StaleDataException {
    
    // If startingIndex isn't specified, then default to 1.
    if (startingIndex == null) {
      startingIndex = Integer.valueOf(1);
    }

    // If duplicationFactor isn't specified, then default to 1.
    if (duplicationFactor == null) {
      duplicationFactor = Integer.valueOf(1);
    }
    
    // Make N copies of the source building (where N=duplicationFactor).
    for (int duplicationIndex=startingIndex; duplicationIndex < (duplicationFactor+startingIndex); duplicationIndex++) {

      // Load the portfolio (down to the point level, as we will just re-evaluate for the duplicated building).
      PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(LoadPortfolioOptions
          .builder()
          .withCustomerId(customerId)
          .withFilterNodeType(NodeType.BUILDING)
          .withFilterNodePersistentIdentity(sourceBuildingId)
          .withDepthNodeType(NodeType.POINT)
          .build());


      // Duplicate the source building.
      BuildingEntity sourceBuilding = portfolio.getChildBuilding(sourceBuildingId);
      BuildingEntity duplicatedBuilding = (BuildingEntity)sourceBuilding.duplicateNode(portfolio, portfolio, duplicationIndex);
      String duplicatedBuildingName = duplicatedBuilding.getName();


      // Store all the changes to the repository.
      boolean reportsWereEvaluated = false;
      try {

        updatePortfolio(
            portfolio,
            PerformPortfolioMaintenanceRequest
            .builder()
            .withCustomerId(portfolio.getCustomerId())
            .withSubmittedBy(NodeHierarchyCommandRequest.SYSTEM)
            .build(),
            reportsWereEvaluated);
        
        
        // Load the portfolio to the building level, so that we can get the duplicated building id.
        portfolio = nodeHierarchyRepository.loadPortfolio(LoadPortfolioOptions
            .builder()
            .withCustomerId(customerId)
            .withDepthNodeType(NodeType.BUILDING)
            .build());
        duplicatedBuilding = portfolio.getChildBuildingByName(duplicatedBuildingName);
        Integer duplicatedBuildingId = duplicatedBuilding.getPersistentIdentity();
        
        
        // Load the portfolio, with the source and duplicated buildings.
        portfolio = nodeHierarchyRepository.loadPortfolio(LoadPortfolioOptions
            .builder()
            .withCustomerId(customerId)
            .withFilterNodeType(NodeType.BUILDING)
            .withFilterNodePersistentIdentities(Arrays.asList(sourceBuildingId, duplicatedBuildingId))
            .withDepthNodeType(NodeType.POINT)
            .build());
        
        
        // Duplicate the energy exchange system relationships.
        sourceBuilding = portfolio.getChildBuilding(sourceBuildingId);
        duplicatedBuilding = portfolio.getChildBuildingByName(duplicatedBuildingName);
        sourceBuilding.duplicateEnergyExhangeSystemNodes(portfolio, duplicatedBuilding, duplicationIndex);
        
        
        // Store all the changes to the repository.
        updatePortfolio(
            portfolio,
            PerformPortfolioMaintenanceRequest
            .builder()
            .withCustomerId(portfolio.getCustomerId())
            .withSubmittedBy(NodeHierarchyCommandRequest.SYSTEM)
            .build(),
            reportsWereEvaluated);

        
        // Create AD function/report instances, regardless of what was in source building.
        findAdFunctionInstanceCandidates(FindAdFunctionInstanceCandidatesRequest
            .builder()
            .withCustomerId(customerId)
            .withBuildingIds(Arrays.asList(sourceBuildingId, duplicatedBuildingId))
            .withFunctionType(FunctionType.RULE)
            .build());
        
        findAdFunctionInstanceCandidates(FindAdFunctionInstanceCandidatesRequest
            .builder()
            .withCustomerId(customerId)
            .withBuildingIds(Arrays.asList(sourceBuildingId, duplicatedBuildingId))
            .withFunctionType(FunctionType.COMPUTED_POINT)
            .build());

        createAdFunctionInstancesFromCandidates(CreateAdFunctionInstancesRequest
            .builder()
            .withCustomerId(customerId)
            .withBuildingIds(Arrays.asList(sourceBuildingId, duplicatedBuildingId))
            .withFunctionType(NodeHierarchyCommandRequest.RULE)
            .build());
        
        createAdFunctionInstancesFromCandidates(CreateAdFunctionInstancesRequest
            .builder()
            .withCustomerId(customerId)
            .withBuildingIds(Arrays.asList(sourceBuildingId, duplicatedBuildingId))
            .withFunctionType(NodeHierarchyCommandRequest.COMPUTED_POINT)
            .build());
        
        evaluateReports(EvaluateReportsRequest
            .builder()
            .withCustomerId(customerId)
            .withBuildingIds(Arrays.asList(sourceBuildingId, duplicatedBuildingId))
            .build());
        
        List<ReportInstanceData> reportData = new ArrayList<>();
        for (Integer reportTemplateId: DictionaryContext.getReportTemplatesContainer().getReportTemplateIds()) {
          reportData.add(ReportInstanceData
              .builder()
              .withBuildingId(sourceBuildingId)
              .withReportTemplateId(reportTemplateId)
              .withState(ReportState.ENABLED.toString())
              .build());
        }
        updateReportInstances(UpdateReportInstancesRequest
            .builder()
            .withCustomerId(customerId)
            .withBuildingId(sourceBuildingId)
            .withData(reportData)
            .build());
        
        reportData = new ArrayList<>();
        for (Integer reportTemplateId: DictionaryContext.getReportTemplatesContainer().getReportTemplateIds()) {
          reportData.add(ReportInstanceData
              .builder()
              .withBuildingId(duplicatedBuildingId)
              .withReportTemplateId(reportTemplateId)
              .withState(ReportState.ENABLED.toString())
              .build());
        }
        updateReportInstances(UpdateReportInstancesRequest
            .builder()
            .withCustomerId(customerId)
            .withBuildingId(duplicatedBuildingId)
            .withData(reportData)
            .build());        
        
      } catch (Exception e) {
        LOGGER.error("Unable to duplicate building: " 
            + sourceBuildingId
            + " with duplicationIndex: "
            + duplicationIndex, e);
      }
    }
  }
  
  @Override
  public Boolean addPointTemplateUnitMappingOverride(
      AddPointTemplateOverrideRequest request)
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException,
      StaleDataException {
    
    PointTemplateUnitMappingOverride pointTemplateUnitMappingOverride = request.getPointTemplateUnitMappingOverride();
    
    boolean keepIpUnitSystem = pointTemplateUnitMappingOverride.getKeepIpUnitSystem();
    Integer unitMappingId = pointTemplateUnitMappingOverride.getUnitMappingId();
    
    if (keepIpUnitSystem && unitMappingId != null) {
      throw new IllegalArgumentException("'unitMappingId' must be null when 'keepIpUnitSystem' is true.");
    } else if (!keepIpUnitSystem && unitMappingId == null) {
      throw new IllegalArgumentException("'unitMappingId' must be non-null when 'keepIpUnitSystem' is false.");
    }
    
    Integer pointTemplateId = pointTemplateUnitMappingOverride.getPointTemplateId();
    PointTemplateEntity pointTemplate = null;
    if (pointTemplateId != null) {
      pointTemplate = DictionaryContext.getNodeTagTemplatesContainer().getPointTemplate(pointTemplateUnitMappingOverride.getPointTemplateId());
    } else {
      throw new IllegalArgumentException("'pointTemplateId' must be specified."); 
    }
    
    PointTemplateUnitMappingEntity pointTemplateUnitMapping = DictionaryContext.getNodeTagTemplatesContainer().getDefaultPointTemplateUnitMapping(pointTemplateId);
    if (pointTemplateUnitMapping == null) {
      throw new IllegalArgumentException("Cannot add a point template unit mapping override when there does not exist a default point template unit mapping to begin with for point template: "
          + pointTemplateId
          + "].");
    }
    
    UnitMappingEntity unitMapping = null;
    if (unitMappingId != null) {
      
      unitMapping = DictionaryContext.getNodeTagTemplatesContainer().getUnitMapping(pointTemplateUnitMappingOverride.getUnitMappingId());
    }

    Integer distributorId = pointTemplateUnitMappingOverride.getDistributorId();
    Integer customerId = pointTemplateUnitMappingOverride.getCustomerId();
    Integer buildingId = pointTemplateUnitMappingOverride.getBuildingId();
    if (distributorId != null) {
      
      AbstractDistributorEntity distributor = distributorRepository.loadDistributor(distributorId);
      
      if (distributor.getUnitSystem().equals(UnitSystem.IP)) {
        throw new IllegalArgumentException("Cannot add a point template unit mapping override to a distributor that has the IP unit system: ["
            + distributor.getName()
            + "].");
      }
      
      DistributorLevelPointTemplateUnitMappingOverrideEntity e = new DistributorLevelPointTemplateUnitMappingOverrideEntity(
          null,
          pointTemplate,
          Boolean.valueOf(keepIpUnitSystem),
          unitMapping,
          distributor);
      distributor.addPointTemplateUnitMappingOverride(e);
      
      distributorRepository.storeDistributor(distributor);
      
    } else if (buildingId != null) {

      LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
          .builder()
          .withCustomerId(request.getCustomerId())
          .build();
      
      List<Integer> buildingIds = request.getBuildingIds();
      if (buildingIds != null && !buildingIds.isEmpty()) {

        loadPortfolioOptions = LoadPortfolioOptions
            .builder(loadPortfolioOptions)
            .withFilterNodeType(NodeType.BUILDING)
            .withFilterNodePersistentIdentities(buildingIds)
            .withLoadAdFunctionInstances(Boolean.TRUE)
            .build();
      }    
      
      PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);
      BuildingEntity building = portfolio.getChildBuilding(buildingId);
      
      if (building.getUnitSystem().equals(UnitSystem.IP)) {
        throw new IllegalArgumentException("Cannot add a point template unit mapping override to a building that has the IP unit system: ["
            + building.getNodePath()
            + "].");
      }
      
      BuildingLevelPointTemplateUnitMappingOverrideEntity e = new BuildingLevelPointTemplateUnitMappingOverrideEntity(
          null,
          pointTemplate,
          Boolean.valueOf(keepIpUnitSystem),
          unitMapping,
          building);
      building.addPointTemplateUnitMappingOverride(e);      

      // Re-process all mapped points and AD function instances to deal with the override.
      // (Only deal with the overridden point template) 
      for (AbstractNodeEntity node: portfolio.getAllNodes()) {
        if (node.getAncestorBuilding().equals(building)) {
          if (node instanceof AbstractPointEntity) {
            AbstractPointEntity point = (AbstractPointEntity)node;
            PointTemplateEntity pointPointTemplate = point.getPointTemplateNullIfEmpty();
            if (pointPointTemplate != null && pointPointTemplate.equals(pointTemplate)) {
              if (pointTemplateUnitMappingOverride.getKeepIpUnitSystem()) {
                point.setUnitSystem(UnitSystem.IP);    
              } else {
                // Here, even though the point is already associated with an SI unit, we are going to use the
                // "overridden" unit mapping SI unit that is associated with the override (i.e. priority > 1)
                point.setUnitSystem(UnitSystem.SI);
              }
            }
          } else if (node instanceof EnergyExchangeEntity) {
            EnergyExchangeEntity energyExchangeEntity = (EnergyExchangeEntity)node;
            if (pointTemplateUnitMappingOverride.getKeepIpUnitSystem()) {
              energyExchangeEntity.setUnitSystem(UnitSystem.IP, pointTemplate);    
            } else {
              // Here, even though the point is already associated with an SI unit, we are going to use the
              // "overridden" unit mapping SI unit that is associated with the override (i.e. priority > 1)
              energyExchangeEntity.setUnitSystem(UnitSystem.SI, pointTemplate);
            }
          }
        }
      }
      
      // Intelligent store to the repository.
      updatePortfolio(
          portfolio,
          request,
          Boolean.FALSE);
      
    } else if (customerId != null) {

      boolean loadDistributorPaymentMethods = false;
      boolean loadDistributorUsers = false;
      AbstractCustomerEntity customer = customerRepository.loadCustomer(customerId, loadDistributorPaymentMethods, loadDistributorUsers);
      
      if (customer.getUnitSystem().equals(UnitSystem.IP)) {
        throw new IllegalArgumentException("Cannot add a point template unit mapping override to a customer that has the IP unit system: ["
            + customer.getName()
            + "].");
      }
      
      CustomerLevelPointTemplateUnitMappingOverrideEntity e = new CustomerLevelPointTemplateUnitMappingOverrideEntity(
          null,
          pointTemplate,
          Boolean.valueOf(keepIpUnitSystem),
          unitMapping,
          customer);
      customer.addPointTemplateUnitMappingOverride(e);
      
      customerRepository.storeCustomer(customer);
      
    } else {
      throw new IllegalArgumentException("Exactly one of: ['distributorId', 'customerId' or 'buildingId'] must be specified.");
    }
    
    return Boolean.TRUE;
  }

  @Override
  public Boolean removePointTemplateUnitMappingOverride(
      RemovePointTemplateOverrideRequest request)
  throws 
      EntityDoesNotExistException,
      StaleDataException {
    
    PointTemplateUnitMappingOverride pointTemplateUnitMappingOverride = request.getPointTemplateUnitMappingOverride();
    
    Integer pointTemplateId = pointTemplateUnitMappingOverride.getPointTemplateId();
    PointTemplateEntity pointTemplate = null;
    if (pointTemplateId != null) {
      pointTemplate = DictionaryContext.getNodeTagTemplatesContainer().getPointTemplate(pointTemplateUnitMappingOverride.getPointTemplateId());
    } else {
      throw new IllegalArgumentException("'pointTemplateId' must be specified."); 
    }
    
    Integer pointTemplateUnitMappingOverrideId = pointTemplateUnitMappingOverride.getId();
    Integer distributorId = pointTemplateUnitMappingOverride.getDistributorId();
    Integer customerId = pointTemplateUnitMappingOverride.getCustomerId();
    Integer buildingId = pointTemplateUnitMappingOverride.getBuildingId();
    if (distributorId != null) {
      
      AbstractDistributorEntity distributor = distributorRepository.loadDistributor(distributorId);
      
      if (distributor.getUnitSystem().equals(UnitSystem.IP)) {
        throw new IllegalArgumentException("Cannot remove a point template unit mapping override from a distributor that has the IP unit system: ["
            + distributor.getName()
            + "].");
      }
      
      distributor.removePointTemplateUnitMappingOverride(pointTemplateUnitMappingOverrideId);
      
      distributorRepository.storeDistributor(distributor);
      
    } else if (buildingId != null) {

      LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
          .builder()
          .withCustomerId(request.getCustomerId())
          .build();
      
      List<Integer> buildingIds = request.getBuildingIds();
      if (buildingIds != null && !buildingIds.isEmpty()) {

        loadPortfolioOptions = LoadPortfolioOptions
            .builder(loadPortfolioOptions)
            .withFilterNodeType(NodeType.BUILDING)
            .withFilterNodePersistentIdentities(buildingIds)
            .withLoadAdFunctionInstances(Boolean.TRUE)
            .build();
      }    
      
      PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);
      BuildingEntity building = portfolio.getChildBuilding(buildingId);
      
      if (building.getUnitSystem().equals(UnitSystem.IP)) {
        throw new IllegalArgumentException("Cannot remove a point template unit mapping override from a building that has the IP unit system: ["
            + building.getNodePath()
            + "].");
      }
      
      building.removePointTemplateUnitMappingOverride(pointTemplateUnitMappingOverrideId);      

      
      // Re-process all mapped points and AD function instances to deal with the default unit mapping.
      // (Only deal with the overridden point template) 
      for (AbstractNodeEntity node: portfolio.getAllNodes()) {
        if (node.getAncestorBuilding().equals(building)) {
          if (node instanceof AbstractPointEntity) {
            AbstractPointEntity point = (AbstractPointEntity)node;
            PointTemplateEntity pointPointTemplate = point.getPointTemplateNullIfEmpty();
            if (pointPointTemplate != null && pointPointTemplate.equals(pointTemplate)) {
              point.setUnitSystem(UnitSystem.SI);
            }
          } else if (node instanceof EnergyExchangeEntity) {
            EnergyExchangeEntity energyExchangeEntity = (EnergyExchangeEntity)node;
            energyExchangeEntity.setUnitSystem(UnitSystem.IP, pointTemplate);
          }
        }
      }
      
      // Intelligent store to the repository.
      updatePortfolio(
          portfolio,
          request,
          Boolean.FALSE);
      
    } else if (customerId != null) {

      boolean loadDistributorPaymentMethods = false;
      boolean loadDistributorUsers = false;
      AbstractCustomerEntity customer = customerRepository.loadCustomer(customerId, loadDistributorPaymentMethods, loadDistributorUsers);
      
      if (customer.getUnitSystem().equals(UnitSystem.IP)) {
        throw new IllegalArgumentException("Cannot remove a point template unit mapping override from a customer that has the IP unit system: ["
            + customer.getName()
            + "].");
      }
      
      customer.removePointTemplateUnitMappingOverride(pointTemplateUnitMappingOverrideId);
      
      customerRepository.storeCustomer(customer);
      
    } else {
      throw new IllegalArgumentException("Exactly one of: ['distributorId', 'customerId' or 'buildingId'] must be specified.");
    }
    
    return Boolean.TRUE;
  }
  
  @Override
  public List<String> evaluateCustomAsyncPoints(
      Integer customerId,
      Integer buildingId,
      Integer pointId,
      Set<ComputationInterval> computationIntervals,
      Boolean performRecalculate,
      Timestamp startTimestamp,
      Timestamp endTimestamp)
  throws
      EntityDoesNotExistException,
      TimeSeriesClientException {
    
    List<String> errors = new ArrayList<>();
    long start = System.currentTimeMillis();
    LOGGER.info("BEGIN: EVALUATE CUSTOM ASYNC COMPUTED POINTS: CUSTOMER: {}, BUILDING: {}",
        customerId,
        buildingId);

    String buildingNodePath = null;
    try {
      
      
      // PERFORM VALIDATION.
      if (performRecalculate == null) {
        performRecalculate = Boolean.FALSE;
      } else if (performRecalculate && startTimestamp != null && endTimestamp != null) {
        startTimestamp = null;
        endTimestamp = null;
        LOGGER.warn("startTimestamp and endTimestamp are ignored when performRecalculate is true");
      }

      
      // GET THE LIST OF BUILDINGS, OR BUILDING, TO PROCESS.
      List<Integer> buildingIds = null;
      if (buildingId != null) {
        buildingIds = Arrays.asList(buildingId);
      } else {
        buildingIds = nodeHierarchyRepository.getBuildingIds(customerId);  
      }

      
      // FOR EACH BUILDING.
      for (Integer bid: buildingIds) {

        
        // LOAD THE PORTFOLIO WITH JUST THE GIVEN BUILDING.
        LoadPortfolioOptions loadPortfolioOptions = LoadPortfolioOptions
            .builder()
            .withCustomerId(customerId)
            .withFilterNodeType(NodeType.BUILDING)
            .withFilterNodePersistentIdentity(bid)
            .withDepthNodeType(NodeType.POINT)
            .withLoadCustomPointTemporalData(Boolean.TRUE)
            .build();
        PortfolioEntity portfolio = nodeHierarchyRepository.loadPortfolio(loadPortfolioOptions);
        BuildingEntity building = portfolio.getBuilding(bid);
        buildingNodePath = building.getNodePath();
        

        // LOAD BUILDING STATE.
        BuildingCustomAsyncComputedPointState buildingState = null;
        if (!performRecalculate) {
          buildingState = loadBuildingState(bid);
        } else {
          buildingState = new BuildingCustomAsyncComputedPointState(bid);
        }


        // PROCESS EACH CUSTOM ASYNC COMPUTED POINT IN THE BUILDING SERIALLY.
        for (CustomAsyncComputedPointEntity customAsyncComputedPoint: portfolio.getAllCustomAsyncComputedPoints()) {
          
          
          // PROCESS IF THE POINT/COMPUTATION INTERVAL OPTIONAL FILTERS MATCH.
          if ((pointId == null || customAsyncComputedPoint.getPersistentIdentity().equals(pointId))
              || (computationIntervals == null || computationIntervals.isEmpty() || computationIntervals.contains(customAsyncComputedPoint.getComputationInterval()))) {

            
            // EVALULATE THE CUSTOM ASYNC COMPUTED POINT FOR THE GIVEN TIME RANGE.
            evaluateCustomAsyncPoint(
                portfolio, 
                customAsyncComputedPoint, 
                buildingState,
                performRecalculate,
                startTimestamp,
                endTimestamp);
            }            
          }
        
        
        // STORE BUILDING STATE.
        storeBuildingState(buildingState); 
      }

    } catch (Exception e) {

      String errorMessagePrefix = "Unable to evaluate custom async computed points for building: ["
          + buildingNodePath
          +"], error: "
          + e.getMessage();
      String error = ExceptionUtils.extractReason(errorMessagePrefix, e);
      LOGGER.error(error, e);
      errors.add(error);
    }

    LOGGER.info("END: EVALUATE CUSTOM ASYNC COMPUTED POINTS: ELAPSED(ms): {}, CUSTOMER: {}, BUILDING: {}",
        (System.currentTimeMillis()-start),
        customerId,
        buildingId);

    return errors;
  }
  
  private void evaluateCustomAsyncPoint(
      PortfolioEntity portfolio,
      CustomAsyncComputedPointEntity customAsyncComputedPoint,
      BuildingCustomAsyncComputedPointState buildingState,
      Boolean performRecalculate,
      Timestamp startTimestamp,
      Timestamp endTimestamp)
  throws 
      EntityDoesNotExistException,
      TimeSeriesClientException {

    
    // USED FOR SUBMITTING TIME SERIES DATA.
    int numMetricValues = 0;
    String metricQueryPrefix = null;
    String kafkaJobId = null;
    Map<String, String> tags = new HashMap<>();
    tags.put(TimeSeriesServiceClient.TAG, TimeSeriesServiceClient.DUMMY);
    String customAsyncComputedPointMetricId = customAsyncComputedPoint.getMetricIdForTsdb();
    Map<Long, Double> metricValuesToSubmit = new TreeMap<>();

    
    // ADJUST THE CURRENT TIME AND SETUP TIME-SERIES CLIENT VARS. ACCORDING TO THE COMPUTATION INTERVAL.
    ComputationInterval computationInterval = customAsyncComputedPoint.getComputationInterval();
    LocalDateTime currentLocalDateTime = null;
    if (computationInterval.equals(ComputationInterval.QUARTER_HOUR)) {
      
      currentLocalDateTime = AbstractEntity.adjustCurrentTimeIntoFifteenMinuteFloor();
      metricQueryPrefix = TimeSeriesServiceClient.METRIC_QUERY_AGGREGATOR_SUM + TimeSeriesServiceClient.METRIC_QUERY_DOWNSAMPLER_FIFTEEN_MIN_INTERVAL;
      kafkaJobId = CUSTOM_COMPUTED_POINT_INTRADAY_KAFKA_JOB_ID;
      
    } else if (computationInterval.equals(ComputationInterval.DAILY)) {
      
      currentLocalDateTime = AbstractEntity.adjustCurrentTimeIntoDailyFloor();
      metricQueryPrefix = TimeSeriesServiceClient.METRIC_QUERY_AGGREGATOR_SUM + TimeSeriesServiceClient.METRIC_QUERY_DOWNSAMPLER_ONE_DAY_INTERVAL;
      kafkaJobId = CUSTOM_COMPUTED_POINT_INTERDAY_KAFKA_JOB_ID;
      
    } else {
      
      currentLocalDateTime = AbstractEntity.adjustCurrentTimeIntoMonthlyFloor();
      metricQueryPrefix = TimeSeriesServiceClient.METRIC_QUERY_AGGREGATOR_SUM + TimeSeriesServiceClient.METRIC_QUERY_DOWNSAMPLER_ONE_MONTH_INTERVAL;
      kafkaJobId = CUSTOM_COMPUTED_POINT_INTERDAY_KAFKA_JOB_ID;
      
    }

    
    // GET/SET POINT STATE, OF WHICH WE REALLY CARE ABOUT THE FUNCTION STATE (FOR STATEFUL FUNCTIONS).
    Integer customAsyncComputedPointId = customAsyncComputedPoint.getPersistentIdentity();
    CustomAsyncComputedPointState pointState = buildingState.getPointState(customAsyncComputedPointId);
    if (pointState == null) {
      
      pointState = new CustomAsyncComputedPointState();
      buildingState.addPointState(customAsyncComputedPointId, pointState);
    }
    Map<String, String> functionState = pointState.getState();
    
    
    
    // START TIME FOR PROCESSING.
    AbstractCustomerEntity customer = customAsyncComputedPoint.getRootPortfolioNode().getParentCustomer();
    if (!performRecalculate) {
      startTimestamp = pointState.getTimestamp();
      if (startTimestamp == null) {
        startTimestamp = getLastProcessedAtFromTsdb(customer, customAsyncComputedPoint);
      }
    }
    if (startTimestamp == null) {
      startTimestamp = portfolio.getParentCustomer().getStartDate();
    }


    // END TIME FOR PROCESSING.
    if (endTimestamp == null) {
      endTimestamp = Timestamp.valueOf(currentLocalDateTime);  
    }    

    
    // ITERATION IS DONE VIA EPOCH MILLIS.
    long startMillis = startTimestamp.getTime();
    long endMillis = endTimestamp.getTime();
    
    
    // LOOP THROUGH EACH TEMPORAL CONFIG.
    List<TemporalAsyncComputedPointConfigEntity> childTemporalConfigs = new ArrayList<>();
    childTemporalConfigs.addAll(customAsyncComputedPoint.getChildTemporalConfigs());
    for (int i=0; i < childTemporalConfigs.size(); i++) {
      
      TemporalAsyncComputedPointConfigEntity childTemporalConfig = childTemporalConfigs.get(i);

      
      // DETERMINE IF THE START/END MILLIS NEED TO BE CLIPPED BASED ON EFFECTIVE DATE.
      LocalDateTime childTemporalConfigEffectiveLocalDateTime = childTemporalConfig.getEffectiveDate().atStartOfDay();
      ZoneOffset zo = AbstractEntity.UTC_ZONE_ID.getRules().getOffset(childTemporalConfigEffectiveLocalDateTime);        
      long childTemporalConfigEffectiveEpochMillis = childTemporalConfigEffectiveLocalDateTime.toInstant(zo).toEpochMilli();
      
      
      // START
      if (i == 0 && childTemporalConfigEffectiveEpochMillis <= startTimestamp.getTime()) {
        
        startMillis = startTimestamp.getTime();
        
      } else if (childTemporalConfigEffectiveEpochMillis <= startTimestamp.getTime()) {
        
        startMillis = childTemporalConfigEffectiveEpochMillis;
        
      } else {
        throw new IllegalArgumentException("startTimestamp: ["
            + startTimestamp 
            + "] is out of range for custom point: ["
            + customAsyncComputedPoint.getNodePath()
            + "], configs: "
            + childTemporalConfigs);              
      }
      
      
      // END
      if (i == childTemporalConfigs.size()-1) {
        
        endMillis = endTimestamp.getTime();

      } else if (childTemporalConfigEffectiveEpochMillis >= startTimestamp.getTime()) {
        
        TemporalAsyncComputedPointConfigEntity nextChildTemporalConfig = childTemporalConfigs.get(i + 1); 
        LocalDateTime nextChildTemporalConfigEffectiveLocalDateTime = nextChildTemporalConfig.getEffectiveDate().atStartOfDay();
        long nextChildTemporalConfigEffectiveEpochMillis = nextChildTemporalConfigEffectiveLocalDateTime.toInstant(zo).toEpochMilli();
        endMillis = nextChildTemporalConfigEffectiveEpochMillis;
        
      }
      
      
      // GATHER THE FORMULA POINT VARIABLES FOR THE GIVEN TEMPORAL CONFIG.
      Map<String, String> lastKnownVariableValues = new HashMap<>();
      Map<String, Integer> metricIdToPointMap = new TreeMap<>();
      for (FormulaVariableEntity formulaVariable: childTemporalConfig.getChildVariables()) {
        
        CustomPointFormulaVariableEligiblePoint variablePoint = formulaVariable.getParentPoint();
        metricIdToPointMap.put(metricQueryPrefix + variablePoint.getMetricIdForTsdb(), variablePoint.getPersistentIdentity());
      }
      
      
      // RETRIEVE ANY PREVIOUSLY COMPUTED TIME SERIES VALUES FOR THE GIVEN POINT SO THAT WE KNOW THAT WE HAVE
      // TO EITHER EVALUATE OR NOT SUBMIT ON A RECALCULATE IF VALUE HAS NOT CHANGED FROM WHAT IS WAS BEFORE.
      metricIdToPointMap.put(metricQueryPrefix + customAsyncComputedPoint.getMetricIdForTsdb(), customAsyncComputedPoint.getPersistentIdentity());
      
      
      LocalDateTime startLocalDateTime = Instant
          .ofEpochMilli(startMillis)
          .atZone(TimeZone
              .getTimeZone(CustomAsyncComputedPointEntity.ETC_UTC_TIMEZONE)
              .toZoneId())
          .toLocalDateTime();            

      LocalDateTime endLocalDateTime = Instant
          .ofEpochMilli(endMillis)
          .atZone(TimeZone
              .getTimeZone(CustomAsyncComputedPointEntity.ETC_UTC_TIMEZONE)
              .toZoneId())
          .toLocalDateTime();

      
      // PROCESS TIME SERIES ACCORDING TO THE COMPUTATION INTERVAL.
      LocalDateTime fromLocalDateTime = startLocalDateTime;
      Long fromMillis = fromLocalDateTime.toEpochSecond(ZoneOffset.UTC) * 1000;
      
      LocalDateTime toLocalDateTime = null;
      if (computationInterval.equals(ComputationInterval.QUARTER_HOUR)) {

        toLocalDateTime = fromLocalDateTime.plusDays(TimeSeriesServiceClient.FIFTEEN_MINUTE_INTERVAL_NUM_DAYS_TO_PROCESS_AT_A_TIME);
        
      } else if (computationInterval.equals(ComputationInterval.DAILY)) {
        
        toLocalDateTime = fromLocalDateTime.plusDays(TimeSeriesServiceClient.DAILY_INTERVAL_NUM_DAYS_TO_PROCESS_AT_A_TIME);
        
      } else {
        
        toLocalDateTime = fromLocalDateTime.plusMonths(TimeSeriesServiceClient.MONTHLY_INTERVAL_NUM_MONTHS_TO_PROCESS_AT_A_TIME);
        
      }
      if (toLocalDateTime.isAfter(endLocalDateTime)) {
        toLocalDateTime = endLocalDateTime;
      }
      Long toMillis = toLocalDateTime.toEpochSecond(ZoneOffset.UTC) * 1000;

      
      // PROCESS THE GIVEN INTERVAL.
      while (fromLocalDateTime.isBefore(toLocalDateTime)) {
        
        
        // RETRIEVE ALL RELEVANT TIME SERIES DATA FOR THE GIVEN INTERVAL.
        Map<String, Map<Long, Double>> allMetricValues = timeSeriesServiceClient.retrieveMetricValuesFromTsdb(
            customer,
            metricIdToPointMap.keySet(),
            Long.valueOf(fromMillis),
            Long.valueOf(toMillis));
        
        for (Map.Entry<String, Map<Long, Double>> entry: allMetricValues.entrySet()) {
          
          String metricId = metricQueryPrefix + entry.getKey();
          Map<Long, Double> metricValues = entry.getValue();
          
          Integer pointId = metricIdToPointMap.get(metricId);
          AbstractPointEntity p = portfolio.getPoint(pointId);
          
          p.resetValues();
          p.addValues(metricValues);
        }

        
        // EVALUATE THE CUSTOM ASYNC POINT FOR THE PROPER NUMBER OF INCREMENTS BETWEEN THE FROM AND TO TIMESTAMPS.
        long timeMillis = fromMillis;
        while (timeMillis <= toMillis) {
          
          LocalDateTime localDateTime = Instant
              .ofEpochMilli(timeMillis)
              .atZone(TimeZone
                  .getTimeZone(CustomAsyncComputedPointEntity.ETC_UTC_TIMEZONE)
                  .toZoneId())
              .toLocalDateTime();
          
          Map<String, Double> variableValues = new TreeMap<>();
          for (FormulaVariableEntity formulaVariable: childTemporalConfig.getChildVariables()) {
            
            String variableName = formulaVariable.getName();
            CustomPointFormulaVariableEligiblePoint variablePoint = formulaVariable.getParentPoint();
            Long epochSeconds = Long.valueOf(timeMillis/1000);
            String variableValue = variablePoint.getValue(epochSeconds);
            
            
            // IF THERE'S NO VALUE FOR THE GIVEN TIME STAMP, THEN USE THE FILL POLICY TO DETERMINE THE VALUE
            if (variableValue != null) {
              lastKnownVariableValues.put(variableName, variableValue);
            }
            if (variableValue == null) {
              
              FillPolicy fillPolicy = formulaVariable.getFillPolicy();
              if (fillPolicy.equals(FillPolicy.ZERO)) {
                
                variableValue = "0.0";
                
              } else if (fillPolicy.equals(FillPolicy.LAST_KNOWN)) {
                
                variableValue = lastKnownVariableValues.get(variableName);
                if (variableValue == null) {
                  
                  variableValue = "0.0";
                  
                  LOGGER.error("Variable: ["
                      + formulaVariable
                      + "] does not have any last known value for: ["
                      + localDateTime
                      + "], lastKnownVariableValues: "
                      + lastKnownVariableValues
                      + " for custom point: "
                      + customAsyncComputedPoint.getMetricIdForTsdb());
                }
              }
            }
            variableValues.put(
                variableName,
                Double.parseDouble(variableValue));
          }
          
          
          // EVALUATE THE FORMULA FOR THE GIVEN TIMESTAMP.
          Result result = childTemporalConfig.evaluateFormula(
              timeMillis,
              variableValues,
              functionState);
          
          
          // PROCESS THE RESULTS OF THE EVALUATION.
          Double newValue = null;
          Optional<Double> optionalValue = result.getValue();
          if (optionalValue.isPresent()) {
            
            // NOTE: Stateful functions (like "delta", which does not have a value the first time computed)
            // https://github.com/MadDogTechnology/computed-point-expression-parser 
            newValue = optionalValue.get();
          }

          
          // STORE THE FUNCTION STATE (ONLY HAS MEANING FOR STATEFUL FUNCTIONS)
          functionState = result.getState();
          buildingState.addPointState(customAsyncComputedPointId, new CustomAsyncComputedPointState(
              Timestamp.valueOf(toLocalDateTime), 
              newValue, 
              functionState));
          
          
          // EXTRACT AND SUBMIT RESULTS TO TSDB/KAFKA (IF BATCH SIZE HAS BEEN REACHED)
          if (newValue != null) {

            // SEE IF AN OLD VALUE EXISTS.
            Double oldValue = null;
            String strOldValue = customAsyncComputedPoint.getValue(timeMillis);
            if (strOldValue != null) {
              oldValue = Double.parseDouble(strOldValue);
            }
            
            
            // ONLY SUBMIT THE VALUE IF THERE ISN'T A VALUE OR THE NEW VALUE IS DIFFERENT THAN THE OLD VALUE.
            if (oldValue == null || !oldValue.equals(newValue)) {

              customAsyncComputedPoint.addValue(timeMillis, newValue.toString());
              metricValuesToSubmit.put(timeMillis, newValue);
              numMetricValues++;
            }
            
            
            // SEE IF THE BATCH THRESHOLD HAS BEEN REACHED.
            if (numMetricValues >= TimeSeriesServiceClient.NUM_METRICS_FOR_TSDB_BATCH) {
              
              timeSeriesServiceClient.submitMetricValuesToTsdb(
                  customer,
                  customAsyncComputedPointMetricId,
                  tags,
                  metricValuesToSubmit);

              timeSeriesServiceClient.submitMetricValuesToKafka(
                  customer,
                  kafkaJobId,
                  customAsyncComputedPointMetricId, 
                  tags, 
                  metricValuesToSubmit);
              
              numMetricValues = 0;
              metricValuesToSubmit.clear();
            }
          }
          
          
          // INCREMENT THE TIME WITHIN THE GIVEN TIME INTERVAL.
          if (computationInterval.equals(ComputationInterval.QUARTER_HOUR)) {
            
            timeMillis = timeMillis + TimeSeriesServiceClient.NUM_MILLISECONDS_IN_15_MINUTES;
            
          } else if (computationInterval.equals(ComputationInterval.DAILY)) {
            
            timeMillis = timeMillis + TimeSeriesServiceClient.NUM_MILLISECONDS_IN_1_DAY;
            
          } else {

            LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeMillis), AbstractEntity.UTC_ZONE_ID);
            LocalDateTime nextIntervalLdt = ldt.plusMonths(1);
            long deltaMillis = nextIntervalLdt.toEpochSecond(ZoneOffset.UTC) * 1000 - timeMillis;
            timeMillis = timeMillis + deltaMillis;
            
          }
        }
        
        
        // INCREMENT THE TIME INTERVAL TO PROCESS (IF THE END OF THE LAST INTERVAL IS BEFORE THE END TIMESTAMP).
        fromLocalDateTime = toLocalDateTime;
        fromMillis = toMillis;
        if (toLocalDateTime.isBefore(endLocalDateTime)) {

          if (computationInterval.equals(ComputationInterval.QUARTER_HOUR)) {

            toLocalDateTime = fromLocalDateTime.plusDays(TimeSeriesServiceClient.FIFTEEN_MINUTE_INTERVAL_NUM_DAYS_TO_PROCESS_AT_A_TIME);
            
          } else if (computationInterval.equals(ComputationInterval.DAILY)) {
            
            toLocalDateTime = fromLocalDateTime.plusDays(TimeSeriesServiceClient.DAILY_INTERVAL_NUM_DAYS_TO_PROCESS_AT_A_TIME);
            
          } else {
            
            toLocalDateTime = fromLocalDateTime.plusMonths(TimeSeriesServiceClient.MONTHLY_INTERVAL_NUM_MONTHS_TO_PROCESS_AT_A_TIME);
            
          }
          
          if (toLocalDateTime.isAfter(endLocalDateTime)) {
            toLocalDateTime = endLocalDateTime;
          }
          toMillis = toLocalDateTime.toEpochSecond(ZoneOffset.UTC) * 1000;
        }
      }
    }
    
    
    // POST ANY REMAINING METRIC VALUES TO TSDB/KAFKA
    if (numMetricValues > 0) {
      
      timeSeriesServiceClient.submitMetricValuesToTsdb(
          customer,
          customAsyncComputedPointMetricId,
          tags,
          metricValuesToSubmit);

      timeSeriesServiceClient.submitMetricValuesToKafka(
          customer,
          kafkaJobId,
          customAsyncComputedPointMetricId, 
          tags, 
          metricValuesToSubmit);
      
    }
  }
  
  private Timestamp getLastProcessedAtFromTsdb(
      AbstractCustomerEntity customer,
      CustomAsyncComputedPointEntity customAsyncComputedPoint) 
  throws 
      TimeSeriesClientException {
    
    Long fromMillis = null;
    String metricQueryPrefix = null;
    
    ComputationInterval computationInterval = customAsyncComputedPoint.getComputationInterval();
    if (computationInterval.equals(ComputationInterval.QUARTER_HOUR)) {
      
      fromMillis = AbstractEntity.getTimeKeeper().getCurrentTimeInMillis() - TimeSeriesServiceClient.NUM_MILLISECONDS_IN_1_DAY;
      metricQueryPrefix = TimeSeriesServiceClient.METRIC_QUERY_AGGREGATOR_SUM + TimeSeriesServiceClient.METRIC_QUERY_DOWNSAMPLER_FIFTEEN_MIN_INTERVAL;
      
    } else if (computationInterval.equals(ComputationInterval.DAILY)) {
      
      fromMillis = AbstractEntity.getTimeKeeper().getCurrentTimeInMillis() - TimeSeriesServiceClient.NUM_MILLISECONDS_IN_90_DAYS;
      metricQueryPrefix = TimeSeriesServiceClient.METRIC_QUERY_AGGREGATOR_SUM + TimeSeriesServiceClient.METRIC_QUERY_DOWNSAMPLER_ONE_DAY_INTERVAL;
      
    } else {
      
      fromMillis = AbstractEntity.getTimeKeeper().getCurrentTimeInMillis() - TimeSeriesServiceClient.NUM_MILLISECONDS_IN_9_MONTHS;
      metricQueryPrefix = TimeSeriesServiceClient.METRIC_QUERY_AGGREGATOR_SUM + TimeSeriesServiceClient.METRIC_QUERY_DOWNSAMPLER_ONE_MONTH_INTERVAL;
      
    }
    
    String metricId = customAsyncComputedPoint.getMetricIdForTsdb();
    Map<String, Map<Long, Double>> allMetricValues = timeSeriesServiceClient.retrieveMetricValuesFromTsdb(
        customer,
        Arrays.asList(metricQueryPrefix + metricId),
        Long.valueOf(fromMillis),
        null);
    
    Long lastValueEpochSeconds = null;
    Map<Long, Double> metricValues = allMetricValues.get(metricId);
    if (metricValues != null) {
      for (Long timeEpochSeconds: metricValues.keySet()) {
        if (lastValueEpochSeconds == null || timeEpochSeconds >= lastValueEpochSeconds) {
          lastValueEpochSeconds = timeEpochSeconds;
        }
      }      
    }
    
    if (lastValueEpochSeconds != null) {

      LocalDateTime lastValueLocalDateTime = Instant
          .ofEpochMilli(lastValueEpochSeconds*1000)
          .atZone(TimeZone
              .getTimeZone(CustomAsyncComputedPointEntity.ETC_UTC_TIMEZONE)
              .toZoneId())
          .toLocalDateTime();
      
      return Timestamp.valueOf(lastValueLocalDateTime);
    }
    
    return null;
  }
  
  private BuildingCustomAsyncComputedPointState loadBuildingState(Integer buildingId) {
    
    BuildingCustomAsyncComputedPointState buildingState = null;
    
    String key = generateCustomComputedCacheBuildingStateKey(buildingId);
    byte[] bytes = cacheClient.get(key);
    if (bytes != null) {
    
      buildingState = KryoSerialize.getInstance().decode(bytes, BuildingCustomAsyncComputedPointState.class);
      
    } else {
      
      buildingState = new BuildingCustomAsyncComputedPointState(buildingId);
      
    }
    
    return buildingState;
  }
  
  private void storeBuildingState(BuildingCustomAsyncComputedPointState buildingState) {

    String key = generateCustomComputedCacheBuildingStateKey(buildingState.getBuildingId());
    
    cacheClient.set(
        key, 
        KryoSerialize.getInstance().encode(buildingState), 
        CacheClient.ONE_WEEK_TIME_TO_LIVE);
  }
  
  private String generateCustomComputedCacheBuildingStateKey(Integer buildingId) {
    
    return CUSTOM_COMPUTED_POINT_CACHE_KEY_PREFIX + buildingId;
  }
  
  // FAST LANE WRITER OPERATIONS (I.E. DOES NOT LOAD PORTFOLIO, DOES STRAIGHT INSERT/UPDATE)
  @Override
  public int addNodeTag(int customerId, int nodeId, int tagId) {
    
    int rowsInserted = nodeHierarchyRepository.insertNodeTag(customerId, nodeId, tagId);
    
    CompletableFuture.runAsync(() -> {
      cacheClient.removeAllCacheEntriesForCustomer(customerId);
    });    
    
    return rowsInserted;
  }

  @Override
  public int removeNodeTag(int customerId, int nodeId, int tagId) {
    
    int rowsRemoved = nodeHierarchyRepository.deleteNodeTag(customerId, nodeId, tagId);
    
    CompletableFuture.runAsync(() -> {
      cacheClient.removeAllCacheEntriesForCustomer(customerId);
    });    
    
    return rowsRemoved;
  }
  
  @Override
  public List<AddNodeDto> addNodes(int customerId, List<AddNodeDto> nodesToAdd) {
    
    List<AddNodeDto> addedNodes = nodeHierarchyRepository.insertNodes(customerId, nodesToAdd);
    
    CompletableFuture.runAsync(() -> {
      cacheClient.removeAllCacheEntriesForCustomer(customerId);
    });    
    
    return addedNodes;
  }

  @Override
  public AddNodeDto addNode(int customerId, AddNodeDto nodeToAdd) {

    AddNodeDto addedNode = nodeHierarchyRepository.insertNode(customerId, nodeToAdd);
    
    CompletableFuture.runAsync(() -> {
      cacheClient.removeAllCacheEntriesForCustomer(customerId);
    });    
    
    return addedNode;
  }
  
  @Override
  public AsyncPoint addCustomAsyncComputedPoint(AsyncPoint dto) {
    
    AsyncPoint asyncPoint = nodeHierarchyRepository.insertCustomAsyncComputedPoint(dto);
    
    CompletableFuture.runAsync(() -> {
      cacheClient.removeAllCacheEntriesForCustomer(asyncPoint.getCustomerId());
    });    
    
    return asyncPoint;
  }
    
  @Override
  public AsyncPoint updateCustomAsyncComputedPoint(AsyncPoint dto) {
    
    AsyncPoint asyncPoint = nodeHierarchyRepository.updateCustomAsyncComputedPoint(dto);

    CompletableFuture.runAsync(() -> {
      cacheClient.removeAllCacheEntriesForCustomer(asyncPoint.getCustomerId());
    });    
    
    return asyncPoint;
  }
  
  @Override
  public void updateNodeDisplayName(int customerId, int nodeId, int nodeTypeId, int pointTypeId, String nodeDisplayName) {

    nodeHierarchyRepository.updateNodeDisplayName(customerId, nodeId, nodeTypeId, pointTypeId, nodeDisplayName);

    CompletableFuture.runAsync(() -> {
      cacheClient.removeAllCacheEntriesForCustomer(customerId);
    });    
  }
  
  // REPOSITORY METHOD TO INSERT, UPDATE AND DELETE ARE WRAPPED HERE.
  private Map<String, List<AbstractNodeEntity>> updatePortfolio(
      PortfolioEntity portfolio,
      NodeHierarchyCommandRequest commandRequest,
      boolean reportsWereEvaluated) 
  throws 
      StaleDataException {

    // RP-11831: Ensure that any added raw points (for testing) have been persisted.
    AbstractCustomerEntity customer = portfolio.getParentCustomer();
    if (customer.hasUnpersistedRawPoints()) {

      Set<RawPointEntity> unpersistedRawPoints = new HashSet<>();
      for (RawPointEntity rawPoint: portfolio.getParentCustomer().getRawPoints()) {
        if (rawPoint.getPersistentIdentity() == null) {
          unpersistedRawPoints.add(rawPoint);
        }
      }
      if (!unpersistedRawPoints.isEmpty()) {
        rawPointRepository.storeRawPoints(portfolio.getParentCustomer().getPersistentIdentity(), unpersistedRawPoints);
      }
      customer.setHasUnpersistedRawPoints(false);
    }

    
    // Store the changes to the portfolio to the repository, with the results of what was done being returned.
    Map<String, List<AbstractNodeEntity>> nodes = nodeHierarchyRepository.storePortfolio(
        portfolio,
        commandRequest,
        reportsWereEvaluated);


    // Create, and publish, a node hierarchy change event that contains nodes that were inserted, 
    // updated or deleted (as well as any enabled/disabled AD function instances and report instances).
    List<AbstractNodeEntity> createdNodes = nodes.get(NodeHierarchyRepository.CREATED);
    List<AbstractNodeEntity> updatedNodes = nodes.get(NodeHierarchyRepository.UPDATED);
    List<AbstractNodeEntity> deletedNodes = nodes.get(NodeHierarchyRepository.DELETED);
    
    if (!createdNodes.isEmpty() || !updatedNodes.isEmpty() || !deletedNodes.isEmpty()) {

      NodeHierarchyChangeEvent event = publishRepositoryEvent(
          portfolio, 
          commandRequest, 
          nodes);
      
      // Store the event to the repository
      nodeHierarchyRepository.storeNodeHierarchyChangeEvent(event);
    }
    
    return nodes;    
  }
  
  // EVENT METHOD TO PUBLISH CHANGES.
  private NodeHierarchyChangeEvent publishRepositoryEvent(
      PortfolioEntity portfolio,
      NodeHierarchyCommandRequest commandRequest,
      Map<String, List<AbstractNodeEntity>> storePortfolioResultsMap) {

    Integer customerId = portfolio.getParentCustomer().getPersistentIdentity();
    Integer portfolioId = portfolio.getPersistentIdentity();
    
    String operationCategory = commandRequest.getOperationCategory();
    String operationType = commandRequest.getOperationType();
    String submittedBy = commandRequest.getSubmittedBy();

    List<AbstractNodeEntity> createdNodes = storePortfolioResultsMap.get(NodeHierarchyRepository.CREATED);
    List<Integer> createdNodeIds = new ArrayList<>();
    if (!createdNodes.isEmpty()) {
      
      for (AbstractNodeEntity createdNode: createdNodes) {
        
        Integer persistentIdentity = createdNode.getPersistentIdentity();
        if (persistentIdentity != null) {
        
          createdNodeIds.add(createdNode.getPersistentIdentity());
          
        } else {
          
          throw new IllegalStateException("Created node: ["
              + createdNode 
              + "] was never assigned a persistent identity.");
        }
      }
    }

    List<AbstractNodeEntity> updatedNodes = storePortfolioResultsMap.get(NodeHierarchyRepository.UPDATED);
    List<Integer> updatedNodeIds = new ArrayList<>();
    if (!updatedNodes.isEmpty()) {
      
      for (AbstractNodeEntity updatedNode: updatedNodes) {
        
        if (updatedNode instanceof PortfolioEntity == false) {
          updatedNodeIds.add(updatedNode.getPersistentIdentity());  
        }
      }
    }
    
    List<AbstractNodeEntity> deletedNodes = storePortfolioResultsMap.get(NodeHierarchyRepository.DELETED);
    List<Integer> deletedNodeIds = new ArrayList<>();
    if (!deletedNodes.isEmpty()) {
      
      for (AbstractNodeEntity deletedNode: deletedNodes) {
        
        deletedNodeIds.add(deletedNode.getPersistentIdentity());
      }
    }
    
    List<AbstractAdFunctionInstanceEntity> newlyCreatedAdFunctionInstances = portfolio.getNewlyCreatedAdFunctionInstances();
    List<Integer> newlyCreatedAdFunctionInstanceIds = new ArrayList<>();
    if (newlyCreatedAdFunctionInstances != null) {
      
      for (AbstractAdFunctionInstanceEntity entity: newlyCreatedAdFunctionInstances) {
        newlyCreatedAdFunctionInstanceIds.add(entity.getPersistentIdentity());
      }
    }
    portfolio.resetNewlyCreatedAdFunctionInstances();
    
    List<ReportInstanceEntity> newlyCreatedReportInstances = portfolio.getNewlyCreatedReportInstances();
    List<Integer> newlyCreatedReportInstanceIds = new ArrayList<>();
    if (newlyCreatedReportInstances != null) {
      
      for (ReportInstanceEntity entity: newlyCreatedReportInstances) {
        Integer reportInstanceId = entity.getPersistentIdentity();
        if (!newlyCreatedReportInstanceIds.contains(reportInstanceId)) {
          newlyCreatedReportInstanceIds.add(reportInstanceId);  
        }
      }
    }
    portfolio.resetNewlyCreatedReportInstances();

    List<Integer> newlyDisabledAdFunctionInstanceIds = portfolio.getNewlyDisabledAdFunctionInstanceIds();
    portfolio.resetNewlyDisabledAdFunctionInstanceIds();
    
    List<Integer> newlyDisabledReportInstanceIds = portfolio.getNewlyDisabledReportInstanceIds();
    portfolio.resetNewlyDisabledReportInstanceIds();

    // PUBLISH EVENT WITH PAYLOAD OF UPDATED NODE IDS.
    // (IN CASE WE EVER WANT TO IMPLEMENT CACHING, THIS CAN BE USED FOR INVALIDATION)
    Map<String, Object> payload = new LinkedHashMap<>();
    
    UUID eventUUID = UUID.randomUUID();
    Timestamp occurredOnDate = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
    
    payload.put(AbstractEvent.EVENT_UUID_KEY, eventUUID);
    payload.put(AbstractEvent.OCCURRED_ON_DATE_KEY, occurredOnDate);
    payload.put(AbstractEvent.OWNER_KEY, submittedBy);
    payload.put(NodeHierarchyChangeEvent.CUSTOMER_ID_KEY, customerId);
    payload.put(NodeHierarchyChangeEvent.PORTFOLIO_ID_KEY, portfolioId);
    payload.put(NodeHierarchyChangeEvent.OPERATION_TYPE_KEY, operationType);
    payload.put(NodeHierarchyChangeEvent.OPERATION_CATEGORY_KEY, operationCategory);
    payload.put(NodeHierarchyChangeEvent.CREATED_NODE_IDS_KEY, createdNodeIds);
    payload.put(NodeHierarchyChangeEvent.UPDATED_NODE_IDS_KEY, updatedNodeIds);
    payload.put(NodeHierarchyChangeEvent.DELETED_NODE_IDS_KEY, deletedNodeIds);
    payload.put(NodeHierarchyChangeEvent.ENABLED_AD_FUNCTION_INSTANCE_IDS_KEY, newlyCreatedAdFunctionInstanceIds);
    payload.put(NodeHierarchyChangeEvent.ENABLED_REPORT_INSTANCE_IDS_KEY, newlyCreatedReportInstanceIds);
    payload.put(NodeHierarchyChangeEvent.DISABLED_AD_FUNCTION_INSTANCE_IDS_KEY, newlyDisabledAdFunctionInstanceIds);
    payload.put(NodeHierarchyChangeEvent.DISABLED_REPORT_INSTANCE_IDS_KEY, newlyDisabledReportInstanceIds);
    
    publishEvent(payload);
    
    return NodeHierarchyChangeEvent
        .builder()
        .withEventUuid(eventUUID)
        .withOccurredOnDate(occurredOnDate)
        .withOwner(submittedBy)
        .withCustomerId(customerId)
        .withPortfolioId(portfolioId)
        .withOperationType(operationType)
        .withOperationCategory(operationCategory)
        .withCreatedNodeIds(createdNodeIds)
        .withUpdatedNodeIds(updatedNodeIds)
        .withDeletedNodeIds(deletedNodeIds)
        .withEnabledAdFunctionInstanceIds(newlyCreatedAdFunctionInstanceIds)
        .withEnabledReportInstanceIds(newlyCreatedReportInstanceIds)
        .withDisabledAdFunctionInstanceIds(newlyDisabledAdFunctionInstanceIds)
        .withDisabledReportInstanceIds(newlyDisabledReportInstanceIds)
        .build();
  }
  
  // EVENT BASED BEHAVIORS
  @Override
  public NodeHierarchyChangeEvent publishEvent(Map<String, Object> payload) {
    return eventPublisherDelegate.publishEvent(payload);
  }

  @Override
  public DictionaryChangeEvent publishDictionaryChangeEvent(String category) {
    return eventPublisherDelegate.publishDictionaryChangeEvent(category);
  }
  
  // GENERIC SERVICE INTERFACE
  @Override
  public NodeHierarchyCommandResponse processCommand(NodeHierarchyCommandRequest request) {

    if (request == null) {
      throw new IllegalStateException("request must be specified");
    }
    
    NodeHierarchyCommandResponse responseWrapper = null;
    Object response = null;
    
    try {
      
      if (request instanceof MapRawPointsRequest) {
        response = mapRawPoints((MapRawPointsRequest)request);
        
      } else if (request instanceof UnmapRawPointsRequest) {
        response = unmapRawPoints((UnmapRawPointsRequest)request);

      } else if (request instanceof IgnoreRawPointsRequest) {
        response = ignoreRawPoints((IgnoreRawPointsRequest)request);

      } else if (request instanceof UnignoreRawPointsRequest) {
        response = unignoreRawPoints((UnignoreRawPointsRequest)request);

      } else if (request instanceof UpdateBuildingNodesRequest) {
        response = updateBuildingNodes((UpdateBuildingNodesRequest)request);
        
      } else if (request instanceof UpdateEnergyExchangeSystemNodesRequest) {
        response = updateEnergyExchangeSystemNodes((UpdateEnergyExchangeSystemNodesRequest)request);

      } else if (request instanceof UpdateMappablePointNodesRequest) {
        response = updateMappablePointNodes((UpdateMappablePointNodesRequest)request);

      } else if (request instanceof CreateAdFunctionInstancesRequest) {
        response = createAdFunctionInstancesFromCandidates((CreateAdFunctionInstancesRequest)request);

      } else if (request instanceof UpdateAdFunctionInstancesRequest) {
        response = updateAdFunctionInstances((UpdateAdFunctionInstancesRequest)request);

      } else if (request instanceof DeleteAdFunctionInstancesRequest) {
        response = deleteAdFunctionInstances((DeleteAdFunctionInstancesRequest)request);

      } else if (request instanceof MoveChildNodesRequest) {
        response = moveChildNodesToNewParentNode((MoveChildNodesRequest)request);

      } else if (request instanceof DeleteChildNodesRequest) {
        response = deleteChildNodes((DeleteChildNodesRequest)request);

      } else if (request instanceof EvaluateReportsRequest) {
        response = evaluateReports((EvaluateReportsRequest)request);

      } else if (request instanceof FindAdFunctionInstanceCandidatesRequest) {
        response = findAdFunctionInstanceCandidates((FindAdFunctionInstanceCandidatesRequest)request);

      } else if (request instanceof UpdateReportInstancesRequest) {
        response = updateReportInstances((UpdateReportInstancesRequest)request);

      } else if (request instanceof ValidatePortfolioRequest) {
        response = validatePortfolio((ValidatePortfolioRequest)request);

      } else if (request instanceof RemediatePortfolioRequest) {
        response = remediatePortfolio((RemediatePortfolioRequest)request);
        
      } else if (request instanceof CreateNodeRequest) {
        response = createNode((CreateNodeRequest)request);
        
      } else if (request instanceof AddPointTemplateOverrideRequest) {
        response = addPointTemplateUnitMappingOverride((AddPointTemplateOverrideRequest)request);
        
      } else if (request instanceof RemovePointTemplateOverrideRequest) {
        response = removePointTemplateUnitMappingOverride((RemovePointTemplateOverrideRequest)request);
        
      }
      
      if (response != null) {
      
        responseWrapper = NodeHierarchyCommandResponse
            .builder()
            .withResult(Boolean.TRUE)
            .withResponseObject(response)
            .build();
        
      } else {

        responseWrapper = NodeHierarchyCommandResponse
            .builder()
            .withResult(Boolean.FALSE)
            .withResponseObject("Unsupported request: " + request)
            .build(); 
        
      }
            
    } catch (Exception e) {
      
      responseWrapper = NodeHierarchyCommandResponse
          .builder()
          .withResult(Boolean.FALSE)
          .withResponseObject(buildFailureReason(request, e))
          .build(); 
      
    }
    
    return responseWrapper;
  }
  
  private String buildFailureReason(
      NodeHierarchyCommandRequest request, 
      Exception e) {

    String reason = ExceptionUtils.extractReason(
        request.getOperationCategory()
        + " "
        + request.getOperationType()
        + " ERROR: ", e);
    LOGGER.error(reason, e);
    return reason;
  }
}
//@formatter:on