//@formatter:off
package com.djt.hvac.domain.model.notification.repository;

import java.util.List;
import java.util.SortedMap;

import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.notification.dto.CreateNotificationEventOptions;
import com.djt.hvac.domain.model.notification.dto.UpdateDraftNotificationEventOptions;
import com.djt.hvac.domain.model.notification.dto.UserNotificationDto;
import com.djt.hvac.domain.model.notification.entity.NotificationEventEntity;
import com.djt.hvac.domain.model.notification.entity.UserNotificationEntity;
import com.djt.hvac.domain.model.user.AbstractUserEntity;

import java.util.Set;

/**
 * 
 * @author tommyers
 * 
 */
public interface NotificationRepository {
  
  /**
   * 
   * @param createNotificationEventOptions
   * 
   * @return
   */
  NotificationEventEntity createNotificationEvent(CreateNotificationEventOptions createNotificationEventOptions);

  /**
   * 
   * @param parentEventId
   * @param expirationDate
   * @param details
   * @param substitutionTokenValues
   * @return
   * @throws EntityDoesNotExistException
   */
  NotificationEventEntity createChildNotificationEvent(
      Integer parentEventId,
      String expirationDate,
      String details,
      SortedMap<String, String> substitutionTokenValues) throws EntityDoesNotExistException;

  /**
   * 
   * @param eventId The id of the event to update
   * @param eventOptions The attributes to update:
   * <pre>
   * customerId The id of the customer to update (set to null or -1 to remove the customerId scoping)
   * expirationDate The expiration date to update
   * substitutionTokenValues The substitution token values to update
   * details The details to update
   * </pre>
   * 
   * @throws EntityDoesNotExistException If the notification event given by <code>eventId</code> does not exist
   */
  void updateDraftNotificationEvent(
      Integer eventId,
      UpdateDraftNotificationEventOptions eventOptions) 
  throws 
      EntityDoesNotExistException;
  
  /**
   * 
   * @param eventId
   * @param publishedBy
   * @throws EntityDoesNotExistException
   */
  void updateNotificationEventAsNonDraft(Integer eventId, String publishedBy) throws EntityDoesNotExistException;
  
  /**
   * 
   * @param newlyPublishedNotificationEventIds
   */
  void updateNotificationEventsAsPublished(Set<Integer> newlyPublishedNotificationEventIds);
  
  /**
   * 
   * @param hasBeenPublished
   * @param isDraft
   * 
   * @return
   */
  List<NotificationEventEntity> loadNotificationEvents(
      boolean hasBeenPublished,
      boolean isDraft);
  
  /**
   * @return
   */
  List<NotificationEventEntity> loadAllNotificationEvents();

  /**
   * 
   * @param notificationEventId
   */
  void deleteNotificationEvent(Integer notificationEventId);
  
  /**
   * 
   */
  void deleteAllNotificationEvents();  
  
  /**
   * 
   * @param users
   * @param notificationEvent
   * @return 
   * @throws EntityDoesNotExistException
   */
  List<UserNotificationDto> createUserNotifications(Set<AbstractUserEntity> users, NotificationEventEntity notificationEvent) throws EntityDoesNotExistException;
  
  /**
   * 
   * @param userId The id of the user to delete the given user notification for
   * @param notificationEventId The id of the user notification to delete on behalf of the user
   */
  void deleteUserNotification(Integer userId, Integer notificationEventId);
  
  /**
   * 
   * @param userId The id of the user to delete all notifications for
   */
  void deleteAllUserNotifications(Integer userId);
  
  /**
   * 
   */
  void deleteAllUserNotifications();
  
  /**
   * 
   * @param userId The id of the user to mark the given user notification as read 
   * @param notificationEventId The id of the notification to mark as read on behalf of the user
   */
  void markUserNotificationAsRead(Integer userId, Integer notificationEventId);

  /**
   * 
   * @param userId The id of the user to mark the given user notification as unread 
   * @param notificationEventId The id of the notification to mark as read on behalf of the user
   */
  void markUserNotificationAsUnread(Integer userId, Integer notificationEventId);
  
  /**
   * 
   * @param userId The id of the user to mark all user notifications as read
   */
  void markAllUserNotificationsAsRead(Integer userId);
    
  /**
   * 
   * @param userId
   * 
   * @return
   * 
   * @throws EntityDoesNotExistException
   */
  List<UserNotificationEntity> loadUserNotifications(Integer userId) throws EntityDoesNotExistException;
  
  /**
   * 
   * @return
   */
  List<UserNotificationDto> loadAllUnEmailedUserNotifications();
  
  /**
   * 
   * @param userId The id of the user to disable the email notifications for the given notification event type
   * @param eventType The notification event type to disable email notifications on behalf of the given user
   * @throws EntityDoesNotExistException
   * 
   * NOTE: This method only has meaning if one of the <code>presentationType</code> values for the given notification event type is EMAIL
   */
  void disableEmailUserNotification(Integer userId, String eventType) throws EntityDoesNotExistException;
  
  /**
   * Deletes all disabled email user notification settings (for all users)
   * 
   * @throws EntityDoesNotExistException If the root distributor could not be loaded
   */
  void deleteAllDisabledEmailUserNotifications() throws EntityDoesNotExistException;
  
  /**
   * 
   * @param userNotificationDtos
   */
  void markUserNotificationsAsEmailed(List<UserNotificationDto> userNotificationDtos);
}  
//@formatter:on