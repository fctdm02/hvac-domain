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
@JsonDeserialize(builder = ReportTemplatePointSpecDto.Builder.class)
public class ReportTemplatePointSpecDto {

  public static final String TYPE_STANDARD = "STANDARD";
  public static final String TYPE_RULE = "RULE";

  private final Integer id;
  private final String name;
  private final String type;
  private final Boolean isRequired;
  private final Boolean isArray; // Always false for RULE
  private final String currentObjectExpression; // Always null for RULE
  private final String errorMessage;
  private final Integer requiredDataTypeId; // Defaults to null
  private final List<String> tags; // Always null for RULL
  private final Integer ruleTemplateId; // Always specified for RULE

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (ReportTemplatePointSpecDto reportTemplatePointSpecDto) {
    return new Builder(reportTemplatePointSpecDto);
  }

  private ReportTemplatePointSpecDto (Builder builder) {
    this.id = builder.id;
    this.name = builder.name;
    this.type = builder.type;
    this.isRequired = builder.isRequired;
    this.isArray = builder.isArray;
    this.currentObjectExpression = builder.currentObjectExpression;
    this.errorMessage = builder.errorMessage;
    this.requiredDataTypeId = builder.requiredDataTypeId;
    this.tags = builder.tags;
    this.ruleTemplateId = builder.ruleTemplateId;
  }

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public Boolean getIsRequired() {
    return isRequired;
  }

  public Boolean getIsArray() {
    return isArray;
  }

  public String getCurrentObjectExpression() {
    return currentObjectExpression;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public Integer getRequiredDataTypeId() {
    return requiredDataTypeId;
  }

  public List<String> getTags() {
    return tags;
  }

  public Integer getRuleTemplateId() {
    return ruleTemplateId;
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Integer id;
    private String name;
    private String type;
    private Boolean isRequired;
    private Boolean isArray;
    private String currentObjectExpression;
    private String errorMessage;
    private Integer requiredDataTypeId;
    private List<String> tags;
    private Integer ruleTemplateId;

    private Builder() {}

    private Builder(ReportTemplatePointSpecDto reportTemplatePointSpecDto) {
      requireNonNull(reportTemplatePointSpecDto, "reportTemplatePointSpecDto cannot be null");
      this.id = reportTemplatePointSpecDto.id;
      this.name = reportTemplatePointSpecDto.name;
      this.type = reportTemplatePointSpecDto.type;
      this.isRequired = reportTemplatePointSpecDto.isRequired;
      this.isArray = reportTemplatePointSpecDto.isArray;
      this.currentObjectExpression = reportTemplatePointSpecDto.currentObjectExpression;
      this.errorMessage = reportTemplatePointSpecDto.errorMessage;
      this.requiredDataTypeId = reportTemplatePointSpecDto.requiredDataTypeId;
      this.tags = reportTemplatePointSpecDto.tags;
      this.ruleTemplateId = reportTemplatePointSpecDto.ruleTemplateId;
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

    public Builder withType(String type) {
      requireNonNull(type, "type cannot be null");
      this.type = type;
      return this;
    }

    public Builder withIsRequired(Boolean isRequired) {
      requireNonNull(isRequired, "isRequired cannot be null");
      this.isRequired = isRequired;
      return this;
    }

    public Builder withIsArray(Boolean isArray) {
      requireNonNull(isArray, "isArray cannot be null");
      this.isArray = isArray;
      return this;
    }

    public Builder withCurrentObjectExpression(String currentObjectExpression) {
      this.currentObjectExpression = currentObjectExpression;
      return this;
    }

    public Builder withErrorMessage(String errorMessage) {
      requireNonNull(errorMessage, "errorMessage cannot be null");
      this.errorMessage = errorMessage;
      return this;
    }

    public Builder withRequiredDataTypeId(Integer requiredDataTypeId) {
      this.requiredDataTypeId = requiredDataTypeId;
      return this;
    }

    public Builder withTags(List<String> tags) {
      requireNonNull(tags, "tags cannot be null");
      this.tags = tags;
      return this;
    }

    public Builder withRuleTemplateId(Integer ruleTemplateId) {
      requireNonNull(id, "id cannot be null");
      this.ruleTemplateId = ruleTemplateId;
      return this;
    }

    public ReportTemplatePointSpecDto build() {
      requireNonNull(id, "id cannot be null");
      requireNonNull(name, "name cannot be null");
      requireNonNull(type, "type cannot be null");
      requireNonNull(isRequired, "isRequired cannot be null");
      requireNonNull(errorMessage, "errorMessage cannot be null");
      return new ReportTemplatePointSpecDto(this);
    }
  }
}
//@formatter:on