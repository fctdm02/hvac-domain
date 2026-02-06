//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service.command;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.function.Consumer;

import com.djt.hvac.domain.model.dictionary.enums.NodeType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableMap;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = CreateNodeRequest.Builder.class)
public class CreateNodeRequest extends AbstractNodeHierarchyCommandRequest {
  
  private static final long serialVersionUID = 1L;
  
  public static final Integer CHILLED_WATER_SYSTEM_TYPE_ID = Integer.valueOf(1);
  public static final Integer HOT_WATER_SYSTEM_TYPE_ID = Integer.valueOf(2);
  public static final Integer STEAM_SYSTEM_TYPE_ID = Integer.valueOf(3);
  public static final Integer AIR_SUPPLY_SYSTEM_TYPE_ID = Integer.valueOf(4);
  
  private final NodeType nodeType;
  private final Integer parentId;
  private final String name;
  private final String displayName;
  private final Map<String, Object> additionalProperties;

  @JsonCreator
  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(CreateNodeRequest createNodeRequest) {
    return new Builder(createNodeRequest);
  }

  private CreateNodeRequest(Builder builder) {
    super(builder);
    this.nodeType = builder.nodeType;
    this.parentId = builder.parentId;
    this.name = builder.name;
    this.displayName = builder.displayName;
    this.additionalProperties = builder.additionalProperties;
  }

  public NodeType getNodeType() {
    return nodeType;
  }

  public Integer getParentId() {
    return parentId;
  }

  public String getName() {
    return name;
  }

  public String getDisplayName() {
    return displayName;
  }

  public Map<String, Object> getAdditionalProperties() {
    return additionalProperties;
  }

  @Override
  public String getOperationCategory() {
    return NodeHierarchyCommandRequest.BULK_NODE_OPERATION_CATEGORY;
  }
  
  @Override
  public String getOperationType() {
    return NodeHierarchyCommandRequest.CREATE_OPERATION_TYPE;
  }
  
  @Override
  public String toString() {
    
    return new StringBuilder()
        .append(super.toString())
        .append(", nodeType=")
        .append(nodeType)
        .append(", parentId=")
        .append(parentId)
        .append(", name=")
        .append(name)
        .append(", displayName=")
        .append(displayName)
        .append(", additionalProperties=")
        .append(additionalProperties)
        .append("]")
        .toString();
  }
  
  @JsonPOJOBuilder
  public static class Builder extends AbstractNodeHierarchyCommandRequest.Builder<CreateNodeRequest, Builder> {
    
    private NodeType nodeType;
    private Integer parentId;
    private String name;
    private String displayName;
    private Map<String, Object> additionalProperties;

    private Builder() {}

    private Builder(CreateNodeRequest createNodeRequest) {
      requireNonNull(createNodeRequest, "createNodeRequest cannot be null");
      this.nodeType = createNodeRequest.nodeType;
      this.parentId = createNodeRequest.parentId;
      this.name = createNodeRequest.name;
      this.displayName = createNodeRequest.displayName;
      this.additionalProperties = createNodeRequest.additionalProperties;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withNodeType(NodeType nodeType) {
      requireNonNull(nodeType, "nodeType cannot be null");
      this.nodeType = nodeType;
      return this;
    }

    public Builder withParentId(Integer parentId) {
      this.parentId = parentId;
      return this;
    }

    public Builder withName(String name) {
      requireNonNull(name, "name cannot be null");
      this.name = name;
      return this;
    }

    public Builder withDisplayName(String displayName) {
      this.displayName = displayName;
      return this;
    }

    public Builder withAdditionalProperties(Map<String, Object> additionalProperties) {
      if (additionalProperties != null) {
        this.additionalProperties = ImmutableMap.copyOf(additionalProperties);  
      }
      return this;
    }

    @Override
    protected Builder getThis() {
      return this;
    }

    @Override
    protected CreateNodeRequest newInstance() {
      requireNonNull(nodeType, "nodeType cannot be null");
      requireNonNull(name, "name cannot be null");
      return new CreateNodeRequest(this);
    }
  }
}
//@formatter:on