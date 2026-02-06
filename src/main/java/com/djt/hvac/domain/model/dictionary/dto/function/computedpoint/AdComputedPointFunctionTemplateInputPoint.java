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
@JsonDeserialize(builder = AdComputedPointFunctionTemplateInputPoint.Builder.class)
public class AdComputedPointFunctionTemplateInputPoint implements Comparable<AdComputedPointFunctionTemplateInputPoint> {
  
  private final Integer id;
  private final String name;
  private final String description;
  private final String currentObjectExpression;
  private final Boolean isRequired;
  private final Boolean isArray;
  private final Integer sequenceNumber;
  private final String tags;
  private transient String _normalizedTags;
  private transient Set<String> _normalizedTagsAsSet;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (AdComputedPointFunctionTemplateInputPoint ruleTemplateInputPoint) {
    return new Builder(ruleTemplateInputPoint);
  }

  private AdComputedPointFunctionTemplateInputPoint (Builder builder) {
    this.id = builder.id;
    this.name = builder.name;
    this.description = builder.description;
    this.currentObjectExpression = builder.currentObjectExpression;
    this.isRequired = builder.isRequired;
    this.isArray = builder.isArray;
    this.sequenceNumber = builder.sequenceNumber;
    this.tags = builder.tags;
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

  public String getCurrentObjectExpression() {
    return currentObjectExpression;
  }

  public Boolean getIsRequired() {
    return isRequired;
  }

  public Boolean getIsArray() {
    return isArray;
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
    private String name;
    private String description;
    private String currentObjectExpression;
    private Boolean isRequired;
    private Boolean isArray;
    private Integer sequenceNumber;
    private String tags;

    private Builder() {}

    private Builder(AdComputedPointFunctionTemplateInputPoint ruleTemplateInputPoint) {
      requireNonNull(ruleTemplateInputPoint, "ruleTemplateInputPoint cannot be null");
      this.id = ruleTemplateInputPoint.id;
      this.name = ruleTemplateInputPoint.name;
      this.description = ruleTemplateInputPoint.description;
      this.currentObjectExpression = ruleTemplateInputPoint.currentObjectExpression;
      this.isRequired = ruleTemplateInputPoint.isRequired;
      this.isArray = ruleTemplateInputPoint.isArray;
      this.sequenceNumber = ruleTemplateInputPoint.sequenceNumber;
      this.tags = ruleTemplateInputPoint.tags;
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

    public Builder withCurrentObjectExpression(String currentObjectExpression) {
      this.currentObjectExpression = currentObjectExpression;
      return this;
    }

    public Builder withIsRequired(Boolean isRequired) {
      requireNonNull(isRequired, "isRequired cannot be null");
      this.isRequired = isRequired;
      return this;
    }

    public Builder withIsArray(Boolean isArray) {
      requireNonNull(isArray, "isArray cannot be null");
      this.isArray = isArray;
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
    
    public AdComputedPointFunctionTemplateInputPoint build() {
      requireNonNull(id, "id cannot be null");
      requireNonNull(name, "name cannot be null");
      requireNonNull(description, "description cannot be null");
      requireNonNull(isRequired, "isRequired cannot be null");
      requireNonNull(isArray, "isArray cannot be null");
      requireNonNull(sequenceNumber, "sequenceNumber cannot be null");
      requireNonNull(tags, "tags cannot be null");
      return new AdComputedPointFunctionTemplateInputPoint(this);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((currentObjectExpression == null) ? 0 : currentObjectExpression.hashCode());
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((isArray == null) ? 0 : isArray.hashCode());
    result = prime * result + ((isRequired == null) ? 0 : isRequired.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((sequenceNumber == null) ? 0 : sequenceNumber.hashCode());
    result = prime * result + ((tags == null) ? 0 : tags.hashCode());
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
    AdComputedPointFunctionTemplateInputPoint other = (AdComputedPointFunctionTemplateInputPoint) obj;
    if (currentObjectExpression == null) {
      if (other.currentObjectExpression != null)
        return false;
    } else if (!currentObjectExpression.equals(other.currentObjectExpression))
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
    if (isArray == null) {
      if (other.isArray != null)
        return false;
    } else if (!isArray.equals(other.isArray))
      return false;
    if (isRequired == null) {
      if (other.isRequired != null)
        return false;
    } else if (!isRequired.equals(other.isRequired))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
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
    return true;
  }

  @Override
  public String toString() {
    return "RuleTemplateInputPointDto [id=" + id + ", name=" + name + ", description=" + description
        + ", currentObjectExpression=" + currentObjectExpression + ", isRequired=" + isRequired
        + ", isArray=" + isArray + ", sequenceNumber=" + sequenceNumber + ", tags=" + getNormalizedTagsAsSet() + "]";
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
  
  public int compareTo(AdComputedPointFunctionTemplateInputPoint that) {

    return this.sequenceNumber.compareTo(((AdComputedPointFunctionTemplateInputPoint) that).sequenceNumber);
  }    
}