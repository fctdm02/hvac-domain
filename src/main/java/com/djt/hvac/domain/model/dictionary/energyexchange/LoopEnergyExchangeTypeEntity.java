//@formatter:off
package com.djt.hvac.domain.model.dictionary.energyexchange;

import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.enums.TagGroupType;

public class LoopEnergyExchangeTypeEntity extends AbstractEnergyExchangeTypeEntity {
  private static final long serialVersionUID = 1L;
  public LoopEnergyExchangeTypeEntity(TagEntity energyExchangeType) {
    super(energyExchangeType);
    
    // Ensure that we are dealing with the right tag group type.
    TagGroupType tagGroupType = energyExchangeType.getTagGroupType();
    if (!tagGroupType.equals(TagGroupType.LOOP_TYPE)) {
      
      throw new IllegalStateException("Invalid energy exchange tag group type: ["
          + tagGroupType
          + "].  Expected LOOP_TYPE.");
    }
  }
}
//@formatter:on