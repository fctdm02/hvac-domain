//@formatter:off
package com.djt.hvac.domain.model.notification.service;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.djt.hvac.domain.model.AbstractResoluteDomainModelTest;
import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.timekeeper.TimeKeeper;
import com.djt.hvac.domain.model.common.timekeeper.impl.TestTimeKeeperImpl;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.customer.enums.CustomerType;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.enums.UnitSystem;
import com.djt.hvac.domain.model.distributor.AbstractDistributorEntity;
import com.djt.hvac.domain.model.distributor.enums.DistributorType;
import com.djt.hvac.domain.model.email.client.MockEmailClient;
import com.djt.hvac.domain.model.email.dto.EmailDto;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingStatus;
import com.djt.hvac.domain.model.nodehierarchy.point.mapped.MappablePointEntity;
import com.djt.hvac.domain.model.nodehierarchy.service.NodeHierarchyService;
import com.djt.hvac.domain.model.nodehierarchy.service.command.MapRawPointsRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.RawPointData;
import com.djt.hvac.domain.model.notification.dto.CreateNotificationEventOptions;
import com.djt.hvac.domain.model.notification.entity.NotificationEventEntity;
import com.djt.hvac.domain.model.notification.entity.UserNotificationEntity;
import com.djt.hvac.domain.model.rawpoint.RawPointEntity;
import com.djt.hvac.domain.model.user.AbstractUserEntity;
import com.djt.hvac.domain.model.user.enums.UserRoleType;

public class NotificationServiceTest extends AbstractResoluteDomainModelTest {
  
  private static TimeKeeper OLD_TIME_KEEPER;
  private static TestTimeKeeperImpl TEST_TIME_KEEPER;
  
  @BeforeClass
  public static void beforeClass() throws Exception {
    
    AbstractResoluteDomainModelTest.beforeClass();
    
    OLD_TIME_KEEPER = AbstractEntity.getTimeKeeper();
    
    TEST_TIME_KEEPER = new TestTimeKeeperImpl("2022-02-01");
    AbstractEntity.setTimeKeeper(TEST_TIME_KEEPER);
  }
  
  @AfterClass
  public static void afterClass() throws Exception {
    
    AbstractEntity.setTimeKeeper(OLD_TIME_KEEPER);
  }
  
  @Before
  public void before() throws Exception {
    
    notificationRepository.deleteAllNotificationEvents();
    notificationRepository.deleteAllUserNotifications();
    notificationRepository.deleteAllDisabledEmailUserNotifications();
    
    MockEmailClient.getInstance().reset();
  }
  
  @Test(expected=IllegalArgumentException.class)
  public void createNotificationEvent_missingCustomerId_nonGlobal() throws Exception {
    
    CreateNotificationEventOptions createNotificationEventOptions = CreateNotificationEventOptions
        .builder()
        .withEventType("CREDIT_CARD_EXPIRATION")
        .withDetails("Credit Card Expiration Details")
        .build();
    
    notificationService.createNotificationEvent(createNotificationEventOptions);
  }

  @Test(expected=IllegalArgumentException.class)
  public void createNotificationEvent_unexpectedCustomerId_globalLevel() throws Exception {
    
    CreateNotificationEventOptions createNotificationEventOptions = CreateNotificationEventOptions
        .builder()
        .withEventType("PLANNED_SITE_MAINTENANCE")
        .withDetails("Planned Site Maintenance Details")
        .withCustomerId(1)
        .build();
    
    notificationService.createNotificationEvent(createNotificationEventOptions);
  }
  
  @Test
  public void lifecycle() throws Exception {
    
    // CREATE DISTRIBUTOR/CUSTOMER AND ONE USER FOR EACH
    AbstractDistributorEntity distributor = distributorService.createDistributor(
        RESOLUTE_DISTRIBUTOR_ID, 
        DistributorType.ONLINE,
        "Test Distributor Name",
        UnitSystem.IP.toString(),
        false);

    AbstractCustomerEntity customer = customerService.createCustomer(
        distributor, 
        CustomerType.ONLINE,
        "Test Customer Name",
        UnitSystem.IP.toString());
    customer.getPersistentIdentity();

    nodeHierarchyService.createPortfolio(
        customer, 
        "Test_Customer_Portfolio_Node", 
        "Test Customer Portfolio Node");

    AbstractUserEntity customerUser = userRepository.createCustomerUser(
        customer, 
        UserRoleType.LIMITED_USER, 
        "user1@customer.com", 
        "First Name 1", 
        "Last Name 1");
    Integer customerUserId = customerUser.getPersistentIdentity();
    
    AbstractUserEntity distributorUser = userRepository.createDistributorUser(
        distributor, 
        UserRoleType.DISTRIBUTOR_ADMIN, 
        "user2@distributor.com", 
        "First Name 2", 
        "Last Name 2",
        Boolean.TRUE);
    Integer distributorUserId = distributorUser.getPersistentIdentity();
    
    
    
    
    // VERIFY THAT USERS HAVE NO NOTIFICATIONS
    List<UserNotificationEntity> customerUserNotifications = notificationService.getUserNotifications(customerUserId);
    Assert.assertNotNull(customerUserNotifications);
    Assert.assertTrue(customerUserNotifications.size() == 0);
    
    List<UserNotificationEntity> distributorUserNotifications = notificationService.getUserNotifications(distributorUserId);
    Assert.assertNotNull(distributorUserNotifications);
    Assert.assertTrue(distributorUserNotifications.size() == 0);    
    

    
    
    // 1: CREATE A NOTIFICATION EVENT: PLANNED_SITE_MAINTENANCE (SYNERGY AND FUSION)
    SortedMap<String, String> substitutionTokenValues1 = new TreeMap<>();
    substitutionTokenValues1.put("DATE_AND_TIME_OF_MAINTENANCE", "July 15th, 2022 at 3:00AM");
    
    CreateNotificationEventOptions createNotificationEventOptions1 = CreateNotificationEventOptions
        .builder()
        .withEventType("PLANNED_SITE_MAINTENANCE")
        .withSubstitutionTokenValues(substitutionTokenValues1)
        .withDetails("PLANNED_SITE_MAINTENANCE Details")
        .build();
    NotificationEventEntity notificationEvent1 = notificationService.createNotificationEvent(createNotificationEventOptions1);
    Assert.assertNotNull(notificationEvent1);
    
    notificationService.updateNotificationEventAsNonDraft(notificationEvent1.getPersistentIdentity(), "tmyers@resolutebi.com");
    
    List<NotificationEventEntity> notificationEvents1 = notificationService.evaluateNotificationEvents();
    Assert.assertNotNull(notificationEvents1);
    Assert.assertTrue(notificationEvents1.size() == 1);
    
    customerUserNotifications = notificationService.getUserNotifications(customerUserId);
    Assert.assertNotNull(customerUserNotifications);
    Assert.assertTrue(customerUserNotifications.size() == 1);
    UserNotificationEntity customerUserNotification1 = customerUserNotifications.get(0);
    String customerUserHtmlBody1 = customerUserNotification1.getNotificationEvent().getTokenSubstitutedTemplateBody(customerUser);
    System.err.println(customerUserHtmlBody1);
    
    distributorUserNotifications = notificationService.getUserNotifications(distributorUserId);
    Assert.assertNotNull(distributorUserNotifications);
    Assert.assertTrue(distributorUserNotifications.size() == 1);    
    UserNotificationEntity distributorUserNotification1 = distributorUserNotifications.get(0);
    String distributorUserHtmlBody1 = distributorUserNotification1.getNotificationEvent().getTokenSubstitutedTemplateBody(customerUser);
    System.err.println(distributorUserHtmlBody1);
    
    notificationService.deleteAllUserNotifications();
    
    
    

    // 2: CREATE A NOTIFICATION EVENT: UPDATED_RULES_REPORTS (SYNERGY ONLY) 
    SortedMap<String, String> substitutionTokenValues2 = new TreeMap<>();
    substitutionTokenValues2.put("RULE_OR_REPORT", "Rule");
    substitutionTokenValues2.put("OUTDATED_VERSION_TITLE", "3.1.1.1 v1");
    substitutionTokenValues2.put("UPDATED_VERSION_TITLE", "3.1.1.1 v2");
    substitutionTokenValues2.put("INSERT_REQUIRED_RULES", "");
    substitutionTokenValues2.put("INSERT_REQUIRED_COMPUTED_POINTS", "");
    substitutionTokenValues2.put("INSERT_REQUIRED_POINT_TEMPLATES", "DaFlowSp, DaFlowMinSp, DaFlow");
    
    CreateNotificationEventOptions createNotificationEventOptions2 = CreateNotificationEventOptions
        .builder()
        .withEventType("UPDATED_RULES_REPORTS")
        .withSubstitutionTokenValues(substitutionTokenValues2)
        .withDetails("UPDATED_RULES_REPORTS Details")
        .build();
    NotificationEventEntity notificationEvent2 = notificationService.createNotificationEvent(createNotificationEventOptions2);
    Assert.assertNotNull(notificationEvent2);
    notificationService.updateNotificationEventAsNonDraft(notificationEvent2.getPersistentIdentity(), "tmyers@resolutebi.com");

    List<NotificationEventEntity> notificationEvents2 = notificationService.evaluateNotificationEvents();
    Assert.assertNotNull(notificationEvents2);
    Assert.assertTrue(notificationEvents2.size() == 1);
    
    customerUserNotifications = notificationService.getUserNotifications(customerUserId);
    Assert.assertNotNull(customerUserNotifications);
    Assert.assertTrue(customerUserNotifications.size() == 0);
    
    distributorUserNotifications = notificationService.getUserNotifications(distributorUserId);
    Assert.assertNotNull(distributorUserNotifications);
    Assert.assertTrue(distributorUserNotifications.size() == 1);    
    UserNotificationEntity distributorUserNotification2 = distributorUserNotifications.get(0);
    String distributorUserHtmlBody2 = distributorUserNotification2.getNotificationEvent().getTokenSubstitutedTemplateBody(customerUser);
    System.err.println(distributorUserHtmlBody2);
    
    notificationService.deleteAllUserNotifications();
    
    
    
    
    // 3: CREATE A NOTIFICATION EVENT: UPDATED_RULES_REPORTS_NO_SUPPORT (SYNERGY ONLY)
    SortedMap<String, String> substitutionTokenValues3 = new TreeMap<>();
    substitutionTokenValues3.put("RULE_OR_REPORT", "Rule");
    substitutionTokenValues3.put("OUTDATED_VERSION_TITLE", "3.1.1.1 v1");
    substitutionTokenValues3.put("UPDATED_VERSION_TITLE", "3.1.1.1 v2");
    substitutionTokenValues3.put("DATE", "2022-09-01");
    
    CreateNotificationEventOptions createNotificationEventOptions3 = CreateNotificationEventOptions
        .builder()
        .withEventType("UPDATED_RULES_REPORTS_NO_SUPPORT")
        .withSubstitutionTokenValues(substitutionTokenValues3)
        .withDetails("UPDATED_RULES_REPORTS_NO_SUPPORT Details")
        .build();
    NotificationEventEntity notificationEvent3 = notificationService.createNotificationEvent(createNotificationEventOptions3);
    Assert.assertNotNull(notificationEvent3);
    notificationService.updateNotificationEventAsNonDraft(notificationEvent3.getPersistentIdentity(), "tmyers@resolutebi.com");

    List<NotificationEventEntity> notificationEvents3 = notificationService.evaluateNotificationEvents();
    Assert.assertNotNull(notificationEvents3);
    Assert.assertTrue(notificationEvents3.size() == 1);
    
    customerUserNotifications = notificationService.getUserNotifications(customerUserId);
    Assert.assertNotNull(customerUserNotifications);
    Assert.assertTrue(customerUserNotifications.size() == 0);
    
    distributorUserNotifications = notificationService.getUserNotifications(distributorUserId);
    Assert.assertNotNull(distributorUserNotifications);
    Assert.assertTrue(distributorUserNotifications.size() == 1);    
    UserNotificationEntity distributorUserNotification3 = distributorUserNotifications.get(0);
    String distributorUserHtmlBody3 = distributorUserNotification3.getNotificationEvent().getTokenSubstitutedTemplateBody(customerUser);
    System.err.println(distributorUserHtmlBody3);
    
    notificationService.deleteAllUserNotifications();    

    
    
    
    // 4: CREATE A NOTIFICATION EVENT: NEW_RULES_REPORTS_ADDED (SYNERGY ONLY)
    SortedMap<String, String> substitutionTokenValues4 = new TreeMap<>();
    substitutionTokenValues4.put("INSERT_UPDATED_TITLES", "Rule 3.1.1.1 v2, Rule 3.1.1.10 v1");
    
    CreateNotificationEventOptions createNotificationEventOptions4 = CreateNotificationEventOptions
        .builder()
        .withEventType("NEW_RULES_REPORTS_ADDED")
        .withSubstitutionTokenValues(substitutionTokenValues4)
        .withDetails("NEW_RULES_REPORTS_ADDED Details")
        .build();
    NotificationEventEntity notificationEvent4 = notificationService.createNotificationEvent(createNotificationEventOptions4);
    Assert.assertNotNull(notificationEvent4);
    notificationService.updateNotificationEventAsNonDraft(notificationEvent4.getPersistentIdentity(), "tmyers@resolutebi.com");

    List<NotificationEventEntity> notificationEvents4 = notificationService.evaluateNotificationEvents();
    Assert.assertNotNull(notificationEvents4);
    Assert.assertTrue(notificationEvents4.size() == 1);
    
    customerUserNotifications = notificationService.getUserNotifications(customerUserId);
    Assert.assertNotNull(customerUserNotifications);
    Assert.assertTrue(customerUserNotifications.size() == 0);
    
    distributorUserNotifications = notificationService.getUserNotifications(distributorUserId);
    Assert.assertNotNull(distributorUserNotifications);
    Assert.assertTrue(distributorUserNotifications.size() == 1);    
    UserNotificationEntity distributorUserNotification4 = distributorUserNotifications.get(0);
    String distributorUserHtmlBody4 = distributorUserNotification4.getNotificationEvent().getTokenSubstitutedTemplateBody(customerUser);
    System.err.println(distributorUserHtmlBody4);
    
    notificationService.deleteAllUserNotifications();      
    
    

    
    // 5: CREATE A NOTIFICATION EVENT: NEW_OR_UPDATED_FEATURES (SYNERGY AND FUSION)
    SortedMap<String, String> substitutionTokenValues5 = new TreeMap<>();
    substitutionTokenValues5.put("NEW_OR_UPDATED_FEATURE", "New Feature");
    substitutionTokenValues5.put("FEATURE", "Announcements");
    substitutionTokenValues5.put("SYNERGY_OR_FUSION", "Synergy and Fusion");
    substitutionTokenValues5.put("FEATURE_DETAILS", "User Announcements Feature Details");
    
    CreateNotificationEventOptions createNotificationEventOptions5 = CreateNotificationEventOptions
        .builder()
        .withEventType("NEW_OR_UPDATED_FEATURES")
        .withSubstitutionTokenValues(substitutionTokenValues5)
        .withDetails("NEW_OR_UPDATED_FEATURES Details")
        .build();
    NotificationEventEntity notificationEvent5 = notificationService.createNotificationEvent(createNotificationEventOptions5);
    Assert.assertNotNull(notificationEvent5);
    notificationService.updateNotificationEventAsNonDraft(notificationEvent5.getPersistentIdentity(), "tmyers@resolutebi.com");

    List<NotificationEventEntity> notificationEvents5 = notificationService.evaluateNotificationEvents();
    Assert.assertNotNull(notificationEvents5);
    Assert.assertTrue(notificationEvents5.size() == 1);
    
    customerUserNotifications = notificationService.getUserNotifications(customerUserId);
    Assert.assertNotNull(customerUserNotifications);
    Assert.assertTrue(customerUserNotifications.size() == 1);
    UserNotificationEntity customerUserNotification5 = customerUserNotifications.get(0);
    String customerUserHtmlBody5 = customerUserNotification5.getNotificationEvent().getTokenSubstitutedTemplateBody(customerUser);
    System.err.println(customerUserHtmlBody5);
    
    distributorUserNotifications = notificationService.getUserNotifications(distributorUserId);
    Assert.assertNotNull(distributorUserNotifications);
    Assert.assertTrue(distributorUserNotifications.size() == 1);    
    UserNotificationEntity distributorUserNotification5 = distributorUserNotifications.get(0);
    String distributorUserHtmlBody5 = distributorUserNotification5.getNotificationEvent().getTokenSubstitutedTemplateBody(customerUser);
    System.err.println(distributorUserHtmlBody5);
    
    notificationService.deleteAllUserNotifications();       

    
    
    
    // 6: CREATE A NOTIFICATION EVENT: RELEASE_NOTES (SYNERGY AND FUSION)
    SortedMap<String, String> substitutionTokenValues6 = new TreeMap<>();
    
    CreateNotificationEventOptions createNotificationEventOptions6 = CreateNotificationEventOptions
        .builder()
        .withEventType("RELEASE_NOTES")
        .withSubstitutionTokenValues(substitutionTokenValues6)
        .withDetails("RELEASE_NOTES Details")
        .build();
    NotificationEventEntity notificationEvent6 = notificationService.createNotificationEvent(createNotificationEventOptions6);
    Assert.assertNotNull(notificationEvent6);
    notificationService.updateNotificationEventAsNonDraft(notificationEvent6.getPersistentIdentity(), "tmyers@resolutebi.com");

    List<NotificationEventEntity> notificationEvents6 = notificationService.evaluateNotificationEvents();
    Assert.assertNotNull(notificationEvents6);
    Assert.assertTrue(notificationEvents6.size() == 1);
    
    customerUserNotifications = notificationService.getUserNotifications(customerUserId);
    Assert.assertNotNull(customerUserNotifications);
    Assert.assertTrue(customerUserNotifications.size() == 1);
    UserNotificationEntity customerUserNotification6 = customerUserNotifications.get(0);
    String customerUserHtmlBody6 = customerUserNotification6.getNotificationEvent().getTokenSubstitutedTemplateBody(customerUser);
    System.err.println(customerUserHtmlBody6);
    
    distributorUserNotifications = notificationService.getUserNotifications(distributorUserId);
    Assert.assertNotNull(distributorUserNotifications);
    Assert.assertTrue(distributorUserNotifications.size() == 1);    
    UserNotificationEntity distributorUserNotification6 = distributorUserNotifications.get(0);
    String distributorUserHtmlBody6 = distributorUserNotification6.getNotificationEvent().getTokenSubstitutedTemplateBody(customerUser);
    System.err.println(distributorUserHtmlBody6);
    
    notificationService.markUserNotificationAsRead(distributorUserId, distributorUserNotifications.get(0).getNotificationEvent().getPersistentIdentity());
    notificationService.markUserNotificationAsUnread(distributorUserId, distributorUserNotifications.get(0).getNotificationEvent().getPersistentIdentity());
    notificationService.markAllUserNotificationsAsRead(distributorUserId);
    notificationService.deleteUserNotification(distributorUserId, distributorUserNotifications.get(0).getNotificationEvent().getPersistentIdentity());
    notificationService.deleteAllUserNotifications(distributorUserId);
    
    notificationService.deleteAllUserNotifications();
    
    
    
    
    /*
    // CREATE A CHILD (A.K.A. REMINDER) NOTIFICATION EVENT
    boolean publishImmediately = false;
    NotificationEventEntity reminderNotificationEvent = notificationService.createChildNotificationEvent(
        notificationEvent.getPersistentIdentity(),
        "2022-12-31 00:00:00",
        "Planned Site Maintenance Reminder details",
        substitutionTokenValues,
        publishImmediately);
    Assert.assertNotNull(reminderNotificationEvent);
    notificationService.updateNotificationEventAsNonDraft(reminderNotificationEvent.getPersistentIdentity(), "tmyers@resolutebi.com");
    


    // EVALUATE FOR NEW/EXPIRED NOTIFICATIONS
    List<NotificationEventEntity> notificationEvents = notificationService.evaluateNotificationEvents();
    Assert.assertNotNull(notificationEvents);
    Assert.assertTrue(notificationEvents.size() == 2);

    
    
    // VERIFY THAT 2 USERS HAVE EACH RECEIVED THE 2 UNPUBLISHED NOTIFICATIONS
    customerUserNotifications = notificationService.getUserNotifications(customerUserId);
    Assert.assertNotNull(customerUserNotifications);
    Assert.assertTrue(customerUserNotifications.size() == 2);
    
    distributorUserNotifications = notificationService.getUserNotifications(distributorUserId);
    Assert.assertNotNull(distributorUserNotifications);
    Assert.assertTrue(distributorUserNotifications.size() == 2);
    
    
    
    // CREATE A NEW NOTIFICATION EVENT
    notificationEvent = notificationService.createNotificationEvent(createNotificationEventOptions);
    Assert.assertNotNull(notificationEvent);
    notificationService.updateNotificationEventAsNonDraft(notificationEvent.getPersistentIdentity(), "tmyers@resolutebi.com");

    
    
    // VERIFY THAT THERE IS ONLY 1 UNPUBLISHED EVENT (THE OTHER 2 HAVE BEEN ALREADY)
    notificationEvents = notificationService.evaluateNotificationEvents();
    Assert.assertNotNull(notificationEvents);
    Assert.assertTrue(notificationEvents.size() == 1);
    
    
    
    // VERIFY THAT EACH USER NOW HAS 3 NOTIFICATIONS
    customerUserNotifications = notificationService.getUserNotifications(customerUserId);
    Assert.assertNotNull(customerUserNotifications);
    Assert.assertTrue(customerUserNotifications.size() == 3);
    
    distributorUserNotifications = notificationService.getUserNotifications(distributorUserId);
    Assert.assertNotNull(distributorUserNotifications);
    Assert.assertTrue(distributorUserNotifications.size() == 3);
    
    
    
    // MARK A SPECIFIC NOTIFICATION AS READ FOR THE CUSTOMER USER
    UserNotificationEntity customerUserNotification = customerUserNotifications.get(0);
    Integer customerUserNotificationEventId = customerUserNotification.getNotificationEvent().getPersistentIdentity();
    notificationService.markUserNotificationAsRead(customerUserId, customerUserNotificationEventId);
    customerUserNotifications = notificationService.getUserNotifications(customerUserId);
    for (UserNotificationEntity userNotification: customerUserNotifications) {
      if (userNotification.getNotificationEvent().getPersistentIdentity().equals(customerUserNotificationEventId)) {
        Assert.assertTrue(userNotification.getHasBeenRead());        
      }
    }
    
    
    
    // MARK A SPECIFIC NOTIFICATION AS UNREAD FOR THE CUSTOMER USER
    notificationService.markUserNotificationAsUnread(customerUserId, customerUserNotificationEventId);
    customerUserNotifications = notificationService.getUserNotifications(customerUserId);
    for (UserNotificationEntity userNotification: customerUserNotifications) {
      if (userNotification.getNotificationEvent().getPersistentIdentity().equals(customerUserNotificationEventId)) {
        Assert.assertFalse(userNotification.getHasBeenRead());        
      }
    }
    
    
    
    // MARK ALL NOTIFICATIONS AS READ FOR THE DISTRIBUTOR USER
    notificationService.markAllUserNotificationsAsRead(distributorUserId);
    distributorUserNotifications = notificationService.getUserNotifications(distributorUserId);
    for (UserNotificationEntity userNotification: distributorUserNotifications) {
      Assert.assertTrue(userNotification.getHasBeenRead());
    }
    
    
    
    // DELETE A SPECIFIC NOTIFICATION FOR THE CUSTOMER USER
    notificationService.deleteUserNotification(customerUserId, customerUserNotificationEventId);
    customerUserNotifications = notificationService.getUserNotifications(customerUserId);
    for (UserNotificationEntity userNotification: customerUserNotifications) {
      if (userNotification.getNotificationEvent().getPersistentIdentity().equals(customerUserNotificationEventId)) {
        throw new RuntimeException("Expected user notification to be deleted, instead it still exists.");        
      }
    }
    
    
    
    // DELETE ALL REMAINING NOTIFICATIONS FOR THE CUSTOMER USER
    notificationService.deleteAllUserNotifications(customerUserId);
    customerUserNotifications = notificationService.getUserNotifications(customerUserId);
    Assert.assertTrue(customerUserNotifications.isEmpty());
    
    
    
    // DELETE ALL USER NOTIFICATIONS
    notificationService.deleteAllUserNotifications();
    customerUserNotifications = notificationService.getUserNotifications(customerUserId);
    distributorUserNotifications = notificationService.getUserNotifications(distributorUserId);
    Assert.assertTrue(customerUserNotifications.isEmpty());
    Assert.assertTrue(distributorUserNotifications.isEmpty());
    
    
    
    // CREATE AN APPLICATION ALERT NOTIFICATION EVENT THAT HAS EMAIL AS A PRESENTATION TYPE
    createNotificationEventOptions = CreateNotificationEventOptions
        .builder()
        .withEventType("CONNECTOR_STATUS")
        .withCustomerId(customerId)
        .withDetails("Connector Status Details")
        .build();
    notificationEvent = notificationService.createNotificationEvent(createNotificationEventOptions);
    notificationService.updateNotificationEventAsNonDraft(notificationEvent.getPersistentIdentity(), "tmyers@resolutebi.com");
    notificationEvents = notificationService.evaluateNotificationEvents();
    Assert.assertNotNull(notificationEvents);
    Assert.assertTrue(notificationEvents.size() == 1);
    Assert.assertNotNull(notificationEvent);
    customerUserNotifications = notificationService.getUserNotifications(customerUserId);
    Assert.assertTrue(customerUserNotifications.size() == 1);
    notificationService.deleteAllUserNotifications(customerUserId);
    customerUserNotifications = notificationService.getUserNotifications(customerUserId);
    Assert.assertTrue(customerUserNotifications.size() == 0);
    
    
    
    // DISABLE EMAIL NOTIFICATIONS FOR CONNECTOR_STATUS EVENT TYPES
    notificationService.disableEmailUserNotification(customerUserId, "CONNECTOR_STATUS");
    
    
    
    // PUBLISH ANOTHER CONNECTOR_STATUS NOTIFICATION FOR THE GIVEN CUSTOMER AND VERIFY THE USER DID NOT RECEIVE AN EMAIL
    // SINCE EMAILS ARE PUBLISHED ON THE FIRST AND THIRD MONDAY OF EVERY MONTH AND THE CURRENT DATE IS FRI, 02-04-2022,
    // WE FAST FORWARD TO TUES 02-08-2022, A DAY AFTER EMAIL PUBLISHING ON THE FIRST MON OF FEB, 02-07-2022
    MockEmailClient.getInstance().reset();
    TEST_TIME_KEEPER.setCurrentDate("2022-02-04");
    notificationEvent = notificationService.createNotificationEvent(createNotificationEventOptions);
    notificationService.updateNotificationEventAsNonDraft(notificationEvent.getPersistentIdentity(), "tmyers@resolutebi.com");
    notificationEvents = notificationService.evaluateNotificationEvents();
    TEST_TIME_KEEPER.setCurrentDate("2022-02-07");
    LocalDate nextEmailDate = notificationService.getNextDateForBiMonthlyEmailSubmission();
    LocalDate currentDate = AbstractEntity.getTimeKeeper().getCurrentLocalDate();
    Assert.assertEquals(nextEmailDate, currentDate);
    TEST_TIME_KEEPER.setCurrentDate("2022-02-08");
    List<EmailDto> sentEmails = MockEmailClient.getInstance().getSentEmails(customerUser.getEmail());
    Assert.assertTrue(sentEmails.size() == 0);
    
    
    
    // CREATE A NOTIFICATION EVENT THAT HAS AN EXPIRATION DATE
    notificationService.deleteAllUserNotifications();
    createNotificationEventOptions = CreateNotificationEventOptions
        .builder()
        .withEventType("PLANNED_SITE_MAINTENANCE")
        .withDetails("Planned Site Maintenance Details")
        .withExpirationDate("2022-02-10 00:00:00")
        .withSubstitutionTokenValues(substitutionTokenValues)
        .build();
    notificationEvent = notificationService.createNotificationEvent(createNotificationEventOptions);
    notificationService.updateNotificationEventAsNonDraft(notificationEvent.getPersistentIdentity(), "tmyers@resolutebi.com");
    notificationEvents = notificationService.evaluateNotificationEvents();
    customerUserNotifications = notificationService.getUserNotifications(customerUserId);
    Assert.assertTrue(customerUserNotifications.size() == 1);
    distributorUserNotifications = notificationService.getUserNotifications(distributorUserId);
    Assert.assertTrue(distributorUserNotifications.size() == 1);
    
    
    
    // VERIFY THAT THE EXPIRED NOTIFICATION EVENT (AND ALL ASSOCIATED USER NOTIFICATIONS) WERE DELETED
    TEST_TIME_KEEPER.setCurrentDate("2022-02-12");
    notificationEvents = notificationService.evaluateNotificationEvents();
    customerUserNotifications = notificationService.getUserNotifications(customerUserId);
    Assert.assertTrue(customerUserNotifications.size() == 0);
    distributorUserNotifications = notificationService.getUserNotifications(distributorUserId);
    Assert.assertTrue(distributorUserNotifications.size() == 0);
    
    
    
    // CREATE A DISTRIBUTOR USER THAT IS NOT AN ACCOUNT MANAGER
    //AbstractUserEntity distributorUser2 = userRepository.createDistributorUser(
    //    distributor, 
    //    UserRoleType.DISTRIBUTOR_USER, 
    //    "user3@distributor.com", 
    //    "First Name 3", 
    //    "Last Name 3",
    //    Boolean.FALSE);
    //Integer distributorUserId2 = distributorUser2.getPersistentIdentity();    
    
    
    
    // CREATE A CREDIT_CARD_EXPIRATION NOTIFICATION EVENT
    // VERIFY THAT ONLY THE ACCOUNT MANAGER FOR THE GIVEN DISTRIBUTOR RECEIVED THE USER NOTIFICATION
    MockEmailClient.getInstance().reset();
    notificationService.deleteAllUserNotifications();
    
    createNotificationEventOptions = CreateNotificationEventOptions
        .builder()
        .withEventType("CREDIT_CARD_EXPIRATION")
        .withDetails("Credit Card Expiration Details 1")
        .withCustomerId(customerId)
        .build();
    notificationEvent = notificationService.createNotificationEvent(createNotificationEventOptions);
    notificationService.updateNotificationEventAsNonDraft(notificationEvent.getPersistentIdentity(), "tmyers@resolutebi.com");
    notificationEvents = notificationService.evaluateNotificationEvents();
    
    customerUserNotifications = notificationService.getUserNotifications(customerUserId);
    //Assert.assertTrue(customerUserNotifications.size() == 0);
    
    distributorUserNotifications = notificationService.getUserNotifications(distributorUserId);
    Assert.assertTrue(distributorUserNotifications.size() == 1);

    //List<UserNotificationEntity> distributorUser2Notifications = notificationService.getUserNotifications(distributorUserId2);
    //Assert.assertTrue(distributorUser2Notifications.size() == 0);
    
    
    // TEST EMAIL GROUPING FUNCTIONALITY (THESE TWO OUGHT TO BE GROUPED TOGETHER IN A BI-MONTHLY NEWSLETTER)
    createNotificationEventOptions = CreateNotificationEventOptions
        .builder()
        .withEventType("PLANNED_SITE_MAINTENANCE")
        .withDetails("Planned Site Maintenance Details 1")
        .withSubstitutionTokenValues(substitutionTokenValues)
        .build();
    notificationEvent = notificationService.createNotificationEvent(createNotificationEventOptions);
    notificationService.updateNotificationEventAsNonDraft(notificationEvent.getPersistentIdentity(), "tmyers@resolutebi.com");
    
    SortedMap<String, String> substitutionTokenValues2 = new TreeMap<>();
    substitutionTokenValues2.put("NEW_OR_UPDATED_FEATURE", "New Feature XYZ");
    substitutionTokenValues2.put("FEATURE", "Feature 123");
    substitutionTokenValues2.put("SYNERGY_OR_FUSION", "Synergy");
    substitutionTokenValues2.put("FEATURE_DETAILS", "Feature Details");
    createNotificationEventOptions = CreateNotificationEventOptions
        .builder()
        .withEventType("NEW_OR_UPDATED_FEATURES")
        .withSubstitutionTokenValues(substitutionTokenValues2)
        .withDetails("New or Updated Features 1")
        .build();
    notificationEvent = notificationService.createNotificationEvent(createNotificationEventOptions);
    notificationService.updateNotificationEventAsNonDraft(notificationEvent.getPersistentIdentity(), "tmyers@resolutebi.com");
    
    
    // FAST FORWARD TO TUE 02-22-2022 AND VERIFY THAT THE ACCOUNT MANAGER RECEIVED AN EMAIL WITH 3 NOTIFICATIONS:
    // 1. CREDIT_CARD_EXPIRATION: "Credit Card Expiration Details 1"
    // 2. PLANNED_SITE_MAINTENANCE: "Planned Site Maintenance Details 1"
    // 3. NEW_OR_UPDATED_FEATURES: "New or Updated Features 1"
    TEST_TIME_KEEPER.setCurrentDate("2022-02-21");
    notificationEvents = notificationService.evaluateNotificationEvents();
    sentEmails = MockEmailClient.getInstance().getSentEmails(distributorUser.getEmail());
    //Assert.assertTrue(sentEmails.size() == 2);

    distributorUserNotifications = notificationService.getUserNotifications(distributorUserId);
    //Assert.assertTrue(distributorUserNotifications.size() == 3);
     */
  }
  
  @Test
  public void system_created_POINT_CAP_EXCEEDED() throws Exception {
    
    int maxPointCap = 100;
    try {

      // ***********************************************
      // Reduce the max point cap.
      dictionaryService.getPaymentPlansContainer().setMaxPointCapForTesting(maxPointCap);

      
      // ***********************************************
      // Create an out of band distributor.
      boolean allowOutOfBandBuildings = true;
      AbstractDistributorEntity distributor = distributorService.createDistributor(
          RESOLUTE_DISTRIBUTOR_ID, 
          DistributorType.ONLINE,
          "OnlineDistributor_maxPointCapExceeded",
          UnitSystem.IP.toString(),
          allowOutOfBandBuildings);
      distributorId = distributor.getPersistentIdentity();

      
      AbstractUserEntity distributorUser = userRepository.createDistributorUser(
          distributor, 
          UserRoleType.DISTRIBUTOR_ADMIN, 
          "user2@distributor.com", 
          "First Name 2", 
          "Last Name 2",
          Boolean.TRUE);
      Integer distributorUserId = distributorUser.getPersistentIdentity();
      
      
      // ***********************************************
      // Create an online child customer.
      AbstractCustomerEntity customer = customerService.createCustomer(
          distributor, 
          CustomerType.ONLINE,
          "OnlineCustomer_maxPointCapExceeded",
          UnitSystem.IP.toString());
      customerId = customer.getPersistentIdentity();

      
      // ***********************************************
      // Create the root portfolio for the customer.
      PortfolioEntity portfolio = nodeHierarchyService.createPortfolio(
          customer, 
          "OnlineCustomer_maxPointCapExceeded", 
          "OnlineCustomer_maxPointCapExceeded");
      
      
      // ***********************************************
      // Map more points so that we get a point count that exceeds the max point cap of payment plans.
      int numPointsToMap = 10;
      numPointsToMap = maxPointCap + 10;
      String mappingExpression = "/Drivers/NiagaraNetwork/{building}/{point}";
      String metricIdPattern = "/Drivers/NiagaraNetwork/Building_1/Point_Y";
      List<MappablePointEntity> createdMappablePoints = mapRawPoints(distributor, customer, numPointsToMap, numPointsToMap, metricIdPattern, mappingExpression);
      portfolio = nodeHierarchyService.loadPortfolio(customerId);
      
      BuildingEntity building = portfolio.getChildBuildingByName("Building_1");
      Assert.assertEquals("Building status is incorrect", 
          BuildingStatus.PENDING_ACTIVATION, 
          building.getBuildingStatus());

      Assert.assertEquals("Point count is incorrect", 
          Integer.toString(maxPointCap), 
          Integer.toString(createdMappablePoints.size()));
      
      Assert.assertEquals("Point count is incorrect", 
          Integer.toString(maxPointCap), 
          Integer.toString(building.getTotalMappedPointCount()));
      

      
      // ***********************************************
      // Validate that a POINT_CAP_EXCEEDED notification was generated and an email sent to the distributor user.
      List<UserNotificationEntity> distributorUserNotifications = notificationService.getUserNotifications(distributorUserId);
      Assert.assertEquals("distributorUserNotifications size is incorrect",
          "1",
          Integer.toString(distributorUserNotifications.size()));
      
      Assert.assertNotNull(distributorUserNotifications);
      Assert.assertTrue(distributorUserNotifications.size() == 1);
      
      List<EmailDto> sentEmails = MockEmailClient.getInstance().getSentEmails(distributorUser.getEmail());
      Assert.assertEquals("sent email size is incorrect",
          "1",
          Integer.toString(sentEmails.size()));
      
      // Resolute - Point Cap Exceeded
      // 2022-02-01 00:00:00 Resolute - Point Cap Exceeded<BR>Point cap of: 100 has been exceeded for building: OnlineCustomer_maxPointCapExceeded/Building_1 for customer: Resolute/OnlineDistributor_maxPointCapExceeded/OnlineCustomer_maxPointCapExceeded<BR>
      System.err.println(sentEmails.get(0).getSubject());
      System.err.println(sentEmails.get(0).getBody());
      
    } finally {
      DictionaryContext.setPaymentPlansContainer(null);
      dictionaryService.ensureDictionaryDataIsLoaded();
   }
  }
  
  protected List<MappablePointEntity> mapRawPoints(
      AbstractDistributorEntity distributor,
      AbstractCustomerEntity customer,
      int maxPointCap,
      int numPointsToMap,
      String metricIdPattern,
      String mappingExpression) throws Exception {
  
    return mapRawPoints(
        distributor,
        customer,
        maxPointCap,
        numPointsToMap,
        metricIdPattern,
        mappingExpression,
        null);
  }
  
  protected List<MappablePointEntity> mapRawPoints(
      AbstractDistributorEntity distributor,
      AbstractCustomerEntity customer,
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
    return nodeHierarchyService.mapRawPoints(mapRawPointsRequest);
  }  
}  
//@formatter:on