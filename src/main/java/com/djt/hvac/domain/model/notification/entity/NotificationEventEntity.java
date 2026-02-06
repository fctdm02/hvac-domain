//@formatter:off
package com.djt.hvac.domain.model.notification.entity;

import static java.util.Objects.requireNonNull;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.UUID;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.mapper.DtoMapper;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.notification.dto.NotificationEventDto;
import com.djt.hvac.domain.model.notification.enums.NotificationEventAppType;
import com.djt.hvac.domain.model.notification.enums.NotificationEventType;
import com.djt.hvac.domain.model.user.AbstractUserEntity;

public class NotificationEventEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  
  private final NotificationEventType eventType;
  private final Integer customerId;
  private final UUID eventUuid;
  private final Timestamp occurredOnDate;
  private final NotificationEventEntity parentEvent;
  private final boolean isDownStatus;
  private final SortedMap<String, String> substitutionTokenValues;
  private Timestamp expirationDate;
  private String details;
  private boolean hasBeenPublished;
  private boolean isDraft;
  private String publishedBy;
  private NotificationEventAppType appType;

  public NotificationEventEntity(
      Integer persistentIdentity,
      NotificationEventType eventType,
      Integer customerId,
      UUID eventUuid,
      Timestamp occurredOnDate,
      NotificationEventEntity parentEvent,
      boolean isDownStatus,
      SortedMap<String, String> substitutionTokenValues,
      Timestamp expirationDate,
      String details,
      boolean hasBeenPublished,
      boolean isDraft,
      String publishedBy) {
    this(
        persistentIdentity,
        eventType,
        customerId,
        eventUuid,
        occurredOnDate,
        parentEvent,
        isDownStatus,
        substitutionTokenValues,
        expirationDate,
        details,
        hasBeenPublished,
        isDraft,
        publishedBy,
        NotificationEventEntity.getAppType(eventType));
  }
  
  public NotificationEventEntity(
      Integer persistentIdentity,
      NotificationEventType eventType,
      Integer customerId,
      UUID eventUuid,
      Timestamp occurredOnDate,
      NotificationEventEntity parentEvent,
      boolean isDownStatus,
      SortedMap<String, String> substitutionTokenValues,
      Timestamp expirationDate,
      String details,
      boolean hasBeenPublished,
      boolean isDraft,
      String publishedBy,
      NotificationEventAppType appType) {
    super(persistentIdentity);
    requireNonNull(eventType, "eventType cannot be null");
    requireNonNull(eventUuid, "eventUuid cannot be null");
    requireNonNull(substitutionTokenValues, "substitutionTokenValues cannot be null");
    requireNonNull(appType, "appType cannot be null");
    this.eventType = eventType;
    this.customerId = customerId;
    this.eventUuid = eventUuid;
    this.occurredOnDate = occurredOnDate;
    this.parentEvent = parentEvent;
    this.isDownStatus = isDownStatus;
    this.substitutionTokenValues = substitutionTokenValues;
    this.expirationDate = expirationDate;
    this.details = details;
    this.hasBeenPublished = hasBeenPublished;
    this.isDraft = isDraft;
    this.publishedBy = publishedBy;
    this.appType = appType;
  }
  
  public NotificationEventType getEventType() {
    return eventType;
  }

  public Integer getCustomerId() {
    return customerId;
  }
  
  public UUID getEventUuid() {
    return eventUuid;
  }

  public Timestamp getOccurredOnDate() {
    return occurredOnDate;
  }
  
  public NotificationEventEntity getParentEvent() {
    return parentEvent;
  }
  
  public boolean getIsDownStatus() {
    return isDownStatus;
  }
  
  public SortedMap<String, String> getSubstitutionTokenValues() {
    return substitutionTokenValues;
  }
  
  public Timestamp getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(Timestamp expirationDate) {
    
    this.expirationDate = expirationDate;
    setIsModified("expirationDate");
  }
  
  public String getDetails() {
    return details;
  }
  
  public void setDetails(String details) {
    
    this.details = details;
    setIsModified("details");
  }
  
  public boolean getHasBeenPublished() {
    return hasBeenPublished;
  }
  
  public void setHasBeenPublished(boolean hasBeenPublished) {
    
    if (this.hasBeenPublished != hasBeenPublished) {
    
      this.hasBeenPublished = hasBeenPublished;
      setIsModified("hasBeenPublished");
    }
  }
  
  public boolean getIsDraft() {
    return isDraft;
  }
  
  public void setIsDraft(boolean isDraft) {
    
    if (this.isDraft != isDraft) {
    
      this.isDraft = isDraft;
      setIsModified("isDraft");
    }
  }  

  public String getPublishedBy() {
    return publishedBy;
  }
  
  public void setPublishedBy(String publishedBy) {
    
    if (publishedBy == null) {
      throw new IllegalArgumentException("'publishedBy' cannot be null");
    }
    if (this.publishedBy.equals(publishedBy)) {

      this.publishedBy = publishedBy;
      setIsModified("publishedBy");
    }
  }
  
  public NotificationEventAppType getAppType() {
    return appType;
  }
  
  public void setPublishedBy(NotificationEventAppType appType) {

    if (appType == null) {
      throw new IllegalArgumentException("'appType' cannot be null");
    }
    if (this.appType != appType) {
      
      this.appType = appType;
      setIsModified("appType");
    }
  }  

  public String getTokenSubstitutedTemplateBody(AbstractUserEntity user) {
    
    return this.getTokenSubstitutedTemplateBody(
        user.getEmail(), 
        user.getFirstName() + " " + user.getLastName());
  }
  
  public String getTokenSubstitutedTemplateBody(
      String userEmail,
      String userName) {

    // Perform token substitution.
    String tokenSubstitutedTemplateBody = eventType.getTemplateBody();
    for (SortedMap.Entry<String, String> entry: substitutionTokenValues.entrySet()) {
      
      tokenSubstitutedTemplateBody = tokenSubstitutedTemplateBody.replace("[" + entry.getKey() + "]", entry.getValue());
    }
    
    // Perform generic token substitutions.
    tokenSubstitutedTemplateBody = tokenSubstitutedTemplateBody.replace("[CURRENT_DATE]", AbstractEntity.getCurrentTimestampAsFormattedString());
    
    // Perform user specific token substitutions.
    tokenSubstitutedTemplateBody = tokenSubstitutedTemplateBody.replace("[USER_NAME]", userName).trim();
    tokenSubstitutedTemplateBody = tokenSubstitutedTemplateBody.replace("[USER_EMAIL]", userEmail).trim();
    
    // Verify that all tokens were replaced.
    List<String> tokenSubstitutionKeys = eventType.getTokenSubstitutionKeys();
    for (String tokenSubstitutionKey: tokenSubstitutionKeys) {
      
      if (tokenSubstitutedTemplateBody.contains(tokenSubstitutionKey)) {
        
        throw new IllegalStateException("Token: ["
            + tokenSubstitutionKey
            + "] was not substituted for event type: ["
            + eventType.getName()
            + "]. Tokens for this event type are: ["
            + eventType.getTokenSubstitutionKeys()
            + "]");
      }
    }
    
    // Verify that there aren't any additional tokens present in the substituted template body.
    if (tokenSubstitutedTemplateBody.contains("[")) {
      
      throw new IllegalStateException("Event type: ["
          + eventType.getName()
          + "] has unsubstituted tokens");
    }
    
    return tokenSubstitutedTemplateBody;
  }
  
  @Override
  public String getNaturalIdentity() {
    
    return new StringBuilder()
        .append(eventUuid.toString())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(eventType.getName())
        .toString();
  }
  
  @Override
  public int compareTo(AbstractEntity obj) {
    
    int compareTo = 0;
    if (obj instanceof NotificationEventEntity) {

      NotificationEventEntity that = (NotificationEventEntity)obj;
      compareTo = Boolean.valueOf(this.hasBeenPublished).compareTo(that.hasBeenPublished);
      if (compareTo == 0) {
        compareTo = that.getOccurredOnDate().compareTo(this.getOccurredOnDate());  
      }
    }
    return compareTo;
  }

  @Override
  public String toString() {
    
    return new StringBuilder()
        .append(getNaturalIdentity())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append("occurredOnDate=")
        .append(occurredOnDate)
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append("expirationDate=")
        .append(expirationDate)
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append("customerId=")
        .append(customerId)
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append("hasBeenPublished=")
        .append(hasBeenPublished)
        .toString();
  }  
  
  @Override
  public void validate(
      Set<IssueType> issueTypes, 
      List<ValidationMessage> validationMessages,
      boolean remediate) {
    
  }
  
  public static class Mapper implements DtoMapper<Map<Integer, NotificationEventDto>, NotificationEventEntity, NotificationEventDto> {

    private static final Mapper INSTANCE = new Mapper();

    private Mapper() {}

    public static Mapper getInstance() {
      return INSTANCE;
    }

    public List<NotificationEventDto> mapEntitiesToDtos(List<NotificationEventEntity> entities) {

      List<NotificationEventDto> list = new ArrayList<>();
      Iterator<NotificationEventEntity> iterator = entities.iterator();
      while (iterator.hasNext()) {

        NotificationEventEntity entity = iterator.next();
        if (!entity.getIsDeleted()) {
          list.add(mapEntityToDto(entity));
        }
      }
      return list;
    }
    
    public List<NotificationEventEntity> mapDtosToEntities(Map<Integer, NotificationEventDto> map, List<NotificationEventDto> dtos) {

      List<NotificationEventEntity> list = new ArrayList<>();
      Iterator<NotificationEventDto> iterator = dtos.iterator();
      while (iterator.hasNext()) {

        NotificationEventDto dto = iterator.next();
        list.add(mapDtoToEntity(map, dto));
      }
      return list;
    }    

    @Override
    public NotificationEventDto mapEntityToDto(NotificationEventEntity entity) {

      NotificationEventDto dto = new NotificationEventDto();
      
      dto.setId(entity.getPersistentIdentity());
      dto.setEventUuid(entity.getEventUuid().toString());
      dto.setOccurredOnDate(AbstractEntity.formatTimestamp(entity.getOccurredOnDate()));
      dto.setEventType(entity.getEventType().toString());
      dto.setCustomerId(entity.getCustomerId());
      
      if (entity.getParentEvent() != null) {
        dto.setParentEventId(entity.getParentEvent().getPersistentIdentity());
      }
      
      dto.setIsDownStatus(entity.getIsDownStatus());
      dto.setSubstitutionTokenValues(entity.getSubstitutionTokenValues());
      
      if (entity.getExpirationDate() != null) {
        dto.setExpirationDate(AbstractEntity.formatTimestamp(entity.getExpirationDate()));  
      }
      
      dto.setDetails(entity.getDetails());
      dto.setHasBeenPublished(entity.getHasBeenPublished());
      dto.setIsDraft(entity.getIsDraft());
      dto.setPublishedBy(entity.getPublishedBy());
      dto.setAppType(entity.getAppType().toString());
      
      return dto;
    }

    @Override
    public NotificationEventEntity mapDtoToEntity(
        Map<Integer, NotificationEventDto> map,
        NotificationEventDto dto) {

      NotificationEventEntity parentEvent = null;
      if (dto.getParentEventId() != null) {
        
        parentEvent = NotificationEventEntity
            .Mapper
            .getInstance()
            .mapDtoToEntity(map, map.get(dto.getParentEventId()));
      }
      
      Timestamp expirationDate = null;
      if (dto.getExpirationDate() != null) {
        
        expirationDate = AbstractEntity.parseTimestamp(dto.getExpirationDate());
      }
      
      return new NotificationEventEntity(
          dto.getId(),
          NotificationEventType.get(dto.getEventType()),
          dto.getCustomerId(),
          UUID.fromString(dto.getEventUuid()),
          AbstractEntity.parseTimestamp(dto.getOccurredOnDate()),
          parentEvent,
          dto.getIsDownStatus(),
          dto.getSubstitutionTokenValues(),
          expirationDate,
          dto.getDetails(),
          dto.getHasBeenPublished(),
          dto.getIsDraft(),
          dto.getPublishedBy(),
          NotificationEventAppType.get(dto.getAppType()));
    }
  }
  
  public static NotificationEventAppType getAppType(NotificationEventType et) {
    
    if (et.equals(NotificationEventType.PLANNED_SITE_MAINTENANCE)
        || et.equals(NotificationEventType.RELEASE_NOTES)
        || et.equals(NotificationEventType.NEW_OR_UPDATED_FEATURES)
        || et.equals(NotificationEventType.WELCOME_MESSAGE)) {
      return NotificationEventAppType.BOTH;
    }
    return NotificationEventAppType.SYNERGY;
  }
}
//@formatter:on