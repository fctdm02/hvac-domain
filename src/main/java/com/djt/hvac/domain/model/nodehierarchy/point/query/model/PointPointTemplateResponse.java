package com.djt.hvac.domain.model.nodehierarchy.point.query.model;

import java.util.List;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableList;

import static java.util.Objects.requireNonNull;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = PointPointTemplateResponse.Builder.class)
public class PointPointTemplateResponse {
  private final Integer id;
  private final List<PointPointTemplate> templates;

  @JsonCreator
  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(PointPointTemplateResponse pointTemplates) {
    return new Builder(pointTemplates);
  }

  private PointPointTemplateResponse(Builder builder) {
    this.id = builder.id;
    this.templates = builder.templates;
  }

  public Integer getId() {
    return id;
  }

  public List<PointPointTemplate> getTemplates() {
    return templates;
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Integer id;
    private List<PointPointTemplate> templates;

    private Builder() {}

    private Builder(PointPointTemplateResponse pointTemplates) {
      requireNonNull(pointTemplates, "pointTemplates cannot be null");
      this.id = pointTemplates.id;
      this.templates = pointTemplates.templates;
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

    public Builder withTemplates(List<PointPointTemplate> templates) {
      requireNonNull(templates, "templates cannot be null");
      this.templates = ImmutableList.copyOf(templates);
      return this;
    }

    public PointPointTemplateResponse build() {
      requireNonNull(templates, "templates cannot be null");
      return new PointPointTemplateResponse(this);
    }
  }
}
