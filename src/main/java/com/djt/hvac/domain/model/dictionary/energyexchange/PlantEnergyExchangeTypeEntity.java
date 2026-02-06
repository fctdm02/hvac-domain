//@formatter:off
package com.djt.hvac.domain.model.dictionary.energyexchange;

import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.enums.TagGroupType;

public class PlantEnergyExchangeTypeEntity extends AbstractEnergyExchangeTypeEntity {
  private static final long serialVersionUID = 1L;
  public PlantEnergyExchangeTypeEntity(TagEntity energyExchangeType) {
    super(energyExchangeType);
    
    // Ensure that we are dealing with the right tag group type.
    TagGroupType tagGroupType = energyExchangeType.getTagGroupType();
    if (!tagGroupType.equals(TagGroupType.PLANT_TYPE)) {
      
      throw new IllegalStateException("Invalid energy exchange tag group type: ["
          + tagGroupType
          + "].  Expected PLANT_TYPE.");
    }
  }
}
//@formatter:on