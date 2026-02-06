package com.djt.hvac.domain.model.dictionary.event;

/**
 *
 * @author tmyers
 *
 */
public interface DictionaryChangeEventPublisher {
  
  /**
   * 
   * @param category The category of dictionary data that's changed
   *        
   * @return The dictionary change event that was created/published
   */
  DictionaryChangeEvent publishDictionaryChangeEvent(String category);
}
