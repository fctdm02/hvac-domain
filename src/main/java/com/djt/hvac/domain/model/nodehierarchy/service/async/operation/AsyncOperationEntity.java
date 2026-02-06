//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service.async.operation;

import static java.util.Objects.requireNonNull;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.mapper.DtoMapper;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.nodehierarchy.service.async.operation.dto.AsyncOperationDto;
import com.djt.hvac.domain.model.nodehierarchy.service.async.operation.enums.AsyncOperationStatus;
import com.djt.hvac.domain.model.nodehierarchy.service.async.operation.valueobject.AsyncOperationInfo;
import com.djt.hvac.domain.model.nodehierarchy.service.command.NodeHierarchyCommandRequest;

public class AsyncOperationEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  public static final String RESULT_SUCCESS = "Success";
  public static final String RESULT_FAILURE = "Failure";
  public static final String RESULT_PENDING = "Pending";
  
  public static final String REASON_NEVER_PROCESSED = "Job was never processed because of an unexpected system error";

  private final Integer customerId;
  private final String operationCategory;
  private final String operationType;
  private final String requestJson;
  private final String submittedBy;
  private final Timestamp submissionTime;
  private AsyncOperationStatus status = AsyncOperationStatus.CREATED;
  private String reason;
  private Timestamp endTime;
  
  public AsyncOperationEntity(
      Integer persistentIdentity,
      Integer customerId,
      String operationCategory,
      String operationType,
      String requestJson,
      String submittedBy,
      Timestamp submissionTime) {
    super(persistentIdentity);
    requireNonNull(persistentIdentity, "persistentIdentity cannot be null");
    requireNonNull(customerId, "customerId cannot be null");
    requireNonNull(operationCategory, "operationCategory cannot be null");
    requireNonNull(operationType, "operationType cannot be null");
    requireNonNull(submittedBy, "submittedBy cannot be null");
    requireNonNull(submissionTime, "submissionTime cannot be null");
    this.customerId = customerId;
    this.operationCategory = operationCategory;
    this.operationType = operationType;
    this.requestJson = requestJson;
    this.submittedBy = submittedBy;
    this.submissionTime = submissionTime;
  }

  public AsyncOperationEntity(
      Integer persistentIdentity,
      Integer customerId,
      String operationCategory,
      String operationType,
      String requestJson,
      String submittedBy,
      Timestamp submissionTime,
      AsyncOperationStatus status,
      String reason,
      Timestamp endTime) {
    super(persistentIdentity);
    requireNonNull(persistentIdentity, "persistentIdentity cannot be null");
    requireNonNull(customerId, "customerId cannot be null");
    requireNonNull(operationCategory, "operationCategory cannot be null");
    requireNonNull(operationType, "operationType cannot be null");
    requireNonNull(submittedBy, "submittedBy cannot be null");
    requireNonNull(submissionTime, "submissionTime cannot be null");
    requireNonNull(status, "status cannot be null");
    this.customerId = customerId;
    this.operationCategory = operationCategory;
    this.operationType = operationType;
    this.requestJson = requestJson;
    this.submittedBy = submittedBy;
    this.submissionTime = submissionTime;
    this.status = status;
    this.reason = reason;
    this.endTime = endTime;
  }
  
  public Integer getCustomerId() {
    return customerId;
  }

  public String getOperationCategory() {
    return operationCategory;
  }

  public String getOperationType() {
    return operationType;
  }

  public String getSubmittedBy() {
    return submittedBy;
  }

  public Timestamp getSubmissionTime() {
    return submissionTime;
  }
  
  public AsyncOperationStatus getStatus() {
    return status;
  }
  
  public String getReason() {
    return reason;
  }
  
  public void setDispatchedStatus() {
    
    if (status.equals(AsyncOperationStatus.CREATED)) {
      status = AsyncOperationStatus.DISPATCHED;
    } else {
      throw new IllegalStateException("Cannot set status to DISPATCHED from status: ["
          + status
          + "] for async operation: ["
          + this
          + "] with id: ["
          + this.getPersistentIdentity()
          + "].");
    }
  }

  public String getRequestJson() {
    return requestJson;
  }

  public Timestamp getEndTime() {
    return endTime;
  }

  public void updateForCompletedStatus(
      Timestamp endTime) {
    
    this.endTime = endTime;
    this.status = AsyncOperationStatus.COMPLETED;
  }

  public void updateForFailedStatus(
      String reason,
      Timestamp endTime) {

    this.reason = reason;
    this.endTime = endTime;
    this.status = AsyncOperationStatus.FAILED;
  }
  
  public boolean isComplete() {
    
    if (status.equals(AsyncOperationStatus.COMPLETED) 
        || status.equals(AsyncOperationStatus.FAILED)) {
      
      return true;
    }
    return isOperationTimedOut();
  }
  
  public boolean isOperationTimedOut() {

    long currentTimeMillis = AbstractEntity.getTimeKeeper().getCurrentTimeInMillis();
    long submissionTimeMillis = submissionTime.getTime();
    long durationMillis = currentTimeMillis - submissionTimeMillis;
    long durationMinutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis);
    
    int timeoutThresholdMinutes = 15;
    if (operationCategory.equals(NodeHierarchyCommandRequest.TIME_TO_VALUE_OPERATION_CATEGORY)) {
      
      timeoutThresholdMinutes = 2;
    }
    
    boolean isTimedOut = false;
    if (durationMinutes > timeoutThresholdMinutes) {
      
      isTimedOut = true;
    }
    return isTimedOut;
  }

  @Override
  public String getNaturalIdentity() {

    if (_naturalIdentity == null) {

      _naturalIdentity = new StringBuilder()
          .append(customerId.toString())
          .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
          .append(operationCategory)
          .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
          .append(operationType)
          .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
          .append(submittedBy)
          .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
          .append(submissionTime)
          .toString();
    }
    return _naturalIdentity;
  }
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
  }
  
  /*
   * @param asyncOperation The async operation to convert to the corresponding 
   *        async operation status value object
   * @param timezone The timezone (ruby label)       
   *        
   * @return The async operation status value object       
   */
  public static AsyncOperationInfo createAsyncOperationStatus(
      AsyncOperationEntity asyncOperation,
      String timezone) {
    
    return AsyncOperationInfo
        .builder()
        .withId(asyncOperation.getPersistentIdentity())
        .withCustomerId(asyncOperation.getCustomerId())
        .withOperationCategory(asyncOperation.getOperationCategory())
        .withOperationType(asyncOperation.getOperationType())
        .withStatus(asyncOperation.getStatus().toString())
        .withReason(asyncOperation.getReason())
        .withSubmittedBy(asyncOperation.getSubmittedBy())
        .withSubmissionTime(AbstractEntity
            .toDisplayFormattedZonedTime(
                asyncOperation.getSubmissionTime(), 
                timezone))
        .withIsComplete(asyncOperation.isComplete())
        .withCompletionTime(AbstractEntity
            .toDisplayFormattedZonedTime(
                asyncOperation.getEndTime(), 
                timezone))
        .build();
  }
  
  public static class Mapper implements DtoMapper<AbstractCustomerEntity, AsyncOperationEntity, AsyncOperationDto> {
    
    private static final Mapper INSTANCE = new Mapper();
    private Mapper() {
    }
    
    public static Mapper getInstance() {
      return INSTANCE;
    }
    
    public List<AsyncOperationDto> mapEntitiesToDtos(List<AsyncOperationEntity> entities) {
      
      List<AsyncOperationDto> list = new ArrayList<>();
      for (AsyncOperationEntity entity: entities) {
        list.add(mapEntityToDto(entity));  
      }
      return list;
    }
    
    @Override
    public AsyncOperationDto mapEntityToDto(AsyncOperationEntity entity) {
      
      return AsyncOperationDto
          .builder()
          .withId(entity.getPersistentIdentity())
          .withCustomerId(entity.getCustomerId())
          .withOperationCategory(entity.getOperationCategory())
          .withOperationType(entity.getOperationType())
          .withSubmittedBy(entity.getSubmittedBy())
          .withSubmissionTime(AbstractEntity.formatTimestamp(entity.getSubmissionTime()))
          .withStatus(entity.getStatus().toString())
          .withReason(entity.getReason())
          .withRequestJson(entity.getRequestJson())
          .withEndTime(AbstractEntity.formatTimestamp(entity.getEndTime()))
          .build();
    }
    
    public List<AsyncOperationEntity> mapDtosToEntities(Integer customerId, List<AsyncOperationDto> dtos) {
      
      List<AsyncOperationEntity> list = new ArrayList<>();
      for (AsyncOperationDto dto: dtos) {
        list.add(mapDtoToEntity(customerId, dto));  
      }
      return list;
    }
    
    @Override
    public AsyncOperationEntity mapDtoToEntity(AbstractCustomerEntity parentCustomer, AsyncOperationDto dto) {
     
      return this.mapDtoToEntity(parentCustomer.getPersistentIdentity(), dto);
    }
    
    public AsyncOperationEntity mapDtoToEntity(Integer customerId, AsyncOperationDto dto) {
      
      return new AsyncOperationEntity(
          dto.getId(),
          customerId,
          dto.getOperationCategory(),
          dto.getOperationType(),
          dto.getRequestJson(),
          dto.getSubmittedBy(),
          AbstractEntity.parseTimestamp(dto.getSubmissionTime()),
          AsyncOperationStatus.valueOf(dto.getStatus()),
          dto.getReason(),
          AbstractEntity.parseTimestamp(dto.getEndTime()));
    }
  }    
}
//@formatter:on
