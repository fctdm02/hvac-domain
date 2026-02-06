package com.djt.hvac.domain.model.notification.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.distributor.OutOfBandDistributorEntity;
import com.djt.hvac.domain.model.notification.entity.NotificationEventEntity;
import com.djt.hvac.domain.model.notification.entity.UserNotificationEntity;
import com.djt.hvac.domain.model.notification.enums.NotificationEventType;
import com.djt.hvac.domain.model.user.DistributorUserEntity;
import com.djt.hvac.domain.model.user.enums.UserRoleType;

public class UserNotificationEntityTest {

  @Test
  public void compareTo() throws EntityAlreadyExistsException {

    // STEP 1: ARRANGE
    OutOfBandDistributorEntity distributor = OutOfBandDistributorEntity.buildResoluteDistributorStub();
    
    DistributorUserEntity user = new DistributorUserEntity(
        null,
        UserRoleType.DISTRIBUTOR_ADMIN,
        "email@company.com",
        "first",
        "last",
        false,
        true,
        distributor,
        Boolean.TRUE);
    distributor.addChildDistributorUser(user);
    
    List<NotificationEventEntity> notificationEvents = new ArrayList<>();
    notificationEvents.add(new NotificationEventEntity(
        1,
        NotificationEventType.get("PLANNED_SITE_MAINTENANCE"),
        Integer.valueOf(4),
        UUID.randomUUID(),
        AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
        null,
        false,
        new TreeMap<>(),
        null,
        "details",
        true,
        true,
        null));

    notificationEvents.add(new NotificationEventEntity(
        2,
        NotificationEventType.get("RELEASE_NOTES"),
        Integer.valueOf(4),
        UUID.randomUUID(),
        AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
        null,
        false,
        new TreeMap<>(),
        null,
        "details",
        true,
        true,
        null));

    notificationEvents.add(new NotificationEventEntity(
        3,
        NotificationEventType.get("NEW_RULES_REPORTS_ADDED"),
        Integer.valueOf(4),
        UUID.randomUUID(),
        AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
        null,
        false,
        new TreeMap<>(),
        null,
        "details",
        true,
        true,
        null));
    
    List<UserNotificationEntity> userNotifications = new ArrayList<>();
    for (int i=0; i < notificationEvents.size(); i++) {
      
      boolean hasBeenRead = true;
      if (i > 0) {
        hasBeenRead = false;
      }
      
      boolean hasBeenEmailed = false;
      userNotifications.add(new UserNotificationEntity(
          user,
          notificationEvents.get(i),
          hasBeenRead,
          hasBeenEmailed));
    }


    // STEP 2: ACT
    Collections.sort(userNotifications);



    // STEP 3: ASSERT
    // VERIFY THAT UNREAD NOTIFICATIONS ARE AT THE TOP
    UserNotificationEntity userNotification = userNotifications.get(0);
    Assert.assertTrue(userNotification.toString().contains("hasBeenRead=false"));  
  }
}