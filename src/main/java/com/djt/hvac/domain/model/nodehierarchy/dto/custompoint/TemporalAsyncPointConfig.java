package com.djt.hvac.domain.model.nodehierarchy.dto.custompoint;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.djt.hvac.domain.model.nodehierarchy.utils.LocalDateDeserializer;
import com.djt.hvac.domain.model.nodehierarchy.utils.LocalDateSerializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = TemporalAsyncPointConfig.Builder.class)
public class TemporalAsyncPointConfig {
  private final Integer id;
  private final Integer asyncPointConfigId;
  private final LocalDate effectiveDate;
  private final String formula;
  private final List<TemporalAsyncPointVar> variables;
  private final String description;

  private TemporalAsyncPointConfig(Builder builder) {
    this.id = builder.id;
    this.asyncPointConfigId = builder.asyncPointConfigId;
    this.effectiveDate = builder.effectiveDate;
    this.formula = builder.formula;
    this.variables = ImmutableList.copyOf(builder.variables);
    this.description = builder.description;
  }

  public Integer getId() {
    return id;
  }

  public Integer getAsyncPointConfigId() {
    return asyncPointConfigId;
  }

  @NotNull(message = "{AsyncPointConfig.0}")
  @JsonSerialize(using = LocalDateSerializer.class)
  public LocalDate getEffectiveDate() {
    return effectiveDate;
  }

  @NotNull(message = "{AsyncPointConfig.1}")
  public String getFormula() {
    return formula;
  }

  @Valid
  @NotNull(message = "{AsyncPointConfig.2}")
  public List<TemporalAsyncPointVar> getVariables() {
    return variables;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public String toString() {
    return "TemporalAsyncPointConfig [id=" + id + ", asyncPointConfigId=" + asyncPointConfigId
        + ", effectiveDate=" + effectiveDate + ", formula=" + formula + ", variables=" + variables
        + ", description=" + description + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((asyncPointConfigId == null) ? 0 : asyncPointConfigId.hashCode());
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((effectiveDate == null) ? 0 : effectiveDate.hashCode());
    result = prime * result + ((formula == null) ? 0 : formula.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((variables == null) ? 0 : variables.hashCode());
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
    TemporalAsyncPointConfig other = (TemporalAsyncPointConfig) obj;
    if (asyncPointConfigId == null) {
      if (other.asyncPointConfigId != null)
        return false;
    } else if (!asyncPointConfigId.equals(other.asyncPointConfigId))
      return false;
    if (description == null) {
      if (other.description != null)
        return false;
    } else if (!description.equals(other.description))
      return false;
    if (effectiveDate == null) {
      if (other.effectiveDate != null)
        return false;
    } else if (!effectiveDate.equals(other.effectiveDate))
      return false;
    if (formula == null) {
      if (other.formula != null)
        return false;
    } else if (!formula.equals(other.formula))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (variables == null) {
      if (other.variables != null)
        return false;
    } else if (!variables.equals(other.variables))
      return false;
    return true;
  }

  /**
   * Creates builder to build {@link TemporalAsyncPointConfig}.
   * 
   * @return created builder
   */
  @JsonCreator
  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(TemporalAsyncPointConfig other) {
    return new Builder(other);
  }

  /**
   * Builder to build {@link TemporalAsyncPointConfig}.
   */
  @JsonPOJOBuilder
  public static final class Builder {
    private Integer id;
    private Integer asyncPointConfigId;
    private LocalDate effectiveDate;
    private String formula;
    private List<TemporalAsyncPointVar> variables = Lists.newArrayList();
    private String description;


    private Builder() {}

    private Builder(TemporalAsyncPointConfig other) {
      this.id = other.id;
      this.asyncPointConfigId = other.asyncPointConfigId;
      this.effectiveDate = other.effectiveDate;
      this.formula = other.formula;
      this.variables = other.variables;
      this.description = other.description;
    }

    public Builder withId(Integer id) {
      this.id = id;
      return this;
    }

    public Builder withAsyncPointConfigId(Integer asyncPointConfigId) {
      this.asyncPointConfigId = asyncPointConfigId;
      return this;
    }

    @JsonDeserialize(using = LocalDateDeserializer.class)
    public Builder withEffectiveDate(LocalDate effectiveDate) {
      this.effectiveDate = effectiveDate;
      return this;
    }

    public Builder withFormula(String formula) {
      this.formula = formula;
      return this;
    }

    public Builder withVariables(List<TemporalAsyncPointVar> variables) {
      this.variables = variables;
      return this;
    }

    public Builder withDescription(String description) {
      this.description = description;
      return this;
    }

    public TemporalAsyncPointConfig build() {
      return new TemporalAsyncPointConfig(this);
    }
  }
}
