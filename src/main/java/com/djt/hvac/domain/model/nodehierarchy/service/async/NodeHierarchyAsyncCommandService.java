//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service.async;

import java.util.List;
import java.util.Optional;

import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.nodehierarchy.service.async.lock.exception.AsyncOperationLockAcquisitionException;
import com.djt.hvac.domain.model.nodehierarchy.service.async.lock.valueobject.AsyncOperationLockInfo;
import com.djt.hvac.domain.model.nodehierarchy.service.async.operation.AsyncOperationEntity;
import com.djt.hvac.domain.model.nodehierarchy.service.async.operation.valueobject.AsyncOperationInfo;
import com.djt.hvac.domain.model.nodehierarchy.service.async.operation.valueobject.GetAsyncOperationInfoSummary;
import com.djt.hvac.domain.model.nodehierarchy.service.command.NodeHierarchyCommandRequest;

/**
 * 
 * @author tmyers
 * 
 * Order of calls needed for async bulk operation:
 *
 <pre>
    COMMAND PRODUCER (LIBRARY CALL IN APPLICATION LAYER)
    =====================================================
    1: See if a lock already exists for the given customer and user.
    2: Acquire the lock for the given customer and user.
    3: Create the async bulk operation job record (with request JSON)
        
        
    COMMAND CONSUMER (SEPARATELY DEPLOYED, GETS OFF A WORK "QUEUE")
    ===============================================================    
    4: The specific service method corresponding to the unmarshalled
       request JSON is executed.
    5: For each "item" in the request, a job status item is updated 
       with the results of the operation.
    6. When the operation is done, the lock is released (NOTE: 5/6 
       may be the same method, as most operations will be in one
       transaction for performance/consistency/integrity reasons.
    
      
    APPLICATION LAYER:
    ==================
    7: Get async bulk operation job statuses.
    8: Delete async bulk operation job records.
 </pre>
 * 
 * @author tommyers
 */
public interface NodeHierarchyAsyncCommandService {
  
  // ASYNC OPERATION LOCK RELATED
  /**
   * 
   * @param customerId The owning customer id
   * @param submittedBy Assumed to be the logged in user, used to determine if the same as in the lock
   * 
   * @return The lock status, if it exists
   */
  Optional<AsyncOperationLockInfo> getLockInfo(Integer customerId, String submittedBy);

  /**
   * 
   * @param customerId The owning customer id
   * @param submittedBy Assumed to be the logged in user, used to determine if the same as in the lock
   * 
   * @return The lock info.  If successful, then the lock's 'submittedBy' will own the lock and 
   *         'loggedInUser' will be true.  If unsuccessful, the lock acquisition exception will 
   *         be eaten, and instead, a lock info will be returned, giving the info about the current lock.
   *         NOTE: operation category/type will both be "ALL", as it is not specified in the parameters.
   *         
   * @throws AsyncOperationLockAcquisitionException If the customer lock could not be acquired (i.e. someone else has it and it hasn't expired)
   */
  AsyncOperationLockInfo acquireLock(Integer customerId, String submittedBy) throws AsyncOperationLockAcquisitionException;
  
  /**
   * 
   * @param customerId The owning customer id
   * @param submittedBy Assumed to be the logged in user, used to determine if the same as in the lock
   * 
   * @return The lock info.  If successful, then the lock's 'submittedBy' will own the lock and 
   *         'loggedInUser' will be true.  If unsuccessful, the lock acquisition exception will 
   *         be eaten, and instead, a lock info will be returned, giving the info about the current lock.
   *         
   * @throws AsyncOperationLockAcquisitionException If the customer lock could not be acquired (i.e. someone else has it and it hasn't expired)         
   */
  AsyncOperationLockInfo acquireLock(Integer customerId, String submittedBy, String operationCategory, String operationType) throws AsyncOperationLockAcquisitionException;
  
  /**
   * 
   * @param customerId The owning customer id
   * @param submittedBy Assumed to be the logged in user, used to determine if the same as in the lock
   * 
   * @return true if the lock exists, and is owned by the user
   *         false if the lock does not exist or is not owned by the user
   */
  boolean deleteLock(Integer customerId, String submittedBy);

  
  // ASYNC OPERATION RELATED
  /**
   * 
   * PRODUCER: This method does two things:
   * <ol>
   *   <li>Creates the async operation in the repository</li>
   *   <li>Puts the command in the appropriate customer FIFO queue</li>
   * </ol>
   * 
   * @param commandRequest The node hierarchy command request to process asynchronously
   * 
   * @return The async operation id
   */
  Integer submitAsyncOperation(NodeHierarchyCommandRequest commandRequest);
  
  /**
   * CONSUMER: Gets command requests to process from the various customer FIFO queues and processes them
   *           sequentially by customer (but can be in parallel across different customers)
   * 
   * @param asyncOperationId The id of the async operation to process (wraps a node hierarchy command request)
   * 
   * @throws EntityDoesNotExistException If the async operation does not exist
   * @throws AsyncOperationLockAcquisitionException If the customer lock could not be acquired (i.e. someone else has it and it hasn't expired)
   */
  void processAsyncOperation(Integer asyncOperationId) throws EntityDoesNotExistException, AsyncOperationLockAcquisitionException;

  /**
   * The "Eastern Time (US & Canada)" is used as a default.
   * 
   * @param asyncOperationId The async operation id
   * 
   * @return The async operation status, if it exists
   */
  Optional<AsyncOperationInfo> getAsyncOperationInfo(Integer asyncOperationId);
  
  /**
   * 
   * @param asyncOperationId The async operation id
   * @param timezone The timezone (ruby label)
   * 
   * @return The async operation status, if it exists
   */
  Optional<AsyncOperationInfo> getAsyncOperationInfo(
      Integer asyncOperationId,
      String timezone);
  
  /**
   * 
   * @param customerId The owning customer id
   * @param operationCategory The operation category to filter for (optional).
   *        If null, then means the same as wildcard (i.e. any)
   * @param operationType The operation type to filter for (optional).
   *        If null, then means the same as wildcard (i.e. any)
   * @param timezone The timezone (ruby label)
   * 
   * @return The response wrapper
   */
  GetAsyncOperationInfoSummary getAsyncOperationInfoSummary(
      Integer customerId, 
      String operationCategory, 
      String operationType,
      String timezone);
  
  /**
   * 
   * @param customerId The owning customer id
   * @param asyncOperationIds The async operation ids to delete
   * 
   * @return The number of deleted async operations
   */
  Integer deleteAsyncOperations(
      Integer customerId, 
      List<Integer> asyncOperationIds);
  
  
  // TIME TO VALUE RELATED
  /**
   * 
   * @param commandRequest The command request to create
   * 
   * @return The created async operation, which wraps the command request, 
   *         enriching with start time, response, etc
   */
  AsyncOperationEntity createAsyncOperation(NodeHierarchyCommandRequest commandRequest);
  
  /**
   * 
   * @param asyncOperationId The id of the async operation to mark as dispatched
   */
  void updateAsyncOperationForDispatchedStatus(Integer asyncOperationId);
  
  /**
   * 
   * @param asyncOperationId The id of the async operation to complete (success) 
   */
  void updateAsyncOperationForCompletedStatus(Integer asyncOperationId);
  
  /**
   * 
   * @param asyncOperationId The id of the async operation to fail
   * @param reason Will be marshalled to JSON (e.g. lock acquisition failure)
   */
  void updateAsyncOperationForFailedStatus(
      Integer asyncOperationId,
      String reason);
}
//@formatter:on