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
import com.djt.hvac.domain.model.dictionary.container.AdFunctionTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.dto.function.DatabaseWrapperDto;
import com.djt.hvac.domain.model.dictionary.dto.function.computedpoint.AdComputedPointFunctionTemplateHierarchyDto;
import com.djt.hvac.domain.model.dictionary.dto.function.rule.AdRuleFunctionTemplateHierarchyDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AdFunctionTemplatesContainerTest extends AbstractResoluteDomainModelTest {

  private static final ObjectMapper MAPPER = AbstractEntity.OBJECT_MAPPER.get();
  
  private static final String JSON;
  private static final String JSON_BAD_DATA;
  
  static {
    try (InputStream in = AdFunctionTemplatesContainerTest.class.getResourceAsStream("FunctionTemplateList.json");
        Scanner s = new Scanner(in).useDelimiter("\\A")) {
     JSON = s.hasNext() ? s.next() : "";

    } catch (IOException e) {
      throw new RuntimeException(e);
    }   
    
    try (InputStream in = AdFunctionTemplatesContainerTest.class.getResourceAsStream("FunctionTemplateList_with_bad_data.json");
        Scanner s = new Scanner(in).useDelimiter("\\A")) {
      JSON_BAD_DATA = s.hasNext() ? s.next() : "";

    } catch (IOException e) {
      throw new RuntimeException(e);
    }   
  }
  
  @Test
  public void deserialize() throws IOException {
    
    // STEP 1: ARRANGE
    
    
    // STEP 2: ACT
    List<DatabaseWrapperDto> dtoList = MAPPER.readValue(JSON, new TypeReference<List<DatabaseWrapperDto>>() {});
    AdFunctionTemplatesContainer adFunctionTemplatesContainer = AdFunctionTemplatesContainer.mapFromDtos(dtoList, new ArrayList<>());
    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("adFunctionTemplatesContainer is null", adFunctionTemplatesContainer);
  }
  
  @Test
  public void serialize() throws IOException {
    
    // STEP 1: ARRANGE
    List<DatabaseWrapperDto> dtoList = MAPPER.readValue(JSON, new TypeReference<List<DatabaseWrapperDto>>() {});
    AdFunctionTemplatesContainer adFunctionTemplatesContainer = AdFunctionTemplatesContainer.mapFromDtos(dtoList, new ArrayList<>());
    
    
    Map<String, Object> map = AdFunctionTemplatesContainer.mapToDtos(adFunctionTemplatesContainer);
    @SuppressWarnings("unchecked")
    List<DatabaseWrapperDto> serializedDtoList = (List<DatabaseWrapperDto>)map.get("adFunctionTemplates");
    
    
    // STEP 2: ACT
    String json = ObjectMappers.create().writerWithDefaultPrettyPrinter().writeValueAsString(serializedDtoList);
    
    
    // STEP 3: ASSERT
    //System.err.println(json);
    Assert.assertNotNull("json is null", json);
  }

  @Test
  public void getAdComputedPointFunctionTemplateHierarchyDto() throws IOException {
    
    // STEP 1: ARRANGE
    List<DatabaseWrapperDto> dtoList = MAPPER.readValue(JSON, new TypeReference<List<DatabaseWrapperDto>>() {});
    AdFunctionTemplatesContainer adFunctionTemplatesContainer = AdFunctionTemplatesContainer.mapFromDtos(dtoList, new ArrayList<>());
    
    
    // STEP 2: ACT
    AdComputedPointFunctionTemplateHierarchyDto computedPointTemplateHierarchy = adFunctionTemplatesContainer.getAdComputedPointFunctionTemplateHierarchy();
    String json = ObjectMappers.create().writerWithDefaultPrettyPrinter().writeValueAsString(computedPointTemplateHierarchy);
    
    
    // STEP 3: ASSERT
    //System.err.println(json);
    Assert.assertNotNull("json is null", json);
  }  

  @Test
  public void getAdRuleFunctionTemplateHierarchyDto_badData() throws IOException {
    
    // STEP 1: ARRANGE
    List<DatabaseWrapperDto> dtoList = MAPPER.readValue(JSON_BAD_DATA, new TypeReference<List<DatabaseWrapperDto>>() {});
    AdFunctionTemplatesContainer adFunctionTemplatesContainer = AdFunctionTemplatesContainer.mapFromDtos(dtoList, new ArrayList<>());
    
    
    // STEP 2: ACT
    AdRuleFunctionTemplateHierarchyDto ruleTemplateHierarchy = adFunctionTemplatesContainer.getAdRuleFunctionTemplateHierarchyDto();
    String json = ObjectMappers.create().writerWithDefaultPrettyPrinter().writeValueAsString(ruleTemplateHierarchy);
    
    
    // STEP 3: ASSERT
    //System.err.println("AD Rule Function Templates: " + json);
    Assert.assertNotNull("json is null", json);
  }
 
  @Test
  public void getAdRuleFunctionTemplateHierarchyDto() throws IOException {
    
    // STEP 1: ARRANGE
    List<DatabaseWrapperDto> dtoList = MAPPER.readValue(JSON, new TypeReference<List<DatabaseWrapperDto>>() {});
    AdFunctionTemplatesContainer adFunctionTemplatesContainer = AdFunctionTemplatesContainer.mapFromDtos(dtoList, new ArrayList<>());
    
    
    // STEP 2: ACT
    AdRuleFunctionTemplateHierarchyDto ruleTemplateHierarchy = adFunctionTemplatesContainer.getAdRuleFunctionTemplateHierarchyDto();
    String json = ObjectMappers.create().writerWithDefaultPrettyPrinter().writeValueAsString(ruleTemplateHierarchy);
    
    
    // STEP 3: ASSERT
    //System.err.println(json);
    Assert.assertNotNull("json is null", json);
  }  
}