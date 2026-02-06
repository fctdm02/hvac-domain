package com.djt.hvac.domain.model.nodehierarchy.dto;

import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EnergyExchangeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/*
-- energy exchange system edges
-- ============================
-- system types:
--   1. Chilled Water
--   2. Hot Water
--   3. Steam
--   4. Air Supply
-- ============================ 
SELECT 
  nge.parent_id,
  nge.child_id,
  nge.system_type_id
FROM
  nodes n 
  JOIN node_graph_edges nge ON n.id = nge.child_id
WHERE 
      n.customer_id = ?  -- e.g. 4 for McLaren
  AND n.node_type_id <= ? -- NODE DEPTH, e.g. 9 for point
  AND n.id IN (SELECT child_id FROM node_closures WHERE parent_type_id = ? AND parent_id = ?) -- FILTER NODE TYPE/ID, e.g. 3 and 40 for Port Huron bldg      
ORDER BY 
  nge.system_type_id,
  nge.parent_id,
  nge.child_id;  
*/
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "system_type_id",
    "parent_id",
    "child_id",
    "child_node"
})
public class EnergyExchangeSystemEdgeDto {

  @JsonProperty("system_type_id")
  private Integer systemTypeId;

  @JsonProperty("parent_id")
  private Integer parentId;

  @JsonProperty("child_id")
  private Integer childId;
  
  @JsonProperty("childNode")
  private EnergyExchangeEntity childNode;

  @JsonProperty("system_type_id")
  public Integer getSystemTypeId() {
    return systemTypeId;
  }

  @JsonProperty("system_type_id")
  public void setSystemTypeId(Integer systemTypeId) {
    this.systemTypeId = systemTypeId;
  }
  
  @JsonProperty("parent_id")
  public Integer getParentId() {
    return parentId;
  }

  @JsonProperty("parent_id")
  public void setParentId(Integer parentId) {
    this.parentId = parentId;
  }

  @JsonProperty("child_id")
  public Integer getChildId() {
    return childId;
  }

  @JsonProperty("child_id")
  public void setChildId(Integer childId) {
    this.childId = childId;
  }

  @JsonProperty("child_node")
  public EnergyExchangeEntity getChildNode() {
    return childNode;
  }

  @JsonProperty("child_node")
  public void setChildNode(EnergyExchangeEntity childNode) {
    this.childNode = childNode;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((childId == null) ? 0 : childId.hashCode());
    result = prime * result + ((parentId == null) ? 0 : parentId.hashCode());
    result = prime * result + ((systemTypeId == null) ? 0 : systemTypeId.hashCode());
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
    EnergyExchangeSystemEdgeDto other = (EnergyExchangeSystemEdgeDto) obj;
    if (childId == null) {
      if (other.childId != null)
        return false;
    } else if (!childId.equals(other.childId))
      return false;
    if (parentId == null) {
      if (other.parentId != null)
        return false;
    } else if (!parentId.equals(other.parentId))
      return false;
    if (systemTypeId == null) {
      if (other.systemTypeId != null)
        return false;
    } else if (!systemTypeId.equals(other.systemTypeId))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append("EnergyExchangeSystemEdgeDto [systemTypeId=")
        .append(systemTypeId)
        .append(", parentId=")
        .append(parentId)
        .append(", childId=")
        .append(childId)
        .append("]")
        .toString();
  }
}
