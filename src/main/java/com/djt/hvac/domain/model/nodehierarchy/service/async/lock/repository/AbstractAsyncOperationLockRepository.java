//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service.async.lock.repository;

import java.sql.Timestamp;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.nodehierarchy.service.async.lock.AsyncOperationLockEntity;
import com.djt.hvac.domain.model.nodehierarchy.service.async.lock.exception.AsyncOperationLockAcquisitionException;
import com.djt.hvac.domain.model.nodehierarchy.service.async.lock.valueobject.AsyncOperationLockInfo;

public abstract class AbstractAsyncOperationLockRepository implements AsyncOperationLockRepository {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAsyncOperationLockRepository.class);
  
  @Override
  public  Optional<AsyncOperationLockEntity> retrieveLockBySubmittedBy(
      Integer customerId,
      String submittedBy) {
    
    Optional<AsyncOperationLockEntity> optional = retrieveLock(customerId);
    if (optional.isPresent()) {
      if (optional.get().getSubmittedBy().equals(submittedBy)) {
        return optional;
      }
    }
    return Optional.empty();
  }
  
  @Override
  public AsyncOperationLockEntity acquireLock(
      Integer customerId,
      String ownerUuid,
      String operationCategory,
      String operationType,
      String submittedBy) 
  throws 
      AsyncOperationLockAcquisitionException {
    
    Timestamp currentTime = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
    LOGGER.info("acquireLock(): currentTime: {}, customerId: {}, category: {}, type: {}, user: {}",
        currentTime,
        customerId,
        operationCategory,
        operationType,
        submittedBy);
    
    // There are 4 scenarios:
    // 1: Row does not exist in lock table: 
    //    ACTION: Insert lock
    //
    // 2: Row exists in lock table and is "owned" by passed in user: 
    //    ACTION: Update lock for category and type
    //
    // 3: Row exists in lock table and is "owned" by another user, but is "expired": 
    //    ACTION: Update lock for uuid, user, category and type
    //
    // 4: Row exists in lock table and is "owned" by another user and the lock is not "expired": 
    //    ACTION: Throw lock acquisition exception
    
    String errorMessage = "Unable to acquire async operation lock.";
    
    try {

      Optional<AsyncOperationLockEntity> lockOptional = retrieveLock(
          customerId, 
          operationCategory, 
          operationType, 
          "*", 
          "*");
      
      if (!lockOptional.isPresent()) {
        
        // Scenario 1
        final AsyncOperationLockEntity lock = new AsyncOperationLockEntity(
            customerId,
            ownerUuid,
            operationCategory,
            operationType,
            submittedBy,
            AbstractEntity.getTimeKeeper().getCurrentTimestamp(),
            AbstractEntity.getTimeKeeper().getCurrentTimestamp());
        
        int rowsUpdated = createLock(lock); 
        
        // If successful, return the lock.
        if (rowsUpdated == 1) {
          return lock;
        }
      } else {
        
        final AsyncOperationLockEntity lock = lockOptional.get();
        
        // Scenario 2
        if (lock.getSubmittedBy().equals(submittedBy)) {

          lock.setOperationCategory(operationCategory);
          lock.setOperationType(operationType);
          lock.setLockUpdateTime(AbstractEntity.getTimeKeeper().getCurrentTimestamp());
          
          int rowsUpdated = updateLock(
              customerId, 
              operationCategory, 
              operationType, 
              submittedBy);
          
          // If successful, return the lock.
          if (rowsUpdated == 1) {
            return lock;
          }
        } else {

          if (lock.isLockTimedOut()) {
            
            // Scenario 3
            // The lock has expired, update it for the new owner.
            lock.setOwnerUuid(ownerUuid);
            lock.setSubmittedBy(submittedBy);
            lock.setOperationCategory(operationCategory);
            lock.setOperationType(operationType);
            lock.setLockAcquisitionTime(AbstractEntity.getTimeKeeper().getCurrentTimestamp());
            lock.setLockUpdateTime(AbstractEntity.getTimeKeeper().getCurrentTimestamp());
            
            int rowsUpdated = updateLock(
                customerId, 
                operationCategory, 
                operationType, 
                submittedBy);
            
            // If successful, return the lock.
            if (rowsUpdated == 1) {
              return lock;
            }
          } else {
            
            // Scenario 4
            // At this point, we were unable to acquire the lock, so throw an exception.
            // RP-6576: Return the lock info in the case of a lock acquisition exception.
            AsyncOperationLockInfo lockInfo =
                AsyncOperationLockInfo.builder()
                    .withLockedEntityId(customerId)
                    .withOperationCategory(lock.getOperationCategory())
                    .withOperationType(lock.getOperationType())
                    .withSubmittedBy(lock.getSubmittedBy())
                    .withLockAgeInSeconds(lock.getLockAgeInSeconds())
                    .withLockIdleTimeInSeconds(lock.getLockIdleTimeInSeconds())
                    .withLoggedInUserHasLock(Boolean.FALSE)
                    .build();            

            String json = null;
            try {
              json = AbstractEntity.OBJECT_MAPPER.get().writeValueAsString(lockInfo);  
            } catch (Exception e) {
              json = "{\"result\":\"" + e.getMessage() + "\"}";
            }
            throw new AsyncOperationLockAcquisitionException(json);    
          }
        }
      }
    } catch (Exception e) {
      errorMessage = "Unable to acquire lock, error: " + e.getMessage();
      LOGGER.error(errorMessage, e);
    }
    throw new AsyncOperationLockAcquisitionException("{\"result\":\"" + errorMessage + "\"}");
  }  
}
//@formatter:on