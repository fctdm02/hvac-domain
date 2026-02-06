//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.event;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.djt.hvac.domain.model.common.event.AbstractEvent;
import com.djt.hvac.domain.model.common.mapper.DtoMapper;
import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.nodehierarchy.event.dto.NodeHierarchyChangeEventDto;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableList;

/**
 *
 * @author tmyers
 *
 */
@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = NodeHierarchyChangeEvent.Builder.class)
public class NodeHierarchyChangeEvent extends AbstractEvent {

  private static final long serialVersionUID = 1L;

  public static final String CUSTOMER_ID_KEY = "customerId";
  public static final String PORTFOLIO_ID_KEY = "portfolioId";
  public static final String OPERATION_TYPE_KEY = "operationType";
  public static final String OPERATION_CATEGORY_KEY = "operationCategory";
  public static final String CREATED_NODE_IDS_KEY = "createdNodeIds";
  public static final String UPDATED_NODE_IDS_KEY = "updatedNodeIds";
  public static final String DELETED_NODE_IDS_KEY = "deletedNodeIds";
  public static final String ENABLED_AD_FUNCTION_INSTANCE_IDS_KEY = "enabledAdFunctionInstanceIds";
  public static final String ENABLED_REPORT_INSTANCE_IDS_KEY = "enabledReportInstanceIds";
  public static final String DISABLED_AD_FUNCTION_INSTANCE_IDS_KEY = "disabledAdFunctionInstanceIds";
  public static final String DISABLED_REPORT_INSTANCE_IDS_KEY = "disabledReportInstanceIds";
  
  private final Integer customerId;
  private final Integer portfolioId;
  private final String operationType;
  private final String operationCategory;
  private final List<Integer> createdNodeIds = new ArrayList<>();
  private final List<Integer> updatedNodeIds = new ArrayList<>();
  private final List<Integer> deletedNodeIds = new ArrayList<>();
  private final List<Integer> enabledAdFunctionInstanceIds = new ArrayList<>();
  private final List<Integer> enabledReportInstanceIds = new ArrayList<>();
  private final List<Integer> disabledAdFunctionInstanceIds = new ArrayList<>();
  private final List<Integer> disabledReportInstanceIds = new ArrayList<>();

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (NodeHierarchyChangeEvent event) {
    return new Builder(event);
  }

  private NodeHierarchyChangeEvent (Builder builder) {
    super(builder);
    
    this.customerId = builder.customerId;
    this.portfolioId = builder.portfolioId;
    this.operationType = builder.operationType;
    this.operationCategory = builder.operationCategory;
    
    if (builder.createdNodeIds != null) {
      this.createdNodeIds.addAll(builder.createdNodeIds);
    }

    if (builder.updatedNodeIds != null) {
      this.updatedNodeIds.addAll(builder.updatedNodeIds);
    }
    
    if (builder.deletedNodeIds != null) {
      this.deletedNodeIds.addAll(builder.deletedNodeIds);
    }
    
    if (builder.enabledAdFunctionInstanceIds != null) {
      this.enabledAdFunctionInstanceIds.addAll(builder.enabledAdFunctionInstanceIds);
    }
    
    if (builder.enabledReportInstanceIds != null) {
      this.enabledReportInstanceIds.addAll(builder.enabledReportInstanceIds);
    }
    
    if (builder.disabledAdFunctionInstanceIds != null) {
      this.disabledAdFunctionInstanceIds.addAll(builder.disabledAdFunctionInstanceIds);
    }
    
    if (builder.disabledReportInstanceIds != null) {
      this.disabledReportInstanceIds.addAll(builder.disabledReportInstanceIds);
    }
  }

  public Integer getCustomerId() {
    return customerId;
  }

  public Integer getPortfolioId() {
    return portfolioId;
  }

  public String getOperationType() {
    return operationType;
  }

  public String getOperationCategory() {
    return operationCategory;
  }

  public List<Integer> getCreatedNodeIds() {
    return createdNodeIds;
  }

  public List<Integer> getUpdatedNodeIds() {
    return updatedNodeIds;
  }

  public List<Integer> getDeletedNodeIds() {
    return deletedNodeIds;
  }

  public List<Integer> getEnabledAdFunctionInstanceIds() {
    return enabledAdFunctionInstanceIds;
  }

  public List<Integer> getEnabledReportInstanceIds() {
    return enabledReportInstanceIds;
  }

  public List<Integer> getDisabledAdFunctionInstanceIds() {
    return disabledAdFunctionInstanceIds;
  }

  public List<Integer> getDisabledReportInstanceIds() {
    return disabledReportInstanceIds;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((getEventUuid() == null) ? 0 : getEventUuid().hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    NodeHierarchyChangeEvent other = (NodeHierarchyChangeEvent) obj;
    if (getEventUuid() == null) {
      if (other.getEventUuid() != null)
        return false;
    } else if (!getEventUuid().equals(other.getEventUuid()))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("NodeHierarchyChangeEvent [customerId=").append(customerId)
        .append(", portfolioId=").append(portfolioId).append(", operationType=")
        .append(operationType).append(", operationCategory=").append(operationCategory)
        .append(", createdNodeIds=").append(createdNodeIds).append(", updatedNodeIds=")
        .append(updatedNodeIds).append(", deletedNodeIds=").append(deletedNodeIds)
        .append(", getEventUuid()=").append(getEventUuid()).append(", getOccurredOnDate()=")
        .append(getOccurredOnDate()).append(", getOwner()=").append(getOwner()).append("]");
    return builder.toString();
  }
  
  public static Object buildPayloadFromEventEntity(NodeHierarchyChangeEvent event) {
    
    Map<String, Object> payload = new LinkedHashMap<>();
    
    payload.put(AbstractEvent.EVENT_UUID_KEY, event.getEventUuid());
    payload.put(AbstractEvent.OCCURRED_ON_DATE_KEY, event.getOccurredOnDate());
    payload.put(AbstractEvent.OWNER_KEY, event.getOwner());
    payload.put(NodeHierarchyChangeEvent.CUSTOMER_ID_KEY, event.getCustomerId());
    payload.put(NodeHierarchyChangeEvent.PORTFOLIO_ID_KEY, event.getPortfolioId());
    payload.put(NodeHierarchyChangeEvent.OPERATION_TYPE_KEY, event.getOperationType());
    payload.put(NodeHierarchyChangeEvent.OPERATION_CATEGORY_KEY, event.getOperationCategory());
    payload.put(NodeHierarchyChangeEvent.CREATED_NODE_IDS_KEY, event.getCreatedNodeIds());
    payload.put(NodeHierarchyChangeEvent.UPDATED_NODE_IDS_KEY, event.getUpdatedNodeIds());
    payload.put(NodeHierarchyChangeEvent.DELETED_NODE_IDS_KEY, event.getDeletedNodeIds());
    payload.put(NodeHierarchyChangeEvent.ENABLED_AD_FUNCTION_INSTANCE_IDS_KEY, event.getEnabledAdFunctionInstanceIds());
    payload.put(NodeHierarchyChangeEvent.ENABLED_REPORT_INSTANCE_IDS_KEY, event.getEnabledReportInstanceIds());
    payload.put(NodeHierarchyChangeEvent.DISABLED_AD_FUNCTION_INSTANCE_IDS_KEY, event.getDisabledAdFunctionInstanceIds());
    payload.put(NodeHierarchyChangeEvent.DISABLED_REPORT_INSTANCE_IDS_KEY, event.getDisabledReportInstanceIds());
    
    return payload;
  }
  
  @JsonPOJOBuilder
  public static class Builder extends AbstractEvent.Builder<NodeHierarchyChangeEvent, Builder> {
    
    private Integer customerId;
    private Integer portfolioId;
    private String operationType;
    private String operationCategory;
    private List<Integer> createdNodeIds = new ArrayList<>();
    private List<Integer> updatedNodeIds = new ArrayList<>();
    private List<Integer> deletedNodeIds = new ArrayList<>();
    private List<Integer> enabledAdFunctionInstanceIds = new ArrayList<>();
    private List<Integer> enabledReportInstanceIds = new ArrayList<>();
    private List<Integer> disabledAdFunctionInstanceIds = new ArrayList<>();
    private List<Integer> disabledReportInstanceIds = new ArrayList<>();
    
    private Builder() {}

    private Builder(NodeHierarchyChangeEvent event) {
      requireNonNull(event, "event cannot be null");
      this.customerId = event.customerId;
      this.portfolioId = event.portfolioId;
      this.operationType = event.operationType;
      this.operationCategory = event.operationCategory;
      this.createdNodeIds = event.createdNodeIds;
      this.updatedNodeIds = event.updatedNodeIds;
      this.deletedNodeIds = event.deletedNodeIds;
      this.enabledAdFunctionInstanceIds = event.enabledAdFunctionInstanceIds;
      this.enabledReportInstanceIds = event.enabledReportInstanceIds;
      this.disabledAdFunctionInstanceIds = event.disabledAdFunctionInstanceIds;
      this.disabledReportInstanceIds = event.disabledReportInstanceIds;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withCustomerId(Integer customerId) {
      requireNonNull(customerId, "customerId cannot be null");
      this.customerId = customerId;
      return this;
    }

    public Builder withPortfolioId(Integer portfolioId) {
      requireNonNull(portfolioId, "portfolioId cannot be null");
      this.portfolioId = portfolioId;
      return this;
    }

    public Builder withOperationType(String operationType) {
      requireNonNull(operationType, "operationType cannot be null");
      this.operationType = operationType;
      return this;
    }

    public Builder withOperationCategory(String operationCategory) {
      requireNonNull(operationCategory, "operationCategory cannot be null");
      this.operationCategory = operationCategory;
      return this;
    }

    public Builder withCreatedNodeIds(List<Integer> createdNodeIds) {
      requireNonNull(createdNodeIds, "createdNodeIds cannot be null");
      this.createdNodeIds = ImmutableList.copyOf(createdNodeIds);
      return this;
    }

    public Builder withUpdatedNodeIds(List<Integer> updatedNodeIds) {
      requireNonNull(updatedNodeIds, "updatedNodeIds cannot be null");
      this.updatedNodeIds = ImmutableList.copyOf(updatedNodeIds);
      return this;
    }

    public Builder withDeletedNodeIds(List<Integer> deletedNodeIds) {
      requireNonNull(deletedNodeIds, "deletedNodeIds cannot be null");
      this.deletedNodeIds = ImmutableList.copyOf(deletedNodeIds);
      return this;
    }

    public Builder withEnabledAdFunctionInstanceIds(List<Integer> enabledAdFunctionInstanceIds) {
      requireNonNull(enabledAdFunctionInstanceIds, "enabledAdFunctionInstanceIds cannot be null");
      this.enabledAdFunctionInstanceIds = ImmutableList.copyOf(enabledAdFunctionInstanceIds);
      return this;
    }

    public Builder withEnabledReportInstanceIds(List<Integer> enabledReportInstanceIds) {
      requireNonNull(enabledReportInstanceIds, "enabledReportInstanceIds cannot be null");
      this.enabledReportInstanceIds = ImmutableList.copyOf(enabledReportInstanceIds);
      return this;
    }

    public Builder withDisabledAdFunctionInstanceIds(List<Integer> disabledAdFunctionInstanceIds) {
      requireNonNull(disabledAdFunctionInstanceIds, "disabledAdFunctionInstanceIds cannot be null");
      this.disabledAdFunctionInstanceIds = ImmutableList.copyOf(disabledAdFunctionInstanceIds);
      return this;
    }

    public Builder withDisabledReportInstanceIds(List<Integer> disabledReportInstanceIds) {
      requireNonNull(disabledReportInstanceIds, "disabledReportInstanceIds cannot be null");
      this.disabledReportInstanceIds = ImmutableList.copyOf(disabledReportInstanceIds);
      return this;
    }
    
    @Override
    protected Builder getThis() {
      return this;
    }

    @Override
    protected NodeHierarchyChangeEvent newInstance() {
      return new NodeHierarchyChangeEvent(this);
    }
  }
  
  public static class Mapper implements DtoMapper<AbstractCustomerEntity, NodeHierarchyChangeEvent, NodeHierarchyChangeEventDto> {
    
    private static final Mapper INSTANCE = new Mapper();
    private Mapper() {
    }
    
    public static Mapper getInstance() {
      return INSTANCE;
    }
    
    public List<NodeHierarchyChangeEventDto> mapEntitiesToDtos(List<NodeHierarchyChangeEvent> entities) {
      
      List<NodeHierarchyChangeEventDto> list = new ArrayList<>();
      for (NodeHierarchyChangeEvent entity: entities) {
        list.add(mapEntityToDto(entity));  
      }
      return list;
    }
    
    @Override
    public NodeHierarchyChangeEventDto mapEntityToDto(NodeHierarchyChangeEvent e) {
      
      return NodeHierarchyChangeEventDto
          .builder()
          .withEventUuid(e.getEventUuid())
          .withOccurredOnDate(e.getOccurredOnDate())
          .withOwner(e.getOwner())
          .withCustomerId(e.getCustomerId())
          .withPortfolioId(e.getPortfolioId())
          .withOperationType(e.getOperationType())
          .withOperationCategory(e.getOperationCategory())
          .withCreatedNodeIds(e.getCreatedNodeIds())
          .withUpdatedNodeIds(e.getUpdatedNodeIds())
          .withDeletedNodeIds(e.getDeletedNodeIds())
          .withEnabledAdFunctionInstanceIds(e.getEnabledAdFunctionInstanceIds())
          .withEnabledReportInstanceIds(e.getEnabledReportInstanceIds())
          .withDisabledAdFunctionInstanceIds(e.getDisabledAdFunctionInstanceIds())
          .withDisabledReportInstanceIds(e.getDisabledReportInstanceIds())
          .build();
    }
    
    public List<NodeHierarchyChangeEvent> mapDtosToEntities(Integer customerId, List<NodeHierarchyChangeEventDto> dtos) {
      
      List<NodeHierarchyChangeEvent> list = new ArrayList<>();
      for (NodeHierarchyChangeEventDto dto: dtos) {
        list.add(mapDtoToEntity(customerId, dto));  
      }
      return list;
    }
    
    @Override
    public NodeHierarchyChangeEvent mapDtoToEntity(AbstractCustomerEntity parentCustomer, NodeHierarchyChangeEventDto dto) {
     
      return this.mapDtoToEntity(parentCustomer.getPersistentIdentity(), dto);
    }
    
    public NodeHierarchyChangeEvent mapDtoToEntity(Integer customerId, NodeHierarchyChangeEventDto d) {
      
      return NodeHierarchyChangeEvent
          .builder()
          .withEventUuid(d.getEventUuid())
          .withOccurredOnDate(d.getOccurredOnDate())
          .withOwner(d.getOwner())
          .withCustomerId(d.getCustomerId())
          .withPortfolioId(d.getPortfolioId())
          .withOperationType(d.getOperationType())
          .withOperationCategory(d.getOperationCategory())
          .withCreatedNodeIds(d.getCreatedNodeIds())
          .withUpdatedNodeIds(d.getUpdatedNodeIds())
          .withDeletedNodeIds(d.getDeletedNodeIds())
          .withEnabledAdFunctionInstanceIds(d.getEnabledAdFunctionInstanceIds())
          .withEnabledReportInstanceIds(d.getEnabledReportInstanceIds())
          .withDisabledAdFunctionInstanceIds(d.getDisabledAdFunctionInstanceIds())
          .withDisabledReportInstanceIds(d.getDisabledReportInstanceIds())
          .build();
    }
  }    
}
//@formatter:on