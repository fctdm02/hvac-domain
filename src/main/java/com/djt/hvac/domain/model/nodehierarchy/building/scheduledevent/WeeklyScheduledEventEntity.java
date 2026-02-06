package com.djt.hvac.domain.model.nodehierarchy.building.scheduledevent;

import java.sql.Date;
import java.util.Set;

import com.djt.hvac.domain.model.nodehierarchy.building.enums.DayOfWeek;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.ScheduledAsyncComputedPointEntity;

public class WeeklyScheduledEventEntity extends AbstractRecurringScheduledEventEntity {
  private static final long serialVersionUID = 1L;
  private Set<DayOfWeek> daysOfWeek;

  public WeeklyScheduledEventEntity(
      Integer persistentIdentity,
      ScheduledAsyncComputedPointEntity parentScheduledPoint,
      Date startDate,
      Date endDate,
      Date startTime,
      Date endTime,
      Integer everyXWeeks,
      Set<DayOfWeek> daysOfWeek) {
    super(
        persistentIdentity,
        parentScheduledPoint,
        startDate,
        endDate,
        startTime,
        endTime,
        everyXWeeks);
    this.daysOfWeek = daysOfWeek;
  }

  public Integer getEveryXWeeks() {
    return super.getEveryX();
  }

  public Set<DayOfWeek> getDaysOfWeek() {
    return daysOfWeek;
  }
}
