package com.djt.hvac.domain.model.nodehierarchy.building.scheduledevent;

import java.sql.Date;
import java.util.List;
import java.util.Set;

import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.nodehierarchy.point.computed.async.ScheduledAsyncComputedPointEntity;

public abstract class AbstractScheduledEventEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  private final ScheduledAsyncComputedPointEntity parentScheduledPoint;
  private Date startDate;
  private Date endDate;
  private Date startTime;
  private Date endTime;

  public AbstractScheduledEventEntity(
      Integer persistentIdentity,
      ScheduledAsyncComputedPointEntity parentScheduledPoint,
      Date startDate,
      Date endDate,
      Date startTime,
      Date endTime) {
    super(persistentIdentity);
    this.parentScheduledPoint = parentScheduledPoint;
    this.startDate = startDate;
    this.endDate = endDate;
    this.startTime = startTime;
    this.endTime = endTime;
  }
  
  public ScheduledAsyncComputedPointEntity getParentScheduledPoint() {
    return parentScheduledPoint;
  }

  public Date getStartDate() {
    return startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public Date getStartTime() {
    return startTime;
  }

  public Date getEndTime() {
    return endTime;
  }

  public ScheduledEventOccurrence getScheduledEventOccurrence(Date date) {
    throw new RuntimeException("Not implemented yet");
  }

  public String getNaturalIdentity() {
    throw new RuntimeException("Not implemented yet.");
  }

  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages,
      boolean remediate) {}
}
