package com.djt.hvac.domain.model.nodehierarchy.event;

/**
 *
 * @author tmyers
 *
 */
public interface NodeHierarchyChangeEventSubscriber {

  /**
   * 
   * @param nodeHierarchyChangeEvent The node hierarchy change event
   */
  void handleEvent(NodeHierarchyChangeEvent nodeHierarchyChangeEvent);
}
