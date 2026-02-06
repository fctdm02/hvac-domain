//@formatter:off
package com.djt.hvac.domain.model.notification.service;

import static java.util.Objects.requireNonNull;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.customer.repository.CustomerRepository;
import com.djt.hvac.domain.model.distributor.AbstractDistributorEntity;
import com.djt.hvac.domain.model.distributor.repository.DistributorRepository;
import com.djt.hvac.domain.model.email.client.EmailClient;
import com.djt.hvac.domain.model.notification.dto.CreateNotificationEventOptions;
import com.djt.hvac.domain.model.notification.dto.UpdateDraftNotificationEventOptions;
import com.djt.hvac.domain.model.notification.dto.UserNotificationDto;
import com.djt.hvac.domain.model.notification.entity.NotificationEventEntity;
import com.djt.hvac.domain.model.notification.entity.UserNotificationEntity;
import com.djt.hvac.domain.model.notification.enums.EmailType;
import com.djt.hvac.domain.model.notification.enums.NotificationCategory;
import com.djt.hvac.domain.model.notification.enums.NotificationEventAppType;
import com.djt.hvac.domain.model.notification.enums.NotificationEventType;
import com.djt.hvac.domain.model.notification.enums.NotificationPresentationType;
import com.djt.hvac.domain.model.notification.repository.NotificationRepository;
import com.djt.hvac.domain.model.user.AbstractUserEntity;
import com.djt.hvac.domain.model.user.CustomerUserEntity;
import com.djt.hvac.domain.model.user.DistributorUserEntity;
import com.djt.hvac.domain.model.user.repository.UserRepository;

/**
 * 
 * @author tommyers
 * 
 */
public class NotificationServiceImpl implements NotificationService {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);
  
  private final DistributorRepository distributorRepository;
  private final CustomerRepository customerRepository;
  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;
  private final EmailClient emailClient;
  
  public NotificationServiceImpl(
      DistributorRepository distributorRepository,
      CustomerRepository customerRepository,
      UserRepository userRepository,
      NotificationRepository notificationRepository,
      EmailClient emailClient) {
    
    requireNonNull(distributorRepository, "distributorRepository cannot be null");
    requireNonNull(customerRepository, "customerRepository cannot be null");
    requireNonNull(userRepository, "userRepository cannot be null");
    requireNonNull(notificationRepository, "notificationRepository cannot be null");
    requireNonNull(emailClient, "emailClient cannot be null");
    this.distributorRepository = distributorRepository;
    this.customerRepository = customerRepository;
    this.userRepository = userRepository;
    this.notificationRepository = notificationRepository;
    this.emailClient = emailClient;
  }
  
  @Override
  public NotificationEventEntity createNotificationEvent(CreateNotificationEventOptions createNotificationEventOptions) throws EntityDoesNotExistException {
    
    LOGGER.info("createNotificationEvent(): ENTER");
    
    createNotificationEventOptions.validate();
    
    NotificationEventType eventType = NotificationEventType.get(createNotificationEventOptions.getEventType());
    NotificationCategory category = eventType.getCategory();
    Integer customerId = createNotificationEventOptions.getCustomerId();
    if (!category.equals(NotificationCategory.SYSTEM) && customerId == null) {
      
      throw new IllegalArgumentException(" 'customerId' must be specified when 'category' is SYSTEM for notification event type: ["
          + eventType
          + "].");
      
    } else if (category.equals(NotificationCategory.SYSTEM) && customerId != null) {

      throw new IllegalArgumentException(" 'customerId' cannot be be specified when 'category' is SYSTEM for notification event type: ["
          + eventType
          + "].");
      
    }

    if (createNotificationEventOptions.getPublishImmediately() && createNotificationEventOptions.getPublishedBy() == null) {
      throw new IllegalStateException("'publishedBy' must be specified when 'publishImmediately' is true");
    }
    
    NotificationEventEntity notificationEvent = notificationRepository.createNotificationEvent(createNotificationEventOptions);
    if (createNotificationEventOptions.getPublishImmediately()) {
      if (createNotificationEventOptions.getPublishedBy() == null) {
        throw new IllegalStateException("'publishedBy' must be specified when 'publishImmediately' is true");        
      }
      
      evaluateNotificationEvents();
    }
    
    LOGGER.info("createNotificationEvent(): EXIT");
    return notificationEvent;
  }

  @Override
  public NotificationEventEntity createChildNotificationEvent(
      Integer parentEventId,
      String expirationDate,
      String details,
      SortedMap<String, String> substitutionTokenValues,
      boolean publishImmediately) throws EntityDoesNotExistException {
    
    NotificationEventEntity notificationEvent =  notificationRepository.createChildNotificationEvent(
        parentEventId, 
        expirationDate, 
        details,
        substitutionTokenValues);
    
    if (publishImmediately) {
      evaluateNotificationEvents();
    }
    return notificationEvent;
  }

  @Override
  public void updateDraftNotificationEvent(
      Integer eventId,
      UpdateDraftNotificationEventOptions eventOptions) 
  throws 
      EntityDoesNotExistException {
    
    LOGGER.info("updateDraftNotificationEvent(): ENTER");
   
    notificationRepository.updateDraftNotificationEvent(
        eventId,
        eventOptions);
    
    LOGGER.info("updateDraftNotificationEvent(): EXIT");
  }

  @Override
  public void updateNotificationEventAsNonDraft(Integer eventId, String publishedBy) throws EntityDoesNotExistException {
    
    LOGGER.info("updateNotificationEventAsNonDraft(): ENTER");
    
    notificationRepository.updateNotificationEventAsNonDraft(eventId, publishedBy);
    
    LOGGER.info("updateNotificationEventAsNonDraft(): EXIT");
  }
  
  @Override
  public void deleteNotificationEvent(Integer eventId) throws EntityDoesNotExistException {
    
    LOGGER.info("deleteNotificationEvent(): ENTER");
    
    notificationRepository.deleteNotificationEvent(eventId);
    
    LOGGER.info("deleteNotificationEvent(): EXIT");
  }
  
  @Override
  public List<NotificationEventEntity> evaluateNotificationEvents() throws EntityDoesNotExistException {
    
    LOGGER.info("evaluateNotificationEvents(): ENTER");
    
    
    // LOAD ALL DISTRIBUTORS/CUSTOMERS/USERS FOR PROCESSING NOTIFICATION EVENTS.
    boolean loadDistributorPaymentMethods = false;
    boolean loadDistributorUsers = true;
    AbstractDistributorEntity rootDistributor = distributorRepository.getRootDistributor(loadDistributorPaymentMethods, loadDistributorUsers);
    customerRepository.loadChildCustomers(rootDistributor);
    for (AbstractDistributorEntity parentDistributor: rootDistributor.getAllDescendantDistributors()) {
      
      customerRepository.loadChildCustomers(parentDistributor);  
    }
    Set<AbstractUserEntity> allUsers = new TreeSet<>();
    allUsers.addAll(userRepository.loadAllUsers(rootDistributor));
    
    Set<AbstractUserEntity> distributorUsers = new TreeSet<>();
    Set<AbstractUserEntity> customerUsers = new TreeSet<>();
    for (AbstractUserEntity user: allUsers) {
      
      if (user instanceof DistributorUserEntity) {
        distributorUsers.add(user);
      } else if (user instanceof CustomerUserEntity) {
        customerUsers.add(user);
      }
    }
    
    
    // PROCESS NEW/UNPUBLISHED NOTIFICATION EVENTS.
    Set<Integer> newlyPublishedNotificationEventIds = new HashSet<>(); 
    boolean hasBeenPublished = false;
    boolean isDraft = false;
    List<NotificationEventEntity> unpublishedNotificationEvents = notificationRepository.loadNotificationEvents(hasBeenPublished, isDraft);
    for (NotificationEventEntity unpublishedNotificationEvent: unpublishedNotificationEvents) {
     
      //NotificationEventType eventType = unpublishedNotificationEvent.getEventType();
      //NotificationCategory category = eventType.getCategory();
      //List<NotificationApplicationType> applicationTypes = eventType.getApplicationTypes();
      //Integer customerId = unpublishedNotificationEvent.getCustomerId();
      
      newlyPublishedNotificationEventIds.add(unpublishedNotificationEvent.getPersistentIdentity());

      /*
      if (category.equals(NotificationCategory.SYSTEM)) {
        
        users.addAll(rootDistributor.getAllDescendantUsers());
        
      } else if (applicationTypes.contains(NotificationApplicationType.SYNERGY)) {
        
        AbstractCustomerEntity customer = rootDistributor.getDescendantCustomer(customerId);
        AbstractDistributorEntity distributor = customer.getParentDistributor();
        users.addAll(distributor.getChildDistributorUsers());
        
      } else if (applicationTypes.contains(NotificationApplicationType.FUSION)) {

        AbstractCustomerEntity customer = rootDistributor.getDescendantCustomer(customerId);
        users.addAll(customer.getChildCustomerUsers());
        users.addAll(customer.getParentDistributor().getChildDistributorUsers());
        
      }
      */
      
      /*
      Set<AbstractUserEntity> filteredUsers = new TreeSet<>();
      for (AbstractUserEntity user: users) {
        
        if (!isDistributorUserAccountManagerOnlyEventType(unpublishedNotificationEvent.getEventType()) 
            || (user instanceof DistributorUserEntity && ((DistributorUserEntity)user).isAccountManager())) {
          
          filteredUsers.add(user);
        }
      }
      */
    
      Set<AbstractUserEntity> users = null;
      NotificationEventAppType appType = unpublishedNotificationEvent.getAppType();
      if (appType.equals(NotificationEventAppType.SYNERGY)) {

        users = distributorUsers;
        
      } else if (appType.equals(NotificationEventAppType.FUSION)) {

        users = customerUsers;
        
      } else {
        
        users = allUsers;
        
      }
      
      notificationRepository.createUserNotifications(
          users, 
          unpublishedNotificationEvent);
      
      
      // Now that notifications have been created for all eligible users, mark the notification event as "published"
      // NOTE: We leave it up to a different component to actually "push" user notifications to the UI (if available,
      // otherwise, we assume that the UI will poll/request for user notifications)
      notificationRepository.updateNotificationEventsAsPublished(newlyPublishedNotificationEventIds);
    }
    
    
    // PROCESS EXPIRED NOTIFICATION EVENTS.
    Map<Integer, NotificationEventEntity> allNonExpiredNotificationEvents = new TreeMap<>();
    List<NotificationEventEntity> allNotificationEvents = notificationRepository.loadAllNotificationEvents();
    for (NotificationEventEntity notificationEvent: allNotificationEvents) {
      
      Timestamp expirationDate = notificationEvent.getExpirationDate();
      if (expirationDate != null && expirationDate.getTime() <= AbstractEntity.getTimeKeeper().getCurrentTimeInMillis()) {

        // Deleting the notification event will result in a cascading delete of all associated user notifications.
        notificationRepository.deleteNotificationEvent(notificationEvent.getPersistentIdentity());
        
      } else {
        
        allNonExpiredNotificationEvents.put(notificationEvent.getPersistentIdentity(), notificationEvent);
        
      }
    }
    
    
    // LASTLY, PERFORM EMAIL PROCESSING
    Map<Integer, AbstractUserEntity> allUsersMap = new TreeMap<>();
    for (AbstractUserEntity user: allUsers) {
      
      allUsersMap.put(user.getPersistentIdentity(), user);
    }
    
    //List<Integer> notificationEventIds = new ArrayList<>();
    List<UserNotificationDto> allEmailedUserNotifications = new ArrayList<>();
    
    List<UserNotificationDto> allUserNotifications = notificationRepository.loadAllUnEmailedUserNotifications();
    for (UserNotificationDto userNotification: allUserNotifications) {
      
      NotificationEventEntity notificationEvent = allNonExpiredNotificationEvents.get(userNotification.getNotificationEventId());
      AbstractUserEntity user = allUsersMap.get(userNotification.getUserId());

      NotificationEventType eventType = notificationEvent.getEventType();
      Set<NotificationPresentationType> presentationTypes = eventType.getPresentationTypes();
      Set<EmailType> emailTypes = eventType.getEmailTypes();
      
      /*
      // BI-MONTHLY
      // If we are on the first or third Monday of the month, then we group all unpublished notifications together
      // into a single email.  Once we do that, we mark the notifications as published (so they only get published once).
      LocalDate currentDate = AbstractEntity.getTimeKeeper().getCurrentLocalDate();
      String subject = "Resolute - Bi-Monthly Newsletter";
      StringBuilder body = new StringBuilder();
      LocalDate nextDateForBiMonthlyEmailSubmission = getNextDateForBiMonthlyEmailSubmission();
      if (currentDate.equals(nextDateForBiMonthlyEmailSubmission)) {

        // Create a batch email body with all the user notifications for the given user.
        body.setLength(0);
        
        NotificationEventType eventType = notificationEvent.getEventType();
        Set<NotificationPresentationType> presentationTypes = eventType.getPresentationTypes();
        Set<EmailType> emailTypes = eventType.getEmailTypes();
        
        if (presentationTypes.contains(NotificationPresentationType.EMAIL)
            && emailTypes.contains(EmailType.BI_MONTHLY_NEWS_LETTER)
            && !user.getDisabledEmailNotifications().contains(eventType.getName())
            && !userNotification.getHasBeenEmailed()) {
          
          body.append("<HR>")
              .append(notificationEvent.getTokenSubstituedTemplateBody());
          
          userNotification.setHasBeenEmailed(true);
          notificationEventIds.add(notificationEvent.getPersistentIdentity());
        }
      }
      
      // Only email the data status connection alert email if there are notifications to send.
      if (body.length() > 0 && canEmailUser(notificationEventIds, user)) {
        
        // Mark all emailed notifications as such in the repository (so they aren't emailed again)
        notificationRepository.markUserNotificationsAsEmailed(user.getPersistentIdentity(), notificationEventIds);
        
        // Send the email to the user.
        emailClient.sendEmail(
            user,
            subject,
            body.toString());
      }
      
      
      // MORNING_EVENING_ALERT_EMAIL
      LocalDateTime currentLocalDateTime = AbstractEntity.getTimeKeeper().getCurrentLocalDateTime();
      subject = "Resolute - Data Connection Status Alert";
      int hourOfDay = currentLocalDateTime.getHour();
      if ((hourOfDay >= 4 && hourOfDay <= 6) || (hourOfDay >= 16 && hourOfDay <= 18)) {
        
        // Create a batch email body with all the user notifications for the given user.
        body.setLength(0);

        NotificationEventType eventType = notificationEvent.getEventType();
        Set<NotificationPresentationType> presentationTypes = eventType.getPresentationTypes();
        Set<EmailType> emailTypes = eventType.getEmailTypes();
        
        if (presentationTypes.contains(NotificationPresentationType.EMAIL)
            && emailTypes.contains(EmailType.MORNING_EVENING_ALERT_EMAIL)
            && !user.getDisabledEmailNotifications().contains(eventType.getName())
            && !userNotification.getHasBeenEmailed()) {
          
          body.append("<HR>")
              .append(notificationEvent.getTokenSubstituedTemplateBody());
          
          userNotification.setHasBeenEmailed(true);
          notificationEventIds.add(notificationEvent.getPersistentIdentity());
        }
        
        // Only email the data status connection alert email if there are notifications to send.
        if (body.length() > 0 && canEmailUser(notificationEventIds, user)) {

          // Mark all emailed notifications as such in the repository (so they aren't emailed again)
          notificationRepository.markUserNotificationsAsEmailed(user.getPersistentIdentity(), notificationEventIds);
          
          // Send the email to the user.
          emailClient.sendEmail(
              user, 
              subject, 
              body.toString());
        }
      }
      */
      
      
      // AT_TIME_OF_EVENT
      //String subject = "";
      //StringBuilder body = new StringBuilder();
      //body.setLength(0);

      Integer customerId = notificationEvent.getCustomerId();
      AbstractCustomerEntity customer = null;
      if (customerId != null) {
        if (user instanceof DistributorUserEntity) {
          customer = ((DistributorUserEntity)user).getParentDistributor().getDescendantCustomerNullIfNotExists(customerId);
        } else if (user instanceof CustomerUserEntity) {
          customer = ((CustomerUserEntity) user).getParentCustomer();
        }
      }
      
      // Make sure that if the event is scoped at the customer level, that the user belongs to the given customer.
      if (customer == null || (customer != null && customer.getPersistentIdentity().equals(customerId))) {
        
        if (presentationTypes.contains(NotificationPresentationType.EMAIL)
            && emailTypes.contains(EmailType.AT_TIME_OF_EVENT)
            //&& !user.getDisabledEmailNotifications().contains(eventType.getName())
            && !userNotification.getHasBeenEmailed()) {
          
          // TODO: TDM: Eventually, either add an allow list, or otherwise open up who can get emails until this feature is fully tested.
          String email = user.getEmail();
          if (email.endsWith("customer.com") 
              || (email.endsWith("company.com") 
              || email.endsWith("distributor.com")
              || email.endsWith("adingman@resolutebi.com")
              || email.endsWith("cvandeputte@resolutebi.com")
              || email.endsWith("jgraham@resolutebi.com")
              || email.endsWith("ayik@resolutebi.com")
              || email.endsWith("zcouts@resolutebi.com")
              || email.endsWith("zcouts98@gmail.com")
              || email.endsWith("zweaver@maddogtechnology.com")
              || email.endsWith("tmyers@resolutebi.com")
              || email.endsWith("rstropoli@maddogtechnology.com")
              || email.endsWith("cmurad@resolutebi.com")
              || email.endsWith("adrian@lenderauto.com")
              || email.endsWith("dcilluffo@resolutebi.com"))) {
            
            // Send the email to the user.
            boolean canSendEmail = false;
            if (notificationEvent.getAppType().equals(NotificationEventAppType.BOTH)) {
              
              canSendEmail = true;
              
            } else if (notificationEvent.getAppType().equals(NotificationEventAppType.SYNERGY) && user instanceof DistributorUserEntity) {
              
              canSendEmail = true;
              
            } else if (notificationEvent.getAppType().equals(NotificationEventAppType.FUSION) && user instanceof CustomerUserEntity) {
              
              canSendEmail = true;
            }
            
            if (canSendEmail) {

              emailClient.sendEmailNotification(
                  user,
                  notificationEvent);
              
              userNotification.setHasBeenEmailed(true);
              
              allEmailedUserNotifications.add(userNotification);
              
            }            
          }
        }
      }
    }    
      
    // Mark all emailed notifications as such in the repository (so they aren't emailed again)
    notificationRepository.markUserNotificationsAsEmailed(allEmailedUserNotifications);
    
    LOGGER.info("evaluateNotificationEvents(): EXIT");
    
    return unpublishedNotificationEvents;
  }
  /*
  private boolean isDistributorUserAccountManagerOnlyEventType(NotificationEventType eventType) {
    
    if (eventType.equals(NotificationEventType.CREDIT_CARD_EXPIRATION)) {
      return true;
    }
    return false;
  }
  */
  @Override
  public List<NotificationEventEntity> getNotificationEvents() {
    
    List<NotificationEventEntity> list = notificationRepository.loadAllNotificationEvents();
    Collections.sort(list);
    return list;
  }
  
  @Override
  public List<UserNotificationEntity> getUserNotifications(Integer userId) throws EntityDoesNotExistException {
    
    List<UserNotificationEntity> list = notificationRepository.loadUserNotifications(userId);
    Collections.sort(list);
    return list;
  }

  @Override
  public void markUserNotificationAsRead(Integer userId, Integer notificationEventId) {
    
    notificationRepository.markUserNotificationAsRead(userId, notificationEventId);
  }

  @Override
  public void markUserNotificationAsUnread(Integer userId, Integer notificationEventId) {
    
    notificationRepository.markUserNotificationAsUnread(userId, notificationEventId);
  }
  
  @Override
  public void markAllUserNotificationsAsRead(Integer userId) {
    
    notificationRepository.markAllUserNotificationsAsRead(userId);
  }
  
  @Override
  public void deleteUserNotification(Integer userId, Integer notificationEventId) {
    
    notificationRepository.deleteUserNotification(userId, notificationEventId);
  }
  
  @Override
  public void deleteAllUserNotifications(Integer userId) {
    
    notificationRepository.deleteAllUserNotifications(userId);
  }
  
  @Override
  public void deleteAllUserNotifications() {
    
    notificationRepository.deleteAllUserNotifications(); 
  }
  
  @Override
  public void disableEmailUserNotification(Integer userId, String eventType) throws EntityDoesNotExistException {
    
    NotificationEventType net = NotificationEventType.get(eventType);
    if (net.getEmailCannotBeTurnedOff()) {
      throw new IllegalArgumentException("Notification type: ["
          + eventType
          + "] cannot be disabled.");
    }
    
    notificationRepository.disableEmailUserNotification(userId, eventType);
  }
  
  @Override
  public LocalDate getNextDateForBiMonthlyEmailSubmission() {
    
    LocalDate currentLocalDate = AbstractEntity.getTimeKeeper().getCurrentLocalDate();
    LocalDate firstMondayInMonth = currentLocalDate.with(TemporalAdjusters.dayOfWeekInMonth(1, DayOfWeek.MONDAY));
    LocalDate thirdMondayInMonth = currentLocalDate.with(TemporalAdjusters.dayOfWeekInMonth(3, DayOfWeek.MONDAY));
    
    if (firstMondayInMonth.isAfter(currentLocalDate) || firstMondayInMonth.isEqual(currentLocalDate)) {
      return firstMondayInMonth;
    }
    return thirdMondayInMonth;
  }  
}
//@formatter:on