package com.djt.hvac.domain.model.common.service;

import com.djt.hvac.domain.model.common.event.EventPublisher;
import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;

public interface AggregateRootService<AR, T> extends EventPublisher<T> {
  
  /**
   * Loads the aggregate root by persistent identity.
   * NOTE: This is the trivial "retrieve" in CRUD.  Anything that involves
   * search criteria/filtering, sorting and pagination are handled by
   * <code>AggregateRootQueryProcessor</code> implementations.
   * 
   * @param persistentIdentity The aggregate root to load
   * 
   * @return The aggregate root identified by <code>persistentIdentity</code>
   * 
   * @throws EntityDoesNotExistException If the specified aggregate root does not exist
   */
  AR loadAggregateRoot(int persistentIdentity) throws EntityDoesNotExistException;
}
