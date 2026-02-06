//@formatter:off
package com.djt.hvac.domain.model.dictionary.repository.migrations;

import java.util.List;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.dto.function.AdFunctionTemplateDto;
import com.djt.hvac.domain.model.dictionary.template.function.AbstractAdFunctionTemplateEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

public final class AdFunctionTemplateJsonToEntityMapper implements DictionaryDataJsonToEntityMapper<AbstractAdFunctionTemplateEntity> {

  @Override
  public List<AbstractAdFunctionTemplateEntity> mapFromJson(String json) throws JsonMappingException, JsonProcessingException {
    
    List<AdFunctionTemplateDto> dtos = AbstractEntity
        .OBJECT_MAPPER
        .get()
        .readValue(
            json, 
            new TypeReference<List<AdFunctionTemplateDto>>() {});
    
    return AbstractAdFunctionTemplateEntity
        .Mapper
        .getInstance()
        .mapDtosToEntities(
            DictionaryContext.getAdFunctionTemplatesContainer(), 
            dtos);
  }
}
//@formatter:on