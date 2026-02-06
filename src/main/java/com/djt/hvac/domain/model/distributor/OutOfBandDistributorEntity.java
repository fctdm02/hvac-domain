//@formatter:off
package com.djt.hvac.domain.model.distributor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.enums.UnitSystem;
import com.djt.hvac.domain.model.distributor.enums.DistributorPaymentStatus;
import com.djt.hvac.domain.model.distributor.enums.DistributorStatus;
import com.djt.hvac.domain.model.referralagent.AbstractReferralAgentEntity;
import com.djt.hvac.domain.model.referralagent.InternalOrganizationalReferralAgentEntity;

public class OutOfBandDistributorEntity extends AbstractDistributorEntity {
  private static final long serialVersionUID = 1L;
  public static final String DISTRIBUTOR_TYPE_OUT_OF_BAND = "OUT_OF_BAND";
  
  
  public static OutOfBandDistributorEntity buildResoluteDistributorStub() {
    
    return new OutOfBandDistributorEntity(
        1,
        null,
        "Resolute",
        AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
        AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
        "d637555e-c809-4adc-ade8-336d5b8daab8",
        UnitSystem.IP,
        InternalOrganizationalReferralAgentEntity.buildResoluteReferralAgentStub(),
        DistributorStatus.CREATED,
        AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
        DistributorPaymentStatus.UP_TO_DATE,
        AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
        Timestamp.from(LocalDateTime.parse("1/1/2015 12:00 PM", DateTimeFormatter.ofPattern("M/d/uuuu hh:mm a", Locale.US)).atZone(ZoneId.of("America/New_York")).toInstant()),
        Timestamp.from(LocalDateTime.parse("1/1/2115 12:00 PM", DateTimeFormatter.ofPattern("M/d/uuuu hh:mm a", Locale.US)).atZone(ZoneId.of("America/New_York")).toInstant()));
  }

  
  
  private Timestamp billingStartDate;
  private Timestamp billingRenewalDate;

  // For new instances (i.e. have not been persisted yet)
  public OutOfBandDistributorEntity(
      AbstractDistributorEntity parentDistributor,
      String name,
      UnitSystem unitSystem) {
    super(
        parentDistributor,
        name,
        unitSystem);
  }
  
  public OutOfBandDistributorEntity(
      Integer persistentIdentity,
      AbstractDistributorEntity parentDistributor,
      String name,
      Timestamp createdAt,
      Timestamp updatedAt,
      String uuid,
      UnitSystem unitSystem,
      AbstractReferralAgentEntity referralAgent,
      DistributorStatus distributorStatus,
      Timestamp distributorStatusUpdatedAt,
      DistributorPaymentStatus distributorPaymentStatus,
      Timestamp distributorPaymentStatusUpdatedAt,
      Timestamp billingStartDate,
      Timestamp billingRenewalDate) {
    super(
        persistentIdentity,
        parentDistributor,
        name,
        createdAt,
        updatedAt,
        uuid,
        unitSystem,
        referralAgent,
        distributorStatus,
        distributorStatusUpdatedAt,
        distributorPaymentStatus,
        distributorPaymentStatusUpdatedAt);
    this.billingStartDate = billingStartDate;
    this.billingRenewalDate = billingRenewalDate;
  }
  
  public Timestamp getBillingStartDate() {
    return billingStartDate;
  }

  public void setBillingStartDate(Timestamp billingStartDate) {
    this.billingStartDate = billingStartDate;
    this.setIsModified("billingStartDate");
  }

  public Timestamp getBillingRenewalDate() {
    return billingRenewalDate;
  }

  public void setBillingRenewalDate(Timestamp billingRenewalDate) {
    this.billingRenewalDate = billingRenewalDate;
    this.setIsModified("billingRenewalDate");
  }
  
  @Override
  public void validate(
      Set<IssueType> issueTypes, 
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
   
    super.validate(issueTypes, validationMessages, remediate);
  }

  @Override
  public String getDistributorTypeDescription() {
    return DISTRIBUTOR_TYPE_OUT_OF_BAND;
  }
}
//@formatter:on