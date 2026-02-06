//@formatter:off
package com.djt.hvac.domain.model.customer;

import java.sql.Timestamp;
import java.time.LocalDate;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.customer.enums.CustomerPaymentStatus;
import com.djt.hvac.domain.model.customer.enums.CustomerStatus;
import com.djt.hvac.domain.model.dictionary.enums.UnitSystem;
import com.djt.hvac.domain.model.distributor.AbstractDistributorEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BillableBuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.enums.BuildingStatus;

public class OnlineCustomerEntity extends AbstractCustomerEntity {
  private static final long serialVersionUID = 1L;
  // For new instances (i.e. have not been persisted yet)
  public OnlineCustomerEntity(
      AbstractDistributorEntity parentDistributor,
      String name,
      UnitSystem unitSystem) {
    super(
        parentDistributor,
        name,
        unitSystem);
  }
  
  public OnlineCustomerEntity(
      Integer persistentIdentity,
      AbstractDistributorEntity parentDistributor,
      String name,
      String uuid,
      UnitSystem unitSystem,
      Timestamp createdAt,
      Timestamp updatedAt,
      Timestamp startDate,
      CustomerStatus customerStatus,
      Timestamp customerStatusUpdatedAt,
      CustomerPaymentStatus customerPaymentStatus,
      Timestamp customerPaymentStatusUpdatedAt) {
    this(
        persistentIdentity,
        parentDistributor,
        name,
        uuid,
        unitSystem,
        createdAt,
        updatedAt,
        startDate,
        customerStatus,
        customerStatusUpdatedAt,
        customerPaymentStatus,
        customerPaymentStatusUpdatedAt,
        null);
  }

  public OnlineCustomerEntity(
      Integer persistentIdentity,
      AbstractDistributorEntity parentDistributor,
      String name,
      String uuid,
      UnitSystem unitSystem,
      Timestamp createdAt,
      Timestamp updatedAt,
      Timestamp startDate,
      CustomerStatus customerStatus,
      Timestamp customerStatusUpdatedAt,
      CustomerPaymentStatus customerPaymentStatus,
      Timestamp customerPaymentStatusUpdatedAt,
      PortfolioEntity portfolio) {
    super(
        persistentIdentity,
        parentDistributor,
        name,
        uuid,
        unitSystem,
        createdAt,
        updatedAt,
        startDate,
        customerStatus,
        customerStatusUpdatedAt,
        customerPaymentStatus,
        customerPaymentStatusUpdatedAt,
        portfolio);
  }
  
  @Override
  public boolean allowAutomaticConfiguration() {
    return true;
  }
  
  public int getActiveBuildingCount() {
    
    int numActiveBuildings = 0;
    if (getChildPortfolio() != null) {
      for (BuildingEntity childBuilding: getChildPortfolio().getChildBuildings()) {
        if (childBuilding instanceof BillableBuildingEntity) {
          
          BillableBuildingEntity bb = (BillableBuildingEntity)childBuilding;
          if (bb.getBuildingStatus().equals(BuildingStatus.ACTIVE)) {
            
            numActiveBuildings++;
          }
        } else {
          throw new IllegalStateException("Online customer: ["
              + this
              + "] must have child billable buildings only, but a non billable building: ["
              + childBuilding
              + "] was found.");
        }
      }
    }
    return numActiveBuildings;
  }
  
  @Override
  public void setIsDeleted() {
    
    validateHardDeletable();
    super.setIsDeleted();
  }
  
  public LocalDate getSoftDeletePeriodEnd() {
    
    LocalDate statusUpdatedAt = getCustomerStatusUpdatedAt()
        .toLocalDateTime()
        .toLocalDate();

    return statusUpdatedAt.plusDays(30);
  }  
  
  public int getNumberDaysSinceSoftDeletePeriodEnd() {

    LocalDate currentLocalDate = AbstractEntity.getTimeKeeper().getCurrentLocalDate();
    
    LocalDate statusUpdatedAt = getCustomerStatusUpdatedAt()
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
    CustomerStatus customerStatus = getCustomerStatus();
    if (!customerStatus.equals(CustomerStatus.DELETED) && getNumberDaysSinceSoftDeletePeriodEnd() > 30) {

      throw new IllegalStateException(
          this   
          + " cannot be hard deleted because its status has not been DELETED for greater than 30 days.  Rather, it is: ["
          + customerStatus
          + "] and ["
          + getNumberDaysSinceSoftDeletePeriodEnd()
          + "] number of days.");      
    }
  }  
}
//@formatter:on