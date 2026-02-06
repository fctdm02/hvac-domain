//@formatter:off
package com.djt.hvac.domain.model.report.dto;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.djt.hvac.domain.model.dictionary.template.report.enums.ReportPriority;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = ReportInstanceStatusDto.Builder.class)
public class ReportInstanceStatusDto {
  
  private final Integer id;
  private final Integer reportTemplateId;
  private final Integer buildingId;
  private final String priority;
  private final Long createdAt;
  private final Long updatedAt;
  private final String candidateJson;
  private final List<ReportInstanceStatusErrorMessageDto> errorMessages;
  private final boolean stateHasChanged;
  private final boolean needsEnabling;
  private final boolean needsDisabling;
  private final boolean ignored;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (ReportInstanceStatusDto reportInstanceStatusDto) {
    return new Builder(reportInstanceStatusDto);
  }

  private ReportInstanceStatusDto (Builder builder) {
    this.id = builder.id;
    this.reportTemplateId = builder.reportTemplateId;
    this.buildingId = builder.buildingId;
    this.priority = builder.priority;
    this.createdAt = builder.createdAt;
    this.updatedAt = builder.updatedAt;
    this.candidateJson = builder.candidateJson;
    this.errorMessages = builder.errorMessages;
    this.stateHasChanged = builder.stateHasChanged;
    this.needsEnabling = builder.needsEnabling;
    this.needsDisabling = builder.needsDisabling;
    this.ignored = builder.ignored;
  }

  public Integer getId() {
    return id;
  }

  public Integer getReportTemplateId() {
    return reportTemplateId;
  }

  public Integer getBuildingId() {
    return buildingId;
  }

  public String getPriority() {
    return priority;
  }
  
  public Long getCreatedAt() {
    return createdAt;
  }

  public Long getUpdatedAt() {
    return updatedAt;
  }
  
  public String getCandidateJson() {
    return candidateJson;
  }
  
  public List<ReportInstanceStatusErrorMessageDto> getErrorMessages() {
    return errorMessages;
  }
  
  public boolean getStateHasChanged() {
    return stateHasChanged;
  }
  
  public boolean getNeedsEnabling() {
    return needsEnabling;
  }

  public boolean getNeedsDisabling() {
    return needsDisabling;
  }

  public boolean getIgnored() {
    return ignored;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((buildingId == null) ? 0 : buildingId.hashCode());
    result = prime * result + ((reportTemplateId == null) ? 0 : reportTemplateId.hashCode());
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
    
    ReportInstanceStatusDto other = (ReportInstanceStatusDto) obj;
    if (buildingId == null) {
      if (other.buildingId != null)
        return false;
    } else if (!buildingId.equals(other.buildingId))
      return false;
    
    if (reportTemplateId == null) {
      if (other.reportTemplateId != null)
        return false;
    } else if (!reportTemplateId.equals(other.reportTemplateId))
      return false;
    
    return true;
  }  
  
  @Override
  public String toString() {
    
    return new StringBuilder()
        .append("ReportInstanceStatusDto [reportTemplateId=")
        .append(reportTemplateId)
        .append(", buildingId=")
        .append(buildingId)
        .append("]")
        .toString();
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Integer id;
    private Integer reportTemplateId;
    private Integer buildingId;
    private String priority;
    private Long createdAt;
    private Long updatedAt;
    private String candidateJson;
    private List<ReportInstanceStatusErrorMessageDto> errorMessages = new ArrayList<>();
    private boolean stateHasChanged;
    private boolean needsEnabling;
    private boolean needsDisabling;
    private boolean ignored = false;

    private Builder() {}

    private Builder(ReportInstanceStatusDto reportInstanceStatusDto) {
      requireNonNull(reportInstanceStatusDto, "reportCandidateDto cannot be null");
      this.id = reportInstanceStatusDto.id;
      this.reportTemplateId = reportInstanceStatusDto.reportTemplateId;
      this.buildingId = reportInstanceStatusDto.buildingId;
      this.priority = reportInstanceStatusDto.priority;
      this.createdAt = reportInstanceStatusDto.createdAt;
      this.updatedAt = reportInstanceStatusDto.updatedAt;
      this.candidateJson = reportInstanceStatusDto.candidateJson;
      this.errorMessages = reportInstanceStatusDto.errorMessages;
      this.stateHasChanged = reportInstanceStatusDto.stateHasChanged;
      this.needsEnabling = reportInstanceStatusDto.needsEnabling;
      this.needsDisabling = reportInstanceStatusDto.needsDisabling;
      this.ignored = reportInstanceStatusDto.ignored;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withId(Integer id) {
      this.id = id;
      return this;
    }

    public Builder withReportTemplateId(Integer reportTemplateId) {
      requireNonNull(reportTemplateId, "reportTemplateId cannot be null");
      this.reportTemplateId = reportTemplateId;
      return this;
    }

    public Builder withBuildingId(Integer buildingId) {
      requireNonNull(buildingId, "buildingId cannot be null");
      this.buildingId = buildingId;
      return this;
    }

    public Builder withPriority(String priority) {
      if (priority == null || priority.trim().isEmpty()) {
        priority = ReportPriority.LOW.toString();
      }
      this.priority = priority;
      return this;
    }
    
    public Builder withCreatedAt(Long createdAt) {
      requireNonNull(createdAt, "createdAt cannot be null");
      this.createdAt = createdAt;
      return this;
    }

    public Builder withUpdatedAt(Long updatedAt) {
      requireNonNull(updatedAt, "updatedAt cannot be null");
      this.updatedAt = updatedAt;
      return this;
    }

    public Builder withCandidateJson(String candidateJson) {
      requireNonNull(candidateJson, "candidateJson cannot be null");
      this.candidateJson = candidateJson;
      return this;
    }
    
    public Builder withErrorMessages(List<ReportInstanceStatusErrorMessageDto> errorMessages) {
      if (errorMessages != null) {
        this.errorMessages = errorMessages;  
      }
      return this;
    }

    public Builder withStateHasChanged(boolean stateHasChanged) {
      this.stateHasChanged = stateHasChanged;
      return this;
    }
    
    public Builder withNeedsEnabling(boolean needsEnabling) {
      this.needsEnabling = needsEnabling;
      return this;
    }

    public Builder withNeedsDisabling(boolean needsDisabling) {
      this.needsDisabling = needsDisabling;
      return this;
    }

    public Builder withIgnored(boolean ignored) {
      this.ignored = ignored;
      return this;
    }
    
    public ReportInstanceStatusDto build() {
      requireNonNull(reportTemplateId, "reportTemplateId cannot be null");
      requireNonNull(buildingId, "buildingId cannot be null");
      requireNonNull(createdAt, "createdAt cannot be null");
      requireNonNull(updatedAt, "updatedAt cannot be null");
      requireNonNull(candidateJson, "candidateJson cannot be null");
      return new ReportInstanceStatusDto(this);
    }
  }
}
//@formatter:on