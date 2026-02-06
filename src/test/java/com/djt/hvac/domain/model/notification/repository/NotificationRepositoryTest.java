//@formatter:off
package com.djt.hvac.domain.model.notification.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.djt.hvac.domain.model.AbstractResoluteDomainModelTest;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.customer.enums.CustomerType;
import com.djt.hvac.domain.model.dictionary.enums.UnitSystem;
import com.djt.hvac.domain.model.distributor.AbstractDistributorEntity;
import com.djt.hvac.domain.model.distributor.enums.DistributorType;
import com.djt.hvac.domain.model.notification.dto.CreateNotificationEventOptions;
import com.djt.hvac.domain.model.notification.dto.UserNotificationDto;
import com.djt.hvac.domain.model.notification.entity.NotificationEventEntity;
import com.djt.hvac.domain.model.notification.entity.UserNotificationEntity;
import com.djt.hvac.domain.model.user.AbstractUserEntity;
import com.djt.hvac.domain.model.user.enums.UserRoleType;

public class NotificationRepositoryTest extends AbstractResoluteDomainModelTest {
  
  @Before
  public void before() throws Exception {
    
    notificationRepository.deleteAllNotificationEvents();
    notificationRepository.deleteAllUserNotifications();
    notificationRepository.deleteAllDisabledEmailUserNotifications();
  }
  
  @Test
  public void notificationEvents_lifecycle() throws Exception {
    
    String distributorName = "Test Distributor Name " + UUID.randomUUID().toString();
    AbstractDistributorEntity distributor = distributorService.createDistributor(
        RESOLUTE_DISTRIBUTOR_ID, 
        DistributorType.ONLINE,
        distributorName,
        UnitSystem.IP.toString(),
        false);

    String customerName = "Test Customer Name" + UUID.randomUUID().toString();
    AbstractCustomerEntity customer = customerService.createCustomer(
        distributor, 
        CustomerType.ONLINE,
        customerName,
        UnitSystem.IP.toString());
    customerId = customer.getPersistentIdentity();
    
    CreateNotificationEventOptions createNotificationEventOptions = CreateNotificationEventOptions
        .builder()
        .withEventType("PLANNED_SITE_MAINTENANCE")
        .build();
    NotificationEventEntity notificationEvent = notificationRepository.createNotificationEvent(createNotificationEventOptions);
    Assert.assertNotNull(notificationEvent);

    
    SortedMap<String, String> substitutionTokenValues = new TreeMap<>();
    substitutionTokenValues.put("DATE_AND_TIME_OF_MAINTENANCE", "July 15th, 2022 at 3:00AM");
    NotificationEventEntity reminderNotificationEvent = notificationRepository.createChildNotificationEvent(
        notificationEvent.getPersistentIdentity(),
        "2022-12-31 00:00:00",
        "Reminder details",
        substitutionTokenValues);
    Assert.assertNotNull(reminderNotificationEvent);
    
    
    boolean hasBeenPublished = false;
    boolean isDraft = true;
    List<NotificationEventEntity> notificationEvents = notificationRepository.loadNotificationEvents(hasBeenPublished, isDraft);
    Assert.assertNotNull(notificationEvents);
    Assert.assertFalse(notificationEvents.isEmpty());
    
    
    notificationRepository.deleteAllNotificationEvents();
    notificationEvents = notificationRepository.loadNotificationEvents(hasBeenPublished, isDraft);
    Assert.assertNotNull(notificationEvents);
    Assert.assertTrue(notificationEvents.isEmpty());
    
    
    notificationEvent = notificationRepository.createNotificationEvent(createNotificationEventOptions);
    notificationEvent = notificationRepository.createNotificationEvent(createNotificationEventOptions);
    notificationEvent = notificationRepository.createNotificationEvent(createNotificationEventOptions);
    notificationEvents = notificationRepository.loadNotificationEvents(hasBeenPublished, isDraft);
    Assert.assertNotNull(notificationEvents);
    Assert.assertTrue(notificationEvents.size() == 3);

    
    notificationEvents = notificationRepository.loadAllNotificationEvents();
    Assert.assertNotNull(notificationEvents);
    Assert.assertTrue(notificationEvents.size() == 3);
    
    
    Set<Integer> newlyPublishedNotificationEventIds = new HashSet<>();
    for (NotificationEventEntity ne: notificationEvents) {
      newlyPublishedNotificationEventIds.add(ne.getPersistentIdentity());
    }
    notificationRepository.updateNotificationEventsAsPublished(newlyPublishedNotificationEventIds);
    notificationEvents = notificationRepository.loadAllNotificationEvents();
    for (NotificationEventEntity ne: notificationEvents) {
      Assert.assertTrue(ne.getHasBeenPublished());
    }
    
    
    notificationRepository.deleteNotificationEvent(notificationEvent.getPersistentIdentity());
    notificationEvents = notificationRepository.loadAllNotificationEvents();
    Assert.assertNotNull(notificationEvents);
    Assert.assertTrue(notificationEvents.size() == 2);
    
    createNotificationEventOptions = CreateNotificationEventOptions
        .builder()
        .withEventType("POINT_CAP_EXCEEDED")
        .withCustomerId(customerId)
        .build();
    notificationEvent = notificationRepository.createNotificationEvent(createNotificationEventOptions);
    Assert.assertNotNull(notificationEvent);
    notificationEvents = notificationRepository.loadAllNotificationEvents();
    Assert.assertNotNull(notificationEvents);
    Assert.assertTrue(notificationEvents.size() == 3);    
    
    /*
    CreateNotificationEventOptions createNotificationEventOptions = CreateNotificationEventOptions
        .builder()
        .withEventType("PLANNED_SITE_MAINTENANCE")
        .build();
    NotificationEventEntity notificationEvent = notificationRepository.createNotificationEvent(createNotificationEventOptions);
    Assert.assertNotNull(notificationEvent);

    
    NotificationEventEntity reminderNotificationEvent = notificationRepository.createChildNotificationEvent(
        notificationEvent.getPersistentIdentity(),
        "2022-12-31 00:00:00",
        "Reminder details");
    Assert.assertNotNull(reminderNotificationEvent);
    
    
    boolean hasBeenPublished = false;
    List<NotificationEventEntity> notificationEvents = notificationRepository.loadNotificationEvents(hasBeenPublished);
    Assert.assertNotNull(notificationEvents);
    Assert.assertFalse(notificationEvents.isEmpty());
    
    
    notificationRepository.deleteAllNotificationEvents();
    notificationEvents = notificationRepository.loadNotificationEvents(hasBeenPublished);
    Assert.assertNotNull(notificationEvents);
    Assert.assertTrue(notificationEvents.isEmpty());
    
    
    notificationEvent = notificationRepository.createNotificationEvent(createNotificationEventOptions);
    notificationEvent = notificationRepository.createNotificationEvent(createNotificationEventOptions);
    notificationEvent = notificationRepository.createNotificationEvent(createNotificationEventOptions);
    notificationEvents = notificationRepository.loadNotificationEvents(hasBeenPublished);
    Assert.assertNotNull(notificationEvents);
    Assert.assertTrue(notificationEvents.size() == 3);

    
    notificationEvents = notificationRepository.loadAllNotificationEvents();
    Assert.assertNotNull(notificationEvents);
    Assert.assertTrue(notificationEvents.size() == 3);
    
    
    Set<Integer> newlyPublishedNotificationEventIds = new HashSet<>();
    for (NotificationEventEntity ne: notificationEvents) {
      newlyPublishedNotificationEventIds.add(ne.getPersistentIdentity());
    }
    notificationRepository.updateNotificationEventsAsPublished(newlyPublishedNotificationEventIds);
    notificationEvents = notificationRepository.loadAllNotificationEvents();
    for (NotificationEventEntity ne: notificationEvents) {
      Assert.assertTrue(ne.getHasBeenPublished());
    }
    
    
    notificationRepository.deleteNotificationEvent(notificationEvent.getPersistentIdentity());
    notificationEvents = notificationRepository.loadAllNotificationEvents();
    Assert.assertNotNull(notificationEvents);
    Assert.assertTrue(notificationEvents.size() == 2);
    */
  }
  
  @Test
  public void userNotifications_lifecycle() throws Exception {
    
    String distributorName = "Test Distributor Name";
    
    AbstractDistributorEntity distributor = distributorService.createDistributor(
        RESOLUTE_DISTRIBUTOR_ID, 
        DistributorType.ONLINE,
        distributorName,
        UnitSystem.IP.toString(),
        false);

    String customerName = "Test Customer Name";
    AbstractCustomerEntity customer = customerService.createCustomer(
        distributor, 
        CustomerType.ONLINE,
        customerName,
        UnitSystem.IP.toString());

    String portfolioName = "Test_Customer_Portfolio_Node";
    String portfolioDisplayName = "Test Customer Portfolio Node";
    nodeHierarchyService.createPortfolio(
        customer, 
        portfolioName, 
        portfolioDisplayName);
    
    UserRoleType userRole = UserRoleType.DISTRIBUTOR_ADMIN;
    String email = "username@company.com";
    String firstName = "First Name";
    String lastName = "Last Name";
    Boolean isAccountManager = Boolean.TRUE;    
    AbstractUserEntity user = userRepository.createDistributorUser(
        distributor, 
        userRole, 
        email, 
        firstName, 
        lastName,
        isAccountManager);
    Integer userId = user.getPersistentIdentity();
    
    CreateNotificationEventOptions createNotificationEventOptions = CreateNotificationEventOptions
        .builder()
        .withEventType("PLANNED_SITE_MAINTENANCE")
        .build();
    NotificationEventEntity notificationEvent1 = notificationRepository.createNotificationEvent(createNotificationEventOptions);
    Integer notificationEventId1 = notificationEvent1.getPersistentIdentity();
    
    Set<AbstractUserEntity> users = new HashSet<>();
    users.add(user);
    List<UserNotificationDto> userNotificationDtos = notificationRepository.createUserNotifications(
        users,
        notificationEvent1);
    Assert.assertNotNull(userNotificationDtos);
    Assert.assertTrue(userNotificationDtos.size() == 1);
    
    List<UserNotificationEntity> userNotifications = notificationRepository.loadUserNotifications(userId);
    Assert.assertNotNull(userNotifications);
    Assert.assertFalse(userNotifications.isEmpty());
    
    notificationRepository.deleteAllUserNotifications(userId);
    userNotifications = notificationRepository.loadUserNotifications(userId);
    Assert.assertNotNull(userNotifications);
    Assert.assertTrue(userNotifications.isEmpty());

    userNotificationDtos = notificationRepository.createUserNotifications(
        users,
        notificationEvent1);
    Assert.assertNotNull(userNotificationDtos);
    Assert.assertTrue(userNotificationDtos.size() == 1);

    NotificationEventEntity notificationEvent2 = notificationRepository.createNotificationEvent(createNotificationEventOptions);
    userNotificationDtos = notificationRepository.createUserNotifications(
        users,
        notificationEvent2);
    Assert.assertNotNull(userNotificationDtos);
    Assert.assertTrue(userNotificationDtos.size() == 1);
    
    
    notificationRepository.markUserNotificationAsRead(userId, notificationEventId1);
    userNotifications = notificationRepository.loadUserNotifications(userId);
    for (UserNotificationEntity un: userNotifications) {
      if (un.getNotificationEvent().getPersistentIdentity().equals(notificationEventId1)) {
        Assert.assertTrue(un.getHasBeenRead());
      }
    }
    
    
    notificationRepository.markAllUserNotificationsAsRead(userId);
    userNotifications = notificationRepository.loadUserNotifications(userId);
    for (UserNotificationEntity un: userNotifications) {
      Assert.assertTrue(un.getHasBeenRead());
    }
    
    
    notificationRepository.markUserNotificationAsUnread(userId, notificationEventId1);
    userNotifications = notificationRepository.loadUserNotifications(userId);
    for (UserNotificationEntity un: userNotifications) {
      if (un.getNotificationEvent().getPersistentIdentity().equals(notificationEventId1)) {
        Assert.assertFalse(un.getHasBeenRead());
      }
    }
    
    
    notificationRepository.disableEmailUserNotification(userId, "CONNECTOR_STATUS");
    
    
    boolean loadDistributorPaymentMethods = false;
    boolean loadDistributorUsers = true;
    user = userRepository.loadUser(userId, distributorRepository.getRootDistributor(loadDistributorPaymentMethods, loadDistributorUsers));
    Assert.assertTrue(user.getDisabledEmailNotifications().contains("CONNECTOR_STATUS"));
    
    
    notificationRepository.deleteUserNotification(userId, notificationEventId1);
    userNotifications = notificationRepository.loadUserNotifications(userId);
    Assert.assertNotNull(userNotifications);
    Assert.assertTrue(userNotifications.size() == 1);
    
    
    notificationRepository.deleteAllUserNotifications();
    userNotifications = notificationRepository.loadUserNotifications(userId);
    Assert.assertNotNull(userNotifications);
    Assert.assertTrue(userNotifications.isEmpty());
  }
}  
//@formatter:on