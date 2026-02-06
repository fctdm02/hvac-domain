package com.djt.hvac.domain.model.report.status;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.djt.hvac.domain.model.AbstractResoluteDomainModelTest;
import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.report.status.ReportEquipmentErrorMessageListResponse;
import com.djt.hvac.domain.model.report.status.ReportEquipmentErrorMessageSearchCriteria;
import com.djt.hvac.domain.model.report.status.ReportEquipmentErrorMessageValueObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReportEquipmentErrorMessageListResponseTest extends AbstractResoluteDomainModelTest {

  private static final ObjectMapper MAPPER = AbstractEntity.OBJECT_MAPPER.get();
  
  private static final String JSON;
  private static final String COMPACT_JSON;
  
  static {
    try (InputStream in = ReportEquipmentErrorMessageListResponse.class.getResourceAsStream("ReportEquipmentErrorMessageListResponse.json");
        Scanner s = new Scanner(in).useDelimiter("\\A")) {
     JSON = s.hasNext() ? s.next() : "";

     JsonNode jsonNode = MAPPER.readValue(JSON, JsonNode.class);
     COMPACT_JSON = jsonNode.toString();

    } catch (IOException e) {
      throw new RuntimeException(e);
    }   
  }

  
  @Before
  public void before() throws Exception {
    
    super.before();
  }

  @Test
  public void serialize() throws Exception {
    
    // STEP 1: ARRANGE
    List<String> errorMessages = Arrays.asList("Missing ZoneTemp point");
    
    List<ReportEquipmentErrorMessageValueObject> equipmentErrorMessages = new ArrayList<>();
    equipmentErrorMessages.add(ReportEquipmentErrorMessageValueObject
        .builder()
        .withEquipmentId(1001)
        .withEquipmentNodePath("McLaren/Bay Region/AHU_01")
        .withErrorMessages(errorMessages)
        .withErrorMessages(errorMessages)
        .build());

    equipmentErrorMessages.add(ReportEquipmentErrorMessageValueObject
        .builder()
        .withEquipmentId(1002)
        .withEquipmentNodePath("McLaren/Bay Region/AHU_02")
        .withErrorMessages(errorMessages)
        .withErrorMessages(errorMessages)
        .build());

    equipmentErrorMessages.add(ReportEquipmentErrorMessageValueObject
        .builder()
        .withEquipmentId(1003)
        .withEquipmentNodePath("McLaren/Bay Region/AHU_03")
        .withErrorMessages(errorMessages)
        .withErrorMessages(errorMessages)
        .build());

    equipmentErrorMessages.add(ReportEquipmentErrorMessageValueObject
        .builder()
        .withEquipmentId(1004)
        .withEquipmentNodePath("McLaren/Bay Region/AHU_04")
        .withErrorMessages(errorMessages)
        .withErrorMessages(errorMessages)
        .build());

    equipmentErrorMessages.add(ReportEquipmentErrorMessageValueObject
        .builder()
        .withEquipmentId(1005)
        .withEquipmentNodePath("McLaren/Bay Region/AHU_05")
        .withErrorMessages(errorMessages)
        .withErrorMessages(errorMessages)
        .build());
    
    ReportEquipmentErrorMessageSearchCriteria searchCriteria = ReportEquipmentErrorMessageSearchCriteria
        .builder()
        .withBuildingId(100)
        .withReportTemplateId(10)
        .build();
    
    
    ReportEquipmentErrorMessageListResponse response = ReportEquipmentErrorMessageListResponse
        .builder()
        .withSearchCriteria(searchCriteria)
        .withCount(equipmentErrorMessages.size())
        .withData(equipmentErrorMessages)
        .build();
    
    
    // STEP 2: ACT
    String actualJson = MAPPER.writeValueAsString(response);
    
    
    // STEP 3: ASSERT
    Assert.assertEquals("serialized json is incorrect", COMPACT_JSON, actualJson);
  }
  
  @Test
  public void deserialize() throws IOException {
    
    // STEP 1: ARRANGE
    
    
    // STEP 2: ACT
    ReportEquipmentErrorMessageListResponse response = MAPPER.readValue(JSON, new TypeReference<ReportEquipmentErrorMessageListResponse>() {});
    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("response", response);
  }
}