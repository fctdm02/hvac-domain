package com.djt.hvac.domain.model.dictionary.container;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.junit.Assert;
import org.junit.Test;

import com.djt.hvac.domain.model.AbstractResoluteDomainModelTest;
import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.utils.ObjectMappers;
import com.djt.hvac.domain.model.dictionary.container.NodeTagTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.dto.EquipmentPointTemplateDto;
import com.djt.hvac.domain.model.dictionary.dto.NodeTagTemplateDto;
import com.djt.hvac.domain.model.dictionary.dto.PointTemplateDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NodeTagTemplatesContainerTest extends AbstractResoluteDomainModelTest {

  private static final ObjectMapper MAPPER = AbstractEntity.OBJECT_MAPPER.get();
  
  private static final String JSON;
  
  static {
    try (InputStream in = NodeTagTemplatesContainerTest.class.getResourceAsStream("NodeTagTemplateList.json");
        Scanner s = new Scanner(in).useDelimiter("\\A")) {
     JSON = s.hasNext() ? s.next() : "";

    } catch (IOException e) {
      throw new RuntimeException(e);
    }   
  }
  
  @Test
  public void deserialize() throws IOException {
    
    // STEP 1: ARRANGE
    
    
    // STEP 2: ACT
    List<NodeTagTemplateDto> dtoList = MAPPER.readValue(JSON, new TypeReference<List<NodeTagTemplateDto>>() {});
    NodeTagTemplatesContainer container = NodeTagTemplatesContainer.mapFromDtos(
        dictionaryRepository.getTagsContainer(),
        dictionaryRepository.getUnitsContainer(),
        dtoList,
        new ArrayList<>(),
        new ArrayList<>());
    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("container is null", container);
  }
  
  @Test
  public void serialize() throws IOException {
    
    // STEP 1: ARRANGE
    List<NodeTagTemplateDto> dtoList = MAPPER.readValue(JSON, new TypeReference<List<NodeTagTemplateDto>>() {});
    NodeTagTemplatesContainer container = NodeTagTemplatesContainer.mapFromDtos(
        dictionaryRepository.getTagsContainer(),
        dictionaryRepository.getUnitsContainer(),
        dtoList,
        new ArrayList<>(),
        new ArrayList<>());
    
    Map<String, Object> map = NodeTagTemplatesContainer.mapToDtos(container);
    @SuppressWarnings("unchecked")
    List<NodeTagTemplateDto> serializedDtoList = (List<NodeTagTemplateDto>)map.get("pointTemplates");
    
    
    // STEP 2: ACT
    String json = ObjectMappers.create().writerWithDefaultPrettyPrinter().writeValueAsString(serializedDtoList);
    
    
    // STEP 3: ASSERT
    System.err.println(json);
    Assert.assertNotNull("json is null", json);
  }
  
  @Test
  public void getEquipmentPointTemplateHierarchyDto() throws IOException {

    // STEP 1: ARRANGE
    List<NodeTagTemplateDto> dtoList = MAPPER.readValue(JSON, new TypeReference<List<NodeTagTemplateDto>>() {});
    NodeTagTemplatesContainer container = NodeTagTemplatesContainer.mapFromDtos(
        dictionaryRepository.getTagsContainer(),
        dictionaryRepository.getUnitsContainer(),
        dtoList,
        new ArrayList<>(),
        new ArrayList<>());
    
    
    // STEP 2: ACT
    List<EquipmentPointTemplateDto> dto = container.getEquipmentPointTemplateHierarchyDto();
    String json = ObjectMappers.create().writerWithDefaultPrettyPrinter().writeValueAsString(dto);
    
    
    // STEP 3: ASSERT
    //System.err.println(json);
    Assert.assertNotNull("json is null", json);
  }  

  @Test
  public void getBuildingPointTemplatesDto() throws IOException {

    // STEP 1: ARRANGE
    List<NodeTagTemplateDto> dtoList = MAPPER.readValue(JSON, new TypeReference<List<NodeTagTemplateDto>>() {});
    NodeTagTemplatesContainer container = NodeTagTemplatesContainer.mapFromDtos(
        dictionaryRepository.getTagsContainer(),
        dictionaryRepository.getUnitsContainer(),
        dtoList,
        new ArrayList<>(),
        new ArrayList<>());
    
    
    // STEP 2: ACT
    List<PointTemplateDto> dto = container.getBuildingPointTemplatesDto();
    String json = ObjectMappers.create().writerWithDefaultPrettyPrinter().writeValueAsString(dto);
    
    
    // STEP 3: ASSERT
    //System.err.println(json);
    Assert.assertNotNull("json is null", json);
  }
}