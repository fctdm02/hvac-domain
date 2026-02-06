package com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import com.djt.hvac.domain.model.common.dto.EntityIndex;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableList;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = EquipmentChildrenSearchResponse.Builder.class)
@JsonPropertyOrder({
    "query",
    "totalRows",
    "data"
})
public class EquipmentChildrenSearchResponse {

  @JsonProperty("query")
  private final EquipmentChildrenSearchCriteria query;

  @JsonProperty("totalRows")
  private final int totalRows;

  @JsonProperty("data")
  private final List<EntityIndex> data;


  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(EquipmentChildrenSearchResponse response) {
    return new Builder(response);
  }

  private EquipmentChildrenSearchResponse(Builder builder) {
    this.query = builder.query;
    this.totalRows = builder.totalRows;
    this.data = builder.data;
  }

  public List<EntityIndex> getData() {
    return data;
  }

  public int getTotalRows() {
    return totalRows;
  }

  public EquipmentChildrenSearchCriteria getQuery() {
    return query;
  }

  public static class Builder {
    private EquipmentChildrenSearchCriteria query;
    private int totalRows;
    private List<EntityIndex> data = new ArrayList<>();;

    private Builder() {}

    private Builder(EquipmentChildrenSearchResponse response) {
      requireNonNull(response, "response cannot be null");
      this.query = response.query;
      this.data = response.data;
    }

    public Builder withQuery(EquipmentChildrenSearchCriteria query) {
      this.query = query;
      return this;
    }

    public Builder withTotalRows(int totalRows) {
      this.totalRows = totalRows;
      return this;
    }

    public Builder withData(List<EntityIndex> data) {
      this.data = ImmutableList.copyOf(data);
      return this;
    }

    public EquipmentChildrenSearchResponse build() {
      requireNonNull(data, "data cannot be null");
      return new EquipmentChildrenSearchResponse(this);
    }
  }
}
