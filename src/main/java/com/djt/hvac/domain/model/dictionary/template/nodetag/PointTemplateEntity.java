//@formatter:off
package com.djt.hvac.domain.model.dictionary.template.nodetag;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.djt.hvac.domain.model.common.mapper.DtoMapper;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.UnitEntity;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.container.NodeTagTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.container.TagsContainer;
import com.djt.hvac.domain.model.dictionary.container.UnitsContainer;
import com.djt.hvac.domain.model.dictionary.dto.NodeTagTemplateDto;
import com.djt.hvac.domain.model.dictionary.energyexchange.AbstractEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.EquipmentEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.LoopEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.energyexchange.PlantEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.enums.NodeType;
import com.djt.hvac.domain.model.dictionary.enums.TagGroupType;
import com.google.common.collect.ImmutableSet;

public class PointTemplateEntity extends AbstractNodeTagTemplateEntity {
  private static final long serialVersionUID = 1L;
  
  public static final Set<EquipmentEnergyExchangeTypeEntity> EMPTY_PARENT_EQUIPMENT_TYPES = ImmutableSet.copyOf(new HashSet<>());
  public static final Set<PlantEnergyExchangeTypeEntity> EMPTY_PARENT_PLANT_TYPES = ImmutableSet.copyOf(new HashSet<>());
  public static final Set<LoopEnergyExchangeTypeEntity> EMPTY_PARENT_LOOP_TYPES = ImmutableSet.copyOf(new HashSet<>());
  
  private UnitEntity unit;
  
  private final Set<EquipmentEnergyExchangeTypeEntity> parentEquipmentTypes = new TreeSet<>();
  private final Set<PlantEnergyExchangeTypeEntity> parentPlantTypes = new TreeSet<>();
  private final Set<LoopEnergyExchangeTypeEntity> parentLoopTypes = new TreeSet<>();
  
  public PointTemplateEntity(
      Integer persistentIdentity,
      String name,
      String description,
      Set<NodeType> parentNodeTypes,
      Boolean isPublic,
      Set<TagEntity> tags,
      Boolean isDeprecated,
      Integer replacementPointTemplateId,
      UnitEntity unit,
      Set<EquipmentEnergyExchangeTypeEntity> parentEquipmentTypes,
      Set<PlantEnergyExchangeTypeEntity> parentPlantTypes,
      Set<LoopEnergyExchangeTypeEntity> parentLoopTypes) {
    super(
        persistentIdentity,
        name,
        description,
        parentNodeTypes,
        NodeType.POINT,
        TagGroupType.POINT_HAYSTACK_TAG,
        isPublic,
        tags,
        isDeprecated,
        replacementPointTemplateId);
    
    requireNonNull(unit, "unit cannot be null");
    setUnit(unit);
    
    if (parentNodeTypes.isEmpty()) {
      throw new IllegalStateException("At least one parent node type has to be specified for point template with id: " + persistentIdentity);
    }
    
    if (parentNodeTypes.contains(NodeType.EQUIPMENT)
        || parentNodeTypes.contains(NodeType.PLANT)
        || parentNodeTypes.contains(NodeType.LOOP)) {
      
      setParentEnergyExchangeTypes(
          parentEquipmentTypes,
          parentPlantTypes,
          parentLoopTypes);
    }
  }
  
  public UnitEntity getUnit() {
    return unit;
  }
  
  public void setUnit(UnitEntity unit) {
    
    if (this.unit == null && unit == null) {
      
      // BOTH NULL: DO NOTHING
      
    } else if ((this.unit == null && unit != null) 
        || (this.unit != null && unit == null)) {
      
      this.unit = unit;
      setIsModified("unit");
      
    } else if (this.unit != null && unit != null) {
      
      if (!this.unit.equals(unit)) {

        this.unit = unit;
        setIsModified("unit");
        
      } else {
        
        // BOTH NOT NULL AND EQUAL TO EACH OTHER: DO NOTHING
        
      }
    }     
  }   
  
  public void setParentEnergyExchangeTypes(
      Set<EquipmentEnergyExchangeTypeEntity> parentEquipmentTypes,
      Set<PlantEnergyExchangeTypeEntity> parentPlantTypes,
      Set<LoopEnergyExchangeTypeEntity> parentLoopTypes) {

    if (parentEquipmentTypes.isEmpty() && parentPlantTypes.isEmpty() && parentLoopTypes.isEmpty()) {
      throw new IllegalArgumentException("At least 1 parent equipment/plant/loop type must be specified for: " + getName());
    }
    
    setParentEquipmentTypes(parentEquipmentTypes);
    setParentPlantTypes(parentPlantTypes);
    setParentLoopTypes(parentLoopTypes);
  }
  
  public void setParentEquipmentTypes(Set<EquipmentEnergyExchangeTypeEntity> parentEquipmentTypes) {
    
    if (parentEquipmentTypes == null) {
      throw new IllegalArgumentException("parentEquipmentTypes cannot be null.");
    }
    
    if (!this.parentEquipmentTypes.equals(parentEquipmentTypes)) {

      this.parentEquipmentTypes.clear();
      this.parentEquipmentTypes.addAll(parentEquipmentTypes);
      setIsModified("parentEquipmentTypes");
      
    } else {
      
      // BOTH NOT NULL AND EQUAL TO EACH OTHER: DO NOTHING
      
    }    
  }
  
  public void setParentPlantTypes(Set<PlantEnergyExchangeTypeEntity> parentPlantTypes) {
    
    if (parentPlantTypes == null) {
      throw new IllegalArgumentException("parentPlantTypes cannot be null.");
    }
    
    if (!this.parentPlantTypes.equals(parentPlantTypes)) {

      this.parentPlantTypes.clear();
      this.parentPlantTypes.addAll(parentPlantTypes);
      setIsModified("parentPlantTypes");
      
    } else {
      
      // BOTH NOT NULL AND EQUAL TO EACH OTHER: DO NOTHING
      
    }    
  }
  
  public void setParentLoopTypes(Set<LoopEnergyExchangeTypeEntity> parentLoopTypes) {
    
    if (parentLoopTypes == null) {
      throw new IllegalArgumentException("parentLoopTypes cannot be null.");
    }
    
    if (!this.parentLoopTypes.equals(parentLoopTypes)) {

      this.parentLoopTypes.clear();
      this.parentLoopTypes.addAll(parentLoopTypes);
      setIsModified("parentLoopTypes");
      
    } else {
      
      // BOTH NOT NULL AND EQUAL TO EACH OTHER: DO NOTHING
      
    }    
  }  
  
  public Set<EquipmentEnergyExchangeTypeEntity> getParentEquipmentTypes() {
    return parentEquipmentTypes;
  }
  
  public Set<PlantEnergyExchangeTypeEntity> getParentPlantTypes() {
    return parentPlantTypes;
  }

  public Set<LoopEnergyExchangeTypeEntity> getParentLoopTypes() {
    return parentLoopTypes;
  }
  
  public Set<AbstractEnergyExchangeTypeEntity> getParentEnergyExchangeTypes() {
    
    Set<AbstractEnergyExchangeTypeEntity> set = new TreeSet<>();
    set.addAll(parentEquipmentTypes);
    set.addAll(parentPlantTypes);
    set.addAll(parentLoopTypes);
    return set;
  }
  
  public static class Mapper implements DtoMapper<NodeTagTemplatesContainer, PointTemplateEntity, NodeTagTemplateDto> {

    private static final Mapper INSTANCE = new Mapper();

    private Mapper() {}

    public static Mapper getInstance() {
      return INSTANCE;
    }

    public List<NodeTagTemplateDto> mapEntitiesToDtos(List<PointTemplateEntity> entities) {

      List<NodeTagTemplateDto> list = new ArrayList<>();
      for (PointTemplateEntity entity: entities) {
        if (!entity.getIsDeleted()) {
          list.add(mapEntityToDto(entity));
        }
      }
      return list;
    }

    @Override
    public NodeTagTemplateDto mapEntityToDto(PointTemplateEntity entity) {

      NodeTagTemplateDto dto = new NodeTagTemplateDto();
      
      if (entity instanceof PointTemplateEntity) {
        
        PointTemplateEntity pointTemplate = (PointTemplateEntity)entity;
        
        dto.setUnitId(pointTemplate.getUnit().getPersistentIdentity());
        if (pointTemplate instanceof PointTemplateEntity) {
          
          PointTemplateEntity equipmentLevelPointTemplate = (PointTemplateEntity)entity;
          dto.setParentEnergyExchangeTypeIds(getParentEnergyExchangeTypeIdsAsString(equipmentLevelPointTemplate.getParentEnergyExchangeTypes()));
        }
      }
      dto.setId(entity.getPersistentIdentity());
      dto.setName(entity.getName());
      dto.setDescription(entity.getDescription());
      dto.setIsPublic(entity.getIsPublic());
      dto.setParentNodeTypeId(Integer.valueOf(entity.getParentNodeType().getId()));
      dto.setTags("{" + getTagsAsString(entity.getTags()) + "}");
      
      return dto;
    }

    public List<PointTemplateEntity> mapDtosToEntities(
        NodeTagTemplatesContainer nodeTagTemplatesContainer,
        List<NodeTagTemplateDto> dtos) {

      List<PointTemplateEntity> list = new ArrayList<>();
      for (NodeTagTemplateDto dto: dtos) {
        list.add(mapDtoToEntity(nodeTagTemplatesContainer, dto));
      }
      return list;
    }
    
    @Override
    public PointTemplateEntity mapDtoToEntity(
        NodeTagTemplatesContainer nodeTagTemplatesContainer,
        NodeTagTemplateDto dto) {
      
      UnitsContainer unitsContainer = DictionaryContext.getUnitsContainer();
      TagsContainer tagsContainer = DictionaryContext.getTagsContainer();
      
      try {
        
        PointTemplateEntity pointTemplate = null;
        
        Set<PointTemplateEntity> buildingPointTemplates = new TreeSet<>();
        Map<EquipmentEnergyExchangeTypeEntity, Set<PointTemplateEntity>> equipmentPointTemplates = new TreeMap<>();
        Map<PlantEnergyExchangeTypeEntity, Set<PointTemplateEntity>> plantPointTemplates = new TreeMap<>();
        Map<LoopEnergyExchangeTypeEntity, Set<PointTemplateEntity>> loopPointTemplates = new TreeMap<>();
        
        Integer persistentIdentity = dto.getId();
        String name = dto.getName();
        String description = dto.getDescription();
        Boolean isPublic = dto.getIsPublic();
        Boolean isDeprecated = dto.getIsDeprecated();
        Integer replacementPointTemplateId = dto.getReplacementPointTemplateId();
        Integer parentNodeType = dto.getParentNodeTypeId();
        
        Integer unitId = dto.getUnitId();
        UnitEntity unit = null;
        if (unitId != null) {
          unit = unitsContainer.getUnit(dto.getUnitId()); 
        } else {
          unit = unitsContainer.getUnit(dto.getUnits());
        }
        
        Set<TagEntity> haystackTags = new TreeSet<>();
        String[] tagArray = dto
            .getTags()
            .replace("{", "")
            .replace("}", "")
            .replace("\"", "")
            .split(",");
        for (int i=0; i < tagArray.length; i++) {
          haystackTags.add(tagsContainer.getHaystackTag(tagArray[i]));
        }
        
        Set<NodeType> parentNodeTypes = new HashSet<>();
        
        // First, see if there are any "energy exchange" associations (i.e. equipment, plant or loop).
        // Otherwise, use the default parent node type associated with the point template.
        String parentEnergyExchangeTypeIds = dto.getParentEnergyExchangeTypeIds();
        if (parentEnergyExchangeTypeIds != null 
            && !parentEnergyExchangeTypeIds.equals("{}") 
            && !parentEnergyExchangeTypeIds.equalsIgnoreCase("{NULL}")) {
          
          Set<EquipmentEnergyExchangeTypeEntity> parentEquipmentTypes = new TreeSet<>();
          Set<PlantEnergyExchangeTypeEntity> parentPlantTypes = new TreeSet<>();
          Set<LoopEnergyExchangeTypeEntity> parentLoopTypes = new TreeSet<>();
          
          String[] parentEnergyExchangeTypeIdArray = parentEnergyExchangeTypeIds
              .replace("{", "")
              .replace("}", "")
              .replace("\"", "")
              .split(",");

          for (int i=0; i < parentEnergyExchangeTypeIdArray.length; i++) {
            
            String parentEnergyExchangeTypeId = parentEnergyExchangeTypeIdArray[i];
            if (!parentEnergyExchangeTypeId.trim().equalsIgnoreCase("null")) {

              AbstractEnergyExchangeTypeEntity energyExchangeType = tagsContainer.getEnergyExchangeTypeById(Integer.parseInt(parentEnergyExchangeTypeId));
              if (energyExchangeType instanceof EquipmentEnergyExchangeTypeEntity) {
                parentEquipmentTypes.add((EquipmentEnergyExchangeTypeEntity)energyExchangeType);
                
              } else if (energyExchangeType instanceof PlantEnergyExchangeTypeEntity) {
                parentPlantTypes.add((PlantEnergyExchangeTypeEntity)energyExchangeType);
                
              } else if (energyExchangeType instanceof LoopEnergyExchangeTypeEntity) {
                parentLoopTypes.add((LoopEnergyExchangeTypeEntity)energyExchangeType);
                
              } else {
                
                throw new IllegalArgumentException("Unsupported energy exchange type: ["
                    + energyExchangeType.getClassAndNaturalIdentity()
                    + "], expected: [Equipment, Plant or Loop].");
              }
            }
          }
          
          if (!parentEquipmentTypes.isEmpty() 
              || !parentPlantTypes.isEmpty() 
              || !parentLoopTypes.isEmpty()) {
            
            if (parentEquipmentTypes.isEmpty()) {
              parentEquipmentTypes = EMPTY_PARENT_EQUIPMENT_TYPES;
            } else {
              parentNodeTypes.add(NodeType.EQUIPMENT);              
            }
            
            if (parentPlantTypes.isEmpty()) {
              parentPlantTypes = EMPTY_PARENT_PLANT_TYPES;
            } else {
              parentNodeTypes.add(NodeType.PLANT);
            }

            if (parentLoopTypes.isEmpty()) {
              parentLoopTypes = EMPTY_PARENT_LOOP_TYPES;
            } else {
              parentNodeTypes.add(NodeType.LOOP);
            }

            // It's possible that a point template can be associated with both 
            // buildings and energy exchange types (plants, loops and equipment).
            if (parentNodeType != null && parentNodeType.equals(NodeType.BUILDING.getId())) {
              parentNodeTypes.add(NodeType.BUILDING);
            }
            
            pointTemplate = new PointTemplateEntity(
                persistentIdentity,
                name,
                description,
                parentNodeTypes,
                isPublic,
                haystackTags,
                isDeprecated,
                replacementPointTemplateId,
                unit,
                parentEquipmentTypes,
                parentPlantTypes,
                parentLoopTypes);
            
            if (!parentEquipmentTypes.isEmpty()) {
              
              for (EquipmentEnergyExchangeTypeEntity equipmentType: parentEquipmentTypes) {
                
                Set<PointTemplateEntity> set = equipmentPointTemplates.get(equipmentType);
                if (set == null) {
                  
                  set = new TreeSet<>();
                  equipmentPointTemplates.put(equipmentType, set);
                }
                set.add(pointTemplate);
              }
            }

            if (!parentPlantTypes.isEmpty()) {

              for (PlantEnergyExchangeTypeEntity plantType: parentPlantTypes) {
                
                Set<PointTemplateEntity> set = plantPointTemplates.get(plantType);
                if (set == null) {
                  
                  set = new TreeSet<>();
                  plantPointTemplates.put(plantType, set);
                }
                set.add(pointTemplate);
              }
            }

            if (!parentLoopTypes.isEmpty()) {

              for (LoopEnergyExchangeTypeEntity loopType: parentLoopTypes) {
                
                Set<PointTemplateEntity> set = loopPointTemplates.get(loopType);
                if (set == null) {
                  
                  set = new TreeSet<>();
                  loopPointTemplates.put(loopType, set);
                }
                set.add(pointTemplate);
              }
            }
          }
          
          return pointTemplate;
          
        } else if (parentNodeType != null && parentNodeType.equals(NodeType.BUILDING.getId())) {
         
          parentNodeTypes.add(NodeType.BUILDING);
          
          pointTemplate = new PointTemplateEntity(
              persistentIdentity,
              name,
              description,
              parentNodeTypes,
              isPublic,
              haystackTags,
              isDeprecated,
              replacementPointTemplateId,
              unit,
              EMPTY_PARENT_EQUIPMENT_TYPES,
              EMPTY_PARENT_PLANT_TYPES,
              EMPTY_PARENT_LOOP_TYPES);
          
          buildingPointTemplates.add(pointTemplate);
          
          return pointTemplate;
          
        } else if (dto.getEquipment() != null) {

          Set<EquipmentEnergyExchangeTypeEntity> parentEquipmentTypes = new TreeSet<>();
          Set<PlantEnergyExchangeTypeEntity> parentPlantTypes = new TreeSet<>();
          Set<LoopEnergyExchangeTypeEntity> parentLoopTypes = new TreeSet<>();
          
          String[] energyExchangeTypeNames = dto.getEquipment().split(",");
          for (String energyExchangeTypeName: energyExchangeTypeNames) {
            
            AbstractEnergyExchangeTypeEntity energyExchangeType = tagsContainer.getEnergyExchangeTypeByName(energyExchangeTypeName.trim());
            
            if (energyExchangeType instanceof EquipmentEnergyExchangeTypeEntity) {
          
              parentNodeTypes.add(NodeType.EQUIPMENT);
              parentEquipmentTypes.add((EquipmentEnergyExchangeTypeEntity)energyExchangeType);
              
            } else if (energyExchangeType instanceof PlantEnergyExchangeTypeEntity) {

              parentNodeTypes.add(NodeType.PLANT);
              parentPlantTypes.add((PlantEnergyExchangeTypeEntity)energyExchangeType);
              
            } else if (energyExchangeType instanceof LoopEnergyExchangeTypeEntity) {

              parentNodeTypes.add(NodeType.LOOP);
              parentLoopTypes.add((LoopEnergyExchangeTypeEntity)energyExchangeType);
              
            }
          }
          
          pointTemplate = new PointTemplateEntity(
              persistentIdentity,
              name,
              description,
              parentNodeTypes,
              Boolean.TRUE,
              haystackTags,
              Boolean.FALSE,
              null,
              unit,
              parentEquipmentTypes,
              parentPlantTypes,
              parentLoopTypes);
          
          return pointTemplate;
          
        } else {
          
          throw new IllegalStateException("Unsupported parent node type: ["
              + parentNodeType
              + "] for point template: ["
              + dto.getName() 
              + "].");
        }
        
      } catch (Exception e) {
        throw new RuntimeException("Unable to map DTO: ["
            + dto, e);
      }      
    }
  }
  
  public static String getParentEnergyExchangeTypeIdsAsString(Set<AbstractEnergyExchangeTypeEntity> entities) {
    
    String s = null;
    StringBuilder sb = new StringBuilder();
    if (entities != null && !entities.isEmpty()) {
      sb.append("{");
      Iterator<AbstractEnergyExchangeTypeEntity> iterator = entities.iterator();
      while (iterator.hasNext()) {
        
        AbstractEnergyExchangeTypeEntity equipmentType = iterator.next();
        sb.append(equipmentType.getPersistentIdentity().toString());
        
        if (iterator.hasNext()) {
          sb.append(",");
        }
      }
      sb.append("}");
      s = sb.toString();
    }
    return s;
  }    
  
  public static String getTagsAsString(Set<TagEntity> entities) {
    
    String tags = null;
    if (entities != null && !entities.isEmpty()) {
      tags = entities
          .toString()
          .replace(" ", "")
          .replace("[", "")
          .replace("\"", "")
          .replace("]", "");
    } else {
      tags = "";
    }
    return tags;
  }  
}
//@formatter:on