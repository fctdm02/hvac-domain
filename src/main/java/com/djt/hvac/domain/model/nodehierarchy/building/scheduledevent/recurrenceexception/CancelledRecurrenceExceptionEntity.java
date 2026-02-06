package com.djt.hvac.domain.model.nodehierarchy.building.scheduledevent.recurrenceexception;

import java.sql.Date;

public class CancelledRecurrenceExceptionEntity extends AbstractRecurrenceExceptionEntity {
  private static final long serialVersionUID = 1L;
  public CancelledRecurrenceExceptionEntity(
      Integer persistentIdentity,
      Date startDate) {
    super(persistentIdentity, startDate);
  }

  public String getNaturalIdentity() {
    throw new RuntimeException("Not implemented yet.");
  }
}
