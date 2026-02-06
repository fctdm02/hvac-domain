package com.djt.hvac.domain.model.nodehierarchy.event;

import java.util.Map;

import com.djt.hvac.domain.model.common.event.EventPublisher;

/**
 *
 * @author tmyers
 *
 */
public interface NodeHierarchyChangeEventPublisher extends EventPublisher<NodeHierarchyChangeEvent> {
  
  /**
   * 
   * @param payload Consisting of name/value pairs for:
   * <ol>
   *   <li>eventUuid</li>
   *   <li>occurredOnDate</li>
   *   <li>owner</li>
   *   <li>customerId</li>
   *   <li>portfolioId</li>
   *   <li>createdNodeIds</li>
   *   <li>updatedNodeIds</li>
   *   <li>deletedNodeIds</li>
   *   <li>enabledAdFunctionInstanceIds</li>
   *   <li>enabledReportInstanceIds</li>
   *   <li>disabledAdFunctionInstanceIds</li>
   *   <li>disabledReportInstanceIds</li>
   * </ol>
   *        
   * @return The node hierarchy change event that was created/published
   */
  NodeHierarchyChangeEvent publishEvent(Map<String, Object> payload);
}
