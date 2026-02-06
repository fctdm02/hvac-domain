//@formatter:off
package com.djt.hvac.domain.model.nodehierarchy.point.computed.async.custom;

import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;

/**
 * 
 * Marker interface for points that are eligible to be bound to custom
 * async computed point temporal config "formula variable" points.
 * 
 * @author tommyers
 *
 */
public interface CustomPointFormulaVariableEligiblePoint {
  
  /**
   * 
   * @return The persistent identity of the custom formula variable eligible point
   */
  Integer getPersistentIdentity();
  
  /**
   * 
   * @return The metric id of the that does NOT include the customer UUID and "." prefix 
   */
  String getMetricId();
  
  /***
   * 
   * @return The metric id of the that DOES include the customer UUID and "." prefix
   */
  String getMetricIdForTsdb();

  /**
   * 
   * @return The natural identity of the custom formula variable eligible point
   */
  String getNaturalIdentity();
  
  /**
   * 
   * @return The display name natural identity of the custom formula variable eligible point
   */
  String getNodePath();
  
  /**
   * 
   * @param epochSeconds The epochSeconds to retrieve the value for
   * 
   * @return The value at the given timestamp
   * 
   * @throws EntityDoesNotExistException If no values exist, or no value exist for the given timestamp
   */
  String getValue(Long epochSeconds) throws EntityDoesNotExistException;
  
  /**
   * 
   * @return <code>true</code> if logically deleted, <code>false</code> otherwise
   */
  boolean getIsDeleted();
}
//@formatter:on