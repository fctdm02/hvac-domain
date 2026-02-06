package com.djt.hvac.domain.model.dictionary.dto.report;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.junit.Assert;
import org.junit.Test;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.dictionary.dto.report.ReportTemplateDto;
import com.djt.hvac.domain.model.dictionary.dto.report.ReportTemplateEquipmentSpecDto;
import com.djt.hvac.domain.model.dictionary.dto.report.ReportTemplatePointSpecDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReportTemplateDtoTest {

  private static final ObjectMapper MAPPER = AbstractEntity.OBJECT_MAPPER.get();
  
  private static final String JSON;
  private static final String COMPACT_JSON;
  
  static {
    try (InputStream in = ReportTemplateDtoTest.class.getResourceAsStream("ReportTemplateDto.json");
        Scanner s = new Scanner(in).useDelimiter("\\A")) {
     JSON = s.hasNext() ? s.next() : "";

     JsonNode jsonNode = MAPPER.readValue(JSON, JsonNode.class);
     COMPACT_JSON = jsonNode.toString();

    } catch (IOException e) {
      throw new RuntimeException(e);
    }   
  }

  @Test
  public void serialize() throws JsonProcessingException {
    
    // STEP 1: ARRANGE
    List<ReportTemplatePointSpecDto> pointSpecs = new ArrayList<>();
    pointSpecs.add(ReportTemplatePointSpecDto
        .builder()
        .withId(1001)
        .withName("BldgPress")
        .withType(ReportTemplatePointSpecDto.TYPE_STANDARD)
        .withIsRequired(Boolean.TRUE)
        .withIsArray(Boolean.FALSE)
        .withCurrentObjectExpression("currentObjectExpression")
        .withErrorMessage("errorMessage1")
        .withTags(Arrays.asList("air", "building", "pressure", "sensor"))
        .build());
    
    pointSpecs.add(ReportTemplatePointSpecDto
        .builder()
        .withId(1002)
        .withName("RULE_3_1_10_2_BLDG_AIR_PRESSURE_SENSOR_FAILURE")
        .withType(ReportTemplatePointSpecDto.TYPE_RULE)
        .withIsRequired(Boolean.TRUE)
        .withErrorMessage("errorMessage2")
        .withRuleTemplateId(20)
        .build());

    List<ReportTemplateEquipmentSpecDto> equipmentSpecs = new ArrayList<>();
    equipmentSpecs.add(ReportTemplateEquipmentSpecDto
        .builder()
        .withId(101)
        .withEquipmentTypeId(42)
        .withEquipmentTypeName("ahu")
        .withNodeFilterExpression("nodeFilterExpression1")
        .withNodeFilterErrorMessage("nodeFilterErrorMessage1")
        .withTupleConstraintExpression("tupleConstraintExpression1")
        .withTupleConstraintErrorMessage("tupleConstraintErrorMessage1")
        .withPointSpecs(pointSpecs)
        .build());
    
    ReportTemplateDto reportTemplateDto = ReportTemplateDto
        .builder()
        .withId(11)
        .withName("report template name")
        .withDescription("report template description")
        .withIsInternal(false)
        .withIsBeta(false)
        .withEquipmentSpecs(equipmentSpecs)
        .build();
    
    
    // STEP 2: ACT
    String actualJson = MAPPER.writeValueAsString(reportTemplateDto);
    
    
    // STEP 3: ASSERT
    Assert.assertEquals("serialized json is incorrect", COMPACT_JSON, actualJson);
  }
  
  @Test
  public void deserialize() throws IOException {
    
    // STEP 1: ARRANGE
    
    
    // STEP 2: ACT
    ReportTemplateDto reportTemplateDto = MAPPER.readValue(JSON, ReportTemplateDto.class);
    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("reportTemplateDto is null", reportTemplateDto);
    Assert.assertEquals("id is incorrect", "11", Integer.toString(reportTemplateDto.getId()));
  }
}