package com.djt.hvac.domain.model.common.event;

import java.util.Map;

/**
 *
 * @author tmyers
 *
 */
public interface EventPublisher<T> {

  /**
   * 
   * @param payload The event to be published, and possibly handled by, the subscribers
   * 
   * @return The event that was created from the given payload
   */
  T publishEvent(Map<String, Object> payload);
}
