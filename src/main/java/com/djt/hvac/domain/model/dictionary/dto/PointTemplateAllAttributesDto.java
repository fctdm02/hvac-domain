package com.djt.hvac.domain.model.dictionary.dto;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = PointTemplateAllAttributesDto.Builder.class)
public class PointTemplateAllAttributesDto implements Comparable<PointTemplateAllAttributesDto> {
  private final Integer id;
  private final String name;
  private final String description;
  private final String tags;
  private final String unit;
  private final Boolean isPublic;
  private final Boolean isDeprecated;
  private final String replacementPointTemplateId;
  private final String parentNodeTypes;
  private final String parentEnergyExchangeTypes;
  private final String referencedAdFunctionTemplates;
  private final String referencedReportTemplates;

  @JsonCreator
  public static Builder builder () {
    return new Builder();
  }

  public static Builder builder (PointTemplateAllAttributesDto pointTemplateAllAttributesDto) {
    return new Builder(pointTemplateAllAttributesDto);
  }

  private PointTemplateAllAttributesDto (Builder builder) {
    this.id = builder.id;
    this.name = builder.name;
    this.description = builder.description;
    this.tags = builder.tags;
    this.unit = builder.unit;
    this.isPublic = builder.isPublic;
    this.isDeprecated = builder.isDeprecated;
    this.replacementPointTemplateId = builder.replacementPointTemplateId;
    this.parentNodeTypes = builder.parentNodeTypes;
    this.parentEnergyExchangeTypes = builder.parentEnergyExchangeTypes;
    this.referencedAdFunctionTemplates = builder.referencedAdFunctionTemplates;
    this.referencedReportTemplates = builder.referencedReportTemplates;
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

  public String getTags() {
    return tags;
  }

  public String getUnit() {
    return unit;
  }
  
  public Boolean getIsPublic() {
    return isPublic;
  }
  
  public Boolean getIsDeprecated() {
    return isDeprecated;
  }
  
  public String getReplacementPointTemplateId() {
    return replacementPointTemplateId;
  }
  
  public String getParentNodeTypes() {
    return parentNodeTypes;
  }

  public String getParentEnergyExchangeTypes() {
    return parentEnergyExchangeTypes;
  }
  
  public String getReferencedAdFunctionTemplates() {
    return referencedAdFunctionTemplates;
  }
  
  public String getReferencedReportTemplates() {
    return referencedReportTemplates;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((isDeprecated == null) ? 0 : isDeprecated.hashCode());
    result = prime * result + ((isPublic == null) ? 0 : isPublic.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result
        + ((parentEnergyExchangeTypes == null) ? 0 : parentEnergyExchangeTypes.hashCode());
    result = prime * result + ((parentNodeTypes == null) ? 0 : parentNodeTypes.hashCode());
    result = prime * result
        + ((referencedAdFunctionTemplates == null) ? 0 : referencedAdFunctionTemplates.hashCode());
    result = prime * result
        + ((referencedReportTemplates == null) ? 0 : referencedReportTemplates.hashCode());
    result = prime * result
        + ((replacementPointTemplateId == null) ? 0 : replacementPointTemplateId.hashCode());
    result = prime * result + ((tags == null) ? 0 : tags.hashCode());
    result = prime * result + ((unit == null) ? 0 : unit.hashCode());
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
    PointTemplateAllAttributesDto other = (PointTemplateAllAttributesDto) obj;
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
    if (isDeprecated == null) {
      if (other.isDeprecated != null)
        return false;
    } else if (!isDeprecated.equals(other.isDeprecated))
      return false;
    if (isPublic == null) {
      if (other.isPublic != null)
        return false;
    } else if (!isPublic.equals(other.isPublic))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (parentEnergyExchangeTypes == null) {
      if (other.parentEnergyExchangeTypes != null)
        return false;
    } else if (!parentEnergyExchangeTypes.equals(other.parentEnergyExchangeTypes))
      return false;
    if (parentNodeTypes == null) {
      if (other.parentNodeTypes != null)
        return false;
    } else if (!parentNodeTypes.equals(other.parentNodeTypes))
      return false;
    if (referencedAdFunctionTemplates == null) {
      if (other.referencedAdFunctionTemplates != null)
        return false;
    } else if (!referencedAdFunctionTemplates.equals(other.referencedAdFunctionTemplates))
      return false;
    if (referencedReportTemplates == null) {
      if (other.referencedReportTemplates != null)
        return false;
    } else if (!referencedReportTemplates.equals(other.referencedReportTemplates))
      return false;
    if (replacementPointTemplateId == null) {
      if (other.replacementPointTemplateId != null)
        return false;
    } else if (!replacementPointTemplateId.equals(other.replacementPointTemplateId))
      return false;
    if (tags == null) {
      if (other.tags != null)
        return false;
    } else if (!tags.equals(other.tags))
      return false;
    if (unit == null) {
      if (other.unit != null)
        return false;
    } else if (!unit.equals(other.unit))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder2 = new StringBuilder();
    builder2.append("PointTemplateAllAttributesDto [id=").append(id).append(", name=").append(name)
        .append(", description=").append(description).append(", tags=").append(tags)
        .append(", unit=").append(unit).append(", isPublic=").append(isPublic)
        .append(", isDeprecated=").append(isDeprecated).append(", replacementPointTemplateId=")
        .append(replacementPointTemplateId).append(", parentNodeTypes=").append(parentNodeTypes)
        .append(", parentEnergyExchangeTypes=").append(parentEnergyExchangeTypes)
        .append(", referencedAdFunctionTemplates=").append(referencedAdFunctionTemplates)
        .append(", referencedReportTemplates=").append(referencedReportTemplates).append("]");
    return builder2.toString();
  }

  @Override
  public int compareTo(PointTemplateAllAttributesDto that) {
    return this.name.compareTo(that.name);
  }  

  @JsonPOJOBuilder
  public static class Builder {
    private Integer id;
    private String name;
    private String description;
    private String tags;
    private String unit;
    private Boolean isPublic;
    private Boolean isDeprecated;
    private String replacementPointTemplateId;
    private String parentNodeTypes;
    private String parentEnergyExchangeTypes;
    private String referencedAdFunctionTemplates;
    private String referencedReportTemplates;

    private Builder() {}

    private Builder(PointTemplateAllAttributesDto pointTemplateAllAttributesDto) {
      requireNonNull(pointTemplateAllAttributesDto, "pointTemplateAllAttributesDto cannot be null");
      this.id = pointTemplateAllAttributesDto.id;
      this.name = pointTemplateAllAttributesDto.name;
      this.description = pointTemplateAllAttributesDto.description;
      this.tags = pointTemplateAllAttributesDto.tags;
      this.unit = pointTemplateAllAttributesDto.unit;
      this.parentNodeTypes = pointTemplateAllAttributesDto.parentNodeTypes;
      this.parentEnergyExchangeTypes = pointTemplateAllAttributesDto.parentEnergyExchangeTypes;
      this.referencedAdFunctionTemplates = pointTemplateAllAttributesDto.referencedAdFunctionTemplates;
      this.referencedReportTemplates = pointTemplateAllAttributesDto.referencedReportTemplates;
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

    public Builder withTags(String tags) {
      requireNonNull(tags, "tags cannot be null");
      this.tags = tags;
      return this;
    }

    public Builder withUnit(String unit) {
      this.unit = unit;
      return this;
    }

    public Builder withIsPublic(Boolean isPublic) {
      requireNonNull(isPublic, "isPublic cannot be null");
      this.isPublic = isPublic;
      return this;
    }

    public Builder withIsDeprecated(Boolean isDeprecated) {
      requireNonNull(isDeprecated, "isDeprecated cannot be null");
      this.isDeprecated = isDeprecated;
      return this;
    }

    public Builder withReplacementPointTemplateId(String replacementPointTemplateId) {
      this.replacementPointTemplateId = replacementPointTemplateId;
      return this;
    }
    
    public Builder withParentNodeTypes(String parentNodeTypes) {
      requireNonNull(parentNodeTypes, "parentNodeTypes cannot be null");
      if (parentNodeTypes.isEmpty()) {
        throw new IllegalArgumentException("parentNodeTypes cannot be empty");
      }
      this.parentNodeTypes = parentNodeTypes;
      return this;
    }
    
    public Builder withParentEnergyExchangeTypes(String parentEnergyExchangeTypes) {
      this.parentEnergyExchangeTypes = parentEnergyExchangeTypes;
      return this;
    }

    public Builder withReferencedAdFunctionTemplates(String referencedAdFunctionTemplates) {
      this.referencedAdFunctionTemplates = referencedAdFunctionTemplates;
      return this;
    }

    public Builder withReferencedReportTemplates(String referencedReportTemplates) {
      this.referencedReportTemplates = referencedReportTemplates;
      return this;
    }
    
    public PointTemplateAllAttributesDto build() {
      requireNonNull(id, "id cannot be null");
      requireNonNull(name, "name cannot be null");
      requireNonNull(description, "description cannot be null");
      requireNonNull(tags, "tags cannot be null");
      requireNonNull(isPublic, "isPublic cannot be null");
      requireNonNull(isDeprecated, "isDeprecated cannot be null");
      requireNonNull(parentNodeTypes, "parentNodeTypes cannot be null");
      return new PointTemplateAllAttributesDto(this);
    }
  }
}