//@formatter:off
package com.djt.hvac.domain.model.dictionary.energyexchange;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Set;

import com.djt.hvac.domain.model.common.AbstractPersistentEntity;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.TagEntity;

public abstract class AbstractEnergyExchangeTypeEntity extends AbstractPersistentEntity {
  private static final long serialVersionUID = 1L;
  private final TagEntity energyExchangeType;
  
  public AbstractEnergyExchangeTypeEntity(TagEntity energyExchangeType) {
    
    super(extractPersistentIdentity(energyExchangeType));
    requireNonNull(energyExchangeType, "energyExchangeType cannot be null");
    this.energyExchangeType = energyExchangeType;
  }
  
  public TagEntity getTag() {
    return this.energyExchangeType;
  }
  
  public String getName() {
    return energyExchangeType.getName();
  }
  
  @Override
  public String getNaturalIdentity() {
    return getName();
  }
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
    
    // Nothing to do here.
  }
  
  private static Integer extractPersistentIdentity(TagEntity energyExchangeType) {
    
    Integer persistentIdentity = null;
    if (energyExchangeType != null) {
      
      persistentIdentity = energyExchangeType.getPersistentIdentity();
    }
    return persistentIdentity;
  }
}
//@formatter:on