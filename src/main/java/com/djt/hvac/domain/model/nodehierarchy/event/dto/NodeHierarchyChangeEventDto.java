package com.djt.hvac.domain.model.nodehierarchy.event.dto;

import static java.util.Objects.requireNonNull;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableList;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = NodeHierarchyChangeEventDto.Builder.class)
public class NodeHierarchyChangeEventDto {
  private final UUID eventUuid;
  private final Timestamp occurredOnDate;
  private final String owner;
  private final Integer customerId;
  private final Integer portfolioId;
  private final String operationType;
  private final String operationCategory;
  private final List<Integer> createdNodeIds;
  private final List<Integer> updatedNodeIds;
  private final List<Integer> deletedNodeIds;
  private final List<Integer> enabledAdFunctionInstanceIds;
  private final List<Integer> enabledReportInstanceIds;
  private final List<Integer> disabledAdFunctionInstanceIds;
  private final List<Integer> disabledReportInstanceIds;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (NodeHierarchyChangeEventDto nodeHierarchyChangeEventDto) {
    return new Builder(nodeHierarchyChangeEventDto);
  }

  private NodeHierarchyChangeEventDto (Builder builder) {
    this.eventUuid = builder.eventUuid;
    this.occurredOnDate = builder.occurredOnDate;
    this.owner = builder.owner;
    this.customerId = builder.customerId;
    this.portfolioId = builder.portfolioId;
    this.operationType = builder.operationType;
    this.operationCategory = builder.operationCategory;
    this.createdNodeIds = builder.createdNodeIds;
    this.updatedNodeIds = builder.updatedNodeIds;
    this.deletedNodeIds = builder.deletedNodeIds;
    this.enabledAdFunctionInstanceIds = builder.enabledAdFunctionInstanceIds;
    this.enabledReportInstanceIds = builder.enabledReportInstanceIds;
    this.disabledAdFunctionInstanceIds = builder.disabledAdFunctionInstanceIds;
    this.disabledReportInstanceIds = builder.disabledReportInstanceIds;
  }

  public UUID getEventUuid() {
    return eventUuid;
  }

  public Timestamp getOccurredOnDate() {
    return occurredOnDate;
  }

  public String getOwner() {
    return owner;
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
    int result = 1;
    result = prime * result + ((createdNodeIds == null) ? 0 : createdNodeIds.hashCode());
    result = prime * result + ((customerId == null) ? 0 : customerId.hashCode());
    result = prime * result + ((deletedNodeIds == null) ? 0 : deletedNodeIds.hashCode());
    result = prime * result
        + ((disabledAdFunctionInstanceIds == null) ? 0 : disabledAdFunctionInstanceIds.hashCode());
    result = prime * result
        + ((disabledReportInstanceIds == null) ? 0 : disabledReportInstanceIds.hashCode());
    result = prime * result
        + ((enabledAdFunctionInstanceIds == null) ? 0 : enabledAdFunctionInstanceIds.hashCode());
    result = prime * result
        + ((enabledReportInstanceIds == null) ? 0 : enabledReportInstanceIds.hashCode());
    result = prime * result + ((eventUuid == null) ? 0 : eventUuid.hashCode());
    result = prime * result + ((occurredOnDate == null) ? 0 : occurredOnDate.hashCode());
    result = prime * result + ((operationCategory == null) ? 0 : operationCategory.hashCode());
    result = prime * result + ((operationType == null) ? 0 : operationType.hashCode());
    result = prime * result + ((owner == null) ? 0 : owner.hashCode());
    result = prime * result + ((portfolioId == null) ? 0 : portfolioId.hashCode());
    result = prime * result + ((updatedNodeIds == null) ? 0 : updatedNodeIds.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    NodeHierarchyChangeEventDto other = (NodeHierarchyChangeEventDto) obj;
    if (createdNodeIds == null) {
      if (other.createdNodeIds != null)
        return false;
    } else if (!createdNodeIds.equals(other.createdNodeIds))
      return false;
    if (customerId == null) {
      if (other.customerId != null)
        return false;
    } else if (!customerId.equals(other.customerId))
      return false;
    if (deletedNodeIds == null) {
      if (other.deletedNodeIds != null)
        return false;
    } else if (!deletedNodeIds.equals(other.deletedNodeIds))
      return false;
    if (disabledAdFunctionInstanceIds == null) {
      if (other.disabledAdFunctionInstanceIds != null)
        return false;
    } else if (!disabledAdFunctionInstanceIds.equals(other.disabledAdFunctionInstanceIds))
      return false;
    if (disabledReportInstanceIds == null) {
      if (other.disabledReportInstanceIds != null)
        return false;
    } else if (!disabledReportInstanceIds.equals(other.disabledReportInstanceIds))
      return false;
    if (enabledAdFunctionInstanceIds == null) {
      if (other.enabledAdFunctionInstanceIds != null)
        return false;
    } else if (!enabledAdFunctionInstanceIds.equals(other.enabledAdFunctionInstanceIds))
      return false;
    if (enabledReportInstanceIds == null) {
      if (other.enabledReportInstanceIds != null)
        return false;
    } else if (!enabledReportInstanceIds.equals(other.enabledReportInstanceIds))
      return false;
    if (eventUuid == null) {
      if (other.eventUuid != null)
        return false;
    } else if (!eventUuid.equals(other.eventUuid))
      return false;
    if (occurredOnDate == null) {
      if (other.occurredOnDate != null)
        return false;
    } else if (!occurredOnDate.equals(other.occurredOnDate))
      return false;
    if (operationCategory == null) {
      if (other.operationCategory != null)
        return false;
    } else if (!operationCategory.equals(other.operationCategory))
      return false;
    if (operationType == null) {
      if (other.operationType != null)
        return false;
    } else if (!operationType.equals(other.operationType))
      return false;
    if (owner == null) {
      if (other.owner != null)
        return false;
    } else if (!owner.equals(other.owner))
      return false;
    if (portfolioId == null) {
      if (other.portfolioId != null)
        return false;
    } else if (!portfolioId.equals(other.portfolioId))
      return false;
    if (updatedNodeIds == null) {
      if (other.updatedNodeIds != null)
        return false;
    } else if (!updatedNodeIds.equals(other.updatedNodeIds))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder2 = new StringBuilder();
    builder2.append("NodeHierarchyChangeEventDto [eventUuid=").append(eventUuid)
        .append(", occurredOnDate=").append(occurredOnDate).append(", owner=").append(owner)
        .append(", customerId=").append(customerId).append(", portfolioId=").append(portfolioId)
        .append(", operationType=").append(operationType).append(", operationCategory=")
        .append(operationCategory).append(", createdNodeIds=").append(createdNodeIds)
        .append(", updatedNodeIds=").append(updatedNodeIds).append(", deletedNodeIds=")
        .append(deletedNodeIds).append(", enabledAdFunctionInstanceIds=")
        .append(enabledAdFunctionInstanceIds).append(", enabledReportInstanceIds=")
        .append(enabledReportInstanceIds).append(", disabledAdFunctionInstanceIds=")
        .append(disabledAdFunctionInstanceIds).append(", disabledReportInstanceIds=")
        .append(disabledReportInstanceIds).append("]");
    return builder2.toString();
  }

  @JsonPOJOBuilder
  public static class Builder {
    private UUID eventUuid;
    private Timestamp occurredOnDate;
    private String owner;
    private Integer customerId;
    private Integer portfolioId;
    private String operationType;
    private String operationCategory;
    private List<Integer> createdNodeIds;
    private List<Integer> updatedNodeIds;
    private List<Integer> deletedNodeIds;
    private List<Integer> enabledAdFunctionInstanceIds;
    private List<Integer> enabledReportInstanceIds;
    private List<Integer> disabledAdFunctionInstanceIds;
    private List<Integer> disabledReportInstanceIds;

    private Builder() {}

    private Builder(NodeHierarchyChangeEventDto nodeHierarchyChangeEventDto) {
      requireNonNull(nodeHierarchyChangeEventDto, "nodeHierarchyChangeEventDto cannot be null");
      this.eventUuid = nodeHierarchyChangeEventDto.eventUuid;
      this.occurredOnDate = nodeHierarchyChangeEventDto.occurredOnDate;
      this.owner = nodeHierarchyChangeEventDto.owner;
      this.customerId = nodeHierarchyChangeEventDto.customerId;
      this.portfolioId = nodeHierarchyChangeEventDto.portfolioId;
      this.operationType = nodeHierarchyChangeEventDto.operationType;
      this.operationCategory = nodeHierarchyChangeEventDto.operationCategory;
      this.createdNodeIds = nodeHierarchyChangeEventDto.createdNodeIds;
      this.updatedNodeIds = nodeHierarchyChangeEventDto.updatedNodeIds;
      this.deletedNodeIds = nodeHierarchyChangeEventDto.deletedNodeIds;
      this.enabledAdFunctionInstanceIds = nodeHierarchyChangeEventDto.enabledAdFunctionInstanceIds;
      this.enabledReportInstanceIds = nodeHierarchyChangeEventDto.enabledReportInstanceIds;
      this.disabledAdFunctionInstanceIds = nodeHierarchyChangeEventDto.disabledAdFunctionInstanceIds;
      this.disabledReportInstanceIds = nodeHierarchyChangeEventDto.disabledReportInstanceIds;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withEventUuid(UUID eventUuid) {
      requireNonNull(eventUuid, "eventUuid cannot be null");
      this.eventUuid = eventUuid;
      return this;
    }

    public Builder withOccurredOnDate(Timestamp occurredOnDate) {
      requireNonNull(occurredOnDate, "occurredOnDate cannot be null");
      this.occurredOnDate = occurredOnDate;
      return this;
    }

    public Builder withOwner(String owner) {
      requireNonNull(owner, "owner cannot be null");
      this.owner = owner;
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

    public NodeHierarchyChangeEventDto build() {
      requireNonNull(eventUuid, "eventUuid cannot be null");
      requireNonNull(occurredOnDate, "occurredOnDate cannot be null");
      requireNonNull(owner, "owner cannot be null");
      requireNonNull(customerId, "customerId cannot be null");
      requireNonNull(portfolioId, "portfolioId cannot be null");
      requireNonNull(operationType, "operationType cannot be null");
      requireNonNull(operationCategory, "operationCategory cannot be null");
      requireNonNull(createdNodeIds, "createdNodeIds cannot be null");
      requireNonNull(updatedNodeIds, "updatedNodeIds cannot be null");
      requireNonNull(deletedNodeIds, "deletedNodeIds cannot be null");
      requireNonNull(enabledAdFunctionInstanceIds, "enabledAdFunctionInstanceIds cannot be null");
      requireNonNull(enabledReportInstanceIds, "enabledReportInstanceIds cannot be null");
      requireNonNull(disabledAdFunctionInstanceIds, "disabledAdFunctionInstanceIds cannot be null");
      requireNonNull(disabledReportInstanceIds, "disabledReportInstanceIds cannot be null");
      return new NodeHierarchyChangeEventDto(this);
    }
  }
}