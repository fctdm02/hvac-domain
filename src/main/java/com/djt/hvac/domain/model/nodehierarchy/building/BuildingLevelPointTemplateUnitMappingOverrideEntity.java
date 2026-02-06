//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.building;

import static java.util.Objects.requireNonNull;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.AbstractPointTemplateUnitMappingOverrideEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.UnitMappingEntity;

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
public class BuildingLevelPointTemplateUnitMappingOverrideEntity extends AbstractPointTemplateUnitMappingOverrideEntity {
  private static final long serialVersionUID = 1L;
  
  private final BuildingEntity parentBuilding;
  
  public BuildingLevelPointTemplateUnitMappingOverrideEntity(
      Integer persistentIdentity,
      PointTemplateEntity pointTemplate,
      Boolean keepIpUnitSystem,
      UnitMappingEntity unitMapping,
      BuildingEntity parentBuilding) {
    super(
        persistentIdentity,
        pointTemplate,
        keepIpUnitSystem,
        unitMapping);
        
    requireNonNull(parentBuilding, "parentBuilding cannot be null");
    this.parentBuilding = parentBuilding;
  }
  
  public BuildingEntity getParentBuilding() {
    return parentBuilding;
  }
  
  @Override
  public String getNaturalIdentity() {
    return new StringBuilder()
        .append(getPointTemplate().getNaturalIdentity())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(this.parentBuilding.getNaturalIdentity())
        .toString();
  }    
}
//@formatter:on