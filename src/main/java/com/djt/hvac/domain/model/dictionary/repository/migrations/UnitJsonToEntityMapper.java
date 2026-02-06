//@formatter:off
package com.djt.hvac.domain.model.dictionary.repository.migrations;

import java.util.List;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.dictionary.UnitEntity;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.dto.IdNameDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

public final class UnitJsonToEntityMapper implements DictionaryDataJsonToEntityMapper<UnitEntity> {

  @Override
  public List<UnitEntity> mapFromJson(String json) throws JsonMappingException, JsonProcessingException {
    
    List<IdNameDto> dtos = AbstractEntity
        .OBJECT_MAPPER
        .get()
        .readValue(
            json, 
            new TypeReference<List<IdNameDto>>() {});
    
    return UnitEntity
        .Mapper
        .getInstance()
        .mapDtosToEntities(
            DictionaryContext.getUnitsContainer(), 
            dtos);
  }
}
//@formatter:on