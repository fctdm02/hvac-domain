
package com.djt.hvac.domain.model.dictionary.dto;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/*
<pre>
-- All Node Tag Templates
SELECT 
  pt.id, 
  pt.name, 
  pt.description,
  pt.is_public,
  pt.parent_node_type_id,
  pt.is_deprecated,
  pt.replacement_point_template_id,
  pt.unit_id,
  parent_energy_exchange_type_ids,
  parent_energy_exchange_tag_group_ids,
  ARRAY_AGG(t.name) AS tags
FROM  
  (
  SELECT 
    pt.id,
    pt.name, 
    pt.description,
    pt.is_public,
    pt.parent_node_type_id,
    pt.is_deprecated,
    pt.replacement_point_template_id,
    pt.unit_id,
    ARRAY_AGG(ptet.tag_id) AS parent_energy_exchange_type_ids,
    ARRAY_AGG(DISTINCT(ptett.tag_group_id)) AS parent_energy_exchange_tag_group_ids
  FROM  
    point_templates pt 
    LEFT OUTER JOIN point_template_equipment_types ptet ON pt.id = ptet.node_template_id
    LEFT OUTER JOIN tags ptett ON ptet.tag_id = ptett.id
  GROUP BY 
    pt.id,
    pt.name, 
    pt.description,
    pt.is_public,
    pt.parent_node_type_id,
    pt.is_deprecated,
    pt.replacement_point_template_id,
    pt.unit_id
  ) pt 
  JOIN point_template_tags ptt ON ptt.node_template_id = pt.id 
  JOIN tags t ON t.id = ptt.tag_id 
GROUP BY 
  pt.id, 
  pt.name, 
  pt.description,
  pt.is_public,
  pt.parent_node_type_id,
  pt.is_deprecated,
  pt.replacement_point_template_id,
  pt.unit_id,
  pt.parent_energy_exchange_type_ids,
  pt.parent_energy_exchange_tag_group_ids
ORDER BY 
  pt.parent_energy_exchange_tag_group_ids DESC,
  pt.name;
</pre>
*/
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "parent_energy_exchange_type_ids",
    "parent_energy_exchange_tag_group_ids",
    "id",
    "name",
    "description",
    "tags",
    "is_public",
    "is_deprecated",
    "replacement_point_template_id",
    "parent_node_type_id",
    "unit_id",
    "units",
    "equipment"
})
public class NodeTagTemplateDto implements Serializable {

  private static final long serialVersionUID = 1852566501273642235L;
  
  @JsonProperty("parent_energy_exchange_type_ids")
  private String parentEnergyExchangeTypeIds;
  @JsonProperty("parent_energy_exchange_tag_group_ids")
  private String parentEnergyExchangeTagGroupIds;
  @JsonProperty("id")
  private Integer id;
  @JsonProperty("name")
  private String name;
  @JsonProperty("description")
  private String description;
  @JsonProperty("tags")
  private String tags;
  @JsonProperty("is_public")
  private Boolean isPublic;
  @JsonProperty("parent_node_type_id")
  private Integer parentNodeTypeId;
  @JsonProperty("is_deprecated")
  private Boolean isDeprecated = Boolean.FALSE;
  @JsonProperty("replacement_point_template_id")
  private Integer replacementPointTemplateId;
  @JsonProperty("unit_id")
  private Integer unitId;
  @JsonProperty("units")
  private String units;
  @JsonProperty("equipment")
  private String equipment;
  @JsonProperty("isDeleted")
  private String isDeleted;
  

  @JsonProperty("parent_energy_exchange_type_ids")
  public String getParentEnergyExchangeTypeIds() {
    return parentEnergyExchangeTypeIds;
  }

  @JsonProperty("parent_energy_exchange_type_ids")
  public void setParentEnergyExchangeTypeIds(String parentEnergyExchangeTypeIds) {
    this.parentEnergyExchangeTypeIds = parentEnergyExchangeTypeIds;
  }
  
  @JsonProperty("parent_energy_exchange_tag_group_ids")
  public String getParentEnergyExchangeTagGroupIds() {
    return parentEnergyExchangeTagGroupIds;
  }

  @JsonProperty("parent_energy_exchange_tag_group_ids")
  public void setParentEnergyExchangeTagGroupIds(String parentEnergyExchangeTagGroupIds) {
    this.parentEnergyExchangeTagGroupIds = parentEnergyExchangeTagGroupIds;
  }
  
  @JsonProperty("id")
  public Integer getId() {
    return id;
  }

  @JsonProperty("id")
  public void setId(Integer id) {
    this.id = id;
  }

  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  @JsonProperty("description")
  public void setDescription(String description) {
    this.description = description;
  }

  @JsonProperty("tags")
  public String getTags() {
    return tags;
  }

  @JsonProperty("tags")
  public void setTags(String tags) {
    this.tags = tags;
  }

  @JsonProperty("is_public")
  public Boolean getIsPublic() {
    return isPublic;
  }

  @JsonProperty("is_public")
  public void setIsPublic(Boolean isPublic) {
    this.isPublic = isPublic;
  }

  @JsonProperty("parent_node_type_id")
  public Integer getParentNodeTypeId() {
    return parentNodeTypeId;
  }

  @JsonProperty("parent_node_type_id")
  public void setParentNodeTypeId(Integer parentNodeTypeId) {
    this.parentNodeTypeId = parentNodeTypeId;
  }
  
  @JsonProperty("is_deprecated")
  public Boolean getIsDeprecated() {
    return isDeprecated;
  }

  @JsonProperty("is_deprecated")
  public void setIsDeprecated(Boolean isDeprecated) {
    if (isDeprecated != null) {
      this.isDeprecated = isDeprecated;
    }
  }

  @JsonProperty("replacement_point_template_id")
  public Integer getReplacementPointTemplateId() {
    return replacementPointTemplateId;
  }

  @JsonProperty("replacement_point_template_id")
  public void setReplacementPointTemplateId(Integer replacementPointTemplateId) {
    this.replacementPointTemplateId = replacementPointTemplateId;
  }  
  
  @JsonProperty("unit_id")
  public Integer getUnitId() {
    return unitId;
  }

  @JsonProperty("unit_id")
  public void setUnitId(Integer unitId) {
    this.unitId = unitId;
  }
  
  @JsonProperty("units")
  public String getUnits() {
    return units;
  }

  @JsonProperty("units")
  public void setUnits(String units) {
    this.units = units;
  }
  
  @JsonProperty("equipment")
  public String getEquipment() {
    return equipment;
  }

  @JsonProperty("equipment")
  public void setEquipment(String equipment) {
    this.equipment = equipment;
  }  

  @JsonProperty("isDeleted")
  public String getIsDeleted() {
    return isDeleted;
  }

  @JsonProperty("isDeleted")
  public void setIsDeleted(String isDeleted) {
    this.isDeleted = isDeleted;
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
    result = prime * result + ((parentEnergyExchangeTagGroupIds == null) ? 0
        : parentEnergyExchangeTagGroupIds.hashCode());
    result = prime * result
        + ((parentEnergyExchangeTypeIds == null) ? 0 : parentEnergyExchangeTypeIds.hashCode());
    result = prime * result + ((parentNodeTypeId == null) ? 0 : parentNodeTypeId.hashCode());
    result = prime * result
        + ((replacementPointTemplateId == null) ? 0 : replacementPointTemplateId.hashCode());
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
    NodeTagTemplateDto other = (NodeTagTemplateDto) obj;
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
    if (parentEnergyExchangeTagGroupIds == null) {
      if (other.parentEnergyExchangeTagGroupIds != null)
        return false;
    } else if (!parentEnergyExchangeTagGroupIds.equals(other.parentEnergyExchangeTagGroupIds))
      return false;
    if (parentEnergyExchangeTypeIds == null) {
      if (other.parentEnergyExchangeTypeIds != null)
        return false;
    } else if (!parentEnergyExchangeTypeIds.equals(other.parentEnergyExchangeTypeIds))
      return false;
    if (parentNodeTypeId == null) {
      if (other.parentNodeTypeId != null)
        return false;
    } else if (!parentNodeTypeId.equals(other.parentNodeTypeId))
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
    if (unitId == null) {
      if (other.unitId != null)
        return false;
    } else if (!unitId.equals(other.unitId))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("NodeTagTemplateDto [parentEnergyExchangeTypeIds=")
        .append(parentEnergyExchangeTypeIds).append(", parentEnergyExchangeTagGroupIds=")
        .append(parentEnergyExchangeTagGroupIds).append(", id=").append(id).append(", name=")
        .append(name).append(", description=").append(description).append(", tags=").append(tags)
        .append(", isPublic=").append(isPublic).append(", parentNodeTypeId=")
        .append(parentNodeTypeId).append(", isDeprecated=").append(isDeprecated)
        .append(", replacementPointTemplateId=").append(replacementPointTemplateId)
        .append(", unitId=").append(unitId).append("]");
    return builder.toString();
  }
}
