package com.djt.hvac.domain.model.common.service.command;

import java.io.Serializable;

/**
 * MARKER INTERFACE
 * 
 * NOTE: This interface extends <code>Serializable</code>, as they
 * is intended to be placed into command queues for processing
 * (for load/scalability purposes)
 * 
 * @author tmyers
 *
 */
public interface AggregateRootCommandRequest extends Serializable {
  
}
