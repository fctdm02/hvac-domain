package com.djt.hvac.domain.model.common.repository;

import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;

public interface AggregateRootRepository<T> {
  
  /**
   * Loads the aggregate root by persistent identity
   * 
   * @param persistentIdentity The aggregate root to load
   * 
   * @return The aggregate root identified by <code>persistentIdentity</code>
   * 
   * @throws EntityDoesNotExistException If the specified aggregate root does not exist
   */
  T loadAggregateRoot(int persistentIdentity) throws EntityDoesNotExistException;
    
  /**
   * 
   * @param aggregateRoot The aggregate root to store
   */
  void storeAggregateRoot(T aggregateRoot);  
}
