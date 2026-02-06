package com.djt.hvac.domain.model.dictionary.event.impl;

import com.djt.hvac.domain.model.dictionary.event.DictionaryChangeEvent;
import com.djt.hvac.domain.model.dictionary.event.DictionaryChangeEventPublisher;

/**
 *
 * @author tmyers
 *
 */
public class NoOpDictionaryChangeEventPublisher implements DictionaryChangeEventPublisher {

  private static NoOpDictionaryChangeEventPublisher INSTANCE = new NoOpDictionaryChangeEventPublisher();

  public static NoOpDictionaryChangeEventPublisher getInstance() {
    return INSTANCE;
  }

  private NoOpDictionaryChangeEventPublisher() {
  }
  
  @Override
  public DictionaryChangeEvent publishDictionaryChangeEvent(String category) {

    return DictionaryChangeEvent
        .builder()
        .withCategory(category)
        .build();  
  }
}