package com.djt.hvac.domain.model.report.dto;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

import com.djt.hvac.domain.model.AbstractResoluteDomainModelTest;
import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.report.dto.ReportInstanceEquipmentDto;
import com.djt.hvac.domain.model.report.dto.ReportInstancePointDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReportInstanceEquipmentDtoListTest extends AbstractResoluteDomainModelTest {

  private static final ObjectMapper MAPPER = AbstractEntity.OBJECT_MAPPER.get();

  private static final String JSON;

  static {
    try (InputStream in = ReportInstanceEquipmentDtoListTest.class.getResourceAsStream("ReportInstanceEquipmentDtoList.json");
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
    List<ReportInstanceEquipmentDto> equipment = MAPPER.readValue(JSON, new TypeReference<List<ReportInstanceEquipmentDto>>() {});


    // STEP 3: ASSERT
    Assert.assertNotNull("equipment is null", equipment);
    
    
    List<ReportInstancePointDto> list = new ArrayList<>();
    for (ReportInstanceEquipmentDto dto: equipment) {
      list.addAll(dto.getReportInstancePoints());
    }
    int listSize = list.size();
    
    Set<ReportInstancePointDto> set = new TreeSet<>();
    set.addAll(list);
    int setSize = set.size();
    
    Assert.assertEquals("listSize does not equal setSize", 
        Integer.toString(listSize*2),
        Integer.toString(listSize+setSize));
  }
}
