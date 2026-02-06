package com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableList;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = EquipmentUpdateRequest.Builder.class)
public class EquipmentUpdateRequest {

  private final List<Integer> equipmentIds;

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(EquipmentUpdateRequest pointQuery) {
    return new Builder(pointQuery);
  }

  private EquipmentUpdateRequest(Builder builder) {
    this.equipmentIds = builder.equipmentIds;
  }

  public List<Integer> getEquipmentIds() {
    return equipmentIds;
  }

  public static class Builder {
    private List<Integer> equipmentIds = new ArrayList<>();

    private Builder() {}

    private Builder(EquipmentUpdateRequest equipmentUpdateRequest) {
      requireNonNull(equipmentUpdateRequest, "equipmentUpdateRequest cannot be null");
      this.equipmentIds = equipmentUpdateRequest.equipmentIds;
    }

    public Builder withEquipmentIds(List<Integer> equipmentIds) {
      requireNonNull(equipmentIds, "equipmentIds cannot be null");
      this.equipmentIds = ImmutableList.copyOf(equipmentIds);
      return this;
    }

    public EquipmentUpdateRequest build() {
      requireNonNull(equipmentIds, "equipmentIds cannot be null");
      return new EquipmentUpdateRequest(this);
    }

  }
}