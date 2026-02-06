//@formatter:off
package com.djt.hvac.domain.model.dictionary.template.v3.function.inputpoint;

import java.util.Set;

import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.UnitEntity;

public class StringAdFunctionTemplateInputPointEntity extends AbstractAdFunctionTemplateInputPointEntity {
  private static final long serialVersionUID = 1L;
  
  public StringAdFunctionTemplateInputPointEntity(
      Integer persistentIdentity,
      AdFunctionTemplateInputPointGroupEntity parentInputPointGroup,
      String name,
      String description,
      UnitEntity unit,
      String currentObjectExpression,
      Boolean isArray,
      Integer sequenceNumber,
      Set<TagEntity> tags) {
    super(
        persistentIdentity,
        parentInputPointGroup,
        name,
        description,
        unit,
        currentObjectExpression,
        isArray,
        sequenceNumber,
        tags);
  }  
}
///@formatter:on