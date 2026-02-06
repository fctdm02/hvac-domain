package com.djt.hvac.domain.model.function.dto;

import static java.util.Objects.requireNonNull;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableMap;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = AdFunctionInstanceCandidateBoundPointsDto.Builder.class)
public class AdFunctionInstanceCandidateBoundPointsDto {
  
  private final Map<Integer, List<Integer>> boundCandidatePoints;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (AdFunctionInstanceCandidateBoundPointsDto adFunctionInstanceCandidateBoundPointsDto) {
    return new Builder(adFunctionInstanceCandidateBoundPointsDto);
  }

  private AdFunctionInstanceCandidateBoundPointsDto (Builder builder) {
    this.boundCandidatePoints = builder.boundCandidatePoints;
  }

  public Map<Integer, List<Integer>> getBoundCandidatePoints() {
    return boundCandidatePoints;
  }

  @JsonPOJOBuilder
  public static class Builder {
    private Map<Integer, List<Integer>> boundCandidatePoints;

    private Builder() {}

    private Builder(AdFunctionInstanceCandidateBoundPointsDto adFunctionInstanceCandidateBoundPointsDto) {
      requireNonNull(adFunctionInstanceCandidateBoundPointsDto, "adFunctionInstanceCandidateBoundPointsDto cannot be null");
      this.boundCandidatePoints = adFunctionInstanceCandidateBoundPointsDto.boundCandidatePoints;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withBoundCandidatePoints(Map<Integer, List<Integer>> boundCandidatePoints) {
      requireNonNull(boundCandidatePoints, "boundCandidatePoints cannot be null");
      this.boundCandidatePoints = ImmutableMap.copyOf(boundCandidatePoints);
      return this;
    }

    public AdFunctionInstanceCandidateBoundPointsDto build() {
      requireNonNull(boundCandidatePoints, "boundCandidatePoints cannot be null");
      return new AdFunctionInstanceCandidateBoundPointsDto(this);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result =
        prime * result + ((boundCandidatePoints == null) ? 0 : boundCandidatePoints.hashCode());
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
    AdFunctionInstanceCandidateBoundPointsDto other = (AdFunctionInstanceCandidateBoundPointsDto) obj;
    if (boundCandidatePoints == null) {
      if (other.boundCandidatePoints != null)
        return false;
    } else if (!boundCandidatePoints.equals(other.boundCandidatePoints))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "AdFunctionInstanceCandidateBoundPointsDto [boundCandidatePoints=" + boundCandidatePoints + "]";
  }
}