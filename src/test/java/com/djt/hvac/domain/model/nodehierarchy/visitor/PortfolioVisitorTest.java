package com.djt.hvac.domain.model.nodehierarchy.visitor;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.djt.hvac.domain.model.AbstractResoluteDomainModelTest;
import com.djt.hvac.domain.model.dictionary.enums.FunctionType;
import com.djt.hvac.domain.model.function.dto.AdFunctionInstanceDto;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.visitor.PortfolioVisitor;
import com.djt.hvac.domain.model.report.ReportInstanceEntity;

public class PortfolioVisitorTest extends AbstractResoluteDomainModelTest {

  @Test
  public void findRuleCandidatesCustomerPortfolios() throws Exception {
    
    //customerId = MCLAREN_CUSTOMER_ID;
    //findAdRuleFunctionInstanceCandidates();
    
    customerId = REDICO_CUSTOMER_ID;
    findAdRuleFunctionInstanceCandidates();
    
    //customerId = DOMINOS_CUSTOMER_ID;
    //findAdRuleFunctionInstanceCandidates();
  }
  
  private void findAdRuleFunctionInstanceCandidates() throws Exception {
    
    // STEP 1: ARRANGE
    FunctionType functionType = FunctionType.RULE;
    PortfolioEntity portfolio = nodeHierarchyService.loadPortfolio(customerId);
    Assert.assertNotNull("portfolio is null", portfolio);
    portfolio.removeAllAdFunctionInstanceCandidates();      
    
    
    
    // STEP 2: ACT
    List<AdFunctionInstanceDto> candidateDtos = PortfolioVisitor.findAdFunctionInstanceCandidates(portfolio, functionType);

    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("candidateDtos is null", candidateDtos);
  }  

  @Test
  public void findComputedPointCandidatesCustomerPortfolios() throws Exception {
    
    //customerId = MCLAREN_CUSTOMER_ID;
    //findAdComputedPointFunctionInstanceCandidates();
    
    customerId = REDICO_CUSTOMER_ID;
    findAdComputedPointFunctionInstanceCandidates();
    
    //customerId = DOMINOS_CUSTOMER_ID;
    //findAdComputedPointFunctionInstanceCandidates();
  }

  private void findAdComputedPointFunctionInstanceCandidates() throws Exception {
    
    // STEP 1: ARRANGE
    FunctionType functionType = FunctionType.COMPUTED_POINT;
    PortfolioEntity portfolio = nodeHierarchyService.loadPortfolio(customerId);
    Assert.assertNotNull("portfolio is null", portfolio);
    portfolio.removeAllAdFunctionInstanceCandidates();      
    
    
    
    // STEP 2: ACT
    List<AdFunctionInstanceDto> candidateDtos = PortfolioVisitor.findAdFunctionInstanceCandidates(portfolio, functionType);

    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("candidateDtos is null", candidateDtos);
  } 

  @Test
  public void evaluateReportsCustomerPortfolios() throws Exception {
    
    //customerId = MCLAREN_CUSTOMER_ID;
    //evaluateReports();
    
    customerId = REDICO_CUSTOMER_ID;
    evaluateReports();
    
    //customerId = DOMINOS_CUSTOMER_ID;
    //evaluateReports();
  }
  
  private void evaluateReports() throws Exception {
    
    // STEP 1: ARRANGE
    PortfolioEntity portfolio = nodeHierarchyService.loadPortfolio(customerId);
    Assert.assertNotNull("portfolio is null", portfolio);
    
    
    
    // STEP 2: ACT
    List<ReportInstanceEntity> changedReportInstances = PortfolioVisitor.evaluateReports(portfolio);

    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("changedReportInstances is null", changedReportInstances);
    changedReportInstances = PortfolioVisitor.evaluateReports(portfolio);
    
    portfolio.removeAllAdFunctions();
    changedReportInstances = PortfolioVisitor.evaluateReports(portfolio);
    
    /*
    Iterator<ReportInstanceEntity> reportInstanceIterator = changedReportInstances.iterator();
    while (reportInstanceIterator.hasNext()) {
      
      ReportInstanceEntity reportInstance = reportInstanceIterator.next();
      
      System.err.println(reportInstance 
          + ": GREEN: "
          + reportInstance.getNumEquipmentInGreenStatus()
          + ", RED: " + reportInstance.getNumEquipmentInRedStatus()
          + ", TOTAL: " + reportInstance.getNumEquipmentTotal()
          + ", GREEN HASH: " + reportInstance.getReportInstanceEquipment().hashCode()
          + ", RED HASH: " + reportInstance.getAllEquipmentErrorMessages().hashCode());
    }
    */
    
    if (customerId == MCLAREN_CUSTOMER_ID || customerId == REDICO_CUSTOMER_ID || customerId == DOMINOS_CUSTOMER_ID) {
      Assert.assertFalse("changedReportInstances is empty", changedReportInstances.isEmpty());  
    }
    
    /*
    Iterator<BuildingEntity> buildingIterator = portfolio.getChildBuildings().iterator();
    while (buildingIterator.hasNext()) {
      
     BuildingEntity building = buildingIterator.next();
      
     Iterator<ReportInstanceEntity> reportInstanceIterator = building.getReportInstances().iterator();
     while (reportInstanceIterator.hasNext()) {
       
      ReportInstanceEntity reportInstance = reportInstanceIterator.next();
       
      System.err.println(reportInstance 
          + ": GREEN: "
          + reportInstance.getNumEquipmentInGreenStatus()
          + ", RED: " + reportInstance.getNumEquipmentInRedStatus()
          + ", TOTAL: " + reportInstance.getNumEquipmentTotal()
          + ", GREEN HASH: " + reportInstance.getReportInstanceEquipment().hashCode()
          + ", RED HASH: " + reportInstance.getAllEquipmentErrorMessages().hashCode());
     }
    }
    */
  }
}
