//@formatter:off
package com.djt.hvac.domain.model.dictionary.template.nodetag;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.djt.hvac.domain.model.common.AbstractAssociativeEntity;
import com.djt.hvac.domain.model.common.AbstractEntity;
import com.djt.hvac.domain.model.common.mapper.DtoMapper;
import com.djt.hvac.domain.model.common.validation.IssueType;
import com.djt.hvac.domain.model.common.validation.ValidationMessage;
import com.djt.hvac.domain.model.dictionary.container.NodeTagTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.dto.PointTemplateUnitMappingDto;

public class PointTemplateUnitMappingEntity extends AbstractAssociativeEntity {
  private static final long serialVersionUID = 1L;
  
  private final PointTemplateEntity pointTemplate;
  private final UnitMappingEntity unitMapping;
  private final Integer priority;
  
  public PointTemplateUnitMappingEntity(
      PointTemplateEntity pointTemplate,
      UnitMappingEntity unitMapping,
      Integer priority) {
    super();
    requireNonNull(pointTemplate, "pointTemplate cannot be null");
    requireNonNull(unitMapping, "unitMapping cannot be null");
    requireNonNull(priority, "priority cannot be null");
    this.pointTemplate = pointTemplate;
    this.unitMapping = unitMapping;
    this.priority = priority;
  }
  
  public PointTemplateEntity getPointTemplate() {
    return pointTemplate;
  }

  public UnitMappingEntity getUnitMapping() {
    return unitMapping;
  }

  public Integer getPriority() {
    return priority;
  }
  
  public Map<String, Integer> getParentIdentities() {
    
    Map<String, Integer> parentIdentities = new TreeMap<>();
    parentIdentities.put("pointTemplateId", pointTemplate.getPersistentIdentity());
    parentIdentities.put("unitMappingId", unitMapping.getPersistentIdentity());
    parentIdentities.put("priority", priority);
    return parentIdentities;
  }

  @Override
  public String getNaturalIdentity() {
    return new StringBuilder()
        .append(this.pointTemplate.getNaturalIdentity())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(this.unitMapping.getNaturalIdentity())
        .append(AbstractEntity.NATURAL_IDENTITY_DELIMITER)
        .append(this.priority.toString())
        .toString();
  }  
  
  @Override
  public void validate(
      Set<IssueType> issueTypes,
      List<ValidationMessage> validationMessages, 
      boolean remediate) {
  }
  
  public int compareTo(AbstractEntity that) {
    
    int compareTo = this.pointTemplate.getPersistentIdentity().compareTo(((PointTemplateUnitMappingEntity)that).pointTemplate.getPersistentIdentity());
    if (compareTo == 0) {
      compareTo = this.unitMapping.getPersistentIdentity().compareTo(((PointTemplateUnitMappingEntity)that).unitMapping.getPersistentIdentity());
      if (compareTo == 0) {
        compareTo = this.priority.compareTo(((PointTemplateUnitMappingEntity)that).priority);  
      }
    }
    return compareTo;
  }
  
  public static class Mapper implements DtoMapper<NodeTagTemplatesContainer, PointTemplateUnitMappingEntity, PointTemplateUnitMappingDto> {

    private static final Mapper INSTANCE = new Mapper();

    private Mapper() {}

    public static Mapper getInstance() {
      return INSTANCE;
    }

    public List<PointTemplateUnitMappingDto> mapEntitiesToDtos(List<PointTemplateUnitMappingEntity> entities) {

      List<PointTemplateUnitMappingDto> list = new ArrayList<>();
      for (PointTemplateUnitMappingEntity entity: entities) {
        if (!entity.getIsDeleted()) {
          list.add(mapEntityToDto(entity));
        }
      }
      return list;
    }

    @Override
    public PointTemplateUnitMappingDto mapEntityToDto(PointTemplateUnitMappingEntity e) {

      return new PointTemplateUnitMappingDto(
          e.getPointTemplate().getPersistentIdentity(),
          e.getUnitMapping().getPersistentIdentity(),
          e.getPriority());
    }

    public List<PointTemplateUnitMappingEntity> mapDtosToEntities(
        NodeTagTemplatesContainer nodeTagTemplatesContainer,
        List<PointTemplateUnitMappingDto> dtos) {

      List<PointTemplateUnitMappingEntity> list = new ArrayList<>();
      for (PointTemplateUnitMappingDto dto: dtos) {
        list.add(mapDtoToEntity(nodeTagTemplatesContainer, dto));
      }
      return list;
    }
    
    @Override
    public PointTemplateUnitMappingEntity mapDtoToEntity(
        NodeTagTemplatesContainer nodeTagTemplatesContainer,
        PointTemplateUnitMappingDto d) {
      
      try {
        
        return new PointTemplateUnitMappingEntity(
            nodeTagTemplatesContainer.getPointTemplate(d.getPointTemplateId()),
            nodeTagTemplatesContainer.getUnitMapping(d.getUnitMappingId()),
            d.getPriority());
        
      } catch (Exception e) {
        throw new RuntimeException("Unable to map DTO: ["
            + d, e);
      }
    }
  }  
}
//@formatter:on