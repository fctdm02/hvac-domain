//@formatter:off
package com.djt.hvac.domain.model.dictionary.repository.migrations;

import java.util.List;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.dto.UnitMappingDto;
import com.djt.hvac.domain.model.dictionary.template.nodetag.UnitMappingEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

public final class UnitMappingJsonToEntityMapper implements DictionaryDataJsonToEntityMapper<UnitMappingEntity> {

  @Override
  public List<UnitMappingEntity> mapFromJson(String json) throws JsonMappingException, JsonProcessingException {
    
    List<UnitMappingDto> dtos = AbstractEntity
        .OBJECT_MAPPER
        .get()
        .readValue(
            json, 
            new TypeReference<List<UnitMappingDto>>() {});
    
    return UnitMappingEntity
        .Mapper
        .getInstance()
        .mapDtosToEntities(
            DictionaryContext.getNodeTagTemplatesContainer(), 
            dtos);
  }
}
//@formatter:on