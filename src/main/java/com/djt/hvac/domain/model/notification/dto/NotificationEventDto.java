//@formatter:off
package com.djt.hvac.domain.model.notification.dto;

import java.util.SortedMap;

public class NotificationEventDto {
  
  private Integer id;
  private String eventUuid;
  private String occurredOnDate;
  private String eventType;
  private Integer customerId;
  private Integer parentEventId;
  private boolean isDownStatus;
  private SortedMap<String, String> substitutionTokenValues;
  private String expirationDate;
  private String details = "";
  private boolean hasBeenPublished;
  private boolean isDraft = true;
  private String publishedBy;
  private String appType = "SYNERGY";
  
  public NotificationEventDto() {
  }

  public Integer getId() {
    return id;
  }
  public void setId(Integer id) {
    this.id = id;
  }
  public String getEventUuid() {
    return eventUuid;
  }
  public void setEventUuid(String eventUuid) {
    this.eventUuid = eventUuid;
  }
  public String getOccurredOnDate() {
    return occurredOnDate;
  }
  public void setOccurredOnDate(String occurredOnDate) {
    this.occurredOnDate = occurredOnDate;
  }
  public String getEventType() {
    return eventType;
  }
  public void setEventType(String eventType) {
    this.eventType = eventType;
  }
  public Integer getCustomerId() {
    return customerId;
  }
  public void setCustomerId(Integer customerId) {
    this.customerId = customerId;
  }
  public Integer getParentEventId() {
    return parentEventId;
  }
  public void setParentEventId(Integer parentEventId) {
    this.parentEventId = parentEventId;
  }
  public boolean getIsDownStatus() {
    return isDownStatus;
  }
  public void setIsDownStatus(boolean isDownStatus) {
    this.isDownStatus = isDownStatus;
  }
  public SortedMap<String, String> getSubstitutionTokenValues() {
    return substitutionTokenValues;
  }
  public void setSubstitutionTokenValues(SortedMap<String, String> substitutionTokenValues) {
    this.substitutionTokenValues = substitutionTokenValues;
  }
  public String getExpirationDate() {
    return expirationDate;
  }
  public void setExpirationDate(String expirationDate) {
    this.expirationDate = expirationDate;
  }
  public String getDetails() {
    return details;
  }
  public void setDetails(String details) {
    if (details != null) {
      this.details = details;
    } else {
      this.details = "";
    }
  }
  public boolean getHasBeenPublished() {
    return hasBeenPublished;
  }
  public void setHasBeenPublished(boolean hasBeenPublished) {
    this.hasBeenPublished = hasBeenPublished;
  }
  public boolean getIsDraft() {
    return isDraft;
  }
  public void setIsDraft(boolean isDraft) {
    this.isDraft = isDraft;
  }  
  public String getPublishedBy() {
    return publishedBy;
  }
  public void setPublishedBy(String publishedBy) {
    this.publishedBy = publishedBy;
  } 
  public String getAppType() {
    return appType;
  }
  public void setAppType(String appType) {
    this.appType = appType;
  } 
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((customerId == null) ? 0 : customerId.hashCode());
    result = prime * result + ((details == null) ? 0 : details.hashCode());
    result = prime * result + ((eventType == null) ? 0 : eventType.hashCode());
    result = prime * result + ((eventUuid == null) ? 0 : eventUuid.hashCode());
    result = prime * result + ((expirationDate == null) ? 0 : expirationDate.hashCode());
    result = prime * result + (hasBeenPublished ? 1231 : 1237);
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + (isDownStatus ? 1231 : 1237);
    result = prime * result + ((occurredOnDate == null) ? 0 : occurredOnDate.hashCode());
    result = prime * result + ((parentEventId == null) ? 0 : parentEventId.hashCode());
    result = prime * result
        + ((substitutionTokenValues == null) ? 0 : substitutionTokenValues.hashCode());
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
    NotificationEventDto other = (NotificationEventDto) obj;
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
    if (eventUuid == null) {
      if (other.eventUuid != null)
        return false;
    } else if (!eventUuid.equals(other.eventUuid))
      return false;
    if (expirationDate == null) {
      if (other.expirationDate != null)
        return false;
    } else if (!expirationDate.equals(other.expirationDate))
      return false;
    if (hasBeenPublished != other.hasBeenPublished)
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (isDownStatus != other.isDownStatus)
      return false;
    if (occurredOnDate == null) {
      if (other.occurredOnDate != null)
        return false;
    } else if (!occurredOnDate.equals(other.occurredOnDate))
      return false;
    if (parentEventId == null) {
      if (other.parentEventId != null)
        return false;
    } else if (!parentEventId.equals(other.parentEventId))
      return false;
    if (substitutionTokenValues == null) {
      if (other.substitutionTokenValues != null)
        return false;
    } else if (!substitutionTokenValues.equals(other.substitutionTokenValues))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("NotificationEventDto [id=").append(id).append(", eventUuid=").append(eventUuid)
        .append(", occurredOnDate=").append(occurredOnDate).append(", eventType=").append(eventType)
        .append(", customerId=").append(customerId).append(", parentEventId=").append(parentEventId)
        .append(", isDownStatus=").append(isDownStatus).append(", substitutionTokenValues=")
        .append(substitutionTokenValues).append(", expirationDate=").append(expirationDate)
        .append(", details=").append(details).append(", hasBeenPublished=").append(hasBeenPublished)
        .append(", isDraft=").append(isDraft).append(", publishedBy=").append(publishedBy)
        .append(", appType=").append(appType).append("]");
    return builder.toString();
  }
}
//@formatter:on