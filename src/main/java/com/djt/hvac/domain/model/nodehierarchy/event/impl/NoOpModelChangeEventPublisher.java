package com.djt.hvac.domain.model.nodehierarchy.event.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.djt.hvac.domain.model.common.event.AbstractEvent;
import com.djt.hvac.domain.model.dictionary.event.DictionaryChangeEvent;
import com.djt.hvac.domain.model.nodehierarchy.event.ModelChangeEventPublisher;
import com.djt.hvac.domain.model.nodehierarchy.event.NodeHierarchyChangeEvent;

/**
 *
 * @author tmyers
 *
 */
public class NoOpModelChangeEventPublisher implements ModelChangeEventPublisher {

  private static NoOpModelChangeEventPublisher INSTANCE = new NoOpModelChangeEventPublisher();

  public static NoOpModelChangeEventPublisher getInstance() {
    return INSTANCE;
  }

  private NoOpModelChangeEventPublisher() {
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public NodeHierarchyChangeEvent publishEvent(Map<String, Object> payload) {

    return NodeHierarchyChangeEvent
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
  }
  
  @Override
  public DictionaryChangeEvent publishDictionaryChangeEvent(String category) {
    
    return DictionaryChangeEvent
        .builder()
        .withCategory(category)
        .build();
  }  
}