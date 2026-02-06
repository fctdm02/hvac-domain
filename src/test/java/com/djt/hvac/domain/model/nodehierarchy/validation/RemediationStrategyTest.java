//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.validation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.djt.hvac.domain.model.AbstractResoluteDomainModelTest;
import com.djt.hvac.domain.model.dictionary.template.function.AbstractAdFunctionTemplateEntity;
import com.djt.hvac.domain.model.distributor.service.DistributorHierarchyStateEvaluator;
import com.djt.hvac.domain.model.function.AbstractAdFunctionInstanceEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EnergyExchangeEntity;
import com.djt.hvac.domain.model.nodehierarchy.utils.NodeHierarchyTestDataBuilderOptions;

public class RemediationStrategyTest extends AbstractResoluteDomainModelTest {

  protected Integer customerId;
  protected List<Integer> customerIds;
  protected DistributorHierarchyStateEvaluator distributorHierarchyStateEvaluator;
  protected boolean performStripePaymentProcessing = false;
  
  @Before
  public void before() throws Exception {
    
    super.before();
    try {
      TimeZone.setDefault(TimeZone.getTimeZone("GMT"));    
    } catch (Exception e) {
    }
    distributorHierarchyStateEvaluator = modelServiceProvider.getDistributorHierarchyStateEvaluator();
    
    int numBuildings = 1;
    int numFloors = 1;
    int numEquipmentPerEquipmentType = 1;
    int numPointsPerEquipmentType = 1;
    Set<String> equipmentTypeNames = new HashSet<>();
    Set<String> pointTemplateNames = new HashSet<>();
    boolean performPointMapping = true;
    boolean performEquipmentTagging = true;
    boolean performPointTagging = true;
    boolean createCustomPoints = false;
    boolean createWeatherPoints = false;
    boolean createBuildingTemporalData = false;
    boolean createAdFunctionInstanceCandidates = true;
    boolean createAdFunctionInstances = true;
    boolean evaluateReports = true;
    boolean enableReports = true;
    
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
    
    customerId = nodeHierarchyTestDataBuilder.createNodeHierarchy(nodeHierarchyTestDataBuilderOptions);
    
    customerIds = Arrays.asList(customerId);
  }
  
  @Test
  public void performPortfolioMaintenance_MigrateAdFunctionInstanceVersionStrategyImpl() throws Exception {
    
    // STEP 1: ARRANGE
    PortfolioEntity portfolioBefore = nodeHierarchyService.loadPortfolio(customerId);
    List<AbstractAdFunctionInstanceEntity> allAdFunctionInstancesBefore = portfolioBefore.getAllAdFunctionInstances();
    Assert.assertFalse("allAdFunctionInstancesBefore is empty", allAdFunctionInstancesBefore.isEmpty());
    
    AbstractAdFunctionInstanceEntity adFunctionInstanceBefore = allAdFunctionInstancesBefore.get(0);
    
    AbstractAdFunctionTemplateEntity adFunctionTemplateBefore = adFunctionInstanceBefore.getAdFunctionTemplate();
    Integer adFunctionTemplateIdBefore = adFunctionTemplateBefore.getPersistentIdentity();
    
    Integer adFunctionInstanceIdBefore = adFunctionInstanceBefore.getPersistentIdentity();
    EnergyExchangeEntity energyExchangeEntityBefore = adFunctionInstanceBefore.getEquipment(); 
    Integer energyExchangeEntityIdBefore = energyExchangeEntityBefore.getPersistentIdentity();
    
    Assert.assertEquals("template/instance template version mismatch", adFunctionTemplateBefore.getVersion(), adFunctionInstanceBefore.getTemplateVersion());
    
    // Update the version of an AD function instance's associated AD function template.
    Integer updatedVersion = adFunctionTemplateBefore.getVersion() + 1;
    dictionaryService.updateAdFunctionTemplateVersion(adFunctionTemplateIdBefore, updatedVersion);
    
    AbstractAdFunctionTemplateEntity adFunctionTemplateUpdated = dictionaryService
        .getAdFunctionTemplatesContainer()
        .getAdFunctionTemplate(adFunctionTemplateIdBefore);
    Assert.assertEquals("template version not updated", updatedVersion, adFunctionTemplateUpdated.getVersion());

    
    // STEP 2: ACT
    nodeHierarchyService.performPortfolioMaintenance(
        customerIds, 
        distributorHierarchyStateEvaluator, 
        performStripePaymentProcessing);
    
    
    // STEP 3: ASSERT
    PortfolioEntity portfolioAfter = nodeHierarchyService.loadPortfolio(customerId);
    EnergyExchangeEntity energyExchangeEntityAfter = portfolioAfter.getEnergyExchangeSystemNode(energyExchangeEntityIdBefore);
    AbstractAdFunctionInstanceEntity adFunctionInstanceAfter = energyExchangeEntityAfter.getAdFunctionInstanceByTemplateIdNullIfNotExists(adFunctionTemplateIdBefore);
    Integer adFunctionInstanceIdAfter = adFunctionInstanceAfter.getPersistentIdentity();
    
    Assert.assertNotEquals("instance id was not updated", adFunctionInstanceIdBefore, adFunctionInstanceIdAfter);
    Assert.assertEquals("instance template version was not incremented", updatedVersion, adFunctionInstanceAfter.getTemplateVersion());
  }
}