package com.djt.hvac.domain.model.nodehierarchy.building.scheduledevent;

import java.sql.Date;

import com.djt.hvac.domain.model.nodehierarchy.building.enums.OnDayQualifier;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.OnNonDayQualifier;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.OnQualifier;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.ScheduledAsyncComputedPointEntity;

public class MonthlyScheduledEventEntity extends AbstractOnDayNonDayQualifiedScheduledEventEntity {
  private static final long serialVersionUID = 1L;
  public MonthlyScheduledEventEntity(
      Integer persistentIdentity,
      ScheduledAsyncComputedPointEntity parentScheduledPoint,
      Date startDate,
      Date endDate,
      Date startTime,
      Date endTime,
      Integer everyXMonths,
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
        everyXMonths,
        onQualifier,
        onDayQualifier,
        onNonDayQualifier);
  }

  public Integer getEveryXMonths() {
    return super.getEveryX();
  }
}
