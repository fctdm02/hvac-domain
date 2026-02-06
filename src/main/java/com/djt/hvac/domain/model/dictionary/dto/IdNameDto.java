package com.djt.hvac.domain.model.dictionary.dto;

import static java.util.Objects.requireNonNull;
import java.util.function.Consumer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = IdNameDto.Builder.class)
@JsonPropertyOrder({
  "id",
  "name",
  "aggregator_id"
})
public class IdNameDto {
  
  private final Integer id;
  private final String name;
  private final Integer aggregator_id;

  @JsonCreator
  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(IdNameDto entityIndex) {
    return new Builder(entityIndex);
  }

  private IdNameDto(Builder builder) {
    this.id = builder.id;
    this.name = builder.name;
    this.aggregator_id = builder.aggregator_id;
  }

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }
  
  public Integer getAggregator_id() {
    return aggregator_id;
  }
  
  @Override
  public String toString() {
    return new StringBuilder()
        .append("IdNameDto [id=")
        .append(id)
        .append(", name=")
        .append(name)
        .append("]")
        .toString();
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Integer id;
    private String name;
    private Integer aggregator_id;

    private Builder() {}

    private Builder(IdNameDto dto) {
      requireNonNull(dto, "dto cannot be null");
      this.id = dto.id;
      this.name = dto.name;
      this.aggregator_id = dto.aggregator_id;
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
    
    public Builder withAggregator_id(Integer aggregator_id) {
      requireNonNull(aggregator_id, "aggregator_id cannot be null");
      this.aggregator_id = aggregator_id;
      return this;
    }

    public IdNameDto build() {
      requireNonNull(name, "name cannot be null");
      return new IdNameDto(this);
    }
  }
}
