//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service.async;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.nodehierarchy.service.async.lock.AsyncOperationLockEntity;
import com.djt.hvac.domain.model.nodehierarchy.service.async.lock.exception.AsyncOperationLockAcquisitionException;
import com.djt.hvac.domain.model.nodehierarchy.service.async.lock.repository.AsyncOperationLockRepository;
import com.djt.hvac.domain.model.nodehierarchy.service.async.lock.valueobject.AsyncOperationLockInfo;
import com.djt.hvac.domain.model.nodehierarchy.service.async.operation.AsyncOperationEntity;
import com.djt.hvac.domain.model.nodehierarchy.service.async.operation.repository.AsyncOperationRepository;
import com.djt.hvac.domain.model.nodehierarchy.service.async.operation.valueobject.AsyncOperationInfo;
import com.djt.hvac.domain.model.nodehierarchy.service.async.operation.valueobject.GetAsyncOperationInfoSummary;
import com.djt.hvac.domain.model.nodehierarchy.service.command.NodeHierarchyCommandRequest;

/**
 * 
 * @author tommyers
 */
public class NodeHierarchyAsyncCommandServiceImpl implements NodeHierarchyAsyncCommandService {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(NodeHierarchyAsyncCommandServiceImpl.class);
  
  private final AsyncOperationRepository asyncOperationRepository;
  private final AsyncOperationLockRepository asyncOperationLockRepository;
  
  public NodeHierarchyAsyncCommandServiceImpl(
      AsyncOperationRepository asyncOperationRepository,
      AsyncOperationLockRepository asyncOperationLockRepository) {
    requireNonNull(asyncOperationRepository, "asyncOperationRepository");
    requireNonNull(asyncOperationLockRepository, "asyncOperationLockRepository cannot be null");
    this.asyncOperationRepository = asyncOperationRepository;
    this.asyncOperationLockRepository = asyncOperationLockRepository;
  }
  
  @Override
  public Optional<AsyncOperationLockInfo> getLockInfo(Integer customerId, String submittedBy) {
    
    Optional<AsyncOperationLockEntity> lockOptional = asyncOperationLockRepository.retrieveLock(customerId);
    if (lockOptional.isPresent()) {
      
      AsyncOperationLockEntity lock = lockOptional.get();
      
      if (lock.isLockTimedOut()) {
       
        return Optional.empty();
      }
      
      Boolean loggedInUserHasLock = Boolean.FALSE;
      if (submittedBy != null && lock.getSubmittedBy().equals(submittedBy)) {
        loggedInUserHasLock = Boolean.TRUE; 
      }
      
      return Optional.of(AsyncOperationLockInfo
          .builder()
          .withLockedEntityId(lock.getCustomerId())
          .withOperationCategory(lock.getOperationCategory())
          .withOperationType(lock.getOperationType())
          .withSubmittedBy(lock.getSubmittedBy())
          .withLockAgeInSeconds(lock.getLockAgeInSeconds())
          .withLockIdleTimeInSeconds(lock.getLockIdleTimeInSeconds())
          .withLoggedInUserHasLock(loggedInUserHasLock)
          .build());
    }
    
    return Optional.empty();
  }

  @Override
  public AsyncOperationLockInfo acquireLock(
      Integer customerId, 
      String submittedBy) throws AsyncOperationLockAcquisitionException {

    return acquireLock(
        customerId, 
        submittedBy, 
        NodeHierarchyCommandRequest.ALL, 
        NodeHierarchyCommandRequest.ALL);
  }
  
  @Override
  public AsyncOperationLockInfo acquireLock(
      Integer customerId, 
      String submittedBy, 
      String operationCategory, 
      String operationType) throws AsyncOperationLockAcquisitionException {
    
    if (operationCategory == null || operationCategory.isEmpty()) {
      operationCategory = NodeHierarchyCommandRequest.ALL;
    }

    if (operationType == null || operationType.isEmpty()) {
      operationType = NodeHierarchyCommandRequest.ALL;
    }
    
    AsyncOperationLockEntity lock = asyncOperationLockRepository.acquireLock(
        customerId,
        UUID.randomUUID().toString(),
        operationCategory,
        operationType,
        submittedBy);      
    
    return AsyncOperationLockInfo
        .builder()
        .withLockedEntityId(lock.getCustomerId())
        .withOperationCategory(lock.getOperationCategory())
        .withOperationType(lock.getOperationType())
        .withSubmittedBy(lock.getSubmittedBy())
        .withLockAgeInSeconds(lock.getLockAgeInSeconds())
        .withLockIdleTimeInSeconds(lock.getLockIdleTimeInSeconds())
        .withLoggedInUserHasLock(Boolean.TRUE)
        .build();      
  }

  @Override
  public boolean deleteLock(
      Integer customerId, 
      String submittedBy) {
    
    return asyncOperationLockRepository.deleteLock(
        customerId, 
        submittedBy);
  }
  
  @Override
  public AsyncOperationEntity createAsyncOperation(NodeHierarchyCommandRequest commandRequest) {
    return asyncOperationRepository.createAsyncOperation(commandRequest);
  }
  
  @Override
  public void updateAsyncOperationForDispatchedStatus(Integer asyncOperationId) {
    this.asyncOperationRepository.updateAsyncOperationForDispatchedStatus(asyncOperationId);
  }
  
  @Override
  public void updateAsyncOperationForCompletedStatus(Integer asyncOperationId) {
    
    LOGGER.info("About to update async operation: [{}] for COMPLETED status", asyncOperationId);
    
    Optional<AsyncOperationEntity> asyncOperationOptional = asyncOperationRepository.retrieveAsyncOperation(asyncOperationId);
    if (asyncOperationOptional.isPresent()) {
      
      AsyncOperationEntity asyncOperation = asyncOperationOptional.get();
      
      asyncOperationRepository.updateAsyncOperationForCompletedStatus(asyncOperationId);
      
      Integer customerId = asyncOperation.getCustomerId();
      String operationCategory = asyncOperation.getOperationCategory();
      String operationType = asyncOperation.getOperationType();
      String submittedBy = asyncOperation.getSubmittedBy();
      
      Optional<AsyncOperationLockEntity> lockOptional = asyncOperationLockRepository.retrieveLock(customerId);
      if (lockOptional.isPresent()) {
        
        AsyncOperationLockEntity lock = lockOptional.get();
        
        if (operationCategory.equals(NodeHierarchyCommandRequest.TIME_TO_VALUE_OPERATION_CATEGORY)
            && operationType.equals(NodeHierarchyCommandRequest.FAST_TRACK_CONNECTOR_MODEL_DATA)) {
          
          operationType = NodeHierarchyCommandRequest.FAST_TRACK_CONNECTOR_TIME_SERIES_DATA;
        }
        lock.setOperationCategory(operationCategory);
        lock.setOperationType(operationType);
        lock.setLockUpdateTime(AbstractEntity.getTimeKeeper().getCurrentTimestamp());
        asyncOperationLockRepository.updateLock(
            customerId, 
            operationCategory, 
            operationType, 
            submittedBy);
        
        asyncOperationRepository.releaseLockIfAsyncOperationsComplete(
            asyncOperation.getCustomerId(), 
            asyncOperation.getSubmittedBy());
      }
    } else {
      LOGGER.info("Cannot update async operation: [{}] for COMPLETED status because it no longer exists", asyncOperationId);
    }
  }
  
  @Override
  public void updateAsyncOperationForFailedStatus(
      Integer asyncOperationId,
      String reason) {
   
    LOGGER.info("About to update async operation: [{}] for FAILED status with reason: [{}]", asyncOperationId, reason);
    Optional<AsyncOperationEntity> asyncOperationOptional = asyncOperationRepository.retrieveAsyncOperation(asyncOperationId);
    if (asyncOperationOptional.isPresent()) {
      
      AsyncOperationEntity asyncOperation = asyncOperationOptional.get();
      
      asyncOperationRepository.updateAsyncOperationForFailedStatus(
          asyncOperationId,
          reason);

      Integer customerId = asyncOperation.getCustomerId();
      String operationCategory = asyncOperation.getOperationCategory();
      String operationType = asyncOperation.getOperationType();
      String submittedBy = asyncOperation.getSubmittedBy();
      
      Optional<AsyncOperationLockEntity> lockOptional = asyncOperationLockRepository.retrieveLock(customerId);
      if (lockOptional.isPresent()) {
        
        AsyncOperationLockEntity lock = lockOptional.get();
        
        if (operationCategory.equals(NodeHierarchyCommandRequest.TIME_TO_VALUE_OPERATION_CATEGORY)
            && operationType.equals(NodeHierarchyCommandRequest.FAST_TRACK_CONNECTOR_MODEL_DATA)) {
          
          operationType = NodeHierarchyCommandRequest.FAST_TRACK_CONNECTOR_TIME_SERIES_DATA;
        }
        lock.setOperationCategory(operationCategory);
        lock.setOperationType(operationType);
        lock.setLockUpdateTime(AbstractEntity.getTimeKeeper().getCurrentTimestamp());
        asyncOperationLockRepository.updateLock(
            customerId, 
            operationCategory, 
            operationType, 
            submittedBy);
        
        asyncOperationRepository.releaseLockIfAsyncOperationsComplete(
            asyncOperation.getCustomerId(), 
            asyncOperation.getSubmittedBy());
      }
    } else {
      LOGGER.info("Cannot update async operation: [{}] for FAILED status with reason: [{}] because it no longer exists", asyncOperationId, reason);  
    }
  }
  
  @Override
  public Integer submitAsyncOperation(NodeHierarchyCommandRequest commandRequest) {
    
    Integer customerId = commandRequest.getCustomerId();
    if (customerId == null) {
      throw new IllegalArgumentException("customerId must be specified in request");
    }
    
    String operationCategory = commandRequest.getOperationCategory();
    if (operationCategory == null) {
      throw new IllegalArgumentException("operationCategory must be specified in request");
    }
    
    String operationType = commandRequest.getOperationType();
    if (operationType == null) {
      throw new IllegalArgumentException("operationType must be specified in request");
    }
    
    String submittedBy = commandRequest.getSubmittedBy();
    if (submittedBy == null) {
      throw new IllegalArgumentException("submittedBy must be specified in request");
    }
    
    // Hand off the request to the async operation client, which will take care
    // of the "producer" aspect:
    // 1. Creating the "job"
    // 2. Queuing the job/command
    // 
    // The "consumer", which can be a separately deployed component, or for the mock implementation,
    // an asynchronous thread running in an infinite "worker loop", thread, aspect will handle:
    // 3. Actually processing the request
    // 4. Updating the job for the result/response of the processing of the command
    // 
    // The UI can query the progress of this "job" by using this same service.
    return asyncOperationRepository.submitAsyncOperation(commandRequest);
  }
  
  @Override
  public void processAsyncOperation(Integer asyncOperationId) throws EntityDoesNotExistException, AsyncOperationLockAcquisitionException {
    
    Optional<AsyncOperationEntity> optional = asyncOperationRepository.retrieveAsyncOperation(asyncOperationId, true);
    if (optional.isPresent()) {
      asyncOperationRepository.processAsyncOperation(optional.get());
    } else {
      throw new EntityDoesNotExistException("Async operation with id: ["
          + asyncOperationId 
          + "] does not exist.");
    }
  }

  @Override
  public Optional<AsyncOperationInfo> getAsyncOperationInfo(Integer asyncOperationId) {

    return getAsyncOperationInfo(asyncOperationId, "Eastern Time (US & Canada)");
  }
  
  @Override
  public Optional<AsyncOperationInfo> getAsyncOperationInfo(
      Integer asyncOperationId,
      String timezone) {
    
    Optional<AsyncOperationEntity> optional = asyncOperationRepository.retrieveAsyncOperation(asyncOperationId);
    if (optional.isPresent()) {
      return Optional.of(AsyncOperationEntity.createAsyncOperationStatus(optional.get(), timezone));
    }
    return Optional.empty();
  }
 
  @Override
  public GetAsyncOperationInfoSummary getAsyncOperationInfoSummary(
      Integer customerId, 
      String operationCategory, 
      String operationType,
      String timezone) {
    
    List<AsyncOperationEntity> list = asyncOperationRepository.retrieveAsyncOperations(
        customerId, 
        operationCategory, 
        operationType);

    int numberOpenJobs = 0;
    int numberCompleteJobs = 0;
    int totalNumberJobs = 0;
    List<AsyncOperationInfo> statuses = new ArrayList<>();
    for (AsyncOperationEntity asyncOperation: list) {
      
      statuses.add(AsyncOperationEntity.createAsyncOperationStatus(asyncOperation, timezone));
      totalNumberJobs++;
      if (asyncOperation.isComplete()) {
        numberCompleteJobs++;
      } else {
        numberOpenJobs++;
      }
    }
    return GetAsyncOperationInfoSummary
        .builder()
        .withNumberOpenJobs(Integer.valueOf(numberOpenJobs))
        .withNumberCompleteJobs(Integer.valueOf(numberCompleteJobs))
        .withTotalNumberJobs(Integer.valueOf(totalNumberJobs))
        .withStatuses(statuses)
        .build();
  }
  
  @Override
  public Integer deleteAsyncOperations(
      Integer customerId, 
      List<Integer> asyncOperationIds) {
    
    return asyncOperationRepository.deleteAsyncOperations(
        customerId, 
        asyncOperationIds);
  }
}
//@formatter:on
