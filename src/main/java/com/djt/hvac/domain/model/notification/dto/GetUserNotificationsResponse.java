package com.djt.hvac.domain.model.notification.dto;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableList;

@JsonInclude(Include.NON_ABSENT)
@JsonDeserialize(builder = GetUserNotificationsResponse.Builder.class)
public class GetUserNotificationsResponse {
  private final Integer userId;
  private final Integer parentEventId;
  private final Integer audienceScopeId;
  private final String category;
  private final String producer;
  private final String attentionLevel;
  private final List<String> presentationTypes;
  private final List<String> applicationTypes;
  private final List<String> emailTypes;
  private final String name;
  private final String displayName;
  private final Boolean emailCannotBeTurnedOff;
  private final Integer eventId;
  private final String eventUuid;
  private final String occurredOnDate;
  private final String expirationDate;
  private final Boolean isDownStatus;
  private final SortedMap<String, String> substitutionTokenValues;
  private final String details;
  private final Boolean hasBeenPublished;
  private final Boolean hasBeenRead;
  private final Boolean hasBeenEmailed;
  private final String publishedBy;
  private final String templateBody;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (GetUserNotificationsResponse getUserNotificationsResponse) {
    return new Builder(getUserNotificationsResponse);
  }

  private GetUserNotificationsResponse (Builder builder) {
    this.userId = builder.userId;
    this.parentEventId = builder.parentEventId;
    this.audienceScopeId = builder.audienceScopeId;
    this.category = builder.category;
    this.producer = builder.producer;
    this.attentionLevel = builder.attentionLevel;
    this.presentationTypes = builder.presentationTypes;
    this.applicationTypes = builder.applicationTypes;
    this.emailTypes = builder.emailTypes;
    this.name = builder.name;
    this.displayName = builder.displayName;
    this.emailCannotBeTurnedOff = builder.emailCannotBeTurnedOff;
    this.eventUuid = builder.eventUuid;
    this.occurredOnDate = builder.occurredOnDate;
    this.expirationDate = builder.expirationDate;
    this.isDownStatus = builder.isDownStatus;
    this.substitutionTokenValues = builder.substitutionTokenValues;
    this.details = builder.details;
    this.hasBeenPublished = builder.hasBeenPublished;
    this.hasBeenRead = builder.hasBeenRead;
    this.hasBeenEmailed = builder.hasBeenEmailed;
    this.eventId = builder.eventId;
    this.publishedBy = builder.publishedBy;
    this.templateBody = builder.templateBody;
  }

  public Integer getUserId() {
    return userId;
  }

  public Integer getParentEventId() {
    return parentEventId;
  }

  public Integer getAudienceScopeId() {
    return audienceScopeId;
  }

  public String getCategory() {
    return category;
  }

  public String getProducer() {
    return producer;
  }

  public String getAttentionLevel() {
    return attentionLevel;
  }

  public List<String> getPresentationTypes() {
    return presentationTypes;
  }

  public List<String> getApplicationTypes() {
    return applicationTypes;
  }

  public List<String> getEmailTypes() {
    return emailTypes;
  }

  public String getName() {
    return name;
  }

  public String getDisplayName() {
    return displayName;
  }

  public Boolean getEmailCannotBeTurnedOff() {
    return emailCannotBeTurnedOff;
  }

  public String getEventUuid() {
    return eventUuid;
  }

  public String getOccurredOnDate() {
    return occurredOnDate;
  }

  public String getExpirationDate() {
    return expirationDate;
  }

  public Boolean getIsDownStatus() {
    return isDownStatus;
  }

  public SortedMap<String, String> getSubstitutionTokenValues() {
    return substitutionTokenValues;
  }

  public String getDetails() {
    return details;
  }

  public Boolean getHasBeenPublished() {
    return hasBeenPublished;
  }

  public Boolean getHasBeenRead() {
    return hasBeenRead;
  }

  public Boolean getHasBeenEmailed() {
    return hasBeenEmailed;
  }

  public Integer getEventId() {
    return eventId;
  }
  
  public String getPublishedBy() {
    return publishedBy;
  }
  
  public String getTemplateBody() {
    return templateBody;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Objects.hash(userId, parentEventId, audienceScopeId, category, producer, attentionLevel, presentationTypes, applicationTypes, emailTypes, name, displayName, emailCannotBeTurnedOff, eventUuid, occurredOnDate, expirationDate, isDownStatus, substitutionTokenValues, details, hasBeenPublished, hasBeenRead, hasBeenEmailed, eventId);
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
    GetUserNotificationsResponse other = (GetUserNotificationsResponse) obj;
    return Objects.equals(userId, other.userId) && Objects.equals(parentEventId, other.parentEventId) && Objects.equals(audienceScopeId, other.audienceScopeId) && Objects.equals(category, other.category) && Objects.equals(producer, other.producer) && Objects.equals(attentionLevel, other.attentionLevel) && Objects.equals(presentationTypes, other.presentationTypes) && Objects.equals(applicationTypes, other.applicationTypes) && Objects.equals(emailTypes, other.emailTypes) && Objects.equals(name, other.name) && Objects.equals(displayName, other.displayName) && Objects.equals(emailCannotBeTurnedOff, other.emailCannotBeTurnedOff) && Objects.equals(eventUuid, other.eventUuid) && Objects.equals(occurredOnDate, other.occurredOnDate) && Objects.equals(expirationDate, other.expirationDate) && Objects.equals(isDownStatus, other.isDownStatus) && Objects.equals(substitutionTokenValues, other.substitutionTokenValues) && Objects.equals(details, other.details) && Objects.equals(hasBeenPublished, other.hasBeenPublished) && Objects.equals(hasBeenRead, other.hasBeenRead) && Objects.equals(hasBeenEmailed, other.hasBeenEmailed) && Objects.equals(eventId, other.eventId);
  }

  @Override
  public String toString() {
    return "User [userId=" + userId + ", parentEventId=" + parentEventId + ", audienceScopeId=" + audienceScopeId + ", category=" + category + ", producer=" + producer + ", attentionLevel=" + attentionLevel + ", presentationTypes=" + presentationTypes + ", applicationTypes=" + applicationTypes + ", emailTypes=" + emailTypes + ", name=" + name + ", displayName=" + displayName + ", emailCannotBeTurnedOff=" + emailCannotBeTurnedOff + ", eventUuid=" + eventUuid + ", occurredOnDate=" + occurredOnDate + ", expirationDate=" + expirationDate + ", isDownStatus=" + isDownStatus + ", substitutionTokenValues=" + substitutionTokenValues + ", details=" + details + ", hasBeenPublished=" + hasBeenPublished + ", hasBeenRead=" + hasBeenRead + ", hasBeenEmailed=" + hasBeenEmailed + ", eventId=" + eventId + "]";
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Integer userId;
    private Integer parentEventId;
    private Integer audienceScopeId;
    private String category;
    private String producer;
    private String attentionLevel;
    private List<String> presentationTypes;
    private List<String> applicationTypes;
    private List<String> emailTypes;
    private String name;
    private String displayName;
    private Boolean emailCannotBeTurnedOff;
    private String eventUuid;
    private String occurredOnDate;
    private String expirationDate;
    private Boolean isDownStatus;
    private SortedMap<String, String> substitutionTokenValues;
    private String details;
    private Boolean hasBeenPublished;
    private Boolean hasBeenRead;
    private Boolean hasBeenEmailed;
    private Integer eventId;
    private String publishedBy;
    private String templateBody;

    private Builder() {}

    private Builder(GetUserNotificationsResponse getUserNotificationsResponse) {
      requireNonNull(getUserNotificationsResponse, "getUserNotificationsResponse cannot be null");
      this.userId = getUserNotificationsResponse.userId;
      this.parentEventId = getUserNotificationsResponse.parentEventId;
      this.audienceScopeId = getUserNotificationsResponse.audienceScopeId;
      this.category = getUserNotificationsResponse.category;
      this.producer = getUserNotificationsResponse.producer;
      this.attentionLevel = getUserNotificationsResponse.attentionLevel;
      this.presentationTypes = getUserNotificationsResponse.presentationTypes;
      this.applicationTypes = getUserNotificationsResponse.applicationTypes;
      this.emailTypes = getUserNotificationsResponse.emailTypes;
      this.name = getUserNotificationsResponse.name;
      this.displayName = getUserNotificationsResponse.displayName;
      this.emailCannotBeTurnedOff = getUserNotificationsResponse.emailCannotBeTurnedOff;
      this.eventUuid = getUserNotificationsResponse.eventUuid;
      this.occurredOnDate = getUserNotificationsResponse.occurredOnDate;
      this.expirationDate = getUserNotificationsResponse.expirationDate;
      this.isDownStatus = getUserNotificationsResponse.isDownStatus;
      this.substitutionTokenValues = getUserNotificationsResponse.substitutionTokenValues;
      this.details = getUserNotificationsResponse.details;
      this.hasBeenPublished = getUserNotificationsResponse.hasBeenPublished;
      this.hasBeenRead = getUserNotificationsResponse.hasBeenRead;
      this.hasBeenEmailed = getUserNotificationsResponse.hasBeenEmailed;
      this.eventId = getUserNotificationsResponse.eventId;
      this.publishedBy = getUserNotificationsResponse.publishedBy;
      this.templateBody = getUserNotificationsResponse.templateBody;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withUserId(Integer userId) {
      requireNonNull(userId, "userId cannot be null");
      this.userId = userId;
      return this;
    }

    public Builder withParentEventId(Integer parentEventId) {
      this.parentEventId = parentEventId;
      return this;
    }

    public Builder withAudienceScopeId(Integer audienceScopeId) {
      this.audienceScopeId = audienceScopeId;
      return this;
    }

    public Builder withCategory(String category) {
      requireNonNull(category, "category cannot be null");
      this.category = category;
      return this;
    }

    public Builder withProducer(String producer) {
      requireNonNull(producer, "producer cannot be null");
      this.producer = producer;
      return this;
    }

    public Builder withAttentionLevel(String attentionLevel) {
      requireNonNull(attentionLevel, "attentionLevel cannot be null");
      this.attentionLevel = attentionLevel;
      return this;
    }

    public Builder withPresentationTypes(List<String> presentationTypes) {
      requireNonNull(presentationTypes, "presentationTypes cannot be null");
      this.presentationTypes = ImmutableList.copyOf(presentationTypes);
      return this;
    }

    public Builder withApplicationTypes(List<String> applicationTypes) {
      requireNonNull(applicationTypes, "applicationTypes cannot be null");
      this.applicationTypes = ImmutableList.copyOf(applicationTypes);
      return this;
    }

    public Builder withEmailTypes(List<String> emailTypes) {
      requireNonNull(emailTypes, "emailTypes cannot be null");
      this.emailTypes = ImmutableList.copyOf(emailTypes);
      return this;
    }

    public Builder withName(String name) {
      requireNonNull(name, "name cannot be null");
      this.name = name;
      return this;
    }

    public Builder withDisplayName(String displayName) {
      requireNonNull(displayName, "displayName cannot be null");
      this.displayName = displayName;
      return this;
    }

    public Builder withEmailCannotBeTurnedOff(Boolean emailCannotBeTurnedOff) {
      requireNonNull(emailCannotBeTurnedOff, "emailCannotBeTurnedOff cannot be null");
      this.emailCannotBeTurnedOff = emailCannotBeTurnedOff;
      return this;
    }

    public Builder withEventUuid(String eventUuid) {
      requireNonNull(eventUuid, "eventUuid cannot be null");
      this.eventUuid = eventUuid;
      return this;
    }

    public Builder withOccurredOnDate(String occurredOnDate) {
      requireNonNull(occurredOnDate, "occurredOnDate cannot be null");
      this.occurredOnDate = occurredOnDate;
      return this;
    }

    public Builder withExpirationDate(String expirationDate) {
      this.expirationDate = expirationDate;
      return this;
    }

    public Builder withIsDownStatus(Boolean isDownStatus) {
      this.isDownStatus = isDownStatus;
      return this;
    }

    public Builder withSubstitutionTokenValues(SortedMap<String, String> substitutionTokenValues) {
      if (substitutionTokenValues != null) {
        this.substitutionTokenValues = substitutionTokenValues;  
      }
      return this;
    }

    public Builder withDetails(String details) {
      this.details = details;
      return this;
    }

    public Builder withHasBeenPublished(Boolean hasBeenPublished) {
      requireNonNull(hasBeenPublished, "hasBeenPublished cannot be null");
      this.hasBeenPublished = hasBeenPublished;
      return this;
    }

    public Builder withHasBeenRead(Boolean hasBeenRead) {
      requireNonNull(hasBeenRead, "hasBeenRead cannot be null");
      this.hasBeenRead = hasBeenRead;
      return this;
    }

    public Builder withHasBeenEmailed(Boolean hasBeenEmailed) {
      requireNonNull(hasBeenEmailed, "hasBeenEmailed cannot be null");
      this.hasBeenEmailed = hasBeenEmailed;
      return this;
    }

    public Builder withEventId(Integer eventId) {
      requireNonNull(eventId, "eventId cannot be null");
      this.eventId = eventId;
      return this;
    }

    public Builder withPublishedBy(String publishedBy) {
      requireNonNull(publishedBy, "publishedBy cannot be null");
      this.publishedBy = publishedBy;
      return this;
    }

    public Builder withTemplateBody(String templateBody) {
      requireNonNull(templateBody, "templateBody cannot be null");
      this.templateBody = templateBody;
      return this;
    }
    
    public GetUserNotificationsResponse build() {
      requireNonNull(userId, "userId cannot be null");
      requireNonNull(category, "category cannot be null");
      requireNonNull(producer, "producer cannot be null");
      requireNonNull(attentionLevel, "attentionLevel cannot be null");
      requireNonNull(presentationTypes, "presentationTypes cannot be null");
      requireNonNull(applicationTypes, "applicationTypes cannot be null");
      requireNonNull(emailTypes, "emailTypes cannot be null");
      requireNonNull(name, "name cannot be null");
      requireNonNull(displayName, "displayName cannot be null");
      requireNonNull(emailCannotBeTurnedOff, "emailCannotBeTurnedOff cannot be null");
      requireNonNull(eventUuid, "eventUuid cannot be null");
      requireNonNull(occurredOnDate, "occurredOnDate cannot be null");
      requireNonNull(hasBeenPublished, "hasBeenPublished cannot be null");
      requireNonNull(hasBeenRead, "hasBeenRead cannot be null");
      requireNonNull(hasBeenEmailed, "hasBeenEmailed cannot be null");
      requireNonNull(eventId, "eventId cannot be null");
      return new GetUserNotificationsResponse(this);
    }
  }
}