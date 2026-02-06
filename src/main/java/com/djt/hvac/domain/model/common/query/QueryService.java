//@formatter:off
package com.djt.hvac.domain.model.common.query;

import com.djt.hvac.domain.model.common.query.model.QueryResponse;
import com.djt.hvac.domain.model.common.query.model.QueryResponseItem;
import com.djt.hvac.domain.model.common.query.model.SearchCriteria;

/**
 * MARKER INTERFACE
 * 
 * @author tmyers
 *
 */
public interface QueryService<S extends SearchCriteria, R extends QueryResponse<SearchCriteria, QueryResponseItem>> {
  
  /**
   * Queries handle requests for complicated searches that involve:
   * <ol>
   *   <li>search criteria</li>
   *   <li>sorting</li>
   *   <li>pagination</li>
   *   <li>UI use case specific tabular data</li>
   * </ol>
   * The last item, can be likened to the old "Fast Lane Reader" 
   * design pattern, where the data requested is a selection/project
   * of information spread across many entities.
   * 
   * @param searchCriteria The search request
   * 
   * @return The search response
   */
  R query(S searchCriteria);
}
//@formatter:on