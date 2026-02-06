//@formatter:off
package com.djt.hvac.domain.model.customer;

import static java.util.Objects.requireNonNull;

import java.sql.Timestamp;

import com.djt.hvac.domain.model.customer.enums.CustomerPaymentStatus;
import com.djt.hvac.domain.model.customer.enums.CustomerStatus;
import com.djt.hvac.domain.model.dictionary.enums.UnitSystem;
import com.djt.hvac.domain.model.distributor.AbstractDistributorEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;

public class DemoCustomerEntity extends OutOfBandCustomerEntity {
  private static final long serialVersionUID = 1L;
  private Boolean isExpires = Boolean.FALSE;
  private Boolean isInternal = Boolean.FALSE;
  
  // For new instances (i.e. have not been persisted yet)
  public DemoCustomerEntity(
      AbstractDistributorEntity parentDistributor,
      String name,
      UnitSystem unitSystem) {
    super(
        parentDistributor,
        name,
        unitSystem);
  }
  
  public DemoCustomerEntity(
      AbstractDistributorEntity parentDistributor,
      String name,
      UnitSystem unitSystem,
      Boolean isExpires,
      Boolean isInternal) {
    super(
        parentDistributor,
        name,
        unitSystem);
    this.isExpires = isExpires;
    this.isInternal = isInternal;
  }
  
  public DemoCustomerEntity(
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
      Boolean isExpires,
      Boolean isInternal) {
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
        isExpires,
        isInternal,
        null);
  }

  public DemoCustomerEntity(
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
      Boolean isExpires,
      Boolean isInternal,
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
    requireNonNull(isExpires, "isExpires cannot be null");
    requireNonNull(isInternal, "isInternal cannot be null");
    this.isExpires = isExpires;
    this.isInternal = isInternal;
  }
  
  @Override
  public boolean allowAutomaticConfiguration() {
    return true;
  }
  
  public Boolean isExpires() {
    return isExpires;
  }
  
  public Boolean isInternal() {
    return isInternal;
  }  
}
//@formatter:on