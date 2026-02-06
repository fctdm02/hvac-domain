package com.djt.hvac.domain.model.nodehierarchy.point.query.model;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = PointPointTemplate.Builder.class)
public class PointPointTemplate implements Comparable<PointPointTemplate> {
  private final int id;
  private final String description;
  private final String tags;
  private final String name;

  @JsonCreator
  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(PointPointTemplate pointTemplateIndex) {
    return new Builder(pointTemplateIndex);
  }

  private PointPointTemplate(Builder builder) {
    this.id = builder.id;
    this.description = builder.description;
    this.tags = builder.tags;
    this.name = builder.name;
  }

  public int getId() {
    return id;
  }

  public String getDescription() {
    return description;
  }

  public String getTags() {
    return tags;
  }

  public String getName() {
    return name;
  }
  
  @Override
  public int compareTo(PointPointTemplate that) {
    
    return this.name.compareTo(that.name);
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Integer id;
    private String description;
    private String tags;
    private String name;

    private Builder() {}

    private Builder(PointPointTemplate pointTemplateIndex) {
      requireNonNull(pointTemplateIndex, "pointTemplateIndex cannot be null");
      this.id = pointTemplateIndex.id;
      this.description = pointTemplateIndex.description;
      this.tags = pointTemplateIndex.tags;
      this.name = pointTemplateIndex.name;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withId(int id) {
      this.id = id;
      return this;
    }

    public Builder withDescription(String description) {
      requireNonNull(description, "description cannot be null");
      this.description = description;
      return this;
    }

    public Builder withTags(String tags) {
      requireNonNull(tags, "tags cannot be null");
      this.tags = tags;
      return this;
    }

    public Builder withName(String name) {
      requireNonNull(name, "name cannot be null");
      this.name = name;
      return this;
    }

    public PointPointTemplate build() {
      requireNonNull(id, "id cannot be null");
      requireNonNull(description, "description cannot be null");
      requireNonNull(tags, "tags cannot be null");
      requireNonNull(name, "name cannot be null");
      return new PointPointTemplate(this);
    }
  }
}
