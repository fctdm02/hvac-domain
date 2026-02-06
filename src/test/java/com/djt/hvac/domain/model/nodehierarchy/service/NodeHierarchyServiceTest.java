//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.djt.hvac.domain.model.AbstractResoluteDomainModelTest;
import com.djt.hvac.domain.model.cache.kryo.KryoSerialize;
import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.event.AbstractEvent;
import com.djt.hvac.domain.model.common.timekeeper.TimeKeeper;
import com.djt.hvac.domain.model.common.timekeeper.impl.TestTimeKeeperImpl;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.customer.enums.CustomerType;
import com.djt.hvac.domain.model.customer.repository.CustomerRepositoryFileSystemImpl;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.UnitEntity;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.container.NodeTagTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.container.TagsContainer;
import com.djt.hvac.domain.model.dictionary.dto.PointTemplateAllAttributesDto;
import com.djt.hvac.domain.model.dictionary.energyexchange.AbstractEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.EquipmentEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.LoopEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.PlantEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.enums.EnergyExchangeSystemType;
import com.djt.hvac.domain.model.dictionary.enums.DataType;
import com.djt.hvac.domain.model.dictionary.enums.FunctionType;
import com.djt.hvac.domain.model.dictionary.enums.NodeSubType;
import com.djt.hvac.domain.model.dictionary.enums.NodeType;
import com.djt.hvac.domain.model.dictionary.enums.TagGroupType;
import com.djt.hvac.domain.model.dictionary.enums.UnitSystem;
import com.djt.hvac.domain.model.dictionary.repository.DictionaryRepositoryFileSystemImpl;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateInputConstantEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.AbstractNodeTagTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.report.enums.ReportPriority;
import com.djt.hvac.domain.model.dictionary.template.report.enums.ReportState;
import com.djt.hvac.domain.model.dictionary.weather.WeatherStationEntity;
import com.djt.hvac.domain.model.distributor.AbstractDistributorEntity;
import com.djt.hvac.domain.model.distributor.enums.DistributorType;
import com.djt.hvac.domain.model.distributor.repository.DistributorRepository;
import com.djt.hvac.domain.model.distributor.repository.DistributorRepositoryFileSystemImpl;
import com.djt.hvac.domain.model.distributor.service.DistributorHierarchyStateEvaluator;
import com.djt.hvac.domain.model.email.client.MockEmailClient;
import com.djt.hvac.domain.model.function.AbstractAdFunctionInstanceEntity;
import com.djt.hvac.domain.model.function.AdFunctionEvaluator;
import com.djt.hvac.domain.model.function.AdFunctionInstanceEligiblePoint;
import com.djt.hvac.domain.model.function.dto.AdFunctionInstanceDto;
import com.djt.hvac.domain.model.function.status.AdFunctionErrorMessageSearchCriteria;
import com.djt.hvac.domain.model.function.status.AdFunctionErrorMessageSearchResponse;
import com.djt.hvac.domain.model.nodehierarchy.AbstractNodeEntity;
import com.djt.hvac.domain.model.nodehierarchy.FloorEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BillableBuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingTemporalConfigEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingPaymentType;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingStatus;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingUtilityType;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.OperationType;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.UtilityComputationInterval;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EnergyExchangeEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EquipmentEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.LoopEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.PlantEntity;
import com.djt.hvac.domain.model.nodehierarchy.event.NodeHierarchyChangeEvent;
import com.djt.hvac.domain.model.nodehierarchy.event.impl.MockModelChangeEventPublisher;
import com.djt.hvac.domain.model.nodehierarchy.point.AbstractPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.ScheduledAsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.CustomAsyncComputedPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.enums.ComputationInterval;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom.enums.FillPolicy;
import com.djt.hvac.domain.model.nodehierarchy.point.mapped.MappablePointEntity;
import com.djt.hvac.domain.model.nodehierarchy.repository.NodeHierarchyRepositoryFileSystemImpl;
import com.djt.hvac.domain.model.nodehierarchy.service.NodeHierarchyService;
import com.djt.hvac.domain.model.nodehierarchy.service.command.CreateAdFunctionInstancesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.CreateNodeRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.DeleteAdFunctionInstancesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.DeleteChildNodesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.EvaluateReportsRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.FindAdFunctionInstanceCandidatesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.MapRawPointsRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.MoveChildNodesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.NodeHierarchyCommandRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.RemediatePortfolioRequest;
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
import com.djt.hvac.domain.model.rawpoint.dto.RawPointDto;
import com.djt.hvac.domain.model.report.ReportEvaluator;
import com.djt.hvac.domain.model.report.ReportInstanceEntity;
import com.djt.hvac.domain.model.report.ReportInstanceEquipmentEntity;
import com.djt.hvac.domain.model.report.ReportInstanceEquipmentErrorMessagesEntity;
import com.djt.hvac.domain.model.report.dto.ReportInstanceStatusDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;

public class NodeHierarchyServiceTest extends AbstractResoluteDomainModelTest {

  protected static final Integer NON_ZERO = Integer.valueOf(1);
  protected static final Integer ZERO = Integer.valueOf(0);

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
    
    try {
      TimeZone.setDefault(TimeZone.getTimeZone("GMT"));    
      System.err.println("Using default time zone:" + TimeZone.getDefault());
      System.err.println();
    } catch (Exception e) {
    }
    
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
  public void mapRawPoints() throws Exception {
    
    // STEP 1: ARRANGE
    int numPointsToMap = 3;
    String mappingExpression = "/Drivers/NiagaraNetwork/{building}/{point}";
    String metricIdPattern = "/Drivers/NiagaraNetwork/Building_1/Point_Y";
    String buildingNameFilter = "Building_1";
    MockModelChangeEventPublisher.PUBLISHED_EVENTS.clear();

    
    // STEP 2: ACT
    mapRawPoints(numPointsToMap, metricIdPattern, mappingExpression, buildingNameFilter);
    
    
    // STEP 3: ASSERT
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
    
    MockModelChangeEventPublisher.getInstance().printEventsAsJson();
  }

  @Test
  public void mapRawPoints_emptyRequest() throws Exception {
    
    // STEP 1: ARRANGE
    int numPointsToMap = 3;
    String mappingExpression = "/Drivers/NiagaraNetwork/{building}/{point}";
    String metricIdPattern = "/Drivers/NiagaraNetwork/Building_1/Point_Y";
    String buildingNameFilter = "Building_1";
    MockModelChangeEventPublisher.PUBLISHED_EVENTS.clear();

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
        .withRawPoints(Lists.newArrayList())
        .withBuildingName(buildingNameFilter)
        .withMappingExpression(mappingExpression)
        .withMetricIdDelimiter(NodeHierarchyService.DEFAULT_METRIC_ID_DELIMITER)
        .build();
    
    
    
    // STEP 2: ACT
    createdMappablePoints = nodeHierarchyService.mapRawPoints(mapRawPointsRequest);
    
    
    
    // STEP 3: ASSERT
    Assert.assertEquals("created mappable points list size is incorrect", 
        Integer.toString(numPointsToMap), 
        Integer.toString(createdMappablePoints.size()));
    
    // Reload the domain entities
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    customer = portfolio.getParentCustomer();
    distributor = customer.getParentDistributor();
    
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
    
    MockModelChangeEventPublisher.getInstance().printEventsAsJson();
  }
  
  @Test
  public void mapRawPoints_allNodeTypes() throws Exception {
    
    int numPointsToMap = 3;
    int totalPoints = numPointsToMap; 
    String mappingExpression = "/Drivers/NiagaraNetwork/{building}/{*}/{plant}/{*}/{point}";
    String metricIdPattern = "/Drivers/NiagaraNetwork/Building_1/intron1/Plant_1/intron2/Point_Y";
    mapRawPoints(numPointsToMap, metricIdPattern, mappingExpression);

    
    totalPoints = totalPoints + numPointsToMap; 
    mappingExpression = "/Drivers/NiagaraNetwork/{building}/{*}/{subBuilding}/{*}/{plant}/{*}/{point}";
    metricIdPattern = "/Drivers/NiagaraNetwork/Building_1/intron1/SubBuilding_1/intron2/Plant_1/intron3/Point_Y";
    mapRawPoints(numPointsToMap, metricIdPattern, mappingExpression);
    
    
    totalPoints = totalPoints + numPointsToMap;
    mappingExpression = "/Drivers/NiagaraNetwork/{building}/{*}/{subBuilding}/{*}/{floor}/{*}/{equipment}/{*}/{equipment}/{*}/{point}";
    metricIdPattern = "/Drivers/NiagaraNetwork/Building_1/intron1/SubBuilding_1/intron2/Floor_1/intron3/Parent_Equipment_1/intron4/Child_Equipment_1/intron5/Point_Y";
    mapRawPoints(numPointsToMap, metricIdPattern, mappingExpression);

    
    totalPoints = totalPoints + numPointsToMap;
    mappingExpression = "/Drivers/NiagaraNetwork/{building}/{*}/{plant}/{*}/{floor}/{*}/{equipment}/{*}/{equipment}/{*}/{point}";
    metricIdPattern = "/Drivers/NiagaraNetwork/Building_1/intron1/Plant_1/intron2/Floor_1/intron3/Parent_Equipment_1/intron4/Child_Equipment_1/intron5/Point_Y";
    mapRawPoints(numPointsToMap, metricIdPattern, mappingExpression);

    
    totalPoints = totalPoints + numPointsToMap;
    mappingExpression = "/Drivers/NiagaraNetwork/{building}/{*}/{subBuilding}/{*}/{plant}/{*}/{equipment}/{*}/{equipment}/{*}/{point}";
    metricIdPattern = "/Drivers/NiagaraNetwork/Building_1/intron1/SubBuilding_1/intron2/Plant_1/intron3/Parent_Equipment_1/intron4/Child_Equipment_1/intron5/Point_Y";
    mapRawPoints(numPointsToMap, metricIdPattern, mappingExpression);
    
    
    List<MappablePointEntity> points = portfolio.getAllMappablePoints();
    Assert.assertEquals("totalPoints is incorrect", 
        Integer.toString(totalPoints), 
        Integer.toString(points.size()));
  }
  
  @Test
  public void updateMappablePointNodes_useGroupingTrue() throws Exception {
    
    // STEP 1: ARRANGE
    TagsContainer tagsContainer = dictionaryService.getTagsContainer();
    NodeTagTemplatesContainer pointTemplatesContainer = dictionaryService.getNodeTagTemplatesContainer();
    
    // Build the node hierarchy.
    String equipmentTypeName = "ahu";
    String pointTemplateName = "BldgPress";

    int numFloors = 0; // At a minimum, there is a set of rooftop units.
    int numEquipmentPerEquipmentType = 5;
    int numPointsPerEquipmentType = 1;
    Set<String> equipmentTypeNames = new HashSet<>();
    equipmentTypeNames.add(equipmentTypeName);
    Set<String> pointTemplateNames = new HashSet<>();
    pointTemplateNames.add(pointTemplateName);
    boolean performPointMapping = true;
    boolean performEquipmentTagging = true;
    
    int expectedQuantity = (numFloors+1);
    expectedQuantity = expectedQuantity * numEquipmentPerEquipmentType;
    expectedQuantity = expectedQuantity * numPointsPerEquipmentType;
    expectedQuantity = expectedQuantity * equipmentTypeNames.size();
    expectedQuantity = expectedQuantity * pointTemplateNames.size();
    
    NodeHierarchyTestDataBuilderOptions nodeHierarchyTestDataBuilderOptions = NodeHierarchyTestDataBuilderOptions
        .builder()
        .withNumFloors(numFloors) // At a minimum, there is a set of rooftop units.
        .withNumEquipmentPerEquipmentType(numEquipmentPerEquipmentType)
        .withNumPointsPerEquipmentType(numPointsPerEquipmentType)
        .withEquipmentTypeNames(equipmentTypeNames)
        .withPointTemplateNames(pointTemplateNames)
        .withPerformPointMapping(performPointMapping)
        .withPerformEquipmentTagging(performEquipmentTagging)
        .build();
    
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
        .build();

    
    
    
    // STEP 2: ACT
    List<MappablePointEntity> actualUpdatedPoints = nodeHierarchyService.updateMappablePointNodes(
        updateMappablePointNodesRequest);
    
    
    
    
    // STEP 3: ASSERT
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    System.err.println("expectedQuantity: " + expectedQuantity);
    System.err.println("actualUpdatedPoints: " + actualUpdatedPoints.size());
    int numTotalMappedPoints = portfolio.getTotalMappedPointCount();
    System.err.println("numTotalMappedPoints: " + numTotalMappedPoints);
    
    Assert.assertNotNull("actualUpdatedPoints is null", actualUpdatedPoints);
    Assert.assertEquals("actualUpdatedPoints size is incorrect", 
        Integer.toString(expectedQuantity), 
        Integer.toString(actualUpdatedPoints.size()));

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
  public void updateMappablePointNodes_useGroupingFalse() throws Exception {
    
    // STEP 1: ARRANGE
    TagsContainer tagsContainer = dictionaryService.getTagsContainer();
    NodeTagTemplatesContainer pointTemplatesContainer = dictionaryService.getNodeTagTemplatesContainer();
    
    // Build the node hierarchy.
    String equipmentTypeName = "ahu";
    String pointTemplateName = "BldgPress";

    int numFloors = 0; // At a minimum, there is a set of rooftop units.
    int numEquipmentPerEquipmentType = 5;
    int numPointsPerEquipmentType = 1;
    Set<String> equipmentTypeNames = new HashSet<>();
    equipmentTypeNames.add(equipmentTypeName);
    Set<String> pointTemplateNames = new HashSet<>();
    pointTemplateNames.add(pointTemplateName);
    boolean performPointMapping = true;
    boolean performEquipmentTagging = true;
    
    int expectedQuantity = (numFloors+1);
    expectedQuantity = expectedQuantity * numEquipmentPerEquipmentType;
    expectedQuantity = expectedQuantity * numPointsPerEquipmentType;
    expectedQuantity = expectedQuantity * equipmentTypeNames.size();
    expectedQuantity = expectedQuantity * pointTemplateNames.size();
    
    NodeHierarchyTestDataBuilderOptions nodeHierarchyTestDataBuilderOptions = NodeHierarchyTestDataBuilderOptions
        .builder()
        .withNumFloors(numFloors) // At a minimum, there is a set of rooftop units.
        .withNumEquipmentPerEquipmentType(numEquipmentPerEquipmentType)
        .withNumPointsPerEquipmentType(numPointsPerEquipmentType)
        .withEquipmentTypeNames(equipmentTypeNames)
        .withPointTemplateNames(pointTemplateNames)
        .withPerformPointMapping(performPointMapping)
        .withPerformEquipmentTagging(performEquipmentTagging)
        .build();
    
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
    
    for (MappablePointEntity point: portfolio.getAllMappablePoints()) {

      data.add(MappablePointNodeData
          .builder()
          .withId(point.getPersistentIdentity())
          .withParentEquipmentTypeId(parentEquipmentTypeId)
          .withName(name)
          .withOldDisplayName(oldDisplayName)
          .withDisplayName(newDisplayName)
          .withOldPointTemplateId(oldPointTemplateId)
          .withPointTemplateId(newPointTemplateId)
          .withOldUnitId(oldUnitId)
          .withUnitId(newUnitId)
          .withPointDataTypeId(pointDataTypeId)
          .build());
    }
    
    Boolean useGrouping = Boolean.FALSE;
    UpdateMappablePointNodesRequest updateMappablePointNodesRequest = UpdateMappablePointNodesRequest
        .builder()
        .withCustomerId(customerId)
        .withUseGrouping(useGrouping)
        .withData(data)
        .build();

    
    
    
    // STEP 2: ACT
    List<MappablePointEntity> actualUpdatedPoints = nodeHierarchyService.updateMappablePointNodes(
        updateMappablePointNodesRequest);
    
    
    
    
    // STEP 3: ASSERT
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    System.err.println("expectedQuantity: " + expectedQuantity);
    System.err.println("actualUpdatedPoints: " + actualUpdatedPoints.size());
    int numTotalMappedPoints = portfolio.getTotalMappedPointCount();
    System.err.println("numTotalMappedPoints: " + numTotalMappedPoints);
    
    Assert.assertNotNull("actualUpdatedPoints is null", actualUpdatedPoints);
    Assert.assertEquals("actualUpdatedPoints size is incorrect", 
        Integer.toString(expectedQuantity), 
        Integer.toString(actualUpdatedPoints.size()));

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
  public void updateMappablePointNodes_singlePoint() throws Exception {
    
    // STEP 1: ARRANGE
    TagsContainer tagsContainer = dictionaryService.getTagsContainer();
    NodeTagTemplatesContainer pointTemplatesContainer = dictionaryService.getNodeTagTemplatesContainer();
    
    // Build the node hierarchy.
    String equipmentTypeName = "ahu";
    String pointTemplateName = "BldgPress";

    int numFloors = 0; // At a minimum, there is a set of rooftop units.
    int numEquipmentPerEquipmentType = 1;
    int numPointsPerEquipmentType = 1;
    Set<String> equipmentTypeNames = new HashSet<>();
    equipmentTypeNames.add(equipmentTypeName);
    Set<String> pointTemplateNames = new HashSet<>();
    pointTemplateNames.add(pointTemplateName);
    boolean performPointMapping = true;
    boolean performEquipmentTagging = true;
    
    int expectedQuantity = (numFloors+1);
    expectedQuantity = expectedQuantity * numEquipmentPerEquipmentType;
    expectedQuantity = expectedQuantity * numPointsPerEquipmentType;
    expectedQuantity = expectedQuantity * equipmentTypeNames.size();
    expectedQuantity = expectedQuantity * pointTemplateNames.size();
    
    NodeHierarchyTestDataBuilderOptions nodeHierarchyTestDataBuilderOptions = NodeHierarchyTestDataBuilderOptions
        .builder()
        .withNumFloors(numFloors) // At a minimum, there is a set of rooftop units.
        .withNumEquipmentPerEquipmentType(numEquipmentPerEquipmentType)
        .withNumPointsPerEquipmentType(numPointsPerEquipmentType)
        .withEquipmentTypeNames(equipmentTypeNames)
        .withPointTemplateNames(pointTemplateNames)
        .withPerformPointMapping(performPointMapping)
        .withPerformEquipmentTagging(performEquipmentTagging)
        .build();
    
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
    
    List<MappablePointEntity> mappablePoints = portfolio.getAllMappablePoints();
    MappablePointEntity point = mappablePoints.get(0); 
    Integer pointId = point.getPersistentIdentity();
    
    String metadataTagName = "MainElectricConsumption"; 
    List<String> metadataTagNames = Arrays.asList(metadataTagName);
    TagEntity metadataTag = tagsContainer.getTagByName(metadataTagName, TagGroupType.POINT_TAG);
    data.add(MappablePointNodeData
        .builder()
        .withId(pointId)
        .withParentEquipmentTypeId(parentEquipmentTypeId)
        .withName(name)
        .withOldDisplayName(oldDisplayName)
        .withDisplayName(newDisplayName)
        .withOldPointTemplateId(oldPointTemplateId)
        .withPointTemplateId(newPointTemplateId)
        .withOldUnitId(oldUnitId)
        .withUnitId(newUnitId)
        .withPointDataTypeId(pointDataTypeId)
        .withMetadataTags(metadataTagNames)
        .build());
    
    Boolean useGrouping = Boolean.FALSE;
    UpdateMappablePointNodesRequest updateMappablePointNodesRequest = UpdateMappablePointNodesRequest
        .builder()
        .withCustomerId(customerId)
        .withUseGrouping(useGrouping)
        .withData(data)
        .build();

    
    
    
    // STEP 2: ACT
    List<MappablePointEntity> actualUpdatedPoints = nodeHierarchyService.updateMappablePointNodes(
        updateMappablePointNodesRequest);
    
    
    
    
    // STEP 3: ASSERT
    Assert.assertEquals("actualUpdatedPoints size is incorrect", 
        "1", 
        Integer.toString(actualUpdatedPoints.size()));
    
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    point = portfolio.getMappablePoint(pointId);

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

    Assert.assertEquals("pointTemplate is incorrect", 
        Integer.toString(newPointTemplateId), 
        Integer.toString(pt.getPersistentIdentity()));
    
    Set<TagEntity> metadataTags = point.getMetadataTags();
    Assert.assertEquals("metadataTags size is incorrect", 
        "1", 
        Integer.toString(metadataTags.size()));

    Assert.assertTrue("metadataTag is incorrect", metadataTags.contains(metadataTag));
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
    MoveChildNodesRequest request = MoveChildNodesRequest
        .builder()
        .withCustomerId(customerId)
        .withNewParentId(newParentId)
        .withChildIds(childIds)
        .build();
    
    
    // STEP 2: ACT
    movedNodes = nodeHierarchyService.moveChildNodesToNewParentNode(request);
    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("movedNodes is null", movedNodes);
    Assert.assertEquals("movedNodes size is incorrect", "1", Integer.toString(movedNodes.size()));
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
  public void point_cap_for_point_map() throws Exception {
    
    int maxPointCap = 100;
    try {

      // STEP 1: ARRANGE
      dictionaryService.getPaymentPlansContainer().setMaxPointCapForTesting(maxPointCap);
      int numPointsToMap = maxPointCap + 100;
      
      String mappingExpression = "/Drivers/NiagaraNetwork/{building}/{point}";
      String metricIdPattern = "/Drivers/NiagaraNetwork/Building_1/Point_Y";
      
      
      // STEP 2: ACT
      mapRawPoints(maxPointCap, numPointsToMap, metricIdPattern, mappingExpression);

      
      // STEP 3: ASSERT
      Set<BuildingEntity> childBuildings = portfolio.getChildBuildings();
      Assert.assertNotNull("childBuildings is null", childBuildings);
      Assert.assertEquals("childBuildings size is incorrect", 
          "1", 
          Integer.toString(childBuildings.size()));
      List<BuildingEntity> buildings = new ArrayList<>();
      buildings.addAll(childBuildings);
      BuildingEntity buildingOne = buildings.get(0);
      
      Assert.assertEquals("buildingOne point count is incorrect", 
          Integer.toString(maxPointCap), 
          Integer.toString(buildingOne.getChildPoints().size()));
      
    } finally {
      DictionaryContext.setPaymentPlansContainer(null);
      dictionaryService.ensureDictionaryDataIsLoaded();
    }
  }
  
  @Test
  public void point_cap_for_node_move() throws Exception {
    
    int maxPointCap = 100;
    try {

      // STEP 1: ARRANGE
      dictionaryService.getPaymentPlansContainer().setMaxPointCapForTesting(maxPointCap);
      String mappingExpression = "/Drivers/NiagaraNetwork/{building}/{point}";
      
      int numPointsToMap = 10;
      String metricIdPattern = "/Drivers/NiagaraNetwork/Building_1/Point_Y";
      mapRawPoints(numPointsToMap, metricIdPattern, mappingExpression);

      // Only max point cap points will be mapped.
      numPointsToMap = maxPointCap + 100;
      metricIdPattern = "/Drivers/NiagaraNetwork/Building_2/Point_Y";
      mapRawPoints(maxPointCap, numPointsToMap, metricIdPattern, mappingExpression);
      
      Set<BuildingEntity> childBuildings = portfolio.getChildBuildings();
      Assert.assertNotNull("childBuildings is null", childBuildings);
      Assert.assertEquals("childBuildings size is incorrect", 
          "2", 
          Integer.toString(childBuildings.size()));
      
      List<BuildingEntity> buildings = new ArrayList<>();
      buildings.addAll(childBuildings);
      BuildingEntity buildingOne = buildings.get(0);
      BuildingEntity buildingTwo = buildings.get(1);
      Assert.assertEquals("buildingOne point count is incorrect", 
          Integer.toString(10), 
          Integer.toString(buildingOne.getChildPoints().size()));
      Assert.assertEquals("buildingTwo point count is incorrect", 
          Integer.toString(maxPointCap), 
          Integer.toString(buildingTwo.getChildPoints().size()));
      
      // Attempt to move the 10 points from building one to building two.
      // None should be moved because of the point cap limit.
      Integer newParentId = buildingTwo.getPersistentIdentity();
      List<Integer> childIds = new ArrayList<>();
      for (AbstractPointEntity point: buildingOne.getChildPoints()) {
        
        childIds.add(point.getPersistentIdentity());
      }
      portfolio = nodeHierarchyService.loadPortfolio(customerId);
      MoveChildNodesRequest request = MoveChildNodesRequest
          .builder()
          .withCustomerId(customerId)
          .withNewParentId(newParentId)
          .withChildIds(childIds)
          .build();
      
      
      // STEP 2: ACT
      movedNodes = nodeHierarchyService.moveChildNodesToNewParentNode(request);
      
      
      // STEP 3: ASSERT
      portfolio = nodeHierarchyService.loadPortfolio(customerId);
      Assert.assertNotNull("movedNodes is null", movedNodes);
      Assert.assertEquals("movedNodes size is incorrect", "0", Integer.toString(movedNodes.size()));

      buildings = new ArrayList<>();
      buildings.addAll(childBuildings);
      buildingOne = buildings.get(0);
      buildingTwo = buildings.get(1);
      Assert.assertEquals("buildingOne point count is incorrect", 
          Integer.toString(10), 
          Integer.toString(buildingOne.getChildPoints().size()));
      Assert.assertEquals("buildingTwo point count is incorrect", 
          Integer.toString(maxPointCap), 
          Integer.toString(buildingTwo.getChildPoints().size()));
      
    } finally {
      DictionaryContext.setPaymentPlansContainer(null);
      dictionaryService.ensureDictionaryDataIsLoaded();
    }
  }
  
  @Test
  public void deleteChildNodes() throws Exception {
    
    // STEP 1: ARRANGE
    int numPointsToMap = 3;
    String mappingExpression = "/Drivers/NiagaraNetwork/{building}/{point}";
    String metricIdPattern = "/Drivers/NiagaraNetwork/Building_1/Point_Y";
    mapRawPoints(numPointsToMap, metricIdPattern, mappingExpression);
    List<Integer> childIds = new ArrayList<>();
    for (MappablePointEntity point: createdMappablePoints) {
      
      childIds.add(point.getPersistentIdentity());
    }
    MockModelChangeEventPublisher.PUBLISHED_EVENTS.clear();
    DeleteChildNodesRequest request = DeleteChildNodesRequest
        .builder()
        .withCustomerId(customerId)
        .withChildIds(childIds)
        .withSubmittedBy("tmyers@resolutebi.com")
        .build();
    
    
    // STEP 2: ACT
    deletedNodes = nodeHierarchyService.deleteChildNodes(request);
    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("deletedNodes is null", deletedNodes);
    Assert.assertEquals("deletedNodes size is incorrect", 
        Integer.toString(createdMappablePoints.size()), 
        Integer.toString(deletedNodes.size()));
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

    Assert.assertEquals("operationCategory is incorrect", 
        request.getOperationCategory(), 
        event.getOperationCategory());

    Assert.assertEquals("operationType is incorrect", 
        request.getOperationType(), 
        event.getOperationType());

    Assert.assertEquals("owner is incorrect", 
        request.getSubmittedBy(), 
        event.getOwner());
    
    Assert.assertEquals("deletedNodeIds size is incorrect", 
        Integer.toString(numPointsToMap), 
        Integer.toString(deletedNodeIds.size()));
    
    MockModelChangeEventPublisher.getInstance().printEventsAsJson();
  }
  
  @Test
  public void getReportEquipmentErrorMessages() throws Exception {

    // STEP 1: ARRANGE
    customerId = REDICO_CUSTOMER_ID;

    boolean loadReportInstances = true;
    portfolio = nodeHierarchyService.loadPortfolio(LoadPortfolioOptions
        .builder()
        .withCustomerId(customerId)
        .withLoadReportInstances(loadReportInstances)
        .build());
    
    EvaluateReportsRequest evaluateReportsRequest = EvaluateReportsRequest
        .builder()
        .withCustomerId(customerId)
        .build();
    nodeHierarchyService.evaluateReports(evaluateReportsRequest);
    portfolio = nodeHierarchyService.loadPortfolio(customerId);

    int numReportTemplates = dictionaryService.getReportTemplatesContainer().getReportTemplates().size();
    int numBuildings = portfolio.getChildBuildings().size();
    List<ReportInstanceEntity> reportInstances = portfolio.getAllReportInstances();
    Assert.assertEquals("reportInstance size is incorrect",
        Integer.toString(numReportTemplates * numBuildings),
        Integer.toString(reportInstances.size()));

    Integer reportTemplateId = 2;
    Integer buildingId = 460;



    // STEP 2: ACT
    BuildingEntity building = portfolio.getChildBuilding(buildingId);
    ReportInstanceEntity reportInstance = building.getReportInstanceByReportTemplateId(reportTemplateId);
    int numEquipmentErrorMessages = reportInstance.getReportInstanceEquipmentErrorMessages().size();



    // STEP 3: ASSERT
    Assert.assertEquals("numEquipmentErrorMessages is incorrect", 
        "3",
        Integer.toString(numEquipmentErrorMessages));
  }  
  
  @Test
  public void loadPortfolio_withDistributorHierarchy() throws Exception {
    
    // STEP 1: ARRANGE
    boolean loadDistributorUsers = false;
    AbstractDistributorEntity resoluteRootDistributor = distributorService.getResoluteRootDistributor(loadDistributorUsers);
    Integer resoluteRootDistributorId = resoluteRootDistributor.getPersistentIdentity();
    
    AbstractDistributorEntity parentDistributor = distributorService.createDistributor(
        resoluteRootDistributorId, 
        DistributorType.ONLINE, 
        "Parent Distributor",
        UnitSystem.IP.toString(),
        false);
        
    Integer parentDistributorId = parentDistributor.getPersistentIdentity();

    AbstractDistributorEntity childDistributor = distributorService.createDistributor(
        parentDistributorId, 
        DistributorType.ONLINE, 
        "Child Distributor",
        UnitSystem.IP.toString(),
        false);

    customer = customerService.createCustomer(
        childDistributor, 
        CustomerType.ONLINE,
        "Customer Name",
        UnitSystem.IP.toString());
    
    customerId = customer.getPersistentIdentity();
    
    portfolio = nodeHierarchyService.createPortfolio(
        customer, 
        "Customer Name", 
        "Customer Name");
    
    portfolioId = portfolio.getPersistentIdentity();
    

    

    // STEP 2: ACT
    portfolio = nodeHierarchyService.loadPortfolio(customerId);

    
    
    // STEP 3: ASSERT
  }    
  
  @Ignore
  @Test
  public void performPortfolioMaintenance_MCLAREN() throws Exception {
   
    // STEP 1: ARRANGE
    customerId = MCLAREN_CUSTOMER_ID;
    boolean performStripePaymentProcessing = true;
    List<Integer> customerIdList = Arrays.asList(MCLAREN_CUSTOMER_ID);
    MockEmailClient emailClient = MockEmailClient.getInstance();
    DistributorHierarchyStateEvaluator distributorHierarchyStateEvaluator = new DistributorHierarchyStateEvaluator(
        distributorService,
        customerService,
        nodeHierarchyService,
        emailClient);
    
    boolean loadAdFunctionInstances = true;
    boolean loadReportInstances = true;
    boolean loadBuildingTemporalData = true;
    
    portfolio = nodeHierarchyService.loadPortfolio(LoadPortfolioOptions
        .builder()
        .withCustomerId(customerId)
        .withLoadAdFunctionInstances(loadAdFunctionInstances)
        .withLoadReportInstances(loadReportInstances)
        .withLoadBuildingTemporalData(loadBuildingTemporalData)
        .build());
    
    BuildingEntity b = portfolio.getChildBuilding(38);
    System.err.println("Num Configs Before: " + b.getChildTemporalConfigs().size());
    
    
    
    // STEP 2: ACT
    nodeHierarchyService.performPortfolioMaintenance(
        customerIdList, 
        distributorHierarchyStateEvaluator, 
        performStripePaymentProcessing);

    

    // STEP 3: ASSERT
    portfolio = nodeHierarchyService.loadPortfolio(LoadPortfolioOptions
        .builder()
        .withCustomerId(customerId)
        .withLoadAdFunctionInstances(loadAdFunctionInstances)
        .withLoadReportInstances(loadReportInstances)
        .withLoadBuildingTemporalData(loadBuildingTemporalData)
        .build());
    
    b = portfolio.getChildBuilding(38);
    System.err.println("Num Configs After: " + b.getChildTemporalConfigs().size());
    
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
  
  @Test
  public void performPortfolioMaintenance_REDICO() throws Exception {
   
    // STEP 1: ARRANGE
    boolean performStripePaymentProcessing = true;
    List<Integer> customerIdList = Arrays.asList(REDICO_CUSTOMER_ID);
    MockEmailClient emailClient = MockEmailClient.getInstance();
    DistributorHierarchyStateEvaluator distributorHierarchyStateEvaluator = new DistributorHierarchyStateEvaluator(
        distributorService,
        customerService,
        nodeHierarchyService,
        emailClient);
    
    
    // STEP 2: ACT
    nodeHierarchyService.performPortfolioMaintenance(
        customerIdList, 
        distributorHierarchyStateEvaluator, 
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
  
  @Ignore
  @Test
  public void performPortfolioMaintenance_DOMINOS() throws Exception {
   
    // STEP 1: ARRANGE
    boolean performStripePaymentProcessing = true;
    List<Integer> customerIdList = Arrays.asList(DOMINOS_CUSTOMER_ID);
    MockEmailClient emailClient = MockEmailClient.getInstance();
    DistributorHierarchyStateEvaluator distributorHierarchyStateEvaluator = new DistributorHierarchyStateEvaluator(
        distributorService,
        customerService,
        nodeHierarchyService,
        emailClient);
    
    
    // STEP 2: ACT
    nodeHierarchyService.performPortfolioMaintenance(
        customerIdList, 
        distributorHierarchyStateEvaluator, 
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
    BuildingEntity building = (BuildingEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer buildingId = building.getPersistentIdentity();
    System.err.println("building: " + building);
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
    ScheduledAsyncComputedPointEntity scheduledPoint = (ScheduledAsyncComputedPointEntity)nodeHierarchyService.createNode(
        createNodeRequest);
    Integer scheduledPointId = scheduledPoint.getPersistentIdentity();
    System.err.println("scheduledPointId: " + scheduledPointId);
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
    System.err.println("floorOne: " + floorOne);
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
    System.err.println("floorTwo: " + floorTwo);
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
        .build(); 
    createdMappablePoints = nodeHierarchyService.mapRawPoints(mapRawPointsRequest);
    
    System.err.println("Number of points mapped for building: "
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
          .build(); 
      createdMappablePoints = nodeHierarchyService.mapRawPoints(mapRawPointsRequest);
      
      System.err.println("Number of points mapped for floor: "
          + floor
          + ": "
          + createdMappablePoints.size());
    }
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
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
        .withSubmittedBy("tmyers@resolutebi.com")
        .withPerformAutomaticRemediation(true)
        .withPerformAutomaticEvaluateReports(true)
        .withPerformAutomaticConfiguration(true)
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
        .withSubmittedBy("tmyers@resolutebi.com")
        .withPerformAutomaticRemediation(true)
        .withPerformAutomaticEvaluateReports(true)
        .withPerformAutomaticConfiguration(true)
        .build(); 
    nodeHierarchyService.updateEnergyExchangeSystemNodes(updateEquipmentNodesRequest);
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
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
      
      PointTemplateEntity pointTemplate = pointTemplates.stream().findFirst().get();
      
      additionalProperties.put("pointTemplateId", pointTemplate.getPersistentIdentity());
      
      additionalProperties.put("unitId", pointTemplate.getUnit().getPersistentIdentity());

      String name = "Custom" + pointTemplate.getName();
      additionalProperties.put("metricId", equipment.getNodePath() + "/ " + name);
      
      additionalProperties.put("computationInterval", ComputationInterval.QUARTER_HOUR.getName());
      
      // Used for evaluation of the custom async computed point.
      Map<String, Double> variableValues = new LinkedHashMap<>();

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
            
            variableValues.put(variableName, Double.valueOf(1.0));
            
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
      
      /*
      // Validate and Evaluate the custom async computed point's formula
      customPoint = portfolio.getCustomAsyncComputedPointNullIfNotExists(customPointId);
      
      customPoint.validate();
      
      // TODO: TDM: Retrieve metric values for child variables for the given timestamp
      // TODO: TDM: Figure out what "function state" is
      // TODO: TDM: Add a service method that does the following:
      //  1. Retrieves all the time series values for all of the child variables at the given timestamp
      //  2. Evaluate the result
      //  3. Publish the time series for the custom point from the result to both TSDB and the computed points KAFKA topic
      Timestamp timestampToEvaluate = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
      Map<String, String> functionState = new HashMap<>();
      Result result = customPoint.evaluateFormula(timestampToEvaluate, variableValues, functionState);
      System.err.println("result: " + result);
      */
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
        .build();
    nodeHierarchyService.updateMappablePointNodes(updateMappablePointNodesRequest);
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
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
        .withPerformAutomaticRemediation(true)
        .withPerformAutomaticEvaluateReports(true)
        .withPerformAutomaticConfiguration(true)
        .withData(updateMappablePointNodeRequestDtoList)
        .build();
    nodeHierarchyService.updateMappablePointNodes(updateMappablePointNodesRequest);
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building = portfolio.getChildBuilding(buildingId);
    floorOne = building.getChildFloor(floorOneId);
    floorTwo = building.getChildFloor(floorTwoId);
    
    
    /*
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
        //.withCandidateIds(candidateIds) RP-9411: Treat an empty request as "create all"
        .build();
    List<AbstractAdFunctionInstanceEntity> createdInstances = nodeHierarchyService.createAdFunctionInstancesFromCandidates(createRequest);
    Assert.assertEquals("createdInstances size is incorrect", 
        Integer.toString(candidateIds.size()),
        Integer.toString(createdInstances.size()));    
    
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
        .withPerformAutomaticEvaluateReports(Boolean.TRUE)
        .withSubmittedBy("tmyers@resolutebi.com")
        //.withCandidateIds(candidateIds) RP-9411: Treat an empty request as "create all"
        .build();
    createdInstances = nodeHierarchyService.createAdFunctionInstancesFromCandidates(createRequest);
    Assert.assertEquals("createdInstances size is incorrect", 
        Integer.toString(candidateIds.size()),
        Integer.toString(createdInstances.size()));    
    
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building = portfolio.getChildBuilding(buildingId);
    floorOne = building.getChildFloor(floorOneId);
    floorTwo = building.getChildFloor(floorTwoId);
    */

    
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
        .withSubmittedBy("tmyers@resolutebi.com")
        .withPerformAutomaticRemediation(true)
        .withPerformAutomaticEvaluateReports(true)
        .withPerformAutomaticConfiguration(true)
        .build();
    nodeHierarchyService.updateAdFunctionInstances(updateAdFunctionsRequest);
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building = portfolio.getChildBuilding(buildingId);
    floorOne = building.getChildFloor(floorOneId);
    floorTwo = building.getChildFloor(floorTwoId);
    
    // Verify that all were updated.
    int numAdRuleFunctionInstancesAfter = portfolio.getAllAdFunctionInstances(FunctionType.RULE).size();
    Assert.assertEquals("numAdRuleFunctionInstancesAfter is incorrect", 
        Integer.toString(numAdRuleFunctionInstancesBefore),
        Integer.toString(numAdRuleFunctionInstancesAfter));    
    
    for (AbstractAdFunctionInstanceEntity instance: portfolio.getAllAdFunctionInstances(FunctionType.RULE)) {
      
      Assert.assertEquals("instant input constant value is incorrect: "
              + instance.getAdFunctionTemplate()
              + ", version: "
              + instance.getAdFunctionTemplate().getVersion()
              + ", instanceTemplateVersion: "
              + instance.getTemplateVersion(), 
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
    nodeHierarchyService.evaluateReports(evaluateReportsRequest);
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building = portfolio.getChildBuilding(buildingId);
    floorOne = building.getChildFloor(floorOneId);
    floorTwo = building.getChildFloor(floorTwoId);
    */
    
    
    // ***********************************************
    // Enable all reports.
    /*
    for (BuildingEntity childBuilding: portfolio.getChildBuildings()) {
    
      List<Integer> enabledReportTemplateIds = new ArrayList<>();
      for (ReportInstanceEntity reportInstance: childBuilding.getReportInstances()) {
        
        boolean isValid = reportInstance.isValid();
        Assert.assertEquals("reportInstance isValid is incorrect for: " + reportInstance,
            "true",
            Boolean.toString(isValid));
        
        enabledReportTemplateIds.add(reportInstance.getReportTemplate().getPersistentIdentity());
      }
      SetBuildingReportEnabledState enabledReportsRequest = SetBuildingReportEnabledState
          .builder()
          .withBuildingId(childBuilding.getPersistentIdentity())
          .withDisabledReportTemplateIds(new ArrayList<>())
          .withEnabledReportTemplateIds(enabledReportTemplateIds)
          .build(); 
      SetBuildingsReportEnabledStateRequest setBuildingsReportEnabledStateRequest = SetBuildingsReportEnabledStateRequest
          .builder()
          .withCustomerId(customerId)
          .withSetBuildingReportEnabledState(enabledReportsRequest)
          .build();
      nodeHierarchyService.setReportInstanceEnabledState(setBuildingsReportEnabledStateRequest);
      portfolio = nodeHierarchyService.loadPortfolio(customerId);
      building = portfolio.getChildBuilding(buildingId);
      floorOne = building.getChildFloor(floorOneId);
      floorTwo = building.getChildFloor(floorTwoId);
    }
    */
    
    
    Double adRuleFunctionPct = nodeHierarchyService.getAdFunctionConfigurationStatusPercent(customerId, FunctionType.RULE.getName());
    
    Double adComputedPointFunctionPct = nodeHierarchyService.getAdFunctionConfigurationStatusPercent(customerId, FunctionType.COMPUTED_POINT.getName());
    
    AdFunctionErrorMessageSearchResponse adRuleFunctionErrorMessages = nodeHierarchyService.getAdFunctionErrorMessages(customerId, AdFunctionErrorMessageSearchCriteria
        .builder()
        .withFunctionTypeId(AdFunctionErrorMessageSearchCriteria.FUNCTION_TYPE_RULE)
        .build());
    
    AdFunctionErrorMessageSearchResponse adComputedPointFunctionErrorMessages = nodeHierarchyService.getAdFunctionErrorMessages(customerId, AdFunctionErrorMessageSearchCriteria
        .builder()
        .withFunctionTypeId(AdFunctionErrorMessageSearchCriteria.FUNCTION_TYPE_COMPUTED_POINT)
        .build());
    
    
    System.err.println("adRuleFunctionPct: " + adRuleFunctionPct);
    System.err.println("adComputedPointFunctionPct: " + adComputedPointFunctionPct);

    System.err.println(AbstractEntity.OBJECT_WRITER.get().writeValueAsString(adRuleFunctionErrorMessages));
    System.err.println();
    System.err.println(AbstractEntity.OBJECT_WRITER.get().writeValueAsString(adComputedPointFunctionErrorMessages));
    System.err.println();
      

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
        .withBuildingId(buildingId)
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
    // Test remediations.
    
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
        .build();
    nodeHierarchyService.deleteAdFunctionInstances(deleteAdFunctionsRequest);
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
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
        .build(); 
    nodeHierarchyService.updateEnergyExchangeSystemNodes(updateEquipmentNodesRequest);
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
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
        .build();
    nodeHierarchyService.updateMappablePointNodes(updateMappablePointNodesRequest);
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building = portfolio.getChildBuilding(buildingId);
    floorOne = building.getChildFloor(floorOneId);
    floorTwo = building.getChildFloor(floorTwoId);
    
    
    // ***********************************************
    // Remediate portfolio (all function instances should be deleted/deactivated).
    RemediatePortfolioRequest remediatePortfolioRequest = RemediatePortfolioRequest
        .builder()
        .withCustomerId(customerId)
        .build();
    nodeHierarchyService.remediatePortfolio(remediatePortfolioRequest);
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
    nodeHierarchyService.remediatePortfolio(remediatePortfolioRequest);
    List<Integer> childIds = new ArrayList<>();
    for (BuildingEntity childBuilding: portfolio.getChildBuildings()) {
      
      childIds.add(childBuilding.getPersistentIdentity());
    }
    DeleteChildNodesRequest request = DeleteChildNodesRequest
        .builder()
        .withCustomerId(customerId)
        .withChildIds(childIds)
        .build();
    nodeHierarchyService.deleteChildNodes(request);
    
    
    // ***********************************************
    // ==VERIFY THAT WE ARE BACK TO GROUND STATE==
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    Assert.assertEquals("number of child buildings is incorrect",
        "0",
        Integer.toString(portfolio.getChildBuildings().size()));
    /*
    System.err.println("Number of published events: " + MockDictionaryChangeEventPublisher.PUBLISHED_EVENTS.size());
    for (AbstractEvent event: MockDictionaryChangeEventPublisher.PUBLISHED_EVENTS) {
      System.err.println(event);
    }
    */
  }

  protected void mapRawPoints(
      int numPointsToMap,
      String metricIdPattern,
      String mappingExpression) throws Exception {
    
    mapRawPoints(
        dictionaryService.getPaymentPlansContainer().getMaxPointCap(),
        numPointsToMap, 
        metricIdPattern, 
        mappingExpression,
        null);
  }

  protected void mapRawPoints(
      int numPointsToMap,
      String metricIdPattern,
      String mappingExpression,
      String buildingNameFilter) throws Exception {
    
    mapRawPoints(
        dictionaryService.getPaymentPlansContainer().getMaxPointCap(),
        numPointsToMap, 
        metricIdPattern, 
        mappingExpression,
        buildingNameFilter);
  }  
  
  protected void mapRawPoints(
      int maxPointCap,
      int numPointsToMap,
      String metricIdPattern,
      String mappingExpression) throws Exception {
  
    mapRawPoints(
        maxPointCap,
        numPointsToMap,
        metricIdPattern,
        mappingExpression,
        null);
  }
  
  protected void mapRawPoints(
      int maxPointCap,
      int numPointsToMap,
      String metricIdPattern,
      String mappingExpression,
      String buildingNameFilter) throws Exception {
    
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
        .withBuildingName(buildingNameFilter)
        .withMappingExpression(mappingExpression)
        .withMetricIdDelimiter(NodeHierarchyService.DEFAULT_METRIC_ID_DELIMITER)
        .build(); 
    createdMappablePoints = nodeHierarchyService.mapRawPoints(mapRawPointsRequest);
    
    if (numPointsToMap < maxPointCap) {
      Assert.assertEquals("created mappable points list size is incorrect", 
          Integer.toString(numPointsToMap), 
          Integer.toString(createdMappablePoints.size()));
    } else {
      Assert.assertEquals("created mappable points list size is incorrect", 
          Integer.toString(maxPointCap), 
          Integer.toString(createdMappablePoints.size()));
    }
    
    // Reload the domain entities
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    customer = portfolio.getParentCustomer();
    distributor = customer.getParentDistributor();
  }
  
  @Test
  public void evaluateReports_rp9184_nothingBeingStored() throws Exception {
    
    try {
      
      // STEP 1: ARRANGE
      customerId = 106;
      Integer buildingId = 1522154;
      Integer equipmentId = 1526316;
      Integer reportTemplateId = 12;
      Integer pointSpecId = 82;
      Integer pointId = 1542903;
      
      ReportEvaluator.DEBUG_REPORT_TEMPLATE_ID = reportTemplateId;
      ReportEvaluator.DEBUG_POINT_SPEC_ID = pointSpecId;
      ReportEvaluator.DEBUG_EQUIPMENT_ID = equipmentId;
      ReportEvaluator.DEBUG_POINT_ID = pointId;
      
      String path = TEST_DATA_PATH + "/rp9184"; 
      unzipTestFile(path, "rp-9184.zip");
      ((DistributorRepositoryFileSystemImpl)distributorRepository).setBasePath(path);
      ((CustomerRepositoryFileSystemImpl)customerRepository).setBasePath(path);
      ((NodeHierarchyRepositoryFileSystemImpl)nodeHierarchyRepository).setBasePath(path);
      
      portfolio = nodeHierarchyService.loadPortfolio(customerId);
      BuildingEntity building = portfolio.getChildBuilding(buildingId);
      EquipmentEntity equipment = portfolio.getEquipment(equipmentId);
      ReportInstanceEntity reportInstanceBefore = building.getReportInstanceByReportTemplateId(reportTemplateId);
      List<Integer> errorMessagesBefore = reportInstanceBefore.getEquipmentErrorMessages(equipment);
      
      EvaluateReportsRequest evaluateReportsRequest = EvaluateReportsRequest
          .builder()
          .withCustomerId(customerId)
          .withBuildingId(buildingId)
          .build();
      
      
      
      // STEP 2: ACT
      List<ReportInstanceStatusDto> reportInstanceStatusDtoList = nodeHierarchyService.evaluateReports(evaluateReportsRequest);
      
      
      
      // STEP 3: ASSERT
      System.err.println("reportInstanceStatusDtoList size: " + reportInstanceStatusDtoList.size());
      System.err.println("reportInstanceStatusDtoList: " + reportInstanceStatusDtoList);

      System.err.println("BEFORE: errorMessages size: " + errorMessagesBefore.size());
      System.err.println("BEFORE: errorMessages: " + errorMessagesBefore);
      
      portfolio = nodeHierarchyService.loadPortfolio(customerId);
      building = portfolio.getChildBuilding(buildingId);
      equipment = portfolio.getEquipment(equipmentId);
      ReportInstanceEntity reportInstanceAfter = building.getReportInstanceByReportTemplateId(reportTemplateId);
      
      List<Integer> errorMessagesAfter = reportInstanceAfter.getEquipmentErrorMessages(equipment);
      System.err.println("BEFORE: errorMessages size: " + errorMessagesAfter.size());
      System.err.println("BEFORE: errorMessages: " + errorMessagesAfter);
      
    } finally {
      ((DistributorRepositoryFileSystemImpl)distributorRepository).setBasePath(TEST_DATA_PATH);
      ((CustomerRepositoryFileSystemImpl)customerRepository).setBasePath(TEST_DATA_PATH);
      ((NodeHierarchyRepositoryFileSystemImpl)nodeHierarchyRepository).setBasePath(TEST_DATA_PATH);      
    }
  }    
  
  @Ignore
  @Test
  public void evaluateReports_rp10306_changeStateAndPersistenceOptimization() throws Exception {
    
    try {
      
      // STEP 1: ARRANGE
      Integer reportTemplateId = 2;
      Integer buildingId = 460;
      Integer equipmentId = 8258; // Redico/Blue Care Network/North/AC_1
      
      ReportEvaluator.DEBUG_REPORT_TEMPLATE_ID = reportTemplateId;
      ReportEvaluator.DEBUG_EQUIPMENT_ID = equipmentId;
      
      /*
      select * from generated_report_instances where generated_report_template_id = 2 and node_id = 460;
      select * from generated_report_instance_equipment e join node_names nn on e.equipment_id = nn.id where e.generated_report_instance_id = 17078 order by nn.fully_qualified_name;
      select * from generated_report_instance_statuses where generated_report_template_id = 2 and building_id = 460;
      select * from generated_report_instance_status_error_messages where generated_report_template_id = 2 and building_id = 460;      
      
      Integer pointSpecId = 82;
      Integer pointId = 1542903;
      
      ReportEvaluator.DEBUG_POINT_SPEC_ID = pointSpecId;
      ReportEvaluator.DEBUG_POINT_ID = pointId;
      */
      
      String path = TEST_DATA_PATH + "/rp10306"; 
      unzipTestFile(path, "rp-10306.zip");
      ((DictionaryRepositoryFileSystemImpl)dictionaryRepository).setBasePath(path);
      ((DictionaryRepositoryFileSystemImpl)dictionaryRepository).invalidateDictionaryData();
      ((DictionaryRepositoryFileSystemImpl)dictionaryRepository).ensureDictionaryDataIsLoaded();
      ((NodeHierarchyRepositoryFileSystemImpl)nodeHierarchyRepository).setBasePath(path);
      
      portfolio = nodeHierarchyService.loadPortfolio(9);
      BuildingEntity building = portfolio.getChildBuilding(buildingId);
      ReportInstanceEntity reportInstanceBefore = building.getReportInstanceByReportTemplateId(reportTemplateId);
      
      Set<ReportInstanceEquipmentEntity> greenEquipmentBefore = reportInstanceBefore.getReportInstanceEquipment();
      Set<ReportInstanceEquipmentErrorMessagesEntity> redEquipmentBefore = reportInstanceBefore.getReportInstanceEquipmentErrorMessages();
      
      EvaluateReportsRequest evaluateReportsRequest = EvaluateReportsRequest
          .builder()
          .withCustomerId(9)
          .withBuildingId(buildingId)
          .build();
      
      List<ReportInstanceStatusDto> reportInstanceStatusDtoList = nodeHierarchyService.evaluateReports(evaluateReportsRequest);
      Assert.assertEquals("modified report instances size is incorrect", 
          "12", 
          Integer.toString(reportInstanceStatusDtoList.size()));
      
      
      
      // STEP 2: ACT
      reportInstanceStatusDtoList = nodeHierarchyService.evaluateReports(evaluateReportsRequest);
      
      
      
      // STEP 3: ASSERT
      Assert.assertEquals("modified report instances size is incorrect", 
          "0", 
          Integer.toString(reportInstanceStatusDtoList.size()));
      
      portfolio = nodeHierarchyService.loadPortfolio(9);
      building = portfolio.getChildBuilding(buildingId);
      ReportInstanceEntity reportInstanceAfter = building.getReportInstanceByReportTemplateId(reportTemplateId);
      
      Set<ReportInstanceEquipmentEntity> greenEquipmentAfter = reportInstanceAfter.getReportInstanceEquipment();
      Set<ReportInstanceEquipmentErrorMessagesEntity> redEquipmentAfter = reportInstanceAfter.getReportInstanceEquipmentErrorMessages();

      Assert.assertEquals("green equipment is incorrect", 
          greenEquipmentBefore.toString(), 
          greenEquipmentAfter.toString());

      Assert.assertEquals("red equipment is incorrect", 
          redEquipmentBefore.toString(), 
          redEquipmentAfter.toString());
      
    } finally {
      ((DictionaryRepositoryFileSystemImpl)dictionaryRepository).setBasePath(TEST_DATA_PATH);
      ((DictionaryRepositoryFileSystemImpl)dictionaryRepository).invalidateDictionaryData();
      ((DictionaryRepositoryFileSystemImpl)dictionaryRepository).ensureDictionaryDataIsLoaded();
      ((NodeHierarchyRepositoryFileSystemImpl)nodeHierarchyRepository).setBasePath(TEST_DATA_PATH);      
    }
  }
  
  @Test
  public void energyExchangeSystems() throws Exception {
    
    // ***********************************************
    // Create building 1.
    Map<String, Object> additionalProperties = new HashMap<>();
    CreateNodeRequest createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.BUILDING)
        .withParentId(null)
        .withName("Building_1")
        .withDisplayName("Building_1")
        .withAdditionalProperties(additionalProperties)
        .build();
    BuildingEntity building1 = (BuildingEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer building1Id = building1.getPersistentIdentity();


    // ***********************************************
    // Create plant 1.
    createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.PLANT)
        .withParentId(building1Id)
        .withName("Plant_1")
        .withDisplayName("Plant_1")
        .withAdditionalProperties(additionalProperties)
        .build();
    PlantEntity plant1 = (PlantEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer plant1Id = plant1.getPersistentIdentity();
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    plant1 = portfolio.getPlant(plant1Id);
    Assert.assertNotNull("plant1 is null", plant1);
    
    
    // ***********************************************
    // Create loop 1.
    additionalProperties.put("energyExchangeSystemTypeId", CreateNodeRequest.AIR_SUPPLY_SYSTEM_TYPE_ID);
    createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.LOOP)
        .withParentId(plant1Id)
        .withName("Loop_1")
        .withDisplayName("Loop_1")
        .withAdditionalProperties(additionalProperties)
        .build();
    LoopEntity loop1 = (LoopEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer loop1Id = loop1.getPersistentIdentity();
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    loop1 = portfolio.getLoop(loop1Id);
    Assert.assertNotNull("loop1 is null", loop1);
    additionalProperties.clear();

    
    // ***********************************************
    // Create pump 1.
    createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.EQUIPMENT)
        .withParentId(building1Id)
        .withName("Pump_1")
        .withDisplayName("Pump_1")
        .withAdditionalProperties(additionalProperties)
        .build();
    EquipmentEntity pump1 = (EquipmentEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer pump1Id = pump1.getPersistentIdentity();    
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    pump1 = portfolio.getEquipment(pump1Id);
    Assert.assertNotNull("pump1 is null", pump1);

    
    // ***********************************************
    // Create pump 2.
    createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.EQUIPMENT)
        .withParentId(building1Id)
        .withName("Pump_2")
        .withDisplayName("Pump_2")
        .withAdditionalProperties(additionalProperties)
        .build();
    EquipmentEntity pump2 = (EquipmentEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer pump2Id = pump2.getPersistentIdentity();    
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    pump2 = portfolio.getEquipment(pump2Id);
    Assert.assertNotNull("pump2 is null", pump2);

    
    // ***********************************************
    // Create chiller 1.
    createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.EQUIPMENT)
        .withParentId(building1Id)
        .withName("Chiller_1")
        .withDisplayName("Chiller_1")
        .withAdditionalProperties(additionalProperties)
        .build();
    EquipmentEntity chiller1 = (EquipmentEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer chiller1Id = chiller1.getPersistentIdentity();    
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    chiller1 = portfolio.getEquipment(chiller1Id);
    Assert.assertNotNull("chiller1 is null", chiller1);

    
    // ***********************************************
    // Create chiller 2.
    createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.EQUIPMENT)
        .withParentId(building1Id)
        .withName("Chiller_2")
        .withDisplayName("Chiller_2")
        .withAdditionalProperties(additionalProperties)
        .build();
    EquipmentEntity chiller2 = (EquipmentEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer chiller2Id = chiller2.getPersistentIdentity();    
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    chiller2 = portfolio.getEquipment(chiller2Id);
    Assert.assertNotNull("chiller2 is null", chiller2);
    
    
    // ***********************************************
    // Tag the plant/loop/equipment.
    TagsContainer tagsContainer = dictionaryService.getTagsContainer();
    PlantEnergyExchangeTypeEntity chilledWaterPlantType = tagsContainer.getPlantTypeByName("chilledWaterPlant");
    LoopEnergyExchangeTypeEntity primaryLoopType = tagsContainer.getLoopTypeByName("primaryLoop");
    EquipmentEnergyExchangeTypeEntity pumpEquipmentType = tagsContainer.getEquipmentTypeByName("pump");
    EquipmentEnergyExchangeTypeEntity chillerEquipmentType = tagsContainer.getEquipmentTypeByName("chiller");
    List<String> equipmentMetadataTags = Arrays.asList("rooftop");
    List<EnergyExchangeSystemNodeData> energyExchangeSystemNodeData = new ArrayList<>();

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(plant1Id)
        .withDisplayName(plant1.getDisplayName() + "_updated")
        .withTypeId(chilledWaterPlantType.getPersistentIdentity())
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_PLANT)
        .build());

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(loop1Id)
        .withDisplayName(loop1.getDisplayName() + "_updated")
        .withTypeId(primaryLoopType.getPersistentIdentity())
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_LOOP)
        //.withSystemTypeId(EnergyExchangeSystemType.AIR_SUPPLY.getId()) // No need for this, as it is done when the loop is created
        //.withParentIds(Arrays.asList(plant1Id))
        .build());
    
    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(pump1Id)
        .withDisplayName(pump1.getDisplayName() + "_updated")
        .withTypeId(pumpEquipmentType.getPersistentIdentity())
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .withSystemTypeId(EnergyExchangeSystemType.AIR_SUPPLY.getId())
        .withParentIds(Arrays.asList(loop1Id))
        .withMetadataTags(equipmentMetadataTags)
        .build());

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(pump2Id)
        .withDisplayName(pump2.getDisplayName() + "_updated")
        .withTypeId(pumpEquipmentType.getPersistentIdentity())
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .withSystemTypeId(EnergyExchangeSystemType.AIR_SUPPLY.getId())
        .withParentIds(Arrays.asList(loop1Id))
        .withMetadataTags(equipmentMetadataTags)
        .build());

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(chiller1Id)
        .withDisplayName(chiller1.getDisplayName() + "_updated")
        .withTypeId(chillerEquipmentType.getPersistentIdentity())
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .withSystemTypeId(EnergyExchangeSystemType.AIR_SUPPLY.getId())
        .withParentIds(Arrays.asList(loop1Id, pump1Id))
        .withMetadataTags(equipmentMetadataTags)
        .build());

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(chiller2Id)
        .withDisplayName(chiller2.getDisplayName() + "_updated")
        .withTypeId(chillerEquipmentType.getPersistentIdentity())
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .withSystemTypeId(EnergyExchangeSystemType.AIR_SUPPLY.getId())
        .withParentIds(Arrays.asList(loop1Id, pump2Id))
        .withMetadataTags(equipmentMetadataTags)
        .build());
    
    UpdateEnergyExchangeSystemNodesRequest updateEnergyExchangeSystemNodesRequest = UpdateEnergyExchangeSystemNodesRequest
        .builder()
        .withBuildingId(building1Id)
        .withCustomerId(customerId)
        .withSubmittedBy("tmyers@resolutebi.com")
        .withData(energyExchangeSystemNodeData)
        .build(); 
    nodeHierarchyService.updateEnergyExchangeSystemNodes(updateEnergyExchangeSystemNodesRequest);
    

    // **********************************************************************************************
    // Verify plant/loop/equipment type tag and energy exchange system parent/child relationships
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building1 = portfolio.getChildBuilding(building1Id);
    plant1 = portfolio.getPlant(plant1Id);
    loop1 = portfolio.getLoop(loop1Id);
    pump1 = portfolio.getEquipment(pump1Id);
    pump2 = portfolio.getEquipment(pump2Id);
    chiller1 = portfolio.getEquipment(chiller1Id);
    chiller2 = portfolio.getEquipment(chiller2Id);
    
    Assert.assertNotNull("plant1 plant type is null", plant1.getPlantTypeNullIfNotExists());
    Assert.assertNotNull("loop1 loop type is null", loop1.getLoopTypeNullIfNotExists());
    Assert.assertNotNull("pump1 equipment type is null", pump1.getEquipmentTypeNullIfNotExists());
    Assert.assertNotNull("pump2 equipment type is null", pump2.getEquipmentTypeNullIfNotExists());
    Assert.assertNotNull("chiller1 equipment type is null", chiller1.getEquipmentTypeNullIfNotExists());
    Assert.assertNotNull("chiller2 equipment type is null", chiller2.getEquipmentTypeNullIfNotExists());
    
    Assert.assertEquals("plant1 plant type is incorrect", chilledWaterPlantType, plant1.getPlantTypeNullIfNotExists());
    Assert.assertEquals("loop1 loop type is incorrect", primaryLoopType, loop1.getLoopTypeNullIfNotExists());
    Assert.assertEquals("pump1 equipment type is incorrect", pumpEquipmentType, pump1.getEquipmentTypeNullIfNotExists());
    Assert.assertEquals("pump2 equipment type is incorrect", pumpEquipmentType, pump2.getEquipmentTypeNullIfNotExists());
    Assert.assertEquals("chiller1 equipment type is incorrect", chillerEquipmentType, chiller1.getEquipmentTypeNullIfNotExists());
    Assert.assertEquals("chiller2 equipment type is incorrect", chillerEquipmentType, chiller2.getEquipmentTypeNullIfNotExists());
    
    Assert.assertTrue("plant1 displayName is incorrect", plant1.getDisplayName().endsWith("_updated"));
    Assert.assertTrue("loop1 displayName is incorrect", loop1.getDisplayName().endsWith("_updated"));
    Assert.assertTrue("pump1 displayName is incorrect", pump1.getDisplayName().endsWith("_updated"));
    Assert.assertTrue("pump2 displayName is incorrect", pump2.getDisplayName().endsWith("_updated"));
    Assert.assertTrue("chiller1 displayName is incorrect", chiller1.getDisplayName().endsWith("_updated"));
    Assert.assertTrue("chiller2 displayName is incorrect", chiller2.getDisplayName().endsWith("_updated"));

    Assert.assertTrue("pump1 metadata tags is incorrect", pump1.getMetadataTags().toString().equals(equipmentMetadataTags.toString()));
    Assert.assertTrue("pump2 metadata tags is incorrect", pump2.getMetadataTags().toString().equals(equipmentMetadataTags.toString()));
    Assert.assertTrue("chiller1 metadata tags is incorrect", chiller1.getMetadataTags().toString().equals(equipmentMetadataTags.toString()));
    Assert.assertTrue("chiller2 metadata tags is incorrect", chiller2.getMetadataTags().toString().equals(equipmentMetadataTags.toString()));
    
    Set<EnergyExchangeEntity> plant1ParentEnergyExchangeNodes = plant1.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("plant1ParentEnergyExchangeNodes is incorrect", plant1ParentEnergyExchangeNodes.isEmpty());
    
    Set<EnergyExchangeEntity> plant1ChildEnergyExchangeNodes = plant1.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("plant1ChildEnergyExchangeNodes is incorrect", plant1ChildEnergyExchangeNodes.contains(loop1));

    Set<EnergyExchangeEntity> loop1ParentEnergyExchangeNodes = loop1.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("loop1ParentEnergyExchangeNodes is incorrect", loop1ParentEnergyExchangeNodes.contains(plant1));
    
    Set<EnergyExchangeEntity> loop1ChildEnergyExchangeNodes = loop1.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("loop1ChildEnergyExchangeNodes is incorrect", loop1ChildEnergyExchangeNodes.contains(pump1));
    Assert.assertTrue("loop1ChildEnergyExchangeNodes is incorrect", loop1ChildEnergyExchangeNodes.contains(pump2));
    Assert.assertTrue("loop1ChildEnergyExchangeNodes is incorrect", loop1ChildEnergyExchangeNodes.contains(chiller1));
    Assert.assertTrue("loop1ChildEnergyExchangeNodes is incorrect", loop1ChildEnergyExchangeNodes.contains(chiller2));

    Set<EnergyExchangeEntity> pump1ParentEnergyExchangeNodes = pump1.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("pump1ParentEnergyExchangeNodes is incorrect", pump1ParentEnergyExchangeNodes.contains(loop1));
    
    Set<EnergyExchangeEntity> pump1ChildEnergyExchangeNodes = pump1.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("pump1ChildEnergyExchangeNodes is incorrect", pump1ChildEnergyExchangeNodes.contains(chiller1));

    Set<EnergyExchangeEntity> pump2ParentEnergyExchangeNodes = pump2.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("pump2ParentEnergyExchangeNodes is incorrect", pump2ParentEnergyExchangeNodes.contains(loop1));
    
    Set<EnergyExchangeEntity> pump2ChildEnergyExchangeNodes = pump2.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("pump2ChildEnergyExchangeNodes is incorrect", pump2ChildEnergyExchangeNodes.contains(chiller2));

    Set<EnergyExchangeEntity> chiller1ParentEnergyExchangeNodes = chiller1.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("chiller1ParentEnergyExchangeNodes is incorrect", chiller1ParentEnergyExchangeNodes.contains(loop1));
    Assert.assertTrue("chiller1ParentEnergyExchangeNodes is incorrect", chiller1ParentEnergyExchangeNodes.contains(pump1));
    
    Set<EnergyExchangeEntity> chiller1ChildEnergyExchangeNodes = chiller1.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("chiller1ChildEnergyExchangeNodes is incorrect", chiller1ChildEnergyExchangeNodes.isEmpty());

    Set<EnergyExchangeEntity> chiller2ParentEnergyExchangeNodes = chiller2.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("chiller2ParentEnergyExchangeNodes is incorrect", chiller2ParentEnergyExchangeNodes.contains(loop1));
    Assert.assertTrue("chiller2ParentEnergyExchangeNodes is incorrect", chiller2ParentEnergyExchangeNodes.contains(pump2));
    
    Set<EnergyExchangeEntity> chiller2ChildEnergyExchangeNodes = chiller2.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("chiller2ChildEnergyExchangeNodes is incorrect", chiller2ChildEnergyExchangeNodes.isEmpty());
    

    // ***********************************************
    // Untag the plant/loop/equipment.
    equipmentMetadataTags = new ArrayList<>();
    energyExchangeSystemNodeData = new ArrayList<>();

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(plant1Id)
        .withTypeId(null)
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_PLANT)
        .withSystemTypeId(EnergyExchangeSystemType.AIR_SUPPLY.getId())
        .withParentIds(new ArrayList<>())
        .build());

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(loop1Id)
        .withTypeId(null)
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_LOOP)
        .withSystemTypeId(EnergyExchangeSystemType.AIR_SUPPLY.getId())
        .withParentIds(new ArrayList<>())
        .build());
    
    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(pump1Id)
        .withTypeId(null)
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .withSystemTypeId(EnergyExchangeSystemType.AIR_SUPPLY.getId())
        .withParentIds(new ArrayList<>())
        .withMetadataTags(equipmentMetadataTags)
        .build());

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(pump2Id)
        .withTypeId(null)
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .withSystemTypeId(EnergyExchangeSystemType.AIR_SUPPLY.getId())
        .withParentIds(new ArrayList<>())
        .withMetadataTags(equipmentMetadataTags)
        .build());

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(chiller1Id)
        .withTypeId(null)
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .withSystemTypeId(EnergyExchangeSystemType.AIR_SUPPLY.getId())
        .withParentIds(new ArrayList<>())
        .withMetadataTags(equipmentMetadataTags)
        .build());

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(chiller2Id)
        .withTypeId(null)
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .withSystemTypeId(EnergyExchangeSystemType.AIR_SUPPLY.getId())
        .withParentIds(new ArrayList<>())
        .withMetadataTags(equipmentMetadataTags)
        .build());
    
    updateEnergyExchangeSystemNodesRequest = UpdateEnergyExchangeSystemNodesRequest
        .builder()
        .withBuildingId(building1Id)
        .withCustomerId(customerId)
        .withSubmittedBy("tmyers@resolutebi.com")
        .withData(energyExchangeSystemNodeData)
        .build(); 
    nodeHierarchyService.updateEnergyExchangeSystemNodes(updateEnergyExchangeSystemNodesRequest);
    
    
    // **********************************************************************************************
    // Verify plant/loop/equipment type tag and energy exchange system parent/child relationships
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    plant1 = portfolio.getPlant(plant1Id);
    
    // RP-12902: Verify that the child loop was automatically deleted.
    Assert.assertTrue("child loop was not automatically deleted", plant1.getChildLoops().isEmpty());
    
    pump1 = portfolio.getEquipment(pump1Id);
    pump2 = portfolio.getEquipment(pump2Id);
    chiller1 = portfolio.getEquipment(chiller1Id);
    chiller2 = portfolio.getEquipment(chiller2Id);
    
    Assert.assertNull("plant1 plant type is not null", plant1.getPlantTypeNullIfNotExists());
    Assert.assertNull("pump1 equipment type is not null", pump1.getEquipmentTypeNullIfNotExists());
    Assert.assertNull("pump2 equipment type is not null", pump2.getEquipmentTypeNullIfNotExists());
    Assert.assertNull("chiller1 equipment type is not null", chiller1.getEquipmentTypeNullIfNotExists());
    Assert.assertNull("chiller2 equipment type is not null", chiller2.getEquipmentTypeNullIfNotExists());
    
    Assert.assertTrue("plant1 displayName is incorrect", plant1.getDisplayName().endsWith("_updated"));
    Assert.assertTrue("pump1 displayName is incorrect", pump1.getDisplayName().endsWith("_updated"));
    Assert.assertTrue("pump2 displayName is incorrect", pump2.getDisplayName().endsWith("_updated"));
    Assert.assertTrue("chiller1 displayName is incorrect", chiller1.getDisplayName().endsWith("_updated"));
    Assert.assertTrue("chiller2 displayName is incorrect", chiller2.getDisplayName().endsWith("_updated"));

    Assert.assertTrue("pump1 metadata tags is incorrect", pump1.getMetadataTags().isEmpty());
    Assert.assertTrue("pump2 metadata tags is incorrect", pump2.getMetadataTags().isEmpty());
    Assert.assertTrue("chiller1 metadata tags is incorrect", chiller1.getMetadataTags().isEmpty());
    Assert.assertTrue("chiller2 metadata tags is incorrect", chiller2.getMetadataTags().isEmpty());
    
    plant1ParentEnergyExchangeNodes = plant1.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("plant1ParentEnergyExchangeNodes is incorrect", plant1ParentEnergyExchangeNodes.isEmpty());
    
    plant1ChildEnergyExchangeNodes = plant1.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("plant1ChildEnergyExchangeNodes is incorrect", plant1ChildEnergyExchangeNodes.isEmpty());

    pump1ParentEnergyExchangeNodes = pump1.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("pump1ParentEnergyExchangeNodes is incorrect", pump1ParentEnergyExchangeNodes.isEmpty());
    
    pump1ChildEnergyExchangeNodes = pump1.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("pump1ChildEnergyExchangeNodes is incorrect", pump1ChildEnergyExchangeNodes.isEmpty());

    pump2ParentEnergyExchangeNodes = pump2.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("pump2ParentEnergyExchangeNodes is incorrect", pump2ParentEnergyExchangeNodes.isEmpty());
    
    pump2ChildEnergyExchangeNodes = pump2.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("pump2ChildEnergyExchangeNodes is incorrect", pump2ChildEnergyExchangeNodes.isEmpty());

    chiller1ParentEnergyExchangeNodes = chiller1.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("chiller1ParentEnergyExchangeNodes is incorrect", chiller1ParentEnergyExchangeNodes.isEmpty());
    
    chiller1ChildEnergyExchangeNodes = chiller1.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("chiller1ChildEnergyExchangeNodes is incorrect", chiller1ChildEnergyExchangeNodes.isEmpty());

    chiller2ParentEnergyExchangeNodes = chiller2.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("chiller2ParentEnergyExchangeNodes is incorrect", chiller2ParentEnergyExchangeNodes.isEmpty());
    
    chiller2ChildEnergyExchangeNodes = chiller2.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("chiller2ChildEnergyExchangeNodes is incorrect", chiller2ChildEnergyExchangeNodes.isEmpty());
  } 
  
  @Test
  public void equipmentHierarchy_setParent() throws Exception {
    
    // ***********************************************
    // Create building 1.
    Map<String, Object> additionalProperties = new HashMap<>();
    CreateNodeRequest createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.BUILDING)
        .withParentId(null)
        .withName("Building_1")
        .withDisplayName("Building_1")
        .withAdditionalProperties(additionalProperties)
        .build();
    BuildingEntity building1 = (BuildingEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer building1Id = building1.getPersistentIdentity();


    // ***********************************************
    // Create rooftop AHU 1.
    createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.EQUIPMENT)
        .withParentId(building1Id)
        .withName("AHU")
        .withDisplayName("AHU")
        .withAdditionalProperties(additionalProperties)
        .build();
    EquipmentEntity ahu1 = (EquipmentEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer ahu1Id = ahu1.getPersistentIdentity();
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    ahu1 = portfolio.getEquipment(ahu1Id);
    Assert.assertNotNull("ahu is null", ahu1);

    
    // ***********************************************
    // Create vav 1.
    createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.EQUIPMENT)
        .withParentId(building1Id)
        .withName("VAV1")
        .withDisplayName("VAV1")
        .withAdditionalProperties(additionalProperties)
        .build();
    EquipmentEntity vav1 = (EquipmentEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer vav1Id = vav1.getPersistentIdentity();    
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    vav1 = portfolio.getEquipment(vav1Id);
    Assert.assertNotNull("vav1 is null", vav1);

    
    // ***********************************************
    // Create vav 2.
    createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.EQUIPMENT)
        .withParentId(building1Id)
        .withName("VAV2")
        .withDisplayName("VAV2")
        .withAdditionalProperties(additionalProperties)
        .build();
    EquipmentEntity vav2 = (EquipmentEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer vav2Id = vav2.getPersistentIdentity();    
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    vav2 = portfolio.getEquipment(vav2Id);
    Assert.assertNotNull("vav2 is null", vav2);
    
    
    // ***********************************************
    // Tag the equipment.
    TagsContainer tagsContainer = dictionaryService.getTagsContainer();
    EquipmentEnergyExchangeTypeEntity ahuEquipmentType = tagsContainer.getEquipmentTypeByName("ahu");
    EquipmentEnergyExchangeTypeEntity vavEquipmentType = tagsContainer.getEquipmentTypeByName("vav");
    List<EnergyExchangeSystemNodeData> energyExchangeSystemNodeData = new ArrayList<>();

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(ahu1Id)
        .withTypeId(ahuEquipmentType.getPersistentIdentity())
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .withMetadataTags(Arrays.asList("rooftop"))
        .build());

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(vav1Id)
        .withTypeId(vavEquipmentType.getPersistentIdentity())
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .withSystemTypeId(EnergyExchangeSystemType.AIR_SUPPLY.getId())
        .withParentIds(Arrays.asList(ahu1Id))
        .build());

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(vav2Id)
        .withTypeId(vavEquipmentType.getPersistentIdentity())
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .withSystemTypeId(EnergyExchangeSystemType.AIR_SUPPLY.getId())
        .withParentIds(Arrays.asList(ahu1Id))
        .build());
    
    UpdateEnergyExchangeSystemNodesRequest updateEnergyExchangeSystemNodesRequest = UpdateEnergyExchangeSystemNodesRequest
        .builder()
        .withBuildingId(building1Id)
        .withCustomerId(customerId)
        .withSubmittedBy("tmyers@resolutebi.com")
        .withData(energyExchangeSystemNodeData)
        .build(); 
    nodeHierarchyService.updateEnergyExchangeSystemNodes(updateEnergyExchangeSystemNodesRequest);
    

    // **********************************************************************************************
    // Verify equipment type tag and energy exchange system parent/child relationships
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    ahu1 = portfolio.getEquipment(ahu1Id);
    vav1 = portfolio.getEquipment(vav1Id);
    vav2 = portfolio.getEquipment(vav2Id);
    
    Assert.assertNotNull("ahu1 equipment type is null", ahu1.getEquipmentTypeNullIfNotExists());
    Assert.assertNotNull("vav1 equipment type is null", vav1.getEquipmentTypeNullIfNotExists());
    Assert.assertNotNull("vav2 equipment type is null", vav2.getEquipmentTypeNullIfNotExists());
    
    Assert.assertEquals("ahu1 equipment type is incorrect", ahuEquipmentType, ahu1.getEquipmentTypeNullIfNotExists());
    Assert.assertEquals("vav1 equipment type is incorrect", vavEquipmentType, vav1.getEquipmentTypeNullIfNotExists());
    Assert.assertEquals("vav2 equipment type is incorrect", vavEquipmentType, vav2.getEquipmentTypeNullIfNotExists());
    
    Assert.assertTrue("ahu1 metadata tags is incorrect", ahu1.getMetadataTags().toString().equals(Arrays.asList("rooftop").toString()));
    
    Set<EnergyExchangeEntity> ahu1ParentEnergyExchangeNodes = ahu1.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("ahu1ParentEnergyExchangeNodes is incorrect", ahu1ParentEnergyExchangeNodes.isEmpty());
    
    Set<EnergyExchangeEntity> ahu1ChildEnergyExchangeNodes = ahu1.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("ahu1ChildEnergyExchangeNodes is incorrect", ahu1ChildEnergyExchangeNodes.contains(vav1));
    Assert.assertTrue("ahu1ChildEnergyExchangeNodes is incorrect", ahu1ChildEnergyExchangeNodes.contains(vav2));

    Set<EnergyExchangeEntity> vav1ParentEnergyExchangeNodes = vav1.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("vav1ParentEnergyExchangeNodes is incorrect", vav1ParentEnergyExchangeNodes.contains(ahu1));
    
    Set<EnergyExchangeEntity> vav1ChildEnergyExchangeNodes = vav1.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("vav1ChildEnergyExchangeNodes is incorrect", vav1ChildEnergyExchangeNodes.isEmpty());

    Set<EnergyExchangeEntity> vav2ParentEnergyExchangeNodes = vav2.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("vav2ParentEnergyExchangeNodes is incorrect", vav2ParentEnergyExchangeNodes.contains(ahu1));
    
    Set<EnergyExchangeEntity> vav2ChildEnergyExchangeNodes = vav2.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("vav2ChildEnergyExchangeNodes is incorrect", vav2ChildEnergyExchangeNodes.isEmpty());
    
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    
    // ***********************************************
    // Untag the equipment.
    energyExchangeSystemNodeData = new ArrayList<>();

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(ahu1Id)
        .withTypeId(null)
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .withMetadataTags(new ArrayList<>())
        .build());

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(vav1Id)
        .withTypeId(null)
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .withSystemTypeId(EnergyExchangeSystemType.AIR_SUPPLY.getId())
        .withParentIds(new ArrayList<>())
        .build());

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(vav2Id)
        .withTypeId(null)
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .withSystemTypeId(EnergyExchangeSystemType.AIR_SUPPLY.getId())
        .withParentIds(new ArrayList<>())
        .build());
    
    updateEnergyExchangeSystemNodesRequest = UpdateEnergyExchangeSystemNodesRequest
        .builder()
        .withBuildingId(building1Id)
        .withCustomerId(customerId)
        .withSubmittedBy("tmyers@resolutebi.com")
        .withData(energyExchangeSystemNodeData)
        .build(); 
    nodeHierarchyService.updateEnergyExchangeSystemNodes(updateEnergyExchangeSystemNodesRequest);
    
    
    // **********************************************************************************************
    // Verify equipment type tag and energy exchange system parent/child relationships
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    ahu1 = portfolio.getEquipment(ahu1Id);
    vav1 = portfolio.getEquipment(vav1Id);
    vav2 = portfolio.getEquipment(vav2Id);
    
    Assert.assertNull("ahu1 equipment type is not null", ahu1.getEquipmentTypeNullIfNotExists());
    Assert.assertNull("vav1 equipment type is not null", vav1.getEquipmentTypeNullIfNotExists());
    Assert.assertNull("vav2 equipment type is not null", vav2.getEquipmentTypeNullIfNotExists());
    
    Assert.assertTrue("ahu1 metadata tags is incorrect", ahu1.getMetadataTags().toString().equals(new ArrayList<>().toString()));
    
    ahu1ParentEnergyExchangeNodes = ahu1.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("ahu1ParentEnergyExchangeNodes is incorrect", ahu1ParentEnergyExchangeNodes.isEmpty());
    
    ahu1ChildEnergyExchangeNodes = ahu1.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("ahu1ChildEnergyExchangeNodes is incorrect", ahu1ChildEnergyExchangeNodes.isEmpty());

    vav1ParentEnergyExchangeNodes = vav1.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("vav1ParentEnergyExchangeNodes is incorrect", vav1ParentEnergyExchangeNodes.isEmpty());
    
    vav1ChildEnergyExchangeNodes = vav1.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("vav1ChildEnergyExchangeNodes is incorrect", vav1ChildEnergyExchangeNodes.isEmpty());

    vav2ParentEnergyExchangeNodes = vav2.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("vav2ParentEnergyExchangeNodes is incorrect", vav2ParentEnergyExchangeNodes.isEmpty());
    
    vav2ChildEnergyExchangeNodes = vav2.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("vav2ChildEnergyExchangeNodes is incorrect", vav2ChildEnergyExchangeNodes.isEmpty());  
  }
  
  @Test
  public void equipmentHierarchy_setChildren() throws Exception {
    
    // ***********************************************
    // Create building 1.
    Map<String, Object> additionalProperties = new HashMap<>();
    CreateNodeRequest createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.BUILDING)
        .withParentId(null)
        .withName("Building_1")
        .withDisplayName("Building_1")
        .withAdditionalProperties(additionalProperties)
        .build();
    BuildingEntity building1 = (BuildingEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer building1Id = building1.getPersistentIdentity();


    // ***********************************************
    // Create rooftop AHU 1.
    createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.EQUIPMENT)
        .withParentId(building1Id)
        .withName("AHU")
        .withDisplayName("AHU")
        .withAdditionalProperties(additionalProperties)
        .build();
    EquipmentEntity ahu1 = (EquipmentEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer ahu1Id = ahu1.getPersistentIdentity();
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    ahu1 = portfolio.getEquipment(ahu1Id);
    Assert.assertNotNull("ahu is null", ahu1);

    
    // ***********************************************
    // Create vav 1.
    createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.EQUIPMENT)
        .withParentId(building1Id)
        .withName("VAV1")
        .withDisplayName("VAV1")
        .withAdditionalProperties(additionalProperties)
        .build();
    EquipmentEntity vav1 = (EquipmentEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer vav1Id = vav1.getPersistentIdentity();    
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    vav1 = portfolio.getEquipment(vav1Id);
    Assert.assertNotNull("vav1 is null", vav1);

    
    // ***********************************************
    // Create vav 2.
    createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.EQUIPMENT)
        .withParentId(building1Id)
        .withName("VAV2")
        .withDisplayName("VAV2")
        .withAdditionalProperties(additionalProperties)
        .build();
    EquipmentEntity vav2 = (EquipmentEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer vav2Id = vav2.getPersistentIdentity();    
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    vav2 = portfolio.getEquipment(vav2Id);
    Assert.assertNotNull("vav2 is null", vav2);
    
    
    // ***********************************************
    // Tag the equipment.
    TagsContainer tagsContainer = dictionaryService.getTagsContainer();
    EquipmentEnergyExchangeTypeEntity ahuEquipmentType = tagsContainer.getEquipmentTypeByName("ahu");
    EquipmentEnergyExchangeTypeEntity vavEquipmentType = tagsContainer.getEquipmentTypeByName("vav");
    List<EnergyExchangeSystemNodeData> energyExchangeSystemNodeData = new ArrayList<>();

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(ahu1Id)
        .withTypeId(ahuEquipmentType.getPersistentIdentity())
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .withSystemTypeId(EnergyExchangeSystemType.AIR_SUPPLY.getId())
        .withChildIds(Arrays.asList(vav1Id,vav2Id))
        .withMetadataTags(Arrays.asList("rooftop"))
        .build());

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(vav1Id)
        .withTypeId(vavEquipmentType.getPersistentIdentity())
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .build());

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(vav2Id)
        .withTypeId(vavEquipmentType.getPersistentIdentity())
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .build());
    
    UpdateEnergyExchangeSystemNodesRequest updateEnergyExchangeSystemNodesRequest = UpdateEnergyExchangeSystemNodesRequest
        .builder()
        .withBuildingId(building1Id)
        .withCustomerId(customerId)
        .withSubmittedBy("tmyers@resolutebi.com")
        .withData(energyExchangeSystemNodeData)
        .build(); 
    nodeHierarchyService.updateEnergyExchangeSystemNodes(updateEnergyExchangeSystemNodesRequest);
    

    // **********************************************************************************************
    // Verify equipment type tag and energy exchange system parent/child relationships
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    ahu1 = portfolio.getEquipment(ahu1Id);
    vav1 = portfolio.getEquipment(vav1Id);
    vav2 = portfolio.getEquipment(vav2Id);
    
    Assert.assertNotNull("ahu1 equipment type is null", ahu1.getEquipmentTypeNullIfNotExists());
    Assert.assertNotNull("vav1 equipment type is null", vav1.getEquipmentTypeNullIfNotExists());
    Assert.assertNotNull("vav2 equipment type is null", vav2.getEquipmentTypeNullIfNotExists());
    
    Assert.assertEquals("ahu1 equipment type is incorrect", ahuEquipmentType, ahu1.getEquipmentTypeNullIfNotExists());
    Assert.assertEquals("vav1 equipment type is incorrect", vavEquipmentType, vav1.getEquipmentTypeNullIfNotExists());
    Assert.assertEquals("vav2 equipment type is incorrect", vavEquipmentType, vav2.getEquipmentTypeNullIfNotExists());
    
    Assert.assertTrue("ahu1 metadata tags is incorrect", ahu1.getMetadataTags().toString().equals(Arrays.asList("rooftop").toString()));
    
    Set<EnergyExchangeEntity> ahu1ParentEnergyExchangeNodes = ahu1.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("ahu1ParentEnergyExchangeNodes is incorrect", ahu1ParentEnergyExchangeNodes.isEmpty());
    
    Set<EnergyExchangeEntity> ahu1ChildEnergyExchangeNodes = ahu1.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("ahu1ChildEnergyExchangeNodes is incorrect", ahu1ChildEnergyExchangeNodes.contains(vav1));
    Assert.assertTrue("ahu1ChildEnergyExchangeNodes is incorrect", ahu1ChildEnergyExchangeNodes.contains(vav2));

    Set<EnergyExchangeEntity> vav1ParentEnergyExchangeNodes = vav1.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("vav1ParentEnergyExchangeNodes is incorrect", vav1ParentEnergyExchangeNodes.contains(ahu1));
    
    Set<EnergyExchangeEntity> vav1ChildEnergyExchangeNodes = vav1.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("vav1ChildEnergyExchangeNodes is incorrect", vav1ChildEnergyExchangeNodes.isEmpty());

    Set<EnergyExchangeEntity> vav2ParentEnergyExchangeNodes = vav2.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("vav2ParentEnergyExchangeNodes is incorrect", vav2ParentEnergyExchangeNodes.contains(ahu1));
    
    Set<EnergyExchangeEntity> vav2ChildEnergyExchangeNodes = vav2.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("vav2ChildEnergyExchangeNodes is incorrect", vav2ChildEnergyExchangeNodes.isEmpty());
    
    
    // ***********************************************
    // Untag the equipment.
    energyExchangeSystemNodeData = new ArrayList<>();

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(ahu1Id)
        .withTypeId(null)
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .withMetadataTags(new ArrayList<>())
        .build());

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(vav1Id)
        .withTypeId(null)
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .withSystemTypeId(EnergyExchangeSystemType.AIR_SUPPLY.getId())
        .withParentIds(new ArrayList<>())
        .build());

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(vav2Id)
        .withTypeId(null)
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .withSystemTypeId(EnergyExchangeSystemType.AIR_SUPPLY.getId())
        .withParentIds(new ArrayList<>())
        .build());
    
    updateEnergyExchangeSystemNodesRequest = UpdateEnergyExchangeSystemNodesRequest
        .builder()
        .withBuildingId(building1Id)
        .withCustomerId(customerId)
        .withSubmittedBy("tmyers@resolutebi.com")
        .withData(energyExchangeSystemNodeData)
        .build(); 
    nodeHierarchyService.updateEnergyExchangeSystemNodes(updateEnergyExchangeSystemNodesRequest);
    
    
    // **********************************************************************************************
    // Verify equipment type tag and energy exchange system parent/child relationships
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    ahu1 = portfolio.getEquipment(ahu1Id);
    vav1 = portfolio.getEquipment(vav1Id);
    vav2 = portfolio.getEquipment(vav2Id);
    
    Assert.assertNull("ahu1 equipment type is not null", ahu1.getEquipmentTypeNullIfNotExists());
    Assert.assertNull("vav1 equipment type is not null", vav1.getEquipmentTypeNullIfNotExists());
    Assert.assertNull("vav2 equipment type is not null", vav2.getEquipmentTypeNullIfNotExists());
    
    Assert.assertTrue("ahu1 metadata tags is incorrect", ahu1.getMetadataTags().toString().equals(new ArrayList<>().toString()));
    
    ahu1ParentEnergyExchangeNodes = ahu1.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("ahu1ParentEnergyExchangeNodes is incorrect", ahu1ParentEnergyExchangeNodes.isEmpty());
    
    ahu1ChildEnergyExchangeNodes = ahu1.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("ahu1ChildEnergyExchangeNodes is incorrect", ahu1ChildEnergyExchangeNodes.isEmpty());

    vav1ParentEnergyExchangeNodes = vav1.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("vav1ParentEnergyExchangeNodes is incorrect", vav1ParentEnergyExchangeNodes.isEmpty());
    
    vav1ChildEnergyExchangeNodes = vav1.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("vav1ChildEnergyExchangeNodes is incorrect", vav1ChildEnergyExchangeNodes.isEmpty());

    vav2ParentEnergyExchangeNodes = vav2.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("vav2ParentEnergyExchangeNodes is incorrect", vav2ParentEnergyExchangeNodes.isEmpty());
    
    vav2ChildEnergyExchangeNodes = vav2.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("vav2ChildEnergyExchangeNodes is incorrect", vav2ChildEnergyExchangeNodes.isEmpty());  
  }

  @Test
  public void equipmentHierarchy_setChildrenWithExisting() throws Exception {
    
    // ***********************************************
    // Create building 1.
    Map<String, Object> additionalProperties = new HashMap<>();
    CreateNodeRequest createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.BUILDING)
        .withParentId(null)
        .withName("Building_1")
        .withDisplayName("Building_1")
        .withAdditionalProperties(additionalProperties)
        .build();
    BuildingEntity building1 = (BuildingEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer building1Id = building1.getPersistentIdentity();


    // ***********************************************
    // Create rooftop AHU 1.
    createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.EQUIPMENT)
        .withParentId(building1Id)
        .withName("AHU")
        .withDisplayName("AHU")
        .withAdditionalProperties(additionalProperties)
        .build();
    EquipmentEntity ahu1 = (EquipmentEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer ahu1Id = ahu1.getPersistentIdentity();
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    ahu1 = portfolio.getEquipment(ahu1Id);
    Assert.assertNotNull("ahu is null", ahu1);

    
    // ***********************************************
    // Create vav 1.
    createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.EQUIPMENT)
        .withParentId(building1Id)
        .withName("VAV1")
        .withDisplayName("VAV1")
        .withAdditionalProperties(additionalProperties)
        .build();
    EquipmentEntity vav1 = (EquipmentEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer vav1Id = vav1.getPersistentIdentity();    
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    vav1 = portfolio.getEquipment(vav1Id);
    Assert.assertNotNull("vav1 is null", vav1);

    
    // ***********************************************
    // Create vav 2.
    createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.EQUIPMENT)
        .withParentId(building1Id)
        .withName("VAV2")
        .withDisplayName("VAV2")
        .withAdditionalProperties(additionalProperties)
        .build();
    EquipmentEntity vav2 = (EquipmentEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer vav2Id = vav2.getPersistentIdentity();    
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    vav2 = portfolio.getEquipment(vav2Id);
    Assert.assertNotNull("vav2 is null", vav2);
    
    
    // ***********************************************
    // Create vav 3.
    createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.EQUIPMENT)
        .withParentId(building1Id)
        .withName("VAV3")
        .withDisplayName("VAV3")
        .withAdditionalProperties(additionalProperties)
        .build();
    EquipmentEntity vav3 = (EquipmentEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer vav3Id = vav3.getPersistentIdentity();    
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    vav3 = portfolio.getEquipment(vav3Id);
    Assert.assertNotNull("vav3 is null", vav3);

    
    // ***********************************************
    // Create vav 4.
    createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.EQUIPMENT)
        .withParentId(building1Id)
        .withName("VAV4")
        .withDisplayName("VAV4")
        .withAdditionalProperties(additionalProperties)
        .build();
    EquipmentEntity vav4 = (EquipmentEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer vav4Id = vav4.getPersistentIdentity();    
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    vav4 = portfolio.getEquipment(vav4Id);
    Assert.assertNotNull("vav4 is null", vav4);    
    
    
    // ***********************************************
    // Tag the equipment.
    TagsContainer tagsContainer = dictionaryService.getTagsContainer();
    EquipmentEnergyExchangeTypeEntity ahuEquipmentType = tagsContainer.getEquipmentTypeByName("ahu");
    EquipmentEnergyExchangeTypeEntity vavEquipmentType = tagsContainer.getEquipmentTypeByName("vav");
    List<EnergyExchangeSystemNodeData> energyExchangeSystemNodeData = new ArrayList<>();

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(ahu1Id)
        .withTypeId(ahuEquipmentType.getPersistentIdentity())
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .withSystemTypeId(EnergyExchangeSystemType.AIR_SUPPLY.getId())
        .withChildIds(Arrays.asList(vav1Id,vav2Id,vav3Id,vav4Id))
        .withMetadataTags(Arrays.asList("rooftop"))
        .build());

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(vav1Id)
        .withTypeId(vavEquipmentType.getPersistentIdentity())
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .build());

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(vav2Id)
        .withTypeId(vavEquipmentType.getPersistentIdentity())
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .build());

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(vav3Id)
        .withTypeId(vavEquipmentType.getPersistentIdentity())
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .build());

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(vav4Id)
        .withTypeId(vavEquipmentType.getPersistentIdentity())
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .build());
    
    UpdateEnergyExchangeSystemNodesRequest updateEnergyExchangeSystemNodesRequest = UpdateEnergyExchangeSystemNodesRequest
        .builder()
        .withBuildingId(building1Id)
        .withCustomerId(customerId)
        .withSubmittedBy("tmyers@resolutebi.com")
        .withData(energyExchangeSystemNodeData)
        .build(); 
    nodeHierarchyService.updateEnergyExchangeSystemNodes(updateEnergyExchangeSystemNodesRequest);
    

    // **********************************************************************************************
    // Verify equipment type tag and energy exchange system parent/child relationships
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    ahu1 = portfolio.getEquipment(ahu1Id);
    vav1 = portfolio.getEquipment(vav1Id);
    vav2 = portfolio.getEquipment(vav2Id);
    vav3 = portfolio.getEquipment(vav3Id);
    vav4 = portfolio.getEquipment(vav4Id);
    
    Assert.assertNotNull("ahu1 equipment type is null", ahu1.getEquipmentTypeNullIfNotExists());
    Assert.assertNotNull("vav1 equipment type is null", vav1.getEquipmentTypeNullIfNotExists());
    Assert.assertNotNull("vav2 equipment type is null", vav2.getEquipmentTypeNullIfNotExists());
    Assert.assertNotNull("vav3 equipment type is null", vav3.getEquipmentTypeNullIfNotExists());
    Assert.assertNotNull("vav4 equipment type is null", vav4.getEquipmentTypeNullIfNotExists());
    
    Assert.assertEquals("ahu1 equipment type is incorrect", ahuEquipmentType, ahu1.getEquipmentTypeNullIfNotExists());
    Assert.assertEquals("vav1 equipment type is incorrect", vavEquipmentType, vav1.getEquipmentTypeNullIfNotExists());
    Assert.assertEquals("vav2 equipment type is incorrect", vavEquipmentType, vav2.getEquipmentTypeNullIfNotExists());
    Assert.assertEquals("vav3 equipment type is incorrect", vavEquipmentType, vav3.getEquipmentTypeNullIfNotExists());
    Assert.assertEquals("vav4 equipment type is incorrect", vavEquipmentType, vav4.getEquipmentTypeNullIfNotExists());
    
    Assert.assertTrue("ahu1 metadata tags is incorrect", ahu1.getMetadataTags().toString().equals(Arrays.asList("rooftop").toString()));
    
    Set<EnergyExchangeEntity> ahu1ParentEnergyExchangeNodes = ahu1.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("ahu1ParentEnergyExchangeNodes is incorrect", ahu1ParentEnergyExchangeNodes.isEmpty());
    
    Set<EnergyExchangeEntity> ahu1ChildEnergyExchangeNodes = ahu1.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("ahu1ChildEnergyExchangeNodes is incorrect", ahu1ChildEnergyExchangeNodes.contains(vav1));
    Assert.assertTrue("ahu1ChildEnergyExchangeNodes is incorrect", ahu1ChildEnergyExchangeNodes.contains(vav2));
    Assert.assertTrue("ahu1ChildEnergyExchangeNodes is incorrect", ahu1ChildEnergyExchangeNodes.contains(vav3));
    Assert.assertTrue("ahu1ChildEnergyExchangeNodes is incorrect", ahu1ChildEnergyExchangeNodes.contains(vav4));
    
    Set<EquipmentEntity> ahu1EquipmentHierarchyChildren = ahu1.getEquipmentHierarchyChildEquipment();
    Assert.assertTrue("ahu1EquipmentHierarchyChildren is incorrect", ahu1EquipmentHierarchyChildren.contains(vav1));
    Assert.assertTrue("ahu1EquipmentHierarchyChildren is incorrect", ahu1EquipmentHierarchyChildren.contains(vav2));
    Assert.assertTrue("ahu1EquipmentHierarchyChildren is incorrect", ahu1EquipmentHierarchyChildren.contains(vav3));
    Assert.assertTrue("ahu1EquipmentHierarchyChildren is incorrect", ahu1EquipmentHierarchyChildren.contains(vav4));

    Set<EnergyExchangeEntity> vav1ParentEnergyExchangeNodes = vav1.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("vav1ParentEnergyExchangeNodes is incorrect", vav1ParentEnergyExchangeNodes.contains(ahu1));
    Assert.assertEquals("vav1ParentEquipment is incorrect", ahu1, vav1.getParentEquipmentNullIfNotExists());
    
    Set<EnergyExchangeEntity> vav1ChildEnergyExchangeNodes = vav1.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("vav1ChildEnergyExchangeNodes is incorrect", vav1ChildEnergyExchangeNodes.isEmpty());

    Set<EnergyExchangeEntity> vav2ParentEnergyExchangeNodes = vav2.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("vav2ParentEnergyExchangeNodes is incorrect", vav2ParentEnergyExchangeNodes.contains(ahu1));
    Assert.assertEquals("vav2ParentEquipment is incorrect", ahu1, vav2.getParentEquipmentNullIfNotExists());
    
    Set<EnergyExchangeEntity> vav2ChildEnergyExchangeNodes = vav2.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("vav2ChildEnergyExchangeNodes is incorrect", vav2ChildEnergyExchangeNodes.isEmpty());

    Set<EnergyExchangeEntity> vav3ParentEnergyExchangeNodes = vav3.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("vav3ParentEnergyExchangeNodes is incorrect", vav3ParentEnergyExchangeNodes.contains(ahu1));
    Assert.assertEquals("vav3ParentEquipment is incorrect", ahu1, vav3.getParentEquipmentNullIfNotExists());
    
    Set<EnergyExchangeEntity> vav3ChildEnergyExchangeNodes = vav3.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("vav3ChildEnergyExchangeNodes is incorrect", vav3ChildEnergyExchangeNodes.isEmpty());

    Set<EnergyExchangeEntity> vav4ParentEnergyExchangeNodes = vav4.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("vav4ParentEnergyExchangeNodes is incorrect", vav4ParentEnergyExchangeNodes.contains(ahu1));
    Assert.assertEquals("vav4ParentEquipment is incorrect", ahu1, vav4.getParentEquipmentNullIfNotExists());
    
    Set<EnergyExchangeEntity> vav4ChildEnergyExchangeNodes = vav4.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("vav4ChildEnergyExchangeNodes is incorrect", vav4ChildEnergyExchangeNodes.isEmpty());
    
    
    // ***********************************************
    // Remove VAV3 and VAV4 as child equipment for AHU1, leaving VAV1 and VAV2
    energyExchangeSystemNodeData = new ArrayList<>();
    
    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(ahu1Id)
        .withTypeId(ahuEquipmentType.getPersistentIdentity())
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .withSystemTypeId(EnergyExchangeSystemType.AIR_SUPPLY.getId())
        .withChildIds(Arrays.asList(vav1Id,vav2Id))
        .withMetadataTags(Arrays.asList("rooftop"))
        .build());

    updateEnergyExchangeSystemNodesRequest = UpdateEnergyExchangeSystemNodesRequest
        .builder()
        .withBuildingId(building1Id)
        .withCustomerId(customerId)
        .withSubmittedBy("tmyers@resolutebi.com")
        .withData(energyExchangeSystemNodeData)
        .build(); 
    nodeHierarchyService.updateEnergyExchangeSystemNodes(updateEnergyExchangeSystemNodesRequest);
    
    
    // **********************************************************************************************
    // Verify equipment type tag and energy exchange system parent/child relationships
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    ahu1 = portfolio.getEquipment(ahu1Id);
    vav1 = portfolio.getEquipment(vav1Id);
    vav2 = portfolio.getEquipment(vav2Id);
    vav3 = portfolio.getEquipment(vav3Id);
    vav4 = portfolio.getEquipment(vav4Id);
    
    Assert.assertNotNull("ahu1 equipment type is null", ahu1.getEquipmentTypeNullIfNotExists());
    Assert.assertNotNull("vav1 equipment type is null", vav1.getEquipmentTypeNullIfNotExists());
    Assert.assertNotNull("vav2 equipment type is null", vav2.getEquipmentTypeNullIfNotExists());
    Assert.assertNotNull("vav3 equipment type is null", vav3.getEquipmentTypeNullIfNotExists());
    Assert.assertNotNull("vav4 equipment type is null", vav4.getEquipmentTypeNullIfNotExists());
    
    Assert.assertEquals("ahu1 equipment type is incorrect", ahuEquipmentType, ahu1.getEquipmentTypeNullIfNotExists());
    Assert.assertEquals("vav1 equipment type is incorrect", vavEquipmentType, vav1.getEquipmentTypeNullIfNotExists());
    Assert.assertEquals("vav2 equipment type is incorrect", vavEquipmentType, vav2.getEquipmentTypeNullIfNotExists());
    Assert.assertEquals("vav3 equipment type is incorrect", vavEquipmentType, vav3.getEquipmentTypeNullIfNotExists());
    Assert.assertEquals("vav4 equipment type is incorrect", vavEquipmentType, vav4.getEquipmentTypeNullIfNotExists());
    
    Assert.assertTrue("ahu1 metadata tags is incorrect", ahu1.getMetadataTags().toString().equals(Arrays.asList("rooftop").toString()));
    
    ahu1ParentEnergyExchangeNodes = ahu1.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("ahu1ParentEnergyExchangeNodes is incorrect", ahu1ParentEnergyExchangeNodes.isEmpty());
    
    ahu1ChildEnergyExchangeNodes = ahu1.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("ahu1ChildEnergyExchangeNodes is incorrect", ahu1ChildEnergyExchangeNodes.contains(vav1));
    Assert.assertTrue("ahu1ChildEnergyExchangeNodes is incorrect", ahu1ChildEnergyExchangeNodes.contains(vav2));
    Assert.assertFalse("ahu1ChildEnergyExchangeNodes is incorrect", ahu1ChildEnergyExchangeNodes.contains(vav3));
    Assert.assertFalse("ahu1ChildEnergyExchangeNodes is incorrect", ahu1ChildEnergyExchangeNodes.contains(vav4));
    
    ahu1EquipmentHierarchyChildren = ahu1.getEquipmentHierarchyChildEquipment();
    Assert.assertTrue("ahu1EquipmentHierarchyChildren is incorrect", ahu1EquipmentHierarchyChildren.contains(vav1));
    Assert.assertTrue("ahu1EquipmentHierarchyChildren is incorrect", ahu1EquipmentHierarchyChildren.contains(vav2));
    Assert.assertFalse("ahu1EquipmentHierarchyChildren is incorrect", ahu1EquipmentHierarchyChildren.contains(vav3));
    Assert.assertFalse("ahu1EquipmentHierarchyChildren is incorrect", ahu1EquipmentHierarchyChildren.contains(vav4));
    
    vav1ParentEnergyExchangeNodes = vav1.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("vav1ParentEnergyExchangeNodes is incorrect", vav1ParentEnergyExchangeNodes.contains(ahu1));
    Assert.assertEquals("vav1ParentEquipment is incorrect", ahu1, vav1.getParentEquipmentNullIfNotExists());
    
    vav1ChildEnergyExchangeNodes = vav1.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("vav1ChildEnergyExchangeNodes is incorrect", vav1ChildEnergyExchangeNodes.isEmpty());

    vav2ParentEnergyExchangeNodes = vav2.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("vav2ParentEnergyExchangeNodes is incorrect", vav2ParentEnergyExchangeNodes.contains(ahu1));
    Assert.assertEquals("vav2ParentEquipment is incorrect", ahu1, vav2.getParentEquipmentNullIfNotExists());
    
    vav2ChildEnergyExchangeNodes = vav2.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("vav2ChildEnergyExchangeNodes is incorrect", vav2ChildEnergyExchangeNodes.isEmpty());

    vav3ParentEnergyExchangeNodes = vav3.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("vav3ParentEnergyExchangeNodes is incorrect", vav3ParentEnergyExchangeNodes.isEmpty());
    Assert.assertNull("vav3ParentEquipment is incorrect", vav3.getParentEquipmentNullIfNotExists());
    
    vav3ChildEnergyExchangeNodes = vav3.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("vav3ChildEnergyExchangeNodes is incorrect", vav3ChildEnergyExchangeNodes.isEmpty());

    vav4ParentEnergyExchangeNodes = vav4.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("vav4ParentEnergyExchangeNodes is incorrect", vav4ParentEnergyExchangeNodes.isEmpty());
    Assert.assertNull("vav4ParentEquipment is incorrect", vav4.getParentEquipmentNullIfNotExists());
    
    vav4ChildEnergyExchangeNodes = vav4.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.AIR_SUPPLY);
    Assert.assertTrue("vav4ChildEnergyExchangeNodes is incorrect", vav4ChildEnergyExchangeNodes.isEmpty());
  }
  
  @Test
  public void chilledWaterPlantRule_2_5_12_1() throws Exception {
    
    /*
      CONSTANTS:
      ===========
      Label                                           Name                        Default Value
      -----                                           ----                        -------------
      Delay (minutes)                                 DELAY                       60
      % Cooling Valve Threshold                       VALVE_THRESHOLD_PERCENT     90
      % of Cooling Equipment Valves Below Threshold   CLG_EQUIP_PERCENT           90
      % Return Air Humidity High Limit                RA_HUMIDITY_PERCENT         55
      Chilled Water High Limit Setpoint               CHILLED_WATER_SETPOINT      55

      
      EXPRESSION:
      =========== 
      Item 1) applies to all the childEquipment within the chilledWaterPlant
      Item 2) applies to the supply water temperature setpoint within the chiller evaporator loop within the chilledWaterPlant
      Item 3) applies to chillers within the chilledWaterPlant
      
      FAULT IF:
      1)  >= CLG_EQUIP_PERCENT of the total qty of childEquipment(type=AHU/FCU) have ClgCmd < VALVE_THRESHOLD_PERCENT AND DaFanSts == True AND, IF RaHumidity EXISTS, RaHumidity < RA_HUMIDITY_PERCENT
      2)  ChWSp < CHILLED_WATER_SETPOINT
      3)  at least one chiller has a ChrSts == True, or at least one chilled water pump has a ChWCPSts == True
        
        
      INPUT POINTS:
      =============
      
      Id: Label                       Name        Tags                            Current Object                                                                  Required    Array   Types
      --- -----                       ----        ----                            --------------                                                                  --------    -----   -----
      322 Cooling Command             ClgCmd      cmd,cooling                     descendant(model=chilledWater,type=equipment,tags=ahu|fcu)                      true        true    42,44,149 (ahu,fcu,heatPump)
      323 DA Fan Status               DaFanSts    discharge,air,fan,run,sensor    descendant(model=chilledWater,type=equipment,tags=ahu|fcu)                      true        true    42 (ahu)
      324 RA Humidity                 RaHumidity  return,air,humidity,sensor      descendant(model=chilledWater,type=equipment,tags=ahu|fcu)                      false       true    42 (ahu)
                          
      325 Chilled Water Setpoint      ChWSp       chilled,water,leaving,temp,sp   descendant(model=chilledWater,type=equipment,tags=chiller|chilledWaterPlant)    false       false   204,103 (chilledWaterPlant,chiller)
                          
      326 Chiller Status              ChrSts      chiller,run,sensor              descendant(model=chilledWater,type=equipment,tags=chiller)                      true        true    103 (chiller)
      327 Chilled Water Pump Status   ChWCPSts    chilled,water,pump,run,sensor   descendant(model=chilledWater,type=equipment,tags=ahu|chilledWaterPlant)        false       true    42,204 (ahu,chilledWaterPlant)
      
      Equipment Types: 
      42:  ahu
      44:  fcu
      149: heatPump
      204: chilledWaterPlant
      103: chiller
      
      
      
      Mappable Points:
      ================
      /Building_1/AHU1/ClgCmd
      /Building_1/AHU1/DaFanSts
      /Building_1/AHU1/RaHumidity
      /Building_1/AHU1/ChWCPSts 

      /Building_1/AHU2/ClgCmd
      /Building_1/AHU2/DaFanSts
      /Building_1/AHU2/RaHumidity
      /Building_1/AHU2/ChWCPSts 
        
      /Building_1/ChillerPlant/ChWSp
      
      /Building_1/Chiller1/ChrSts
 
      /Building_1/Chiller2/ChrSts   
     */
    
    // ***********************************************
    // Create the raw points related to chilled water plants
    String chillerRawPointsJsonFilename = "chilledWaterPlant_rule_2.5.12.1_raw_points.json";
    String chillerRawPointsJson = null;
    try (InputStream in = NodeHierarchyServiceTest.class.getResourceAsStream(chillerRawPointsJsonFilename);
        @SuppressWarnings("resource")
        Scanner s = new Scanner(in).useDelimiter("\\A")) {
      
      chillerRawPointsJson = s.hasNext() ? s.next() : "";

    } catch (IOException e) {
      throw new IllegalStateException("Could not load: ["
          + chillerRawPointsJsonFilename 
          + "], error: ["
          + e.getMessage()
          + "].", e);
    }
    List<RawPointDto> rawPointDtoList = AbstractEntity.OBJECT_MAPPER.get().readValue(chillerRawPointsJson, new TypeReference<List<RawPointDto>>() {});
    List<RawPointEntity> rawPointList = RawPointEntity.Mapper.getInstance().mapDtosToEntities(customerId, rawPointDtoList);
    customer.addRawPoints(rawPointList);
    boolean storeRawPoints = true;
    customer = customerService.updateCustomer(customer, storeRawPoints);

    
    // ***********************************************
    // Map the raw points
    List<RawPointData> rawPointData = new ArrayList<>();
    for (RawPointEntity rp: rawPointList) {
      
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
        .withMappingExpression("/Drivers/NiagaraNetwork/{building}/points/{equipment}/{point}")
        .withMetricIdDelimiter(NodeHierarchyService.DEFAULT_METRIC_ID_DELIMITER)
        .build(); 
    createdMappablePoints = nodeHierarchyService.mapRawPoints(mapRawPointsRequest);
    Assert.assertEquals("created mappable points list size is incorrect", 
        Integer.toString(rawPointDtoList.size()), 
        Integer.toString(createdMappablePoints.size()));
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    customer = portfolio.getParentCustomer();
    distributor = customer.getParentDistributor();    
    BuildingEntity building1 = portfolio.getChildBuildingByName("Building_1");
    Integer building1Id = building1.getPersistentIdentity();


    // ***********************************************
    // Create plant 1.
    Map<String, Object> additionalProperties = new HashMap<>();
    CreateNodeRequest createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.PLANT)
        .withParentId(building1Id)
        .withName("Plant_1")
        .withDisplayName("Plant_1")
        .withAdditionalProperties(additionalProperties)
        .build();
    PlantEntity plant1 = (PlantEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer plant1Id = plant1.getPersistentIdentity();
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    plant1 = portfolio.getPlant(plant1Id);
    Assert.assertNotNull("plant1 is null", plant1);
    
    
    // ***********************************************
    // Create loop 1.
    additionalProperties.clear();
    additionalProperties.put("energyExchangeSystemTypeId", CreateNodeRequest.CHILLED_WATER_SYSTEM_TYPE_ID);
    createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.LOOP)
        .withParentId(plant1Id)
        .withName("Loop_1")
        .withDisplayName("Loop_1")
        .withAdditionalProperties(additionalProperties)
        .build();
    LoopEntity loop1 = (LoopEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer loop1Id = loop1.getPersistentIdentity();
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    loop1 = portfolio.getLoop(loop1Id);
    Assert.assertNotNull("loop1 is null", loop1);
    additionalProperties.clear();

    
    // ***********************************************
    // Move points from "ChillerPlant" equipment to "Plant_1" plant.
    List<Integer> childIds = new ArrayList<>();
    EquipmentEntity chillerPlantEquipment = building1.getChildEquipmentByName("ChillerPlant");
    Set<AdFunctionInstanceEligiblePoint> points = chillerPlantEquipment.getAdFunctionInstanceEligiblePoints();
    for (AdFunctionInstanceEligiblePoint point: points) {
      
      childIds.add(point.getPersistentIdentity());
    }
    MoveChildNodesRequest moveChildNodesRequest = MoveChildNodesRequest
        .builder()
        .withCustomerId(customerId)
        .withNewParentId(plant1Id)
        .withChildIds(childIds)
        .build();
    movedNodes = nodeHierarchyService.moveChildNodesToNewParentNode(moveChildNodesRequest);
    Assert.assertEquals("moved nodes list size is incorrect", 
        Integer.toString(points.size()), 
        Integer.toString(movedNodes.size()));
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building1 = portfolio.getChildBuilding(building1Id);
    plant1 = building1.getChildPlant(plant1Id);
    Assert.assertEquals("plant point size is incorrect", 
        Integer.toString(points.size()), 
        Integer.toString(plant1.getAdFunctionInstanceEligiblePoints().size()));

    
    // ***********************************************
    // Delete the "ChillerPlant" equipment.
    childIds.clear();
    childIds.add(chillerPlantEquipment.getPersistentIdentity());
    DeleteChildNodesRequest deleteChildNodesRequest = DeleteChildNodesRequest
        .builder()
        .withCustomerId(customerId)
        .withChildIds(childIds)
        .build();
    deletedNodes = nodeHierarchyService.deleteChildNodes(deleteChildNodesRequest);
    Assert.assertEquals("deleted nodes list size is incorrect", 
        "1", 
        Integer.toString(deletedNodes.size()));
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building1 = portfolio.getChildBuilding(building1Id);
    plant1 = portfolio.getPlant(plant1Id);
    loop1 = portfolio.getLoop(loop1Id);
    EquipmentEntity ahu1 = building1.getChildEquipmentByName("AHU1");
    EquipmentEntity ahu2 = building1.getChildEquipmentByName("AHU2");
    EquipmentEntity chiller1 = building1.getChildEquipmentByName("Chiller1");
    EquipmentEntity chiller2 = building1.getChildEquipmentByName("Chiller2");
    
    
    // ***********************************************
    // Tag the plant/loop/equipment.
    TagsContainer tagsContainer = dictionaryService.getTagsContainer();
    PlantEnergyExchangeTypeEntity chilledWaterPlantType = tagsContainer.getPlantTypeByName("chilledWaterPlant");
    LoopEnergyExchangeTypeEntity primaryLoopType = tagsContainer.getLoopTypeByName("primaryLoop");
    EquipmentEnergyExchangeTypeEntity ahuEquipmentType = tagsContainer.getEquipmentTypeByName("ahu");
    EquipmentEnergyExchangeTypeEntity chillerEquipmentType = tagsContainer.getEquipmentTypeByName("chiller");
    List<String> equipmentMetadataTags = Arrays.asList("rooftop");
    List<EnergyExchangeSystemNodeData> energyExchangeSystemNodeData = new ArrayList<>();

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(plant1Id)
        .withDisplayName(plant1.getDisplayName())
        .withTypeId(chilledWaterPlantType.getPersistentIdentity())
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_PLANT)
        .build());

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(loop1Id)
        .withDisplayName(loop1.getDisplayName())
        .withTypeId(primaryLoopType.getPersistentIdentity())
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_LOOP)
        //.withSystemTypeId(EnergyExchangeSystemType.CHILLED_WATER.getId()) No need to do this, as it is done when the loop is created
        //.withParentIds(Arrays.asList(plant1Id))
        .build());
    
    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(ahu1.getPersistentIdentity())
        .withDisplayName(ahu1.getDisplayName())
        .withTypeId(ahuEquipmentType.getPersistentIdentity())
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .withSystemTypeId(EnergyExchangeSystemType.CHILLED_WATER.getId())
        .withParentIds(Arrays.asList(loop1Id))
        .withMetadataTags(equipmentMetadataTags)
        .build());

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(ahu2.getPersistentIdentity())
        .withDisplayName(ahu2.getDisplayName())
        .withTypeId(ahuEquipmentType.getPersistentIdentity())
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .withSystemTypeId(EnergyExchangeSystemType.CHILLED_WATER.getId())
        .withParentIds(Arrays.asList(loop1Id))
        .withMetadataTags(equipmentMetadataTags)
        .build());
    
    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(chiller1.getPersistentIdentity())
        .withDisplayName(chiller1.getDisplayName())
        .withTypeId(chillerEquipmentType.getPersistentIdentity())
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .withSystemTypeId(EnergyExchangeSystemType.CHILLED_WATER.getId())
        .withParentIds(Arrays.asList(loop1Id))
        .withMetadataTags(equipmentMetadataTags)
        .build());

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(chiller2.getPersistentIdentity())
        .withDisplayName(chiller2.getDisplayName())
        .withTypeId(chillerEquipmentType.getPersistentIdentity())
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .withSystemTypeId(EnergyExchangeSystemType.CHILLED_WATER.getId())
        .withParentIds(Arrays.asList(loop1Id))
        .withMetadataTags(equipmentMetadataTags)
        .build());
    
    UpdateEnergyExchangeSystemNodesRequest updateEnergyExchangeSystemNodesRequest = UpdateEnergyExchangeSystemNodesRequest
        .builder()
        .withBuildingId(building1Id)
        .withCustomerId(customerId)
        .withSubmittedBy("tmyers@resolutebi.com")
        .withData(energyExchangeSystemNodeData)
        .build(); 
    nodeHierarchyService.updateEnergyExchangeSystemNodes(updateEnergyExchangeSystemNodesRequest);
    

    // **********************************************************************************************
    // Verify plant/loop/equipment type tag and energy exchange system parent/child relationships
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building1 = portfolio.getChildBuilding(building1Id);
    plant1 = portfolio.getPlant(plant1Id);
    loop1 = portfolio.getLoop(loop1Id);
    ahu1 = building1.getChildEquipmentByName("AHU1");
    ahu2 = building1.getChildEquipmentByName("AHU2");
    chiller1 = building1.getChildEquipmentByName("Chiller1");
    chiller2 = building1.getChildEquipmentByName("Chiller2");
    
    Assert.assertNotNull("plant1 plant type is null", plant1.getPlantTypeNullIfNotExists());
    Assert.assertNotNull("loop1 loop type is null", loop1.getLoopTypeNullIfNotExists());
    Assert.assertNotNull("ahu1 equipment type is null", ahu1.getEquipmentTypeNullIfNotExists());
    Assert.assertNotNull("ahu2 equipment type is null", ahu2.getEquipmentTypeNullIfNotExists());
    Assert.assertNotNull("chiller1 equipment type is null", chiller1.getEquipmentTypeNullIfNotExists());
    Assert.assertNotNull("chiller2 equipment type is null", chiller2.getEquipmentTypeNullIfNotExists());
    
    Assert.assertEquals("plant1 plant type is incorrect", chilledWaterPlantType, plant1.getPlantTypeNullIfNotExists());
    Assert.assertEquals("loop1 loop type is incorrect", primaryLoopType, loop1.getLoopTypeNullIfNotExists());
    Assert.assertEquals("ahu1 equipment type is incorrect", ahuEquipmentType, ahu1.getEquipmentTypeNullIfNotExists());
    Assert.assertEquals("ahu2 equipment type is incorrect", ahuEquipmentType, ahu2.getEquipmentTypeNullIfNotExists());
    Assert.assertEquals("chiller1 equipment type is incorrect", chillerEquipmentType, chiller1.getEquipmentTypeNullIfNotExists());
    Assert.assertEquals("chiller2 equipment type is incorrect", chillerEquipmentType, chiller2.getEquipmentTypeNullIfNotExists());
    
    Assert.assertTrue("pump1 metadata tags is incorrect", ahu1.getMetadataTags().toString().equals(equipmentMetadataTags.toString()));
    Assert.assertTrue("pump2 metadata tags is incorrect", ahu2.getMetadataTags().toString().equals(equipmentMetadataTags.toString()));
    Assert.assertTrue("chiller1 metadata tags is incorrect", chiller1.getMetadataTags().toString().equals(equipmentMetadataTags.toString()));
    Assert.assertTrue("chiller2 metadata tags is incorrect", chiller2.getMetadataTags().toString().equals(equipmentMetadataTags.toString()));
    
    Set<EnergyExchangeEntity> plant1ParentEnergyExchangeNodes = plant1.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.CHILLED_WATER);
    Assert.assertTrue("plant1ParentEnergyExchangeNodes is incorrect", plant1ParentEnergyExchangeNodes.isEmpty());
    
    Set<EnergyExchangeEntity> plant1ChildEnergyExchangeNodes = plant1.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.CHILLED_WATER);
    Assert.assertTrue("plant1ChildEnergyExchangeNodes is incorrect", plant1ChildEnergyExchangeNodes.contains(loop1));

    Set<EnergyExchangeEntity> loop1ParentEnergyExchangeNodes = loop1.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.CHILLED_WATER);
    Assert.assertTrue("loop1ParentEnergyExchangeNodes is incorrect", loop1ParentEnergyExchangeNodes.contains(plant1));
    
    Set<EnergyExchangeEntity> loop1ChildEnergyExchangeNodes = loop1.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.CHILLED_WATER);
    Assert.assertTrue("loop1ChildEnergyExchangeNodes is incorrect", loop1ChildEnergyExchangeNodes.contains(ahu1));
    Assert.assertTrue("loop1ChildEnergyExchangeNodes is incorrect", loop1ChildEnergyExchangeNodes.contains(ahu2));
    Assert.assertTrue("loop1ChildEnergyExchangeNodes is incorrect", loop1ChildEnergyExchangeNodes.contains(chiller1));
    Assert.assertTrue("loop1ChildEnergyExchangeNodes is incorrect", loop1ChildEnergyExchangeNodes.contains(chiller2));

    Set<EnergyExchangeEntity> ahu1ParentEnergyExchangeNodes = ahu1.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.CHILLED_WATER);
    Assert.assertTrue("ahu1ParentEnergyExchangeNodes is incorrect", ahu1ParentEnergyExchangeNodes.contains(loop1));
    
    Set<EnergyExchangeEntity> ahu2ParentEnergyExchangeNodes = ahu2.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.CHILLED_WATER);
    Assert.assertTrue("ahu2ParentEnergyExchangeNodes is incorrect", ahu2ParentEnergyExchangeNodes.contains(loop1));
    
    Set<EnergyExchangeEntity> chiller1ParentEnergyExchangeNodes = chiller1.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.CHILLED_WATER);
    Assert.assertTrue("chiller1ParentEnergyExchangeNodes is incorrect", chiller1ParentEnergyExchangeNodes.contains(loop1));
    
    Set<EnergyExchangeEntity> chiller1ChildEnergyExchangeNodes = chiller1.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.CHILLED_WATER);
    Assert.assertTrue("chiller1ChildEnergyExchangeNodes is incorrect", chiller1ChildEnergyExchangeNodes.isEmpty());

    Set<EnergyExchangeEntity> chiller2ParentEnergyExchangeNodes = chiller2.getParentEnergyExchangeSystemNodes(EnergyExchangeSystemType.CHILLED_WATER);
    Assert.assertTrue("chiller2ParentEnergyExchangeNodes is incorrect", chiller2ParentEnergyExchangeNodes.contains(loop1));
    
    Set<EnergyExchangeEntity> chiller2ChildEnergyExchangeNodes = chiller2.getChildEnergyExchangeSystemNodes(EnergyExchangeSystemType.CHILLED_WATER);
    Assert.assertTrue("chiller2ChildEnergyExchangeNodes is incorrect", chiller2ChildEnergyExchangeNodes.isEmpty());
    
    
    // ***********************************************
    // Tag the points.
    NodeTagTemplatesContainer pointTemplatesContainer = DictionaryContext.getNodeTagTemplatesContainer();
    PointTemplateEntity clgCmdPointTemplate = pointTemplatesContainer.getPointTemplateByName("ClgCmd");
    PointTemplateEntity daFanStsPointTemplate = pointTemplatesContainer.getPointTemplateByName("DaFanSts");
    PointTemplateEntity raHumidityPointTemplate = pointTemplatesContainer.getPointTemplateByName("RaHumidity");
    PointTemplateEntity chWCPStsPointTemplate = pointTemplatesContainer.getPointTemplateByName("ChWCPSts");
    PointTemplateEntity chWSpPointTemplate = pointTemplatesContainer.getPointTemplateByName("ChWSp");
    PointTemplateEntity chrStsPointTemplate = pointTemplatesContainer.getPointTemplateByName("ChrSts");
    
    List<MappablePointNodeData> data = new ArrayList<>();
    for (MappablePointEntity point: portfolio.getAllMappablePoints()) {
      
      Integer parentEquipmentTypeId = null;
      PointTemplateEntity pointTemplate = null;
      
      String name = point.getName();
      if (name.equals("ClgCmd")) {
        
        parentEquipmentTypeId = ahuEquipmentType.getPersistentIdentity();
        pointTemplate = clgCmdPointTemplate;
        
      } else if (name.equals("DaFanSts")) {

        parentEquipmentTypeId = ahuEquipmentType.getPersistentIdentity();
        pointTemplate = daFanStsPointTemplate;
        
      } else if (name.equals("RaHumidity")) {

        parentEquipmentTypeId = ahuEquipmentType.getPersistentIdentity();
        pointTemplate = raHumidityPointTemplate;
        
      } else if (name.equals("ChWCPSts")) {

        parentEquipmentTypeId = ahuEquipmentType.getPersistentIdentity();
        pointTemplate = chWCPStsPointTemplate;
        
      } else if (name.equals("ChWSp")) {

        parentEquipmentTypeId = chilledWaterPlantType.getPersistentIdentity();
        pointTemplate = chWSpPointTemplate;
        
      } else if (name.equals("ChrSts")) {

        parentEquipmentTypeId = chillerEquipmentType.getPersistentIdentity();
        pointTemplate = chrStsPointTemplate;
        
      }
      
      if (pointTemplate != null) {

        data.add(MappablePointNodeData
            .builder()
            .withId(point.getPersistentIdentity())
            .withParentEquipmentTypeId(parentEquipmentTypeId)
            .withName(name)
            .withDisplayName(point.getDisplayName())
            .withPointTemplateId(pointTemplate.getPersistentIdentity())
            .withUnitId(pointTemplate.getUnit().getPersistentIdentity())
            .withPointDataTypeId(point.getDataType().getId())
            .build());
      }
    }
    Boolean useGrouping = Boolean.FALSE;
    UpdateMappablePointNodesRequest updateMappablePointNodesRequest = UpdateMappablePointNodesRequest
        .builder()
        .withCustomerId(customerId)
        .withUseGrouping(useGrouping)
        .withData(data)
        .build();
    List<MappablePointEntity> updatedPoints = nodeHierarchyService.updateMappablePointNodes(updateMappablePointNodesRequest);
    Assert.assertEquals("updatedPoints size is incorrect", 
        "11", 
        Integer.toString(updatedPoints.size()));
    

    // ***********************************************
    // Find Ad Function Instance Candidates
    FindAdFunctionInstanceCandidatesRequest findAdFunctionInstanceCandidatesRequest = FindAdFunctionInstanceCandidatesRequest
        .builder()
        .withCustomerId(customerId)
        .withFunctionType(FunctionType.RULE)
        .build();
    List<AdFunctionInstanceDto> candidateDtoList = nodeHierarchyService.findAdFunctionInstanceCandidates(findAdFunctionInstanceCandidatesRequest);
    Assert.assertEquals("candidateDtoList size is incorrect", 
        "3", 
        Integer.toString(candidateDtoList.size()));
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building1 = portfolio.getChildBuilding(building1Id);
    plant1 = portfolio.getPlant(plant1Id);
    loop1 = portfolio.getLoop(loop1Id);
    ahu1 = building1.getChildEquipmentByName("AHU1");
    ahu2 = building1.getChildEquipmentByName("AHU2");
    chiller1 = building1.getChildEquipmentByName("Chiller1");
    chiller2 = building1.getChildEquipmentByName("Chiller2");
    Set<AbstractAdFunctionInstanceEntity> candidates = plant1.getAdFunctionInstanceCandidates();
    Assert.assertEquals("candidates size is incorrect", 
        "1", 
        Integer.toString(candidates.size()));
    List<AbstractAdFunctionInstanceEntity> candidateList = new ArrayList<>();
    candidateList.addAll(candidates);
    AbstractAdFunctionInstanceEntity candidate = candidateList.get(0);

    
    // ***********************************************
    // Create Ad Function Instances from Candidates
    Map<Integer, Set<Integer>> candidateTemplateEquipmentIds = new HashMap<>();
    Set<Integer> energyExchangeIds = new HashSet<>();
    energyExchangeIds.add(candidate.getEquipment().getPersistentIdentity());
    candidateTemplateEquipmentIds.put(candidate.getAdFunctionTemplate().getPersistentIdentity(), energyExchangeIds);
    CreateAdFunctionInstancesRequest createAdFunctionInstancesRequest = CreateAdFunctionInstancesRequest
        .builder()
        .withCustomerId(customerId)
        .withFunctionType(NodeHierarchyCommandRequest.RULE)
        .withCandidateTemplateEquipmentIds(candidateTemplateEquipmentIds)
        .build();
    List<AbstractAdFunctionInstanceEntity> instances = nodeHierarchyService.createAdFunctionInstancesFromCandidates(createAdFunctionInstancesRequest);
    Assert.assertEquals("instances size is incorrect", 
        "1", 
        Integer.toString(instances.size()));
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building1 = portfolio.getChildBuilding(building1Id);
    plant1 = portfolio.getPlant(plant1Id);
    loop1 = portfolio.getLoop(loop1Id);
    ahu1 = building1.getChildEquipmentByName("AHU1");
    ahu2 = building1.getChildEquipmentByName("AHU2");
    chiller1 = building1.getChildEquipmentByName("Chiller1");
    chiller2 = building1.getChildEquipmentByName("Chiller2");
    List<AbstractAdFunctionInstanceEntity> instanceList = new ArrayList<>();
    instanceList.addAll(plant1.getAdFunctionInstances());
    Assert.assertEquals("instanceList size is incorrect", 
        "1", 
        Integer.toString(instanceList.size()));
    AbstractAdFunctionInstanceEntity instance = instanceList.get(0);

    Assert.assertEquals("instance is incorrect", 
        "129", 
        Integer.toString(instance.getAdFunctionTemplate().getPersistentIdentity()));
  }
  
  @Test
  public void onlineDistributorWithOutOfBandBuilding() throws Exception {
    
    TimeKeeper timeKeeper = AbstractEntity.getTimeKeeper();
    try {

      // ***********************************************
      // Initialize test time keeper and distributor hierarchy state evaluator
      TestTimeKeeperImpl testTimeKeeper = new TestTimeKeeperImpl("2019-11-01");
      AbstractEntity.setTimeKeeper(testTimeKeeper);
      DistributorHierarchyStateEvaluator distributorHierarchyStateEvaluator;
      distributorHierarchyStateEvaluator = new DistributorHierarchyStateEvaluator(
          distributorService,
          customerService,
          nodeHierarchyService,
          MockEmailClient.getInstance());
      
      
      // ***********************************************
      // Create an online distributor that is allowed to have out of band buildings.
      boolean allowOutOfBandBuildings = true;
      distributor = distributorService.createDistributor(
          RESOLUTE_DISTRIBUTOR_ID, 
          DistributorType.ONLINE,
          "Distributor_allowOutOfBandBuildings",
          UnitSystem.IP.toString(),
          allowOutOfBandBuildings);
      distributorId = distributor.getPersistentIdentity();

      
      // ***********************************************
      // Create an online child customer.
      customer = customerService.createCustomer(
          distributor, 
          CustomerType.ONLINE,
          "Customer_allowOutOfBandBuildings",
          UnitSystem.IP.toString());
      customerId = customer.getPersistentIdentity();

      
      // ***********************************************
      // Create the root portfolio for the customer.
      portfolio = nodeHierarchyService.createPortfolio(
          customer, 
          "Customer_allowOutOfBandBuildings", 
          "Customer_allowOutOfBandBuildings");
      portfolioId = portfolio.getPersistentIdentity();
      
      
      // ***********************************************
      // Map points so that we get a building in PENDING_ACTIVATION status.
      numPointsToMap = 10;
      mappingExpression = "/Drivers/NiagaraNetwork/{building}/{point}";
      metricIdPattern = "/Drivers/NiagaraNetwork/Building_1/Point_Y";
      mapRawPoints(numPointsToMap, numPointsToMap, metricIdPattern, mappingExpression);
      portfolio = nodeHierarchyService.loadPortfolio(customerId);
      BuildingEntity building = portfolio.getChildBuildingByName("Building_1");
      Integer buildingId = building.getPersistentIdentity();
      Assert.assertEquals("Building status is incorrect", 
          BuildingStatus.PENDING_ACTIVATION, 
          building.getBuildingStatus());

      
      // ***********************************************
      // Update the building to be out of band.
      Assert.assertTrue("allowBuildingPaymentTypeChange() is incorrect", building.allowBuildingPaymentTypeChange());
      UpdateBuildingNodesRequest updateBuildingNodesRequest = UpdateBuildingNodesRequest
          .builder()
          .withData(Arrays.asList(UpdateBuildingNodeRequest
              .builder()
              .withId(buildingId)
              .withBuildingPaymentType(BuildingPaymentType.OUT_OF_BAND.getName())
              .build()))
          .withCustomerId(customerId)
          .withSubmittedBy("tmyers@resolutebi.com")
          .build();
      List<BuildingEntity> updatedBuildings = nodeHierarchyService.updateBuildingNodes(updateBuildingNodesRequest);
      Assert.assertEquals("updatedBuildings size is incorrect", 
          "1",
          Integer.toString(updatedBuildings.size()));
      portfolio = nodeHierarchyService.loadPortfolio(customerId);
      building = portfolio.getChildBuilding(buildingId);
      Assert.assertEquals("Building payment is incorrect", 
          BuildingPaymentType.OUT_OF_BAND, 
          building.getBuildingPaymentType());
      
      
      // ***********************************************
      // Update the building back to be online.
      Assert.assertTrue("allowBuildingPaymentTypeChange() is incorrect", building.allowBuildingPaymentTypeChange());
      updateBuildingNodesRequest = UpdateBuildingNodesRequest
          .builder()
          .withData(Arrays.asList(UpdateBuildingNodeRequest
              .builder()
              .withId(buildingId)
              .withBuildingPaymentType(BuildingPaymentType.ONLINE.getName())
              .build()))
          .withCustomerId(customerId)
          .withSubmittedBy("tmyers@resolutebi.com")
          .build();
      updatedBuildings = nodeHierarchyService.updateBuildingNodes(updateBuildingNodesRequest);
      Assert.assertEquals("updatedBuildings size is incorrect", 
          "1",
          Integer.toString(updatedBuildings.size()));
      portfolio = nodeHierarchyService.loadPortfolio(customerId);
      building = portfolio.getChildBuilding(buildingId);
      Assert.assertEquals("Building payment is incorrect", 
          BuildingPaymentType.ONLINE, 
          building.getBuildingPaymentType());

      
      // ***********************************************
      // Fast forward to past the trial period
      testTimeKeeper.forwardTimeInDays(35);
      distributorHierarchyStateEvaluator.evaluatePortfolioState(portfolio);
      distributorHierarchyStateEvaluator.evaluateRootDistributorState(Arrays.asList(distributor));
   
      
      // ***********************************************
      // Attempt to update the building back to be out of band.
      Assert.assertFalse("allowBuildingPaymentTypeChange() is incorrect", building.allowBuildingPaymentTypeChange());
      updateBuildingNodesRequest = UpdateBuildingNodesRequest
          .builder()
          .withData(Arrays.asList(UpdateBuildingNodeRequest
              .builder()
              .withId(buildingId)
              .withBuildingPaymentType(BuildingPaymentType.OUT_OF_BAND.getName())
              .build()))
          .withCustomerId(customerId)
          .withSubmittedBy("tmyers@resolutebi.com")
          .build();
      try {
        updatedBuildings = nodeHierarchyService.updateBuildingNodes(updateBuildingNodesRequest);
        throw new IllegalStateException("Expected exception not thrown.");
      } catch (IllegalStateException ise) {
        if (!ise.getMessage().contains("because it is no longer in the trial period")) {
          throw ise;
        }
      }
    } finally {
      AbstractEntity.setTimeKeeper(timeKeeper);     
    }
  }
  
  @Test
  public void onlineDistributorWithOutOfBandBuilding_outOfBandDistributorNoPermission() throws Exception {

    // ***********************************************
    // Create an out of band distributor.
    distributor = distributorService.createDistributor(
        RESOLUTE_DISTRIBUTOR_ID, 
        DistributorType.OUT_OF_BAND,
        "OutOfBandDistributor",
        UnitSystem.IP.toString(),
        false);
        
    distributorId = distributor.getPersistentIdentity();

    
    // ***********************************************
    // Create an out of band child customer.
    customer = customerService.createCustomer(
        distributor, 
        CustomerType.OUT_OF_BAND,
        "OutOfBandCustomer",
        UnitSystem.IP.toString());
    customerId = customer.getPersistentIdentity();

    
    // ***********************************************
    // Create the root portfolio for the customer.
    portfolio = nodeHierarchyService.createPortfolio(
        customer, 
        "OutOfBandCustomer", 
        "OutOfBandCustomer");
    portfolioId = portfolio.getPersistentIdentity();
    
    
    // ***********************************************
    // Map points.
    numPointsToMap = 10;
    mappingExpression = "/Drivers/NiagaraNetwork/{building}/{point}";
    metricIdPattern = "/Drivers/NiagaraNetwork/Building_1/Point_Y";
    mapRawPoints(numPointsToMap, numPointsToMap, metricIdPattern, mappingExpression);
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    BuildingEntity building = portfolio.getChildBuildingByName("Building_1");
    Integer buildingId = building.getPersistentIdentity();

    
    // ***********************************************
    // Attempt to update the building to be online.
    Assert.assertFalse("allowBuildingPaymentTypeChange() is incorrect", building.allowBuildingPaymentTypeChange());
    UpdateBuildingNodesRequest updateBuildingNodesRequest = UpdateBuildingNodesRequest
        .builder()
        .withData(Arrays.asList(UpdateBuildingNodeRequest
            .builder()
            .withId(buildingId)
            .withBuildingPaymentType(BuildingPaymentType.ONLINE.getName())
            .build()))
        .withCustomerId(customerId)
        .withSubmittedBy("tmyers@resolutebi.com")
        .build();
    try {
      nodeHierarchyService.updateBuildingNodes(updateBuildingNodesRequest);
      throw new IllegalStateException("Expected exception not thrown.");
    } catch (IllegalStateException ise) {
      if (!ise.getMessage().contains("'buildingPaymentType' cannot be changed on out of band distributors")) {
        throw ise;
      }
    }
  }
  
  @Test
  public void onlineDistributorWithOutOfBandBuilding_onlineDistributorNoPermission() throws Exception {

    // ***********************************************
    // Create an out of band distributor.
    boolean allowOutOfBandBuildings = false;
    distributor = distributorService.createDistributor(
        RESOLUTE_DISTRIBUTOR_ID, 
        DistributorType.ONLINE,
        "OnlineDistributor_noAllowOutOfBandBuildings",
        UnitSystem.IP.toString(),
        allowOutOfBandBuildings);
    distributorId = distributor.getPersistentIdentity();

    
    // ***********************************************
    // Create an online child customer.
    customer = customerService.createCustomer(
        distributor, 
        CustomerType.ONLINE,
        "OnlineCustomer_noAllowOutOfBandBuildings",
        UnitSystem.IP.toString());
    customerId = customer.getPersistentIdentity();

    
    // ***********************************************
    // Create the root portfolio for the customer.
    portfolio = nodeHierarchyService.createPortfolio(
        customer, 
        "OnlineCustomer_noAllowOutOfBandBuildings", 
        "OnlineCustomer_noAllowOutOfBandBuildings");
    portfolioId = portfolio.getPersistentIdentity();
    
    
    // ***********************************************
    // Map points so that we get a building in PENDING_ACTIVATION status.
    numPointsToMap = 10;
    mappingExpression = "/Drivers/NiagaraNetwork/{building}/{point}";
    metricIdPattern = "/Drivers/NiagaraNetwork/Building_1/Point_Y";
    mapRawPoints(numPointsToMap, numPointsToMap, metricIdPattern, mappingExpression);
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    BuildingEntity building = portfolio.getChildBuildingByName("Building_1");
    Integer buildingId = building.getPersistentIdentity();
    Assert.assertEquals("Building status is incorrect", 
        BuildingStatus.PENDING_ACTIVATION, 
        building.getBuildingStatus());

    
    // ***********************************************
    // Attempt to update the building to be out of band.
    Assert.assertFalse("allowBuildingPaymentTypeChange() is incorrect", building.allowBuildingPaymentTypeChange());
    UpdateBuildingNodesRequest updateBuildingNodesRequest = UpdateBuildingNodesRequest
        .builder()
        .withData(Arrays.asList(UpdateBuildingNodeRequest
            .builder()
            .withId(buildingId)
            .withBuildingPaymentType(BuildingPaymentType.OUT_OF_BAND.getName())
            .build()))
        .withCustomerId(customerId)
        .withSubmittedBy("tmyers@resolutebi.com")
        .build();
    try {
      nodeHierarchyService.updateBuildingNodes(updateBuildingNodesRequest);
      throw new IllegalStateException("Expected exception not thrown.");
    } catch (IllegalStateException ise) {
      if (!ise.getMessage().contains("because its 'allowOutOfBandBuildings' value is false")) {
        throw ise;
      }
    }
  }
  
  @Test
  public void onlineDistributorWithOutOfBandBuilding_maxPointCapExceeded() throws Exception {
    
    int maxPointCap = 100;
    try {

      // ***********************************************
      // Reduce the max point cap.
      dictionaryService.getPaymentPlansContainer().setMaxPointCapForTesting(maxPointCap);

      
      // ***********************************************
      // Create an out of band distributor.
      boolean allowOutOfBandBuildings = true;
      distributor = distributorService.createDistributor(
          RESOLUTE_DISTRIBUTOR_ID, 
          DistributorType.ONLINE,
          "OnlineDistributor_maxPointCapExceeded",
          UnitSystem.IP.toString(),
          allowOutOfBandBuildings);
      distributorId = distributor.getPersistentIdentity();

      
      // ***********************************************
      // Create an online child customer.
      customer = customerService.createCustomer(
          distributor, 
          CustomerType.ONLINE,
          "OnlineCustomer_maxPointCapExceeded",
          UnitSystem.IP.toString());
      customerId = customer.getPersistentIdentity();

      
      // ***********************************************
      // Create the root portfolio for the customer.
      portfolio = nodeHierarchyService.createPortfolio(
          customer, 
          "OnlineCustomer_maxPointCapExceeded", 
          "OnlineCustomer_maxPointCapExceeded");
      portfolioId = portfolio.getPersistentIdentity();
      
      
      // ***********************************************
      // Map points so that we get a building in PENDING_ACTIVATION status.
      numPointsToMap = 10;
      mappingExpression = "/Drivers/NiagaraNetwork/{building}/{point}";
      metricIdPattern = "/Drivers/NiagaraNetwork/Building_1/Point_Y";
      mapRawPoints(numPointsToMap, numPointsToMap, metricIdPattern, mappingExpression);
      portfolio = nodeHierarchyService.loadPortfolio(customerId);
      BuildingEntity building = portfolio.getChildBuildingByName("Building_1");
      Integer buildingId = building.getPersistentIdentity();
      Assert.assertEquals("Building status is incorrect", 
          BuildingStatus.PENDING_ACTIVATION, 
          building.getBuildingStatus());

      
      // ***********************************************
      // Update the building to be out of band.
      Assert.assertTrue("allowBuildingPaymentTypeChange() is incorrect", building.allowBuildingPaymentTypeChange());
      UpdateBuildingNodesRequest updateBuildingNodesRequest = UpdateBuildingNodesRequest
          .builder()
          .withData(Arrays.asList(UpdateBuildingNodeRequest
              .builder()
              .withId(buildingId)
              .withBuildingPaymentType(BuildingPaymentType.OUT_OF_BAND.getName())
              .build()))
          .withCustomerId(customerId)
          .withSubmittedBy("tmyers@resolutebi.com")
          .build();
      List<BuildingEntity> updatedBuildings = nodeHierarchyService.updateBuildingNodes(updateBuildingNodesRequest);
      Assert.assertEquals("updatedBuildings size is incorrect", 
          "1",
          Integer.toString(updatedBuildings.size()));
      
      
      // ***********************************************
      // Map more points so that we get a point count that exceeds the max point cap of payment plans.
      numPointsToMap = maxPointCap + 10;
      mappingExpression = "/Drivers/NiagaraNetwork/{building}/{floor}/{point}";
      metricIdPattern = "/Drivers/NiagaraNetwork/Building_1/Floor_1/Point_Y";
      mapRawPoints(numPointsToMap, numPointsToMap, metricIdPattern, mappingExpression);
      portfolio = nodeHierarchyService.loadPortfolio(customerId);
      building = portfolio.getChildBuildingByName("Building_1");
      buildingId = building.getPersistentIdentity();
      Assert.assertEquals("Building status is incorrect", 
          BuildingStatus.PENDING_ACTIVATION, 
          building.getBuildingStatus());
      Assert.assertTrue("building point count is incorrect", (building.getTotalMappedPointCount() > maxPointCap));

      
      // ***********************************************
      // Attempt to update the building back to be online.
      Assert.assertFalse("allowBuildingPaymentTypeChange() is incorrect", building.allowBuildingPaymentTypeChange());
      updateBuildingNodesRequest = UpdateBuildingNodesRequest
          .builder()
          .withData(Arrays.asList(UpdateBuildingNodeRequest
              .builder()
              .withId(buildingId)
              .withBuildingPaymentType(BuildingPaymentType.ONLINE.getName())
              .build()))
          .withCustomerId(customerId)
          .withSubmittedBy("tmyers@resolutebi.com")
          .build();
      try {
        nodeHierarchyService.updateBuildingNodes(updateBuildingNodesRequest);
        throw new IllegalStateException("Expected exception not thrown.");
      } catch (IllegalStateException ise) {
        if (!ise.getMessage().contains("exceeds the online payment point cap value of")) {
          throw ise;
        }
      }
    } finally {
      DictionaryContext.setPaymentPlansContainer(null);
      dictionaryService.ensureDictionaryDataIsLoaded();
   }
  }
  
  @Test
  public void onlineDistributorWithOutOfBandBuilding_updateBeforePointMapping() throws Exception {

    // ***********************************************
    // Create an online distributor that is allowed to have out of band buildings.
    boolean allowOutOfBandBuildings = true;
    distributor = distributorService.createDistributor(
        RESOLUTE_DISTRIBUTOR_ID, 
        DistributorType.ONLINE,
        "Distributor_allowOutOfBandBuildings",
        UnitSystem.IP.toString(),
        allowOutOfBandBuildings);
    distributorId = distributor.getPersistentIdentity();

    
    // ***********************************************
    // Create an online child customer.
    customer = customerService.createCustomer(
        distributor, 
        CustomerType.ONLINE,
        "Customer_allowOutOfBandBuildings",
        UnitSystem.IP.toString());
    customerId = customer.getPersistentIdentity();

    
    // ***********************************************
    // Create the root portfolio for the customer.
    portfolio = nodeHierarchyService.createPortfolio(
        customer, 
        "Customer_allowOutOfBandBuildings", 
        "Customer_allowOutOfBandBuildings");
    portfolioId = portfolio.getPersistentIdentity();

    
    // ***********************************************
    // Create a building manually (i.e. node editor)
    Map<String, Object> additionalProperties = new HashMap<>();
    CreateNodeRequest createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.BUILDING)
        .withParentId(null)
        .withName("Building_1")
        .withDisplayName("Building_1")
        .withAdditionalProperties(additionalProperties)
        .build();
    BuildingEntity building = (BuildingEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer buildingId = building.getPersistentIdentity();
    
    
    // ***********************************************
    // Attempt to update the building to be out of band (even though we haven't mapped any points yet)
    Assert.assertFalse("allowBuildingPaymentTypeChange() is incorrect", building.allowBuildingPaymentTypeChange());
    UpdateBuildingNodesRequest updateBuildingNodesRequest = UpdateBuildingNodesRequest
        .builder()
        .withData(Arrays.asList(UpdateBuildingNodeRequest
            .builder()
            .withId(buildingId)
            .withBuildingPaymentType(BuildingPaymentType.OUT_OF_BAND.getName())
            .build()))
        .withCustomerId(customerId)
        .withSubmittedBy("tmyers@resolutebi.com")
        .build();
    try {
      nodeHierarchyService.updateBuildingNodes(updateBuildingNodesRequest);
      throw new IllegalStateException("Expected exception not thrown.");
    } catch (IllegalStateException ise) {
      if (!ise.getMessage().contains("because its trial period has not started yet (i.e. no points have been mapped yet)")) {
        throw ise;
      }
    }
  }
  
  @Test
  public void getPaymentMethodRefCounts() throws Exception {
    
    // STEP 1: ARRANGE
    AbstractDistributorEntity distributor = distributorService.createDistributor(
        DistributorRepository.ROOT_DISTRIBUTOR_ID,
        DistributorType.ONLINE,
        "Test Online Distributor for getPaymentMethodRefCounts()",
        UnitSystem.IP.toString(),
        false);

    
    
    
    // STEP 2: ACT
    Map<Integer, Integer> paymentMethodRefCounts = nodeHierarchyService.getPaymentMethodRefCounts(distributor.getPersistentIdentity());

    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("paymentMethodRefCounts is null", paymentMethodRefCounts);
  }
  
  @Test
  public void setEquipmentMetadataTags() throws Exception {
    
    // ***********************************************
    // Create building 1.
    Map<String, Object> additionalProperties = new HashMap<>();
    CreateNodeRequest createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.BUILDING)
        .withParentId(null)
        .withName("Building_1")
        .withDisplayName("Building_1")
        .withAdditionalProperties(additionalProperties)
        .build();
    BuildingEntity building1 = (BuildingEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer building1Id = building1.getPersistentIdentity();


    // ***********************************************
    // Create rooftop AHU 1.
    createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.EQUIPMENT)
        .withParentId(building1Id)
        .withName("AHU")
        .withDisplayName("AHU")
        .withAdditionalProperties(additionalProperties)
        .build();
    EquipmentEntity ahu1 = (EquipmentEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer ahu1Id = ahu1.getPersistentIdentity();
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    ahu1 = portfolio.getEquipment(ahu1Id);
    Assert.assertNotNull("ahu is null", ahu1);

    
    // ***********************************************
    // Set the equipment type first.
    TagsContainer tagsContainer = dictionaryService.getTagsContainer();
    EquipmentEnergyExchangeTypeEntity ahuEquipmentType = tagsContainer.getEquipmentTypeByName("ahu");
    List<EnergyExchangeSystemNodeData> energyExchangeSystemNodeData = new ArrayList<>();

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .withId(ahu1Id)
        .withTypeId(ahuEquipmentType.getPersistentIdentity())
        .build());
    
    UpdateEnergyExchangeSystemNodesRequest updateEnergyExchangeSystemNodesRequest = UpdateEnergyExchangeSystemNodesRequest
        .builder()
        .withBuildingId(building1Id)
        .withCustomerId(customerId)
        .withSubmittedBy("tmyers@resolutebi.com")
        .withData(energyExchangeSystemNodeData)
        .build(); 
    nodeHierarchyService.updateEnergyExchangeSystemNodes(updateEnergyExchangeSystemNodesRequest);
    

    // **********************************************************************************************
    // Verify equipment type tag.
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    ahu1 = portfolio.getEquipment(ahu1Id);
    Assert.assertNotNull("ahu1 equipment type is null", ahu1.getEquipmentTypeNullIfNotExists());
    Assert.assertEquals("ahu1 equipment type is incorrect", ahuEquipmentType, ahu1.getEquipmentTypeNullIfNotExists());
    Assert.assertTrue("ahu1 metadata tags is incorrect", ahu1.getMetadataTags().isEmpty());

    
    // ***********************************************
    // Set the equipment metadata tags next.
    energyExchangeSystemNodeData = new ArrayList<>();

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .withId(ahu1Id)
        .withTypeId(EnergyExchangeSystemNodeData.IGNORE)
        .withMetadataTags(Arrays.asList("rooftop"))
        .build());
    
    updateEnergyExchangeSystemNodesRequest = UpdateEnergyExchangeSystemNodesRequest
        .builder()
        .withBuildingId(building1Id)
        .withCustomerId(customerId)
        .withSubmittedBy("tmyers@resolutebi.com")
        .withData(energyExchangeSystemNodeData)
        .build(); 
    nodeHierarchyService.updateEnergyExchangeSystemNodes(updateEnergyExchangeSystemNodesRequest);
    

    // **********************************************************************************************
    // Verify metadata tags (and that the equipment type tag is still set).
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    ahu1 = portfolio.getEquipment(ahu1Id);
    Assert.assertNotNull("ahu1 equipment type is null", ahu1.getEquipmentTypeNullIfNotExists());
    Assert.assertEquals("ahu1 equipment type is incorrect", ahuEquipmentType, ahu1.getEquipmentTypeNullIfNotExists());
    Assert.assertTrue("ahu1 metadata tags is incorrect", ahu1.getMetadataTags().toString().equals(Arrays.asList("rooftop").toString()));    
    
    
    // ***********************************************
    // Remove the equipment metadata tags.
    energyExchangeSystemNodeData = new ArrayList<>();

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .withId(ahu1Id)
        .withMetadataTags(new ArrayList<>())
        .build());

    updateEnergyExchangeSystemNodesRequest = UpdateEnergyExchangeSystemNodesRequest
        .builder()
        .withBuildingId(building1Id)
        .withCustomerId(customerId)
        .withSubmittedBy("tmyers@resolutebi.com")
        .withData(energyExchangeSystemNodeData)
        .build(); 
    nodeHierarchyService.updateEnergyExchangeSystemNodes(updateEnergyExchangeSystemNodesRequest);
    
    
    // **********************************************************************************************
    // Verify metadata tags were removed (and that the equipment type tag is still set).
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    ahu1 = portfolio.getEquipment(ahu1Id);
    Assert.assertNotNull("ahu1 equipment type is null", ahu1.getEquipmentTypeNullIfNotExists());
    Assert.assertEquals("ahu1 equipment type is incorrect", ahuEquipmentType, ahu1.getEquipmentTypeNullIfNotExists());
    Assert.assertTrue("ahu1 metadata tags is incorrect", ahu1.getMetadataTags().isEmpty());
 
    
    // ***********************************************
    // Remove the equipment type tag.
    energyExchangeSystemNodeData = new ArrayList<>();

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .withId(ahu1Id)
        .withTypeId(EnergyExchangeSystemNodeData.NULL)
        .build());

    updateEnergyExchangeSystemNodesRequest = UpdateEnergyExchangeSystemNodesRequest
        .builder()
        .withBuildingId(building1Id)
        .withCustomerId(customerId)
        .withSubmittedBy("tmyers@resolutebi.com")
        .withData(energyExchangeSystemNodeData)
        .build(); 
    nodeHierarchyService.updateEnergyExchangeSystemNodes(updateEnergyExchangeSystemNodesRequest);
    
    
    // **********************************************************************************************
    // Verify the equipment type tag was removed.
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    ahu1 = portfolio.getEquipment(ahu1Id);
    Assert.assertNull("ahu1 equipment type is not null", ahu1.getEquipmentTypeNullIfNotExists());
    Assert.assertTrue("ahu1 metadata tags is incorrect", ahu1.getMetadataTags().isEmpty());    
  }
  
  @Ignore
  @Test
  public void performPortfolioMaintenance_missingTemporalConfigVar() throws Exception {
   
    // STEP 1: ARRANGE
    customerId = MCLAREN_CUSTOMER_ID;
    
    // 59465   2000    $23
    // 40729   2000    $24
    // Verify that the custom point with two deleted child var points in question do not exist
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    AbstractNodeEntity point1 = portfolio.getChildNodeNullIfNotExists(59465);
    AbstractNodeEntity point2 = portfolio.getChildNodeNullIfNotExists(40729);
    Assert.assertNull("point1 is non-null", point1);
    Assert.assertNull("point2 is non-null", point2);
    AbstractNodeEntity customPoint = portfolio.getChildNodeNullIfNotExists(1048108);
    Assert.assertNotNull("customPoint is null", customPoint);
          
    // Perform portfolio maintenance so that the issue is remediated
    boolean performStripePaymentProcessing = false;
    List<Integer> customerIdList = Arrays.asList(customerId);
    MockEmailClient emailClient = MockEmailClient.getInstance();
    DistributorHierarchyStateEvaluator distributorHierarchyStateEvaluator = new DistributorHierarchyStateEvaluator(
        distributorService,
        customerService,
        nodeHierarchyService,
        emailClient);
    
    
    
    // STEP 2: ACT
    nodeHierarchyService.performPortfolioMaintenance(
        customerIdList, 
        distributorHierarchyStateEvaluator, 
        performStripePaymentProcessing);


    
    // STEP 3: ASSERT
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    customPoint = portfolio.getChildNodeNullIfNotExists(1048108);
    Assert.assertNull("customPoint is non-null", customPoint);
  }
  
  @Ignore
  @Test
  public void performPortfolioMaintenance_shouldHaveNoModifications() throws Exception {
   
    // STEP 1: ARRANGE
    String buildingName = "Macomb";
    Integer reportTemplateId = 10; // AHU Controller Performance
    customerId = MCLAREN_CUSTOMER_ID;
    
    boolean performStripePaymentProcessing = false;
    List<Integer> customerIdList = Arrays.asList(customerId);
    MockEmailClient emailClient = MockEmailClient.getInstance();
    DistributorHierarchyStateEvaluator distributorHierarchyStateEvaluator = new DistributorHierarchyStateEvaluator(
        distributorService,
        customerService,
        nodeHierarchyService,
        emailClient);

    nodeHierarchyService.performPortfolioMaintenance(
        customerIdList, 
        distributorHierarchyStateEvaluator, 
        performStripePaymentProcessing);
    
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    ReportInstanceEntity reportInstanceBefore = portfolio.getChildBuildingByName(buildingName).getReportInstanceByReportTemplateId(reportTemplateId);  
    String greenBefore = ReportInstanceEntity.buildGreenEquipmentHierarchy(reportInstanceBefore, reportInstanceBefore.getReportInstanceEquipment());
    String redBefore = ReportInstanceEntity.buildRedEquipmentHierarchy(reportInstanceBefore, reportInstanceBefore.getReportInstanceEquipmentErrorMessages());

    
    
    // STEP 2: ACT
    nodeHierarchyService.performPortfolioMaintenance(
        customerIdList, 
        distributorHierarchyStateEvaluator, 
        performStripePaymentProcessing);


    
    // STEP 3: ASSERT
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    ReportInstanceEntity reportInstanceAfter = portfolio.getChildBuildingByName(buildingName).getReportInstanceByReportTemplateId(reportTemplateId); 
    String greenAfter = ReportInstanceEntity.buildGreenEquipmentHierarchy(reportInstanceAfter, reportInstanceAfter.getReportInstanceEquipment());
    String redAfter = ReportInstanceEntity.buildRedEquipmentHierarchy(reportInstanceAfter, reportInstanceAfter.getReportInstanceEquipmentErrorMessages());

    Assert.assertEquals("Green equipment before vs after is incorrect", 
        greenBefore, 
        greenAfter);

    Assert.assertEquals("Red equipment before vs after is incorrect", 
        redBefore, 
        redAfter);
  }
  
  /*
    POINT MAPPING #1
    /Drivers/NiagaraNetwork/{building}/points/HVAC/{subBuilding}/{equipment}/{point}
    /Drivers/NiagaraNetwork/Building1/points/HVAC/South/AC_11/AhuState
    ||
    \/         {building}/{subBuilding}/{equipment}/{point}
    /Portfolio/Building1 /South        /AC_11      /AhuState
    
    POINT MAPPING #2
    /Drivers/NiagaraNetwork/{building}/points/HVAC/{subBuilding}/{floor}/{equipment}/{point}
    /Drivers/NiagaraNetwork/Building1/points/HVAC/South/AC_11/Sf_Vfd_Pts/AlmAct
    ||
    \/         {building}/{subBuilding}/{floor}/{equipment}/{point}
    /Portfolio/Building1 /South        /AC_11  /Sf_Vfd_Pts /AlmAct
    
    ISSUE:
    Point mapping #2 creates a floor under Building1/South named "AC_11", when in fact, 
    there already exists equipment under Building1/South named "AC_11".
    
    RESOLUTION: 
    Since equipment can exist under other equipment in the node hierarchy, then the 
    "Sf_Vfd_Pts" equipment, and the point underneath it, "AlmAct", will be mapped to 
    the "AC_11" equipment. 
   */
  @Test
  public void mapRawPoints_preexisting_AC_11_node() throws Exception {
    
    // POINT MAPPING #1
    List<RawPointEntity> rawPoints = Arrays.asList(buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building1/points/HVAC/South/AC_11/AhuState"));
    customerId = customer.getPersistentIdentity();
    customer.addRawPoints(rawPoints);
    customer = customerService.updateCustomer(customer, true);
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
        .withMappingExpression("/Drivers/NiagaraNetwork/{building}/points/HVAC/{subBuilding}/{equipment}/{point}")
        .withMetricIdDelimiter(NodeHierarchyService.DEFAULT_METRIC_ID_DELIMITER)
        .build(); 
    createdMappablePoints = nodeHierarchyService.mapRawPoints(mapRawPointsRequest);
    Assert.assertEquals("created mappable points list size is incorrect", 
        "1", 
        Integer.toString(createdMappablePoints.size()));

    
    // POINT MAPPING #2
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    customer = portfolio.getParentCustomer();
    rawPoints = Arrays.asList(buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building1/points/HVAC/South/AC_11/Sf_Vfd_Pts/AlmAct"));
    customer.addRawPoints(rawPoints);
    customer = customerService.updateCustomer(customer, true);
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
        .withCustomerId(customer.getPersistentIdentity())
        .withRawPoints(rawPointData)
        .withMappingExpression("/Drivers/NiagaraNetwork/{building}/points/HVAC/{subBuilding}/{floor}/{equipment}/{point}")
        .withMetricIdDelimiter(NodeHierarchyService.DEFAULT_METRIC_ID_DELIMITER)
        .build(); 
    createdMappablePoints = nodeHierarchyService.mapRawPoints(mapRawPointsRequest);
    Assert.assertEquals("created mappable points list size is incorrect", 
        "1", 
        Integer.toString(createdMappablePoints.size()));
    
    
    
    // VERIFY THAT AC_11 IS NOT MAPPED AS A FLOOR, INSTEAD THE PRE-EXISTING AC_11 EQUIPMENT IS USED
    MappablePointEntity mappablePoint = createdMappablePoints.get(0);
    AbstractNodeEntity parentNode = mappablePoint.getParentNode();
    AbstractNodeEntity grandParentNode = parentNode.getParentNode();

    Assert.assertEquals("grand parent node type is incorrect, expected EquipmentEntity, but was: " + grandParentNode.getClass().getSimpleName(), 
        "EquipmentEntity", 
        grandParentNode.getClass().getSimpleName());

    Assert.assertEquals("grand parent node name is incorrect, expected AC_11, but was: " + grandParentNode.getName(), 
        "AC_11", 
        grandParentNode.getName());
  }
  
  @Test
  public void moveChildNodes_equipmentToOtherEquipment() throws Exception {
    
    // POINT MAPPING #1
    List<RawPointEntity> rawPoints = Arrays.asList(buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building1/points/HVAC/South/AC_11/AhuState"));
    customerId = customer.getPersistentIdentity();
    customer.addRawPoints(rawPoints);
    customer = customerService.updateCustomer(customer, true);
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
        .withMappingExpression("/Drivers/NiagaraNetwork/{building}/points/HVAC/{subBuilding}/{equipment}/{point}")
        .withMetricIdDelimiter(NodeHierarchyService.DEFAULT_METRIC_ID_DELIMITER)
        .build(); 
    createdMappablePoints = nodeHierarchyService.mapRawPoints(mapRawPointsRequest);
    Assert.assertEquals("created mappable points list size is incorrect", 
        "1", 
        Integer.toString(createdMappablePoints.size()));

    
    // POINT MAPPING #2
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    customer = portfolio.getParentCustomer();
    rawPoints = Arrays.asList(buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building1/points/HVAC/South/AC_11/Sf_Vfd_Pts/AlmAct"));
    customer.addRawPoints(rawPoints);
    customer = customerService.updateCustomer(customer, true);
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
        .withCustomerId(customer.getPersistentIdentity())
        .withRawPoints(rawPointData)
        .withMappingExpression("/Drivers/NiagaraNetwork/{building}/points/HVAC/{subBuilding}/{floor}/{equipment}/{point}")
        .withMetricIdDelimiter(NodeHierarchyService.DEFAULT_METRIC_ID_DELIMITER)
        .build(); 
    createdMappablePoints = nodeHierarchyService.mapRawPoints(mapRawPointsRequest);
    Assert.assertEquals("created mappable points list size is incorrect", 
        "1", 
        Integer.toString(createdMappablePoints.size()));
    MappablePointEntity mappablePoint = createdMappablePoints.get(0);
    AbstractNodeEntity parentNode = mappablePoint.getParentNode();
    AbstractNodeEntity grandParentNode = parentNode.getParentNode();
    Integer newParentId = grandParentNode.getPersistentIdentity();

    
    // POINT MAPPING #3
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    customer = portfolio.getParentCustomer();
    rawPoints = Arrays.asList(buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building1/points/HVAC/South/Rf_Vfd_Pts/AlmAct"));
    customer.addRawPoints(rawPoints);
    customer = customerService.updateCustomer(customer, true);
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
        .withCustomerId(customer.getPersistentIdentity())
        .withRawPoints(rawPointData)
        .withMappingExpression("/Drivers/NiagaraNetwork/{building}/points/HVAC/{subBuilding}/{equipment}/{point}")
        .withMetricIdDelimiter(NodeHierarchyService.DEFAULT_METRIC_ID_DELIMITER)
        .build(); 
    createdMappablePoints = nodeHierarchyService.mapRawPoints(mapRawPointsRequest);
    Assert.assertEquals("created mappable points list size is incorrect", 
        "1", 
        Integer.toString(createdMappablePoints.size()));
    
    
    // MOVE EQUIPMENT Rf_Vfd_Pts TO AC_11
    mappablePoint = createdMappablePoints.get(0);
    Integer mappablePointId = mappablePoint.getPersistentIdentity();
    parentNode = mappablePoint.getParentNode();
    Integer childId = parentNode.getPersistentIdentity();
    MoveChildNodesRequest moveChildNodesRequest = MoveChildNodesRequest
        .builder()
        .withCustomerId(customerId)
        .withNewParentId(newParentId)
        .withChildIds(Arrays.asList(childId))
        .build();
    movedNodes = nodeHierarchyService.moveChildNodesToNewParentNode(moveChildNodesRequest);
    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("movedNodes is null", movedNodes);
    Assert.assertEquals("movedNodes size is incorrect", "1", Integer.toString(movedNodes.size()));
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    mappablePoint = portfolio.getMappablePoint(mappablePointId);
    parentNode = mappablePoint.getParentNode();
    grandParentNode = parentNode.getParentNode();
    
    Assert.assertEquals("grand parent node type is incorrect, expected EquipmentEntity, but was: " + grandParentNode.getClass().getSimpleName(), 
        "EquipmentEntity", 
        grandParentNode.getClass().getSimpleName());

    Assert.assertEquals("grand parent node name is incorrect, expected AC_11, but was: " + grandParentNode.getName(), 
        "AC_11", 
        grandParentNode.getName());
  }
  
  @Test
  public void updateEnergyExchangeSystemNodes_convertEnergyExchangeSystemNode() throws Exception {
    
    // STEP 1: ARRANGE
    // POINT MAPPING #1
    List<RawPointEntity> rawPoints = new ArrayList<>();
    rawPoints.add(buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building1/points/HVAC/South/AC_11/AhuState"));
    rawPoints.add(buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building1/points/HVAC/South/AC_11/CdxMaxDmprSp"));
    rawPoints.add(buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building1/points/HVAC/South/ChlrPlant/Chlr1Alm"));
    rawPoints.add(buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building1/points/HVAC/South/ChlrPlant/Chlr1Ena"));
    String mappingExpression1 = "/Drivers/NiagaraNetwork/{building}/points/HVAC/{subBuilding}/{equipment}/{point}";
    customerId = customer.getPersistentIdentity();
    customer.addRawPoints(rawPoints);
    customer = customerService.updateCustomer(customer, true);
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
        .withMappingExpression(mappingExpression1)
        .withMetricIdDelimiter(NodeHierarchyService.DEFAULT_METRIC_ID_DELIMITER)
        .build(); 
    createdMappablePoints = nodeHierarchyService.mapRawPoints(mapRawPointsRequest);
    Assert.assertEquals("created mappable points list size is incorrect", 
        "4", 
        Integer.toString(createdMappablePoints.size()));

    // POINT MAPPING #2
    rawPoints = new ArrayList<>();
    rawPoints.add(buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building1/points/HVAC/South/ChlrPlant/Tower/Pmp1Dp"));
    rawPoints.add(buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building1/points/HVAC/South/ChlrPlant/Tower/PMP1Runtime"));         
    String mappingExpression2 = "/Drivers/NiagaraNetwork/{building}/points/HVAC/{subBuilding}/{equipment}/Tower/{point}";
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    customer = portfolio.getParentCustomer();
    customer.addRawPoints(rawPoints);
    customer = customerService.updateCustomer(customer, true);
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
        .withCustomerId(customer.getPersistentIdentity())
        .withRawPoints(rawPointData)
        .withMappingExpression(mappingExpression2)
        .withMetricIdDelimiter(NodeHierarchyService.DEFAULT_METRIC_ID_DELIMITER)
        .build(); 
    createdMappablePoints = nodeHierarchyService.mapRawPoints(mapRawPointsRequest);
    Assert.assertEquals("created mappable points list size is incorrect", 
        "2", 
        Integer.toString(createdMappablePoints.size()));
    
    // POINT MAPPING #3
    rawPoints = new ArrayList<>();
    rawPoints.add(buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building1/points/HVAC/South/ChlrPlant/Tower/Fan1_Vfd/AlmAct"));   
    rawPoints.add(buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building1/points/HVAC/South/ChlrPlant/Tower/Fan1_Vfd/Ao1Act"));
    rawPoints.add(buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building1/points/HVAC/South/ChlrPlant/Tower/Fan2_Vfd/AlmAct"));
    rawPoints.add(buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building1/points/HVAC/South/ChlrPlant/Tower/Fan2_Vfd/Ao1Act"));
    String mappingExpression3 = "/Drivers/NiagaraNetwork/{building}/points/HVAC/{subBuilding}/ChlrPlant/Tower/{equipment}/{point}";    
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    customer = portfolio.getParentCustomer();
    customer.addRawPoints(rawPoints);
    customer = customerService.updateCustomer(customer, true);
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
        .withCustomerId(customer.getPersistentIdentity())
        .withRawPoints(rawPointData)
        .withMappingExpression(mappingExpression3)
        .withMetricIdDelimiter(NodeHierarchyService.DEFAULT_METRIC_ID_DELIMITER)
        .build(); 
    createdMappablePoints = nodeHierarchyService.mapRawPoints(mapRawPointsRequest);
    Assert.assertEquals("created mappable points list size is incorrect", 
        "4", 
        Integer.toString(createdMappablePoints.size()));
    
    
    
    // STEP 2: ACT
    // Convert equipment to plant
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    String nodePath = portfolio.getDisplayName() + "/Building1/South/ChlrPlant";
    AbstractNodeEntity node = portfolio.getChildNodeByNodePath(nodePath);
    int numChildrenBefore = node.getAllChildNodes().size();
    List<EnergyExchangeSystemNodeData> energyExchangeSystemNodeDataList = new ArrayList<>();
    energyExchangeSystemNodeDataList.add(EnergyExchangeSystemNodeData
        .builder()
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .withId(node.getPersistentIdentity())
        .withConvertToNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_PLANT)
        .build());
    
    UpdateEnergyExchangeSystemNodesRequest updateEquipmentNodesRequest = UpdateEnergyExchangeSystemNodesRequest
        .builder()
        .withCustomerId(customerId)
        .withData(energyExchangeSystemNodeDataList)
        .build(); 
    nodeHierarchyService.updateEnergyExchangeSystemNodes(updateEquipmentNodesRequest);

    
    
    // STEP 3: ASSERT
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    node = portfolio.getChildNodeByNodePath(nodePath);
    int numChildrenAfter = node.getAllChildNodes().size();
    Assert.assertEquals("node type is incorrect, expected PlantEntity, but was: " + node.getClass().getSimpleName(), 
        "PlantEntity", 
        node.getClass().getSimpleName());
    Assert.assertEquals("child node size is incorrect, expected: " + numChildrenBefore + ", but was: numChildrenAfter", 
        Integer.toString(numChildrenBefore), 
        Integer.toString(numChildrenAfter));
  }
  
  @Test
  public void duplicatePortfolio() throws Exception {
    
    // STEP 1: ARRANGE
    int numBuildings = 1;
    int numFloors = 1;
    int numEquipmentPerEquipmentType = 1;
    int numPointsPerEquipmentType = 1;
    Set<String> equipmentTypeNames = new HashSet<>();
    Set<String> pointTemplateNames = new HashSet<>();
    boolean performPointMapping = true;
    boolean performEquipmentTagging = true;
    boolean performPointTagging = true;
    boolean createAdFunctionInstanceCandidates = true;
    boolean createAdFunctionInstances = true;
    boolean evaluateReports = true;
    boolean enableReports = true; 
    
    int expectedQuantity = (numFloors+1);
    expectedQuantity = expectedQuantity * numEquipmentPerEquipmentType;
    expectedQuantity = expectedQuantity * numPointsPerEquipmentType;
    expectedQuantity = expectedQuantity * equipmentTypeNames.size();
    expectedQuantity = expectedQuantity * pointTemplateNames.size();
    
    NodeHierarchyTestDataBuilderOptions nodeHierarchyTestDataBuilderOptions = NodeHierarchyTestDataBuilderOptions
        .builder()
        .withNumBuildings(numBuildings)
        .withNumFloors(0) // At a minimum, there is a set of rooftop units.
        .withNumEquipmentPerEquipmentType(5)
        .withNumPointsPerEquipmentType(1)
        .withEquipmentTypeNames(equipmentTypeNames)
        .withPointTemplateNames(pointTemplateNames)
        .withPerformPointMapping(performPointMapping)
        .withPerformEquipmentTagging(performEquipmentTagging)
        .withPerformPointTagging(performPointTagging)
        .withCreateAdFunctionInstanceCandidates(createAdFunctionInstanceCandidates)
        .withCreateAdFunctionInstances(createAdFunctionInstances)
        .withEvaluateReports(evaluateReports)
        .withEnableReports(enableReports)
        .build();
    
    Integer customerId = nodeHierarchyTestDataBuilder.createNodeHierarchy(nodeHierarchyTestDataBuilderOptions);
    
    Integer startingIndex = 1;
    Integer duplicationFactor = 1; // Make 1 copy.
    
    PortfolioEntity portfolioBefore = nodeHierarchyService.loadPortfolio(customerId);
    
    int numBuildingsBefore = portfolioBefore.getAllBuildings().size();
    int numEquipmentBefore = portfolioBefore.getAllEquipment().size();
    int numMappablePointsBefore = portfolioBefore.getAllMappablePoints().size();
    int numAdFunctionInstancesBefore = portfolioBefore.getAllAdFunctionInstances().size();
    int numEnabledReportsBefore = portfolioBefore.getAllEnabledReportInstances().size();

    
    
    // STEP 2: ACT
    nodeHierarchyService.duplicatePortfolio(customerId, startingIndex, duplicationFactor);
    
    
    
    // STEP 3: ASSERT
    PortfolioEntity portfolioAfter = nodeHierarchyService.loadPortfolio(customerId);
    
    int numBuildingsAfter = portfolioAfter.getAllBuildings().size();
    int numEquipmentAfter = portfolioAfter.getAllEquipment().size();
    int numMappablePointsAfter = portfolioAfter.getAllMappablePoints().size();
    int numAdFunctionInstancesAfter = portfolioAfter.getAllAdFunctionInstances().size();
    int numEnabledReportsAfter = portfolioAfter.getAllEnabledReportInstances().size();
    
    System.err.println("numBuildingsBefore: " + numBuildingsBefore + ", numBuildingsAfter: " + numBuildingsAfter);
    System.err.println("numEquipmentBefore: " + numEquipmentBefore + ", numEquipmentAfter: " + numEquipmentAfter);
    System.err.println("numMappablePointsBefore: " + numMappablePointsBefore + ", numMappablePointsAfter: " + numMappablePointsAfter);
    System.err.println("numAdFunctionInstancesBefore: " + numAdFunctionInstancesBefore + ", numAdFunctionInstancesAfter: " + numAdFunctionInstancesAfter);
    System.err.println("numEnabledReportsBefore: " + numEnabledReportsBefore + ", numEnabledReportsAfter: " + numEnabledReportsAfter);
    System.err.println();
    
    /* TODO:
    Assert.assertEquals("duplicated portfolio size is incorrect", 
        (numNodesBefore * (duplicationFactor+1)), 
        numNodesAfter);
    */      
  }
  
  @Test
  public void deleteChildNodes_loop() throws Exception {
    
    // ***********************************************
    // Create Building_1.
    Map<String, Object> additionalProperties = new HashMap<>();
    String buildingName = "Building_1";
    CreateNodeRequest createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.BUILDING)
        .withParentId(null)
        .withName(buildingName)
        .withDisplayName(buildingName)
        .withAdditionalProperties(additionalProperties)
        .build();
    BuildingEntity building1 = (BuildingEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer building1Id = building1.getPersistentIdentity();
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building1 = portfolio.getChildBuilding(building1Id);
    Assert.assertNotNull("building1 is null", building1);


    // ***********************************************
    // Create Chilled_Water_Plant.
    createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.PLANT)
        .withParentId(building1Id)
        .withName("Chilled_Water_Plant")
        .withDisplayName("Chilled_Water_Plant")
        .withAdditionalProperties(additionalProperties)
        .build();
    PlantEntity chilledWaterPlant = (PlantEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer chilledWaterPlantId = chilledWaterPlant.getPersistentIdentity();
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    chilledWaterPlant = portfolio.getPlant(chilledWaterPlantId);
    Assert.assertNotNull("chilledWaterPlant is null", chilledWaterPlant);
    
    
    // ***********************************************
    // Create Primary_Loop.
    additionalProperties.put("energyExchangeSystemTypeId", CreateNodeRequest.CHILLED_WATER_SYSTEM_TYPE_ID);
    createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.LOOP)
        .withParentId(chilledWaterPlantId)
        .withName("Primary_Loop")
        .withDisplayName("Primary_Loop")
        .withAdditionalProperties(additionalProperties)
        .build();
    LoopEntity primaryLoop = (LoopEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer primaryLoopId = primaryLoop.getPersistentIdentity();
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    primaryLoop = portfolio.getLoop(primaryLoopId);
    Assert.assertNotNull("primaryLoop is null", primaryLoop);
    additionalProperties.clear();

    
    
    // ***********************************************
    // Create Secondary_Loop.
    additionalProperties.put("energyExchangeSystemTypeId", CreateNodeRequest.CHILLED_WATER_SYSTEM_TYPE_ID);
    createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.LOOP)
        .withParentId(chilledWaterPlantId)
        .withName("Secondary_Loop")
        .withDisplayName("Secondary_Loop")
        .withAdditionalProperties(additionalProperties)
        .build();
    LoopEntity secondaryLoop = (LoopEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer secondaryLoopId = secondaryLoop.getPersistentIdentity();
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    secondaryLoop = portfolio.getLoop(secondaryLoopId);
    Assert.assertNotNull("secondaryLoop is null", secondaryLoop);
    additionalProperties.clear();
    
    
    // ***********************************************
    // Create Tertiary_Loop.
    additionalProperties.put("energyExchangeSystemTypeId", CreateNodeRequest.CHILLED_WATER_SYSTEM_TYPE_ID);
    createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.LOOP)
        .withParentId(chilledWaterPlantId)
        .withName("Tertiary_Loop")
        .withDisplayName("Tertiary_Loop")
        .withAdditionalProperties(additionalProperties)
        .build();
    LoopEntity tertiaryLoop = (LoopEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer tertiaryLoopId = tertiaryLoop.getPersistentIdentity();
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    tertiaryLoop = portfolio.getLoop(tertiaryLoopId);
    Assert.assertNotNull("tertiaryLoop is null", tertiaryLoop);
    additionalProperties.clear();    
    
    
    // ***********************************************
    // Create pump 1.
    createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.EQUIPMENT)
        .withParentId(building1Id)
        .withName("Pump_1")
        .withDisplayName("Pump_1")
        .withAdditionalProperties(additionalProperties)
        .build();
    EquipmentEntity pump1 = (EquipmentEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer pump1Id = pump1.getPersistentIdentity();    
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    pump1 = portfolio.getEquipment(pump1Id);
    Assert.assertNotNull("pump1 is null", pump1);

    
    // Map 6 raw points with 2 going to each of the loops.
    List<RawPointEntity> rawPoints = new ArrayList<>();
    for (int i=1; i <= numPointsToMap; i++) {
      
      String metricId = metricIdPattern
          .replace("Y", Integer.toString(i));
     
      rawPoints.add(buildMockRawPoint(customerId, metricId));
    }
    customer.addRawPoints(rawPoints);
    boolean storeRawPoints = true;
    customer = customerService.updateCustomer(customer, storeRawPoints);

    
    // Create/map raw points that we will move to the loops.
    String mappingExpression = "/Drivers/NiagaraNetwork/{building}/{point}";
    rawPoints = new ArrayList<>();
    rawPoints.add(buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building_1/Point_1"));
    rawPoints.add(buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building_1/Point_2"));
    rawPoints.add(buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building_1/Point_3"));
    rawPoints.add(buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building_1/Point_4"));
    rawPoints.add(buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building_1/Point_5"));
    rawPoints.add(buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building_1/Point_6"));
    customer.addRawPoints(rawPoints);
    storeRawPoints = true;
    customer = customerService.updateCustomer(customer, storeRawPoints);    
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
        .withBuildingName(buildingName)
        .withMappingExpression(mappingExpression)
        .withMetricIdDelimiter(NodeHierarchyService.DEFAULT_METRIC_ID_DELIMITER)
        .build(); 
    List<MappablePointEntity> createdMappablePoints = nodeHierarchyService.mapRawPoints(mapRawPointsRequest);    
    Assert.assertEquals("createdMappablePoints size is incorrect", rawPoints.size(), createdMappablePoints.size());
    
    List<Integer> childIds = new ArrayList<>();
    childIds.add(createdMappablePoints.get(0).getPersistentIdentity());
    childIds.add(createdMappablePoints.get(1).getPersistentIdentity());
    MoveChildNodesRequest request = MoveChildNodesRequest
        .builder()
        .withCustomerId(customerId)
        .withNewParentId(primaryLoopId)
        .withChildIds(childIds)
        .build();
    List<AbstractNodeEntity> movedNodes = nodeHierarchyService.moveChildNodesToNewParentNode(request);
    Assert.assertEquals("movedNodes size is incorrect", 2, movedNodes.size());

    childIds.clear();
    childIds.add(createdMappablePoints.get(2).getPersistentIdentity());
    childIds.add(createdMappablePoints.get(3).getPersistentIdentity());
    request = MoveChildNodesRequest
        .builder()
        .withCustomerId(customerId)
        .withNewParentId(secondaryLoopId)
        .withChildIds(childIds)
        .build();
    movedNodes = nodeHierarchyService.moveChildNodesToNewParentNode(request);
    Assert.assertEquals("movedNodes size is incorrect", 2, movedNodes.size());

    childIds.clear();
    childIds.add(createdMappablePoints.get(4).getPersistentIdentity());
    childIds.add(createdMappablePoints.get(5).getPersistentIdentity());
    request = MoveChildNodesRequest
        .builder()
        .withCustomerId(customerId)
        .withNewParentId(tertiaryLoopId)
        .withChildIds(childIds)
        .build();
    movedNodes = nodeHierarchyService.moveChildNodesToNewParentNode(request);
    Assert.assertEquals("movedNodes size is incorrect", 2, movedNodes.size());
    
    
    // ***********************************************
    // Tag the plant/loop/equipment.
    TagsContainer tagsContainer = dictionaryService.getTagsContainer();
    PlantEnergyExchangeTypeEntity chilledWaterPlantType = tagsContainer.getPlantTypeByName("chilledWaterPlant");
    LoopEnergyExchangeTypeEntity primaryLoopType = tagsContainer.getLoopTypeByName("primaryLoop");
    LoopEnergyExchangeTypeEntity secondaryLoopType = tagsContainer.getLoopTypeByName("secondaryLoop");
    LoopEnergyExchangeTypeEntity tertiaryLoopType = tagsContainer.getLoopTypeByName("tertiaryLoop");
    EquipmentEnergyExchangeTypeEntity pumpEquipmentType = tagsContainer.getEquipmentTypeByName("pump");
    List<String> equipmentMetadataTags = Arrays.asList("rooftop");
    List<EnergyExchangeSystemNodeData> energyExchangeSystemNodeData = new ArrayList<>();

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(chilledWaterPlantId)
        .withDisplayName(chilledWaterPlant.getDisplayName() + "_updated")
        .withTypeId(chilledWaterPlantType.getPersistentIdentity())
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_PLANT)
        .build());

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(primaryLoopId)
        .withDisplayName(primaryLoop.getDisplayName() + "_updated")
        .withTypeId(primaryLoopType.getPersistentIdentity())
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_LOOP)
        .build());
    
    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(secondaryLoopId)
        .withDisplayName(secondaryLoop.getDisplayName() + "_updated")
        .withTypeId(secondaryLoopType.getPersistentIdentity())
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_LOOP)
        .build());

    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(tertiaryLoopId)
        .withDisplayName(tertiaryLoop.getDisplayName() + "_updated")
        .withTypeId(tertiaryLoopType.getPersistentIdentity())
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_LOOP)
        .withSystemTypeId(EnergyExchangeSystemType.CHILLED_WATER.getId())
        .withParentIds(Arrays.asList(secondaryLoopId))
        .build());
    
    energyExchangeSystemNodeData.add(EnergyExchangeSystemNodeData
        .builder()
        .withId(pump1Id)
        .withDisplayName(pump1.getDisplayName() + "_updated")
        .withTypeId(pumpEquipmentType.getPersistentIdentity())
        .withNodeType(EnergyExchangeSystemNodeData.NODE_TYPE_EQUIPMENT)
        .withSystemTypeId(EnergyExchangeSystemType.CHILLED_WATER.getId())
        .withParentIds(Arrays.asList(primaryLoopId))
        .withMetadataTags(equipmentMetadataTags)
        .build());
    
    UpdateEnergyExchangeSystemNodesRequest updateEnergyExchangeSystemNodesRequest = UpdateEnergyExchangeSystemNodesRequest
        .builder()
        .withBuildingId(building1Id)
        .withCustomerId(customerId)
        .withSubmittedBy("tmyers@resolutebi.com")
        .withData(energyExchangeSystemNodeData)
        .build(); 
    nodeHierarchyService.updateEnergyExchangeSystemNodes(updateEnergyExchangeSystemNodesRequest);
    

    // **********************************************************************************************
    // Verify plant/loop/equipment type tag and energy exchange system parent/child relationships
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building1 = portfolio.getChildBuilding(building1Id);
    chilledWaterPlant = portfolio.getPlant(chilledWaterPlantId);
    primaryLoop = portfolio.getLoop(primaryLoopId);
    secondaryLoop = portfolio.getLoop(secondaryLoopId);
    tertiaryLoop = portfolio.getLoop(tertiaryLoopId);
    pump1 = portfolio.getEquipment(pump1Id);
    
    childIds.clear();
    childIds.add(primaryLoopId);
    childIds.add(secondaryLoopId);
    DeleteChildNodesRequest deleteChildNodesRequest = DeleteChildNodesRequest
        .builder()
        .withCustomerId(customerId)
        .withChildIds(childIds)
        .withSubmittedBy("tmyers@resolutebi.com")
        .build();
    
    
    
    
    // STEP 2: ACT
    deletedNodes = nodeHierarchyService.deleteChildNodes(deleteChildNodesRequest);
    
    
    
    // STEP 3: ASSERT
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building1 = portfolio.getChildBuilding(building1Id);
    chilledWaterPlant = portfolio.getPlant(chilledWaterPlantId);
    pump1 = portfolio.getEquipment(pump1Id);
    Set<AbstractPointEntity> childPoints = chilledWaterPlant.getChildPoints();
    Assert.assertEquals("childPoints size is incorrect", createdMappablePoints.size(), childPoints.size());
    Set<LoopEntity> childLoops = chilledWaterPlant.getChildLoops();
    Assert.assertEquals("childLoops size is incorrect", 0, childLoops.size());
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void moveChildNodesToNewParentNode_loop() throws Exception {
    
    // STEP 1: ARRANGE
    
    // ***********************************************
    // Create Building_1.
    Map<String, Object> additionalProperties = new HashMap<>();
    String buildingName = "Building_1";
    CreateNodeRequest createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.BUILDING)
        .withParentId(null)
        .withName(buildingName)
        .withDisplayName(buildingName)
        .withAdditionalProperties(additionalProperties)
        .build();
    BuildingEntity building1 = (BuildingEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer building1Id = building1.getPersistentIdentity();
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building1 = portfolio.getChildBuilding(building1Id);
    Assert.assertNotNull("building1 is null", building1);


    // ***********************************************
    // Create Chilled_Water_Plant.
    createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.PLANT)
        .withParentId(building1Id)
        .withName("Chilled_Water_Plant")
        .withDisplayName("Chilled_Water_Plant")
        .withAdditionalProperties(additionalProperties)
        .build();
    PlantEntity chilledWaterPlant = (PlantEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer chilledWaterPlantId = chilledWaterPlant.getPersistentIdentity();
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    chilledWaterPlant = portfolio.getPlant(chilledWaterPlantId);
    Assert.assertNotNull("chilledWaterPlant is null", chilledWaterPlant);
    
    
    // ***********************************************
    // Create Primary_Loop.
    additionalProperties.put("energyExchangeSystemTypeId", CreateNodeRequest.CHILLED_WATER_SYSTEM_TYPE_ID);
    createNodeRequest = CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.LOOP)
        .withParentId(chilledWaterPlantId)
        .withName("Primary_Loop")
        .withDisplayName("Primary_Loop")
        .withAdditionalProperties(additionalProperties)
        .build();
    LoopEntity primaryLoop = (LoopEntity)nodeHierarchyService.createNode(createNodeRequest);
    Integer primaryLoopId = primaryLoop.getPersistentIdentity();
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    primaryLoop = portfolio.getLoop(primaryLoopId);
    Assert.assertNotNull("primaryLoop is null", primaryLoop);
    
    List<Integer> childIds = Arrays.asList(primaryLoopId);
    MoveChildNodesRequest moveChildNodesRequest = MoveChildNodesRequest
        .builder()
        .withCustomerId(customerId)
        .withNewParentId(building1Id)
        .withChildIds(childIds)
        .build();
    
    
    
    // STEP 2: ACT
    nodeHierarchyService.moveChildNodesToNewParentNode(moveChildNodesRequest);
  }  
  
  @Ignore
  @Test
  public void moveChildNodes_rp13012() throws Exception {
    
    // STEP 1: ARRANGE
    customerId = DOMINOS_CUSTOMER_ID;
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    EquipmentEntity childEquipment = portfolio.getEquipment(3022021);
    Assert.assertNotNull("childEquipment is null", childEquipment);
    EquipmentEntity newParentEquipment = portfolio.getEquipment(183192);
    Assert.assertNotNull("newParentEquipment is null", newParentEquipment);

    
    // STEP 2: ACT
    movedNodes = nodeHierarchyService.moveChildNodesToNewParentNode(MoveChildNodesRequest
        .builder()
        .withCustomerId(customerId)
        .withNewParentId(183192)
        .withChildIds(Arrays.asList(3022021))
        .withPerformAutomaticConfiguration(Boolean.FALSE)
        .withPerformAutomaticRemediation(Boolean.TRUE)
        .withPerformAutomaticEvaluateReports(Boolean.TRUE)
        .build());
    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("movedNodes is null", movedNodes);
    Assert.assertEquals("movedNodes size is incorrect", 1, movedNodes.size());
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
    nodeHierarchyService.updateReportInstances(updateReportInstancesRequest);

    
    
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
    nodeHierarchyService.updateReportInstances(updateReportInstancesRequest);    
    
    
    
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
    nodeHierarchyService.updateReportInstances(updateReportInstancesRequest);
    
    
    
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
  
  @Test
  public void serializePortfolio_REDICO_kyro() throws Exception {
   
    // STEP 1: ARRANGE
    portfolio = nodeHierarchyService.loadPortfolio(REDICO_CUSTOMER_ID);
    
    
    // STEP 2: ACT
    // SERIALIZE
    byte[] bytes = KryoSerialize.getInstance().encode(portfolio);
    
    // DE-SERIALIZE
    PortfolioEntity dsPortfolio = KryoSerialize.getInstance().decode(bytes, PortfolioEntity.class);
    
    
    // STEP 3: ASSERT
    int portfolioMappedPointCount = portfolio.getTotalMappedPointCount();
    int dsPortfolioMappedPointCount = dsPortfolio.getTotalMappedPointCount();
    Assert.assertEquals("deserialized portfolio with Kryo mapped point count is incorrect", portfolioMappedPointCount, dsPortfolioMappedPointCount);
  }
}
//@formatter:on