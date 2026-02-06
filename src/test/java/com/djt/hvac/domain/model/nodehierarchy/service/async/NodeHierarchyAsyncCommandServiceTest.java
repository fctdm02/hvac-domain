package com.djt.hvac.domain.model.nodehierarchy.service.async;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.djt.hvac.domain.model.AbstractResoluteDomainModelTest;
import com.djt.hvac.domain.model.common.event.AbstractEvent;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.customer.enums.CustomerType;
import com.djt.hvac.domain.model.dictionary.UnitEntity;
import com.djt.hvac.domain.model.dictionary.container.NodeTagTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.container.TagsContainer;
import com.djt.hvac.domain.model.dictionary.dto.PointTemplateAllAttributesDto;
import com.djt.hvac.domain.model.dictionary.energyexchange.AbstractEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.EquipmentEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.enums.EnergyExchangeSystemType;
import com.djt.hvac.domain.model.dictionary.enums.DataType;
import com.djt.hvac.domain.model.dictionary.enums.FunctionType;
import com.djt.hvac.domain.model.dictionary.enums.NodeSubType;
import com.djt.hvac.domain.model.dictionary.enums.NodeType;
import com.djt.hvac.domain.model.dictionary.enums.UnitSystem;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateInputConstantEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.AbstractNodeTagTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.report.enums.ReportPriority;
import com.djt.hvac.domain.model.dictionary.template.report.enums.ReportState;
import com.djt.hvac.domain.model.dictionary.weather.WeatherStationEntity;
import com.djt.hvac.domain.model.distributor.AbstractDistributorEntity;
import com.djt.hvac.domain.model.distributor.enums.DistributorType;
import com.djt.hvac.domain.model.function.AbstractAdFunctionInstanceEntity;
import com.djt.hvac.domain.model.function.AdFunctionEvaluator;
import com.djt.hvac.domain.model.nodehierarchy.AbstractNodeEntity;
import com.djt.hvac.domain.model.nodehierarchy.FloorEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BillableBuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingTemporalConfigEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingUtilityType;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.OperationType;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.UtilityComputationInterval;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EquipmentEntity;
import com.djt.hvac.domain.model.nodehierarchy.event.NodeHierarchyChangeEvent;
import com.djt.hvac.domain.model.nodehierarchy.event.impl.MockModelChangeEventPublisher;
import com.djt.hvac.domain.model.nodehierarchy.point.AbstractPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.CustomAsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.enums.ComputationInterval;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.enums.FillPolicy;
import com.djt.hvac.domain.model.nodehierarchy.point.mapped.MappablePointEntity;
import com.djt.hvac.domain.model.nodehierarchy.service.NodeHierarchyService;
import com.djt.hvac.domain.model.nodehierarchy.service.async.lock.valueobject.AsyncOperationLockInfo;
import com.djt.hvac.domain.model.nodehierarchy.service.async.operation.valueobject.AsyncOperationInfo;
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
import com.djt.hvac.domain.model.nodehierarchy.service.command.RemediatePortfolioRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UnignoreRawPointsRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UnmapRawPointsRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UpdateAdFunctionInstancesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UpdateBuildingNodesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UpdateEnergyExchangeSystemNodesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UpdateMappablePointNodesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UpdateReportInstancesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.ValidatePortfolioRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.AdFunctionInstanceData;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.BuildingAddressData;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.BuildingTemporalData;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.BuildingUtilityData;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.EnergyExchangeSystemNodeData;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.MappablePointNodeData;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.RawPointData;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.ReportInstanceData;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.UpdateBuildingNodeRequest;
import com.djt.hvac.domain.model.nodehierarchy.utils.LoadPortfolioOptions;
import com.djt.hvac.domain.model.nodehierarchy.utils.NodeHierarchyTestDataBuilderOptions;
import com.djt.hvac.domain.model.nodehierarchy.utils.ReportAssertionHolder;
import com.djt.hvac.domain.model.rawpoint.RawPointEntity;
import com.djt.hvac.domain.model.report.ReportInstanceEntity;
import com.djt.hvac.domain.model.report.ReportInstanceEquipmentErrorMessagesEntity;
import com.djt.hvac.domain.model.report.dto.ReportInstanceStatusDto;
import com.google.common.collect.Lists;

public class NodeHierarchyAsyncCommandServiceTest extends AbstractResoluteDomainModelTest {
  
  protected static Integer NON_ZERO = Integer.valueOf(1);
  protected static Integer ZERO = Integer.valueOf(0);

  protected static String submittedBy = "tmyers@resolutebi.com";
  protected static String distributorName = "Test Distributor Name";
  protected static String customerName = "Test Customer Name";
  protected static String portfolioName = "Test_Customer_Portfolio_Node";
  protected static String displayName = "Test Customer Portfolio Node";
  
  protected AbstractDistributorEntity distributor;
  protected Integer distributorId;
  protected AbstractCustomerEntity customer;
  protected Integer customerId;
  protected PortfolioEntity portfolio;
  protected Integer portfolioId;
  
  protected int mappedPointCount = -1;
  protected int numPointsToMap = -1;
  protected String mappingExpression;
  protected String metricIdPattern;
  protected List<RawPointEntity> rawPoints;
  protected List<MappablePointEntity> createdMappablePoints;
  protected List<AbstractNodeEntity> movedNodes;
  protected List<AbstractNodeEntity> deletedNodes;
  
  @Before
  public void before() throws Exception {
    
    super.before();
    
    distributor = distributorService.createDistributor(
        RESOLUTE_DISTRIBUTOR_ID, 
        DistributorType.ONLINE,
        distributorName,
        UnitSystem.IP.toString(),
        false);
    
    distributorId = distributor.getPersistentIdentity();

    mappedPointCount = -1;
    numPointsToMap = -1;
    mappingExpression = null;
    metricIdPattern = null;
    
    rawPoints = null;
    customer = customerService.createCustomer(
        distributor, 
        CustomerType.ONLINE,
        customerName,
        UnitSystem.IP.toString());
    
    customerId = customer.getPersistentIdentity();
    
    portfolio = nodeHierarchyService.createPortfolio(
        customer, 
        portfolioName, 
        displayName);
    
    portfolioId = portfolio.getPersistentIdentity();
    
    createdMappablePoints = null;
    movedNodes = null;
    deletedNodes = null;
  }
  
  @Test
  public void pointMapping_allOperations() throws Exception {
    
    // STEP 1.1: ARRANGE
    int numPointsToMap = 3;
    String mappingExpression = "/Drivers/NiagaraNetwork/{building}/{point}";
    String metricIdPattern = "/Drivers/NiagaraNetwork/Building_1/Point_Y";
    MockModelChangeEventPublisher.PUBLISHED_EVENTS.clear();

    
    // STEP 2.1: ACT
    mapRawPoints(numPointsToMap, metricIdPattern, mappingExpression);
    
    
    // STEP 3.1: ASSERT
    createdMappablePoints = portfolio.getAllMappablePoints();
    Assert.assertNotNull("createdMappablePoints is null", createdMappablePoints);
    Assert.assertEquals("createdMappablePoints size is incorrect", 
        Integer.toString(numPointsToMap), 
        Integer.toString(createdMappablePoints.size()));
    MappablePointEntity mappablePoint = createdMappablePoints.get(0);
    BuildingEntity building = mappablePoint.getAncestorBuilding();
    Assert.assertTrue("building is not of type BillableBuildingEntity", building instanceof BillableBuildingEntity);

    // VERIFY EVENT
    Assert.assertEquals("publishedEvents size is incorrect", 
        Integer.toString(1), 
        Integer.toString(MockModelChangeEventPublisher.PUBLISHED_EVENTS.size()));
    
    NodeHierarchyChangeEvent event = (NodeHierarchyChangeEvent)MockModelChangeEventPublisher.PUBLISHED_EVENTS.get(0);
    List<Integer> createdNodeIds = event.getCreatedNodeIds();
    
    Assert.assertEquals("createdNodeIds size is incorrect", 
        Integer.toString(numPointsToMap + 1), // 3 points are created, along with the parent building 
        Integer.toString(createdNodeIds.size()));
    
    // ****************
    
    // STEP 1.2: ARRANGE
    boolean storeRawPoints = true; 
    customer = customerService.updateCustomer(customer, storeRawPoints);
    List<Integer> rawPointIds = Lists.newArrayList();
    for (MappablePointEntity mp: createdMappablePoints) {
      rawPointIds.add(mp.getRawPoint().getPersistentIdentity());
    }
    UnmapRawPointsRequest unmapRawPointsRequest = UnmapRawPointsRequest
        .builder()
        .withCustomerId(customer.getPersistentIdentity())
        .withRawPoints(rawPointIds)
        .withSubmittedBy(submittedBy)
        .build();
    
    // STEP 2.2: ACT
    Integer asyncOperationId = nodeHierarchyAsyncCommandService.submitAsyncOperation(unmapRawPointsRequest);
    
    // Wait for a response
    AsyncOperationInfo status = waitForJobToComplete(asyncOperationId);
    Assert.assertEquals("job result is incorrect: REASON: " + status.getReason(), 
        "COMPLETED", 
        status.getStatus());
    
    // STEP 3.2: ASSERT
    createdMappablePoints = portfolio.getAllMappablePoints();
    Assert.assertNotNull("createdMappablePoints is null", createdMappablePoints);
    Assert.assertEquals("createdMappablePoints size is incorrect", 
        "0", 
        Integer.toString(createdMappablePoints.size()));
    
    // ****************
    
    // STEP 1.3: ARRANGE
    customer = customerService.updateCustomer(customer, storeRawPoints);
    rawPointIds = Lists.newArrayList();
    for (RawPointEntity rp: customer.getRawPoints()) {
      rawPointIds.add(rp.getPersistentIdentity());
    }
    IgnoreRawPointsRequest ignoreRawPointsRequest = IgnoreRawPointsRequest
        .builder()
        .withCustomerId(customer.getPersistentIdentity())
        .withRawPoints(rawPointIds)
        .withSubmittedBy(submittedBy)
        .build();
    
    // STEP 2.3: ACT
    asyncOperationId = nodeHierarchyAsyncCommandService.submitAsyncOperation(ignoreRawPointsRequest);
    
    // Wait for a response
    status = waitForJobToComplete(asyncOperationId);
    Assert.assertEquals("job result is incorrect: REASON: " + status.getReason(), 
        "COMPLETED", 
        status.getStatus());
    
    // STEP 3.3: ASSERT
    boolean loadDistributorPaymentMethods = false;
    boolean loadDistributorUsers = false; 
    boolean loadUnmappedRawPointsOnly = false;
    boolean loadIgnoredRawPoints = true;
    boolean loadDeletedRawPoints = false;
    AbstractCustomerEntity customer = customerService.loadCustomer(
        customerId, 
        loadDistributorPaymentMethods,
        loadDistributorUsers,
        loadUnmappedRawPointsOnly, 
        loadIgnoredRawPoints, 
        loadDeletedRawPoints);
    
    Set<RawPointEntity> ignoredRawPoints = customer.getIgnoredRawPoints();
    Assert.assertNotNull("ignoredRawPoints is null", ignoredRawPoints);
    Assert.assertEquals("ignoredRawPoints size is incorrect", 
        Integer.toString(ignoreRawPointsRequest.getRawPoints().size()), 
        Integer.toString(ignoredRawPoints.size()));    
    
    // ****************
    
    // STEP 1.4: ARRANGE
    customer = customerService.updateCustomer(customer, storeRawPoints);
    rawPointIds = Lists.newArrayList();
    for (RawPointEntity rp: customer.getRawPoints()) {
      rawPointIds.add(rp.getPersistentIdentity());
    }
    UnignoreRawPointsRequest unignoreRawPointsRequest = UnignoreRawPointsRequest
        .builder()
        .withCustomerId(customer.getPersistentIdentity())
        .withRawPoints(rawPointIds)
        .withSubmittedBy(submittedBy)
        .build();
    
    // STEP 2.4: ACT
    asyncOperationId = nodeHierarchyAsyncCommandService.submitAsyncOperation(unignoreRawPointsRequest);
    
    // Wait for a response
    status = waitForJobToComplete(asyncOperationId);
    Assert.assertEquals("job result is incorrect: REASON: " + status.getReason(), 
        "COMPLETED", 
        status.getStatus());
    
    // STEP 3.4: ASSERT
    loadUnmappedRawPointsOnly = false;
    loadIgnoredRawPoints = true;
    loadDeletedRawPoints = false;
    customer = customerService.loadCustomer(
        customerId, 
        loadDistributorPaymentMethods,
        loadDistributorUsers,
        loadUnmappedRawPointsOnly, 
        loadIgnoredRawPoints, 
        loadDeletedRawPoints);
    
    ignoredRawPoints = customer.getIgnoredRawPoints();
    Assert.assertNotNull("ignoredRawPoints is null", ignoredRawPoints);
    Assert.assertEquals("ignoredRawPoints size is incorrect", 
        "0", 
        Integer.toString(ignoredRawPoints.size()));
    
    Set<RawPointEntity> rawPoints = customer.getRawPoints();
    Assert.assertNotNull("rawPoints is null", ignoredRawPoints);
    Assert.assertEquals("rawPoints size is incorrect", 
        Integer.toString(numPointsToMap), 
        Integer.toString(rawPoints.size()));
  }

  @Test
  public void updateMappablePointNodes_useGroupingTrue() throws Exception {
    
    // STEP 1: ARRANGE
    TagsContainer tagsContainer = dictionaryService.getTagsContainer();
    NodeTagTemplatesContainer pointTemplatesContainer = dictionaryService.getNodeTagTemplatesContainer();
    
    // Build the node hierarchy.
    String equipmentTypeName = "ahu";
    String pointTemplateName = "BldgPress";

    int numBuildings = 1;
    int numFloors = 0; // At a minimum, there is a set of rooftop units.
    int numEquipmentPerEquipmentType = 5;
    int numPointsPerEquipmentType = 1;
    Set<String> equipmentTypeNames = new HashSet<>();
    equipmentTypeNames.add(equipmentTypeName);
    Set<String> pointTemplateNames = new HashSet<>();
    pointTemplateNames.add(pointTemplateName);
    boolean performPointMapping = true;
    boolean performEquipmentTagging = true;
    
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
        .build();
    
    int expectedQuantity = (numFloors+1);
    expectedQuantity = expectedQuantity * numEquipmentPerEquipmentType;
    expectedQuantity = expectedQuantity * numPointsPerEquipmentType;
    expectedQuantity = expectedQuantity * equipmentTypeNames.size();
    expectedQuantity = expectedQuantity * pointTemplateNames.size();
    
    Integer customerId = nodeHierarchyTestDataBuilder.createNodeHierarchy(nodeHierarchyTestDataBuilderOptions);
    
    PortfolioEntity portfolio = nodeHierarchyService.loadPortfolio(customerId);
    
    // Build up the update mappable point nodes request with grouping=true
    List<MappablePointNodeData> data = new ArrayList<>();
    
    String name = NodeHierarchyService.WILDCARD + "BldgPress";
    
    String oldDisplayName = NodeHierarchyService.WILDCARD + "BldgPress";
    String newDisplayName = "BldgPress UPDATED";
    
    EquipmentEnergyExchangeTypeEntity equipmentType = tagsContainer.getEquipmentTypeByName(equipmentTypeName);
    Integer parentEquipmentTypeId = equipmentType.getPersistentIdentity();
    
    Integer oldPointTemplateId = NodeHierarchyService.ANY;
    PointTemplateEntity pointTemplate = (PointTemplateEntity)
        pointTemplatesContainer.getPointTemplateByName(pointTemplateName);
    Integer newPointTemplateId = pointTemplate.getPersistentIdentity();
    
    Integer oldUnitId = NodeHierarchyService.ANY;
    UnitEntity unit = pointTemplate.getUnit();
    Integer newUnitId = unit.getPersistentIdentity();
    
    DataType pointDataType = DataType.NUMERIC;
    Integer pointDataTypeId = pointDataType.getId();
    
    Integer quantity = Integer.valueOf(expectedQuantity);
    
    Integer id = MappablePointNodeData.calculatePointGroupHashValue(
        parentEquipmentTypeId, 
        name, 
        oldPointTemplateId, 
        oldDisplayName, 
        oldUnitId, 
        pointDataTypeId, 
        quantity);
    
    data.add(MappablePointNodeData
        .builder()
        .withId(id)
        .withParentEquipmentTypeId(parentEquipmentTypeId)
        .withName(name)
        .withOldDisplayName(oldDisplayName)
        .withDisplayName(newDisplayName)
        .withOldPointTemplateId(oldPointTemplateId)
        .withPointTemplateId(newPointTemplateId)
        .withOldUnitId(oldUnitId)
        .withUnitId(newUnitId)
        .withPointDataTypeId(pointDataTypeId)
        .withQuantity(expectedQuantity)
        .build());
    
    Boolean useGrouping = Boolean.TRUE;
    UpdateMappablePointNodesRequest updateMappablePointNodesRequest = UpdateMappablePointNodesRequest
        .builder()
        .withCustomerId(customerId)
        .withUseGrouping(useGrouping)
        .withData(data)
        .withSubmittedBy(submittedBy)
        .build();

    
    
    
    // STEP 2: ACT
    Integer asyncOperationId = nodeHierarchyAsyncCommandService.submitAsyncOperation(updateMappablePointNodesRequest);
    AsyncOperationInfo status = waitForJobToComplete(asyncOperationId);
    Assert.assertEquals("job result is incorrect: REASON: " + status.getReason(), 
        "COMPLETED", 
        status.getStatus());

    
    
    
    // STEP 3: ASSERT
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    System.err.println("expectedQuantity: " + expectedQuantity);
    int numTotalMappedPoints = portfolio.getTotalMappedPointCount();
    System.err.println("numTotalMappedPoints: " + numTotalMappedPoints);

    Assert.assertEquals("numTotalMappedPoints is incorrect", 
        Integer.toString(expectedQuantity), 
        Integer.toString(numTotalMappedPoints));
    
    for (MappablePointEntity point: portfolio.getAllMappablePoints()) {

      Assert.assertEquals("displayName is incorrect", 
          newDisplayName, 
          point.getDisplayName());

      AbstractNodeTagTemplateEntity pt = point.getPointTemplateNullIfEmpty();
      Assert.assertNotNull("pointTemplate is null", pt);
      Assert.assertEquals("pointTemplate is incorrect", 
          Integer.toString(newPointTemplateId), 
          Integer.toString(pt.getPersistentIdentity()));

      UnitEntity u = point.getUnitNullIfNotExists();
      Assert.assertNotNull("unit is null", u);
      Assert.assertEquals("unit is incorrect", 
          Integer.toString(newUnitId), 
          Integer.toString(u.getPersistentIdentity()));
    }
  }

  @Test
  public void moveChildNodesToNewParentNode() throws Exception {
    
    // STEP 1: ARRANGE
    int numPointsToMap = 3;
    String mappingExpression = "/Drivers/NiagaraNetwork/{building}/{point}";
    String metricIdPattern = "/Drivers/NiagaraNetwork/Building_Y/Point_Y";
    mapRawPoints(numPointsToMap, metricIdPattern, mappingExpression);
    Set<BuildingEntity> childBuildings = portfolio.getChildBuildings();
    Assert.assertNotNull("childBuildings is null", childBuildings);
    Assert.assertEquals("childBuildings size is incorrect", 
        Integer.toString(numPointsToMap), 
        Integer.toString(childBuildings.size()));
    List<BuildingEntity> buildings = new ArrayList<>();
    buildings.addAll(childBuildings);
    BuildingEntity buildingOne = buildings.get(0);
    BuildingEntity buildingTwo = buildings.get(1);
    BuildingEntity buildingThree = buildings.get(2);
    Integer newParentId = buildingTwo.getPersistentIdentity();
    List<Integer> childIds = new ArrayList<>();
    for (AbstractPointEntity point: buildingOne.getChildPoints()) {
      
      childIds.add(point.getPersistentIdentity());
    }
    MockModelChangeEventPublisher.PUBLISHED_EVENTS.clear();
    MoveChildNodesRequest moveChildNodesRequest = MoveChildNodesRequest
        .builder()
        .withCustomerId(customerId)
        .withNewParentId(newParentId)
        .withChildIds(childIds)
        .withSubmittedBy(submittedBy)
        .build();

    
    
    
    // STEP 2: ACT
    Integer asyncOperationId = nodeHierarchyAsyncCommandService.submitAsyncOperation(moveChildNodesRequest);
    AsyncOperationInfo status = waitForJobToComplete(asyncOperationId);
    Assert.assertEquals("job result is incorrect: REASON: " + status.getReason(), 
        "COMPLETED", 
        status.getStatus());
    
    
    
    
    // STEP 3: ASSERT
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    childBuildings = portfolio.getChildBuildings();
    Assert.assertNotNull("childBuildings is null", childBuildings);
    Assert.assertEquals("childBuildings size is incorrect", 
        Integer.toString(numPointsToMap), 
        Integer.toString(childBuildings.size()));
    buildings = new ArrayList<>();
    buildings.addAll(childBuildings);
    buildingOne = buildings.get(0);
    buildingTwo = buildings.get(1);
    buildingThree = buildings.get(2);
    Assert.assertEquals("buildingOne point count is incorrect", 
        "0", 
        Integer.toString(buildingOne.getChildPoints().size()));
    Assert.assertEquals("buildingTwo point count is incorrect", 
        "2", 
        Integer.toString(buildingTwo.getChildPoints().size()));
    Assert.assertEquals("buildingThree point count is incorrect", 
        "1", 
        Integer.toString(buildingThree.getChildPoints().size()));
    
    // VERIFY EVENT
    Assert.assertEquals("publishedEvents size is incorrect", 
        Integer.toString(1), 
        Integer.toString(MockModelChangeEventPublisher.PUBLISHED_EVENTS.size()));
    
    NodeHierarchyChangeEvent event = (NodeHierarchyChangeEvent)MockModelChangeEventPublisher.PUBLISHED_EVENTS.get(0);
    List<Integer> updatedNodeIds = event.getUpdatedNodeIds();
    
    Assert.assertEquals("updatedNodeIds size is incorrect", 
        Integer.toString(numPointsToMap), 
        Integer.toString(updatedNodeIds.size()));
    
    MockModelChangeEventPublisher.getInstance().printEventsAsJson();
  }
  
  @Test
  public void deleteChildNodes() throws Exception {
    
    // STEP 1: ARRANGE
    int numPointsToMap = 3;
    String mappingExpression = "/Drivers/NiagaraNetwork/{building}/{point}";
    String metricIdPattern = "/Drivers/NiagaraNetwork/Building_1/Point_Y";
    mapRawPoints(numPointsToMap, metricIdPattern, mappingExpression);
    createdMappablePoints = portfolio.getAllMappablePoints();
    List<Integer> childIds = new ArrayList<>();
    for (MappablePointEntity point: createdMappablePoints) {
      
      childIds.add(point.getPersistentIdentity());
    }
    MockModelChangeEventPublisher.PUBLISHED_EVENTS.clear();
    DeleteChildNodesRequest deleteChildNodesRequest = DeleteChildNodesRequest
        .builder()
        .withCustomerId(customerId)
        .withChildIds(childIds)
        .withSubmittedBy(submittedBy)
        .build(); 
    
    
    // STEP 2: ACT
    Integer asyncOperationId = nodeHierarchyAsyncCommandService.submitAsyncOperation(deleteChildNodesRequest);
    AsyncOperationInfo status = waitForJobToComplete(asyncOperationId);
    Assert.assertEquals("job result is incorrect: REASON: " + status.getReason(), 
        "COMPLETED", 
        status.getStatus());
    
    
    
    
    // STEP 3: ASSERT
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    int totalMappedPointCount = portfolio.getTotalMappedPointCount();
    Assert.assertEquals("totalMappedPointCount is incorrect", 
        Integer.toString(0), 
        Integer.toString(totalMappedPointCount));
    
    // VERIFY EVENT
    List<AbstractEvent> publishedEvents = MockModelChangeEventPublisher.PUBLISHED_EVENTS;
    Assert.assertEquals("publishedEvents size is incorrect", 
        Integer.toString(1), 
        Integer.toString(publishedEvents.size()));
    
    NodeHierarchyChangeEvent event = (NodeHierarchyChangeEvent)MockModelChangeEventPublisher.PUBLISHED_EVENTS.get(0);
    List<Integer> deletedNodeIds = event.getDeletedNodeIds();
    
    Assert.assertEquals("deletedNodeIds size is incorrect", 
        Integer.toString(numPointsToMap), 
        Integer.toString(deletedNodeIds.size()));
    
    MockModelChangeEventPublisher.getInstance().printEventsAsJson();
  }

  @Test
  public void performPortfolioMaintenance() throws Exception {
   
    // STEP 1: ARRANGE
    boolean performStripePaymentProcessing = true;
    List<Integer> customerIdList = Arrays.asList(
        USA_HOCKEY_ARENA_CUSTOMER_ID,
        KTB_FLORIDA_SPORTS_ARENA_CUSTOMER_ID);

    
    // STEP 2: ACT
    nodeHierarchyService.performPortfolioMaintenance(
        customerIdList, 
        modelServiceProvider.getDistributorHierarchyStateEvaluator(), 
        performStripePaymentProcessing);
    
    
    // STEP 3: ASSERT
    for (Integer customerId: customerIdList) {
      
      portfolio = nodeHierarchyService.loadPortfolio(customerId);
      ValidatePortfolioRequest validatePortfolioRequest = ValidatePortfolioRequest
          .builder()
          .withCustomerId(customerId)
          .build();
      List<ValidationMessage> validationMessages = nodeHierarchyService.validatePortfolio(validatePortfolioRequest);
      Assert.assertNotNull("validationMessages is null", validationMessages);
      if (validationMessages.size() > 0) {
        System.err.println("BREAKPOINT");
      }
      Assert.assertEquals("validationMessages size is incorrect for customer: " + customerId, 
          "0", 
          Integer.toString(validationMessages.size()));
    }
  }
  
  @Test
  public void lifecycle() throws Exception {
    
    AdFunctionEvaluator.INCLUDE_BETA_FUNCTION_TEMPLATES = true;
    
    // STEP 1: ARRANGE
    TagsContainer tagsContainer = dictionaryService.getTagsContainer();
    NodeTagTemplatesContainer nodeTagTemplatesContainer = dictionaryService.getNodeTagTemplatesContainer();
    List<PointTemplateAllAttributesDto> allPointTemplateDtos = nodeTagTemplatesContainer.getPointTemplatesAllAttributes();
    Map<String, Object> additionalProperties = new HashMap<>();

    
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
    Integer asyncOperationId = nodeHierarchyAsyncCommandService.submitAsyncOperation(createNodeRequest);
    AsyncOperationInfo status = waitForJobToComplete(asyncOperationId);
    Assert.assertEquals("job result is incorrect: REASON: " + status.getReason(), 
        "COMPLETED", 
        status.getStatus());
    
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    customerId = portfolio.getCustomerId();
    
    BuildingEntity building = portfolio.getChildBuildingByNameNullIfNotExists("Test__Building");
    Integer buildingId = building.getPersistentIdentity();
    System.err.println("building: " + building);

    
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
    Assert.assertEquals("updatedBuildings size is incorrect", 
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
    Assert.assertNotNull("weatherStation is null", weatherStation);
    
    
    // ***********************************************
    // Update the parent building to just UPDATE the last building temporal data.
    temporals.clear();
    utilities.clear();
    BuildingTemporalConfigEntity childTemporalConfig = building.getChildTemporalConfigByEffectiveDate(LocalDate.parse("2019-01-01"));
    utilities.add(BuildingUtilityData
        .builder()
        .withUtilityId(Integer.valueOf(BuildingUtilityType.ELECTRIC.getId()))
        .withComputationIntervalId(Integer.valueOf(UtilityComputationInterval.DAILY.getId()))
        .withFormula("IF (WEEK_DAY)\n" + 
            "   IF (AVG_DAILY_TEMP < 52.35)\n" + 
            "      (3495.51 + 32.64 * (52.35 - AVG_DAILY_TEMP))\n" + 
            "   ELSE\n" + 
            "      (3495.51 + 79.67 * (AVG_DAILY_TEMP - 52.35))\n" + 
            "ELSE\n" + 
            "   IF (AVG_DAILY_TEMP < 56.57)\n" + 
            "      (1901.67 + 43.03 * (56.57 - AVG_DAILY_TEMP))\n" + 
            "   ELSE\n" + 
            "      (1901.67 + 48.66 * (AVG_DAILY_TEMP - 56.57))")
        .withUtilityRate(Double.valueOf(0.0878))
        .withBaselineDescription("Baseline Description 1 UPDATED")
        .withUserNotes("User Notes 1 UPDATED")
        .build());
    utilities.add(BuildingUtilityData
        .builder()
        .withUtilityId(Integer.valueOf(BuildingUtilityType.GAS.getId()))
        .withComputationIntervalId(Integer.valueOf(UtilityComputationInterval.MONTHLY_CALC_DAILY.getId()))
        .withFormula("IF(AVG_MONTHLY_TEMP <42.17)\n" + 
            "( 111.92+1.93 * (42.17-AVG_MONTHLY_TEMP ))* ELAPSED_DAYS_IN_MONTH\n" + 
            "ELSE\n" + 
            " \n" + 
            "IF(AVG_MONTHLY_TEMP >59.55)\n" + 
            "(111.92-2.92 * (AVG_MONTHLY_TEMP -59.55))* ELAPSED_DAYS_IN_MONTH\n" + 
            "ELSE\n" + 
            "(111.92)* ELAPSED_DAYS_IN_MONTH")
        .withUtilityRate(Double.valueOf(5.708))
        .withBaselineDescription("The baseline uses a change point linear regression model to correlate natural gas use per day with average monthly temperature. Baseline period is from mid-year 2015 to mid-year 2016.")
        .withUserNotes("User Notes 2")
        .build());
    utilities.add(BuildingUtilityData
        .builder()
        .withUtilityId(Integer.valueOf(BuildingUtilityType.WATER.getId()))
        .withComputationIntervalId(Integer.valueOf(UtilityComputationInterval.HISTORICAL.getId()))
        .withFormula("SAME_MONTH_YEAR(2018)")
        .withUtilityRate(Double.valueOf(0.01))
        .withBaselineDescription("The water baseline period is from mid-year 2015 to mid-year 2016.")
        .withUserNotes("User Notes 3 UPDATED")
        .build());    
    temporals.add(BuildingTemporalData
        .builder()
        .withOperationType(OperationType.UPDATE)
        .withTemporalId(childTemporalConfig.getPersistentIdentity())
        .withSquareFeet(Integer.valueOf(20000))
        .withUtilities(utilities)
        .build());
    updateBuildingNodesRequest = UpdateBuildingNodesRequest
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
    updatedBuildings = nodeHierarchyService.updateBuildingNodes(updateBuildingNodesRequest);
    Assert.assertEquals("updatedBuildings size is incorrect", 
        "1",
        Integer.toString(updatedBuildings.size()));
    loadReportInstances = true;
    loadBuildingTemporalData = true;
    portfolio = nodeHierarchyService.loadPortfolio(LoadPortfolioOptions
        .builder()
        .withCustomerId(customerId)
        .withLoadReportInstances(loadReportInstances)
        .withLoadBuildingTemporalData(loadBuildingTemporalData)
        .build());
    customerId = portfolio.getCustomerId();
    building = portfolio.getChildBuilding(buildingId);
    weatherStation = building.getWeatherStation();
    Assert.assertNotNull("weatherStation is null", weatherStation);

    
    // ***********************************************
    // Update the parent building to just DELETE the first building temporal data.
    temporals.clear();
    utilities.clear();
    childTemporalConfig = building.getChildTemporalConfigByEffectiveDate(LocalDate.parse("2018-01-01"));
    temporals.add(BuildingTemporalData
        .builder()
        .withOperationType(OperationType.DELETE)
        .withTemporalId(childTemporalConfig.getPersistentIdentity())
        .build());
    updateBuildingNodesRequest = UpdateBuildingNodesRequest
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
    updatedBuildings = nodeHierarchyService.updateBuildingNodes(updateBuildingNodesRequest);
    Assert.assertEquals("updatedBuildings size is incorrect", 
        "1",
        Integer.toString(updatedBuildings.size()));
    loadReportInstances = true;
    loadBuildingTemporalData = true;
    portfolio = nodeHierarchyService.loadPortfolio(LoadPortfolioOptions
        .builder()
        .withCustomerId(customerId)
        .withLoadReportInstances(loadReportInstances)
        .withLoadBuildingTemporalData(loadBuildingTemporalData)
        .build());
    customerId = portfolio.getCustomerId();
    building = portfolio.getChildBuilding(buildingId);
    weatherStation = building.getWeatherStation();
    Assert.assertNotNull("weatherStation is null", weatherStation);    
    
    
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
    asyncOperationId = nodeHierarchyAsyncCommandService.submitAsyncOperation(createNodeRequest);
    status = waitForJobToComplete(asyncOperationId);
    Assert.assertEquals("job result is incorrect: REASON: " + status.getReason(), 
        "COMPLETED", 
        status.getStatus());
    
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building = portfolio.getChildBuilding(buildingId);
    AbstractPointEntity scheduledPoint = building.getChildPointByNameNullIfNotExists("ScheduledOccSt");
    Integer scheduledPointId = scheduledPoint.getPersistentIdentity();
    System.err.println("scheduledPointId: " + scheduledPointId);

    
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
    asyncOperationId = nodeHierarchyAsyncCommandService.submitAsyncOperation(createNodeRequest);
    status = waitForJobToComplete(asyncOperationId);
    Assert.assertEquals("job result is incorrect: REASON: " + status.getReason(), 
        "COMPLETED", 
        status.getStatus());
    
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building = portfolio.getChildBuilding(buildingId);
    FloorEntity floorOne = building.getChildFloorByNameNullIfNotExists("Floor__One");
    Integer floorOneId = floorOne.getPersistentIdentity();
    System.err.println("floorOne: " + floorOne);
    

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
    asyncOperationId = nodeHierarchyAsyncCommandService.submitAsyncOperation(createNodeRequest);
    status = waitForJobToComplete(asyncOperationId);
    Assert.assertEquals("job result is incorrect: REASON: " + status.getReason(), 
        "COMPLETED", 
        status.getStatus());
    
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building = portfolio.getChildBuilding(buildingId);
    FloorEntity floorTwo = building.getChildFloorByNameNullIfNotExists("Floor__Two");
    Integer floorTwoId = floorTwo.getPersistentIdentity();
    System.err.println("floorTwo: " + floorTwo);

    
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
              //System.err.println("ROOF: Creating point for energyExchangeTypeName: " + energyExchangeTypeName + " for pointTemplateName: " + pointTemplateName);
              
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
        .withSubmittedBy(submittedBy)
        .build();
    asyncOperationId = nodeHierarchyAsyncCommandService.submitAsyncOperation(mapRawPointsRequest);
    status = waitForJobToComplete(asyncOperationId);
    Assert.assertEquals("job result is incorrect: REASON: " + status.getReason(), 
        "COMPLETED", 
        status.getStatus());
    
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
                //System.err.println(floor.getName() + ": Creating point for energyExchangeType: " + energyExchangeTypeName + " for pointTemplateName: " + pointTemplateName);
                
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
          .withSubmittedBy(submittedBy)
          .build(); 
      asyncOperationId = nodeHierarchyAsyncCommandService.submitAsyncOperation(mapRawPointsRequest);
      status = waitForJobToComplete(asyncOperationId);
      Assert.assertEquals("job result is incorrect: REASON: " + status.getReason(), 
          "COMPLETED", 
          status.getStatus());
    }
    building = portfolio.getChildBuilding(buildingId);
    floorOne = building.getChildFloor(floorOneId);
    floorTwo = building.getChildFloor(floorTwoId);
    int totalMappedPointCount = portfolio.getTotalMappedPointCount();
    System.err.println("totalMappedPointCount: " + totalMappedPointCount);
    
    
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
        .withSubmittedBy(submittedBy)
        .build(); 
    asyncOperationId = nodeHierarchyAsyncCommandService.submitAsyncOperation(updateEquipmentNodesRequest);
    status = waitForJobToComplete(asyncOperationId);
    Assert.assertEquals("job result is incorrect: REASON: " + status.getReason(), 
        "COMPLETED", 
        status.getStatus());
    
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
      
      updateEquipmentNodeRequestList.add(EnergyExchangeSystemNodeData
          .builder()
          .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
          .withId(equipment.getPersistentIdentity())
          .withTypeId(equipmentType.getPersistentIdentity())
          .withDisplayName("F1__" + equipmentTypeName.toUpperCase())
          .withSystemTypeId(EnergyExchangeSystemType.AIR_SUPPLY.getId())
          .withParentIds(Arrays.asList(buildingEquipmentTypesToParentEquipmentMap.get(equipmentType)))
          .build());
    }
    updateEquipmentNodesRequest = UpdateEnergyExchangeSystemNodesRequest
        .builder()
        .withCustomerId(customerId)
        .withData(updateEquipmentNodeRequestList)
        .withSubmittedBy(submittedBy)
        .build(); 
    asyncOperationId = nodeHierarchyAsyncCommandService.submitAsyncOperation(updateEquipmentNodesRequest);
    status = waitForJobToComplete(asyncOperationId);
    Assert.assertEquals("job result is incorrect: REASON: " + status.getReason(), 
        "COMPLETED", 
        status.getStatus());

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
        .withSubmittedBy(submittedBy)
        .build(); 
    asyncOperationId = nodeHierarchyAsyncCommandService.submitAsyncOperation(updateEquipmentNodesRequest);
    status = waitForJobToComplete(asyncOperationId);
    Assert.assertEquals("job result is incorrect: REASON: " + status.getReason(), 
        "COMPLETED", 
        status.getStatus());

    building = portfolio.getChildBuilding(buildingId);
    floorOne = building.getChildFloor(floorOneId);
    floorTwo = building.getChildFloor(floorTwoId);
    
    
    // ***********************************************
    // Perform point tagging for the building (roof units).
    Boolean useGrouping = Boolean.FALSE;
    List<MappablePointNodeData> updateMappablePointNodeRequestDtoList = new ArrayList<>();
    for (EquipmentEntity equipment: building.getChildEquipment()) {
      
      for (AbstractPointEntity point: equipment.getChildPoints()) {
        
        if (point instanceof MappablePointEntity) {

          int idx = point.getName().lastIndexOf("__")+2;
          String pointTemplateName = point.getName().substring(idx);
          PointTemplateEntity pointTemplate = (PointTemplateEntity)nodeTagTemplatesContainer.getPointTemplateByName(pointTemplateName);
          //System.err.println("ROOF: pointTemplateId: " + pointTemplate.getPersistentIdentity() + " equipmentId: " + equipment.getPersistentIdentity() + ", pointId: " + point.getPersistentIdentity());
          updateMappablePointNodeRequestDtoList.add(MappablePointNodeData
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
        .withData(updateMappablePointNodeRequestDtoList)
        .withSubmittedBy(submittedBy)
        .build();
    asyncOperationId = nodeHierarchyAsyncCommandService.submitAsyncOperation(updateMappablePointNodesRequest);
    status = waitForJobToComplete(asyncOperationId);
    Assert.assertEquals("job result is incorrect: REASON: " + status.getReason(), 
        "COMPLETED", 
        status.getStatus());

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
      
      PointTemplateEntity pointTemplate = pointTemplates.stream().findFirst().get();
      
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
      System.err.println("customPoint: " + customPointId);
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
    updateMappablePointNodeRequestDtoList.clear();
    for (EquipmentEntity equipment: floorOne.getChildEquipment()) {
      
      for (AbstractPointEntity point: equipment.getChildPoints()) {
        
        int idx = point.getName().lastIndexOf("__")+2;
        String pointTemplateName = point.getName().substring(idx);
        PointTemplateEntity pointTemplate = (PointTemplateEntity)nodeTagTemplatesContainer.getPointTemplateByName(pointTemplateName);
        //System.err.println("FLOOR 1: pointTemplateId: " + pointTemplate.getPersistentIdentity() + " equipmentId: " + equipment.getPersistentIdentity() + ", pointId: " + point.getPersistentIdentity());
        updateMappablePointNodeRequestDtoList.add(MappablePointNodeData
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
        .withData(updateMappablePointNodeRequestDtoList)
        .withSubmittedBy(submittedBy)
        .build();
    asyncOperationId = nodeHierarchyAsyncCommandService.submitAsyncOperation(updateMappablePointNodesRequest);
    status = waitForJobToComplete(asyncOperationId);
    Assert.assertEquals("job result is incorrect: REASON: " + status.getReason(), 
        "COMPLETED", 
        status.getStatus());

    building = portfolio.getChildBuilding(buildingId);
    floorOne = building.getChildFloor(floorOneId);
    floorTwo = building.getChildFloor(floorTwoId);
    
    
    // ***********************************************
    // Perform point tagging for floor two.
    updateMappablePointNodeRequestDtoList.clear();
    for (EquipmentEntity equipment: floorTwo.getChildEquipment()) {
      
      for (AbstractPointEntity point: equipment.getChildPoints()) {
        
        int idx = point.getName().lastIndexOf("__")+2;
        String pointTemplateName = point.getName().substring(idx);
        PointTemplateEntity pointTemplate = (PointTemplateEntity)nodeTagTemplatesContainer.getPointTemplateByName(pointTemplateName);
        //System.err.println("FLOOR 2: pointTemplateId: " + pointTemplate.getPersistentIdentity() + " equipmentId: " + equipment.getPersistentIdentity() + ", pointId: " + point.getPersistentIdentity());
        updateMappablePointNodeRequestDtoList.add(MappablePointNodeData
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
        .withData(updateMappablePointNodeRequestDtoList)
        .withSubmittedBy(submittedBy)
        .build();
    asyncOperationId = nodeHierarchyAsyncCommandService.submitAsyncOperation(updateMappablePointNodesRequest);
    status = waitForJobToComplete(asyncOperationId);
    Assert.assertEquals("job result is incorrect: REASON: " + status.getReason(), 
        "COMPLETED", 
        status.getStatus());

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
    nodeHierarchyAsyncCommandService.submitAsyncOperation(findAdFunctionInstanceCandidatesRequest);
    Thread.sleep(500);
    status = waitForJobToComplete(asyncOperationId);
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
        .withFunctionType(NodeHierarchyCommandRequest.COMPUTED_POINT)
        .withCandidateIds(candidateIds)
        .withSubmittedBy(submittedBy)
        .build();
    asyncOperationId = nodeHierarchyAsyncCommandService.submitAsyncOperation(createRequest);
    status = waitForJobToComplete(asyncOperationId);
    Assert.assertEquals("job result is incorrect: REASON: " + status.getReason(), 
        "COMPLETED", 
        status.getStatus());

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
    nodeHierarchyAsyncCommandService.submitAsyncOperation(findAdFunctionInstanceCandidatesRequest);
    Thread.sleep(500);
    status = waitForJobToComplete(asyncOperationId);
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
        .withFunctionType(NodeHierarchyCommandRequest.RULE)
        .withCandidateIds(candidateIds)
        .withSubmittedBy(submittedBy)
        .build();
    asyncOperationId = nodeHierarchyAsyncCommandService.submitAsyncOperation(createRequest);
    status = waitForJobToComplete(asyncOperationId);
    Assert.assertEquals("job result is incorrect: REASON: " + status.getReason(), 
        "COMPLETED", 
        status.getStatus());

    building = portfolio.getChildBuilding(buildingId);
    floorOne = building.getChildFloor(floorOneId);
    floorTwo = building.getChildFloor(floorTwoId);
    

    
    // ***********************************************
    // Update AD rule function input constants.
    int numAdRuleFunctionInstancesBefore = portfolio.getAllAdFunctionInstances(FunctionType.RULE).size();
    List<AdFunctionInstanceData> updateAdFunctionItems = new ArrayList<>();
    List<Integer> adFunctionIdsToUpdate = new ArrayList<>();
    for (AbstractAdFunctionInstanceEntity instance: portfolio.getAllAdFunctionInstances(FunctionType.RULE)) {
      
      Integer instanceId = instance.getPersistentIdentity();

      Map<Integer, String> constantDataMap = new HashMap<>();
      
      AdFunctionTemplateInputConstantEntity inputConstant = instance.getAdFunctionTemplate().getInputConstant("DELAY");
      
      constantDataMap.put(inputConstant.getPersistentIdentity(), "999");
      
      adFunctionIdsToUpdate.add(instanceId);
      
      updateAdFunctionItems.add(AdFunctionInstanceData
          .builder()
          .withEnergyExchangeId(instance.getEquipment().getPersistentIdentity())
          .withAdFunctionTemplateId(instance.getAdFunctionTemplate().getPersistentIdentity())
          .withDataByConstantIds(constantDataMap)
          .build());
    }
    UpdateAdFunctionInstancesRequest updateAdFunctionsRequest = UpdateAdFunctionInstancesRequest
        .builder()
        .withCustomerId(customerId)
        .withBuildingId(buildingId)
        .withFunctionType(NodeHierarchyCommandRequest.RULE)
        .withData(updateAdFunctionItems)
        .withSubmittedBy(submittedBy)
        .withPerformAutomaticEvaluateReports(Boolean.TRUE)
        .build();
    asyncOperationId = nodeHierarchyAsyncCommandService.submitAsyncOperation(updateAdFunctionsRequest);
    status = waitForJobToComplete(asyncOperationId);
    Assert.assertEquals("job result is incorrect: REASON: " + status.getReason(), 
        "COMPLETED", 
        status.getStatus());

    building = portfolio.getChildBuilding(buildingId);
    floorOne = building.getChildFloor(floorOneId);
    floorTwo = building.getChildFloor(floorTwoId);
    
    // Verify that all were updated.
    int numAdRuleFunctionInstancesAfter = portfolio.getAllAdFunctionInstances(FunctionType.RULE).size();
    Assert.assertEquals("numAdRuleFunctionInstancesAfter is incorrect", 
        Integer.toString(numAdRuleFunctionInstancesBefore),
        Integer.toString(numAdRuleFunctionInstancesAfter));    
    
    for (AbstractAdFunctionInstanceEntity instance: portfolio.getAllAdFunctionInstances(FunctionType.RULE)) {
      
      Assert.assertEquals("instant input constant value is incorrect", 
          "999",
          instance.getInputConstant("DELAY").getValue());    
    }

    
    // ***********************************************
    // Perform report evaluation.
    /* RP-9685: Automatically perform report evaluation
     * when creating/enabling rules/computed points.
    evaluateReportsRequest = EvaluateReportsRequest
        .builder()
        .withCustomerId(customerId)
        .withBuildingId(buildingId)
        .build();
    nodeHierarchyAsyncCommandService.submitAsyncOperation(evaluateReportsRequest);
    Thread.sleep(250);
    status = waitForJobToComplete(asyncOperationId);
    Thread.sleep(250);
    building = portfolio.getChildBuilding(buildingId);
    floorOne = building.getChildFloor(floorOneId);
    floorTwo = building.getChildFloor(floorTwoId);
    */
    
    
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
      nodeHierarchyAsyncCommandService.submitAsyncOperation(UpdateReportInstancesRequest
          .builder()
          .withCustomerId(customerId)
          .withBuildingId(childBuilding.getPersistentIdentity())
          .withData(reportData)
          .build());
      
      Thread.sleep(750);
      status = waitForJobToComplete(asyncOperationId);
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
    for (ReportInstanceEntity reportInstance: portfolio.getAllReportInstances()) {
      
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
      
      System.err.println("=======================================================");
      System.err.println("reportInstance: " + reportInstance);
      System.err.println("reportInstanceId: " + reportInstanceId);
      System.err.println("reportTemplateId: " + reportTemplateId);
      System.err.println("parentBuildingId: " + parentBuildingId);
      System.err.println("isEnabled: " + isEnabled);
      System.err.println("isValid: " + isValid);
      System.err.println("numEquipmentTotal: " + numEquipmentTotal);
      System.err.println("numGreenEquipment: " + numGreenEquipment);
      System.err.println("numRedEquipment: " + numRedEquipment);
      System.err.println("numRedEquipmentViaErrorMessages: " + numRedEquipmentViaErrorMessages);
      System.err.println("redEquipmentErrorMessages: " + reportInstanceEquipmentErrorMessages);
      
      // For the "RTU DX Staging - Short Cycling" report, we only deal with equipment that have the 
      // "rooftop" equipment metadata tag, so the total number of equipment for the given type will 
      // be 1.  All the other reports will have 3 (1 at roof, 1 for each of the 2 floors)
      int expectedNumEquipmentTotal = 3;
      if (reportTemplateId.equals(Integer.valueOf(9))) {
        expectedNumEquipmentTotal = 1;
      }
      
      Assert.assertEquals("reportInstance isEnabled is incorrect for: " + reportInstance,
          "true",
          Boolean.toString(isEnabled));

      Assert.assertEquals("reportInstance isValid is incorrect for: " + reportInstance,
          "true",
          Boolean.toString(isValid));

      Assert.assertEquals("reportInstance numEquipmentTotal is incorrect for: " + reportInstance,
          Integer.toString(expectedNumEquipmentTotal),
          Integer.toString(numEquipmentTotal));
      
      Assert.assertEquals("reportInstance numGreenEquipment is incorrect for: " + reportInstance,
          Integer.toString(expectedNumEquipmentTotal),
          Integer.toString(numGreenEquipment));

      Assert.assertEquals("reportInstance numRedEquipment is incorrect for: " + reportInstance,
          "0",
          Integer.toString(numRedEquipment));
      
      Assert.assertEquals("reportInstance redEquipmentErrorMessages is not empty for: " + reportInstance,
          "true",
          Boolean.toString(reportInstanceEquipmentErrorMessages.isEmpty()));

      Assert.assertEquals("reportInstance numRedEquipmentViaErrorMessages is incorrect for: " + reportInstance,
          "0",
          Integer.toString(numRedEquipmentViaErrorMessages));
    }
    
    
    // ***********************************************
    // Verify that report evaluation results in zero modified report instances if nothing has changed.
    evaluateReportsRequest = EvaluateReportsRequest
        .builder()
        .withCustomerId(customerId)
        .build();
    List<ReportInstanceStatusDto> dtoList = nodeHierarchyService.evaluateReports(evaluateReportsRequest);
    
    Assert.assertEquals("dtoList size is incorrect for",
        "0",
        Integer.toString(dtoList.size()));
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building = portfolio.getChildBuilding(buildingId);
    floorOne = building.getChildFloor(floorOneId);
    floorTwo = building.getChildFloor(floorTwoId);
    
    
    
    
    // ***********************************************
    // TODO: TDM: Test remediations.
    
    // DeleteAdFunctionInstanceCandidateStrategyImpl
    
    // DeleteAndReAddTagsFromTemplateStrategyImpl
    
    // AddAdFunctionInstanceInputPointStrategyImpl
    
    // DeleteAdFunctionInstanceInputPointStrategyImpl
    
    // DeleteTagsStrategyImpl
    
    // RemoveParentEquipmentReferenceStrategyImpl
    
    // AddMissingAdFunctionInstanceInputConstantStrategyImpl
    
    // DeleteAdFunctionInstanceStrategyImpl
    
    // HardDeleteNodeStrategyImpl
    
    // RemovePointTemplateAndTagsStrategyImpl
    
    
    
    
    // ***********************************************
    // Disable all reports.
    for (BuildingEntity childBuilding: portfolio.getChildBuildings()) {
    
      List<ReportInstanceData> reportData = new ArrayList<>();
      for (ReportInstanceEntity reportInstance: childBuilding.getReportInstances()) {
        
        reportData.add(ReportInstanceData
            .builder()
            .withBuildingId(childBuilding.getPersistentIdentity())
            .withReportTemplateId(reportInstance.getReportTemplate().getPersistentIdentity())
            .withState(ReportState.DISABLED.toString())
            .build());
      }
      nodeHierarchyAsyncCommandService.submitAsyncOperation(UpdateReportInstancesRequest
          .builder()
          .withCustomerId(customerId)
          .withBuildingId(childBuilding.getPersistentIdentity())
          .withData(reportData)
          .build());
      
      Thread.sleep(750);
      status = waitForJobToComplete(asyncOperationId);
      portfolio = nodeHierarchyService.loadPortfolio(customerId);
      building = portfolio.getChildBuilding(buildingId);
      floorOne = building.getChildFloor(floorOneId);
      floorTwo = building.getChildFloor(floorTwoId);
    }

    
    // Verify all reports have been disabled.
    for (ReportInstanceEntity reportInstance: portfolio.getAllReportInstances()) {
      
      boolean isEnabled = reportInstance.isEnabled();
      Assert.assertEquals("reportInstance isEnabled is incorrect for: " + reportInstance,
          "false",
          Boolean.toString(isEnabled));
    }
    
    
    // ***********************************************
    // Delete AD computed point and rule functions.
    List<Integer> deleteAdFunctionIds = new ArrayList<>();
    List<AbstractAdFunctionInstanceEntity> allAdFunctionInstancesBefore = portfolio.getAllAdFunctionInstances();
    for (AbstractAdFunctionInstanceEntity instance: allAdFunctionInstancesBefore) {
      deleteAdFunctionIds.add(instance.getPersistentIdentity());
    }
    DeleteAdFunctionInstancesRequest deleteAdFunctionsRequest = DeleteAdFunctionInstancesRequest
        .builder()
        .withCustomerId(customerId)
        .withBuildingId(buildingId)
        .withData(deleteAdFunctionIds)
        .withFunctionType(NodeHierarchyCommandRequest.RULE)
        .withSubmittedBy(submittedBy)
        .build();
    asyncOperationId = nodeHierarchyAsyncCommandService.submitAsyncOperation(deleteAdFunctionsRequest);
    status = waitForJobToComplete(asyncOperationId);
    Assert.assertEquals("job result is incorrect: REASON: " + status.getReason(), 
        "COMPLETED", 
        status.getStatus());

    building = portfolio.getChildBuilding(buildingId);
    floorOne = building.getChildFloor(floorOneId);
    floorTwo = building.getChildFloor(floorTwoId);
    
    // Verify that all were deactivated.
    List<AbstractAdFunctionInstanceEntity> allAdFunctionInstancesAfter = portfolio.getAllAdFunctionInstances();
    Assert.assertEquals("allAdFunctionInstancesAfter size is incorrect", 
        "0",
        Integer.toString(allAdFunctionInstancesAfter.size()));
    
    // RP-9032: Verify that the corresponding AD function instance candidates were created automatically.
    List<AbstractAdFunctionInstanceEntity> allAdFunctionCandidatesAfter = portfolio.getAllAdFunctionInstanceCandidates();
    Assert.assertEquals("allAdFunctionCandidatesAfter size is incorrect", 
        Integer.toString(allAdFunctionInstancesBefore.size()),
        Integer.toString(allAdFunctionCandidatesAfter.size()));


    // ***********************************************
    // Untag all equipment.
    updateEquipmentNodeRequestList.clear();
    equipmentMetadataTags = Arrays.asList("");
    for (EquipmentEntity equipment: building.getChildEquipment()) {
      
      updateEquipmentNodeRequestList.add(EnergyExchangeSystemNodeData
          .builder()
          .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
          .withId(equipment.getPersistentIdentity())
          .withTypeId(null)
          .withSystemTypeId(EnergyExchangeSystemType.AIR_SUPPLY.getId())
          .withParentIds(new ArrayList<>())
          .withMetadataTags(equipmentMetadataTags)
          .build());
    }
    for (EquipmentEntity equipment: floorOne.getChildEquipment()) {
      
      updateEquipmentNodeRequestList.add(EnergyExchangeSystemNodeData
          .builder()
          .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
          .withId(equipment.getPersistentIdentity())
          .withTypeId(null)
          .withSystemTypeId(EnergyExchangeSystemType.AIR_SUPPLY.getId())
          .withParentIds(new ArrayList<>())
          .build());
    }
    for (EquipmentEntity equipment: floorTwo.getChildEquipment()) {
      
      updateEquipmentNodeRequestList.add(EnergyExchangeSystemNodeData
          .builder()
          .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
          .withId(equipment.getPersistentIdentity())
          .withTypeId(null)
          .withSystemTypeId(EnergyExchangeSystemType.AIR_SUPPLY.getId())
          .withParentIds(new ArrayList<>())
          .build());
    }
    updateEquipmentNodesRequest = UpdateEnergyExchangeSystemNodesRequest
        .builder()
        .withCustomerId(customerId)
        .withData(updateEquipmentNodeRequestList)
        .withSubmittedBy(submittedBy)
        .build(); 
    asyncOperationId = nodeHierarchyAsyncCommandService.submitAsyncOperation(updateEquipmentNodesRequest);
    status = waitForJobToComplete(asyncOperationId);
    Assert.assertEquals("job result is incorrect: REASON: " + status.getReason(), 
        "COMPLETED", 
        status.getStatus());

    building = portfolio.getChildBuilding(buildingId);
    floorOne = building.getChildFloor(floorOneId);
    floorTwo = building.getChildFloor(floorTwoId);
    
    
    // ***********************************************
    // Untag all points.
    updateMappablePointNodeRequestDtoList.clear();
    for (EquipmentEntity equipment: building.getChildEquipment()) {
      
      for (AbstractPointEntity point: equipment.getChildPoints()) {
        
        if (point instanceof MappablePointEntity) {
          updateMappablePointNodeRequestDtoList.add(MappablePointNodeData
              .builder()
              .withId(point.getPersistentIdentity())
              .withDisplayName(point.getDisplayName())
              .withPointTemplateId(NodeHierarchyService.NULL)
              .withUnitId(NodeHierarchyService.NULL)
              .withPointDataTypeId(point.getDataType().getId())
              .build());
        }
      }
    }
    for (EquipmentEntity equipment: floorOne.getChildEquipment()) {
      
      for (AbstractPointEntity point: equipment.getChildPoints()) {
        
        if (point instanceof MappablePointEntity) {
          updateMappablePointNodeRequestDtoList.add(MappablePointNodeData
              .builder()
              .withId(point.getPersistentIdentity())
              .withDisplayName(point.getDisplayName())
              .withPointTemplateId(NodeHierarchyService.NULL)
              .withUnitId(NodeHierarchyService.NULL)
              .withPointDataTypeId(point.getDataType().getId())
              .build());
        }
      }
    }
    
    for (EquipmentEntity equipment: floorTwo.getChildEquipment()) {
      
      for (AbstractPointEntity point: equipment.getChildPoints()) {
        
        if (point instanceof MappablePointEntity) {
          updateMappablePointNodeRequestDtoList.add(MappablePointNodeData
              .builder()
              .withId(point.getPersistentIdentity())
              .withDisplayName(point.getDisplayName())
              .withPointTemplateId(NodeHierarchyService.NULL)
              .withUnitId(NodeHierarchyService.NULL)
              .withPointDataTypeId(point.getDataType().getId())
              .build());
        }
      }
    }
    updateMappablePointNodesRequest = UpdateMappablePointNodesRequest
        .builder()
        .withCustomerId(customerId)
        .withUseGrouping(useGrouping)
        .withData(updateMappablePointNodeRequestDtoList)
        .withSubmittedBy(submittedBy)
        .build();
    asyncOperationId = nodeHierarchyAsyncCommandService.submitAsyncOperation(updateEquipmentNodesRequest);
    status = waitForJobToComplete(asyncOperationId);
    Assert.assertEquals("job result is incorrect: REASON: " + status.getReason(), 
        "COMPLETED", 
        status.getStatus());

    building = portfolio.getChildBuilding(buildingId);
    floorOne = building.getChildFloor(floorOneId);
    floorTwo = building.getChildFloor(floorTwoId);
    
    
    // ***********************************************
    // Validate portfolio.
    ValidatePortfolioRequest validatePortfolioRequest = ValidatePortfolioRequest
        .builder()
        .withCustomerId(customerId)
        .withSubmittedBy(submittedBy)
        .build();
    asyncOperationId = nodeHierarchyAsyncCommandService.submitAsyncOperation(validatePortfolioRequest);
    status = waitForJobToComplete(asyncOperationId);
    
    
    // ***********************************************
    // Remediate portfolio (all function instances should be deleted/deactivated).
    RemediatePortfolioRequest remediatePortfolioRequest = RemediatePortfolioRequest
        .builder()
        .withCustomerId(customerId)
        .build();
    asyncOperationId = nodeHierarchyAsyncCommandService.submitAsyncOperation(remediatePortfolioRequest);
    status = waitForJobToComplete(asyncOperationId);
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building = portfolio.getChildBuilding(buildingId);
    floorOne = building.getChildFloor(floorOneId);
    floorTwo = building.getChildFloor(floorTwoId);

    
    // ***********************************************
    // Evaluate reports (all reports should be invalid/disabled)
    for (BuildingEntity childBuilding: portfolio.getChildBuildings()) {
      
      evaluateReportsRequest = EvaluateReportsRequest
          .builder()
          .withCustomerId(customerId)
          .withBuildingId(childBuilding.getPersistentIdentity())
          .build();
      nodeHierarchyService.evaluateReports(evaluateReportsRequest);
    }
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building = portfolio.getChildBuilding(buildingId);
    floorOne = building.getChildFloor(floorOneId);
    floorTwo = building.getChildFloor(floorTwoId);

    
    // ***********************************************
    // Delete the building.
    asyncOperationId = nodeHierarchyAsyncCommandService.submitAsyncOperation(remediatePortfolioRequest);
    status = waitForJobToComplete(asyncOperationId);
    List<Integer> childIds = new ArrayList<>();
    for (BuildingEntity childBuilding: portfolio.getChildBuildings()) {
      
      childIds.add(childBuilding.getPersistentIdentity());
    }
    DeleteChildNodesRequest deleteChildNodesRequest = DeleteChildNodesRequest
        .builder()
        .withCustomerId(customerId)
        .withChildIds(childIds)
        .withSubmittedBy(submittedBy)
        .build(); 
    asyncOperationId = nodeHierarchyAsyncCommandService.submitAsyncOperation(deleteChildNodesRequest);
    status = waitForJobToComplete(asyncOperationId);
    
    
    // ***********************************************
    // ==VERIFY THAT WE ARE BACK TO GROUND STATE==
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    Assert.assertEquals("number of child buildings is incorrect",
        "0",
        Integer.toString(portfolio.getChildBuildings().size()));

    //System.err.println("Number of published events: " + MockDictionaryChangeEventPublisher.PUBLISHED_EVENTS.size());
    //for (AbstractEvent event: MockDictionaryChangeEventPublisher.PUBLISHED_EVENTS) {
    //  System.err.println(event);
    //}
  }
 
  @Test
  public void updateReportInstances() throws Exception {
    
    customerId = nodeHierarchyTestDataBuilder.createFullyConfiguredNodeHierarchy();    
    
    
    
    // SET ALL DISABLED
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    List<BuildingEntity> buildings = portfolio.getAllBuildings();
    BuildingEntity building = buildings.get(0);
    List<ReportInstanceData> data = new ArrayList<>();
    Set<ReportInstanceEntity> reportInstances = building.getReportInstances();
    for (ReportInstanceEntity reportInstance: reportInstances) {
      
      Assert.assertTrue("reportInstance is not enabled: " + reportInstance, reportInstance.isEnabled());
      data.add(ReportInstanceData
          .builder()
          .withBuildingId(building.getPersistentIdentity())
          .withReportTemplateId(reportInstance.getReportTemplate().getPersistentIdentity())
          .withState(ReportState.DISABLED.toString())
          .build());
    }
    UpdateReportInstancesRequest updateReportInstancesRequest = UpdateReportInstancesRequest
        .builder()
        .withCustomerId(customerId)
        .withSubmittedBy("tmyers@resolutebi.com")
        .withData(data)
        .build();
    Integer asyncOperationId = nodeHierarchyAsyncCommandService.submitAsyncOperation(updateReportInstancesRequest);
    AsyncOperationInfo status = waitForJobToComplete(asyncOperationId);
    Assert.assertEquals("job result is incorrect: REASON: " + status.getReason(), 
        "COMPLETED", 
        status.getStatus());    

    
    
    // SET DISABLED REPORTS TO LOW PRIORITY
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    buildings = portfolio.getAllBuildings();
    building = buildings.get(0);
    data = new ArrayList<>();
    reportInstances = building.getReportInstances();
    for (ReportInstanceEntity reportInstance: reportInstances) {
      
      Assert.assertFalse("reportInstance is not disabled: " + reportInstance, reportInstance.isEnabled());
      data.add(ReportInstanceData
          .builder()
          .withBuildingId(building.getPersistentIdentity())
          .withReportTemplateId(reportInstance.getReportTemplate().getPersistentIdentity())
          .withPriority(ReportPriority.LOW.toString())
          .build());
    }
    updateReportInstancesRequest = UpdateReportInstancesRequest
        .builder()
        .withCustomerId(customerId)
        .withSubmittedBy("tmyers@resolutebi.com")
        .withData(data)
        .build();
    asyncOperationId = nodeHierarchyAsyncCommandService.submitAsyncOperation(updateReportInstancesRequest);
    status = waitForJobToComplete(asyncOperationId);
    Assert.assertEquals("job result is incorrect: REASON: " + status.getReason(), 
        "COMPLETED", 
        status.getStatus());    
    
    
    
    // SET LOW PRIORITY DISABLED REPORTS TO ENABLED WITH HIGH PRIORITY
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    buildings = portfolio.getAllBuildings();
    building = buildings.get(0);
    data = new ArrayList<>();
    reportInstances = building.getReportInstances();
    for (ReportInstanceEntity reportInstance: reportInstances) {
      
      Assert.assertEquals("reportInstance is not low priority: " + reportInstance, ReportPriority.LOW, reportInstance.getPriority());
      data.add(ReportInstanceData
          .builder()
          .withBuildingId(building.getPersistentIdentity())
          .withReportTemplateId(reportInstance.getReportTemplate().getPersistentIdentity())
          .withState(ReportState.ENABLED.toString())
          .withPriority(ReportPriority.HIGH.toString())
          .build());
    }
    updateReportInstancesRequest = UpdateReportInstancesRequest
        .builder()
        .withCustomerId(customerId)
        .withSubmittedBy("tmyers@resolutebi.com")
        .withData(data)
        .build();
    asyncOperationId = nodeHierarchyAsyncCommandService.submitAsyncOperation(updateReportInstancesRequest);
    status = waitForJobToComplete(asyncOperationId);
    Assert.assertEquals("job result is incorrect: REASON: " + status.getReason(), 
        "COMPLETED", 
        status.getStatus());    
    
    
    
    // ASSERT REPORTS ARE ENABLED AND HIGH PRIORITY
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    buildings = portfolio.getAllBuildings();
    building = buildings.get(0);
    data = new ArrayList<>();
    reportInstances = building.getReportInstances();
    for (ReportInstanceEntity reportInstance: reportInstances) {
      
      Assert.assertEquals("reportInstance is not high priority: " + reportInstance, ReportPriority.HIGH, reportInstance.getPriority());
      Assert.assertTrue("reportInstance is not enabled: " + reportInstance, reportInstance.isEnabled());
    }
  }
  
  protected void mapRawPoints(
      int numPointsToMap,
      String metricIdPattern,
      String mappingExpression) throws Exception {
    
    mapRawPoints(
        dictionaryService.getPaymentPlansContainer().getMaxPointCap(),
        numPointsToMap, 
        metricIdPattern, 
        mappingExpression);
  }
  
  protected void mapRawPoints(
      int maxPointCap,
      int numPointsToMap,
      String metricIdPattern,
      String mappingExpression) throws Exception {
    
    // This is to simulate ingestion of raw points from a cloudfill connector.
    List<RawPointEntity> rawPoints = new ArrayList<>();
    for (int i=1; i <= numPointsToMap; i++) {
      
      String metricId = metricIdPattern
          .replace("Y", Integer.toString(i));
     
      rawPoints.add(buildMockRawPoint(customerId, metricId));
    }
    customer.addRawPoints(rawPoints);
    boolean storeRawPoints = true;
    customer = customerService.updateCustomer(customer, storeRawPoints);

    
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
        .withCustomerId(customer.getPersistentIdentity())
        .withRawPoints(rawPointData)
        .withMappingExpression(mappingExpression)
        .withMetricIdDelimiter(NodeHierarchyService.DEFAULT_METRIC_ID_DELIMITER)
        .withSubmittedBy(submittedBy)
        .build();
    
    // Submit the request asynchronously
    Integer asyncOperationId = nodeHierarchyAsyncCommandService.submitAsyncOperation(mapRawPointsRequest);
    
    // Wait for a response
    AsyncOperationInfo status = waitForJobToComplete(asyncOperationId);
    Assert.assertEquals("job result is incorrect: REASON: " + status.getReason(), 
        "COMPLETED", 
        status.getStatus());
    
    /* TODO: TDM: How do we best figure out how many "items" are in the response JSON?
    if (numPointsToMap < maxPointCap) {
      Assert.assertEquals("created mappable points list size is incorrect", 
          Integer.toString(numPointsToMap), 
          Integer.toString(items.size()));
    } else {
      Assert.assertEquals("created mappable points list size is incorrect", 
          Integer.toString(maxPointCap), 
          Integer.toString(items.size()));
    }
    */
  }
  
  protected AsyncOperationInfo waitForJobToComplete(Integer asyncOperationId) throws Exception {

    int count = 0;
    while (count < 6000) {

      count++;
      Thread.sleep(100);
      
      Optional<AsyncOperationInfo> statusOptional = modelServiceProvider
          .getNodeHierarchyAsyncCommandService()
          .getAsyncOperationInfo(asyncOperationId);
      
      if (statusOptional.isPresent()) {

        AsyncOperationInfo status = statusOptional.get();
        boolean isComplete = status.getIsComplete();
        if (isComplete) {
          
          // We don't consider the job to be complete until the lock is released
          Optional<AsyncOperationLockInfo> lockOptional = modelServiceProvider
              .getNodeHierarchyAsyncCommandService()
              .getLockInfo(customerId, submittedBy);
          
          if (!lockOptional.isPresent()) {
            
            // Reload the domain entities
            portfolio = nodeHierarchyService.loadPortfolio(customerId);
            customer = portfolio.getParentCustomer();
            distributor = customer.getParentDistributor();
            return status;
          }
        }
      }
    }
    throw new RuntimeException("Async operation with id: ["
        + asyncOperationId
        + "] never completed.");
  }    
}