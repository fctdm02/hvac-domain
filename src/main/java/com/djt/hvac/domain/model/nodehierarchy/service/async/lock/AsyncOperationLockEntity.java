//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service.async.lock;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.djt.hvac.domain.model.common.AbstractAssociativeEntity;
import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.mapper.DtoMapper;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.nodehierarchy.service.async.lock.dto.AsyncOperationLockDto;
import com.djt.hvac.domain.model.nodehierarchy.service.command.NodeHierarchyCommandRequest;

public class AsyncOperationLockEntity extends AbstractAssociativeEntity {
  private static final long serialVersionUID = 1L;
  private final Integer customerId;
  private String ownerUuid;
  private String operationCategory;
  private String operationType;
  private String submittedBy;
  private Timestamp lockAcquisitionTime;
  private Timestamp lockUpdateTime;
  
  public AsyncOperationLockEntity(
      Integer customerId,
      String ownerUuid,
      String operationCategory,
      String operationType,
      String submittedBy,
      Timestamp lockAcquisitionTime,
      Timestamp lockUpdateTime) {
    
    this.customerId = customerId;
    this.ownerUuid = ownerUuid;
    this.operationCategory = operationCategory;
    this.operationType = operationType;
    this.submittedBy = submittedBy;
    this.lockAcquisitionTime = lockAcquisitionTime;
    this.lockUpdateTime = lockUpdateTime;
  }

  public String getOwnerUuid() {
    return ownerUuid;
  }

  public void setOwnerUuid(String ownerUuid) {
    this.ownerUuid = ownerUuid;
  }

  public String getOperationCategory() {
    return operationCategory;
  }

  public void setOperationCategory(String operationCategory) {
    this.operationCategory = operationCategory;
  }

  public String getOperationType() {
    return operationType;
  }

  public void setOperationType(String operationType) {
    this.operationType = operationType;
  }

  public String getSubmittedBy() {
    return submittedBy;
  }

  public void setSubmittedBy(String submittedBy) {
    this.submittedBy = submittedBy;
  }

  public Timestamp getLockAcquisitionTime() {
    return lockAcquisitionTime;
  }

  public void setLockAcquisitionTime(Timestamp lockAcquisitionTime) {
    this.lockAcquisitionTime = lockAcquisitionTime;
  }

  public Timestamp getLockUpdateTime() {
    return lockUpdateTime;
  }

  public void setLockUpdateTime(Timestamp lockUpdateTime) {
    this.lockUpdateTime = lockUpdateTime;
  }

  public Integer getCustomerId() {
    return customerId;
  }
  
  public Integer getLockAgeInSeconds() {
    
    Timestamp currentTime = AbstractEntity
        .getTimeKeeper()
        .getCurrentTimestamp();
    
    Long lockAgeInSeconds = 
        Duration.between(
            lockAcquisitionTime.toLocalDateTime(),
            currentTime.toLocalDateTime())
            .getSeconds();
    
    return lockAgeInSeconds.intValue();
  }
  
  public Integer getLockIdleTimeInSeconds() {

    Timestamp currentTime = AbstractEntity
        .getTimeKeeper()
        .getCurrentTimestamp();

    Long lockIdleTimeInSeconds = 
        Duration.between(
            lockUpdateTime.toLocalDateTime(),
            currentTime.toLocalDateTime())
            .getSeconds();
    
    return lockIdleTimeInSeconds.intValue();
  }
  
  public boolean isLockTimedOut() {
    
    Integer lockIdleTimeInSeconds = getLockIdleTimeInSeconds();

    int timeoutThresholdSeconds = 600;
    if (operationCategory.equals(NodeHierarchyCommandRequest.TIME_TO_VALUE_OPERATION_CATEGORY)) {
      
      timeoutThresholdSeconds = 120;
    }
    
    boolean isTimedOut = false;
    if (lockIdleTimeInSeconds > timeoutThresholdSeconds) {
      
      isTimedOut = true;
    }
    return isTimedOut;
  }
  
  @Override
  public Map<String, Integer> getParentIdentities() {
    
    Map<String, Integer> parentIdentities = new LinkedHashMap<>();
    parentIdentities.put("customerId", customerId);
    return parentIdentities;
  }
  
  @Override
  public String getNaturalIdentity() {

    return customerId.toString();
  }
  
  public static class Mapper implements DtoMapper<AbstractCustomerEntity, AsyncOperationLockEntity, AsyncOperationLockDto> {
    
    private static final Mapper INSTANCE = new Mapper();
    private Mapper() {
    }
    
    public static Mapper getInstance() {
      return INSTANCE;
    }
    
    public List<AsyncOperationLockDto> mapEntitiesToDtos(List<AsyncOperationLockEntity> entities) {
      
      List<AsyncOperationLockDto> list = new ArrayList<>();
      for (AsyncOperationLockEntity entity: entities) {
        list.add(mapEntityToDto(entity));  
      }
      return list;
    }
    
    @Override
    public AsyncOperationLockDto mapEntityToDto(AsyncOperationLockEntity entity) {
      
      AsyncOperationLockDto dto = AsyncOperationLockDto
          .builder()
          .withCustomerId(entity.getCustomerId())
          .withOwnerUuid(entity.getOwnerUuid())
          .withOperationCategory(entity.getOperationCategory())
          .withOperationType(entity.getOperationType())
          .withSubmittedBy(entity.getSubmittedBy())
          .withLockAcquisitionTime(AbstractEntity.formatTimestamp(entity.getLockAcquisitionTime()))
          .withLockUpdateTime(AbstractEntity.formatTimestamp(entity.getLockUpdateTime()))
          .build();
      return dto;
    }
    
    public List<AsyncOperationLockEntity> mapDtosToEntities(Integer customerId, List<AsyncOperationLockDto> dtos) {
      
      List<AsyncOperationLockEntity> list = new ArrayList<>();
      for (AsyncOperationLockDto dto: dtos) {
        list.add(mapDtoToEntity(customerId, dto));  
      }
      return list;
    }
    
    @Override
    public AsyncOperationLockEntity mapDtoToEntity(AbstractCustomerEntity parentCustomer, AsyncOperationLockDto dto) {
     
      return this.mapDtoToEntity(parentCustomer.getPersistentIdentity(), dto);
    }
    
    public AsyncOperationLockEntity mapDtoToEntity(Integer customerId, AsyncOperationLockDto dto) {
      
      AsyncOperationLockEntity entity = new AsyncOperationLockEntity(
         dto.getCustomerId(),
         dto.getOwnerUuid(),
         dto.getOperationCategory(),
         dto.getOperationType(),
         dto.getSubmittedBy(),
         AbstractEntity.parseTimestamp(dto.getLockAcquisitionTime()),
         AbstractEntity.parseTimestamp(dto.getLockUpdateTime()));        
      return entity;
    }
  }   
}
//@formatter:on