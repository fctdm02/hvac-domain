package com.djt.hvac.domain.model.dictionary.dto.function;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.junit.Assert;
import org.junit.Test;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.dictionary.dto.function.AdFunctionTemplateDto;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AdFunctionTemplateDtoTest {

  private static final ObjectMapper MAPPER = AbstractEntity.OBJECT_MAPPER.get();
  
  private static final String JSON;
  
  static {
    try (InputStream in = AdFunctionTemplateDtoTest.class.getResourceAsStream("AdFunctionTemplateDto.json");
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
    AdFunctionTemplateDto adFunctionTemplateDto = MAPPER.readValue(JSON, AdFunctionTemplateDto.class);
    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("ruleTemplateDto is null", adFunctionTemplateDto);
    Assert.assertEquals("id is incorrect", "4", Integer.toString(adFunctionTemplateDto.getId()));
  }
}