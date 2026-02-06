//@formatter:off
package com.djt.hvac.domain.model.notification.service;

import java.time.LocalDate;
import java.util.List;
import java.util.SortedMap;

import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.notification.dto.CreateNotificationEventOptions;
import com.djt.hvac.domain.model.notification.dto.UpdateDraftNotificationEventOptions;
import com.djt.hvac.domain.model.notification.entity.NotificationEventEntity;
import com.djt.hvac.domain.model.notification.entity.UserNotificationEntity;

/**
 * 
 * @author tommyers
 * 
 */
public interface NotificationService {
  
  /**
   * 
   * @param createNotificationEventOptions A POJO container/builder that has the following parameters:
   * <ul>
   *   <li>eventType: required: A string that is one of:
   *     <ul>
   *       <li>Resolute/System Category
   *         <ul> 
   *           <li>PLANNED_SITE_MAINTENANCE
   *           <li>UPDATED_RULES_REPORTS
   *           <li>UPDATED_RULES_REPORTS_NO_SUPPORT
   *           <li>NEW_RULES_REPORTS_ADDED
   *           <li>RELEASE_NOTES
   *           <li>NEW_OR_UPDATED_FEATURES
   *           <li>WELCOME_MESSAGE
   *         </ul>
   *       <li>Application Alert Category (These will have a "down" event, with an "up" event at some point)
   *         <ul> 
   *           <li>CONNECTOR_STATUS
   *           <li>VPN_STATUS
   *           <li>POINT_DATAFLOW_AUDIT
   *           <li>RULE_DATAFLOW_AUDIT
   *           <li>COMPUTED_POINT_DATAFLOW_AUDIT
   *           <li>TRIAL_EXPIRATION
   *           <li>CREDIT_CARD_EXPIRATION (Only the account manager(s) of the given distributor receive these)
   *           <li>POINT_CAP_EXCEEDED
   *         </ul>
   *       <li>Application Status Category
   *         <ul>
   *           <li>LOCK_STATUS
   *           <li>TRIAL_STATUS
   *           <li>TRIAL_EXPIRATION_WARNING
   *           <li>DATAFLOW_HAS_BEGUN
   *           <li>SUCCESSFUL_JOB
   *           <li>FAILED_JOB
   *         </ul>
   *     </ul>
   *     
   *   <li>customerId: optional: If the given <code>eventType</code> has <code>applicationTypes</code> that has either FUSION, SYNERGY or both, then this specifies the id of that customer.  
   *       Otherwise, if <code>category</code> is SYSTE<, then this value is ignored. 
   *   
   *   <li>expirationDate: optional: A string that is formatted: YYYY-MM-DD that specifies when the notification is automatically deleted from the system. The default is 180 days in the future.
   *   
   *   <li>details: required: A string that has specific details as to the nature of the notification (e.g. release notes or information on how to upgrade to a newer rule/report)
   *   
   *   <li>publishImmediately: optional: A boolean that specifies whether or not the notification event should be published immediately.  If <code>false</code>, then a regularly scheduled job
   *       will evaluate for new notification events and publish them.  For the initial implementation, this will occur every 2 hours with portfolio maintenance. 
   * </ul>
   * 
   * NOTE: To create a notification event that is in the "isDraft" state, then specify publishImmediately=false
   * 
   * @return The newly created notification event
   * 
   * @throws EntityDoesNotExistException If an entity could not be found
   */
  NotificationEventEntity createNotificationEvent(CreateNotificationEventOptions createNotificationEventOptions) throws EntityDoesNotExistException;

  /**
   * 
   * @param parentEventId required: An integer that is the id of the parent notification event
   * @param expirationDate optional: A string that is formatted: YYYY-MM-DD that specifies when the notification is automatically deleted from the system. The default is 180 days in the future.
   * @param details required: A string that has specific details as to the nature of the child (i.e. reminder) notification
   * @param substitutionTokenValues: required: A map containing token keys and the token values
   * @param publishImmediately optional: A boolean that specifies whether or not the notification event should be published immediately.  If <code>false</code>, then a regularly scheduled job
   *        will evaluate for new notification events and publish them.  For the initial implementation, this will occur every 2 hours with portfolio maintenance.
   *
   * @return The newly created child notification event (which is associated to the given parent notification event)
   * 
   * @throws EntityDoesNotExistException If the parent notification event was not found
   */
  NotificationEventEntity createChildNotificationEvent(
      Integer parentEventId,
      String expirationDate,
      String details,
      SortedMap<String, String> substitutionTokenValues,
      boolean publishImmediately) throws EntityDoesNotExistException;
  
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
   * @param eventId The event to delete, which includes a cascading delete on all user notifications
   * @throws EntityDoesNotExistException If the given event doesn't exist
   */
  void deleteNotificationEvent(Integer eventId) throws EntityDoesNotExistException;
  
  /**
   * This method will:
   * <ul>
   *   <li>create user notifications for all "new", that is, unpublished, notification events
   *   <li>delete any user notifications that are associated with "expired" notification events
   * </ul>
   * 
   *  @return All "new" notification events.
   *  
   *  @throws EntityDoesNotExistException If customer does not exist (as specified by <code>customerId</code>
   */
  List<NotificationEventEntity> evaluateNotificationEvents() throws EntityDoesNotExistException;
  
  /**
   * 
   * @return All notification events
   */
  List<NotificationEventEntity> getNotificationEvents();
  
  /**
   * This method can be used by the UI to "poll" for new notifications, and should only be used if the push/publish mechanism is not available. 
   * 
   * @param userId
   * 
   * @return The given user
   * 
   * @throws EntityDoesNotExistException If the user does not exist
   */
  List<UserNotificationEntity> getUserNotifications(Integer userId) throws EntityDoesNotExistException;
  
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
   * @param userId The id of the user to disable the email notifications for the given notification event type
   * @param eventType The notification event type to disable email notifications on behalf of the given user
   * 
   * @throws EntityDoesNotExistException If the resolute root distributor could not be loaded.
   * 
   * NOTE: This method only has meaning if one of the <code>presentationType</code> values for the given notification event type is EMAIL
   */
  void disableEmailUserNotification(Integer userId, String eventType) throws EntityDoesNotExistException;
  
  /**
   * 
   * 
   * @return Given the current month and year, returns either the first monday of that month, or the third monday for that month (UTC). 
   */
  LocalDate getNextDateForBiMonthlyEmailSubmission();
}
//@formatter:on