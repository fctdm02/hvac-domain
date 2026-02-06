//@formatter:off
package com.djt.hvac.domain.model.dictionary.repository.migrations;

import java.util.List;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.dto.NodeTagTemplateDto;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

public final class PointTemplateJsonToEntityMapper implements DictionaryDataJsonToEntityMapper<PointTemplateEntity> {

  @Override
  public List<PointTemplateEntity> mapFromJson(String json) throws JsonMappingException, JsonProcessingException {
    
    List<NodeTagTemplateDto> dtos = AbstractEntity
        .OBJECT_MAPPER
        .get()
        .readValue(
            json, 
            new TypeReference<List<NodeTagTemplateDto>>() {});
    
    return PointTemplateEntity
        .Mapper
        .getInstance()
        .mapDtosToEntities(
            DictionaryContext.getNodeTagTemplatesContainer(), 
            dtos);
  }
}
//@formatter:on