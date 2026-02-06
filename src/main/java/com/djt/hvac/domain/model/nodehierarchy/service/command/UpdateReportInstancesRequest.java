package com.djt.hvac.domain.model.nodehierarchy.service.command;

import java.util.List;
import java.util.function.Consumer;

import com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject.ReportInstanceData;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableList;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = UpdateReportInstancesRequest.Builder.class)
public class UpdateReportInstancesRequest extends AbstractNodeHierarchyCommandRequest {
  
  private static final long serialVersionUID = 1L;
  
  private final List<ReportInstanceData> data;
  
  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (UpdateReportInstancesRequest commandRequest) {
    return new Builder(commandRequest);
  }
  
  private UpdateReportInstancesRequest(Builder builder) {
    super(builder);
    this.data = builder.data;
  }
  
  public List<ReportInstanceData> getData() {
    return data;
  }
  
  @Override
  public String getOperationCategory() {
    return NodeHierarchyCommandRequest.BULK_REPORT_INSTANCE_OPERATION_CATEGORY;
  }
  
  @Override
  public String getOperationType() {
    return NodeHierarchyCommandRequest.UPDATE_OPERATION_TYPE;
  }    
  
  @Override
  public String toString() {
    
    return new StringBuilder()
        .append(super.toString())
        .append(", data=")
        .append(data)
        .append("]")
        .toString();
  }
  
  public void validate() {
    requireNonNull(data, "'data' cannot be null");
  }
  
  @SuppressWarnings("rawtypes")
  private static <T> T requireNonNull(T obj, String message) {
    if (obj ==  null) {
      throw new IllegalArgumentException(message);
    }
    if (obj instanceof List) {
      if (((List)obj).isEmpty()) {
        throw new IllegalArgumentException(message);
      }
    }
    return obj;
  }

  @JsonPOJOBuilder
  public static class Builder extends AbstractNodeHierarchyCommandRequest.Builder<UpdateReportInstancesRequest, Builder> {
    
    private List<ReportInstanceData> data;

    private Builder() {}

    private Builder(UpdateReportInstancesRequest commandRequest) {
      requireNonNull(commandRequest, "commandRequest cannot be null");
      this.data = commandRequest.data;
    }

    public Builder with(Consumer<Builder> consumer) {
      consumer.accept(this);
      return this;
    }
    
    public Builder withData(List<ReportInstanceData> data) {
      requireNonNull(data, "data cannot be null");
      this.data = ImmutableList.copyOf(data);
      return this;
    }

    @Override
    protected Builder getThis() {
      return this;
    }

    @Override
    protected UpdateReportInstancesRequest newInstance() {
      requireNonNull(data, "data cannot be null or empty");
      return new UpdateReportInstancesRequest(this);
    }
  }
}