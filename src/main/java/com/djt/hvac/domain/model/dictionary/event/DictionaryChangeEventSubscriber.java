package com.djt.hvac.domain.model.dictionary.event;

/**
 *
 * @author tmyers
 *
 */
public interface DictionaryChangeEventSubscriber {

  /**
   * 
   * @param dictionaryChangeEvent The dictionary change event
   */
  void handleEvent(DictionaryChangeEvent dictionaryChangeEvent);
}
