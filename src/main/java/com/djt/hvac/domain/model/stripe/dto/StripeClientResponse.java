package com.djt.hvac.domain.model.stripe.dto;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.util.Map;
import com.google.common.collect.ImmutableMap;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = StripeClientResponse.Builder.class)
public class StripeClientResponse {
  
  public static final String RESULT_SUCCESS = "SUCCESS";
  public static final String RESULT_FAILURE = "FAILURE";
  
  public static final StripeClientResponse STRIPE_CLIENT_SUCCESS_OBJECT = StripeClientResponse
      .builder()
      .withResult(RESULT_SUCCESS)
      .build();
  
  public static final StripeClientResponse buildSuccessResponse() {
    
    return STRIPE_CLIENT_SUCCESS_OBJECT;    
  }

  public static final StripeClientResponse buildSuccessResponse(Map<String, Object> responseObjects) {
    
    return StripeClientResponse
        .builder()
        .withResult(RESULT_SUCCESS)
        .withResponseObjects(responseObjects)
        .build();    
  }
  
  public static final StripeClientResponse buildFailureResponse(String reason) {
    
    return StripeClientResponse
        .builder()
        .withResult(RESULT_FAILURE)
        .withReason(reason)
        .build();    
  }

  public static final StripeClientResponse buildFailureResponse(String reason, Map<String, Object> responseObjects) {
    
    return StripeClientResponse
        .builder()
        .withResult(RESULT_FAILURE)
        .withReason(reason)
        .withResponseObjects(responseObjects)
        .build();    
  }
  
  private final String result;
  private final Map<String, Object> responseObjects;
  private final String reason;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (StripeClientResponse stripeClientResponse) {
    return new Builder(stripeClientResponse);
  }

  private StripeClientResponse (Builder builder) {
    this.result = builder.result;
    this.responseObjects = builder.responseObjects;
    this.reason = builder.reason;
  }

  public String getResult() {
    return result;
  }

  public Map<String, Object> getResponseObjects() {
    return responseObjects;
  }

  public String getReason() {
    return reason;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((reason == null) ? 0 : reason.hashCode());
    result = prime * result + ((responseObjects == null) ? 0 : responseObjects.hashCode());
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
    StripeClientResponse other = (StripeClientResponse) obj;
    if (reason == null) {
      if (other.reason != null)
        return false;
    } else if (!reason.equals(other.reason))
      return false;
    if (responseObjects == null) {
      if (other.responseObjects != null)
        return false;
    } else if (!responseObjects.equals(other.responseObjects))
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
    builder2.append("StripeClientResponse [result=").append(result).append(", responseObjects=")
        .append(responseObjects).append(", reason=").append(reason).append("]");
    return builder2.toString();
  }
  
  @JsonPOJOBuilder
  public static class Builder {
    private String result;
    private Map<String, Object> responseObjects;
    private String reason;

    private Builder() {}

    private Builder(StripeClientResponse stripeClientResponse) {
      requireNonNull(stripeClientResponse, "stripeClientResponse cannot be null");
      this.result = stripeClientResponse.result;
      this.responseObjects = stripeClientResponse.responseObjects;
      this.reason = stripeClientResponse.reason;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withResult(String result) {
      requireNonNull(result, "result cannot be null");
      this.result = result;
      return this;
    }

    public Builder withResponseObjects(Map<String, Object> responseObjects) {
      this.responseObjects = ImmutableMap.copyOf(responseObjects);
      return this;
    }

    public Builder withReason(String reason) {
      this.reason = reason;
      return this;
    }

    public StripeClientResponse build() {
      requireNonNull(result, "result cannot be null");
      return new StripeClientResponse(this);
    }
  }
}
