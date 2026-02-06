package com.djt.hvac.domain.model.dictionary.dto.function.computedpoint;

import static java.util.Objects.requireNonNull;

import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = AdComputedPointFunctionTemplateOutputPoint.Builder.class)
public class AdComputedPointFunctionTemplateOutputPoint implements Comparable<AdComputedPointFunctionTemplateOutputPoint> {
  
  private final Integer id;
  private final String description;
  private final Integer dataTypeId;
  private final Integer unitId;
  private final String range;
  private final Integer sequenceNumber;
  private final String tags;
  private transient String _normalizedTags;
  private transient Set<String> _normalizedTagsAsSet;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (AdComputedPointFunctionTemplateOutputPoint ruleTemplateOutputPoint) {
    return new Builder(ruleTemplateOutputPoint);
  }

  private AdComputedPointFunctionTemplateOutputPoint (Builder builder) {
    this.id = builder.id;
    this.description = builder.description;
    this.dataTypeId = builder.dataTypeId;
    this.unitId = builder.unitId;
    this.range = builder.range;
    this.sequenceNumber = builder.sequenceNumber;
    this.tags = builder.tags;
  }

  public Integer getId() {
    return id;
  }

  public String getDescription() {
    return description;
  }

  public Integer getDataTypeId() {
    return dataTypeId;
  }

  public Integer getUnitId() {
    return unitId;
  }

  public String getRange() {
    return range;
  }

  public Integer getSequenceNumber() {
    return sequenceNumber;
  }

  public String getTags() {
    return tags;
  }
  
  @JsonPOJOBuilder
  public static class Builder {
    private Integer id;
    private String description;
    private Integer dataTypeId;
    private Integer unitId;
    private String range;
    private Integer sequenceNumber;
    private String tags;

    private Builder() {}

    private Builder(AdComputedPointFunctionTemplateOutputPoint ruleTemplateOutputPoint) {
      requireNonNull(ruleTemplateOutputPoint, "ruleTemplateOutputPoint cannot be null");
      this.id = ruleTemplateOutputPoint.id;
      this.description = ruleTemplateOutputPoint.description;
      this.dataTypeId = ruleTemplateOutputPoint.dataTypeId;
      this.unitId = ruleTemplateOutputPoint.unitId;
      this.range = ruleTemplateOutputPoint.range;
      this.sequenceNumber = ruleTemplateOutputPoint.sequenceNumber;
      this.tags = ruleTemplateOutputPoint.tags;
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

    public Builder withDescription(String description) {
      requireNonNull(description, "description cannot be null");
      this.description = description;
      return this;
    }

    public Builder withDataTypeId(Integer dataTypeId) {
      requireNonNull(dataTypeId, "dataTypeId cannot be null");
      this.dataTypeId = dataTypeId;
      return this;
    }

    public Builder withUnitId(Integer unitId) {
      requireNonNull(unitId, "unitId cannot be null");
      this.unitId = unitId;
      return this;
    }

    public Builder withRange(String range) {
      this.range = range;
      return this;
    }

    public Builder withSequenceNumber(Integer sequenceNumber) {
      requireNonNull(sequenceNumber, "sequenceNumber cannot be null");
      this.sequenceNumber = sequenceNumber;
      return this;
    }

    public Builder withTags(String tags) {
      requireNonNull(tags, "tags cannot be null");
      this.tags = tags;
      return this;
    }
    
    public AdComputedPointFunctionTemplateOutputPoint build() {
      requireNonNull(id, "id cannot be null");
      requireNonNull(description, "description cannot be null");
      requireNonNull(dataTypeId, "dataTypeId cannot be null");
      requireNonNull(unitId, "unitId cannot be null");
      requireNonNull(sequenceNumber, "sequenceNumber cannot be null");
      return new AdComputedPointFunctionTemplateOutputPoint(this);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((dataTypeId == null) ? 0 : dataTypeId.hashCode());
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((range == null) ? 0 : range.hashCode());
    result = prime * result + ((sequenceNumber == null) ? 0 : sequenceNumber.hashCode());
    result = prime * result + ((tags == null) ? 0 : tags.hashCode());
    result = prime * result + ((unitId == null) ? 0 : unitId.hashCode());
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
    AdComputedPointFunctionTemplateOutputPoint other = (AdComputedPointFunctionTemplateOutputPoint) obj;
    if (dataTypeId == null) {
      if (other.dataTypeId != null)
        return false;
    } else if (!dataTypeId.equals(other.dataTypeId))
      return false;
    if (description == null) {
      if (other.description != null)
        return false;
    } else if (!description.equals(other.description))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (range == null) {
      if (other.range != null)
        return false;
    } else if (!range.equals(other.range))
      return false;
    if (sequenceNumber == null) {
      if (other.sequenceNumber != null)
        return false;
    } else if (!sequenceNumber.equals(other.sequenceNumber))
      return false;
    if (tags == null) {
      if (other.tags != null)
        return false;
    } else if (!tags.equals(other.tags))
      return false;
    if (unitId == null) {
      if (other.unitId != null)
        return false;
    } else if (!unitId.equals(other.unitId))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder2 = new StringBuilder();
    builder2.append("RuleTemplateOutputPoint [id=").append(id).append(", description=")
        .append(description).append(", dataTypeId=").append(dataTypeId).append(", unitId=")
        .append(unitId).append(", range=").append(range).append(", sequenceNumber=")
        .append(sequenceNumber).append(", tags=").append(tags).append("]");
    return builder2.toString();
  }
  
  @JsonIgnore
  public String getNormalizedTags() {
    
    if (_normalizedTags == null) {
      if (tags == null) {
        _normalizedTags = "";
      } else {
        String[] tagArray = tags.split(",");
        Set<String> set = new TreeSet<>();
        for (int i=0; i < tagArray.length; i++) {
          set.add(tagArray[i]);
        }
        _normalizedTags = set.toString().replaceAll(", ",  ",").replace("[", "").replace("]", "").replace("\"", "");  
      }
    }
    return _normalizedTags;
  }
  
  @JsonIgnore
  public Set<String> getNormalizedTagsAsSet() {
    
    if (_normalizedTagsAsSet == null) {
      _normalizedTagsAsSet = new TreeSet<>();
      if (tags != null) {
        String[] tagArray = tags.split(",");
        for (int i=0; i < tagArray.length; i++) {
          _normalizedTagsAsSet.add(tagArray[i]);
        }
      }
    }
    return _normalizedTagsAsSet;
  }
  
  public int compareTo(AdComputedPointFunctionTemplateOutputPoint that) {

    return this.sequenceNumber.compareTo(((AdComputedPointFunctionTemplateOutputPoint) that).sequenceNumber);
  }    
}