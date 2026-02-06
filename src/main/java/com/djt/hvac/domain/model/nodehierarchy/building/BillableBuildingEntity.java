//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.building;

import java.sql.Timestamp;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.enums.UnitSystem;
import com.djt.hvac.domain.model.dictionary.weather.WeatherStationEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingPaymentStatus;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingPaymentType;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingStatus;

public class BillableBuildingEntity extends BuildingEntity {
  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = LoggerFactory.getLogger(BillableBuildingEntity.class);
  
  private Boolean pendingDeletion = Boolean.FALSE; // A building is hard deleted after the subscription period ends.  It is implied that the subscription was canceled.
  private Timestamp pendingDeletionUpdatedAt = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
  private BuildingSubscriptionEntity childBuildingSubscription;

  // For new instances (i.e. have not been persisted yet)
  public BillableBuildingEntity(
      PortfolioEntity parentNode,
      String name,
      String displayName,
      UnitSystem unitSystem) {
    super(
        parentNode,
        name,
        displayName,
        BuildingPaymentType.ONLINE,
        unitSystem);
  }
  
  public BillableBuildingEntity(
      Integer persistentIdentity,
      PortfolioEntity parentNode,
      String name,
      String displayName,
      String uuid,
      String createdAt,
      String updatedAt,
      Set <TagEntity> nodeTags,
      String timezone,
      String address,
      String city,
      String stateOrProvince,
      String postalCode,
      String countryCode,
      UnitSystem unitSystem,
      String latitude,
      String longitude,
      WeatherStationEntity weatherStation,
      BuildingStatus buildingStatus,
      Timestamp buildingStatusUpdatedAt,
      BuildingPaymentStatus buildingPaymentStatus,
      Timestamp buildingPaymentStatusUpdatedAt,
      String billingGracePeriod,
      Integer buildingGracePeriodWarningNotificationId,
      Boolean pendingDeletion,
      Timestamp pendingDeletionUpdatedAt) {
    super(
        persistentIdentity, 
        parentNode, 
        name, 
        displayName,
        uuid,
        createdAt,
        updatedAt,
        nodeTags,
        timezone,
        address,
        city,
        stateOrProvince,
        postalCode,
        countryCode,
        unitSystem,
        latitude,
        longitude,
        weatherStation,
        buildingStatus,
        buildingStatusUpdatedAt,
        buildingPaymentStatus,
        buildingPaymentStatusUpdatedAt,
        billingGracePeriod,
        buildingGracePeriodWarningNotificationId,
        BuildingPaymentType.ONLINE);

    this.pendingDeletion = pendingDeletion;
    this.pendingDeletionUpdatedAt = pendingDeletionUpdatedAt;
  }

  public BuildingSubscriptionEntity getChildBuildingSubscriptionNullIfNotExists() {
    return childBuildingSubscription;
  }

  public void setChildBuildingSubscription(BuildingSubscriptionEntity childBuildingSubscription) {

    BuildingStatus currentBuildingStatus = getBuildingStatus();
    BuildingPaymentStatus currentBuildingPaymentStatus = getBuildingPaymentStatus();
    
    if (this.childBuildingSubscription != null) {
      throw new IllegalStateException("Billable building: ["
          + getNodePath()
          + "] already has a building subscription: ["
          + this.childBuildingSubscription
          + "], config status: ["
          + currentBuildingStatus
          + "], payment status: ["
          + currentBuildingPaymentStatus
          + "].");
    }
    
    this.childBuildingSubscription = childBuildingSubscription;
    setIsModified("childBuildingSubscription");
    
    // Setting the building subscription means that we are creating it, thus, we are activating the building.
    // The payment state, if DELINQUENT, moves to UP_TO_DATE.
    if (currentBuildingStatus.equals(BuildingStatus.PENDING_ACTIVATION)) {

      BuildingStatus newBuildingStatus = BuildingStatus.ACTIVE;
      setBuildingStatus(newBuildingStatus);
      
      LOGGER.info("{}: {} : config status: {} --> {} --> : subscription: {}",
          AbstractEntity.getTimeKeeper().getCurrentLocalDate(),
          getPersistentIdentity(),
          currentBuildingStatus,
          newBuildingStatus,
          childBuildingSubscription);

      if (currentBuildingPaymentStatus.equals(BuildingPaymentStatus.DELINQUENT)) {

        BuildingPaymentStatus newBuildingPaymentStatus = BuildingPaymentStatus.UP_TO_DATE;
        setBuildingPaymentStatus(newBuildingPaymentStatus);
        
        LOGGER.info("{}: {}: payment status: {} --> {}: as building has been activated, subscription: {}",
            AbstractEntity.getTimeKeeper().getCurrentLocalDate(),
            getPersistentIdentity(),
            currentBuildingPaymentStatus,
            newBuildingPaymentStatus,
            childBuildingSubscription);        
      }      
    }
  }
  
  public Boolean getPendingDeletion() {
    return pendingDeletion;
  }

  public void setPendingDeletion() {
    
    if (pendingDeletion.booleanValue()) {
      
      throw new IllegalStateException(
          getNodePath() 
          + " has already been canceled, as of: "
          + pendingDeletionUpdatedAt);      
    }
    
    BuildingStatus buildingStatus = getBuildingStatus();
    BuildingPaymentStatus buildingPaymentStatus = getBuildingPaymentStatus();
    
    if (buildingStatus.equals(BuildingStatus.ACTIVE) 
        && buildingPaymentStatus.equals(BuildingPaymentStatus.DELINQUENT)) {
      
      throw new IllegalStateException(
          getNodePath() 
          + " cannot be canceled when its config status is ACTIVE and its payment status is DELINQUENT");      
    }
    
    this.pendingDeletion = Boolean.TRUE;
    this.pendingDeletionUpdatedAt = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
    this.setIsModified("pendingDeletion");
  }

  public Timestamp getPendingDeletionUpdatedAt() {
    return pendingDeletionUpdatedAt;
  }
  
  @Override
  public void setIsDeleted() {
    
    validateHardDeletable();
    super.setIsDeleted();
  }
  
  public void cancelSubscription() {

    if (childBuildingSubscription == null) {
      
      throw new IllegalStateException(
          getNodePath() 
          + " does not have a subscription, so there is nothing to cancel.");
    }
    
    // Pending deletion updated at is set at the same time.  Once the current (i.e. last) payment
    // interval ends, then the building can be hard deleted.  So, in other words, canceling the
    // subscription, is essentially "soft deleting" the building (hence the pending deletion flag).
    setPendingDeletion();
  }
  
  public boolean isSubscriptionCanceled() {

    boolean isSubscriptionCanceled = false;
    if (childBuildingSubscription != null && pendingDeletion.equals(Boolean.TRUE)) {
      
      isSubscriptionCanceled = true;
    }
    return isSubscriptionCanceled;
  }
  
  public boolean shouldBeHardDeleted() {
    
    // We can only remove a building (corresponding to a hard delete when dealing with the repository
    // (i.e. persistence) layer, if:
    // =============================
    // 1. If it is billable, then pending deletion is true and:
    //     a. It is in the CREATED or PENDING_ACTIVATION state
    //     OR
    //     b. It is in the ACTIVE state, then only if all of the following are true:
    //         i. The pending deletion flag is TRUE
    //         ii. the payment state is UP_TO_DATE
    //         iii. The current (i.e. last) payment interval has expired/passed (i.e. in the past)
    //
    // 2. It is not billable (i.e. parent customer is out of band)
    //
    BuildingStatus buildingStatus = getBuildingStatus();
    if (pendingDeletion.booleanValue()) {
      
      if (!buildingStatus.equals(BuildingStatus.ACTIVE)) {
        return true;
      }

      BuildingPaymentStatus buildingPaymentStatus = this.getBuildingPaymentStatus();
      if (!buildingPaymentStatus.equals(BuildingPaymentStatus.UP_TO_DATE)) {
        return false;
      }

      if (childBuildingSubscription != null && childBuildingSubscription.hasCurrentPaymentIntervalExpired()) {
        return true;
      }
    }
    return false;
  }
  
  public void validateHardDeletable() {
    
    // We can only remove a building (corresponding to a hard delete when dealing with the repository
    // (i.e. persistence) layer, if:
    // =============================
    // 1. If it is billable, then if:
    //     a. It is in the CREATED or PENDING_ACTIVATION state
    //     b. It is in the ACTIVE state, then only if all of the following are true:
    //         i. The pending deletion flag is TRUE
    //         ii. the payment state is UP_TO_DATE
    //         iii. The current (i.e. last) payment interval has expired/passed (i.e. in the past)
    //
    // 2. It is not billable (i.e. parent customer is out of band)
    //
    BuildingStatus buildingStatus = this.getBuildingStatus();
    if (buildingStatus.equals(BuildingStatus.ACTIVE)) {

      if (!pendingDeletion.booleanValue()) {

        throw new IllegalStateException(
            getNodePath() 
            + " cannot be hard deleted when its status is ACTIVE and pending deletion is not TRUE.");      
      }
      
      // 1.b.ii.
      BuildingPaymentStatus buildingPaymentStatus = this.getBuildingPaymentStatus();
      if (!buildingPaymentStatus.equals(BuildingPaymentStatus.UP_TO_DATE)) {
        
        throw new IllegalStateException(
            getNodePath() 
            + " cannot be hard deleted when its payment status is not UP_TO_DATE.");      
      }
      
      if (childBuildingSubscription == null) {

        throw new IllegalStateException(
            getNodePath() 
            + " does not have a building subscription, even though it is in the ACTIVE state.");      
      }

      // 1.b.iii.
      boolean hasCurrentPaymentIntervalExpired = childBuildingSubscription.hasCurrentPaymentIntervalExpired();
      if (!hasCurrentPaymentIntervalExpired) {
        
        throw new IllegalStateException(
            getNodePath() 
            + " cannot be hard deleted when its status is ACTIVE and the current time: ["
            + AbstractEntity.getTimeKeeper().getCurrentTimestamp()
            + "] is earlier than the current (i.e. last) payment interval's end time, which is: ["
            + childBuildingSubscription.getCurrentIntervalEndsAt()
            + "].");
      }
    }
  }  
  
  @Override
  public void evaluateConfigState() {
    
    // This method just validates the state as the actual state transitions are done when 
    // entities are created/updated/deleted (e.g. child mapped points and child subscription) 
    switch (getBuildingStatus()) {
      
      case CREATED:
        if (getTotalMappedPointCount() > 0) {
          
          throw new IllegalStateException(
              getNodePath() 
              + ": building status is CREATED, yet has mapped point count: ["
              + getTotalMappedPointCount()
              + "].");
        }
        break;
        
      case PENDING_ACTIVATION:

        if (childBuildingSubscription != null) {

          throw new IllegalStateException(
              getNodePath() 
              + ": building status is PENDING_ACTIVATION, yet has subscription: ["
              + childBuildingSubscription
              + "].");
        }
        break;

      case ACTIVE:
        if (childBuildingSubscription == null) {

          throw new IllegalStateException(
              getNodePath() 
              + ": building status is ACTIVE, yet does not have a subscription.");
        }
        break;
    }
  } 
    
  @Override
  public void evaluatePaymentState() {
    
    // See if the payment interval has expired, and thus, we need to check on the 
    // status of the payment for the new interval, which Stripe automatically
    // performs as part of the Stripe subscription.
    BuildingStatus buildingStatus = getBuildingStatus();
    BuildingPaymentStatus buildingPaymentStatus = getBuildingPaymentStatus();
    
    // This method handles time based state transitions.  Transitions from failed payments are
    // handled by the Stripe context that does "nightly" procesing of payments. 
    if (buildingPaymentStatus.equals(BuildingPaymentStatus.UP_TO_DATE)) {

      // NOTE: The up to date to delinquent transition can occur in one of two ways:
      // 1: A payment on the subscription has failed.  The occurs with the "nightly" processing,
      // so is not dealt with here.
      // 
      // 2: If the grace period has expired, implying building status of pending activation,
      // then move into the delinquent building payment status.
      if (buildingStatus.equals(BuildingStatus.PENDING_ACTIVATION) && hasGracePeriodExpired()) {

        setBuildingPaymentStatus(BuildingPaymentStatus.DELINQUENT);
        LOGGER.info("{}:{} building payment status: UP_TO_DATE --> DELINQUENT: grace period: [{}] has expired, as it started on: [{}] and the current time is: [{}]",
            AbstractEntity.getTimeKeeper().getCurrentLocalDate(),
            getPersistentIdentity(),
            getBillingGracePeriod(),
            getBuildingStatusUpdatedAt(),
            AbstractEntity.getTimeKeeper().getCurrentTimestamp());
      }
      
    } else if (buildingPaymentStatus.equals(BuildingPaymentStatus.DELINQUENT)) {

      // DELIQUENT to UP_TO_DATE happens automatically when building status goes from
      // PENDING_ACTIVATION to ACTIVE (i.e. when a subscription is successfully created,
      // meaning that the first payment was posted to Stripe successfully as well).
      //
      // Or if the user processed all failed payments manually, doing this also causes a 
      // automatic state transition.
      
    }
  }
  
  @Override
  public void evaluatePendingDeletionState() {
    
    // Pending deletion handling is done at the parent customer level.
  }
  
  @Override
  public String toString() {
    
    return new StringBuilder()
        .append(super.toString())
        .append(" building status: ")
        .append(getBuildingStatus())
        .append(" updated at: ")
        .append(getBuildingStatusUpdatedAt())
        .append("     payment status: ")
        .append(getBuildingPaymentStatus())
        .append(" updated at: ")
        .append(getBuildingPaymentStatusUpdatedAt())
        .toString();
  }
}
//@formatter:on