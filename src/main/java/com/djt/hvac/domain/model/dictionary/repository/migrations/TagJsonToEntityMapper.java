//@formatter:off
package com.djt.hvac.domain.model.dictionary.repository.migrations;

import java.util.List;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.dto.TagDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

public final class TagJsonToEntityMapper implements DictionaryDataJsonToEntityMapper<TagEntity> {

  @Override
  public List<TagEntity> mapFromJson(String json) throws JsonMappingException, JsonProcessingException {
    
    List<TagDto> dtos = AbstractEntity
        .OBJECT_MAPPER
        .get()
        .readValue(
            json, 
            new TypeReference<List<TagDto>>() {});
    
    return TagEntity
        .Mapper
        .getInstance()
        .mapDtosToEntities(
            DictionaryContext.getTagsContainer(), 
            dtos);
  }
}
//@formatter:on