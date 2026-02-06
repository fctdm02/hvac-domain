package com.djt.hvac.domain.model.nodehierarchy.service.command;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import com.djt.hvac.domain.model.common.service.command.AggregateRootCommandResponse;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = NodeHierarchyCommandResponse.Builder.class)
public class NodeHierarchyCommandResponse implements AggregateRootCommandResponse {
  
  private static final long serialVersionUID = 1L;
  
  private final Boolean result;
  private final String reason;
  private final Object responseObject;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (NodeHierarchyCommandResponse nodeHierarchyCommandResponse) {
    return new Builder(nodeHierarchyCommandResponse);
  }

  private NodeHierarchyCommandResponse (Builder builder) {
    this.result = builder.result;
    this.reason = builder.reason;
    this.responseObject = builder.responseObject;
  }

  public Boolean getResult() {
    return result;
  }

  public String getReason() {
    return reason;
  }

  public Object getResponseObject() {
    return responseObject;
  }

  @Override
  public String toString() {
    StringBuilder builder2 = new StringBuilder();
    builder2.append("NodeHierarchyCommandResponse [result=").append(result).append(", reason=")
        .append(reason).append(", responseObject=").append(responseObject).append("]");
    return builder2.toString();
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Boolean result;
    private String reason;
    private Object responseObject;

    private Builder() {}

    private Builder(NodeHierarchyCommandResponse nodeHierarchyCommandResponse) {
      requireNonNull(nodeHierarchyCommandResponse, "nodeHierarchyCommandResponse cannot be null");
      this.result = nodeHierarchyCommandResponse.result;
      this.reason = nodeHierarchyCommandResponse.reason;
      this.responseObject = nodeHierarchyCommandResponse.responseObject;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withResult(Boolean result) {
      requireNonNull(result, "result cannot be null");
      this.result = result;
      return this;
    }

    public Builder withReason(String reason) {
      this.reason = reason;
      return this;
    }

    public Builder withResponseObject(Object responseObject) {
      requireNonNull(responseObject, "responseObject cannot be null");
      this.responseObject = responseObject;
      return this;
    }

    public NodeHierarchyCommandResponse build() {
      requireNonNull(result, "result cannot be null");
      requireNonNull(responseObject, "responseObject cannot be null");
      return new NodeHierarchyCommandResponse(this);
    }
  }
}