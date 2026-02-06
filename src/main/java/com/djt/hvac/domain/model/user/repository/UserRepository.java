//@formatter:off
package com.djt.hvac.domain.model.user.repository;

import java.util.Collection;

import com.djt.hvac.domain.model.common.exception.EntityAlreadyExistsException;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.distributor.AbstractDistributorEntity;
import com.djt.hvac.domain.model.user.AbstractUserEntity;
import com.djt.hvac.domain.model.user.CustomerUserEntity;
import com.djt.hvac.domain.model.user.DistributorUserEntity;
import com.djt.hvac.domain.model.user.enums.UserRoleType;

/**
 * 
 * @author tommyers
 * 
 */
public interface UserRepository {
  
  /**
   * For now, this is only used by the tests, but can be refactored to support production use.
   * 
   * @param parentDistributor The parent distributor
   * @param userRole Must be either Distributor Admin or Distributor User
   * @param email The user email - must be unique among all users for any distributor (global)
   * @param firstName The first name
   * @param lastName The last name
   * @param isAccountManager Whether or not the user is an account manager or not 
   * (role must be 'ADMIN'
   * 
   * @return Thew newly created distributor user
   * 
   * @throws EntityAlreadyExistsException If the distributor user already exists
   * @throws EntityDoesNotExistException If the parent distributor does not exist
   */
  DistributorUserEntity createDistributorUser(
      AbstractDistributorEntity parentDistributor,
      UserRoleType userRole,
      String email,
      String firstName,
      String lastName,
      Boolean isAccountManager)
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException;

  /**
   * 
   * @param parentCustomer
   * @param userRole
   * @param email
   * @param firstName
   * @param lastName
   * @return
   * @throws EntityAlreadyExistsException
   * @throws EntityDoesNotExistException
   */
  CustomerUserEntity createCustomerUser(
      AbstractCustomerEntity parentCustomer,
      UserRoleType userRole,
      String email,
      String firstName,
      String lastName)
  throws 
      EntityAlreadyExistsException,
      EntityDoesNotExistException;
  
  /**
   * 
   * @param parentDistributor The parent distributor
   * @return The users for the given distributor
   */
  Collection<AbstractUserEntity> loadDistributorUsers(AbstractDistributorEntity parentDistributor);

  /**
   * 
   * @param parentDistributor The parent customer
   * @return The users for the given customer
   */
  Collection<AbstractUserEntity> loadCustomerUsers(AbstractCustomerEntity parentCustomer);
  
  /**
   * 
   * @param userId
   * @param rootDistributor
   * @return
   * @throws EntityDoesNotExistException
   */
  AbstractUserEntity loadUser(Integer userId, AbstractDistributorEntity rootDistributor) throws EntityDoesNotExistException;
  
  /**
   * 
   * @param user
   */
  void storeUser(AbstractUserEntity user);
  
  /**
   * 
   * @param rootDistributor
   * 
   * @return
   */
  Collection<AbstractUserEntity> loadAllUsers(AbstractDistributorEntity rootDistributor);

  /**
   * 
   * @param parentDistributors The parent distributors to load the users for
   */
  void loadAllDistributorUsers(Collection<AbstractDistributorEntity> parentDistributors);
  
  /**
   * 
   * @param parentDistributor The parent distributor
   */
  void storeDistributorUsers(AbstractDistributorEntity parentDistributor);
  
  /**
   * 
   * @param parentCustomer The parent customers to load the users for
   */
  void loadAllCustomerUsers(Collection<AbstractCustomerEntity> parentCustomers);
  
  /**
   * 
   * @param parentCustomer The parent customer
   */
  void storeCustomerUsers(AbstractCustomerEntity parentCustomer);
  
}
//@formatter:on