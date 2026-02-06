//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service.async.lock.repository;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.nodehierarchy.service.async.lock.AsyncOperationLockEntity;
import com.djt.hvac.domain.model.nodehierarchy.service.async.lock.dto.AsyncOperationLockDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

public class AsyncOperationLockRepositoryFileSystemImpl extends AbstractAsyncOperationLockRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(AsyncOperationLockRepositoryFileSystemImpl.class);
  
  private String basePath;

  public AsyncOperationLockRepositoryFileSystemImpl() {
    this(null);
  }

  public AsyncOperationLockRepositoryFileSystemImpl(String basePath) {
    
    super();
    if (basePath != null) {
      this.basePath = basePath;
    } else {
      this.basePath = System.getProperty("user.home") + "/";      
    }
    LOGGER.debug("Using path: {}", this.basePath);
  }
  
  @Override
  public int createLock(AsyncOperationLockEntity lock) {
    
    Timestamp currentTime = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
    LOGGER.debug("createLock(): currentTime: {}, lock: {}",
        currentTime,
        lock);
    
    Optional<AsyncOperationLockEntity> check = retrieveLock(lock.getCustomerId());
    if (check.isPresent()) {
      throw new RuntimeException("Lock for customer id: ["
          + lock.getCustomerId()
          + "] already exists: ["
          + lock
          + "].");
    }
    
    Map<Integer, AsyncOperationLockEntity> locks = loadAsyncOperationLocks();
    locks.put(lock.getCustomerId(), lock);
    storeAsyncOperationLocks(locks);
    
    return 1;
  }
  
  @Override
  public Optional<AsyncOperationLockEntity> retrieveLock(Integer customerId) {

    Timestamp currentTime = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
    LOGGER.debug("retrieveLock(): currentTime: {}, customerId: {}",
        currentTime,
        customerId);
    
    for (Map.Entry<Integer, AsyncOperationLockEntity> entry: loadAsyncOperationLocks().entrySet()) {
      
      if (entry.getKey().equals(customerId)) {
        
        return Optional.of(entry.getValue());
      }
    }
    
    return Optional.empty();
  }  
  
  @Override
  public Optional<AsyncOperationLockEntity> retrieveLock(
      Integer customerId,
      String operationCategory,
      String operationType,
      String ownerUuid,
      String submittedBy) {

    Timestamp currentTime = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
    LOGGER.debug("retrieveLock(): currentTime: {}, customerId: {}, operationCategory: {}, operationType: {}, ownerUuid: {}, submittedBy: {}",
        currentTime,
        customerId,
        operationCategory,
        operationType,
        ownerUuid,
        submittedBy);
    
    if (ownerUuid == null) {
      ownerUuid = "*";
    }

    if (submittedBy == null) {
      submittedBy = "*";
    }
    
    for (Map.Entry<Integer, AsyncOperationLockEntity> entry: loadAsyncOperationLocks().entrySet()) {
      
      if (entry.getKey().equals(customerId)) {
        
        AsyncOperationLockEntity lock = entry.getValue();
        if (ownerUuid != null && !ownerUuid.equals("*") && lock.getOwnerUuid().equals(ownerUuid)) {
          return Optional.of(lock);
          
        } else if (submittedBy != null && !submittedBy.equals("*") && lock.getSubmittedBy().equals(submittedBy)) {
          return Optional.of(lock);
          
        } else if (!ownerUuid.equals("*") && !submittedBy.equals("*")) {
          return Optional.of(lock);
          
        }
      }
    }
    return Optional.empty();
  }  
  
  @Override
  public int updateLock(
      Integer customerId,
      String operationCategory,
      String operationType,
      String submittedBy) {

    Timestamp currentTime = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
    LOGGER.debug("updateLock(): currentTime: {}, customerId: {}, operationCategory: {}, operationType: {}, lockUpdateTime: {}, submittedBy: {}",
        currentTime,
        customerId,
        operationCategory,
        operationType,
        currentTime,
        submittedBy);
    
    Map<Integer, AsyncOperationLockEntity> locks = loadAsyncOperationLocks();
    AsyncOperationLockEntity lock = locks.get(customerId);
    if (!lock.getSubmittedBy().equals(submittedBy)) {
      throw new RuntimeException("Lock for customer with id: ["
          + customerId
          + "] cannot be updated for operationCategory: ["
          + operationCategory
          + "], operationType: ["
          + operationType
          + "] and updateTime: ["
          + currentTime
          + "] for submittedBy: ["
          + submittedBy
          + "] because it is owned by another user: ["
          + lock.getSubmittedBy()
          + "].");
    }
    
    lock.setOperationCategory(operationCategory);
    lock.setOperationType(operationType);
    lock.setLockUpdateTime(currentTime);
    locks.remove(lock.getCustomerId());
    locks.put(customerId, lock);
    storeAsyncOperationLocks(locks);

    return 1;
  }

  @Override
  public boolean deleteLock(Integer customerId, String submittedBy) {

    Timestamp currentTime = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
    LOGGER.debug("deleteLock(): currentTime: {}, customerId: {}, submittedBy: {}",
        currentTime,
        customerId,
        submittedBy);
    
    Map<Integer, AsyncOperationLockEntity> locks = loadAsyncOperationLocks();
    
    AsyncOperationLockEntity lock = locks.get(customerId);
    if (lock != null && lock.getSubmittedBy().equals(submittedBy)) {
      
      locks.remove(customerId);
      storeAsyncOperationLocks(locks);
      
      return true;
    }
    return false;
  }
  
  private Map<Integer, AsyncOperationLockEntity> loadAsyncOperationLocks() {
    
    Map<Integer, AsyncOperationLockEntity> entities = new TreeMap<>();
    List<AsyncOperationLockDto> dtos = loadAsyncOperationLockDtos();
    for (AsyncOperationLockDto dto: dtos) {
      
      AsyncOperationLockEntity entity = AsyncOperationLockEntity
          .Mapper
          .getInstance()
          .mapDtoToEntity(
              dto.getCustomerId(), 
              dto);
      
      entities.put(dto.getCustomerId(), entity);
    }
    return entities;
  }
  
  private void storeAsyncOperationLocks(Map<Integer, AsyncOperationLockEntity> entities) {
    
    List<AsyncOperationLockDto> dtos = new ArrayList<>();
    for (AsyncOperationLockEntity entity: entities.values()) {
      
      AsyncOperationLockDto dto = AsyncOperationLockEntity
          .Mapper
          .getInstance()
          .mapEntityToDto(entity);
      
      dtos.add(dto);
    }
    storeAsyncOperationLockDtos(dtos);
  }
  
  private List<AsyncOperationLockDto> loadAsyncOperationLockDtos() {
    
    File file = new File(basePath + "/Async_Operation_Locks.json");
    if (file.exists() && file.length() > 0) {
      try (InputStream in = new BufferedInputStream(new FileInputStream(file));
          
          Scanner s = new Scanner(in).useDelimiter("\\A")) {
          String fullJson = s.hasNext() ? s.next() : "";
  
          JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(fullJson, JsonNode.class);
          String compactJson = jsonNode.toString();
          
          return AbstractEntity.OBJECT_MAPPER.get().readValue(compactJson, new TypeReference<List<AsyncOperationLockDto>>() {});
  
      } catch (IOException e) {
        LOGGER.error("Unable to load file: " + file.getAbsolutePath(), e);
      }
    }
    return new ArrayList<>();
  }
  
  private void storeAsyncOperationLockDtos(List<AsyncOperationLockDto> locks) {
    
    File file = new File(basePath + "/Async_Operation_Locks.json");
    OutputStream out = null;
    try {
      
      out = new BufferedOutputStream(new FileOutputStream(file));
      AbstractEntity.OBJECT_WRITER.get().writeValue(out, locks);

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