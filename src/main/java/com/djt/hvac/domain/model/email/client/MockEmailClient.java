//@formatter:off
package com.djt.hvac.domain.model.email.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.djt.hvac.domain.model.email.dto.EmailDto;
import com.djt.hvac.domain.model.email.exception.EmailClientException;
import com.djt.hvac.domain.model.nodehierarchy.building.BillableBuildingEntity;
import com.djt.hvac.domain.model.notification.entity.NotificationEventEntity;
import com.djt.hvac.domain.model.user.AbstractUserEntity;
import com.djt.hvac.domain.model.user.DistributorUserEntity;

public class MockEmailClient extends AbstractEmailClient {
  
  private static final MockEmailClient INSTANCE = new MockEmailClient();
  public static final MockEmailClient getInstance() {
    return INSTANCE;
  }
  
  private static Integer MAX_PERSISTENT_IDENTITY_VALUE = Integer.valueOf(0);
  synchronized private static Integer getNextPersistentIdentityValue() {
    
    Integer nextValue = Integer.valueOf((MAX_PERSISTENT_IDENTITY_VALUE.intValue()-1));
    MAX_PERSISTENT_IDENTITY_VALUE = nextValue;
    return nextValue;
  }

  private Map<String, List<EmailDto>> sentEmails = new TreeMap<>();
 
  private MockEmailClient() {
    super(EmailClient.SUPPORT_AT_RESOLUTE_BI_COM);
  }
  
  public void reset() {
    this.sentEmails.clear();
  }
  
  @Override
  public int sendGracePeriodExpirationWarningEmail(
      DistributorUserEntity accountManager,
      BillableBuildingEntity billableBuilding)
  throws
      EmailClientException {
    
    EmailDto emailDto = buildGracePeriodExpirationWarningEmail(
        accountManager,
        billableBuilding);
    
    Integer emailNotificationId = getNextPersistentIdentityValue();
    
    EmailDto sentEmailDto = EmailDto
        .builder(emailDto)
        .withId(emailNotificationId)
        .build();
    
    saveEmail(emailDto.getRecipient(), sentEmailDto);
    
    return emailNotificationId;
  }
  
  @Override
  public void sendEmailNotification(AbstractUserEntity user, NotificationEventEntity notificationEvent) {
    
    EmailDto emailDto = buildNotificationEmail(
        user,
        notificationEvent);
    
    saveEmail(emailDto.getRecipient(), emailDto);
  }
  
  @Override
  public void sendEmail(
      AbstractUserEntity user,
      String subject,
      String body) {
    
    EmailDto emailDto = buildEmail(
        user,
        subject,
        body);
    
    saveEmail(emailDto.getRecipient(), emailDto);
  }
  
  private void saveEmail(String recipient, EmailDto emailDto) {
    
    List<EmailDto> emails = sentEmails.get(recipient);
    if (emails == null) {
      
      emails = new ArrayList<>();
      sentEmails.put(recipient, emails);
    }
    emails.add(emailDto);
  }
  
  public List<EmailDto> getSentEmails(String recipient) {
    
    List<EmailDto> list = sentEmails.get(recipient);
    
    if (list == null) {
      list = new ArrayList<>();
    }
    
    return list;
  }
}
//@formatter:on