//@formatter:off
package com.djt.hvac.domain.model.report.dto;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = ReportInstanceStatusErrorMessageDto.Builder.class)
public class ReportInstanceStatusErrorMessageDto {
  private final Integer reportTemplateId;
  private final Integer buildingId;
  private final Integer equipmentId;
  private final Integer messageIndex;
  private final String messageText;
  private final Boolean noMatchOnType;
  private final Boolean noMatchOnTypeOrMetadata;
  private final List<Integer> errorMessages;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (ReportInstanceStatusErrorMessageDto reportInstanceStatusErrorMessageDto) {
    return new Builder(reportInstanceStatusErrorMessageDto);
  }

  private ReportInstanceStatusErrorMessageDto (Builder builder) {
    this.reportTemplateId = builder.reportTemplateId;
    this.buildingId = builder.buildingId;
    this.equipmentId = builder.equipmentId;
    this.messageIndex = builder.messageIndex;
    this.messageText = builder.messageText;
    this.noMatchOnType = builder.noMatchOnType;
    this.noMatchOnTypeOrMetadata = builder.noMatchOnTypeOrMetadata;
    this.errorMessages = builder.errorMessages;
  }

  public Integer getReportTemplateId() {
    return reportTemplateId;
  }

  public Integer getBuildingId() {
    return buildingId;
  }

  public Integer getEquipmentId() {
    return equipmentId;
  }

  public Integer getMessageIndex() {
    return messageIndex;
  }

  public String getMessageText() {
    return messageText;
  }

  public Boolean getNoMatchOnType() {
    return noMatchOnType;
  }

  public Boolean getNoMatchOnTypeOrMetadata() {
    return noMatchOnTypeOrMetadata;
  }
  
  public List<Integer> getErrorMessages() {
    return errorMessages;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((buildingId == null) ? 0 : buildingId.hashCode());
    result = prime * result + ((equipmentId == null) ? 0 : equipmentId.hashCode());
    result = prime * result + ((messageIndex == null) ? 0 : messageIndex.hashCode());
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
    ReportInstanceStatusErrorMessageDto other = (ReportInstanceStatusErrorMessageDto) obj;
    if (buildingId == null) {
      if (other.buildingId != null)
        return false;
    } else if (!buildingId.equals(other.buildingId))
      return false;
    if (equipmentId == null) {
      if (other.equipmentId != null)
        return false;
    } else if (!equipmentId.equals(other.equipmentId))
      return false;
    if (messageIndex == null) {
      if (other.messageIndex != null)
        return false;
    } else if (!messageIndex.equals(other.messageIndex))
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
        .append("ReportInstanceStatusErrorMessageDto [reportTemplateId=")
        .append(reportTemplateId)
        .append(", buildingId=")
        .append(buildingId)
        .append(", equipmentId=")
        .append(equipmentId)
        .append(", messageIndex=")
        .append(messageIndex)
        .append("]")
        .toString();
  }
  
  @JsonPOJOBuilder
  public static class Builder {
    private Integer reportTemplateId;
    private Integer buildingId;
    private Integer equipmentId;
    private Integer messageIndex;
    private String messageText;
    private Boolean noMatchOnType = Boolean.FALSE;
    private Boolean noMatchOnTypeOrMetadata = Boolean.FALSE;
    private List<Integer> errorMessages = new ArrayList<>();

    private Builder() {}

    private Builder(ReportInstanceStatusErrorMessageDto reportInstanceStatusErrorMessageDto) {
      requireNonNull(reportInstanceStatusErrorMessageDto, "reportInstanceStatusErrorMessageDto cannot be null");
      this.reportTemplateId = reportInstanceStatusErrorMessageDto.reportTemplateId;
      this.buildingId = reportInstanceStatusErrorMessageDto.buildingId;
      this.equipmentId = reportInstanceStatusErrorMessageDto.equipmentId;
      this.messageIndex = reportInstanceStatusErrorMessageDto.messageIndex;
      this.messageText = reportInstanceStatusErrorMessageDto.messageText;
      this.noMatchOnType = reportInstanceStatusErrorMessageDto.noMatchOnType;
      this.noMatchOnTypeOrMetadata = reportInstanceStatusErrorMessageDto.noMatchOnTypeOrMetadata;
      this.errorMessages = reportInstanceStatusErrorMessageDto.errorMessages;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
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

    public Builder withEquipmentId(Integer equipmentId) {
      requireNonNull(equipmentId, "equipmentId cannot be null");
      this.equipmentId = equipmentId;
      return this;
    }

    public Builder withMessageIndex(Integer messageIndex) {
      requireNonNull(messageIndex, "messageIndex cannot be null");
      this.messageIndex = messageIndex;
      return this;
    }

    public Builder withMessageText(String messageText) {
      requireNonNull(messageText, "messageText cannot be null");
      this.messageText = messageText;
      return this;
    }

    public Builder withNoMatchOnType(Boolean noMatchOnType) {
      requireNonNull(noMatchOnType, "noMatchOnType cannot be null");
      this.noMatchOnType = noMatchOnType;
      return this;
    }

    public Builder withNoMatchOnTypeOrMetadata(Boolean noMatchOnTypeOrMetadata) {
      requireNonNull(noMatchOnTypeOrMetadata, "noMatchOnTypeOrMetadata cannot be null");
      this.noMatchOnTypeOrMetadata = noMatchOnTypeOrMetadata;
      return this;
    }
    
    public Builder withErrorMessages(List<Integer> errorMessages) {
      requireNonNull(errorMessages, "errorMessages cannot be null");
      this.errorMessages = errorMessages;
      return this;
    }

    public ReportInstanceStatusErrorMessageDto build() {
      requireNonNull(reportTemplateId, "reportTemplateId cannot be null");
      requireNonNull(buildingId, "buildingId cannot be null");
      requireNonNull(equipmentId, "equipmentId cannot be null");
      requireNonNull(errorMessages, "errorMessages cannot be null");
      return new ReportInstanceStatusErrorMessageDto(this);
    }
  }
}
//@formatter:on