package com.djt.hvac.domain.model.nodehierarchy.building.scheduledevent;

import java.sql.Date;

import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.ScheduledAsyncComputedPointEntity;

public class DailyScheduledEventEntity extends AbstractRecurringScheduledEventEntity {
  private static final long serialVersionUID = 1L;
  public DailyScheduledEventEntity(
      Integer persistentIdentity,
      ScheduledAsyncComputedPointEntity parentScheduledPoint,
      Date startDate,
      Date endDate,
      Date startTime,
      Date endTime,
      Integer everyXDays) {
    super(
        persistentIdentity,
        parentScheduledPoint,
        startDate,
        endDate,
        startTime,
        endTime,
        everyXDays);
  }

  public Integer getEveryXDays() {
    return super.getEveryX();
  }
}
