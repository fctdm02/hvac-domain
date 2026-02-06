//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.ModelServiceProvider;
import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.customer.enums.CustomerType;
import com.djt.hvac.domain.model.customer.service.CustomerService;
import com.djt.hvac.domain.model.dictionary.container.NodeTagTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.container.TagsContainer;
import com.djt.hvac.domain.model.dictionary.dto.PointTemplateAllAttributesDto;
import com.djt.hvac.domain.model.dictionary.energyexchange.AbstractEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.EquipmentEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.enums.EnergyExchangeSystemType;
import com.djt.hvac.domain.model.dictionary.enums.FunctionType;
import com.djt.hvac.domain.model.dictionary.enums.NodeSubType;
import com.djt.hvac.domain.model.dictionary.enums.NodeType;
import com.djt.hvac.domain.model.dictionary.service.DictionaryService;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.report.enums.ReportState;
import com.djt.hvac.domain.model.dictionary.weather.WeatherStationEntity;
import com.djt.hvac.domain.model.distributor.AbstractDistributorEntity;
import com.djt.hvac.domain.model.distributor.enums.DistributorType;
import com.djt.hvac.domain.model.distributor.service.DistributorService;
import com.djt.hvac.domain.model.function.AbstractAdFunctionInstanceEntity;
import com.djt.hvac.domain.model.function.AdFunctionEvaluator;
import com.djt.hvac.domain.model.nodehierarchy.AbstractNodeEntity;
import com.djt.hvac.domain.model.nodehierarchy.FloorEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingUtilityType;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.OperationType;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.UtilityComputationInterval;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EquipmentEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.AbstractPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.ScheduledAsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.CustomAsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.TemporalAsyncComputedPointConfigEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.enums.ComputationInterval;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.enums.FillPolicy;
import com.djt.hvac.domain.model.nodehierarchy.point.mapped.MappablePointEntity;
import com.djt.hvac.domain.model.nodehierarchy.service.NodeHierarchyService;
import com.djt.hvac.domain.model.nodehierarchy.service.command.CreateAdFunctionInstancesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.CreateNodeRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.EvaluateReportsRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.FindAdFunctionInstanceCandidatesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.MapRawPointsRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.NodeHierarchyCommandRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UpdateBuildingNodesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UpdateCustomAsyncComputedPointNodesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UpdateEnergyExchangeSystemNodesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UpdateMappablePointNodesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UpdateReportInstancesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.BuildingAddressData;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.BuildingTemporalData;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.BuildingUtilityData;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.CustomAsyncComputedPointNodeData;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.EnergyExchangeSystemNodeData;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.MappablePointNodeData;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.RawPointData;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.ReportInstanceData;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.UpdateBuildingNodeRequest;
import com.djt.hvac.domain.model.rawpoint.RawPointEntity;
import com.djt.hvac.domain.model.report.ReportInstanceEntity;
import com.djt.hvac.domain.model.report.ReportInstanceEquipmentErrorMessagesEntity;

/**
 * 
 * Used to create node hierarchies, in various states of configuration, for testing purposes.
 * </p>
 * The following options are available (the defaults are shown as well):
 * <pre>
     Integer numBuildings = Integer.valueOf(1): How many buildings to create.
     Integer numFloors = Integer.valueOf(0): How many floors to create equipment/points for.  Note that there will be a set with "rooftop" equipment metadata even if numFloors = 0.
     Integer numEquipmentPerEquipmentType = Integer.valueOf(1): How many pieces of equipment per equipment type to create.
     Integer numPointsPerEquipmentType = Integer.valueOf(1): How many points for a given point template, per qualifying equipment, to create.
     Set<String> equipmentTypeNames = new HashSet<>(Arrays.asList("ahu")): Which equipment types to use.  Empty/null means "all"
     Set<String> pointTemplateNames = new HashSet<>(Arrays.asList("ClgCmd", "HtgCmd", "DaFanSts")): Which point templates to use.  Empty/null means "all"
     Boolean performPointMapping = Boolean.TRUE: Whether or not to perform point mapping.
     Boolean performEquipmentTagging = Boolean.FALSE: Whether or not to perform equipment tagging (point mapping is pre-req) 
     Boolean performPointTagging = Boolean.FALSE: Whether or not to perform point tagging (point mapping and equipment tagging are pre-reqs)
     Boolean createCustomPoints = Boolean.FALSE: Whether or not to create a custom point associated with the building.
     Boolean createWeatherPoints = Boolean.FALSE: Whether or not to create an off prem weather station and associated weather points.
     Boolean createBuildingTemporalData = Boolean.FALSE: Whether or not to create building temporal data (all utilities).  Needed for baselines.
     Boolean createAdFunctionInstanceCandidates = Boolean.FALSE: Whether or not to perform AD function candidate creation (point mapping, equipment tagging and point tagging are pre-reqs)
     Boolean createAdFunctionInstances = Boolean.FALSE: Whether or not to perform AD function instance creation (point mapping, equipment tagging, point tagging and AD function candidate creation are pre-reqs)
     Boolean evaluateReports = Boolean.FALSE: Whether or not to perform report evaluation (point mapping, equipment tagging, point tagging, AD function candidate creation and AD function instance creation are pre-reqs)
     Boolean enableReports = Boolean.FALSE: Whether or not to enable eligible reports (should be all, point mapping, equipment tagging, point tagging, AD function candidate creation, AD function instance creation and report evaluation are pre-reqs)
 * <pre>
 * </p>
 * NOTE: An online distributor is created with an online child customer for the given node hierarchy.  Support for out of band will be done in the future.
 * </p>
 *  
 * @author tmyers
 *
 */
public class NodeHierarchyTestDataBuilder {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(NodeHierarchyTestDataBuilder.class);
  
  public static final Integer RESOLUTE_DISTRIBUTOR_ID = Integer.valueOf(1);
  
  public static String DEFAULT_DISTRIBUTOR_UNIT_SYSTEM = null;
  public static String DEFAULT_CUSTOMER_UNIT_SYSTEM = null;
  public static Integer NON_ZERO = Integer.valueOf(1);
  public static Integer ZERO = Integer.valueOf(0);
  
  private final ModelServiceProvider modelServiceProvider;

  /**
   * 
   * @param modelServiceProvider the <code>modelServiceProvider</code> to use
   */
  public NodeHierarchyTestDataBuilder(ModelServiceProvider modelServiceProvider) {
    this.modelServiceProvider = modelServiceProvider;
  }

  /**
   * 
   * @return The corresponding node hierarchy
   */
  public int createMinimalNodeHierarchyNoPointMapping() {
    
    int numBuildings = 1;
    int numFloors = 0;
    int numEquipmentPerEquipmentType = 1;
    int numPointsPerEquipmentType = 1;
    Set<String> equipmentTypeNames = new HashSet<>();
    equipmentTypeNames.add("vav");
    Set<String> pointTemplateNames = new HashSet<>();
    boolean performPointMapping = false;
    boolean performEquipmentTagging = false;
    boolean performPointTagging = false;
    boolean createCustomPoints = false;
    boolean createWeatherPoints = false;
    boolean createBuildingTemporalData = false;
    boolean createAdFunctionInstanceCandidates = false;
    boolean createAdFunctionInstances = false;
    boolean evaluateReports = false;
    boolean enableReports = false; 

    NodeHierarchyTestDataBuilderOptions nodeHierarchyTestDataBuilderOptions = NodeHierarchyTestDataBuilderOptions
        .builder()
        .withNumBuildings(numBuildings)
        .withNumFloors(numFloors) // At a minimum, there is a set of rooftop units.
        .withNumEquipmentPerEquipmentType(numEquipmentPerEquipmentType)
        .withNumPointsPerEquipmentType(numPointsPerEquipmentType)
        .withEquipmentTypeNames(equipmentTypeNames)
        .withPointTemplateNames(pointTemplateNames)
        .withPerformPointMapping(performPointMapping)
        .withPerformEquipmentTagging(performEquipmentTagging)
        .withPerformPointTagging(performPointTagging)
        .withCreateCustomPoints(createCustomPoints)
        .withCreateWeatherPoints(createWeatherPoints)
        .withCreateBuildingTemporalData(createBuildingTemporalData)
        .withCreateAdFunctionInstanceCandidates(createAdFunctionInstanceCandidates)
        .withCreateAdFunctionInstances(createAdFunctionInstances)
        .withEvaluateReports(evaluateReports)
        .withEnableReports(enableReports)
        .build();
        
    return createNodeHierarchy(nodeHierarchyTestDataBuilderOptions);
  }
  
  /**
   *
   * Creates a minimal node hierarchy with mappable points for DaTemp under an AHU, 
   * but no equipment or points are tagged.
   * 
   * </p>
   * Only one building is created.
   * 
   * @return The corresponding node hierarchy
   */
  public int createMinimalNodeHierarchyNoTagging() {
   
    return createMinimalNodeHierarchyNoTagging(1);
  }
  
  /**
   * 
   * Creates a minimal node hierarchy with mappable points for DaTemp under an AHU, 
   * but no equipment or points are tagged.  
   * 
   * @param numBuildings The number of buildings to create a minimal node hierarchy for
   * 
   * @return The corresponding node hierarchy
   */
  public int createMinimalNodeHierarchyNoTagging(int numBuildings) {
    
    int numFloors = 0;
    int numEquipmentPerEquipmentType = 1;
    int numPointsPerEquipmentType = 1;
    Set<String> equipmentTypeNames = new HashSet<>();
    equipmentTypeNames.add("ahu");
    Set<String> pointTemplateNames = new HashSet<>();
    pointTemplateNames.add("DaTemp");
    boolean performPointMapping = true;
    boolean performEquipmentTagging = false;
    boolean performPointTagging = false;
    boolean createCustomPoints = false;
    boolean createWeatherPoints = false;
    boolean createBuildingTemporalData = false;
    boolean createAdFunctionInstanceCandidates = false;
    boolean createAdFunctionInstances = false;
    boolean evaluateReports = false;
    boolean enableReports = false; 

    NodeHierarchyTestDataBuilderOptions nodeHierarchyTestDataBuilderOptions = NodeHierarchyTestDataBuilderOptions
        .builder()
        .withNumBuildings(numBuildings)
        .withNumFloors(numFloors) // At a minimum, there is a set of rooftop units.
        .withNumEquipmentPerEquipmentType(numEquipmentPerEquipmentType)
        .withNumPointsPerEquipmentType(numPointsPerEquipmentType)
        .withEquipmentTypeNames(equipmentTypeNames)
        .withPointTemplateNames(pointTemplateNames)
        .withPerformPointMapping(performPointMapping)
        .withPerformEquipmentTagging(performEquipmentTagging)
        .withPerformPointTagging(performPointTagging)
        .withCreateCustomPoints(createCustomPoints)
        .withCreateWeatherPoints(createWeatherPoints)
        .withCreateBuildingTemporalData(createBuildingTemporalData)
        .withCreateAdFunctionInstanceCandidates(createAdFunctionInstanceCandidates)
        .withCreateAdFunctionInstances(createAdFunctionInstances)
        .withEvaluateReports(evaluateReports)
        .withEnableReports(enableReports)
        .build();
        
    return createNodeHierarchy(nodeHierarchyTestDataBuilderOptions);
  }  
  
  /**
   * 
   * Creates a single building, no floors, 1 AHU and 1 child DaTemp. Nothing else.
   * 
   * @return The corresponding node hierarchy
   */
  public int createMinimalNodeHierarchy() {
    
    int numBuildings = 1;
    int numFloors = 0;
    int numEquipmentPerEquipmentType = 1;
    int numPointsPerEquipmentType = 1;
    Set<String> equipmentTypeNames = new HashSet<>();
    equipmentTypeNames.add("ahu");
    Set<String> pointTemplateNames = new HashSet<>();
    pointTemplateNames.add("DaTemp");
    boolean performPointMapping = true;
    boolean performEquipmentTagging = true;
    boolean performPointTagging = true;
    boolean createCustomPoints = false;
    boolean createWeatherPoints = false;
    boolean createBuildingTemporalData = false;
    boolean createAdFunctionInstanceCandidates = false;
    boolean createAdFunctionInstances = false;
    boolean evaluateReports = false;
    boolean enableReports = false; 

    NodeHierarchyTestDataBuilderOptions nodeHierarchyTestDataBuilderOptions = NodeHierarchyTestDataBuilderOptions
        .builder()
        .withNumBuildings(numBuildings)
        .withNumFloors(numFloors) // At a minimum, there is a set of rooftop units.
        .withNumEquipmentPerEquipmentType(numEquipmentPerEquipmentType)
        .withNumPointsPerEquipmentType(numPointsPerEquipmentType)
        .withEquipmentTypeNames(equipmentTypeNames)
        .withPointTemplateNames(pointTemplateNames)
        .withPerformPointMapping(performPointMapping)
        .withPerformEquipmentTagging(performEquipmentTagging)
        .withPerformPointTagging(performPointTagging)
        .withCreateCustomPoints(createCustomPoints)
        .withCreateWeatherPoints(createWeatherPoints)
        .withCreateBuildingTemporalData(createBuildingTemporalData)
        .withCreateAdFunctionInstanceCandidates(createAdFunctionInstanceCandidates)
        .withCreateAdFunctionInstances(createAdFunctionInstances)
        .withEvaluateReports(evaluateReports)
        .withEnableReports(enableReports)
        .build();
        
    return createNodeHierarchy(nodeHierarchyTestDataBuilderOptions);
  }

  /**
   *
   * Creates a single building, 1 floor, AHU and VAV equipment on roof/floor 1 with all eligible point templates for each equipment type.
   * Rule instances are created for qualifying rules.
   * 
   * @return The corresponding node hierarchy
   */
  public int createMinimalNodeHierarchyWithRules() {
    
    int numBuildings = 1;
    int numFloors = 0;
    int numEquipmentPerEquipmentType = 1;
    int numPointsPerEquipmentType = 1;
    Set<String> equipmentTypeNames = new HashSet<>();
    equipmentTypeNames.add("vav");
    Set<String> pointTemplateNames = new HashSet<>();
    pointTemplateNames.add("EffCoolSp");
    pointTemplateNames.add("EffHeatSp");
    boolean performPointMapping = true;
    boolean performEquipmentTagging = true;
    boolean performPointTagging = true;
    boolean createCustomPoints = false;
    boolean createWeatherPoints = false;
    boolean createBuildingTemporalData = false;
    boolean createAdFunctionInstanceCandidates = true;
    boolean createAdFunctionInstances = true;
    boolean evaluateReports = false;
    boolean enableReports = false;

    NodeHierarchyTestDataBuilderOptions nodeHierarchyTestDataBuilderOptions = NodeHierarchyTestDataBuilderOptions
        .builder()
        .withNumBuildings(numBuildings)
        .withNumFloors(numFloors) // At a minimum, there is a set of rooftop units.
        .withNumEquipmentPerEquipmentType(numEquipmentPerEquipmentType)
        .withNumPointsPerEquipmentType(numPointsPerEquipmentType)
        .withEquipmentTypeNames(equipmentTypeNames)
        .withPointTemplateNames(pointTemplateNames)
        .withPerformPointMapping(performPointMapping)
        .withPerformEquipmentTagging(performEquipmentTagging)
        .withPerformPointTagging(performPointTagging)
        .withCreateCustomPoints(createCustomPoints)
        .withCreateWeatherPoints(createWeatherPoints)
        .withCreateBuildingTemporalData(createBuildingTemporalData)
        .withCreateAdFunctionInstanceCandidates(createAdFunctionInstanceCandidates)
        .withCreateAdFunctionInstances(createAdFunctionInstances)
        .withEvaluateReports(evaluateReports)
        .withEnableReports(enableReports)
        .build();
        
    return createNodeHierarchy(nodeHierarchyTestDataBuilderOptions);
  }
  
  /**
   * Creates a fully configured node hierarchy with:
   * <ul>
   *   <li>1 building</li>
   *   <li>2 floors</li>
   *   <li>2 equipment per equipment type</li>
   *   <li>2 points per each point template per equipment type</li>
   *   <li>Point mapping, equipment tagging and point tagging are all performed</li>
   *   <li>1 custom point, off prem weather station and building temporal data is created at building level</li>
   *   <li>All AD function candidates are created into AD function instances</li>
   *   <li>All reports are evaluated and enabled</li>
   * </ul>
   * </p>
   * THERE SHOULD BE ZERO CONFIGURATION ERRORS FOR ALL REPORTS!
   * </p>
   * 
   * @return The corresponding node hierarchy
   */
  public int createFullyConfiguredNodeHierarchy() {
    
    try {
      return lifecycle();
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  /**
   * 
   * @param nodeHierarchyTestDataBuilderOptions The options to create for the node hierarchy.
   * 
   * @return The corresponding node hierarchy
   */
  public int createNodeHierarchy(NodeHierarchyTestDataBuilderOptions nodeHierarchyTestDataBuilderOptions) {
    
    try {
      int numBuildings = nodeHierarchyTestDataBuilderOptions.getNumBuildings();
      int numFloors = nodeHierarchyTestDataBuilderOptions.getNumFloors();
      int numEquipmentPerEquipmentType = nodeHierarchyTestDataBuilderOptions.getNumEquipmentPerEquipmentType();
      int numPointsPerEquipmentType = nodeHierarchyTestDataBuilderOptions.getNumPointsPerEquipmentType();
      Set<String> equipmentTypeNames = nodeHierarchyTestDataBuilderOptions.getEquipmentTypeNames();
      Set<String> pointTemplateNames = nodeHierarchyTestDataBuilderOptions.getPointTemplateNames();
      boolean performPointMapping = nodeHierarchyTestDataBuilderOptions.getPerformPointMapping();
      boolean performEquipmentTagging = nodeHierarchyTestDataBuilderOptions.getPerformEquipmentTagging();
      boolean performPointTagging = nodeHierarchyTestDataBuilderOptions.getPerformPointTagging();
      boolean createCustomPoints = nodeHierarchyTestDataBuilderOptions.getCreateCustomPoints();
      boolean createWeatherPoints = nodeHierarchyTestDataBuilderOptions.getCreateWeatherPoints();
      boolean createScheduledPoints = nodeHierarchyTestDataBuilderOptions.getCreateScheduledPoints();
      boolean createBuildingTemporalData = nodeHierarchyTestDataBuilderOptions.getCreateBuildingTemporalData();
      boolean createAdFunctionInstanceCandidates = nodeHierarchyTestDataBuilderOptions.getCreateAdFunctionInstanceCandidates();
      boolean createAdFunctionInstances = nodeHierarchyTestDataBuilderOptions.getCreateAdFunctionInstances();
      boolean evaluateReports = nodeHierarchyTestDataBuilderOptions.getEvaluateReports();
      boolean enableReports = nodeHierarchyTestDataBuilderOptions.getEnableReports();
      
      if (equipmentTypeNames == null) {
        equipmentTypeNames = new HashSet<>();
      }

      if (pointTemplateNames == null) {
        pointTemplateNames = new HashSet<>();
      }
      
      final Integer RESOLUTE_DISTRIBUTOR_ID = Integer.valueOf(1);
      
      DictionaryService dictionaryService = modelServiceProvider.getDictonaryService();
      DistributorService distributorService = modelServiceProvider.getDistributorService();
      CustomerService customerService = modelServiceProvider.getCustomerService();
      NodeHierarchyService nodeHierarchyService = modelServiceProvider.getNodeHierarchyService();
      TagsContainer tagsContainer = dictionaryService.getTagsContainer();
      Set<EquipmentEnergyExchangeTypeEntity> equipmentTypes = tagsContainer.getEquipmentTypes();
      NodeTagTemplatesContainer nodeTagTemplatesContainer = dictionaryService.getNodeTagTemplatesContainer();
      List<PointTemplateAllAttributesDto> allPointTemplateDtos = nodeTagTemplatesContainer.getPointTemplatesAllAttributes();
      Map<String, Object> additionalProperties = new HashMap<>();

      
      // ***********************************************
      // Create the distributor.
      String distributorName = "Test Distributor " + UUID.randomUUID().toString();
      AbstractDistributorEntity distributor = distributorService.createDistributor(
          RESOLUTE_DISTRIBUTOR_ID, 
          DistributorType.ONLINE,
          distributorName,
          DEFAULT_DISTRIBUTOR_UNIT_SYSTEM,
          false);

      
      // ***********************************************
      // Create the customer.
      String customerName = "Test Customer " + UUID.randomUUID().toString();
      AbstractCustomerEntity customer = customerService.createCustomer(
          distributor, 
          CustomerType.ONLINE,
          customerName,
          DEFAULT_CUSTOMER_UNIT_SYSTEM);
      Integer customerId = customer.getPersistentIdentity();
      
      
      // ***********************************************
      // Create the root portfolio node.
      PortfolioEntity portfolio = nodeHierarchyService.createPortfolio(
          customer, 
          customerName, 
          customerName);
      portfolio = nodeHierarchyService.loadPortfolio(customerId);
      

      // ***********************************************
      // Create the parent building.
      if (numBuildings < 1) {
        throw new IllegalArgumentException("numBuildings must be non-zero");
      }
      for (int buildingIdx=1; buildingIdx <= numBuildings; buildingIdx++) {
        
        String buildingName = "Test--Building";
        String buildingDisplayName = "Test Building";
        if (numBuildings > 1) {
          buildingName = buildingName + Integer.toString(buildingIdx);
          buildingDisplayName = buildingDisplayName + Integer.toString(buildingIdx);
        }

        CreateNodeRequest createNodeRequest = CreateNodeRequest
            .builder()
            .withCustomerId(customerId)
            .withNodeType(NodeType.BUILDING)
            .withParentId(null)
            .withName(buildingName)
            .withDisplayName(buildingDisplayName)
            .withAdditionalProperties(additionalProperties)
            .build();
        nodeHierarchyService.createNode(createNodeRequest);
        
        portfolio = nodeHierarchyService.loadPortfolio(customerId);
        BuildingEntity building = portfolio.getChildBuildingByName(buildingName);
        Integer buildingId = building.getPersistentIdentity();
        LOGGER.error("buildingId: "
            + buildingId
            + " name: "
            + building.getName());
        
        
        // ***********************************************
        // Update the parent building to have an address and associated weather station.
        if (createWeatherPoints) {
          
          UpdateBuildingNodesRequest updateBuildingNodesRequest = UpdateBuildingNodesRequest
              .builder()
              .withData(Arrays.asList(UpdateBuildingNodeRequest
                  .builder()
                  .withId(buildingId)
                  .withAddressData(BuildingAddressData
                      .builder()
                      .withRubyTimeZoneLabel("Central Time (US & Canada)")
                      .withAddress("875 N Michigan Ave")
                      .withCity("Chicago")
                      .withStateOrProvince("IL")
                      .withPostalCode("60611")
                      .withCountryCode("US")
                      .withLatitude("41.8988")
                      .withLongitude("87.6229")
                      .withWeatherStationId(Integer.valueOf(1)) // KDTW - Detroit Metro
                      .build())
                  .build()))
              .withCustomerId(customerId)
              .withSubmittedBy("tmyers@resolutebi.com")
              .withPerformAutomaticConfiguration(Boolean.TRUE)
              .withPerformAutomaticRemediation(Boolean.TRUE)
              .withPerformAutomaticEvaluateReports(Boolean.TRUE)
              .build();
          nodeHierarchyService.updateBuildingNodes(updateBuildingNodesRequest);
        }


        
        // ***********************************************
        // Update the parent building to have building temporal data.
        if (createBuildingTemporalData) {
          
          List<BuildingTemporalData> temporals = new ArrayList<>();
          List<BuildingUtilityData> utilities = new ArrayList<>();
          utilities.add(BuildingUtilityData
              .builder()
              .withUtilityId(Integer.valueOf(BuildingUtilityType.GAS.getId()))
              .withComputationIntervalId(Integer.valueOf(UtilityComputationInterval.DAILY.getId()))
              .withFormula("IF(AVG_DAILY_TEMP < 38.43)\n" + 
                  "    5.71 + 1.08 * (38.43 - AVG_DAILY_TEMP)\n" + 
                  "ELSE IF(AVG_DAILY_TEMP > 58)\n" + 
                  "    0.24\n" + 
                  "ELSE\n" + 
                  "    5.71 - 0.16 * (AVG_DAILY_TEMP - 38.43)")
              .withUtilityRate(Double.valueOf(8.449))
              .withBaselineDescription("The baseline was generated using 2018 data.")
              .withUserNotes("User Notes 1")
              .build());
          utilities.add(BuildingUtilityData
              .builder()
              .withUtilityId(Integer.valueOf(BuildingUtilityType.WATER.getId()))
              .withComputationIntervalId(Integer.valueOf(UtilityComputationInterval.MONTHLY_CALC_DAILY.getId()))
              .withFormula("IF(AVG_MONTHLY_TEMP <38.71)\n" + 
                  " ((272.03+3.56 * (38.71-AVG_MONTHLY_TEMP )) * ELAPSED_DAYS_IN_MONTH)/682512*(682512+44000+(0.3*88000))\n" + 
                  "ELSE\n" + 
                  " ((272.03-2.46*(AVG_MONTHLY_TEMP -38.71)) * ELAPSED_DAYS_IN_MONTH)/682512*(682512+44000+(0.3*88000))\n")
              .withUtilityRate(Double.valueOf(3.588))
              .withBaselineDescription("The baseline uses a change point linear regression model to correlate natural gas use per day with average monthly temperature. Baseline period is from mid-year 2015 to mid-year 2016.")
              .withUserNotes("/682512*(682512+44000+(0.3*88000)) added to account for the square footage adjustment in October 2019 (44,000 sqft of used space and 30% of the 88,000 sqft of shell space)")
              .build());
          utilities.add(BuildingUtilityData
              .builder()
              .withUtilityId(Integer.valueOf(BuildingUtilityType.ELECTRIC.getId()))
              .withComputationIntervalId(Integer.valueOf(UtilityComputationInterval.DAILY.getId()))
              .withFormula("IF (WEEK_DAY)\n" + 
                  "  IF(AVG_DAILY_TEMP < 47.20)\n" + 
                  "    14231.91 + 150.15 * (47.20 - AVG_DAILY_TEMP)\n" + 
                  "  ELSE\n" + 
                  "    14231.91 + 306.34 * (AVG_DAILY_TEMP - 47.20)\n" + 
                  "ELSE\n" + 
                  "  IF(AVG_DAILY_TEMP < 49.44)\n" + 
                  "    12903.21 + 113.87 * (49.44 - AVG_DAILY_TEMP)\n" + 
                  "  ELSE\n" + 
                  "    12903.21 + 308.35 * (AVG_DAILY_TEMP - 49.44)")
              .withUtilityRate(Double.valueOf(0.11))
              .withBaselineDescription("The baseline was generated using a change point regression model to correlate daily electric data (mid-year 2015 through mid-year 2016) and average daily temperature, for both weekdays and weekends.")
              .withUserNotes("User Notes 3")
              .build());
          temporals.add(BuildingTemporalData
              .builder()
              .withOperationType(OperationType.ADD)
              .withEffectiveDate("2018-01-01")
              .withSquareFeet(Integer.valueOf(10000))
              .withUtilities(utilities)
              .build());
          temporals.add(BuildingTemporalData
              .builder()
              .withOperationType(OperationType.ADD)
              .withEffectiveDate("2019-01-01")
              .withSquareFeet(Integer.valueOf(15000))
              .withUtilities(utilities)
              .build());
          UpdateBuildingNodesRequest updateBuildingNodesRequest = UpdateBuildingNodesRequest
              .builder()
              .withData(Arrays.asList(UpdateBuildingNodeRequest
                  .builder()
                  .withId(buildingId)
                  .withTemporalData(temporals)
                  .build()))
              .withCustomerId(customerId)
              .withSubmittedBy("tmyers@resolutebi.com")
              .withPerformAutomaticConfiguration(Boolean.TRUE)
              .withPerformAutomaticRemediation(Boolean.TRUE)
              .withPerformAutomaticEvaluateReports(Boolean.TRUE)
              .build();
          nodeHierarchyService.updateBuildingNodes(updateBuildingNodesRequest);
        }
        

        
        // ***********************************************
        // Create a scheduled async computed point for the building.
        if (createScheduledPoints) {
          
          additionalProperties.clear();
          additionalProperties.put("pointType", NodeSubType.SCHEDULED_ASYNC_COMPUTED_POINT);
          additionalProperties.put("pointTemplateId", Integer.valueOf(218));
          additionalProperties.put("scheduledEventTypeId", Integer.valueOf(1));
          createNodeRequest = CreateNodeRequest
              .builder()
              .withCustomerId(customerId)
              .withNodeType(NodeType.POINT)
              .withParentId(buildingId)
              .withName("ScheduledOccSt")
              .withDisplayName("ScheduledOccSt")
              .withAdditionalProperties(additionalProperties)
              .build();
          ScheduledAsyncComputedPointEntity scheduledPoint = (ScheduledAsyncComputedPointEntity)nodeHierarchyService.createNode(
              createNodeRequest);
          Integer scheduledPointId = scheduledPoint.getPersistentIdentity();
          LOGGER.error("scheduledPointId: " + scheduledPointId);
        }

        
        // ***********************************************
        // Create the floors
        if (numFloors < 0) {
          throw new IllegalArgumentException("numFloors must zero or greater");
        }
        for (int floorIdx=1; floorIdx <= numFloors; floorIdx++) {
          
          additionalProperties.clear();
          createNodeRequest = CreateNodeRequest
              .builder()
              .withCustomerId(customerId)
              .withNodeType(NodeType.FLOOR)
              .withParentId(buildingId)
              .withName("Floor--" + Integer.toString(floorIdx))
              .withDisplayName("Floor " + Integer.toString(floorIdx))
              .withAdditionalProperties(additionalProperties)
              .build();
          FloorEntity floor = (FloorEntity)nodeHierarchyService.createNode(
              createNodeRequest);
          LOGGER.error("floor: " + floor);
        }

        
        // ***********************************************
        // For every equipment type, create a piece of equipment for the roof that has
        // a point of every point template compatible for that equipment type.
        String mappingExpression = "/Drivers/NiagaraNetwork/{building}/{equipment}/{point}";
        List<RawPointEntity> rawPoints = new ArrayList<>();
        if (equipmentTypeNames.isEmpty() && pointTemplateNames.isEmpty()) {
          for (PointTemplateAllAttributesDto pointTemplateDto: allPointTemplateDtos) {
            
            if (!pointTemplateDto.getReferencedReportTemplates().trim().isEmpty() 
                || !pointTemplateDto.getReferencedAdFunctionTemplates().trim().isEmpty()) {
              
              if (!pointTemplateDto.getParentEnergyExchangeTypes().trim().isEmpty()) {
                String[] energyExchangeTypeNames = pointTemplateDto.getParentEnergyExchangeTypes().split(",");
                for (int i=0; i < energyExchangeTypeNames.length; i++) {
                  
                  String energyExchangeTypeName = energyExchangeTypeNames[i].trim();
                  AbstractEnergyExchangeTypeEntity energyExchangeType = tagsContainer.getEquipmentTypeByNameNullIfNotExists(energyExchangeTypeName);
                  if (energyExchangeType != null) {
                    
                    String pointTemplateName = pointTemplateDto.getName();
                    
                    String metricIdPattern = "/Drivers/NiagaraNetwork/"
                        + buildingName 
                        + "/Roof--<EQ-SEQ>--"
                        + energyExchangeTypeName
                        + "/Roof--<PT-SEQ>--"
                        + pointTemplateName;
                    
                    for (int j=1; j <= numPointsPerEquipmentType; j++) {

                      String metricId = metricIdPattern
                          .replace("<EQ-SEQ>", Integer.toString(i))
                          .replace("<PT-SEQ>", Integer.toString(j));
                      
                      rawPoints.add(buildMockRawPoint(customerId, metricId));          
                    }                    
                  }
                }
              }
            }
          }
        } else {
          for (EquipmentEnergyExchangeTypeEntity equipmentType: equipmentTypes) {
            
            String equipmentTypeName = equipmentType.getName();
            
            if (equipmentTypeNames.isEmpty() || equipmentTypeNames.contains(equipmentTypeName)) {

              Set<PointTemplateEntity> pointTemplates = nodeTagTemplatesContainer
                  .getEquipmentPointTemplatesForEquipmentType(equipmentType);

              for (int i=1; i <= numEquipmentPerEquipmentType; i++) {
                
                for (PointTemplateEntity pointTemplate: pointTemplates) {
                  
                  String pointTemplateName = pointTemplate.getName();
                  
                  if (pointTemplateNames.isEmpty() || pointTemplateNames.contains(pointTemplateName)) {

                    String metricIdPattern = "/Drivers/NiagaraNetwork/"
                        + buildingName 
                        + "/Roof--<EQ-SEQ>--"
                        + equipmentTypeName
                        + "/Roof--<PT-SEQ>--"
                        + pointTemplateName;
                    
                    for (int j=1; j <= numPointsPerEquipmentType; j++) {

                      String metricId = metricIdPattern
                          .replace("<EQ-SEQ>", Integer.toString(i))
                          .replace("<PT-SEQ>", Integer.toString(j));
                      
                      rawPoints.add(buildMockRawPoint(customerId, metricId));          
                    }
                  }
                }
              }
            }
          }
        }
        customer.addRawPoints(rawPoints);
        boolean storeRawPoints = true;
        customer = customerService.updateCustomer(customer, storeRawPoints);
        
        
        // ***********************************************
        // Map the raw points
        if (performPointMapping) {
          
          List<RawPointData> rawPointData = new ArrayList<>();
          for (RawPointEntity rp: rawPoints) {
            
            rawPointData.add(RawPointData
                .builder()
                .withRawPointId(rp.getPersistentIdentity())
                .withMetricId(rp.getMetricId())
                .build());
          }
          MapRawPointsRequest mapRawPointsRequest = MapRawPointsRequest
              .builder()
              .withCustomerId(customerId)
              .withRawPoints(rawPointData)
              .withMappingExpression(mappingExpression)
              .withMetricIdDelimiter(NodeHierarchyService.DEFAULT_METRIC_ID_DELIMITER)
              .build(); 
          List<MappablePointEntity> createdMappablePoints = nodeHierarchyService.mapRawPoints(mapRawPointsRequest);
          LOGGER.error("Number of points mapped: "
              + createdMappablePoints.size());
        }

        
        // ***********************************************
        // For every equipment type, create a piece of equipment for each floor that has
        // a point of every point template compatible for that equipment type.
        portfolio = nodeHierarchyService.loadPortfolio(customerId);
        building = portfolio.getChildBuilding(buildingId);
        rawPoints.clear();
        for (FloorEntity floor: building.getChildFloors()) {
          
          mappingExpression = "/Drivers/NiagaraNetwork/{building}/{floor}/{equipment}/{point}";
          
          if (equipmentTypeNames.isEmpty() && pointTemplateNames.isEmpty()) {
            
            for (PointTemplateAllAttributesDto pointTemplateDto: allPointTemplateDtos) {
              
              if (!pointTemplateDto.getReferencedReportTemplates().trim().isEmpty() 
                  || !pointTemplateDto.getReferencedAdFunctionTemplates().trim().isEmpty()) {
                
                if (!pointTemplateDto.getParentEnergyExchangeTypes().trim().isEmpty()) {
                  String[] energyExchangeTypeNames = pointTemplateDto.getParentEnergyExchangeTypes().split(",");
                  for (int i=0; i < energyExchangeTypeNames.length; i++) {
                    
                    String energyExchangeTypeName = energyExchangeTypeNames[i].trim();
                    AbstractEnergyExchangeTypeEntity energyExchangeType = tagsContainer.getEquipmentTypeByNameNullIfNotExists(energyExchangeTypeName);
                    if (energyExchangeType != null) {
                      
                      String pointTemplateName = pointTemplateDto.getName();
                      
                      String metricIdPattern = "/Drivers/NiagaraNetwork/" 
                          + buildingName 
                          + "/"
                          + floor.getName()
                          + "/" 
                          + floor.getName()
                          + "--<EQ-SEQ>--"
                          + energyExchangeTypeName
                          + "/"
                          + floor.getName()
                          + "--<PT-SEQ>--"
                          + pointTemplateName;
                      
                      for (int j=1; j <= numPointsPerEquipmentType; j++) {

                        String metricId = metricIdPattern
                            .replace("<EQ-SEQ>", Integer.toString(i))
                            .replace("<PT-SEQ>", Integer.toString(j));
                        
                        rawPoints.add(buildMockRawPoint(customerId, metricId));          
                      }                      
                    }
                  }
                }
              }
            }
            
          } else {
            for (EquipmentEnergyExchangeTypeEntity equipmentType: equipmentTypes) {
              
              String equipmentTypeName = equipmentType.getName();
              
              if (equipmentTypeNames.isEmpty() || equipmentTypeNames.contains(equipmentTypeName)) {

                Set<PointTemplateEntity> pointTemplates = dictionaryService
                    .getNodeTagTemplatesContainer()
                    .getEquipmentPointTemplatesForEquipmentType(equipmentType);
                
                for (int i=0; i < numEquipmentPerEquipmentType; i++) {

                  for (PointTemplateEntity pointTemplate: pointTemplates) {
                    
                    String pointTemplateName = pointTemplate.getName();
                    
                    if (pointTemplateNames.isEmpty() || pointTemplateNames.contains(pointTemplateName)) {
                      
                      String metricIdPattern = "/Drivers/NiagaraNetwork/" 
                          + buildingName 
                          + "/"
                          + floor.getName()
                          + "/" 
                          + floor.getName()
                          + "--<EQ-SEQ>--"
                          + equipmentTypeName
                          + "/"
                          + floor.getName()
                          + "--<PT-SEQ>--"
                          + pointTemplateName;
                      
                      for (int j=1; j <= numPointsPerEquipmentType; j++) {

                        String metricId = metricIdPattern
                            .replace("<EQ-SEQ>", Integer.toString(i))
                            .replace("<PT-SEQ>", Integer.toString(j));
                        
                        rawPoints.add(buildMockRawPoint(customerId, metricId));          
                      }
                    }
                  }
                }
              }
            }
          }
          customer.addRawPoints(rawPoints);
          storeRawPoints = true;
          customer = customerService.updateCustomer(customer, storeRawPoints);
          
          if (performPointMapping) {

            // ***********************************************
            // Map the raw points
            List<RawPointData> rawPointData = new ArrayList<>();
            for (RawPointEntity rp: rawPoints) {
              
              rawPointData.add(RawPointData
                  .builder()
                  .withRawPointId(rp.getPersistentIdentity())
                  .withMetricId(rp.getMetricId())
                  .build());
            }
            MapRawPointsRequest mapRawPointsRequest = MapRawPointsRequest
                .builder()
                .withCustomerId(customerId)
                .withRawPoints(rawPointData)
                .withMappingExpression(mappingExpression)
                .withMetricIdDelimiter(NodeHierarchyService.DEFAULT_METRIC_ID_DELIMITER)
                .build(); 
            List<MappablePointEntity> createdMappablePoints = nodeHierarchyService.mapRawPoints(mapRawPointsRequest);
            
            LOGGER.error("Number of points mapped for floor: "
                + floor
                + ": "
                + createdMappablePoints.size());
          }
        }
        portfolio = nodeHierarchyService.loadPortfolio(customerId);
        int totalMappedPointCount = portfolio.getTotalMappedPointCount();
        LOGGER.error("totalMappedPointCount: " + totalMappedPointCount);
        
        
        if (performEquipmentTagging) {
          
          // ***********************************************
          // Now that all the points have been mapped, perform equipment tagging for the building (roof units).
          portfolio = nodeHierarchyService.loadPortfolio(customerId);
          building = portfolio.getChildBuilding(buildingId);
          List<String> equipmentMetadataTags = Arrays.asList("rooftop");
          List<EnergyExchangeSystemNodeData> updateEquipmentNodeRequestList = new ArrayList<>();
          Map<EquipmentEnergyExchangeTypeEntity, Integer> buildingEquipmentTypesToParentEquipmentMap = new HashMap<>();
          for (EquipmentEntity equipment: building.getChildEquipment()) {
            
            String equipmentName = equipment.getName();
            if (!equipmentName.startsWith(BuildingEntity.OFF_PREM_WEATH_STATION_EQUIP_NAME)) {

              int idx = equipment.getName().lastIndexOf("--")+2;
              String equipmentTypeName = equipment.getName().substring(idx);
              EquipmentEnergyExchangeTypeEntity equipmentType = tagsContainer.getEquipmentTypeByName(equipmentTypeName.toLowerCase());
              buildingEquipmentTypesToParentEquipmentMap.put(equipmentType, equipment.getPersistentIdentity());
              updateEquipmentNodeRequestList.add(EnergyExchangeSystemNodeData
                  .builder()
                  .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
                  .withId(equipment.getPersistentIdentity())
                  .withTypeId(equipmentType.getPersistentIdentity())
                  .withDisplayName("R--" + equipmentTypeName)
                  .withMetadataTags(equipmentMetadataTags)
                  .build());
            }
          }
          UpdateEnergyExchangeSystemNodesRequest updateEquipmentNodesRequest = UpdateEnergyExchangeSystemNodesRequest
              .builder()
              .withCustomerId(customerId)
              .withData(updateEquipmentNodeRequestList)
              .build(); 
          nodeHierarchyService.updateEnergyExchangeSystemNodes(updateEquipmentNodesRequest);


          // ***********************************************
          // Now that all the points have been mapped, perform equipment tagging for the equipment on the floors.
          portfolio = nodeHierarchyService.loadPortfolio(customerId);
          building = portfolio.getChildBuilding(buildingId);
          for (FloorEntity floor: building.getChildFloors()) {

            updateEquipmentNodeRequestList = new ArrayList<>();
            for (EquipmentEntity equipment: floor.getChildEquipment()) {
              
              int idx = equipment.getName().lastIndexOf("--")+2;
              String equipmentTypeName = equipment.getName().substring(idx);
              EquipmentEnergyExchangeTypeEntity equipmentType = tagsContainer.getEquipmentTypeByName(equipmentTypeName.toLowerCase());
              Integer parentId = buildingEquipmentTypesToParentEquipmentMap.get(equipmentType);
              updateEquipmentNodeRequestList.add(EnergyExchangeSystemNodeData
                  .builder()
                  .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
                  .withId(equipment.getPersistentIdentity())
                  .withTypeId(equipmentType.getPersistentIdentity())
                  .withDisplayName("F" 
                      + Integer.toString(floor.getFloorOrdinal()) 
                      + "--" + equipmentTypeName)
                  .withSystemTypeId(EnergyExchangeSystemType.AIR_SUPPLY.getId())
                  .withParentIds(Arrays.asList(parentId))
                  .build());
            }
            updateEquipmentNodesRequest = UpdateEnergyExchangeSystemNodesRequest
                .builder()
                .withCustomerId(customerId)
                .withData(updateEquipmentNodeRequestList)
                .build(); 
            nodeHierarchyService.updateEnergyExchangeSystemNodes(updateEquipmentNodesRequest);
          }

          if (performPointTagging) {

            // ***********************************************
            // Perform point tagging for the building (roof units).
            portfolio = nodeHierarchyService.loadPortfolio(customerId);
            building = portfolio.getChildBuilding(buildingId);
            Boolean useGrouping = Boolean.FALSE;
            List<MappablePointNodeData> updateMappablePointNodeRequestList = new ArrayList<>();
            for (EquipmentEntity equipment: building.getChildEquipment()) {
              
              for (AbstractPointEntity point: equipment.getChildPoints()) {
                
                if (point instanceof MappablePointEntity) {

                  int idx = point.getName().lastIndexOf("--")+2;
                  String pointTemplateName = point.getDisplayName().substring(idx);
                  PointTemplateEntity pointTemplate = nodeTagTemplatesContainer.getPointTemplateByName(pointTemplateName);
                  //LOGGER.error("ROOF: pointTemplateId: " + pointTemplate.getPersistentIdentity() + " equipmentId: " + equipment.getPersistentIdentity() + ", pointId: " + point.getPersistentIdentity());
                  updateMappablePointNodeRequestList.add(MappablePointNodeData
                      .builder()
                      .withId(point.getPersistentIdentity())
                      .withDisplayName("R--" + pointTemplateName)
                      .withPointTemplateId(pointTemplate.getPersistentIdentity())
                      .withUnitId(pointTemplate.getUnit().getPersistentIdentity())
                      .withPointDataTypeId(point.getDataType().getId())
                      .build());
                }
              }
            }
            UpdateMappablePointNodesRequest updateMappablePointNodesRequest = UpdateMappablePointNodesRequest
                .builder()
                .withCustomerId(customerId)
                .withUseGrouping(useGrouping)
                .withData(updateMappablePointNodeRequestList)
                .build();
            nodeHierarchyService.updateMappablePointNodes(updateMappablePointNodesRequest);
            

            // ***********************************************
            // Perform point tagging for the points on the floors.
            portfolio = nodeHierarchyService.loadPortfolio(customerId);
            building = portfolio.getChildBuilding(buildingId);
            for (FloorEntity floor: building.getChildFloors()) {

              updateMappablePointNodeRequestList.clear();
              for (EquipmentEntity equipment: floor.getChildEquipment()) {
                
                for (AbstractPointEntity point: equipment.getChildPoints()) {
                  
                  int idx = point.getName().lastIndexOf("--")+2;
                  String pointTemplateName = point.getDisplayName().substring(idx);
                  PointTemplateEntity pointTemplate = nodeTagTemplatesContainer.getPointTemplateByName(pointTemplateName);
                  //LOGGER.error("FLOOR 1: pointTemplateId: " + pointTemplate.getPersistentIdentity() + " equipmentId: " + equipment.getPersistentIdentity() + ", pointId: " + point.getPersistentIdentity());
                  updateMappablePointNodeRequestList.add(MappablePointNodeData
                      .builder()
                      .withId(point.getPersistentIdentity())
                      .withDisplayName("F1--" + pointTemplateName)
                      .withPointTemplateId(pointTemplate.getPersistentIdentity())
                      .withUnitId(pointTemplate.getUnit().getPersistentIdentity())
                      .withPointDataTypeId(point.getDataType().getId())
                      .build());
                }
              }
              updateMappablePointNodesRequest = UpdateMappablePointNodesRequest
                  .builder()
                  .withCustomerId(customerId)
                  .withUseGrouping(useGrouping)
                  .withData(updateMappablePointNodeRequestList)
                  .build();
              nodeHierarchyService.updateMappablePointNodes(updateMappablePointNodesRequest);
            }
            
            
            // ***********************************************
            // For every equipment, create a custom async computed point1, whose variables are the 
            // child mappable points and whose formula is simply the sum of them.
            if (createCustomPoints) {
            
              portfolio = nodeHierarchyService.loadPortfolio(customerId);
              building = portfolio.getChildBuilding(buildingId);
              for (EquipmentEntity equipment: building.getChildEquipment()) {
                
                EquipmentEnergyExchangeTypeEntity equipmentType = equipment.getEquipmentTypeNullIfNotExists();
                
                Set<PointTemplateEntity> pointTemplates = dictionaryService
                    .getNodeTagTemplatesContainer()
                    .getEquipmentPointTemplatesForEquipmentType(equipmentType);
                
                additionalProperties.clear();
                additionalProperties.put("pointType", NodeSubType.CUSTOM_ASYNC_COMPUTED_POINT);
                
                if (!pointTemplates.isEmpty()) {
                  for (PointTemplateEntity pointTemplate: pointTemplates) {

                    additionalProperties.put("pointTemplateId", pointTemplate.getPersistentIdentity());
                    additionalProperties.put("unitId", pointTemplate.getUnit().getPersistentIdentity());
                    additionalProperties.put("metricId", equipment.getNodePath() + "/ " + "Custom" + pointTemplate.getName() + "/" + UUID.randomUUID().toString());
                  }
                } else {
                  throw new IllegalStateException("No point templates are defined for energy exchange type: " + equipmentType);
                }
                
                additionalProperties.put("computationInterval", ComputationInterval.QUARTER_HOUR.getName());

                // NOTE: One can create multiple temporal configs, hence the array list below.
                List<Map<String, Object>> childTemporalConfigs = new ArrayList<>();
                additionalProperties.put("childTemporalConfigs", childTemporalConfigs);
                int effectiveDateYear = 2018;
                for (int i=0; i < 2; i++) {
                  
                  Map<String, Object> childTemporalConfigMap = new HashMap<>();
                  
                  List<Map<String, Object>> variables = new ArrayList<>();
                  
                  StringBuilder formulaSb = new StringBuilder();
                  List<AbstractPointEntity> points = new ArrayList<>();
                  points.addAll(equipment.getChildPoints());
                  for (int j=0; j< equipment.getChildPoints().size(); j++) {
                    
                    AbstractPointEntity point = points.get(j);
                    
                    if (point instanceof MappablePointEntity) {

                      Map<String, Object> variable = new HashMap<>();
                      variables.add(variable);
                      
                      String variableName = "$" + Integer.toString(j+1);
                      
                      variable.put("pointId", point.getPersistentIdentity());
                      variable.put("fillPolicyId", FillPolicy.LAST_KNOWN.getId());
                      variable.put("name", variableName);
                      
                      formulaSb.append(variableName);
                      if (j < points.size()-1) {
                      
                        formulaSb.append(" + ");
                      }
                    }
                  }
                  
                  String effectiveDate = Integer.toString(effectiveDateYear++) + "-01-01";

                  childTemporalConfigMap.put("effectiveDate", effectiveDate); 
                  
                  childTemporalConfigMap.put("description", buildingName + " formula description");
                  
                  childTemporalConfigMap.put("formula", formulaSb.toString());
                  
                  childTemporalConfigMap.put("childVariables", variables);
                  
                  childTemporalConfigs.add(childTemporalConfigMap);
                }
                
                createNodeRequest = CreateNodeRequest
                    .builder()
                    .withCustomerId(customerId)
                    .withNodeType(NodeType.POINT)
                    .withParentId(equipment.getPersistentIdentity())
                    .withName(buildingName)
                    .withDisplayName(buildingName)
                    .withAdditionalProperties(additionalProperties)
                    .build();
                nodeHierarchyService.createNode(createNodeRequest);
              }
            }
          }  
        }      
      }

      
      // ***********************************************
      // Now that all buildings/floors/equipment/points have been created/tagged
      if (!createAdFunctionInstanceCandidates) {
        return customerId;
      }
      // ***********************************************
      // Perform AD computed point function instance candidate finding.
      FindAdFunctionInstanceCandidatesRequest findAdFunctionInstanceCandidatesRequest = FindAdFunctionInstanceCandidatesRequest
          .builder()
          .withCustomerId(customerId)
          .withFunctionType(FunctionType.COMPUTED_POINT)
          .build();
      nodeHierarchyService.findAdFunctionInstanceCandidates(findAdFunctionInstanceCandidatesRequest);

      
      // ***********************************************
      // Perform AD rule function instance candidate finding.
      findAdFunctionInstanceCandidatesRequest = FindAdFunctionInstanceCandidatesRequest
          .builder()
          .withCustomerId(customerId)
          .withFunctionType(FunctionType.RULE)
          .build();
      nodeHierarchyService.findAdFunctionInstanceCandidates(findAdFunctionInstanceCandidatesRequest);
      
      
      if (!createAdFunctionInstances) {
        return customerId;
      }
      // ***********************************************
      // Create AD computed point function instances from candidates.
      List<AbstractAdFunctionInstanceEntity> computedPointCandidates = portfolio.getAllAdFunctionInstanceCandidates(
          FunctionType.COMPUTED_POINT);
      List<Integer> candidateIds = new ArrayList<>();
      for (AbstractAdFunctionInstanceEntity candidate: computedPointCandidates) {
        candidateIds.add(candidate.getPersistentIdentity());
      }
      CreateAdFunctionInstancesRequest createRequest = CreateAdFunctionInstancesRequest
          .builder()
          .withCustomerId(customerId)
          .withFunctionType(NodeHierarchyCommandRequest.COMPUTED_POINT)
          .withCandidateIds(candidateIds)
          .build();
      nodeHierarchyService.createAdFunctionInstancesFromCandidates(createRequest);
      
      
      // ***********************************************
      // Create AD rule function instances from candidates.
      List<AbstractAdFunctionInstanceEntity> ruleCandidates = portfolio.getAllAdFunctionInstanceCandidates(
          FunctionType.RULE);
      candidateIds = new ArrayList<>();
      for (AbstractAdFunctionInstanceEntity candidate: ruleCandidates) {
        candidateIds.add(candidate.getPersistentIdentity());
      }
      createRequest = CreateAdFunctionInstancesRequest
          .builder()
          .withCustomerId(customerId)
          .withFunctionType(NodeHierarchyCommandRequest.RULE)
          .withCandidateIds(candidateIds)
          .build();
      nodeHierarchyService.createAdFunctionInstancesFromCandidates(createRequest);

      
      if (!evaluateReports) {
        return customerId;
      }
      // ***********************************************
      // Evaluate all reports.
      EvaluateReportsRequest evaluateReportsRequest = EvaluateReportsRequest
          .builder()
          .withCustomerId(customerId)
          .build();
      nodeHierarchyService.evaluateReports(evaluateReportsRequest);

      
      if (!enableReports) {
        return customerId;
      }
      // ***********************************************
      // Enable all reports.
      for (BuildingEntity childBuilding: portfolio.getChildBuildings()) {
      
        List<ReportInstanceData> reportData = new ArrayList<>();
        for (ReportInstanceEntity reportInstance: childBuilding.getReportInstances()) {
          
          reportData.add(ReportInstanceData
              .builder()
              .withBuildingId(childBuilding.getPersistentIdentity())
              .withReportTemplateId(reportInstance.getReportTemplate().getPersistentIdentity())
              .withState(ReportState.ENABLED.toString())
              .build());
        }
        nodeHierarchyService.updateReportInstances(UpdateReportInstancesRequest
            .builder()
            .withCustomerId(customerId)
            .withBuildingId(childBuilding.getPersistentIdentity())
            .withData(reportData)
            .build());
      }
      
      return customerId;
    } catch (Exception e) {
      throw new RuntimeException("Unable to create node hierarchy with the following options: " + nodeHierarchyTestDataBuilderOptions, e);
    }
  }
  
  public static RawPointEntity buildMockRawPoint(int customerId, String metricId) {
    
    return buildMockRawPoint(
        customerId,
        999,
        metricId);     
  }  
  
  public static RawPointEntity buildMockRawPoint(int customerId, int componentId, String metricId) {
    
    //LOGGER.error("Created raw point: " + metricId);
    return new RawPointEntity(
        null,
        customerId,
        Integer.valueOf(componentId),
        metricId,
        "NumericPoint",
        "",
        "kWh",
        Boolean.FALSE,
        Boolean.FALSE,
        null);     
  }
  
  private int lifecycle() throws Exception {

    // ***********************************************
    // Set up everything.
    AdFunctionEvaluator.INCLUDE_BETA_FUNCTION_TEMPLATES = true;
    DictionaryService dictionaryService = modelServiceProvider.getDictonaryService();
    DistributorService distributorService = modelServiceProvider.getDistributorService();
    CustomerService customerService = modelServiceProvider.getCustomerService();
    NodeHierarchyService nodeHierarchyService = modelServiceProvider.getNodeHierarchyService();
    TagsContainer tagsContainer = dictionaryService.getTagsContainer();
    NodeTagTemplatesContainer nodeTagTemplatesContainer = dictionaryService.getNodeTagTemplatesContainer();
    List<PointTemplateAllAttributesDto> allPointTemplateDtos = nodeTagTemplatesContainer.getPointTemplatesAllAttributes();
    Map<String, Object> additionalProperties = new HashMap<>();

    
    // ***********************************************
    // Create the distributor.
    String distributorName = "Test Distributor " + UUID.randomUUID().toString();
    AbstractDistributorEntity distributor = distributorService.createDistributor(
        RESOLUTE_DISTRIBUTOR_ID, 
        DistributorType.ONLINE,
        distributorName,
        DEFAULT_DISTRIBUTOR_UNIT_SYSTEM,
        false);

    
    // ***********************************************
    // Create the customer.
    String customerName = "Test Customer " + UUID.randomUUID().toString();
    AbstractCustomerEntity customer = customerService.createCustomer(
        distributor, 
        CustomerType.ONLINE,
        customerName,
        DEFAULT_CUSTOMER_UNIT_SYSTEM);
    Integer customerId = customer.getPersistentIdentity();
    
    
    // ***********************************************
    // Create the root portfolio node.
    PortfolioEntity portfolio = nodeHierarchyService.createPortfolio(
        customer, 
        customerName, 
        customerName);
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    
    
    // ***********************************************
    // Create the parent building.
    CreateNodeRequest createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.BUILDING)
        .withParentId(null)
        .withName("Test__Building")
        .withDisplayName("Test Building")
        .withAdditionalProperties(additionalProperties)
        .build();
    BuildingEntity building = (BuildingEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer buildingId = building.getPersistentIdentity();
    LOGGER.debug("building: " + building);
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    customerId = portfolio.getCustomerId();
    building = portfolio.getChildBuilding(buildingId);
    
   
    // ***********************************************
    // Update the parent building to have an address, associated weather station and ADD building temporal data.
    List<BuildingTemporalData> temporals = new ArrayList<>();
    List<BuildingUtilityData> utilities = new ArrayList<>();
    utilities.add(BuildingUtilityData
        .builder()
        .withUtilityId(Integer.valueOf(BuildingUtilityType.ELECTRIC.getId()))
        .withComputationIntervalId(Integer.valueOf(UtilityComputationInterval.DAILY.getId()))
        .withFormula("IF (WEEK_DAY)\n" + 
            "  IF(AVG_DAILY_TEMP < 47.20)\n" + 
            "    14231.91 + 150.15 * (47.20 - AVG_DAILY_TEMP)\n" + 
            "  ELSE\n" + 
            "    14231.91 + 306.34 * (AVG_DAILY_TEMP - 47.20)\n" + 
            "ELSE\n" + 
            "  IF(AVG_DAILY_TEMP < 49.44)\n" + 
            "    12903.21 + 113.87 * (49.44 - AVG_DAILY_TEMP)\n" + 
            "  ELSE\n" + 
            "    12903.21 + 308.35 * (AVG_DAILY_TEMP - 49.44)")
        .withUtilityRate(Double.valueOf(0.11))
        .withBaselineDescription("The baseline was generated using a change point regression model to correlate daily electric data (mid-year 2015 through mid-year 2016) and average daily temperature, for both weekdays and weekends.")
        .withUserNotes("User Notes 1")
        .build());
    utilities.add(BuildingUtilityData
        .builder()
        .withUtilityId(Integer.valueOf(BuildingUtilityType.GAS.getId()))
        .withComputationIntervalId(Integer.valueOf(UtilityComputationInterval.DAILY.getId()))
        .withFormula("IF(AVG_DAILY_TEMP < 38.43)\n" + 
            "    5.71 + 1.08 * (38.43 - AVG_DAILY_TEMP)\n" + 
            "ELSE IF(AVG_DAILY_TEMP > 58)\n" + 
            "    0.24\n" + 
            "ELSE\n" + 
            "    5.71 - 0.16 * (AVG_DAILY_TEMP - 38.43)")
        .withUtilityRate(Double.valueOf(8.449))
        .withBaselineDescription("The baseline was generated using 2018 data.")
        .withUserNotes("User Notes 2")
        .build());
    utilities.add(BuildingUtilityData
        .builder()
        .withUtilityId(Integer.valueOf(BuildingUtilityType.WATER.getId()))
        .withComputationIntervalId(Integer.valueOf(UtilityComputationInterval.MONTHLY_CALC_DAILY.getId()))
        .withFormula("IF(AVG_MONTHLY_TEMP <38.71)\n" + 
            " ((272.03+3.56 * (38.71-AVG_MONTHLY_TEMP )) * ELAPSED_DAYS_IN_MONTH)/682512*(682512+44000+(0.3*88000))\n" + 
            "ELSE\n" + 
            " ((272.03-2.46*(AVG_MONTHLY_TEMP -38.71)) * ELAPSED_DAYS_IN_MONTH)/682512*(682512+44000+(0.3*88000))\n")
        .withUtilityRate(Double.valueOf(3.588))
        .withBaselineDescription("The baseline uses a change point linear regression model to correlate natural gas use per day with average monthly temperature. Baseline period is from mid-year 2015 to mid-year 2016.")
        .withUserNotes("/682512*(682512+44000+(0.3*88000)) added to account for the square footage adjustment in October 2019 (44,000 sqft of used space and 30% of the 88,000 sqft of shell space)")
        .build());
    temporals.add(BuildingTemporalData
        .builder()
        .withOperationType(OperationType.ADD)
        .withEffectiveDate("2018-01-01")
        .withSquareFeet(Integer.valueOf(10000))
        .withUtilities(utilities)
        .build());
    temporals.add(BuildingTemporalData
        .builder()
        .withOperationType(OperationType.ADD)
        .withEffectiveDate("2019-01-01")
        .withSquareFeet(Integer.valueOf(15000))
        .withUtilities(utilities)
        .build());
    UpdateBuildingNodesRequest updateBuildingNodesRequest = UpdateBuildingNodesRequest
        .builder()
        .withData(Arrays.asList(UpdateBuildingNodeRequest
            .builder()
            .withId(buildingId)
            .withAddressData(BuildingAddressData
                .builder()
                .withRubyTimeZoneLabel("Central Time (US & Canada)")
                .withAddress("875 N Michigan Ave")
                .withCity("Chicago")
                .withStateOrProvince("IL")
                .withPostalCode("60611")
                .withCountryCode("US")
                .withLatitude("41.8988")
                .withLongitude("87.6229")
                .withWeatherStationId(Integer.valueOf(1)) // KDTW - Detroit Metro
                .build())
            .withTemporalData(temporals)
            .build()))
        .withCustomerId(customerId)
        .withSubmittedBy("tmyers@resolutebi.com")
        .withPerformAutomaticConfiguration(Boolean.TRUE)
        .withPerformAutomaticRemediation(Boolean.TRUE)
        .withPerformAutomaticEvaluateReports(Boolean.TRUE)
        .build();
    List<BuildingEntity> updatedBuildings = nodeHierarchyService.updateBuildingNodes(updateBuildingNodesRequest);
    assertEquals("updatedBuildings size is incorrect", 
        "1",
        Integer.toString(updatedBuildings.size()));
    boolean loadReportInstances = true;
    boolean loadBuildingTemporalData = true;
    portfolio = nodeHierarchyService.loadPortfolio(LoadPortfolioOptions
        .builder()
        .withCustomerId(customerId)
        .withLoadReportInstances(loadReportInstances)
        .withLoadBuildingTemporalData(loadBuildingTemporalData)
        .build());
    customerId = portfolio.getCustomerId();
    building = portfolio.getChildBuilding(buildingId);
    WeatherStationEntity weatherStation = building.getWeatherStation();
    assertNotNull("weatherStation is null", weatherStation);
    
    
    // ***********************************************
    // Create a scheduled async computed point for the building.
    additionalProperties.clear();
    additionalProperties.put("pointType", NodeSubType.SCHEDULED_ASYNC_COMPUTED_POINT);
    additionalProperties.put("pointTemplateId", Integer.valueOf(218));
    additionalProperties.put("scheduledEventTypeId", Integer.valueOf(1));
    createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.POINT)
        .withParentId(buildingId)
        .withName("ScheduledOccSt")
        .withDisplayName("ScheduledOccSt")
        .withAdditionalProperties(additionalProperties)
        .build();
    ScheduledAsyncComputedPointEntity scheduledPoint = (ScheduledAsyncComputedPointEntity)nodeHierarchyService.createNode(
        createNodeRequest);
    Integer scheduledPointId = scheduledPoint.getPersistentIdentity();
    LOGGER.debug("scheduledPointId: " + scheduledPointId);
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building = portfolio.getChildBuilding(buildingId);

    
    // ***********************************************
    // Create floor one
    additionalProperties.clear();
    createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.FLOOR)
        .withParentId(buildingId)
        .withName("Floor__One")
        .withDisplayName("Floor One")
        .withAdditionalProperties(additionalProperties)
        .build();
    FloorEntity floorOne = (FloorEntity)nodeHierarchyService.createNode(
        createNodeRequest);
    Integer floorOneId = floorOne.getPersistentIdentity();
    LOGGER.debug("floorOne: " + floorOne);
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building = portfolio.getChildBuilding(buildingId);
    floorOne = building.getChildFloor(floorOneId);
    

    // ***********************************************
    // Create floor two
    additionalProperties.clear();
    createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.FLOOR)
        .withParentId(buildingId)
        .withName("Floor__Two")
        .withDisplayName("Floor Two")
        .withAdditionalProperties(additionalProperties)
        .build();
    FloorEntity floorTwo = (FloorEntity)nodeHierarchyService.createNode(
        createNodeRequest);
    Integer floorTwoId = floorTwo.getPersistentIdentity();
    LOGGER.debug("floorTwo: " + floorTwo);
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building = portfolio.getChildBuilding(buildingId);
    floorOne = building.getChildFloor(floorOneId);
    floorTwo = building.getChildFloor(floorTwoId);


    // ***********************************************
    // For every equipment type, create a piece of equipment for the roof that has
    // a point of every point template compatible for that equipment type.
    String mappingExpression = "/Drivers/NiagaraNetwork/{building}/{equipment}/{point}";
    List<RawPointEntity> rawPoints = new ArrayList<>();
    for (PointTemplateAllAttributesDto pointTemplateDto: allPointTemplateDtos) {
      
      if (!pointTemplateDto.getReferencedReportTemplates().trim().isEmpty() 
          || !pointTemplateDto.getReferencedAdFunctionTemplates().trim().isEmpty()) {
        
        if (!pointTemplateDto.getParentEnergyExchangeTypes().trim().isEmpty()) {
          String[] energyExchangeTypeNames = pointTemplateDto.getParentEnergyExchangeTypes().split(",");
          for (int i=0; i < energyExchangeTypeNames.length; i++) {
            
            String energyExchangeTypeName = energyExchangeTypeNames[i].trim().toUpperCase();
            AbstractEnergyExchangeTypeEntity energyExchangeType = tagsContainer.getEquipmentTypeByNameNullIfNotExists(energyExchangeTypeName);
            if (energyExchangeType != null) {
              
              String pointTemplateName = pointTemplateDto.getName().toUpperCase();
              //LOGGER.debug("ROOF: Creating point for energyExchangeTypeName: " + energyExchangeTypeName + " for pointTemplateName: " + pointTemplateName);
              
              rawPoints.add(buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Test__Building/Roof__"
                  + energyExchangeTypeName
                  + "/Roof__"
                  + pointTemplateName));          
            }
          }
        }
      }
    }
    customer.addRawPoints(rawPoints);
    boolean storeRawPoints = true;
    customer = customerService.updateCustomer(customer, storeRawPoints);
    
    
    // ***********************************************
    // Map the raw points
    List<RawPointData> rawPointData = new ArrayList<>();
    for (RawPointEntity rp: rawPoints) {
      
      rawPointData.add(RawPointData
          .builder()
          .withRawPointId(rp.getPersistentIdentity())
          .withMetricId(rp.getMetricId())
          .build());
    }    
    MapRawPointsRequest mapRawPointsRequest = MapRawPointsRequest
        .builder()
        .withCustomerId(customerId)
        .withRawPoints(rawPointData)
        .withMappingExpression(mappingExpression)
        .withMetricIdDelimiter(NodeHierarchyService.DEFAULT_METRIC_ID_DELIMITER)
        .build();
    
    List<MappablePointEntity> createdMappablePoints = nodeHierarchyService.mapRawPoints(mapRawPointsRequest);
    LOGGER.debug("Number of points mapped for building: "
        + building
        + ": "
        + createdMappablePoints.size());
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building = portfolio.getChildBuilding(buildingId);
    floorOne = building.getChildFloor(floorOneId);
    floorTwo = building.getChildFloor(floorTwoId);
    
    
    // ***********************************************
    // For every equipment type, create a piece of equipment for each floor that has
    // a point of every point template compatible for that equipment type.
    for (FloorEntity floor: building.getChildFloors()) {
      
      mappingExpression = "/Drivers/NiagaraNetwork/{building}/{floor}/{equipment}/{point}";
      rawPoints = new ArrayList<>();
      for (PointTemplateAllAttributesDto pointTemplateDto: allPointTemplateDtos) {
        if (!pointTemplateDto.getReferencedReportTemplates().trim().isEmpty() 
            || !pointTemplateDto.getReferencedAdFunctionTemplates().trim().isEmpty()) {
          
          if (!pointTemplateDto.getParentEnergyExchangeTypes().trim().isEmpty()) {
            String[] energyExchangeTypeNames = pointTemplateDto.getParentEnergyExchangeTypes().split(",");
            for (int i=0; i < energyExchangeTypeNames.length; i++) {
              
              String energyExchangeTypeName = energyExchangeTypeNames[i].trim().toUpperCase();
              AbstractEnergyExchangeTypeEntity energyExchangeType = tagsContainer.getEquipmentTypeByNameNullIfNotExists(energyExchangeTypeName);
              if (energyExchangeType != null) {

                String pointTemplateName = pointTemplateDto.getName().toUpperCase();
                //LOGGER.debug(floor.getName() + ": Creating point for energyExchangeType: " + energyExchangeTypeName + " for pointTemplateName: " + pointTemplateName);
                
                rawPoints.add(buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Test__Building/"
                    + floor.getName()
                    + "/" 
                    + floor.getName()
                    + "__"
                    + energyExchangeTypeName
                    + "/"
                    + floor.getName()
                    + "__"
                    + pointTemplateName));
              }
            }
          }
        }
      }
      customer.addRawPoints(rawPoints);
      storeRawPoints = true;
      customer = customerService.updateCustomer(customer, storeRawPoints);
      
      
      // ***********************************************
      // Map the raw points
      rawPointData.clear();
      for (RawPointEntity rp: rawPoints) {
        
        rawPointData.add(RawPointData
            .builder()
            .withRawPointId(rp.getPersistentIdentity())
            .withMetricId(rp.getMetricId())
            .build());
      }      
      mapRawPointsRequest = MapRawPointsRequest
          .builder()
          .withCustomerId(customerId)
          .withRawPoints(rawPointData)
          .withMappingExpression(mappingExpression)
          .withMetricIdDelimiter(NodeHierarchyService.DEFAULT_METRIC_ID_DELIMITER)
          .build();
      
      createdMappablePoints = nodeHierarchyService.mapRawPoints(mapRawPointsRequest);
      
      LOGGER.debug("Number of points mapped for floor: "
          + floor
          + ": "
          + createdMappablePoints.size());
    }
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building = portfolio.getChildBuilding(buildingId);
    floorOne = building.getChildFloor(floorOneId);
    floorTwo = building.getChildFloor(floorTwoId);
    int totalMappedPointCount = portfolio.getTotalMappedPointCount();
    LOGGER.debug("totalMappedPointCount: " + totalMappedPointCount);
    
    
    // ***********************************************
    // Perform report evaluation one.
    EvaluateReportsRequest evaluateReportsRequest = EvaluateReportsRequest
        .builder()
        .withCustomerId(customerId)
        .withBuildingId(buildingId)
        .build();
    nodeHierarchyService.evaluateReports(evaluateReportsRequest);
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building = portfolio.getChildBuilding(buildingId);
    floorOne = building.getChildFloor(floorOneId);
    floorTwo = building.getChildFloor(floorTwoId);
    ReportAssertionHolder.assertReportState(portfolio, ReportAssertionHolder
        .builder()
        .withExpectedIsEnabled(false)
        .withExpectedIsValid(false)
        .withExpectedNumGreenEquipment(ZERO)
        .withExpectedNumRedEquipment(ZERO)
        .build());     

    
    // ***********************************************
    // Now that all the points have been mapped, perform equipment tagging for the building (roof units).
    EquipmentEntity ahuEquipment = null;
    List<String> equipmentMetadataTags = Arrays.asList("rooftop");
    List<EnergyExchangeSystemNodeData> updateEquipmentNodeRequestList = new ArrayList<>();
    Map<EquipmentEnergyExchangeTypeEntity, Integer> buildingEquipmentTypesToParentEquipmentMap = new HashMap<>();
    for (EquipmentEntity equipment: building.getChildEquipment()) {
      
      String equipmentName = equipment.getName();
      if (!equipmentName.startsWith(BuildingEntity.OFF_PREM_WEATH_STATION_EQUIP_NAME)) {

        int idx = equipment.getName().lastIndexOf("__")+2;
        String equipmentTypeName = equipment.getName().substring(idx);
        EquipmentEnergyExchangeTypeEntity equipmentType = tagsContainer.getEquipmentTypeByName(equipmentTypeName.toLowerCase());
        buildingEquipmentTypesToParentEquipmentMap.put(equipmentType, equipment.getPersistentIdentity());
        
        EnergyExchangeSystemNodeData energyExchangeSystemNodeData = EnergyExchangeSystemNodeData
            .builder()
            .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
            .withId(equipment.getPersistentIdentity())
            .withTypeId(equipmentType.getPersistentIdentity())
            .withDisplayName("R__" + equipmentTypeName.toUpperCase())
            .withMetadataTags(equipmentMetadataTags)
            .build();        

        if (equipment.getName().toUpperCase().endsWith("AHU")) {
          ahuEquipment = equipment;
        } else if (equipment.getName().toUpperCase().endsWith("VAV")) {
          energyExchangeSystemNodeData = EnergyExchangeSystemNodeData
              .builder(energyExchangeSystemNodeData)
              .withSystemTypeId(EnergyExchangeSystemNodeData.AIR_SUPPLY_SYSTEM_TYPE_ID)
              .withParentIds(Arrays.asList(ahuEquipment.getPersistentIdentity()))
              .build();
        }
        
        updateEquipmentNodeRequestList.add(energyExchangeSystemNodeData);
      }
    }
    UpdateEnergyExchangeSystemNodesRequest updateEquipmentNodesRequest = UpdateEnergyExchangeSystemNodesRequest
        .builder()
        .withCustomerId(customerId)
        .withData(updateEquipmentNodeRequestList)
        .build();     
    nodeHierarchyService.updateEnergyExchangeSystemNodes(updateEquipmentNodesRequest);
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building = portfolio.getChildBuilding(buildingId);
    floorOne = building.getChildFloor(floorOneId);
    floorTwo = building.getChildFloor(floorTwoId);


    // ***********************************************
    // Now that all the points have been mapped, perform equipment tagging for floor one.
    updateEquipmentNodeRequestList = new ArrayList<>();
    for (EquipmentEntity equipment: floorOne.getChildEquipment()) {
      
      int idx = equipment.getName().lastIndexOf("__")+2;
      String equipmentTypeName = equipment.getName().substring(idx);
      EquipmentEnergyExchangeTypeEntity equipmentType = tagsContainer.getEquipmentTypeByName(equipmentTypeName.toLowerCase());
      
      EnergyExchangeSystemNodeData energyExchangeSystemNodeData = EnergyExchangeSystemNodeData
          .builder()
          .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
          .withId(equipment.getPersistentIdentity())
          .withTypeId(equipmentType.getPersistentIdentity())
          .withDisplayName("F1__" + equipmentTypeName.toUpperCase())
          .withSystemTypeId(EnergyExchangeSystemType.AIR_SUPPLY.getId())
          .withParentIds(Arrays.asList(buildingEquipmentTypesToParentEquipmentMap.get(equipmentType)))
          .build();        

      if (equipment.getName().toUpperCase().endsWith("AHU")) {
        ahuEquipment = equipment;
      } else if (equipment.getName().toUpperCase().endsWith("VAV")) {
        energyExchangeSystemNodeData = EnergyExchangeSystemNodeData
            .builder(energyExchangeSystemNodeData)
            .withSystemTypeId(EnergyExchangeSystemNodeData.AIR_SUPPLY_SYSTEM_TYPE_ID)
            .withParentIds(Arrays.asList(ahuEquipment.getPersistentIdentity()))
            .build();
      }
      
      updateEquipmentNodeRequestList.add(energyExchangeSystemNodeData);      
    }
    updateEquipmentNodesRequest = UpdateEnergyExchangeSystemNodesRequest
        .builder()
        .withCustomerId(customerId)
        .withData(updateEquipmentNodeRequestList)
        .build();     
    nodeHierarchyService.updateEnergyExchangeSystemNodes(updateEquipmentNodesRequest);
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building = portfolio.getChildBuilding(buildingId);
    floorOne = building.getChildFloor(floorOneId);
    floorTwo = building.getChildFloor(floorTwoId);

    
    // ***********************************************
    // Now that all the points have been mapped, perform equipment tagging for floor two.
    updateEquipmentNodeRequestList = new ArrayList<>();
    for (EquipmentEntity equipment: floorTwo.getChildEquipment()) {
      
      int idx = equipment.getName().lastIndexOf("__")+2;
      String equipmentTypeName = equipment.getName().substring(idx);
      EquipmentEnergyExchangeTypeEntity equipmentType = tagsContainer.getEquipmentTypeByName(equipmentTypeName.toLowerCase());
      
      EnergyExchangeSystemNodeData energyExchangeSystemNodeData = EnergyExchangeSystemNodeData
          .builder()
          .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
          .withId(equipment.getPersistentIdentity())
          .withTypeId(equipmentType.getPersistentIdentity())
          .withDisplayName("F2__" + equipmentTypeName.toUpperCase())
          .withSystemTypeId(EnergyExchangeSystemType.AIR_SUPPLY.getId())
          .withParentIds(Arrays.asList(buildingEquipmentTypesToParentEquipmentMap.get(equipmentType)))
          .build();        

      if (equipment.getName().toUpperCase().endsWith("AHU")) {
        ahuEquipment = equipment;
      } else if (equipment.getName().toUpperCase().endsWith("VAV")) {
        energyExchangeSystemNodeData = EnergyExchangeSystemNodeData
            .builder(energyExchangeSystemNodeData)
            .withSystemTypeId(EnergyExchangeSystemNodeData.AIR_SUPPLY_SYSTEM_TYPE_ID)
            .withParentIds(Arrays.asList(ahuEquipment.getPersistentIdentity()))
            .build();
      }
      
      updateEquipmentNodeRequestList.add(energyExchangeSystemNodeData);         
    }
    updateEquipmentNodesRequest = UpdateEnergyExchangeSystemNodesRequest
        .builder()
        .withCustomerId(customerId)
        .withData(updateEquipmentNodeRequestList)
        .build();
    nodeHierarchyService.updateEnergyExchangeSystemNodes(updateEquipmentNodesRequest);
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building = portfolio.getChildBuilding(buildingId);
    floorOne = building.getChildFloor(floorOneId);
    floorTwo = building.getChildFloor(floorTwoId);
    
    
    // ***********************************************
    // Perform point tagging for the building (roof units).
    Boolean useGrouping = Boolean.FALSE;
    List<MappablePointNodeData> updateMappablePointNodeRequestList = new ArrayList<>();
    for (EquipmentEntity equipment: building.getChildEquipment()) {
      
      for (AbstractPointEntity point: equipment.getChildPoints()) {
        
        if (point instanceof MappablePointEntity) {

          int idx = point.getName().lastIndexOf("__")+2;
          String pointTemplateName = point.getName().substring(idx);
          PointTemplateEntity pointTemplate = nodeTagTemplatesContainer.getPointTemplateByName(pointTemplateName);
          //LOGGER.debug("ROOF: pointTemplateId: " + pointTemplate.getPersistentIdentity() + " equipmentId: " + equipment.getPersistentIdentity() + ", pointId: " + point.getPersistentIdentity());
          updateMappablePointNodeRequestList.add(MappablePointNodeData
              .builder()
              .withId(point.getPersistentIdentity())
              .withDisplayName("R__" + pointTemplateName.toUpperCase())
              .withUnitId(pointTemplate.getUnit().getPersistentIdentity())
              .withPointTemplateId(pointTemplate.getPersistentIdentity())
              .withPointDataTypeId(point.getDataType().getId())
              .build());
        }
      }
    }
    UpdateMappablePointNodesRequest updateMappablePointNodesRequest = UpdateMappablePointNodesRequest
        .builder()
        .withCustomerId(customerId)
        .withUseGrouping(useGrouping)
        .withData(updateMappablePointNodeRequestList)
        .build();
    nodeHierarchyService.updateMappablePointNodes(updateMappablePointNodesRequest);
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building = portfolio.getChildBuilding(buildingId);
    floorOne = building.getChildFloor(floorOneId);
    floorTwo = building.getChildFloor(floorTwoId);
    
    
    // ***********************************************
    // For every equipment, create a custom async computed point1, whose variables are the 
    // child mappable points and whose formula is simply the sum of them.
    for (EquipmentEntity equipment: building.getChildEquipment()) {
      
      EquipmentEnergyExchangeTypeEntity equipmentType = equipment.getEquipmentTypeNullIfNotExists();
      
      Set<PointTemplateEntity> pointTemplates = dictionaryService
          .getNodeTagTemplatesContainer()
          .getEquipmentPointTemplatesForEquipmentType(equipmentType);
      
      additionalProperties.clear();
      additionalProperties.put("pointType", NodeSubType.CUSTOM_ASYNC_COMPUTED_POINT);
      
      PointTemplateEntity pointTemplate = null;
      Optional<PointTemplateEntity> pointTemplateOpt = pointTemplates.stream().findFirst();
      if (pointTemplateOpt.isPresent()) {
        
        pointTemplate = pointTemplateOpt.get();
        
      } else {
        throw new IllegalStateException("Equipment type: ["
            + equipmentType
            + "] does not have point templates associated with it.");
      }
      
      additionalProperties.put("pointTemplateId", pointTemplate.getPersistentIdentity());
      
      additionalProperties.put("unitId", pointTemplate.getUnit().getPersistentIdentity());

      String name = "Custom" + pointTemplate.getName();
      additionalProperties.put("metricId", equipment.getNodePath() + "/ " + name);
      
      additionalProperties.put("computationInterval", ComputationInterval.QUARTER_HOUR.getName());

      // NOTE: One can create multiple temporal configs, hence the array list below.
      List<Map<String, Object>> childTemporalConfigs = new ArrayList<>();
      additionalProperties.put("childTemporalConfigs", childTemporalConfigs);
      int effectiveDateYear = 2018;
      for (int i=0; i < 2; i++) {
        
        Map<String, Object> childTemporalConfigMap = new HashMap<>();
        
        List<Map<String, Object>> variables = new ArrayList<>();
        
        StringBuilder formulaSb = new StringBuilder();
        List<AbstractPointEntity> points = new ArrayList<>();
        points.addAll(equipment.getChildPoints());
        for (int j=0; j< equipment.getChildPoints().size(); j++) {
          
          AbstractPointEntity point = points.get(j);
          
          if (point instanceof MappablePointEntity) {

            Map<String, Object> variable = new HashMap<>();
            variables.add(variable);
            
            String variableName = "$" + Integer.toString(j+1);
            
            variable.put("pointId", point.getPersistentIdentity());
            variable.put("fillPolicyId", FillPolicy.LAST_KNOWN.getId());
            variable.put("name", variableName);
            
            formulaSb.append(variableName);
            if (j < points.size()-1) {
            
              formulaSb.append(" + ");
            }
          }
        }
        
        String effectiveDate = Integer.toString(effectiveDateYear++) + "-01-01";

        childTemporalConfigMap.put("effectiveDate", effectiveDate); 
        
        childTemporalConfigMap.put("description", name + " formula description");
        
        childTemporalConfigMap.put("formula", formulaSb.toString());
        
        childTemporalConfigMap.put("childVariables", variables);
        
        childTemporalConfigs.add(childTemporalConfigMap);
      }
      
      createNodeRequest = CreateNodeRequest
          .builder()
          .withCustomerId(customerId)
          .withNodeType(NodeType.POINT)
          .withParentId(equipment.getPersistentIdentity())
          .withName(name)
          .withDisplayName(name)
          .withAdditionalProperties(additionalProperties)
          .build();
      CustomAsyncComputedPointEntity customPoint = (CustomAsyncComputedPointEntity)nodeHierarchyService.createNode(
          createNodeRequest);
      Integer customPointId = customPoint.getPersistentIdentity();
      LOGGER.debug("customPoint: " + customPointId);
      portfolio = nodeHierarchyService.loadPortfolio(customerId);
      building = portfolio.getChildBuilding(buildingId);
      floorOne = building.getChildFloor(floorOneId);
      floorTwo = building.getChildFloor(floorTwoId);    
    }     
    
    
    // ***********************************************
    // Perform report evaluation two.
    evaluateReportsRequest = EvaluateReportsRequest
        .builder()
        .withCustomerId(customerId)
        .withBuildingId(buildingId)
        .build();
    nodeHierarchyService.evaluateReports(evaluateReportsRequest);
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building = portfolio.getChildBuilding(buildingId);
    floorOne = building.getChildFloor(floorOneId);
    floorTwo = building.getChildFloor(floorTwoId);
    ReportAssertionHolder.assertReportState(portfolio, ReportAssertionHolder
        .builder()
        .withExpectedIsEnabled(false)
        .build());     

    
    // ***********************************************
    // Perform point tagging for floor one.
    updateMappablePointNodeRequestList.clear();
    for (EquipmentEntity equipment: floorOne.getChildEquipment()) {
      
      for (AbstractPointEntity point: equipment.getChildPoints()) {
        
        int idx = point.getName().lastIndexOf("__")+2;
        String pointTemplateName = point.getName().substring(idx);
        PointTemplateEntity pointTemplate = nodeTagTemplatesContainer.getPointTemplateByName(pointTemplateName);
        //LOGGER.debug("FLOOR 1: pointTemplateId: " + pointTemplate.getPersistentIdentity() + " equipmentId: " + equipment.getPersistentIdentity() + ", pointId: " + point.getPersistentIdentity());
        updateMappablePointNodeRequestList.add(MappablePointNodeData
            .builder()
            .withId(point.getPersistentIdentity())
            .withDisplayName("F1__" + pointTemplateName.toUpperCase())
            .withUnitId(pointTemplate.getUnit().getPersistentIdentity())
            .withPointTemplateId(pointTemplate.getPersistentIdentity())
            .withPointDataTypeId(point.getDataType().getId())
            .build());
      }
    }
    updateMappablePointNodesRequest = UpdateMappablePointNodesRequest
        .builder()
        .withCustomerId(customerId)
        .withUseGrouping(useGrouping)
        .withData(updateMappablePointNodeRequestList)
        .build();
    nodeHierarchyService.updateMappablePointNodes(updateMappablePointNodesRequest);
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building = portfolio.getChildBuilding(buildingId);
    floorOne = building.getChildFloor(floorOneId);
    floorTwo = building.getChildFloor(floorTwoId);
    
    
    // ***********************************************
    // Perform point tagging for floor two.
    updateMappablePointNodeRequestList.clear();
    for (EquipmentEntity equipment: floorTwo.getChildEquipment()) {
      
      for (AbstractPointEntity point: equipment.getChildPoints()) {
        
        int idx = point.getName().lastIndexOf("__")+2;
        String pointTemplateName = point.getName().substring(idx);
        PointTemplateEntity pointTemplate = nodeTagTemplatesContainer.getPointTemplateByName(pointTemplateName);
        //LOGGER.debug("FLOOR 2: pointTemplateId: " + pointTemplate.getPersistentIdentity() + " equipmentId: " + equipment.getPersistentIdentity() + ", pointId: " + point.getPersistentIdentity());
        updateMappablePointNodeRequestList.add(MappablePointNodeData
            .builder()
            .withId(point.getPersistentIdentity())
            .withDisplayName("F2__" + pointTemplateName.toUpperCase())
            .withUnitId(pointTemplate.getUnit().getPersistentIdentity())
            .withPointTemplateId(pointTemplate.getPersistentIdentity())
            .withPointDataTypeId(point.getDataType().getId())
            .build());
      }
    }
    updateMappablePointNodesRequest = UpdateMappablePointNodesRequest
        .builder()
        .withCustomerId(customerId)
        .withUseGrouping(useGrouping)
        .withData(updateMappablePointNodeRequestList)
        .withSubmittedBy("tmyers@resolutebi.com")
        .withPerformAutomaticRemediation(true)
        .withPerformAutomaticEvaluateReports(true)
        .withPerformAutomaticConfiguration(false)
        .build();
    nodeHierarchyService.updateMappablePointNodes(updateMappablePointNodesRequest);
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building = portfolio.getChildBuilding(buildingId);
    floorOne = building.getChildFloor(floorOneId);
    floorTwo = building.getChildFloor(floorTwoId);
    
    
    // ***********************************************
    // Perform AD computed point function instance candidate finding.
    FindAdFunctionInstanceCandidatesRequest findAdFunctionInstanceCandidatesRequest = FindAdFunctionInstanceCandidatesRequest
        .builder()
        .withCustomerId(customerId)
        .withFunctionType(FunctionType.COMPUTED_POINT)
        .build();
    nodeHierarchyService.findAdFunctionInstanceCandidates(findAdFunctionInstanceCandidatesRequest);
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building = portfolio.getChildBuilding(buildingId);
    floorOne = building.getChildFloor(floorOneId);
    floorTwo = building.getChildFloor(floorTwoId);
    
    
    // ***********************************************
    // Perform AD rule function instance candidate finding.
    findAdFunctionInstanceCandidatesRequest = FindAdFunctionInstanceCandidatesRequest
        .builder()
        .withCustomerId(customerId)
        .withFunctionType(FunctionType.RULE)
        .build();
    nodeHierarchyService.findAdFunctionInstanceCandidates(findAdFunctionInstanceCandidatesRequest);
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building = portfolio.getChildBuilding(buildingId);
    floorOne = building.getChildFloor(floorOneId);
    floorTwo = building.getChildFloor(floorTwoId);
    
    
    // ***********************************************
    // Create AD computed point function instances from candidates.
    List<AbstractAdFunctionInstanceEntity> computedPointCandidates = portfolio.getAllAdFunctionInstanceCandidates(
        FunctionType.COMPUTED_POINT);
    List<Integer> candidateIds = new ArrayList<>();
    for (AbstractAdFunctionInstanceEntity candidate: computedPointCandidates) {
      candidateIds.add(candidate.getPersistentIdentity());
    }
    CreateAdFunctionInstancesRequest createRequest = CreateAdFunctionInstancesRequest
        .builder()
        .withCustomerId(customerId)
        .withBuildingId(buildingId)
        .withCandidateIds(candidateIds)
        .withFunctionType(NodeHierarchyCommandRequest.COMPUTED_POINT)
        .build();
    nodeHierarchyService.createAdFunctionInstancesFromCandidates(createRequest);    
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building = portfolio.getChildBuilding(buildingId);
    floorOne = building.getChildFloor(floorOneId);
    floorTwo = building.getChildFloor(floorTwoId);
    
    
    // ***********************************************
    // Create AD rule function instances from candidates.
    List<AbstractAdFunctionInstanceEntity> ruleCandidates = portfolio.getAllAdFunctionInstanceCandidates(
        FunctionType.RULE);
    candidateIds = new ArrayList<>();
    for (AbstractAdFunctionInstanceEntity candidate: ruleCandidates) {
      candidateIds.add(candidate.getPersistentIdentity());
    }
    createRequest = CreateAdFunctionInstancesRequest
        .builder()
        .withCustomerId(customerId)
        .withBuildingId(buildingId)
        .withCandidateIds(candidateIds)
        .withFunctionType(NodeHierarchyCommandRequest.RULE)
        .withPerformAutomaticEvaluateReports(Boolean.TRUE)
        .withSubmittedBy("tmyers@resolutebi.com")
        .build();
    nodeHierarchyService.createAdFunctionInstancesFromCandidates(createRequest);    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building = portfolio.getChildBuilding(buildingId);
    floorOne = building.getChildFloor(floorOneId);
    floorTwo = building.getChildFloor(floorTwoId);

    
    // ***********************************************
    // Perform report evaluation.
    evaluateReportsRequest = EvaluateReportsRequest
        .builder()
        .withCustomerId(customerId)
        .withBuildingId(buildingId)
        .build();
    nodeHierarchyService.evaluateReports(evaluateReportsRequest);
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building = portfolio.getChildBuilding(buildingId);
    floorOne = building.getChildFloor(floorOneId);
    floorTwo = building.getChildFloor(floorTwoId);
    
    
    // ***********************************************
    // Enable all reports.
    for (BuildingEntity childBuilding: portfolio.getChildBuildings()) {
      
      List<ReportInstanceData> reportData = new ArrayList<>();
      for (ReportInstanceEntity reportInstance: childBuilding.getReportInstances()) {
        
        reportData.add(ReportInstanceData
            .builder()
            .withBuildingId(childBuilding.getPersistentIdentity())
            .withReportTemplateId(reportInstance.getReportTemplate().getPersistentIdentity())
            .withState(ReportState.ENABLED.toString())
            .build());
      }
      nodeHierarchyService.updateReportInstances(UpdateReportInstancesRequest
          .builder()
          .withCustomerId(customerId)
          .withBuildingId(childBuilding.getPersistentIdentity())
          .withData(reportData)
          .build());
      
      portfolio = nodeHierarchyService.loadPortfolio(customerId);
      building = portfolio.getChildBuilding(buildingId);
      floorOne = building.getChildFloor(floorOneId);
      floorTwo = building.getChildFloor(floorTwoId);
    }
    
    
    // ***********************************************
    // ==VERIFY APEX OF NODE HIERARCHY CONFIGURATION==
    // ***********************************************
    // For each report instance, verify that we have 3 GREEN equipment and 0 RED equipment,
    // for an obvious total of 3 equipment and likewise, zero error messages.  Also
    // verify that isEnabed=true and isValid=true
    List<ReportInstanceEntity> reportInstances = portfolio.getAllReportInstances();
    for (ReportInstanceEntity reportInstance: reportInstances) {
      
      Integer reportInstanceId = reportInstance.getPersistentIdentity();
      Integer reportTemplateId = reportInstance.getReportTemplate().getPersistentIdentity();
      Integer parentBuildingId = reportInstance.getBuilding().getPersistentIdentity();
      boolean isEnabled = reportInstance.isEnabled();
      boolean isValid = reportInstance.isValid();
      int numGreenEquipment = reportInstance.getNumEquipmentInGreenStatus();
      int numRedEquipment = reportInstance.getNumEquipmentInRedStatus();
      int numEquipmentTotal = reportInstance.getNumEquipmentTotal();
      Set<ReportInstanceEquipmentErrorMessagesEntity> reportInstanceEquipmentErrorMessages = reportInstance.getReportInstanceEquipmentErrorMessages();
      int numRedEquipmentViaErrorMessages = reportInstanceEquipmentErrorMessages.size();
      
      LOGGER.debug("=======================================================");
      LOGGER.debug("reportInstance: " + reportInstance);
      LOGGER.debug("reportInstanceId: " + reportInstanceId);
      LOGGER.debug("reportTemplateId: " + reportTemplateId);
      LOGGER.debug("parentBuildingId: " + parentBuildingId);
      LOGGER.debug("isEnabled: " + isEnabled);
      LOGGER.debug("isValid: " + isValid);
      LOGGER.debug("numEquipmentTotal: " + numEquipmentTotal);
      LOGGER.debug("numGreenEquipment: " + numGreenEquipment);
      LOGGER.debug("numRedEquipment: " + numRedEquipment);
      LOGGER.debug("numRedEquipmentViaErrorMessages: " + numRedEquipmentViaErrorMessages);
      LOGGER.debug("redEquipmentErrorMessages: " + reportInstanceEquipmentErrorMessages);
      
      // For the "RTU DX Staging - Short Cycling" report, we only deal with equipment that have the 
      // "rooftop" equipment metadata tag, so the total number of equipment for the given type will 
      // be 1.  All the other reports will have 3 (1 at roof, 1 for each of the 2 floors)
      /*
      int expectedNumEquipmentTotal = 3;
      if (reportTemplateId.equals(Integer.valueOf(9))) {
        expectedNumEquipmentTotal = 1;
      }
      
      assertEquals("reportInstance isEnabled is incorrect for: " + reportInstance,
          "true",
          Boolean.toString(isEnabled));

      assertEquals("reportInstance isValid is incorrect for: " + reportInstance,
          "true",
          Boolean.toString(isValid));

      assertEquals("reportInstance numEquipmentTotal is incorrect for: " + reportInstance,
          Integer.toString(expectedNumEquipmentTotal),
          Integer.toString(numEquipmentTotal));
      
      assertEquals("reportInstance numGreenEquipment is incorrect for: " + reportInstance,
          Integer.toString(expectedNumEquipmentTotal),
          Integer.toString(numGreenEquipment));

      assertEquals("reportInstance numRedEquipment is incorrect for: " + reportInstance,
          "0",
          Integer.toString(numRedEquipment));
      
      assertEquals("reportInstance redEquipmentErrorMessages is not empty for: " + reportInstance,
          "true",
          Boolean.toString(reportInstanceEquipmentErrorMessages.isEmpty()));

      assertEquals("reportInstance numRedEquipmentViaErrorMessages is incorrect for: " + reportInstance,
          "0",
          Integer.toString(numRedEquipmentViaErrorMessages));
      */
    }
    
    return customerId;
  }
  
  private void assertNotNull(String message, Object one) {
    
    if (one == null) {
      throw new IllegalStateException(message);
    }
  }
  
  private void assertEquals(String message, Object one, Object two) {
    
    if (!one.equals(two)) {
      throw new IllegalStateException(message 
          + ": expected [" 
          + one 
          + "] to be equal to: [" 
          + two
          + "].");
    }
  }
  
  public CreateNodeRequest createCustomAsyncPointRequest(
      AbstractNodeEntity equipment,
      String name,
      String displayName,
      String metricId,
      Integer pointTemplateId,
      Integer unitId,
      ComputationInterval computationInterval,
      String formula,
      String formulaEffectiveDate,
      String formulaDescription,
      List<Map<String, Object>> childVariables) {
    
    Map<String, Object> additionalProperties = new HashMap<>();
    additionalProperties.put("metricId", metricId);
    additionalProperties.put("pointType", NodeSubType.CUSTOM_ASYNC_COMPUTED_POINT);
    additionalProperties.put("computationInterval", computationInterval.getName());

    List<Map<String, Object>> childTemporalConfigs = new ArrayList<>();
    Map<String, Object> childTemporalConfigMap = new HashMap<>();
    
    childTemporalConfigMap.put("formula", formula);
    childTemporalConfigMap.put("description", formulaDescription);
    childTemporalConfigMap.put("effectiveDate", formulaEffectiveDate); 
    childTemporalConfigMap.put("childVariables", childVariables);
    
    childTemporalConfigs.add(childTemporalConfigMap);
    additionalProperties.put("childTemporalConfigs", childTemporalConfigs);
    
    return CreateNodeRequest
        .builder()
        .withCustomerId(equipment.getCustomerId())
        .withNodeType(NodeType.POINT)
        .withParentId(equipment.getPersistentIdentity())
        .withName(name)
        .withDisplayName(name)
        .withAdditionalProperties(additionalProperties)
        .build();    
  }
  
  public UpdateCustomAsyncComputedPointNodesRequest createUpdateCustomAsyncComputedPointNodesRequest(CustomAsyncComputedPointEntity point) {
    
    Map<String, Object> additionalProperties = new HashMap<>();
    additionalProperties.put("metricId", point.getMetricId());
    additionalProperties.put("pointType", NodeSubType.CUSTOM_ASYNC_COMPUTED_POINT);
    additionalProperties.put("computationInterval", point.getComputationInterval().getName());

    List<Map<String, Object>> childTemporalConfigs = new ArrayList<>();
    Map<String, Object> childTemporalConfigMap = new HashMap<>();
    
    for (TemporalAsyncComputedPointConfigEntity childTemporalConfig: point.getChildTemporalConfigs()) {

      childTemporalConfigMap.put("id", childTemporalConfig.getPersistentIdentity());
      childTemporalConfigMap.put("formula", childTemporalConfig.getFormula());
      childTemporalConfigMap.put("description", childTemporalConfig.getDescription());
      childTemporalConfigMap.put("effectiveDate", AbstractEntity.LOCAL_DATE_FORMATTER.get().format(childTemporalConfig.getEffectiveDate())); 
    }
    
    childTemporalConfigs.add(childTemporalConfigMap);
    additionalProperties.put("childTemporalConfigs", childTemporalConfigs);
    
    return UpdateCustomAsyncComputedPointNodesRequest
        .builder()
        .withCustomerId(point.getCustomerId())
        .withBuildingId(point.getAncestorBuilding().getPersistentIdentity())
        .withDtoList(Arrays.asList(CustomAsyncComputedPointNodeData
            .builder()
            .withId(point.getPersistentIdentity())
            .withAdditionalProperties(additionalProperties)
            .build()))
        .build();
  }
}
//@formatter:on