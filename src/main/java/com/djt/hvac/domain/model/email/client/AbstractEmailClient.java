//@formatter:off
package com.djt.hvac.domain.model.email.client;

import static java.util.Objects.requireNonNull;

import com.djt.hvac.domain.model.email.dto.EmailDto;
import com.djt.hvac.domain.model.nodehierarchy.building.BillableBuildingEntity;
import com.djt.hvac.domain.model.notification.entity.NotificationEventEntity;
import com.djt.hvac.domain.model.user.AbstractUserEntity;
import com.djt.hvac.domain.model.user.DistributorUserEntity;

public abstract class AbstractEmailClient implements EmailClient {
  
  public static final String TIME_UNIT_TOKEN = "TIME_UNIT";
  public static final String GRACE_PERIOD_TOKEN = "GRACE_PERIOD";
  public static final String FIRST_NAME_TOKEN = "FIRST_NAME";
  public static final String BUILDING_NAME_TOKEN = "BUILDING_NAME";
  public static final String MONTH_DAY_YEAR_TOKEN = "MONTH_DAY_YEAR";
  
  // TODO: TDM: Change the last sentence to be the following once HTML formatted emails are supported
  // https://support.resolutebi.com/en/
  // Please call us at 1-888-798-6699, or visit the <a href='https://support.resolutebi.com/en/'>Resolute Help Center<a>, if you would like additional information about how to activate your building.
  
  public static final String GRACE_PERIOD_EXPIRATION_WARNING_EMAIL_SUBJECT = "Only 2 TIME_UNITs remaining on your free GRACE_PERIOD-TIME_UNIT Resolute trial period";
  public static final String GRACE_PERIOD_EXPIRATION_WARNING_EMAIL_BODY;
  static {
    // Per https://www.businesswritingblog.com/business_writing/2006/01/greetings_and_s.html, changing the salutation, as what was there didn't look right.
    GRACE_PERIOD_EXPIRATION_WARNING_EMAIL_BODY = new StringBuilder(2048)
        .append("Hi FIRST_NAME,\n") 
        .append("\n") 
        .append("We hope you're enjoying trying out the Resolute solution and seeing firsthand how fast and easy setting up and using analytics can be. ")
        .append("We noticed, however, that you haven't yet activated [BUILDING_NAME] in Resolute Synergy, so we wanted to send a quic­k reminder that ")
        .append("only 2 TIME_UNITs remain on your free GRACE_PERIOD-TIME_UNIT trial period.\n")
        .append("\n") 
        .append("Your trial period is set to expire on: MONTH_DAY_YEAR.\n") 
        .append("\n") 
        .append("If you haven't activated your building by then, your access to Resolute Fusion will be disabled. To avoid losing access to Resolute Fusion and its advanced ")
        .append("analytics functionality, real-time fault detection and alerts, on-demand reports and scorecards, root-cause analysis, prioritized action items, and more, ")
        .append("please activate your building before your trial period expires on MONTH_DAY_YEAR.\n") 
        .append("\n") 
        .append("To activate your building:\n") 
        .append("\n") 
        .append("   1. Launch Resolute Synergy and click your name in the upper righthand corner of the top navigation bar.\n\n") 
        .append("   2. Click 'Account Management' in the dropdown menu. You will be taken to the Account Management screen.\n\n") 
        .append("   3. Click the 'Activate' link in the 'Next Payment' column for [BUILDING_NAME]. You will be taken to the Subscription screen.\n\n") 
        .append("   4. Add a payment method or select a previously added payment method, and then select your preferred Point Plan. The [Activate] button will become available.\n\n") 
        .append("   5. Click the [Activate] button to activate your building.\n\n\n") 
        .append("Please call us at 1-888-798-6699 if you would like additional information about how to activate your building.\n") 
        .append("\n") 
        .append("-The Resolute Team \n")
        .toString();
  }
  
  private final String senderEmailAddress;
  
  public AbstractEmailClient(String senderEmailAddress) {
    
    requireNonNull(senderEmailAddress, "senderEmailAddress cannot be null");
    this.senderEmailAddress = senderEmailAddress;
  }
  
  protected EmailDto buildGracePeriodExpirationWarningEmail(
      DistributorUserEntity accountManager,
      BillableBuildingEntity billableBuilding) {
    
    String body = GRACE_PERIOD_EXPIRATION_WARNING_EMAIL_BODY
        .replaceAll(MONTH_DAY_YEAR_TOKEN, billableBuilding.getGracePeriodExpiration()) // needs to be invoked first
        .replaceAll(FIRST_NAME_TOKEN, accountManager.getFirstName())
        .replaceAll(TIME_UNIT_TOKEN, billableBuilding.getGracePeriodTimeUnit())
        .replaceAll(GRACE_PERIOD_TOKEN, billableBuilding.getGracePeriodOrdinal())
        .replaceAll(BUILDING_NAME_TOKEN, billableBuilding.getDisplayName());

    String subject = GRACE_PERIOD_EXPIRATION_WARNING_EMAIL_SUBJECT
        .replaceAll(TIME_UNIT_TOKEN, billableBuilding.getGracePeriodTimeUnit())
        .replaceAll(GRACE_PERIOD_TOKEN, billableBuilding.getGracePeriodOrdinal());
    
    return EmailDto
        .builder()
        .withSender(senderEmailAddress)
        .withRecipient(accountManager.getEmail())
        .withSubject(subject)
        .withBody(body)
        .build();
  }
  
  protected EmailDto buildNotificationEmail(
      AbstractUserEntity user,
      NotificationEventEntity notificationEvent) {

    return EmailDto
        .builder()
        .withSender(SUPPORT_AT_RESOLUTE_BI_COM)
        .withRecipient(user.getEmail())
        .withSubject(notificationEvent.getEventType().getDisplayName())
        .withBody(notificationEvent.getTokenSubstitutedTemplateBody(user))
        .build();    
  }
  
  protected EmailDto buildEmail(
      AbstractUserEntity user,
      String subject,
      String body) {

    return EmailDto
        .builder()
        .withSender(SUPPORT_AT_RESOLUTE_BI_COM)
        .withRecipient(user.getEmail())
        .withSubject(subject)
        .withBody(body)
        .build();    
  }  
}
//@formatter:on
