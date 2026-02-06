//@formatter:off
package com.djt.hvac.domain.model.dictionary.repository.migrations;

import java.util.List;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.dto.PointTemplateUnitMappingDto;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateUnitMappingEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

public final class PointTemplateUnitMappingJsonToEntityMapper implements DictionaryDataJsonToEntityMapper<PointTemplateUnitMappingEntity> {

  @Override
  public List<PointTemplateUnitMappingEntity> mapFromJson(String json) throws JsonMappingException, JsonProcessingException {
    
    List<PointTemplateUnitMappingDto> dtos = AbstractEntity
        .OBJECT_MAPPER
        .get()
        .readValue(
            json, 
            new TypeReference<List<PointTemplateUnitMappingDto>>() {});
    
    return PointTemplateUnitMappingEntity
        .Mapper
        .getInstance()
        .mapDtosToEntities(
            DictionaryContext.getNodeTagTemplatesContainer(), 
            dtos);
  }
}
//@formatter:on