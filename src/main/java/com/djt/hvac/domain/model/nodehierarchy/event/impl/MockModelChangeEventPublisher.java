package com.djt.hvac.domain.model.nodehierarchy.event.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.event.AbstractEvent;
import com.djt.hvac.domain.model.dictionary.event.DictionaryChangeEvent;
import com.djt.hvac.domain.model.nodehierarchy.event.ModelChangeEventPublisher;
import com.djt.hvac.domain.model.nodehierarchy.event.NodeHierarchyChangeEvent;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 *
 * @author tmyers
 *
 */
public class MockModelChangeEventPublisher implements ModelChangeEventPublisher {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(MockModelChangeEventPublisher.class);

  public static List<AbstractEvent> PUBLISHED_EVENTS = new ArrayList<>();
  
  private static MockModelChangeEventPublisher INSTANCE = new MockModelChangeEventPublisher();
  
  public static MockModelChangeEventPublisher getInstance() {
    return INSTANCE;
  }

  private MockModelChangeEventPublisher() {
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public NodeHierarchyChangeEvent publishEvent(Map<String, Object> payload) {
    
    NodeHierarchyChangeEvent event = NodeHierarchyChangeEvent
        .builder()
        .withEventUuid((UUID)payload.get(AbstractEvent.EVENT_UUID_KEY))
        .withOccurredOnDate((Timestamp)payload.get(AbstractEvent.OCCURRED_ON_DATE_KEY))
        .withOwner((String)payload.get(AbstractEvent.OWNER_KEY))
        .withCustomerId((Integer)payload.get(NodeHierarchyChangeEvent.CUSTOMER_ID_KEY))
        .withPortfolioId((Integer)payload.get(NodeHierarchyChangeEvent.PORTFOLIO_ID_KEY))
        .withOperationType((String)payload.get(NodeHierarchyChangeEvent.OPERATION_TYPE_KEY))
        .withOperationCategory((String)payload.get(NodeHierarchyChangeEvent.OPERATION_CATEGORY_KEY))
        .withCreatedNodeIds((List<Integer>)payload.get(NodeHierarchyChangeEvent.CREATED_NODE_IDS_KEY))
        .withUpdatedNodeIds((List<Integer>)payload.get(NodeHierarchyChangeEvent.UPDATED_NODE_IDS_KEY))
        .withDeletedNodeIds((List<Integer>)payload.get(NodeHierarchyChangeEvent.DELETED_NODE_IDS_KEY))
        .withEnabledAdFunctionInstanceIds((List<Integer>)payload.get(NodeHierarchyChangeEvent.ENABLED_AD_FUNCTION_INSTANCE_IDS_KEY))
        .withEnabledReportInstanceIds((List<Integer>)payload.get(NodeHierarchyChangeEvent.ENABLED_REPORT_INSTANCE_IDS_KEY))
        .withDisabledAdFunctionInstanceIds((List<Integer>)payload.get(NodeHierarchyChangeEvent.DISABLED_AD_FUNCTION_INSTANCE_IDS_KEY))
        .withDisabledReportInstanceIds((List<Integer>)payload.get(NodeHierarchyChangeEvent.DISABLED_REPORT_INSTANCE_IDS_KEY))
        .build();

    PUBLISHED_EVENTS.add(event);

    return event;
  }
  
  @Override
  public DictionaryChangeEvent publishDictionaryChangeEvent(String category) {
    
    DictionaryChangeEvent event = DictionaryChangeEvent
        .builder()
        .withCategory(category)
        .build();
    
    PUBLISHED_EVENTS.add(event);
    
    return event;
  }
  
  public void printEventsAsJson() {
    
    for (AbstractEvent event: PUBLISHED_EVENTS) {
      try {
        LOGGER.error(AbstractEntity.OBJECT_MAPPER.get().writeValueAsString(event));
      } catch (JsonProcessingException e) {
        throw new RuntimeException("Unable to marshall event to JSON: "
            + event
            + "], error: "
            + e.getMessage(), e);
      }
    }
  }
}
