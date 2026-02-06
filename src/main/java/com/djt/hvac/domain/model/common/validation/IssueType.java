package com.djt.hvac.domain.model.common.validation;

import java.util.HashMap;
import java.util.Map;

public enum IssueType {
  
  // NO-OP
  INVALID_ASSOCIATIVE_ENTITY_INSTANTIATION(0, "Invalid instantiation", 0),
  NON_NUMERIC_POINT_HAS_EMPTY_RANGE(6, "Empty range for non-numeric point", 0),
  BOOLEAN_POINT_HAS_INVALID_RANGE(7, "Invalid range for boolean point", 0),
  ENUM_POINT_HAS_INVALID_RANGE(8, "Invalid range for enum point", 0),
  
  // POINT/EQUIPMENT RELATED (PHASE ONE)
  POINT_HAS_TEMPLATE_YET_NON_EQUIPMENT_PARENT(1, "Point has point template association yet has non-equipment parent", 1),
  POINT_HAS_TEMPLATE_YET_PARENT_EQUIPMENT_NO_TYPE(2, "Point has point template association yet parent equipment does not have an equipment type", 1),
  POINT_HAS_TEMPLATE_THAT_IS_INVALID_FOR_PARENT_EQUIPMENT_TYPE(3, "Point has point template association that is invalid for parent equipment type", 1),
  POINT_HAS_MISMATCH_BETWEEN_TAGS_AND_TEMPLATE_TAGS(4, "Mismatch between point haystack tags and its associated point template tags", 1),
  POINT_HAS_TAGS_BUT_NO_TEMPLATE(5, "Point has haystack tags but no point template association", 1),
  POINT_HAS_DELETED_RAW_POINT(23, "Mappable point is associated with a raw point that has been deleted", 1),
  POINT_HAS_IGNORED_RAW_POINT(24, "Mappable point is associated with a raw point that has been marked as ignored", 1),
  EQUIPMENT_HAS_PARENT_EQUIPMENT_FROM_ANOTHER_BUILDING(25, "Equipment has parent equipment that is from another building", 1),
  CUSTOM_POINT_HAS_INVALID_FORMULA(28, "Custom async computed point has a formula with a variable point that no longer exists, or has a syntax error", 1),
  POINT_HAS_DEPRECATED_TEMPLATE(30, "Point is associated with a deprecated point template", 1),
  
  // AD FUNCTION RELATED (PHASE TWO)
  AD_FUNCTION_EQUIPMENT_DOES_NOT_HAVE_TYPE(9, "Function instance equipment does not have equipment type", 2),
  AD_FUNCTION_EQUIPMENT_HAS_INVALID_EQUIPMENT_TYPE(10, "Function instance equipment has invalid equipment type", 2),
  AD_FUNCTION_EQUIPMENT_HAS_TOO_MANY_BOUND_POINTS_FOR_SCALAR_INPUT(11, "Function instance equipment has too many bound points for scalar input", 2),
  AD_FUNCTION_EQUIPMENT_DOESNT_HAVE_ANY_BOUND_POINTS_FOR_REQUIRED_INPUT(12, "Function instance equipment doesn't have any bound points for required input", 2),
  AD_FUNCTION_INPUT_POINT_HAS_INVALID_TAGS_FOR_OPTIONAL_SCALAR_INPUT(13, "Function instance input point has invalid haystack tags for optional scalar input", 2),
  AD_FUNCTION_INPUT_POINT_HAS_INVALID_TAGS_FOR_REQUIRED_SCALAR_INPUT(14, "Function instance input point has invalid haystack tags for required scalar input", 2),
  AD_FUNCTION_INPUT_POINT_HAS_INVALID_TAGS_FOR_OPTIONAL_ARRAY_INPUT(15, "Function instance input point has invalid haystack tags for optional array input", 2),
  AD_FUNCTION_INPUT_POINT_HAS_INVALID_TAGS_FOR_REQUIRED_ARRAY_INPUT_COUNT_EQ_1(16, "Function instance input point has invalid haystack tags for required array input with count = 1", 2),
  AD_FUNCTION_INPUT_POINT_HAS_INVALID_TAGS_FOR_REQUIRED_ARRAY_INPUT_COUNT_GT_1(17, "Function instance input point has invalid haystack tags for required array input with count > 1", 2),
  AD_FUNCTION_EQUIPMENT_HAS_ZERO_BOUND_POINTS(20, "Function instance equipment must have at least one bound point", 2),
  NEW_QUALIFYING_INPUT_POINT_FOR_AD_FUNCTION_AVAILABLE(18, "Function instance has new qualifying input point available to be bound", 2),
  AD_FUNCTION_EQUIPMENT_DOESNT_MATCH_NODE_FILTER_EXPRESSION(21, "Function instance equipment tags doesn't match non-null node filter expression", 2),
  AD_FUNCTION_INPUT_POINT_IS_NO_LONGER_ELIGIBLE(22, "Function instance input point is no longer eligible ", 2),
  AD_FUNCTION_POINT_TUPLE_CONSTRAINT_EXPRESSION_FALSE(26, "Function instance equipment does not satisfy point tuple constraint expression", 2),
  AD_FUNCTION_MISSING_INPUT_CONSTANT(27, "Function instance is missing an input constant", 2),
  VERSION_MISMATCH_MIGRATE_AD_FUNCTION_INSTANCE(29, "Function instance has a version mismatch", 2);
  
  private static final Map<Integer, IssueType> TYPES;
  
  private final int id;
  private final String name;
  private final int issueGroup;
  
  static {
    TYPES = new HashMap<>();
    for (IssueType type : IssueType.values()) {
      TYPES.put(type.id, type);
    }
  }
  
  public static IssueType get(int id) {
    return TYPES.get(id);
  }
  
  private IssueType(int id, String name, int issueGroup) {
    this.id = id;
    this.name = name;
    this.issueGroup = issueGroup;
  }
  
  public int getId() {
    return id;
  }
  
  public String getName() {
    return name;
  }
  
  public int getIssueGroup() {
    return issueGroup;
  }
}