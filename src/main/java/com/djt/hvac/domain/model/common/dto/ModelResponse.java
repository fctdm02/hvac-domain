package com.djt.hvac.domain.model.common.dto;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = ModelResponse.Builder.class)
public class ModelResponse {
  
  public static final String RESULT_SUCCESS = "Success";
  public static final String RESULT_FAILURE = "Failure";
  
  private final Integer persistentIdentity;
  private final String naturalIdentity;
  private final String result;
  private final String reason;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (ModelResponse modelResponse) {
    return new Builder(modelResponse);
  }

  private ModelResponse (Builder builder) {
    this.persistentIdentity = builder.persistentIdentity;
    this.naturalIdentity = builder.naturalIdentity;
    this.result = builder.result;
    this.reason = builder.reason;
  }

  public Integer getPersistentIdentity() {
    return persistentIdentity;
  }

  public String getNaturalIdentity() {
    return naturalIdentity;
  }

  public String getResult() {
    return result;
  }

  public String getReason() {
    return reason;
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Integer persistentIdentity;
    private String naturalIdentity;
    private String result;
    private String reason;

    private Builder() {}

    private Builder(ModelResponse modelResponse) {
      requireNonNull(modelResponse, "modelResponse cannot be null");
      this.persistentIdentity = modelResponse.persistentIdentity;
      this.naturalIdentity = modelResponse.naturalIdentity;
      this.result = modelResponse.result;
      this.reason = modelResponse.reason;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withPersistentIdentity(Integer persistentIdentity) {
      this.persistentIdentity = persistentIdentity;
      return this;
    }

    public Builder withNaturalIdentity(String naturalIdentity) {
      requireNonNull(naturalIdentity, "naturalIdentity cannot be null");
      this.naturalIdentity = naturalIdentity;
      return this;
    }

    public Builder withResult(String result) {
      requireNonNull(result, "result cannot be null");
      this.result = result;
      return this;
    }

    public Builder withReason(String reason) {
      this.reason = reason;
      return this;
    }

    public ModelResponse build() {
      requireNonNull(naturalIdentity, "naturalIdentity cannot be null");
      requireNonNull(result, "result cannot be null");
      return new ModelResponse(this);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((naturalIdentity == null) ? 0 : naturalIdentity.hashCode());
    result = prime * result + ((persistentIdentity == null) ? 0 : persistentIdentity.hashCode());
    result = prime * result + ((reason == null) ? 0 : reason.hashCode());
    result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
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
    ModelResponse other = (ModelResponse) obj;
    if (naturalIdentity == null) {
      if (other.naturalIdentity != null)
        return false;
    } else if (!naturalIdentity.equals(other.naturalIdentity))
      return false;
    if (persistentIdentity == null) {
      if (other.persistentIdentity != null)
        return false;
    } else if (!persistentIdentity.equals(other.persistentIdentity))
      return false;
    if (reason == null) {
      if (other.reason != null)
        return false;
    } else if (!reason.equals(other.reason))
      return false;
    if (result == null) {
      if (other.result != null)
        return false;
    } else if (!result.equals(other.result))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder2 = new StringBuilder();
    builder2.append("ModelResponse [persistentIdentity=").append(persistentIdentity)
        .append(", naturalIdentity=").append(naturalIdentity).append(", result=").append(result)
        .append(", reason=").append(reason).append("]");
    return builder2.toString();
  }
}