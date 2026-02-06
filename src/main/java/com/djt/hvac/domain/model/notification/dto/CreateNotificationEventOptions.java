package com.djt.hvac.domain.model.notification.dto;

import static java.util.Objects.requireNonNull;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Consumer;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.notification.entity.NotificationEventEntity;
import com.djt.hvac.domain.model.notification.enums.NotificationCategory;
import com.djt.hvac.domain.model.notification.enums.NotificationEventType;
import com.djt.hvac.domain.model.notification.enums.NotificationProducer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = CreateNotificationEventOptions.Builder.class)
public class CreateNotificationEventOptions {
  
  public static final int NUM_DAYS_TO_RETAIN_NOTIFICATION_EVENT = 90;
  
  private final String eventType; // For non-child events only
  private final Integer customerId; // For non-child events only
  
  private final Integer parentEventId; // For child events only
  private final Boolean isDownStatus; // For child events only whose category is: APPLICATION_ALERT.  These notifications are of the DOWN/UP variety.
  
  private final String expirationDate;
  private final SortedMap<String, String> substitutionTokenValues;
  private final String details;
  private final Boolean publishImmediately;
  private final String publishedBy;
  private final String appType; // SYNERGY, FUSION or BOTH

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (CreateNotificationEventOptions createNotificationEventOptions) {
    return new Builder(createNotificationEventOptions);
  }

  private CreateNotificationEventOptions (Builder builder) {
    this.eventType = builder.eventType;
    this.customerId = builder.customerId;
    this.parentEventId = builder.parentEventId;
    this.isDownStatus = builder.isDownStatus;
    this.expirationDate = builder.expirationDate;
    this.substitutionTokenValues = builder.substitutionTokenValues;
    this.details = builder.details;
    this.publishImmediately = builder.publishImmediately;
    this.publishedBy = builder.publishedBy;
    this.appType = builder.appType;
  }

  public String getEventType() {
    return eventType;
  }

  public Integer getCustomerId() {
    return customerId;
  }
  
  public Integer getParentEventId() {
    return parentEventId;
  }
  
  public Boolean getIsDownStatus() {
    return isDownStatus;
  }

  public String getExpirationDate() {
    return expirationDate;
  }
  
  public SortedMap<String, String> getSubstitutionTokenValues() {
    return this.substitutionTokenValues;
  }

  public String getDetails() {
    return details;
  }
  
  public Boolean getPublishImmediately() {
    return publishImmediately;
  }
  
  public String getPublishedBy() {
    return publishedBy;
  }
  
  public String getAppType() {
    return appType;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((customerId == null) ? 0 : customerId.hashCode());
    result = prime * result + ((details == null) ? 0 : details.hashCode());
    result = prime * result + ((eventType == null) ? 0 : eventType.hashCode());
    result = prime * result + ((expirationDate == null) ? 0 : expirationDate.hashCode());
    result = prime * result + ((isDownStatus == null) ? 0 : isDownStatus.hashCode());
    result = prime * result + ((parentEventId == null) ? 0 : parentEventId.hashCode());
    result = prime * result + ((publishImmediately == null) ? 0 : publishImmediately.hashCode());
    result = prime * result + ((substitutionTokenValues == null) ? 0 : substitutionTokenValues.hashCode());
    result = prime * result + ((appType == null) ? 0 : appType.hashCode());
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
    CreateNotificationEventOptions other = (CreateNotificationEventOptions) obj;
    if (customerId == null) {
      if (other.customerId != null)
        return false;
    } else if (!customerId.equals(other.customerId))
      return false;
    if (details == null) {
      if (other.details != null)
        return false;
    } else if (!details.equals(other.details))
      return false;
    if (eventType == null) {
      if (other.eventType != null)
        return false;
    } else if (!eventType.equals(other.eventType))
      return false;
    if (expirationDate == null) {
      if (other.expirationDate != null)
        return false;
    } else if (!expirationDate.equals(other.expirationDate))
      return false;
    if (isDownStatus == null) {
      if (other.isDownStatus != null)
        return false;
    } else if (!isDownStatus.equals(other.isDownStatus))
      return false;
    if (parentEventId == null) {
      if (other.parentEventId != null)
        return false;
    } else if (!parentEventId.equals(other.parentEventId))
      return false;
    if (publishImmediately == null) {
      if (other.publishImmediately != null)
        return false;
    } else if (!publishImmediately.equals(other.publishImmediately))
      return false;
    if (substitutionTokenValues == null) {
      if (other.substitutionTokenValues != null)
        return false;
    } else if (!substitutionTokenValues.equals(other.substitutionTokenValues))
      return false;
    if (appType == null) {
      if (other.appType != null)
        return false;
    } else if (!appType.equals(other.appType))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder2 = new StringBuilder();
    builder2.append("CreateNotificationEventOptions [eventType=").append(eventType)
        .append(", customerId=").append(customerId).append(", parentEventId=").append(parentEventId)
        .append(", isDownStatus=").append(isDownStatus).append(", expirationDate=")
        .append(expirationDate).append(", substitutionTokenValues=").append(substitutionTokenValues)
        .append(", details=").append(details).append(", publishImmediately=")
        .append(publishImmediately).append("]");
    return builder2.toString();
  }

  public void validate() {
   
    if (parentEventId == null && eventType == null) {
      throw new IllegalArgumentException("Either one of 'eventType' or 'parentEventId' must be specified, but not both");
    } else if (parentEventId != null && eventType != null) {
      throw new IllegalArgumentException("Only one of 'eventType' or 'parentEventId' must be specified, but not both");
    }
    
    if (eventType != null) {

      NotificationEventType et = NotificationEventType.get(eventType);
      NotificationCategory category = et.getCategory();
      if (category.equals(NotificationCategory.APPLICATION_ALERT) && isDownStatus == null) {
        
        throw new IllegalArgumentException("'isDownStatus' must be specified (true/false)");      
      }
    }
  }
  
  public void validateUserGeneratedEvent() {
    
    NotificationEventType et = NotificationEventType.get(eventType);
    NotificationProducer producer = et.getProducer();
    if (!producer.equals(NotificationProducer.USER)) {
      
      throw new IllegalArgumentException("'eventType' must be one of: " + NotificationProducer.getValues());      
    }
  }

  @JsonPOJOBuilder
  public static class Builder {
    private String eventType;
    private Integer customerId;
    private Integer parentEventId;
    private Boolean isDownStatus = Boolean.FALSE;
    private String expirationDate;
    private SortedMap<String, String> substitutionTokenValues = new TreeMap<>();
    private String details = "";
    private Boolean publishImmediately = Boolean.FALSE;
    private String publishedBy; // if non-null, then the notification event's "is_draft" attribute will be set to false (for SYSTEM events, there's no approval workflow)
    private String appType;

    private Builder() {}

    private Builder(CreateNotificationEventOptions createNotificationEventOptions) {
      requireNonNull(createNotificationEventOptions, "createNotificationEventOptions cannot be null");
      this.eventType = createNotificationEventOptions.eventType;
      this.customerId = createNotificationEventOptions.customerId;
      this.parentEventId = createNotificationEventOptions.parentEventId;
      this.isDownStatus = createNotificationEventOptions.isDownStatus;
      this.expirationDate = createNotificationEventOptions.expirationDate;
      this.substitutionTokenValues = createNotificationEventOptions.substitutionTokenValues;
      this.details = createNotificationEventOptions.details;
      this.publishImmediately = createNotificationEventOptions.publishImmediately;
      this.publishedBy = createNotificationEventOptions.publishedBy;
      this.appType = createNotificationEventOptions.appType;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withEventType(String eventType) {
      this.eventType = eventType;
      return this;
    }

    public Builder withCustomerId(Integer customerId) {
      this.customerId = customerId;
      return this;
    }

    public Builder withParentEventId(Integer parentEventId) {
      this.parentEventId = parentEventId;
      return this;
    }
    
    public Builder withIsDownStatus(Boolean isDownStatus) {
      if (isDownStatus != null) {
        this.isDownStatus = isDownStatus;  
      }
      return this;
    }    

    public Builder withExpirationDate(String expirationDate) {
      this.expirationDate = expirationDate;
      return this;
    }

    public Builder withSubstitutionTokenValues(SortedMap<String, String> substitutionTokenValues) {
      if (substitutionTokenValues != null) {
        this.substitutionTokenValues = substitutionTokenValues;  
      }
      return this;
    }
    
    public Builder withDetails(String details) {
      if (details != null) {
        this.details = details;  
      }
      return this;
    }

    public Builder withPublishImmediately(Boolean publishImmediately) {
      if (publishImmediately != null) {
        this.publishImmediately = publishImmediately;  
      }
      return this;
    }

    public Builder withPublishedBy(String publishedBy) {
      this.publishedBy = publishedBy;
      return this;
    }
    
    public Builder withAppType(String appType) {
      
      if (appType == null) {
        throw new IllegalArgumentException("'appType' must be one of: ['SYNERGY', 'FUSION' or 'BOTH'");
      }
      this.appType = appType;
      return this;
    }

    public CreateNotificationEventOptions build() {
      
      setExpirationDate(expirationDate);
      setAppType();
      return new CreateNotificationEventOptions(this);
    }
    
    private void setExpirationDate(String expirationDate) {
      
      if (expirationDate != null) {
        if (!expirationDate.contains(":")) {
          this.expirationDate = expirationDate + " 00:00:00";
        } else {
          this.expirationDate = expirationDate;  
        }
      } else {
        this.expirationDate = AbstractEntity.formatTimestamp(AbstractEntity.getTimeKeeper().getTimestampForDaysFromCurrent(NUM_DAYS_TO_RETAIN_NOTIFICATION_EVENT));
      }
    }
    
    private void setAppType() {
      
      if (parentEventId == null) {
        this.appType = NotificationEventEntity.getAppType(NotificationEventType.get(eventType)).toString();
      }
    }
  }
}