package com.djt.hvac.domain.model.dictionary.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.dto.IdNameDto;
import com.djt.hvac.domain.model.dictionary.dto.TagDto;
import com.djt.hvac.domain.model.dictionary.energyexchange.AbstractEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.EquipmentEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.LoopEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.PlantEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.enums.NodeType;
import com.djt.hvac.domain.model.dictionary.enums.TagGroupType;
import com.djt.hvac.domain.model.dictionary.enums.TagType;

import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class TagsContainer {

  private final Map<Integer, TagEntity> tagsById;
  private final Map<String, TagEntity> tagsByName;
  
  private final Map<String, TagEntity> haystackTagsByName;
  private final Map<String, Integer> haystackTagsIdsByName;
  
  private final Map<String, TagEntity> equipmentMetadataTagsByName;
  private final Map<String, Integer> equipmentMetadataTagIdsByName;
  
  private final Map<Integer, EquipmentEnergyExchangeTypeEntity> equipmentEnergyExchangeTypesById;
  private final Map<String, EquipmentEnergyExchangeTypeEntity> equipmentEnergyExchangeTypesByName;

  private final Map<Integer, PlantEnergyExchangeTypeEntity> plantEnergyExchangeTypesById;
  private final Map<String, PlantEnergyExchangeTypeEntity> plantEnergyExchangeTypesByName;

  private final Map<Integer, LoopEnergyExchangeTypeEntity> loopEnergyExchangeTypesById;
  private final Map<String, LoopEnergyExchangeTypeEntity> loopEnergyExchangeTypesByName;
  
  private Integer maxTagId = Integer.valueOf(0);
  
  public TagsContainer(Map<Integer, TagEntity> tags) {
    super();
    
    this.tagsById = tags;
    this.tagsByName = new HashMap<>();
    
    this.haystackTagsByName = new TreeMap<>();
    this.haystackTagsIdsByName = new HashMap<>();
    
    this.equipmentMetadataTagsByName = new TreeMap<>();
    this.equipmentMetadataTagIdsByName = new TreeMap<>();
    
    this.equipmentEnergyExchangeTypesById = new HashMap<>();
    this.equipmentEnergyExchangeTypesByName = new TreeMap<>();

    this.plantEnergyExchangeTypesById = new HashMap<>();
    this.plantEnergyExchangeTypesByName = new TreeMap<>();

    this.loopEnergyExchangeTypesById = new HashMap<>();
    this.loopEnergyExchangeTypesByName = new TreeMap<>();
    
    Iterator<Entry<Integer, TagEntity>> iterator = tags.entrySet().iterator();
    while (iterator.hasNext()) {
      
      Entry<Integer, TagEntity> entry = iterator.next();
      
      Integer tagId = entry.getKey();
      
      if (tagId.intValue() > maxTagId.intValue()) {
        maxTagId = tagId;
      }
      
      TagEntity tag = entry.getValue();
      String tagName = tag.getName();
      
      // There will be collisions in this collection, but we don't care about tag group for curr. obj. expr. parsing
      this.tagsByName.put(tagName, tag);
      
      if (tag.getTagGroupType().equals(TagGroupType.POINT_HAYSTACK_TAG)) {
        
        this.haystackTagsByName.put(tagName, tag);
        this.haystackTagsIdsByName.put(tagName, tagId);
        
      } else if (tag.getTagGroupType().equals(TagGroupType.EQUIPMENT_METADATA)) {
        
        this.equipmentMetadataTagsByName.put(tagName, tag);
        this.equipmentMetadataTagIdsByName.put(tagName, tagId);
        
      } else if (tag.getTagGroupType().equals(TagGroupType.EQUIPMENT_TYPE)) {
        
        EquipmentEnergyExchangeTypeEntity equipmentEnergyExchangeType = new EquipmentEnergyExchangeTypeEntity(tag); 
        this.equipmentEnergyExchangeTypesById.put(tagId, equipmentEnergyExchangeType);
        this.equipmentEnergyExchangeTypesByName.put(tagName, equipmentEnergyExchangeType);
        
      } else if (tag.getTagGroupType().equals(TagGroupType.PLANT_TYPE)) {
        
        PlantEnergyExchangeTypeEntity plantEnergyExchangeType = new PlantEnergyExchangeTypeEntity(tag); 
        this.plantEnergyExchangeTypesById.put(tagId, plantEnergyExchangeType);
        this.plantEnergyExchangeTypesByName.put(tagName, plantEnergyExchangeType);
        
      } else if (tag.getTagGroupType().equals(TagGroupType.LOOP_TYPE)) {
        
        LoopEnergyExchangeTypeEntity loopEnergyExchangeType = new LoopEnergyExchangeTypeEntity(tag); 
        this.loopEnergyExchangeTypesById.put(tagId, loopEnergyExchangeType);
        this.loopEnergyExchangeTypesByName.put(tagName, loopEnergyExchangeType);
        
      }
    }
  }
  
  public Integer getMaxTagId() {
    return maxTagId;
  }
  
  public Collection<TagEntity> getTags() {
    
    return tagsById.values();
  }

  public Set<TagEntity> getTagsById(Collection<Integer> tagIds) throws EntityDoesNotExistException {

    Set<TagEntity> set = new TreeSet<>();
    Iterator<Integer> iterator = tagIds.iterator();
    while (iterator.hasNext()) {
     
      Integer tagId = iterator.next();
      TagEntity tag = this.tagsById.get(tagId);
      if (tag == null) {
        throw new EntityDoesNotExistException("Could not find tag with id: [" + tagId + "].");
      }
      set.add(tag);
    }
    return set;
  }
  
  public Set<TagEntity> getTagsByTagNames(String tagNames) throws EntityDoesNotExistException {
    
    Set<TagEntity> set = new TreeSet<>();
    if (tagNames != null) {

      tagNames = tagNames
          .replaceAll("\\{", "")
          .replaceAll("\\}", "")
          .replaceAll("\"", "");
      
      if (tagNames.contains(",")) {
        
        String[] tagArray = tagNames.split(",");
        for (int i=0; i < tagArray.length; i++) {
          
          String tagName = tagArray[i].trim();
          TagEntity tag = this.tagsByName.get(tagName);
          if (tag == null) {
            throw new EntityDoesNotExistException("Could not find tag with name: [" + tagName + "].");
          }
          set.add(tag);
        }
      } else {
        String tagName = tagNames.trim();
        TagEntity tag = this.tagsByName.get(tagName);
        if (tag == null) {
          throw new EntityDoesNotExistException("Could not find tag with name: [" + tagName + "].");
        }
        set.add(tag);
      }
    }
    return set;
  }
  
  public Set<TagEntity> getTags(String tagIds) throws EntityDoesNotExistException {
    
    Set<TagEntity> set = new TreeSet<>();
    if (tagIds != null) {

      tagIds = tagIds
          .replaceAll("\\{", "")
          .replaceAll("\\}", "")
          .replaceAll("\"", "");
      
      if (tagIds.contains(",")) {
        
        String[] tagArray = tagIds.split(",");
        for (int i=0; i < tagArray.length; i++) {
          
          Integer tagId = Integer.parseInt(tagArray[i]);
          TagEntity tag = this.tagsById.get(tagId);
          if (tag == null) {
            throw new EntityDoesNotExistException("Could not find tag with id: [" + tagId + "].");
          }
          set.add(tag);
        }
      } else {
        Integer tagId = Integer.parseInt(tagIds.trim());
        TagEntity tag = this.tagsById.get(tagId);
        if (tag == null) {
          throw new EntityDoesNotExistException("Could not find tag with id: [" + tagId + "].");
        }
        set.add(tag);
      }
    }
    return set;
  }
  
  public Set<TagEntity> getTags(List<Integer> tagIds) throws EntityDoesNotExistException {
    
    Set<TagEntity> set = new TreeSet<>();
    for (Integer tagId: tagIds) {

      TagEntity tag = this.tagsById.get(tagId);
      if (tag == null) {
        throw new EntityDoesNotExistException("Could not find tag with id: [" + tagId + "].");
      }
      set.add(tag);
    }
    return set;
  }
  
  public TagEntity getTag(Integer tagId) throws EntityDoesNotExistException {

    if (tagId == null || tagId.intValue() < 1) {
      throw new IllegalStateException("tagId must be specified.");
    }
    
    TagEntity tag = this.tagsById.get(tagId);
    
    if (tag == null) {
      throw new EntityDoesNotExistException("Could not find tag with id: [" + tagId + "].");
    }
    
    return tag;
  }  

  public TagEntity getTagByName(String tagName) throws EntityDoesNotExistException {

    if (tagName == null || tagName.trim().isEmpty()) {
      throw new IllegalStateException("tagName must be specified.");
    }
    
    TagEntity tag = this.tagsByName.get(tagName);
    
    if (tag == null) {
      
      tag = this.tagsByName.get(tagName.trim().toLowerCase());
      if (tag == null) {
        throw new EntityDoesNotExistException("Could not find tag with tagName: [" + tagName + "].");  
      }
    }
    
    return tag;
  }
  
  public TagEntity getTagByName(String tagName, TagGroupType tagGroupType) throws EntityDoesNotExistException {

    TagEntity tag = getTagByNameNullIfNotExists(tagName, tagGroupType);
    
    if (tag != null && tag.getTagGroupType().equals(tagGroupType)) {
      return tag;
    }
    
    throw new EntityDoesNotExistException("Could not find tag with tagName: [" + tagName + "] with tagGroupType: [" + tagGroupType + "].");
  }
  
  public TagEntity getTagByNameNullIfNotExists(String tagName, TagGroupType tagGroupType) throws EntityDoesNotExistException {

    if (tagName == null || tagName.trim().isEmpty()) {
      throw new IllegalStateException("tagName must be specified.");
    }

    if (tagGroupType == null) {
      throw new IllegalStateException("tagGroupType must be specified.");
    }
    
    TagEntity tag = this.tagsByName.get(tagName);
    
    if (tag != null && tag.getTagGroupType().equals(tagGroupType)) {
      return tag;
    }
    
    return null;
  }  
  
  public Set<TagEntity> getHaystackTagsByName(Collection<String> tagNames) throws EntityDoesNotExistException {
    
    Set<TagEntity> set = new TreeSet<>();
    Iterator<String> iterator = tagNames.iterator();
    while (iterator.hasNext()) {
      String tagName = iterator.next();
      if (tagName != null) {
        set.add(getHaystackTag(tagName));  
      }
    }
    return set;
  }  

  public TagEntity getHaystackTag(String tagName) throws EntityDoesNotExistException {
    
    TagEntity tag = this.getHaystackTagNullIfNotExists(tagName.trim());
    if (tag != null) {
      return tag;
    }
    throw new EntityDoesNotExistException("Could not find haystack tag with name: [" + tagName.trim() + "].");
  }
  
  public TagEntity getHaystackTagNullIfNotExists(String tagName) {
    
    if (tagName == null || tagName.trim().isEmpty()) {
      throw new IllegalStateException("tagName must be specified.");
    }
    return haystackTagsByName.get(tagName);
  }
  
  public Integer getHaystackTagId(String tagName) {
    
    try {
      TagEntity tag = getHaystackTag(tagName);
      return tag.getPersistentIdentity();
    } catch (EntityDoesNotExistException e) {
      throw new IllegalStateException(
      "Haystack tag with name: ["
      + tagName 
      + "] does not exist in: [" 
      + this.haystackTagsByName 
      + "]", e);
    }
  }
  
  public Set<TagEntity> getEquipmentMetadataTagsByName(Collection<String> tagNames) throws EntityDoesNotExistException {
    
    Set<TagEntity> set = new TreeSet<>();
    for (String tagName: tagNames) {
      set.add(getEquipmentMetadataTag(tagName));
    }
    return set;
  }  
  
  public TagEntity getEquipmentMetadataTag(String equipmentMetadataTagName) throws EntityDoesNotExistException {
    
    if (equipmentMetadataTagName == null || equipmentMetadataTagName.trim().isEmpty()) {
      throw new IllegalStateException("equipmentMetadataTagName must be specified.");
    }
    
    TagEntity tag = this.equipmentMetadataTagsByName.get(equipmentMetadataTagName);
    if (tag != null) {
      return tag;
    }
    throw new EntityDoesNotExistException("Could not find equipment metadata tag with name: [" + equipmentMetadataTagName + "].");
  }
  
  // EQUIPMENT ENERGY EXCHANGE TYPES
  public EquipmentEnergyExchangeTypeEntity getEquipmentTypeById(Integer equipmentEnergyExchangeTypeId) {
    
    EquipmentEnergyExchangeTypeEntity equipmentEnergyExchangeType = this.equipmentEnergyExchangeTypesById.get(equipmentEnergyExchangeTypeId);
    if (equipmentEnergyExchangeType != null) {
      return equipmentEnergyExchangeType;
    }
    throw new IllegalStateException("Could not find equipment energy exchange type with id: [" + equipmentEnergyExchangeTypeId + "].");
  }

  public EquipmentEnergyExchangeTypeEntity getEquipmentTypeByName(String equipmentEnergyExchangeTypeName) {

    EquipmentEnergyExchangeTypeEntity equipmentType = getEquipmentTypeByNameNullIfNotExists(equipmentEnergyExchangeTypeName);
    if (equipmentType == null) {
      throw new IllegalStateException("Could not find equipment energy exchange type with name: [" + equipmentEnergyExchangeTypeName + "].");
    }
    return equipmentType;
  }
  
  public EquipmentEnergyExchangeTypeEntity getEquipmentTypeByNameNullIfNotExists(String equipmentEnergyExchangeTypeName) {

    if (equipmentEnergyExchangeTypeName == null || equipmentEnergyExchangeTypeName.trim().isEmpty()) {
      throw new IllegalStateException("equipmentEnergyExchangeTypeName must be specified.");
    }
    
    // Exact match.
    EquipmentEnergyExchangeTypeEntity et = this.equipmentEnergyExchangeTypesByName.get(equipmentEnergyExchangeTypeName);
    if (et != null) {
      return et;
    }
    
    // Fuzzy match.
    String n = equipmentEnergyExchangeTypeName.toLowerCase().trim();
    for (EquipmentEnergyExchangeTypeEntity equipmentEnergyExchangeType: this.equipmentEnergyExchangeTypesByName.values()) {
      
      String name = equipmentEnergyExchangeType.getName().toLowerCase().trim();
      if (n.equals(name)) {
        return equipmentEnergyExchangeType;
      }
    }
    return null;
  }
  
  public Set<EquipmentEnergyExchangeTypeEntity> getEquipmentTypes() {
    
    Set<EquipmentEnergyExchangeTypeEntity> set = new TreeSet<>();
    set.addAll(equipmentEnergyExchangeTypesByName.values());
    return set;
  }
  
  public void addEquipmentType(EquipmentEnergyExchangeTypeEntity equipmentEnergyExchangeType) {
    
    equipmentEnergyExchangeTypesById.put(equipmentEnergyExchangeType.getPersistentIdentity(), equipmentEnergyExchangeType);
  }
  
  // METHODS TO FULFILL UI USE CASES
  public List<IdNameDto> getEquipmentTypesDto() {
    
    List<IdNameDto> dtos = new ArrayList<>();
    Iterator<EquipmentEnergyExchangeTypeEntity> iterator = equipmentEnergyExchangeTypesByName.values().iterator();
    while (iterator.hasNext()) {
      
      EquipmentEnergyExchangeTypeEntity e = iterator.next();
      
      dtos.add(IdNameDto
          .builder()
          .withId(e.getPersistentIdentity())
          .withName(e.getName())
          .build());
    }
    return dtos;
  }  
  
  // PLANT ENERGY EXCHANGE TYPES
  public PlantEnergyExchangeTypeEntity getPlantTypeById(Integer plantEnergyExchangeTypeId) {
    
    PlantEnergyExchangeTypeEntity plantEnergyExchangeType = this.plantEnergyExchangeTypesById.get(plantEnergyExchangeTypeId);
    if (plantEnergyExchangeType != null) {
      return plantEnergyExchangeType;
    }
    throw new IllegalStateException("Could not find plant energy exchange type with id: [" + plantEnergyExchangeTypeId + "].");
  }

  public PlantEnergyExchangeTypeEntity getPlantTypeByName(String plantEnergyExchangeTypeName) {

    if (plantEnergyExchangeTypeName == null || plantEnergyExchangeTypeName.trim().isEmpty()) {
      throw new IllegalStateException("plantEnergyExchangeTypeName must be specified.");
    }
    
    // Exact match.
    PlantEnergyExchangeTypeEntity et = this.plantEnergyExchangeTypesByName.get(plantEnergyExchangeTypeName);
    if (et != null) {
      return et;
    }
    
    // Fuzzy match.
    String n = plantEnergyExchangeTypeName.toLowerCase().trim();
    for (PlantEnergyExchangeTypeEntity plantEnergyExchangeType: this.plantEnergyExchangeTypesByName.values()) {
      
      String name = plantEnergyExchangeType.getName().toLowerCase().trim();
      if (n.equals(name)) {
        return plantEnergyExchangeType;
      }
    }
    throw new IllegalStateException("Could not find plant energy exchange type with name: [" + plantEnergyExchangeTypeName + "].");
  }
  
  public Set<PlantEnergyExchangeTypeEntity> getPlantTypes() {
    
    Set<PlantEnergyExchangeTypeEntity> set = new TreeSet<>();
    set.addAll(plantEnergyExchangeTypesByName.values());
    return set;
  }
  
  public void addPlantType(PlantEnergyExchangeTypeEntity plantEnergyExchangeType) {
    
    plantEnergyExchangeTypesById.put(plantEnergyExchangeType.getPersistentIdentity(), plantEnergyExchangeType);
  }  
  
  // METHODS TO FULFILL UI USE CASES
  public List<IdNameDto> getPlantTypesDto() {
    
    List<IdNameDto> dtos = new ArrayList<>();
    Iterator<PlantEnergyExchangeTypeEntity> iterator = plantEnergyExchangeTypesByName.values().iterator();
    while (iterator.hasNext()) {
      
      PlantEnergyExchangeTypeEntity e = iterator.next();
      
      dtos.add(IdNameDto
          .builder()
          .withId(e.getPersistentIdentity())
          .withName(e.getName())
          .build());
    }
    return dtos;
  }  
  
  // LOOP ENERGY EXCHANGE TYPES
  public LoopEnergyExchangeTypeEntity getLoopTypeById(Integer loopEnergyExchangeTypeId) {
    
    LoopEnergyExchangeTypeEntity loopEnergyExchangeType = this.loopEnergyExchangeTypesById.get(loopEnergyExchangeTypeId);
    if (loopEnergyExchangeType != null) {
      return loopEnergyExchangeType;
    }
    throw new IllegalStateException("Could not find loop energy exchange type with id: [" + loopEnergyExchangeTypeId + "].");
  }

  public LoopEnergyExchangeTypeEntity getLoopTypeByName(String loopEnergyExchangeTypeName) {

    if (loopEnergyExchangeTypeName == null || loopEnergyExchangeTypeName.trim().isEmpty()) {
      throw new IllegalStateException("loopEnergyExchangeTypeName must be specified.");
    }
    
    // Exact match.
    LoopEnergyExchangeTypeEntity et = this.loopEnergyExchangeTypesByName.get(loopEnergyExchangeTypeName);
    if (et != null) {
      return et;
    }
    
    // Fuzzy match.
    String n = loopEnergyExchangeTypeName.toLowerCase().trim();
    for (LoopEnergyExchangeTypeEntity loopEnergyExchangeType: this.loopEnergyExchangeTypesByName.values()) {
      
      String name = loopEnergyExchangeType.getName().toLowerCase().trim();
      if (n.equals(name)) {
        return loopEnergyExchangeType;
      }
    }
    throw new IllegalStateException("Could not find loop energy exchange type with name: [" + loopEnergyExchangeTypeName + "].");
  }
  
  public Set<LoopEnergyExchangeTypeEntity> getLoopTypes() {
    
    Set<LoopEnergyExchangeTypeEntity> set = new TreeSet<>();
    set.addAll(loopEnergyExchangeTypesByName.values());
    return set;
  }
  
  public void addLoopType(LoopEnergyExchangeTypeEntity loopEnergyExchangeType) {
    
    loopEnergyExchangeTypesById.put(loopEnergyExchangeType.getPersistentIdentity(), loopEnergyExchangeType);
  }    
  
  // METHODS TO FULFILL UI USE CASES
  public List<IdNameDto> getLoopTypesDto() {
    
    List<IdNameDto> dtos = new ArrayList<>();
    Iterator<LoopEnergyExchangeTypeEntity> iterator = loopEnergyExchangeTypesByName.values().iterator();
    while (iterator.hasNext()) {
      
      LoopEnergyExchangeTypeEntity e = iterator.next();
      
      dtos.add(IdNameDto
          .builder()
          .withId(e.getPersistentIdentity())
          .withName(e.getName())
          .build());
    }
    return dtos;
  }   
  
  // ALL ENERGY EXCHANGE TYPES
  public AbstractEnergyExchangeTypeEntity getEnergyExchangeTypeById(Integer energyExchangeTypeId) {
    
    AbstractEnergyExchangeTypeEntity energyExchangeType = this.equipmentEnergyExchangeTypesById.get(energyExchangeTypeId);
    if (energyExchangeType != null) {
      return energyExchangeType;
    }

    energyExchangeType = this.plantEnergyExchangeTypesById.get(energyExchangeTypeId);
    if (energyExchangeType != null) {
      return energyExchangeType;
    }

    energyExchangeType = this.loopEnergyExchangeTypesById.get(energyExchangeTypeId);
    if (energyExchangeType != null) {
      return energyExchangeType;
    }
    
    throw new IllegalStateException("Could not find energy exchange type with id: [" + energyExchangeTypeId + "].");
  }

  public AbstractEnergyExchangeTypeEntity getEnergyExchangeTypeByName(String energyExchangeTypeName) {

    if (energyExchangeTypeName == null || energyExchangeTypeName.trim().isEmpty()) {
      throw new IllegalStateException("energyExchangeTypeName must be specified.");
    }
    String n = energyExchangeTypeName.toLowerCase().trim();
    
    // Exact match.
    AbstractEnergyExchangeTypeEntity energyExchangeType = this.equipmentEnergyExchangeTypesByName.get(energyExchangeTypeName);
    if (energyExchangeType != null) {
      return energyExchangeType;
    }
    
    // Fuzzy match.
    for (AbstractEnergyExchangeTypeEntity equipmentEnergyExchangeType: this.equipmentEnergyExchangeTypesByName.values()) {
      
      String name = equipmentEnergyExchangeType.getName().toLowerCase().trim();
      if (n.equals(name)) {
        return equipmentEnergyExchangeType;
      }
    }
    
    // Exact match.
    energyExchangeType = this.plantEnergyExchangeTypesByName.get(energyExchangeTypeName);
    if (energyExchangeType != null) {
      return energyExchangeType;
    }
    
    // Fuzzy match.
    for (AbstractEnergyExchangeTypeEntity equipmentEnergyExchangeType: this.plantEnergyExchangeTypesByName.values()) {
      
      String name = equipmentEnergyExchangeType.getName().toLowerCase().trim();
      if (n.equals(name)) {
        return equipmentEnergyExchangeType;
      }
    }    

    // Exact match.
    energyExchangeType = this.loopEnergyExchangeTypesByName.get(energyExchangeTypeName);
    if (energyExchangeType != null) {
      return energyExchangeType;
    }
    
    // Fuzzy match.
    for (AbstractEnergyExchangeTypeEntity equipmentEnergyExchangeType: this.loopEnergyExchangeTypesByName.values()) {
      
      String name = equipmentEnergyExchangeType.getName().toLowerCase().trim();
      if (n.equals(name)) {
        return equipmentEnergyExchangeType;
      }
    }    
    
    throw new IllegalStateException("Could not find energy exchange type with name: [" + energyExchangeTypeName + "].");
  }
  
  public Set<AbstractEnergyExchangeTypeEntity> getEnergyExchangeTypes() {
    
    Set<AbstractEnergyExchangeTypeEntity> set = new TreeSet<>();
    set.addAll(equipmentEnergyExchangeTypesByName.values());
    set.addAll(plantEnergyExchangeTypesByName.values());
    set.addAll(loopEnergyExchangeTypesByName.values());
    return set;
  }
  
  @Override
  public String toString() {
    return new StringBuilder()
        .append("TagsContainer [tags=")
        .append(tagsById)
        .append("]")
        .toString();
  }

  public static TagsContainer mapFromDtos(List<TagDto> dtoList) {
    
    Map<Integer, TagEntity> map = new HashMap<>(); 
    Iterator<TagDto> iterator = dtoList.iterator();
    while (iterator.hasNext()) {
      
      TagDto tagDto = iterator.next();
      
      Integer id = tagDto.getId();
      Integer nodeTypeId = tagDto.getScopedToConstraint();
      
      TagGroupType tagGroupType = null;
      Integer tagGroupId = tagDto.getTagGroupId();
      tagGroupType = TagGroupType.get(tagGroupId);
      
      NodeType scopedToConstraint = null;
      if (nodeTypeId != null) {
        scopedToConstraint = NodeType.get(tagDto.getScopedToConstraint());
      }
      map.put(id, new TagEntity(
          id, 
          tagDto.getName(),
          tagGroupType,
          TagType.get(tagDto.getTagTypeId()),
          scopedToConstraint,
          tagDto.getUiInferred()));
    }
    return new TagsContainer(map);
  }
  
  public static List<TagDto> mapToDtos(TagsContainer tagsContainer) {
    
    return mapToDtos(tagsContainer.getTags());
  }
  
  public static List<TagDto> mapToDtos(Collection<TagEntity> entities) {
    
    List<TagDto> dtos = new ArrayList<>();
    Iterator<TagEntity> iterator = entities.iterator();
    while (iterator.hasNext()) {
      dtos.add(mapToDto(iterator.next()));
    }
    return dtos;
  }
  
  public static TagDto mapToDto(TagEntity e) {
    
    TagDto dto = new TagDto();
    dto.setId(e.getPersistentIdentity());
    dto.setTagGroupId(e.getTagGroupType().getId());
    dto.setTagGroup(e.getTagGroupType().getName());
    dto.setTagTypeId(e.getTagType().getId());
    dto.setTagType(e.getTagType().getName());
    dto.setName(e.getName());
    dto.setUiInferred(e.getUiInferred());
    NodeType scopedToConstraint = e.getScopedToConstraint();
    if (scopedToConstraint != null) {
      dto.setScopedToConstraint(scopedToConstraint.getId());  
    }    
    return dto;
  }   
}