package com.djt.hvac.domain.model.user;

import static java.util.Objects.requireNonNull;

import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.user.enums.UserRoleType;
import com.djt.hvac.domain.model.user.enums.UserType;

public class CustomerUserEntity extends AbstractUserEntity {
  private static final long serialVersionUID = 1L;
  private AbstractCustomerEntity parentCustomer;

  public CustomerUserEntity(
      UserRoleType userRole,
      String email,
      String firstName,
      String lastName,
      boolean acceptedTerms,
      boolean enableReportNotifications,
      AbstractCustomerEntity parentCustomer) {
    this(
        null,
        userRole,
        email,
        firstName,
        lastName,
        acceptedTerms,
        enableReportNotifications,
        parentCustomer);
  }
  
  public CustomerUserEntity(
      Integer persistentIdentity,
      UserRoleType userRole,
      String email,
      String firstName,
      String lastName,
      boolean acceptedTerms,
      boolean enableReportNotifications,
      AbstractCustomerEntity parentCustomer) {
    super(
        persistentIdentity,
        userRole,
        UserType.CUSTOMER_USER,
        email,
        firstName,
        lastName,
        acceptedTerms,
        enableReportNotifications);
    requireNonNull(parentCustomer, "parentCustomer cannot be null");
    this.parentCustomer = parentCustomer;
  }  

  public AbstractCustomerEntity getParentCustomer() {
    return parentCustomer;
  }
  
  public void setParentCustomer(AbstractCustomerEntity customer) {
    this.parentCustomer = customer;
  }
}