package com.djt.hvac.domain.model.nodehierarchy.validation;

import com.djt.hvac.domain.model.common.validation.AbstractRemediationStrategy;
import com.djt.hvac.domain.model.dictionary.enums.FunctionType;

public abstract class AbstractPortfolioRemediationStrategy extends AbstractRemediationStrategy {
   
  protected String performNameReplacement(Integer functionTypeId, String sql) {
    
    String replacement = null;
    if (functionTypeId == FunctionType.RULE.getId()) {
      replacement = "rule";
    } else if (functionTypeId == FunctionType.COMPUTED_POINT.getId()) {
      replacement = "computed_point";
    } else {
      throw new RuntimeException("Unsupported AD function type id: " + functionTypeId);
    }
    return sql.replace("function", replacement);
  }
}
