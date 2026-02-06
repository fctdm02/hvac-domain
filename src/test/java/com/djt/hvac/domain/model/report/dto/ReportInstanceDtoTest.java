package com.djt.hvac.domain.model.report.dto;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.djt.hvac.domain.model.AbstractResoluteDomainModelTest;
import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.dictionary.dto.report.ReportTemplatePointSpecDto;
import com.djt.hvac.domain.model.report.dto.ReportInstanceDto;
import com.djt.hvac.domain.model.report.dto.ReportInstanceEquipmentDto;
import com.djt.hvac.domain.model.report.dto.ReportInstancePointDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReportInstanceDtoTest extends AbstractResoluteDomainModelTest {

  private static final ObjectMapper MAPPER = AbstractEntity.OBJECT_MAPPER.get();

  private static final String JSON;
  private static final String COMPACT_JSON;

  static {
    try (InputStream in = ReportInstanceDtoTest.class.getResourceAsStream("ReportInstanceDto.json");
        Scanner s = new Scanner(in).useDelimiter("\\A")) {
      JSON = s.hasNext() ? s.next() : "";

      JsonNode jsonNode = MAPPER.readValue(JSON, JsonNode.class);
      COMPACT_JSON = jsonNode.toString();

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private final long epochMillis = 1556891100000L;

  @Override
  @Before
  public void before() throws Exception {

    super.before();
  }

  @Test
  public void serialize() throws Exception {

    // STEP 1: ARRANGE
    Integer reportInstanceId = 11111;
    Integer reportTemplateId = 1;
    Integer buildingId = 115149;

    Integer reportTemplateEquipmentSpecId = 1;
    Integer equipmentId = 115320;

    Integer reportTemplateStandardPointSpecId = 1;
    Integer standardPointId = 115151;

    Integer reportTemplateRulePointSpecId = 10;
    Integer rulePointId = 129405;

    List<ReportInstancePointDto> reportInstancePoints = new ArrayList<>();
    reportInstancePoints.add(ReportInstancePointDto
        .builder()
        .withReportInstanceId(reportInstanceId)
        .withReportTemplateEquipmentSpecId(reportTemplateEquipmentSpecId)
        .withReportTemplatePointSpecId(reportTemplateStandardPointSpecId)
        .withEquipmentId(equipmentId)
        .withPointId(standardPointId)
        .withType(ReportTemplatePointSpecDto.TYPE_STANDARD)
        .build());

    reportInstancePoints.add(ReportInstancePointDto
        .builder()
        .withReportInstanceId(reportInstanceId)
        .withReportTemplateEquipmentSpecId(reportTemplateEquipmentSpecId)
        .withReportTemplatePointSpecId(reportTemplateRulePointSpecId)
        .withEquipmentId(equipmentId)
        .withPointId(rulePointId)
        .withType(ReportTemplatePointSpecDto.TYPE_RULE)
        .build());

    List<ReportInstanceEquipmentDto> reportInstanceEquipment = new ArrayList<>();
    reportInstanceEquipment.add(ReportInstanceEquipmentDto
        .builder()
        .withReportTemplateEquipmentSpecId(reportTemplateEquipmentSpecId)
        .withEquipmentId(equipmentId)
        .withReportInstancePoints(reportInstancePoints)
        .build());

    ReportInstanceDto reportInstanceDto = ReportInstanceDto
        .builder()
        .withId(reportInstanceId)
        .withReportTemplateId(reportTemplateId)
        .withBuildingId(buildingId)
        .withCreatedAt(epochMillis)
        .withUpdatedAt(epochMillis)
        .withReportInstanceEquipment(reportInstanceEquipment)
        .build();


    // STEP 2: ACT
    String actualJson = MAPPER.writeValueAsString(reportInstanceDto);


    // STEP 3: ASSERT
    Assert.assertEquals("serialized json is incorrect", COMPACT_JSON, actualJson);
  }

  @Test
  public void deserialize() throws IOException {

    // STEP 1: ARRANGE


    // STEP 2: ACT
    ReportInstanceDto reportInstanceDto = MAPPER.readValue(JSON, ReportInstanceDto.class);


    // STEP 3: ASSERT
    Assert.assertNotNull("reportInstanceDto is null", reportInstanceDto);
    Assert.assertEquals("id is incorrect", "11111", Integer.toString(reportInstanceDto.getId()));
  }
}
