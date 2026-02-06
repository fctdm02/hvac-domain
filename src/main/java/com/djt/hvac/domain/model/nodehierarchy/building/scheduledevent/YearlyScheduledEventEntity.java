package com.djt.hvac.domain.model.nodehierarchy.building.scheduledevent;

import java.sql.Date;

import com.djt.hvac.domain.model.nodehierarchy.building.enums.EveryMonth;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.OnDayQualifier;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.OnNonDayQualifier;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.OnQualifier;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.ScheduledAsyncComputedPointEntity;

public class YearlyScheduledEventEntity extends AbstractOnDayNonDayQualifiedScheduledEventEntity {
  private static final long serialVersionUID = 1L;
  private EveryMonth everyMonth;

  public YearlyScheduledEventEntity(
      Integer persistentIdentity,
      ScheduledAsyncComputedPointEntity parentScheduledPoint,
      Date startDate,
      Date endDate,
      Date startTime,
      Date endTime,
      EveryMonth everyMonth,
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
        Integer.valueOf(-1), // everyX has no meaning for yearly.
        onQualifier,
        onDayQualifier,
        onNonDayQualifier);
    this.everyMonth = everyMonth;
  }

  public EveryMonth getEveryMonth() {
    return everyMonth;
  }
}
