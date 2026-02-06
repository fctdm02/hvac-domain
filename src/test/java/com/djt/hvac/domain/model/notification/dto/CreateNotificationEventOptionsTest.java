package com.djt.hvac.domain.model.notification.dto;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.notification.dto.CreateNotificationEventOptions;
import com.djt.hvac.domain.model.notification.enums.NotificationEventType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public class CreateNotificationEventOptionsTest {

  private static final String PARENT_EVENT_JSON;
  private static final String PARENT_EVENT_COMPACT_JSON;

  private static final String CHILD_EVENT_JSON;
  private static final String CHILD_EVENT_COMPACT_JSON;
  
  static {
    try (InputStream in = CreateNotificationEventOptions.class.getResourceAsStream("ParentCreateNotificationEventOptions.json");
        Scanner s = new Scanner(in).useDelimiter("\\A")) {
      PARENT_EVENT_JSON = s.hasNext() ? s.next() : "";

     JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(PARENT_EVENT_JSON, JsonNode.class);
     PARENT_EVENT_COMPACT_JSON = jsonNode.toString();

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    
    try (InputStream in = CreateNotificationEventOptions.class.getResourceAsStream("ChildCreateNotificationEventOptions.json");
        Scanner s = new Scanner(in).useDelimiter("\\A")) {
      CHILD_EVENT_JSON = s.hasNext() ? s.next() : "";

     JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(CHILD_EVENT_JSON, JsonNode.class);
     CHILD_EVENT_COMPACT_JSON = jsonNode.toString();

    } catch (IOException e) {
      throw new RuntimeException(e);
    }   
  }

  @Test
  public void serialize_parentEvent() throws JsonProcessingException {
    
    // STEP 1: ARRANGE
    CreateNotificationEventOptions dto = buildParentCreateNotificationEventOptions();
    
    
    // STEP 2: ACT
    String actualJson = AbstractEntity.OBJECT_MAPPER.get().writeValueAsString(dto);
    
    
    // STEP 3: ASSERT
    Assert.assertEquals("serialized json is incorrect", PARENT_EVENT_COMPACT_JSON, actualJson);
  }
  
  @Test
  public void deserialize_parentEvent() throws IOException {
    
    // STEP 1: ARRANGE
    CreateNotificationEventOptions expected = buildParentCreateNotificationEventOptions();
    
    
    // STEP 2: ACT
    CreateNotificationEventOptions actual = AbstractEntity.OBJECT_MAPPER.get().readValue(PARENT_EVENT_JSON, CreateNotificationEventOptions.class);
    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("dto is null", actual);
    Assert.assertEquals("dtos are not equal", expected.toString(), actual.toString());
  }
  
  @Test
  public void serialize_childEvent() throws JsonProcessingException {
    
    // STEP 1: ARRANGE
    CreateNotificationEventOptions dto = buildChildCreateNotificationEventOptions();
    
    
    // STEP 2: ACT
    String actualJson = AbstractEntity.OBJECT_MAPPER.get().writeValueAsString(dto);
    
    
    // STEP 3: ASSERT
    Assert.assertEquals("serialized json is incorrect", CHILD_EVENT_COMPACT_JSON, actualJson);
  }
  
  @Test
  public void deserialize_childEvent() throws IOException {
    
    // STEP 1: ARRANGE
    CreateNotificationEventOptions expected = buildChildCreateNotificationEventOptions();
    
    
    // STEP 2: ACT
    CreateNotificationEventOptions actual = AbstractEntity.OBJECT_MAPPER.get().readValue(CHILD_EVENT_JSON, CreateNotificationEventOptions.class);
    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("dto is null", actual);
    Assert.assertEquals("dtos are not equal", expected.toString(), actual.toString());
  }
  
  private CreateNotificationEventOptions buildChildCreateNotificationEventOptions() {
    
    return CreateNotificationEventOptions
        .builder()
        .withParentEventId(1)
        .withExpirationDate("2022-12-31 00:00:00")
        .withDetails("Additional details")
        .withPublishImmediately(Boolean.TRUE)
        .build();    
  }  
  
  private CreateNotificationEventOptions buildParentCreateNotificationEventOptions() {
    
    SortedMap<String, String> substitutionTokenValues = new TreeMap<>();
    substitutionTokenValues.put("ABC", "DEF");
    
    return CreateNotificationEventOptions
        .builder()
        .withEventType(NotificationEventType.VPN_STATUS.toString())
        .withCustomerId(4)
        .withExpirationDate("2022-12-31 00:00:00")
        .withSubstitutionTokenValues(substitutionTokenValues)
        .withDetails("Additional details")
        .withPublishImmediately(Boolean.TRUE)
        .build();    
  }
}