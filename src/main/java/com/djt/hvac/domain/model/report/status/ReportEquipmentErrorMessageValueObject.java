package com.djt.hvac.domain.model.report.status;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableList;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = ReportEquipmentErrorMessageValueObject.Builder.class)
public class ReportEquipmentErrorMessageValueObject {
  
  private final Integer equipmentId;
  private final String equipmentNodePath;
  private final List<String> errorMessages;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (ReportEquipmentErrorMessageValueObject reportEquipmentErrorMessageValueObject) {
    return new Builder(reportEquipmentErrorMessageValueObject);
  }

  private ReportEquipmentErrorMessageValueObject (Builder builder) {
    this.equipmentId = builder.equipmentId;
    this.equipmentNodePath = builder.equipmentNodePath;
    this.errorMessages = builder.errorMessages;
  }

  public Integer getEquipmentId() {
    return equipmentId;
  }

  public String getEquipmentNodePath() {
    return equipmentNodePath;
  }
  
  public List<String> getErrorMessages() {
    return errorMessages;
  }

  @Override
  public String toString() {
    StringBuilder builder2 = new StringBuilder();
    builder2.append("ReportEquipmentErrorMessageValueObject [equipmentId=").append(equipmentId)
        .append(", equipmentNodePath=").append(equipmentNodePath).append(", errorMessages=")
        .append(errorMessages).append("]");
    return builder2.toString();
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Integer equipmentId;
    private String equipmentNodePath;
    private List<String> errorMessages;

    private Builder() {}

    private Builder(ReportEquipmentErrorMessageValueObject reportEquipmentErrorMessageValueObject) {
      requireNonNull(reportEquipmentErrorMessageValueObject, "reportEquipmentStatusValueObject cannot be null");
      this.equipmentId = reportEquipmentErrorMessageValueObject.equipmentId;
      this.equipmentNodePath = reportEquipmentErrorMessageValueObject.equipmentNodePath;
      this.errorMessages = reportEquipmentErrorMessageValueObject.errorMessages;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withEquipmentId(Integer equipmentId) {
      requireNonNull(equipmentId, "equipmentId cannot be null");
      this.equipmentId = equipmentId;
      return this;
    }

    public Builder withEquipmentNodePath(String equipmentNodePath) {
      requireNonNull(equipmentNodePath, "equipmentNodePath cannot be null");
      this.equipmentNodePath = equipmentNodePath;
      return this;
    }
    
    public Builder withErrorMessages(List<String> errorMessages) {
      requireNonNull(errorMessages, "errorMessages cannot be null");
      if (errorMessages.isEmpty()) {
        throw new IllegalArgumentException("errorMessages cannot be empty");
      }
      this.errorMessages = ImmutableList.copyOf(errorMessages);
      return this;
    }

    public ReportEquipmentErrorMessageValueObject build() {
      requireNonNull(equipmentId, "equipmentId cannot be null");
      requireNonNull(equipmentNodePath, "equipmentNodePath cannot be null");
      requireNonNull(errorMessages, "errorMessages cannot be null");
      return new ReportEquipmentErrorMessageValueObject(this);
    }
  }
}