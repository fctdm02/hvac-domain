package com.djt.hvac.domain.model.dictionary.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.djt.hvac.domain.model.dictionary.ScheduledEventTypeEntity;
import com.djt.hvac.domain.model.dictionary.dto.ScheduledEventTypeDto;

public class ScheduledEventTypesContainer {

  private final Map<Integer, ScheduledEventTypeEntity> scheduledEventTypes;
  
  public ScheduledEventTypesContainer() {
    super();
    scheduledEventTypes = new HashMap<>();
  }

  public ScheduledEventTypesContainer(Map<Integer, ScheduledEventTypeEntity> scheduledEventTypes) {
    super();
    this.scheduledEventTypes = scheduledEventTypes;
  }
  
  public Set<ScheduledEventTypeEntity> getScheduledEventTypes() {
    
    Set<ScheduledEventTypeEntity> set = new TreeSet<>();
    set.addAll(scheduledEventTypes.values());
    return set;
  }
  
  public ScheduledEventTypeEntity getScheduledEventType(Integer scheduledEventTypeId) {
    
    return this.scheduledEventTypes.get(scheduledEventTypeId);
  }
  
  public void addUnit(ScheduledEventTypeEntity scheduledEventType) {
    
    scheduledEventTypes.put(scheduledEventType.getPersistentIdentity(), scheduledEventType);
  }
  
  @Override
  public String toString() {
    return new StringBuilder()
        .append("ScheduledEventTypesContainer [scheduledEventTypes=")
        .append(scheduledEventTypes)
        .append("]")
        .toString();
  }

  public static ScheduledEventTypesContainer mapFromDtos(List<ScheduledEventTypeDto> dtoList) {
    
    Map<Integer, ScheduledEventTypeEntity> map = new HashMap<>(); 
    Iterator<ScheduledEventTypeDto> iterator = dtoList.iterator();
    while (iterator.hasNext()) {
      
      ScheduledEventTypeDto dto = iterator.next();
      
      Integer id = dto.getId();
      Set<String> tags = new TreeSet<>();
      String haystackTags = dto
          .getHaystackTags()
          .replace("{", "")
          .replace("}", "")
          .replace("\"", "");
      String[] tagArray = haystackTags.split(",");
      for (int i=0; i < tagArray.length; i++) {
        tags.add(tagArray[i]);
      }
      map.put(id, new ScheduledEventTypeEntity(
          id,
          dto.getName(),
          dto.getMetricId(),
          dto.getRange(),
          tags));
    }
    ScheduledEventTypesContainer scheduledEventTypesContainer = new ScheduledEventTypesContainer(map);
    return scheduledEventTypesContainer;
  }
  
  public static List<ScheduledEventTypeDto> mapToDtos(ScheduledEventTypesContainer scheduledEventTypesContainer) {
    
    List<ScheduledEventTypeDto> dtos = new ArrayList<>();
    Iterator<ScheduledEventTypeEntity> iterator = scheduledEventTypesContainer.getScheduledEventTypes().iterator();
    while (iterator.hasNext()) {
      
      ScheduledEventTypeEntity entity = iterator.next();
      
      ScheduledEventTypeDto dto = new ScheduledEventTypeDto();
      dto.setId(entity.getPersistentIdentity());
      dto.setName(entity.getName());
      dto.setMetricId(entity.getMetricId());
      dto.setRange(entity.getRange());
      dto.setHaystackTags("{" + entity.getHaystackTagsAsString() + "}");
      dtos.add(dto);
    }
    return dtos;
  }  
}