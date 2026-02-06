package com.djt.hvac.domain.model.nodehierarchy.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.djt.hvac.domain.model.AbstractResoluteDomainModelTest;
import com.djt.hvac.domain.model.common.dsl.pointmap.Node;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.customer.OnlineCustomerEntity;
import com.djt.hvac.domain.model.customer.enums.CustomerType;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.enums.UnitSystem;
import com.djt.hvac.domain.model.distributor.OnlineDistributorEntity;
import com.djt.hvac.domain.model.distributor.enums.DistributorType;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.service.NodeHierarchyService;
import com.djt.hvac.domain.model.nodehierarchy.service.command.MapRawPointsRequest;
import com.djt.hvac.domain.model.nodehierarchy.utils.RawPointMappingNodeNameFilter;
import com.djt.hvac.domain.model.rawpoint.RawPointEntity;
import com.google.common.collect.Lists;

public class RawPointMappingNodeNameFilterTest extends AbstractResoluteDomainModelTest {
  
  private Integer rootDistributorId = RESOLUTE_DISTRIBUTOR_ID;
  private String distributorName;
  private OnlineDistributorEntity distributor;
  private String customerName;
  private OnlineCustomerEntity customer;
  private boolean loadRawPoints = true;
  private AbstractCustomerEntity redicoCustomer;
  private Collection<RawPointEntity> customerRawPoints;
  private String portfolioName;
  private String portfolioDisplayName;
  private PortfolioEntity portfolio;
  
  @Before
  public void before() throws Exception {
    
    super.before();
    
    rootDistributorId = RESOLUTE_DISTRIBUTOR_ID;
    distributorName = "Online Distributor Name";
    distributor = (OnlineDistributorEntity)distributorRepository.createDistributor(
        rootDistributorId,
        DistributorType.ONLINE,
        distributorName,
        UnitSystem.IP.toString(),
        false);
        
    distributorId = distributor.getPersistentIdentity();
    
    customerName = "Online Customer Name";
    customer = (OnlineCustomerEntity)customerRepository.createCustomer(
        distributor, 
        CustomerType.ONLINE,
        customerName,
        UnitSystem.IP.toString());
    customerId = customer.getPersistentIdentity();
    
    redicoCustomer = customerService.loadCustomer(
        REDICO_CUSTOMER_ID, 
        false, // loadDistributorPaymentMethods
        false, // loadDistributorUsers
        loadRawPoints);
    customerRawPoints = redicoCustomer.getRawPoints();
    customer.addRawPoints(customerRawPoints);
    
    portfolioName = "Online_Customer_Portfolio_Node";
    portfolioDisplayName = "Online Customer Portfolio Node";
    portfolio = nodeHierarchyRepository.createPortfolio(
        customer, 
        portfolioName, 
        portfolioDisplayName);
  }
  
  @AfterClass
  public static void afterClass() throws Exception {
    
    DictionaryContext.setPaymentPlansContainer(null);
    nodeHierarchyService.ensureDictionaryDataIsLoaded();
  }   
  
  @Test
  public void getEligibleRawPoints() throws Exception {
    
    // STEP 1: ARRANGE
    List<RawPointEntity> rawPointsToMap = Arrays.asList(
        
        // Elegible
        buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building_1/Sub_Building_1/Plant_1/Floor_1/points/HVAC/Equipment_1/Point_1"),
        buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building_1/Sub_Building_1/Plant_1/Floor_1/points/HVAC/Equipment_1/Point_2"),
        buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building_1/Sub_Building_1/Plant_1/Floor_1/points/HVAC/Equipment_1/Point_3"),
        buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building_1/Sub_Building_1/Plant_1/Floor_1/points/HVAC/Equipment_1/Point_4"),
        buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building_1/Sub_Building_1/Plant_1/Floor_1/points/HVAC/Equipment_1/Point_5"),
        buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building_1/Sub_Building_1/Plant_1/Floor_1/points/HVAC/Equipment_1/Point_6"),
        buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building_1/Sub_Building_1/Plant_1/Floor_1/points/HVAC/Equipment_1/Point_7"),
        buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building_1/Sub_Building_1/Plant_1/Floor_1/points/HVAC/Equipment_1/Point_8"),
        buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building_1/Sub_Building_1/Plant_1/Floor_1/points/HVAC/Equipment_1/Point_9"),
        buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building_1/Sub_Building_1/Plant_1/Floor_1/points/HVAC/Equipment_1/Point_10"),

        // Ineligible (node name exclusions)
        buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building_1/Sub_Building_1/Plant_1/Floor_1/points/HVAC/Equipment_1/Point_11"),
        buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building_1/Sub_Building_1/Plant_1/Floor_1/points/HVAC/Equipment_1/Point_12"),
        buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building_1/Sub_Building_1/Plant_1/Floor_1/points/HVAC/Equipment_1/Point_13"),
        buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building_1/Sub_Building_1/Plant_1/Floor_1/points/HVAC/Equipment_1/Point_14"),
        buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building_1/Sub_Building_1/Plant_1/Floor_1/points/HVAC/Equipment_1/Point_15"),
        
        // Ineligible (different node structure)
        buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building_1/points/HVAC/Equipment_1/Point_16"),
        buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building_1/points/HVAC/Equipment_1/Point_17"),
        buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building_1/points/HVAC/Equipment_1/Point_18"),
        buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building_1/points/HVAC/Equipment_1/Point_19"),
        buildMockRawPoint(customerId, "/Drivers/NiagaraNetwork/Building_1/points/HVAC/Equipment_1/Point_20"));
    
    String mappingExpression = "/Drivers/NiagaraNetwork/{building}/{subBuilding}/{plant}/{floor}/points/HVAC/{equipment}/{point}";
    String metricIdDelimiter = NodeHierarchyService.DEFAULT_METRIC_ID_DELIMITER;      
    
    MapRawPointsRequest request = MapRawPointsRequest
        .builder()
        .withCustomerId(Integer.valueOf(1))
        .withSubmittedBy("tmyers@resolutebi.com")
        .withRawPoints(Lists.newArrayList())
        .withMappingExpression(mappingExpression)
        .withMetricIdDelimiter(metricIdDelimiter)
        .withBuildingName("Building_1")
        .withSubBuildingNames(Arrays.asList("Sub_Building_1"))
        .withPlantNames(Arrays.asList("Plant_1"))
        .withFloorNames(Arrays.asList("Floor_1"))
        .withEquipmentNames(Arrays.asList("Equipment_1"))
        .withPointNames(Arrays.asList("Point_1", "Point_2", "Point_3", "Point_4", "Point_5", "Point_6", "Point_7", "Point_8", "Point_9", "Point_10"))
        .withPerformExclusionOnNames(Boolean.FALSE)
        .build();

    
    
    // STEP 2: ACT
    Map<RawPointEntity, List<Node>> eligibleRawPointsMap = RawPointMappingNodeNameFilter.getEligibleRawPoints(
        portfolio, 
        rawPointsToMap, 
        request);     

    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("eligibleRawPointsMap is null", eligibleRawPointsMap);
    Assert.assertEquals("eligibleRawPointsMap size is incorrect", "10", Integer.toString(eligibleRawPointsMap.size()));  
  }
}
