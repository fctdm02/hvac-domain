package com.djt.hvac.domain.model.dictionary.event.impl;

import java.util.ArrayList;
import java.util.List;

import com.djt.hvac.domain.model.dictionary.event.DictionaryChangeEvent;
import com.djt.hvac.domain.model.dictionary.event.DictionaryChangeEventSubscriber;

/**
 *
 * @author tmyers
 *
 */
public class MockDictionaryChangeEventSubscriberImpl implements DictionaryChangeEventSubscriber {

  private static MockDictionaryChangeEventSubscriberImpl INSTANCE = new MockDictionaryChangeEventSubscriberImpl();

  public static MockDictionaryChangeEventSubscriberImpl getInstance() {
    return INSTANCE;
  }

  private MockDictionaryChangeEventSubscriberImpl() {}

  private List<DictionaryChangeEvent> events = new ArrayList<>();

  public void handleEvent(DictionaryChangeEvent event) {
    this.events.add(event);
  }

  public List<DictionaryChangeEvent> getEvents() {
    return this.events;
  }

  public void clearEvents() {
    this.events.clear();
  }
}
