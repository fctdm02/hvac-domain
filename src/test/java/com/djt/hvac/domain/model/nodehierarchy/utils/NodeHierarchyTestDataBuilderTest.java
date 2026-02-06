//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.utils;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.djt.hvac.domain.model.AbstractResoluteDomainModelTest;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.utils.NodeHierarchyTestDataBuilderOptions;

public class NodeHierarchyTestDataBuilderTest extends AbstractResoluteDomainModelTest {
  
  @Test
  public void createNodeHierarchy() throws Exception {
    
    // STEP 1: ARRANGE
    int numBuildings = 3;
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
    
    
    
    // STEP 2: ACT
    Integer customerId = nodeHierarchyTestDataBuilder.createNodeHierarchy(nodeHierarchyTestDataBuilderOptions);


    
    // STEP 3: ASSERT
    PortfolioEntity p = modelServiceProvider.getNodeHierarchyService().loadPortfolio(customerId);    
    Assert.assertNotNull("portfolio is null", p);
    
    Assert.assertEquals("total buildings is incorrect", 
        Integer.toString(numBuildings), 
        Integer.toString(p.getAllBuildings().size()));
    
    Assert.assertEquals("total floors is incorrect", 
        Integer.toString(numBuildings*numFloors), 
        Integer.toString(p.getAllFloors().size()));  
  }
}
//@formatter:on