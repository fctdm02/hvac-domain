//@formatter:off
package com.djt.hvac.domain.model.function.query;

import java.util.SortedMap;

/**
 * MARKER INTERFACE
 * 
 * @author tmyers
 *
 */
public interface AdFunctionTemplateQueryDao {
  
  /**
   * 
   * @param pointTemplateId The point template id to get affected rules for
   * @return The set of affected rules (key is rule id, value is rule name)
   */
  SortedMap<String, String> getAffectedRulesForPointTemplateId(int pointTemplateId);
  
  /**
   * 
   * @param tagId The tag id to get affected rules for
   * @return The set of affected rules (key is report id, value is report name)
   */
  SortedMap<String, String> getAffectedRulesForTagId(int tagId);
}
//@formatter:on
