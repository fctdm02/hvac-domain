package com.djt.hvac.domain.model.report.status;

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
import com.djt.hvac.domain.model.report.status.BuildingReportStatusValueObject;
import com.djt.hvac.domain.model.report.status.PortfolioReportSummaryValueObject;
import com.djt.hvac.domain.model.report.status.ReportStatusValueObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PortfolioReportStatusValueObjectTest extends AbstractResoluteDomainModelTest {

  private static final ObjectMapper MAPPER = AbstractEntity.OBJECT_MAPPER.get();
  
  private static final String JSON;
  private static final String COMPACT_JSON;
  
  static {
    try (InputStream in = PortfolioReportStatusValueObjectTest.class.getResourceAsStream("PortfolioReportStatusValueObject.json");
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
    
    String lastUpdated = "5/3/2019, 9:45:00 AM";
    
    // STEP 1: ARRANGE
    List<BuildingReportStatusValueObject> buildingReportStatuses = new ArrayList<>();
    buildingReportStatuses.add(BuildingReportStatusValueObject
        .builder()
        .withBuildingId(11)
        .withBuildingName("Daytek Building")
        .withNumGreen(7)
        .withNumYellow(0)
        .withNumRed(1)
        .withReports(new ArrayList<>())
        .build());
    
    buildingReportStatuses.add(BuildingReportStatusValueObject
        .builder()
        .withBuildingId(12)
        .withBuildingName("First National")
        .withNumGreen(8)
        .withNumYellow(0)
        .withNumRed(0)
        .withReports(new ArrayList<>())
        .build());
    
    List<ReportStatusValueObject> reports = new ArrayList<>();
    reports.add(ReportStatusValueObject
        .builder()
        .withReportTemplateId(1)
        .withReportTemplateName("Operating Room HVAC Performance")
        .withReportTemplateDescription("Operating Room HVAC Performance Description")
        .withNumGreenEquipment(272)
        .withNumEquipmentTotal(272)
        .withStatus(ReportStatusValueObject.GREEN)
        .withIsEnabled(true)
        .withIsValid(true)
        .withIsIgnored(false)
        .withLastUpdated(lastUpdated)
        .withPriority("LOW")
        .build());

    reports.add(ReportStatusValueObject
        .builder()
        .withReportTemplateId(2)
        .withReportTemplateName("Operating Room Compliance")
        .withReportTemplateDescription("Operating Room Compliance Description")
        .withNumGreenEquipment(301)
        .withNumEquipmentTotal(301)
        .withStatus(ReportStatusValueObject.GREEN)
        .withIsEnabled(true)
        .withIsValid(true)
        .withIsIgnored(false)
        .withLastUpdated(lastUpdated)
        .withPriority("LOW")
        .build());

    reports.add(ReportStatusValueObject
        .builder()
        .withReportTemplateId(3)
        .withReportTemplateName("AHU | RTU Operations Score")
        .withReportTemplateDescription("AHU | RTU Operations Score Description")
        .withNumGreenEquipment(150)
        .withNumEquipmentTotal(175)
        .withStatus(ReportStatusValueObject.YELLOW)
        .withIsEnabled(false)
        .withIsValid(true)
        .withIsIgnored(false)
        .withLastUpdated(lastUpdated)
        .withPriority("LOW")
        .build());

    List<String> errorMessages = new ArrayList<>();
    errorMessages.add("Missing ZoneTemp point");
    
    reports.add(ReportStatusValueObject
        .builder()
        .withReportTemplateId(4)
        .withReportTemplateName("AHU | RTU Energy Performance Score")
        .withReportTemplateDescription("AHU | RTU Energy Performance Score")
        .withNumGreenEquipment(195)
        .withNumEquipmentTotal(200)
        .withStatus(ReportStatusValueObject.YELLOW)
        .withIsEnabled(false)
        .withIsValid(true)
        .withIsIgnored(false)
        .withLastUpdated(lastUpdated)
        .withPriority("LOW")
        .build());
    
    reports.add(ReportStatusValueObject
        .builder()
        .withReportTemplateId(5)
        .withReportTemplateName("Chilled Water Plant Score")
        .withReportTemplateDescription("Chilled Water Plant Score")
        .withNumGreenEquipment(75)
        .withNumEquipmentTotal(100)
        .withStatus(ReportStatusValueObject.YELLOW)
        .withIsEnabled(false)
        .withIsValid(true)
        .withIsIgnored(false)
        .withLastUpdated(lastUpdated)
        .withPriority("LOW")
        .build());
    
    reports.add(ReportStatusValueObject
        .builder()
        .withReportTemplateId(6)
        .withReportTemplateName("VAV Score")
        .withReportTemplateDescription("VAV Score")
        .withNumGreenEquipment(0)
        .withNumEquipmentTotal(175)
        .withStatus(ReportStatusValueObject.RED)
        .withIsEnabled(false)
        .withIsValid(false)
        .withIsIgnored(false)
        .withLastUpdated(lastUpdated)
        .withPriority("LOW")
        .build());    

    reports.add(ReportStatusValueObject
        .builder()
        .withReportTemplateId(7)
        .withReportTemplateName("Heating Hot Water Plant Score")
        .withReportTemplateDescription("Heating Hot Water Plant Score")
        .withNumGreenEquipment(0)
        .withNumEquipmentTotal(175)
        .withStatus(ReportStatusValueObject.RED)
        .withIsEnabled(false)
        .withIsValid(false)
        .withIsIgnored(false)
        .withLastUpdated(lastUpdated)
        .withPriority("LOW")
        .build());    

    reports.add(ReportStatusValueObject
        .builder()
        .withReportTemplateId(8)
        .withReportTemplateName("Equipment Score")
        .withReportTemplateDescription("Equipment Score")
        .withNumGreenEquipment(0)
        .withNumEquipmentTotal(175)
        .withStatus(ReportStatusValueObject.RED)
        .withIsEnabled(false)
        .withIsValid(false)
        .withIsIgnored(false)
        .withLastUpdated(lastUpdated)
        .withPriority("LOW")
        .build());    
    
    buildingReportStatuses.add(BuildingReportStatusValueObject
        .builder()
        .withBuildingId(13)
        .withBuildingName("Palmer Park Building")
        .withNumGreen(2)
        .withNumYellow(3)
        .withNumRed(3)
        .withReports(reports)
        .build());

    buildingReportStatuses.add(BuildingReportStatusValueObject
        .builder()
        .withBuildingId(14)
        .withBuildingName("Gresham Estates")
        .withNumGreen(6)
        .withNumYellow(1)
        .withNumRed(1)
        .withReports(new ArrayList<>())
        .build());
    
    PortfolioReportSummaryValueObject portfolioReportStatus = PortfolioReportSummaryValueObject
        .builder()
        .withBuildingReportStatuses(buildingReportStatuses)
        .build(); 
    
    
    // STEP 2: ACT
    String actualJson = MAPPER.writeValueAsString(portfolioReportStatus);
    
    
    // STEP 3: ASSERT
    Assert.assertEquals("serialized json is incorrect", COMPACT_JSON, actualJson);
  }
  
  @Test
  public void deserialize() throws IOException {
    
    // STEP 1: ARRANGE
    
    
    // STEP 2: ACT
    PortfolioReportSummaryValueObject portfolioReportStatus = MAPPER.readValue(JSON, new TypeReference<PortfolioReportSummaryValueObject>() {});
    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("portfolioReportStatus", portfolioReportStatus);
  }
}