package com.djt.hvac.domain.model.function;

import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.RemediationStrategy;
import com.djt.hvac.domain.model.nodehierarchy.validation.AddAdFunctionInstanceInputPointStrategyImpl;
import com.djt.hvac.domain.model.nodehierarchy.validation.AddMissingAdFunctionInstanceInputConstantStrategyImpl;
import com.djt.hvac.domain.model.nodehierarchy.validation.DeleteAdFunctionInstanceCandidateStrategyImpl;
import com.djt.hvac.domain.model.nodehierarchy.validation.DeleteAdFunctionInstanceInputPointStrategyImpl;
import com.djt.hvac.domain.model.nodehierarchy.validation.DeleteAdFunctionInstanceStrategyImpl;
import com.djt.hvac.domain.model.nodehierarchy.validation.MigrateAdFunctionInstanceVersionStrategyImpl;

public interface AdFunctionRemediationStrategyFinder {
    
  static RemediationStrategy find(IssueType issueType, AbstractAdFunctionInstanceEntity adFunctionInstance) {

    RemediationStrategy remediationStrategy = null;
    if (adFunctionInstance.getIsCandidate()) {
      
      if (issueType.equals(IssueType.AD_FUNCTION_EQUIPMENT_DOES_NOT_HAVE_TYPE)
          || issueType.equals(IssueType.AD_FUNCTION_EQUIPMENT_HAS_INVALID_EQUIPMENT_TYPE)
          || issueType.equals(IssueType.AD_FUNCTION_EQUIPMENT_DOESNT_MATCH_NODE_FILTER_EXPRESSION)
          || issueType.equals(IssueType.AD_FUNCTION_EQUIPMENT_HAS_TOO_MANY_BOUND_POINTS_FOR_SCALAR_INPUT)
          || issueType.equals(IssueType.AD_FUNCTION_EQUIPMENT_DOESNT_HAVE_ANY_BOUND_POINTS_FOR_REQUIRED_INPUT)
          || issueType.equals(IssueType.AD_FUNCTION_INPUT_POINT_HAS_INVALID_TAGS_FOR_OPTIONAL_SCALAR_INPUT)
          || issueType.equals(IssueType.AD_FUNCTION_INPUT_POINT_HAS_INVALID_TAGS_FOR_REQUIRED_SCALAR_INPUT)
          || issueType.equals(IssueType.AD_FUNCTION_INPUT_POINT_HAS_INVALID_TAGS_FOR_OPTIONAL_ARRAY_INPUT)
          || issueType.equals(IssueType.AD_FUNCTION_INPUT_POINT_HAS_INVALID_TAGS_FOR_REQUIRED_ARRAY_INPUT_COUNT_EQ_1)
          || issueType.equals(IssueType.AD_FUNCTION_INPUT_POINT_HAS_INVALID_TAGS_FOR_REQUIRED_ARRAY_INPUT_COUNT_GT_1)
          || issueType.equals(IssueType.AD_FUNCTION_INPUT_POINT_IS_NO_LONGER_ELIGIBLE)
          || issueType.equals(IssueType.NEW_QUALIFYING_INPUT_POINT_FOR_AD_FUNCTION_AVAILABLE)
          || issueType.equals(IssueType.AD_FUNCTION_EQUIPMENT_HAS_ZERO_BOUND_POINTS)
          || issueType.equals(IssueType.AD_FUNCTION_POINT_TUPLE_CONSTRAINT_EXPRESSION_FALSE)
          || issueType.equals(IssueType.VERSION_MISMATCH_MIGRATE_AD_FUNCTION_INSTANCE)) {
        
        return DeleteAdFunctionInstanceCandidateStrategyImpl.get();
      }
      
    } else {

      if (issueType.equals(IssueType.AD_FUNCTION_EQUIPMENT_DOES_NOT_HAVE_TYPE)
          || issueType.equals(IssueType.AD_FUNCTION_EQUIPMENT_HAS_INVALID_EQUIPMENT_TYPE)
          || issueType.equals(IssueType.AD_FUNCTION_EQUIPMENT_DOESNT_MATCH_NODE_FILTER_EXPRESSION)
          || issueType.equals(IssueType.AD_FUNCTION_EQUIPMENT_HAS_TOO_MANY_BOUND_POINTS_FOR_SCALAR_INPUT)
          || issueType.equals(IssueType.AD_FUNCTION_EQUIPMENT_DOESNT_HAVE_ANY_BOUND_POINTS_FOR_REQUIRED_INPUT)
          || issueType.equals(IssueType.AD_FUNCTION_INPUT_POINT_HAS_INVALID_TAGS_FOR_REQUIRED_ARRAY_INPUT_COUNT_EQ_1)
          || issueType.equals(IssueType.AD_FUNCTION_EQUIPMENT_HAS_ZERO_BOUND_POINTS)
          || issueType.equals(IssueType.AD_FUNCTION_POINT_TUPLE_CONSTRAINT_EXPRESSION_FALSE)) {
        
        return DeleteAdFunctionInstanceStrategyImpl.get();
        
      } else if (issueType.equals(IssueType.AD_FUNCTION_INPUT_POINT_HAS_INVALID_TAGS_FOR_OPTIONAL_SCALAR_INPUT)
          || issueType.equals(IssueType.AD_FUNCTION_INPUT_POINT_HAS_INVALID_TAGS_FOR_REQUIRED_SCALAR_INPUT)
          || issueType.equals(IssueType.AD_FUNCTION_INPUT_POINT_HAS_INVALID_TAGS_FOR_OPTIONAL_ARRAY_INPUT)
          || issueType.equals(IssueType.AD_FUNCTION_INPUT_POINT_HAS_INVALID_TAGS_FOR_REQUIRED_ARRAY_INPUT_COUNT_GT_1)
          || issueType.equals(IssueType.AD_FUNCTION_INPUT_POINT_IS_NO_LONGER_ELIGIBLE)) {
        
        return DeleteAdFunctionInstanceInputPointStrategyImpl.get();
        
      } else if (issueType.equals(IssueType.NEW_QUALIFYING_INPUT_POINT_FOR_AD_FUNCTION_AVAILABLE)) {
        
        return AddAdFunctionInstanceInputPointStrategyImpl.get();
        
      } else if (issueType.equals(IssueType.AD_FUNCTION_MISSING_INPUT_CONSTANT)) {
        
        return AddMissingAdFunctionInstanceInputConstantStrategyImpl.get();

      } else if (issueType.equals(IssueType.VERSION_MISMATCH_MIGRATE_AD_FUNCTION_INSTANCE)) {
        
        return MigrateAdFunctionInstanceVersionStrategyImpl.get();
        
      } else {
        
        throw new IllegalStateException("Cannot find remediation strategy for issueType: [" + issueType + "]");
        
      }
    }
      
    return remediationStrategy;
  }
}