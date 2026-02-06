//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.djt.hvac.domain.model.AbstractResoluteDomainModelTest;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.customer.enums.CustomerType;
import com.djt.hvac.domain.model.dictionary.UnitEntity;
import com.djt.hvac.domain.model.dictionary.enums.FunctionType;
import com.djt.hvac.domain.model.dictionary.enums.NodeType;
import com.djt.hvac.domain.model.dictionary.enums.UnitSystem;
import com.djt.hvac.domain.model.distributor.AbstractDistributorEntity;
import com.djt.hvac.domain.model.distributor.enums.DistributorType;
import com.djt.hvac.domain.model.function.AbstractAdFunctionInstanceEntity;
import com.djt.hvac.domain.model.function.AdFunctionInstanceEligiblePoint;
import com.djt.hvac.domain.model.function.AdFunctionInstanceInputConstantEntity;
import com.djt.hvac.domain.model.function.AdFunctionInstanceInputPointEntity;
import com.djt.hvac.domain.model.nodehierarchy.AbstractNodeEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.service.command.CreateNodeRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UpdateBuildingNodesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.UpdateBuildingNodeRequest;
import com.djt.hvac.domain.model.nodehierarchy.utils.NodeHierarchyTestDataBuilder;

public class CanadianCustomerFunctionalTest extends AbstractResoluteDomainModelTest {

  private static String distributorName = "Canadian Distributor Name";
  private static String customerName = "Canadian Customer Name";
  private static String portfolioName = "Canadian Portfolio Node";
  private static String buildingName = "Canadian Building Name";
  
  private static boolean loadDistributorPaymentMethods = false;
  private static boolean loadDistributorUsers = false;
  
  @BeforeClass
  public static void beforeClass() throws Exception {
    AbstractResoluteDomainModelTest.beforeClass();
  }
  
  @Before
  public void before() throws Exception {
    super.before();
  }

  @Test
  public void createOnlineDistributor() throws Exception {  
    
    // STEP 1: ARRANGE

    
    // STEP 2: ACT
    AbstractDistributorEntity distributor = distributorService.createDistributor(
        RESOLUTE_DISTRIBUTOR_ID, 
        DistributorType.ONLINE,
        distributorName,
        UnitSystem.SI.toString(),
        false);
    Integer distributorId = distributor.getPersistentIdentity();
    

    // STEP 3: ASSERT
    distributor = distributorService.loadDistributor(distributorId);
    Assert.assertEquals("unit system is incorrect", 
        UnitSystem.SI.toString(), 
        distributor.getUnitSystem().toString());
  }
  
  @Test
  public void updateOnlineDistributor() throws Exception {  
    
    // STEP 1: ARRANGE
    AbstractDistributorEntity distributor = distributorService.createDistributor(
        RESOLUTE_DISTRIBUTOR_ID, 
        DistributorType.ONLINE,
        distributorName,
        UnitSystem.IP.toString(),
        false);
    Integer distributorId = distributor.getPersistentIdentity();
    distributor = distributorService.loadDistributor(distributorId);
    distributor.setUnitSystem(UnitSystem.SI);
    
    
    // STEP 2: ACT
    distributorService.updateDistributor(distributor);
    

    // STEP 3: ASSERT
    distributor = distributorService.loadDistributor(distributorId);
    Assert.assertEquals("unit system is incorrect", 
        UnitSystem.SI.toString(), 
        distributor.getUnitSystem().toString());
  }  
  
  @Test
  public void createOutOfBandDistributor() throws Exception {  
    
    // STEP 1: ARRANGE

    
    // STEP 2: ACT
    AbstractDistributorEntity distributor = distributorService.createDistributor(
        RESOLUTE_DISTRIBUTOR_ID, 
        DistributorType.OUT_OF_BAND,
        distributorName,
        UnitSystem.SI.toString(),
        false);
    Integer distributorId = distributor.getPersistentIdentity();
    

    // STEP 3: ASSERT
    distributor = distributorService.loadDistributor(distributorId);
    Assert.assertEquals("unit system is incorrect", 
        UnitSystem.SI.toString(), 
        distributor.getUnitSystem().toString());
  }
  
  @Test
  public void updateOutOfBandDistributor() throws Exception {  
    
    // STEP 1: ARRANGE
    AbstractDistributorEntity distributor = distributorService.createDistributor(
        RESOLUTE_DISTRIBUTOR_ID, 
        DistributorType.OUT_OF_BAND,
        distributorName,
        UnitSystem.IP.toString(),
        false);
    Integer distributorId = distributor.getPersistentIdentity();
    distributor = distributorService.loadDistributor(distributorId);
    distributor.setUnitSystem(UnitSystem.SI);
    
    
    // STEP 2: ACT
    distributorService.updateDistributor(distributor);
    

    // STEP 3: ASSERT
    distributor = distributorService.loadDistributor(distributorId);
    Assert.assertEquals("unit system is incorrect", 
        UnitSystem.SI.toString(), 
        distributor.getUnitSystem().toString());
  }  
  
  @Test
  public void createOnlineCustomer() throws Exception {  
    
    // STEP 1: ARRANGE
    AbstractDistributorEntity distributor = distributorService.createDistributor(
        RESOLUTE_DISTRIBUTOR_ID, 
        DistributorType.ONLINE,
        distributorName,
        UnitSystem.SI.toString(),
        false);
    Integer distributorId = distributor.getPersistentIdentity();
    distributor = distributorService.loadDistributor(distributorId);
    Assert.assertEquals("unit system is incorrect", 
        UnitSystem.SI.toString(), 
        distributor.getUnitSystem().toString());
    
    
    // STEP 2: ACT
    AbstractCustomerEntity customer = customerService.createCustomer(
        distributor, 
        CustomerType.ONLINE, 
        customerName,
        UnitSystem.SI.toString());
    Integer customerId = customer.getPersistentIdentity();

    
    // STEP 3: ASSERT
    customer = customerService.loadCustomer(customerId, loadDistributorPaymentMethods, loadDistributorUsers);
    Assert.assertEquals("unit system is incorrect", 
        UnitSystem.SI.toString(), 
        customer.getUnitSystem().toString());
  }
  
  @Test
  public void updateOnlineCustomer() throws Exception {  
    
    // STEP 1: ARRANGE
    AbstractDistributorEntity distributor = distributorService.createDistributor(
        RESOLUTE_DISTRIBUTOR_ID, 
        DistributorType.ONLINE,
        distributorName,
        UnitSystem.IP.toString(),
        false);

    AbstractCustomerEntity customer = customerService.createCustomer(
        distributor, 
        CustomerType.ONLINE, 
        customerName,
        UnitSystem.IP.toString());
    
    Integer customerId = customer.getPersistentIdentity();
    customer = customerService.loadCustomer(customerId, loadDistributorPaymentMethods, loadDistributorUsers);
    Assert.assertEquals("unit system is incorrect", 
        UnitSystem.IP.toString(), 
        customer.getUnitSystem().toString());
    customer.setUnitSystem(UnitSystem.SI);
    
    
    // STEP 2: ACT
    customerService.updateCustomer(customer);
    

    // STEP 3: ASSERT
    customer = customerService.loadCustomer(customerId, loadDistributorPaymentMethods, loadDistributorUsers);
    Assert.assertEquals("unit system is incorrect", 
        UnitSystem.SI.toString(), 
        customer.getUnitSystem().toString());
  }  
  
  @Test
  public void createOutOfBandCustomer() throws Exception {  
    
    // STEP 1: ARRANGE
    AbstractDistributorEntity distributor = distributorService.createDistributor(
        RESOLUTE_DISTRIBUTOR_ID, 
        DistributorType.OUT_OF_BAND,
        distributorName,
        UnitSystem.SI.toString(),
        false);
    Integer distributorId = distributor.getPersistentIdentity();
    distributor = distributorService.loadDistributor(distributorId);
    Assert.assertEquals("unit system is incorrect", 
        UnitSystem.SI.toString(), 
        distributor.getUnitSystem().toString());
    
    
    // STEP 2: ACT
    AbstractCustomerEntity customer = customerService.createCustomer(
        distributor, 
        CustomerType.OUT_OF_BAND, 
        customerName,
        UnitSystem.SI.toString());
    Integer customerId = customer.getPersistentIdentity();

    
    // STEP 3: ASSERT
    customer = customerService.loadCustomer(customerId, loadDistributorPaymentMethods, loadDistributorUsers);
    Assert.assertEquals("unit system is incorrect", 
        UnitSystem.SI.toString(), 
        customer.getUnitSystem().toString());
  }
  
  @Test
  public void updateOutOfBandCustomer() throws Exception {  
    
    // STEP 1: ARRANGE
    AbstractDistributorEntity distributor = distributorService.createDistributor(
        RESOLUTE_DISTRIBUTOR_ID, 
        DistributorType.OUT_OF_BAND,
        distributorName,
        UnitSystem.IP.toString(),
        false);

    AbstractCustomerEntity customer = customerService.createCustomer(
        distributor, 
        CustomerType.OUT_OF_BAND, 
        customerName,
        UnitSystem.IP.toString());
    
    Integer customerId = customer.getPersistentIdentity();
    customer = customerService.loadCustomer(customerId, loadDistributorPaymentMethods, loadDistributorUsers);
    Assert.assertEquals("unit system is incorrect", 
        UnitSystem.IP.toString(), 
        customer.getUnitSystem().toString());
    customer.setUnitSystem(UnitSystem.SI);
    
    
    // STEP 2: ACT
    customerService.updateCustomer(customer);
    

    // STEP 3: ASSERT
    customer = customerService.loadCustomer(customerId, loadDistributorPaymentMethods, loadDistributorUsers);
    Assert.assertEquals("unit system is incorrect", 
        UnitSystem.SI.toString(), 
        customer.getUnitSystem().toString());
  }
  
  @Test
  public void updateBuildingNodes() throws Exception {  
    
    // STEP 1: ARRANGE
    AbstractDistributorEntity distributor = distributorService.createDistributor(
        RESOLUTE_DISTRIBUTOR_ID, 
        DistributorType.OUT_OF_BAND,
        distributorName,
        UnitSystem.IP.toString(),
        false);

    AbstractCustomerEntity customer = customerService.createCustomer(
        distributor, 
        CustomerType.OUT_OF_BAND, 
        customerName,
        UnitSystem.IP.toString());
    Integer customerId = customer.getPersistentIdentity();
    
    nodeHierarchyService.createPortfolio(customer, portfolioName, portfolioName);
    
    AbstractNodeEntity node = nodeHierarchyService.createNode(CreateNodeRequest
        .builder()
        .withCustomerId(customerId)
        .withNodeType(NodeType.BUILDING)
        .withName(buildingName)
        .build());
    Integer buildingId = node.getPersistentIdentity();
    
    
    // STEP 2: ACT
    List<UpdateBuildingNodeRequest> data = new ArrayList<>();
    data.add(UpdateBuildingNodeRequest
        .builder()
        .withId(buildingId)
        .withUnitSystem(UnitSystem.SI.toString())
        .build());
    nodeHierarchyService.updateBuildingNodes(UpdateBuildingNodesRequest
        .builder()
        .withCustomerId(customerId)
        .withData(data)
        .build());
    

    // STEP 3: ASSERT
    PortfolioEntity portfolio = nodeHierarchyService.loadPortfolio(customerId);
    BuildingEntity building = portfolio.getChildBuilding(buildingId);
    Assert.assertEquals("unit system is incorrect", 
        UnitSystem.SI.toString(), 
        building.getUnitSystem().toString());
  }
  
  @Test
  public void updateBuildingNodes_autoMigrationPointUnitsAndInstanceConstants() throws Exception {
    
    // STEP 1: ARRANGE
    customerId = nodeHierarchyTestDataBuilder.createMinimalNodeHierarchyWithRules();
    PortfolioEntity portfolio = nodeHierarchyService.loadPortfolio(customerId);
    List<BuildingEntity> buildings = portfolio.getAllBuildings();
    BuildingEntity building = buildings.get(0);
    Integer buildingId = building.getPersistentIdentity();
    
    List<AbstractAdFunctionInstanceEntity> adFunctionInstances = portfolio.getAllAdFunctionInstances(FunctionType.RULE);
    AbstractAdFunctionInstanceEntity adFunctionInstance = null;
    for (AbstractAdFunctionInstanceEntity fi: adFunctionInstances) {
      if (fi.getAdFunctionTemplate().getPersistentIdentity().equals(Integer.valueOf(43))) {
        
        adFunctionInstance = fi;
        break;
      }
    }
    if (adFunctionInstance == null) {
      throw new RuntimeException("Expected rule instance for AD rule function template with id: 43");
    }

    List<UpdateBuildingNodeRequest> data = new ArrayList<>();
    data.add(UpdateBuildingNodeRequest
        .builder()
        .withId(buildingId)
        .withUnitSystem(UnitSystem.SI.toString())
        .build());
    
    
    
    // STEP 2: ACT
    nodeHierarchyService.updateBuildingNodes(UpdateBuildingNodesRequest
        .builder()
        .withCustomerId(customerId)
        .withData(data)
        .build());
    

    
    // STEP 3: ASSERT
    portfolio = nodeHierarchyService.loadPortfolio(customerId);
    building = portfolio.getChildBuilding(buildingId);
    Assert.assertEquals("unit system is incorrect", 
        UnitSystem.SI.toString(), 
        building.getUnitSystem().toString());
    
    adFunctionInstances = portfolio.getAllAdFunctionInstances(FunctionType.RULE);
    adFunctionInstance = null;
    for (AbstractAdFunctionInstanceEntity fi: adFunctionInstances) {
      if (fi.getAdFunctionTemplate().getPersistentIdentity().equals(Integer.valueOf(43))) {
        
        adFunctionInstance = fi;
        break;
      }
    }
    if (adFunctionInstance == null) {
      throw new RuntimeException("Expected rule instance for AD rule function template with id: 43");
    }
    
    // ONE: Verify that the input point mappable point units were migrated to SI.
    for (AdFunctionInstanceInputPointEntity inputPoint: adFunctionInstance.getInputPoints()) {
      
      AdFunctionInstanceEligiblePoint point = inputPoint.getPoint();
      UnitEntity unit = point.getUnitNullIfNotExists();
      Assert.assertNotNull("unit is null", unit);
      Assert.assertEquals("unit is incorrect", 
          "°C", 
          unit.getName());
    }
    
    // TWO: Verify that the input point constant values were migrated to SI.
    // NOTE: The default value for DEADBAND for 3.2.19.1 is 5 F°, so the expected converted value is: 2.777777778
    for (AdFunctionInstanceInputConstantEntity inputConstant: adFunctionInstance.getInputConstants()) {
      if (inputConstant.getAdFunctionTemplateInputConstant().getName().equals("DEADBAND")) {

        Assert.assertEquals("DEADBAND value is incorrect for SI unit system", 
            "2.777777778", 
            inputConstant.getValue());
        break;
      }
    }
  }
  
  @Test
  public void enableAdFunctionInstances_SI_default_for_distributor() throws Exception {
    
    // STEP 1: ARRANGE
    NodeHierarchyTestDataBuilder.DEFAULT_DISTRIBUTOR_UNIT_SYSTEM = "SI";
    
    
    
    // STEP 2: ACT
    customerId = nodeHierarchyTestDataBuilder.createMinimalNodeHierarchyWithRules();
    

    
    // STEP 3: ASSERT
    // ONE: Verify that the input point mappable point units were migrated to SI.
    PortfolioEntity portfolio = nodeHierarchyService.loadPortfolio(customerId);
    List<AbstractAdFunctionInstanceEntity> adFunctionInstances = portfolio.getAllAdFunctionInstances(FunctionType.RULE);
    AbstractAdFunctionInstanceEntity adFunctionInstance = null;
    for (AbstractAdFunctionInstanceEntity fi: adFunctionInstances) {
      if (fi.getAdFunctionTemplate().getPersistentIdentity().equals(Integer.valueOf(43))) {
        
        adFunctionInstance = fi;
        break;
      }
    }
    if (adFunctionInstance == null) {
      throw new RuntimeException("Expected rule instance for AD rule function template with id: 43");
    }

    for (AdFunctionInstanceInputPointEntity inputPoint: adFunctionInstance.getInputPoints()) {
      
      AdFunctionInstanceEligiblePoint point = inputPoint.getPoint();
      UnitEntity unit = point.getUnitNullIfNotExists();
      Assert.assertNotNull("unit is null", unit);
      Assert.assertEquals("unit is incorrect", 
          "°C", 
          unit.getName());
    }

    // TWO: Verify that the input point constant values were migrated to SI.
    // NOTE: The default value for DEADBAND for 3.2.19.1 is 5 F°, so the expected converted value is: 2.777777778
    for (AdFunctionInstanceInputConstantEntity inputConstant: adFunctionInstance.getInputConstants()) {
      if (inputConstant.getAdFunctionTemplateInputConstant().getName().equals("DEADBAND")) {

        Assert.assertEquals("DEADBAND value is incorrect for SI unit system", 
            "2.777777778", 
            inputConstant.getValue());
        break;
      }
    }
  }
}
//@formatter:on