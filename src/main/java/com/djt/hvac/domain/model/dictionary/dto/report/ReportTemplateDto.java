//@formatter:off
// DTO is for transferring information from the repository (database or file system)
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
@JsonDeserialize(builder = ReportTemplateDto.Builder.class)
public class ReportTemplateDto {
  private final Integer id;
  private final String name;
  private final String description;
  private final Boolean isInternal;
  private final Boolean isBeta;
  private final List<ReportTemplateEquipmentSpecDto> equipmentSpecs;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (ReportTemplateDto reportTemplateDto) {
    return new Builder(reportTemplateDto);
  }

  private ReportTemplateDto (Builder builder) {
    this.id = builder.id;
    this.name = builder.name;
    this.description = builder.description;
    this.isInternal = builder.isInternal;
    this.isBeta = builder.isBeta;
    this.equipmentSpecs = builder.equipmentSpecs;
  }

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public Boolean getIsInternal() {
    return isInternal;
  }

  public Boolean getIsBeta() {
    return isBeta;
  }
  
  public List<ReportTemplateEquipmentSpecDto> getEquipmentSpecs() {
    return equipmentSpecs;
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Integer id;
    private String name;
    private String description;
    private Boolean isInternal;
    private Boolean isBeta;
    private List<ReportTemplateEquipmentSpecDto> equipmentSpecs;

    private Builder() {}

    private Builder(ReportTemplateDto reportTemplateDto) {
      requireNonNull(reportTemplateDto, "reportTemplateDto cannot be null");
      this.id = reportTemplateDto.id;
      this.name = reportTemplateDto.name;
      this.description = reportTemplateDto.description;
      this.isInternal = reportTemplateDto.isInternal;
      this.isBeta = reportTemplateDto.isBeta;
      this.equipmentSpecs = reportTemplateDto.equipmentSpecs;
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

    public Builder withName(String name) {
      requireNonNull(name, "name cannot be null");
      this.name = name;
      return this;
    }

    public Builder withDescription(String description) {
      requireNonNull(description, "description cannot be null");
      this.description = description;
      return this;
    }

    public Builder withIsInternal(Boolean isInternal) {
      requireNonNull(isInternal, "isInternal cannot be null");
      this.isInternal = isInternal;
      return this;
    }    
    
    public Builder withIsBeta(Boolean isBeta) {
      requireNonNull(isBeta, "isBeta cannot be null");
      this.isBeta = isBeta;
      return this;
    }

    public Builder withEquipmentSpecs(List<ReportTemplateEquipmentSpecDto> equipmentSpecs) {
      requireNonNull(equipmentSpecs, "equipmentSpecs cannot be null");
      this.equipmentSpecs = equipmentSpecs;
      return this;
    }

    public ReportTemplateDto build() {
      requireNonNull(id, "id cannot be null");
      requireNonNull(name, "name cannot be null");
      requireNonNull(description, "description cannot be null");
      requireNonNull(isInternal, "isInternal cannot be null");
      requireNonNull(isBeta, "isBeta cannot be null");
      requireNonNull(equipmentSpecs, "equipmentSpecs cannot be null");
      return new ReportTemplateDto(this);
    }
  }
}
//@formatter:on