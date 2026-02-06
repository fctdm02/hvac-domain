package com.djt.hvac.domain.model.notification.entity;
/*
package com.djt.hvac.domain.model.notification.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;

import com.djt.hvac.domain.model.notification.enums.NotificationEventType;

public class NotificationEventEntityTest {

  @Test
  public void compareTo() {

    // STEP 1: ARRANGE
    List<NotificationEventEntity> list = new ArrayList<>();
    list.add(new NotificationEventEntity(
        1,
        NotificationEventType.get("PLANNED_SITE_MAINTENANCE"),
        null,
        null,
        false,
        new TreeMap<>(),
        null,
        "details",
        true,
        true,
        null));

    list.add(new NotificationEventEntity(
        2,
        NotificationEventType.get("RELEASE_NOTES"),
        null,
        null,
        false,
        new TreeMap<>(),
        null,
        "details",
        true,
        true,
        null));

    list.add(new NotificationEventEntity(
        3,
        NotificationEventType.get("NEW_RULES_REPORTS_ADDED"),
        null,
        null,
        false,
        new TreeMap<>(),
        null,
        "details",
        true,
        true,
        null));


    // STEP 2: ACT
    Collections.sort(list);



    // STEP 3: ASSERT
    // VERIFY THAT UNPUBLISHED NOTIFICATIONS ARE AT THE TOP
    NotificationEventEntity ne = list.get(0);
    Assert.assertTrue(ne.toString().contains("hasBeenPublished=false"));  
  }
}
*/