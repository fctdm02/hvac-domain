package com.djt.hvac.domain.model.nodehierarchy.event;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.timekeeper.TimeKeeper;
import com.djt.hvac.domain.model.common.timekeeper.impl.TestTimeKeeperImpl;
import com.djt.hvac.domain.model.nodehierarchy.event.NodeHierarchyChangeEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public class NodeHierarchyChangeEventTest {
  
  private static TimeKeeper OLD_TIME_KEEPER;

  private static final String JSON;
  private static final String COMPACT_JSON;
  
  static {
    try (InputStream in = NodeHierarchyChangeEventTest.class.getResourceAsStream("NodeHierarchyChangeEvent.json");
        Scanner s = new Scanner(in).useDelimiter("\\A")) {
     JSON = s.hasNext() ? s.next() : "";

     JsonNode jsonNode = AbstractEntity.OBJECT_MAPPER.get().readValue(JSON, JsonNode.class);
     COMPACT_JSON = jsonNode.toString();

    } catch (IOException e) {
      throw new RuntimeException(e);
    }   
  }

  @BeforeClass
  public static void beforeClass() throws Exception {
    
    OLD_TIME_KEEPER = AbstractEntity.getTimeKeeper();
    AbstractEntity.setTimeKeeper(new TestTimeKeeperImpl("2020-08-27"));
  }

  @AfterClass
  public static void afterClass() throws Exception {
    
    AbstractEntity.setTimeKeeper(OLD_TIME_KEEPER);
  }
  
  @Before
  public void before() throws Exception {
    
  }

  @After
  public void after() throws Exception {
    
  }  
  
  @Test
  public void serialize() throws JsonProcessingException {
    
    // STEP 1: ARRANGE
    NodeHierarchyChangeEvent event = createEvent();
    
    
    // STEP 2: ACT
    String actualJson = AbstractEntity.OBJECT_MAPPER.get().writeValueAsString(event);
    
    
    // STEP 3: ASSERT
    Assert.assertEquals("serialized json is incorrect", COMPACT_JSON, actualJson);
  }
  
  @Test
  public void deserialize() throws IOException {
    
    // STEP 1: ARRANGE
    NodeHierarchyChangeEvent expectedEvent = createEvent();
    String expectedJson = AbstractEntity.OBJECT_MAPPER.get().writeValueAsString(expectedEvent);
    
    
    // STEP 2: ACT
    NodeHierarchyChangeEvent actualEvent = AbstractEntity.OBJECT_MAPPER.get().readValue(JSON, NodeHierarchyChangeEvent.class);
    
    
    // STEP 3: ASSERT
    Assert.assertNotNull("actualEvent is null", actualEvent);
    String actualJson = AbstractEntity.OBJECT_MAPPER.get().writeValueAsString(actualEvent);
    Assert.assertEquals("content is incorrect", expectedJson, actualJson);
  }
  
  private NodeHierarchyChangeEvent createEvent() {
    
    UUID eventUuid = UUID.fromString("6648c387-c975-4572-8c5f-9a50157573d6");
    Timestamp occurredOnDate = AbstractEntity.getTimeKeeper().getCurrentTimestamp();
    String owner = "tmyers@resolutebi.com"; 
    Integer customerId = 100;
    Integer portfolioId = 1000;
    String operationCategory = "operation_category";
    String operationType = "operation_type";
    List<Integer> createdNodeIds = Arrays.asList(1,2,3);
    List<Integer> updatedNodeIds = Arrays.asList(4,5,6);
    List<Integer> deletedNodeIds = Arrays.asList(7,8,9);
    List<Integer> enabledAdFunctionInstanceIds = Arrays.asList(10,11,12);
    List<Integer> enabledReportInstanceIds = Arrays.asList(13,14,15);
    List<Integer> disabledAdFunctionInstanceIds = Arrays.asList(16, 17, 18);
    List<Integer> disabledReportInstanceIds = Arrays.asList(19, 20, 21);
    
    return NodeHierarchyChangeEvent
        .builder()
        .withEventUuid(eventUuid)
        .withOccurredOnDate(occurredOnDate)
        .withOwner(owner)
        .withCustomerId(customerId)
        .withPortfolioId(portfolioId)
        .withOperationType(operationType)
        .withOperationCategory(operationCategory)
        .withCreatedNodeIds(createdNodeIds)
        .withUpdatedNodeIds(updatedNodeIds)
        .withDeletedNodeIds(deletedNodeIds)
        .withEnabledAdFunctionInstanceIds(enabledAdFunctionInstanceIds)
        .withEnabledReportInstanceIds(enabledReportInstanceIds)
        .withDisabledAdFunctionInstanceIds(disabledAdFunctionInstanceIds)
        .withDisabledReportInstanceIds(disabledReportInstanceIds)
        .build();  
  }
}