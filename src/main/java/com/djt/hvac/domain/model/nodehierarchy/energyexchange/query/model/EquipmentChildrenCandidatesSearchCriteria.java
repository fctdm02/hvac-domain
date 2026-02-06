package com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableList;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = EquipmentChildrenCandidatesSearchCriteria.Builder.class)
public class EquipmentChildrenCandidatesSearchCriteria {
  
  public static final String WILDCARD = "*";

  public static final Integer ZERO = Integer.valueOf(0);
  public static final Integer ONE_HUNDRED = Integer.valueOf(100);

  public static final Integer DEFAULT_LIMIT = ONE_HUNDRED;
  public static final Integer DEFAULT_OFFSET = ZERO;

  public static final Integer MIN_VALUE = ZERO;

  public static final String SORT_DIRECTION_ASC = "asc";
  public static final String SORT_DIRECTION_DESC = "desc";


  private final Integer buildingId;
  private final Integer limit;
  private final Integer offset;
  private final String sortDirection;
  private final String searchString; // Search based on a partial fully qualified name
  private final List<Integer> excludedEquipmentIds;
  private final Integer equipmentId;

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(EquipmentChildrenCandidatesSearchCriteria pointQuery) {
    return new Builder(pointQuery);
  }

  private EquipmentChildrenCandidatesSearchCriteria(Builder builder) {

    this.buildingId = builder.buildingId;
    this.limit = builder.limit;
    this.offset = builder.offset;
    this.sortDirection = builder.sortDirection;
    this.searchString = builder.searchString;
    this.excludedEquipmentIds = builder.excludedEquipmentIds;
    this.equipmentId = builder.equipmentId;
  }
  
  public Integer getBuildingId() {
    return buildingId;
  }

  public Integer getLimit() {
    return limit;
  }

  public Integer getOffset() {
    return offset;
  }

  public String getSortDirection() {
    return sortDirection;
  }

  public String getSearchString() {
    return searchString;
  }

  public List<Integer> getExcludedEquipmentIds() {
    return excludedEquipmentIds;
  }

  public Integer getEquipmentId() {
    return equipmentId;
  }

  public static class Builder {
    private Integer buildingId;
    private Integer limit = DEFAULT_LIMIT;
    private Integer offset = DEFAULT_OFFSET;
    private String sortDirection = SORT_DIRECTION_ASC;
    private String searchString = WILDCARD;
    private List<Integer> excludedEquipmentIds = new ArrayList<>();
    private Integer equipmentId;

    private Builder() {}

    private Builder(EquipmentChildrenCandidatesSearchCriteria criteria) {
      requireNonNull(criteria, "criteria cannot be null");
      this.buildingId = criteria.buildingId;
      this.limit = criteria.limit;
      this.offset = criteria.offset;
      this.sortDirection = criteria.sortDirection;
      this.searchString = criteria.searchString;
      this.excludedEquipmentIds = criteria.excludedEquipmentIds;
      this.equipmentId = criteria.equipmentId;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withBuildingId(Integer buildingId) {
      this.buildingId = buildingId;
      return this;
    }

    public Builder withExcludedEquipmentIds(List<Integer> excludedEquipmentIds) {
      if (excludedEquipmentIds != null) {
        requireNonNull(excludedEquipmentIds, "excludedEquipmentIds cannot be null");
        this.excludedEquipmentIds = ImmutableList.copyOf(excludedEquipmentIds);
      }
      return this;
    }

    public Builder withLimit(Integer limit) {
      if (limit != null) {
        checkArgument(limit.intValue() > MIN_VALUE, "limit must be greater than " + MIN_VALUE);
        this.limit = limit;
      }
      return this;
    }

    public Builder withOffset(Integer offset) {
      if (offset != null) {
        checkArgument(offset.intValue() >= MIN_VALUE, "offset must be greater than or equal to " + MIN_VALUE);
        this.offset = offset;
      }
      return this;
    }

    public Builder withSortDirection(String sortDirection) {
      if (sortDirection != null) {
        if (!sortDirection.equals(SORT_DIRECTION_ASC) && !sortDirection.equals(SORT_DIRECTION_DESC)) {
          throw new IllegalArgumentException("sortDirection must be one of: [asc, desc] yet was: [" + sortDirection + "].");
        }
        this.sortDirection = sortDirection;
      }
      return this;
    }

    public Builder withSearchString(String searchString) {
      if (searchString != null) {
        checkArgument(!searchString.trim().isEmpty(),"searchString must include at least one non-space character");
        this.searchString = searchString;
      }
      return this;
    }

    public Builder withEquipmentId(int equipmentId) {
      this.equipmentId = equipmentId;
      return this;
    }

    public EquipmentChildrenCandidatesSearchCriteria build() {
      requireNonNull(excludedEquipmentIds, "excludedEquipmentIds cannot be null");
      return new EquipmentChildrenCandidatesSearchCriteria(this);
    }
  }
}