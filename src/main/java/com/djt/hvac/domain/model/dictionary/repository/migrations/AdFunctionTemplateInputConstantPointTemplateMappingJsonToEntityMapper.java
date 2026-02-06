//@formatter:off
package com.djt.hvac.domain.model.dictionary.repository.migrations;

import java.util.List;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.dto.AdFunctionTemplateInputConstantPointTemplateMappingDto;
import com.djt.hvac.domain.model.dictionary.template.function.AdFunctionTemplateInputConstantPointTemplateMappingEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

public final class AdFunctionTemplateInputConstantPointTemplateMappingJsonToEntityMapper implements DictionaryDataJsonToEntityMapper<AdFunctionTemplateInputConstantPointTemplateMappingEntity> {

  @Override
  public List<AdFunctionTemplateInputConstantPointTemplateMappingEntity> mapFromJson(String json) throws JsonMappingException, JsonProcessingException {
    
    List<AdFunctionTemplateInputConstantPointTemplateMappingDto> dtos = AbstractEntity
        .OBJECT_MAPPER
        .get()
        .readValue(
            json, 
            new TypeReference<List<AdFunctionTemplateInputConstantPointTemplateMappingDto>>() {});
    
    return AdFunctionTemplateInputConstantPointTemplateMappingEntity
        .Mapper
        .getInstance()
        .mapDtosToEntities(
            DictionaryContext.getAdFunctionTemplatesContainer(), 
            dtos);
  }
}
//@formatter:on