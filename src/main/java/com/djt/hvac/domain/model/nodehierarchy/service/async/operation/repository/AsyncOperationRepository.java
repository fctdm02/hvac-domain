//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service.async.operation.repository;

import java.util.List;
import java.util.Optional;

import com.djt.hvac.domain.model.nodehierarchy.service.async.lock.exception.AsyncOperationLockAcquisitionException;
import com.djt.hvac.domain.model.nodehierarchy.service.async.operation.AsyncOperationEntity;
import com.djt.hvac.domain.model.nodehierarchy.service.command.NodeHierarchyCommandRequest;

/**
 * 
 * @author tmyers
 *
 */
public interface AsyncOperationRepository {
  
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
   * @param asyncOperation The async operation to process (wraps a node hierarchy command request)
   * 
   * @return <code>true</code> if the customer lock was released, <code>false</code> otherwise
   * 
   * @throws AsyncOperationLockAcquisitionException If the lock could not be acquired
   */
  boolean processAsyncOperation(AsyncOperationEntity asyncOperation) throws AsyncOperationLockAcquisitionException;
  
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
   * @param asyncOperationId The id of the async operation to retrieve
   * 
   * @return The given async operation
   */
  Optional<AsyncOperationEntity> retrieveAsyncOperation(Integer asyncOperationId);

  /**
   * 
   * @param asyncOperationId The id of the async operation to retrieve
   * @param loadRequestJson If true, loads the request JSON
   * 
   * @return The given async operation
   */
  Optional<AsyncOperationEntity> retrieveAsyncOperation(Integer asyncOperationId, boolean loadRequestJson);
  
  /**
   * 
   * NOTE: The operation request/response JSON is NOT retrieved, as this method is meant
   *       to return status info to the UI.
   * 
   * @param customerId The owning customer id
   * @param operationCategory The operation category to filter for (optional).
   *        If null, then means the same as wildcard (i.e. any)
   * @param operationType The operation type to filter for (optional).
   *        If null, then means the same as wildcard (i.e. any)
   * 
   * @return The list of async operations that fit the given criteria
   */
  List<AsyncOperationEntity> retrieveAsyncOperations(
      Integer customerId,
      String operationCategory,
      String operationType);
  
  /**
   * 
   * @param asyncOperationId The id of the async operation to mark as dispatched
   */
  void updateAsyncOperationForDispatchedStatus(Integer asyncOperationId);
  
  /**
   * 
   * TIME TO VALUE
   * 
   * SUCCESS SCENARIO
   * 
   * @param asyncOperationId The id of the async operation to update
   */
  void updateAsyncOperationForCompletedStatus(Integer asyncOperationId);
  
  /**
   * 
   * TIME TO VALUE
   * 
   * FAILURE SCENARIO
   * 
   * @param asyncOperationId The async operation to fail (e.g. lock acquisition failure)
   * @param reason Will be marshalled to JSON
   */
  void updateAsyncOperationForFailedStatus(
      Integer asyncOperationId,
      String reason);
  
  /**
   * 
   * TIME TO VALUE
   * 
   * Checks to see if all async operations are complete.  If so, then the customer
   * lock is released.
   * 
   * NOTE: For fast track operations, if the operations is greater then 2 minutes old,
   * then it is considered to be complete as well. 
   * 
   * @param customerId The customer id
   * @param submittedBy The user that submitted the operation
   * 
   * @return Whether or not the customer lock was released
   */
  boolean releaseLockIfAsyncOperationsComplete(int customerId, String submittedBy);
  
  /**
   * 
   * @param customerId The owning customer id
   * @param asyncOperationIds The async operations to delete
   * 
   * @return The number of deleted async operations
   */
  Integer deleteAsyncOperations(
      Integer customerId, 
      List<Integer> asyncOperationIds);
}
//@formatter:on
