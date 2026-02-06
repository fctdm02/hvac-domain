package com.djt.hvac.domain.model.nodehierarchy.service.command;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.nodehierarchy.service.command.CreateAdFunctionInstancesRequest;
import com.fasterxml.jackson.databind.JsonNode;

public class CreateAdFunctionInstancesRequestTest {

  private static final String JSON1;
  private static final String COMPACT_JSON1;

  private static final String JSON2;
  private static final String COMPACT_JSON2;
  
  static {
    try (InputStream in = CreateAdFunctionInstancesRequestTest.class.getResourceAsStream("createAdFunctionInstances.json");
        Scanner s = new Scanner(in).useDelimiter("\\A")) {
      JSON1 = s.hasNext() ? s.next() : "";

      JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(JSON1, JsonNode.class);
      COMPACT_JSON1 = jsonNode.toString();

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    
    try (InputStream in = CreateAdFunctionInstancesRequestTest.class.getResourceAsStream("createAdFunctionInstances2.json");
        Scanner s = new Scanner(in).useDelimiter("\\A")) {
      JSON2 = s.hasNext() ? s.next() : "";

      JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(JSON2, JsonNode.class);
      COMPACT_JSON2 = jsonNode.toString();

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void serialization() throws IOException {

    CreateAdFunctionInstancesRequest commandRequest = CreateAdFunctionInstancesRequest
        .builder()
        .withCustomerId(1)
        .withBuildingId(2)
        .withEquipmentId(3)
        .withCandidateIds(Arrays.asList(4,5,6))
        .withSubmittedBy("tmyers@resolutebi.com")
        .withFunctionType("RULE")
        .build();

    String json1 = AbstractEntity.OBJECT_MAPPER.get().writeValueAsString(commandRequest);

    Assert.assertEquals("json1 is incorrect", json1, COMPACT_JSON1);
    
    
    Map<Integer, Set<Integer>> map = new TreeMap<>();
    Set<Integer> set = new TreeSet<>();
    set.add(4);
    set.add(5);
    set.add(6);
    map.put(3, set);
    
    commandRequest = CreateAdFunctionInstancesRequest
        .builder()
        .withCustomerId(1)
        .withBuildingId(2)
        .withEquipmentId(3)
        .withCandidateTemplateEquipmentIds(map)
        .withSubmittedBy("tmyers@resolutebi.com")
        .withFunctionType("RULE")
        .build();

    String json2 = AbstractEntity.OBJECT_MAPPER.get().writeValueAsString(commandRequest);

    Assert.assertEquals("json2 is incorrect", json2, COMPACT_JSON2);    
  }

  @Test
  public void deserialization() throws IOException {
    
    CreateAdFunctionInstancesRequest commandRequest = AbstractEntity.OBJECT_MAPPER.get().readValue(JSON1, CreateAdFunctionInstancesRequest.class);

    Assert.assertNotNull(commandRequest);
  }
}
