//@formatter:off
package com.djt.hvac.domain.model.function.query;

import com.djt.hvac.domain.model.function.query.model.AdFunctionInstanceQueryResponse;
import com.djt.hvac.domain.model.function.query.model.AdFunctionInstanceSearchCriteria;

/**
 * MARKER INTERFACE
 * 
 * @author tmyers
 *
 */
public interface AdFunctionInstanceQueryDao {
  
  /**
   * 
   * Deals with AD function instances in every state:
   * <ul>
   *   <li>Ignored</li>
   *   <li>Disabled (i.e. "candidate")</li>
   *   <li>Enabled (i.e. "instance")</li>
   * </ul>
   * 
   * @param searchCriteria The criteria for retrieving AD function instances
   * 
   * @return The data corresponding to the given search criteria
   */
  AdFunctionInstanceQueryResponse query(AdFunctionInstanceSearchCriteria searchCriteria);
}
//@formatter:on
