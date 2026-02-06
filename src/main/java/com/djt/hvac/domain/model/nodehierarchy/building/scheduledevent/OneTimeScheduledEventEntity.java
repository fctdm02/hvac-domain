package com.djt.hvac.domain.model.nodehierarchy.building.scheduledevent;

import java.sql.Date;

import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.ScheduledAsyncComputedPointEntity;

public class OneTimeScheduledEventEntity extends AbstractScheduledEventEntity {
  private static final long serialVersionUID = 1L;
  public OneTimeScheduledEventEntity(
      Integer persistentIdentity,
      ScheduledAsyncComputedPointEntity parentScheduledPoint,
      Date startDate,
      Date startTime,
      Date endTime) {
    super(
        persistentIdentity,
        parentScheduledPoint,
        startDate,
        startDate,
        startTime,
        endTime);
  }
}
