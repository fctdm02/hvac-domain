package com.djt.hvac.domain.model.nodehierarchy.building.scheduledevent;

import java.sql.Date;

import com.djt.hvac.domain.model.nodehierarchy.building.enums.OnDayQualifier;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.OnNonDayQualifier;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.OnQualifier;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.ScheduledAsyncComputedPointEntity;

public abstract class AbstractOnDayNonDayQualifiedScheduledEventEntity extends AbstractRecurringScheduledEventEntity {
  private static final long serialVersionUID = 1L;
  private OnQualifier onQualifier;
  private OnDayQualifier onDayQualifier;
  private OnNonDayQualifier onNonDayQualifier;

  public AbstractOnDayNonDayQualifiedScheduledEventEntity(
      Integer persistentIdentity,
      ScheduledAsyncComputedPointEntity parentScheduledPoint,
      Date startDate,
      Date endDate,
      Date startTime,
      Date endTime,
      Integer everyX,
      OnQualifier onQualifier,
      OnDayQualifier onDayQualifier,
      OnNonDayQualifier onNonDayQualifier) {
    super(
        persistentIdentity,
        parentScheduledPoint,
        startDate,
        endDate,
        startTime,
        endTime,
        everyX);
    this.onDayQualifier = onDayQualifier;
    this.onDayQualifier = onDayQualifier;
    this.onNonDayQualifier = onNonDayQualifier;
  }

  public OnQualifier getOnQualifier() {
    return onQualifier;
  }

  public OnDayQualifier getOnDayQualifier() {
    return onDayQualifier;
  }

  public OnNonDayQualifier getOnNonDayQualifier() {
    return onNonDayQualifier;
  }
}
