//@formatter:off
package com.djt.hvac.domain.model.function;

import java.util.Map;
import java.util.Set;

import com.djt.hvac.domain.model.dictionary.UnitEntity;
import com.djt.hvac.domain.model.dictionary.enums.DataType;

/**
 * 
 * Marker interface for points that are eligible to be bound to AD
 * function input points (not all point types are supported).
 * 
 * @author tommyers
 *
 */
public interface AdFunctionInstanceEligiblePoint {
  
  /**
   * 
   * @return The persistent identity of the ad function instance eligible point
   */
  Integer getPersistentIdentity();

  /**
   * 
   * @return The natural identity of the ad function instance eligible point
   */
  String getNaturalIdentity();
  
  /**
   * 
   * @return The display name natural identity of the point
   */
  String getNodePath();
  
  /**
   * 
   * @return Whether the ad function instance eligible point has been logically deleted or not
   */
  boolean getIsDeleted();
  
  /**
   * 
   * @return The haystack tags of the ad function instance eligible point
   */
  Set<String> getHaystackTags();
  
  /**
   * 
   * @return The metric id of the ad function instance eligible point (i.e. associated raw point metric id)
   */
  String getMetricId();
  
  /**
   * 
   * @return The data type of the ad function instance eligible point
   */
  DataType getDataType();
  
  /**
   * 
   * @param timestamp The timestamp
   * @param value The value (parseable as the given data type)
   */
  void addValue(Long timestamp, String value);
  
  /**
   * 
   * @return The values for the point, as a map, keyed by timestamp and value at that timestamp. (sorted)
   */
  Map<Long, String> getValues();
  
  /**
   * 
   * @return The associated unit (if one exists)
   */
  UnitEntity getUnitNullIfNotExists();
}
//@formatter:on