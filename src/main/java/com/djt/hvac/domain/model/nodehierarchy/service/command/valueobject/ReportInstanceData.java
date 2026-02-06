package com.djt.hvac.domain.model.nodehierarchy.service.command.valueobject;

import java.util.function.Consumer;

import com.djt.hvac.domain.model.dictionary.template.report.enums.ReportPriority;
import com.djt.hvac.domain.model.dictionary.template.report.enums.ReportState;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = ReportInstanceData.Builder.class)
public class ReportInstanceData {
  private final Integer buildingId;
  private final Integer reportTemplateId;
  private final String priority;
  private final String state;
  
  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (ReportInstanceData reportInstanceData) {
    return new Builder(reportInstanceData);
  }

  private ReportInstanceData (Builder builder) {
    this.buildingId = builder.buildingId;
    this.reportTemplateId = builder.reportTemplateId;
    this.priority = builder.priority;
    this.state = builder.state;
  }
  
  public Integer getBuildingId() {
    return buildingId;
  }

  public Integer getReportTemplateId() {
    return reportTemplateId;
  }

  public String getPriority() {
    return priority;
  }

  public String getState() {
    return state;
  }
  
  @Override
  public String toString() {
    return new StringBuilder()
        .append("ReportInstanceData [buildingId=")
        .append(buildingId)
        .append(", reportTemplateId=")
        .append(reportTemplateId)
        .append(", priority=")
        .append(priority)
        .append(", state=")
        .append(state)
        .append("]")
        .toString();
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Integer buildingId;
    private Integer reportTemplateId;
    private String priority;
    private String state;
    private Builder() {}

    private Builder(ReportInstanceData reportInstanceData) {
      requireNonNull(reportInstanceData, "reportInstanceData cannot be null");
      this.buildingId = reportInstanceData.buildingId;
      this.reportTemplateId = reportInstanceData.reportTemplateId;
      this.priority = reportInstanceData.priority;
      this.state = reportInstanceData.state;
    }

    public Builder with(Consumer<Builder> consumer) {
      consumer.accept(this);
      return this;
    }

    public Builder withBuildingId(Integer buildingId) {
      this.buildingId = buildingId;
      return this;
    }

    public Builder withReportTemplateId(Integer reportTemplateId) {
      this.reportTemplateId = reportTemplateId;
      return this;
    }

    public Builder withPriority(String priority) {
      if (priority != null) {
        if (!priority.equals(ReportPriority.LOW.toString()) 
            && !priority.equals(ReportPriority.MEDIUM.toString()) 
            && !priority.equals(ReportPriority.HIGH.toString())) {
          
          throw new IllegalArgumentException("Invalid priority value: ["
              + priority
              + "], supported values are: ['LOW', 'MEDIUM' or 'HIGH'].");
        }
        this.priority = priority;
      }
      return this;
    }

    public Builder withState(String state) {
      if (state != null) {
        if (!state.equals(ReportState.ENABLED.toString()) 
            && !state.equals(ReportState.DISABLED.toString()) 
            && !state.equals(ReportState.IGNORED.toString())) {
          
          throw new IllegalArgumentException("Invalid state value: ["
              + state
              + "], supported values are: ['ENABLED', 'DISABLED' or 'IGNORED'].");
        }
        this.state = state;
      }
      return this;
    }

    public ReportInstanceData build() {
      
      requireNonNull(buildingId, "'buildingId' cannot be null");
      requireNonNull(reportTemplateId, "'reportTemplateId' cannot be null");
      
      if (priority == null && state == null) {
        throw new IllegalArgumentException("One of the following must be set: 'priority' or 'state'");
      }
      return new ReportInstanceData(this);
    }
    
    private <T> T requireNonNull(T obj, String message) {
      if (obj ==  null) {
        throw new IllegalArgumentException(message);
      }
      return obj;
    }    
  }
}