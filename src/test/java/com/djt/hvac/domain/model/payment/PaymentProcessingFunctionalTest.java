//@formatter:off
package com.djt.hvac.domain.model.payment;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.djt.hvac.domain.model.AbstractResoluteDomainModelTest;
import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.common.timekeeper.impl.TestTimeKeeperImpl;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.customer.OnlineCustomerEntity;
import com.djt.hvac.domain.model.customer.enums.CustomerPaymentStatus;
import com.djt.hvac.domain.model.customer.enums.CustomerStatus;
import com.djt.hvac.domain.model.customer.enums.CustomerType;
import com.djt.hvac.domain.model.dictionary.PaymentPlanEntity;
import com.djt.hvac.domain.model.dictionary.enums.UnitSystem;
import com.djt.hvac.domain.model.distributor.AbstractDistributorEntity;
import com.djt.hvac.domain.model.distributor.OnlineDistributorEntity;
import com.djt.hvac.domain.model.distributor.enums.DistributorPaymentStatus;
import com.djt.hvac.domain.model.distributor.enums.DistributorStatus;
import com.djt.hvac.domain.model.distributor.enums.DistributorType;
import com.djt.hvac.domain.model.distributor.enums.PaymentMethodType;
import com.djt.hvac.domain.model.distributor.paymentmethod.AbstractPaymentMethodEntity;
import com.djt.hvac.domain.model.distributor.service.DistributorHierarchyStateEvaluator;
import com.djt.hvac.domain.model.distributor.service.model.CreatePaymentMethodRequest;
import com.djt.hvac.domain.model.email.client.MockEmailClient;
import com.djt.hvac.domain.model.email.dto.EmailDto;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BillableBuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingSubscriptionEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingPaymentStatus;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingStatus;
import com.djt.hvac.domain.model.nodehierarchy.point.mapped.MappablePointEntity;
import com.djt.hvac.domain.model.nodehierarchy.service.NodeHierarchyService;
import com.djt.hvac.domain.model.nodehierarchy.service.command.MapRawPointsRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.RawPointData;
import com.djt.hvac.domain.model.nodehierarchy.utils.LoadPortfolioOptions;
import com.djt.hvac.domain.model.rawpoint.RawPointEntity;
import com.djt.hvac.domain.model.stripe.client.MockStripeClient;
import com.djt.hvac.domain.model.user.DistributorUserEntity;
import com.djt.hvac.domain.model.user.enums.UserRoleType;

public class PaymentProcessingFunctionalTest extends AbstractResoluteDomainModelTest {
  
  public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static String currentDate = "2019-11-01";
  private static TestTimeKeeperImpl TEST_TIME_KEEPER;
  
  private static String distributorName = "Online Distributor Name";
  private static String customerName = "Online Customer Name";
  private static UserRoleType userRoleType = UserRoleType.DISTRIBUTOR_ADMIN;
  private static String email = "tmyers@resolutebi.com";
  private static String firstName = "Tom";
  private static String lastName = "Myers";
  private static Boolean isAccountManager = Boolean.TRUE;
  private static String portfolioName = "Online_Customer_Portfolio_Node";
  private static String displayName = "Online Customer Portfolio Node";
  
  private MockEmailClient emailClient;
  private DistributorHierarchyStateEvaluator distributorHierarchyStateEvaluator;

  
  private OnlineDistributorEntity onlineDistributor;
  private Integer onlineDistributorId;
  
  private DistributorUserEntity accountManager;
  private Integer accountManagerId;
  
  private OnlineCustomerEntity onlineCustomer;
  private Integer onlineCustomerId;
  
  private PortfolioEntity portfolio;
  private Integer portfolioId;
  
  private BillableBuildingEntity billableBuilding;
  private Integer billableBuildingId;
  
  private int mappedPointCount;
  
  private BuildingSubscriptionEntity buildingSubscription;
  private Integer buildingSubscriptionId;
  
  private boolean distributorShouldNoLongerExist;
  private boolean customerShouldNoLongerExist;
  private boolean buildingShouldNoLongerExist;
  
  private int useCaseIndex;
  
  @BeforeClass
  public static void beforeClass() throws Exception {
    AbstractResoluteDomainModelTest.beforeClass();
  }
  
  @Before
  public void before() throws Exception {
    
    super.before();
    
    try {
      TimeZone.setDefault(TimeZone.getTimeZone("GMT"));    
      System.err.println("Using default time zone:" + TimeZone.getDefault());
      System.err.println();
    } catch (Exception e) {
    }
    
    onlineDistributor = null;
    onlineDistributorId = null;
    accountManager = null;
    accountManagerId = null;
    onlineCustomer = null;
    onlineCustomerId = null;
    portfolio = null;
    portfolioId = null;
    billableBuilding = null;
    billableBuildingId = null;
    mappedPointCount = -1;
    buildingSubscription = null;
    buildingSubscriptionId = null;
    distributorShouldNoLongerExist = false;
    customerShouldNoLongerExist = false;
    buildingShouldNoLongerExist = false;

    TEST_TIME_KEEPER = new TestTimeKeeperImpl("2019-11-01");
    AbstractEntity.setTimeKeeper(TEST_TIME_KEEPER);
    
    emailClient = MockEmailClient.getInstance();
    distributorHierarchyStateEvaluator = new DistributorHierarchyStateEvaluator(
        distributorService,
        customerService,
        nodeHierarchyService,
        emailClient);
    
    MockStripeClient.getInstance().reset();
    MockEmailClient.getInstance().reset();
    
    useCaseIndex = 1;
  }
  
 @Test
 public void scenario_1_1_1_lifecycle() throws Exception {

   PaymentPlanEntity paymentPlan = null;
   Integer paymentPlanId = null;
   AbstractPaymentMethodEntity paymentMethod = null;
   Integer paymentMethodId = null;

   
   // ======================================================================================================
   currentDate = "2020-01-01";
   TEST_TIME_KEEPER.setCurrentDate(currentDate);
   System.err.println("\n" + currentDate + ": UC #" + (useCaseIndex++) + ": GROUND STATE - NOTHING CREATED YET");
   loadAndEvaluateObjects();
   assertState(AssertionHolder
       .builder()
       .withExpectedDistributorConfigStatus(null)
       .withExpectedDistributorPaymentStatus(null)
       .withExpectedCustomerConfigStatus(null)
       .withExpectedCustomerPaymentStatus(null)
       .withExpectedBuildingConfigStatus(null)
       .withExpectedBuildingPaymentStatus(null)
       .withExpectedCustomerCount(0)
       .withExpectedBuildingCount(0)
       .withExpectedBuildingMappedPointCount(0)
       .withExpectedBuildingPendingDeletion(false)
       .build());    
   
   
   // ======================================================================================================
   System.err.println("\n" + currentDate + ": UC #" + (useCaseIndex++) + ": CREATE DISTRIBUTOR (SYNERGY)");
   onlineDistributor = (OnlineDistributorEntity)distributorService.createDistributor(
       RESOLUTE_DISTRIBUTOR_ID, 
       DistributorType.ONLINE,
       distributorName,
       UnitSystem.IP.toString(),
       false);
   
   onlineDistributorId = onlineDistributor.getPersistentIdentity();
   System.err.println("Created online distributor: " + onlineDistributor + " with id: " + onlineDistributorId);
   
   loadAndEvaluateObjects();
   assertState(AssertionHolder
       .builder()
       .withExpectedDistributorConfigStatus(DistributorStatus.CREATED)
       .withExpectedDistributorPaymentStatus(DistributorPaymentStatus.UP_TO_DATE)
       .withExpectedCustomerConfigStatus(null)
       .withExpectedCustomerPaymentStatus(null)
       .withExpectedBuildingConfigStatus(null)
       .withExpectedBuildingPaymentStatus(null)
       .withExpectedCustomerCount(0)
       .withExpectedBuildingCount(0)
       .withExpectedBuildingMappedPointCount(0)
       .withExpectedBuildingPendingDeletion(false)
       .build());    
   
   
   // ======================================================================================================
   System.err.println("\n" + currentDate + ": UC #" + (useCaseIndex++) + ": CREATE ACCOUNT MANAGER FOR ONLINE DISTRIBUTOR (SYNERGY)");
   accountManager = distributorService.createDistributorUser(
       onlineDistributor, 
       userRoleType, 
       email, 
       firstName, 
       lastName, 
       isAccountManager);
   
   accountManagerId = accountManager.getPersistentIdentity();
   System.err.println("Created account manager: " + accountManager + " with id: " + accountManagerId);
   loadAndEvaluateObjects();
   
   
   // ======================================================================================================
   System.err.println("\n" + currentDate + ": UC #" + (useCaseIndex++) + ": CREATE ONLINE CUSTOMER (SYNERGY)");
   onlineCustomer = (OnlineCustomerEntity)customerService.createCustomer(
       onlineDistributor, 
       CustomerType.ONLINE,
       customerName,
       UnitSystem.IP.toString());
   
   onlineCustomerId = onlineCustomer.getPersistentIdentity();
   System.err.println("Created customer: " + onlineCustomer + " with id: " + onlineCustomerId);
   
   loadAndEvaluateObjects();
   assertState(AssertionHolder
       .builder()
       .withExpectedDistributorConfigStatus(DistributorStatus.CREATED)
       .withExpectedDistributorPaymentStatus(DistributorPaymentStatus.UP_TO_DATE)
       .withExpectedCustomerConfigStatus(CustomerStatus.CREATED)
       .withExpectedCustomerPaymentStatus(CustomerPaymentStatus.UP_TO_DATE)
       .withExpectedBuildingConfigStatus(null)
       .withExpectedBuildingPaymentStatus(null)
       .withExpectedCustomerCount(1)
       .withExpectedBuildingCount(0)
       .withExpectedBuildingMappedPointCount(0)
       .withExpectedBuildingPendingDeletion(false)
       .build()); 
   
   
   // ======================================================================================================
   System.err.println("\n" + currentDate + ": UC #" + (useCaseIndex++) + ": CREATE PORTFOLIO FOR ONLINE CUSTOMER (AUTOMATIC IN SYNERGY)");
   portfolio = nodeHierarchyService.createPortfolio(
       onlineCustomer, 
       portfolioName, 
       displayName);
   
   portfolioId = portfolio.getPersistentIdentity();
   System.err.println("Created portfolio node: " + portfolio + " with id: " + portfolioId);


   // ======================================================================================================
   // ======================================================================================================
   // This is to simulate ingestion of raw points from a cloudfill connector.
   int numPointsToMap = 3;
   String mappingExpression1 = "/Drivers/NiagaraNetwork/{building}/{subBuilding}/{floor}/{equipment}/{point}";
   String metricIdPattern = "/Drivers/NiagaraNetwork/Building_1/SubBuilding_1/Floor_1/Equipment_1/Point_Y";
   List<RawPointEntity> rawPoints = new ArrayList<>();
   for (int i=1; i <= numPointsToMap; i++) {
     
     String metricId = metricIdPattern
         .replace("Y", Integer.toString(i));
    
     rawPoints.add(buildMockRawPoint(onlineCustomerId, metricId));
   }
   onlineCustomer.addRawPoints(rawPoints);
   boolean storeRawPoints = true;
   customerService.updateCustomer(onlineCustomer, storeRawPoints);
   // ======================================================================================================
   // ======================================================================================================

   
   // ======================================================================================================
   fastForwardAnEvaluate("2020-01-06");
   System.err.println("\n" + currentDate + ": UC #" + (useCaseIndex++) + ": MAP RAW POINTS (SYNERGY)");

   // The building will be created first, then the three mappable points.
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
       .withCustomerId(onlineCustomerId)
       .withRawPoints(rawPointData)
       .withMappingExpression(mappingExpression1)
       .withMetricIdDelimiter(NodeHierarchyService.DEFAULT_METRIC_ID_DELIMITER)
       .build(); 
   List<MappablePointEntity> createdMappablePoints = nodeHierarchyService.mapRawPoints(mapRawPointsRequest);

   // Get the one and only billable building that was the result of the mapping.
   Assert.assertTrue("created mappable points list size is incorrect", createdMappablePoints.size() == numPointsToMap);
   MappablePointEntity mappablePoint = createdMappablePoints.get(0);
   BuildingEntity building = mappablePoint.getAncestorBuilding();
   Assert.assertTrue("building is not of type BillableBuildingEntity", building instanceof BillableBuildingEntity);
   
   billableBuilding = (BillableBuildingEntity)building;
   billableBuildingId = billableBuilding.getPersistentIdentity();
   System.err.println("Created building node with id: " + billableBuildingId);
   
   loadAndEvaluateObjects();
   assertState(AssertionHolder
       .builder()
       .withExpectedDistributorConfigStatus(DistributorStatus.CREATED)
       .withExpectedDistributorPaymentStatus(DistributorPaymentStatus.UP_TO_DATE)
       .withExpectedCustomerConfigStatus(CustomerStatus.CREATED)
       .withExpectedCustomerPaymentStatus(CustomerPaymentStatus.UP_TO_DATE)
       .withExpectedBuildingConfigStatus(BuildingStatus.PENDING_ACTIVATION)
       .withExpectedBuildingPaymentStatus(BuildingPaymentStatus.UP_TO_DATE)
       .withExpectedCustomerCount(1)
       .withExpectedBuildingCount(1)
       .withExpectedBuildingMappedPointCount(numPointsToMap)
       .withExpectedBuildingPendingDeletion(false)
       .build());
   
   
   // ======================================================================================================
   // We need to simulate the workflow for adding a payment method, so we just mimic that
   // interaction with the front end that supplies us with the payment method info, including
   // the Stripe source id (a.k.a. Stripe payment method id)
   String cardExpiry = "02/2020";
   String cardLastFour = "1234";
   String stripeSourceId = ((MockStripeClient)stripeClient).createStripePaymentMethod(
       onlineDistributor.getStripeCustomerId(), 
       cardExpiry, 
       cardLastFour);
   
   CreatePaymentMethodRequest request = CreatePaymentMethodRequest
       .builder()
       .withParentDistributorId(onlineDistributor.getPersistentIdentity())
       .withPaymentMethodType(PaymentMethodType.CREDIT_CARD.getName())
       .withName("name")
       .withStripeSourceId(stripeSourceId)
       .withAccountHolderName("accountHolderName")
       .withAddress("address")
       .withCity("city")
       .withState("state")
       .withZipCode("zipCode")
       .withPhoneNumber("phoneNumber")
       .withCardBrand("Visa")
       .withCardExpiry(cardExpiry)
       .withCardLastFour(cardLastFour)
       .build();

   
   // ======================================================================================================
   System.err.println("\n" + currentDate + ": UC #" + (useCaseIndex++) + ": CREATE PAYMENT METHOD (SYNERGY)");
   paymentMethod = distributorService.createPaymentMethod(onlineDistributor, request);
   
   Integer paymentMethodIdToUse = paymentMethod.getPersistentIdentity();
   System.err.println("Created payment method: " + paymentMethod + " with id: " + paymentMethodId);
   
   
   // ======================================================================================================
   fastForwardAnEvaluate("2020-01-07");
   System.err.println("\n" + currentDate + ": UC #" + (useCaseIndex++) + ": GET THE LOWEST COST PER INTERVAL QUALIFYING MONTHLY PAYMENT PLAN - MONTHLY/3000 - (SYNERGY)");
   paymentPlan = dictionaryService.getLowestCostPerIntervalQualifyingPaymentPlan(mappedPointCount);
   paymentPlanId = paymentPlan.getPersistentIdentity();
   Integer paymentPlanIdToUse = paymentPlan.getPersistentIdentity();
   System.err.println("Retrieved lowest cost per interval qualifying payment plan: " + paymentPlan + " with id: " + paymentPlanId);
   
   
   // ======================================================================================================
   System.err.println("\n" + currentDate + ": UC #" + (useCaseIndex++) + ": BUILDING IS ACTIVATED BY SUBSCRIPTION CREATION WITH SUCCESSFUL PAYMENT METHOD - MONTHLY/3000 - (SYNERGY)");
   buildingSubscription = nodeHierarchyService.createBuildingSubscription(
       onlineCustomerId,
       billableBuildingId, 
       paymentPlanIdToUse, 
       paymentMethodIdToUse);
   
   buildingSubscriptionId = buildingSubscription.getPersistentIdentity();
   System.err.println("Created building subscription: " + buildingSubscription + " with id: " + buildingSubscriptionId);
   
   loadAndEvaluateObjects();
   assertState(AssertionHolder
       .builder()
       .withExpectedDistributorConfigStatus(DistributorStatus.BILLABLE)
       .withExpectedDistributorPaymentStatus(DistributorPaymentStatus.UP_TO_DATE)
       .withExpectedCustomerConfigStatus(CustomerStatus.BILLABLE)
       .withExpectedCustomerPaymentStatus(CustomerPaymentStatus.UP_TO_DATE)
       .withExpectedBuildingConfigStatus(BuildingStatus.ACTIVE)
       .withExpectedBuildingPaymentStatus(BuildingPaymentStatus.UP_TO_DATE)
       .withExpectedCustomerCount(1)
       .withExpectedBuildingCount(1)
       .withExpectedBuildingMappedPointCount(numPointsToMap)
       .withExpectedBuildingPendingDeletion(false)
       .build()); 

   
   // ======================================================================================================
   fastForwardAnEvaluate("2020-02-07");
   System.err.println("\n" + currentDate + ": UC #" + (useCaseIndex++) + ": FIRST RENEWAL OF SUBSCRIPTION WITH SUCCESSFUL PAYMENT - MONTHLY/3000 - (SYSTEM)");
   
   loadAndEvaluateObjects();
   assertState(AssertionHolder
       .builder()
       .withExpectedDistributorConfigStatus(DistributorStatus.BILLABLE)
       .withExpectedDistributorPaymentStatus(DistributorPaymentStatus.UP_TO_DATE)
       .withExpectedCustomerConfigStatus(CustomerStatus.BILLABLE)
       .withExpectedCustomerPaymentStatus(CustomerPaymentStatus.UP_TO_DATE)
       .withExpectedBuildingConfigStatus(BuildingStatus.ACTIVE)
       .withExpectedBuildingPaymentStatus(BuildingPaymentStatus.UP_TO_DATE)
       .withExpectedCustomerCount(1)
       .withExpectedBuildingCount(1)
       .withExpectedBuildingMappedPointCount(numPointsToMap)
       .withExpectedBuildingPendingDeletion(false)
       .build()); 

   
   // ======================================================================================================
   fastForwardAnEvaluate("2020-03-07");
   System.err.println("\n" + currentDate + ": UC #" + (useCaseIndex++) + ": SECOND RENEWAL OF SUBSCRIPTION WITH FAILED PAYMENT - MONTHLY/3000 - (1ST FAILED PAYMENT)");
   
   assertState(AssertionHolder
       .builder()
       .withExpectedDistributorConfigStatus(DistributorStatus.BILLABLE)
       .withExpectedDistributorPaymentStatus(DistributorPaymentStatus.DELINQUENT)
       .withExpectedCustomerConfigStatus(CustomerStatus.BILLABLE)
       .withExpectedCustomerPaymentStatus(CustomerPaymentStatus.DELINQUENT)
       .withExpectedBuildingConfigStatus(BuildingStatus.ACTIVE)
       .withExpectedBuildingPaymentStatus(BuildingPaymentStatus.DELINQUENT)
       .withExpectedCustomerCount(1)
       .withExpectedBuildingCount(1)
       .withExpectedBuildingMappedPointCount(numPointsToMap)
       .withExpectedBuildingPendingDeletion(false)
       .build());
   
   
   // ======================================================================================================
   fastForwardAnEvaluate("2020-04-07");
   System.err.println("\n" + currentDate + ": UC #" + (useCaseIndex++) + ": SECOND RENEWAL OF SUBSCRIPTION WITH FAILED PAYMENT - MONTHLY/3000 - (2ND FAILED PAYMENT)");
   
   assertState(AssertionHolder
       .builder()
       .withExpectedDistributorConfigStatus(DistributorStatus.BILLABLE)
       .withExpectedDistributorPaymentStatus(DistributorPaymentStatus.DELINQUENT)
       .withExpectedCustomerConfigStatus(CustomerStatus.BILLABLE)
       .withExpectedCustomerPaymentStatus(CustomerPaymentStatus.DELINQUENT)
       .withExpectedBuildingConfigStatus(BuildingStatus.ACTIVE)
       .withExpectedBuildingPaymentStatus(BuildingPaymentStatus.DELINQUENT)
       .withExpectedCustomerCount(1)
       .withExpectedBuildingCount(1)
       .withExpectedBuildingMappedPointCount(numPointsToMap)
       .withExpectedBuildingPendingDeletion(false)
       .build()); 
   
   
   // ======================================================================================================
   fastForwardAnEvaluate("2020-05-07");
   System.err.println("\n" + currentDate + ": UC #" + (useCaseIndex++) + ": SECOND RENEWAL OF SUBSCRIPTION WITH FAILED PAYMENT - MONTHLY/3000 - (3RD FAILED PAYMENT)");
   
   assertState(AssertionHolder
       .builder()
       .withExpectedDistributorConfigStatus(DistributorStatus.BILLABLE)
       .withExpectedDistributorPaymentStatus(DistributorPaymentStatus.DELINQUENT)
       .withExpectedCustomerConfigStatus(CustomerStatus.BILLABLE)
       .withExpectedCustomerPaymentStatus(CustomerPaymentStatus.DELINQUENT)
       .withExpectedBuildingConfigStatus(BuildingStatus.ACTIVE)
       .withExpectedBuildingPaymentStatus(BuildingPaymentStatus.DELINQUENT)
       .withExpectedCustomerCount(1)
       .withExpectedBuildingCount(1)
       .withExpectedBuildingMappedPointCount(numPointsToMap)
       .withExpectedBuildingPendingDeletion(false)
       .build());
   
   
   // ======================================================================================================
   fastForwardAnEvaluate("2020-05-08");
   System.err.println("\n" + currentDate + ": UC #" + (useCaseIndex++) + ": CREATE NEW PAYMENT METHOD THAT WORKS (SYNERGY)");
   
   cardExpiry = "09/2024";
   cardLastFour = "5678";
   stripeSourceId = ((MockStripeClient)stripeClient).createStripePaymentMethod(
       onlineDistributor.getStripeCustomerId(), 
       cardExpiry, 
       cardLastFour);
   
   request = CreatePaymentMethodRequest
       .builder()
       .withParentDistributorId(onlineDistributor.getPersistentIdentity())
       .withPaymentMethodType(PaymentMethodType.CREDIT_CARD.getName())
       .withName("name2")
       .withStripeSourceId(stripeSourceId)
       .withAccountHolderName("accountHolderName")
       .withAddress("address2")
       .withCity("city2")
       .withState("state2")
       .withZipCode("zipCode2")
       .withPhoneNumber("phoneNumber2")
       .withCardBrand("Visa")
       .withCardExpiry(cardExpiry)
       .withCardLastFour(cardLastFour)
       .build();
   
   paymentMethod = distributorService.createPaymentMethod(onlineDistributor, request);
   paymentMethodId = paymentMethod.getPersistentIdentity();
   System.err.println("Created payment method: " + paymentMethod + " with id: " + paymentMethodId);
   nodeHierarchyService.updateBuildingSubscriptionForNewPaymentMethod(
       onlineCustomerId,
       billableBuildingId, 
       paymentMethodId);
   
   loadAndEvaluateObjects();
   assertState(AssertionHolder
       .builder()
       .withExpectedDistributorConfigStatus(DistributorStatus.BILLABLE)
       .withExpectedDistributorPaymentStatus(DistributorPaymentStatus.UP_TO_DATE)
       .withExpectedCustomerConfigStatus(CustomerStatus.BILLABLE)
       .withExpectedCustomerPaymentStatus(CustomerPaymentStatus.UP_TO_DATE)
       .withExpectedBuildingConfigStatus(BuildingStatus.ACTIVE)
       .withExpectedBuildingPaymentStatus(BuildingPaymentStatus.UP_TO_DATE)
       .withExpectedCustomerCount(1)
       .withExpectedBuildingCount(1)
       .withExpectedBuildingMappedPointCount(numPointsToMap)
       .withExpectedBuildingPendingDeletion(false)
       .build());
   
   
   // ======================================================================================================
   fastForwardAnEvaluate("2020-05-09");
   System.err.println("\n" + currentDate + ": UC #" + (useCaseIndex++) + ": UPDATE SUBSCRIPTION FOR NEW PAYMENT PLAN - SAME INTERVAL - MONTHLY/5000 - (SYNERGY)");
   
   PaymentPlanEntity newPaymentPlan = null;
   List<PaymentPlanEntity> paymentPlans = dictionaryService.getQualifyingPaymentPlansAsList(mappedPointCount);
   for (PaymentPlanEntity pp: paymentPlans) {
     
     // Get a different payment plan that has the same interval
     if (pp.getPaymentInterval().equals(paymentPlan.getPaymentInterval())
         && !pp.equals(paymentPlan)) {
       
       paymentPlan = pp;
       newPaymentPlan = paymentPlan;
       paymentPlanId = paymentPlan.getPersistentIdentity();
       break;
     }
   }
   nodeHierarchyService.updateBuildingSubscriptionForNewPaymentPlanSameInterval(
       onlineCustomerId, 
       billableBuildingId, 
       paymentPlanId);
   
   loadAndEvaluateObjects();
   printState();
   Assert.assertEquals("Updated subscription payment plan with same interval is incorrect",
       newPaymentPlan,
       paymentPlan);

   
   // ======================================================================================================
   fastForwardAnEvaluate("2020-05-09");
   System.err.println("\n" + currentDate + ": UC #" + (useCaseIndex++) + ": UPDATE SUBSCRIPTION FOR NEW PAYMENT PLAN - DIFFERENT INTERVAL - YEARLY/3000 - NO TRANSITION YET - (SYNERGY)");
   
   PaymentPlanEntity pendingPaymentPlan = null;
   paymentPlans = dictionaryService.getQualifyingPaymentPlansAsList(mappedPointCount);
   for (PaymentPlanEntity pp: paymentPlans) {
     
     // Get a different payment plan that has the same interval
     if (!pp.getPaymentInterval().equals(paymentPlan.getPaymentInterval())
         && !pp.equals(paymentPlan)) {
       
       pendingPaymentPlan = pp;
       break;
     }
   }
   nodeHierarchyService.updateBuildingSubscriptionForNewPaymentPlanDifferentInterval(
       onlineCustomerId, 
       billableBuildingId, 
       pendingPaymentPlan.getPersistentIdentity());
   PaymentPlanEntity currentPaymentPlan = paymentPlan;
   
   // VERIFY THAT THE PENDING PAYMENT PLAN IS SET AND THAT THE CURRENT PAYMENT PLAN IS UNCHANGED
   loadObjectsOnly();
   printState();
   Assert.assertEquals("subscription payment plan is different",
       currentPaymentPlan,
       buildingSubscription.getParentPaymentPlan());
   Assert.assertEquals("Updated subscription payment plan with different interval is incorrect",
       pendingPaymentPlan,
       buildingSubscription.getPendingPaymentPlan());
   
   
   // ======================================================================================================
   fastForwardAnEvaluate("2020-06-07");
   System.err.println("\n" + currentDate + ": UC #" + (useCaseIndex++) + ": VERIFY SUBSCRIPTION FOR NEW PAYMENT PLAN - DIFFERENT INTERVAL HAS TRANSITIONED - YEARLY/3000 - (SYNERGY)");
   
   // VERIFY THAT THE PENDING PAYMENT PLAN HAS TRANSITIONED NOW THAT THE PAYMENT INTERVAL HAS ENDED
   loadObjectsOnly();
   printState();
   Assert.assertEquals("subscription payment plan was not transitioned",
       pendingPaymentPlan,
       buildingSubscription.getParentPaymentPlan());
   Assert.assertNull("Pending payment plan is not null",
       buildingSubscription.getPendingPaymentPlan());

   
   // ======================================================================================================
   fastForwardAnEvaluate("2021-05-07");
   System.err.println("\n" + currentDate + ": UC #" + (useCaseIndex++) + ": UPDATE SUBSCRIPTION FOR NEW PAYMENT PLAN - SAME INTERVAL - YEARLY/5000 - (SYNERGY)");
   
   paymentPlan = buildingSubscription.getParentPaymentPlan();
   newPaymentPlan = null;
   paymentPlans = dictionaryService.getQualifyingPaymentPlansAsList(mappedPointCount);
   for (PaymentPlanEntity pp: paymentPlans) {
     
     // Get a different payment plan that has the same interval
     if (pp.getPaymentInterval().equals(paymentPlan.getPaymentInterval())
         && !pp.equals(paymentPlan)) {
       
       paymentPlan = pp;
       newPaymentPlan = paymentPlan;
       paymentPlanId = paymentPlan.getPersistentIdentity();
       break;
     }
   }
   nodeHierarchyService.updateBuildingSubscriptionForNewPaymentPlanSameInterval(
       onlineCustomerId, 
       billableBuildingId, 
       paymentPlanId);
   
   loadObjectsOnly();
   printState();
   Assert.assertEquals("Updated subscription payment plan with same interval is incorrect",
       newPaymentPlan,
       paymentPlan);

   
   // ======================================================================================================
   fastForwardAnEvaluate("2021-07-07");
   printState();
   System.err.println("\n" + currentDate + ": UC #" + (useCaseIndex++) + ": UPDATE SUBSCRIPTION FOR NEW PAYMENT PLAN - DIFFERENT INTERVAL - MONTHLY/3000 - NO TRANSITION YET - (SYNERGY)");
   
   pendingPaymentPlan = null;
   paymentPlans = dictionaryService.getQualifyingPaymentPlansAsList(mappedPointCount);
   for (PaymentPlanEntity pp: paymentPlans) {
     
     // Get a different payment plan that has the same interval
     if (!pp.getPaymentInterval().equals(paymentPlan.getPaymentInterval())
         && !pp.equals(paymentPlan)) {
       
       pendingPaymentPlan = pp;
       break;
     }
   }
   nodeHierarchyService.updateBuildingSubscriptionForNewPaymentPlanDifferentInterval(
       onlineCustomerId, 
       billableBuildingId, 
       pendingPaymentPlan.getPersistentIdentity());
   currentPaymentPlan = paymentPlan;
   
   // VERIFY THAT THE PENDING PAYMENT PLAN IS SET AND THAT THE CURRENT PAYMENT PLAN IS UNCHANGED
   loadObjectsOnly();
   printState();
   Assert.assertEquals("subscription payment plan is different",
       currentPaymentPlan,
       buildingSubscription.getParentPaymentPlan());
   Assert.assertEquals("Updated subscription payment plan with different interval is incorrect",
       pendingPaymentPlan,
       buildingSubscription.getPendingPaymentPlan());
   
   
   // ======================================================================================================
   fastForwardAnEvaluate("2022-07-07");
   System.err.println("\n" + currentDate + ": UC #" + (useCaseIndex++) + ": VERIFY SUBSCRIPTION FOR NEW PAYMENT PLAN - DIFFERENT INTERVAL HAS TRANSITIONED - MONTHLY/3000 - (SYNERGY)");
   
   // VERIFY THAT THE PENDING PAYMENT PLAN HAS TRANSITIONED NOW THAT THE PAYMENT INTERVAL HAS ENDED
   loadObjectsOnly();
   printState();
   Assert.assertEquals("subscription payment plan was not transitioned",
       pendingPaymentPlan,
       buildingSubscription.getParentPaymentPlan());
   Assert.assertNull("Pending payment plan is not null",
       buildingSubscription.getPendingPaymentPlan());

   
   // ======================================================================================================
   currentDate = "2022-09-15";
   fastForwardAnEvaluate(currentDate);
   System.err.println("\n" + currentDate + ": UC #" + (useCaseIndex++) + ": CANCEL THE SUBSCRIPTION - CURRENT PAYMENT INTERVAL HAS NOT EXPIRED - MONTHLY/3000 - (SYNERGY)");
   
   nodeHierarchyService.cancelBuildingSubscription(onlineCustomerId, billableBuildingId);
   
   loadAndEvaluateObjects();
   assertState(AssertionHolder
       .builder()
       .withExpectedDistributorConfigStatus(DistributorStatus.BILLABLE)
       .withExpectedDistributorPaymentStatus(DistributorPaymentStatus.UP_TO_DATE)
       .withExpectedCustomerConfigStatus(CustomerStatus.BILLABLE)
       .withExpectedCustomerPaymentStatus(CustomerPaymentStatus.UP_TO_DATE)
       .withExpectedBuildingConfigStatus(BuildingStatus.ACTIVE)
       .withExpectedBuildingPaymentStatus(BuildingPaymentStatus.UP_TO_DATE)
       .withExpectedCustomerCount(1)
       .withExpectedBuildingCount(1)
       .withExpectedBuildingMappedPointCount(numPointsToMap)
       .withExpectedBuildingPendingDeletion(true)
       .build()); 

   
   // ======================================================================================================
   currentDate = "2022-10-07";
   buildingShouldNoLongerExist = true;
   fastForwardAnEvaluate(currentDate);
   System.err.println("\n" + currentDate + ": UC #" + (useCaseIndex++) + ": CURRENT PAYMENT INTERVAL EXPIRES AND BUILDING IS HARD DELETED, CUSTOMER/DISTRIBUTOR CHANGED TO CREATED STATUS (SYSTEM)");
   
   assertState(AssertionHolder
       .builder()
       .withExpectedDistributorConfigStatus(DistributorStatus.CREATED)
       .withExpectedDistributorPaymentStatus(DistributorPaymentStatus.UP_TO_DATE)
       .withExpectedCustomerConfigStatus(CustomerStatus.CREATED)
       .withExpectedCustomerPaymentStatus(CustomerPaymentStatus.UP_TO_DATE)
       .withExpectedBuildingConfigStatus(null)
       .withExpectedBuildingPaymentStatus(null)
       .withExpectedCustomerCount(1)
       .withExpectedBuildingCount(0)
       .withExpectedBuildingMappedPointCount(0)
       .withExpectedBuildingPendingDeletion(false)
       .build());
 }
  
 @Test
 public void test_gracePeriodExpirationWarningEmail() throws Exception {
   
   // ======================================================================================================
   currentDate = "2020-01-01";
   TEST_TIME_KEEPER.setCurrentDate(currentDate);
   System.err.println("\n" + currentDate + ": UC #" + (useCaseIndex++) + ": CREATE DISTRIBUTOR (SYNERGY)");
   
   onlineDistributor = (OnlineDistributorEntity)distributorService.createDistributor(
       RESOLUTE_DISTRIBUTOR_ID, 
       DistributorType.ONLINE,
       distributorName,
       UnitSystem.IP.toString(),
       false);
   
   onlineDistributorId = onlineDistributor.getPersistentIdentity();
   System.err.println("Created online distributor: " + onlineDistributor + " with id: " + onlineDistributorId);
   
   loadAndEvaluateObjects();
   assertState(AssertionHolder
       .builder()
       .withExpectedDistributorConfigStatus(DistributorStatus.CREATED)
       .withExpectedDistributorPaymentStatus(DistributorPaymentStatus.UP_TO_DATE)
       .withExpectedCustomerConfigStatus(null)
       .withExpectedCustomerPaymentStatus(null)
       .withExpectedBuildingConfigStatus(null)
       .withExpectedBuildingPaymentStatus(null)
       .withExpectedCustomerCount(0)
       .withExpectedBuildingCount(0)
       .withExpectedBuildingMappedPointCount(0)
       .withExpectedBuildingPendingDeletion(false)
       .build());    
   
   
   // ======================================================================================================
   System.err.println("\n" + currentDate + ": UC #" + (useCaseIndex++) + ": CREATE ACCOUNT MANAGER FOR ONLINE DISTRIBUTOR (SYNERGY)");
   
   accountManager = distributorService.createDistributorUser(
       onlineDistributor, 
       userRoleType, 
       email, 
       firstName, 
       lastName, 
       isAccountManager);
   
   accountManagerId = accountManager.getPersistentIdentity();
   System.err.println("Created account manager: " + accountManager + " with id: " + accountManagerId);
   loadAndEvaluateObjects();
   
   
   // ======================================================================================================
   System.err.println("\n" + currentDate + ": UC #" + (useCaseIndex++) + ": CREATE ONLINE CUSTOMER (SYNERGY)");
   
   onlineCustomer = (OnlineCustomerEntity)customerService.createCustomer(
       onlineDistributor, 
       CustomerType.ONLINE,
       customerName,
       UnitSystem.IP.toString());
   
   onlineCustomerId = onlineCustomer.getPersistentIdentity();
   System.err.println("Created customer: " + onlineCustomer + " with id: " + onlineCustomerId);
   
   loadAndEvaluateObjects();
   assertState(AssertionHolder
       .builder()
       .withExpectedDistributorConfigStatus(DistributorStatus.CREATED)
       .withExpectedDistributorPaymentStatus(DistributorPaymentStatus.UP_TO_DATE)
       .withExpectedCustomerConfigStatus(CustomerStatus.CREATED)
       .withExpectedCustomerPaymentStatus(CustomerPaymentStatus.UP_TO_DATE)
       .withExpectedBuildingConfigStatus(null)
       .withExpectedBuildingPaymentStatus(null)
       .withExpectedCustomerCount(1)
       .withExpectedBuildingCount(0)
       .withExpectedBuildingMappedPointCount(0)
       .withExpectedBuildingPendingDeletion(false)
       .build()); 
   
   
   // ======================================================================================================
   System.err.println("\n" + currentDate + ": UC #" + (useCaseIndex++) + ": CREATE PORTFOLIO FOR ONLINE CUSTOMER (AUTOMATIC IN SYNERGY)");
   
   portfolio = nodeHierarchyService.createPortfolio(
       onlineCustomer, 
       portfolioName, 
       displayName);
   
   portfolioId = portfolio.getPersistentIdentity();
   System.err.println("Created portfolio node: " + portfolio + " with id: " + portfolioId);

   // This is to simulate ingestion of raw points from a cloudfill connector.
   String mappingExpression1 = "/Drivers/NiagaraNetwork/{building}/{point}";
   List<RawPointEntity> rawPoints = Arrays.asList(
       buildMockRawPoint(onlineCustomerId, "/Drivers/NiagaraNetwork/Building_1/Point_1"),
       buildMockRawPoint(onlineCustomerId, "/Drivers/NiagaraNetwork/Building_1/Point_2"),
       buildMockRawPoint(onlineCustomerId, "/Drivers/NiagaraNetwork/Building_1/Point_3"));
   onlineCustomer.addRawPoints(rawPoints);
   boolean storeRawPoints = true;
   customerService.updateCustomer(onlineCustomer, storeRawPoints);
   
   
   // ======================================================================================================
   fastForwardAnEvaluate("2020-01-06");
   System.err.println("\n" + currentDate + ": UC #" + (useCaseIndex++) + ": MAP RAW POINTS (SYNERGY)");

   // The building will be created first, then the three mappable points.
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
       .withCustomerId(onlineCustomerId)
       .withRawPoints(rawPointData)
       .withMappingExpression(mappingExpression1)
       .withMetricIdDelimiter(NodeHierarchyService.DEFAULT_METRIC_ID_DELIMITER)
       .build(); 
   List<MappablePointEntity> createdMappablePoints = nodeHierarchyService.mapRawPoints(mapRawPointsRequest);

   // Get the one and only billable building that was the result of the mapping.
   Assert.assertTrue("created mappable points list size is incorrect", createdMappablePoints.size() == 3);
   MappablePointEntity mappablePoint = createdMappablePoints.get(0);
   BuildingEntity building = mappablePoint.getAncestorBuilding();
   Assert.assertTrue("building is not of type BillableBuildingEntity", building instanceof BillableBuildingEntity);
   
   billableBuilding = (BillableBuildingEntity)building;
   billableBuildingId = billableBuilding.getPersistentIdentity();
   System.err.println("Created building node with id: " + billableBuildingId);
   
   loadAndEvaluateObjects();
   assertState(AssertionHolder
       .builder()
       .withExpectedDistributorConfigStatus(DistributorStatus.CREATED)
       .withExpectedDistributorPaymentStatus(DistributorPaymentStatus.UP_TO_DATE)
       .withExpectedCustomerConfigStatus(CustomerStatus.CREATED)
       .withExpectedCustomerPaymentStatus(CustomerPaymentStatus.UP_TO_DATE)
       .withExpectedBuildingConfigStatus(BuildingStatus.PENDING_ACTIVATION)
       .withExpectedBuildingPaymentStatus(BuildingPaymentStatus.UP_TO_DATE)
       .withExpectedCustomerCount(1)
       .withExpectedBuildingCount(1)
       .withExpectedBuildingMappedPointCount(3)
       .withExpectedBuildingPendingDeletion(false)
       .build());
   
   
   // ======================================================================================================
   currentDate = "2020-02-04"; // The grace period ends on 2020-02-06, as it started on 2020-01-06
   fastForwardAnEvaluate(currentDate);
   System.err.println("\n" + currentDate + ": UC #" + (useCaseIndex++) + ": BUILDING GRACE PERIOD HAS EXPIRED AND WARNING EMAIL SENT (SYSTEM)");
   
   loadAndEvaluateObjects();
   assertState(AssertionHolder
       .builder()
       .withExpectedDistributorConfigStatus(DistributorStatus.CREATED)
       .withExpectedDistributorPaymentStatus(DistributorPaymentStatus.UP_TO_DATE)
       .withExpectedCustomerConfigStatus(CustomerStatus.CREATED)
       .withExpectedCustomerPaymentStatus(CustomerPaymentStatus.UP_TO_DATE)
       .withExpectedBuildingConfigStatus(BuildingStatus.PENDING_ACTIVATION)
       .withExpectedBuildingPaymentStatus(BuildingPaymentStatus.UP_TO_DATE)
       .withExpectedCustomerCount(1)
       .withExpectedBuildingCount(1)
       .withExpectedBuildingMappedPointCount(3)
       .withExpectedBuildingPendingDeletion(false)
       .build());
   List<EmailDto> sentEmails = emailClient.getSentEmails(accountManager.getEmail());
   Assert.assertNotNull("sent emails for account manager are null", sentEmails);
   Assert.assertTrue("sent emails size for account manager are null", sentEmails.size() == 1);
   EmailDto emailDto = sentEmails.get(0);
   System.err.println("EMAIL: " + emailDto);
   Assert.assertTrue("email doesn't contain account manager name", emailDto.getBody().contains(accountManager.getFirstName()));
   Assert.assertTrue("email doesn't contain building name", emailDto.getBody().contains(billableBuilding.getDisplayName()));
 }
 
 @Test
 public void test_hardDelete_demoCustomer_noPendingActivation() throws Exception {

   // ====================================================================================================================================
   // Verify that demo customer with no buildings and its distributor are soft deleted after 30 days, then hard deleted 30 days after that.
   currentDate = "2020-01-01";
   TEST_TIME_KEEPER.setCurrentDate(currentDate, true);
   
   AbstractDistributorEntity distributor = distributorService.createDistributor(
       RESOLUTE_DISTRIBUTOR_ID, 
       DistributorType.ONLINE,
       "OnlineDistributor",
       UnitSystem.IP.toString(),
       false);
       
   Integer distributorId = distributor.getPersistentIdentity();
   distributor = distributorService.loadDistributor(
       distributorId,
       false,  // loadDistributorPaymentMethods
       false); // loadDistributorUsers
   
   AbstractCustomerEntity demoCustomer = customerService.createDemoCustomer(
       distributor, 
       "DemoCustomer", 
       Boolean.TRUE, 
       Boolean.TRUE);
   
   Integer demoCustomerId = demoCustomer.getPersistentIdentity();
   demoCustomer = customerService.loadCustomer(
       demoCustomerId,
       false,  // loadDistributorPaymentMethods
       false); // loadDistributorUsers
   nodeHierarchyService.createPortfolio(demoCustomer, "DemoCustomerPortfolio", "DemoCustomerPortfolio");

   
   // Verify soft deletion.
   currentDate = "2020-04-02";
   TEST_TIME_KEEPER.setCurrentDate(currentDate, true);
   performPortfolioMaintenanceWithNightlyProcessing(demoCustomerId);
   demoCustomer = customerService.loadCustomer(
       demoCustomerId,
       false,  // loadDistributorPaymentMethods
       false); // loadDistributorUsers
   Assert.assertEquals("customer is not in soft deleted status", 
       CustomerStatus.DELETED, 
       demoCustomer.getCustomerStatus());
   
   
   // Verify hard deletion of customer.
   currentDate = "2020-05-03";
   TEST_TIME_KEEPER.setCurrentDate(currentDate, true);
   performPortfolioMaintenanceWithNightlyProcessing(demoCustomerId);
   try {
     demoCustomer = customerService.loadCustomer(
         demoCustomerId,
         false,  // loadDistributorPaymentMethods
         false); // loadDistributorUsers
     throw new RuntimeException("Demo customer with no buildings not hard deleted as expected");
   } catch (EntityDoesNotExistException ednee) {
     System.err.println("Demo customer with no buildings hard deleted as expected");
   }
   
   
   // Verify hard deletion of distributor.
   try {
     distributor = distributorService.loadDistributor(
         distributorId,
         false,  // loadDistributorPaymentMethods
         false); // loadDistributorUsers
     throw new RuntimeException("Online distributor not hard deleted as expected");
   } catch (EntityDoesNotExistException ednee) {
     System.err.println("Online distributor hard deleted as expected");
   }
   
   
   AbstractDistributorEntity rootDistributor = distributorService.loadDistributor(
       RESOLUTE_DISTRIBUTOR_ID,
       false,  // loadDistributorPaymentMethods
       false); // loadDistributorUsers
   try {
     rootDistributor.getChildDistributor(distributorId);
     throw new RuntimeException("Online distributor not deleted as expected");
   } catch (EntityDoesNotExistException ednee) {
     System.err.println("Online distributor deleted as expected");
   }   
 }
 
 @Test
 public void test_softDelete_exception_onlineCustomer_pending_activation_building() throws Exception {

   // ====================================================================================================================================
   // Verify that demo customer with no buildings and its distributor are soft deleted after 30 days, then hard deleted 30 days after that.
   currentDate = "2020-01-01";
   TEST_TIME_KEEPER.setCurrentDate(currentDate, true);
   
   onlineDistributor = (OnlineDistributorEntity)distributorService.createDistributor(
       RESOLUTE_DISTRIBUTOR_ID, 
       DistributorType.ONLINE,
       "OnlineDistributor",
       UnitSystem.IP.toString(),
       false);
       
   Integer distributorId = onlineDistributor.getPersistentIdentity();
   onlineDistributor = (OnlineDistributorEntity)distributorService.loadDistributor(
       distributorId,
       true,  // loadDistributorPaymentMethods
       true); // loadDistributorUsers
   
   accountManager = distributorService.createDistributorUser(
       onlineDistributor, 
       userRoleType, 
       email, 
       firstName, 
       lastName, 
       isAccountManager);
   
   accountManagerId = accountManager.getPersistentIdentity();
   System.err.println("Created account manager: " + accountManager + " with id: " + accountManagerId);
   loadAndEvaluateObjects();
   
   onlineCustomer = (OnlineCustomerEntity)customerService.createCustomer(
       onlineDistributor, 
       CustomerType.ONLINE,
       customerName,
       UnitSystem.IP.toString());
   
   onlineCustomerId = onlineCustomer.getPersistentIdentity();
   System.err.println("Created customer: " + onlineCustomer + " with id: " + onlineCustomerId);
   
   nodeHierarchyService.createPortfolio(onlineCustomer, "OnlineCustomerPortfolio", "OnlineCustomerPortfolio");
   
   
   // Create a subscription for the building
   //
   // This is to simulate ingestion of raw points from a cloudfill connector.
   int numPointsToMap = 3;
   String mappingExpression1 = "/Drivers/NiagaraNetwork/{building}/{subBuilding}/{floor}/{equipment}/{point}";
   String metricIdPattern = "/Drivers/NiagaraNetwork/Building_1/SubBuilding_1/Floor_1/Equipment_1/Point_Y";
   List<RawPointEntity> rawPoints = new ArrayList<>();
   for (int i=1; i <= numPointsToMap; i++) {
     
     String metricId = metricIdPattern
         .replace("Y", Integer.toString(i));
    
     rawPoints.add(buildMockRawPoint(onlineCustomerId, metricId));
   }
   onlineCustomer.addRawPoints(rawPoints);
   boolean storeRawPoints = true;
   customerService.updateCustomer(onlineCustomer, storeRawPoints);

   // The building will be created first, then the three mappable points.
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
       .withCustomerId(onlineCustomerId)
       .withRawPoints(rawPointData)
       .withMappingExpression(mappingExpression1)
       .withMetricIdDelimiter(NodeHierarchyService.DEFAULT_METRIC_ID_DELIMITER)
       .build(); 
   List<MappablePointEntity> createdMappablePoints = nodeHierarchyService.mapRawPoints(mapRawPointsRequest);

   // Get the one and only billable building that was the result of the mapping.
   Assert.assertTrue("created mappable points list size is incorrect", createdMappablePoints.size() == numPointsToMap);
   MappablePointEntity mappablePoint = createdMappablePoints.get(0);
   BuildingEntity building = mappablePoint.getAncestorBuilding();
   Assert.assertTrue("building is not of type BillableBuildingEntity", building instanceof BillableBuildingEntity);
   
   billableBuilding = (BillableBuildingEntity)building;
   billableBuildingId = billableBuilding.getPersistentIdentity();
   System.err.println("Created building node with id: " + billableBuildingId);
   
   portfolio = (PortfolioEntity)billableBuilding.getParentNode();
   portfolioId = portfolio.getPersistentIdentity();
   
   loadAndEvaluateObjects();
   assertState(AssertionHolder
       .builder()
       .withExpectedDistributorConfigStatus(DistributorStatus.CREATED)
       .withExpectedDistributorPaymentStatus(DistributorPaymentStatus.UP_TO_DATE)
       .withExpectedCustomerConfigStatus(CustomerStatus.CREATED)
       .withExpectedCustomerPaymentStatus(CustomerPaymentStatus.UP_TO_DATE)
       .withExpectedBuildingConfigStatus(BuildingStatus.PENDING_ACTIVATION)
       .withExpectedBuildingPaymentStatus(BuildingPaymentStatus.UP_TO_DATE)
       .withExpectedCustomerCount(1)
       .withExpectedBuildingCount(1)
       .withExpectedBuildingMappedPointCount(numPointsToMap)
       .withExpectedBuildingPendingDeletion(false)
       .build());

   
   // Verify soft deletion did not occur because of an exception to the rule.
   currentDate = "2020-04-02";
   TEST_TIME_KEEPER.setCurrentDate(currentDate, true);
   performPortfolioMaintenanceWithNightlyProcessing(onlineCustomerId);
   onlineCustomer = (OnlineCustomerEntity)customerService.loadCustomer(
       onlineCustomerId,
       false,  // loadDistributorPaymentMethods
       false); // loadDistributorUsers
   Assert.assertEquals("customer is not in CREATED status", 
       CustomerStatus.CREATED, 
       onlineCustomer.getCustomerStatus());
 }
 
 @Test
 public void test_softDelete_exception_onlineCustomer_delinquent_building() throws Exception {

   // ====================================================================================================================================
   // Verify that demo customer with no buildings and its distributor are soft deleted after 30 days, then hard deleted 30 days after that.
   currentDate = "2020-01-01";
   TEST_TIME_KEEPER.setCurrentDate(currentDate, true);
   
   onlineDistributor = (OnlineDistributorEntity)distributorService.createDistributor(
       RESOLUTE_DISTRIBUTOR_ID, 
       DistributorType.ONLINE,
       "OnlineDistributor",
       UnitSystem.IP.toString(),
       false);
       
   Integer distributorId = onlineDistributor.getPersistentIdentity();
   onlineDistributor = (OnlineDistributorEntity)distributorService.loadDistributor(
       distributorId,
       true,  // loadDistributorPaymentMethods
       true); // loadDistributorUsers
   
   accountManager = distributorService.createDistributorUser(
       onlineDistributor, 
       userRoleType, 
       email, 
       firstName, 
       lastName, 
       isAccountManager);
   
   accountManagerId = accountManager.getPersistentIdentity();
   System.err.println("Created account manager: " + accountManager + " with id: " + accountManagerId);
   loadAndEvaluateObjects();
   
   onlineCustomer = (OnlineCustomerEntity)customerService.createCustomer(
       onlineDistributor, 
       CustomerType.ONLINE,
       customerName,
       UnitSystem.IP.toString());
   
   onlineCustomerId = onlineCustomer.getPersistentIdentity();
   System.err.println("Created customer: " + onlineCustomer + " with id: " + onlineCustomerId);
   
   nodeHierarchyService.createPortfolio(onlineCustomer, "OnlineCustomerPortfolio", "OnlineCustomerPortfolio");
   
   
   // Create a subscription for the building
   //
   // This is to simulate ingestion of raw points from a cloudfill connector.
   int numPointsToMap = 3;
   String mappingExpression1 = "/Drivers/NiagaraNetwork/{building}/{subBuilding}/{floor}/{equipment}/{point}";
   String metricIdPattern = "/Drivers/NiagaraNetwork/Building_1/SubBuilding_1/Floor_1/Equipment_1/Point_Y";
   List<RawPointEntity> rawPoints = new ArrayList<>();
   for (int i=1; i <= numPointsToMap; i++) {
     
     String metricId = metricIdPattern
         .replace("Y", Integer.toString(i));
    
     rawPoints.add(buildMockRawPoint(onlineCustomerId, metricId));
   }
   onlineCustomer.addRawPoints(rawPoints);
   boolean storeRawPoints = true;
   customerService.updateCustomer(onlineCustomer, storeRawPoints);

   // The building will be created first, then the three mappable points.
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
       .withCustomerId(onlineCustomerId)
       .withRawPoints(rawPointData)
       .withMappingExpression(mappingExpression1)
       .withMetricIdDelimiter(NodeHierarchyService.DEFAULT_METRIC_ID_DELIMITER)
       .build(); 
   List<MappablePointEntity> createdMappablePoints = nodeHierarchyService.mapRawPoints(mapRawPointsRequest);

   // Get the one and only billable building that was the result of the mapping.
   Assert.assertTrue("created mappable points list size is incorrect", createdMappablePoints.size() == numPointsToMap);
   MappablePointEntity mappablePoint = createdMappablePoints.get(0);
   BuildingEntity building = mappablePoint.getAncestorBuilding();
   Assert.assertTrue("building is not of type BillableBuildingEntity", building instanceof BillableBuildingEntity);
   
   billableBuilding = (BillableBuildingEntity)building;
   billableBuildingId = billableBuilding.getPersistentIdentity();
   System.err.println("Created building node with id: " + billableBuildingId);
   
   portfolio = (PortfolioEntity)billableBuilding.getParentNode();
   portfolioId = portfolio.getPersistentIdentity();
   
   loadAndEvaluateObjects();
   assertState(AssertionHolder
       .builder()
       .withExpectedDistributorConfigStatus(DistributorStatus.CREATED)
       .withExpectedDistributorPaymentStatus(DistributorPaymentStatus.UP_TO_DATE)
       .withExpectedCustomerConfigStatus(CustomerStatus.CREATED)
       .withExpectedCustomerPaymentStatus(CustomerPaymentStatus.UP_TO_DATE)
       .withExpectedBuildingConfigStatus(BuildingStatus.PENDING_ACTIVATION)
       .withExpectedBuildingPaymentStatus(BuildingPaymentStatus.UP_TO_DATE)
       .withExpectedCustomerCount(1)
       .withExpectedBuildingCount(1)
       .withExpectedBuildingMappedPointCount(numPointsToMap)
       .withExpectedBuildingPendingDeletion(false)
       .build());

   
   // ======================================================================================================
   // We need to simulate the workflow for adding a payment method, so we just mimic that
   // interaction with the front end that supplies us with the payment method info, including
   // the Stripe source id (a.k.a. Stripe payment method id)
   String cardExpiry = "02/2020";
   String cardLastFour = "1234";
   String stripeSourceId = ((MockStripeClient)stripeClient).createStripePaymentMethod(
       onlineDistributor.getStripeCustomerId(), 
       cardExpiry, 
       cardLastFour);
   
   CreatePaymentMethodRequest request = CreatePaymentMethodRequest
       .builder()
       .withParentDistributorId(onlineDistributor.getPersistentIdentity())
       .withPaymentMethodType(PaymentMethodType.CREDIT_CARD.getName())
       .withName("name")
       .withStripeSourceId(stripeSourceId)
       .withAccountHolderName("accountHolderName")
       .withAddress("address")
       .withCity("city")
       .withState("state")
       .withZipCode("zipCode")
       .withPhoneNumber("phoneNumber")
       .withCardBrand("Visa")
       .withCardExpiry(cardExpiry)
       .withCardLastFour(cardLastFour)
       .build();

   
   // ======================================================================================================
   System.err.println("\n" + currentDate + ": UC #" + (useCaseIndex++) + ": CREATE PAYMENT METHOD (SYNERGY)");
   AbstractPaymentMethodEntity paymentMethod = distributorService.createPaymentMethod(onlineDistributor, request);
   Integer paymentMethodId = paymentMethod.getPersistentIdentity();
   
   Integer paymentMethodIdToUse = paymentMethod.getPersistentIdentity();
   System.err.println("Created payment method: " + paymentMethod + " with id: " + paymentMethodId);
   
   
   // ======================================================================================================
   fastForwardAnEvaluate("2020-01-07");
   System.err.println("\n" + currentDate + ": UC #" + (useCaseIndex++) + ": GET THE LOWEST COST PER INTERVAL QUALIFYING MONTHLY PAYMENT PLAN - MONTHLY/3000 - (SYNERGY)");
   PaymentPlanEntity paymentPlan = dictionaryService.getLowestCostPerIntervalQualifyingPaymentPlan(mappedPointCount);
   Integer paymentPlanId = paymentPlan.getPersistentIdentity();
   Integer paymentPlanIdToUse = paymentPlan.getPersistentIdentity();
   System.err.println("Retrieved lowest cost per interval qualifying payment plan: " + paymentPlan + " with id: " + paymentPlanId);
   
   
   // ======================================================================================================
   System.err.println("\n" + currentDate + ": UC #" + (useCaseIndex++) + ": BUILDING IS ACTIVATED BY SUBSCRIPTION CREATION WITH SUCCESSFUL PAYMENT METHOD - MONTHLY/3000 - (SYNERGY)");
   buildingSubscription = nodeHierarchyService.createBuildingSubscription(
       onlineCustomerId,
       billableBuildingId, 
       paymentPlanIdToUse, 
       paymentMethodIdToUse);
   
   buildingSubscriptionId = buildingSubscription.getPersistentIdentity();
   System.err.println("Created building subscription: " + buildingSubscription + " with id: " + buildingSubscriptionId);
   
   loadAndEvaluateObjects();
   assertState(AssertionHolder
       .builder()
       .withExpectedDistributorConfigStatus(DistributorStatus.BILLABLE)
       .withExpectedDistributorPaymentStatus(DistributorPaymentStatus.UP_TO_DATE)
       .withExpectedCustomerConfigStatus(CustomerStatus.BILLABLE)
       .withExpectedCustomerPaymentStatus(CustomerPaymentStatus.UP_TO_DATE)
       .withExpectedBuildingConfigStatus(BuildingStatus.ACTIVE)
       .withExpectedBuildingPaymentStatus(BuildingPaymentStatus.UP_TO_DATE)
       .withExpectedCustomerCount(1)
       .withExpectedBuildingCount(1)
       .withExpectedBuildingMappedPointCount(numPointsToMap)
       .withExpectedBuildingPendingDeletion(false)
       .build());
   
   
   // ======================================================================================================
   fastForwardAnEvaluate("2020-03-07");
   System.err.println("\n" + currentDate + ": UC #" + (useCaseIndex++) + ": RENEWAL OF SUBSCRIPTION WITH FAILED PAYMENT - MONTHLY/3000 - (1ST FAILED PAYMENT)");
   
   assertState(AssertionHolder
       .builder()
       .withExpectedDistributorConfigStatus(DistributorStatus.BILLABLE)
       .withExpectedDistributorPaymentStatus(DistributorPaymentStatus.DELINQUENT)
       .withExpectedCustomerConfigStatus(CustomerStatus.BILLABLE)
       .withExpectedCustomerPaymentStatus(CustomerPaymentStatus.DELINQUENT)
       .withExpectedBuildingConfigStatus(BuildingStatus.ACTIVE)
       .withExpectedBuildingPaymentStatus(BuildingPaymentStatus.DELINQUENT)
       .withExpectedCustomerCount(1)
       .withExpectedBuildingCount(1)
       .withExpectedBuildingMappedPointCount(numPointsToMap)
       .withExpectedBuildingPendingDeletion(false)
       .build());   

   
   // Verify soft deletion did not occur because of an exception to the rule.
   currentDate = "2020-04-02";
   TEST_TIME_KEEPER.setCurrentDate(currentDate, true);
   performPortfolioMaintenanceWithNightlyProcessing(onlineCustomerId);
   onlineCustomer = (OnlineCustomerEntity)customerService.loadCustomer(
       onlineCustomerId,
       false,  // loadDistributorPaymentMethods
       false); // loadDistributorUsers
   Assert.assertEquals("customer is not in BILLABLE status", 
       CustomerStatus.BILLABLE, 
       onlineCustomer.getCustomerStatus());
 } 
 
 @Test
 public void test_softDelete_exception_demoCustomer_descendant_distributor_not_soft_deleted() throws Exception {

   // ====================================================================================================================================
   // Verify that demo customer with no buildings and its distributor are soft deleted after 30 days, then hard deleted 30 days after that.
   currentDate = "2020-01-01";
   TEST_TIME_KEEPER.setCurrentDate(currentDate, true);
   
   AbstractDistributorEntity distributor = distributorService.createDistributor(
       RESOLUTE_DISTRIBUTOR_ID, 
       DistributorType.ONLINE,
       "OnlineDistributor",
       UnitSystem.IP.toString(),
       false);
       
   Integer distributorId = distributor.getPersistentIdentity();
   distributor = distributorService.loadDistributor(
       distributorId,
       false,  // loadDistributorPaymentMethods
       false); // loadDistributorUsers
   System.err.println("distributorId: " + distributorId);
   
   AbstractCustomerEntity demoCustomer = customerService.createDemoCustomer(
       distributor, 
       "DemoCustomer", 
       Boolean.TRUE, 
       Boolean.TRUE);
   
   Integer demoCustomerId = demoCustomer.getPersistentIdentity();
   demoCustomer = customerService.loadCustomer(
       demoCustomerId,
       false,  // loadDistributorPaymentMethods
       false); // loadDistributorUsers
   nodeHierarchyService.createPortfolio(demoCustomer, "DemoCustomerPortfolio", "DemoCustomerPortfolio");

   
   // Verify soft deletion does not occur because of an exception to the rules.
   currentDate = "2020-04-02";
   TEST_TIME_KEEPER.setCurrentDate(currentDate, true);
   
   distributorService.createDistributor(
       distributorId, 
       DistributorType.ONLINE,
       "ChildDistributor",
       UnitSystem.IP.toString(),
       false);
   
   distributor = distributorService.loadDistributor(
       distributorId,
       false,  // loadDistributorPaymentMethods
       false); // loadDistributorUsers
   Assert.assertNotEquals("distributor should not be in DELETED status", 
       CustomerStatus.DELETED, 
       distributor.getDistributorStatus());
 }
 
 @Test
 public void test_softDelete_exception_demoCustomer_demo_expires_is_false() throws Exception {

   // ====================================================================================================================================
   // Verify that demo customer with no buildings and its distributor are soft deleted after 30 days, then hard deleted 30 days after that.
   currentDate = "2020-01-01";
   TEST_TIME_KEEPER.setCurrentDate(currentDate, true);
   
   AbstractDistributorEntity distributor = distributorService.createDistributor(
       RESOLUTE_DISTRIBUTOR_ID, 
       DistributorType.ONLINE,
       "OnlineDistributor",
       UnitSystem.IP.toString(),
       false);
       
   Integer distributorId = distributor.getPersistentIdentity();
   distributor = distributorService.loadDistributor(
       distributorId,
       false,  // loadDistributorPaymentMethods
       false); // loadDistributorUsers
   
   AbstractCustomerEntity demoCustomer = customerService.createDemoCustomer(
       distributor, 
       "DemoCustomer", 
       Boolean.FALSE, 
       Boolean.FALSE);
   
   Integer demoCustomerId = demoCustomer.getPersistentIdentity();
   demoCustomer = customerService.loadCustomer(
       demoCustomerId,
       false,  // loadDistributorPaymentMethods
       false); // loadDistributorUsers
   nodeHierarchyService.createPortfolio(demoCustomer, "DemoCustomerPortfolio", "DemoCustomerPortfolio");

   
   // Verify soft deletion did not occur because of an exception to the rule.
   currentDate = "2020-04-02";
   TEST_TIME_KEEPER.setCurrentDate(currentDate, true);
   performPortfolioMaintenanceWithNightlyProcessing(onlineCustomerId);
   demoCustomer = customerService.loadCustomer(
       demoCustomerId,
       false,  // loadDistributorPaymentMethods
       false); // loadDistributorUsers
   Assert.assertEquals("customer is not in CREATED status", 
       CustomerStatus.CREATED, 
       demoCustomer.getCustomerStatus());   
 }
 
 private void performPortfolioMaintenanceWithNightlyProcessing(Integer customerId) throws Exception {
   
   boolean performStripePaymentProcessing = true;
   
   List<String> errors = nodeHierarchyService.performPortfolioMaintenance(
       Arrays.asList(customerId),
       distributorHierarchyStateEvaluator,
       performStripePaymentProcessing);   
   
   Assert.assertTrue("result is incorrect", errors.isEmpty());
 }
 
 @Test
 public void scenario_1_1_1_grace_period_expiration_then_delete() throws Exception {

   // ======================================================================================================
   currentDate = "2020-01-01";
   TEST_TIME_KEEPER.setCurrentDate(currentDate);
   System.err.println(currentDate + ": CREATE DISTRIBUTOR (SYNERGY)");
   onlineDistributor = (OnlineDistributorEntity)distributorService.createDistributor(
       RESOLUTE_DISTRIBUTOR_ID, 
       DistributorType.ONLINE,
       distributorName,
       UnitSystem.IP.toString(),
       false);
   
   onlineDistributorId = onlineDistributor.getPersistentIdentity();
   System.err.println("Created online distributor: " + onlineDistributor + " with id: " + onlineDistributorId);
   
   loadAndEvaluateObjects();
   assertState(AssertionHolder
       .builder()
       .withExpectedDistributorConfigStatus(DistributorStatus.CREATED)
       .withExpectedDistributorPaymentStatus(DistributorPaymentStatus.UP_TO_DATE)
       .withExpectedCustomerConfigStatus(null)
       .withExpectedCustomerPaymentStatus(null)
       .withExpectedBuildingConfigStatus(null)
       .withExpectedBuildingPaymentStatus(null)
       .withExpectedCustomerCount(0)
       .withExpectedBuildingCount(0)
       .withExpectedBuildingMappedPointCount(0)
       .withExpectedBuildingPendingDeletion(false)
       .build());    

   
   // ======================================================================================================
   System.err.println(currentDate + ": CREATE ACCOUNT MANAGER FOR ONLINE DISTRIBUTOR (SYNERGY)");
   accountManager = distributorService.createDistributorUser(
       onlineDistributor, 
       userRoleType, 
       email, 
       firstName, 
       lastName, 
       isAccountManager);
   
   accountManagerId = accountManager.getPersistentIdentity();
   System.err.println("Created account manager: " + accountManager + " with id: " + accountManagerId);
   loadAndEvaluateObjects();
   
   
   // ======================================================================================================
   System.err.println(currentDate + ": CREATE ONLINE CUSTOMER (SYNERGY)");
   onlineCustomer = (OnlineCustomerEntity)customerService.createCustomer(
       onlineDistributor, 
       CustomerType.ONLINE,
       customerName,
       UnitSystem.IP.toString());
   
   onlineCustomerId = onlineCustomer.getPersistentIdentity();
   System.err.println("Created customer: " + onlineCustomer + " with id: " + onlineCustomerId);
   
   loadAndEvaluateObjects();
   assertState(AssertionHolder
       .builder()
       .withExpectedDistributorConfigStatus(DistributorStatus.CREATED)
       .withExpectedDistributorPaymentStatus(DistributorPaymentStatus.UP_TO_DATE)
       .withExpectedCustomerConfigStatus(CustomerStatus.CREATED)
       .withExpectedCustomerPaymentStatus(CustomerPaymentStatus.UP_TO_DATE)
       .withExpectedBuildingConfigStatus(null)
       .withExpectedBuildingPaymentStatus(null)
       .withExpectedCustomerCount(1)
       .withExpectedBuildingCount(0)
       .withExpectedBuildingMappedPointCount(0)
       .withExpectedBuildingPendingDeletion(false)
       .build()); 
   
   
   // ======================================================================================================
   System.err.println(currentDate + ": CREATE PORTFOLIO FOR ONLINE CUSTOMER (AUTOMATIC IN SYNERGY)");
   portfolio = nodeHierarchyService.createPortfolio(
       onlineCustomer, 
       portfolioName, 
       displayName);
   
   portfolioId = portfolio.getPersistentIdentity();
   System.err.println("Created portfolio node: " + portfolio + " with id: " + portfolioId);

   
   // ======================================================================================================
   // ======================================================================================================
   // This is to simulate ingestion of raw points from a cloudfill connector.
   int numPointsToMap = 3;
   String mappingExpression1 = "/Drivers/NiagaraNetwork/{building}/{subBuilding}/{floor}/{equipment}/{point}";
   String metricIdPattern = "/Drivers/NiagaraNetwork/Building_1/SubBuilding_1/Floor_1/Equipment_1/Point_Y";
   List<RawPointEntity> rawPoints = new ArrayList<>();
   for (int i=1; i <= numPointsToMap; i++) {
     
     String metricId = metricIdPattern
         .replace("Y", Integer.toString(i));
    
     rawPoints.add(buildMockRawPoint(onlineCustomerId, metricId));
   }
   onlineCustomer.addRawPoints(rawPoints);
   boolean storeRawPoints = true;
   customerService.updateCustomer(onlineCustomer, storeRawPoints);
   // ======================================================================================================
   // ======================================================================================================
   
   
   // ======================================================================================================
   System.err.println(currentDate + ": MAP RAW POINTS (SYNERGY)");
   loadObjectsOnly();
   // The building will be created first, then the three mappable points.
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
       .withCustomerId(onlineCustomerId)
       .withRawPoints(rawPointData)
       .withMappingExpression(mappingExpression1)
       .withMetricIdDelimiter(NodeHierarchyService.DEFAULT_METRIC_ID_DELIMITER)
       .build(); 
   List<MappablePointEntity> createdMappablePoints = nodeHierarchyService.mapRawPoints(mapRawPointsRequest);

   // Get the one and only billable building that was the result of the mapping.
   Assert.assertTrue("created mappable points list size is incorrect", createdMappablePoints.size() == numPointsToMap);
   MappablePointEntity mappablePoint = createdMappablePoints.get(0);
   BuildingEntity building = mappablePoint.getAncestorBuilding();
   Assert.assertTrue("building is not of type BillableBuildingEntity", building instanceof BillableBuildingEntity);
   
   billableBuilding = (BillableBuildingEntity)building;
   billableBuildingId = billableBuilding.getPersistentIdentity();
   System.err.println("Created building node with id: " + billableBuildingId);
   
   loadAndEvaluateObjects();
   assertState(AssertionHolder
       .builder()
       .withExpectedDistributorConfigStatus(DistributorStatus.CREATED)
       .withExpectedDistributorPaymentStatus(DistributorPaymentStatus.UP_TO_DATE)
       .withExpectedCustomerConfigStatus(CustomerStatus.CREATED)
       .withExpectedCustomerPaymentStatus(CustomerPaymentStatus.UP_TO_DATE)
       .withExpectedBuildingConfigStatus(BuildingStatus.PENDING_ACTIVATION)
       .withExpectedBuildingPaymentStatus(BuildingPaymentStatus.UP_TO_DATE)
       .withExpectedCustomerCount(1)
       .withExpectedBuildingCount(1)
       .withExpectedBuildingMappedPointCount(numPointsToMap)
       .withExpectedBuildingPendingDeletion(false)
       .build());

   
   // ======================================================================================================
   fastForwardAnEvaluate("2020-01-31");
   System.err.println(currentDate + ": BUILDING GRACE PERIOD HAS EXPIRED (SYSTEM)");
   
   assertState(AssertionHolder
       .builder()
       .withExpectedDistributorConfigStatus(DistributorStatus.CREATED)
       .withExpectedDistributorPaymentStatus(DistributorPaymentStatus.DELINQUENT)
       .withExpectedCustomerConfigStatus(CustomerStatus.CREATED)
       .withExpectedCustomerPaymentStatus(CustomerPaymentStatus.DELINQUENT)
       .withExpectedBuildingConfigStatus(BuildingStatus.PENDING_ACTIVATION)
       .withExpectedBuildingPaymentStatus(BuildingPaymentStatus.DELINQUENT)
       .withExpectedCustomerCount(1)
       .withExpectedBuildingCount(1)
       .withExpectedBuildingMappedPointCount(numPointsToMap)
       .withExpectedBuildingPendingDeletion(false)
       .build());
   
   /* TODO: TDM: Refactor the code to have the mail client have the mail JDBC dao be injected so that it
    * is written to the DB, so that the mock email client only sends the mail ONCE.
    */
   // Verify that the grace period expiration warning email was sent as well.
   List<EmailDto> sentEmails = emailClient.getSentEmails(accountManager.getEmail());
   Assert.assertNotNull("sent emails for account manager are null", sentEmails);
   Assert.assertTrue("sent emails size for account manager are null", sentEmails.size() > 0); // TODO: TDM: Should be exactly 1.
   EmailDto emailDto = sentEmails.get(0);
   System.err.println("EMAIL: " + emailDto);
   Assert.assertTrue("email doesn't contain account manager name", emailDto.getBody().contains(accountManager.getFirstName()));
   Assert.assertTrue("email doesn't contain building name", emailDto.getBody().contains(billableBuilding.getDisplayName()));

   // ======================================================================================================
   // Below is done regardless of whether customer is CREATED or DELETED
   // ======================================================================================================
   currentDate = "2023-03-15";
   distributorShouldNoLongerExist = true;
   customerShouldNoLongerExist = true;
   buildingShouldNoLongerExist = true;
   fastForwardAnEvaluate(currentDate);
   System.err.println("\n" + currentDate + ": UC #" + (useCaseIndex++) + ": SOFT DELETED DISTRIBUTOR IS HARD DELETED AFTER 30 DAYS (SYSTEM) - BACK TO GROUND STATE");
   
   assertState(AssertionHolder
       .builder()
       .withExpectedDistributorConfigStatus(null)
       .withExpectedDistributorPaymentStatus(null)
       .withExpectedCustomerConfigStatus(null)
       .withExpectedCustomerPaymentStatus(null)
       .withExpectedBuildingConfigStatus(null)
       .withExpectedBuildingPaymentStatus(null)
       .withExpectedCustomerCount(0)
       .withExpectedBuildingCount(0)
       .withExpectedBuildingMappedPointCount(0)
       .withExpectedBuildingPendingDeletion(false)
       .build());   
 }
 
  protected void loadObjectsOnly() throws Exception {

    loadAndEvaluateObjects(false);
  }  
  
  protected void loadAndEvaluateObjects() throws Exception {
    loadAndEvaluateObjects(true);
  }
  
  protected void loadAndEvaluateObjects(boolean performEvaluate) throws Exception {

    boolean loadDistributorPaymentMethods = true;
    boolean loadDistributorUsers = true;
    if (portfolioId != null) {

      portfolio = nodeHierarchyService.loadPortfolio(
          LoadPortfolioOptions
          .builder()
          .withCustomerId(onlineCustomerId)
          .withLoadDistributorPaymentMethods(loadDistributorPaymentMethods)
          .withLoadDistributorUsers(loadDistributorUsers)
          .build());
      onlineCustomer = (OnlineCustomerEntity)portfolio.getParentCustomer();
      onlineDistributor = (OnlineDistributorEntity)onlineCustomer.getParentDistributor();
      
      if (billableBuildingId != null) {
        
        billableBuilding = (BillableBuildingEntity)portfolio.getChildBuilding(billableBuildingId);
        mappedPointCount = billableBuilding.getTotalMappedPointCount(); 
        
        buildingSubscription = billableBuilding.getChildBuildingSubscriptionNullIfNotExists();
      }
      
      if (performEvaluate) {
        distributorHierarchyStateEvaluator.evaluatePortfolioState(portfolio);
      }
      
    } else if (onlineCustomerId != null) {
      
      onlineCustomer = (OnlineCustomerEntity)customerService.loadCustomer(
          onlineCustomerId,
          true,  // loadDistributorPaymentMethods
          true); // loadDistributorUsers
      onlineDistributor = (OnlineDistributorEntity)onlineCustomer.getParentDistributor();
      
    } else if (onlineDistributorId != null) {
      
      onlineDistributor = (OnlineDistributorEntity)distributorService.loadDistributor(
          onlineDistributorId,
          loadDistributorPaymentMethods,
          loadDistributorUsers);
    }
    
    // Regardless, we evaluate the Resolute root distributor in order to deal with any 
    // immediate child distributors that may need to be hard deleted.
    if (performEvaluate) {
      distributorHierarchyStateEvaluator.evaluateRootDistributorState(new ArrayList<>());  
    }
    
    // Since we evaluated above, we need to reload everything
    if (performEvaluate) {
      if (distributorShouldNoLongerExist) {
        onlineDistributor = null;
        onlineDistributorId = null;
      }
      if (customerShouldNoLongerExist) {
        onlineCustomer = null;
        onlineCustomerId = null;
        portfolio = null;
        portfolioId = null;
      }
      if (buildingShouldNoLongerExist) {
        billableBuilding = null;
        billableBuildingId = null;
        buildingSubscription = null;
        buildingSubscriptionId = null;
      }
      
      if (portfolioId != null) {

        portfolio = nodeHierarchyService.loadPortfolio(
            LoadPortfolioOptions
            .builder()
            .withCustomerId(onlineCustomerId)
            .withLoadDistributorPaymentMethods(loadDistributorPaymentMethods)
            .withLoadDistributorUsers(loadDistributorUsers)
            .build());
        onlineCustomer = (OnlineCustomerEntity)portfolio.getParentCustomer();
        onlineDistributor = (OnlineDistributorEntity)onlineCustomer.getParentDistributor();
        
        if (billableBuildingId != null) {
          
          billableBuilding = (BillableBuildingEntity)portfolio.getChildBuilding(billableBuildingId);
          mappedPointCount = billableBuilding.getTotalMappedPointCount(); 
          
          buildingSubscription = billableBuilding.getChildBuildingSubscriptionNullIfNotExists();
        }
        
      } else if (onlineCustomerId != null) {
        
        onlineCustomer = (OnlineCustomerEntity)customerService.loadCustomer(
            onlineCustomerId,
            true,  // loadDistributorPaymentMethods
            true); // loadDistributorUsers
        onlineDistributor = (OnlineDistributorEntity)onlineCustomer.getParentDistributor();
        
      } else if (onlineDistributorId != null) {
        
        onlineDistributor = (OnlineDistributorEntity)distributorService.loadDistributor(
            onlineDistributorId,
            loadDistributorPaymentMethods,
            loadDistributorUsers);
      }    
    }
  }
  
  protected void printState() {
    
    System.err.println("### CURRENT TIMESTAMP: " + TEST_TIME_KEEPER.getCurrentTimestamp() + "   EXPECTED: " + currentDate);

    if (onlineDistributor != null) {
      System.err.println("distributor config status: " + onlineDistributor.getDistributorStatus());
      System.err.println("distributor config status updated at: " + onlineDistributor.getDistributorStatusUpdatedAt());
      System.err.println("distributor payment status: " + onlineDistributor.getDistributorPaymentStatus());
      System.err.println("distributor payment status updated at: " + onlineDistributor.getDistributorPaymentStatusUpdatedAt());
      
    } else {
      System.err.println("distributor: NOT CREATED YET");
    }
    
    if (onlineCustomer != null) {
      System.err.println("customer config status: " + onlineCustomer.getCustomerStatus());
      System.err.println("customer config status updated at: " + onlineCustomer.getCustomerStatusUpdatedAt());
      System.err.println("customer payment status: " + onlineCustomer.getCustomerPaymentStatus());
      System.err.println("customer payment status updated at: " + onlineCustomer.getCustomerPaymentStatusUpdatedAt());
      
    } else {
      System.err.println("customer: NOT CREATED YET");
    }
    
    if (billableBuilding != null) {
      System.err.println("building config status: " + billableBuilding.getBuildingStatus());
      System.err.println("building config status updated at: " + billableBuilding.getBuildingStatusUpdatedAt());
      System.err.println("building payment status: " + billableBuilding.getBuildingPaymentStatus());
      System.err.println("building payment status updated at: " + billableBuilding.getBuildingPaymentStatusUpdatedAt());
      System.err.println("building pending deletion: " + billableBuilding.getPendingDeletion());
      System.err.println("building pending deletion updated at: " + billableBuilding.getPendingDeletionUpdatedAt());
      System.err.println("building mapped point count: " + mappedPointCount);
      
      BuildingSubscriptionEntity bs = billableBuilding.getChildBuildingSubscriptionNullIfNotExists();
      System.err.println("building subscription: " + bs);
      if (bs == null) {
        System.err.println("building subscription: null");
      } else {
        
        boolean isSubscriptionCanceled = billableBuilding.isSubscriptionCanceled();
        boolean hasCurrentPaymentIntervalExpired = false;
        if (isSubscriptionCanceled) {
          
          hasCurrentPaymentIntervalExpired = bs.hasCurrentPaymentIntervalExpired();
        }
        System.err.println("building subscription started at: " + bs.getStartedAt());
        System.err.println("building subscription current payment interval start: " + bs.getCurrentIntervalStartedAt());
        System.err.println("building subscription current payment interval end: " + bs.getCurrentIntervalEndsAt());
        System.err.println("building subscription next payment interval start: " + bs.getNextIntervalStartsAt());
        System.err.println("building subscription current payment plan: " + bs.getParentPaymentPlan());
        System.err.println("building subscription pending payment plan: " + bs.getPendingPaymentPlan());
        System.err.println("building subscription pending payment plan updated at: " + bs.getPendingPaymentPlanUpdatedAt());
        System.err.println("building subscription expired: " + hasCurrentPaymentIntervalExpired);
      }
    } else {
      System.err.println("building: NOT CREATED YET");
    }
  }
  
  private void assertState(AssertionHolder ah) {
    
    printState();
    if (ah.getExpectedDistributorConfigStatus() == null) {
      Assert.assertNull("distributor is not null", onlineDistributor);
    } else {
      
      Assert.assertEquals("distributor config status is incorrect", 
          ah.getExpectedDistributorConfigStatus(), 
          onlineDistributor.getDistributorStatus());
      
      Assert.assertEquals("distributor payment status is incorrect", 
          ah.getExpectedDistributorPaymentStatus(), 
          onlineDistributor.getDistributorPaymentStatus());
      
      Assert.assertEquals("customer count is incorrect", 
          Integer.toString(ah.getExpectedCustomerCount()), 
          Integer.toString(onlineDistributor.getChildCustomers().size()));
    }

    if (ah.getExpectedCustomerConfigStatus() == null) {
      Assert.assertNull("customer is not null", onlineCustomer);
    } else {
      
      Assert.assertEquals("customer config status is incorrect", 
          ah.getExpectedCustomerConfigStatus(), 
          onlineCustomer.getCustomerStatus());
      
      Assert.assertEquals("customer payment status is incorrect", 
          ah.getExpectedCustomerPaymentStatus(), 
          onlineCustomer.getCustomerPaymentStatus());
      
      if (onlineCustomer.getChildPortfolio() != null) {
        Assert.assertEquals("building count is incorrect", 
            Integer.toString(ah.getExpectedBuildingCount()), 
            Integer.toString(onlineCustomer.getChildPortfolio().getChildBuildings().size()));
      } else {
        Assert.assertEquals("building count is incorrect", 
            Integer.toString(ah.getExpectedBuildingCount()), 
            Integer.toString(0));
      }
    }
    
     if (ah.getExpectedBuildingConfigStatus() == null) {
       Assert.assertNull("building is not null", billableBuilding);   
     } else {

       Assert.assertEquals("building config status is incorrect", 
           ah.getExpectedBuildingConfigStatus(), 
           billableBuilding.getBuildingStatus());
       
       Assert.assertEquals("building payment status is incorrect", 
           ah.getExpectedBuildingPaymentStatus(), 
           billableBuilding.getBuildingPaymentStatus());
       
       Assert.assertEquals("building pending deletion is incorrect", 
           ah.getExpectedBuildingPendingDeletion(), 
           billableBuilding.getPendingDeletion());
       
       if (ah.getExpectedBuildingPendingDeletion()) {
         Assert.assertEquals("building pending deletion updated at is incorrect", 
             Boolean.TRUE, 
             billableBuilding.getPendingDeletionUpdatedAt() != null);
       }

       Assert.assertEquals("building mapped point count is incorrect", 
           Integer.toString(ah.getExpectedBuildingMappedPointCount()), 
           Integer.toString(billableBuilding.getTotalMappedPointCount()));
     }
  }
  
  private void fastForwardAnEvaluate(String strEndDate) throws Exception {

    LocalDate endDate = LocalDate.parse(
        strEndDate, 
        DATE_TIME_FORMATTER);
    
    LocalDate startDate = TEST_TIME_KEEPER.getCurrentLocalDate();
    
    List<LocalDate> datesBetween = TEST_TIME_KEEPER.getDatesBetween(endDate);
    datesBetween.remove(startDate);
    datesBetween.add(endDate);
    
    System.err.println("FAST FORWARD: "
        + currentDate 
        + " TO " 
        + strEndDate 
        + " NUM DAYS IN BETWEEN: " 
        + datesBetween.size());
    
    for (LocalDate localDate: datesBetween) {

      currentDate = localDate.format(DATE_TIME_FORMATTER);
      if (localDate.equals(endDate)) {
        // Log the time transition at the end date only 
        TEST_TIME_KEEPER.setCurrentDate(currentDate, true); 
      } else {
        TEST_TIME_KEEPER.setCurrentDate(currentDate, false);
      }
      if (currentDate.equals("2020-03-08")) {
        //System.err.println("BREAKPOINT");
      }
      try {
        loadAndEvaluateObjects();  
      } catch (Exception e) {
        throw new RuntimeException("Could not load or evaluate object state at localDate: " + localDate, e);
      }
    }
  }
}
//@formatter:on