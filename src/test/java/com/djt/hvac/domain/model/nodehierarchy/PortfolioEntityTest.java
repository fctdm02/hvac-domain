package com.djt.hvac.domain.model.nodehierarchy;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.djt.hvac.domain.model.AbstractResoluteDomainModelTest;
import com.djt.hvac.domain.model.cache.client.MockCacheClient;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.function.dto.AdFunctionErrorMessagesDto;
import com.djt.hvac.domain.model.function.dto.AdFunctionInstanceDto;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.dto.AsyncComputedPointNodeDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.CustomAsyncComputedPointNodeDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.EnergyExchangeSystemEdgeDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.MappablePointNodeDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.NodeTagDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.NonPointNodeDto;
import com.djt.hvac.domain.model.nodehierarchy.dto.ScheduledAsyncComputedPointNodeDto;
import com.djt.hvac.domain.model.nodehierarchy.mapper.PortfolioDtoMapper;
import com.djt.hvac.domain.model.nodehierarchy.validation.PortfolioValidationResult;
import com.djt.hvac.domain.model.nodehierarchy.visitor.PortfolioVisitor;
import com.djt.hvac.domain.model.rawpoint.dto.RawPointDto;
import com.djt.hvac.domain.model.report.ReportInstanceEntity;
import com.djt.hvac.domain.model.report.dto.ReportInstanceDto;
import com.djt.hvac.domain.model.report.dto.ReportInstanceStatusDto;

public class PortfolioEntityTest extends AbstractResoluteDomainModelTest {

  @BeforeClass
  public static void beforeClass() throws Exception {
    AbstractResoluteDomainModelTest.beforeClass();
    MockCacheClient.getInstance().removeAllCacheEntries();
  }
  
  @Test
  public void mapFromDtos() throws Exception {

    // STEP 1: ARRANGE



    // STEP 2: ACT
    PortfolioEntity portfolio = nodeHierarchyService.loadPortfolio(customerId);



    // STEP 3: ASSERT
    validateUsaHockeyAreanaPortfolioNodeCounts(portfolio);
  }

  @Test
  public void mapToDtos() throws Exception {

    // STEP 1: ARRANGE
    PortfolioEntity portfolio = nodeHierarchyService.loadPortfolio(customerId);
    
    System.err.println("numBuildings:                             " + portfolio.getNumBuildingsProcessed());
    System.err.println("numSubBuildings:                          " + portfolio.getNumSubBuildingsProcessed());
    System.err.println("numPlants:                                " + portfolio.getNumPlantsProcessed());
    System.err.println("numLoops:                                 " + portfolio.getNumLoopsProcessed());
    System.err.println("numFloors:                                " + portfolio.getNumFloorsProcessed());
    System.err.println("numEquipment:                             " + portfolio.getNumEquipmentProcessed());
    System.err.println("numMappablePoints:                        " + portfolio.getNumMappablePointsProcessed());
    System.err.println("numCustomAsyncComputedPointsProcessed:    " + portfolio.getNumCustomAsyncComputedPointsProcessed());
    System.err.println("numScheduledAsyncComputedPointsProcessed: " + portfolio.getNumScheduledAsyncComputedPointsProcessed());
    System.err.println("numAsyncComputedPointsProcessed:          " + portfolio.getNumAsyncComputedPointsProcessed());
    System.err.println("numAdFunctionInstanceCandidatesProcessed: " + portfolio.getNumAdFunctionInstanceCandidatesProcessed());
    System.err.println("numAdFunctionInstancesProcessed:          " + portfolio.getNumAdFunctionInstancesProcessed());
    System.err.println("numAdFunctionErrorMessagesProcessed:      " + portfolio.getNumAdFunctionErrorMessagesProcessed());
    System.err.println("numReportInstancesProcessed:              " + portfolio.getNumReportInstancesProcessed());
    
    Set<IssueType> issueTypes = new HashSet<>();
    List<ValidationMessage> validationMessages = portfolio.validate();
    PortfolioValidationResult portfolioValidationResult =
        ValidationMessage.buildPortfolioValidationResult(portfolio, validationMessages, issueTypes);
    Assert.assertNotNull("portfolioValidationResult is null", portfolioValidationResult);
    validateUsaHockeyAreanaPortfolioNodeCounts(portfolio);



    // STEP 2: ACT
    Map<String, Object> dtos = PortfolioEntity.mapToDtos(portfolio);



    // STEP 3: ASSERT
    Assert.assertNotNull("dtos is null", dtos);
    rawPointDtoList = PortfolioDtoMapper.getRawPointDtoList(dtos);
    nonPointNodeDtoList = PortfolioDtoMapper.getNonPointNodeDtoList(dtos);
    mappablePointNodeDtoList = PortfolioDtoMapper.getMappablePointNodeDtoList(dtos);
    customAsyncComputedPointNodeDtoList = PortfolioDtoMapper.getCustomAsyncComputedPointNodeDtoList(dtos);
    scheduledAsyncComputedPointNodeDtoList = PortfolioDtoMapper.getScheduledAsyncComputedPointNodeDtoList(dtos);
    asyncComputedPointNodeDtoList = PortfolioDtoMapper.getAsyncComputedPointNodeDtoList(dtos);
    nodeTagDtoList = PortfolioDtoMapper.getNodeTagDtoList(dtos);
    energyExchangeSystemEdgeDtoList = PortfolioDtoMapper.getEnergyExchangeSystemEdgeDtoList(dtos);
    reportInstanceDtoList = PortfolioDtoMapper.getReportInstanceDtoList(dtos);
    reportInstanceStatusDtoList = PortfolioDtoMapper.getReportInstanceStatusDtoList(dtos);
    adFunctionInstanceCandidateDtoList = PortfolioDtoMapper.getAdFunctionInstanceCandidateDtoList(dtos);
    adFunctionInstanceDtoList = PortfolioDtoMapper.getAdFunctionInstanceDtoList(dtos);
    adFunctionErrorMessagesDtoList = PortfolioDtoMapper.getAdFunctionErrorMessagesDtoList(dtos);

    PortfolioEntity rehydratedPortfolio = PortfolioEntity.mapFromDtos(
        portfolio.getParentCustomer(),
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
        adFunctionErrorMessagesDtoList);

    validateUsaHockeyAreanaPortfolioNodeCounts(rehydratedPortfolio);

    List<ValidationMessage> rehydratedValidationMessages = rehydratedPortfolio.validate();
    Assert.assertNotNull("rehydratedValidationMessages is null", rehydratedValidationMessages);

    Assert.assertEquals("validationMessages size is incorrect",
        Integer.toString(validationMessages.size()),
        Integer.toString(rehydratedValidationMessages.size()));
  }

  @Ignore
  @Test
  public void validateMcLaren() throws Exception {

    customerId = MCLAREN_CUSTOMER_ID;
    validate();
  }

  @Test
  public void validateRedico() throws Exception {

    customerId = REDICO_CUSTOMER_ID;
    validate();
  }

  @Ignore
  @Test
  public void validateDominos() throws Exception {

    customerId = DOMINOS_CUSTOMER_ID;
    validate();
  }

  private void validate() throws Exception {

    // STEP 1: ARRANGE
    PortfolioEntity portfolio = nodeHierarchyService.loadPortfolio(customerId);
    Assert.assertNotNull("portfolio is null", portfolio);



    // STEP 2: ACT
    List<ValidationMessage> validationMessages = portfolio.validate();



    // STEP 3: ASSERT
    Assert.assertNotNull("validationMessages is null", validationMessages);
  }

  @Test
  public void validatePhase1ValidationResultCustomerPortfolios() throws Exception {

    //customerId = MCLAREN_CUSTOMER_ID;
    //validatePhase1ValidationResult();
    
    customerId = REDICO_CUSTOMER_ID;
    validatePhase1ValidationResult();
    
    //customerId = DOMINOS_CUSTOMER_ID;
    //validatePhase1ValidationResult();
  }

  private void validatePhase1ValidationResult() throws Exception {

    // STEP 1: ARRANGE
    // http://localhost:8080/customers/12/validation/sync?fullResult=true&issueIds=1,2,3,4,5,23,24,25
    PortfolioEntity portfolio = nodeHierarchyService.loadPortfolio(customerId);
    Assert.assertNotNull("portfolio is null", portfolio);
    Set<IssueType> issueTypes = new HashSet<>();
    issueTypes.add(IssueType.get(1));
    issueTypes.add(IssueType.get(2));
    issueTypes.add(IssueType.get(3));
    issueTypes.add(IssueType.get(4));
    issueTypes.add(IssueType.get(5));
    issueTypes.add(IssueType.get(23));
    issueTypes.add(IssueType.get(24));
    issueTypes.add(IssueType.get(25));



    // STEP 2: ACT
    List<ValidationMessage> validationMessages = portfolio.validate(issueTypes);



    // STEP 3: ASSERT
    Assert.assertNotNull("validationMessages is null", validationMessages);
    // PortfolioValidationResult portfolioValidationResult =
    // ValidationMessage.buildPortfolioValidationResult(portfolio, validationMessages,
    // issueTypes);
    // System.err.println(OBJECT_WRITER_WITH_PRETTY_PRINTER.writeValueAsString(portfolioValidationResult));
  }

  @Test
  public void validatePhase2ValidationResultCustomerPortfolios() throws Exception {

    customerId = MCLAREN_CUSTOMER_ID;
    validatePhase2ValidationResult();
    
    customerId = REDICO_CUSTOMER_ID;
    validatePhase2ValidationResult();
    
    customerId = DOMINOS_CUSTOMER_ID;
    validatePhase2ValidationResult();
  }

  private void validatePhase2ValidationResult()
      throws Exception {

    // STEP 1: ARRANGE
    PortfolioEntity portfolio = nodeHierarchyService.loadPortfolio(customerId);
    Assert.assertNotNull("portfolio is null", portfolio);
    Set<IssueType> phase1IssueTypes = ValidationMessage.extractPhaseOneIssueTypes();
    portfolio.remediate(phase1IssueTypes);
    Set<IssueType> phase2IssueTypes = ValidationMessage.extractPhaseTwoIssueTypes();



    // STEP 2: ACT
    List<ValidationMessage> phase2validationMessages = portfolio.validate(phase2IssueTypes);



    // STEP 3: ASSERT
    Assert.assertNotNull("phase2validationMessages is null", phase2validationMessages);
    PortfolioValidationResult phase2PortfolioValidationResult =
    ValidationMessage.buildPortfolioValidationResult(portfolio, phase2validationMessages,
    phase2IssueTypes);
    System.err.println(OBJECT_WRITER_WITH_PRETTY_PRINTER.writeValueAsString(phase2PortfolioValidationResult));
  }

  @Test
  public void validateWithPhaseOneIssueFilter() throws Exception {

    // STEP 1: ARRANGE
    customerId = REDICO_CUSTOMER_ID;
    Set<IssueType> issueTypes = ValidationMessage.extractPhaseOneIssueTypes();
    PortfolioEntity portfolio = nodeHierarchyService.loadPortfolio(customerId);



    // STEP 2: ACT
    List<ValidationMessage> validationMessages = portfolio.validate();
    validationMessages = ValidationMessage.filterByIssueType(validationMessages, issueTypes);
    PortfolioValidationResult portfolioValidationResult = ValidationMessage.buildPortfolioValidationResult(portfolio, validationMessages);



    // STEP 3: ASSERT
    Assert.assertNotNull("portfolioValidationResult is null", portfolioValidationResult);
    Assert.assertNotNull("validationMessages is null", validationMessages);

    Assert.assertEquals("getTotalNumberOfValidationMessages is incorrect",
        Integer.toString(validationMessages.size()),
        Integer.toString(portfolioValidationResult.getTotalNumberOfValidationMessages()));
  }

  @Test
  public void validateWithPhaseTwoIssueFilter() throws Exception {

    // STEP 1: ARRANGE
    customerId = REDICO_CUSTOMER_ID;
    Set<IssueType> issueTypes = ValidationMessage.extractPhaseTwoIssueTypes();
    PortfolioEntity portfolio = nodeHierarchyService.loadPortfolio(customerId);



    // STEP 2: ACT
    List<ValidationMessage> validationMessages = portfolio.validate();
    validationMessages = ValidationMessage.filterByIssueType(validationMessages, issueTypes);
    PortfolioValidationResult portfolioValidationResult = ValidationMessage.buildPortfolioValidationResult(portfolio, validationMessages);



    // STEP 3: ASSERT
    Assert.assertNotNull("portfolioValidationResult is null", portfolioValidationResult);
    Assert.assertNotNull("validationMessages is null", validationMessages);

    Assert.assertEquals("getTotalNumberOfValidationMessages is incorrect",
        Integer.toString(validationMessages.size()),
        Integer.toString(portfolioValidationResult.getTotalNumberOfValidationMessages()));
  }  
  
  @Test
  public void rehydrateFromDtosCustomerPortfolios() throws Exception {

    //customerId = MCLAREN_CUSTOMER_ID;
    //rehydrateFromDtos();
    
    customerId = REDICO_CUSTOMER_ID;
    rehydrateFromDtos();
    
    //customerId = DOMINOS_CUSTOMER_ID;
    //rehydrateFromDtos();
  }

  private void rehydrateFromDtos() throws Exception {

    // STEP 1: ARRANGE
    PortfolioEntity portfolio = nodeHierarchyService.loadPortfolio(customerId);

    Assert.assertNotNull("portfolio is null", portfolio);

    List<ValidationMessage> validationMessages = portfolio.validate();
    Assert.assertNotNull("validationMessages is null", validationMessages);

    Map<String, Object> dtos = PortfolioEntity.mapToDtos(portfolio);
    Assert.assertNotNull("dtos is null", dtos);

    List<RawPointDto> rawPointDtoList2 = PortfolioDtoMapper.getRawPointDtoList(dtos);
    List<NonPointNodeDto> nonPointNodeDtoList2 = PortfolioDtoMapper.getNonPointNodeDtoList(dtos);
    List<MappablePointNodeDto> mappablePointNodeDtoList2 = PortfolioDtoMapper.getMappablePointNodeDtoList(dtos);
    List<CustomAsyncComputedPointNodeDto> customAsyncComputedPointNodeDtoList2 = PortfolioDtoMapper.getCustomAsyncComputedPointNodeDtoList(dtos);
    List<ScheduledAsyncComputedPointNodeDto> scheduledAsyncComputedPointNodeDtoList2 = PortfolioDtoMapper.getScheduledAsyncComputedPointNodeDtoList(dtos);
    List<AsyncComputedPointNodeDto> asyncComputedPointNodeDtoList2 = PortfolioDtoMapper.getAsyncComputedPointNodeDtoList(dtos);
    List<NodeTagDto> nodeTagDtoList2 = PortfolioDtoMapper.getNodeTagDtoList(dtos);
    List<EnergyExchangeSystemEdgeDto> energyExchangeSystemEdgeDtoList2 = PortfolioDtoMapper.getEnergyExchangeSystemEdgeDtoList(dtos);
    List<ReportInstanceDto> reportInstanceDtoList2 = PortfolioDtoMapper.getReportInstanceDtoList(dtos);
    List<ReportInstanceStatusDto> reportInstanceStatusDtoList2 = PortfolioDtoMapper.getReportInstanceStatusDtoList(dtos);
    List<AdFunctionInstanceDto> adFunctionInstanceCandidateDtoList2 = PortfolioDtoMapper.getAdFunctionInstanceCandidateDtoList(dtos);
    List<AdFunctionInstanceDto> adFunctionInstanceDtoList2 = PortfolioDtoMapper.getAdFunctionInstanceDtoList(dtos);
    List<AdFunctionErrorMessagesDto> adFunctionErrorMessagesDtoList2 = PortfolioDtoMapper.getAdFunctionErrorMessagesDtoList(dtos);


    // STEP 2: ACT
    PortfolioEntity rehydratedPortfolio = PortfolioEntity.mapFromDtos(
        portfolio.getParentCustomer(),
        rawPointDtoList2,
        nonPointNodeDtoList2,
        mappablePointNodeDtoList2,
        customAsyncComputedPointNodeDtoList2,
        scheduledAsyncComputedPointNodeDtoList2,
        asyncComputedPointNodeDtoList2,
        nodeTagDtoList2,
        energyExchangeSystemEdgeDtoList2,
        reportInstanceDtoList2,
        reportInstanceStatusDtoList2,
        adFunctionInstanceCandidateDtoList2,
        adFunctionInstanceDtoList2,
        adFunctionErrorMessagesDtoList2);



    // STEP 3: ASSERT
    Assert.assertNotNull("rehydratedPortfolio is null", rehydratedPortfolio);

    Assert.assertEquals("rehydrated building entities is incorrect",
        Integer.toString(portfolio.getNumBuildingsProcessed()),
        Integer.toString(rehydratedPortfolio.getNumBuildingsProcessed()));
    Assert.assertEquals("rehydrated sub-building entities is incorrect",
        Integer.toString(portfolio.getNumSubBuildingsProcessed()),
        Integer.toString(rehydratedPortfolio.getNumSubBuildingsProcessed()));
    Assert.assertEquals("rehydrated plants entities is incorrect",
        Integer.toString(portfolio.getNumPlantsProcessed()),
        Integer.toString(rehydratedPortfolio.getNumPlantsProcessed()));
    Assert.assertEquals("rehydrated loops entities is incorrect",
        Integer.toString(portfolio.getNumLoopsProcessed()),
        Integer.toString(rehydratedPortfolio.getNumLoopsProcessed()));
    Assert.assertEquals("rehydrated floors entities is incorrect",
        Integer.toString(portfolio.getNumFloorsProcessed()),
        Integer.toString(rehydratedPortfolio.getNumFloorsProcessed()));
    Assert.assertEquals("rehydrated equipment entities is incorrect",
        Integer.toString(portfolio.getNumEquipmentProcessed()),
        Integer.toString(rehydratedPortfolio.getNumEquipmentProcessed()));
    Assert.assertEquals("rehydrated mappable point entities is incorrect",
        Integer.toString(portfolio.getNumMappablePointsProcessed()),
        Integer.toString(rehydratedPortfolio.getNumMappablePointsProcessed()));
    Assert.assertEquals("rehydrated custom point entities is incorrect",
        Integer.toString(portfolio.getNumCustomAsyncComputedPointsProcessed()),
        Integer.toString(rehydratedPortfolio.getNumCustomAsyncComputedPointsProcessed()));
    Assert.assertEquals("rehydrated scheduled point entities is incorrect",
        Integer.toString(portfolio.getNumScheduledAsyncComputedPointsProcessed()),
        Integer.toString(rehydratedPortfolio.getNumScheduledAsyncComputedPointsProcessed()));
    Assert.assertEquals("rehydrated async computed point entities is incorrect",
        Integer.toString(portfolio.getNumAsyncComputedPointsProcessed()),
        Integer.toString(rehydratedPortfolio.getNumAsyncComputedPointsProcessed()));
    Assert.assertEquals("rehydrated ad function instance entities is incorrect",
        Integer.toString(portfolio.getNumAdFunctionInstancesProcessed()),
        Integer.toString(rehydratedPortfolio.getNumAdFunctionInstancesProcessed()));
    Assert.assertEquals("rehydrated ad function error messages entities is incorrect",
        Integer.toString(portfolio.getNumAdFunctionErrorMessagesProcessed()),
        Integer.toString(rehydratedPortfolio.getNumAdFunctionErrorMessagesProcessed()));
    Assert.assertEquals("rehydrated report instance entities is incorrect",
        Integer.toString(portfolio.getNumReportInstancesProcessed()),
        Integer.toString(rehydratedPortfolio.getNumReportInstancesProcessed()));
  }

  @Test
  public void getAllReportInstances() throws Exception {

    // STEP 1: ARRANGE
    PortfolioEntity portfolio = nodeHierarchyService.loadPortfolio(customerId);
    PortfolioVisitor.evaluateReports(portfolio);



    // STEP 2: ACT
    List<ReportInstanceEntity> reportInstances = portfolio.getAllReportInstances();



    // STEP 3: ASSERT
    int numActualReportInstances = reportInstances.size();
    int numBuildings = portfolio.getAllBuildings().size();
    int numReportTemplates =
        DictionaryContext.getReportTemplatesContainer().getReportTemplates().size();
    int numExpectedReportInstances = numBuildings * numReportTemplates;

    Assert.assertEquals("reportInstances size is incorrect",
        Integer.toString(numExpectedReportInstances),
        Integer.toString(numActualReportInstances));
  }
  
  @Test
  public void like() throws Exception {
   
    // STEP 1: ARRANGE
    String string = "digital";
    String searchTerm = "dig*";
    String regex = null;
    if (searchTerm.contains("*")) {
      regex = PortfolioEntity.wildcardToRegex(searchTerm);
    }
    boolean matches = PortfolioEntity.like(searchTerm, regex, string);
    Assert.assertTrue("matches is incorrect", matches);
    
    
    searchTerm = "*tal";
    regex = null;
    if (searchTerm.contains("*")) {
      regex = PortfolioEntity.wildcardToRegex(searchTerm);
    }
    matches = PortfolioEntity.like(searchTerm, regex, string);
    Assert.assertTrue("matches is incorrect", matches);

    
    searchTerm = "*gita*";
    regex = null;
    if (searchTerm.contains("*")) {
      regex = PortfolioEntity.wildcardToRegex(searchTerm);
    }
    matches = PortfolioEntity.like(searchTerm, regex, string);
    Assert.assertTrue("matches is incorrect", matches);

    
    searchTerm = "*g*ta*";
    regex = null;
    if (searchTerm.contains("*")) {
      regex = PortfolioEntity.wildcardToRegex(searchTerm);
    }
    matches = PortfolioEntity.like(searchTerm, regex, string);
    Assert.assertTrue("matches is incorrect", matches);

    
    searchTerm = string;
    regex = null;
    if (searchTerm.contains("*")) {
      regex = PortfolioEntity.wildcardToRegex(searchTerm);
    }
    matches = PortfolioEntity.like(searchTerm, regex, string);
    Assert.assertTrue("matches is incorrect", matches);
  }
}
