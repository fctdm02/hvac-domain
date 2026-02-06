//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service.async.operation.repository;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.nodehierarchy.service.NodeHierarchyService;
import com.djt.hvac.domain.model.nodehierarchy.service.async.lock.repository.AsyncOperationLockRepository;
import com.djt.hvac.domain.model.nodehierarchy.service.async.operation.AsyncOperationEntity;
import com.djt.hvac.domain.model.nodehierarchy.service.async.operation.dto.AsyncOperationDto;
import com.djt.hvac.domain.model.nodehierarchy.service.command.NodeHierarchyCommandRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

public class AsyncOperationRepositoryFileSystemImpl extends AbstractAsyncOperationRepository {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(AsyncOperationRepositoryFileSystemImpl.class);
  
  private static Integer MAX_PERSISTENT_IDENTITY_VALUE = Integer.valueOf(0);
  synchronized private static Integer getNextPersistentIdentityValue() {
    
    Integer nextValue = Integer.valueOf((MAX_PERSISTENT_IDENTITY_VALUE.intValue()-1));
    MAX_PERSISTENT_IDENTITY_VALUE = nextValue;
    return nextValue;
  }

  private final String basePath;
  
  // In the production implementation, there will be a FIFO queue for every customer.  There will be a pool
  // of workers such that only one customer command request can be processed at a given time, yet many
  // can be processed concurrently.  So, we create the appropriate data structure here to support that.
  // The algorithm here, round robin, is just meant to implement this mock, as the production implementation
  // may be more elaborate.
  
  public AsyncOperationRepositoryFileSystemImpl(
      String basePath,
      AsyncOperationLockRepository asyncOperationLockRepository,
      NodeHierarchyService nodeHierarchyService) {
    
    super(
        asyncOperationLockRepository,
        nodeHierarchyService);
    
    this.basePath = basePath;
    LOGGER.debug("Using path: {}", this.basePath);
  }
  
  /*
   * MOCK PRODUCER
   */
  @Override
  public synchronized AsyncOperationEntity createAsyncOperation(NodeHierarchyCommandRequest commandRequest) {
    
    LOGGER.info("Creating async operation for command request: [{}]",
        commandRequest);
    
    Integer customerId = commandRequest.getCustomerId();
    
    String requestJson = null;
    try {
      requestJson = AbstractEntity.OBJECT_WRITER.get().writeValueAsString(commandRequest);
    } catch (JsonProcessingException jpe) {
      throw new IllegalStateException("JSON processing error: " + commandRequest, jpe);
    }
    
    Map<Integer, AsyncOperationEntity> asyncOperations = loadAsyncOperations();
    Integer asyncOperationId = getNextPersistentIdentityValue();
    
    AsyncOperationEntity asyncOperation = new AsyncOperationEntity(
        asyncOperationId,
        customerId,
        commandRequest.getOperationCategory(),
        commandRequest.getOperationType(),
        requestJson,
        commandRequest.getSubmittedBy(),
        AbstractEntity.getTimeKeeper().getCurrentTimestamp());
    
    asyncOperations.put(asyncOperationId, asyncOperation);
    storeAsyncOperations(asyncOperations);
    
    // MOCK CONSUMER
    CompletableFuture.runAsync(() -> {
      
      try {
        
        super.processAsyncOperation(asyncOperation);
        
      } catch (Exception e) {
        
        Throwable cause = e.getCause();
        String reason = null;
        if (cause != null) {
          reason = "Unable to process operation, error: "
              + e.getMessage()
              + ", cause: "
              + cause.getMessage();
        } else {
          reason = "Unable to process operation, error: "
              + e.getMessage();
        }
        LOGGER.error(reason, e);
        
        updateAsyncOperationForFailedStatus(
            asyncOperationId, 
            reason);
      }
    });
    
    return asyncOperation;
  }
  
  @Override
  public Optional<AsyncOperationEntity> retrieveAsyncOperation(Integer asyncOperationId) {
    
    return retrieveAsyncOperation(asyncOperationId, true);
  }

  @Override
  public Optional<AsyncOperationEntity> retrieveAsyncOperation(Integer asyncOperationId, boolean loadRequestJson) {
    
    Map<Integer, AsyncOperationEntity> asyncOperations = loadAsyncOperations();
    
    return Optional.ofNullable(asyncOperations.get(asyncOperationId));
  }
  
  @Override
  public List<AsyncOperationEntity> retrieveAsyncOperations(
      Integer customerId,
      String operationCategory,
      String operationType) {
    
    List<AsyncOperationEntity> list = new ArrayList<>();
    Map<Integer, AsyncOperationEntity> asyncOperations = loadAsyncOperations();
    for (AsyncOperationEntity asyncOperation: asyncOperations.values()) {
      
      if (asyncOperation.getCustomerId().equals(customerId)) {
        
        boolean match = true;
        if (operationCategory != null && operationCategory.equals(asyncOperation.getOperationCategory())) {
          match = false;
        }
        if (operationType != null && operationType.equals(asyncOperation.getOperationCategory())) {
          match = false;
        }
        if (match) {
          list.add(asyncOperation);
        }
      }
    }    
    return list;
  }
    
  @Override
  public void updateAsyncOperationForDispatchedStatus(Integer asyncOperationId) {
    
    LOGGER.info("Dispatching async operation with id: [{}]", asyncOperationId);
    
    Map<Integer, AsyncOperationEntity> asyncOperations = loadAsyncOperations();
    
    AsyncOperationEntity asyncOperation = asyncOperations.get(asyncOperationId);
    
    asyncOperation.setDispatchedStatus();
    
    storeAsyncOperations(asyncOperations);
  }
  
  @Override
  public void updateAsyncOperationForCompletedStatus(Integer asyncOperationId) {
  
    Timestamp endTime = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
    
    LOGGER.info("Completing async operation for subject id items: [{}], endTime: [{}]",
        asyncOperationId,
        endTime);
    
    Map<Integer, AsyncOperationEntity> asyncOperations = loadAsyncOperations();
    
    AsyncOperationEntity asyncOperation = asyncOperations.get(asyncOperationId);
    
    asyncOperation.updateForCompletedStatus(endTime);
    
    storeAsyncOperations(asyncOperations);
  }
  
  @Override
  public void updateAsyncOperationForFailedStatus(
      Integer asyncOperationId,
      String reason) {
    
    Timestamp endTime = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
    
    LOGGER.info("Failing async operation with id: [{}] with reason: [{}] and endTime: [{}].",
        asyncOperationId,
        reason,
        endTime);
    
    Map<Integer, AsyncOperationEntity> asyncOperations = loadAsyncOperations();
    
    AsyncOperationEntity asyncOperation = asyncOperations.get(asyncOperationId);
    
    asyncOperation.updateForFailedStatus(reason, endTime);
    
    storeAsyncOperations(asyncOperations);
  }
  
  @Override
  public Integer deleteAsyncOperations(
      Integer customerId, 
      List<Integer> asyncOperationIds) {
    
    LOGGER.info("Deleting async operations for customerId: ["
        + customerId
        + "] and asyncOperationIds: ["
        + asyncOperationIds
        + "].");
    
    Map<Integer, AsyncOperationEntity> asyncOperations = loadAsyncOperations();
    for (Integer id: asyncOperationIds) {
      
      asyncOperations.remove(id);  
    }
    storeAsyncOperations(asyncOperations);
    return asyncOperationIds.size();
  }
  
  private Map<Integer, AsyncOperationEntity> loadAsyncOperations() {
    
    Map<Integer, AsyncOperationEntity> entities = new TreeMap<>();
    List<AsyncOperationDto> dtos = loadAsyncOperationDtos();
    for (AsyncOperationDto dto: dtos) {
      
      AsyncOperationEntity entity = AsyncOperationEntity
          .Mapper
          .getInstance()
          .mapDtoToEntity(
              dto.getCustomerId(), 
              dto);
      
      entities.put(dto.getId(), entity);
    }
    return entities;
  }
  
  private void storeAsyncOperations(Map<Integer, AsyncOperationEntity> entities) {

    List<AsyncOperationDto> dtos = new ArrayList<>();
    for (AsyncOperationEntity entity: entities.values()) {
      
      AsyncOperationDto dto = AsyncOperationEntity
          .Mapper
          .getInstance()
          .mapEntityToDto(entity);
      
      dtos.add(dto);
    }
    storeAsyncOperationDtos(dtos);
  }
    
  private synchronized List<AsyncOperationDto> loadAsyncOperationDtos() {
    
    File file = new File(basePath + "/Async_Operations.json");
    if (file.exists() && file.length() > 0) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));
          
          Scanner s = new Scanner(in).useDelimiter("\\A")) {
          String fullJson = s.hasNext() ? s.next() : "";
  
          JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
          String compactJson = jsonNode.toString();
          
          return AbstractEntity.OBJECT_MAPPER.get().readValue(compactJson, new TypeReference<List<AsyncOperationDto>>() {});
  
      } catch (IOException e) {
        LOGGER.error("Unable to load file: " + file.getAbsolutePath(), e);
      }
    }
    return new ArrayList<>();
  }
  
  private synchronized void storeAsyncOperationDtos(List<AsyncOperationDto> asyncOperations) {
    
    File file = new File(basePath + "/Async_Operations.json");
    OutputStream out = null;
    try {
      
      out = new BufferedOutputStream(new FileOutputStream(file));
      AbstractEntity.OBJECT_WRITER.get().writeValue(out, asyncOperations);

    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (out != null) {
        try {
          out.flush();
          out.close();
        } catch (IOException ioe) {
        }
      }
    }
  }
}
//@formatter:on
