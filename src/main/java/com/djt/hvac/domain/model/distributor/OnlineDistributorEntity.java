//@formatter:off
package com.djt.hvac.domain.model.distributor;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.customer.OnlineCustomerEntity;
import com.djt.hvac.domain.model.customer.enums.CustomerStatus;
import com.djt.hvac.domain.model.dictionary.enums.UnitSystem;
import com.djt.hvac.domain.model.distributor.enums.DistributorPaymentStatus;
import com.djt.hvac.domain.model.distributor.enums.DistributorStatus;
import com.djt.hvac.domain.model.distributor.paymentmethod.AbstractPaymentMethodEntity;
import com.djt.hvac.domain.model.referralagent.AbstractReferralAgentEntity;

public class OnlineDistributorEntity extends AbstractDistributorEntity {
  private static final long serialVersionUID = 1L;
  public static final String DISTRIBUTOR_TYPE_ONLINE = "ONLINE";

  private String stripeCustomerId; // Gets set at the time when the first descendant child building subscription is created (Stripe interaction)
  private boolean allowOutOfBandBuildings = false; // Only has meaning for ONLINE distributors.
  private Set<AbstractPaymentMethodEntity> childPaymentMethods = new TreeSet<>();

  // For new instances (i.e. have not been persisted yet)
  public OnlineDistributorEntity(
      AbstractDistributorEntity parentDistributor,
      String name,
      UnitSystem unitSystem) {
    super(
        parentDistributor,
        name,
        unitSystem);
  }

  // For new instances (i.e. have not been persisted yet)
  public OnlineDistributorEntity(
      AbstractDistributorEntity parentDistributor,
      String name,
      UnitSystem unitSystem,
      boolean allowOutOfBandBuildings) {
    super(
        parentDistributor,
        name,
        unitSystem);
    this.allowOutOfBandBuildings = allowOutOfBandBuildings;
  }
  
  public OnlineDistributorEntity(
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
      boolean allowOutOfBandBuildings) {
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
    this.allowOutOfBandBuildings = allowOutOfBandBuildings;
  }
  
  public boolean getAllowOutOfBandBuildings() {
    return this.allowOutOfBandBuildings;
  }
  
  public Set<AbstractPaymentMethodEntity> getChildPaymentMethods() {
    return childPaymentMethods;
  }

  public boolean addChildPaymentMethod(AbstractPaymentMethodEntity paymentMethod) throws EntityAlreadyExistsException {
    return addChild(childPaymentMethods, paymentMethod, this);
  }

  public AbstractPaymentMethodEntity getChildPaymentMethod(Integer persistentIdentity) throws EntityDoesNotExistException {
    return getChild(AbstractPaymentMethodEntity.class, childPaymentMethods, persistentIdentity, this);
  }

  public AbstractPaymentMethodEntity getChildPaymentMethodNullIfNotExists(Integer persistentIdentity) {
    
    for (AbstractPaymentMethodEntity paymentMethod: childPaymentMethods) {
      if (paymentMethod.getPersistentIdentity().equals(persistentIdentity)) {
        return paymentMethod;
      }
    }
    return null;
  }
  
  public AbstractPaymentMethodEntity removeChildPaymentMethod(Integer persistentIdentity) throws EntityDoesNotExistException {
    
    AbstractPaymentMethodEntity childPaymentMethod = getChildPaymentMethod(persistentIdentity);
    childPaymentMethod.setIsDeleted();
    return childPaymentMethod;
  }
    
  public String getStripeCustomerId() {
    return stripeCustomerId;
  }
  
  public void setStripeCustomerId(String stripeCustomerId) {
    this.stripeCustomerId = stripeCustomerId;
    this.setIsModified("stripeCustomerId");
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
    return DISTRIBUTOR_TYPE_ONLINE;
  }
  
  public int getBillableCustomersCount() {
    
    int numBillableCustomers = 0;
    
    for (AbstractCustomerEntity childCustomer: getChildCustomers()) {

      if (childCustomer instanceof OnlineCustomerEntity) {
        
        OnlineCustomerEntity oc = (OnlineCustomerEntity)childCustomer;
        if (oc.getCustomerStatus().equals(CustomerStatus.BILLABLE)) {
          
          numBillableCustomers++;
        }
      } else {
        throw new IllegalStateException("Online distributor: ["
            + this
            + "] must have child online customers only, but a non online customer: ["
            + childCustomer
            + "] was found.");
      }
    }
    return numBillableCustomers;
  }
  
  @Override
  public void setIsDeleted() {
    
    validateHardDeletable();
    super.setIsDeleted();
  }

  
  public LocalDate getSoftDeletePeriodEnd() {
    
    LocalDate statusUpdatedAt = getDistributorStatusUpdatedAt()
        .toLocalDateTime()
        .toLocalDate();

    LocalDate softDeletePeriodEnd = statusUpdatedAt.plusDays(30);
    
    return softDeletePeriodEnd;
  }  
  
  public int getNumberDaysSinceSoftDeletePeriodEnd() {

    LocalDate currentLocalDate = AbstractEntity.getTimeKeeper().getCurrentLocalDate();
    
    LocalDate statusUpdatedAt = getDistributorStatusUpdatedAt()
        .toLocalDateTime()
        .toLocalDate();
    
    int numDays = 0;
    LocalDate ld = statusUpdatedAt;
    while (ld.isBefore(currentLocalDate)) {
      
      numDays++;
      ld = ld.plusDays(1);
    }
    
    return numDays;
  }
  
  public void validateHardDeletable() {
    
    // We can only hard delete a customer if it has been in the "soft deleted" state for greater than 30 days.
    DistributorStatus distributorStatus = getDistributorStatus();
    if (!distributorStatus.equals(DistributorStatus.DELETED) && getNumberDaysSinceSoftDeletePeriodEnd() > 30) {

      throw new IllegalStateException(
          this   
          + " cannot be hard deleted because its status has not been DELETED for greater than 30 days.  Rather, it is: ["
          + distributorStatus
          + "] and ["
          + getNumberDaysSinceSoftDeletePeriodEnd()
          + "] number of days.");      
    }
  }
  
  public Timestamp getCreatePeriodEndTimestamp() {
    
    Timestamp statusUpdatedAtTimestamp = getDistributorPaymentStatusUpdatedAt();
    
    LocalDate statusUpdatedAtLocalDate = statusUpdatedAtTimestamp
        .toLocalDateTime()
        .toLocalDate();

    LocalDate createPeriodEndLocalDate = statusUpdatedAtLocalDate.plusDays(90);
    
    Timestamp createPeriodEndTimestamp = Timestamp.valueOf(createPeriodEndLocalDate.atStartOfDay());
    
    return createPeriodEndTimestamp;
  }  
  
  public boolean hasCreatePeriodExpired() {
    
    long currentTimeMillis = getTimeKeeper().getCurrentTimeInMillis();
    long distributorStatusUpdatedAtMillis = getDistributorStatusUpdatedAt().getTime();
    
    long durationMillis = currentTimeMillis - distributorStatusUpdatedAtMillis;
    
    long durationDays = TimeUnit.MILLISECONDS.toDays(durationMillis);
    if (durationDays >= 90) {
      return true;
    }    
    return false;
  }
  
  public void validateSoftDeletable() {
    
    // We can only move an online distributor to the DELETED state if it has been
    // in the CREATED state for 90 days or more.
    DistributorStatus distributorStatus = getDistributorStatus();
    if (!distributorStatus.equals(DistributorStatus.CREATED)) {

      throw new IllegalStateException(
          this   
          + " can only be deleted when its status has been CREATED for greater than 90 days, yet status is: ["
          + distributorStatus
          + "]");      
    }

    if (!hasCreatePeriodExpired()) {

      Timestamp currentTimestamp = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
      Timestamp createPeriodEndTimestamp = getCreatePeriodEndTimestamp();
      
      throw new IllegalStateException(
          this   
          + " can only be put in the DELETED state when it has been in the CREATED for greater than 90 days.  Current time: ["
          + currentTimestamp
          + "] and has been in DELETED status since: ["
          + createPeriodEndTimestamp
          + "].");      
    }
  }
  
  public long getNumberDaysInDelinquentState() {
    
    if (!getDistributorPaymentStatus().equals(DistributorPaymentStatus.DELINQUENT)) {
      
      throw new IllegalStateException("numberDaysPastDue() should only be invoked when the distributor is in the DELINQUENT state. Distributor: "
          + this
          + "] ");
    }
    
    long currentTimeMillis = getTimeKeeper().getCurrentTimeInMillis();
    
    long distributorPaymentStatusUpdatedAtMillis = getDistributorPaymentStatusUpdatedAt().getTime();
    
    long durationMillis = currentTimeMillis - distributorPaymentStatusUpdatedAtMillis;
    
    long durationDays = TimeUnit.MILLISECONDS.toDays(durationMillis);

    return durationDays;
  }  
}
//@formatter:on