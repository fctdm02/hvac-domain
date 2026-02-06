//@formatter:off
package com.djt.hvac.domain.model.notification.entity;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.djt.hvac.domain.model.common.AbstractAssociativeEntity;
import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.mapper.DtoMapper;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.notification.dto.UserNotificationDto;
import com.djt.hvac.domain.model.user.AbstractUserEntity;

public class UserNotificationEntity extends AbstractAssociativeEntity {
  private static final long serialVersionUID = 1L;
  
  private final AbstractUserEntity user;
  private final NotificationEventEntity notificationEvent;
  private boolean hasBeenRead = false;
  private boolean hasBeenEmailed = false;

  public UserNotificationEntity(
      AbstractUserEntity user,
      NotificationEventEntity notificationEvent) {
    
    this(
        user,
        notificationEvent,
        false,
        false);
  }
  
  public UserNotificationEntity(
      AbstractUserEntity user,
      NotificationEventEntity notificationEvent,
      boolean hasBeenRead,
      boolean hasBeenEmailed) {
    
    requireNonNull(user, "user cannot be null");
    requireNonNull(notificationEvent, "notificationEvent cannot be null");
    this.user = user;
    this.notificationEvent = notificationEvent;
    this.hasBeenRead = hasBeenRead;
    this.hasBeenEmailed = hasBeenEmailed;
  }
  
  public AbstractUserEntity getUser() {
    return user;
  }

  public NotificationEventEntity getNotificationEvent() {
    return notificationEvent;
  }

  public boolean getHasBeenRead() {
    return hasBeenRead;
  }

  public void setHasBeenRead(boolean hasBeenRead) {
    this.hasBeenRead = hasBeenRead;
    setIsModified("hasBeenRead");
  }

  public boolean getHasBeenEmailed() {
    return hasBeenEmailed;
  }
  
  public void setHasBeenEmailed(boolean hasBeenEmailed) {
    this.hasBeenEmailed = hasBeenEmailed;
    setIsModified("hasBeenEmailed");
  }
  
  @Override
  public String getNaturalIdentity() {
    
    return new StringBuilder()
        .append(user.getNaturalIdentity())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(notificationEvent.getNaturalIdentity())
        .toString();
  }
  
  @Override
  public int compareTo(AbstractEntity obj) {
    
    int compareTo = 0;
    if (obj instanceof UserNotificationEntity) {

      UserNotificationEntity that = (UserNotificationEntity)obj;
      compareTo = Boolean.valueOf(this.hasBeenRead).compareTo(that.hasBeenRead);
      if (compareTo == 0) {
        compareTo = that.getNotificationEvent().getOccurredOnDate().compareTo(this.getNotificationEvent().getOccurredOnDate());  
      }
    }
    return compareTo;
  } 
  
  @Override
  public String toString() {
    
    return new StringBuilder()
        .append(user.getNaturalIdentity())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(notificationEvent.getNaturalIdentity())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(notificationEvent.getOccurredOnDate())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(notificationEvent.getDetails())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append("hasBeenRead=")
        .append(hasBeenRead)
        .append(", hasBeenEmailed=")
        .append(hasBeenEmailed)
        .toString();
  }  

  @Override
  public void validate(
      Set<IssueType> issueTypes, 
      List<ValidationMessage> validationMessages,
      boolean remediate) {
    
  }
  
  @Override
  public Map<String, Integer> getParentIdentities() {
    
    Map<String, Integer> parentIdentities = new LinkedHashMap<>();
    parentIdentities.put("user", user.getPersistentIdentity());
    parentIdentities.put("notificationEvent", notificationEvent.getPersistentIdentity());
    return parentIdentities;
  }
  
  public static class Holder {
    
    private final Map<Integer, AbstractUserEntity> users;
    private final Map<Integer, NotificationEventEntity> notificationEvents;
    
    public Holder(
        Map<Integer, AbstractUserEntity> users,
        Map<Integer, NotificationEventEntity> notificationEvents) {

      requireNonNull(users, "users cannot be null");
      requireNonNull(notificationEvents, "notificationEvents cannot be null");
      this.users = users;
      this.notificationEvents = notificationEvents;
    }

    public Map<Integer, AbstractUserEntity> getUsers() {
      return users;
    }

    public Map<Integer, NotificationEventEntity> getNotificationEvents() {
      return notificationEvents;
    }
  }
  
  public static class Mapper implements DtoMapper<Holder, UserNotificationEntity, UserNotificationDto> {

    private static final Mapper INSTANCE = new Mapper();

    private Mapper() {}

    public static Mapper getInstance() {
      return INSTANCE;
    }

    public List<UserNotificationDto> mapEntitiesToDtos(List<UserNotificationEntity> entities) {

      List<UserNotificationDto> list = new ArrayList<>();
      Iterator<UserNotificationEntity> iterator = entities.iterator();
      while (iterator.hasNext()) {

        UserNotificationEntity entity = iterator.next();
        if (!entity.getIsDeleted()) {
          list.add(mapEntityToDto(entity));
        }
      }
      return list;
    }
    
    public List<UserNotificationEntity> mapDtosToEntities(Holder holder, List<UserNotificationDto> dtos) {

      List<UserNotificationEntity> list = new ArrayList<>();
      Iterator<UserNotificationDto> iterator = dtos.iterator();
      while (iterator.hasNext()) {

        UserNotificationDto dto = iterator.next();
        list.add(mapDtoToEntity(holder, dto));
      }
      return list;
    }    

    @Override
    public UserNotificationDto mapEntityToDto(UserNotificationEntity entity) {

      UserNotificationDto dto = new UserNotificationDto();
      
      dto.setUserId(entity.getUser().getPersistentIdentity());
      dto.setNotificationEventId(entity.getNotificationEvent().getPersistentIdentity());
      dto.setHasBeenRead(entity.getHasBeenRead());
      dto.setHasBeenEmailed(entity.getHasBeenEmailed());
      
      return dto;
    }

    @Override
    public UserNotificationEntity mapDtoToEntity(
        Holder holder,
        UserNotificationDto dto) {
      
      return new UserNotificationEntity(
          holder.getUsers().get(dto.getUserId()),
          holder.getNotificationEvents().get(dto.getNotificationEventId()),
          dto.getHasBeenRead(),
          dto.getHasBeenEmailed());
    }
  }  
}
//@formatter:on