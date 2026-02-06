package com.djt.hvac.domain.model.dictionary.event.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.dictionary.event.DictionaryChangeEvent;
import com.djt.hvac.domain.model.dictionary.event.DictionaryChangeEventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 *
 * @author tmyers
 *
 */
public class MockDictionaryChangeEventPublisher implements DictionaryChangeEventPublisher {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(MockDictionaryChangeEventPublisher.class);

  public static List<DictionaryChangeEvent> PUBLISHED_EVENTS = new ArrayList<>();
  
  private static MockDictionaryChangeEventPublisher INSTANCE = new MockDictionaryChangeEventPublisher();
  
  public static MockDictionaryChangeEventPublisher getInstance() {
    return INSTANCE;
  }

  private MockDictionaryChangeEventPublisher() {
  }
  
  @Override
  public DictionaryChangeEvent publishDictionaryChangeEvent(String category) {
    
    DictionaryChangeEvent event = DictionaryChangeEvent
        .builder()
        .withCategory(category)
        .build();

    PUBLISHED_EVENTS.add(event);

    return event;
  }
  
  public void printEventsAsJson() {
    
    for (DictionaryChangeEvent event: PUBLISHED_EVENTS) {
      try {
        LOGGER.info(AbstractEntity.OBJECT_MAPPER.get().writeValueAsString(event));
      } catch (JsonProcessingException e) {
        throw new RuntimeException("Unable to marshall event to JSON: "
            + event
            + "], error: "
            + e.getMessage(), e);
      }
    }
  }
}
