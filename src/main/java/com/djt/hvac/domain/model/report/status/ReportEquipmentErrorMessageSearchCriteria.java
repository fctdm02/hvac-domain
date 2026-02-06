package com.djt.hvac.domain.model.report.status;

import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = ReportEquipmentErrorMessageSearchCriteria.Builder.class)
public class ReportEquipmentErrorMessageSearchCriteria {
  private final Integer buildingId;
  private final Integer reportTemplateId;
  private final String nodePath;
  private final String sortDirection;
  private final Integer limit;
  private final Integer offset;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (ReportEquipmentErrorMessageSearchCriteria reportEquipmentErrorMessageSearchCriteria) {
    return new Builder(reportEquipmentErrorMessageSearchCriteria);
  }

  private ReportEquipmentErrorMessageSearchCriteria (Builder builder) {
    this.buildingId = builder.buildingId;
    this.reportTemplateId = builder.reportTemplateId;
    this.nodePath = builder.nodePath;
    this.sortDirection = builder.sortDirection;
    this.limit = builder.limit;
    this.offset = builder.offset;
  }

  public Integer getBuildingId() {
    return buildingId;
  }

  public Integer getReportTemplateId() {
    return reportTemplateId;
  }

  public String getNodePath() {
    return nodePath;
  }

  public String getSortDirection() {
    return sortDirection;
  }

  public Integer getLimit() {
    return limit;
  }

  public Integer getOffset() {
    return offset;
  }

  @Override
  public String toString() {
    StringBuilder builder2 = new StringBuilder();
    builder2.append("ReportEquipmentErrorMessageSearchCriteria [buildingId=").append(buildingId)
        .append(", reportTemplateId=").append(reportTemplateId).append(", nodePath=")
        .append(nodePath).append(", sortDirection=").append(sortDirection).append(", limit=")
        .append(limit).append(", offset=").append(offset).append("]");
    return builder2.toString();
  }

  public void validate() {
    requireNonNull(buildingId, "'buildingId' cannot be null");
    requireNonNull(reportTemplateId, "'reportTemplateId' cannot be null");
    if (sortDirection != null && !sortDirection.equals("asc") && !sortDirection.equals("desc")) {
      throw new IllegalArgumentException("'sortDirection' must be either 'asc' or 'desc'");
    }
    if (limit < 100 || limit > 1000) {
      throw new IllegalArgumentException("'limit' must be between 100 and 1,000 (inclusive)");
    }
    if (offset < 0) {
      throw new IllegalArgumentException("'offset' must be between 0 or greater");
    }
    if (nodePath == null || nodePath.trim().equals("")) {
      throw new IllegalArgumentException("'nodePath', if specified, must be non-empty");
    }
  }
  
  private static <T> T requireNonNull(T obj, String message) {
    if (obj ==  null) {
      throw new IllegalArgumentException(message);
    }
    return obj;
  }
  
  @JsonPOJOBuilder
  public static class Builder {
    private Integer buildingId;
    private Integer reportTemplateId;
    private String nodePath = "*";
    private String sortDirection = "asc";
    private Integer limit = Integer.valueOf(100);
    private Integer offset = Integer.valueOf(0);

    private Builder() {}

    private Builder(ReportEquipmentErrorMessageSearchCriteria reportEquipmentErrorMessageSearchCriteria) {
      requireNonNull(reportEquipmentErrorMessageSearchCriteria, "reportEquipmentErrorMessageSearchCriteria cannot be null");
      this.buildingId = reportEquipmentErrorMessageSearchCriteria.buildingId;
      this.reportTemplateId = reportEquipmentErrorMessageSearchCriteria.reportTemplateId;
      this.nodePath = reportEquipmentErrorMessageSearchCriteria.nodePath;
      this.sortDirection = reportEquipmentErrorMessageSearchCriteria.sortDirection;
      this.limit = reportEquipmentErrorMessageSearchCriteria.limit;
      this.offset = reportEquipmentErrorMessageSearchCriteria.offset;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withBuildingId(Integer buildingId) {
      requireNonNull(buildingId, "buildingId cannot be null");
      this.buildingId = buildingId;
      return this;
    }

    public Builder withReportTemplateId(Integer reportTemplateId) {
      requireNonNull(reportTemplateId, "reportTemplateId cannot be null");
      this.reportTemplateId = reportTemplateId;
      return this;
    }

    public Builder withNodePath(String nodePath) {
      requireNonNull(nodePath, "nodePath cannot be null");
      this.nodePath = nodePath;
      return this;
    }

    public Builder withSortDirection(String sortDirection) {
      requireNonNull(sortDirection, "sortDirection cannot be null");
      this.sortDirection = sortDirection;
      return this;
    }

    public Builder withLimit(Integer limit) {
      requireNonNull(limit, "limit cannot be null");
      this.limit = limit;
      return this;
    }

    public Builder withOffset(Integer offset) {
      requireNonNull(offset, "offset cannot be null");
      this.offset = offset;
      return this;
    }

    public ReportEquipmentErrorMessageSearchCriteria build() {
      return new ReportEquipmentErrorMessageSearchCriteria(this);
    }
  }
}