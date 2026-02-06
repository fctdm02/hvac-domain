package com.djt.hvac.domain.model.function.status;

import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = AdFunctionErrorMessageSearchCriteria.Builder.class)
public class AdFunctionErrorMessageSearchCriteria {
  
  public static final Integer FUNCTION_TYPE_RULE = Integer.valueOf(1);
  public static final Integer FUNCTION_TYPE_COMPUTED_POINT = Integer.valueOf(2);
  
  public static final Integer ZERO = Integer.valueOf(0);
  public static final Integer ONE_HUNDRED = Integer.valueOf(100);
  public static final Integer ONE_THOUSAND = Integer.valueOf(1000);
  
  public static final String SORT_AD_FUNCTION_TEMPLATE_NAME = "templateName";
  public static final String SORT_ENERGY_EXCHANGE_TYPE_NAME = "energyExchangeTypeName";
  public static final String SORT_NODE_NAME = "nodeName";
  public static final String SORT_NODE_PATH = "nodePath";

  public static final String SORT_DIRECTION_ASC = "asc";
  public static final String SORT_DIRECTION_DESC = "desc";   
  
  public static final Integer DEFAULT_LIMIT = ONE_HUNDRED;
  public static final Integer DEFAULT_OFFSET = ZERO;
  
  public static final String DEFAULT_SORT = SORT_NODE_PATH;
  public static final String DEFAULT_SORT_DIRECTION = SORT_DIRECTION_ASC;

  
  private final Integer functionTypeId;
  private final Integer energyExchangeTypeId;
  private final Integer energyExchangeId;
  private final Integer adFunctionTemplateId;
  private final String nodePath;
  private final String sort;
  private final String sortDirection;
  private final Integer limit;
  private final Integer offset;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (AdFunctionErrorMessageSearchCriteria reportEquipmentErrorMessageSearchCriteria) {
    return new Builder(reportEquipmentErrorMessageSearchCriteria);
  }

  private AdFunctionErrorMessageSearchCriteria (Builder builder) {
    this.functionTypeId = builder.functionTypeId;
    this.energyExchangeTypeId = builder.energyExchangeTypeId;
    this.energyExchangeId = builder.energyExchangeId;
    this.adFunctionTemplateId = builder.adFunctionTemplateId;
    this.nodePath = builder.nodePath;
    this.sort = builder.sort;
    this.sortDirection = builder.sortDirection;
    this.limit = builder.limit;
    this.offset = builder.offset;
  }
  
  public Integer getFunctionTypeId() {
    return functionTypeId;
  }
  
  public Integer getEnergyExchangeTypeId() {
    return energyExchangeTypeId;
  }

  public Integer getEnergyExchangeId() {
    return energyExchangeId;
  }

  public Integer getAdFunctionTemplateId() {
    return adFunctionTemplateId;
  }

  public String getNodePath() {
    return nodePath;
  }

  public String getSort() {
    return sort;
  }
  
  public String getSortDirection() {
    return sortDirection;
  }

  public Integer getLimit() {
    return limit;
  }

  public Integer getOffset() {
    return offset;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result =
        prime * result + ((adFunctionTemplateId == null) ? 0 : adFunctionTemplateId.hashCode());
    result = prime * result + ((energyExchangeId == null) ? 0 : energyExchangeId.hashCode());
    result = prime * result + ((functionTypeId == null) ? 0 : functionTypeId.hashCode());
    result = prime * result + ((limit == null) ? 0 : limit.hashCode());
    result = prime * result + ((nodePath == null) ? 0 : nodePath.hashCode());
    result = prime * result + ((offset == null) ? 0 : offset.hashCode());
    result = prime * result + ((sort == null) ? 0 : sort.hashCode());
    result = prime * result + ((sortDirection == null) ? 0 : sortDirection.hashCode());
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
    AdFunctionErrorMessageSearchCriteria other = (AdFunctionErrorMessageSearchCriteria) obj;
    if (adFunctionTemplateId == null) {
      if (other.adFunctionTemplateId != null)
        return false;
    } else if (!adFunctionTemplateId.equals(other.adFunctionTemplateId))
      return false;
    if (energyExchangeId == null) {
      if (other.energyExchangeId != null)
        return false;
    } else if (!energyExchangeId.equals(other.energyExchangeId))
      return false;
    if (functionTypeId == null) {
      if (other.functionTypeId != null)
        return false;
    } else if (!functionTypeId.equals(other.functionTypeId))
      return false;
    if (limit == null) {
      if (other.limit != null)
        return false;
    } else if (!limit.equals(other.limit))
      return false;
    if (nodePath == null) {
      if (other.nodePath != null)
        return false;
    } else if (!nodePath.equals(other.nodePath))
      return false;
    if (offset == null) {
      if (other.offset != null)
        return false;
    } else if (!offset.equals(other.offset))
      return false;
    if (sort == null) {
      if (other.sort != null)
        return false;
    } else if (!sort.equals(other.sort))
      return false;
    if (sortDirection == null) {
      if (other.sortDirection != null)
        return false;
    } else if (!sortDirection.equals(other.sortDirection))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder2 = new StringBuilder();
    builder2.append("AdFunctionErrorMessageSearchCriteria [functionTypeId=").append(functionTypeId)
        .append(", energyExchangeId=").append(energyExchangeId).append(", adFunctionTemplateId=")
        .append(adFunctionTemplateId).append(", nodePath=").append(nodePath).append(", sort=")
        .append(sort).append(", sortDirection=").append(sortDirection).append(", limit=")
        .append(limit).append(", offset=").append(offset).append("]");
    return builder2.toString();
  }

  private static <T> T requireNonNull(T obj, String message) {
    if (obj ==  null) {
      throw new IllegalArgumentException(message);
    }
    return obj;
  }
  
  @JsonPOJOBuilder
  public static class Builder {
    private Integer functionTypeId;
    private Integer energyExchangeTypeId;
    private Integer energyExchangeId;
    private Integer adFunctionTemplateId;
    private String nodePath;
    private String sort = SORT_NODE_PATH;
    private String sortDirection = SORT_DIRECTION_ASC;
    private Integer limit = ONE_THOUSAND;
    private Integer offset = ZERO;

    private Builder() {}

    private Builder(AdFunctionErrorMessageSearchCriteria criteria) {
      requireNonNull(criteria, "criteria cannot be null");
      this.functionTypeId = criteria.functionTypeId;
      this.energyExchangeTypeId = criteria.energyExchangeTypeId;
      this.energyExchangeId = criteria.energyExchangeId;
      this.adFunctionTemplateId = criteria.adFunctionTemplateId;
      this.nodePath = criteria.nodePath;
      this.sortDirection = criteria.sortDirection;
      this.limit = criteria.limit;
      this.offset = criteria.offset;
    }

    public Builder with(Consumer<Builder> consumer) {
      requireNonNull(consumer, "consumer cannot be null");
      consumer.accept(this);
      return this;
    }

    public Builder withFunctionTypeId(Integer functionTypeId) {
      requireNonNull(functionTypeId, "functionTypeId cannot be null");
      if (functionTypeId.equals(FUNCTION_TYPE_RULE) && functionTypeId.equals(FUNCTION_TYPE_COMPUTED_POINT)) {
        throw new IllegalArgumentException("'functionTypeId' must be either '1' for Rule or '2' for Computed Point");
      }
      this.functionTypeId = functionTypeId;
      return this;
    }

    public Builder withEnergyExchangeTypeId(Integer energyExchangeTypeId) {
      this.energyExchangeTypeId = energyExchangeTypeId;
      return this;
    }
    
    public Builder withEnergyExchangeId(Integer energyExchangeId) {
      this.energyExchangeId = energyExchangeId;
      return this;
    }

    public Builder withAdFunctionTemplateId(Integer adFunctionTemplateId) {
      this.adFunctionTemplateId = adFunctionTemplateId;
      return this;
    }

    public Builder withNodePath(String nodePath) {
      this.nodePath = nodePath;
      return this;
    }

    public Builder withSort(String sort) {
      requireNonNull(sort, "sort cannot be null");
      this.sort = sort;
      return this;
    }
    
    public Builder withSortDirection(String sortDirection) {
      requireNonNull(sortDirection, "sortDirection cannot be null");
      this.sortDirection = sortDirection;
      return this;
    }

    public Builder withLimit(Integer limit) {
      requireNonNull(limit, "limit cannot be null");
      this.limit = limit;
      return this;
    }

    public Builder withOffset(Integer offset) {
      requireNonNull(offset, "offset cannot be null");
      this.offset = offset;
      return this;
    }

    public AdFunctionErrorMessageSearchCriteria build() {
      requireNonNull(functionTypeId, "functionTypeId cannot be null");
      if (sort != null && !sort.equals(SORT_AD_FUNCTION_TEMPLATE_NAME) && !sort.equals(SORT_ENERGY_EXCHANGE_TYPE_NAME) && !sort.equals(SORT_NODE_NAME) && !sort.equals(SORT_NODE_PATH)) {
        throw new IllegalArgumentException("'sort' must be either 'templateName', 'energyExchangeTypeName', 'nodeName' or 'nodePath'");
      }
      if (sortDirection != null && !sortDirection.equals("asc") && !sortDirection.equals("desc")) {
        throw new IllegalArgumentException("'sortDirection' must be either 'asc' or 'desc'");
      }
      if (limit < 100 || limit > 1000) {
        throw new IllegalArgumentException("'limit' must be between 100 and 1,000 (inclusive)");
      }
      if (offset < 0) {
        throw new IllegalArgumentException("'offset' must be between 0 or greater");
      }
      if (nodePath != null && nodePath.trim().equals("")) {
        throw new IllegalArgumentException("'nodePath', if specified, must be non-empty");
      }
      return new AdFunctionErrorMessageSearchCriteria(this);
    }
  }
}