package com.djt.hvac.domain.model.user;

import static java.util.Objects.requireNonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.distributor.AbstractDistributorEntity;
import com.djt.hvac.domain.model.user.enums.UserRoleType;
import com.djt.hvac.domain.model.user.enums.UserType;

public class DistributorUserEntity extends AbstractUserEntity {
  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = LoggerFactory.getLogger(DistributorUserEntity.class);
  
  private final AbstractDistributorEntity parentDistributor;
  private Boolean isAccountManager;

  public DistributorUserEntity(
      UserRoleType userRole,
      String email,
      String firstName,
      String lastName,
      boolean acceptedTerms,
      boolean enableReportNotifications,
      AbstractDistributorEntity parentDistributor,
      boolean isAccountManager) {
    this(
        null,
        userRole,
        email,
        firstName,
        lastName,
        acceptedTerms,
        enableReportNotifications,
        parentDistributor,
        isAccountManager);
  }
  
  public DistributorUserEntity(
      Integer persistentIdentity,
      UserRoleType userRole,
      String email,
      String firstName,
      String lastName,
      boolean acceptedTerms,
      boolean enableReportNotifications,
      AbstractDistributorEntity parentDistributor,
      boolean isAccountManager) {
    super(
        persistentIdentity,
        userRole,
        UserType.DISTRIBUTOR_USER,
        email,
        firstName,
        lastName,
        acceptedTerms,
        enableReportNotifications);
    requireNonNull(parentDistributor, "parentDistributor cannot be null");
    requireNonNull(isAccountManager, "isAccountManager cannot be null");
    this.parentDistributor = parentDistributor;
    this.isAccountManager = isAccountManager;
    
    if (userRole.equals(UserRoleType.DISTRIBUTOR_USER) && isAccountManager) {
      
      throw new IllegalArgumentException("Distributor user role must be: ["
          + UserRoleType.DISTRIBUTOR_ADMIN 
          + "] if isAccountManager=true");
    }
  }  

  public AbstractDistributorEntity getParentDistributor() {
    return parentDistributor;
  }
  
  public Boolean isAccountManager() {
    return isAccountManager;
  }
  
  public void setAccountManager(Boolean isAccountManager) {
    
    if (!this.isAccountManager.equals(isAccountManager)) {
      
      if (isAccountManager && !getUserRole().equals(UserRoleType.DISTRIBUTOR_ADMIN)) {
        throw new IllegalStateException("Distributor user: ["
            + this
            + "] cannot be made the account manager because the user is not an administrator.");
      }
      LOGGER.info("Changing account manager status for distributor user: ["
          + this
          + "] to be: ["
          + isAccountManager
          + "].");
      this.isAccountManager = isAccountManager;
      setIsModified("isAccountManager");
    }
  }
}
