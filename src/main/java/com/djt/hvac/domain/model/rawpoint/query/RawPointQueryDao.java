//@formatter:off
package com.djt.hvac.domain.model.rawpoint.query;

/**
 * 
 * @author tmyers
 *
 */
public interface RawPointQueryDao<RawPointSearchCriteria, RawPointQueryResponse> {
  
  /**
   * 
   * @param searchCriteria The search criteria
   * @return The raw point query response
   */
  RawPointQueryResponse query(RawPointSearchCriteria searchCriteria);
}
//@formatter:on