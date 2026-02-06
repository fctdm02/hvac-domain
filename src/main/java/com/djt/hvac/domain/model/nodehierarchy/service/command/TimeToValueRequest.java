//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.service.command;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = TimeToValueRequest.Builder.class)
public class TimeToValueRequest extends AbstractNodeHierarchyCommandRequest {
  
  private static final long serialVersionUID = 1L;
  
  private final String operationType;
  private final String subject;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (TimeToValueRequest commandRequest) {
    return new Builder(commandRequest);
  }

  private TimeToValueRequest(Builder builder) {
    super(builder);
    this.operationType = builder.operationType;
    this.subject = builder.subject;
  }

  @Override
  public String getOperationCategory() {
    return NodeHierarchyCommandRequest.TIME_TO_VALUE_OPERATION_CATEGORY;
  }
  
  @Override
  public String getOperationType() {
    return operationType;
  }
  
  public String getSubject() {
    return subject;
  }
  
  public Integer getOperationId() {
    
    if (operationType.equals(NodeHierarchyCommandRequest.FAST_TRACK_CONNECTOR_MODEL_DATA)) {
      return Integer.valueOf(10);
    } else if (operationType.equals(NodeHierarchyCommandRequest.FAST_TRACK_CONNECTOR_TIME_SERIES_DATA)) {
      return Integer.valueOf(20);
    } else if (operationType.equals(NodeHierarchyCommandRequest.FAST_TRACK_AD_FUNCTION_TIME_SERIES_DATA)) {
      return Integer.valueOf(31);
    } else if (operationType.equals(NodeHierarchyCommandRequest.FAST_TRACK_CUSTOM_POINT_TIME_SERIES_DATA)) {
      return Integer.valueOf(32);
    } else if (operationType.equals(NodeHierarchyCommandRequest.FAST_TRACK_SCHEDULED_POINT_TIME_SERIES_DATA)) {
      return Integer.valueOf(41);
    } else if (operationType.equals(NodeHierarchyCommandRequest.FAST_TRACK_WEATHER_TIME_SERIES_DATA)) {
      return Integer.valueOf(42);
    } else if (operationType.equals(NodeHierarchyCommandRequest.BACK_FILL_CONNECTOR_TIME_SERIES_DATA)) {
      return Integer.valueOf(200);
    } else if (operationType.equals(NodeHierarchyCommandRequest.BACK_FILL_AD_FUNCTION_TIME_SERIES_DATA)) {
      return Integer.valueOf(310);
    } else if (operationType.equals(NodeHierarchyCommandRequest.BACK_FILL_CUSTOM_POINT_TIME_SERIES_DATA)) {
      return Integer.valueOf(320);
    } else if (operationType.equals(NodeHierarchyCommandRequest.BACK_FILL_SCHEDULED_POINT_TIME_SERIES_DATA)) {
      return Integer.valueOf(410);
    } else if (operationType.equals(NodeHierarchyCommandRequest.BACK_FILL_WEATHER_TIME_SERIES_DATA)) {
      return Integer.valueOf(420);
    }
    return Integer.valueOf(0);
  }

  public String getDescription() {
    
    if (operationType.equals(NodeHierarchyCommandRequest.FAST_TRACK_CONNECTOR_MODEL_DATA)) {
      return "FAST_TRACK_CONNECTOR_MODEL_DATA";
    } else if (operationType.equals(NodeHierarchyCommandRequest.FAST_TRACK_CONNECTOR_TIME_SERIES_DATA)) {
      return "FAST_TRACK_CONNECTOR_TIME_SERIES_DATA";
    } else if (operationType.equals(NodeHierarchyCommandRequest.FAST_TRACK_AD_FUNCTION_TIME_SERIES_DATA)) {
      return "FAST_TRACK_AD_FUNCTION_TIME_SERIES_DATA";
    } else if (operationType.equals(NodeHierarchyCommandRequest.FAST_TRACK_CUSTOM_POINT_TIME_SERIES_DATA)) {
      return "FAST_TRACK_CUSTOM_POINT_TIME_SERIES_DATA";
    } else if (operationType.equals(NodeHierarchyCommandRequest.FAST_TRACK_SCHEDULED_POINT_TIME_SERIES_DATA)) {
      return "BACK_FILL_SCHEDULED_POINT_TIME_SERIES_DATA";
    } else if (operationType.equals(NodeHierarchyCommandRequest.FAST_TRACK_WEATHER_TIME_SERIES_DATA)) {
      return "FAST_TRACK_WEATHER_TIME_SERIES_DATA";
    } else if (operationType.equals(NodeHierarchyCommandRequest.BACK_FILL_CONNECTOR_TIME_SERIES_DATA)) {
      return "BACK_FILL_CONNECTOR_TIME_SERIES_DATA";
    } else if (operationType.equals(NodeHierarchyCommandRequest.BACK_FILL_AD_FUNCTION_TIME_SERIES_DATA)) {
      return "BACK_FILL_AD_FUNCTION_TIME_SERIES_DATA";
    } else if (operationType.equals(NodeHierarchyCommandRequest.BACK_FILL_CUSTOM_POINT_TIME_SERIES_DATA)) {
      return "BACK_FILL_CUSTOM_POINT_TIME_SERIES_DATA";
    } else if (operationType.equals(NodeHierarchyCommandRequest.BACK_FILL_SCHEDULED_POINT_TIME_SERIES_DATA)) {
      return "BACK_FILL_SCHEDULED_POINT_TIME_SERIES_DATA";
    } else if (operationType.equals(NodeHierarchyCommandRequest.BACK_FILL_WEATHER_TIME_SERIES_DATA)) {
      return "BACK_FILL_WEATHER_TIME_SERIES_DATA";
    } 
    return "UNKNOWN";
  }

  @Override
  public String toString() {
    
    return new StringBuilder()
        .append(super.toString())
        .append(", operationType=")
        .append(operationType)
        .append(", operationId=")
        .append(getOperationId())
        .append(", subject=")
        .append(subject)
        .append(", description=")
        .append(getDescription())
        .append("]")
        .toString();
  }
    
  @JsonPOJOBuilder
  public static class Builder extends AbstractNodeHierarchyCommandRequest.Builder<TimeToValueRequest, Builder> {
    
    private String operationType;
    private String subject;

    private Builder() {}

    private Builder(TimeToValueRequest request) {
      requireNonNull(request, "request cannot be null");
      this.operationType = request.operationType;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withOperationType(String operationType) {
      requireNonNull(operationType, "operationType cannot be null");
      if (!operationType.equals(NodeHierarchyCommandRequest.FAST_TRACK_CONNECTOR_MODEL_DATA)
          
          && !operationType.equals(NodeHierarchyCommandRequest.FAST_TRACK_CONNECTOR_TIME_SERIES_DATA)
          && !operationType.equals(NodeHierarchyCommandRequest.FAST_TRACK_WEATHER_TIME_SERIES_DATA)
          && !operationType.equals(NodeHierarchyCommandRequest.FAST_TRACK_SCHEDULED_POINT_TIME_SERIES_DATA)
          && !operationType.equals(NodeHierarchyCommandRequest.FAST_TRACK_CUSTOM_POINT_TIME_SERIES_DATA)
          && !operationType.equals(NodeHierarchyCommandRequest.FAST_TRACK_AD_FUNCTION_TIME_SERIES_DATA)
          
          && !operationType.equals(NodeHierarchyCommandRequest.BACK_FILL_CONNECTOR_TIME_SERIES_DATA)
          && !operationType.equals(NodeHierarchyCommandRequest.BACK_FILL_WEATHER_TIME_SERIES_DATA)
          && !operationType.equals(NodeHierarchyCommandRequest.BACK_FILL_SCHEDULED_POINT_TIME_SERIES_DATA)
          && !operationType.equals(NodeHierarchyCommandRequest.BACK_FILL_CUSTOM_POINT_TIME_SERIES_DATA)
          && !operationType.equals(NodeHierarchyCommandRequest.BACK_FILL_AD_FUNCTION_TIME_SERIES_DATA)) {
        
        throw new IllegalArgumentException("Unsupported time to value operation type: ["
            + operationType
            +"]");
      }
      this.operationType = operationType;
      return this;
    }
    
    public Builder withSubject(String subject) {
      requireNonNull(subject, "subject cannot be null");
      this.subject = subject;
      return this;
    }    
    
    @Override
    protected Builder getThis() {
      return this;
    }

    @Override
    protected TimeToValueRequest newInstance() {
      requireNonNull(operationType, "operationType cannot be null");
      requireNonNull(subject, "subject cannot be null");
      return new TimeToValueRequest(this);
    }
  }
}
//@formatter:on