//@formatter:off
package com.djt.hvac.domain.model.email.client;

import com.djt.hvac.domain.model.email.exception.EmailClientException;
import com.djt.hvac.domain.model.nodehierarchy.building.BillableBuildingEntity;
import com.djt.hvac.domain.model.notification.entity.NotificationEventEntity;
import com.djt.hvac.domain.model.user.AbstractUserEntity;
import com.djt.hvac.domain.model.user.DistributorUserEntity;

/**
 * 
 * @author tmyers
 *
 */
public interface EmailClient {

  /**
   * 
   */
  String SUPPORT_AT_RESOLUTE_BI_COM = "support@resolutebi.com";
  
  /**
   * 
   * @param accountManager The account manager to send the grace period
   * expiration warning email notification to
   * @param billableBuilding The building whose grace period is about to
   * expire
   * 
   * @return The id of the email notification that was sent
   * 
   * @throws EmailClientException If there was a problem sending the email
   */
  int sendGracePeriodExpirationWarningEmail(
      DistributorUserEntity accountManager,
      BillableBuildingEntity billableBuilding)
  throws
      EmailClientException;
  
  /**
   * 
   * @param user The user to send an email to
   * @param notificationEvent The notification to send (single)
   */
  void sendEmailNotification(AbstractUserEntity user, NotificationEventEntity notificationEvent);
  
  
  /**
   * 
   * @param user The user to send an email to
   * @param subject The subject for the email
   * @param body The body for the email
   */
  void sendEmail(
      AbstractUserEntity user,
      String subject,
      String body);
}
//@formatter:on