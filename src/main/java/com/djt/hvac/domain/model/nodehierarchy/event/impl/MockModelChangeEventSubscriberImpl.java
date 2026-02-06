package com.djt.hvac.domain.model.nodehierarchy.event.impl;

import java.util.ArrayList;
import java.util.List;

import com.djt.hvac.domain.model.nodehierarchy.event.NodeHierarchyChangeEvent;
import com.djt.hvac.domain.model.nodehierarchy.event.NodeHierarchyChangeEventSubscriber;

/**
 *
 * @author tmyers
 *
 */
public class MockModelChangeEventSubscriberImpl implements NodeHierarchyChangeEventSubscriber {

  private static MockModelChangeEventSubscriberImpl INSTANCE = new MockModelChangeEventSubscriberImpl();

  public static MockModelChangeEventSubscriberImpl getInstance() {
    return INSTANCE;
  }

  private MockModelChangeEventSubscriberImpl() {}

  private List<NodeHierarchyChangeEvent> events = new ArrayList<>();

  public void handleEvent(NodeHierarchyChangeEvent event) {
    this.events.add(event);
  }

  public List<NodeHierarchyChangeEvent> getEvents() {
    return this.events;
  }

  public void clearEvents() {
    this.events.clear();
  }
}
