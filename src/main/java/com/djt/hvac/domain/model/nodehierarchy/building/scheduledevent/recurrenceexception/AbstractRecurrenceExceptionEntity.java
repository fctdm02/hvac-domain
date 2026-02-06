package com.djt.hvac.domain.model.nodehierarchy.building.scheduledevent.recurrenceexception;

import java.sql.Date;
import java.util.List;
import java.util.Set;

import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;

public abstract class AbstractRecurrenceExceptionEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  private Date startDate;

  public AbstractRecurrenceExceptionEntity(
      Integer persistentIdentity,
      Date startDate) {
    super(persistentIdentity);
    this.startDate = startDate;
  }

  public Date getStartDate() {
    return startDate;
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
