package com.djt.hvac.domain.model.nodehierarchy.building.scheduledevent;

import java.sql.Date;
import java.util.Set;
import java.util.TreeSet;

import com.djt.hvac.domain.model.nodehierarchy.building.scheduledevent.recurrenceexception.AbstractRecurrenceExceptionEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.ScheduledAsyncComputedPointEntity;

public abstract class AbstractRecurringScheduledEventEntity extends AbstractScheduledEventEntity {
  private static final long serialVersionUID = 1L;
  private Integer everyX;
  private Set<AbstractRecurrenceExceptionEntity> recurrenceExceptions = new TreeSet<>();

  public AbstractRecurringScheduledEventEntity(
      Integer persistentIdentity,
      ScheduledAsyncComputedPointEntity parentScheduledPoint,
      Date startDate,
      Date endDate,
      Date startTime,
      Date endTime,
      Integer everyX) {
    super(
        persistentIdentity,
        parentScheduledPoint,
        startDate,
        endDate,
        startTime,
        endTime);
    this.everyX = everyX;
  }

  public Set<AbstractRecurrenceExceptionEntity> getRecurrenceExceptions() {

    return recurrenceExceptions;
  }

  protected Integer getEveryX() {
    return this.everyX;
  }
}
