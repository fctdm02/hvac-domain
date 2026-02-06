//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service.command;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Consumer;

import com.djt.hvac.domain.model.dictionary.enums.DataType;
import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.MappablePointNodeData;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableList;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = UpdateMappablePointNodesRequest.Builder.class)
public class UpdateMappablePointNodesRequest extends AbstractNodeHierarchyCommandRequest {
  
  private static final long serialVersionUID = 1L;
  
  private final Boolean useGrouping;
  private final List<MappablePointNodeData> data;

  @JsonCreator
  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(UpdateMappablePointNodesRequest commandRequest) {
    return new Builder(commandRequest);
  }

  private UpdateMappablePointNodesRequest (Builder builder) {
    super(builder);
    this.useGrouping = builder.useGrouping;
    this.data = builder.data;
  }
  public Boolean getUseGrouping() {
    return useGrouping;
  }

  public List<MappablePointNodeData> getData() {
    return data;
  }

  @Override
  public String getOperationCategory() {
    return NodeHierarchyCommandRequest.BULK_POINT_OPERATION_CATEGORY;
  }
  
  @Override
  public String getOperationType() {
    return NodeHierarchyCommandRequest.UPDATE_OPERATION_TYPE;
  }
  
  @Override
  public String toString() {
    
    return new StringBuilder()
        .append(super.toString())
        .append(", useGrouping=")
        .append(useGrouping)
        .append(", data=")
        .append(data)
        .append("]")
        .toString();
  }
  
  @JsonPOJOBuilder
  public static class Builder extends AbstractNodeHierarchyCommandRequest.Builder<UpdateMappablePointNodesRequest, Builder> {
    
    private Boolean useGrouping;
    private List<MappablePointNodeData> data;

    private Builder() {}

    private Builder(UpdateMappablePointNodesRequest commandRequest) {
      requireNonNull(commandRequest, "commandRequest cannot be null");
      this.useGrouping = commandRequest.useGrouping;
      this.data = commandRequest.data;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withUseGrouping(Boolean useGrouping) {
      requireNonNull(useGrouping, "useGrouping cannot be null");
      this.useGrouping = useGrouping;
      return this;
    }

    public Builder withData(List<MappablePointNodeData> data) {
      requireNonNull(data, "data cannot be null");
      this.data = ImmutableList.copyOf(data);
      return this;
    }
    
    @Override
    protected Builder getThis() {
      return this;
    }

    @JsonIgnore
    private void checkRequiredField(Object object, String validationMessage) {
      if (object == null) {
        throw new IllegalArgumentException(validationMessage);
      }
    }
    
    @Override
    protected UpdateMappablePointNodesRequest newInstance() {
      requireNonNull(useGrouping, "'useGrouping' cannot be null");
      requireNonNull(data, "'data' cannot be null");
      
      if (useGrouping.booleanValue()) {
        for (MappablePointNodeData dto: data) {
          
          checkRequiredField(dto.getQuantity(), "'quantity' cannot be null for item: " + dto);
        }
      } else {
        for (MappablePointNodeData dto: data) {
          if (dto.getMetadataTags() == null) {
            Integer dataTypeId = dto.getPointDataTypeId();
            if (dataTypeId.equals(Integer.valueOf(DataType.BOOLEAN.getId())) 
                || dataTypeId.equals(Integer.valueOf(DataType.ENUM.getId()))) {
              
              checkRequiredField(dto.getRange(), "'range' cannot be null for item: " + dto);
            }
          }
        }
      }
      return new UpdateMappablePointNodesRequest(this);
    }
  }
}
//@formatter:on