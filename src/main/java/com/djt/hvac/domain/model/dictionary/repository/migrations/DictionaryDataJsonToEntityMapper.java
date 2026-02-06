//@formatter:off
package com.djt.hvac.domain.model.dictionary.repository.migrations;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * 
 * @author tmyers
 *
 * @param <T>
 */
public interface DictionaryDataJsonToEntityMapper<T> {

  /**
   * 
   * @param json The JSON containing a list of DTOs to be mapped to entities
   *        
   * @return The list of entities that were rehydrated from the json
   * 
   * @throws JsonMappingException If there was a problem mapping
   * @throws JsonProcessingException If there was a problem processing
   */
  List<T> mapFromJson(String json) throws JsonMappingException, JsonProcessingException;
}
//@formatter:on