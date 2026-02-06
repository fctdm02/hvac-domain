//@formatter:off
package com.djt.hvac.domain.model.dictionary.dto.report;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = ReportTemplateEquipmentSpecDto.Builder.class)
public class ReportTemplateEquipmentSpecDto {
  private final Integer id;
  private final Integer equipmentTypeId;
  private final String equipmentTypeName;
  private final String nodeFilterExpression;
  private final String nodeFilterErrorMessage;
  private final String tupleConstraintExpression;
  private final String tupleConstraintErrorMessage;
  private final List<ReportTemplatePointSpecDto> pointSpecs;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (ReportTemplateEquipmentSpecDto reportTemplateEquipmentSpecDto) {
    return new Builder(reportTemplateEquipmentSpecDto);
  }

  private ReportTemplateEquipmentSpecDto (Builder builder) {
    this.id = builder.id;
    this.equipmentTypeId = builder.equipmentTypeId;
    this.equipmentTypeName = builder.equipmentTypeName;
    this.nodeFilterExpression = builder.nodeFilterExpression;
    this.nodeFilterErrorMessage = builder.nodeFilterErrorMessage;
    this.tupleConstraintExpression = builder.tupleConstraintExpression;
    this.tupleConstraintErrorMessage = builder.tupleConstraintErrorMessage;
    this.pointSpecs = builder.pointSpecs;
  }

  public Integer getId() {
    return id;
  }

  public Integer getEquipmentTypeId() {
    return equipmentTypeId;
  }
  
  public String getEquipmentTypeName() {
    return equipmentTypeName;
  }

  public String getNodeFilterExpression() {
    return nodeFilterExpression;
  }

  public String getNodeFilterErrorMessage() {
    return nodeFilterErrorMessage;
  }

  public String getTupleConstraintExpression() {
    return tupleConstraintExpression;
  }

  public String getTupleConstraintErrorMessage() {
    return tupleConstraintErrorMessage;
  }

  public List<ReportTemplatePointSpecDto> getPointSpecs() {
    return pointSpecs;
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Integer id;
    private Integer equipmentTypeId;
    private String equipmentTypeName;
    private String nodeFilterExpression;
    private String nodeFilterErrorMessage;
    private String tupleConstraintExpression;
    private String tupleConstraintErrorMessage;
    private List<ReportTemplatePointSpecDto> pointSpecs;

    private Builder() {}

    private Builder(ReportTemplateEquipmentSpecDto reportTemplateEquipmentSpecDto) {
      requireNonNull(reportTemplateEquipmentSpecDto, "reportTemplateEquipmentSpecDto cannot be null");
      this.id = reportTemplateEquipmentSpecDto.id;
      this.equipmentTypeId = reportTemplateEquipmentSpecDto.equipmentTypeId;
      this.equipmentTypeName = reportTemplateEquipmentSpecDto.equipmentTypeName;
      this.nodeFilterExpression = reportTemplateEquipmentSpecDto.nodeFilterExpression;
      this.nodeFilterErrorMessage = reportTemplateEquipmentSpecDto.nodeFilterErrorMessage;
      this.tupleConstraintExpression = reportTemplateEquipmentSpecDto.tupleConstraintExpression;
      this.tupleConstraintErrorMessage = reportTemplateEquipmentSpecDto.tupleConstraintErrorMessage;
      this.pointSpecs = reportTemplateEquipmentSpecDto.pointSpecs;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withId(Integer id) {
      requireNonNull(id, "id cannot be null");
      this.id = id;
      return this;
    }

    public Builder withEquipmentTypeId(Integer equipmentTypeId) {
      requireNonNull(equipmentTypeId, "equipmentTypeId cannot be null");
      this.equipmentTypeId = equipmentTypeId;
      return this;
    }

    public Builder withEquipmentTypeName(String equipmentTypeName) {
      this.equipmentTypeName = equipmentTypeName;
      return this;
    }
    
    public Builder withNodeFilterExpression(String nodeFilterExpression) {
      this.nodeFilterExpression = nodeFilterExpression;
      return this;
    }

    public Builder withNodeFilterErrorMessage(String nodeFilterErrorMessage) {
      this.nodeFilterErrorMessage = nodeFilterErrorMessage;
      return this;
    }

    public Builder withTupleConstraintExpression(String tupleConstraintExpression) {
      this.tupleConstraintExpression = tupleConstraintExpression;
      return this;
    }

    public Builder withTupleConstraintErrorMessage(String tupleConstraintErrorMessage) {
      this.tupleConstraintErrorMessage = tupleConstraintErrorMessage;
      return this;
    }

    public Builder withPointSpecs(List<ReportTemplatePointSpecDto> pointSpecs) {
      requireNonNull(pointSpecs, "pointSpecs cannot be null");
      this.pointSpecs = pointSpecs;
      return this;
    }

    public ReportTemplateEquipmentSpecDto build() {
      requireNonNull(id, "id cannot be null");
      requireNonNull(equipmentTypeId, "equipmentTypeId cannot be null");
      requireNonNull(pointSpecs, "pointSpecs cannot be null");
      return new ReportTemplateEquipmentSpecDto(this);
    }
  }
}
//@formatter:on