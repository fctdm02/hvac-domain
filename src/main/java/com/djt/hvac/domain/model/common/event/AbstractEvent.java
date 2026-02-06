package com.djt.hvac.domain.model.common.event;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author tmyers
 *
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class AbstractEvent implements Serializable {

  private static final long serialVersionUID = 1L;
  
  public static final String EVENT_UUID_KEY = "eventUuid";
  public static final String OCCURRED_ON_DATE_KEY = "occurredOnDate";
  public static final String OWNER_KEY = "owner";
  
  public static final String DEFAULT_OWNER_VALUE = "system";

  private final UUID eventUuid;
  private final Timestamp occurredOnDate;
  private final String owner;
  
  protected <T extends AbstractEvent, B extends Builder<T, B>> AbstractEvent (B builder) {
    this.eventUuid = builder.eventUuid();
    this.occurredOnDate = builder.occurredOnDate();
    this.owner = builder.owner();
  }

  /**
   * 
   * @return The unique identifier for the event
   */
  public UUID getEventUuid() {
    return this.eventUuid;
  }

  /**
   * 
   * @return The timestamp on which the event occurred
   */
  public Timestamp getOccurredOnDate() {
    return this.occurredOnDate;
  }

  /**
   * 
   * @return The owner of the event (i.e. who initiated the event and can be either a human or
   *         system identifier)
   */
  public String getOwner() {
    return this.owner;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((eventUuid == null) ? 0 : eventUuid.hashCode());
    result = prime * result + ((occurredOnDate == null) ? 0 : occurredOnDate.hashCode());
    result = prime * result + ((owner == null) ? 0 : owner.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AbstractEvent other = (AbstractEvent) obj;
    if (eventUuid == null) {
      if (other.eventUuid != null)
        return false;
    } else if (!eventUuid.equals(other.eventUuid))
      return false;
    if (occurredOnDate == null) {
      if (other.occurredOnDate != null)
        return false;
    } else if (!occurredOnDate.equals(other.occurredOnDate))
      return false;
    if (owner == null) {
      if (other.owner != null)
        return false;
    } else if (!owner.equals(other.owner))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "AbstractEvent [eventUuid=" + eventUuid + ", occurredOnDate=" + occurredOnDate
        + ", owner=" + owner + "]";
  }
  
  public abstract static class Builder <T extends AbstractEvent, B extends Builder<T, B>> {
    
    private UUID eventUuid = UUID.randomUUID();
    private Timestamp occurredOnDate = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
    private String owner = DEFAULT_OWNER_VALUE;
    
    protected Builder () {}
    
    public B withEventUuid(UUID eventUuid) {
      this.eventUuid = requireNonNull(eventUuid, "eventUuid cannot be null");
      return getThis();
    }

    public B withOccurredOnDate(Timestamp occurredOnDate) {
      this.occurredOnDate = requireNonNull(occurredOnDate, "occurredOnDate cannot be null");
      return getThis();
    }

    public B withOwner(String owner) {
      this.owner = requireNonNull(owner, "owner cannot be null");
      return getThis();
    }

    protected UUID eventUuid() {
      return eventUuid;
    }

    protected Timestamp occurredOnDate() {
      return occurredOnDate;
    }

    protected String owner() {
      return owner;
    }

    public T build () {
      requireNonNull(eventUuid, "eventUuid cannot be null");
      requireNonNull(occurredOnDate, "occurredOnDate cannot be null");
      requireNonNull(owner, "owner cannot be null");
      return newInstance();
    }

    protected abstract B getThis ();
    
    protected abstract T newInstance ();
  }    
}