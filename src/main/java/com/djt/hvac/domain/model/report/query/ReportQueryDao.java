//@formatter:off
package com.djt.hvac.domain.model.report.query;

import java.util.SortedMap;

/**
 * MARKER INTERFACE
 * 
 * @author tmyers
 *
 */
public interface ReportQueryDao {
  
  /**
   * 
   * @param adFunctionTemplateId The AD function template id to get affected reports for
   * @return The set of affected reports (key is report id, value is report name)
   */
  SortedMap<Integer, String> getAffectedReportsForAdFunctionTemplateId(int adFunctionTemplateId);
  
  /**
   * 
   * @param pointTemplateId The point template id to get affected reports for
   * @return The set of affected reports (key is report id, value is report name)
   */
  SortedMap<String, String> getAffectedReportsForPointTemplateId(int pointTemplateId);
  
  /**
   * 
   * @param tagId The tag id to get affected reports for
   * @return The set of affected reports (key is report id, value is report name)
   */
  SortedMap<String, String> getAffectedReportsForTagId(int tagId);
}
//@formatter:on