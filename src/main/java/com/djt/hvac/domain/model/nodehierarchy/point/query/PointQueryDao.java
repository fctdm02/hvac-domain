//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.point.query;

import java.util.List;
import java.util.Map;

import com.djt.hvac.domain.model.nodehierarchy.point.query.model.PointLastValue;
import com.djt.hvac.domain.model.nodehierarchy.point.query.model.PointPointTemplateResponse;

/**
 * 
 * @author tmyers
 *
 */
public interface PointQueryDao<PointSearchCriteria, PointQueryResponse> {
  
  /**
   * 
   * @param searchCriteria The search criteria
   * @return The point query response
   */
  PointQueryResponse query(PointSearchCriteria searchCriteria);
  
  /**
   * 
   * @param customerId The owning customer
   * @param pointId The point id to retrieve available point templates for
   * @return The point templates that are available for the given point
   */
  PointPointTemplateResponse getPointTemplatesForPoint(int customerId, int pointId);
  
  /**
   * 
   * @param customerId The owning customer
   * @param buildingId The list of parent buildings
   * @return A map whose key is the id for the point and whose value is a map keyed by last value timestamp and value is actual point value for that timestamp
   */
  Map<Integer, PointLastValue> getLastValueForMappablePoints(int customerId, List<Integer> buildingIds);
  
  /**
   * 
   * @param customerId The owning customer
   * @param buildingId The list of parent buildings
   * @return A map whose key is the id for the point and whose value is a map keyed by last value timestamp and value is actual point value for that timestamp
   */
  Map<Integer, PointLastValue> getLastValueForAsyncComputedPoints(int customerId, List<Integer> buildingIds);
}
//@formatter:on