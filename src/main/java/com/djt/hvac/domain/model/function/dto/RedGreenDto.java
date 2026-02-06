package com.djt.hvac.domain.model.function.dto;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = RedGreenDto.Builder.class)
public class RedGreenDto {
  private final AdFunctionInstanceCandidateBoundPointsDto green;
  private final AdFunctionErrorMessagesDto red;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (RedGreenDto redGreenDto) {
    return new Builder(redGreenDto);
  }

  private RedGreenDto (Builder builder) {
    this.green = builder.green;
    this.red = builder.red;
  }

  public AdFunctionInstanceCandidateBoundPointsDto getGreen() {
    return green;
  }

  public AdFunctionErrorMessagesDto getRed() {
    return red;
  }

  @JsonPOJOBuilder
  public static class Builder {
    private AdFunctionInstanceCandidateBoundPointsDto green;
    private AdFunctionErrorMessagesDto red;

    private Builder() {}

    private Builder(RedGreenDto redGreenDto) {
      requireNonNull(redGreenDto, "redGreenDto cannot be null");
      this.green = redGreenDto.green;
      this.red = redGreenDto.red;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withGreen(AdFunctionInstanceCandidateBoundPointsDto green) {
      this.green = green;
      return this;
    }

    public Builder withRed(AdFunctionErrorMessagesDto red) {
      this.red = red;
      return this;
    }

    public RedGreenDto build() {
      return new RedGreenDto(this);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((green == null) ? 0 : green.hashCode());
    result = prime * result + ((red == null) ? 0 : red.hashCode());
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
    RedGreenDto other = (RedGreenDto) obj;
    if (green == null) {
      if (other.green != null)
        return false;
    } else if (!green.equals(other.green))
      return false;
    if (red == null) {
      if (other.red != null)
        return false;
    } else if (!red.equals(other.red))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder2 = new StringBuilder();
    builder2.append("RedGreenDto [green=").append(green).append(", red=").append(red).append("]");
    return builder2.toString();
  }
}