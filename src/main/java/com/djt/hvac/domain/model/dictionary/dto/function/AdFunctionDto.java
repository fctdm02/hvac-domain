package com.djt.hvac.domain.model.dictionary.dto.function;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import com.djt.hvac.domain.model.dictionary.enums.FunctionType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = AdFunctionDto.Builder.class)
public class AdFunctionDto {
  private final Integer id;
  private final String name;
  private final String description;
  private final FunctionType functionTypeEnum;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (AdFunctionDto adFunctionDto) {
    return new Builder(adFunctionDto);
  }

  private AdFunctionDto (Builder builder) {
    this.id = builder.id;
    this.name = builder.name;
    this.description = builder.description;
    this.functionTypeEnum = builder.functionTypeEnum;
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

  public FunctionType getFunctionTypeEnum() {
    return functionTypeEnum;
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Integer id;
    private String name;
    private String description;
    private FunctionType functionTypeEnum;

    private Builder() {}

    private Builder(AdFunctionDto adFunctionDto) {
      requireNonNull(adFunctionDto, "adFunctionDto cannot be null");
      this.id = adFunctionDto.id;
      this.name = adFunctionDto.name;
      this.description = adFunctionDto.description;
      this.functionTypeEnum = adFunctionDto.functionTypeEnum;
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

    public Builder withFunctionTypeEnum(FunctionType functionTypeEnum) {
      requireNonNull(functionTypeEnum, "functionTypeEnum cannot be null");
      this.functionTypeEnum = functionTypeEnum;
      return this;
    }

    public AdFunctionDto build() {
      requireNonNull(id, "id cannot be null");
      requireNonNull(name, "name cannot be null");
      requireNonNull(description, "description cannot be null");
      requireNonNull(functionTypeEnum, "functionTypeEnum cannot be null");
      return new AdFunctionDto(this);
    }
  }
}