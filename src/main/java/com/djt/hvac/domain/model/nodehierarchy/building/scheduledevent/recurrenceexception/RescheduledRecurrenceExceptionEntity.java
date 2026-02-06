package com.djt.hvac.domain.model.nodehierarchy.building.scheduledevent.recurrenceexception;

import java.sql.Date;

public class RescheduledRecurrenceExceptionEntity extends AbstractRecurrenceExceptionEntity {
  private static final long serialVersionUID = 1L;
  private String startTime;
  private String endTime;

  public RescheduledRecurrenceExceptionEntity(
      Integer persistentIdentity,
      Date startDate,
      String startTime,
      String endTime) {
    super(persistentIdentity, startDate);
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public String getStartTime() {
    return startTime;
  }

  public String getEndTime() {
    return endTime;
  }

  public String getNaturalIdentity() {
    throw new RuntimeException("Not implemented yet.");
  }
}
