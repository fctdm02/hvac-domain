package com.djt.hvac.domain.model.report.status;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = ReportStatusValueObject.Builder.class)
public class ReportStatusValueObject implements Comparable<ReportStatusValueObject> {
  
  public static final String GREEN = "GREEN";
  public static final String YELLOW = "YELLOW";
  public static final String RED = "RED";
  
  private final Integer reportTemplateId;
  private final String reportTemplateName;
  private final String reportTemplateDescription;
  private final Integer numGreenEquipment;
  private final Integer numEquipmentTotal;
  private final String status;
  private final Boolean isEnabled;
  private final Boolean isValid;
  private final Boolean isIgnored;
  private final String lastUpdated;
  private final String priority;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (ReportStatusValueObject reportStatusValueObject) {
    return new Builder(reportStatusValueObject);
  }

  private ReportStatusValueObject (Builder builder) {
    this.reportTemplateId = builder.reportTemplateId;
    this.reportTemplateName = builder.reportTemplateName;
    this.reportTemplateDescription = builder.reportTemplateDescription;
    this.numGreenEquipment = builder.numGreenEquipment;
    this.numEquipmentTotal = builder.numEquipmentTotal;
    this.status = builder.status;
    this.isEnabled = builder.isEnabled;
    this.isValid = builder.isValid;
    this.isIgnored = builder.isIgnored;
    this.lastUpdated = builder.lastUpdated;
    this.priority = builder.priority;
  }

  public Integer getReportTemplateId() {
    return reportTemplateId;
  }

  public String getReportTemplateName() {
    return reportTemplateName;
  }

  public String getReportTemplateDescription() {
    return reportTemplateDescription;
  }
  
  public Integer getNumGreenEquipment() {
    return numGreenEquipment;
  }

  public Integer getNumEquipmentTotal() {
    return numEquipmentTotal;
  }
  
  public String getStatus() {
    return status;
  }

  public Boolean getIsEnabled() {
    return isEnabled;
  }

  public Boolean getIsValid() {
    return isValid;
  }
  
  public Boolean getIsIgnored() {
    return isIgnored;
  }
  
  public String getLastUpdated() {
    return lastUpdated;
  }
  
  public String getPriority() {
    return priority;
  }

  @Override
  public int compareTo(ReportStatusValueObject that) {
    return this.reportTemplateName.compareTo(that.reportTemplateName);
  }
  
  @Override
  public String toString() {
    
    return new StringBuilder()
        .append("ReportStatusValueObject [reportTemplateId=")
        .append(reportTemplateId)
        .append(", reportTemplateName=")
        .append(reportTemplateName)
        .append(", reportTemplateDescription=")
        .append(reportTemplateDescription)
        .append(", numGreenEquipment=")
        .append(numGreenEquipment)
        .append(", numEquipmentTotal=")
        .append(numEquipmentTotal)
        .append(", status=")
        .append(status)
        .append(", isEnabled=")
        .append(isEnabled)
        .append(", isValid=")
        .append(isValid)
        .append(", isIgnored=")
        .append(isIgnored)
        .append(", lastUpdated=")
        .append(lastUpdated)
        .append(", priority=")
        .append(priority)
        .append("]")
        .toString();
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Integer reportTemplateId;
    private String reportTemplateName;
    private String reportTemplateDescription;
    private Integer numGreenEquipment;
    private Integer numEquipmentTotal;
    private String status;
    private Boolean isEnabled;
    private Boolean isValid;
    private Boolean isIgnored;
    private String lastUpdated;
    private String priority;
    
    private Builder() {}

    private Builder(ReportStatusValueObject reportStatusValueObject) {
      requireNonNull(reportStatusValueObject, "reportStatusValueObject cannot be null");
      this.reportTemplateId = reportStatusValueObject.reportTemplateId;
      this.reportTemplateName = reportStatusValueObject.reportTemplateName;
      this.reportTemplateDescription = reportStatusValueObject.reportTemplateDescription;
      this.numGreenEquipment = reportStatusValueObject.numGreenEquipment;
      this.numEquipmentTotal = reportStatusValueObject.numEquipmentTotal;
      this.status = reportStatusValueObject.status;
      this.isEnabled = reportStatusValueObject.isEnabled;
      this.isValid = reportStatusValueObject.isValid;
      this.isIgnored = reportStatusValueObject.isIgnored;
      this.lastUpdated = reportStatusValueObject.lastUpdated;
      this.priority = reportStatusValueObject.priority;
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

    public Builder withReportTemplateName(String reportTemplateName) {
      requireNonNull(reportTemplateName, "reportTemplateName cannot be null");
      this.reportTemplateName = reportTemplateName;
      return this;
    }

    public Builder withReportTemplateDescription(String reportTemplateDescription) {
      requireNonNull(reportTemplateDescription, "reportTemplateDescription cannot be null");
      this.reportTemplateDescription = reportTemplateDescription;
      return this;
    }
    
    public Builder withNumGreenEquipment(Integer numGreenEquipment) {
      requireNonNull(numGreenEquipment, "numGreenEquipment cannot be null");
      this.numGreenEquipment = numGreenEquipment;
      return this;
    }

    public Builder withNumEquipmentTotal(Integer numEquipmentTotal) {
      requireNonNull(numEquipmentTotal, "numEquipmentTotal cannot be null");
      this.numEquipmentTotal = numEquipmentTotal;
      return this;
    }
    
    public Builder withStatus(String status) {
      requireNonNull(status, "status cannot be null");
      this.status = status;
      return this;
    }

    public Builder withIsEnabled(Boolean isEnabled) {
      requireNonNull(isEnabled, "isEnabled cannot be null");
      this.isEnabled = isEnabled;
      return this;
    }

    public Builder withIsValid(Boolean isValid) {
      requireNonNull(isValid, "isValid cannot be null");
      this.isValid = isValid;
      return this;
    }

    public Builder withIsIgnored(Boolean isIgnored) {
      requireNonNull(isIgnored, "isIgnored cannot be null");
      this.isIgnored = isIgnored;
      return this;
    }

    public Builder withLastUpdated(String lastUpdated) {
      requireNonNull(lastUpdated, "lastUpdated cannot be null");
      this.lastUpdated = lastUpdated;
      return this;
    }
    
    public Builder withPriority(String priority) {
      requireNonNull(priority, "priority cannot be null");
      this.priority = priority;
      return this;
    }

    public ReportStatusValueObject build() {
      requireNonNull(reportTemplateId, "reportTemplateId cannot be null");
      requireNonNull(reportTemplateName, "reportTemplateName cannot be null");
      requireNonNull(reportTemplateDescription, "reportTemplateDescription cannot be null");
      requireNonNull(numGreenEquipment, "numGreenEquipment cannot be null");
      requireNonNull(numEquipmentTotal, "numEquipmentTotal cannot be null");
      requireNonNull(status, "status cannot be null");
      requireNonNull(isEnabled, "isEnabled cannot be null");
      requireNonNull(isValid, "isValid cannot be null");
      requireNonNull(isIgnored, "isIgnored cannot be null");
      requireNonNull(lastUpdated, "lastUpdated cannot be null");
      requireNonNull(priority, "priority cannot be null");
      return new ReportStatusValueObject(this);
    }
  }
}