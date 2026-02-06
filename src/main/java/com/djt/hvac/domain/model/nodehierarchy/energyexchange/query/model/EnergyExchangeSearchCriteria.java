//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.energyexchange.query.model;

import java.util.List;

import com.djt.hvac.domain.model.common.query.model.SearchCriteria;

/**
 * 
 * @author tmyers
 *
 */
public class EnergyExchangeSearchCriteria extends SearchCriteria {
  
  public static final String ALL = "ALL"; 

  public static final String NODE_TYPE_EQUIPMENT = "EQUIPMENT";
  public static final String NODE_TYPE_PLANT = "PLANT";
  public static final String NODE_TYPE_LOOP = "LOOP";

  public static final String SYSTEM_TYPE_AIR_SUPPLY = "AIR_SUPPLY";
  public static final String SYSTEM_TYPE_CHILLED_WATER = "CHILLED_WATER";
  public static final String SYSTEM_TYPE_HOT_WATER = "HOT_WATER";
  public static final String SYSTEM_TYPE_STEAM = "STEAM";
  
  public static final String SORT_KEY_NODE_PATH = "nodePath";
  public static final String SORT_KEY_DISPLAY_NAME = "displayName";
  public static final String SORT_KEY_TYPE_NAME= "typeName";
  public static final String SORT_KEY_PARENT_NAME = "parentName";
  public static final String SORT_KEY_CHILD_NAME = "childName";
  
  public static final String DEFAULT_SORT = SORT_KEY_NODE_PATH;
  
  private Integer customerId;
  private Integer buildingId;
  private String nodeType;
  private String nodePath;
  private String displayName;
  private Integer energyExchangeTypeId;
  private String systemType;
  private List<Integer> parentIds;
  private List<Integer> childIds;
  
  public EnergyExchangeSearchCriteria() {
  }
  
  public EnergyExchangeSearchCriteria(
      Integer customerId, 
      Integer buildingId, 
      String nodeType, 
      String nodePath,
      String displayName,
      Integer energyExchangeTypeId,
      String systemType, 
      List<Integer> parentIds,
      List<Integer> childIds,
      String sort, 
      String sortDirection, 
      Integer offset,
      Integer limit) {
    super(
        sort, 
        sortDirection, 
        offset, 
        limit);
    this.customerId = customerId;
    this.buildingId = buildingId;
    this.nodeType = nodeType;
    this.nodePath = nodePath;
    this.displayName = displayName;
    this.energyExchangeTypeId = energyExchangeTypeId;
    this.systemType = systemType;
    this.parentIds = parentIds;
    this.childIds = childIds;
  }

  public Integer getCustomerId() {
    return customerId;
  }

  public Integer getBuildingId() {
    return buildingId;
  }

  public String getNodeType() {
    return nodeType;
  }

  public String getNodePath() {
    return nodePath;
  }

  public String getDisplayName() {
    return displayName;
  }

  public Integer getEnergyExchangeTypeId() {
    return energyExchangeTypeId;
  }

  public String getSystemType() {
    return systemType;
  }

  public List<Integer> getParentIds() {
    return parentIds;
  }

  public List<Integer> getChildIds() {
    return childIds;
  }

  public void setCustomerId(Integer customerId) {
    this.customerId = customerId;
  }

  public void setBuildingId(Integer buildingId) {
    this.buildingId = buildingId;
  }

  public void setNodeType(String nodeType) {
    this.nodeType = nodeType;
  }

  public void setNodePath(String nodePath) {
    this.nodePath = nodePath;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public void setEnergyExchangeTypeId(Integer energyExchangeTypeId) {
    this.energyExchangeTypeId = energyExchangeTypeId;
  }

  public void setSystemType(String systemType) {
    this.systemType = systemType;
  }

  public void setParentIds(List<Integer> parentIds) {
    this.parentIds = parentIds;
  }

  public void setChildIds(List<Integer> childIds) {
    this.childIds = childIds;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((buildingId == null) ? 0 : buildingId.hashCode());
    result = prime * result + ((childIds == null) ? 0 : childIds.hashCode());
    result = prime * result + ((customerId == null) ? 0 : customerId.hashCode());
    result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
    result = prime * result + ((nodePath == null) ? 0 : nodePath.hashCode());
    result = prime * result + ((nodeType == null) ? 0 : nodeType.hashCode());
    result = prime * result + ((parentIds == null) ? 0 : parentIds.hashCode());
    result = prime * result + ((systemType == null) ? 0 : systemType.hashCode());
    result = prime * result + ((energyExchangeTypeId == null) ? 0 : energyExchangeTypeId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    EnergyExchangeSearchCriteria other = (EnergyExchangeSearchCriteria) obj;
    if (buildingId == null) {
      if (other.buildingId != null)
        return false;
    } else if (!buildingId.equals(other.buildingId))
      return false;
    if (childIds == null) {
      if (other.childIds != null)
        return false;
    } else if (!childIds.equals(other.childIds))
      return false;
    if (customerId == null) {
      if (other.customerId != null)
        return false;
    } else if (!customerId.equals(other.customerId))
      return false;
    if (displayName == null) {
      if (other.displayName != null)
        return false;
    } else if (!displayName.equals(other.displayName))
      return false;
    if (nodePath == null) {
      if (other.nodePath != null)
        return false;
    } else if (!nodePath.equals(other.nodePath))
      return false;
    if (nodeType == null) {
      if (other.nodeType != null)
        return false;
    } else if (!nodeType.equals(other.nodeType))
      return false;
    if (parentIds == null) {
      if (other.parentIds != null)
        return false;
    } else if (!parentIds.equals(other.parentIds))
      return false;
    if (systemType == null) {
      if (other.systemType != null)
        return false;
    } else if (!systemType.equals(other.systemType))
      return false;
    if (energyExchangeTypeId == null) {
      if (other.energyExchangeTypeId != null)
        return false;
    } else if (!energyExchangeTypeId.equals(other.energyExchangeTypeId))
      return false;
    
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("EnergyExchangeSearchCriteria [customerId=").append(customerId)
        .append(", buildingId=").append(buildingId).append(", nodeType=").append(nodeType)
        .append(", nodePath=").append(nodePath).append(", displayName=").append(displayName)
        .append(", energyExchangeTypeId=").append(energyExchangeTypeId)
        .append(", systemType=").append(systemType).append(", parentIds=").append(parentIds).append(", childIds=")
        .append(childIds).append(", getSort()=").append(getSort()).append(", getSortDirection()=")
        .append(getSortDirection()).append(", getOffset()=").append(getOffset())
        .append(", getLimit()=").append(getLimit()).append(", toString()=").append(super.toString())
        .append(", getClass()=").append(getClass()).append("]");
    return builder.toString();
  }
}
//@formatter:on