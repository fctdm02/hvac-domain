package com.djt.hvac.domain.model.dictionary.dto;

import static java.util.Objects.requireNonNull;

/**
 * There are two types of overrides: The first is where the IP unit system is kept. That is, when SI unit system is
 * configured for the distributor/customer/building, the user can override any given point template so that the IP
 * unit is still used.  The second is that for any given point template, the user can configure unit mapping for SI
 * to IP be different from that of the default (which is priority 1), assuming that more than one unit mapping exists
 * for any given IP unit to SI unit pair.  NOTE: only one of these two overrides can be "active" simultaneously, so
 * if "keepIpUnitSystem" is true, then "unitMapping" must be NULL (the converse is also true, if 
 * "keepIpUnitSystem" is false, then "unitMapping" must be NON-NULL.
 * 
 * @author tmyers
 *
 */
public class PointTemplateUnitMappingOverrideDto {
  
  private final Integer id;
  private final Integer pointTemplateId;
  private final boolean keepIpUnitSystem;
  private final Integer unitMappingId;
  private final Integer parentDistributorId;
  private final Integer parentCustomerId;
  private final Integer parentBuildingId;
  
  public PointTemplateUnitMappingOverrideDto(
      Integer id,
      Integer pointTemplateId,
      Boolean keepIpUnitSystem,
      Integer unitMappingId,
      Integer parentDistributorId,
      Integer parentCustomerId,
      Integer parentBuildingId) {
    
    requireNonNull(id, "id cannot be null");
    requireNonNull(pointTemplateId, "pointTemplateId cannot be null");
    this.id = id;
    this.pointTemplateId = pointTemplateId;
    this.keepIpUnitSystem = keepIpUnitSystem;
    this.unitMappingId = unitMappingId;
    this.parentDistributorId = parentDistributorId;
    this.parentCustomerId = parentCustomerId;
    this.parentBuildingId = parentBuildingId;
  }
  
  public Integer getId() {
    return id;
  }
  
  public Integer getPointTemplateId() {
    return pointTemplateId;
  }
  
  public boolean getKeepIpUnitSystem() {
    return keepIpUnitSystem;
  }

  public Integer getUnitMappingId() {
    return unitMappingId;
  }
  
  public Integer getParentDistributorId() {
    return parentDistributorId;
  }

  public Integer getParentCustomerId() {
    return parentCustomerId;
  }

  public Integer getParentBuildingId() {
    return parentBuildingId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + (keepIpUnitSystem ? 1231 : 1237);
    result = prime * result + ((parentBuildingId == null) ? 0 : parentBuildingId.hashCode());
    result = prime * result + ((parentCustomerId == null) ? 0 : parentCustomerId.hashCode());
    result = prime * result + ((parentDistributorId == null) ? 0 : parentDistributorId.hashCode());
    result = prime * result + ((pointTemplateId == null) ? 0 : pointTemplateId.hashCode());
    result = prime * result + ((unitMappingId == null) ? 0 : unitMappingId.hashCode());
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
    PointTemplateUnitMappingOverrideDto other = (PointTemplateUnitMappingOverrideDto) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (keepIpUnitSystem != other.keepIpUnitSystem)
      return false;
    if (parentBuildingId == null) {
      if (other.parentBuildingId != null)
        return false;
    } else if (!parentBuildingId.equals(other.parentBuildingId))
      return false;
    if (parentCustomerId == null) {
      if (other.parentCustomerId != null)
        return false;
    } else if (!parentCustomerId.equals(other.parentCustomerId))
      return false;
    if (parentDistributorId == null) {
      if (other.parentDistributorId != null)
        return false;
    } else if (!parentDistributorId.equals(other.parentDistributorId))
      return false;
    if (pointTemplateId == null) {
      if (other.pointTemplateId != null)
        return false;
    } else if (!pointTemplateId.equals(other.pointTemplateId))
      return false;
    if (unitMappingId == null) {
      if (other.unitMappingId != null)
        return false;
    } else if (!unitMappingId.equals(other.unitMappingId))
      return false;
    return true;
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("PointTemplateUnitMappingOverrideDto [id=").append(id)
        .append(", pointTemplateId=").append(pointTemplateId).append(", keepIpUnitSystem=")
        .append(keepIpUnitSystem).append(", unitMappingId=").append(unitMappingId)
        .append(", parentDistributorId=").append(parentDistributorId).append(", parentCustomerId=")
        .append(parentCustomerId).append(", parentBuildingId=").append(parentBuildingId)
        .append("]");
    return builder.toString();
  }  
}