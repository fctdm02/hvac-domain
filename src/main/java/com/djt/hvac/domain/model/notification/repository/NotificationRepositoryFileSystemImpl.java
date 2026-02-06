//@formatter:off
package com.djt.hvac.domain.model.notification.repository;

import static java.util.Objects.requireNonNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedMap;
import java.util.UUID;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.distributor.AbstractDistributorEntity;
import com.djt.hvac.domain.model.distributor.repository.DistributorRepository;
import com.djt.hvac.domain.model.notification.dto.CreateNotificationEventOptions;
import com.djt.hvac.domain.model.notification.dto.NotificationEventDto;
import com.djt.hvac.domain.model.notification.dto.UpdateDraftNotificationEventOptions;
import com.djt.hvac.domain.model.notification.dto.UserNotificationDto;
import com.djt.hvac.domain.model.notification.entity.NotificationEventEntity;
import com.djt.hvac.domain.model.notification.entity.UserNotificationEntity;
import com.djt.hvac.domain.model.notification.entity.UserNotificationEntity.Holder;
import com.djt.hvac.domain.model.notification.enums.NotificationEventAppType;
import com.djt.hvac.domain.model.notification.enums.NotificationEventType;
import com.djt.hvac.domain.model.user.AbstractUserEntity;
import com.djt.hvac.domain.model.user.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 
 * @author tommyers
 * 
 */
public class NotificationRepositoryFileSystemImpl implements NotificationRepository {

  private static Integer MAX_PERSISTENT_IDENTITY_VALUE = Integer.valueOf(0);
  synchronized private static Integer getNextPersistentIdentityValue() {
    
    Integer nextValue = Integer.valueOf((MAX_PERSISTENT_IDENTITY_VALUE.intValue()-1));
    MAX_PERSISTENT_IDENTITY_VALUE = nextValue;
    return nextValue;
  }
  
  private static boolean USE_PRETTY_PRINT = true;
  public static boolean getPrettyPrint() {
    return USE_PRETTY_PRINT;
  }
  public static void setPrettyPrint(boolean prettyPrint) {
    USE_PRETTY_PRINT = prettyPrint;
  }

  private String basePath;
  private DistributorRepository distributorRepository;
  private UserRepository userRepository;
  
  public NotificationRepositoryFileSystemImpl(
      DistributorRepository distributorRepository,
      UserRepository userRepository) {
    this(
        null,
        distributorRepository,
        userRepository);
  }

  public NotificationRepositoryFileSystemImpl(
      String basePath,
      DistributorRepository distributorRepository,
      UserRepository userRepository) {

    requireNonNull(userRepository, "userRepository cannot be null");
    requireNonNull(distributorRepository, "distributorRepository cannot be null");
    
    if (basePath != null) {
      this.basePath = basePath;
    } else {
      this.basePath = System.getProperty("user.home") + "/";      
    }
    
    this.userRepository = userRepository;
    this.distributorRepository = distributorRepository;
  }
  
  public String basePath() {
    return basePath;
  }
  
  public void setBasePath(String basePath) {
    this.basePath = basePath;
  }
  
  @Override
  public NotificationEventEntity createNotificationEvent(CreateNotificationEventOptions createNotificationEventOptions) {
    
    List<NotificationEventDto> dtoList = loadNotificationEventDtoList();
    
    Map<Integer, NotificationEventDto> map = new HashMap<>();
    for (NotificationEventDto dto: dtoList) {
      map.put(dto.getId(), dto);
    }
    
    NotificationEventEntity parentEvent = null;
    boolean hasBeenPublished = false;
    boolean isDraft = true;
    String publishedBy = createNotificationEventOptions.getPublishedBy();
    if (publishedBy != null) {
      isDraft = false;
    }
    
    NotificationEventEntity entity = new NotificationEventEntity(
        getNextPersistentIdentityValue(),
        NotificationEventType.get(createNotificationEventOptions.getEventType()),
        createNotificationEventOptions.getCustomerId(),
        UUID.randomUUID(),
        AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
        parentEvent,
        createNotificationEventOptions.getIsDownStatus(),
        createNotificationEventOptions.getSubstitutionTokenValues(),
        AbstractEntity.parseTimestamp(createNotificationEventOptions.getExpirationDate()),
        createNotificationEventOptions.getDetails(),
        hasBeenPublished,
        isDraft,
        publishedBy,
        NotificationEventAppType.get(createNotificationEventOptions.getAppType()));
    
    NotificationEventDto dto = NotificationEventEntity
        .Mapper
        .getInstance()
        .mapEntityToDto(entity);
    
    dtoList.add(dto);
    
    storeNotificationEventDtoList(dtoList);
    
    return entity;
  }
  
  @Override
  public NotificationEventEntity createChildNotificationEvent(
      Integer parentEventId,
      String expirationDate,
      String details,
      SortedMap<String, String> substitutionTokenValues) throws EntityDoesNotExistException {
    
    List<NotificationEventDto> dtoList = loadNotificationEventDtoList();
    
    Map<Integer, NotificationEventDto> map = new HashMap<>();
    for (NotificationEventDto dto: dtoList) {
      map.put(dto.getId(), dto);
    }
    
    NotificationEventEntity parentEvent = null;
    for (NotificationEventDto dto: dtoList) {
      
      if (dto.getId().equals(parentEventId)) {
        
        parentEvent = NotificationEventEntity
            .Mapper
            .getInstance()
            .mapDtoToEntity(map, dto);
      }
    }
    
    if (parentEvent == null) {
      throw new EntityDoesNotExistException("Parent event with id: ["
          + parentEventId
          + "] does not exist.");
    }
    
    boolean hasBeenPublished = false;
    
    NotificationEventEntity entity = new NotificationEventEntity(
        getNextPersistentIdentityValue(),
        parentEvent.getEventType(),
        parentEvent.getCustomerId(),
        UUID.randomUUID(),
        AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
        parentEvent,
        false,
        substitutionTokenValues,
        AbstractEntity.parseTimestamp(expirationDate),
        details,
        hasBeenPublished,
        true,
        null,
        NotificationEventEntity.getAppType(parentEvent.getEventType()));
    
    NotificationEventDto dto = NotificationEventEntity
        .Mapper
        .getInstance()
        .mapEntityToDto(entity);
    
    dtoList.add(dto);
    
    storeNotificationEventDtoList(dtoList);
    
    return entity;    
  }
  
  @Override
  public void updateDraftNotificationEvent(
      Integer eventId,
      UpdateDraftNotificationEventOptions eventOptions) 
  throws 
      EntityDoesNotExistException {
    
    List<NotificationEventDto> dtoList = loadNotificationEventDtoList();
    
    boolean changesMade = false;
    for (NotificationEventDto dto: dtoList) {
      if (dto.getId().equals(eventId)) {
        
        Integer customerId = eventOptions.getCustomerId();
        if (customerId == null || customerId.equals(Integer.valueOf(-1))) {
           dto.setCustomerId(null);
        } else {
          dto.setCustomerId(customerId);  
        }
        dto.setExpirationDate(eventOptions.getExpirationDate());
        dto.setSubstitutionTokenValues(eventOptions.getSubstitutionTokenValues());
        dto.setDetails(eventOptions.getDetails());
        
        changesMade = true;
      }
      if (!changesMade) {
        changesMade = true;
      }
    }
    
    if (changesMade) {
      storeNotificationEventDtoList(dtoList);
    }    
  }

  @Override
  public void updateNotificationEventAsNonDraft(Integer eventId, String publishedBy) throws EntityDoesNotExistException {
    
    List<NotificationEventDto> dtoList = loadNotificationEventDtoList();
    
    boolean changesMade = false;
    for (NotificationEventDto dto: dtoList) {
      if (dto.getId().equals(eventId)) {
        
        dto.setIsDraft(false);
        dto.setPublishedBy(publishedBy);
        changesMade = true;
      }
      if (!changesMade) {
        changesMade = true;
      }
    }
    
    if (changesMade) {
      storeNotificationEventDtoList(dtoList);
    }
  }
  
  @Override
  public void updateNotificationEventsAsPublished(Set<Integer> newlyPublishedNotificationEventIds) {
    
    List<NotificationEventDto> dtoList = loadNotificationEventDtoList();
    
    boolean changesMade = false;
    for (NotificationEventDto dto: dtoList) {
      if (newlyPublishedNotificationEventIds.contains(dto.getId())) {
        dto.setHasBeenPublished(true);
      }
      if (!changesMade) {
        changesMade = true;
      }
    }
    
    if (changesMade) {
      storeNotificationEventDtoList(dtoList);
    }    
  }
  
  @Override
  public List<NotificationEventEntity> loadNotificationEvents(boolean hasBeenPublished, boolean isDraft) {
    
    List<NotificationEventDto> dtoList = loadNotificationEventDtoList();
    
    Map<Integer, NotificationEventDto> map = new HashMap<>();
    for (NotificationEventDto dto: dtoList) {
      map.put(dto.getId(), dto);
    }
    
    List<NotificationEventEntity> entityList = new ArrayList<>();
    for (NotificationEventDto dto: dtoList) {
      
      NotificationEventEntity entity = NotificationEventEntity
          .Mapper
          .getInstance()
          .mapDtoToEntity(map, dto);
      
      if (entity.getHasBeenPublished() == hasBeenPublished && entity.getIsDraft() == isDraft) {
        entityList.add(entity);
      }
    }
    
    return entityList;
  }
  
  @Override
  public List<NotificationEventEntity> loadAllNotificationEvents() {
    
    List<NotificationEventDto> dtoList = loadNotificationEventDtoList();
    
    Map<Integer, NotificationEventDto> map = new HashMap<>();
    for (NotificationEventDto dto: dtoList) {
      map.put(dto.getId(), dto);
    }
    
    List<NotificationEventEntity> entityList = new ArrayList<>();
    for (NotificationEventDto dto: dtoList) {
      
      entityList.add(NotificationEventEntity
          .Mapper
          .getInstance()
          .mapDtoToEntity(map, dto));
    }
    
    return entityList;
  }
  
  @Override
  public void deleteNotificationEvent(Integer notificationEventId) {
    
    List<NotificationEventDto> dtoList = loadNotificationEventDtoList();
    
    NotificationEventDto victimNotificationEvent = null;
    for (NotificationEventDto dto: dtoList) {
      
      if (dto.getId().equals(notificationEventId)) {
        victimNotificationEvent = dto;
      }
    }
    
    if (victimNotificationEvent != null) {
      
      dtoList.remove(victimNotificationEvent);
      storeNotificationEventDtoList(dtoList);
    }
    
    List<UserNotificationDto> victimUserNotificationDtoList = new ArrayList<>();
    List<UserNotificationDto> userNotificationDtoList = loadUserNotificationDtoList();
    for (UserNotificationDto dto: userNotificationDtoList) {
      
      if (dto.getNotificationEventId().equals(notificationEventId)) {
        
        victimUserNotificationDtoList.add(dto);
      }
    }
    
    if (!victimUserNotificationDtoList.isEmpty()) {
      
      userNotificationDtoList.removeAll(victimUserNotificationDtoList);
      
      storeUserNotificationDtoList(userNotificationDtoList);  
    }      
  }
  
  @Override
  public void deleteAllNotificationEvents() {
    
    storeNotificationEventDtoList(new ArrayList<>());
  }

  @Override
  public List<UserNotificationDto> createUserNotifications(Set<AbstractUserEntity> users, NotificationEventEntity notificationEvent) throws EntityDoesNotExistException {
  
    List<UserNotificationDto> createdUserNotificationDtos = new ArrayList<>();
    List<UserNotificationDto> userNotificationDtos = loadUserNotificationDtoList();
    for (AbstractUserEntity user: users) {
      
      UserNotificationEntity entity = new UserNotificationEntity(user, notificationEvent);
      
      UserNotificationDto dto = UserNotificationEntity
          .Mapper
          .getInstance()
          .mapEntityToDto(entity);
      
      userNotificationDtos.add(dto);
      createdUserNotificationDtos.add(dto);
    }
    
    storeUserNotificationDtoList(userNotificationDtos);
    
    return createdUserNotificationDtos;    
   }
  
  @Override
  public void deleteUserNotification(Integer userId, Integer notificationEventId) {
    
    UserNotificationDto victim = null;

    List<UserNotificationDto> userNotificationDtoList = loadUserNotificationDtoList();
    
    for (UserNotificationDto dto: userNotificationDtoList) {
      
      if (dto.getUserId().equals(userId) && dto.getNotificationEventId().equals(notificationEventId)) {
        
        victim = dto;
      }
    }
    
    if (victim != null) {
      
      userNotificationDtoList.remove(victim);
      
      storeUserNotificationDtoList(userNotificationDtoList);  
    }
  }
  
  @Override
  public void deleteAllUserNotifications(Integer userId) {
   
    List<UserNotificationDto> victims = new ArrayList<>();

    List<UserNotificationDto> userNotificationDtoList = loadUserNotificationDtoList();
    
    for (UserNotificationDto dto: userNotificationDtoList) {
      
      if (dto.getUserId().equals(userId)) {
        
        victims.add(dto);
      }
    }
    
    if (!victims.isEmpty()) {
      
      userNotificationDtoList.removeAll(victims);
      
      storeUserNotificationDtoList(userNotificationDtoList);  
    }    
  }
  
  @Override
  public void deleteAllUserNotifications() {
   
    storeUserNotificationDtoList(new ArrayList<>());
  }
  
  @Override
  public void markUserNotificationAsRead(Integer userId, Integer notificationEventId) {
   
    List<UserNotificationDto> userNotificationDtoList = loadUserNotificationDtoList();
    
    for (UserNotificationDto dto: userNotificationDtoList) {
      
      if (dto.getUserId().equals(userId) && dto.getNotificationEventId().equals(notificationEventId)) {
        
        dto.setHasBeenRead(true);
        
        storeUserNotificationDtoList(userNotificationDtoList);
        
        break;
      }
    }
  }

  @Override
  public void markUserNotificationAsUnread(Integer userId, Integer notificationEventId) {
   
    List<UserNotificationDto> userNotificationDtoList = loadUserNotificationDtoList();
    
    for (UserNotificationDto dto: userNotificationDtoList) {
      
      if (dto.getUserId().equals(userId) && dto.getNotificationEventId().equals(notificationEventId)) {
        
        dto.setHasBeenRead(false);
        
        storeUserNotificationDtoList(userNotificationDtoList);
        
        break;
      }
    }
  }
  
  @Override
  public void markAllUserNotificationsAsRead(Integer userId) {

    List<UserNotificationDto> userNotificationDtoList = loadUserNotificationDtoList();
    
    boolean changesMade = false;
    for (UserNotificationDto dto: userNotificationDtoList) {
      
      if (dto.getUserId().equals(userId)) {
        
        dto.setHasBeenRead(true);
        changesMade = true;
      }
    }
    
    if (changesMade) {
    
      storeUserNotificationDtoList(userNotificationDtoList);
    }
  }
    
  @Override
  public List<UserNotificationDto> loadAllUnEmailedUserNotifications() {
    
    List<UserNotificationDto> dtoList = loadUserNotificationDtoList();
    
    List<UserNotificationDto> filteredDtoList = new ArrayList<>();
    for (UserNotificationDto dto: dtoList) {
      if (dto.getHasBeenEmailed() == false) {
        filteredDtoList.add(dto);
      }
    }
    
    return filteredDtoList;
  }
  
  @Override
  public List<UserNotificationEntity> loadUserNotifications(Integer userId) throws EntityDoesNotExistException {

    boolean loadDistributorPaymentMethods = false;
    boolean loadDistributorUsers = true;
    
    List<NotificationEventDto> dtoList = loadNotificationEventDtoList();
    
    Map<Integer, NotificationEventDto> map = new HashMap<>();
    for (NotificationEventDto dto: dtoList) {
      map.put(dto.getId(), dto);
    }
    
    Map<Integer, AbstractUserEntity> users = new HashMap<>();
    for (AbstractUserEntity user: userRepository.loadAllUsers(distributorRepository.getRootDistributor(loadDistributorPaymentMethods, loadDistributorUsers))) {
      
      users.put(user.getPersistentIdentity(), user);
    }
    
    Map<Integer, NotificationEventEntity> notificationEvents = new HashMap<>();
    for (NotificationEventEntity notificationEvent: loadAllNotificationEvents()) {
      
      notificationEvents.put(notificationEvent.getPersistentIdentity(), notificationEvent);
    }
    
    List<UserNotificationDto> userNotificationDtoList = loadUserNotificationDtoList();
    
    List<UserNotificationEntity> entityList = new ArrayList<>();
    
    Holder holder = new Holder(users, notificationEvents);
    
    for (UserNotificationDto dto: userNotificationDtoList) {
      
      if (dto.getUserId().equals(userId)) {
        
        entityList.add(UserNotificationEntity
            .Mapper
            .getInstance()
            .mapDtoToEntity(holder, dto));
      }
    }
    
    return entityList;
  }
  
  @Override
  public void disableEmailUserNotification(Integer userId, String eventType) throws EntityDoesNotExistException {
    
    boolean loadDistributorPaymentMethods = false;
    boolean loadDistributorUsers = true;
    
    AbstractUserEntity user = userRepository.loadUser(
        userId,
        distributorRepository.getRootDistributor(loadDistributorPaymentMethods, loadDistributorUsers));
    
    user.disableEmailNotification(eventType);
    
    userRepository.storeUser(user);
  }
  
  @Override
  public void deleteAllDisabledEmailUserNotifications() throws EntityDoesNotExistException {

    boolean loadDistributorPaymentMethods = false;
    boolean loadDistributorUsers = true;
    AbstractDistributorEntity rootDistributor = distributorRepository.getRootDistributor(loadDistributorPaymentMethods, loadDistributorUsers);
    
    for (AbstractUserEntity user: userRepository.loadAllUsers(rootDistributor)) {
      
      user.setDisabledEmailNotifications(new ArrayList<>());
      userRepository.storeUser(user);
    }
  }
  
  @Override 
  public void markUserNotificationsAsEmailed(List<UserNotificationDto> userNotificationDtos) {
    
    List<UserNotificationDto> dtoList = loadUserNotificationDtoList();
    Set<UserNotificationDto> dtoSet = new HashSet<>();
    for (UserNotificationDto dto: dtoList) {
      dtoSet.add(dto);
    }
    
    for (UserNotificationDto dto: dtoList) {
      if (dtoSet.contains(dto)) {
        dto.setHasBeenEmailed(true);  
      }
    }
    
    storeUserNotificationDtoList(dtoList);
  }
  
  private List<NotificationEventDto> loadNotificationEventDtoList() {
    
    File file = new File(basePath + "/NotificationEvents.json");
    if (file.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));
          
          Scanner s = new Scanner(in).useDelimiter("\\A")) {
          String fullJson = s.hasNext() ? s.next() : "";
  
          JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
          String compactJson = jsonNode.toString();
          
          List<NotificationEventDto> dtoList = AbstractEntity.OBJECT_MAPPER.get().readValue(compactJson, new TypeReference<List<NotificationEventDto>>() {});
          return dtoList;
  
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return new ArrayList<>();
  }
  
  private void storeNotificationEventDtoList(List<NotificationEventDto> dtoList) {
    
    File file = new File(basePath + "/NotificationEvents.json");
    OutputStream out = null;
    try {
      
      out = new BufferedOutputStream(new FileOutputStream(file));
      AbstractEntity.OBJECT_WRITER.get().writeValue(out, dtoList);

    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (out != null) {
        try {
          out.flush();
          out.close();
        } catch (IOException ioe) {
        }
      }
    }
  }
  
  private List<UserNotificationDto> loadUserNotificationDtoList() {
    
    File file = new File(basePath + "/UserNotifications.json");
    if (file.exists()) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));
          
          Scanner s = new Scanner(in).useDelimiter("\\A")) {
          String fullJson = s.hasNext() ? s.next() : "";
  
          JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
          String compactJson = jsonNode.toString();
          
          List<UserNotificationDto> dtoList = AbstractEntity.OBJECT_MAPPER.get().readValue(compactJson, new TypeReference<List<UserNotificationDto>>() {});
          return dtoList;
  
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return new ArrayList<>();
  }
  
  private void storeUserNotificationDtoList(List<UserNotificationDto> dtoList) {
    
    File file = new File(basePath + "/UserNotifications.json");
    OutputStream out = null;
    try {
      
      out = new BufferedOutputStream(new FileOutputStream(file));
      AbstractEntity.OBJECT_WRITER.get().writeValue(out, dtoList);

    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (out != null) {
        try {
          out.flush();
          out.close();
        } catch (IOException ioe) {
        }
      }
    }
  }   
}
//@formatter:on