//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service.async.operation.repository;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.common.exception.StaleDataException;
import com.djt.hvac.domain.model.nodehierarchy.service.NodeHierarchyService;
import com.djt.hvac.domain.model.nodehierarchy.service.async.lock.AsyncOperationLockEntity;
import com.djt.hvac.domain.model.nodehierarchy.service.async.lock.exception.AsyncOperationLockAcquisitionException;
import com.djt.hvac.domain.model.nodehierarchy.service.async.lock.repository.AsyncOperationLockRepository;
import com.djt.hvac.domain.model.nodehierarchy.service.async.operation.AsyncOperationEntity;
import com.djt.hvac.domain.model.nodehierarchy.service.async.operation.enums.AsyncOperationStatus;
import com.djt.hvac.domain.model.nodehierarchy.service.command.AddPointTemplateOverrideRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.CreateAdFunctionInstancesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.CreateNodeRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.DeleteAdFunctionInstancesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.DeleteChildNodesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.EvaluateReportsRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.FindAdFunctionInstanceCandidatesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.IgnoreRawPointsRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.MapRawPointsRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.MoveChildNodesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.NodeHierarchyCommandRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.RemediatePortfolioRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.RemovePointTemplateOverrideRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UnignoreRawPointsRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UnmapRawPointsRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UpdateAdFunctionInstancesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UpdateBuildingNodesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UpdateCustomAsyncComputedPointNodesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UpdateEnergyExchangeSystemNodesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UpdateMappablePointNodesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.UpdateReportInstancesRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.ValidatePortfolioRequest;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.CustomAsyncComputedPointNodeData;
import com.djt.hvac.domain.model.nodehierarchy.utils.ExceptionUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

public abstract class AbstractAsyncOperationRepository implements AsyncOperationRepository {
  
  protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractAsyncOperationRepository.class);
  
  protected final AsyncOperationLockRepository asyncOperationLockRepository;
  protected final NodeHierarchyService nodeHierarchyService;
  
  public AbstractAsyncOperationRepository(
      AsyncOperationLockRepository asyncOperationLockRepository,
      NodeHierarchyService nodeHierarchyService) {
    requireNonNull(asyncOperationLockRepository, "asyncOperationLockRepository");
    requireNonNull(nodeHierarchyService, "nodeHierarchyService cannot be null");
    this.asyncOperationLockRepository = asyncOperationLockRepository;
    this.nodeHierarchyService = nodeHierarchyService;
  }
  
  @Override
  public Integer submitAsyncOperation(NodeHierarchyCommandRequest commandRequest) {
    
    AsyncOperationEntity asyncOperation = createAsyncOperation(commandRequest);
    
    return asyncOperation.getPersistentIdentity();
  }
  
  /*
   * CONSUMER: Gets command requests to process from the various customer FIFO queues and processes them
   *           sequentially by customer (but can be in parallel across different customers)
   * 
   * @param asyncOperation The async operation to process (wraps a node hierarchy command request)
   * 
   * @throws AsyncOperationLockAcquisitionException If the lock could not be acquired
   */
  @Override
  public boolean processAsyncOperation(AsyncOperationEntity asyncOperation) throws AsyncOperationLockAcquisitionException {
    
    Integer customerId = asyncOperation.getCustomerId();
    String operationCategory = asyncOperation.getOperationCategory();
    String operationType = asyncOperation.getOperationType();
    String submittedBy = asyncOperation.getSubmittedBy();
    
    // Retrieve/acquire the pessimistic lock for the given customer.
    Optional<AsyncOperationLockEntity> lockOptional = asyncOperationLockRepository.retrieveLockBySubmittedBy(
        customerId, 
        submittedBy);
    
    AsyncOperationLockEntity lock = null;
    if (lockOptional.isPresent()) {

      // The user may already have it, as we do a lookup by submitted by here.
      lock = lockOptional.get();
      
      // If so, then update the lock.
      lock.setOperationCategory(operationCategory);
      lock.setOperationType(operationType);
      lock.setLockUpdateTime(AbstractEntity.getTimeKeeper().getCurrentTimestamp());
      asyncOperationLockRepository.updateLock(
          customerId, 
          operationCategory, 
          operationType, 
          submittedBy);
      
    } else {
      
      // If the user does not already have it, then attempt to acquire it.
      lock = asyncOperationLockRepository.acquireLock(
          customerId, 
          UUID.randomUUID().toString(), 
          operationCategory, 
          operationType, 
          submittedBy);
    }

    
    // For time to value, the actual work is done elsewhere, so there's no need to do any
    // processing here, other than to acquire the initial lock on behalf of the user.
    boolean releasedLock = false;
    if (operationCategory.equals(NodeHierarchyCommandRequest.TIME_TO_VALUE_OPERATION_CATEGORY)) {
      
      LOGGER.info("No op for time to value operation: {}", asyncOperation);
      return releasedLock;
    }
    
    
    try {

      if (operationCategory.equals(NodeHierarchyCommandRequest.BULK_POINT_MAPPING_OPERATION_CATEGORY)) {
        
        if (operationType.equals(NodeHierarchyCommandRequest.MAP_RAW_POINT_OPERATION_TYPE)) {
          
          mapRawPoints(asyncOperation);
          
        } else if (operationType.equals(NodeHierarchyCommandRequest.UNMAP_RAW_POINT_OPERATION_TYPE)) {
          
          unmapRawPoints(asyncOperation);
          
        } else if (operationType.equals(NodeHierarchyCommandRequest.IGNORE_RAW_POINT_OPERATION_TYPE)) {
          
          ignoreRawPoints(asyncOperation);
          
        } else if (operationType.equals(NodeHierarchyCommandRequest.UNIGNORE_RAW_POINT_OPERATION_TYPE)) {
          
          unignoreRawPoints(asyncOperation);
          
        } else {
          
          throw new IllegalStateException("Unsupported operation type: "
              + operationType
              + " for operation category: "
              + operationCategory);
        }

      } else if (operationCategory.equals(NodeHierarchyCommandRequest.BULK_BUILDINGS_OPERATION_CATEGORY)) {
        
        if (operationType.equals(NodeHierarchyCommandRequest.UPDATE_OPERATION_TYPE)) {
          
          updateBuildings(asyncOperation);
          
        } else {

          throw new IllegalStateException("Unsupported operation type: "
              + operationType
              + " for operation category: "
              + operationCategory);
        }
        
      } else if (operationCategory.equals(NodeHierarchyCommandRequest.BULK_ENERGY_EXCHANGE_OPERATION_CATEGORY)) {
        
        if (operationType.equals(NodeHierarchyCommandRequest.UPDATE_OPERATION_TYPE)) {
          
          updateEnergyExchangeSystemNodes(asyncOperation);
          
        } else {

          throw new IllegalStateException("Unsupported operation type: "
              + operationType
              + " for operation category: "
              + operationCategory);
        }
        
      } else if (operationCategory.equals(NodeHierarchyCommandRequest.BULK_POINT_OPERATION_CATEGORY)) {

        if (operationType.equals(NodeHierarchyCommandRequest.UPDATE_OPERATION_TYPE)) {
          
          updateMappablePoints(asyncOperation);
          
        } else {

          throw new IllegalStateException("Unsupported operation type: "
              + operationType
              + " for operation category: "
              + operationCategory);
        }
        
      } else if (operationCategory.equals(NodeHierarchyCommandRequest.BULK_AD_FUNCTION_INSTANCE_OPERATION_CATEGORY)) {
        
        if (operationType.equals(NodeHierarchyCommandRequest.CREATE_AD_FUNCTION_INSTANCE_RULES_OPERATION_TYPE)
            || operationType.equals(NodeHierarchyCommandRequest.CREATE_AD_FUNCTION_INSTANCE_COMPUTED_POINTS_OPERATION_TYPE)) {
          
          createAdFunctionInstances(asyncOperation);
          
        } else if (operationType.equals(NodeHierarchyCommandRequest.UPDATE_AD_FUNCTION_INSTANCE_RULES_OPERATION_TYPE)
            || operationType.equals(NodeHierarchyCommandRequest.UPDATE_AD_FUNCTION_INSTANCE_COMPUTED_POINTS_OPERATION_TYPE)) {
          
          updateAdFunctionInstances(asyncOperation);
          
        } else if (operationType.equals(NodeHierarchyCommandRequest.DELETE_AD_FUNCTION_INSTANCE_RULES_OPERATION_TYPE)
            || operationType.equals(NodeHierarchyCommandRequest.DELETE_AD_FUNCTION_INSTANCE_COMPUTED_POINTS_OPERATION_TYPE)) {
          
          deleteAdFunctionInstances(asyncOperation);
          
        } else if (operationType.equals(NodeHierarchyCommandRequest.FIND_AD_FUNCTION_CANDIDATE_RULES_OPERATION_TYPE)
            || operationType.equals(NodeHierarchyCommandRequest.FIND_AD_FUNCTION_CANDIDATE_COMPUTED_POINTS_OPERATION_TYPE)) {
          
          findAdFunctionInstanceCandidatesForPortfolio(asyncOperation);
          
        } else {
          
          throw new IllegalStateException("Unsupported operation type: "
              + operationType
              + " for operation category: "
              + operationCategory);
        }
        
      } else if (operationCategory.equals(NodeHierarchyCommandRequest.BULK_REPORT_INSTANCE_OPERATION_CATEGORY)) {
        
        if (operationType.equals(NodeHierarchyCommandRequest.UPDATE_OPERATION_TYPE)) {
          
          updateReportInstances(asyncOperation);
          
        } else {
          
          throw new IllegalStateException("Unsupported operation type: "
              + operationType
              + " for operation category: "
              + operationCategory);
        }
        
      } else if (operationCategory.equals(NodeHierarchyCommandRequest.BULK_NODE_OPERATION_CATEGORY)) {

        if (operationType.equals(NodeHierarchyCommandRequest.MOVE_OPERATION_TYPE)) {
          
          moveChildNodesToNewParentNode(asyncOperation);
          
        } else if (operationType.equals(NodeHierarchyCommandRequest.DELETE_OPERATION_TYPE)) {
          
          deleteChildNodes(asyncOperation);

        } else if (operationType.equals(NodeHierarchyCommandRequest.CREATE_OPERATION_TYPE)) {
          
          createNode(asyncOperation);

        } else if (operationType.equals(NodeHierarchyCommandRequest.UPDATE_CUSTOM_POINT)) {
          
          updateCustomAsyncComputedPoint(asyncOperation);
          
        } else {
          
          throw new IllegalStateException("Unsupported operation type: "
              + operationType
              + " for operation category: "
              + operationCategory);
        }
        
      } else if (operationCategory.equals(NodeHierarchyCommandRequest.BULK_PORTFOLIO_OPERATION_CATEGORY)) {
        
        if (operationType.equals(NodeHierarchyCommandRequest.VALIDATE_OPERATION_TYPE)) {
          
          validatePortfolio(asyncOperation);
          
        } else if (operationType.equals(NodeHierarchyCommandRequest.REMEDIATE_OPERATION_TYPE)) {
          
          remediatePortfolio(asyncOperation);
          
        } else if (operationType.equals(NodeHierarchyCommandRequest.EVALUATE_REPORTS_OPERATION_TYPE)) {
          
          evaluteReportsForPortfolio(asyncOperation);

        } else if (operationType.equals(NodeHierarchyCommandRequest.UPDATE_OPERATION_TYPE)) {
          
          addPointTemplateUnitMappingOverride(asyncOperation);
          
        } else if (operationType.equals(NodeHierarchyCommandRequest.DELETE_OPERATION_TYPE)) {
          
          removePointTemplateUnitMappingOverride(asyncOperation);
          
        } else {
          
          throw new IllegalStateException("Unsupported operation type: "
              + operationType
              + " for operation category: "
              + operationCategory);
        }
        
      } else {
        
        throw new IllegalStateException("Unsupported operation category: " + operationCategory);
      }
      
    } finally {
      
      // If all jobs for the customer are complete, then release the lock.
      releasedLock = releaseLockIfAsyncOperationsComplete(
          customerId, 
          submittedBy);
    }
    return releasedLock;
  }
  
  @Override
  public boolean releaseLockIfAsyncOperationsComplete(int customerId, String submittedBy) {
    
    // Retrieve/acquire the pessimistic lock for the given customer.
    Optional<AsyncOperationLockEntity> lockOptional = asyncOperationLockRepository.retrieveLockBySubmittedBy(
        customerId, 
        submittedBy);

    // If the lock is present, see if all async operations are complete.
    boolean releasedLock = false;
    AsyncOperationLockEntity lock = null;
    if (lockOptional.isPresent()) {

      // The user may already have it, as we do a lookup by submitted by here.
      lock = lockOptional.get();
      
      String operationCategory = null;
      String operationType = null;
      int numOpenOperations = 0;
      for (AsyncOperationEntity asyncOperation: retrieveAsyncOperations(
          customerId, 
          null,
          null)) {
        
        AsyncOperationStatus status = asyncOperation.getStatus();
        
        boolean isTimedOut = asyncOperation.isOperationTimedOut();
        if (!isTimedOut && (status.equals(AsyncOperationStatus.CREATED) 
            || status.equals(AsyncOperationStatus.DISPATCHED))) {
          
          numOpenOperations++;
          
          if (operationType == null) {
            
            operationCategory = asyncOperation.getOperationCategory();
            operationType = asyncOperation.getOperationType();
          }
        }
      }
      
      if (numOpenOperations == 0) {
        
        LOGGER.info("All async operations have completed or timed out for customerId: [{}] and submittedBy: [{}], releasing lock",
            customerId,
            submittedBy);
        
        asyncOperationLockRepository.deleteLock(customerId, submittedBy);
        releasedLock = true;
        
      } else if (operationType != null) {

        LOGGER.info("Updating open lock for customerId: [{}] and submittedBy: [{}] for operationCategory: [{}] and operationType: [{}]",
            customerId,
            submittedBy,
            operationCategory,
            operationType);
        
        lock.setOperationCategory(operationCategory);
        lock.setOperationType(operationType);
        lock.setLockUpdateTime(AbstractEntity.getTimeKeeper().getCurrentTimestamp());
        asyncOperationLockRepository.updateLock(
            customerId, 
            operationCategory, 
            operationType, 
            submittedBy);
      }
    }
    
    return releasedLock;
  }

  /*
   * 
   * @param asyncOperation
   */
  protected void mapRawPoints(AsyncOperationEntity asyncOperation) {
    
    Integer asyncOperationId = asyncOperation.getPersistentIdentity();
    
    try {
      
      MapRawPointsRequest request = AbstractEntity.OBJECT_MAPPER.get().readValue(
          asyncOperation.getRequestJson(), 
          new TypeReference<MapRawPointsRequest>() {});

      validateTimeToValueOrchestration(asyncOperation, request);
      
      try {
        nodeHierarchyService.mapRawPoints(request);
      } catch (StaleDataException sde) {
        nodeHierarchyService.mapRawPoints(request);
      }
      
      updateAsyncOperationForCompletedStatus(asyncOperationId);
      
    } catch (Exception e) {
      
      updateAsyncOperationForFailedStatus(
          asyncOperationId, 
          buildFailureReason(e));      
    }
  }  
  
  /*
   * 
   * @param asyncOperation
   */
  protected void unmapRawPoints(AsyncOperationEntity asyncOperation) {
   
    Integer asyncOperationId = asyncOperation.getPersistentIdentity();
    
    try {
      
      UnmapRawPointsRequest request = AbstractEntity.OBJECT_MAPPER.get().readValue(
          asyncOperation.getRequestJson(), 
          new TypeReference<UnmapRawPointsRequest>() {});

      try {
        nodeHierarchyService.unmapRawPoints(request);  
      } catch (StaleDataException sde) {
        nodeHierarchyService.unmapRawPoints(request);
      }
      
      updateAsyncOperationForCompletedStatus(asyncOperationId);
      
    } catch (Exception e) {
      
      updateAsyncOperationForFailedStatus(
          asyncOperationId, 
          buildFailureReason(e));      
    }
  }  

  /*
   * 
   * @param asyncOperation
   */
  protected void ignoreRawPoints(AsyncOperationEntity asyncOperation) {
    
    setIgnoredState(asyncOperation, true);
    
  }

  /*
   * 
   * @param asyncOperation
   */
  protected void unignoreRawPoints(AsyncOperationEntity asyncOperation) {
    
    setIgnoredState(asyncOperation, false);
  }

  /*
   * 
   * @param asyncOperation
   * @param ignoredState
   */
  private void setIgnoredState(
      AsyncOperationEntity asyncOperation,
      boolean ignoredState) {
    
    Integer asyncOperationId = asyncOperation.getPersistentIdentity();
    
    try {
      
      String requestJson = asyncOperation.getRequestJson();

      if (ignoredState) {

        try {
          nodeHierarchyService.ignoreRawPoints(unmarshallIgnoreRawPointsRequestFromJson(requestJson));  
        } catch (StaleDataException sde) {
          nodeHierarchyService.ignoreRawPoints(unmarshallIgnoreRawPointsRequestFromJson(requestJson));
        }        
        
      } else {

        nodeHierarchyService.unignoreRawPoints(unmarshallUnIgnoreRawPointsRequestFromJson(requestJson));
      }
      
      updateAsyncOperationForCompletedStatus(asyncOperationId);
      
    } catch (Exception e) {
      
      updateAsyncOperationForFailedStatus(
          asyncOperationId, 
          buildFailureReason(e));      
    }
  }  
  
  /*
   * 
   * @param asyncOperation
   */
  protected void updateBuildings(AsyncOperationEntity asyncOperation) {
   
    Integer asyncOperationId = asyncOperation.getPersistentIdentity();
    
    try {
      
      UpdateBuildingNodesRequest request = AbstractEntity.OBJECT_MAPPER.get().readValue(
          asyncOperation.getRequestJson(), 
          new TypeReference<UpdateBuildingNodesRequest>() {});

      try {
        nodeHierarchyService.updateBuildingNodes(request);  
      } catch (StaleDataException sde) {
        nodeHierarchyService.updateBuildingNodes(request);
      }
      
      updateAsyncOperationForCompletedStatus(asyncOperationId);
      
    } catch (Exception e) {
      
      updateAsyncOperationForFailedStatus(
          asyncOperationId, 
          buildFailureReason(e));      
    }
  } 
  
  /*
   * 
   * @param asyncOperation
   */
  protected void updateEnergyExchangeSystemNodes(AsyncOperationEntity asyncOperation) {
   
    Integer asyncOperationId = asyncOperation.getPersistentIdentity();
    
    try {
      
      UpdateEnergyExchangeSystemNodesRequest request = AbstractEntity.OBJECT_MAPPER.get().readValue(
          asyncOperation.getRequestJson(), 
          new TypeReference<UpdateEnergyExchangeSystemNodesRequest>() {});

      try {
        nodeHierarchyService.updateEnergyExchangeSystemNodes(request);  
      } catch (StaleDataException sde) {
        nodeHierarchyService.updateEnergyExchangeSystemNodes(request);
      }
      
      updateAsyncOperationForCompletedStatus(asyncOperationId);
      
    } catch (Exception e) {
      
      updateAsyncOperationForFailedStatus(
          asyncOperationId, 
          buildFailureReason(e));      
    }
  }  

  /*
   * 
   * @param asyncOperation
   */
  protected void updateMappablePoints(AsyncOperationEntity asyncOperation) {
    
    Integer asyncOperationId = asyncOperation.getPersistentIdentity();
    
    try {
      
      UpdateMappablePointNodesRequest request = AbstractEntity.OBJECT_MAPPER.get().readValue(
          asyncOperation.getRequestJson(), 
          new TypeReference<UpdateMappablePointNodesRequest>() {});

      try {
        nodeHierarchyService.updateMappablePointNodes(request);  
      } catch (StaleDataException sde) {
        nodeHierarchyService.updateMappablePointNodes(request);
      }
      
      updateAsyncOperationForCompletedStatus(asyncOperationId);
      
    } catch (Exception e) {
      
      updateAsyncOperationForFailedStatus(
          asyncOperationId, 
          buildFailureReason(e));      
    }
  }  

  /*
   * 
   * @param asyncOperation
   */
  protected void createAdFunctionInstances(AsyncOperationEntity asyncOperation) {
    
    Integer asyncOperationId = asyncOperation.getPersistentIdentity();
    
    try {
      
      CreateAdFunctionInstancesRequest request = AbstractEntity.OBJECT_MAPPER.get().readValue(
          asyncOperation.getRequestJson(), 
          new TypeReference<CreateAdFunctionInstancesRequest>() {});
      
      validateTimeToValueOrchestration(asyncOperation, request);

      try {
        nodeHierarchyService.createAdFunctionInstancesFromCandidates(request);  
      } catch (StaleDataException sde) {
        nodeHierarchyService.createAdFunctionInstancesFromCandidates(request);
      }
      
      updateAsyncOperationForCompletedStatus(asyncOperationId);
      
    } catch (Exception e) {
      
      updateAsyncOperationForFailedStatus(
          asyncOperationId, 
          buildFailureReason(e));      
    }
  }  

  /*
   * 
   * @param asyncOperation
   */
  protected void updateAdFunctionInstances(AsyncOperationEntity asyncOperation) {
    
    Integer asyncOperationId = asyncOperation.getPersistentIdentity();
    
    try {
      
      UpdateAdFunctionInstancesRequest request = AbstractEntity.OBJECT_MAPPER.get().readValue(
          asyncOperation.getRequestJson(), 
          new TypeReference<UpdateAdFunctionInstancesRequest>() {});
      
      try {
        nodeHierarchyService.updateAdFunctionInstances(request);  
      } catch (StaleDataException sde) {
        nodeHierarchyService.updateAdFunctionInstances(request);
      }

      updateAsyncOperationForCompletedStatus(asyncOperationId);
      
    } catch (Exception e) {
      
      updateAsyncOperationForFailedStatus(
          asyncOperationId, 
          buildFailureReason(e));      
    }
  }  

  /*
   * 
   * @param asyncOperation
   */
  protected void deleteAdFunctionInstances(AsyncOperationEntity asyncOperation) {

    Integer asyncOperationId = asyncOperation.getPersistentIdentity();
    
    try {
      
      DeleteAdFunctionInstancesRequest request = AbstractEntity.OBJECT_MAPPER.get().readValue(
          asyncOperation.getRequestJson(),
          new TypeReference<DeleteAdFunctionInstancesRequest>() {});
      
      try {
        nodeHierarchyService.deleteAdFunctionInstances(request);  
      } catch (StaleDataException sde) {
        nodeHierarchyService.deleteAdFunctionInstances(request);
      }

      updateAsyncOperationForCompletedStatus(asyncOperationId);
      
    } catch (Exception e) {
      
      updateAsyncOperationForFailedStatus(
          asyncOperationId, 
          buildFailureReason(e));      
    }
  }
  
  /*
   * 
   * @param asyncOperation
   */
  protected void updateReportInstances(AsyncOperationEntity asyncOperation) {
    
    Integer asyncOperationId = asyncOperation.getPersistentIdentity();
    
    try {
      
      UpdateReportInstancesRequest request = AbstractEntity.OBJECT_MAPPER.get().readValue(
          asyncOperation.getRequestJson(), 
          new TypeReference<UpdateReportInstancesRequest>() {});
      
      updateReportInstances(request);
      
      updateAsyncOperationForCompletedStatus(asyncOperationId);
      
    } catch (Exception e) {
      
      updateAsyncOperationForFailedStatus(
          asyncOperationId, 
          buildFailureReason(e));      
    }
  }
  
  protected void updateReportInstances(
      UpdateReportInstancesRequest request)
  throws 
      EntityDoesNotExistException,
      StaleDataException {

    try {
      nodeHierarchyService.updateReportInstances(request);  
    } catch (StaleDataException sde) {
      nodeHierarchyService.updateReportInstances(request);
    }
  }   
   
   /*
   * 
   * @param asyncOperation
   */
  protected void moveChildNodesToNewParentNode(AsyncOperationEntity asyncOperation) {
   
    Integer asyncOperationId = asyncOperation.getPersistentIdentity();
    
    try {
      
      MoveChildNodesRequest request = AbstractEntity.OBJECT_MAPPER.get().readValue(
          asyncOperation.getRequestJson(),
          new TypeReference<MoveChildNodesRequest>() {});
      
      try {
        nodeHierarchyService.moveChildNodesToNewParentNode(request);  
      } catch (StaleDataException sde) {
        nodeHierarchyService.moveChildNodesToNewParentNode(request);
      }

      updateAsyncOperationForCompletedStatus(asyncOperationId);
      
    } catch (Exception e) {
      
      updateAsyncOperationForFailedStatus(
          asyncOperationId, 
          buildFailureReason(e));      
    }
  }  
  
  /*
   * 
   * @param asyncOperation
   */
  protected void deleteChildNodes(AsyncOperationEntity asyncOperation) {
   
    Integer asyncOperationId = asyncOperation.getPersistentIdentity();

    try {
      
      DeleteChildNodesRequest request = AbstractEntity.OBJECT_MAPPER.get().readValue(
          asyncOperation.getRequestJson(),
          new TypeReference<DeleteChildNodesRequest>() {});
      
      try {
        nodeHierarchyService.deleteChildNodes(request);  
      } catch (StaleDataException sde) {
        nodeHierarchyService.deleteChildNodes(request);
      }

      updateAsyncOperationForCompletedStatus(asyncOperationId);
      
    } catch (Exception e) {
      
      updateAsyncOperationForFailedStatus(
          asyncOperationId, 
          buildFailureReason(e));      
    }
  }

  protected void validatePortfolio(AsyncOperationEntity asyncOperation) {
    
    Integer asyncOperationId = asyncOperation.getPersistentIdentity();

    try {
      
      ValidatePortfolioRequest request = AbstractEntity.OBJECT_MAPPER.get().readValue(
          asyncOperation.getRequestJson(),
          new TypeReference<ValidatePortfolioRequest>() {});
      
      nodeHierarchyService.validatePortfolio(request);

      updateAsyncOperationForCompletedStatus(asyncOperationId);
      
    } catch (Exception e) {
      
      updateAsyncOperationForFailedStatus(
          asyncOperationId, 
          buildFailureReason(e));      
    }
  }
  
  protected void remediatePortfolio(AsyncOperationEntity asyncOperation) {
    
    Integer asyncOperationId = asyncOperation.getPersistentIdentity();

    try {
      
      RemediatePortfolioRequest request = AbstractEntity.OBJECT_MAPPER.get().readValue(
          asyncOperation.getRequestJson(),
          new TypeReference<RemediatePortfolioRequest>() {});
      
      try {
        nodeHierarchyService.remediatePortfolio(request);  
      } catch (StaleDataException sde) {
        nodeHierarchyService.remediatePortfolio(request);
      }

      updateAsyncOperationForCompletedStatus(asyncOperationId);
      
    } catch (Exception e) {
      
      updateAsyncOperationForFailedStatus(
          asyncOperationId, 
          buildFailureReason(e));      
    }
  }

  protected void evaluteReportsForPortfolio(AsyncOperationEntity asyncOperation) {
    
    Integer asyncOperationId = asyncOperation.getPersistentIdentity();

    try {
      
      EvaluateReportsRequest request = AbstractEntity.OBJECT_MAPPER.get().readValue(
          asyncOperation.getRequestJson(),
          new TypeReference<EvaluateReportsRequest>() {});

      try {
        nodeHierarchyService.evaluateReports(request);  
      } catch (StaleDataException sde) {
        nodeHierarchyService.evaluateReports(request);
      }

      updateAsyncOperationForCompletedStatus(asyncOperationId);
      
    } catch (Exception e) {
      
      updateAsyncOperationForFailedStatus(
          asyncOperationId, 
          buildFailureReason(e));      
    }
  }
  
  protected void addPointTemplateUnitMappingOverride(AsyncOperationEntity asyncOperation) {

    Integer asyncOperationId = asyncOperation.getPersistentIdentity();

    try {
      
      AddPointTemplateOverrideRequest request = AbstractEntity.OBJECT_MAPPER.get().readValue(
          asyncOperation.getRequestJson(),
          new TypeReference<AddPointTemplateOverrideRequest>() {});
      
      validateTimeToValueOrchestration(asyncOperation, request);
      
      try {
        nodeHierarchyService.addPointTemplateUnitMappingOverride(request);  
      } catch (StaleDataException sde) {
        nodeHierarchyService.addPointTemplateUnitMappingOverride(request);
      }

      updateAsyncOperationForCompletedStatus(asyncOperationId);
      
    } catch (Exception e) {
      
      updateAsyncOperationForFailedStatus(
          asyncOperationId, 
          buildFailureReason(e));      
    }
  }
  
  protected void removePointTemplateUnitMappingOverride(AsyncOperationEntity asyncOperation) {

    Integer asyncOperationId = asyncOperation.getPersistentIdentity();

    try {
      
      RemovePointTemplateOverrideRequest request = AbstractEntity.OBJECT_MAPPER.get().readValue(
          asyncOperation.getRequestJson(),
          new TypeReference<RemovePointTemplateOverrideRequest>() {});
      
      validateTimeToValueOrchestration(asyncOperation, request);
      
      try {
        nodeHierarchyService.removePointTemplateUnitMappingOverride(request);  
      } catch (StaleDataException sde) {
        nodeHierarchyService.removePointTemplateUnitMappingOverride(request);
      }

      updateAsyncOperationForCompletedStatus(asyncOperationId);
      
    } catch (Exception e) {
      
      updateAsyncOperationForFailedStatus(
          asyncOperationId, 
          buildFailureReason(e));      
    }
  }
  
  protected void findAdFunctionInstanceCandidatesForPortfolio(AsyncOperationEntity asyncOperation) {
    
    Integer asyncOperationId = asyncOperation.getPersistentIdentity();

    try {
      
      FindAdFunctionInstanceCandidatesRequest request = AbstractEntity.OBJECT_MAPPER.get().readValue(
          asyncOperation.getRequestJson(),
          new TypeReference<FindAdFunctionInstanceCandidatesRequest>() {});

      try {
        nodeHierarchyService.findAdFunctionInstanceCandidates(request);  
      } catch (StaleDataException sde) {
        nodeHierarchyService.findAdFunctionInstanceCandidates(request);
      }

      updateAsyncOperationForCompletedStatus(asyncOperationId);
      
    } catch (Exception e) {
      
      updateAsyncOperationForFailedStatus(
          asyncOperationId, 
          buildFailureReason(e));      
    }
  }  

  protected void createNode(AsyncOperationEntity asyncOperation) {
    
    Integer asyncOperationId = asyncOperation.getPersistentIdentity();

    try {
      
      CreateNodeRequest request = AbstractEntity.OBJECT_MAPPER.get().readValue(
          asyncOperation.getRequestJson(),
          new TypeReference<CreateNodeRequest>() {});

      try {
        nodeHierarchyService.createNode(request);  
      } catch (StaleDataException sde) {
        nodeHierarchyService.createNode(request);
      }

      updateAsyncOperationForCompletedStatus(asyncOperationId);
      
    } catch (Exception e) {
      
      updateAsyncOperationForFailedStatus(
          asyncOperationId, 
          buildFailureReason(e));      
    }
  }  
  
  protected void updateCustomAsyncComputedPoint(AsyncOperationEntity asyncOperation) {
    
    Integer asyncOperationId = asyncOperation.getPersistentIdentity();

    try {
      
      UpdateCustomAsyncComputedPointNodesRequest request = AbstractEntity.OBJECT_MAPPER.get().readValue(
          asyncOperation.getRequestJson(),
          new TypeReference<UpdateCustomAsyncComputedPointNodesRequest>() {});
      
      Map<Long, String> itemResults = new TreeMap<>();
      for (CustomAsyncComputedPointNodeData dto: request.getDtoList()) {
        itemResults.put(Long.valueOf(dto.getId()), AsyncOperationEntity.RESULT_FAILURE);
      }
      
      try {
        nodeHierarchyService.updateCustomAsyncComputedPointNodes(request);  
      } catch (StaleDataException sde) {
        nodeHierarchyService.updateCustomAsyncComputedPointNodes(request);
      }

      for (CustomAsyncComputedPointNodeData dto: request.getDtoList()) {
        itemResults.put(Long.valueOf(dto.getId()), AsyncOperationEntity.RESULT_SUCCESS);
      }
      
      updateAsyncOperationForCompletedStatus(asyncOperationId);
      
    } catch (Exception e) {
      
      updateAsyncOperationForFailedStatus(
          asyncOperationId, 
          buildFailureReason(e));      
    }
  }
  
  protected String buildFailureReason(Exception e) {

    String reason = ExceptionUtils.extractReason(e);
    LOGGER.error(reason, e);
    return reason;
  }
  
  protected String marshallCommandRequestToJson(NodeHierarchyCommandRequest commandRequest) {
    try {
      return AbstractEntity.OBJECT_WRITER.get().writeValueAsString(commandRequest);
    } catch (JsonProcessingException jpe) {
      throw new IllegalStateException("JSON processing error: " + commandRequest, jpe);
    }    
  }
  
  protected String marshallToJson(Object object) {
    try {
      return AbstractEntity.OBJECT_WRITER.get().writeValueAsString(object);
    } catch (JsonProcessingException jpe) {
      throw new IllegalStateException("JSON processing error: " + object, jpe);
    }
  }

  protected IgnoreRawPointsRequest unmarshallIgnoreRawPointsRequestFromJson(String requestJson) {
    try {
      return AbstractEntity.OBJECT_MAPPER.get().readValue(requestJson, new TypeReference<IgnoreRawPointsRequest>() {});
    } catch (IOException jpe) {
      throw new IllegalStateException("JSON processing error: " + requestJson, jpe);
    }
  }

  protected UnignoreRawPointsRequest unmarshallUnIgnoreRawPointsRequestFromJson(String requestJson) {
    try {
      return AbstractEntity.OBJECT_MAPPER.get().readValue(requestJson, new TypeReference<UnignoreRawPointsRequest>() {});
    } catch (IOException jpe) {
      throw new IllegalStateException("JSON processing error: " + requestJson, jpe);
    }
  }
  
  /*
   * TIME TO VALUE (A.K.A. FAST TRACK) OPERATION CATEGORY:
   * =====================================================
   * 
   * OPERATION TYPE GROUPINGS:
   * =========================
   * GROUP 1: CONNECTOR MODEL DATA
   * GROUP 2: CONNECTOR TIME SERIES DATA
   * GROUP 3: RULE, COMPUTED, CUSTOM (TIME SERIES DATA)
   * GROUP 4: WEATHER, SCHEDULED (TIME SERIES DATA)
   * 
   * GATE 1: Users cannot map points until group 1 is complete
   * GATE 2: Users cannot enable rule/computed points or create custom points until group 2 is complete
   * GATE 3: Users cannot enable reports until groups 3/4 are complete
   * 
   * All operations "time out" after 2 min.
   */
  private void validateTimeToValueOrchestration(
      AsyncOperationEntity asyncOperation,
      NodeHierarchyCommandRequest request) {
    
    Integer customerId = asyncOperation.getCustomerId();
    
    boolean isGroup1Complete = true;
    boolean isGroup2Complete = true;
    boolean isGroup3Complete = true;
    boolean isGroup4Complete = true;
    
    for (AsyncOperationEntity ao: retrieveAsyncOperations(
        customerId,
        null,
        null)) {
      
      String ot = ao.getOperationType();
      
      if (!ao.isComplete()) {
        
        // If time to value, then see what group this operation is from.
        if (ot.equals(NodeHierarchyCommandRequest.FAST_TRACK_CONNECTOR_MODEL_DATA)) {
          
          isGroup1Complete = false;
          
        } else if (ot.equals(NodeHierarchyCommandRequest.FAST_TRACK_CONNECTOR_TIME_SERIES_DATA)) {
          
          isGroup2Complete = false;
          
        } else if (ot.equals(NodeHierarchyCommandRequest.FAST_TRACK_AD_FUNCTION_TIME_SERIES_DATA) 
            || ot.equals(NodeHierarchyCommandRequest.FAST_TRACK_CUSTOM_POINT_TIME_SERIES_DATA)) {
          
          isGroup3Complete = false;
          
        } else if (ot.equals(NodeHierarchyCommandRequest.FAST_TRACK_SCHEDULED_POINT_TIME_SERIES_DATA)
            || ot.equals(NodeHierarchyCommandRequest.FAST_TRACK_WEATHER_TIME_SERIES_DATA)) {
          
          isGroup4Complete = false;
        }
        
        // Validate the time to value orchestration gates.
        if (request instanceof MapRawPointsRequest && !isGroup1Complete) {
          
          throw new IllegalStateException("Cannot map points while fast track for connector model data is still in progress");
          
        } else if (request instanceof CreateAdFunctionInstancesRequest && !isGroup2Complete) {
          
          throw new IllegalStateException("Cannot enable rules/computed points while fast track for connector time series data is still in progress");
          
        } else if (request instanceof UpdateReportInstancesRequest && !isGroup3Complete && !isGroup4Complete) {
          
          throw new IllegalStateException("Cannot enable reports while fast track for time series data is still in progress");
          
        }
      }
    }
  }  
}
//@formatter:on
