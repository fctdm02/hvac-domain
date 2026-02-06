//@formatter:off
package com.djt.hvac.domain.model.rawpoint.repository;

import java.util.Collection;
import java.util.List;

import com.djt.hvac.domain.model.customer.AbstractCustomerEntity;
import com.djt.hvac.domain.model.rawpoint.RawPointEntity;

/**
 * 
 * @author tommyers
 * 
 */
public interface RawPointRepository {

  /**
   * 
   * @param customerId The customer id
   * @param rawPointIds The raw point ids
   * 
   * @return The raw points that were loaded
   */
  List<RawPointEntity> loadRawPoints(
      Integer customerId,
      List<Integer> rawPointIds);
  
  /**
   * 
   * @param parentCustomer The owning customer
   * 
   * @return The raw points that were loaded
   */
  List<RawPointEntity> loadRawPoints(AbstractCustomerEntity parentCustomer);
  
  /**
   * 
   * @param parentCustomer The owning customer
   * @param loadUnmappedOnly Load only unmapped points
   * @param loadIgnored load only ignored points
   * @param loadDeleted load only deleted points
   * 
   * @return The raw points that were loaded
   */
  List<RawPointEntity> loadRawPoints(
      AbstractCustomerEntity parentCustomer, 
      boolean loadUnmappedOnly,
      boolean loadIgnored,
      boolean loadDeleted);
  
  /**
   * This method is only used for testing purposes, as raw points are normally
   * inserted via a connector to some upstream building automation system
   *  
   * @param customerId The owning customer
   * @param rawPoints The raw points to insert (either with or without a persistent
   *         identity that has been pre-assigned.
   */
  void storeRawPoints(int customerId, Collection<RawPointEntity> rawPoints);
  
  /**
   * 
   * @param parentCustomer The owning customer
   * @param rawPointIds The points to ignore
   * 
   * @return The actual list of updated points
   */
  List<Integer> ignoreRawPoints(AbstractCustomerEntity parentCustomer, List<Integer> rawPointIds);
  
  /**
   * 
   * @param parentCustomer The owning customer
   * @param rawPointIds The points to unignore
   * 
   * @return The actual list of updated points
   */
  List<Integer> unignoreRawPoints(AbstractCustomerEntity parentCustomer, List<Integer> rawPointIds);
}
//@formatter:on