//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service.async.lock.repository;

import java.util.Optional;

import com.djt.hvac.domain.model.nodehierarchy.service.async.lock.AsyncOperationLockEntity;
import com.djt.hvac.domain.model.nodehierarchy.service.async.lock.exception.AsyncOperationLockAcquisitionException;

/**
 * 
 * @author tmyers
 *
 */
public interface AsyncOperationLockRepository {
  
  /**
   * 
   * @param customerId
   * @param submittedBy
   * @return
   */
  Optional<AsyncOperationLockEntity> retrieveLockBySubmittedBy(
      Integer customerId,
      String submittedBy);
  
  /**
   * 
   * @param customerId
   * @param ownerUuid
   * @param operationCategory
   * @param operationType
   * @param submittedBy
   * 
   * @return
   * 
   * @throws AsyncOperationLockAcquisitionException
   */
  AsyncOperationLockEntity acquireLock(
      Integer customerId,
      String ownerUuid,
      String operationCategory,
      String operationType,
      String submittedBy) 
  throws 
      AsyncOperationLockAcquisitionException;
  
  /**
   * 
   * @param lock The lock to create/insert
   * 
   * @return 1 if successful, 0 if not
   */
  int createLock(AsyncOperationLockEntity lock);
  
  /**
   * 
   * @param customerId The customer id of the lock to retrieve,
   *        which is at the customer level, so one per customer.
   *        
   * @return The lock if present, empty if not.
   */
  Optional<AsyncOperationLockEntity> retrieveLock(Integer customerId);
  
  /**
   * 
   * @param customerId The customer id of the lock to retrieve,
   *        which is at the customer level, so one per customer.
   * @param operationCategory The operation category
   * @param operationType The operation type
   * @param ownerUuid The lock UUID/owner
   * @param submittedBy The lock owner email
   * 
   * @return The lock if present, empty if not.
   */
  Optional<AsyncOperationLockEntity> retrieveLock(
      Integer customerId,
      String operationCategory,
      String operationType,
      String ownerUuid,
      String submittedBy);  

  /**
   * 
   * @param customerId The customer id of the lock to retrieve,
   *        which is at the customer level, so one per customer.
   * @param operationCategory The operation category
   * @param operationType The operation type
   * @param submittedBy The lock owner email
   * 
   * @return 1 if successful, 0 if not
   */
  int updateLock(
      Integer customerId,
      String operationCategory,
      String operationType,
      String submittedBy);
  
  /**
   * 
   * @param customerId The lock to delete (customer level)
   * @param submittedBy The user email
   * 
   * @return true if the lock exists, and is owned by the user
   *         false if the lock does not exist or is not owned by the user
   */
  boolean deleteLock(Integer customerId, String submittedBy);
}
//@formatter:on