package com.djt.hvac.domain.model.common.event;

/**
 *
 * @author tmyers
 *
 */
public interface EventSubscriber<T> {

  /**
   * 
   * @param event The event to handle
   */
  void handleEvent(final T event);
}
